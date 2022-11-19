package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntity;
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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
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
@Table(name = "TB_TDB_PAD_DADOS_INFRACAO")
@NamedQueries({
    @NamedQuery(
            name = "DadosInfracaoPAD.getAMesmaInfracaoFoiRealizadaEm1AnoOuMenos",
            query = "SELECT tdb "
                    + "FROM DadosInfracaoPAD tdb "
                    + "WHERE tdb.cpfCondutor.cpf = :p0 "
                    + " AND tdb.infracaoCodigo = :p1 "
                    + " AND tdb.isn <> :p4 "
                    + " AND tdb.dataInfracao BETWEEN :p2 AND :p3 order by tdb.dataInfracao "
    ),
    @NamedQuery(
            name = "DadosInfracaoPAD.getTodasInfracoesPorCpfCondutor",
            query = "SELECT tdb "
                    + "FROM DadosInfracaoPAD tdb "
                    + "WHERE tdb.cpfCondutor.cpf = :p0 ")
})
@NamedNativeQueries({
    @NamedNativeQuery(
        name = "DadosInfracaoPAD.verificaInfracaoEhEspecificadaEAutuadaCompetenciaDetranOuOrgaoDelegou",
        query = "select tdb.* " 
                + "from dbo.TB_TDB_PAD_DADOS_INFRACAO tdb " 
                + "	inner join dbo.TB_MAA_TABELA_INFRACAO maa on maa.Maa_Codigo = tdb.Tdb_Infracao " 
                + "	inner join dbo.TB_MBA_ACAO_INFRACAO_PENALIDADE mba on mba.Mba_Tabela_Infracao = maa.Maa_ID " 
                + "	inner join dbo.TB_MAZ_INFRACAO_PENALIDADE maz on maz.Maz_ID = mba.Mba_Infracao_Penalidade " 
                + "	inner join dbo.TB_QCE_INSTITUICAO_RESPONSABILIDADE qce on qce.Qce_Codigo_Base_Nacional = tdb.Tdb_Autuador " 
                + "	inner join dbo.TB_XDZ_CLASSIFICACAO_ORGAO_AUTUADOR xdz on qce.Qce_Abrangencia = xdz.Xdz_ID " 
                + "	inner join dbo.TB_QCC_TIPO_EMPRESA_RESPONSABILIDADE qcc on qcc.Qcc_ID = qce.Qce_Tipo_Empresa_Responsabilidade " 
                + "	inner join dbo.TB_XFE_TIPO_EMPRESA xfe on xfe.Xfe_ID = qcc.Qcc_Tipo_Empresa " 
                + "	inner join dbo.TB_XAB_MUNICIPIO xab on qce.Qce_Municipio = xab.Xab_ID " 
                + "	inner join dbo.TB_XAA_UF xaa on xaa.Xaa_ID = xab.Xab_UF " 
                + "where tdb.Tdb_Infracao = :p0 " 
                + "	and xfe.Xfe_Codigo = :p1 " 
                + "	and maz.Maz_Classificao_Penal = :p2 " 
                + "	and (mba.Mba_Data_Validade_Inicial is null " 
                + "                OR mba.Mba_Data_Validade_Final is null " 
                + "                OR tdb.Tdb_Data_Infracao BETWEEN mba.Mba_Data_Validade_Inicial and mba.Mba_Data_Validade_Final) " 
                + "	and ( " 
                + "		(xdz.Xdz_Competencia in (:p3) and (xaa.Xaa_UF = :p4 or (xaa.Xaa_UF not in (:p4) and convert(date, tdb.Tdb_Data_Infracao) < convert(date, :p5)))) " 
                + "			or (xdz.Xdz_Competencia in (:p6) and convert(date, tdb.Tdb_Data_Infracao) < convert(date, :p5)) " 
                + "	) " 
                + "	and maa.Ativo = :p7 " 
                + "	and mba.Ativo = :p7 " 
                + "	and tdb.Tdb_Cpf_Condutor = :p8 ",
        resultClass = DadosInfracaoPAD.class),
    @NamedNativeQuery(
            name = "DadosInfracaoPAD.getInfracoesPorCpfEAmparoEParaCenario3",
            query = "SELECT "
                    + "tdb.* "
                    + "FROM TB_TDB_PAD_DADOS_INFRACAO tdb "
                    + "WHERE tdb.Tdb_Cpf_Condutor = :p0 "
                    + "AND EXISTS (SELECT 1"
                    + "             FROM dbo.TB_MAZ_INFRACAO_PENALIDADE maz "
                    + "             INNER JOIN dbo.TB_MBA_ACAO_INFRACAO_PENALIDADE mba on mba.Mba_Infracao_Penalidade = maz.Maz_ID "
                    + "             INNER JOIN TB_MAA_TABELA_INFRACAO maa on maa.Maa_ID = mba.Mba_Tabela_Infracao "
                    + "             INNER JOIN TB_MBT_AMPARO_LEGAL mbt on maz.Maz_Amparo_Legal = mbt.Mbt_ID "
                    + "             WHERE mba.Ativo = :p4 "
                    + "             AND maa.Ativo = :p4 "
                    + "                 AND ((mba.Mba_Data_Validade_Inicial is null) "
                    + "                     OR (mba.Mba_Data_Validade_Final is null) "
                    + "                     OR (tdb.Tdb_Data_Infracao BETWEEN mba.Mba_Data_Validade_Inicial and mba.Mba_Data_Validade_Final)) "
                    + "             AND maa.Maa_Codigo = tdb.Tdb_Infracao "
                    + "             AND ((mba.Mba_Indice_Historico_Infracao IS NULL AND :p2 IS NULL) OR (mba.Mba_Indice_Historico_Infracao = :p2)) "
                    + "             AND mbt.Mbt_ID = :p1"
                    + "             AND mba.Mba_Codigo_Acao_Instauracao = :p3) "
                    + "ORDER BY tdb.Tdb_Data_Infracao ",
            resultClass = DadosInfracaoPAD.class),
    @NamedNativeQuery(
            name = "DadosInfracaoPAD.getInfracoesPorPontuacao",
            query = "SELECT "
                    + "tdb.* "
                    + "FROM TB_TDB_PAD_DADOS_INFRACAO tdb "
                    + "WHERE  "
                    + "	tdb.Tdb_Cpf_Condutor = :p0 and  "
                    + "	EXISTS( "
                    + "		SELECT 1  "
                    + "		FROM TB_MAZ_INFRACAO_PENALIDADE maz  "
                    + "			INNER JOIN TB_MBA_ACAO_INFRACAO_PENALIDADE mba on mba.Mba_Infracao_Penalidade = maz.Maz_ID "
                    + "			INNER join TB_MBB_MOTIVO_PENALIDADE mbb ON mbb.Mbb_Infracao_Penalidade = maz.Maz_ID "
                    + "			INNER JOIN TB_MAA_TABELA_INFRACAO maa on maa.Maa_ID = mba.Mba_Tabela_Infracao "
                    + "			INNER JOIN TB_MBT_AMPARO_LEGAL mbt on maz.Maz_Amparo_Legal = mbt.Mbt_ID "
                    + "		WHERE maz.Maz_Classificao_Penal = :p3  "
                    + "			AND mba.Ativo = :p4  "
                    + "			AND maa.Ativo = :p4  "
                    + "                 AND ((mba.Mba_Data_Validade_Inicial is null) "
                    + "                     OR (mba.Mba_Data_Validade_Final is null) "
                    + "                     OR (tdb.Tdb_Data_Infracao BETWEEN mba.Mba_Data_Validade_Inicial and mba.Mba_Data_Validade_Final)) "
                    + "			AND maa.Maa_Codigo = tdb.Tdb_Infracao "
                    + "			AND mbt.Mbt_ID = :p1 "
                    + "			AND mbb.Mbb_Codigo_Motivo_Penalidade = :p2 "
                    + "	) "
                    + "ORDER BY tdb.Tdb_Data_Infracao",
            resultClass = DadosInfracaoPAD.class),
    @NamedNativeQuery(
            name = "DadosInfracaoPAD.getInfracoesPorCpfEAmparoLegal",
            query = "SELECT "
                    + "tdb.* "
                    + "FROM TB_TDB_PAD_DADOS_INFRACAO tdb "
                    + "WHERE  "
                    + "	tdb.Tdb_Cpf_Condutor = :p0 and  "
                    + "	EXISTS( "
                    + "		SELECT 1  "
                    + "		FROM TB_MAZ_INFRACAO_PENALIDADE maz  "
                    + "			INNER JOIN TB_MBA_ACAO_INFRACAO_PENALIDADE mba on mba.Mba_Infracao_Penalidade = maz.Maz_ID "
                    + "			INNER JOIN TB_MAA_TABELA_INFRACAO maa on maa.Maa_ID = mba.Mba_Tabela_Infracao "
                    + "			INNER JOIN TB_MBT_AMPARO_LEGAL mbt on maz.Maz_Amparo_Legal = mbt.Mbt_ID "
                    + "		WHERE  mba.Ativo = :p2  "
                    + "			AND maa.Ativo = :p2  "
                    + "                 AND ((mba.Mba_Data_Validade_Inicial is null) "
                    + "                     OR (mba.Mba_Data_Validade_Final is null) "
                    + "                     OR (tdb.Tdb_Data_Infracao BETWEEN mba.Mba_Data_Validade_Inicial and mba.Mba_Data_Validade_Final)) "
                    + "			AND maa.Maa_Codigo = tdb.Tdb_Infracao "
                    + "			AND mbt.Mbt_ID = :p1 "
                    + "	) "
                    + "ORDER BY tdb.Tdb_Data_Infracao",
            resultClass = DadosInfracaoPAD.class),
    @NamedNativeQuery(
            name = "DadosInfracaoPAD.getInfracoesParaEspecificada_R3_3",
            query = "SELECT "
                    + "tdb.* "
                    + "FROM TB_TDB_PAD_DADOS_INFRACAO tdb "
                    + "WHERE  "
                    + "	tdb.Tdb_Cpf_Condutor = :p0 and  "
                    + "	EXISTS( "
                    + "		SELECT 1  "
                    + "		FROM TB_MAZ_INFRACAO_PENALIDADE maz  "
                    + "			INNER JOIN TB_MBA_ACAO_INFRACAO_PENALIDADE mba on mba.Mba_Infracao_Penalidade = maz.Maz_ID "
                    + "			INNER JOIN TB_MAA_TABELA_INFRACAO maa on maa.Maa_ID = mba.Mba_Tabela_Infracao "
                    + "			INNER JOIN TB_MBT_AMPARO_LEGAL mbt on maz.Maz_Amparo_Legal = mbt.Mbt_ID "
                    + "		WHERE  mba.Ativo = :p2  "
                    + "			AND maa.Ativo = :p2  "
                    + "			AND maa.Maa_Codigo = tdb.Tdb_Infracao "
                    + "                 AND mba.Mba_Indice_Historico_Infracao = :p3"
                    + "                 AND ((mba.Mba_Data_Validade_Inicial is null) "
                    + "                     OR (mba.Mba_Data_Validade_Final is null) "
                    + "                     OR (tdb.Tdb_Data_Infracao BETWEEN mba.Mba_Data_Validade_Inicial and mba.Mba_Data_Validade_Final)) "
                    + "			AND mbt.Mbt_ID = :p1 "
                    + "	) "
                    + "ORDER BY tdb.Tdb_Data_Infracao",
            resultClass = DadosInfracaoPAD.class),
    @NamedNativeQuery(
            name = "DadosInfracaoPAD.getInfracoesPorCpfEAmparoEIndiceReincidenciaMAZ",
            query = "SELECT   "
                    + "   tdb.*   "
                    + "   FROM TB_TDB_PAD_DADOS_INFRACAO tdb   "
                    + "   WHERE    "
                    + "   	tdb.Tdb_Cpf_Condutor = :p0 and    "
                    + "   	EXISTS(   "
                    + "   		SELECT 1    "
                    + "   		FROM dbo.TB_MAZ_INFRACAO_PENALIDADE maz    "
                    + "   			INNER JOIN dbo.TB_MBA_ACAO_INFRACAO_PENALIDADE mba on mba.Mba_Infracao_Penalidade = maz.Maz_ID   "
                    + "   			INNER JOIN TB_MAA_TABELA_INFRACAO maa on maa.Maa_ID = mba.Mba_Tabela_Infracao   "
                    + "   			INNER JOIN TB_MBT_AMPARO_LEGAL mbt on maz.Maz_Amparo_Legal = mbt.Mbt_ID   "
                    + "   		WHERE  mba.Ativo = :p3   "
                    + "   			AND maa.Ativo = :p3    "
                    + "                 AND ((mba.Mba_Data_Validade_Inicial is null) "
                    + "                     OR (mba.Mba_Data_Validade_Final is null) "
                    + "                     OR (tdb.Tdb_Data_Infracao BETWEEN mba.Mba_Data_Validade_Inicial and mba.Mba_Data_Validade_Final)) "
                    + "   			AND maa.Maa_Codigo = tdb.Tdb_Infracao   "
                    + "                    AND maz.Maz_Indice_Reincidencia = :p2  "
                    + "   			AND mbt.Mbt_ID = :p1) "
                    + "ORDER BY tdb.Tdb_Data_Infracao",
            resultClass = DadosInfracaoPAD.class)
})
public class DadosInfracaoPAD extends BaseEntity implements Serializable {

