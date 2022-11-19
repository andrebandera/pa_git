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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TAV_CORRESPONDENCIA_DEVOLVIDA")
public class CorrespondenciaDevolvida extends BaseEntityAtivo implements Serializable {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tav_ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tav_Correspondencia", referencedColumnName = "Tag_ID")
    private Correspondencia correspondencia;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "Tav_Data_Devolucao")
    private Date dataDevolucao;
    
    @ManyToOne
    @JoinColumn(name = "Tav_Motivo_Devolucao", referencedColumnName = "Tbb_ID")
    private CorrespondenciaCorreioDevolucao motivoDevolucao;
    
    @Column(name = "Tav_Observacao")
    private String observacao;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public CorrespondenciaDevolvida() {
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

    public Correspondencia getCorrespondencia() {
        return correspondencia;
    }

    public void setCorrespondencia(Correspondencia correspondencia) {
        this.correspondencia = correspondencia;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public CorrespondenciaCorreioDevolucao getMotivoDevolucao() {
        return motivoDevolucao;
    }

    public void setMotivoDevolucao(CorrespondenciaCorreioDevolucao motivoDevolucao) {
        this.motivoDevolucao = motivoDevolucao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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