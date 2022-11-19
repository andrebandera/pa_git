package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.anotacao.negocio.BusinessLogicalExclusion;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.ComissaoAnaliseEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Lillydi
 */
@Entity
@Table(name = "TB_TCM_TIPO_CORPO_ANDAMENTO")
@BusinessLogicalExclusion
@NamedQueries({
    @NamedQuery(
            name = "PATipoCorpoAndamento.getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracaoEComissaoAnaliseETipoCorpoTexto",
            query = "SELECT tcm "
            + "FROM PATipoCorpoAndamento tcm "
            + "INNER JOIN tcm.origemProcesso tdh "
            + "WHERE tcm.tipoNotificacaoProcesso = :p0 "
            + "AND tdh.id = :p1 and tcm.ativo = :p2 and tcm.comissaoAnalise = :p3 and tcm.tipoCorpoTexto = :p4 "),
    @NamedQuery(
            name = "PATipoCorpoAndamento.getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracao",
            query = "SELECT tcm "
            + "FROM PATipoCorpoAndamento tcm "
            + "INNER JOIN tcm.origemProcesso tdh "
            + "WHERE tcm.tipoNotificacaoProcesso = :p0 "
            + "AND tdh.id = :p1 and tcm.ativo = :p2 "),
    @NamedQuery(
            name = "PATipoCorpoAndamento.getPATipoCorpoAndamentoAtivoPorPAFluxoProcessoEdital",
            query = "SELECT tca FROM PATipoCorpoAndamento tca WHERE tca.fluxoProcessoEdital.id=:p0 AND tca.ativo = :p1"),

    @NamedQuery(
            name = "PATipoCorpoAndamento.getPATipoCorpoAndamentoAtivoPorPAFluxoProcessoRecurso",
            query = "SELECT tca FROM PATipoCorpoAndamento tca WHERE tca.fluxoProcessoRecurso.id=:p0 AND tca.ativo = :p1"),
    @NamedQuery(
            name = "PATipoCorpoAndamento.getPATipoCorpoAndamentoPorPAFluxoFase",
            query = "SELECT tca FROM PATipoCorpoAndamento tca WHERE tca.fluxoFase.id=:p0"),
    @NamedQuery(
            name = "PATipoCorpoAndamento.getPATipoCorpoAndamentoPorPAFluxoFaseVinculos",
            query = "SELECT g FROM PATipoCorpoAndamento g WHERE g.fluxoFase.id=:p0 AND g.ativo=:p1"),
    @NamedQuery(
            name = "PATipoCorpoAndamento.getPATipoCorpoAndamentoPorIDApoioOrigemInstauraucao",
            query = "SELECT tca FROM PATipoCorpoAndamento tca WHERE tca.origemProcesso.id = :p0 AND tca.ativo = :p1"),
    @NamedQuery(name = "PATipoCorpoAndamento.getPATipoCorpoAndamentoPorIDRecurso",
            query = "SELECT tcm "
            + " From PATipoCorpoAndamento tcm "
            + " where EXISTS("
            + "         SELECT 1 "
            + "             From Recurso tdp "
            + "                 INNER JOIN tdp.processoAdministrativo tdc "
            + "                 INNER JOIN tdc.origemApoio tdh "
            + "             where tcm.tipoNotificacaoProcesso = tdp.tipoRecurso "
            + "                 and tcm.origemProcesso.id = tdh.id "
            + "                 and tdp.id = :p0) "
            + "       and tcm.ativo = :p1")
})
@NamedNativeQueries({
    @NamedNativeQuery(name = "PATipoCorpoAndamento.getPATipoCorpoAndamentoAtivoPorPA",
            query = "SELECT tcm.* FROM dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde  "
            + "	INNER JOIN dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc on tde.Tde_Processo_Administrativo = tdc.Tdc_ID "
            + "	INNER JOIN dbo.TB_TCD_PAD_STATUS_ANDAMENTO tcd on tde.Tde_Status_Andamento = tcd.Tcd_ID and tcd.Ativo = :p1 "
            + "	inner JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb on tcd.Tcd_Andamento_Processo = tcb.Tcb_ID and tcb.Ativo = :p1 "
            + "	inner JOIN dbo.TB_TCK_PAD_FLUXO_PROCESSO tck on tde.Tde_Fluxo_Processo = tck.Tck_ID and tck.Ativo = :p1 "
            + "	inner JOIN dbo.TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO tci on tci.Tci_Fluxo_Processo = tck.Tck_ID and tci.Ativo = :p1 "
            + "	inner JOIN dbo.TB_TCH_PAD_FLUXO_FASE tch on tch.Tch_Andamento = tcb.Tcb_ID and tch.Tch_Prioridade_Fluxo_Amparo = tci.Tci_ID and tch.Ativo = :p1 "
            + "	inner JOIN dbo.TB_TCR_PAD_FLUXO_ANDAMENTO tcr on tcr.Tcr_Fluxo_Fase = tch.Tch_ID and tcr.Ativo = :p1 "
            + "	inner JOIN dbo.TB_TCK_PAD_FLUXO_PROCESSO tck_rec  on tcr.Tcr_Fluxo_Processo = tck_rec.Tck_ID and tck_rec.Ativo = :p1 "
            + "	inner JOIN dbo.TB_TCM_TIPO_CORPO_ANDAMENTO tcm on tcm.Tcm_Fluxo_Recurso = tck_rec.Tck_ID and tcm.Ativo = :p1 "
            + "	inner JOIN dbo.TB_TDH_PAD_APOIO_ORIGEM_INSTAURACAO tdh on tdh.Tdh_ID = tdc.Tdc_Apoio_Origem_Instauracao and tdh.Tdh_ID = tcm.Tcm_Origem_Processo "
            + "WHERE tdc.Tdc_ID = :p0",
            resultClass = PATipoCorpoAndamento.class)
})
public class PATipoCorpoAndamento extends BaseEntityAtivo implements Serializable {

