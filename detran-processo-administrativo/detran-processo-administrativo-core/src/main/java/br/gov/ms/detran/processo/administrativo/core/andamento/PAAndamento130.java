/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.ConfirmaRetornoARBO;
import br.gov.ms.detran.processo.administrativo.core.bo.DesistenciaEntregaCnhBO;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento130  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento130.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 130.");
        
        RetornoExecucaoAndamentoWrapper retornoWrapper = 
                new DesistenciaEntregaCnhBO().
                        definirDesistenteEntregaCnh(em, wrapper.getProcessoAdministrativo());
        
        if(retornoWrapper == null){
            
        NotificaProcessoAdministrativoWrapper npaWrapper = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();
        
        if(npaWrapper == null || !TipoFasePaEnum.PENALIZACAO.equals(npaWrapper.getTipo())){
            DetranWebUtils.applicationMessageException("Tipo da Notificação inválido.");
        }
            
            retornoWrapper = 
                    new ConfirmaRetornoARBO().
                            confirmarRetornoAR(em, wrapper.getProcessoAdministrativo(), npaWrapper.getTipo());
        }
        
        return retornoWrapper;
        
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    }
}