package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.ComissaoAnaliseEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoNotificacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.ResultadoRecursoEnum;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TDT_PAD_RESULTADO_RECURSO")
@NamedQueries({
    @NamedQuery(
        name    = "ResultadoRecurso.getResultadoRecursoAtivoPorRecurso",
        query   = "SELECT tdt "
                + "FROM ResultadoRecurso tdt "
                + " INNER JOIN tdt.recurso tdp "
                + "WHERE tdp.id = :p0 "
                + " AND tdt.ativo = :p1 "),
    @NamedQuery(
        name    = "ResultadoRecurso.getUsuarioDoResultadoRecursoAtivoPorProcessoAdministrativoEResultado",
        query   = "SELECT new ResultadoRecurso(tdt.id, tdt.usuario) "
                + " FROM ResultadoRecurso tdt "
                + " WHERE tdt.recurso.processoAdministrativo.id = :p0 "
                + "     AND tdt.ativo = :p1 "
                + "     AND tdt.recurso.ativo = :p1 "
                + "     AND tdt.resultado in (:p2) "
    ),
    @NamedQuery(name = "ResultadoRecurso.getResultadoRecursoPorProcessoAdministrativoESituacaoEDestino",
            query = "SELECT tdt "
                    + " FROM ResultadoRecurso tdt "
                    + "     INNER JOIN tdt.recurso tdp "
                    + "     INNER JOIN tdp.destinoFase df "
                    + " where tdp.processoAdministrativo.id = :p0 "
                    + "     and df.origemDestino = :p1 "
                    + "     and tdp.situacao = :p2 "
                    + "     and tdp.ativo = :p3 "
                    + "     and tdt.ativo = :p3"),
    @NamedQuery(name = "ResultadoRecurso.getResultadoRecursoPorProcessoEResultadoEAcao",
                query = "SELECT tct "
                        + " FROM ResultadoRecurso tct "
                        + "     INNER JOIN tct.recurso tdp "
                        + "     INNER JOIN tdp.processoAdministrativo tdc "
                        + " WHERE tdc.id = :p0 "
                        + "     and tct.resultado = :p1 "
                        + "     and tct.acao = :p2 "
                        + "     and tct.ativo = :p3"),
    @NamedQuery(
        name    = "ResultadoRecurso.getResultadoRecursoAtivoPorProcessoAdministrativoEResultado",
        query   = "SELECT tdt "
                + " FROM ResultadoRecurso tdt "
                + " WHERE tdt.recurso.processoAdministrativo.id = :p0 "
                + "     AND tdt.ativo = :p1 "
                + "     AND tdt.recurso.ativo = :p1 "
                + "     AND tdt.resultado in (:p2) "
    )
})
public class ResultadoRecurso extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdt_ID")
    private Long id;
    
    @Column(name = "Tdt_Usuario_Resultado")
    private Long usuario;
    
    @ManyToOne
    @JoinColumn(name = "Tdt_Recurso", referencedColumnName = "Tdp_Id")
    private Recurso recurso;
    
    @Column(name = "Tdt_Resultado")
    @Enumerated(EnumType.STRING)
    private ResultadoRecursoEnum resultado;
    
    @Column(name = "Tdt_Data")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date data;
    
    @Column(name = "Tdt_Parecer")
    private String parecer;
    
    @Column(name = "Tdt_Acao")
    @Enumerated(EnumType.STRING)
    private RecursoNotificacaoAcaoEnum acao;
    
    @Column(name = "Tdt_Comissao_Analise")
    @Enumerated(EnumType.STRING)
    private ComissaoAnaliseEnum comissaoAnalise;
    
    @ManyToOne
    @JoinColumn(name = "Tdt_Pad_Motivo_Alegacao", referencedColumnName = "Tcq_Id")
    private MotivoAlegacao motivo;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public ResultadoRecurso() {
    }

    public ResultadoRecurso(Long id, Long usuario) {
        this.id = id;
        this.usuario = usuario;
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

    
    public Long getUsuario() {
        return usuario;
    }

    public void setUsuario(Long usuario) {
        this.usuario = usuario;
    }

    public ResultadoRecursoEnum getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoRecursoEnum resultado) {
        this.resultado = resultado;
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

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public RecursoNotificacaoAcaoEnum getAcao() {
        return acao;
    }

    public void setAcao(RecursoNotificacaoAcaoEnum acao) {
        this.acao = acao;
    }

    public MotivoAlegacao getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoAlegacao motivo) {
        this.motivo = motivo;
    }

    public ComissaoAnaliseEnum getComissaoAnalise() {
        return comissaoAnalise;
    }

    public void setComissaoAnalise(ComissaoAnaliseEnum comissaoAnalise) {
        this.comissaoAnalise = comissaoAnalise;
    }
}