    @Id
    @Column(name = "Tcm_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "Tcm_Fluxo_Fase", referencedColumnName = "Tch_ID")
    @ManyToOne
    private PAFluxoFase fluxoFase;

    @JoinColumn(name = "Tcm_Origem_Processo", referencedColumnName = "Tdh_ID")
    @ManyToOne
    private ApoioOrigemInstauracao origemProcesso;

    @Column(name = "Tcm_Tipo_Corpo_Texto")
    private Long tipoCorpoTexto;

    @Column(name = "Tcm_Tipo_Notificacao_Processo")
    @Enumerated(EnumType.STRING)
    private TipoFasePaEnum tipoNotificacaoProcesso;

    @ManyToOne
    @JoinColumn(name = "Tcm_Fluxo_Edital", referencedColumnName = "Tck_ID")
    private PAFluxoProcesso fluxoProcessoEdital;

    @ManyToOne
    @JoinColumn(name = "Tcm_Fluxo_Recurso", referencedColumnName = "Tck_ID")
    private PAFluxoProcesso fluxoProcessoRecurso;

    @ManyToOne
    @JoinColumn(name = "Tcm_Fluxo_Recurso_Cancelado", referencedColumnName = "Tck_ID")
    private PAFluxoProcesso fluxoProcessoRecursoCancelado;

    @Column(name = "Tcm_Prazo_Notificacao")
    private Integer prazoNotificacao;

    @Column(name = "Tcm_Comissao_Analise")
    @Enumerated(EnumType.STRING)
    private ComissaoAnaliseEnum comissaoAnalise;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    @Column(name = "Tcm_Prazo_Edital")
    private Integer prazoEdital;

    public PATipoCorpoAndamento() {
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

    public PAFluxoFase getFluxoFase() {
        return fluxoFase;
    }

    public void setFluxoFase(PAFluxoFase fluxoFase) {
        this.fluxoFase = fluxoFase;
    }

    public ApoioOrigemInstauracao getOrigemProcesso() {
        return origemProcesso;
    }

    public void setOrigemProcesso(ApoioOrigemInstauracao origemProcesso) {
        this.origemProcesso = origemProcesso;
    }

    public Long getTipoCorpoTexto() {
        return tipoCorpoTexto;
    }

    public void setTipoCorpoTexto(Long tipoCorpoTexto) {
        this.tipoCorpoTexto = tipoCorpoTexto;
    }

    public TipoFasePaEnum getTipoNotificacaoProcesso() {
        return tipoNotificacaoProcesso;
    }

    public void setTipoNotificacaoProcesso(TipoFasePaEnum tipoNotificacaoProcesso) {
        this.tipoNotificacaoProcesso = tipoNotificacaoProcesso;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public PAFluxoProcesso getFluxoProcessoEdital() {
        return fluxoProcessoEdital;
    }

    public void setFluxoProcessoEdital(PAFluxoProcesso fluxoProcessoEdital) {
        this.fluxoProcessoEdital = fluxoProcessoEdital;
    }

    public PAFluxoProcesso getFluxoProcessoRecurso() {
        return fluxoProcessoRecurso;
    }

    public void setFluxoProcessoRecurso(PAFluxoProcesso fluxoProcessoRecurso) {
        this.fluxoProcessoRecurso = fluxoProcessoRecurso;
    }

    public PAFluxoProcesso getFluxoProcessoRecursoCancelado() {
        return fluxoProcessoRecursoCancelado;
    }

    public void setFluxoProcessoRecursoCancelado(PAFluxoProcesso fluxoProcessoRecursoCancelado) {
        this.fluxoProcessoRecursoCancelado = fluxoProcessoRecursoCancelado;
    }

    public Integer getPrazoNotificacao() {
        return prazoNotificacao;
    }

    public void setPrazoNotificacao(Integer prazoNotificacao) {
        this.prazoNotificacao = prazoNotificacao;
    }

    public ComissaoAnaliseEnum getComissaoAnalise() {
        return comissaoAnalise;
    }

    public void setComissaoAnalise(ComissaoAnaliseEnum comissaoAnalise) {
        this.comissaoAnalise = comissaoAnalise;
    }

    public Integer getPrazoEdital() {
        return prazoEdital;
    }

    public void setPrazoEdital(Integer prazoEdital) {
        this.prazoEdital = prazoEdital;
    }

}
