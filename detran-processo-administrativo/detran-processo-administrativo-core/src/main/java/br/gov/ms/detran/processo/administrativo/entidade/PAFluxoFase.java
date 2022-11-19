package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.ResultLong;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.anotacao.negocio.BusinessLogicalExclusion;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoAndamentoEnum;
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
@Table(name = "TB_TCH_PAD_FLUXO_FASE")
@BusinessLogicalExclusion
@NamedQueries({
    @NamedQuery(
        name = "PAFluxoFase.getFluxoFasePorPrioridadeFluxoAmparoOrdenadoAscendentePorPrioridade",
        query = "SELECT tch "
                + "FROM PAFluxoFase tch "
                + "WHERE tch.prioridadeFluxoAmparo.id = :p0 "
                + " AND tch.ativo = :p1 "
                + "ORDER BY tch.prioridade "),
    @NamedQuery(
        name = "PAFluxoFase.getPAFluxoFasePorAndamentoProcessoEPrioridadeFluxoAmparo",
        query = "SELECT tch "
                + "FROM PAFluxoFase tch "
                + "INNER JOIN tch.prioridadeFluxoAmparo tci "
                + "WHERE tci.fluxoProcesso.id = :p0 "
                + " AND tch.andamentoProcesso.id = :p1 "
                + " AND tch.ativo = :p2 "
                + " AND tci.ativo = :p2 "),
    @NamedQuery(
        name = "PAFluxoFase.getFluxoFaseDoProcessoAdministrativo",
        query = "SELECT tch "
                + "FROM PAFluxoFase tch "
                + "INNER JOIN tch.prioridadeFluxoAmparo tci "
                + "INNER JOIN tci.fluxoProcesso tck "
                + "WHERE tch.andamentoProcesso.id = :p0 "
                + "AND tck.id = :p1 "
                + "AND tch.ativo = :p2 "
                + "AND tci.ativo = :p2 "
                + "AND tck.ativo = :p2 "),
    
    @NamedQuery(
        name = "PAFluxoFase.getPAFluxoFaseAtivoPorPAAndamentoProcesso",
        query = "SELECT tcd FROM PAFluxoFase tcd WHERE tcd.andamentoProcesso.id = :p0 AND tcd.ativo = :p1"),
    @NamedQuery(
        name = "PAFluxoFase.getMaxOrdem",
        query = "select max(x.prioridade) from PAFluxoFase x where x.prioridadeFluxoAmparo.id = :p0"),
    @NamedQuery(
        name = "PAFluxoFase.getPAFluxoFasePosterior",
        query = "SELECT g FROM PAFluxoFase g WHERE g.prioridadeFluxoAmparo.id =:p0 AND g.prioridade = :p1 AND g.ativo=:p2"),
    @NamedQuery(
        name = "PAFluxoFase.getPAFluxoFasePorId",
        query = "SELECT g FROM PAFluxoFase g WHERE g.id =:p0 "),
    @NamedQuery(
        name = "PAFluxoFase.getPAFluxoFasePorPrioridadeFluxoAmparoEMaioresPrioridadesDESC",
        query = "SELECT g FROM PAFluxoFase g WHERE g.prioridadeFluxoAmparo.id =:p0 AND g.ativo=:p2 AND g.prioridade >= :p1 ORDER BY g.prioridade DESC "),
    @NamedQuery(
        name = "PAFluxoFase.getPAFluxoFasePorPrioridadeFluxoAmparoEMaioresPrioridadesASC",
        query = "SELECT g FROM PAFluxoFase g WHERE g.prioridadeFluxoAmparo.id =:p0 AND g.ativo=:p2 AND g.prioridade > :p1 ORDER BY g.prioridade ASC "),
    @NamedQuery(
        name = "PAFluxoFase.getPAFluxoFasePorPrioridadeFluxoAmparoVinculos",
        query = "SELECT g FROM PAFluxoFase g WHERE g.prioridadeFluxoAmparo.id =:p0 AND g.ativo= :p1"),
    @NamedQuery(
        name = "PAFluxoFase.alterarPrioridades",
        query = "UPDATE PAFluxoFase g SET g.prioridade =:p0 WHERE g.id =:p1")
    })
    @NamedNativeQueries({
        @NamedNativeQuery(
        name = "PAFluxoFase.getUltimoPrioridadeDoFluxoDaFaseProcesso",
        query = "SELECT MAX(b.Tci_Prioridade) AS resultado " +
                "FROM TB_TCV_FASE_PROCESSO_ADM a " +
                "JOIN TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO b ON a.Tcv_ID = b.Tci_Fase_Processo_Adm " +
                "JOIN TB_TCK_PAD_FLUXO_PROCESSO c ON b.Tci_Fluxo_Processo = c.Tck_ID " +
                "WHERE a.Tck_ID = :p0", 
        resultClass = ResultLong.class),
    @NamedNativeQuery(name="PAFluxoFase.alterarOrdemPasso1", 
        query = "update TB_TCH_PAD_FLUXO_FASE set Ativo = 0 "                   
                + " where Tch_ID = :p0",
        resultClass = PAFluxoFase.class),
    @NamedNativeQuery(name="PAFluxoFase.alterarOrdemPasso2", 
        query = "update TB_TCH_PAD_FLUXO_FASE set Ativo = 0 "                   
                + " where  Tch_ID = :p1 and Tch_Prioridade = :p0",
        resultClass = PAFluxoFase.class),
    @NamedNativeQuery(name="PAFluxoFase.alterarOrdemPasso3", 
		query = "update TB_TCH_PAD_FLUXO_FASE set Tch_Prioridade = :p0 "
				+ " where Tch_ID= :p1 and Tch_Prioridade = :p2",
		resultClass = PAFluxoFase.class),
    @NamedNativeQuery(name="PAFluxoFase.alterarOrdemPasso4", 
    	query = "update TB_TCH_PAD_FLUXO_FASE set Tch_Prioridade = :p1  "
    			+ "where Tch_ID = :p0 ",
		resultClass = PAFluxoFase.class),
    @NamedNativeQuery(name="PAFluxoFase.alterarOrdemPasso5", 
    	query = "update TB_TCH_PAD_FLUXO_FASE set Ativo = 1  "
    			+ "where Tch_ID = :p0 ",
		resultClass = PAFluxoFase.class),
    @NamedNativeQuery(name="PAFluxoFase.alterarOrdemPasso6", 
        query = "update TB_TCH_PAD_FLUXO_FASE set Ativo = 1 "                   
                + " where Tch_ID = :p0 ",
        resultClass = PAFluxoFase.class)
    })
