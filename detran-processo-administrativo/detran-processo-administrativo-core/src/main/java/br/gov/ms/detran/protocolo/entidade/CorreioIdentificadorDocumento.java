package br.gov.ms.detran.protocolo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TAC_CORREIO_IDENTIFICADOR_DOCUMENTO")
public class CorreioIdentificadorDocumento extends BaseEntityAtivo implements Serializable {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tac_ID")
    private Long id;
    
    @Column(name = "Tac_Setor_Tipo_Correspondencia")
    private Integer setorTipoCorrespondencia;
    
    @Column(name = "Tac_Identificador")
    private String identificador;
    
    @Column(name = "Tac_Data_Inicio")
    @Temporal(TemporalType.DATE)
    private Date dataInicio;
        
    @Column(name = "Tac_Data_Termino")
    @Temporal(TemporalType.DATE)
    private Date dataTermino;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public CorreioIdentificadorDocumento() {
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

    public Integer getSetorTipoCorrespondencia() {
        return setorTipoCorrespondencia;
    }

    public void setSetorTipoCorrespondencia(Integer setorTipoCorrespondencia) {
        this.setorTipoCorrespondencia = setorTipoCorrespondencia;
    }
    
    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
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