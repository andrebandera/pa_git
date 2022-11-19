package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.MotivoBloqueioCnhEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.MotivoDesbloqueioCnhEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoBloqueioBCAEnum;
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

@Entity
@Table(name = "TB_TDK_BLOQUEIO_BCA")
@NamedQueries({
    @NamedQuery(
            name = "BloqueioBCA.getBloqueioBcaPorPaEAtivo",
            query = "SELECT tdk FROM BloqueioBCA tdk INNER JOIN tdk.processoAdministrativo tdc WHERE tdc.id = :p0 AND tdk.ativo = :p1 "),
    @NamedQuery(
            name = "BloqueioBCA.getBloqueioBCAporPaESituacaoEAtivo",
            query = "SELECT new BloqueioBCA(tdk.id, tdk.versaoRegistro, tdk.situacao) "
            + "FROM BloqueioBCA tdk "
            + "INNER JOIN tdk.processoAdministrativo tdc "
            + "WHERE tdc.id = :p0 "
            + "AND tdk.situacao = :p1 "
            + "AND tdk.ativo = :p2 "),
    @NamedQuery(
            name = "BloqueioBCA.findAll",
            query = "SELECT tdk FROM BloqueioBCA tdk"),
    @NamedQuery(
            name = "BloqueioBCA.getBloqueiosPorAndamento",
            query = "SELECT new BloqueioBCA(tdk.id) From BloqueioBCA tdk "
            + "WHERE EXISTS(SELECT 1 "
            + "             FROM PAOcorrenciaStatus tde "
            + "                 INNER JOIN tde.statusAndamento tcd "
            + "             WHERE tdk.processoAdministrativo.id = tde.processoAdministrativo.id "
            + "                 AND tcd.andamentoProcesso.codigo in (:p0) "
            + "                 AND tde.situacao = :p1 "
            + "                 AND tde.ativo = :p2 "
            + "                 AND tcd.ativo = :p2) "
            + "AND tdk.ativo = :p2 "
            + "AND tdk.situacao = :p3"),
    @NamedQuery(
            name = "BloqueioBCA.getBloqueioPorProcessoAdministrativo",
            query = "SELECT tdk "
            + "FROM BloqueioBCA tdk "
            + " INNER JOIN tdk.processoAdministrativo tdc "
            + "where tdc.id = :p0 "
            + " and tdk.ativo = :p1"
    ),
    @NamedQuery(
            name = "BloqueioBCA.validarInfracaoCometidaPeriodoReincidente",
            query = "SELECT new BloqueioBCA(tdk.id) "
            + " FROM BloqueioBCA tdk "
            + " where tdk.processoAdministrativo.id in (:p0) "
            + "     and tdk.ativo = :p1 "
            + "     and tdk.dataInicio <= :p2 "
            + "     and (tdk.dataFim IS NULL or tdk.dataFim >= :p2) "
            + "     and tdk.situacao = :p3 "
    ),
    @NamedQuery(
            name = "BloqueioBCA.getBloqueioBcaPorCpfESituacaoEAtivo",
            query = "SELECT tdk "
            + "FROM BloqueioBCA tdk "
            + " INNER JOIN tdk.processoAdministrativo tdc "
            + "WHERE tdc.cpf = :p0 "
            + " AND tdk.situacao = :p1 "
            + " AND tdk.ativo = :p2 "
            + "ORDER BY tdk.dataFim DESC "
    ),
    @NamedQuery(
            name = "BloqueioBCA.getBloqueioBcaPorCpfTipoEAtivo",
            query = "SELECT tdk FROM BloqueioBCA tdk "
            + "INNER JOIN MovimentoBloqueioBCA tdz ON tdz.bloqueioBCA.id = tdk.id AND tdz.tipo <> :p0 "
            + "INNER JOIN ProcessoAdministrativo tdc ON tdk.processoAdministrativo.id = tdc.id AND tdc.cpf = :p1 "
            + "WHERE tdk.ativo = :p2 AND tdk.dataInicio is not null ORDER BY tdk.dataInicio"
    )

})
public class BloqueioBCA extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdk_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Tdk_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;

    @Column(name = "Tdk_Numero_Bloqueio_BCA")
    private String numeroBloqueioBCA;

    @Column(name = "Tdk_Situacao")
    @Enumerated(EnumType.STRING)
    private SituacaoBloqueioBCAEnum situacao;

    @Column(name = "Tdk_Motivo_Bloqueio")
    private String motivoBloqueio;

    @Column(name = "Tdk_Motivo_Desbloqueio")
    private String motivoDesbloqueio;

    @Column(name = "Tdk_Data_Inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInicio;

    @Column(name = "Tdk_Data_Fim")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataFim;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public BloqueioBCA() {
    }

    public BloqueioBCA(Long id) {
        this.id = id;
    }

    public BloqueioBCA(Long id, Long versaoRegistro, SituacaoBloqueioBCAEnum situacao) {
        this(id);
        super.setVersaoRegistro(versaoRegistro);
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
        this.id = (id != null ? Long.valueOf(id.toString()) : null);
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public String getNumeroBloqueioBCA() {
        return numeroBloqueioBCA;
    }

    public void setNumeroBloqueioBCA(String numeroBloqueioBCA) {
        this.numeroBloqueioBCA = numeroBloqueioBCA;
    }

    public SituacaoBloqueioBCAEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoBloqueioBCAEnum situacao) {
        this.situacao = situacao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    @XmlElement(name = "dataInicio")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    @XmlElement(name = "dataFim")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public MotivoBloqueioCnhEnum getMotivoBloqueio() {
        return MotivoBloqueioCnhEnum.getMotivoBloqueioCnhEnum(this.motivoBloqueio);
    }

    public void setMotivoBloqueio(MotivoBloqueioCnhEnum motivo) {
        this.motivoBloqueio = motivo == null ? null : motivo.getMotivo();
    }

    public MotivoDesbloqueioCnhEnum getMotivoDesbloqueio() {
        return MotivoDesbloqueioCnhEnum.getMotivoDesbloqueioCnhEnum(this.motivoDesbloqueio);
    }

    public void setMotivoDesbloqueio(MotivoDesbloqueioCnhEnum motivo) {
        this.motivoDesbloqueio = motivo == null ? null : motivo.getMotivo();
    }

    public String getMotivoDesbloqueioLabel() {
        MotivoDesbloqueioCnhEnum motivo = MotivoDesbloqueioCnhEnum.getMotivoDesbloqueioCnhEnum(this.motivoDesbloqueio);
        return motivo == null ? "" : motivo.toString();
    }

    public String getMotivoBloqueioLabel() {
        MotivoBloqueioCnhEnum motivo = MotivoBloqueioCnhEnum.getMotivoBloqueioCnhEnum(this.motivoBloqueio);
        return motivo == null ? "" : motivo.toString();
    }
}
