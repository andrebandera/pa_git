package br.gov.ms.detran.pa.instauracao;

import br.gov.ms.detran.comum.projeto.jms.DetranJmsCrossDomainConsumer;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.web.DtnUserCrossDomain;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.ejb.IExecucaoInstauracaoService;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IPAInstauracaoService;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.RegraInstaurarEnum;
import br.gov.ms.detran.processo.administrativo.enums.InstauracaoCenarioEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ApoioOrigemInstauracaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.DadosCondutorWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.InstauracaoBlocoWrapper2;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;

import static br.gov.ms.detran.comum.util.DetranCollectionUtil.ehNuloOuVazio;
import static br.gov.ms.detran.comum.util.DetranSpringSecurityUtil.SPRING_SECURITY_AUTHENTICATION_KEY;
import br.gov.ms.detran.comum.util.exception.AppExceptionUtils;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import static br.gov.ms.detran.processo.administrativo.entidade.SituacaoExecucaoInstauracaoEnum.INSTAURANDO;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;

@MessageDriven(
    mappedName = "jms/instaurarprocessoadministrativoQueue",
    activationConfig = { 
        @ActivationConfigProperty(
            propertyName = "destinationType",
            propertyValue = "javax.jms.Queue"
        )
    }
)
public class InstaurarPAConsumerService extends DetranJmsCrossDomainConsumer {

    private static final Logger LOG = Logger.getLogger(InstaurarPAConsumerService.class);
    private IPAInstauracaoService paService;

    private IExecucaoInstauracaoService execucaoService;
    private IPAControleFalhaService falhaService;

    @PostConstruct
    public void init() {
        paService       = ServiceJndiLocator.<IPAInstauracaoService>lookup("ejb/PAInstauracaoService");
        execucaoService = ServiceJndiLocator.<IExecucaoInstauracaoService>lookup("ejb/ExecucaoInstauracaoService");
        falhaService    = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
    }

