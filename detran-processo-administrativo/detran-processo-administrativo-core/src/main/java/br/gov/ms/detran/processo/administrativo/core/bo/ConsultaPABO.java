package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.TemplateProtocolo;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoNotificacaoEnum;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.wrapper.apo.NotificacaoWrapper;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.core.iface.servico.hab.IControleCnhService;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentacaoPARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoBloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoCnhRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PADocumentoPessoaRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoJsonRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProtocoloRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoMovimentoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.entidade.Movimentacao;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoBloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoJson;
import br.gov.ms.detran.processo.administrativo.entidade.Protocolo;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.DesistenciaRecursoInstauracaoPenalizacaoWS;
import br.gov.ms.detran.processo.administrativo.wrapper.PADocumentoPessoaWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.BloqueioBCAWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.ConsultaGeralPAWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.ConsultaPaWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.InformacoesEntregaCnhWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.MovimentacaoPaWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.PABPMSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.ProcessoAdministrativoDesistenteRetornoWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoCanceladoRetornoWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursosWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RetornosARWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Lillydi
 */
public class ConsultaPABO {
    
    private static final Logger LOG = Logger.getLogger(ConsultaPABO.class);
    
    private IApoioService apoioService;
    
    private IControleCnhService controleCnhService;
    
    private IPAControleFalhaService paControleFalha;
    
    /**
     * 
     * @return 
     */
    IPAControleFalhaService getControleFalha() {
        if (paControleFalha == null) {
            paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
        }
        return paControleFalha;
    }
    
    public IApoioService getApoioService() {
        
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        
        return apoioService;
    }
    
    public IControleCnhService getControleCnhService() {
        
        if (controleCnhService == null) {
            controleCnhService = (IControleCnhService) JNDIUtil.lookup("ejb/ControleCnhService");
        }
        
        return controleCnhService;
    }

