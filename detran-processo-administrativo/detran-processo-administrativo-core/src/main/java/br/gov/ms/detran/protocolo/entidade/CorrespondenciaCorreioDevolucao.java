package br.gov.ms.detran.protocolo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TBB_CORRESPONDENCIA_CORREIO_DEVOLUCAO")
@NamedQueries({
    @NamedQuery(
            name = "CorrespondenciaCorreioDevolucao.findAll",
            query = "SELECT a From CorrespondenciaCorreioDevolucao a order by a.motivo"
    ),
    @NamedQuery(
            name = "CorrespondenciaCorreioDevolucao.getCorrespondenciaCorreioDevolucaoPorCorrespondencia",
            query = "SELECT tbb "
                    + "FROM CorrespondenciaDevolvida tav "
                    + "INNER JOIN tav.motivoDevolucao tbb "
                    + "INNER JOIN tav.correspondencia tag "
                    + "WHERE tag.id = :p0 "),
    @NamedQuery(
            name = "CorrespondenciaCorreioDevolucao.getCorrespondenciaCorreioDevolucaoPorMotivoAtivo",
            query = "SELECT tbb FROM CorrespondenciaCorreioDevolucao tbb WHERE tbb.motivo = :p0 AND tbb.ativo = :p1 ")    
})
public class CorrespondenciaCorreioDevolucao extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tbb_ID")
    private Long id;
    
    @Column(name = "Tbb_Motivo")
    private Integer motivo;
    
    @Column(name = "Tbb_Descricao")
    private String descricao;
    
    @Column(name = "Ativo")
    private AtivoEnum ativo;

    public CorrespondenciaCorreioDevolucao() {
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

    public Integer getMotivo() {
        return motivo;
    }

    public void setMotivo(Integer motivo) {
        this.motivo = motivo;
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
}
