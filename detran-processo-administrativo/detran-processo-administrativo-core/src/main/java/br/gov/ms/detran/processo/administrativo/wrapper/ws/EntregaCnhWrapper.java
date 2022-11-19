/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;

/**
 *
 * @author Lillydi
 */
public class EntregaCnhWrapper {

    private String numeroProcesso;
    
    private AcaoEntregaCnhEnum acao;

    public EntregaCnhWrapper() {
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public AcaoEntregaCnhEnum getAcao() {
        return acao;
    }

    public void setAcao(AcaoEntregaCnhEnum acao) {
        this.acao = acao;
    }
}
