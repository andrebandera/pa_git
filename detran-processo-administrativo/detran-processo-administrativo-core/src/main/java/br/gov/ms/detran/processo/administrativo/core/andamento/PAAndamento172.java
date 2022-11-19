/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.ConfirmaRetornoARBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.PATipoCorpoAndamento;
import br.gov.ms.detran.processo.administrativo.enums.RequisitoRecursoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento172  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento172.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 172.");
        
        NotificaProcessoAdministrativoWrapper npaWrapper = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();
        
        TipoRetornoAndamentoEnum retornoEnum = TipoRetornoAndamentoEnum.INICIA_FLUXO;
        Integer codigoFluxo = null;
        Integer codigoAndamento = null;
        
        if(!wrapper.getProcessoAdministrativo().isJuridico()){
            DetranWebUtils.applicationMessageException("Não é possível executar este andamento para este processo.");
        }
        
        Boolean mudancaFluxo = new ConfirmaRetornoARBO().
                verificaMudancaFluxoParaConfirmaRetornoAR(em, wrapper.getProcessoAdministrativo().getNumeroProcesso(), npaWrapper.getTipo());
        
        
        if (mudancaFluxo) {

            PATipoCorpoAndamento pATipoCorpoAndamento = new ConfirmaRetornoARBO().
                getTipoCorpoAndamentoPorPA(em, npaWrapper.getTipo(), wrapper.getProcessoAdministrativo());
            codigoFluxo = pATipoCorpoAndamento.getFluxoProcessoEdital().getCodigo();
            
        } else {
            
            DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, wrapper.getProcessoAdministrativo().getId());
            
            if(RequisitoRecursoBloqueioEnum.SEM_CURSO.equals(pju.getRequisitoCursoBloqueio())){
                retornoEnum = TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO;
                codigoFluxo = PAFluxoProcessoConstante.FLUXO_GERAL_JURIDICO;
                codigoAndamento = PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_PROTOCOLO_DEVOLUCAO_CNH;
                
            }else{
                
                codigoFluxo = PAFluxoProcessoConstante.CURSO_EXAME;
            }
        }
        
        return new RetornoExecucaoAndamentoWrapper(retornoEnum, codigoFluxo, codigoAndamento);
        
    }

    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    
        NotificaProcessoAdministrativoWrapper npaWrapper = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();
        
        if(npaWrapper == null || !TipoFasePaEnum.DESENTRANHAMENTO.equals(npaWrapper.getTipo())){
            DetranWebUtils.applicationMessageException("Tipo da Notificação inválido.");
        }
    }
}