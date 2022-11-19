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
import javax.persistence.Lob;
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
@Table(name = "TB_TDM_PAD_PROTOCOLO")
@NamedQueries({
    @NamedQuery(
        name = "Protocolo.getProcessoAdministrativoDesistentesRecursoInstauracaoPenalizacao",
        query = "SELECT new br.gov.ms.detran.processo.administrativo.wrapper.DesistenciaRecursoInstauracaoPenalizacaoWS( "
                + " tdc.numeroProcesso, "
                + " tdm.numeroProtocolo, "
                + " tdc.cpf, "
                + " tdm.dataProtocolo, "
                + " tdm.templateProtocolo) "
                + "FROM Protocolo tdm "
                + " INNER JOIN tdm.numeroProcesso tdc "
                + "WHERE EXISTS(SELECT 1 "
                + "                 FROM PAOcorrenciaStatus tde "
                + "                     INNER JOIN tde.statusAndamento tcd "
                + "                     INNER JOIN tcd.andamentoProcesso tcb "
                + "                 WHERE tde.processoAdministrativo.id = tdc.id "
                + "                     AND tcb.codigo = :p0) "
                + " AND tdc.ativo = :p1 "
    ),
    @NamedQuery(
            name = "Protocolo.getProtocoloPorNumeroProtocolo",
            query = "SELECT tdm FROM Protocolo tdm WHERE tdm.numeroProtocolo = :p0"),
    @NamedQuery(
            name = "Protocolo.getProtocoloPorProcessoAdministrativo",
            query = "SELECT p From Protocolo p where p.numeroProcesso.id = :p0 and p.ativo = :p1")
})
@NamedNativeQueries({
    @NamedNativeQuery(
            name = "Protocolo.getProtocoloPorNumeroProtocoloETipoNotificacaoDesistente",
            query = "SELECT tdm.* "
                    + "FROM dbo.TB_TDM_PAD_PROTOCOLO tdm "
                    + "INNER JOIN dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tdc.Tdc_ID = tdm.Tdm_Numero_Processo "
                    + "INNER JOIN dbo.TB_GEK_EDITAL_CORPO gek ON gek.Gek_ID = tdm.Tdm_Edital_Corpo "
                    + "WHERE tdm.Tdm_Numero_Protocolo = :p0 "
                    + "AND gek.Gek_Tipo IN (:p1) "
                    + "AND tdm.Ativo = :p2 ",
            resultClass = Protocolo.class),
    @NamedNativeQuery(
            name = "Protocolo.getProtocoloParaCancelamentoPorCadastroIndevido",
            query = "SELECT TOP(1) "
                    + "tdm.* "
                    + "FROM TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc "
                    + "INNER JOIN dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde ON tdc.Tdc_ID = tde.Tde_Processo_Administrativo "
                    + "INNER JOIN dbo.TB_TCK_PAD_FLUXO_PROCESSO tck ON tde.Tde_Fluxo_Processo = tck.Tck_ID "
                    + "INNER JOIN dbo.TB_TCM_TIPO_CORPO_ANDAMENTO tcm ON Tcm_Fluxo_Recurso_Cancelado = tck.Tck_ID "
                    + "INNER JOIN dbo.TB_TDH_PAD_APOIO_ORIGEM_INSTAURACAO tdh ON tdh.tdh_id = tcm.Tcm_Origem_Processo AND tdc.Tdc_Apoio_Origem_Instauracao = tdh.Tdh_ID "
                    + "INNER JOIN dbo.TB_TDP_PAD_RECURSO tdp ON tdc.Tdc_ID = tdp.Tdp_Processo_Administrativo AND tcm.Tcm_Tipo_Notificacao_Processo = tdp.Tdp_Tipo "
                    + "INNER JOIN dbo.TB_TDQ_PAD_RECURSO_MOVIMENTO tdq ON tdq.Tdq_Recurso = tdp.Tdp_ID "
                    + "INNER JOIN dbo.TB_TDM_PAD_PROTOCOLO tdm ON tdm.tdm_id = tdq.Tdq_Protocolo "
                    + "INNER JOIN dbo.TB_XHV_TEMPLATE_PROTOCOLO xhv ON xhv.Xhv_ID = tdm.Tdm_Template_Protocolo AND xhv.Xhv_Tipo_Situacao = :p1 "
                    + "WHERE tdc.Tdc_ID = :p0 "
                    + "ORDER BY xhv.Xhv_Data_Protocolo DESC ",
            resultClass = Protocolo.class),
    @NamedNativeQuery(
            name = "Protocolo.getProtocoloPorProcessoAdministrativoETipoNotificacao",
            query = "SELECT tdm.* "
                    + "FROM dbo.TB_TDM_PAD_PROTOCOLO tdm "
                    + "INNER JOIN dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tdc.Tdc_ID = tdm.Tdm_Numero_Processo "
                    + "INNER JOIN dbo.TB_GEK_EDITAL_CORPO gek ON gek.Gek_ID = tdm.Tdm_Edital_Corpo "
                    + "WHERE tdc.Tdc_ID = :p0 "
                    + "AND gek.Gek_Tipo = :p1 ",
            resultClass = Protocolo.class)
})
public class Protocolo extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdm_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Tdm_Fluxo_Fase", referencedColumnName = "Tch_ID")
    private PAFluxoFase fluxoFase;

    @ManyToOne
    @JoinColumn(name = "Tdm_Arquivo_Pa", referencedColumnName = "Tct_ID")
    private ArquivoPA arquivoPa;

    @Column(name = "Tdm_Edital_Corpo")
    private Long editalCorpo;

    @Column(name = "Tdm_Template_Protocolo")
    private Long templateProtocolo;

    @Column(name = "Tdm_Numero_Protocolo")
    private String numeroProtocolo;

    @ManyToOne
    @JoinColumn(name = "Tdm_Numero_Processo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo numeroProcesso;

    @Column(name = "Tdm_Data_Protocolo")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataProtocolo;
    
    @Lob
    @Column(name = "Tdm_Byte_Barra")
    private byte[] byteCodigoBarra;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public Protocolo() {
    }

    public Protocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Protocolo(Long protocoloId, String numeroProtocolo, Long protocoloVersaoRegistro) {
        this.id = protocoloId;
        this.numeroProtocolo = numeroProtocolo;
        super.setVersaoRegistro(protocoloVersaoRegistro);
    }

    public Protocolo(Long id, Long versaoRegistro) {
        this.id = id;
        super.setVersaoRegistro(versaoRegistro);
    }

    Protocolo(Long id_TDM, String numeroProtocolo, Long templateProtocoloID, Long versaoRegistro_TDM) {
        this.id = id_TDM;
        this.numeroProtocolo = numeroProtocolo;
        this.templateProtocolo = templateProtocoloID;
        super.setVersaoRegistro(versaoRegistro_TDM);
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

    public ArquivoPA getArquivoPa() {
        return arquivoPa;
    }

    public void setArquivoPa(ArquivoPA arquivoPa) {
        this.arquivoPa = arquivoPa;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public byte[] getByteCodigoBarra() {
        return byteCodigoBarra;
    }

    public void setByteCodigoBarra(byte[] byteCodigoBarra) {
        this.byteCodigoBarra = byteCodigoBarra;
    }

    public Long getEditalCorpo() {
        return editalCorpo;
    }

    public void setEditalCorpo(Long editalCorpo) {
        this.editalCorpo = editalCorpo;
    }

    public ProcessoAdministrativo getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(ProcessoAdministrativo numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Long getTemplateProtocolo() {
        return templateProtocolo;
    }

    public void setTemplateProtocolo(Long templateProtocolo) {
        this.templateProtocolo = templateProtocolo;
    }

    
    public Date getDataProtocolo() {
        return dataProtocolo;
    }

    @XmlElement(name = "dataProtocolo")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataProtocolo(Date dataProtocolo) {
        this.dataProtocolo = dataProtocolo;
    }

    
    @XmlElement(name = "numeroProtocoloMascarado")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public String getNumeroProtocoloMascarado() {
        return numeroProtocolo;
    }

    @XmlElement(name = "numeroProtocoloMascarado")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public String setNumeroProtocoloMascarado(String numeroProtocolo) {
        return this.numeroProtocolo = numeroProtocolo;
    }

}
