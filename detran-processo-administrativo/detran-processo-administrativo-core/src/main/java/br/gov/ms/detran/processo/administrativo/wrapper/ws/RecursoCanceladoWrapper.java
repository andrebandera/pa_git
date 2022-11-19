/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;

/**
 *
 * @author desenvolvimento
 */
public class RecursoCanceladoWrapper {
    
    private String numeroProcesso;
    
    private String numeroProtocolo;
    
    private MovimentoCnh movimento;
    
    private Boolean geraProtocoloDesentranhamento;
    
    public RecursoCanceladoWrapper() {
    }

    public RecursoCanceladoWrapper(String numeroProcesso, String numeroProtocolo) {
        this.numeroProcesso                     = numeroProcesso;
        this.numeroProtocolo                    = numeroProtocolo;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public MovimentoCnh getMovimento() {
        return movimento;
    }

    public void setMovimento(MovimentoCnh movimento) {
        this.movimento = movimento;
    }

    public Boolean getGeraProtocoloDesentranhamento() {
        return geraProtocoloDesentranhamento;
    }

    public void setGeraProtocoloDesentranhamento(Boolean geraProtocoloDesentranhamento) {
        this.geraProtocoloDesentranhamento = geraProtocoloDesentranhamento;
    }
}