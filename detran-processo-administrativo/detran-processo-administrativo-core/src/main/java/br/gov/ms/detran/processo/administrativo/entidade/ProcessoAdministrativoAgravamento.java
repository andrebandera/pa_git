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
@Table(name = "TB_TDX_PROCESSO_ADM_AGRAVAMENTO")
@NamedQueries({
    @NamedQuery(
        name = "ProcessoAdministrativoAgravamento.getPorProcessoAdministrativoAgravado",
        query = "SELECT tdx "
                + "FROM ProcessoAdministrativoAgravamento tdx "
                + "WHERE tdx.processoAdministrativoAgravado.processoAdministrativo.id = :p0 "
                + "AND tdx.processoAdministrativoAgravado.ativo = :p1 "),
    @NamedQuery(
        name = "ProcessoAdministrativoAgravamento.getPorProcessoAdministrativo",
        query = "SELECT tdx "
                + "FROM ProcessoAdministrativoAgravamento tdx "
                + "WHERE tdx.processoAdministrativo.id = :p0 "
                + "AND tdx.ativo = :p1 "),
    @NamedQuery(
        name = "ProcessoAdministrativoAgravamento.getProcessoAdministrativoOrigemPorAgravamento",
        query = "SELECT new ProcessoAdministrativoAgravamento( "
                + " tdx.id, "
                + " tdx.processoAdministrativo.id, "
                + " tdx.processoAdministrativo.numeroProcesso) "
                + "FROM ProcessoAdministrativoAgravamento tdx "
                + "WHERE tdx.processoAdministrativoAgravado.processoAdministrativo.id = :p0 "
                + " AND tdx.processoAdministrativoAgravado.processoAdministrativo.ativo = :p1 "
    )
})
public class ProcessoAdministrativoAgravamento extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdx_ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tdx_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;
    
    @ManyToOne
    @JoinColumn(name = "Tdx_Processo_Adm_Agravado", referencedColumnName = "Tdw_ID")
    private ProcessoAdministrativoAgravado processoAdministrativoAgravado;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public ProcessoAdministrativoAgravamento() {
    }

    public ProcessoAdministrativoAgravamento(
        Long id, 
        Long idProcessoAdministrativoOrigem, 
        String numeroProcessoAdministrativoOrigem) {
        
        this.id = id;
        
        this.processoAdministrativo
            = new ProcessoAdministrativo(
                idProcessoAdministrativoOrigem, 
                numeroProcessoAdministrativoOrigem
            );
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

    public ProcessoAdministrativoAgravado getProcessoAdministrativoAgravado() {
        return processoAdministrativoAgravado;
    }

    public void setProcessoAdministrativoAgravado(ProcessoAdministrativoAgravado processoAdministrativoAgravado) {
        this.processoAdministrativoAgravado = processoAdministrativoAgravado;
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