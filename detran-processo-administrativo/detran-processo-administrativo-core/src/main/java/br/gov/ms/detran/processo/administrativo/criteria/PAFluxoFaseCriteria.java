package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.comum.util.querybuilder.Operand;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAPrioridadeFluxoAmparo;
import br.gov.ms.detran.processo.administrativo.enums.TipoAndamentoEnum;

/**
 *
 * @author Carlos Eduardo
 */
@CriteriaQuery(
        query = "SELECT NEW br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoFaseWrapper(p) FROM PAFluxoFase p",
        selectCount = "SELECT COUNT(p.id) "
)
public class PAFluxoFaseCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder{
    
    @Argument(id="id", name="p.id")
    private Long id;
    
    @Argument(id="andamentoProcesso", name="p.andamentoProcesso")
    private PAAndamentoProcesso andamentoProcesso;
    
    @Argument(id="prioridadeFluxoAmparo", name="p.prioridadeFluxoAmparo")
    private PAPrioridadeFluxoAmparo prioridadeFluxoAmparo;
    
    @Argument(id="prioridade", name="p.prioridade")
    private Integer prioridade;
    
    @Argument(id="ativo", name="p.ativo")
    private AtivoEnum ativo;
    
    @Argument(id="tipoAndamento", name="p.tipoAndamento")
    private TipoAndamentoEnum tipoAndamento;
    
    @Argument(id="descricaoRetorno", name="p.andamentoProcesso.descricao", operand = Operand.CONTAINS)
    private String descricaoRetorno;
    
    public PAFluxoFaseCriteria(){
        this.sort.addSortItem("p.prioridade");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PAAndamentoProcesso getAndamentoProcesso() {
        return andamentoProcesso;
    }

    public void setAndamentoProcesso(PAAndamentoProcesso andamentoProcesso) {
        this.andamentoProcesso = andamentoProcesso;
    }

    public PAPrioridadeFluxoAmparo getPrioridadeFluxoAmparo() {
        return prioridadeFluxoAmparo;
    }

    public void setPrioridadeFluxoAmparo(PAPrioridadeFluxoAmparo prioridadeFluxoAmparo) {
        this.prioridadeFluxoAmparo = prioridadeFluxoAmparo;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public TipoAndamentoEnum getTipoAndamento() {
        return tipoAndamento;
    }

    public void setTipoAndamento(TipoAndamentoEnum tipoAndamento) {
        this.tipoAndamento = tipoAndamento;
    }

    public String getDescricaoRetorno() {
        return descricaoRetorno;
    }

    public void setDescricaoRetorno(String descricaoRetorno) {
        this.descricaoRetorno = descricaoRetorno;
    }
    
    
}
