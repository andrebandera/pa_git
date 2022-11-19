package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.core.projeto.entidade.inf.AmparoLegal;
import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.DataHoraAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TDD_PAD_INFRACAO_PROCESSO_ADMINISTRATIVO")
@NamedQueries({
    @NamedQuery(
            name = "ProcessoAdministrativoInfracao.getInfracaoPorNumeroAutoECodigoInfracao",
            query = "SELECT tdd "
                    + "FROM ProcessoAdministrativoInfracao tdd "
                    + "WHERE tdd.autoInfracao = :p0 "
                    + "AND tdd.codigoInfracao like :p1"),
    @NamedQuery(
            name = "ProcessoAdministrativoInfracao.getInfracaoPorNumeroAutoECodigoInfracaoOrderByAtivo",
            query = "SELECT tdd "
                    + "FROM ProcessoAdministrativoInfracao tdd "
                    + "WHERE tdd.autoInfracao = :p0 "
                    + "AND tdd.codigoInfracao like :p1 ORDER BY tdd.ativo DESC"),
    @NamedQuery(
            name = "ProcessoAdministrativoInfracao.getExtratoEAutuadorECodigoInfracao",
            query = "SELECT new ProcessoAdministrativoInfracao(pai.autoInfracao, pai.orgaoAutuador, pai.codigoInfracao, pai.origemInfracao) "
            + "FROM ProcessoAdministrativoInfracao pai "
            + "WHERE pai.processoAdministrativo.id = :p0 "),
    @NamedQuery(
            name = "ProcessoAdministrativoInfracao.getInfracoesPorProcessoAdministrativoID",
            query = "SELECT pai "
            + "FROM ProcessoAdministrativoInfracao pai "
            + "WHERE pai.processoAdministrativo.id = :p0 "),
    @NamedQuery(
            name = "ProcessoAdministrativoInfracao.getInfracoesPorProcessoParaAEMNPP89",
            query = "SELECT tdd "
            + "FROM ProcessoAdministrativoInfracao tdd "
            + "WHERE tdd.processoAdministrativo.id = :p0 "
            + "AND tdd.situacao IN (:p1) "
    ),
    @NamedQuery(
            name = "ProcessoAdministrativoInfracao.getInfracoesPorProcessoAdministrativo",
            query = "SELECT new ProcessoAdministrativoInfracao(pai.id, pai.infracao) "
            + "FROM ProcessoAdministrativoInfracao pai "
            + "WHERE pai.processoAdministrativo.id = :p0 "),
    @NamedQuery(
            name = "ProcessoAdministrativoInfracao.getInfracaoPorInfracaoNumeroAutoOrgaoAutuador",
            query = "SELECT new ProcessoAdministrativoInfracao(pai.id) "
            + "FROM ProcessoAdministrativoInfracao pai  INNER JOIN pai.processoAdministrativo pa "
            + "WHERE pai.codigoInfracao = :p0 "
            + "AND pai.autoInfracao = :p1 "
            + "AND pai.orgaoAutuador = :p2 and pai.ativo = :p3 and pa.ativo = :p3 "),
    @NamedQuery(
            name = "ProcessoAdministrativoInfracao.findAll",
            query = "SELECT pai FROM ProcessoAdministrativoInfracao pai"),
    @NamedQuery(
            name = "ProcessoAdministrativoInfracao.getSomaPontuacaoDeInfracoesPA",
            query = "SELECT SUM(pai.quantidadePontosInfracao) FROM ProcessoAdministrativoInfracao pai INNER JOIN pai.processoAdministrativo pa where pa.id = :p0 and pai.ativo = :p1")
})
@NamedNativeQueries({
    @NamedNativeQuery(
            name = "ProcessoAdministrativoInfracao.getInfracaoPAReincidenteMesmoArtigo",
            query = "SELECT Distinct tdd.*   "
            + "	FROM dbo.TB_TDD_PAD_INFRACAO_PROCESSO_ADMINISTRATIVO tdd  "
            + "		INNER join dbo.TB_MBA_ACAO_INFRACAO_PENALIDADE mba on tdd.Tdd_Acao_Infracao_Penalidade = mba.Mba_ID  "
            + "		INNER join dbo.TB_MAA_TABELA_INFRACAO maa on mba.Mba_Tabela_Infracao = maa.Maa_ID "
            + "		INNER JOIN dbo.TB_MBT_AMPARO_LEGAL mbt on mbt.Mbt_ID = maa.Maa_Amparo_Legal "
            + "		INNER join dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc on tdd.Tdd_Processo_Administrativo = tdc.Tdc_ID  "
            + "	where tdc.Tdc_ID in (:p1) and tdc.Ativo = :p2 and tdd.Ativo = :p2 and maa.Ativo = :p2  "
            + "		AND EXISTS ( "
            + "				SELECT 1 FROM dbo.TB_MAA_TABELA_INFRACAO maa2 "
            + "						INNER join dbo.TB_MBT_AMPARO_LEGAL mbt2 on mbt2.Mbt_ID = maa2.Maa_Amparo_Legal "
            + "					where maa2.Maa_Codigo = :p0 and mbt.Mbt_Artigo = mbt2.Mbt_Artigo and maa2.Ativo = :p2)",
            resultClass = ProcessoAdministrativoInfracao.class),
    @NamedNativeQuery(
            name = "ProcessoAdministrativoInfracao.getInfracaoPAReincidenteMesmaInfracao",
            query = "SELECT Distinct tdd.*   "
            + "	FROM dbo.TB_TDD_PAD_INFRACAO_PROCESSO_ADMINISTRATIVO tdd  "
            + "		INNER join dbo.TB_MBA_ACAO_INFRACAO_PENALIDADE mba on tdd.Tdd_Acao_Infracao_Penalidade = mba.Mba_ID  "
            + "		INNER join dbo.TB_MAA_TABELA_INFRACAO maa on mba.Mba_Tabela_Infracao = maa.Maa_ID "
            + "		INNER JOIN dbo.TB_MBT_AMPARO_LEGAL mbt on mbt.Mbt_ID = maa.Maa_Amparo_Legal "
            + "		INNER join dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc on tdd.Tdd_Processo_Administrativo = tdc.Tdc_ID  "
            + "	where tdc.Tdc_ID in (:p1) and tdc.Ativo = :p2 and tdd.Ativo = :p2 and maa.Ativo = :p2 and maa.Maa_Codigo = :p0 ",
            resultClass = ProcessoAdministrativoInfracao.class),
    @NamedNativeQuery(
            name = "ProcessoAdministrativoInfracao.getAmparoLegalPorInfracaoId",
            query = "SELECT mbt.* "
            + "FROM dbo.TB_TDD_PAD_INFRACAO_PROCESSO_ADMINISTRATIVO tdd "
            + "INNER JOIN dbo.TB_MBA_ACAO_INFRACAO_PENALIDADE mba ON mba.Mba_ID = tdd.Tdd_Acao_Infracao_Penalidade "
            + "INNER JOIN dbo.TB_MAA_TABELA_INFRACAO maa ON maa.Maa_ID = mba.Mba_Tabela_Infracao "
            + "INNER JOIN dbo.TB_MBT_AMPARO_LEGAL mbt ON mbt.Mbt_ID = maa.Maa_Amparo_Legal "
            + "WHERE tdd.Tdd_ID = :p0 AND tdd.Ativo = 1 ")
})
public class ProcessoAdministrativoInfracao extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdd_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Tdd_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;

    @Column(name = "Tdd_Situacao")
    @Enumerated(EnumType.STRING)
    private ProcessoAdministrativoInfracaoSituacaoEnum situacao;

    @Column(name = "Tdd_Isn")
    private Long isn;

    @Column(name = "Tdd_Auto_Infracao")
    private String autoInfracao;

    @Column(name = "Tdd_Codigo_Infracao")
    private String codigoInfracao;

    @Column(name = "Tdd_Data_Infracao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInfracao;

    @Column(name = "Tdd_Orgao_Autuador")
    private Long orgaoAutuador;

    @Column(name = "Tdd_Origem_Infracao")
    private Long origemInfracao;

    @Column(name = "Tdd_Qte_Pontos_Infracao")
    private Integer quantidadePontosInfracao;

    @Column(name = "Tdd_Placa")
    private String placa;

    @Column(name = "Tdd_Origem_Informacao_Pontuacao")
    private Integer origemInformacaoPontuacao;

    @Column(name = "Tdd_Status_Pontuacao")
    private String statusPontuacao;

    @Column(name = "Tdd_Acao_Infracao_Penalidade")
    private Long infracao;

    @Column(name = "Tdd_Local")
    private String local;

    @Column(name = "Tdd_Municipio")
    private Long municipio;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public ProcessoAdministrativoInfracao() {
    }

    public ProcessoAdministrativoInfracao(Long id) {
        this.id = id;
    }

    public ProcessoAdministrativoInfracao(Long id, Long infracao) {
        this(id);
        this.infracao = infracao;
    }

    public ProcessoAdministrativoInfracao(String autoInfracao, Long orgaoAutuador) {
        this.autoInfracao = autoInfracao;
        this.orgaoAutuador = orgaoAutuador;
    }

    public ProcessoAdministrativoInfracao(String autoInfracao, Long orgaoAutuador, String codigoInfracao) {
        this(autoInfracao, orgaoAutuador);
        this.codigoInfracao = codigoInfracao;
    }
    public ProcessoAdministrativoInfracao(String autoInfracao, Long orgaoAutuador, String codigoInfracao, Long origemInfracao) {
        this(autoInfracao, orgaoAutuador, codigoInfracao);
        this.origemInfracao = origemInfracao;
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

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public Long getIsn() {
        return isn;
    }

    public void setIsn(Long isn) {
        this.isn = isn;
    }

    public String getAutoInfracao() {
        return autoInfracao;
    }

    public void setAutoInfracao(String autoInfracao) {
        this.autoInfracao = autoInfracao;
    }

    public Date getDataInfracao() {
        return dataInfracao;
    }

    @XmlElement(name = "dataInfracao")
    @XmlJavaTypeAdapter(DataHoraAdapter.class)
    public void setDataInfracao(Date dataInfracao) {
        this.dataInfracao = dataInfracao;
    }

    public Long getOrgaoAutuador() {
        return orgaoAutuador;
    }

    public void setOrgaoAutuador(Long orgaoAutuador) {
        this.orgaoAutuador = orgaoAutuador;
    }

    public Long getOrigemInfracao() {
        return origemInfracao;
    }

    public void setOrigemInfracao(Long origemInfracao) {
        this.origemInfracao = origemInfracao;
    }

    public Integer getQuantidadePontosInfracao() {
        return quantidadePontosInfracao;
    }

    public void setQuantidadePontosInfracao(Integer quantidadePontosInfracao) {
        this.quantidadePontosInfracao = quantidadePontosInfracao;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Integer getOrigemInformacaoPontuacao() {
        return origemInformacaoPontuacao;
    }

    public void setOrigemInformacaoPontuacao(Integer origemInformacaoPontuacao) {
        this.origemInformacaoPontuacao = origemInformacaoPontuacao;
    }

    public String getStatusPontuacao() {
        return statusPontuacao;
    }

    public void setStatusPontuacao(String statusPontuacao) {
        this.statusPontuacao = statusPontuacao;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public Long getInfracao() {
        return infracao;
    }

    public void setInfracao(Long infracao) {
        this.infracao = infracao;
    }

    public ProcessoAdministrativoInfracaoSituacaoEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(ProcessoAdministrativoInfracaoSituacaoEnum situacao) {
        this.situacao = situacao;
    }

    public String getCodigoInfracao() {
        return codigoInfracao;
    }

    public void setCodigoInfracao(String codigoInfracao) {
        this.codigoInfracao = codigoInfracao;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Long getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Long municipio) {
        this.municipio = municipio;
    }
}