/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP22;

/**
 *
 * @author dev
 */
public class AEMNPP22BO {
    
    /**
     * 
     * @param cpf
     * @return
     * @throws AppException 
     */
    public AEMNPP22 executarIntegracaoAEMNPP22(String cpf) throws AppException {
        
        ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();
        
        params.adicionarParametro(DetranStringUtil.preencherEspaco(cpf, DetranTamanhoConstante.CPF, DetranStringUtil.TipoDadoEnum.NUMERICO));
        
        IResultadoIntegracao iResultadoIntegracao = new IntegracaoMainFrameBO().executarAEMNPP22(params);
        
        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {
            
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M2", null, "AEMNPP22");
        }
        
        return (AEMNPP22) iResultadoIntegracao.getResultadoBean(); 
    }
}
