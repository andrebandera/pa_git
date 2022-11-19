/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;

/**
 *
 * @author Lillydi
 */
public abstract class AEMNPPBO {

    /**
     * 
     * @param tipo
     * @return 
     */
    protected String getTipoProcesso(TipoProcessoEnum tipo) {
        
        String tipoProcessoMainFrame = "";
       
        if (TipoProcessoEnum.SUSPENSAO.equals(tipo)
                || TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)) {
            tipoProcessoMainFrame = "1";
        }
        
        if (TipoProcessoEnum.CASSACAO.equals(tipo)
                || TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo)) {
            tipoProcessoMainFrame = "2";
        }
        
        if (TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(tipo)) {
            tipoProcessoMainFrame = "3";
        }
        
        return tipoProcessoMainFrame;
    }
}
