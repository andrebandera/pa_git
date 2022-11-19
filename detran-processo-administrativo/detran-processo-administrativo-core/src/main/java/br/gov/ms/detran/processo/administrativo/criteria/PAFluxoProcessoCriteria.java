
package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.comum.util.querybuilder.Operand;

/**
 * @author Carlos
 */
@CriteriaQuery(query = "SELECT NEW br.gov.ms.detran.processo.administrativo.wrapper."
                       + "PAFluxoProcessoWrapper(p) FROM PAFluxoProcesso p", 
               selectCount = "SELECT COUNT(p.id) ")
public class PAFluxoProcessoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    private static final long serialVersionUID = 1L;
    
    @Argument(id = "id", name = "p.id")
    private Long id;
    
    @Argument(id="codigo", name = "p.codigo", operand = Operand.CONTAINS)
    private Integer codigo;
    
    @Argument(id="descricao", name = "p.descricao", operand = Operand.CONTAINS)
    private String descricao;
    
    @Argument(id="fluxoIndependente", name = "p.fluxoIndependente")
    private BooleanEnum fluxoIndependente;

    @Argument(id = "ativo", name = "p.ativo")
    private AtivoEnum ativo;
    
    private Integer codigoAndamento;
    
    private String descricaoAndamento;
  
    public PAFluxoProcessoCriteria(){
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

    public BooleanEnum getFluxoIndependente() {
        return fluxoIndependente;
    }

    public void setFluxoIndependente(BooleanEnum fluxoIndependente) {
        this.fluxoIndependente = fluxoIndependente;
    }

    public Integer getCodigoAndamento() {
        return codigoAndamento;
    }

    public void setCodigoAndamento(Integer codigoAndamento) {
        this.codigoAndamento = codigoAndamento;
    }

    public String getDescricaoAndamento() {
        return descricaoAndamento;
    }

    public void setDescricaoAndamento(String descricaoAndamento) {
        this.descricaoAndamento = descricaoAndamento;
    }
}