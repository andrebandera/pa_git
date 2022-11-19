package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

public enum TipoMovimentoBloqueioBCAEnum {
    
    BLOQUEIO,
    DESBLOQUEIO,
    ALTERACAO;
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}