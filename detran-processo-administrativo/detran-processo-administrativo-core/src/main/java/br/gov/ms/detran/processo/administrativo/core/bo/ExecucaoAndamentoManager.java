package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.enums.TipoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.ejb.IPAAndamentoService;

public class ExecucaoAndamentoManager {

    private static final Logger LOG = Logger.getLogger(ExecucaoAndamentoManager.class);
    
    private IPAAndamentoService iAndamentoServiceLab;
    
    private IProcessoAdministrativoService iProcessoAdministrativoService;
    
    public IPAAndamentoService getPAAndamentoServiceLab() {
        if (iAndamentoServiceLab == null) {
            iAndamentoServiceLab = (IPAAndamentoService) JNDIUtil.lookup("ejb/PAAndamentoService");
        }
        return iAndamentoServiceLab;
    }

    public IProcessoAdministrativoService getProcessoAdministrativoService() {
        
        if (iProcessoAdministrativoService == null) {
            iProcessoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }
        return iProcessoAdministrativoService;
    }
    
    /**
     * 
     * @param andamentoEspecificoWrapper
     * @throws AppException 
     */
    public Boolean iniciaExecucao(ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {
        Boolean sucesso = true;   
        
        getPAAndamentoServiceLab().iniciaExecucao(andamentoEspecificoWrapper);
        
        try{
            PAFluxoFase fluxoFase
                = (PAFluxoFase) getProcessoAdministrativoService()
                    .getFluxoFaseDoProcessoAdministrativo(andamentoEspecificoWrapper.getProcessoAdministrativo());

            
            if(TipoAndamentoEnum.AUTOMATICO.equals(fluxoFase.getTipoAndamento())) {
                executa(andamentoEspecificoWrapper);
            }
        }catch(Exception e){
            LOG.error("ERRO FLUXO ",e);
            sucesso = false;
        }
        return sucesso;
    }
    
    private void executa(ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {
        
        getPAAndamentoServiceLab().executa(andamentoEspecificoWrapper);
        
        PAFluxoFase fluxoFase
            = (PAFluxoFase) getProcessoAdministrativoService()
                .getFluxoFaseDoProcessoAdministrativo(andamentoEspecificoWrapper.getProcessoAdministrativo());

        if(TipoAndamentoEnum.AUTOMATICO.equals(fluxoFase.getTipoAndamento())) {
            executa(andamentoEspecificoWrapper);
        }
    }
}