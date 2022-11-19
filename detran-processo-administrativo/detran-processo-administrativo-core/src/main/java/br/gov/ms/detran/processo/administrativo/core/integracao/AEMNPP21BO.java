/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP21;

/**
 *
 * @author dev
 */
public class AEMNPP21BO {
    
    /**
     * 
     * @param params
     * @return
     * @throws AppException 
     */
    public AEMNPP21 executarIntegracaoAEMNPP21(ParametroEnvioIntegracao params) throws AppException {
        
        IResultadoIntegracao iResultadoIntegracao = new IntegracaoMainFrameBO().executarAEMNPP21(params);
        
        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {
            
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M2", null, "AEMNPP21");
        }
        
        return (AEMNPP21) iResultadoIntegracao.getResultadoBean(); 
    }
}