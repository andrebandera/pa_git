/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento121  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento121.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 121. PA {0}", wrapper.getProcessoAdministrativo().getNumeroProcesso());
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {}
}