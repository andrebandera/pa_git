package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

public enum PASituacaoJsonEnum {
    
    GERADO,
    ENVIADO,
    RECEBIDO
    ;
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}