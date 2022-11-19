package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Lillydi
 */
public class ParametrosIntegracaoBloqueioBCAWrapper implements Serializable {

    private String cpf;

    private TipoProcessoEnum tipoProcesso;

    private String numeroProcesso;

    private String numeroPortaria;

    private Date dataPublicacaoPortaria;

    private String artigoIncisoParagrafo;

    private Date dataBloqueio;

    private Date dataEntregaCnh;

    private String numeroRestricaoComBloqueio;

    private String numeroRestricaoSemBloqueio;

    private String mesesPenalizacao;

    private String autoInfracao;

    private String codigoInfracao;

    private Date dataInicioPenalidade;

    private Long cnh;

    private String motivoRestricao;

    private String numeroRestricao;

    private String orgaoLiberador;

    private String pid;

    private String situacaoDesbloqueio;

    private Long paTdcId;

    public ParametrosIntegracaoBloqueioBCAWrapper() {
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public TipoProcessoEnum getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcessoEnum tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroPortaria() {
        return numeroPortaria;
    }

    public void setNumeroPortaria(String numeroPortaria) {
        this.numeroPortaria = numeroPortaria;
    }

    public Date getDataPublicacaoPortaria() {
        return dataPublicacaoPortaria;
    }

    public void setDataPublicacaoPortaria(Date dataPublicacaoPortaria) {
        this.dataPublicacaoPortaria = dataPublicacaoPortaria;
    }

    public String getArtigoIncisoParagrafo() {
        return artigoIncisoParagrafo;
    }

    public void setArtigoIncisoParagrafo(String artigoIncisoParagrafo) {
        this.artigoIncisoParagrafo = artigoIncisoParagrafo;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public Date getDataEntregaCnh() {
        return dataEntregaCnh;
    }

    public void setDataEntregaCnh(Date dataEntregaCnh) {
        this.dataEntregaCnh = dataEntregaCnh;
    }

    public String getNumeroRestricaoComBloqueio() {
        return numeroRestricaoComBloqueio;
    }

    public void setNumeroRestricaoComBloqueio(String numeroRestricaoComBloqueio) {
        this.numeroRestricaoComBloqueio = numeroRestricaoComBloqueio;
    }

    public String getNumeroRestricaoSemBloqueio() {
        return numeroRestricaoSemBloqueio;
    }

    public void setNumeroRestricaoSemBloqueio(String numeroRestricaoSemBloqueio) {
        this.numeroRestricaoSemBloqueio = numeroRestricaoSemBloqueio;
    }

    public String getMesesPenalizacao() {
        return mesesPenalizacao;
    }

    public void setMesesPenalizacao(String mesesPenalizacao) {
        this.mesesPenalizacao = mesesPenalizacao;
    }

    public String getAutoInfracao() {
        return autoInfracao;
    }

    public void setAutoInfracao(String autoInfracao) {
        this.autoInfracao = autoInfracao;
    }

    public String getCodigoInfracao() {
        return codigoInfracao;
    }

    public void setCodigoInfracao(String codigoInfracao) {
        this.codigoInfracao = codigoInfracao;
    }

    public Date getDataInicioPenalidade() {
        return dataInicioPenalidade;
    }

    public void setDataInicioPenalidade(Date dataInicioPenalidade) {
        this.dataInicioPenalidade = dataInicioPenalidade;
    }

    public Long getCnh() {
        return cnh;
    }

    public void setCnh(Long cnh) {
        this.cnh = cnh;
    }

    public String getMotivoRestricao() {
        return motivoRestricao;
    }

    public void setMotivoRestricao(String motivoRestricao) {
        this.motivoRestricao = motivoRestricao;
    }

    public String getNumeroRestricao() {
        return numeroRestricao;
    }

    public void setNumeroRestricao(String numeroRestricao) {
        this.numeroRestricao = numeroRestricao;
    }

    public String getOrgaoLiberador() {
        return orgaoLiberador;
    }

    public void setOrgaoLiberador(String orgaoLiberador) {
        this.orgaoLiberador = orgaoLiberador;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSituacaoDesbloqueio() {
        return situacaoDesbloqueio;
    }

    public void setSituacaoDesbloqueio(String situacaoDesbloqueio) {
        this.situacaoDesbloqueio = situacaoDesbloqueio;
    }

    public Long getPaTdcId() {
        return paTdcId;
    }

    public void setPaTdcId(Long paTdcId) {
        this.paTdcId = paTdcId;
    }
}