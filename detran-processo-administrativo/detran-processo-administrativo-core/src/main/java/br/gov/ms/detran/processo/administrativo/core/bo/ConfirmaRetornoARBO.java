/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.CONFIRMAR_ARQUIVAMENTO_RECURSO_PROVIDO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante.CODIGO_PA_FLUXO_PROVIMENTO;
import br.gov.ms.detran.processo.administrativo.constantes.SituacaoCorrespondenciaConstante;
import br.gov.ms.detran.processo.administrativo.core.repositorio.CorrespondenciaRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PATipoCorpoAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ResultadoRecursoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.PATipoCorpoAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoRecurso;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.ResultadoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.protocolo.entidade.Correspondencia;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class ConfirmaRetornoARBO {
    
    public RetornoExecucaoAndamentoWrapper confirmarRetornoAR(EntityManager em, 
                                                              ProcessoAdministrativo processoAdministrativo, 
                                                              TipoFasePaEnum tipo) 
            throws AppException {
        
        TipoRetornoAndamentoEnum retornoEnum = null;
        Integer codigoFluxo = null;
        
        Boolean mudancaFluxo = 
                verificaMudancaFluxoParaConfirmaRetornoAR(em, processoAdministrativo.getNumeroProcesso(), tipo);
        
        
        if (mudancaFluxo) {
            
            retornoEnum = TipoRetornoAndamentoEnum.INICIA_FLUXO;
            
            PATipoCorpoAndamento pATipoCorpoAndamento = 
                getTipoCorpoAndamentoPorPA(em, tipo, processoAdministrativo);
            codigoFluxo = pATipoCorpoAndamento.getFluxoProcessoEdital().getCodigo();
            
        } else {
            
            retornoEnum = TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO;
        }
        
        return new RetornoExecucaoAndamentoWrapper(retornoEnum, codigoFluxo);
    }
        
    /**
     * 
     * @param em
     * @param numeroProcesso
     * @param tipo
     * @return
     * @throws DatabaseException
     * @throws AppException 
     */
    public Boolean verificaMudancaFluxoParaConfirmaRetornoAR(EntityManager em, 
                                                             String numeroProcesso, 
                                                             TipoFasePaEnum tipo) throws DatabaseException, AppException {
        
        NotificacaoProcessoAdministrativo notificacao =
                new NotificacaoProcessoAdministrativoRepositorio()
                        .getNotificacaoPorNumeroProcessoETipo(em, numeroProcesso, tipo);
        
        if (notificacao == null) {
            DetranWebUtils.applicationMessageException("Dados inválidos.");
        }
        
        Correspondencia correspondencia =
                new CorrespondenciaRepositorio()
                        .getCorrespondenciaPorNumeroNotificacaoENumeroProcesso(em,
                                notificacao.getNumeroNotificacao(),
                                numeroProcesso);
        
        /** Checa -> Notificação Entregue. **/
        return !SituacaoCorrespondenciaConstante.RECEBIDA_PELO_DESTINATARIO.equals(correspondencia.getSituacao());
        
    }
  
    /**
     * 
     * @param em
     * @param tipo
     * @param processoAdministrativo
     * @return
     * @throws AppException
     * @throws DatabaseException 
     */
    public PATipoCorpoAndamento getTipoCorpoAndamentoPorPA(EntityManager em, 
                                                           TipoFasePaEnum tipo, 
                                                           ProcessoAdministrativo processoAdministrativo) throws AppException, DatabaseException {
        
        PATipoCorpoAndamento pATipoCorpoAndamento =
                new PATipoCorpoAndamentoRepositorio()
                        .getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracao(em,
                                tipo,
                                processoAdministrativo.getOrigemApoio());
        if (pATipoCorpoAndamento == null || pATipoCorpoAndamento.getFluxoProcessoEdital() == null) {
            DetranWebUtils.applicationMessageException("Dados inválidos.");
        }
        return pATipoCorpoAndamento;
    }

    public RetornoExecucaoAndamentoWrapper confirmarRetornoARParaJARI(EntityManager em, 
                                                                      ProcessoAdministrativo processoAdministrativo, 
                                                                      TipoFasePaEnum tipo) throws AppException {
        
        TipoRetornoAndamentoEnum retornoEnum = null;
        Integer codigoFluxo = null;
        
        RetornoExecucaoAndamentoWrapper retorno;
        
        Boolean mudancaFluxo = 
                verificaMudancaFluxoParaConfirmaRetornoAR(em, processoAdministrativo.getNumeroProcesso(), tipo);
        
        
        if (mudancaFluxo) {
            
            retornoEnum = TipoRetornoAndamentoEnum.INICIA_FLUXO;
            
            PATipoCorpoAndamento pATipoCorpoAndamento = 
                getTipoCorpoAndamentoPorPA(em, tipo, processoAdministrativo);
           
            codigoFluxo = pATipoCorpoAndamento.getFluxoProcessoEdital().getCodigo();
            
            retorno = new RetornoExecucaoAndamentoWrapper(retornoEnum, codigoFluxo);
            
        } else {
            retorno = defineRetornoAndamentoJARI(em, processoAdministrativo);
        }
        
        return retorno;
    }

    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    public RetornoExecucaoAndamentoWrapper defineRetornoAndamentoJARI(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        RetornoExecucaoAndamentoWrapper retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
        
        ResultadoRecurso resultado = new ResultadoRecursoRepositorio().
                getResultadoRecursoPorProcessoAdministrativoESituacaoEDestino(em,
                                                                              processoAdministrativo.getId(),
                                                                              SituacaoRecursoEnum.JULGADO,
                                                                              OrigemDestinoEnum.JARI);
        if(resultado == null){
            DetranWebUtils.applicationMessageException("Não foi possível encontrar o resultado do recurso.");
        }
        
        if(ResultadoRecursoEnum.ACOLHIDO.equals(resultado.getResultado())
               || ResultadoRecursoEnum.PROVIDO.equals(resultado.getResultado())){
            
            retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                                                                        CODIGO_PA_FLUXO_PROVIMENTO, 
                                                                                                        CONFIRMAR_ARQUIVAMENTO_RECURSO_PROVIDO);
            
        }
        return retorno;
    }


}
