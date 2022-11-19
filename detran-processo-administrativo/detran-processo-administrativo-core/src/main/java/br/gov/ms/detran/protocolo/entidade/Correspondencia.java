package br.gov.ms.detran.protocolo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TAG_CORRESPONDENCIA")
@NamedQueries({
    @NamedQuery(
            name = "Correspondencia.getCorrespondenciaPorNumeroNotificacaoOuNumeroProcesso",
            query = "SELECT tcx.correspondencia "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + "INNER JOIN tcx.processoAdministrativo tdc "
                    + "WHERE tcx.numeroNotificacao = :p0 "
                    + "AND tdc.numeroProcesso = :p1 "),
    @NamedQuery(
            name = "Correspondencia.getCorrespondenciaPorProcessoAdministrativoETipo",
            query = "SELECT tag "
                    + " From NotificacaoProcessoAdministrativo tcx "
                    + "     inner join tcx.correspondencia tag "
                    + "     inner join tcx.processoAdministrativo tdc "
                    + " where tdc.id = :p0 "
                    + "     and tcx.tipoNotificacaoProcesso = :p1 "
                    + "     and tcx.ativo = :p2 "
                    + "     and tag.ativo = :p2 ")
})
public class Correspondencia extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tag_ID")
    private Long id;

    @Column(name = "Tag_Setor_Tipo_Correspondencia")
    private Long setorTipoCorrespondencia;

    @JoinColumn(name = "Tag_Especie", referencedColumnName = "Tam_ID")
    @ManyToOne
    private CorrespondenciaEspecie especie;

    @Column(name = "Tag_Qte_Especie")
    private Integer qteEspecie;

    @JoinColumn(name = "Tag_Meio_Envio", referencedColumnName = "Tbd_ID")
    @ManyToOne
    private CorrespondenciaMeioEnvio meioEnvio;

    @Column(name = "Tag_Tipo_Origem")
    private Integer tipoOrigem;

    @Column(name = "Tag_Forma_Envio")
    private Integer formaEnvio;

    @Column(name = "Tag_Observacao")
    private String observacao;

    @Column(name = "Tag_Situacao")
    private Integer situacao;

    @Column(name = "Tag_Usuario_Inclusao")
    private String usuarioInclusaoCorrespondencia;

    @Column(name = "Tag_Data_Inclusao")
    @Temporal(TemporalType.DATE)
    private Date dataInclusaoCorrespondencia;

    @Column(name = "Tag_Usuario_Recebimento")
    private String usuarioRecebimento;

    @Column(name = "Tag_Data_Recebimento")
    @Temporal(TemporalType.DATE)
    private Date dataRecebimento;

    @Column(name = "Tag_Remetente")
    private String remetente;

    @Column(name = "Tag_Destinatario")
    private String destinatario;

    @Column(name = "Tag_Situacao_Abertura")
    private Integer situacaoAbertura;

    @Column(name = "Ativo")
    private AtivoEnum ativo;

    public Correspondencia() {
    }

    public Correspondencia(Long id, Integer situacao) {
        this.id = id;
        this.situacao = situacao;
    }

    @Override
    public Long getId() {
        return id;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public void setId(Serializable id) {
        this.id = Long.valueOf(id.toString());
    }

    public Long getSetorTipoCorrespondencia() {
        return setorTipoCorrespondencia;
    }

    public void setSetorTipoCorrespondencia(Long setorTipoCorrespondencia) {
        this.setorTipoCorrespondencia = setorTipoCorrespondencia;
    }

    public CorrespondenciaEspecie getEspecie() {
        return especie;
    }

    public void setEspecie(CorrespondenciaEspecie especie) {
        this.especie = especie;
    }

    public Integer getQteEspecie() {
        return qteEspecie;
    }

    public void setQteEspecie(Integer qteEspecie) {
        this.qteEspecie = qteEspecie;
    }

    public CorrespondenciaMeioEnvio getMeioEnvio() {
        return meioEnvio;
    }

    public void setMeioEnvio(CorrespondenciaMeioEnvio meioEnvio) {
        this.meioEnvio = meioEnvio;
    }

    public Integer getTipoOrigem() {
        return tipoOrigem;
    }

    public void setTipoOrigem(Integer tipoOrigem) {
        this.tipoOrigem = tipoOrigem;
    }

    public Integer getFormaEnvio() {
        return formaEnvio;
    }

    public void setFormaEnvio(Integer formaEnvio) {
        this.formaEnvio = formaEnvio;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Integer getSituacao() {
        return situacao;
    }

    public void setSituacao(Integer situacao) {
        this.situacao = situacao;
    }

    public String getUsuarioInclusaoCorrespondencia() {
        return usuarioInclusaoCorrespondencia;
    }

    public void setUsuarioInclusaoCorrespondencia(String usuarioInclusaoCorrespondencia) {
        this.usuarioInclusaoCorrespondencia = usuarioInclusaoCorrespondencia;
    }

    public Date getDataInclusaoCorrespondencia() {
        return dataInclusaoCorrespondencia;
    }

    public void setDataInclusaoCorrespondencia(Date dataInclusaoCorrespondencia) {
        this.dataInclusaoCorrespondencia = dataInclusaoCorrespondencia;
    }

    public String getUsuarioRecebimento() {
        return usuarioRecebimento;
    }

    public void setUsuarioRecebimento(String usuarioRecebimento) {
        this.usuarioRecebimento = usuarioRecebimento;
    }

    public Date getDataRecebimento() {
        return dataRecebimento;
    }

    @XmlElement(name = "dataRecebimento")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataRecebimento(Date dataRecebimento) {
        this.dataRecebimento = dataRecebimento;
    }

    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public Integer getSituacaoAbertura() {
        return situacaoAbertura;
    }

    public void setSituacaoAbertura(Integer situacaoAbertura) {
        this.situacaoAbertura = situacaoAbertura;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }
}