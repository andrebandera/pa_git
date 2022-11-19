package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class InformacoesEntregaCnhWrapper {
    
    @XmlElement(name = "numeroprocesso", nillable = true)
    private String numeroProcesso;
    
    @XmlElement(name = "numeroprotocolo", nillable = true)
    private String numeroProtocolo;
    
    @XmlElement(name = "dataentrega", nillable = true)
    private Date dataEntrega;
    
    @XmlElement(name = "databloqueio", nillable = true)
    private Date dataBloqueio;
    
    @XmlElement(name = "acao", nillable = true)
    private String acao;
    
    @XmlElement(name = "observacao", nillable = true)
    private String observacao;
    
    @XmlElement(name = "cnhrecolhida", nillable = true)
    private Long cnhRecolhida;
    
    @XmlElement(name = "cpfusuario", nillable = true)
    private String cpfUsuario;
    
    @XmlElement(name = "nomeusuario", nillable = true)
    private String nomeUsuario;
    
    @XmlElement(name = "prazopenalidade", nillable = true)
    private String prazoPenalidade;
    
    @XmlElement(name = "datainiciopenalidade", nillable = true)
    private Date dataInicioPenalidade;
    
    @XmlElement(name = "datafimpenalidade", nillable = true)
    private Date dataFimPenalidade;
    
    @XmlElement(name = "datadevolucao", nillable = true)
    private Date dataDevolucao;
    
    @XmlElement(name = "cpfrepresentantelegal", nillable = true)
    private String cpfRepresentanteLegal;
    
    @XmlElement(name = "nomerepresentantelegal", nillable = true)
    private String nomeRepresentanteLegal;
    
    @XmlElement(name = "motivodesbloqueiobca", nillable = true)
    private String motivoDesbloqueioBCA;
    
    @XmlElement(name = "formaentrega", nillable = true)
    private String formaEntrega;
    
    @XmlElement(name = "numeroprocessooriginal", nillable = true)
    private String numeroProcessoOriginal;
    
    public InformacoesEntregaCnhWrapper() {
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

    public Date getDataEntrega() {
        return dataEntrega;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataEntrega(Date dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getCnhRecolhida() {
        return cnhRecolhida;
    }

    public void setCnhRecolhida(Long cnhRecolhida) {
        this.cnhRecolhida = cnhRecolhida;
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

    public String getPrazoPenalidade() {
        return prazoPenalidade;
    }

    public void setPrazoPenalidade(String prazoPenalidade) {
        this.prazoPenalidade = prazoPenalidade;
    }

    public Date getDataInicioPenalidade() {
        return dataInicioPenalidade;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataInicioPenalidade(Date dataInicioPenalidade) {
        this.dataInicioPenalidade = dataInicioPenalidade;
    }

    public Date getDataFimPenalidade() {
        return dataFimPenalidade;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataFimPenalidade(Date dataFimPenalidade) {
        this.dataFimPenalidade = dataFimPenalidade;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
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

    public String getMotivoDesbloqueioBCA() {
        return motivoDesbloqueioBCA;
    }

    public void setMotivoDesbloqueioBCA(String motivoDesbloqueioBCA) {
        this.motivoDesbloqueioBCA = motivoDesbloqueioBCA;
    }

    public String getFormaEntrega() {
        return formaEntrega;
    }

    public void setFormaEntrega(String formaEntrega) {
        this.formaEntrega = formaEntrega;
    }

    public String getNumeroProcessoOriginal() {
        return numeroProcessoOriginal;
    }

    public void setNumeroProcessoOriginal(String numeroProcessoOriginal) {
        this.numeroProcessoOriginal = numeroProcessoOriginal;
    }
}