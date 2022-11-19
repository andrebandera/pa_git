/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Lillydi
 */
public class DadosPABPMSArtigoDespachoWrapper {

    @XmlElement(name = "tiporegistro", nillable = true)
    private final Integer tipoRegistro = TipoRegistroBPMSEnum.ARTIGO_DESPACHO.getCodigo();

    @XmlElement(name = "processo", nillable = true)
    private String processo;

    @XmlElement(name = "artigoincisowork", nillable = true)
    private String artigoIncisoWork;

    @XmlElement(name = "tipoprocessowork", nillable = true)
    private String tipoProcessoWork;

    @XmlElement(name = "nomework", nillable = true)
    private String nomeWork;

    @XmlElement(name = "cpfwork", nillable = true)
    private String cpfWork;

    @XmlElement(name = "datacadastrowork", nillable = true)
    private Date dataCadastroWork;

    @XmlElement(name = "numeroportaria", nillable = true)
    private String numeroPortaria;

    @XmlElement(name = "anoportaria", nillable = true)
    private String anoPortaria;

    @XmlElement(name = "numerocnhwork", nillable = true)
    private Long numeroCnhWork;

    @XmlElement(name = "numeroregistrowork", nillable = true)
    private String numeroRegistroWork;

    @XmlElement(name = "artigo", nillable = true)
    private String artigo;

    @XmlElement(name = "inciso", nillable = true)
    private String inciso;

    @XmlElement(name = "paragrafo", nillable = true)
    private String paragrafo;

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public String getArtigoIncisoWork() {
        return artigoIncisoWork;
    }

    public void setArtigoIncisoWork(String artigoIncisoWork) {
        this.artigoIncisoWork = artigoIncisoWork;
    }

    public String getTipoProcessoWork() {
        return tipoProcessoWork;
    }

    public void setTipoProcessoWork(String tipoProcessoWork) {
        this.tipoProcessoWork = tipoProcessoWork;
    }

    public String getNomeWork() {
        return nomeWork;
    }

    public void setNomeWork(String nomeWork) {
        this.nomeWork = nomeWork;
    }

    public String getCpfWork() {
        return cpfWork;
    }

    public void setCpfWork(String cpfWork) {
        this.cpfWork = cpfWork;
    }

    public Date getDataCadastroWork() {
        return dataCadastroWork;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataCadastroWork(Date dataCadastroWork) {
        this.dataCadastroWork = dataCadastroWork;
    }

    public String getNumeroPortaria() {
        return numeroPortaria;
    }

    public void setNumeroPortaria(String numeroPortaria) {
        this.numeroPortaria = numeroPortaria;
    }

    public String getAnoPortaria() {
        return anoPortaria;
    }

    public void setAnoPortaria(String anoPortaria) {
        this.anoPortaria = anoPortaria;
    }

    public Long getNumeroCnhWork() {
        return numeroCnhWork;
    }

    public void setNumeroCnhWork(Long numeroCnhWork) {
        this.numeroCnhWork = numeroCnhWork;
    }

    public String getNumeroRegistroWork() {
        return numeroRegistroWork;
    }

    public void setNumeroRegistroWork(String numeroRegistroWork) {
        this.numeroRegistroWork = numeroRegistroWork;
    }

    public String getArtigo() {
        return artigo;
    }

    public void setArtigo(String artigo) {
        this.artigo = artigo;
    }

    public String getInciso() {
        return inciso;
    }

    public void setInciso(String inciso) {
        this.inciso = inciso;
    }

    public String getParagrafo() {
        return paragrafo;
    }

    public void setParagrafo(String paragrafo) {
        this.paragrafo = paragrafo;
    }

    public Integer getTipoRegistro() {
        return tipoRegistro;
    }
}
