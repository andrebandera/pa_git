/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.RequisitoRecursoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.EntregaCnhWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento213  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento213.class);

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 213.");
        RetornoExecucaoAndamentoWrapper retorno;
        
        DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, wrapper.getProcessoAdministrativo().getId());
        
        if(pju == null){
            DetranWebUtils.applicationMessageException("Não foi possível recuperar os dados judiciais do processo.");
        }
        
        NotificacaoProcessoAdministrativo notificacaoEntrega 
                    = new NotificacaoProcessoAdministrativoRepositorio().
                            getNotificacaoPorProcessoAdministrativoETipoNotificacao(em, wrapper.getProcessoAdministrativo().getId(), TipoFasePaEnum.ENTREGA_CNH);
        
        if(RequisitoRecursoBloqueioEnum.SEM_CURSO.equals(pju.getRequisitoCursoBloqueio())){
            
            retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO, 
                                                          PAFluxoProcessoConstante.FLUXO_CUMPRIMENTO_PENA);
        }else{
            if(notificacaoEntrega == null)
            retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO, 
                                                          PAFluxoProcessoConstante.NOTIFICACAO_CURSO_EXAME);
            else
                retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO, 
                                                          PAFluxoProcessoConstante.CURSO_EXAME);
        }
        return retorno;
    }


    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        EntregaCnhWrapper ecWrapper = (EntregaCnhWrapper) wrapper.getObjetoWrapper();
        
        if(ecWrapper == null || !AcaoEntregaCnhEnum.ENTREGA.equals(ecWrapper.getAcao())){
            DetranWebUtils.applicationMessageException("Ação inválida.");
        } 
    }
}