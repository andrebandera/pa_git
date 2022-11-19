package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.iface.IJSPASequencial;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TCK_PAD_FLUXO_PROCESSO")
@NamedQueries({
    @NamedQuery(
        name = "PAFluxoProcesso.getListaPAFluxoProcessoAtivo",
        query = "SELECT tck FROM PAFluxoProcesso tck WHERE tck.codigo <= :p0 and tck.ativo = :p1 "),
    @NamedQuery(
        name = "PAFluxoProcesso.getPAFluxoProcessoPorCodigo",
        query = "SELECT tck FROM PAFluxoProcesso tck WHERE tck.codigo = :p0 AND tck.ativo = :p1 "),
    @NamedQuery(
        name = "PAFluxoProcesso.getPAFluxoProcessoPorApoioOrigemInstauracao",
        query = "SELECT tck "
                + "FROM PAFluxoOrigem tcl "
                + " INNER JOIN tcl.fluxoProcesso tck "
                + " INNER JOIN tcl.origemInstauracao tdh "
                + "WHERE tdh.id = :p0 "
                + " AND tcl.ativo = :p1 "
                + " AND tck.ativo = :p1 "
                + " AND tcl.indiceFluxoInicial = :p2 "
        ),
    @NamedQuery(
            name = "PAFluxoProcesso.PAFluxoProcessoByDescricao",
            query = "SELECT pa FROM PAFluxoProcesso pa WHERE UPPER(pa.descricao) = :p0"),
    @NamedQuery(
            name = "PAFluxoProcesso.PAFluxoProcessoById",
            query = "SELECT pa FROM PAFluxoProcesso pa WHERE pa.id = :p0"),
    @NamedQuery(
            name = "PAFluxoProcesso.getListaPAFluxoProcessoPorProcesso",
            query = "select tck "
                + "from ProcessoAdministrativo tdc  "
                + "inner join tdc.origemApoio tdh "
                + "inner join PAFluxoOrigem tcl on tcl.origemInstauracao = tdh.id "
                + "inner join tcl.fluxoProcesso tck "
                + "where tdc.numeroProcesso = :p0 AND tck.ativo = :p1 and tcl.ativo = :p1")
})
public class PAFluxoProcesso extends BaseEntityAtivo implements Serializable, IJSPASequencial{
    
    @Id
    @Column(name = "Tck_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "Tck_Codigo")
    private Integer codigo;
    
    @Column(name = "Tck_Descricao")
    private String descricao;
    
    @Column(name = "Tck_Amparo_Legal")
    private Long amparoLegal;
    
    @Column(name = "Tck_Fluxo_Independente")
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum fluxoIndependente = BooleanEnum.NAO;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public PAFluxoProcesso() {
    }
    
    public Long getId() {
        return id;
    }
    
    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public void setId(Serializable id) {
        this.id = (id != null ? Long.valueOf(id.toString()) : null);
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getAmparoLegal() {
        return amparoLegal;
    }

    public void setAmparoLegal(Long amparoLegal) {
        this.amparoLegal = amparoLegal;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    @Override
    public void setSequencial(Long sequencial) {
        this.codigo = sequencial.intValue();
    }

    public BooleanEnum getFluxoIndependente() {
        return fluxoIndependente;
    }

    public void setFluxoIndependente(BooleanEnum fluxoIndependente) {
        this.fluxoIndependente = fluxoIndependente;
    }
}