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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TEH_PA_INICIO_FLUXO")
@NamedQueries({
    @NamedQuery(name = "PAInicioFluxo.getPAInicioFluxoAtivoPorProcessoAdministrativo",
            query = "SELECT teh From PAInicioFluxo teh INNER JOIN teh.processoAdministrativo tdc where tdc.id = :p0 and teh.ativo = :p1")
})
public class PAInicioFluxo extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Teh_ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Teh_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;

    @ManyToOne
    @JoinColumn(name = "Teh_Fluxo_Processo", referencedColumnName = "Tck_ID")
    private PAFluxoProcesso fluxoProcesso;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public PAInicioFluxo() {
    }

    public PAInicioFluxo(Long id) {
        this.id = id;
    }

    public PAInicioFluxo(ProcessoAdministrativo processoAdministrativo, PAFluxoProcesso fluxoProcesso) {
        this.processoAdministrativo = processoAdministrativo;
        this.fluxoProcesso = fluxoProcesso;
        this.ativo = AtivoEnum.ATIVO;
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

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public PAFluxoProcesso getFluxoProcesso() {
        return fluxoProcesso;
    }

    public void setFluxoProcesso(PAFluxoProcesso fluxoProcesso) {
        this.fluxoProcesso = fluxoProcesso;
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
