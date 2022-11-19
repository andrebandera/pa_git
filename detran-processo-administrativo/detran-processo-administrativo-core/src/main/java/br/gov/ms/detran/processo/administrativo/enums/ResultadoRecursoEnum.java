package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

public enum ResultadoRecursoEnum {
    
    PROVIDO,
    IMPROVIDO,
    IRREGULAR,
    NAO_CONHECIMENTO,
    ACOLHIDO,
    NAO_ACOLHIDO;
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}