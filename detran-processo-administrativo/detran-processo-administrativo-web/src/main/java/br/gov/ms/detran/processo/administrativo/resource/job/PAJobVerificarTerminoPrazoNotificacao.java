/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource.job;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.scheduler.ejbtimer.JobWrapper;
import br.gov.ms.detran.comum.scheduler.ejbtimer.job.DetranAbstractJob;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.ejb.IPAAndamentoService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAInicioFluxo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;

/**
 *
 * @author desenvolvimento
 */
public class PAJobVerificarTerminoPrazoNotificacao extends DetranAbstractJob {
    
    @EJB
    private IPAAndamentoService iAndamentoServiceLab;
    
     private IProcessoAdministrativoService processoAdministrativoService;
    
    public IProcessoAdministrativoService getProcessoAdministrativoService() {

        if (processoAdministrativoService == null) {
            processoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }

        return processoAdministrativoService;
    }
    
    public IPAAndamentoService getPAAndamentoServiceLab() {
        
        if (iAndamentoServiceLab == null) {
            iAndamentoServiceLab = (IPAAndamentoService) JNDIUtil.lookup("ejb/PAAndamentoService");
        }
        
        return iAndamentoServiceLab;
    }

    @Override
    protected void doJob(JobWrapper job) {
        try {
            List<ProcessoAdministrativo> processos = getProcessoAdministrativoService().
                    getListProcessosComPrazoNotificacaoExpirado();
            
            for (ProcessoAdministrativo processo : processos) {
                try{
                    
                    executarAndamento(processo);
                    
                }catch(Exception e){
                    
                }
            }
        } catch (AppException ex) {
            Logger.getLogger(PAJobVerificarTerminoPrazoNotificacao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void executarAndamento(ProcessoAdministrativo pa) throws AppException {
        PAFluxoFase fluxoFase
                = (PAFluxoFase) getProcessoAdministrativoService()
                        .getFluxoFaseDoProcessoAdministrativo(pa);
        
        PAInicioFluxo inicioFluxo = getProcessoAdministrativoService().getInicioFluxoAtivoPA(pa.getId());
        if (TipoAndamentoEnum.AUTOMATICO.equals(fluxoFase.getTipoAndamento())
                || inicioFluxo != null) {
            
            new ExecucaoAndamentoManager().iniciaExecucao(
                    new ExecucaoAndamentoEspecificoWrapper(
                            pa,
                            null,
                            null,
                            null
                    )
            );
        }
        else{
            DetranWebUtils.applicationMessageException("Não é possível executar o andamento deste Processo pois o mesmo não é automático.");
        }
    }
    
}
