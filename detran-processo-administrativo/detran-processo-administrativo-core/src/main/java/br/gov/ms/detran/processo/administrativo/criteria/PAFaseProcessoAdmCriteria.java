/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.comum.util.querybuilder.Operand;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;

/**
 *
 * @author Carlos
 */
@CriteriaQuery(query = "SELECT NEW br.gov.ms.detran.processo.administrativo.wrapper.PAFaseProcessoAdmWrapper(p) FROM PAFaseProcessoAdm p", selectCount = "SELECT COUNT(p.id) ")
public class PAFaseProcessoAdmCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    private static final long serialVersionUID = 1L;

    @Argument(id = "id", name = "p.id")
    private Long id;
    
    @Argument(id="codigo", name = "p.codigo")
    private String codigo;
    
    @Argument(id="descricao", name = "p.descricao", operand = Operand.CONTAINS)
    private String descricao;

    @Argument(id = "ativo", name = "p.ativo")
    private AtivoEnum ativo;
    
    private PAFluxoProcesso fluxoProcesso;

    public PAFaseProcessoAdmCriteria(){
        this.sort.addSortItem("p.codigo");
    }
        
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
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

    public PAFluxoProcesso getFluxoProcesso() {
        return fluxoProcesso;
    }
    
    
}
