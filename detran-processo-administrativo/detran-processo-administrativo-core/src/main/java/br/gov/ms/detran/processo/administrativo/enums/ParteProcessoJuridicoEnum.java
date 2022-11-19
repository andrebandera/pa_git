package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

/**
 * @author yanko.campos
 */
public enum ParteProcessoJuridicoEnum {
    
    CONDUTOR,
    CANDIDATO,
    CIDADAO;
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}