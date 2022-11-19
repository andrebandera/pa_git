/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;
import br.gov.ms.detran.processo.administrativo.ejb.IExecucaoAndamento;

/**
 *
 * @author desenvolvimento
 */
public abstract class ExecucaoAndamento implements IExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(ExecucaoAndamento.class);
    
    private IProcessoAdministrativoService processoAdministrativoService;
    private IPAControleFalhaService falhaService;

    public IProcessoAdministrativoService getProcessoAdministrativoService() {
        
        if (processoAdministrativoService == null) {
            processoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }
        
        return processoAdministrativoService;
    }
    
    public IPAControleFalhaService getFalhaService() {

        if (falhaService == null) {
            falhaService = (IPAControleFalhaService) JNDIUtil.lookup("ejb/PAControleFalhaService");
        }

        return falhaService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executa(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento");
        
        RetornoExecucaoAndamentoWrapper retornoWrapper = new RetornoExecucaoAndamentoWrapper();
        
        try {
            
            validaEspecifico(em, wrapper);
            
            wrapper.setProcessoAdministrativo(getProcessoAdministrativo(wrapper));
            
            LOG.debug("Número Processo Administrativo: {0}.", wrapper.getProcessoAdministrativo().getNumeroProcesso());
            
            retornoWrapper = executaEspecifico(em, wrapper);

            LOG.debug("Fim Andamento");

        } catch (Exception e) {
            
            LOG.debug("Erro tratado capturado.", e);
            
            getFalhaService()
                .gravarFalhaProcessoAdministrativo(
                    e, 
                    "Falha execução andamento", 
                    wrapper.getProcessoAdministrativo() != null ? wrapper.getProcessoAdministrativo().getCpf() : null,
                    wrapper.getProcessoAdministrativo() != null ? wrapper.getProcessoAdministrativo().getNumeroProcesso() : null
                );
            
            throw e;
        }
        
        return retornoWrapper;
    }

    @Override
    public void valida(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        if(wrapper == null 
                || wrapper.getProcessoAdministrativo() == null
                || (wrapper.getProcessoAdministrativo().getId() == null
                        && DetranStringUtil.ehBrancoOuNulo(wrapper.getProcessoAdministrativo().getNumeroProcesso()))) {
            
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        validaEspecifico(em, wrapper);
    }
    
    abstract RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException;
    
    /**
     * 
     * @param wrapper
     * @return
     * @throws AppException 
     */
    private ProcessoAdministrativo getProcessoAdministrativo(ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        ProcessoAdministrativo processoAdministrativo = null;
        
        if(wrapper.getProcessoAdministrativo().getId() != null) {
            
            processoAdministrativo 
                = getProcessoAdministrativoPorID(wrapper.getProcessoAdministrativo().getId());
        
        } else {
            
            if(!DetranStringUtil.ehBrancoOuNulo(wrapper.getProcessoAdministrativo().getNumeroProcesso())) {
                
                processoAdministrativo 
                    = getProcessoAdministrativoPorNumeroProcesso(wrapper.getProcessoAdministrativo().getNumeroProcesso());
            }
        }
        
        if(processoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return processoAdministrativo;
    }

    
    /**
     * 
     * @param numeroProcesso
     * @return
     * @throws AppException 
     */
    private ProcessoAdministrativo getProcessoAdministrativoPorNumeroProcesso(String numeroProcesso) throws AppException {

        if(DetranStringUtil.ehBrancoOuNulo(numeroProcesso)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return getProcessoAdministrativoService().getProcessoAdministrativo(numeroProcesso);
    }

    /**
     * 
     * @param idProcessoAdministrativo
     * @return
     * @throws AppException 
     */
    private ProcessoAdministrativo getProcessoAdministrativoPorID(Long idProcessoAdministrativo) throws AppException {
        
        if(idProcessoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return getProcessoAdministrativoService().getProcessoAdministrativo(idProcessoAdministrativo);
    }
    
    abstract void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException;
}