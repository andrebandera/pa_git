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
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.PROCESSO_JURIDICO.CUMPRIR_PENALIDADE;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import java.util.List;

/**
 *
 * @author Lillydi.
 */
public class PAJobCumprimentoPenaPJUCartorio extends DetranAbstractJob {

    private static final Logger LOG = Logger.getLogger(PAJobCumprimentoPenaPJUCartorio.class);
    
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
                    getProcessoAdministrativoService().getListProcessosAdministrativosPorAndamento(DetranCollectionUtil.montaLista(CUMPRIR_PENALIDADE));
            
            if (!DetranCollectionUtil.ehNuloOuVazio(listaProcessos)) {
                
                listaProcessos.forEach((processo) -> {
                    try {

                        new ExecucaoAndamentoManager()
                                .iniciaExecucao(
                                        new ExecucaoAndamentoEspecificoWrapper(
                                                processo,
                                                null,
                                                null,
                                                null
                                        )
                                );

                    } catch (AppException e) {
                        LOG.debug("Tratado.", e);
                    }
                });
            }
            
        } catch (AppException ex) {
            LOG.debug("Tratado.", ex);
            getFalhaService().gravarFalha(ex, "Erro ao executar Retorno AR");
        }
    }
}