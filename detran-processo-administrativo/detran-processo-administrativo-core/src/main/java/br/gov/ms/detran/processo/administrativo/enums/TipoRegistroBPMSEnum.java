/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Lillydi
 */
public enum TipoRegistroBPMSEnum {
    
    DADOS_GERAL(0),
    DADOS_PA(1),
    DADOS_INFRACAO(2),
    DADOS_CONDUTOR(3),
    HISTORICO_PONTUACAO(4),
    HISTORICO_PONTUACAO_NOME(5),
    PROCESSOS_DE_RECURSO(6),
    INFRACAO_LOCAL(7),
    INFRACAO_RENAINF(8),
    LOG_TRANSACAO_RENAVAM(9),
    DETALHES_AUTUACAO_INFRACAO(10),
    ARTIGO_DESPACHO(11),
    ERRO(99);
    
    private final int codigo;
    
    private static final Map<Integer,TipoRegistroBPMSEnum> LOOKUP = new TreeMap();
    
    static {
        
        for(TipoRegistroBPMSEnum s : EnumSet.allOf(TipoRegistroBPMSEnum.class)) {
            LOOKUP.put(s.getCodigo(), s);
        }
    }
        
    private TipoRegistroBPMSEnum(int code) {
        this.codigo = code;
    }

    public int getCodigo() { 
    	return codigo; 
    }

    public static TipoRegistroBPMSEnum get(int code) { 
        return LOOKUP.get(code); 
    }

    @Override
    public String toString() {
        return DetranWebUtils.getMessageByKey(getClass().getSimpleName() + "." + this.name());
    }
}
