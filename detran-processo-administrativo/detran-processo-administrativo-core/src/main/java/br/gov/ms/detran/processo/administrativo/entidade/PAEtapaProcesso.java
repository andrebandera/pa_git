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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TCF_PAD_ETAPA_PROCESSO")
public class PAEtapaProcesso extends BaseEntityAtivo implements Serializable {

    @Id
    @Column(name = "Tcf_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "Tcf_Codigo")
    private Integer codigo;
    
    @Column(name = "Tcf_Formulario_Etapa")
    private Integer formularioEtapa;
    
    @Column(name = "Tcf_Titulo_Etapa")
    private String titulo;
    
    @Column(name = "Tcf_Tipo_Etapa")
    private Integer tipoEtapa;
    
    @Column(name = "Tcf_Descricao_Etapa")
    private String descricao;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public PAEtapaProcesso() {
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

    public Integer getFormularioEtapa() {
        return formularioEtapa;
    }

    public void setFormularioEtapa(Integer formularioEtapa) {
        this.formularioEtapa = formularioEtapa;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTipoEtapa() {
        return tipoEtapa;
    }

    public void setTipoEtapa(Integer tipoEtapa) {
        this.tipoEtapa = tipoEtapa;
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