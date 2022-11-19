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
public class DadosPABPMSCondutorWrapper {

    @XmlElement(name = "tiporegistro", nillable = true)
    private final Integer tipoRegistro = TipoRegistroBPMSEnum.DADOS_CONDUTOR.getCodigo();

    @XmlElement(name = "cpf", nillable = true)
    private String cpf;

    @XmlElement(name = "renach", nillable = true)
    private String renach;

    @XmlElement(name = "numeroregistro", nillable = true)
    private String numeroRegistro;

    @XmlElement(name = "numeropgu", nillable = true)
    private String numeroPgu;

    @XmlElement(name = "numerocnh", nillable = true)
    private Long numeroCnh;

    @XmlElement(name = "nacionalidade", nillable = true)
    private String nacionalidade;

    @XmlElement(name = "nomecondutor", nillable = true)
    private String nomeCondutor;

    @XmlElement(name = "nomemae", nillable = true)
    private String nomeMae;

    @XmlElement(name = "nomepai", nillable = true)
    private String nomePai;

    @XmlElement(name = "sexo", nillable = true)
    private String sexo;

    @XmlElement(name = "categoria1habilitacao", nillable = true)
    private String categoria1Habilitacao;

    @XmlElement(name = "dataemissaodefinitiva", nillable = true)
    private String dataEmissaoDefinitiva;

    @XmlElement(name = "documento", nillable = true)
    private String documento;

    @XmlElement(name = "dataatualizacaobinco", nillable = true)
    private Date dataAtualizacaoBinco;

    @XmlElement(name = "nomemunicipio", nillable = true)
    private String nomeMunicipio;

    @XmlElement(name = "dataatualizacao", nillable = true)
    private Date dataAtualizacao;

    @XmlElement(name = "codigoultimatransacao", nillable = true)
    private String codigoUltimaTransacao;

    @XmlElement(name = "datanascimento", nillable = true)
    private Date dataNascimento;

    @XmlElement(name = "municipionascimento", nillable = true)
    private String nomeMunicipioNascimento;

    @XmlElement(name = "categoriaatualcnh", nillable = true)
    private String categoriaAtualCnh;

    @XmlElement(name = "datavalidadecnh", nillable = true)
    private Date dataValidadeCnh;

    @XmlElement(name = "datavalidadedefinitiva", nillable = true)
    private Date dataValidadeDefinitiva;

    @XmlElement(name = "data1habilitacao", nillable = true)
    private Date data1Habilitacao;

    @XmlElement(name = "uf1habilitacao", nillable = true)
    private String uf1Habilitacao;

    @XmlElement(name = "uftransferencia", nillable = true)
    private String ufTransferencia;

    @XmlElement(name = "situacaocnh", nillable = true)
    private String situacaoCnh;

    @XmlElement(name = "situacaosihab", nillable = true)
    private String situacaoSihab;

    @XmlElement(name = "observacao", nillable = true)
    private String indicativoObservacao;

    @XmlElement(name = "situacaocpf", nillable = true)
    private String situacaoCpf;

    @XmlElement(name = "logradouro", nillable = true)
    private String logradouro;

    @XmlElement(name = "complemento", nillable = true)
    private String complemento;

    @XmlElement(name = "bairro", nillable = true)
    private String bairro;

    @XmlElement(name = "cep", nillable = true)
    private String cep;

    @XmlElement(name = "telefone", nillable = true)
    private String telefone;

    public DadosPABPMSCondutorWrapper() {
    }

