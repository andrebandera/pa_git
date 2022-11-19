package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.jspa.JSPANumeroSequencialObjetoCorreio;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.service.FabricaJSPASequencial;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ObjetoCorrespondenciaCorreioWrapper;
import br.gov.ms.detran.protocolo.entidade.Correspondencia;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

public class NotificacaoProcessoAdministrativoRepositorio extends AbstractJpaDAORepository<NotificacaoProcessoAdministrativo> {
    
    private static final Logger LOG = Logger.getLogger(NotificacaoProcessoAdministrativoRepositorio.class);
    
    /**
     * 
     * @param em
     * @param codigoAndamento
     * @return
     * @throws DatabaseException 
     */
    public List getListNotificacoesPorAndamentoIniciado(EntityManager em, Integer codigoAndamento) throws DatabaseException {
        
        Object[] params = {
            codigoAndamento, 
            AtivoEnum.ATIVO,
            SituacaoOcorrenciaEnum.INICIADO
        };
        
        return getListNamedQuery(em, "NotificacaoProcessoAdministrativo.getListNotificacoesPorAndamentoIniciado", params);
    }

    /**
     * @param em
     * @param codigosAndamentos
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public List<NotificacaoProcessoAdministrativo> getNotificadosPorAndamentoESituacao(
        EntityManager em, List<Integer> codigosAndamentos) throws AppException {
        
        if(DetranCollectionUtil.ehNuloOuVazio(codigosAndamentos)) {
            DetranWebUtils.applicationMessageException("Parâmetro obrigatório inválido.");
        }
        
        return 
            getListNamedQuery(
                em, 
                "NotificacaoProcessoAdministrativo.getNotificados", 
                codigosAndamentos,
                SituacaoOcorrenciaEnum.INICIADO,
                AtivoEnum.ATIVO
            );
    }

    /**
     * @param em
     * @param numeroNotificacao
     * @param numeroProcesso
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public NotificacaoProcessoAdministrativo getNotificacaoPorNumeroNotificacaoENumeroProcesso(
        EntityManager em, String numeroNotificacao, String numeroProcesso) throws AppException {
        
        if(numeroNotificacao == null || DetranStringUtil.ehBrancoOuNulo(numeroProcesso)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return 
            getNamedQuery(
                em, 
                "NotificacaoProcessoAdministrativo.getNotificacaoPorNumeroNotificacaoENumeroProcesso", 
                numeroNotificacao,
                numeroProcesso,
                AtivoEnum.ATIVO
            );
    }

    /**
     * @param em
     * @param numeroProcesso
     * @param tipo
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public NotificacaoProcessoAdministrativo getNotificacaoPorNumeroProcessoETipo(
        EntityManager em, String numeroProcesso, TipoFasePaEnum tipo) throws DatabaseException {
        
        return 
            super.getNamedQuery(
                em, 
                "NotificacaoProcessoAdministrativo.getNotificacaoPorNumeroProcessoETipo", 
                numeroProcesso,
                tipo,
                AtivoEnum.ATIVO
            );
    }
    
    /**
     * @param em
     * @param idProcessoAdministrativo
     * @param tipoNotificacao
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public NotificacaoProcessoAdministrativo getNotificacaoPorProcessoAdministrativoETipoNotificacao(
        EntityManager em, Long idProcessoAdministrativo, TipoFasePaEnum tipoNotificacao) throws DatabaseException {
        
        return 
            super.getNamedQuery(
                em, 
                "NotificacaoProcessoAdministrativo.getNotificacaoPorProcessoAdministrativoETipoNotificacao", 
                idProcessoAdministrativo,
                tipoNotificacao,
                AtivoEnum.ATIVO
            );
    }
    
    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @param fluxoFase
     * @return
     * @throws DatabaseException
     * @throws AppException 
     */
    public NotificacaoProcessoAdministrativo getNotificacaoPorNumeroProcessoETipo(EntityManager em, 
                                                                                  ProcessoAdministrativo processoAdministrativo, 
                                                                                  PAFluxoFase fluxoFase) throws AppException {
        
        if (processoAdministrativo == null 
                || processoAdministrativo.getId() == null
                || fluxoFase == null 
                || fluxoFase.getId() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<NotificacaoProcessoAdministrativo> lNotificacao = 
                getListNamedQuery(
                        em, 
                        "NotificacaoProcessoAdministrativo.getNotificacaoPAPorFluxoFase", 
                        processoAdministrativo.getId(), 
                        fluxoFase.getPrioridadeFluxoAmparo().getFaseProcessoAdm().getCodigo().toString().substring(0, 2)+"%", 
                        AtivoEnum.ATIVO
                );
        
        if (DetranCollectionUtil.ehNuloOuVazio(lNotificacao)) {
            DetranWebUtils.applicationMessageException("Notificação não encontrada para o processo administrativo.");
        } else if (lNotificacao.size() > 1) {
            DetranWebUtils.applicationMessageException("Processo administrativo com mais de uma notificação.");
        }
        
        return lNotificacao.get(0);
    }
    
    /**
     * 
     * @param em
     * @param correspondencia
     * @return
     * @throws AppException 
     */
    public NotificacaoProcessoAdministrativo getNotificacaoProcessoAdministrativoPorCorrespondencia(
        EntityManager em, Correspondencia correspondencia) throws AppException {
        
        if(correspondencia == null || correspondencia.getId() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<NotificacaoProcessoAdministrativo> lNotificacao 
            = getListNamedQuery(
                em, 
                "NotificacaoProcessoAdministrativo.getNotificacaoProcessoAdministrativoPorCorrespondencia", 
                correspondencia.getId(), 
                AtivoEnum.ATIVO
            );
        
        if(!DetranCollectionUtil.ehNuloOuVazio(lNotificacao) && lNotificacao.size() > 1) {
            DetranWebUtils.applicationMessageException("PA.M1");
        }
        
        return lNotificacao.get(0);
        
    }
    
    /**
     * 
     * Utiliza SP_TAA_SEQUENCIA_OBJETO_CORREIO
     *
     * @param setor
     * @TIPO SMALLINT,
     * @SETOR_TIPO INT OUTPUT,
     * @IDENTIFICADOR CHAR(2) OUTPUT,
     * @OBJETO BIGINT OUTPUT,
     * @MSG
     * 
     * @param em
     * @return
     * @throws AppException 
     */
    public ObjetoCorrespondenciaCorreioWrapper 
        getObjetoCorrespondenciaCorreio(EntityManager em, String setor) throws AppException {
        
        final Integer TIPO_OBJETO_CORREIO = 2;
        
        ObjetoCorrespondenciaCorreioWrapper wrapper = new ObjetoCorrespondenciaCorreioWrapper();
        
        try {
            
            JSPANumeroSequencialObjetoCorreio jspa = new JSPANumeroSequencialObjetoCorreio();
            jspa.setTipo(TIPO_OBJETO_CORREIO);
            jspa.setSetor(setor);

            jspa = (JSPANumeroSequencialObjetoCorreio) 
                    new FabricaJSPASequencial().getStoredProcedureService().executarStoredProcedure(jspa);
            
            wrapper.setIdentificador(jspa.getIdentificador());
            wrapper.setMensagem(jspa.getMensagem());
            wrapper.setObjetoSequencia(jspa.getObjetoSequencia());
            wrapper.setSetorTipo(jspa.getSetorTipo());
            wrapper.setTipo(jspa.getTipo());
            
        } catch(Exception e) {
            
            LOG.error("Erro sem tratamento.", e);
            DetranWebUtils.applicationMessageException("Execução procedure: SP_TAA_SEQUENCIA_OBJETO_CORREIO inválida.");
        }
            
        return wrapper;
    }
    
    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    public List getListaNotificacaoPorNumeroProcessoParaWSConsultaPA(
        EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        if (processoAdministrativo == null 
                || processoAdministrativo.getId() == null
                || DetranStringUtil.ehBrancoOuNulo(processoAdministrativo.getNumeroProcesso())) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List lNotificacao = 
            getListNamedQuery(
                em, 
                "NotificacaoProcessoAdministrativo.getListaNotificacaoPorNumeroProcessoParaWSConsultaPA", 
                processoAdministrativo.getNumeroProcesso(), 
                AtivoEnum.ATIVO
            );
        
        return lNotificacao;
    }

    /**
     * @param em
     * @param codigosAndamentos
     * @return
     * @throws AppException 
     */
    public List<NotificacaoProcessoAdministrativo> getNotificadosPorAndamentoESituacaoParaRetornoAR(EntityManager em, 
                                                                                                    List<Integer> codigosAndamentos) throws AppException {
        if (DetranCollectionUtil.ehNuloOuVazio(codigosAndamentos)) {
            DetranWebUtils.applicationMessageException("Parâmetro obrigatório inválido.");
        }
        
        return getListNamedQuery(em, 
                                 "NotificacaoProcessoAdministrativo.getNotificadosParaRetornoAr", 
                                 codigosAndamentos,
                                 SituacaoOcorrenciaEnum.INICIADO,
                                 AtivoEnum.ATIVO);
    }

    public List<NotificacaoProcessoAdministrativo> getListNotificacoesAtivasPA(EntityManager em, Long idPA) throws DatabaseException {
        return getListNamedQuery(em, "NotificacaoProcessoAdministrativo.getListNotificacoesAtivasPA", idPA, AtivoEnum.ATIVO);
    }
    
    /**
     * @param em
     * @param idProcessoAdministrativo
     * @param tipoNotificacao
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public void existeNotificacaoAtivaPorProcessoAdministrativoETipoNotificacao(
        EntityManager em, Long idProcessoAdministrativo, TipoFasePaEnum tipoNotificacao) throws AppException {
        
        List<NotificacaoProcessoAdministrativo> listaNotificacao 
            = super.getListNamedQuery(
                em, 
                "NotificacaoProcessoAdministrativo.existeNotificacaoAtivaPorProcessoAdministrativoETipoNotificacao", 
                idProcessoAdministrativo,
                tipoNotificacao,
                AtivoEnum.ATIVO
            );
        
        if(DetranCollectionUtil.ehNuloOuVazio(listaNotificacao) || listaNotificacao.size() != 1) {
            DetranWebUtils.applicationMessageException("Processo Administrativo sem notificaçÃo vÁlida.");
        }
    }

    public NotificacaoProcessoAdministrativo getNotificacaoPorObjetoCorreios(EntityManager em, String objetoCorreios) throws DatabaseException {
        return getNamedQuery(em, "NotificacaoProcessoAdministrativo.getNotificacaoPorObjetoCorreios", objetoCorreios, AtivoEnum.ATIVO);
    }
    
    /**
     * 
     * @param em
     * @param dataNotificacao
     * @return
     * @throws DatabaseException 
     */
    public Long getQuantidadeNotificacaoPorDataNotificacao(EntityManager em, Date dataNotificacao) throws DatabaseException {
        
        Object[] params = {
            dataNotificacao,
            AtivoEnum.ATIVO
        };
        
        return getCountHqlEntitySearch(em, "NotificacaoProcessoAdministrativo.getQuantidadeNotificacaoPorDataNotificacao", params);
    }
    
    /**
     * @param em
     * @param idProcessoAdministrativo
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List<NotificacaoProcessoAdministrativo> getListNotificacaoPorProcessoAdministrativo(
        EntityManager em, Long idProcessoAdministrativo) throws DatabaseException {
        
        return 
            super.getListNamedQuery(
                em, 
                "NotificacaoProcessoAdministrativo.getListNotificacaoPorProcessoAdministrativo", 
                idProcessoAdministrativo,
                AtivoEnum.ATIVO
            );
    }
}