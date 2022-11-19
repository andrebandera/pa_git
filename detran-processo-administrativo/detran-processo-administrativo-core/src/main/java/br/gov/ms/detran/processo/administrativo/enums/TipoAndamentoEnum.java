/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

/**
 *
 * @author desenvolvimento
 */
public enum TipoAndamentoEnum {
    
    MANUAL,
    MANUAL_WS,
    MANUAL_TELA,
    MANUAL_BANCO,
    AUTOMATICO
    ;

    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}
