package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import javax.xml.bind.annotation.XmlElement;

public class DadosPABPMSInfracaoDespachoWrapper {

    @XmlElement(name = "artigo", nillable = true)
    private String artigo;
    
    @XmlElement(name = "paragrafo", nillable = true)
    private String paragrafo;
    
    @XmlElement(name = "inciso", nillable = true)
    private String inciso;
    
    @XmlElement(name = "codigo", nillable = true)
    private String codigo;
    
    @XmlElement(name = "descricao", nillable = true)
    private String descricao;

    public DadosPABPMSInfracaoDespachoWrapper() {
        
    }

    public String getArtigo() {
        return artigo;
    }

    public void setArtigo(String artigo) {
        this.artigo = artigo;
    }

    public String getParagrafo() {
        return paragrafo;
    }

    public void setParagrafo(String paragrafo) {
        this.paragrafo = paragrafo;
    }

    public String getInciso() {
        return inciso;
    }

    public void setInciso(String inciso) {
        this.inciso = inciso;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}