    @Override
    public void processMessage(Message message, DtnUserCrossDomain userCrossDomain) throws AppException {

        InstauracaoBlocoWrapper2 wrapperBloco = getObjectMessageTyped(message);

        String springAuthenticationToken = 
                getObjectMessageProperty(
                        message, SPRING_SECURITY_AUTHENTICATION_KEY);

        if (userCrossDomain != null) {
            createSessionSecurity(userCrossDomain, springAuthenticationToken);
        }

        List<ApoioOrigemInstauracaoWrapper> cenarios = paService.recuperarCenarios();

        if(ehNuloOuVazio(wrapperBloco.getCondutores())) {
            LOG.error("Lista de condutores vazia - Execução ID {0}", wrapperBloco.getExecucaoId());
            return;
        }

        try {

            LOG.debug("Início Instauracao - Execucão {0}", wrapperBloco.getExecucaoId());

            List<DadosCondutorWrapper> condutores = wrapperBloco.getCondutores();
            iniciarExecucao(wrapperBloco.getExecucaoId());

            for (DadosCondutorWrapper condutor : condutores) {
                
                if (isExecucaoInterrompida(wrapperBloco.getExecucaoId())) {
                    return;
                }

                DadosCondutorPAD dadosCondutorPAD = recuperarDadosCondutor(condutor.getId());

                if (dadosCondutorPAD == null) {
                    LOG.warn("Condutor ID {0} NÃO ENCONTRADO!", condutor.getId());
                    continue;
                }
                
//                if(dadosCondutorPAD.getCpf().equals("00026277107")) {
                    
                    LOG.debug("Condutor CPF: {0}", dadosCondutorPAD.getCpf());

                    try {

                        for (ApoioOrigemInstauracaoWrapper apoioOrigemWrapper : cenarios) {

                            LOG.debug("Cenário: {0}", apoioOrigemWrapper.getEntidade().getRegra().name());

                            try {
                                
                                validaApoioOrigemInstauracao(apoioOrigemWrapper);

                                paService.validarCondutorParaInstaurarPA(dadosCondutorPAD);

                                InstaurarProcessoAdministrativoWrapper wrapper
                                    = new InstaurarProcessoAdministrativoWrapper(
                                        apoioOrigemWrapper.getEntidade(),
                                        dadosCondutorPAD,
                                        apoioOrigemWrapper.getValorReferenciaMes(),
                                        apoioOrigemWrapper.getValorReferenciaIntervalo(),
                                        wrapperBloco.getExecucaoId()
                                    );

                                InstauracaoCenarioEnum cenarioInstaurar
                                    = InstauracaoCenarioEnum.valueOf(apoioOrigemWrapper.getEntidade().getRegra().name());

                                /** Instaurar. **/
                                ProcessoAdministrativo processoAdministrativo
                                    = (ProcessoAdministrativo) paService.instaurar(cenarioInstaurar, wrapper);

                                if(processoAdministrativo != null){
                                    new ExecucaoAndamentoManager()
                                            .iniciaExecucao(
                                                    new ExecucaoAndamentoEspecificoWrapper(
                                                            processoAdministrativo,
                                                            null,
                                                            null,
                                                            null
                                                    )
                                            );
                                }

                            } catch (RegraNegocioException e) {
                                LOG.debug("Capturado. Regra: {0}", AppExceptionUtils.getExceptionMessage(e), apoioOrigemWrapper.getEntidade().getRegra().name());
                            } catch (AppException e) {
                                LOG.debug("Capturado. Regra: {0}", AppExceptionUtils.getExceptionMessage(e), apoioOrigemWrapper.getEntidade().getRegra().name());
                                falhaService.gravarFalhaCondutor(e, AppExceptionUtils.getExceptionMessage(e), dadosCondutorPAD.getCpf());
                            } catch (Exception e) {
                                LOG.error("Capturado.", e);
                                falhaService.gravarFalhaCondutor(e, "Erro", dadosCondutorPAD.getCpf());
                            }
                        }

                    } catch (Exception e) {
                        LOG.error("Capturado.", e);
                        falhaService.gravarFalhaCondutor(e, "Erro", dadosCondutorPAD.getCpf());
                    }
//                }
            }

            LOG.debug("******** Fim - Execução {0}", wrapperBloco.getExecucaoId());

        } catch (Exception e) {
            LOG.error("Sem tratamento.", e);
            falhaService.gravarFalha(e, "Erro");
        } finally {
            finalizarExecucao(wrapperBloco.getExecucaoId());
        }
    }

    /**
     * 
     * @param apoioOrigemWrapper
     * @throws AppException 
     */
    private void validaApoioOrigemInstauracao(ApoioOrigemInstauracaoWrapper apoioOrigemWrapper) throws AppException {
        
        if(apoioOrigemWrapper.getEntidade() == null) {
            DetranWebUtils.applicationMessageException("Parâmetro de apoio obrigatório inválido.[Entidade]");
        }
        
        if(!RegraInstaurarEnum.C1.equals(apoioOrigemWrapper.getEntidade().getRegra()) && apoioOrigemWrapper.getValorReferenciaMes() == null) {
            DetranWebUtils.applicationMessageException("Parâmetro de apoio obrigatório inválido.[ValorReferenciaMes]");
        }
    }

    /**
     * 
     * @param execucao
     * @throws AppException 
     */
    boolean isExecucaoInterrompida(Long execucaoId) throws AppException {
        return execucaoService.isInterromperExecucao(execucaoId);
    }

    /**
     * 
     * @param execucaoId
     * @return
     * @throws AppException 
     */
    void iniciarExecucao(Long execucaoId) throws AppException {
        execucaoService.atualizarExecucao(execucaoId, INSTAURANDO);
    }

    /**
     * 
     * @param condutorId
     * @return
     * @throws AppException 
     */
    DadosCondutorPAD recuperarDadosCondutor(Long condutorId) throws AppException {
        return (DadosCondutorPAD) paService.buscarEntidadePeloId(DadosCondutorPAD.class, condutorId);
    }

    /**
     * 
     * @param execucao 
     */
    void finalizarExecucao(Long execucaoId) {
        try {
            execucaoService.finalizarExecucao(execucaoId);
        } catch (AppException ex) {
            //LOG.error("Erro ao finalizar a execucao {0}", ex, execucaoId);
        }
    }
}