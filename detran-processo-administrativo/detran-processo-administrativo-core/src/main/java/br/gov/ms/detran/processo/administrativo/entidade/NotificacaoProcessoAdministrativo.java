package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.protocolo.entidade.Correspondencia;
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
@Table(name = "TB_TCX_NOTIFICACAO_PROCESSO_ADM")
@NamedQueries({
    @NamedQuery(
        name = "NotificacaoProcessoAdministrativo.getQuantidadeNotificacaoPorDataNotificacao", 
        query = "SELECT COUNT(tcx) "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + "WHERE CONVERT(Date, tcx.dataNotificacao) = CONVERT(Date, :p0) "
                    + "AND tcx.ativo = :p1 "),
    @NamedQuery(
            name = "NotificacaoProcessoAdministrativo.existeNotificacaoAtivaPorProcessoAdministrativoETipoNotificacao",
            query = "SELECT tcx.id "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + " INNER JOIN tcx.processoAdministrativo tdc "
                    + "WHERE tdc.id = :p0 "
                    + " AND tcx.tipoNotificacaoProcesso = :p1 "
                    + " AND tcx.ativo = :p2 "
    ),
    @NamedQuery(
            name = "NotificacaoProcessoAdministrativo.getNotificadosParaRetornoAr",
            query = "SELECT "
                    + "new NotificacaoProcessoAdministrativo(tcx.id, "
                    + "                                      tcx.numeroNotificacao, "
                    + "                                      tcx.objetoCorreio, "
                    + "                                      tcx.tipoNotificacaoProcesso, "
                    + "                                      tdc.id, "
                    + "                                      tdc.numeroProcesso,"
                    + "                                      tcx.correspondencia.id,  "
                    + "                                      tcx.correspondencia.situacao) "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + "INNER JOIN tcx.processoAdministrativo tdc "
                    + "INNER JOIN tcx.fluxoFase tch "
                    + "INNER JOIN tch.prioridadeFluxoAmparo tci "
                    + "INNER JOIN tci.faseProcessoAdm tcv "
                    + "WHERE tcx.ativo = :p2 "
                    + "AND EXISTS ( SELECT 1 "
                    + "             FROM PAOcorrenciaStatus tde "
                    + "             INNER JOIN tde.statusAndamento tcd "
                    + "             INNER JOIN tcd.andamentoProcesso tcb "
                    + "             WHERE tdc.id = tde.processoAdministrativo.id "
                    + "             AND tci.fluxoProcesso.id = tde.fluxoProcesso.id "
                    + "             AND EXISTS ( SELECT 1 "
                    + "                          FROM PAFluxoFase tch1 "
                    + "                          INNER JOIN tch1.prioridadeFluxoAmparo tci1 "
                    + "                          INNER JOIN tci1.faseProcessoAdm tcv1 "
                    + "                          WHERE tcv1.id = tcv.id "
                    + "                          AND tch1.andamentoProcesso.id = tcb.id "
                    + "                         ) "
                    + "             AND tcb.codigo IN (:p0) "
                    + "             AND tde.situacao = :p1 "
                    + "            ) "
                    + "ORDER BY tcx.numeroNotificacao "),
    @NamedQuery(
            name = "NotificacaoProcessoAdministrativo.getListaNotificacaoPorNumeroProcessoParaWSConsultaPA",
            query = "SELECT new br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificadosWrapper( "
                    + " tdc.numeroProcesso, "
                    + " tcx.numeroNotificacao, "
                    + " tcx.objetoCorreio, "
                    + " tcx.tipoNotificacaoProcesso "
                    + " ) "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + " INNER JOIN tcx.processoAdministrativo tdc "
                    + "WHERE tdc.numeroProcesso = :p0 "
                    + " AND tcx.ativo = :p1 "
    ),
    @NamedQuery(
            name = "NotificacaoProcessoAdministrativo.getNotificacaoPorProcessoAdministrativoETipoNotificacao",
            query = "SELECT tcx "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + " INNER JOIN tcx.processoAdministrativo tdc "
                    + "WHERE tdc.id = :p0 "
                    + " AND tcx.tipoNotificacaoProcesso = :p1 "
                    + " AND tcx.ativo = :p2 "
    ),
    @NamedQuery(
            name = "NotificacaoProcessoAdministrativo.getListNotificacoesAtivasPA",
            query = "SELECT tcx "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + " INNER JOIN tcx.processoAdministrativo tdc "
                    + "WHERE tdc.id = :p0 "
                    + " AND tcx.ativo = :p1 "
    ),
    @NamedQuery(
            name = "NotificacaoProcessoAdministrativo.getNotificacaoProcessoAdministrativoPorCorrespondencia",
            query = "SELECT new NotificacaoProcessoAdministrativo(tcx.id, tcx.tipoNotificacaoProcesso, tdc.id, tdc.versaoRegistro) "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + " INNER JOIN tcx.processoAdministrativo tdc "
                    + "WHERE tcx.correspondencia.id = :p0 "
                    + " AND tcx.ativo = :p1 "
    ),
    @NamedQuery(
            name = "NotificacaoProcessoAdministrativo.getNotificacaoPAPorFluxoFase",
            query = "SELECT new NotificacaoProcessoAdministrativo(tcx.id, tcx.tipoNotificacaoProcesso) "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + "WHERE tcx.processoAdministrativo.id = :p0 "
                    + " AND tcx.fluxoFase.prioridadeFluxoAmparo.faseProcessoAdm.codigo like :p1 "
                    + " AND tcx.ativo = :p2 "
    ),
    @NamedQuery(
            name = "NotificacaoProcessoAdministrativo.getNotificacaoPorNumeroProcessoETipo",
            query = "SELECT tcx "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + " INNER JOIN tcx.processoAdministrativo tdc "
                    + "WHERE tdc.numeroProcesso = :p0 "
                    + " AND tcx.tipoNotificacaoProcesso = :p1 "
                    + " AND tcx.ativo = :p2 "
    ),
    @NamedQuery(
            name = "NotificacaoProcessoAdministrativo.getNotificacaoPorNumeroNotificacaoENumeroProcesso",
            query = "SELECT tcx "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + "WHERE tcx.numeroNotificacao = :p0 "
                    + "AND tcx.processoAdministrativo.numeroProcesso = :p1 "
                    + "AND tcx.ativo = :p2 "),
    @NamedQuery(
            name = "NotificacaoProcessoAdministrativo.getNotificacaoPorObjetoCorreios",
            query = "SELECT tcx From NotificacaoProcessoAdministrativo tcx where tcx.objetoCorreio = :p0 and tcx.ativo = :p1 "),
    @NamedQuery(name = "NotificacaoProcessoAdministrativo.getListNotificacoesPorAndamentoIniciado",
            query = "SELECT tcx "
                    + " FROM NotificacaoProcessoAdministrativo tcx "
                    + "     INNER JOIN tcx.arquivo tct "
                    + "     INNER JOIN tcx.processoAdministrativo tdc "
                    + " WHERE EXISTS ("
                    + "     SELECT 1 "
                    + "     FROM PAOcorrenciaStatus tde "
                    + "         INNER JOIN tde.statusAndamento sa "
                    + "         INNER JOIN sa.andamentoProcesso tcb "
                    + "         INNER JOIN tde.processoAdministrativo pa "
                    + "     WHERE pa.id = tdc.id "
                    + "         AND tcb.codigo = :p0 "
                    + "         and tde.ativo = :p1 "
                    + "         AND tde.situacao = :p2) "
                    + "   and tct.ativo = :p1 "
                    + "   and tcx.ativo = :p1 "
                    + "   and tdc.ativo = :p1"),
    @NamedQuery(
            name = "NotificacaoProcessoAdministrativo.getNotificados",
            query = "SELECT "
                    + " tcx "
                    //                    + "new NotificacaoProcessoAdministrativo(tcx.id, tcx.numeroNotificacao, tcx.objetoCorreio, tcx.tipoNotificacaoProcesso, tdc.id, tdc.numeroProcesso) "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + " INNER JOIN tcx.processoAdministrativo tdc "
                    + " INNER JOIN tcx.fluxoFase tch "
                    + " INNER JOIN tch.prioridadeFluxoAmparo tci "
                    + " INNER JOIN tci.faseProcessoAdm tcv  "
                    + "WHERE tcx.ativo = :p2 and "
                    + " EXISTS("
                    + "     SELECT 1 "
                    + "     FROM PAOcorrenciaStatus tde "
                    + "         INNER JOIN tde.statusAndamento tcd "
                    + "         INNER JOIN tcd.andamentoProcesso tcb  "
                    + "     WHERE tdc.id = tde.processoAdministrativo.id "
                    + "         AND tci.fluxoProcesso.id = tde.fluxoProcesso.id "
                    + "         AND EXISTS("
                    + "                 SELECT 1 "
                    + "                 FROM PAFluxoFase tch1 "
                    + "                     INNER JOIN tch1.prioridadeFluxoAmparo tci1 "
                    + "                     INNER JOIN tci1.faseProcessoAdm tcv1 "
                    + "                 WHERE tcv1.id = tcv.id "
                    + "                     and tch1.andamentoProcesso.id = tcb.id) "
                    + "         AND tcb.codigo in (:p0) "
                    + "         AND tde.situacao = :p1) order by tcx.numeroNotificacao "),
    @NamedQuery(
            name = "NotificacaoProcessoAdministrativo.getListNotificacaoPorProcessoAdministrativo",
            query = "SELECT tcx "
                    + "FROM NotificacaoProcessoAdministrativo tcx "
                    + " INNER JOIN tcx.processoAdministrativo tdc "
                    + "WHERE tdc.id = :p0 "
                    + " AND tcx.ativo = :p1 "
                    + "ORDER BY tcx.id DESC "
    ),
})
public class NotificacaoProcessoAdministrativo extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tcx_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Tcx_Correspondencia", referencedColumnName = "Tag_ID")
    private Correspondencia correspondencia;

    @ManyToOne
    @JoinColumn(name = "Tcx_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;

    @Column(name = "Tcx_Edital_Corpo")
    private Long corpoNotificacao;

    @ManyToOne
    @JoinColumn(name = "Tcx_Arquivo_PA", referencedColumnName = "Tct_ID")
    private ArquivoPA arquivo;

    @ManyToOne
    @JoinColumn(name = "Tcx_Endereco", referencedColumnName = "Tdr_ID")
    private PAEnderecoAlternativo endereco;

    @Column(name = "Tcx_Numero_Notificacao")
    private String numeroNotificacao;

    @Column(name = "Tcx_Data_Notificacao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataNotificacao;

    @Lob
    @Column(name = "Tcx_Byte_Barra_Correio")
    private byte[] byteImagemCodigoBarraCorreio;

    @Lob
    @Column(name = "Tcx_Byte_Barra_Cep")
    private byte[] byteImagemCodigoBarraCep;

    @Column(name = "Tcx_Objeto_Correio")
    private String objetoCorreio;

    @Column(name = "Tcx_Linha_Correios")
    private String linhaCorreios;

    @Column(name = "Tcx_Tipo_Notificacao")
    @Enumerated(EnumType.STRING)
    private TipoFasePaEnum tipoNotificacaoProcesso;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "Ativo")
    private AtivoEnum ativo;

    @ManyToOne
    @JoinColumn(name = "Tcx_Fluxo_Fase", referencedColumnName = "Tch_ID")
    private PAFluxoFase fluxoFase;

    @ManyToOne
    @JoinColumn(name = "Tcx_Lote_Notificacao", referencedColumnName = "Tdi_ID")
    private LoteNotificacaoPA lote;

    @Column(name = "Tcx_Data_Prazo_Limite")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPrazoLimite;

    @Column(name = "Tcx_Data_Postagem")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPostagem;

    public NotificacaoProcessoAdministrativo() {
    }

    public NotificacaoProcessoAdministrativo(Long id, TipoFasePaEnum tipoNotificacaoProcesso) {
        this.id = id;
        this.tipoNotificacaoProcesso = tipoNotificacaoProcesso;
    }

    public NotificacaoProcessoAdministrativo(Long id, Long idPA, Long vrPA) {
        this.id = id;
        this.processoAdministrativo = new ProcessoAdministrativo(idPA, vrPA);
    }

    public NotificacaoProcessoAdministrativo(Long id, TipoFasePaEnum tipo, Long idPA, Long vrPA) {
        this.id = id;
        this.tipoNotificacaoProcesso = tipo;
        this.processoAdministrativo = new ProcessoAdministrativo(idPA, vrPA);
    }

    public NotificacaoProcessoAdministrativo(Long id, Long idPA, Long vrPA, ArquivoPA arquivo) {

        this(id, idPA, vrPA);
        this.arquivo = arquivo;
    }

    public NotificacaoProcessoAdministrativo(Long notificacaoId,
                                             String numeroNotificacao,
                                             Long notificacaoVersaoRegistro,
                                             Long paId,
                                             Long paVersaoRegistro) {
        this(notificacaoId, paId, paVersaoRegistro);
        this.numeroNotificacao = numeroNotificacao;
        super.setVersaoRegistro(notificacaoVersaoRegistro);
    }

    public NotificacaoProcessoAdministrativo(Long id,
                                             String numeroNotificacao,
                                             String objetoCorreio,
                                             TipoFasePaEnum tipoNotificacaoProcesso,
                                             Long idPA,
                                             String numeroProcesso) {
        this.id = id;
        this.numeroNotificacao = numeroNotificacao;
        this.objetoCorreio = objetoCorreio;
        this.tipoNotificacaoProcesso = tipoNotificacaoProcesso;
        this.processoAdministrativo = new ProcessoAdministrativo(idPA);
        this.processoAdministrativo.setNumeroProcesso(numeroProcesso);
    }

    public NotificacaoProcessoAdministrativo(Long id,
                                             String numeroNotificacao,
                                             String objetoCorreio,
                                             TipoFasePaEnum tipoNotificacaoProcesso,
                                             Long processoAdministrativoId,
                                             String numeroProcesso,
                                             Long correspondenciaId,
                                             Integer correspondenciaSituacao) {

        this(id, numeroNotificacao, objetoCorreio, tipoNotificacaoProcesso, processoAdministrativoId, numeroProcesso);
        this.correspondencia = new Correspondencia(correspondenciaId, correspondenciaSituacao);
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

    public Correspondencia getCorrespondencia() {
        return correspondencia;
    }

    public void setCorrespondencia(Correspondencia correspondencia) {
        this.correspondencia = correspondencia;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public Long getCorpoNotificacao() {
        return corpoNotificacao;
    }

    public void setCorpoNotificacao(Long corpoNotificacao) {
        this.corpoNotificacao = corpoNotificacao;
    }

    public ArquivoPA getArquivo() {
        return arquivo;
    }

    public void setArquivo(ArquivoPA arquivo) {
        this.arquivo = arquivo;
    }

    public String getNumeroNotificacao() {
        return numeroNotificacao;
    }

    public void setNumeroNotificacao(String numeroNotificacao) {
        this.numeroNotificacao = numeroNotificacao;
    }

    public String getObjetoCorreio() {
        return objetoCorreio;
    }

    public void setObjetoCorreio(String objetoCorreio) {
        this.objetoCorreio = objetoCorreio;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public byte[] getByteImagemCodigoBarraCorreio() {
        return byteImagemCodigoBarraCorreio;
    }

    public void setByteImagemCodigoBarraCorreio(byte[] byteImagemCodigoBarraCorreio) {
        this.byteImagemCodigoBarraCorreio = byteImagemCodigoBarraCorreio;
    }

    public byte[] getByteImagemCodigoBarraCep() {
        return byteImagemCodigoBarraCep;
    }

    public void setByteImagemCodigoBarraCep(byte[] byteImagemCodigoBarraCep) {
        this.byteImagemCodigoBarraCep = byteImagemCodigoBarraCep;
    }

    public String getLinhaCorreios() {
        return linhaCorreios;
    }

    public void setLinhaCorreios(String linhaCorreios) {
        this.linhaCorreios = linhaCorreios;
    }

    public TipoFasePaEnum getTipoNotificacaoProcesso() {
        return tipoNotificacaoProcesso;
    }

    public void setTipoNotificacaoProcesso(TipoFasePaEnum tipoNotificacaoProcesso) {
        this.tipoNotificacaoProcesso = tipoNotificacaoProcesso;
    }

    public Date getDataNotificacao() {
        return dataNotificacao;
    }

    @XmlElement(name = "dataNotificacao")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataNotificacao(Date dataNotificacao) {
        this.dataNotificacao = dataNotificacao;
    }

    public PAFluxoFase getFluxoFase() {
        return fluxoFase;
    }

    public void setFluxoFase(PAFluxoFase fluxoFase) {
        this.fluxoFase = fluxoFase;
    }

    public PAEnderecoAlternativo getEndereco() {
        return endereco;
    }

    public void setEndereco(PAEnderecoAlternativo endereco) {
        this.endereco = endereco;
    }

    public LoteNotificacaoPA getLote() {
        return lote;
    }

    public void setLote(LoteNotificacaoPA lote) {
        this.lote = lote;
    }

    public Date getDataPrazoLimite() {
        return dataPrazoLimite;
    }

    @XmlElement(name = "dataPrazoLimite")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataPrazoLimite(Date dataPrazoLimite) {
        this.dataPrazoLimite = dataPrazoLimite;
    }

    @XmlElement(name = "numeroNotificacaoMascarado")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public String getNumeroNotificacaoMascarado() {
        return numeroNotificacao != null ? numeroNotificacao.toString() : null;
    }

    @XmlElement(name = "numeroNotificacaoMascarado")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public void setNumeroNotificacaoMascarado(String numeroProcesso) {
    }

    public Date getDataPostagem() {
        return dataPostagem;
    }

    @XmlElement(name = "dataPostagem")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataPostagem(Date dataPostagem) {
        this.dataPostagem = dataPostagem;
    }
}
