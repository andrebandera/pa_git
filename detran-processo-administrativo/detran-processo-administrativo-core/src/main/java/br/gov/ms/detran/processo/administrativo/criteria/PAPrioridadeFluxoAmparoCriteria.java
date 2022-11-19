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
import br.gov.ms.detran.processo.administrativo.entidade.PAFaseProcessoAdm;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;

/**
 *
 * @author Carlos
 */
@CriteriaQuery(
        query = "SELECT NEW br.gov.ms.detran.processo.administrativo.wrapper.PAPrioridadeFluxoAmparoWrapper(p) FROM PAPrioridadeFluxoAmparo p",
        selectCount = "SELECT COUNT(p.id) "
)
public class PAPrioridadeFluxoAmparoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(id="id", name="p.id")
    private Long id;
    
    @Argument(id="fluxoProcesso", name="p.fluxoProcesso")
    private PAFluxoProcesso fluxoProcesso;
    
    @Argument(id="faseProcessoAdm", name="p.faseProcessoAdm")
    private PAFaseProcessoAdm faseProcessoAdm;
    
    @Argument(id="prioridade", name="p.prioridade")
    private Integer prioridade;
    
    @Argument(id="ativo", name="ativo")
    private AtivoEnum ativo;
    
    public PAPrioridadeFluxoAmparoCriteria(){
        this.sort.addSortItem("p.prioridade");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PAFluxoProcesso getFluxoProcesso() {
        return fluxoProcesso;
    }

    public void setFluxoProcesso(PAFluxoProcesso fluxoProcesso) {
        this.fluxoProcesso = fluxoProcesso;
    }

    public PAFaseProcessoAdm getFaseProcessoAdm() {
        return faseProcessoAdm;
    }

    public void setFaseProcessoAdm(PAFaseProcessoAdm faseProcessoAdm) {
        this.faseProcessoAdm = faseProcessoAdm;
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
    
    
}
