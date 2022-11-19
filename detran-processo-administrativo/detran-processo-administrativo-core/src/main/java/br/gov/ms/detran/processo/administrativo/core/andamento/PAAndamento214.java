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
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.EntregaCnhWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento214  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento214.class);

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 214.");

        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO, 
                                                   PAFluxoProcessoConstante.FLUXO_GERAL_JURIDICO, 
                                                   PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_ARQUIVAMENTO_PENA_CUMPRIDA);
    }


    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        EntregaCnhWrapper ecWrapper = (EntregaCnhWrapper) wrapper.getObjetoWrapper();
        
        if(ecWrapper == null || !AcaoEntregaCnhEnum.DEVOLUCAO.equals(ecWrapper.getAcao())){
            DetranWebUtils.applicationMessageException("Ação inválida.");
        } 
    }
}