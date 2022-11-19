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
@Table(name = "TB_TCO_PAD_FLUXO_FASE_RETORNO")
@BusinessLogicalExclusion
@NamedQueries({
    @NamedQuery(
        name = "PAFluxoFaseRetorno.getPAFluxoFaseRetornoPorFluxoFaseAtualEAndamento",
        query = "SELECT tco "
                + "FROM PAFluxoFaseRetorno tco "
                + "INNER JOIN tco.fluxoAtual tch "
                + "INNER JOIN tco.fluxoRetorno tchretorno "
                + "INNER JOIN tchretorno.andamentoProcesso tcb "
                + "INNER JOIN tchretorno.prioridadeFluxoAmparo tci "
                + "INNER JOIN tci.fluxoProcesso tck "
                + "WHERE tch.id = :p0 "
                + "AND tcb.codigo = :p1 "
                + "AND tck.codigo = :p2 "),
    @NamedQuery(
        name = "PAFluxoFaseRetorno.getPAFluxoFaseRetornoPorFluxoFaseAtual",
        query = "SELECT tco FROM PAFluxoFaseRetorno tco INNER JOIN tco.fluxoAtual tch WHERE tch.id = :p0 "),
    @NamedQuery(
        name = "PAFluxoFaseRetorno.getPAFluxoFaseRetornoPorPAFluxoFaseVinculos",
        query = "SELECT g FROM PAFluxoFaseRetorno g WHERE g.fluxoAtual.id=:p0 AND g.ativo=:p1")
})
public class PAFluxoFaseRetorno extends BaseEntityAtivo implements Serializable {
    
    @Id
    @Column(name = "Tco_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JoinColumn(name = "Tco_Fluxo_Atual", referencedColumnName = "Tch_ID")
    @ManyToOne
    private PAFluxoFase fluxoAtual;
    
    @JoinColumn(name = "Tco_Fluxo_Retorno", referencedColumnName = "Tch_ID")
    @ManyToOne
    private PAFluxoFase fluxoRetorno;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public PAFluxoFaseRetorno() {
    }

    public Long getId() {
        return id;
    }
    
    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public void setId(Serializable id) {
        this.id = (id != null ? Long.valueOf(id.toString()) : null);
    }

    public PAFluxoFase getFluxoAtual() {
        return fluxoAtual;
    }

    public void setFluxoAtual(PAFluxoFase fluxoAtual) {
        this.fluxoAtual = fluxoAtual;
    }

    public PAFluxoFase getFluxoRetorno() {
        return fluxoRetorno;
    }

    public void setFluxoRetorno(PAFluxoFase fluxoRetorno) {
        this.fluxoRetorno = fluxoRetorno;
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