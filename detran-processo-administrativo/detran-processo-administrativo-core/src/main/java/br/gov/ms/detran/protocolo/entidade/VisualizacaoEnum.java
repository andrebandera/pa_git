/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.protocolo.entidade;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

/**
 *
 * @author desenvolvimento
 */
public enum VisualizacaoEnum {
    
    OUTROS,
    DOC_COMUNS_SIMPLES,
    RETORNO_CORREIOS_SPROT_CAMPO_GRANDE,
    BATCH,
    CORREIOS
    ;
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}