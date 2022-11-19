/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import java.io.Serializable;

/**
 *
 * @author Christiano Carrilho.
 */
public class PAAmparoLegalWrapper implements Serializable {

    private Long id;
    private String artigo;
    private String paragrafo;
    private String inciso;

    public PAAmparoLegalWrapper() {
    }

    public PAAmparoLegalWrapper(Long id, String artigo, String paragrafo, String inciso) {
        this.id = id;
        this.artigo = artigo;
        this.paragrafo = paragrafo;
        this.inciso = inciso;
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

    public String getArtigo() {
        return artigo;
    }

    public void setArtigo(String artigo) {
        this.artigo = artigo;
    }
}