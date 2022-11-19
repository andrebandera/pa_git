/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author desenvolvimento
 */
public class ProcessoAdministrativoDesistenteRetornoWSWrapper {
    
    @XmlElement(name = "numeroprocesso", nillable = true)
    private String numeroProcesso;
    
    @XmlElement(name = "numeroprotocolo", nillable = true)
    private String numeroProtocolo;
    
    @XmlElement(name = "cpfcondutor", nillable = true)
    private String cpfCondutor;
    
    @XmlElement(name = "datadesistencia", nillable = true)
    private Date dataDesistencia;
    
     @XmlElement(name = "cpfusuario", nillable = true)
    String cpfUsuario;
    
    @XmlElement(name = "nomeusuario", nillable = true)
    String nomeUsuario;
    
    @XmlElement(name = "observacao", nillable = true)
    String observacao;
    
//    @XmlElement(name = "cnhcontrole", nillable = false)
    @XmlTransient
    Long cnhControle;
    
    public ProcessoAdministrativoDesistenteRetornoWSWrapper() {
    }

    public ProcessoAdministrativoDesistenteRetornoWSWrapper(
        String numeroProcesso, String numeroProtocolo, String cpfCondutor, Date dataDesistencia, Long cnhControle) {
        
        this.numeroProcesso     = numeroProcesso;
        this.numeroProtocolo    = numeroProtocolo;
        this.cpfCondutor        = cpfCondutor;
        this.dataDesistencia    = dataDesistencia;
        this.cnhControle        = cnhControle;
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

    public Date getDataDesistencia() {
        return dataDesistencia;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataDesistencia(Date dataDesistencia) {
        this.dataDesistencia = dataDesistencia;
    }

    public String getCpfCondutor() {
        return cpfCondutor;
    }

    public void setCpfCondutor(String cpfCondutor) {
        this.cpfCondutor = cpfCondutor;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    public void setCpfUsuario(String cpfUsuario) {
        this.cpfUsuario = cpfUsuario;
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

    public Long getCnhControle() {
        return cnhControle;
    }

    public void setCnhControle(Long cnhControle) {
        this.cnhControle = cnhControle;
    }
}