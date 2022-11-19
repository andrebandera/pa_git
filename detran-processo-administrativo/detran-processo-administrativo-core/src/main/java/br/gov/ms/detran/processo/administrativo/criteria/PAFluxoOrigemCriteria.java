
package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;

/**
 *
 * @author Carlos Eduardo
 */
@CriteriaQuery(
        query = "SELECT NEW br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoOrigemWrapper(p) FROM PAFluxoOrigem p",
        selectCount = "SELECT COUNT(p.id) "
)
public class PAFluxoOrigemCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(id="id", name="p.id")
    private Long id;
    
    @Argument(id="origemInstauracao", name="p.origemInstauracao")
    private ApoioOrigemInstauracao origemInstauracao;
    
    @Argument(id="fluxoProcesso", name="p.fluxoProcesso")
    private PAFluxoProcesso fluxoProcesso;
    
    @Argument(id="ativo", name="p.ativo")
    private AtivoEnum ativo;
    
    public PAFluxoOrigemCriteria(){
        this.sort.addSortItem("p.fluxoProcesso");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }
    
    
}
