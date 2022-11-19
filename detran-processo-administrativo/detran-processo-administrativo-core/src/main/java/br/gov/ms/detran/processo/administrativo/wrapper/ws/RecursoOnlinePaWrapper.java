package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.core.projeto.enums.apo.TipoDocumentoEnum;
import br.gov.ms.detran.comum.util.adapter.CPFAdapter;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.ConsultaRecursoPaOnlineSituacaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoSituacaoPAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoOnlinePaDocumentoWrapper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

public class RecursoOnlinePaWrapper {

    private Integer passo;

    private String cpf;

    private String numeroProcesso;

    private String token;

    private String protocolo;

    private String email;

    private ProcessoAdministrativo processoAdministrativo;

    private TipoFasePaEnum tipoRecurso;

    private OrigemDestinoEnum destinoRecurso;

    private String ip;

    private Date dataRecurso;

    private String requerente;

    private String rg;

    private String registroCnh;

    private String cep;

    private String endereco;

    private String numero;

    private String bairro;

    private String complemento;

    private String uf;

    private String municipio;

    private String telefoneFixo;

    private String telefoneCelular;

    private String descricao;

    private String cpfUsuario;

    private TipoDocumentoEnum tipoDocumento;

    private String motivoRecusa;

    private String motivoCancelamento;

    private TipoFasePaEnum tipo;

    private RecursoSituacaoPAEnum situacao;

    private OrigemDestinoEnum destino;

    private Integer ordemPasso;

    private Boolean indiceTempestividade;

    private List<RecursoOnlinePaDocumentoWrapper> documentos;

    private RecursoOnlinePaDocumentoWrapper formularioRecurso;

    private RecursoOnlinePaDocumentoWrapper formularioRecursoAssinado;

    private Boolean consultaAndamento = Boolean.TRUE;

    private ConsultaRecursoPaOnlineSituacaoEnum andamento;

    public RecursoOnlinePaWrapper() {
    }

    public RecursoOnlinePaWrapper(String token, String protocolo) {
        this.token = token;
        this.protocolo = protocolo;
    }

    public Integer getPasso() {
        return passo;
    }

    public void setPasso(Integer passo) {
        this.passo = passo;
    }

    public String getCpf() {
        return cpf;
    }

    @XmlJavaTypeAdapter(CPFAdapter.class)
    @XmlElement(name = "cpf")
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    @XmlElement(name = "numeroProcesso")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public TipoFasePaEnum getTipoRecurso() {
        return tipoRecurso;
    }

    public void setTipoRecurso(TipoFasePaEnum tipoRecurso) {
        this.tipoRecurso = tipoRecurso;
    }

    public OrigemDestinoEnum getDestinoRecurso() {
        return destinoRecurso;
    }

    public void setDestinoRecurso(OrigemDestinoEnum destinoRecurso) {
        this.destinoRecurso = destinoRecurso;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getDataRecurso() {
        return dataRecurso;
    }

    @XmlElement(name = "dataRecurso")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataRecurso(Date dataRecurso) {
        this.dataRecurso = dataRecurso;
    }

    public String getRequerente() {
        return requerente;
    }

    public void setRequerente(String requerente) {
        this.requerente = requerente;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getRegistroCnh() {
        return registroCnh;
    }

    public void setRegistroCnh(String registroCnh) {
        this.registroCnh = registroCnh;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getTelefoneFixo() {
        return telefoneFixo;
    }

    public void setTelefoneFixo(String telefoneFixo) {
        this.telefoneFixo = telefoneFixo;
    }

    public String getTelefoneCelular() {
        return telefoneCelular;
    }

    public void setTelefoneCelular(String telefoneCelular) {
        this.telefoneCelular = telefoneCelular;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    public void setCpfUsuario(String cpfUsuario) {
        this.cpfUsuario = cpfUsuario;
    }

    public TipoDocumentoEnum getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoEnum tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public TipoFasePaEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoFasePaEnum tipo) {
        this.tipo = tipo;
    }

    public RecursoSituacaoPAEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(RecursoSituacaoPAEnum situacao) {
        this.situacao = situacao;
    }

    public OrigemDestinoEnum getDestino() {
        return destino;
    }

    public void setDestino(OrigemDestinoEnum destino) {
        this.destino = destino;
    }

    public Integer getOrdemPasso() {
        return ordemPasso;
    }

    public void setOrdemPasso(Integer ordemPasso) {
        this.ordemPasso = ordemPasso;
    }

    public Boolean getIndiceTempestividade() {
        return indiceTempestividade;
    }

    public void setIndiceTempestividade(Boolean indiceTempestividade) {
        this.indiceTempestividade = indiceTempestividade;
    }

    public List<RecursoOnlinePaDocumentoWrapper> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<RecursoOnlinePaDocumentoWrapper> documentos) {
        this.documentos = documentos;
    }

    public RecursoOnlinePaDocumentoWrapper getFormularioRecurso() {
        return formularioRecurso;
    }

    public void setFormularioRecurso(RecursoOnlinePaDocumentoWrapper formularioRecurso) {
        this.formularioRecurso = formularioRecurso;
    }

    public RecursoOnlinePaDocumentoWrapper getFormularioRecursoAssinado() {
        return formularioRecursoAssinado;
    }

    public void setFormularioRecursoAssinado(RecursoOnlinePaDocumentoWrapper formularioRecursoAssinado) {
        this.formularioRecursoAssinado = formularioRecursoAssinado;
    }

    public Boolean getConsultaAndamento() {
        return consultaAndamento;
    }

    public void setConsultaAndamento(Boolean consultaAndamento) {
        this.consultaAndamento = consultaAndamento;
    }

    public ConsultaRecursoPaOnlineSituacaoEnum getAndamento() {
        return andamento;
    }

    public void setAndamento(ConsultaRecursoPaOnlineSituacaoEnum andamento) {
        this.andamento = andamento;
    }
}
