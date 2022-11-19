package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TCR_PAD_FLUXO_ANDAMENTO")
@BusinessLogicalExclusion
@NamedQueries({
    @NamedQuery(
        name = "PAFluxoAndamento.getPaFluxoAndamentoPorPAFluxoFaseEPAFluxoProcesso",
        query = "SELECT tcr "
                + "FROM PAFluxoAndamento tcr "
                + "INNER JOIN tcr.fluxoFase tch "
                + "INNER JOIN tcr.fluxoProcesso tck "
                + "WHERE tch.id = :p0 "
                + "AND tck.codigo IN (:p1) "),
    @NamedQuery(
        name = "PAFluxoAndamento.getFluxoAndamentoPorPAEFluxoProcesso",
        query = "SELECT tcr "
                + "FROM PAFluxoAndamento tcr "
                + " INNER JOIN tcr.fluxoProcesso tck "
                + " INNER JOIN tcr.fluxoFase tch "
                + " INNER JOIN tch.andamentoProcesso tcb "
                + "WHERE EXISTS (SELECT 1 "
                + "                 FROM PAOcorrenciaStatus tde "
                + "                     INNER JOIN tde.statusAndamento tcd "
                + "                     INNER JOIN tde.processoAdministrativo tdc "
                + "                     INNER JOIN tcd.andamentoProcesso tcb1 "
                + "                 where tcb.id = tcb1.id "
                + "                 and exists(select 1 "
                + "                             from PAFluxoFase tch1 "
                + "                                 inner join tch1.prioridadeFluxoAmparo tci "
                + "                                 inner join tci.fluxoProcesso tck1 "
                + "                             where tde.fluxoProcesso.id = tck1.id "
                + "                                 and tch.id = tch1.id) "
                + "                 and tdc.id = :p0 "
                + "                 and tde.ativo = :p1) "
                + " AND tcr.ativo = :p1 "
                + " AND tck.ativo = :p1 "
                + " AND tch.ativo = :p1 "
                + " AND tcb.ativo = :p1 "
                + " AND EXISTS(SELECT 1 "
                + "             FROM PAPrioridadeFluxoAmparo tci "
                + "             WHERE tci.fluxoProcesso.id = tck.id "
                + "                 AND tci.ativo = :p1) "
                + " AND tck.codigo = :p2 "
    ),
    
    @NamedQuery(
        name = "PAFluxoAndamento.getPAFluxoAndamentoAtivoPorPAFluxoProcesso",
        query = "SELECT tcr FROM PAFluxoAndamento tcr WHERE tcr.fluxoProcesso.id =:p0 AND tcr.ativo = :p1"),
    @NamedQuery(
        name = "PAFluxoAndamento.getPAFluxoAndamentoPorPAFluxoFaseVinculos",
        query = "SELECT g FROM PAFluxoAndamento g WHERE g.fluxoFase.id=:p0 AND g.ativo=:p1")
})
public class PAFluxoAndamento extends BaseEntityAtivo implements Serializable {
    
    @Id
    @Column(name = "Tcr_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tcr_Fluxo_Fase", referencedColumnName = "Tch_ID")
    private PAFluxoFase fluxoFase;
    
    @ManyToOne
    @JoinColumn(name = "Tcr_Fluxo_Processo", referencedColumnName = "Tck_ID")
    private PAFluxoProcesso fluxoProcesso;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public PAFluxoAndamento() {
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

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public PAFluxoFase getFluxoFase() {
        return fluxoFase;
    }

    public void setFluxoFase(PAFluxoFase fluxoFase) {
        this.fluxoFase = fluxoFase;
    }

    public PAFluxoProcesso getFluxoProcesso() {
        return fluxoProcesso;
    }

    public void setFluxoProcesso(PAFluxoProcesso fluxoProcesso) {
        this.fluxoProcesso = fluxoProcesso;
    }
}