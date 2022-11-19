/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.enums;

/**
 *
 * @author Lillydi
 */
public enum PAParametroEnum {

    TEMPO_PENALIDADE("Tempo Penalidade"),
    DESISTENCIA_ENTREGA_CNH("Desistência Entrega CNH"),
    DESISTENCIA_15_SGI("Desistência Recurso Penalização"),
    DESISTENCIA_REC_INST_PEN("Desistência Recurso Instauração e Penalização"),
    ARQUIVAMENTO_PROCESSADO("Arquivamento dos Processos de Suspensão por Cassação")
    ;
    
    private final String label;

    private PAParametroEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    
}
