package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TDO_PAD_EDITAL_PROCESSO_ADMINISTRATIVO")
@NamedQueries({
    @NamedQuery(
            name = "getEditalPorNotificacao",
            query = "SELECT tdo "
            + "FROM EditalProcessoAdministrativo tdo "
            + "WHERE tdo.notificacaoProcessoAdministrativo.id = :p0 "
            + "AND tdo.ativo = :p1 ")
})
public class EditalProcessoAdministrativo extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdo_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Tdo_Notificacao", referencedColumnName = "Tcx_ID")
    private NotificacaoProcessoAdministrativo notificacaoProcessoAdministrativo;

    @Column(name = "Tdo_Numero_Edital")
    private Integer numeroEdital;

    @Column(name = "Tdo_Data_Publicacao")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataPublicacao;

    @Column(name = "Tdo_Termino_Prazo_Edital")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date terminoPrazoEdital;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "Ativo")
    private AtivoEnum ativo;

    public EditalProcessoAdministrativo() {
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

    public NotificacaoProcessoAdministrativo getNotificacaoProcessoAdministrativo() {
        return notificacaoProcessoAdministrativo;
    }

    public void setNotificacaoProcessoAdministrativo(NotificacaoProcessoAdministrativo notificacaoProcessoAdministrativo) {
        this.notificacaoProcessoAdministrativo = notificacaoProcessoAdministrativo;
    }

    public Integer getNumeroEdital() {
        return numeroEdital;
    }

    public void setNumeroEdital(Integer numeroEdital) {
        this.numeroEdital = numeroEdital;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    @XmlElement(name = "dataPublicacao")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    @XmlElement(name = "numeroEditalMascarado")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public String getNumeroEditalMascarado() {
        return numeroEdital.toString();
    }

    public void setNumeroEditalMascarado(String numeroEdital) {
    }

    public Date getTerminoPrazoEdital() {
        return terminoPrazoEdital;
    }

    @XmlElement(name = "terminoPrazoEdital")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setTerminoPrazoEdital(Date terminoPrazoEdital) {
        this.terminoPrazoEdital = terminoPrazoEdital;
    }

}
