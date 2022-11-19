package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.RecursoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoMovimentoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.entidade.RegraInstaurarEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoNotificacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import javax.persistence.EntityManager;

public class PAAndamento141  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento141.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 141.");
        
        RetornoExecucaoAndamentoWrapper retorno = null;
        
        RecursoWSWrapper entrada = (RecursoWSWrapper) wrapper.getObjetoWrapper();
        
        RecursoMovimento recursoMovimento = 
            new RecursoMovimentoRepositorio()
                .getRecursoMovimentoPorNumeroProtocoloENumeroProcessoAdministrativo(
                    em, 
                    entrada.getNumeroProtocolo(), 
                    wrapper.getProcessoAdministrativo().getNumeroProcesso()
                );
        if(recursoMovimento == null || !TipoFasePaEnum.INSTAURACAO.equals(recursoMovimento.getRecurso().getTipoRecurso())){
            DetranWebUtils.applicationMessageException("Não foi possível recuperar recurso.");
        }
        
        entrada.setIdUsuario(wrapper.getIdUsuario());
        
        if (DetranStringUtil.ehBrancoOuNulo(entrada.getParecer())) {
            DetranWebUtils.applicationMessageException("Parecer é obrigatório.");
        }

        new RecursoBO().gravarResultadoRecurso(em, entrada, recursoMovimento.getRecurso());

        if(RegraInstaurarEnum.C1.equals(wrapper.getProcessoAdministrativo().getOrigemApoio().getRegra())){
            retorno = defineRetornoPermissionado(em, entrada, wrapper, retorno);
        }else{
            retorno = defineRetornoGeral(em, entrada, wrapper, retorno);
        }

        return retorno;
        
    }

    /**
     * 
     * @param em
     * @param entrada
     * @param wrapper
     * @param retorno
     * @return
     * @throws AppException 
     */
    private RetornoExecucaoAndamentoWrapper defineRetornoGeral(
        EntityManager em, RecursoWSWrapper entrada, ExecucaoAndamentoEspecificoWrapper wrapper, RetornoExecucaoAndamentoWrapper retorno) throws AppException {
        
        NotificaProcessoAdministrativoWrapper notificaProcessoAdministrativoWrapper = null;
        
        switch (entrada.getResultado()) {
            case ACOLHIDO:
                new RecursoBO().acoesApensamentoAgravamento(em, wrapper.getProcessoAdministrativo());
                
                retorno 
                    = new RetornoExecucaoAndamentoWrapper(
                            TipoRetornoAndamentoEnum.INICIA_FLUXO,
                            PAFluxoProcessoConstante.CODIGO_PA_FLUXO_PROVIMENTO
                    );
                break;
            
            case PROVIDO:
                new RecursoBO().acoesApensamentoAgravamento(em, wrapper.getProcessoAdministrativo());
                
                retorno 
                    = new RetornoExecucaoAndamentoWrapper(
                            TipoRetornoAndamentoEnum.INICIA_FLUXO,
                            PAFluxoProcessoConstante.CODIGO_PA_FLUXO_PROVIMENTO
                    );
                break;
            
            case IMPROVIDO:
                retorno 
                    = new RetornoExecucaoAndamentoWrapper(
                            TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                            PAFluxoProcessoConstante.FLUXO_NORMAL,
                            PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.AGUARDAR_GERACAO_NOTIFICACAO_PENALIZACAO
                    );
                
                notificaProcessoAdministrativoWrapper = new NotificaProcessoAdministrativoWrapper();
                notificaProcessoAdministrativoWrapper.setNumeroProcesso(wrapper.getProcessoAdministrativo().getNumeroProcesso());
                notificaProcessoAdministrativoWrapper.setTipo(TipoFasePaEnum.PENALIZACAO);
                
                wrapper.setObjetoWrapper(notificaProcessoAdministrativoWrapper);
                
                break;
            
            case NAO_ACOLHIDO:
                retorno 
                    = new RetornoExecucaoAndamentoWrapper(
                            TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                            PAFluxoProcessoConstante.FLUXO_NORMAL,
                            PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.AGUARDAR_GERACAO_NOTIFICACAO_PENALIZACAO
                    );
                
                notificaProcessoAdministrativoWrapper = new NotificaProcessoAdministrativoWrapper();
                notificaProcessoAdministrativoWrapper.setNumeroProcesso(wrapper.getProcessoAdministrativo().getNumeroProcesso());
                notificaProcessoAdministrativoWrapper.setTipo(TipoFasePaEnum.PENALIZACAO);
                
                wrapper.setObjetoWrapper(notificaProcessoAdministrativoWrapper);

                break;
            
            case IRREGULAR:
                if (RecursoNotificacaoAcaoEnum.CANCELAMENTO.equals(entrada.getAcao())) {
                
                    retorno 
                        = new RetornoExecucaoAndamentoWrapper(
                                TipoRetornoAndamentoEnum.INICIA_FLUXO,
                                PAFluxoProcessoConstante.CODIGO_PA_FLUXO_ARQUIVAMENTO_CANCELAMENTO_ADMINISTRATIVO
                        );
                    
                } else if (RecursoNotificacaoAcaoEnum.RENOTIFICAR.equals(entrada.getAcao())) {

                    new RecursoBO().desativarRecursosDoPAParaRenotificar(em, wrapper.getProcessoAdministrativo(), TipoFasePaEnum.INSTAURACAO);
                    
                    retorno 
                        = new RetornoExecucaoAndamentoWrapper(
                                TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                PAFluxoProcessoConstante.FLUXO_NORMAL,
                                PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.AGUARDAR_GERACAO_NOTIFICACAO_INSTAURACAO
                        );
                    
                    notificaProcessoAdministrativoWrapper = new NotificaProcessoAdministrativoWrapper();
                    notificaProcessoAdministrativoWrapper.setNumeroProcesso(wrapper.getProcessoAdministrativo().getNumeroProcesso());
                    notificaProcessoAdministrativoWrapper.setTipo(TipoFasePaEnum.INSTAURACAO);
                    

                    wrapper.setObjetoWrapper(notificaProcessoAdministrativoWrapper);
                }
                break;
        
            case NAO_CONHECIMENTO:
                retorno 
                    = new RetornoExecucaoAndamentoWrapper(
                            TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                            PAFluxoProcessoConstante.FLUXO_NORMAL,
                            PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.AGUARDAR_GERACAO_NOTIFICACAO_PENALIZACAO
                    );
                
                notificaProcessoAdministrativoWrapper = new NotificaProcessoAdministrativoWrapper();
                notificaProcessoAdministrativoWrapper.setNumeroProcesso(wrapper.getProcessoAdministrativo().getNumeroProcesso());
                notificaProcessoAdministrativoWrapper.setTipo(TipoFasePaEnum.PENALIZACAO);
                
                wrapper.setObjetoWrapper(notificaProcessoAdministrativoWrapper);
                
                break;
        }
        
        return retorno;
    }
    
    /**
     * 
     * @param em
     * @param entrada
     * @param wrapper
     * @param retorno
     * @return
     * @throws AppException 
     */
    private RetornoExecucaoAndamentoWrapper defineRetornoPermissionado(
        EntityManager em, RecursoWSWrapper entrada, ExecucaoAndamentoEspecificoWrapper wrapper, RetornoExecucaoAndamentoWrapper retorno) throws AppException {
        
        NotificaProcessoAdministrativoWrapper notificaProcessoAdministrativoWrapper = null;
        
        switch (entrada.getResultado()) {
            
            case ACOLHIDO:
                new RecursoBO().acoesApensamentoAgravamento(em, wrapper.getProcessoAdministrativo());
                
                retorno 
                    = new RetornoExecucaoAndamentoWrapper(
                        TipoRetornoAndamentoEnum.INICIA_FLUXO,
                        PAFluxoProcessoConstante.CODIGO_PA_FLUXO_PROVIMENTO
                    );
                
                break;
            
            case PROVIDO:
                
                new RecursoBO().acoesApensamentoAgravamento(em, wrapper.getProcessoAdministrativo());
                
                retorno 
                    = new RetornoExecucaoAndamentoWrapper(
                            TipoRetornoAndamentoEnum.INICIA_FLUXO,
                            PAFluxoProcessoConstante.CODIGO_PA_FLUXO_PROVIMENTO
                    );
                
                break;
            
            case IMPROVIDO:
                
                retorno 
                    = new RetornoExecucaoAndamentoWrapper(
                            TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                            PAFluxoProcessoConstante.FLUXO_GERAL_PERMISSIONADO,
                            PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH
                    );
                
                notificaProcessoAdministrativoWrapper = new NotificaProcessoAdministrativoWrapper();
                notificaProcessoAdministrativoWrapper.setNumeroProcesso(wrapper.getProcessoAdministrativo().getNumeroProcesso());
                notificaProcessoAdministrativoWrapper.setTipo(TipoFasePaEnum.ENTREGA_CNH);
                
                wrapper.setObjetoWrapper(notificaProcessoAdministrativoWrapper);
                
                break;
            
            case NAO_ACOLHIDO:
                
                retorno 
                    = new RetornoExecucaoAndamentoWrapper(
                            TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                            PAFluxoProcessoConstante.FLUXO_GERAL_PERMISSIONADO,
                            PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH
                    );
                
                notificaProcessoAdministrativoWrapper = new NotificaProcessoAdministrativoWrapper();
                notificaProcessoAdministrativoWrapper.setNumeroProcesso(wrapper.getProcessoAdministrativo().getNumeroProcesso());
                notificaProcessoAdministrativoWrapper.setTipo(TipoFasePaEnum.ENTREGA_CNH);
                
                wrapper.setObjetoWrapper(notificaProcessoAdministrativoWrapper);
                
                break;
            
            case IRREGULAR:
                
                if (RecursoNotificacaoAcaoEnum.CANCELAMENTO.equals(entrada.getAcao())) {
                
                    retorno 
                        = new RetornoExecucaoAndamentoWrapper(
                                TipoRetornoAndamentoEnum.INICIA_FLUXO,
                                PAFluxoProcessoConstante.CODIGO_PA_FLUXO_ARQUIVAMENTO_CANCELAMENTO_ADMINISTRATIVO
                        );
                    
                } else if (RecursoNotificacaoAcaoEnum.RENOTIFICAR.equals(entrada.getAcao())) {

                    new RecursoBO().desativarRecursosDoPAParaRenotificar(em, wrapper.getProcessoAdministrativo(), TipoFasePaEnum.INSTAURACAO);
                    
                    retorno 
                        = new RetornoExecucaoAndamentoWrapper(
                                TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                PAFluxoProcessoConstante.FLUXO_GERAL_PERMISSIONADO,
                                PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.AGUARDAR_GERACAO_NOTIFICACAO_INSTAURACAO
                        );
                    
                    notificaProcessoAdministrativoWrapper = new NotificaProcessoAdministrativoWrapper();
                    notificaProcessoAdministrativoWrapper.setNumeroProcesso(wrapper.getProcessoAdministrativo().getNumeroProcesso());
                    notificaProcessoAdministrativoWrapper.setTipo(TipoFasePaEnum.INSTAURACAO);

                    wrapper.setObjetoWrapper(notificaProcessoAdministrativoWrapper);
                }
                
                break;
        
            case NAO_CONHECIMENTO:
                
                retorno 
                        = new RetornoExecucaoAndamentoWrapper(
                                TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                PAFluxoProcessoConstante.FLUXO_GERAL_PERMISSIONADO,
                                PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH
                        );
                
                notificaProcessoAdministrativoWrapper = new NotificaProcessoAdministrativoWrapper();
                notificaProcessoAdministrativoWrapper.setNumeroProcesso(wrapper.getProcessoAdministrativo().getNumeroProcesso());
                notificaProcessoAdministrativoWrapper.setTipo(TipoFasePaEnum.ENTREGA_CNH);
                
                wrapper.setObjetoWrapper(notificaProcessoAdministrativoWrapper);
                
                break;
        }
        
        return retorno;
    }
    
    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
    }
}