/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoMovimentoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento23  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento23.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 23.");
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        RecursoWSWrapper entrada = (RecursoWSWrapper) wrapper.getObjetoWrapper();
        
        RecursoMovimento recursoMovimento = 
                new RecursoMovimentoRepositorio()
                        .getRecursoMovimentoPorNumeroProtocoloENumeroProcessoAdministrativo(
                                em, 
                                entrada.getNumeroProtocolo(), 
                                entrada.getNumeroProcesso());
        
        if(!TipoFasePaEnum.INSTAURACAO.equals(recursoMovimento.getRecurso().getTipoRecurso())){
            DetranWebUtils.applicationMessageException("Não é possível fazer a confirmação neste andamento.");
        }
        
    }
}