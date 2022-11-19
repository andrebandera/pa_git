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
@Table(name = "TB_TEC_PAD_MOVIMENTACAO_INFRACAO_PA")
@NamedQueries({
    @NamedQuery(
            name = "MovimentacaoInfracaoPA.getInfracoesMovimentacaoParaReativar",
            query = "SELECT tec.processoAdministrativoInfracao "
                    + " FROM MovimentacaoInfracaoPA tec "
                    + " WHERE tec.movimentacao.processoAdministrativo.id = :p0  "
                    + "     and tec.indicativoReativarPontuacao = :p1"
                    + "     and tec.processoAdministrativoInfracao.ativo = :p2 "
    ),
    @NamedQuery(
            name = "MovimentacaoInfracaoPA.findall",
            query = "SELECT tec FROM MovimentacaoInfracaoPA tec"),
    @NamedQuery(
                name = "MovimentacaoInfracaoPA.getMovimentacaoInfracaoPAPorMovimentacaoEProcessoAdministrativo",
                query = "SELECT tec FROM MovimentacaoInfracaoPA tec " +
                        "where tec.movimentacao.id = :p0 " +
                        "and tec.movimentacao.processoAdministrativo.id = :p1 " +
                        "and tec.ativo = :p2 " +
                        "order by tec.id desc")
})
public class MovimentacaoInfracaoPA extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tec_ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tec_Movimentacao", referencedColumnName = "Teb_ID")
    private Movimentacao movimentacao;
    
    @ManyToOne
    @JoinColumn(name = "Tec_Infracao_PA", referencedColumnName = "Tdd_ID")
    private ProcessoAdministrativoInfracao processoAdministrativoInfracao;
    
    @Column(name = "Tec_Indicativo_Reativar_Pontuacao")
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum indicativoReativarPontuacao;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public MovimentacaoInfracaoPA() {
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

    public Movimentacao getMovimentacao() {
        return movimentacao;
    }

    public void setMovimentacao(Movimentacao movimentacao) {
        this.movimentacao = movimentacao;
    }

    public ProcessoAdministrativoInfracao getProcessoAdministrativoInfracao() {
        return processoAdministrativoInfracao;
    }

    public void setProcessoAdministrativoInfracao(ProcessoAdministrativoInfracao processoAdministrativoInfracao) {
        this.processoAdministrativoInfracao = processoAdministrativoInfracao;
    }

    public BooleanEnum getIndicativoReativarPontuacao() {
        return indicativoReativarPontuacao;
    }

    public void setIndicativoReativarPontuacao(BooleanEnum indicativoReativarPontuacao) {
        this.indicativoReativarPontuacao = indicativoReativarPontuacao;
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