public class PAFluxoFase extends BaseEntityAtivo implements Serializable {
    
    @Id
    @Column(name = "Tch_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tch_Andamento", referencedColumnName = "Tcb_ID")
    private PAAndamentoProcesso andamentoProcesso;
    
    @ManyToOne
    @JoinColumn(name = "Tch_Prioridade_Fluxo_Amparo", referencedColumnName = "Tci_ID")
    private PAPrioridadeFluxoAmparo prioridadeFluxoAmparo;
    
    @Column(name = "Tch_Prioridade")    
    private Integer prioridade;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;
    
    @Column(name = "Tch_Tipo")
    @Enumerated(EnumType.STRING)
    private TipoAndamentoEnum tipoAndamento;

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

    public PAAndamentoProcesso getAndamentoProcesso() {
        return andamentoProcesso;
    }

    public void setAndamentoProcesso(PAAndamentoProcesso andamentoProcesso) {
        this.andamentoProcesso = andamentoProcesso;
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

    public PAPrioridadeFluxoAmparo getPrioridadeFluxoAmparo() {
        return prioridadeFluxoAmparo;
    }

    public void setPrioridadeFluxoAmparo(PAPrioridadeFluxoAmparo prioridadeFluxoAmparo) {
        this.prioridadeFluxoAmparo = prioridadeFluxoAmparo;
    }

    public TipoAndamentoEnum getTipoAndamento() {
        return tipoAndamento;
    }

    public void setTipoAndamento(TipoAndamentoEnum tipoAndamento) {
        this.tipoAndamento = tipoAndamento;
    }
}
