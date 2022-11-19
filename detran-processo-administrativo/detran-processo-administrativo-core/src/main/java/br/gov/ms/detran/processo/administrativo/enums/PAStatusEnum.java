package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public enum PAStatusEnum {
    ATIVO(1),
    ARQUIVADO(22),
    CANCELADO(23),
    SUSPENSO(24)
    ;
    
 private final int codigo;
    
    private static final Map<Integer,PAStatusEnum> LOOKUP = new TreeMap();
    
    static {
        
        for(PAStatusEnum s : EnumSet.allOf(PAStatusEnum.class)) {
            LOOKUP.put(s.getCodigo(), s);
        }
    }
        
    private PAStatusEnum(int code) {
        this.codigo = code;
    }

    public int getCodigo() { 
    	return codigo; 
    }

    public static PAStatusEnum get(int code) { 
        return LOOKUP.get(code); 
    }
    
    public static List<Integer> getStatusNaoPermitidosParaBuscaPAsAtivos() {
        return DetranCollectionUtil.montaLista(ARQUIVADO.getCodigo(),
                                               CANCELADO.getCodigo(),
                                               SUSPENSO.getCodigo());
    }

    @Override
    public String toString() {
        return DetranWebUtils.getMessageByKey(getClass().getSimpleName() + "." + this.name());
    }
}
