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
public enum TipoPAIntegracaoEnum {

    PERMISSIONADO("1"),
    ESPECIFICADA("2"),
    PONTUACAO("3");
    
    private final String tipo;

    private TipoPAIntegracaoEnum(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
