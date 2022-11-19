/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Lillydi
 */
public class DadosPABPMSRecursosInfracaoWrapper {
    
    @XmlElement(name = "tiporegistro", nillable = true)
    private final Integer tipoRegistro = TipoRegistroBPMSEnum.PROCESSOS_DE_RECURSO.getCodigo();

    @XmlElement(name = "numeroautoinfracao", nillable = true)
    private String numeroAutoInfracao;

    @XmlElement(name = "codigoinfracao", nillable = true)
    private String codigoInfracao;

    @XmlElement(name = "descricaorecurso", nillable = true)
    private String descricaoRecurso;

    @XmlElement(name = "requerente", nillable = true)
    private String requerente;
    
    @XmlElement(name = "situacaoinfracao", nillable = true)
    private String situacaoInfracao;
    
    @XmlElement(name = "sintesedefesa", nillable = true)
    private String sinteseDefesa;
    
    @XmlElement(name = "observacaoresultado", nillable = true)
    private String observacaoResultado;
    
    @XmlElement(name = "descricaoresultado", nillable = true)
    private String descricaoResultado;

    public DadosPABPMSRecursosInfracaoWrapper() {
    }

    public DadosPABPMSRecursosInfracaoWrapper(String autoInfracao, String codigoInfracao) {
        this.numeroAutoInfracao = autoInfracao;
        this.codigoInfracao = codigoInfracao;
    }

    public Integer getTipoRegistro() {
        return tipoRegistro;
    }

    public String getNumeroAutoInfracao() {
        return numeroAutoInfracao;
    }

    public void setNumeroAutoInfracao(String numeroAutoInfracao) {
        this.numeroAutoInfracao = numeroAutoInfracao;
    }

    public String getCodigoInfracao() {
        return codigoInfracao;
    }

    public void setCodigoInfracao(String codigoInfracao) {
        this.codigoInfracao = codigoInfracao;
    }

    public String getDescricaoRecurso() {
        return descricaoRecurso;
    }

    public void setDescricaoRecurso(String descricaoRecurso) {
        this.descricaoRecurso = descricaoRecurso;
    }

    public String getRequerente() {
        return requerente;
    }

    public void setRequerente(String requerente) {
        this.requerente = requerente;
    }

    public String getSituacaoInfracao() {
        return situacaoInfracao;
    }

    public void setSituacaoInfracao(String situacaoInfracao) {
        this.situacaoInfracao = situacaoInfracao;
    }

    public String getSinteseDefesa() {
        return sinteseDefesa;
    }

    public void setSinteseDefesa(String sinteseDefesa) {
        this.sinteseDefesa = sinteseDefesa;
    }

    public String getObservacaoResultado() {
        return observacaoResultado;
    }

    public void setObservacaoResultado(String observacaoResultado) {
        this.observacaoResultado = observacaoResultado;
    }

    public String getDescricaoResultado() {
        return descricaoResultado;
    }

    public void setDescricaoResultado(String descricaoResultado) {
        this.descricaoResultado = descricaoResultado;
    }
    
}
