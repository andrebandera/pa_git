/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class DesistenciaRecursoInstauracaoPenalizacaoWS {

    @XmlElement(name = "numeroprocesso", nillable = true)
    private String numeroProcesso;
    
    @XmlElement(name = "numeroprotocolo", nillable = true)
    private String numeroProtocolo;
    
    @XmlElement(name = "cpfcondutor", nillable = true)
    private String cpfCondutor;
    
    @XmlElement(name = "datadesistencia", nillable = true)
    private Date dataDesistencia;
    
    @XmlTransient
    private Long idUsuario;
    
    @XmlElement(name = "cpfusuario", nillable = true)
    private String cpfUsuario;
    
    @XmlElement(name = "nomeusuario", nillable = true)
    private String nomeUsuario;
    
    @XmlElement(name = "observacao", nillable = true)
    private String observacao;
    
    @XmlTransient
    private Long templateProtocoloID;

    public DesistenciaRecursoInstauracaoPenalizacaoWS(
        String numeroProcesso, String numeroProtocolo, String cpfCondutor, Date dataDesistencia, Long templateProtocolo) {
        
        this.numeroProcesso     = numeroProcesso;
        this.numeroProtocolo    = numeroProtocolo;
        this.cpfCondutor        = cpfCondutor;
        this.dataDesistencia    = dataDesistencia;
        this.templateProtocoloID = templateProtocolo;
    }

    public DesistenciaRecursoInstauracaoPenalizacaoWS() {
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

    public String getCpfCondutor() {
        return cpfCondutor;
    }

    public void setCpfCondutor(String cpfCondutor) {
        this.cpfCondutor = cpfCondutor;
    }

    public Date getDataDesistencia() {
        return dataDesistencia;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataDesistencia(Date dataDesistencia) {
        this.dataDesistencia = dataDesistencia;
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

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getTemplateProtocoloID() {
        return templateProtocoloID;
    }

    public void setTemplateProtocoloID(Long templateProtocoloID) {
        this.templateProtocoloID = templateProtocoloID;
    }
}