    /**
     * @param em
     * @param numeroProcesso
     * @return
     * @throws AppException 
     */
    public ConsultaPaWSWrapper montarConsultaPA(EntityManager em, String numeroProcesso) throws AppException {
        
        ProcessoAdministrativo processoAdministrativo 
            = new ProcessoAdministrativoRepositorio()
                        .getProcessoAdministrativoPorNumeroProcessoSemAtivo(em, numeroProcesso);
        
        if(processoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        ConsultaPaWSWrapper wrapper = new ConsultaPaWSWrapper();
        
        /** Geral. **/
        wrapper.setGeral(montarConsultaGeral(em, processoAdministrativo));
        
        /** Instauração. **/
        wrapper.setInstauracao(montarConsultaInstauracao(em, processoAdministrativo));
        
        /** Notificações. **/
        wrapper.setNotificacoes(montarConsultaNotificacoes(em, processoAdministrativo));
        
        /** Recursos. **/
        wrapper.setRecursos(montarConsultaRecursos(em, processoAdministrativo));

        /** Recursos Cancelados. **/
        wrapper.setRecursosCancelados(montarRecursosCancelados(em, processoAdministrativo));
        
        /** Bloqueios BCA. **/
        wrapper.setBloqueiosBca(montarBloqueiosBCA(em, processoAdministrativo));
        
        /** Entrega CNH. **/
        wrapper.setEntregaCnh(montarConsultaEntregaCnh(em, processoAdministrativo));
        
        /** Informações Prova. **/
        wrapper.setInformacaoProva(new ProcessoAdministrativoBO().getInformacoesProva(em, processoAdministrativo));
        
        /** Movimentações. **/
        wrapper.setMovimentacoes(montarMovimentacoes(em, processoAdministrativo));
        
        /** Desistencia Recurso Instauracao Penalizacao **/
        wrapper.setDesistenciaRecursoInstauracaoPenalizacao(montarDesistenciaRecursoInstauracaoPenalizacao(em, processoAdministrativo));
        
        /** DESISTENTES **/
        wrapper.setDesistente(montarDesistentes(em, processoAdministrativo));

        return wrapper;
    }

    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @return 
     */
    private ConsultaGeralPAWrapper montarConsultaGeral(EntityManager em, ProcessoAdministrativo processoAdministrativo) {
        
        ConsultaGeralPAWrapper wrapper = new ConsultaGeralPAWrapper();
        
        try{
            
            PAOcorrenciaStatus ocorrenciaStatus 
                = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtivaPorNumeroProcesso(em, processoAdministrativo.getNumeroProcesso());
            
            PAFluxoFase fluxo = new PAFluxoFaseRepositorio().getFluxoFaseDoProcessoAdministrativo(em, ocorrenciaStatus.getProcessoAdministrativo());

            wrapper.setAndamento(ocorrenciaStatus.getStatusAndamento().getAndamentoProcesso().getDescricao());
            wrapper.setCpfCondutor(ocorrenciaStatus.getProcessoAdministrativo().getCpf());
            wrapper.setStatus(ocorrenciaStatus.getStatusAndamento().getStatus().getDescricao());
            
            wrapper.setTipoCenario(
                                   ocorrenciaStatus.getProcessoAdministrativo().getTipo().toString()
                                    .concat(" - ")
                                    .concat(ocorrenciaStatus.getProcessoAdministrativo().getOrigemApoio().getDescricao()));
            
            wrapper.setNumeroProcesso(ocorrenciaStatus.getProcessoAdministrativo().getNumeroProcesso());
            wrapper.setFluxo(ocorrenciaStatus.getFluxoProcesso().getDescricao());
            wrapper.setFase(fluxo.getPrioridadeFluxoAmparo().getFaseProcessoAdm().getDescricao());
            
        } catch(AppException e){
            //LOG.error("Error", e);
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
    private PABPMSWrapper montarConsultaInstauracao(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        PABPMSWrapper wrapper = null;
        
        try {
        
            ProcessoAdministrativoJson instauradoJson = 
                    new ProcessoAdministrativoJsonRepositorio()
                            .getProcessoAdministrativoJsonPorProcessoAdministrativoAtivo(em, processoAdministrativo.getId());

            if (instauradoJson != null) {
                wrapper = (PABPMSWrapper) DetranStringUtil.getInstance().fromJson(instauradoJson.getJson(), PABPMSWrapper.class);
            }
            
        } catch(AppException e){
            //LOG.error("Erro sem tratamento BLOCO - Instauracao", e);
        }
        
        return wrapper;
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @return 
     */
    private List<RecursosWrapper> montarConsultaRecursos(EntityManager em, ProcessoAdministrativo processoAdministrativo) {
        List<RecursosWrapper> recursos = new ArrayList<>();
        try {
            List<RecursoMovimento> lista = new RecursoMovimentoRepositorio().getRecursosPorPA(em, processoAdministrativo.getId());

            for (RecursoMovimento recursoMovimento : lista) {
                try {
                    
                    recursos.add(new RecursoBO().preencherRecursoWrapper(em, recursoMovimento));
                    
                } catch (Exception e) {
                    LOG.error("Erro ao preencher Recurso");
                }
            }
        } catch (DatabaseException ex) {
            LOG.error("Erro sem tratamento BLOCO - Recursos", ex);
        }
        return recursos;
    }

    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    private List<NotificacaoWrapper> montarConsultaNotificacoes(
        EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        List<NotificacaoWrapper> notificacoes = null;
        
        try {
            
            notificacoes 
                = new NotificacaoProcessoAdministrativoRepositorio()
                            .getListaNotificacaoPorNumeroProcessoParaWSConsultaPA(em, processoAdministrativo);
        
        } catch(AppException e){
            //LOG.error("Erro sem tratamento BLOCO - Notificações", e);
        }
        
        return notificacoes;
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    private List<RetornosARWrapper> montarConsultaRetornosAR(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        List<RetornosARWrapper> retornos = new ArrayList<>();

        List<NotificacaoProcessoAdministrativo> notificacoes = new NotificacaoProcessoAdministrativoRepositorio().getListNotificacoesAtivasPA(em, processoAdministrativo.getId());
        for (NotificacaoProcessoAdministrativo notificacao : notificacoes) {
            retornos.add(new RetornoARBO().montarRetornoARWrapper(em, notificacao));
        }
        return retornos;
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    private List<RecursoCanceladoRetornoWSWrapper> montarRecursosCancelados(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        List<RecursoMovimento> recursosCancelados = new RecursoMovimentoRepositorio().getRecursosCanceladosPorPA(em, processoAdministrativo.getId());
        
        List<RecursoMovimento> lRecursosCancelados = new ArrayList<>();
        for (RecursoMovimento recursoMovimento : recursosCancelados) {
            if(getControleCnhService().
                    validaTipoTemplateProtocolo(recursoMovimento.getProtocolo().getTemplateProtocolo(), 
                                                TipoSituacaoProtocoloEnum.CANCELAMENTO))
            {
                lRecursosCancelados.add(recursoMovimento);
            }
        }
        
        return new RecursoBO().montarRecursosCancelados(em, lRecursosCancelados);
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    private List<BloqueioBCAWrapper> montarBloqueiosBCA(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        List<MovimentoBloqueioBCA> bloqueios = new MovimentoBloqueioBCARepositorio().getBloqueiosPorPA(em, processoAdministrativo.getId());
        return new BloqueioBCABO().montarBloqueiosBCA(em, bloqueios);
    }
    
    /**
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    private List<InformacoesEntregaCnhWrapper> montarConsultaEntregaCnh(
        EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        return 
            new EntregaCnhBO()
                .getInformacoesEntregaCnh(
                    em, 
                    new MovimentoCnhRepositorio()
                        .getListaMovimentoCnhPorProcessoAdministrativo(em, processoAdministrativo.getId())
                );
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    private List<MovimentacaoPaWrapper> montarMovimentacoes(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        List<Movimentacao> movimentos = new MovimentacaoPARepositorio().getMovimentacoesPorPA(em, processoAdministrativo.getId());
        return new MovimentacaoPABO().montarMovimentacoes(movimentos);
    }
    
    /**
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    private DesistenciaRecursoInstauracaoPenalizacaoWS montarDesistenciaRecursoInstauracaoPenalizacao(EntityManager em, 
                                                                                                      ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        DesistenciaRecursoInstauracaoPenalizacaoWS wrapper = null;
        
        PAComplemento complemento = 
                new PAComplementoRepositorio().
                        getPAComplementoPorParametroEAtivo(em, 
                                                           processoAdministrativo, 
                                                           PAParametroEnum.DESISTENCIA_REC_INST_PEN);
        
        if (null != complemento) {
            
            Protocolo protocolo = 
                    new ProtocoloRepositorio()
                            .getProtocoloPorProcessoAdministrativoETipoNotificacao(
                                    em, 
                                    processoAdministrativo.getId(), 
                                    TipoNotificacaoEnum.PROTOCOLO_PA_DESISTENCIA_REC_INST_PEN);
            
            TemplateProtocolo template = (TemplateProtocolo) getControleCnhService().getTemplateProtocoloPorID(protocolo.getTemplateProtocolo());

            if (null != protocolo) {

                wrapper = new DesistenciaRecursoInstauracaoPenalizacaoWS(processoAdministrativo.getNumeroProcesso(), 
                                                                         protocolo.getNumeroProtocolo(), 
                                                                         processoAdministrativo.getCpf(), 
                                                                         template.getDataProtocolo(),
                                                                         template.getId());

                wrapper.setObservacao(template.getObservacao());
                wrapper.setIdUsuario(template.getUsuario().getId());
                Usuario usuario = (Usuario) getApoioService().getUsuario(wrapper.getIdUsuario());

                if (usuario != null) {
                    wrapper.setNomeUsuario(getApoioService().getNomeUsuarioPeloId(usuario));
                    wrapper.setCpfUsuario(getApoioService().getCpfUsuarioPeloId(usuario));
                }
            }
        }
        
        return wrapper;
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @return 
     */
    private ProcessoAdministrativoDesistenteRetornoWSWrapper montarDesistentes(EntityManager em, 
                                                                               ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        ProcessoAdministrativoDesistenteRetornoWSWrapper desistente = 
                (ProcessoAdministrativoDesistenteRetornoWSWrapper) new MovimentoCnhRepositorio()
                        .getDesistentePorProcessoAdministrativo(em, processoAdministrativo.getId());
        
        if (null != desistente) {
            
            try {
                
                CnhSituacaoEntrega cnhSituacaoEntrega = 
                        (CnhSituacaoEntrega) getApoioService()
                                .getSituacaoEntregaCnh(
                                        desistente.getCnhControle(),
                                        AcaoEntregaCnhEnum.ENTREGA);
                desistente.setObservacao(cnhSituacaoEntrega.getTemplateProtocolo().getObservacao());

                PADocumentoPessoaWrapper documentoWrapper = 
                        new PADocumentoPessoaRepositorio()
                                .getCpfDocumentoPessoa(em, cnhSituacaoEntrega.getUsuario().getPessoa().getId());

                if (documentoWrapper != null) {
                    desistente.setCpfUsuario(documentoWrapper.getNumeroDocumento());
                    desistente.setNomeUsuario(documentoWrapper.getNomePrincipalPessoa());
                }
                
            } catch (Exception e) {
                LOG.error("Erro ao buscar informações de desistente");
                getControleFalha().gravarFalhaEspecifica(desistente.getCpfCondutor(), "Erro ao montar info desistentes", "WS_CONTULTA PA: " + e);
            }
        }
        
        return desistente;
    }
}