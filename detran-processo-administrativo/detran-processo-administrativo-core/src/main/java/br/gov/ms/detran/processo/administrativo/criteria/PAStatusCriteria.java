
package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.comum.util.querybuilder.Operand;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;

/**
 *
 * @author Carlos Eduardo
 */
@CriteriaQuery(query = "SELECT NEW br.gov.ms.detran.processo.administrativo.wrapper.PAStatusWrapper(p) FROM PAStatus p", selectCount = "SELECT COUNT(p.id) ")
public class PAStatusCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    private static final long serialVersionUID = 1L;

    @Argument(id = "id", name = "p.id")
    private Long id;
    
    @Argument(id="codigo", name = "p.codigo")
    private Integer codigo;
    
    @Argument(id="descricao", name = "p.descricao", operand = Operand.CONTAINS)
    private String descricao;

    @Argument(id = "ativo", name = "p.ativo")
    private AtivoEnum ativo;

    private PAAndamentoProcesso andamentoProcesso;
    
    public PAStatusCriteria(){
        this.sort.addSortItem("p.codigo");
    }
        
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public PAAndamentoProcesso getAndamentoProcesso() {
        return andamentoProcesso;
    }
}
