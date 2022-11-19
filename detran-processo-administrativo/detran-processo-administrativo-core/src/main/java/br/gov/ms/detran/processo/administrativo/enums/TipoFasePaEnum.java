package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

/**
 * @author Lillydi
 */
public enum TipoFasePaEnum {

    INSTAURACAO("15"),
    PENALIZACAO("30"),
    ENTREGA_CNH("48"),
    PROVIMENTO("PRV"),
    NAO_CONHECIMENTO("N_CTO"),
    DESENTRANHAMENTO("DES"),
    JARI("JARI"),
    CURSO_EXAME("C_EX"),
    ARQUIVAMENTO("ARQUIVAMENTO")
    ;
    
    private final String tipoFasePaExterna;

    private TipoFasePaEnum(String tipoNotificacaoExterna) {
        this.tipoFasePaExterna = tipoNotificacaoExterna;
    }

    public String getTipoFasePaExterna() {
        return tipoFasePaExterna;
    }
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}
