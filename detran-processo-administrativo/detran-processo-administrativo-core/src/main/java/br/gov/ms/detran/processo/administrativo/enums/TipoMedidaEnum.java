package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

/**
 * @author yanko.campos
 */
public enum TipoMedidaEnum {
    
    CAUTELAR,
    DEFINITIVA,
    SURCI,
    SUSPENSAO_CONDICIONAL,
    PENA;
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}