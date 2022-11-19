package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
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
@Table(name = "TB_TCA_PAD_STATUS")
@NamedQueries({
        @NamedQuery(
            name = "PAStatus.PAStatusAtivoByCodigo",
            query = "SELECT pa FROM PAStatus pa WHERE pa.codigo like :p0 AND pa.ativo = :p1"),
        @NamedQuery(
            name = "PAStatus.PAStatusAtivoById",
            query = "SELECT pa FROM PAStatus pa WHERE pa.id like :p0 AND pa.ativo = :p1"),
        @NamedQuery(
            name = "PAStatus.PAStatusByDescricao",
            query = "SELECT pa FROM PAStatus pa WHERE UPPER(pa.descricao) = :p0"),
        @NamedQuery(name = "PAStatus.getPAStatusNaoUtilizadoPeloAndamentoPA", 
            query = "SELECT e FROM PAStatus e WHERE e.id NOT IN "
                    + "(SELECT ma.id FROM PAStatusAndamento m JOIN "
                    + "m.andamentoProcesso s JOIN m.status ma WHERE s.id = :p0 AND m.ativo = :p2) "
                    + "AND (:p1 IS NULL OR e.descricao LIKE :p1 ) "
                    + "AND e.ativo=:p2 "
                    + "ORDER BY e.descricao "),
        @NamedQuery(name = "PAStatus.getCountPAStatusNaoUtilizadoPeloAndamentoPA", 
            query = "SELECT COUNT(e) FROM PAStatus e WHERE e.id NOT IN "
                    + "(SELECT ma.id FROM PAStatusAndamento m JOIN "
                    + "m.andamentoProcesso s JOIN m.status ma WHERE s.id = :p0 AND m.ativo = :p2) "
                    + "AND (:p1 IS NULL OR e.descricao LIKE :p1 ) "
                    + "AND e.ativo=:p2 ")
})

public class PAStatus extends BaseEntityAtivo implements Serializable, IJSPASequencial {
    
    @Id
    @Column(name = "Tca_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "Tca_Codigo")
    private Integer codigo;
    
    @Column(name = "Tca_ISN_Migracao")
    private Integer isnMigracao;
    
    @Column(name = "Tca_Descricao")
    private String descricao;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public PAStatus() {
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

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getIsnMigracao() {
        return isnMigracao;
    }

    public void setIsnMigracao(Integer isnMigracao) {
        this.isnMigracao = isnMigracao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
}