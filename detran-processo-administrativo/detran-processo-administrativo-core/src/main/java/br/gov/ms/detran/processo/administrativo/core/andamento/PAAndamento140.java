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
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.EntregaCnhWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento140  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento140.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 140.");
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
       EntregaCnhWrapper ecWrapper = (EntregaCnhWrapper) wrapper.getObjetoWrapper();
        
        if(ecWrapper == null || !AcaoEntregaCnhEnum.DEVOLUCAO.equals(ecWrapper.getAcao())){
            DetranWebUtils.applicationMessageException("Ação inválida.");
        } 
    }
}