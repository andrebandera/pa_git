package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

/**
 * @author desenvolvimento
 */
public enum OrigemDestinoEnum {
    
    JARI,
    CETRAN,
    SEPEN,
    ORGAO_AUTUADOR,
    PODER_JUDICIARIO;
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}