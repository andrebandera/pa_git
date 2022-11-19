/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.DesistenciaEntregaCnhBO;
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
public class PAAndamento135  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento135.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 135.");
        
        
        RetornoExecucaoAndamentoWrapper retornoWrapper = new DesistenciaEntregaCnhBO().definirDesistenteEntregaCnh(em, wrapper.getProcessoAdministrativo());
        
        if(retornoWrapper == null){
            NotificaProcessoAdministrativoWrapper notificaWrapper = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();

            if (notificaWrapper == null || !TipoFasePaEnum.ENTREGA_CNH.equals(notificaWrapper.getTipo())) {
                DetranWebUtils.applicationMessageException("Tipo da Notificação inválido.");
            }
            retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
        }
        return retornoWrapper;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    }
}