package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.ResultLong;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.anotacao.negocio.BusinessLogicalExclusion;
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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@BusinessLogicalExclusion
@Table(name = "TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO")
@NamedQueries({
    @NamedQuery(
        name = "PAPrioridadeFluxoAmparo.getPrioridadeFluxoAmparoAtivoPorFluxoProcesso",
        query = "SELECT tci "
                + " FROM PAPrioridadeFluxoAmparo tci "
                + " WHERE tci.fluxoProcesso.id = :p0 "
                + "     AND tci.ativo = :p1 ORDER BY tci.prioridade "
    ),
    
    @NamedQuery(
        name = "PAPrioridadeFluxoAmparo.getPAPrioridadeFluxoAmparoAtivoPorPAFaseProcessoAdm",
        query = "SELECT tcd FROM PAPrioridadeFluxoAmparo tcd WHERE tcd.faseProcessoAdm.id = :p0 AND tcd.ativo = :p1"),
    
    @NamedQuery(
        name = "PAPrioridadeFluxoAmparo.getPAPrioridadeFluxoAmparoPorPAFaseProcessoAdmEPrioridade",
        query = "SELECT tci FROM PAPrioridadeFluxoAmparo tci WHERE tci.faseProcessoAdm.id = :p0 AND tci.prioridade = :p1"),
        
    @NamedQuery(
        name = "PAPrioridadeFluxoAmparo.getPAPrioridadeFluxoAmparoAtivoPorPAFluxoProcessoo",
        query = "SELECT tcd FROM PAPrioridadeFluxoAmparo tcd WHERE tcd.fluxoProcesso.id = :p0 AND tcd.ativo = :p1"),
    
    @NamedQuery(
        name = "PAPrioridadeFluxoAmparo.getMaxOrdem",
        query = "select max(x.prioridade) from PAPrioridadeFluxoAmparo x where x.fluxoProcesso.id = :p0"),
    
    @NamedQuery(
        name = "PAPrioridadeFluxoAmparo.getFaseFluxoAmparoPorFasesFluxoAmparoEMaioresPrioridadesDESC",
        query = "SELECT g FROM PAPrioridadeFluxoAmparo g WHERE g.fluxoProcesso.id =:p0 AND ativo=:p2 AND g.prioridade >= :p1 ORDER BY g.prioridade DESC "),
    
    @NamedQuery(
        name = "PAPrioridadeFluxoAmparo.getFaseFluxoAmparoPorFasesFluxoAmparoEMaioresPrioridadesASC",
        query = "SELECT g FROM PAPrioridadeFluxoAmparo g WHERE g.fluxoProcesso.id =:p0 AND ativo=:p2 AND g.prioridade > :p1 ORDER BY g.prioridade ASC "),
    
    @NamedQuery(
        name = "PAPrioridadeFluxoAmparo.getPAPrioridadeFluxoAmparoPosterior",
        query = "SELECT g FROM PAPrioridadeFluxoAmparo g WHERE g.fluxoProcesso.id =:p0 AND g.prioridade = :p1 AND ativo = :p2"),
    
    @NamedQuery(
        name = "PAPrioridadeFluxoAmparo.alterarPrioridades",
        query = "UPDATE PAPrioridadeFluxoAmparo g SET g.prioridade =:p0 WHERE g.id =:p1"),
    
    @NamedQuery(
        name = "PAPrioridadeFluxoAmparo.getPAPrioridadeFluxoAmparoPorId",
        query = "SELECT g FROM PAPrioridadeFluxoAmparo g WHERE g.id =:p0 "),
    @NamedQuery(
        name = "PAPrioridadeFluxoAmparo.getPAPrioridadeFluxoAmparoPorIdAtivo",
        query = "SELECT g FROM PAPrioridadeFluxoAmparo g WHERE g.fluxoProcesso.id =:p0 AND ativo = :p1"),
    
    @NamedQuery(
        name = "PAPrioridadeFluxoAmparo.getFasesProcessosPorFasesProcessosEIntervaloDePrioridades",
        query = "SELECT g FROM PAPrioridadeFluxoAmparo g WHERE g.faseProcessoAdm.id =:p0 AND :p1 <= g.prioridade AND g.prioridade <= :p2 ORDER BY g.prioridade ")
        
})
    @NamedNativeQueries({
        @NamedNativeQuery(
        name = "PAPrioridadeFluxoAmparo.getUltimoPrioridadeDoFluxoDaFaseProcesso",
        query = "SELECT MAX(b.Tci_Prioridade) AS resultado " +
                "FROM TB_TCV_FASE_PROCESSO_ADM a " +
                "JOIN TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO b ON a.Tcv_ID = b.Tci_Fase_Processo_Adm " +
                "JOIN TB_TCK_PAD_FLUXO_PROCESSO c ON b.Tci_Fluxo_Processo = c.Tck_ID " +
                "WHERE a.Tck_ID = :p0", 
        resultClass = ResultLong.class),
    @NamedNativeQuery(name="PAPrioridadeFluxoAmparo.alterarOrdemPasso1", 
        query = "update TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO set Ativo = 0 "                   
                + " where Tci_ID = :p0",
        resultClass = PAPrioridadeFluxoAmparo.class),
    @NamedNativeQuery(name="PAPrioridadeFluxoAmparo.alterarOrdemPasso2", 
        query = "update TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO set Ativo = 0 "                   
                + " where  Tci_ID = :p1 and Tci_Prioridade = :p0",
        resultClass = PAPrioridadeFluxoAmparo.class),
    @NamedNativeQuery(name="PAPrioridadeFluxoAmparo.alterarOrdemPasso3", 
		query = "update TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO set Tci_Prioridade = :p0 "
				+ " where Tci_ID= :p1 and Tci_Prioridade = :p2",
		resultClass = PAPrioridadeFluxoAmparo.class),
    @NamedNativeQuery(name="PAPrioridadeFluxoAmparo.alterarOrdemPasso4", 
    	query = "update TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO set Tci_Prioridade = :p1  "
    			+ "where Tci_ID = :p0 ",
		resultClass = PAPrioridadeFluxoAmparo.class),
    @NamedNativeQuery(name="PAPrioridadeFluxoAmparo.alterarOrdemPasso5", 
    	query = "update TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO set Ativo = 1  "
    			+ "where Tci_ID = :p0 ",
		resultClass = PAPrioridadeFluxoAmparo.class),
    @NamedNativeQuery(name="PAPrioridadeFluxoAmparo.alterarOrdemPasso6", 
        query = "update TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO set Ativo = 1 "                   
                + " where Tci_ID = :p0 ",
        resultClass = PAPrioridadeFluxoAmparo.class)
    })
public class PAPrioridadeFluxoAmparo extends BaseEntityAtivo implements Serializable {
    
    @Id
    @Column(name = "Tci_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tci_Fase_Processo_Adm", referencedColumnName = "Tcv_ID")
    private PAFaseProcessoAdm faseProcessoAdm;
    
    @Column(name = "Tci_Prioridade")    
    private Integer prioridade;
    
    @ManyToOne
    @JoinColumn(name = "Tci_Fluxo_Processo", referencedColumnName = "Tck_ID")
    private PAFluxoProcesso fluxoProcesso;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

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

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public PAFaseProcessoAdm getFaseProcessoAdm() {
        return faseProcessoAdm;
    }

    public void setFaseProcessoAdm(PAFaseProcessoAdm faseProcessoAdm) {
        this.faseProcessoAdm = faseProcessoAdm;
    }

    public PAFluxoProcesso getFluxoProcesso() {
        return fluxoProcesso;
    }

    public void setFluxoProcesso(PAFluxoProcesso fluxoProcesso) {
        this.fluxoProcesso = fluxoProcesso;
    }
}