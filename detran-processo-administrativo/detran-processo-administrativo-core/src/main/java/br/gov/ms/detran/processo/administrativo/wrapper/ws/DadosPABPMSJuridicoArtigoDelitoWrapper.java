package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import javax.xml.bind.annotation.XmlElement;

public class DadosPABPMSJuridicoArtigoDelitoWrapper {

    @XmlElement(name = "artigodelito", nillable = true)
    private String artigoDelito;
    
    public DadosPABPMSJuridicoArtigoDelitoWrapper() {
    }

    public String getArtigoDelito() {
        return artigoDelito;
    }

    public void setArtigoDelito(String artigoDelito) {
        this.artigoDelito = artigoDelito;
    }
}