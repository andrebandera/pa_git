package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.core.projeto.entidade.adm.OrgaoBca;
import br.gov.ms.detran.comum.core.projeto.enums.apo.EstadoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.iface.bca.IBeanIntegracao;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP11;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP13;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP14;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP17;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP22;
import br.gov.ms.detran.processo.administrativo.core.bo.AndamentoProcessoAdministrativoManager2;
import br.gov.ms.detran.processo.administrativo.core.bo.ConsistenciaBloqueioBCAAtualizacaoBO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP21BO;
import br.gov.ms.detran.processo.administrativo.core.integracao.IntegracaoMainFrameBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAHabilitacaoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.ConsistenciaBloqueioBCAAtualizacao;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.FunctionConsistenciaBloqueioBCAAtualizacao;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.MotivoDesbloqueioCnhEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.RequisitoRecursoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ParametrosIntegracaoBloqueioBCAWrapper;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Christiano Carrilho.
 */
@Stateless(mappedName = "ejb/BloqueioBCAService")
@Remote(IBloqueioBCAService.class)
public class BloqueioBCAService implements IBloqueioBCAService {

    @PersistenceContext(unitName = "DETRAN-PROCESSO-ADMINISTRATIVO-PU")
    private EntityManager em;

    private static final Logger LOG = Logger.getLogger(BloqueioBCAService.class);
    
     private IApoioService apoioService;
    
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    /**
     * Atualiza o andamento da execução do processo.
     * 
     * @param processo Informações do PA.
     * @throws AppException
     */
    void executarProximoAndamento(ProcessoAdministrativo processo) throws AppException {
        executarProximoAndamento(processo.getId());
    }

    /**
     * 
     * @param processoId
     * @throws AppException 
     */
    void executarProximoAndamento(Long processoId) throws AppException {
        new AndamentoProcessoAdministrativoManager2().proximoAndamento(em, processoId, null);
    }

    /**
     * Grava o log da falha ocorrida durante a integração com mainframe.
     * 
     * @param ex Erro lançado pelo sistema.
     * @param cpf CPF do condutor.
     * @param numeroProcesso Número Processo Administrativo.
     */
    void gravarFalha(Exception ex, String origemErro, String cpf, String numeroProcesso) {

        LOG.debug("Erro ao executar AEMNPP11", ex);

        IPAControleFalhaService falhaService = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");

        falhaService.gravarFalhaProcessoAdministrativo(ex, origemErro, cpf, numeroProcesso);
    }

