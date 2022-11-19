package br.gov.ms.detran.protocolo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TAA_CORRESPONDENCIA_TIPO")
@NamedQueries({
        @NamedQuery(
                name = "CorrespondenciaTipo.getIdCorrespondenciaTipoPorCodigoAtivo",
                query = "SELECT taa FROM CorrespondenciaTipo taa " +
                        "where taa.codigo = :p0 " +
                        "and taa.ativo = :p1 " )
})
public class CorrespondenciaTipo extends BaseEntityAtivo implements Serializable {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Taa_ID")
    private Long id;
    
    @Column(name = "Taa_Codigo")
    private Integer codigo;
    
    @Column(name = "Taa_Descricao")
    private String descricao;
    
    @Column(name = "Taa_Visualizacao")
    private VisualizacaoEnum visualizacao;
    
    @Column(name = "Taa_Permite_Envio")
    private Boolean permiteEnvio;
        
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public CorrespondenciaTipo() {
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public VisualizacaoEnum getVisualizacao() {
        return visualizacao;
    }

    public void setVisualizacao(VisualizacaoEnum visualizacao) {
        this.visualizacao = visualizacao;
    }

    public Boolean getPermiteEnvio() {
        return permiteEnvio;
    }

    public void setPermiteEnvio(Boolean permiteEnvio) {
        this.permiteEnvio = permiteEnvio;
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