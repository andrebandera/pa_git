package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
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
@Table(name = "TB_TCV_FASE_PROCESSO_ADM")
@NamedQueries({
        @NamedQuery(
            name = "PAFaseProcessoAdm.PAFaseProcessoAdmAtivoByCodigo",
            query = "SELECT pa FROM PAFaseProcessoAdm pa WHERE pa.codigo like :p0 AND pa.ativo = :p1"),
        @NamedQuery(
            name = "PAFaseProcessoAdm.PAFaseProcessoAdmByDescricao",
            query = "SELECT pa FROM PAFaseProcessoAdm pa WHERE UPPER(pa.descricao) = :p0"),
        @NamedQuery(name = "PAFaseProcessoAdm.getFluxoNaoUtilizadoPeloFluxoProcesso", 
            query = "SELECT e FROM PAFaseProcessoAdm e WHERE e.id NOT IN "
                    + "(SELECT ma.id "
                    + "     FROM PAPrioridadeFluxoAmparo m "
                    + "     JOIN m.fluxoProcesso s "
                    + "     JOIN m.faseProcessoAdm ma "
                    + "     WHERE s.id = :p0 AND m.ativo = :p2) "
                    + "AND (:p1 IS NULL OR e.descricao LIKE :p1 ) "
                    + "AND e.ativo=:p2 "
                    + "ORDER BY e.descricao "),
        @NamedQuery(name = "PAFaseProcessoAdm.getCountFluxoNaoUtilizadoPeloFluxoProcesso", 
            query = "SELECT COUNT(e.id) FROM PAFaseProcessoAdm e WHERE e.id NOT IN "
                    + "(SELECT ma.id "
                    + "     FROM PAPrioridadeFluxoAmparo m "
                    + "     JOIN m.fluxoProcesso s "
                    + "     JOIN m.faseProcessoAdm ma "
                    + "     WHERE s.id = :p0 AND m.ativo = :p2) "
                    + "AND (:p1 IS NULL OR e.descricao LIKE :p1 ) "
                    + "AND e.ativo=:p2 ")
})
public class PAFaseProcessoAdm extends BaseEntityAtivo implements Serializable {
    
    @Id
    @Column(name = "Tcv_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "Tcv_Descricao")
    private String descricao;
    
    @Column(name = "Tcv_Codigo")
    private String codigo;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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