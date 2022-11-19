package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.AndamentoProcessoAdministrativoManager2;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAInicioFluxoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAInicioFluxo;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.PAAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless(mappedName = "ejb/PAAndamentoService")
@Remote(IPAAndamentoService.class)
public class PAAndamentoService implements IPAAndamentoService {

    private static final Logger LOG = Logger.getLogger(PAAndamentoService.class);

    @PersistenceContext(unitName = "DETRAN-PROCESSO-ADMINISTRATIVO-PU")
    private EntityManager em;

    private IPAControleFalhaService falhaService;

    public IPAControleFalhaService getFalhaService() {

        if (falhaService == null) {
            falhaService = (IPAControleFalhaService) JNDIUtil.lookup("ejb/PAControleFalhaService");
        }

        return falhaService;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executa(ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {

        PAAndamentoEnum andamentoEnum = null;

        try {

            andamentoEnum = defineAndamento(andamentoEspecificoWrapper);

            RetornoExecucaoAndamentoWrapper retornoWrapper
                    = andamentoEnum.getAndamento().executa(em, andamentoEspecificoWrapper);

            if (retornoWrapper == null || retornoWrapper.getTipo() == null) {
                DetranWebUtils.applicationMessageException("PAAndamentoServiceLab.M1");
            }

            if (TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO.equals(retornoWrapper.getTipo())) {

                new AndamentoProcessoAdministrativoManager2()
                        .proximoAndamento(em, andamentoEspecificoWrapper.getProcessoAdministrativo().getId(), null);

            } else if (TipoRetornoAndamentoEnum.INICIA_FLUXO.equals(retornoWrapper.getTipo())) {

                if (retornoWrapper.getCodigoFluxo() == null) {
                    DetranWebUtils.applicationMessageException("PAAndamentoServiceLab.M2");
                }

                new AndamentoProcessoAdministrativoManager2()
                        .iniciarFluxo(em, andamentoEspecificoWrapper.getProcessoAdministrativo(), retornoWrapper.getCodigoFluxo());

            } else if (TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO.equals(retornoWrapper.getTipo())) {

                if (retornoWrapper.getCodigoFluxo() == null) {
                    DetranWebUtils.applicationMessageException("PAAndamentoServiceLab.M2");
                }

                if (retornoWrapper.getCodigoAndamento() == null) {
                    DetranWebUtils.applicationMessageException("PAAndamentoServiceLab.M3");
                }

                new AndamentoProcessoAdministrativoManager2()
                        .executarMudancaFluxoEAndamento(
                                em,
                                andamentoEspecificoWrapper.getProcessoAdministrativo(),
                                retornoWrapper.getCodigoFluxo(),
                                retornoWrapper.getCodigoAndamento()
                        );
            }

        } catch (Exception e) {

            LOG.debug("Erro ao executar andamento.", e);

            getFalhaService()
                    .gravarFalhaEspecifica(
                            andamentoEspecificoWrapper.getProcessoAdministrativo().getCpf(),
                            "Falha ao executar andamento para o PA " + andamentoEspecificoWrapper.getProcessoAdministrativo().getNumeroProcesso(),
                            andamentoEnum != null ? andamentoEnum.name() : ""
                    );

            throw e;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public RetornoExecucaoAndamentoWrapper executaAndamentoSemAlterarAndamentoOuFluxo(ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {

        RetornoExecucaoAndamentoWrapper retorno = null;
        PAAndamentoEnum andamentoEnum = null;

        try {

            andamentoEnum = defineAndamento(andamentoEspecificoWrapper);

            retorno = andamentoEnum.getAndamento().executa(em, andamentoEspecificoWrapper);

            if (retorno == null) {
                DetranWebUtils.applicationMessageException("PAAndamentoServiceLab.M1");
            }

        } catch (Exception e) {

            LOG.debug("Erro ao executar andamento.", e);

            getFalhaService()
                    .gravarFalhaEspecifica(
                            andamentoEspecificoWrapper.getProcessoAdministrativo().getCpf(),
                            "Falha ao executar andamento para o PA " + andamentoEspecificoWrapper.getProcessoAdministrativo().getNumeroProcesso(),
                            andamentoEnum != null ? andamentoEnum.name() : ""
                    );

            throw e;
        }

        return retorno;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void iniciaExecucao(ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {

        PAInicioFluxoRepositorio inicioFluxoRepo = new PAInicioFluxoRepositorio();

        PAInicioFluxo inicioFluxo = inicioFluxoRepo.
                getPAInicioFluxoAtivoPorProcessoAdministrativo(em, andamentoEspecificoWrapper.getProcessoAdministrativo().getId());

        PAOcorrenciaStatusRepositorio ocorrenciaRepo = new PAOcorrenciaStatusRepositorio();
        
        if(!DetranStringUtil.ehBrancoOuNulo(andamentoEspecificoWrapper.getUsuarioAlteracao())){
            PAOcorrenciaStatus paOcorrenciaStatus = ocorrenciaRepo.getPAOcorrenciaStatusAtiva(em, andamentoEspecificoWrapper.getProcessoAdministrativo().getId());

            paOcorrenciaStatus.setUsuarioAlteracao(andamentoEspecificoWrapper.getUsuarioAlteracao());
            
            paOcorrenciaStatus.setDefineUsuarioSessao(Boolean.FALSE);
        
            ocorrenciaRepo.update(em, paOcorrenciaStatus);
        }
        
        if (inicioFluxo != null) {

            new AndamentoProcessoAdministrativoManager2().
                    iniciarFluxo(em, inicioFluxo.getProcessoAdministrativo(), inicioFluxo.getFluxoProcesso().getCodigo());

            inicioFluxo.setAtivo(AtivoEnum.DESATIVADO);
            inicioFluxoRepo.update(em, inicioFluxo);
        } else {
            executa(andamentoEspecificoWrapper);
        }
    }

    /**
     *
     * @param andamentoEspecificoWrapper
     * @return
     * @throws AppException
     */
    private PAAndamentoEnum defineAndamento(ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {
        PAAndamentoEnum andamentoEnum = null;

        if (null != andamentoEspecificoWrapper.getProcessoAdministrativo()) {
            andamentoEnum = executaPorNumeroProcesso(andamentoEspecificoWrapper);
        }

        if (andamentoEnum == null || andamentoEnum.getAndamento() == null) {
            DetranWebUtils.applicationMessageException("execucaoandamentoespecifico.M1");
        }

        return andamentoEnum;
    }

    /**
     *
     * @param processo
     * @throws AppException
     */
    private void validar(ProcessoAdministrativo processo) throws AppException {

        if (processo == null || (null == processo.getId() && DetranStringUtil.ehBrancoOuNulo(processo.getNumeroProcesso()))) {
            DetranWebUtils.applicationMessageException("Favor informar o número do Processo e Andamento.");
        }

    }

    /**
     *
     * @param andamentoEspecificoWrapper
     * @return
     * @throws AppException
     */
    private PAAndamentoEnum executaPorNumeroProcesso(ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {

        PAAndamentoEnum andamentoEnum = null;

        validar(andamentoEspecificoWrapper.getProcessoAdministrativo());

        PAOcorrenciaStatus ocorrenciaStatus
                = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, andamentoEspecificoWrapper.getProcessoAdministrativo().getId());

        if (ocorrenciaStatus == null) {
            DetranWebUtils.applicationMessageException("execucaoandamentoespecifico.M2");
        }

        try {

            andamentoEnum
                    = PAAndamentoEnum
                            .valueOf(
                                    "ANDAMENTO_"
                                    + ocorrenciaStatus
                                            .getStatusAndamento().getAndamentoProcesso().getCodigo().toString()
                            );

        } catch (IllegalArgumentException e) {
            DetranWebUtils.applicationMessageException("execucaoandamentoespecifico.M1");
        }

        return andamentoEnum;
    }
}
