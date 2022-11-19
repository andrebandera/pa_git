package br.gov.ms.detran.processo.administrativo.enums;

import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author yanko.campos
 */
public enum FaseProcessoEnum {
    
    
    /*
     * PA ORIGEM PONTUACAO 
     */
    INSTAURACAO("010000"),    
    NOTIFICACAO_INSTAURACAO("020000"),    
    EDITAL_NOTIFICACAO_INSTAURACAO("020001"),    
    DEFESA_NOTIFICACAO_INSTAURACAO("020100"),    
    NOTIFICACAO_PENALIZACAO("030000"),    
    EDITAL_NOTIFICACAO_PENALIZACAO("030001"),    
    DEFESA_NOTIFICACAO_PENALIZACAO("030100"),    
    NOTIFICACAO_RECURSO_NA0_CONHECIDO_30_DIAS_JARI("030203"),    
    RECURSO_CETRAN("030204"),    
    NOTIFICACAO_ENTREGA_CNH("040000"),    
    EDITAL_NOTIFICACAO_ENTREGA_CNH("040001"),    
    DEFESA_NOTIFICACAO_ENTREGA_CNH("040100"),    
    CUMPRIMENTO_DA_PENALIDADE("050000"),    
    BLOQUEIO_CNH_CUMPRIMENTO_PENALIDADE("050100"),    
    CUMPRIMENTO_DE_PENA("050200"),    
    DESBLOQUEIO_CNH_CUMPRIMENTO_PENALIDADE("050300"),    
    NOTIFICACAO_NAO_CONHECIMENTO("060000"),    
    NOTIFICACAO_DESENTRANHAMENTO("070000"),    
    EDITAL_NOTIFICACAO_DESENTRANHAMENTO("070001"),    
    DEFESA_NOTIFICACAO_DESENTRANHAMENTO("070100"),    
    CNH_DEVOLUCAO("080000"),    
    DEVOLUCAO_CNH("080100"),    
    DESBLOQUEIO_CNH_DEVOLUCAO("080200"),    
    
    /*
    * COMUM
    */
    
    COMUM("990000"),    
    PROVIMENTO_PRESIDENTE("990100"),    
    INFRACAO_OUTRA_UF("990200"),    
    SUSPENSAO_DO_PROCESSO("990300"),    
    CANCELAMENTO_COMUM("990400"),    
    ARQUIVAMENTO_COMUM("990500"),    
    ANULACAO("990501"),    
    IMPREVISTO("990502"),    
    APENSAMENTO("990503"),    
    DETERMINACAO("990504"),    
    CANCELAMENTO_INFRACAO("990505"),    
    ERRO("990506"),    
    NOTIFICACAO_ACOLHIMENTO_COMUM("990507"),    
    NOTIFICACAO_ACOLHIMENTO("990600"),    
    EDITAL_NOTIFICACAO_ACOLHIMENTO("990601"),
    
    /*
     * PROCESSO JURIDICO 
     */
    
    PROCESSO_JURIDICO("300000"),
    INSTAURACAO_PROCESSO_JURIDICO("310000"),
    BLOQUEIO_CNH("320000"),
    NOTIFICACAO_ENTREGA_CNH_PROCESSO_JURIDICO("330000"),
    EDITAL_NOTIFICACAO_ENTREGA_CNH_PROCESSO_JURIDICO("330001"),
    CUMPRIMENTO_PENALIDADE("340000"),
    CUMPRIMENTO_PENA("340100"),
    DESBLOQUEIO_CNH("340200"),
    ENTREGA_CNH("350000"),
    CURSO("360000"),
    NOTIFICACAO_DESENTRANHAMENTO_PROCESSO_JURIDICO("370000"),
    EDITAL_NOTIFICACAO_DESENTRANHAMENTO_PROCESSO_JURIDICO("370001"),
    NOTIFICACAO_CURSO_EXAME("380000"),
    EDITAL_NOTIFICACAO_CURSO_EXAME("380001"),
    ;
    
    private final String codigo;
    
    private static final Map<String,FaseProcessoEnum> LOOKUP = new TreeMap();
    
    static {
        
        for(FaseProcessoEnum s : EnumSet.allOf(FaseProcessoEnum.class)) {
            LOOKUP.put(s.getCodigoCompleto(), s);
        }
    }
    
    private FaseProcessoEnum(String code) {
        this.codigo = code;
    }

    public String getCodigoCompleto() { 
    	return codigo; 
    }

    public static FaseProcessoEnum get(String code) { 
        return LOOKUP.get(code); 
    }
    
    public String getCodigoAteParte1() {
        
        String parte1 = codigo.substring(0, 2);
        
        return parte1;
    }
    
    public String getCodigoAteParte2() {
        
        String parte2 = codigo.substring(0, 4);
        
        return parte2;
    }
}