/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Lillydi
 */
public class RetornosARWrapper {

    @XmlElement(name = "numeroprocesso", nillable = true)
    private String numeroProcesso;
    
    @XmlElement(name = "codigoretornoar", nillable = true)
    private String codigoRetornoAR;
    
    @XmlElement(name = "andamentoatual", nillable = true)
    private Integer andamentoAtual;
    
    @XmlElement(name = "status", nillable = true)
    private final Integer status = 1;
    
    @XmlElement(name = "nomerecebedor", nillable = true)
    private String nomeRecebedor;
    
    @XmlElement(name = "documentorecebedor", nillable = true)
    private String documentoRecebedor;
    
    @XmlElement(name = "dataentrega", nillable = true)
    private Date dataEntrega;
    
    @XmlElement(name = "resultadoentrega", nillable = true)
    private Integer resultadoEntrega;
    
    @XmlElement(name = "erro", nillable = true)
    private String erro;
    
    @XmlElement(name = "tipo", nillable = true)
    private TipoFasePaEnum tipo;

    public RetornosARWrapper() {
    }

    public RetornosARWrapper(String erro) {
        this.erro = erro;
    }
    
    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getCodigoRetornoAR() {
        return codigoRetornoAR;
    }

    public void setCodigoRetornoAR(String codigoRetornoAR) {
        this.codigoRetornoAR = codigoRetornoAR;
    }

    public Integer getAndamentoAtual() {
        return andamentoAtual;
    }

    public void setAndamentoAtual(Integer andamentoAtual) {
        this.andamentoAtual = andamentoAtual;
    }

    public Integer getStatus() {
        return status;
    }

    public String getNomeRecebedor() {
        return nomeRecebedor;
    }

    public void setNomeRecebedor(String nomeRecebedor) {
        this.nomeRecebedor = nomeRecebedor;
    }

    public String getDocumentoRecebedor() {
        return documentoRecebedor;
    }

    public void setDocumentoRecebedor(String documentoRecebedor) {
        this.documentoRecebedor = documentoRecebedor;
    }

    public Date getDataEntrega() {
        return dataEntrega;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataEntrega(Date dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public Integer getResultadoEntrega() {
        return resultadoEntrega;
    }

    public void setResultadoEntrega(Integer resultadoEntrega) {
        this.resultadoEntrega = resultadoEntrega;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public TipoFasePaEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoFasePaEnum tipo) {
        this.tipo = tipo;
    }
}
