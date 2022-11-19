/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Lillydi
 */
public class ConsultaGeralPAWrapper {

    @XmlElement(name = "numeroprocessoadministrativo", nillable = true)
    private String numeroProcesso;
    
    @XmlElement(name = "cpfcondutor", nillable = true)
    private String cpfCondutor;
    
    @XmlElement(name = "andamento", nillable = true)
    private String andamento;
    
    @XmlElement(name = "fase", nillable = true)
    private String fase;
    
    @XmlElement(name = "status", nillable = true)
    private String status;
    
    @XmlElement(name = "fluxo", nillable = true)
    private String fluxo;
    
    @XmlElement(name = "tipocenario", nillable = true)
    private String tipoCenario;

    public ConsultaGeralPAWrapper() {
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getCpfCondutor() {
        return cpfCondutor;
    }

    public void setCpfCondutor(String cpfCondutor) {
        this.cpfCondutor = cpfCondutor;
    }

    public String getAndamento() {
        return andamento;
    }

    public void setAndamento(String andamento) {
        this.andamento = andamento;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFluxo() {
        return fluxo;
    }

    public void setFluxo(String fluxo) {
        this.fluxo = fluxo;
    }

    public String getTipoCenario() {
        return tipoCenario;
    }

    public void setTipoCenario(String tipoCenario) {
        this.tipoCenario = tipoCenario;
    }
    
}
