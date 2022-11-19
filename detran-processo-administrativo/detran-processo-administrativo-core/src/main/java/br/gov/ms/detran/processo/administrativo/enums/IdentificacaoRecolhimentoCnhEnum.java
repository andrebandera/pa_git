package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

/**
 * @author yanko.campos
 */
public enum IdentificacaoRecolhimentoCnhEnum {
    
    INEXISTENTE("1"),
    DETRAN("2"),
    CARTORIO_JUDICIARIO("3");
    
    private String codigo;

    private IdentificacaoRecolhimentoCnhEnum(String codigo) {
        this.codigo = codigo;
    }
    
    public String getCodigo(){
        return this.codigo;
    }
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}