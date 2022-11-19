package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
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
@Table(name = "TB_TCJ_PAD_MOVIMENTACAO_FASE")
@NamedQueries({
    @NamedQuery(name = "PAMovimentacaoFase.getMovimentacaoFasePorServico",
            query = "SELECT tcj From PAMovimentacaoFase tcj where tcj.servico = :p0 and tcj.ativo = :p1")
})
public class PAMovimentacaoFase extends BaseEntityAtivo implements Serializable {
    
    @Id
    @Column(name = "Tcj_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "Tcj_Servico")    
    private String servico;
    
    @Column(name = "Tcj_Indicativo_Consumo") 
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum indicativoConsumo;
    
    @Column(name = "Tcj_Indicativo_Entrega")
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum indicativoEntrega;
    
    @ManyToOne
    @JoinColumn(name = "Tcj_Etapa_Andamento", referencedColumnName = "Tcg_ID")
    private PAEtapaAndamento etapaAndamento;
    
    @ManyToOne
    @JoinColumn(name = "Tcj_Fluxo_Fase", referencedColumnName = "Tch_ID")
    private PAFluxoFase fluxoFase;
    
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

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public BooleanEnum getIndicativoConsumo() {
        return indicativoConsumo;
    }

    public void setIndicativoConsumo(BooleanEnum indicativoConsumo) {
        this.indicativoConsumo = indicativoConsumo;
    }

    public BooleanEnum getIndicativoEntrega() {
        return indicativoEntrega;
    }

    public void setIndicativoEntrega(BooleanEnum indicativoEntrega) {
        this.indicativoEntrega = indicativoEntrega;
    }

    public PAEtapaAndamento getEtapaAndamento() {
        return etapaAndamento;
    }

    public void setEtapaAndamento(PAEtapaAndamento etapaAndamento) {
        this.etapaAndamento = etapaAndamento;
    }

    public PAFluxoFase getFluxoFase() {
        return fluxoFase;
    }

    public void setFluxoFase(PAFluxoFase fluxoFase) {
        this.fluxoFase = fluxoFase;
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