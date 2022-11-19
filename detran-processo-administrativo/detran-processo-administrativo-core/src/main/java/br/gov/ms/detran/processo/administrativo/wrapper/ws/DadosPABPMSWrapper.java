package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.entidade.enums.inf.AcaoSistemaPAEnum;
import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Lillydi
 */
public class DadosPABPMSWrapper {

    @XmlElement(name = "tiporegistro", nillable = true)
    private final Integer tipoRegistro = TipoRegistroBPMSEnum.DADOS_PA.getCodigo();
    
    @XmlElement(name = "nomecondutor", nillable = true)
    private String nomeCondutor;
    
    @XmlElement(name = "datacadastro", nillable = true)
    private Date dataCadastro;
    
    @XmlElement(name = "artigoinciso", nillable = true)
    private String artigoInciso;
    
    @XmlElement(name = "numeroregistro", nillable = true)
    private String numeroRegistro;
    
    @XmlElement(name = "numerocnh", nillable = true)
    private Long numeroCnh;
    
    @XmlElement(name = "quantidademesespenalidade", nillable = true)
    private String quantidadeMesesPenalidade;
    
    @XmlElement(name = "valorprazo", nillable = true)
    private String valorPrazo;
    
    @XmlElement(name = "qtdpontosprocesso", nillable = true)
    private Integer qtdPontosProcesso;
    
    @XmlElement(name = "descricaostatus", nillable = true)
    private String descricaoStatus;
    
    @XmlElement(name = "descricaomotivo", nillable = true)
    private String descricaoMotivo;
    
    @XmlElement(name = "descricaoandamento", nillable = true)
    private String descricaoAndamento;
    
    @XmlElement(name = "anoportaria", nillable = true)
    private String anoPortaria;
    
    @XmlElement(name = "artigo", nillable = true)
    private String artigo;
    
    @XmlElement(name = "inciso", nillable = true)
    private String inciso;
    
    @XmlElement(name = "paragrafo", nillable = true)
    private String paragrafo;
    
    @XmlElement(name = "acaosistema", nillable = true)
    private AcaoSistemaPAEnum acaoSistema;
    
    @XmlElement(name = "numeroprocessoorigem", nillable = true)
    private String numeroProcessoOrigem;

    @XmlElement(name = "origem", nillable = true)
    private String origem;

    @XmlElement(name = "unidadepenalidade", nillable = true)
    private String unidadePenalidade;

    public DadosPABPMSWrapper() {
    }

    public Integer getTipoRegistro() {
        return tipoRegistro;
    }

    public String getNomeCondutor() {
        return nomeCondutor;
    }

    public void setNomeCondutor(String nomeCondutor) {
        this.nomeCondutor = nomeCondutor;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    
    public String getArtigoInciso() {
        return artigoInciso;
    }

    public void setArtigoInciso(String artigoInciso) {
        this.artigoInciso = artigoInciso;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public Long getNumeroCnh() {
        return numeroCnh;
    }

    public void setNumeroCnh(Long numeroCnh) {
        this.numeroCnh = numeroCnh;
    }

    public String getQuantidadeMesesPenalidade() {
        return quantidadeMesesPenalidade;
    }

    public void setQuantidadeMesesPenalidade(String quantidadeMesesPenalidade) {
        this.quantidadeMesesPenalidade = quantidadeMesesPenalidade;
    }

    public Integer getQtdPontosProcesso() {
        return qtdPontosProcesso;
    }

    public void setQtdPontosProcesso(Integer qtdPontosProcesso) {
        this.qtdPontosProcesso = qtdPontosProcesso;
    }

    public String getDescricaoStatus() {
        return descricaoStatus;
    }

    public void setDescricaoStatus(String descricaoStatus) {
        this.descricaoStatus = descricaoStatus;
    }

    public String getDescricaoMotivo() {
        return descricaoMotivo;
    }

    public void setDescricaoMotivo(String descricaoMotivo) {
        this.descricaoMotivo = descricaoMotivo;
    }

    public String getDescricaoAndamento() {
        return descricaoAndamento;
    }

    public void setDescricaoAndamento(String descricaoAndamento) {
        this.descricaoAndamento = descricaoAndamento;
    }

    public String getAnoPortaria() {
        return anoPortaria;
    }

    public void setAnoPortaria(String anoPortaria) {
        this.anoPortaria = anoPortaria;
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

    public AcaoSistemaPAEnum getAcaoSistema() {
        return acaoSistema;
    }

    public void setAcaoSistema(AcaoSistemaPAEnum acaoSistema) {
        this.acaoSistema = acaoSistema;
    }

    public String getNumeroProcessoOrigem() {
        return numeroProcessoOrigem;
    }

    public void setNumeroProcessoOrigem(String numeroProcessoOrigem) {
        this.numeroProcessoOrigem = numeroProcessoOrigem;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getValorPrazo() {
        return valorPrazo;
    }

    public void setValorPrazo(String valorPrazo) {
        this.valorPrazo = valorPrazo;
    }

    public String getUnidadePenalidade() {
        return unidadePenalidade;
    }

    public void setUnidadePenalidade(String unidadePenalidade) {
        this.unidadePenalidade = unidadePenalidade;
    }
}