    /**
     * 
     * @param cpf
     * @param mensagem
     * @param origem 
     */
    void gravarFalha(String cpf, String mensagem, String origem) {
        
        IPAControleFalhaService falhaService
            = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");

        falhaService.gravarFalhaEspecifica(cpf, mensagem, origem);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executarAEMNPP11(ProcessoAdministrativo processo, String pid) throws AppException{
        executarAEMNPP11(processo, pid, Boolean.TRUE);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executarAEMNPP11(ProcessoAdministrativo processo, String pid, Boolean proximoAndamento) throws AppException{

        try {
            
            if(proximoAndamento) {
                executarProximoAndamento(processo);
            }
            
            executarDesbloqueioAEMNPP11(processo, pid, null);

        } catch (AppException ex) {
            gravarFalha(ex, "Erro ao executar AEMNPP11", processo.getCpf(), null);
            DetranWebUtils.applicationMessageException("processoAdministrativoBCA.M3", null, ex.getParams());

        } catch (Exception ex) {
            gravarFalha(ex, "Erro ao executar AEMNPP11", processo.getCpf(), null);
            DetranWebUtils.applicationMessageException("Erro ao executar AEMNPP11.");
        }
    }

    @Override
    public void executarDesbloqueioAEMNPP11(ProcessoAdministrativo processo, String pid, String observacao) throws AppException {
        Boolean penaAnulada = verificaPenaAnulada(em, processo);
        ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();
        params.adicionarParametro(DetranStringUtil.preencherEspaco(processo.getCpf(), 11, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(processo.getCnh().toString(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(pid, 9, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 80, DetranStringUtil.TipoDadoEnum.ALFA));
        
        params.adicionarParametro(DetranStringUtil.preencherEspaco(penaAnulada ? "A":
                                                                   getMotivo(processo),
                                                                   1, DetranStringUtil.TipoDadoEnum.ALFA));
        if(DetranStringUtil.ehBrancoOuNulo(observacao)){
            params.adicionarParametro(DetranStringUtil.
                    preencherEspaco(penaAnulada ?
                                                    "PENA ANULADA - PROCESSO ARQUIVADO POR CASSACAO." :
                                                    "ENTREGOU CNH E REALIZOU CURSO",
                                    120,
                                    DetranStringUtil.TipoDadoEnum.ALFA)
            );
            
        }else{
            params.adicionarParametro(DetranStringUtil.
                    preencherEspaco(observacao,
                                    120,
                                    DetranStringUtil.TipoDadoEnum.ALFA)
            );
        }
        
        params.adicionarParametro(DetranStringUtil.preencherEspaco(processo.getOrigem().getSigla(), 3, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(getTipoProcesso(processo), 1, DetranStringUtil.TipoDadoEnum.ALFA));
        
        IResultadoIntegracao resultado = new IntegracaoMainFrameBO().executarAEMNPP11(params);
        
        if (resultado.isAbortarExecucao() || resultado.temMensagemDeErro()) {
            LOG.info("Erro ao executar AEMNPP11: {0}", resultado.getMensagem());
            DetranWebUtils.applicationMessageException("Não foi possível desbloquear o condutor na BCA." + resultado.getMensagem());
        }
        
        AEMNPP11 aemnpp11 = (AEMNPP11) resultado.getResultadoBean();
        if(aemnpp11 == null || DetranStringUtil.ehBrancoOuNulo(aemnpp11.getSituacaoDesbloqueio())
                || !aemnpp11.getSituacaoDesbloqueio().equals("1")){
            DetranWebUtils.applicationMessageException(aemnpp11.getMensagemDesbloqueio());
        }

    }

    private String getTipoProcesso(ProcessoAdministrativo processo) {
        String tipoProcesso = "";

        switch (processo.getTipo().name()) {
            case "SUSPENSAO_JUDICIAL":
                tipoProcesso = "D";
                break;
            case "CASSACAO_JUDICIAL":
                tipoProcesso = "E";
                break;
            case "SUSPENSAO":
                tipoProcesso = "1";
                break;
            case "CASSACAO":
                tipoProcesso = "2";
                break;
            case "CASSACAO_PERMIS_E_CANC_CNH":
                tipoProcesso = "3";
                break;
        }

        return tipoProcesso;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executarAEMNPP13(ParametrosIntegracaoBloqueioBCAWrapper wrapper, Boolean proximoAndamento) throws AppException {

        try {
            
            if(proximoAndamento) {
                executarProximoAndamento(wrapper.getPaTdcId());
            }

            ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();

            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getCpf(), 11, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getTipoProcesso().getTipoProcessoMainframe(), 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getNumeroProcesso(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getNumeroPortaria(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(Utils.formatDate(wrapper.getDataPublicacaoPortaria(), "yyyyMMdd"), 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getArtigoIncisoParagrafo(), 20, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getNumeroRestricaoSemBloqueio(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getMesesPenalizacao(), 2, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getAutoInfracao(), 10, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getCodigoInfracao(), 4, DetranStringUtil.TipoDadoEnum.NUMERICO));
            
            params.adicionarParametro(DetranStringUtil.preencherEspaco("", 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco("", 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco("", 80, DetranStringUtil.TipoDadoEnum.ALFA));
            
            params.adicionarParametro(DetranStringUtil.preencherEspaco(Utils.formatDate(wrapper.getDataInicioPenalidade(), "yyyyMMdd"), 8, DetranStringUtil.TipoDadoEnum.NUMERICO));    
            
            IResultadoIntegracao resultado
                = new IntegracaoMainFrameBO().executarAEMNPP13(params);

            if (resultado.isAbortarExecucao() || resultado.temMensagemDeErro()) {
                DetranWebUtils.applicationMessageException(resultado.getMensagem());
            }

            AEMNPP13 aemnpp13 = (AEMNPP13) resultado.getResultadoBean();

            if (aemnpp13 == null || DetranStringUtil.ehBrancoOuNulo(aemnpp13.getCodigoExecucao())) {
                DetranWebUtils.applicationMessageException("Não foi possível incluir o bloqueio na BCA.");
            }

            if (aemnpp13 != null && (aemnpp13.getCodigoExecucao().equals("0") || aemnpp13.getCodigoExecucao().equals("3"))) {
                DetranWebUtils.applicationMessageException("Não foi possível incluir o bloqueio na BCA.");
            }

            if (aemnpp13.getCodigoExecucao().equals("2")) {

                gravarFalha(
                        wrapper.getCpf(),
                        new StringBuilder()
                                .append("Algo ocorreu ao incluir Bloqueio na BCA. Número Processo: ")
                                .append(wrapper.getNumeroProcesso())
                                .append(". CPF condutor: ")
                                .append(wrapper.getCpf())
                                .append(". Mensagem retornada pela trasação AEMNPP13: ")
                                .append(aemnpp13.getMensagemExecucao()).toString(),
                        "BLOQUEIO_BCA"
                );
            }
            
        } catch (AppException ex) {
            
            gravarFalha(ex, "Erro ao executar AEMNPP13", wrapper != null ? wrapper.getCpf() : null, wrapper != null ? wrapper.getNumeroProcesso() : null);
            DetranWebUtils.applicationMessageException("processoAdministrativoBCA.M2", null, ex.getParams());

        } catch (Exception ex) {
            
            gravarFalha(ex, "Erro ao executar AEMNPP13", wrapper != null ? wrapper.getCpf() : null, wrapper != null ? wrapper.getNumeroProcesso() : null);
            DetranWebUtils.applicationMessageException("Não foi possível incluir o bloqueio na BCA.");
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executarAEMNPP13(ParametrosIntegracaoBloqueioBCAWrapper wrapper) throws AppException{
        executarAEMNPP13(wrapper, Boolean.TRUE);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executarAEMNPP14(ParametrosIntegracaoBloqueioBCAWrapper wrapper) throws AppException{
        executarAEMNPP14(wrapper, Boolean.TRUE);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executarAEMNPP14(ParametrosIntegracaoBloqueioBCAWrapper wrapper, Boolean proximoAndamento) throws AppException{

        try {
            
            if(proximoAndamento) {
                executarProximoAndamento(wrapper.getPaTdcId());
            }

            ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();

            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getCpf(), 11, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getTipoProcesso().getTipoProcessoMainframe(), 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getNumeroProcesso(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getNumeroPortaria(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(Utils.formatDate(wrapper.getDataPublicacaoPortaria(), "yyyyMMdd"), 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getArtigoIncisoParagrafo(), 20, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(Utils.formatDate(wrapper.getDataInicioPenalidade(), "yyyyMMdd"), 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getNumeroRestricao(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getMesesPenalizacao(), 2, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getAutoInfracao(), 10, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getCodigoInfracao(), 4, DetranStringUtil.TipoDadoEnum.NUMERICO));

            IResultadoIntegracao resultado = new IntegracaoMainFrameBO().executarAEMNPP14(params);

            if (resultado.isAbortarExecucao() || resultado.temMensagemDeErro()) {
                DetranWebUtils.applicationMessageException(resultado.getMensagem());
            }

            AEMNPP14 aemnpp14 = (AEMNPP14) resultado.getResultadoBean();
            
             if (aemnpp14 == null || aemnpp14.getCodigoExecucao() == null) {
                DetranWebUtils.applicationMessageException(resultado.getMensagem());
            }
            
            if (aemnpp14 != null && (aemnpp14.getCodigoExecucao().equals("0") || aemnpp14.getCodigoExecucao().equals("3"))) {
                DetranWebUtils.applicationMessageException("Não foi possível incluir o bloqueio na BCA.");
            }

            if (aemnpp14.getCodigoExecucao().equals("2")) {
                gravarFalha( 
                    wrapper.getCpf(), 
                        new StringBuilder()
                                .append("Algo ocorreu ao incluir Bloqueio na BCA. Número Processo: ")
                                .append(wrapper.getNumeroProcesso())
                                .append(". CPF condutor: ")
                                .append(wrapper.getCpf())
                                .append(". Mensagem retornada pela transação AEMNPP14: ")
                                .append(aemnpp14.getMensagemExecucao()).toString(),

                    "BLOQUEIO_BCA"
                );
            }

        } catch (AppException ex) {
            gravarFalha(ex, "Erro ao executar AEMNPP14", wrapper != null ? wrapper.getCpf() : null, wrapper != null ? wrapper.getNumeroProcesso() : null);
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, ex.getParams());

        } catch (Exception ex) {
            gravarFalha(ex, "Erro ao executar AEMNPP14", wrapper != null ? wrapper.getCpf() : null, wrapper != null ? wrapper.getNumeroProcesso() : null);
            DetranWebUtils.applicationMessageException("Não foi possível incluir o bloqueio na BCA.");
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executarAEMNPP25(ProcessoAdministrativo processo) throws AppException{

        try {
            
            ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();
            params.adicionarParametro(DetranStringUtil.preencherEspaco(processo.getCpf(), 11, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(processo.getCnh().toString(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(processo.getTipo().getTipoProcessoMainframe(), 9, DetranStringUtil.TipoDadoEnum.NUMERICO));
            
            IResultadoIntegracao resultado = new IntegracaoMainFrameBO().executarAEMNPP25(params);

            if (resultado.isAbortarExecucao() || resultado.temMensagemDeErro()) {
                LOG.info("Erro ao executar AEMNPP25: {0}", resultado.getMensagem());
                DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, "AEMNPP25 - " + resultado.getMensagem());
            }

        } catch (AppException ex) {
            gravarFalha(ex, "Erro ao executar AEMNPP25", processo.getCpf(), null);
            DetranWebUtils.applicationMessageException("Erro ao executar AEMNPP25.");

        } catch (Exception ex) {
            gravarFalha(ex, "Erro ao executar AEMNPP25", processo.getCpf(), null);
            DetranWebUtils.applicationMessageException("Erro ao executar AEMNPP25.");
        }
    }
    
    /**
     * @param dadoProcessoJudicial
     * @param bloqueio
     * @param penalidade
     * @throws AppException 
     */
    @Override
    public void executarAEMNPP17(
        DadoProcessoJudicial dadoProcessoJudicial, BloqueioBCA bloqueio, PAPenalidadeProcesso penalidade) throws AppException {
        
        ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();
        
        ProcessoAdministrativo pa = dadoProcessoJudicial.getProcessoJudicial().getProcessoAdministrativo();
        
        // CPF
        params.adicionarParametro(DetranStringUtil.preencherEspaco(pa.getCpf(), 11, DetranStringUtil.TipoDadoEnum.NUMERICO));
        
        // NUMERO PROCESSO
        params.adicionarParametro(DetranStringUtil.preencherEspaco(pa.getNumeroProcesso(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
        
        // RESTRIÇÃO
        if (TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(pa.getTipo())) {
            params.adicionarParametro(DetranStringUtil.preencherEspaco("D", 1, DetranStringUtil.TipoDadoEnum.ALFA));
        } else if (TipoProcessoEnum.CASSACAO_JUDICIAL.equals(pa.getTipo())) {
            params.adicionarParametro(DetranStringUtil.preencherEspaco("E", 1, DetranStringUtil.TipoDadoEnum.ALFA));
        } else {
            DetranWebUtils.applicationMessageException("Tipo de processo não permitido para execução.");
        }
        
        // Número da Restrição	
        params.adicionarParametro(DetranStringUtil.preencherEspaco(bloqueio.getNumeroBloqueioBCA(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
        
        // Data de Inicio da Penalização	
        if (null == penalidade) {
            params.adicionarParametro(DetranStringUtil.preencherEspaco(Utils.formatDate(bloqueio.getDataInicio(), "yyyyMMdd"), 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
        } else {
            params.adicionarParametro(DetranStringUtil.preencherEspaco(Utils.formatDate(penalidade.getDataInicioPenalidade(), "yyyyMMdd"), 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
        }
        
        // Dias de Penalização	
        PAComplemento pAComplemento 
            = new PAComplementoRepositorio()
                .getPAComplementoPorParametroEAtivo(
                    em, 
                    pa, 
                    PAParametroEnum.TEMPO_PENALIDADE
                );
        
        params.adicionarParametro(DetranStringUtil.preencherEspaco(pAComplemento.getValor(), 4, DetranStringUtil.TipoDadoEnum.NUMERICO));
        
        // CNH Recolhida	
        Long cnhControleId = new PAHabilitacaoRepositorio().getCnhSituacaoEntregaSemAcaoDeDevolucaoDaCnhParaCpfCondutor(em, pa.getCpf());
        
        if (null == cnhControleId) {
            
            if (null == dadoProcessoJudicial.getIdentificacaoRecolhimentoCnh()) {
                DetranWebUtils.applicationMessageException("Eror ao recuperar identificacao recolhimento cnh.");
            }
                
            switch (dadoProcessoJudicial.getIdentificacaoRecolhimentoCnh()) {
                case INEXISTENTE:
                    params.adicionarParametro(DetranStringUtil.preencherEspaco("N", 1, DetranStringUtil.TipoDadoEnum.ALFA));
                    break;
                case DETRAN:
                    params.adicionarParametro(DetranStringUtil.preencherEspaco("S", 1, DetranStringUtil.TipoDadoEnum.ALFA));
                    break;
                case CARTORIO_JUDICIARIO:
                    params.adicionarParametro(DetranStringUtil.preencherEspaco("S", 1, DetranStringUtil.TipoDadoEnum.ALFA));
                    break;
                default:
                    DetranWebUtils.applicationMessageException("Eror ao recuperar identificacao recolhimento cnh.");
                    break;
            }
            
        } else {
            params.adicionarParametro(DetranStringUtil.preencherEspaco("S", 1, DetranStringUtil.TipoDadoEnum.ALFA));
        }
        
        OrgaoBca orgaoBca = (OrgaoBca) getApoioService().getOrgaoBcaPorID(dadoProcessoJudicial.getOrgaoBca());
        
        if (null != orgaoBca) {
            
            // UF BLOQUEIO
            if(orgaoBca.getUf() != null) {
                params.adicionarParametro(DetranStringUtil.preencherEspaco(orgaoBca.getUf().getSigla(), 2, DetranStringUtil.TipoDadoEnum.ALFA));
            } else {
                params.adicionarParametro(DetranStringUtil.preencherEspaco(EstadoEnum.MS.name(), 2, DetranStringUtil.TipoDadoEnum.ALFA));
            }
            
            // CÓDIGO ORGAO BLOQUEIO
            params.adicionarParametro(DetranStringUtil.preencherEspaco(orgaoBca.getCodigo().toString(), 2, DetranStringUtil.TipoDadoEnum.NUMERICO));
        }
        
        // MENSAGEM BLOQUEIO
        params.adicionarParametro(DetranStringUtil.preencherEspaco(bloqueio.getMotivoBloqueio().name(), 120, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
        
        // DATA BLOQUEIO
        params.adicionarParametro(DetranStringUtil.preencherEspaco(Utils.formatDate(bloqueio.getDataInicio(), "yyyyMMdd"), 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
        
        
        // CODIGO EXECUCAO
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        
        
        // MENSAGEM EXECUCAO
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 80, DetranStringUtil.TipoDadoEnum.ALFA));
        
        
        // REQUER CURSO
        if(RequisitoRecursoBloqueioEnum.SEM_CURSO.equals(dadoProcessoJudicial.getRequisitoCursoBloqueio())){
            params.adicionarParametro(DetranStringUtil.preencherEspaco("N", 1, DetranStringUtil.TipoDadoEnum.ALFA));
        }else{
            params.adicionarParametro(DetranStringUtil.preencherEspaco("S", 1, DetranStringUtil.TipoDadoEnum.ALFA));
        }

        // REQUER PROVA
        if(RequisitoRecursoBloqueioEnum.CURSO_EXAMES.equals(dadoProcessoJudicial.getRequisitoCursoBloqueio())
                || BooleanEnum.SIM.equals(dadoProcessoJudicial.getIndicativoProva())){
            params.adicionarParametro(DetranStringUtil.preencherEspaco("S", 1, DetranStringUtil.TipoDadoEnum.ALFA));
        } else{
            params.adicionarParametro(DetranStringUtil.preencherEspaco("N", 1, DetranStringUtil.TipoDadoEnum.ALFA));
        }
        
        IResultadoIntegracao iResultadoIntegracao = new IntegracaoMainFrameBO().executarAEMNPP17(params);
        
        if (iResultadoIntegracao.isAbortarExecucao() || iResultadoIntegracao.temMensagemDeErro()) {
            DetranWebUtils.applicationMessageException(iResultadoIntegracao.getMensagem());
        }

        AEMNPP17 aemnpp17 = (AEMNPP17) iResultadoIntegracao.getResultadoBean();

        if (aemnpp17 == null || DetranStringUtil.ehBrancoOuNulo(aemnpp17.getCodigoExecucao())) {
            DetranWebUtils.applicationMessageException("Não foi possível incluir o bloqueio na BCA.");
        }

        if (aemnpp17 != null && (aemnpp17.getCodigoExecucao().equals("0"))) {
            DetranWebUtils.applicationMessageException("Não foi possível incluir o bloqueio na BCA.");
        }

        if (aemnpp17.getCodigoExecucao().equals("2")) {

            gravarFalha(
                    pa.getCpf(),
                    new StringBuilder()
                            .append("Algo ocorreu ao incluir Bloqueio na BCA. Número Processo: ")
                            .append(pa.getNumeroProcesso())
                            .append(". CPF condutor: ")
                            .append(pa.getCpf())
                            .append(". Mensagem retornada pela trasação AEMNPP17: ")
                            .append(aemnpp17.getMsgExecucao()).toString(),
                    "BLOQUEIO_BCA_PJU"
            );
        }
    }

    private Boolean verificaPenaAnulada(EntityManager em, ProcessoAdministrativo processo) throws AppException {
        
        BloqueioBCA bloqueio = new BloqueioBCARepositorio().getBloqueioBcaPorPaEAtivo(em, processo.getId());
        
        if(bloqueio == null || !SituacaoBloqueioBCAEnum.FINALIZADO.equals(bloqueio.getSituacao())){
            DetranWebUtils.applicationMessageException("Não foi possível recuperar o desbloqueio desse processo no sistema.");
        }
        
        return MotivoDesbloqueioCnhEnum.PENA_ANULADA.equals(bloqueio.getMotivoDesbloqueio());
    }

    private String getMotivo(ProcessoAdministrativo processo) {
        String motivoLiberacao = "C";
        
        if(TipoProcessoEnum.CASSACAO_JUDICIAL.equals(processo.getTipo())
                || TipoProcessoEnum.CASSACAO.equals(processo.getTipo())){
            motivoLiberacao = "R";
        }
            
        if(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(processo.getTipo())){
            motivoLiberacao = "6";
        }
        return motivoLiberacao;
    }
    
    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public List<ConsistenciaBloqueioBCAAtualizacao> registraConsultaBloqueioLocal(List<FunctionConsistenciaBloqueioBCAAtualizacao> lProcesso, AEMNPP22 aemnpp22) throws AppException {
        return new ConsistenciaBloqueioBCAAtualizacaoBO().registraConsultaBloqueioLocal(em, lProcesso, aemnpp22);
    }
    
    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public List<ConsistenciaBloqueioBCAAtualizacao> registraConsultaBloqueioNacional(List<FunctionConsistenciaBloqueioBCAAtualizacao> lProcesso, AEMNPP22 aemnpp22) throws AppException {
        return new ConsistenciaBloqueioBCAAtualizacaoBO().registraConsultaBloqueioNacional(em, lProcesso, aemnpp22);
    }
    
    @Override
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public List<ConsistenciaBloqueioBCAAtualizacao> registraConsultaBloqueioWeb(List<FunctionConsistenciaBloqueioBCAAtualizacao> lProcesso, AEMNPP22 aemnpp22) throws AppException {
        return new ConsistenciaBloqueioBCAAtualizacaoBO().registraConsultaBloqueioWeb(em, lProcesso, aemnpp22);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IBeanIntegracao executaGravacaoBloqueioBCA(Object parametroEnvioIntegracao) throws AppException {
        return new AEMNPP21BO().executarIntegracaoAEMNPP21((ParametroEnvioIntegracao) parametroEnvioIntegracao);
    }
}