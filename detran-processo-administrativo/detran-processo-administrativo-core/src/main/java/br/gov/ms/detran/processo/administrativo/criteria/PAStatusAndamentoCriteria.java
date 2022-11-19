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
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatus;

/**
 *
 * @author Carlos Eduardo
 */
@CriteriaQuery(
        query = "SELECT NEW br.gov.ms.detran.processo.administrativo.wrapper.PAStatusAndamentoWrapper(p) FROM PAStatusAndamento p",
        selectCount = "SELECT COUNT(p.id) "
)
public class PAStatusAndamentoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(id="id", name="p.id")
    private Long id;
    
    @Argument(id="status", name="p.status")
    private PAStatus status;
    
    @Argument(id="andamentoProcesso", name="p.andamentoProcesso")
    private PAAndamentoProcesso andamentoProcesso;
    
    @Argument(id="ativo", name="ativo")
    private AtivoEnum ativo;
    
    public PAStatusAndamentoCriteria(){
        this.sort.addSortItem("p.id");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PAStatus getStatus() {
        return status;
    }

    public void setStatus(PAStatus status) {
        this.status = status;
    }

    public PAAndamentoProcesso getAndamentoProcesso() {
        return andamentoProcesso;
    }

    public void setAndamentoProcesso(PAAndamentoProcesso andamentoProcesso) {
        this.andamentoProcesso = andamentoProcesso;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }
    
    
}
