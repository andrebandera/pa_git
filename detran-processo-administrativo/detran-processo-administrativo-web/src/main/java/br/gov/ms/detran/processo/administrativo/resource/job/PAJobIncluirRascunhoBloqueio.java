/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource.job;

import br.gov.ms.detran.comum.scheduler.ejbtimer.JobWrapper;
import br.gov.ms.detran.comum.scheduler.ejbtimer.job.DetranAbstractJob;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;

/**
 *
 * @author lilly
 */
public class PAJobIncluirRascunhoBloqueio extends DetranAbstractJob {

    private static final Logger LOG = Logger.getLogger(PAJobIncluirRascunhoBloqueio.class);
    
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

            LOG.info("Inicio Incluir Rascunho Bloqueio.");

            getProcessoAdministrativoService()
                    .processarInclusaoBloqueio();

        } catch (AppException e) {
            LOG.debug("Tratado.", e);
            getFalhaService().gravarFalha(e, "Erro ao processar inclusao do bloqueio.");

        }
    }
}