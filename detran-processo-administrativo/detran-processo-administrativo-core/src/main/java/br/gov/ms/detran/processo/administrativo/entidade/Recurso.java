package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.ResultLong;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TDP_PAD_RECURSO")
@NamedQueries({
        @NamedQuery(
                name = "Recurso.getRecursosDoProcessoAdministrativoNaMesmaFase",
                query = "SELECT tdp "
                        + "FROM Recurso tdp "
                        + "WHERE tdp.processoAdministrativo.id = :p0 "
                        + " AND exists(SELECT 1 "
                        + "             FROM NotificacaoProcessoAdministrativo tcx "
                        + "             WHERE tcx.processoAdministrativo.id = tdp.processoAdministrativo.id "
                        + "                 AND tcx.fluxoFase.prioridadeFluxoAmparo.faseProcessoAdm.codigo like :p1 "
                        + "                 AND tcx.tipoNotificacaoProcesso = tdp.tipoRecurso) "
                        + " AND tdp.tipoRecurso = :p2 "
                        + " AND tdp.ativo = :p3 "
        ),
        @NamedQuery(
                name = "Recurso.naoExisteRecursoPenalizacaoComDestinoJARI",
                query = "SELECT new Recurso(tdp.id) "
                        + "FROM Recurso tdp "
                        + "WHERE tdp.processoAdministrativo.id = :p0 "
                        + " AND tdp.tipoRecurso = :p1 "
                        + " AND tdp.destinoFase.origemDestino = :p2 "
                        + " AND tdp.ativo = :p3 "
        ),
        @NamedQuery(
                name = "Recurso.getRecursoPorProcessoAdministrativoESituacao",
                query = "SELECT tdp "
                        + "FROM Recurso tdp "
                        + "WHERE tdp.processoAdministrativo.id = :p0 "
                        + " AND tdp.ativo = :p1 "
                        + " AND tdp.situacao = :p2 "
        ),
        @NamedQuery(
                name = "Recurso.getRecursoMaisRecentePorProcessoEFase",
                query = "SELECT tdp FROM ResultadoRecurso tdt inner join tdt.recurso tdp "
                        + "WHERE tdp.processoAdministrativo.id = :p0 "
                        + " AND tdp.ativo = :p1 "
                        + " AND tdt.ativo = :p1 "
                        + " AND tdp.tipoRecurso = :p2 ORDER BY tdp.dataRecurso DESC"
        ),
        @NamedQuery(
                name = "Recurso.getRecursoPorProcessoAdministrativoTipoDestinoENaoCancelado",
                query = "SELECT tdp "
                        + "FROM Recurso tdp "
                        + "WHERE tdp.processoAdministrativo.id = :p0 "
                        + " AND tdp.ativo = :p1 "
                        + " AND tdp.situacao <> :p2 "
                        + " AND tdp.tipoRecurso = :p3 "
                        + " AND (:p4 IS NULL OR tdp.destinoFase.origemDestino = :p4) "
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "Recurso.getRecursoPorFiltros",
                query = "SELECT DISTINCT  "
                        + "tdp.Tdp_Id AS Tdp_Id, "
                        + "tdp.Tdp_Processo_Administrativo AS Tdp_Processo_Administrativo, "
                        + "tdp.Tdp_Destino_Fase AS Tdp_Destino_Fase, "
                        + "tdp.Tdp_Motivo_Alegacao AS Tdp_Motivo_Alegacao, "
                        + "tdp.Tdp_Tipo AS Tdp_Tipo, "
                        + "tdp.Tdp_Situacao AS Tdp_Situacao, "
                        + "tdp.Ativo AS Ativo, "
                        + "tdp.versao_registro AS versao_registro, "
                        + "tdp.Usuario_Inclusao AS Usuario_Inclusao, "
                        + "tdp.Data_Inclusao AS Data_Inclusao, "
                        + "tdp.Usuario_Alteracao AS Usuario_Alteracao, "
                        + "tdp.Data_Alteracao AS Data_Alteracao, "
                        + "tdp.Tdp_Data_Recurso AS Tdp_Data_Recurso "
                        + "FROM dbo.TB_TDP_PAD_RECURSO tdp "
                        + "INNER JOIN dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tdc.Tdc_ID = tdp.Tdp_Processo_Administrativo "
                        + "INNER JOIN dbo.TB_XDF_ATENDIMENTO xdf ON xdf.Xdf_ID = tdc.Tdc_Atendimento "
                        + "INNER JOIN dbo.TB_XAC_PESSOA xac ON xac.Xac_ID = xdf.Xdf_Numero_Detran "
                        + "INNER JOIN dbo.TB_XAN_IDENTIFICACAO_DOCUMENTO xan ON xan.Xan_Numero_Detran = xac.Xac_ID "
                        + "INNER JOIN dbo.TB_XBT_SUBTIPO_DOCUMENTO xbt ON xbt.Xbt_ID = xan.Xan_SubTipo_Documento AND xbt.Xbt_Codigo = 0 "
                        + " left join TB_TDT_PAD_RESULTADO_RECURSO tdt on tdt.Tdt_Recurso = tdp.Tdp_ID" +
                        " left join dbo.TB_XEA_CA_USUARIO xea1 on tdt.Tdt_Usuario_Resultado = xea1.Xea_ID " +
                        " LEFT join dbo.TB_XAC_PESSOA xac1 on xea1.Xea_Numero_Detran = xac1.Xac_ID" +
                        " LEFT join dbo.TB_XBQ_NOME xbq on xbq.Xbq_Numero_Detran = xac1.Xac_ID" +
                        " LEFT join dbo.TB_XFA_NOME_ABREVIADO xfa on xfa.Xfa_Nome_Principal = xbq.Xbq_ID" +
                        " LEFT join dbo.TB_XBX_GRUPO_SERVICO xbx on xfa.Xfa_Grupo_Servico = xbx.Xbx_ID "
                        + "WHERE (:p0 IS NULL OR xan.Xan_Numero_Documento = :p0) "
                        + "AND (:p1 IS NULL OR tdc.Tdc_Numero_Processo = :p1) "
                        + "AND (:p2 IS NULL OR tdp.Tdp_Situacao = :p2) "
                        + "AND (:p3 IS NULL OR tdp.Ativo = :p3) "
                        + "AND (:p4 IS NULL OR tdp.Tdp_Id = :p4) "
                        + "AND (:p5 IS NULL OR tdp.Tdp_Tipo = :p5) "
                        + " AND (:p6 is null or tdt.Tdt_Resultado = :p6 AND tdt.Ativo = 1) " +
                        "   and (:p7 is null or xbq.Xbq_Nome like :p7 and xbq.Ativo = 1 and xfa.Ativo = 1 and xbx.Xbx_Codigo_Grupo = 4 AND tdt.Ativo = 1) " +
                        "   and (:p8 is null or :p8 <= tdt.Tdt_Data AND tdt.Ativo = 1) " +
                        "   and (:p9 is null or :p9 >= tdt.Tdt_Data AND tdt.Ativo = 1) ",
                resultClass = Recurso.class),
        @NamedNativeQuery(
                name = "Recurso.getCountRecursoPorFiltros",
                query = "SELECT "
                        + "COUNT(DISTINCT TDP.Tdp_ID) AS RESULTADO "
                        + "FROM dbo.TB_TDP_PAD_RECURSO tdp "
                        + "INNER JOIN dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tdc.Tdc_ID = tdp.Tdp_Processo_Administrativo "
                        + "INNER JOIN dbo.TB_XDF_ATENDIMENTO xdf ON xdf.Xdf_ID = tdc.Tdc_Atendimento "
                        + "INNER JOIN dbo.TB_XAC_PESSOA xac ON xac.Xac_ID = xdf.Xdf_Numero_Detran "
                        + "INNER JOIN dbo.TB_XAN_IDENTIFICACAO_DOCUMENTO xan ON xan.Xan_Numero_Detran = xac.Xac_ID "
                        + "INNER JOIN dbo.TB_XBT_SUBTIPO_DOCUMENTO xbt ON xbt.Xbt_ID = xan.Xan_SubTipo_Documento AND xbt.Xbt_Codigo = 0 " +
                        " left join TB_TDT_PAD_RESULTADO_RECURSO tdt on tdt.Tdt_Recurso = tdp.Tdp_ID" +
                        " left join dbo.TB_XEA_CA_USUARIO xea1 on tdt.Tdt_Usuario_Resultado = xea1.Xea_ID " +
                        " LEFT join dbo.TB_XAC_PESSOA xac1 on xea1.Xea_Numero_Detran = xac1.Xac_ID" +
                        " LEFT join dbo.TB_XBQ_NOME xbq on xbq.Xbq_Numero_Detran = xac1.Xac_ID" +
                        " LEFT join dbo.TB_XFA_NOME_ABREVIADO xfa on xfa.Xfa_Nome_Principal = xbq.Xbq_ID" +
                        " LEFT join dbo.TB_XBX_GRUPO_SERVICO xbx on xfa.Xfa_Grupo_Servico = xbx.Xbx_ID "
                        + "WHERE (:p0 IS NULL OR xan.Xan_Numero_Documento = :p0) "
                        + "AND (:p1 IS NULL OR tdc.Tdc_Numero_Processo = :p1) "
                        + "AND (:p2 IS NULL OR tdp.Tdp_Situacao = :p2) "
                        + "AND (:p3 IS NULL OR tdp.Ativo = :p3) "
                        + "AND (:p4 IS NULL OR tdp.Tdp_Id = :p4) "
                        + "AND (:p5 IS NULL OR tdp.Tdp_Tipo = :p5) "
                        + " AND (:p6 is null or tdt.Tdt_Resultado = :p6 AND tdt.Ativo = 1) " +
                        "   and (:p7 is null or xbq.Xbq_Nome like :p7 and xbq.Ativo = 1 and xfa.Ativo = 1 and xbx.Xbx_Codigo_Grupo = 4 AND tdt.Ativo = 1) " +
                        "   and (:p8 is null or :p8 <= tdt.Tdt_Data AND tdt.Ativo = 1) " +
                        "   and (:p9 is null or :p9 >= tdt.Tdt_Data AND tdt.Ativo = 1 ) ",
                resultClass = ResultLong.class),
        @NamedNativeQuery(
                name = "Recurso.getRecursoPorFiltros2",
                query = "SELECT DISTINCT tdp.* "
                        + "FROM dbo.TB_TDP_PAD_RECURSO tdp "
                        + "INNER JOIN dbo.TB_TCP_PAD_DESTINO_FASE tcp on tdp.TDP_DESTINO_FASE = tcp.TCP_ID "
                        + "INNER JOIN dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tdc.Tdc_ID = tdp.Tdp_Processo_Administrativo "
                        + "INNER JOIN dbo.TB_XDF_ATENDIMENTO xdf ON xdf.Xdf_ID = tdc.Tdc_Atendimento "
                        + "INNER JOIN dbo.TB_XAC_PESSOA xac ON xac.Xac_ID = xdf.Xdf_Numero_Detran "
                        + "INNER JOIN dbo.TB_XAN_IDENTIFICACAO_DOCUMENTO xan ON xan.Xan_Numero_Detran = xac.Xac_ID "
                        + "INNER JOIN dbo.TB_XBT_SUBTIPO_DOCUMENTO xbt ON xbt.Xbt_ID = xan.Xan_SubTipo_Documento AND xbt.Xbt_Codigo = 0 "
                        + " left join TB_TDT_PAD_RESULTADO_RECURSO tdt on tdt.Tdt_Recurso = tdp.Tdp_ID"
                        + " left join dbo.TB_XEA_CA_USUARIO xea1 on tdt.Tdt_Usuario_Resultado = xea1.Xea_ID "
                        + " LEFT join dbo.TB_XAC_PESSOA xac1 on xea1.Xea_Numero_Detran = xac1.Xac_ID"
                        + " LEFT join dbo.TB_XBQ_NOME xbq on xbq.Xbq_Numero_Detran = xac1.Xac_ID"
                        + " LEFT join dbo.TB_XFA_NOME_ABREVIADO xfa on xfa.Xfa_Nome_Principal = xbq.Xbq_ID"
                        + " LEFT join dbo.TB_XBX_GRUPO_SERVICO xbx on xfa.Xfa_Grupo_Servico = xbx.Xbx_ID "
                        + "WHERE (:p0 IS NULL OR xan.Xan_Numero_Documento = :p0) "
                        + "AND (:p1 IS NULL OR tdc.Tdc_Numero_Processo = :p1) "
                        + "AND (:p2 IS NULL OR tdp.Tdp_Situacao = :p2) "
                        + "AND (:p3 IS NULL OR tdp.Ativo = :p3) "
                        + "AND (:p4 IS NULL OR tdp.Tdp_Id = :p4) "
                        + "AND (:p5 IS NULL OR tdp.Tdp_Tipo = :p5) "
                        + " AND (:p6 is null or tdt.Tdt_Resultado = :p6 AND tdt.Ativo = 1) "
                        + "   and (:p7 is null or xbq.Xbq_Nome like :p7 and xbq.Ativo = 1 and xfa.Ativo = 1 and xbx.Xbx_Codigo_Grupo = 4 AND tdt.Ativo = 1) "
                        + "   and (:p8 is null or :p8 <= tdt.Tdt_Data AND tdt.Ativo = 1) "
                        + "   and (:p9 is null or :p9 >= tdt.Tdt_Data AND tdt.Ativo = 1) "
                        + "AND (:p10 IS NULL OR CONVERT(DATE, :p10) <= CONVERT(DATE, tdp.Tdp_Data_Recurso)) "
                        + "AND (:p11 IS NULL OR CONVERT(DATE, :p11) >= CONVERT(DATE, tdp.Tdp_Data_Recurso)) "
                        + "AND (:p12 IS NULL OR tcp.Tcp_Origem_Destino = :p12) "
                        + "ORDER BY tdp.Tdp_Id DESC ",
                resultClass = Recurso.class),
        @NamedNativeQuery(
                name = "Recurso.getCountRecursoPorFiltros2",
                query = "SELECT "
                        + "COUNT(DISTINCT TDP.Tdp_Id) AS RESULTADO "
                        + "FROM dbo.TB_TDP_PAD_RECURSO tdp "
                        + "INNER JOIN dbo.TB_TCP_PAD_DESTINO_FASE tcp on tdp.TDP_DESTINO_FASE = tcp.TCP_ID "
                        + "INNER JOIN dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tdc.Tdc_ID = tdp.Tdp_Processo_Administrativo "
                        + "INNER JOIN dbo.TB_XDF_ATENDIMENTO xdf ON xdf.Xdf_ID = tdc.Tdc_Atendimento "
                        + "INNER JOIN dbo.TB_XAC_PESSOA xac ON xac.Xac_ID = xdf.Xdf_Numero_Detran "
                        + "INNER JOIN dbo.TB_XAN_IDENTIFICACAO_DOCUMENTO xan ON xan.Xan_Numero_Detran = xac.Xac_ID "
                        + "INNER JOIN dbo.TB_XBT_SUBTIPO_DOCUMENTO xbt ON xbt.Xbt_ID = xan.Xan_SubTipo_Documento AND xbt.Xbt_Codigo = 0 "
                        + " left join TB_TDT_PAD_RESULTADO_RECURSO tdt on tdt.Tdt_Recurso = tdp.Tdp_ID"
                        + " left join dbo.TB_XEA_CA_USUARIO xea1 on tdt.Tdt_Usuario_Resultado = xea1.Xea_ID "
                        + " LEFT join dbo.TB_XAC_PESSOA xac1 on xea1.Xea_Numero_Detran = xac1.Xac_ID"
                        + " LEFT join dbo.TB_XBQ_NOME xbq on xbq.Xbq_Numero_Detran = xac1.Xac_ID"
                        + " LEFT join dbo.TB_XFA_NOME_ABREVIADO xfa on xfa.Xfa_Nome_Principal = xbq.Xbq_ID"
                        + " LEFT join dbo.TB_XBX_GRUPO_SERVICO xbx on xfa.Xfa_Grupo_Servico = xbx.Xbx_ID "
                        + "WHERE (:p0 IS NULL OR xan.Xan_Numero_Documento = :p0) "
                        + "AND (:p1 IS NULL OR tdc.Tdc_Numero_Processo = :p1) "
                        + "AND (:p2 IS NULL OR tdp.Tdp_Situacao = :p2) "
                        + "AND (:p3 IS NULL OR tdp.Ativo = :p3) "
                        + "AND (:p4 IS NULL OR tdp.Tdp_Id = :p4) "
                        + "AND (:p5 IS NULL OR tdp.Tdp_Tipo = :p5) "
                        + " AND (:p6 is null or tdt.Tdt_Resultado = :p6 AND tdt.Ativo = 1) "
                        + "   and (:p7 is null or xbq.Xbq_Nome like :p7 and xbq.Ativo = 1 and xfa.Ativo = 1 and xbx.Xbx_Codigo_Grupo = 4 AND tdt.Ativo = 1) "
                        + "   and (:p8 is null or :p8 <= tdt.Tdt_Data AND tdt.Ativo = 1) "
                        + "   and (:p9 is null or :p9 >= tdt.Tdt_Data AND tdt.Ativo = 1) "
                        + "AND (:p10 IS NULL OR CONVERT(DATE, :p10) <= CONVERT(DATE, tdp.Tdp_Data_Recurso)) "
                        + "AND (:p11 IS NULL OR CONVERT(DATE, :p11) >= CONVERT(DATE, tdp.Tdp_Data_Recurso)) "
                        + "AND (:p12 IS NULL OR tcp.Tcp_Origem_Destino = :p12) ",
                resultClass = ResultLong.class)
})
public class Recurso extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdp_Id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tdp_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;
    
    @ManyToOne
    @JoinColumn(name = "Tdp_Destino_Fase", referencedColumnName = "Tcp_Id")
    private DestinoFase destinoFase;
    
    @ManyToOne
    @JoinColumn(name = "Tdp_Motivo_Alegacao", referencedColumnName = "Tcq_Id")
    private MotivoAlegacao motivoAlegacao;
    
    @Column(name = "Tdp_Tipo")
    @Enumerated(EnumType.STRING)
    private TipoFasePaEnum tipoRecurso;
    
    @Column(name = "Tdp_Situacao")
    @Enumerated(EnumType.STRING)
    private SituacaoRecursoEnum situacao;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;
    
    @Column(name = "Tdp_Data_Recurso")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataRecurso;

    public Recurso() {
    }

    public Recurso(Long id) {
        this.id = id;
    }
    
    Recurso(TipoFasePaEnum tipoRecurso, SituacaoRecursoEnum situacao, String numeroProcesso) {
        
        this.tipoRecurso = tipoRecurso;
        this.situacao = situacao;
        
        this.processoAdministrativo = new ProcessoAdministrativo();
        this.processoAdministrativo.setNumeroProcesso(numeroProcesso);
    }

    public Recurso(Long id_TDP, TipoFasePaEnum tipoRecurso, SituacaoRecursoEnum situacao, Long versaoRegistro_TDP, Date dataRecurso) {
        
        this.id = id_TDP;
        this.tipoRecurso = tipoRecurso;
        this.situacao = situacao;
        this.dataRecurso = dataRecurso;
        this.setVersaoRegistro(versaoRegistro_TDP);
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

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public SituacaoRecursoEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoRecursoEnum situacao) {
        this.situacao = situacao;
    }

    public DestinoFase getDestinoFase() {
        return destinoFase;
    }

    public void setDestinoFase(DestinoFase destinoFase) {
        this.destinoFase = destinoFase;
    }

    public MotivoAlegacao getMotivoAlegacao() {
        return motivoAlegacao;
    }

    public void setMotivoAlegacao(MotivoAlegacao motivoAlegacao) {
        this.motivoAlegacao = motivoAlegacao;
    }

    public TipoFasePaEnum getTipoRecurso() {
        return tipoRecurso;
    }

    public void setTipoRecurso(TipoFasePaEnum tipoRecurso) {
        this.tipoRecurso = tipoRecurso;
    }

    public Date getDataRecurso() {
        return dataRecurso;
    }
    
    @XmlElement(name = "dataRecurso")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataRecurso(Date dataRecurso) {
        this.dataRecurso = dataRecurso;
    }
}