    @Id
    @Column(name = "Tdb_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "Tdb_Cpf_Condutor", referencedColumnName = "Tda_Numero_CPF")
    @ManyToOne
    private DadosCondutorPAD cpfCondutor;

    @Column(name = "Tdb_Isn")
    private Long isn;

    @Column(name = "Tdb_Auto")
    private String auto;

    @Column(name = "Tdb_Infracao")
    private String infracaoCodigo;

    @Column(name = "Tdb_Data_Infracao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInfracao;

    @Column(name = "Tdb_Autuador")
    private Integer autuador;

    @Column(name = "Tdb_Origem_Infracao")
    private Integer origemInfracao;

    @Column(name = "Tdb_Qte_Pontos_Infracao")
    private Integer qdePontosInfracao;

    @Column(name = "Tdb_Placa")
    private String placa;

    @Column(name = "Tdb_Origem_Informacao_Pontuacao")
    private Integer origemInformacaoPontuacao;

    @Column(name = "Tdb_Situacao_Pontuacao")
    private Integer situacaoPontuacao;

    @Column(name = "Tdb_Status_Pontuacao")
    private String statusPontuacao;

    public DadosInfracaoPAD() {
    }

    public DadosInfracaoPAD(Long isn,
                            String auto,
                            String infracaoCodigo,
                            Date dataInfracao,
                            Integer autuador,
                            Integer origemInfracao,
                            Integer qdePontosInfracao,
                            String placa,
                            Integer origemInformacaoPontuacao,
                            Integer situacaoPontuacao,
                            String statusPontuacao) {
        this.isn = isn;
        this.auto = auto;
        this.infracaoCodigo = infracaoCodigo;
        this.dataInfracao = dataInfracao;
        this.autuador = autuador;
        this.origemInfracao = origemInfracao;
        this.qdePontosInfracao = qdePontosInfracao;
        this.placa = placa;
        this.origemInformacaoPontuacao = origemInformacaoPontuacao;
        this.situacaoPontuacao = situacaoPontuacao;
        this.statusPontuacao = statusPontuacao;
    }

    public DadosInfracaoPAD(Integer qdePontosInfracao) {
        this.qdePontosInfracao = qdePontosInfracao;
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

    public DadosCondutorPAD getCpfCondutor() {
        return cpfCondutor;
    }

    public void setCpfCondutor(DadosCondutorPAD cpfCondutor) {
        this.cpfCondutor = cpfCondutor;
    }

    public Long getIsn() {
        return isn;
    }

    public void setIsn(Long isn) {
        this.isn = isn;
    }

    public String getAuto() {
        return auto;
    }

    public void setAuto(String auto) {
        this.auto = auto;
    }

    public String getInfracaoCodigo() {
        return infracaoCodigo;
    }

    public void setInfracaoCodigo(String infracaoCodigo) {
        this.infracaoCodigo = infracaoCodigo;
    }

    public Date getDataInfracao() {
        return dataInfracao;
    }

    public void setDataInfracao(Date dataInfracao) {
        this.dataInfracao = dataInfracao;
    }

    public Integer getAutuador() {
        return autuador;
    }

    public void setAutuador(Integer autuador) {
        this.autuador = autuador;
    }

    public Integer getOrigemInfracao() {
        return origemInfracao;
    }

    public void setOrigemInfracao(Integer origemInfracao) {
        this.origemInfracao = origemInfracao;
    }

    public Integer getQdePontosInfracao() {
        return qdePontosInfracao;
    }

    public void setQdePontosInfracao(Integer qdePontosInfracao) {
        this.qdePontosInfracao = qdePontosInfracao;
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

    public Integer getSituacaoPontuacao() {
        return situacaoPontuacao;
    }

    public void setSituacaoPontuacao(Integer situacaoPontuacao) {
        this.situacaoPontuacao = situacaoPontuacao;
    }

    public String getStatusPontuacao() {
        return statusPontuacao;
    }

    public void setStatusPontuacao(String statusPontuacao) {
        this.statusPontuacao = statusPontuacao;
    }
}
