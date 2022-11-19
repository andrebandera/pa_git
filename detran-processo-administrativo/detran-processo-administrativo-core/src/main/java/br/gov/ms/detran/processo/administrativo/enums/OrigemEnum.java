package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

/**
 * @author yanko.campos
 */
public enum OrigemEnum {
    
    PONTUACAO("PA"),
    JURIDICA("PJU"),
    MANUAL("MA"),
    OUTRA_UF("UF");
    
    private final String sigla;

    private OrigemEnum(String sigla) {
        this.sigla = sigla;
    }

    public String getSigla() {
        return sigla;
    }
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}