    public Integer getTipoRegistro() {
        return tipoRegistro;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRenach() {
        return renach;
    }

    public void setRenach(String renach) {
        this.renach = renach;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getNumeroPgu() {
        return numeroPgu;
    }

    public void setNumeroPgu(String numeroPgu) {
        this.numeroPgu = numeroPgu;
    }

    public Long getNumeroCnh() {
        return numeroCnh;
    }

    public void setNumeroCnh(Long numeroCnh) {
        this.numeroCnh = numeroCnh;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getNomeCondutor() {
        return nomeCondutor;
    }

    public void setNomeCondutor(String nomeCondutor) {
        this.nomeCondutor = nomeCondutor;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public String getNomePai() {
        return nomePai;
    }

    public void setNomePai(String nomePai) {
        this.nomePai = nomePai;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCategoria1Habilitacao() {
        return categoria1Habilitacao;
    }

    public void setCategoria1Habilitacao(String categoria1Habilitacao) {
        this.categoria1Habilitacao = categoria1Habilitacao;
    }

    public String getDataEmissaoDefinitiva() {
        return dataEmissaoDefinitiva;
    }

    public void setDataEmissaoDefinitiva(String dataEmissaoDefinitiva) {
        this.dataEmissaoDefinitiva = dataEmissaoDefinitiva;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Date getDataAtualizacaoBinco() {
        return dataAtualizacaoBinco;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataAtualizacaoBinco(Date dataAtualizacaoBinco) {
        this.dataAtualizacaoBinco = dataAtualizacaoBinco;
    }

    public String getNomeMunicipio() {
        return nomeMunicipio;
    }

    public void setNomeMunicipio(String nomeMunicipio) {
        this.nomeMunicipio = nomeMunicipio;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getCodigoUltimaTransacao() {
        return codigoUltimaTransacao;
    }

    public void setCodigoUltimaTransacao(String codigoUltimaTransacao) {
        this.codigoUltimaTransacao = codigoUltimaTransacao;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getNomeMunicipioNascimento() {
        return nomeMunicipioNascimento;
    }

    public void setNomeMunicipioNascimento(String nomeMunicipioNascimento) {
        this.nomeMunicipioNascimento = nomeMunicipioNascimento;
    }

    public String getCategoriaAtualCnh() {
        return categoriaAtualCnh;
    }

    public void setCategoriaAtualCnh(String categoriaAtualCnh) {
        this.categoriaAtualCnh = categoriaAtualCnh;
    }

    public Date getDataValidadeCnh() {
        return dataValidadeCnh;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataValidadeCnh(Date dataValidadeCnh) {
        this.dataValidadeCnh = dataValidadeCnh;
    }

    public Date getDataValidadeDefinitiva() {
        return dataValidadeDefinitiva;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataValidadeDefinitiva(Date dataValidadeDefinitiva) {
        this.dataValidadeDefinitiva = dataValidadeDefinitiva;
    }

    public Date getData1Habilitacao() {
        return data1Habilitacao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setData1Habilitacao(Date data1Habilitacao) {
        this.data1Habilitacao = data1Habilitacao;
    }

    public String getUf1Habilitacao() {
        return uf1Habilitacao;
    }

    public void setUf1Habilitacao(String uf1Habilitacao) {
        this.uf1Habilitacao = uf1Habilitacao;
    }

    public String getUfTransferencia() {
        return ufTransferencia;
    }

    public void setUfTransferencia(String ufTransferencia) {
        this.ufTransferencia = ufTransferencia;
    }

    public String getSituacaoCnh() {
        return situacaoCnh;
    }

    public void setSituacaoCnh(String situacaoCnh) {
        this.situacaoCnh = situacaoCnh;
    }

    public String getSituacaoSihab() {
        return situacaoSihab;
    }

    public void setSituacaoSihab(String situacaoSihab) {
        this.situacaoSihab = situacaoSihab;
    }

    public String getIndicativoObservacao() {
        return indicativoObservacao;
    }

    public void setIndicativoObservacao(String indicativoObservacao) {
        this.indicativoObservacao = indicativoObservacao;
    }

    public String getSituacaoCpf() {
        return situacaoCpf;
    }

    public void setSituacaoCpf(String situacaoCpf) {
        this.situacaoCpf = situacaoCpf;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
}
