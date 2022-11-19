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
public enum MotivoIntegracaoPAEnum {

    SUSPENSAO("1"),
    ARTIGO_263_I("2"),
    ARTIGO_263_II("3"),
    ARTIGO_148("4");
    
    private final String motivo;

    private MotivoIntegracaoPAEnum(String motivo) {
        this.motivo = motivo;
    }

    public String getMotivo() {
        return motivo;
    }
    
}
