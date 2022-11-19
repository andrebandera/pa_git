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
@Table(name = "TB_TDV_PROCESSO_ADM_APENSADO")
@NamedQueries({
    @NamedQuery(
        name = "ProcessoAdministrativoApensado.getProcessoAdministrativoOrigemPorApensado",
        query = "SELECT new ProcessoAdministrativoApensado( "
                + " tdv.id, "
                + " tdv.processoAdministrativo.id, "
                + " tdv.processoAdministrativo.numeroProcesso) "
                + "FROM ProcessoAdministrativoApensado tdv "
                + "WHERE tdv.processoAdministrativoCassacao.processoAdministrativo.id = :p0 "
                + " AND tdv.processoAdministrativoCassacao.processoAdministrativo.ativo = :p1 "),
    @NamedQuery(
            name = "ProcessoAdministrativoApensado.getApensadoPorPAOriginal",
            query = "SELECT tdv "
                    + "FROM ProcessoAdministrativoApensado tdv "
                    + "inner join tdv.processoAdministrativoCassacao tdu "
                    + "where tdv.processoAdministrativo.id = :p0"
                    + "     AND EXISTS(SELECT 1 "
                    + "                   FROM PAOcorrenciaStatus tde "
                    + "                     INNER JOIN tde.statusAndamento tcd "
                    + "                     INNER JOIN tcd.status tca "
                    + "                   WHERE tde.processoAdministrativo.id = tdu.processoAdministrativo.id "
                    + "                     AND tca.codigo not in (:p1) "
                    + "                     AND tca.ativo = :p2 "
                    + "                     AND tde.ativo = :p2 "
                    + "                     AND tcd.ativo = :p2 ) ")
})
public class ProcessoAdministrativoApensado extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdv_ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tdv_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;
    
    @ManyToOne
    @JoinColumn(name = "Tdv_Processo_Adm_Cassado", referencedColumnName = "Tdu_ID")
    private ProcessoAdministrativoCassacao processoAdministrativoCassacao;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public ProcessoAdministrativoApensado() {
    }

    public ProcessoAdministrativoApensado(
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

    public ProcessoAdministrativoCassacao getProcessoAdministrativoCassacao() {
        return processoAdministrativoCassacao;
    }

    public void setProcessoAdministrativoCassacao(ProcessoAdministrativoCassacao processoAdministrativoCassacao) {
        this.processoAdministrativoCassacao = processoAdministrativoCassacao;
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