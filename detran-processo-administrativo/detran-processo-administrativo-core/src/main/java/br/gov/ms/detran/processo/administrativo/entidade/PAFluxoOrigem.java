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
@Table(name = "TB_TCL_PAD_FLUXO_ORIGEM")
@NamedQueries({
    @NamedQuery(
        name = "PAFluxoOrigem.getPAFluxoOrigemPorApoioOrigemInstauracaoParaProcessoJudicial",
        query = "SELECT tcl "
                + "FROM PAFluxoOrigem tcl "
                + "WHERE tcl.origemInstauracao.id = :p0 "
                + "AND tcl.ativo = :p1 "
                + "AND tcl.fluxoProcesso.ativo = :p1 "
                + "AND tcl.indiceFluxoInicial = :p2 "),
    @NamedQuery(
        name = "PAFluxoOrigem.getPAFluxoOrigemPorOrigemEFluxoProcesso",
        query = "SELECT tcl "
                + "FROM PAFluxoOrigem tcl "
                + "WHERE tcl.origemInstauracao.id = :p0 "
                + " AND tcl.fluxoProcesso.id = :p1 "
                + " AND tcl.ativo = :p2 "),
    @NamedQuery(
        name = "PAFluxoOrigem.getPAFluxoOrigemAtivoPorPAFluxoProcesso",
        query = "SELECT tcl FROM PAFluxoOrigem tcl WHERE tcl.fluxoProcesso.id =:p0 AND tcl.ativo = :p1"),
    @NamedQuery(
        name = "PAFluxoOrigem.getPAFluxoOrigemPorIDApoioOrigemInstauracao",
        query = "SELECT tcl FROM PAFluxoOrigem tcl WHERE tcl.origemInstauracao.id =:p0 AND tcl.ativo=:p1")
})
public class PAFluxoOrigem extends BaseEntityAtivo implements Serializable {
    
    @Id
    @Column(name = "Tcl_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  
    @ManyToOne
    @JoinColumn(name = "Tcl_Origem_Instauracao", referencedColumnName = "Tdh_ID")
    private ApoioOrigemInstauracao origemInstauracao;
    
    @ManyToOne
    @JoinColumn(name = "Tcl_Fluxo_Processo", referencedColumnName = "Tck_ID")
    private PAFluxoProcesso fluxoProcesso;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;
    
    @Column(name = "Tcl_Indice_Fluxo_Inicial")
    private Boolean indiceFluxoInicial;

    public PAFluxoOrigem() {
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
 
    public ApoioOrigemInstauracao getOrigemInstauracao() {
        return origemInstauracao;
    }

    public void setOrigemInstauracao(ApoioOrigemInstauracao origemInstauracao) {
        this.origemInstauracao = origemInstauracao;
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

    public Boolean getIndiceFluxoInicial() {
        return indiceFluxoInicial;
    }

    public void setIndiceFluxoInicial(Boolean indiceFluxoInicial) {
        this.indiceFluxoInicial = indiceFluxoInicial;
    }
}