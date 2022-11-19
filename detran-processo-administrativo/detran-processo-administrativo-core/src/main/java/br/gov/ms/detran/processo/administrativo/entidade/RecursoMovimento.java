package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.MotivoCancelamentoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
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
@Table(name = "TB_TDQ_PAD_RECURSO_MOVIMENTO")
@NamedQueries({
    @NamedQuery(
            name = "RecursoMovimento.getRecursoMovimentoPorNumeroProtocoloENumeroProcessoAdministrativo",
            query = "SELECT tdq "
            + "FROM RecursoMovimento tdq "
            + "INNER JOIN tdq.recurso tdp "
            + "INNER JOIN tdq.protocolo tdm "
            + "WHERE tdp.processoAdministrativo.numeroProcesso = :p0 "
            + "AND tdm.numeroProtocolo = :p1 "
            + "AND tdq.ativo = :p2 "
            + "AND tdp.ativo = :p2 "
            + "AND tdm.ativo = :p2 "),
    @NamedQuery(
        name = "RecursoMovimento.getRecursoMovimentoPorRecursoETipoProtocolo",
        query = "SELECT tdq "
                + "FROM RecursoMovimento tdq "
                + "INNER JOIN tdq.recurso tdp "
                + "INNER JOIN tdq.protocolo tdm "
                + "WHERE tdp.id = :p0 "
                + "ORDER BY tdq.dataInclusao DESC "
    ),
    @NamedQuery(
            name = "RecursoMovimento.getRecursos",
            query = "SELECT new RecursoMovimento( "
            + " tdq.id, tdq.dataMovimento, tdq.indiceForaPrazo, tdq.usuario, tdq.versaoRegistro, "
            + " tdp.id, tdp.tipoRecurso, tdp.situacao, tdp.versaoRegistro, tdp.dataRecurso, "
            + " tdc.id, tdc.numeroProcesso, tdc.versaoRegistro, "
            + " tdm.id, tdm.numeroProtocolo, tdm.templateProtocolo, tdm.versaoRegistro, "
            + " tcp.id, tcp.origemDestino, tcp.versaoRegistro) "
            + "FROM RecursoMovimento tdq "
            + " INNER JOIN tdq.protocolo tdm "
            + " INNER JOIN tdq.recurso tdp "
            + " INNER JOIN tdp.processoAdministrativo tdc "
            + " INNER JOIN tdp.destinoFase tcp "
            + "WHERE EXISTS(SELECT 1 "
            + "             FROM PAOcorrenciaStatus tde"
            + "                 INNER JOIN tde.statusAndamento tcd "
            + "             WHERE tdc.id = tde.processoAdministrativo.id "
            + "                 AND tde.situacao = :p1 "
            + "                 AND tcd.andamentoProcesso.codigo in (:p0) "
            + "                 AND tde.ativo = :p2 "
            + "                 AND tcd.ativo = :p2) "
            + "AND tdq.ativo = :p2 "
            + "AND tdp.ativo = :p2 "
            + "AND tdp.situacao = :p3"),
    @NamedQuery(
            name = "RecursoMovimento.getRecursosCancelados",
            query = "SELECT tdq "
            + "FROM RecursoMovimento tdq  INNER JOIN tdq.recurso tdp "
            + "WHERE EXISTS(SELECT 1 "
            + "                 FROM PAOcorrenciaStatus tde "
            + "                     INNER JOIN tde.statusAndamento tcd "
            + "                 WHERE tdp.processoAdministrativo.id = tde.processoAdministrativo.id "
            + "                     AND tde.situacao = :p1 "
            + "                     AND tcd.andamentoProcesso.codigo in (:p0) "
            + "                     AND tde.ativo = :p2 "
            + "                     AND tcd.ativo = :p2 "
            + "             ) "
            + " AND tdp.situacao = :p3 "
            + " AND tdq.ativo = :p2 "
            + " AND tdp.ativo = :p2 "
    ),
    @NamedQuery(
            name = "RecursoMovimento.getRecursosCanceladosPorPA",
            query = "SELECT tdq "
            + "FROM RecursoMovimento tdq  "
            + " INNER JOIN tdq.recurso tdp "
            + " INNER JOIN tdp.processoAdministrativo tdc "
            + "WHERE tdc.id = :p0 "
            + " AND tdp.situacao = :p1 "
            + " AND tdq.ativo = :p2 "
            + " AND tdp.ativo = :p2 "
    ),
    @NamedQuery(
            name = "RecursoMovimento.getRecursoMovimentoPorRecurso",
            query = "SELECT tdq "
            + "FROM RecursoMovimento tdq "
            + " INNER JOIN tdq.recurso tdp "
            + "WHERE tdp.id = :p0 "
            + " AND tdq.ativo = :p1 "
    ),
    @NamedQuery(
            name = "RecursoMovimento.getIndiceForaPrazo",
            query = "SELECT new RecursoMovimento(tdq.indiceForaPrazo) "
            + "FROM RecursoMovimento tdq "
            + "INNER JOIN tdq.recurso tdp "
            + "WHERE tdp.id = :p0 "
            + "AND tdq.ativo = :p1 order by tdq.dataMovimento "),
    @NamedQuery(
            name = "RecursoMovimento.getMovimentoRecursoPorRecursoID",
            query = "SELECT tdq "
            + "FROM RecursoMovimento tdq "
            + "INNER JOIN tdq.recurso tdp "
            + "WHERE tdp.id = :p0 "
            + "AND tdq.ativo = :p1 order by tdq.dataMovimento "),
    @NamedQuery(
            name = "RecursoMovimento.getRecursosPorPA",
            query = "SELECT new RecursoMovimento( "
            + " tdq.id, tdq.dataMovimento, tdq.indiceForaPrazo, tdq.usuario, tdq.versaoRegistro, "
            + " tdp.id, tdp.tipoRecurso, tdp.situacao, tdp.versaoRegistro, tdp.dataRecurso, "
            + " tdc.id, tdc.numeroProcesso, tdc.versaoRegistro, "
            + " tdm.id, tdm.numeroProtocolo, tdm.templateProtocolo, tdm.versaoRegistro, "
            + " tcp.id, tcp.origemDestino, tcp.versaoRegistro) "
            + "FROM RecursoMovimento tdq "
            + " INNER JOIN tdq.protocolo tdm "
            + " INNER JOIN tdq.recurso tdp "
            + " INNER JOIN tdp.processoAdministrativo tdc "
            + " INNER JOIN tdp.destinoFase tcp "
            + "WHERE tdc.id = :p0 "
            + "AND tdp.ativo = :p1 "
            + "AND tdm.id IN (SELECT MIN(tdm2.id) "
                    + "         FROM RecursoMovimento tdq2 "
                    + "         INNER JOIN tdq2.protocolo tdm2 "
                    + "         INNER JOIN tdq2.recurso tdp2 "
                    + "         INNER JOIN tdp2.processoAdministrativo tdc2 "
                    + "         WHERE tdc2.id = :p0 "
                    + "         AND tdp2.ativo = :p1) ")
})
@NamedNativeQueries({
    @NamedNativeQuery(
            name = "RecursoMovimento.getRecursoParaCancelamentoPorCadastroIndevido",
            query = "SELECT TOP(1) "
                    + "tdq.* "
                    + "FROM TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc "
                    + "INNER JOIN dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde ON tdc.Tdc_ID = tde.Tde_Processo_Administrativo "
                    + "INNER JOIN dbo.TB_TCK_PAD_FLUXO_PROCESSO tck ON tde.Tde_Fluxo_Processo = tck.Tck_ID "
                    + "INNER JOIN dbo.TB_TCM_TIPO_CORPO_ANDAMENTO tcm ON Tcm_Fluxo_Recurso_Cancelado = tck.Tck_ID "
                    + "INNER JOIN dbo.TB_TDH_PAD_APOIO_ORIGEM_INSTAURACAO tdh ON tdh.tdh_id = tcm.Tcm_Origem_Processo AND tdc.Tdc_Apoio_Origem_Instauracao = tdh.Tdh_ID "
                    + "INNER JOIN dbo.TB_TDP_PAD_RECURSO tdp ON tdc.Tdc_ID = tdp.Tdp_Processo_Administrativo AND tcm.Tcm_Tipo_Notificacao_Processo = tdp.Tdp_Tipo "
                    + "INNER JOIN dbo.TB_TDQ_PAD_RECURSO_MOVIMENTO tdq ON tdq.Tdq_Recurso = tdp.Tdp_ID "
                    + "INNER JOIN dbo.TB_TDM_PAD_PROTOCOLO tdm ON tdm.tdm_id = tdq.Tdq_Protocolo "
                    + "INNER JOIN dbo.TB_XHV_TEMPLATE_PROTOCOLO xhv ON xhv.Xhv_ID = tdm.Tdm_Template_Protocolo AND xhv.Xhv_Tipo_Situacao = :p1 "
                    + "WHERE tdc.Tdc_Numero_Processo = :p0 "
                    + "ORDER BY xhv.Xhv_Data_Protocolo DESC ",   
            resultClass = RecursoMovimento.class)
})
public class RecursoMovimento extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdq_Id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Tdq_Recurso", referencedColumnName = "Tdp_Id")
    private Recurso recurso;

    @ManyToOne
    @JoinColumn(name = "Tdq_Protocolo", referencedColumnName = "Tdm_ID")
    private Protocolo protocolo;

    @Column(name = "Tdq_Usuario")
    private Long usuario;

    @Column(name = "Tdq_Data_Movimento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataMovimento;

    @Column(name = "Tdq_Indice_Fora_Prazo")
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum indiceForaPrazo;
    
    @Column(name = "Tdq_Motivo_Cancelamento")
    @Enumerated(EnumType.STRING)
    private MotivoCancelamentoRecursoEnum motivoCancelamento;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public RecursoMovimento() {
    }

    public RecursoMovimento(BooleanEnum indiceForaPrazo) {
        this.indiceForaPrazo = indiceForaPrazo;
    }

    public RecursoMovimento(Long id,
            Date dataMovimento,
            TipoFasePaEnum tipoRecurso,
            SituacaoRecursoEnum situacao,
            String numeroProcesso,
            String numeroProtocolo) {
        this.id = id;
        this.dataMovimento = dataMovimento;
        this.recurso = new Recurso(tipoRecurso, situacao, numeroProcesso);
        this.protocolo = new Protocolo(numeroProtocolo);
    }

    /* namedquery: getRecursos */
    public RecursoMovimento(
            Long id_TDQ, Date dataMovimento, BooleanEnum indiceForaPrazo, Long usuario, Long versaoRegistro_TDQ,
            Long id_TDP, TipoFasePaEnum tipoRecurso, SituacaoRecursoEnum situacao, Long versaoRegistro_TDP, Date dataRecurso,
            Long id_TDC, String numeroProcesso, Long versaoRegistro_TDC,
            Long id_TDM, String numeroProtocolo, Long templateProtocoloID, Long versaoRegistro_TDM,
            Long id_TCP, OrigemDestinoEnum origemDestino, Long versaoRegistro_TCP) {

        /**
         * RecursoMovimento - TDQ. *
         */
        this.id = id_TDQ;
        this.dataMovimento = dataMovimento;
        this.indiceForaPrazo = indiceForaPrazo;
        this.usuario = usuario;
        this.setVersaoRegistro(versaoRegistro_TDQ);

        /**
         * Recurso - TDP. *
         */
        this.setRecurso(new Recurso(id_TDP, tipoRecurso, situacao, versaoRegistro_TDP, dataRecurso));

        /**
         * ProcessoAdministrativo - TDC. *
         */
        this.recurso.setProcessoAdministrativo(new ProcessoAdministrativo(id_TDC, numeroProcesso, versaoRegistro_TDC));

        /**
         * Protocolo - TDM. *
         */
        this.setProtocolo(new Protocolo(id_TDM, numeroProtocolo, templateProtocoloID, versaoRegistro_TDM));

        /**
         * Destino Fase - TCP. *
         */
        this.recurso.setDestinoFase(new DestinoFase(id_TCP, origemDestino, versaoRegistro_TCP));

        this.indiceForaPrazo = indiceForaPrazo;
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

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    public Protocolo getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(Protocolo protocolo) {
        this.protocolo = protocolo;
    }

    public Long getUsuario() {
        return usuario;
    }

    public void setUsuario(Long usuario) {
        this.usuario = usuario;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    @XmlElement(name = "dataMovimento")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public BooleanEnum getIndiceForaPrazo() {
        return indiceForaPrazo;
    }

    public void setIndiceForaPrazo(BooleanEnum indiceForaPrazo) {
        this.indiceForaPrazo = indiceForaPrazo;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public MotivoCancelamentoRecursoEnum getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(MotivoCancelamentoRecursoEnum motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }
}