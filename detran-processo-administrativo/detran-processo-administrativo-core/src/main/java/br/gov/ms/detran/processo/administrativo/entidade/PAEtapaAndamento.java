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
@Table(name = "TB_TCG_PAD_ETAPA_ANDAMENTO")
@NamedQueries({
        @NamedQuery(
        name = "PAEtapaAndamento.getPAEtapaAndamentoAtivoPorPAAndamentoProcesso",
        query = "SELECT tcd FROM PAEtapaAndamento tcd WHERE tcd.andamentoProcesso.id = :p0 AND tcd.ativo = :p1")
})

public class PAEtapaAndamento extends BaseEntityAtivo implements Serializable {

    @Id
    @Column(name = "Tcg_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tcg_Etapa_Processo", referencedColumnName = "Tcf_ID")
    private PAEtapaProcesso etapaProcesso;
    
    @ManyToOne
    @JoinColumn(name = "Tcg_Andamento_Processo", referencedColumnName = "Tcb_ID")
    private PAAndamentoProcesso andamentoProcesso;
    
    @ManyToOne
    @JoinColumn(name = "Tcg_Opcao_Movimentacao", referencedColumnName = "Tcc_ID")
    private PAOpcaoMovimentacao opcaoMovimentacao;

    @Column(name = "Tcg_Origem_Plataforma")    
    private Integer origemPlataforma;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public PAEtapaAndamento() {
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

    public PAEtapaProcesso getEtapaProcesso() {
        return etapaProcesso;
    }

    public void setEtapaProcesso(PAEtapaProcesso etapaProcesso) {
        this.etapaProcesso = etapaProcesso;
    }

    public PAAndamentoProcesso getAndamentoProcesso() {
        return andamentoProcesso;
    }

    public void setAndamentoProcesso(PAAndamentoProcesso andamentoProcesso) {
        this.andamentoProcesso = andamentoProcesso;
    }

    public PAOpcaoMovimentacao getOpcaoMovimentacao() {
        return opcaoMovimentacao;
    }

    public void setOpcaoMovimentacao(PAOpcaoMovimentacao opcaoMovimentacao) {
        this.opcaoMovimentacao = opcaoMovimentacao;
    }

    public Integer getOrigemPlataforma() {
        return origemPlataforma;
    }

    public void setOrigemPlataforma(Integer origemPlataforma) {
        this.origemPlataforma = origemPlataforma;
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