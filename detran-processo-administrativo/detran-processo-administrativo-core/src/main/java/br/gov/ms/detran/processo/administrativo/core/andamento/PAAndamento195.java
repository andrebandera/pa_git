/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.ConfirmaRetornoARBO;
import br.gov.ms.detran.processo.administrativo.entidade.PATipoCorpoAndamento;
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
public class PAAndamento195  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento195.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 195.");
        
        NotificaProcessoAdministrativoWrapper npaWrapper = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();
        
        Integer codigoFluxo = null;
        
        Boolean mudancaFluxo = new ConfirmaRetornoARBO().
                verificaMudancaFluxoParaConfirmaRetornoAR(em, wrapper.getProcessoAdministrativo().getNumeroProcesso(), npaWrapper.getTipo());
        
        
        if (mudancaFluxo) {
            
            PATipoCorpoAndamento pATipoCorpoAndamento = new ConfirmaRetornoARBO().
                getTipoCorpoAndamentoPorPA(em, npaWrapper.getTipo(), wrapper.getProcessoAdministrativo());
            codigoFluxo = pATipoCorpoAndamento.getFluxoProcessoEdital().getCodigo();
            
        } else {
            codigoFluxo = PAFluxoProcessoConstante.CURSO_EXAME;
        }
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO, codigoFluxo);
        
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    
        NotificaProcessoAdministrativoWrapper npaWrapper = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();
        
        if(npaWrapper == null || !TipoFasePaEnum.CURSO_EXAME.equals(npaWrapper.getTipo())){
            DetranWebUtils.applicationMessageException("Tipo da Notificação inválido.");
        }
    }
}