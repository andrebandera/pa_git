package br.gov.ms.detran.processo.administrativo.entidade;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author yanko.campos
 */
@Entity
@Table(name = "TB_TEJ_PROCESSO_JUDICIAL")
@NamedQueries({
    @NamedQuery(
            name = "ProcessoJudicial.getAll",
            query = "SELECT tej FROM ProcessoJudicial tej ")
})
public class ProcessoJudicial extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tej_ID")
    private Long id;
    
    @Column(name = "Tej_ISN")
    private Integer isn;
    
    @ManyToOne
    @JoinColumn(name = "Tej_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;
    
    @Column(name = "Tej_Data_Cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;
    
    @Column(name = "Tej_Usuario_Cadastro")
    private Long usuarioCadastro;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public ProcessoJudicial() {
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

    public Integer getIsn() {
        return isn;
    }

    public void setIsn(Integer isn) {
        this.isn = isn;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Long getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(Long usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }
}