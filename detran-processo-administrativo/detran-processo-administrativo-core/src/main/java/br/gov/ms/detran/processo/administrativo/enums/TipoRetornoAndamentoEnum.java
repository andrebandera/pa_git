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
public enum TipoRetornoAndamentoEnum {
    
    PROXIMO_ANDAMENTO,
    INICIA_FLUXO,
    MUDANCA_FLUXO_ANDAMENTO
    ;

    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}
