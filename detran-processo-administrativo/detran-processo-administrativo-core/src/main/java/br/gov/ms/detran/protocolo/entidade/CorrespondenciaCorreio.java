package br.gov.ms.detran.protocolo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TAU_CORRESPONDENCIA_CORREIO")
@NamedQueries({
    @NamedQuery(
            name = "CorrespondenciaCorreio.getCorrespondenciaCorreioParaRetornoAr",
            query = "SELECT new CorrespondenciaCorreio(tau.id, tau.dataChegadaDestino, tau.documentoRecebedor, tau.nomeRecebedor) "
                    + "FROM CorrespondenciaCorreio tau "
                    + "WHERE tau.correspondencia.id = :p0 "
                    + "AND tau.ativo = :p1 "),
    @NamedQuery(
        name = "CorrespondenciaCorreio.getCorrespondenciaCorreioPorNumeroETipoNotificacao",
        query = "SELECT tau "
                + " FROM CorrespondenciaCorreio tau "
                + "     INNER JOIN tau.correspondencia tag "
                + " WHERE EXISTS( "
                + "         SELECT 1 "
                + "             FROM NotificacaoProcessoAdministrativo tcx "
                + "                 INNER JOIN tcx.correspondencia tag1 "
                + "             where tag.id = tag1.id "
                + "                 and tcx.numeroNotificacao = :p0 "
                + "                 and tcx.tipoNotificacaoProcesso = :p1) "
                + " AND tau.ativo = :p2 "
    ),
    @NamedQuery(
                name = "CorrespondenciaCorreio.getCorrespondenciaCorreioPorProcessoETipo",
                query = "SELECT tau "
                        + " From CorrespondenciaCorreio tau "
                        + " where EXISTS ("
                        + "             SELECT 1 "
                        + "             FROM NotificacaoProcessoAdministrativo tcx "
                        + "                 INNER JOIN tcx.correspondencia tag "
                        + "                 INNER JOIN tcx.processoAdministrativo tdc "
                        + "             where tag.id = tau.correspondencia.id "
                        + "                 and tcx.tipoNotificacaoProcesso = :p1 "
                        + "                 and tdc.id = :p0 "
                        + "                 and tcx.ativo = :p2) "),
    @NamedQuery(
            name = "CorrespondenciaCorreio.getCorrespondenciaCorreioPorCorrespondenciaIdTodaEntidade",
            query = "SELECT tau "
                    + "FROM CorrespondenciaCorreio tau "
                    + "WHERE tau.correspondencia.id = :p0 "
                    + "AND tau.ativo = :p1 "),
    @NamedQuery(
            name = "CorrespondenciaCorreio.getCorrespondenciaCorreioPorCorrespondenciaId",
            query = "SELECT new CorrespondenciaCorreio(tau.id, tau.dataPostagem) "
                    + "FROM CorrespondenciaCorreio tau "
                    + "WHERE tau.correspondencia.id = :p0 "
                    + "AND tau.ativo = :p1 ")    
})
public class CorrespondenciaCorreio extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tau_ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tau_Correspondencia", referencedColumnName = "Tag_ID")
    private Correspondencia correspondencia;
    
    @Column(name = "Tau_Identificador_Inicial")
    private Integer identificadorInicial;
    
    @Column(name = "Tau_Identificador_Final")
    private String identificadorFinal;
    
    @Column(name = "Tau_Numero_Correio")
    private String numeroCorreio;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "Tau_Data_Postagem")
    private Date dataPostagem;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "Tau_Data_Chegada_Destino")
    private Date dataChegadaDestino;
    
    @Column(name = "Tau_Documento_Recebedor")
    private String documentoRecebedor;
    
    @Column(name = "Tau_Nome_Recebedor")
    private String nomeRecebedor;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "Tau_Data_Inclusao")
    private Date dataInclusaoCorrespondenciaCorreio;
    
    @Column(name = "Ativo")
    private AtivoEnum ativo;

    public CorrespondenciaCorreio() {
    }
    
    public CorrespondenciaCorreio(Long id, Date dataChegadaDestino, String documentoRecebedor, String nomeRecebedor) {
        this(id);
        this.dataChegadaDestino = dataChegadaDestino;
        this.documentoRecebedor = documentoRecebedor;
        this.nomeRecebedor = nomeRecebedor;
    }

    public CorrespondenciaCorreio(Long id) {
        this.id = id;
    }

    public CorrespondenciaCorreio(Long id, Date dataPostagem) {
        this(id);
        this.dataPostagem = dataPostagem;
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

    public Integer getIdentificadorInicial() {
        return identificadorInicial;
    }

    public void setIdentificadorInicial(Integer identificadorInicial) {
        this.identificadorInicial = identificadorInicial;
    }

    public String getIdentificadorFinal() {
        return identificadorFinal;
    }

    public void setIdentificadorFinal(String identificadorFinal) {
        this.identificadorFinal = identificadorFinal;
    }

    public String getNumeroCorreio() {
        return numeroCorreio;
    }

    public void setNumeroCorreio(String numeroCorreio) {
        this.numeroCorreio = numeroCorreio;
    }
    
    public Date getDataPostagem() {
        return dataPostagem;
    }

    @XmlElement(name = "dataPostagem")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataPostagem(Date dataPostagem) {
        this.dataPostagem = dataPostagem;
    }

    public Date getDataChegadaDestino() {
        return dataChegadaDestino;
    }

    @XmlElement(name = "dataChegadaDestino")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataChegadaDestino(Date dataChegadaDestino) {
        this.dataChegadaDestino = dataChegadaDestino;
    }

    public String getDocumentoRecebedor() {
        return documentoRecebedor;
    }

    public void setDocumentoRecebedor(String documentoRecebedor) {
        this.documentoRecebedor = documentoRecebedor;
    }

    public String getNomeRecebedor() {
        return nomeRecebedor;
    }

    public void setNomeRecebedor(String nomeRecebedor) {
        this.nomeRecebedor = nomeRecebedor;
    }

    public Date getDataInclusaoCorrespondenciaCorreio() {
        return dataInclusaoCorrespondenciaCorreio;
    }

    public void setDataInclusaoCorrespondenciaCorreio(Date dataInclusaoCorrespondenciaCorreio) {
        this.dataInclusaoCorrespondenciaCorreio = dataInclusaoCorrespondenciaCorreio;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }
}