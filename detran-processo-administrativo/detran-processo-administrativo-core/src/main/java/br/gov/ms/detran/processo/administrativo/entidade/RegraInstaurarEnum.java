package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

/**
 *
 * @author desenvolvimento
 */
public enum RegraInstaurarEnum {
    
    C1,
    C2,
    C3,
    C4,
    C5,
    C6,
    C7,
    C8,
    C9,
    C10,
    C11,
    C12,
    C13,
    C14
    ;

    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}
