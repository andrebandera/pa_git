/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoInfracaoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoJsonRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoJson;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.PABPMSWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento117  extends ExecucaoAndamento{
    
    private static final Logger LOG = Logger.getLogger(PAAndamento117.class);
    
    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {
        
        if (null == andamentoEspecificoWrapper 
                || andamentoEspecificoWrapper.getProcessoAdministrativo() == null
                || DetranStringUtil.ehBrancoOuNulo(andamentoEspecificoWrapper.getNumeroProcesso())) {

            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 117.");
        
            ProcessoAdministrativoJson paJson = new ProcessoAdministrativoJsonRepositorio().
                    getProcessoAdministrativoJsonPorProcessoAdministrativoAtivo(em, wrapper.getProcessoAdministrativo().getId());
            
            PABPMSWrapper pABPMSWrapper = (PABPMSWrapper) DetranStringUtil
                                                 .getInstance()
                                                    .fromJson(paJson.getJson(), PABPMSWrapper.class);
                                
            new ProcessoAdministrativoInfracaoBO().atualizarInfracoes(em,pABPMSWrapper, wrapper.getProcessoAdministrativo());
            
            return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);

    }
}
