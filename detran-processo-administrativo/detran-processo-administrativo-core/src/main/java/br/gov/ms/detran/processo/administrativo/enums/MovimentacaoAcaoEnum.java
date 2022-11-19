package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

public enum MovimentacaoAcaoEnum {
    
    ARQUIVAR, 
    CANCELAR, 
    SUSPENDER, 
    RETIRAR_SUSPENSAO,
    ARQUIVAR_COM_BLOQUEIO,
    DESARQUIVAR;
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}