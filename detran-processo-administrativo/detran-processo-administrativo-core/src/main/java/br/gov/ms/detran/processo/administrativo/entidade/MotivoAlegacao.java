package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
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
@Table(name = "TB_TCQ_PAD_MOTIVO_ALEGACAO")
@NamedQueries({
    @NamedQuery(
            name = "MotivoAlegacao.getListaMotivoAlegacao",
            query = "SELECT tcq From MotivoAlegacao tcq where tcq.ativo = :p0 "),
    @NamedQuery(
            name = "MotivoAlegacao.getMotivoPorCodigo",
            query = "SELECT tcq From MotivoAlegacao tcq where tcq.codigo = :p0 and tcq.ativo = :p1 ")
})
public class MotivoAlegacao extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tcq_Id")
    private Long id;
    
    @Column(name = "Tcq_Amparo_Legal")
    private Long amparoLegal;
    
    @Column(name = "Tcq_Codigo")
    private Integer codigo;
    
    @Column(name = "Tcq_Descricao")
    private String descricao;
    
    @Column(name = "Tcq_Indice_Equadramento_Legal")
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum indiceEnquadramentoLegal;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public MotivoAlegacao() {
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

    public Long getAmparoLegal() {
        return amparoLegal;
    }

    public void setAmparoLegal(Long amparoLegal) {
        this.amparoLegal = amparoLegal;
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

    public BooleanEnum getIndiceEnquadramentoLegal() {
        return indiceEnquadramentoLegal;
    }

    public void setIndiceEnquadramentoLegal(BooleanEnum indiceEnquadramentoLegal) {
        this.indiceEnquadramentoLegal = indiceEnquadramentoLegal;
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