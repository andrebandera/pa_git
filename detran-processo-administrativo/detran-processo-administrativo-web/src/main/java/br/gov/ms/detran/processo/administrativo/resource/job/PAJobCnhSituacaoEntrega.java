/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource.job;

import br.gov.ms.detran.comum.scheduler.ejbtimer.JobWrapper;
import br.gov.ms.detran.comum.scheduler.ejbtimer.job.DetranAbstractJob;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Christiano Carrilho.
 */
public class PAJobCnhSituacaoEntrega extends DetranAbstractJob {

    private static final Logger LOG = Logger.getLogger(PAJobCnhSituacaoEntrega.class);
    
    private IProcessoAdministrativoService processoAdministrativoService;

    IPAControleFalhaService falhaService;
    

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
    protected void doJob(JobWrapper job) {

        try {
            
            LOG.info("Inicio Entrega Cnh.");
            
            List<ProcessoAdministrativo> listaProcessos = 
                    getProcessoAdministrativoService().buscarProcessosAdministrativosNoAndamento48ComCnhEntregue();
            
            List<ProcessoAdministrativo> listaProcessos48PenaCumprida = 
                    getProcessoAdministrativoService().buscarProcessosAdministrativosNoAndamento48ComPenaCumprida();

            listaProcessos.addAll(listaProcessos48PenaCumprida);

            listaProcessos = listaProcessos.stream().distinct().collect(Collectors.toList());
            
            if (!DetranCollectionUtil.ehNuloOuVazio(listaProcessos)) {
                
                for (ProcessoAdministrativo processo : listaProcessos) {
                    
                    try {

                        getProcessoAdministrativoService().executarAndamento48paraEntregaCnh(processo);

                    } catch (Exception e) {
                        LOG.debug("Tratado.", e);
                        getFalhaService().gravarFalhaProcessoAdministrativo(e, "CnhSituacaoEntregaServlet", processo.getCpf(), processo.getNumeroProcesso());
                    }
                }
            }
            
        } catch (AppException ex) {
            LOG.debug("Tratado.", ex);
            getFalhaService().gravarFalha(ex, "Erro ao executar Retorno AR");
        }
    }
}