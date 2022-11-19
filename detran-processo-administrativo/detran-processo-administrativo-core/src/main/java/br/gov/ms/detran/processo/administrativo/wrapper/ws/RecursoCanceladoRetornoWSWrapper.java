/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author desenvolvimento
 */
public class RecursoCanceladoRetornoWSWrapper {

    @XmlElement(name = "numeroprocesso", nillable = true)
    private String numeroProcesso;

    @XmlElement(name = "numeroprotocolo", nillable = true)
    private String numeroProtocolo;

    @XmlElement(name = "datacancelamento", nillable = true)
    private Date dataCancelamento;

    @XmlElement(name = "numeroprotocolocancelado", nillable = true)
    private String numeroProtocoloCancelado;

    @XmlElement(name = "cpfusuariocancelou", nillable = true)
    private String cpfUsuarioCancelou;

    @XmlElement(name = "nomeusuario", nillable = true)
    String nomeUsuario;

    @XmlElement(name = "observacao", nillable = true)
    String observacao;
    
    @XmlElement(name = "cpfrepresentantelegal", nillable = true)
    private String cpfRepresentanteLegal;
    
    @XmlElement(name = "nomerepresentantelegal", nillable = true)
    private String nomeRepresentanteLegal;
    
    @XmlElement(name = "tipo", nillable = true)
    private String tipo;
    
    @XmlElement(name = "responsavelpeloprotocolo", nillable = true)
    private String responsavelPeloProtocolo;

    public RecursoCanceladoRetornoWSWrapper() {
    }

    public RecursoCanceladoRetornoWSWrapper(
            String numeroProcesso, String numeroProtocolo, Date dataCancelamento,
            String numeroProtocoloCancelado, String observacao) {

        this.numeroProcesso = numeroProcesso;
        this.numeroProtocolo = numeroProtocolo;
        this.dataCancelamento = dataCancelamento;
        this.numeroProtocoloCancelado = numeroProtocoloCancelado;
        this.observacao = observacao;
    }
    
    public RecursoCanceladoRetornoWSWrapper(String numeroProcesso, 
                                            String numeroProtocolo, 
                                            Date dataCancelamento,
                                            String numeroProtocoloCancelado, 
                                            String observacao,
                                            String responsavelPeloProtocolo) {

        this.numeroProcesso = numeroProcesso;
        this.numeroProtocolo = numeroProtocolo;
        this.dataCancelamento = dataCancelamento;
        this.numeroProtocoloCancelado = numeroProtocoloCancelado;
        this.observacao = observacao;
        this.responsavelPeloProtocolo = responsavelPeloProtocolo;
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

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public String getNumeroProtocoloCancelado() {
        return numeroProtocoloCancelado;
    }

    public void setNumeroProtocoloCancelado(String numeroProtocoloCancelado) {
        this.numeroProtocoloCancelado = numeroProtocoloCancelado;
    }

    public String getCpfUsuarioCancelou() {
        return cpfUsuarioCancelou;
    }

    public void setCpfUsuarioCancelou(String cpfUsuarioCancelou) {
        this.cpfUsuarioCancelou = cpfUsuarioCancelou;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getCpfRepresentanteLegal() {
        return cpfRepresentanteLegal;
    }

    public void setCpfRepresentanteLegal(String cpfRepresentanteLegal) {
        this.cpfRepresentanteLegal = cpfRepresentanteLegal;
    }

    public String getNomeRepresentanteLegal() {
        return nomeRepresentanteLegal;
    }

    public void setNomeRepresentanteLegal(String nomeRepresentanteLegal) {
        this.nomeRepresentanteLegal = nomeRepresentanteLegal;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getResponsavelPeloProtocolo() {
        return responsavelPeloProtocolo;
    }

    public void setResponsavelPeloProtocolo(String responsavelPeloProtocolo) {
        this.responsavelPeloProtocolo = responsavelPeloProtocolo;
    }
}