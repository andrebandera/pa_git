package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.core.projeto.enums.apo.TipoDocumentoEnum;
import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.util.adapter.*;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.PassoRecursoOnlinePAEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoSituacaoPAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "TB_TEM_RECURSO_PA_ONLINE")
@NamedQueries({
        @NamedQuery(
            name = "RecursoPAOnline.getRecursoOnlinePorRecurso",
            query = "select tem From RecursoPAOnline tem where tem.recurso.id = :p0 and tem.ativo = :p1"
        ),
        @NamedQuery(name = "RecursoPAOnline.getRecursoOnlineEfetivadoPorPAETipoEDestino",
                query = "select tem " +
                        "From RecursoPAOnline tem " +
                        "   inner join  tem.processoAdministrativo tdc " +
                        "where tdc.id = :p0 " +
                        "   and tem.tipo = :p1 " +
                        "   and tem.destino = :p2 " +
                        "   and tem.situacao in (:p3) " +
                        "   and tem.ativo = :p4"),
        @NamedQuery(name = "RecursoPAOnline.getRecursoOnlinePorTokenEProtocoloEPasso",
                query = "select  tem " +
                        "From RecursoPAOnline tem " +
                        "where (:p0 is null or :p0 = tem.token) " +
                        "   and (:p1 is null or :p1 = tem.protocolo) " +
                        "   and tem.passo = :p2 " +
                        "   and tem.ativo = :p3"),
        @NamedQuery(name = "RecursoPAOnline.getRecursoOnlineMaisRecentePorPA",
                query = "select tem " +
                        "From RecursoPAOnline tem " +
                        "   inner  join  tem.processoAdministrativo tdc " +
                        "where tdc.id = :p0 " +
                        "   and tem.ativo = :p1 " +
                        "order by tem.ordemPasso desc"),
        @NamedQuery(name = "RecursoPAOnline.getRecursoOnlineMaisRecentePorTokenEProtocolo",
                query = "select tem " +
                        "From RecursoPAOnline tem " +
                        "where (:p0 is null or :p0  = tem.token) " +
                        "   and (:p1 is null or :p1 = tem.protocolo) " +
                        "   and tem.ativo = :p2 " +
                        "order by tem.ordemPasso desc"),
        @NamedQuery(name = "RecursoPAOnline.getRecursoOnlinePorPaESituacao",
                query = "select  r " +
                        "From RecursoPAOnline r " +
                        "   inner join r.processoAdministrativo p " +
                        "where p.id = :p0 " +
                        "   and r.situacao = :p1 " +
                        "   and r.ativo = :p2")
})
public class RecursoPAOnline extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tem_ID")
    private Long id;

    @Column(name = "Tem_Token")
    private String token;

    @ManyToOne
    @JoinColumn(name = "Tem_Processo_Administrivo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;

    @ManyToOne
    @JoinColumn(name = "Tem_Recurso", referencedColumnName = "Tdp_ID")
    private Recurso recurso;

    @Column(name = "Tem_Data_Recurso")
    @Temporal(TemporalType.DATE)
    private Date dataRecurso;

    @Column(name = "Tem_Data_Prazo_Limite")
    @Temporal(TemporalType.DATE)
    private Date dataPrazoLimite;

    @Column(name = "Tem_Numero_Processo")
    private String numeroProcesso;

    @Column(name = "Tem_Requerente")
    private String requerente;

    @Column(name = "Tem_Rg")
    private String rg;

    @Column(name = "Tem_Cpf")
    private String cpf;

    @Column(name = "Tem_Numero_Registro_CNH")
    private String numeroRegistroCnh;

    @Column(name = "Tem_CEP")
    private String enderecoCEP;

    @Column(name = "Tem_Endereco")
    private String endereco;

    @Column(name = "Tem_Numero")
    private String enderecoNumero;

    @Column(name = "Tem_Bairro")
    private String enderecoBairro;

    @Column(name = "Tem_Complemento")
    private String enderecoComplemento;

    @Column(name = "Tem_UF")
    private String uf;

    @Column(name = "Tem_Cidade")
    private String municipio;

    @Column(name = "Tem_Telefone_Fixo")
    private String telefoneFixo;

    @Column(name = "Tem_Celular")
    private String celular;

    @Column(name = "Tem_Email")
    private String email;

    @Column(name = "Tem_Descricao")
    private String descricaoRecurso;

    @Column(name = "Tem_Cpf_Usuario")
    private String cpfUsuario;

    @Column(name = "Tem_Tipo_Documento")
    @Enumerated(EnumType.STRING)
    private TipoDocumentoEnum tipoDocumento;

    @Column(name = "Tem_Numero_Protocolo")
    private String protocolo;

    @Column(name = "Tem_Motivo_Recusa")
    private String motivoRecusa;

    @Column(name = "Tem_Motivo_Cancelamento")
    private String motivoCancelamento;

    @Column(name = "Tem_IP")
    private String ip;

    @Column(name = "Tem_Tipo")
    @Enumerated(EnumType.STRING)
    private TipoFasePaEnum tipo;

    @Column(name = "Tem_Situacao")
    @Enumerated(EnumType.STRING)
    private RecursoSituacaoPAEnum situacao;

    @Column(name = "Tem_Passo")
    @Enumerated(EnumType.STRING)
    private PassoRecursoOnlinePAEnum passo;

    @Column(name = "Tem_Destino")
    @Enumerated(EnumType.STRING)
    private OrigemDestinoEnum destino;

    @Column(name = "Tem_Ordem_Passo")
    private Integer ordemPasso;

    @Column(name = "Tem_Indice_Tempestividade")
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum indiceTempestividade;

    @Column(name = "Tem_Data_Resposta")
    @Temporal(TemporalType.DATE)
    private Date dataResposta;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public RecursoPAOnline() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public void setId(Serializable id) {
        this.id = (id != null ? Long.valueOf(id.toString()) : null);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    public Date getDataRecurso() {
        return dataRecurso;
    }

    @XmlElement(name = "dataRecurso")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataRecurso(Date dataRecurso) {
        this.dataRecurso = dataRecurso;
    }

    public Date getDataPrazoLimite() {
        return dataPrazoLimite;
    }

    @XmlElement(name = "dataPrazoLimite")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataPrazoLimite(Date dataPrazoLimite) {
        this.dataPrazoLimite = dataPrazoLimite;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    @XmlElement(name = "numeroProcesso")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
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

    public String getCpf() {
        return cpf;
    }

    @XmlJavaTypeAdapter(CPFAdapter.class)
    @XmlElement(name = "cpf")
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNumeroRegistroCnh() {
        return numeroRegistroCnh;
    }

    public void setNumeroRegistroCnh(String numeroRegistroCnh) {
        this.numeroRegistroCnh = numeroRegistroCnh;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getEnderecoNumero() {
        return enderecoNumero;
    }

    public void setEnderecoNumero(String enderecoNumero) {
        this.enderecoNumero = enderecoNumero;
    }

    public String getEnderecoBairro() {
        return enderecoBairro;
    }

    public void setEnderecoBairro(String enderecoBairro) {
        this.enderecoBairro = enderecoBairro;
    }

    public String getEnderecoComplemento() {
        return enderecoComplemento;
    }

    public void setEnderecoComplemento(String enderecoComplemento) {
        this.enderecoComplemento = enderecoComplemento;
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

    @XmlJavaTypeAdapter(MascaraSemEspacoEmBrancoAdapter.class)
    public void setTelefoneFixo(String telefoneFixo) {
        this.telefoneFixo = telefoneFixo;
    }

    public String getCelular() {
        return celular;
    }

    @XmlJavaTypeAdapter(MascaraSemEspacoEmBrancoAdapter.class)
    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescricaoRecurso() {
        return descricaoRecurso;
    }

    public void setDescricaoRecurso(String descricaoRecurso) {
        this.descricaoRecurso = descricaoRecurso;
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

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public PassoRecursoOnlinePAEnum getPasso() {
        return passo;
    }

    public void setPasso(PassoRecursoOnlinePAEnum passo) {
        this.passo = passo;
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

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public BooleanEnum getIndiceTempestividade() {
        return indiceTempestividade;
    }

    public void setIndiceTempestividade(BooleanEnum indiceTempestividade) {
        this.indiceTempestividade = indiceTempestividade;
    }

    public String getEnderecoCEP() {
        return enderecoCEP;
    }

    public void setEnderecoCEP(String enderecoCEP) {
        this.enderecoCEP = enderecoCEP;
    }

    public Date getDataResposta() {
        return dataResposta;
    }

    @XmlElement(name = "dataResposta")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataResposta(Date dataResposta) {
        this.dataResposta = dataResposta;
    }
}
