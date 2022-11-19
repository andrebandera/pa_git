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
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Lillydi
 */
@Entity
@Table(name = "TB_TDJ_PAD_NOTIFICACAO_COMPLEMENTO")
@NamedQueries({
    @NamedQuery(
            name = "NotificacaoComplemento.getDataPenalizacaoPorProcessoAdministrativo",
            query = "SELECT new NotificacaoComplemento(tdj.id, tdj.data) "
                    + "FROM NotificacaoComplemento tdj "
                    + "INNER JOIN tdj.notificacao tcx "
                    + "INNER JOIN tcx.processoAdministrativo tdc "
                    + "WHERE tdc.id = :p0 "
                    + "AND tdj.ativo = :p1 "),
    @NamedQuery(
            name = "NotificacaoComplemento.getMenorDataPenalizacaoDosPAs",
            query = "SELECT new NotificacaoComplemento(tdj.id, tdj.data) "
                    + "FROM NotificacaoComplemento tdj "
                    + "INNER JOIN tdj.notificacao tcx "
                    + "INNER JOIN tcx.processoAdministrativo tdc "
                    + "WHERE tdc.id in (:p0) "
                    + "AND tdj.ativo = :p1 ORDER BY tdj.data"),
    @NamedQuery(
            name = "NotificacaoComplemento.getInformacaoEditalPenalidadeParaProcessoAdministrativoParaNotificacaoPorTipo",
            query = "SELECT new NotificacaoComplemento(tdj.id, tdj.data) "
            + "FROM NotificacaoComplemento tdj "
            + " INNER JOIN tdj.notificacao tcx "
            + " INNER JOIN tcx.processoAdministrativo tdc "
            + "WHERE tdc.id = :p0 "
            + " AND tdj.ativo = :p1 "
            + " AND tcx.ativo = :p1 "
            + " AND tdc.ativo = :p1 "
            + " AND tcx.tipoNotificacaoProcesso = :p2 "
    ),
    @NamedQuery(
            name = "NotificacaoComplemento.getListNotificacaoComplementoPorProcessoAdministrativo",
            query = "SELECT tdj "
            + "FROM NotificacaoComplemento tdj "
            + " INNER JOIN tdj.notificacao tcx "
            + " INNER JOIN tcx.processoAdministrativo tdc "
            + "WHERE tdc.id = :p0 "
            + " AND tdj.ativo = :p1 "
            + " AND tcx.ativo = :p1 "
            + " AND tdc.ativo = :p1 "
    ),
    @NamedQuery(
            name = "NotificacaoComplemento.getComplementoPorNotificacao",
            query = "SELECT tdj From NotificacaoComplemento tdj where tdj.notificacao.id = :p0 and tdj.ativo = :p1")
})
public class NotificacaoComplemento extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdj_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Tdj_Notificacao_Processo", referencedColumnName = "Tcx_ID")
    private NotificacaoProcessoAdministrativo notificacao;

    @Column(name = "Tdj_Numero_Portaria")
    private String numeroPortaria;

    @Column(name = "Tdj_Tempo_Penalidade")
    private String tempoPenalidade;

    @Column(name = "Tdj_Data")
    @Temporal(TemporalType.DATE)
    private Date data;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public NotificacaoComplemento() {
    }

    public NotificacaoComplemento(Long id) {
        this.id = id;
    }

    public NotificacaoComplemento(Long id, Date data) {
        this.id = id;
        this.data = data;
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

    public NotificacaoProcessoAdministrativo getNotificacao() {
        return notificacao;
    }

    public void setNotificacao(NotificacaoProcessoAdministrativo notificacao) {
        this.notificacao = notificacao;
    }

    public String getNumeroPortaria() {
        return numeroPortaria;
    }

    public void setNumeroPortaria(String numeroPortaria) {
        this.numeroPortaria = numeroPortaria;
    }

    public String getTempoPenalidade() {
        return tempoPenalidade;
    }

    public void setTempoPenalidade(String tempoPenalidade) {
        this.tempoPenalidade = tempoPenalidade;
    }

    public Date getData() {
        return data;
    }

    @XmlElement(name = "data")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setData(Date data) {
        this.data = data;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    @XmlElement(name = "numeroPortariaMascarado")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public String getNumeroPortariaMascarado() {
        return numeroPortaria;
    }

    @XmlElement(name = "numeroPortariaMascarado")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public void setNumeroPortariaMascarado(String numeroProcesso) {
    }

}
