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
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;

/**
 *
 * @author Carlos Eduardo
 */
@CriteriaQuery(
        query = "SELECT NEW br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoFaseRetornoWrapper(p) FROM PAFluxoFaseRetorno p",
        selectCount = "SELECT COUNT(p.id) "
)
public class PAFluxoFaseRetornoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder{
    
    @Argument(id="id", name="p.id")
    private Long id;
    
    @Argument(id="fluxoAtual", name="p.fluxoAtual")
    private PAFluxoFase fluxoAtual;
    
    @Argument(id="fluxoRetorno", name="p.fluxoRetorno")
    private PAFluxoFase fluxoRetorno;
    
    @Argument(id="ativo", name="p.ativo")
    private AtivoEnum ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PAFluxoFase getFluxoAtual() {
        return fluxoAtual;
    }

    public void setFluxoAtual(PAFluxoFase fluxoAtual) {
        this.fluxoAtual = fluxoAtual;
    }

    public PAFluxoFase getFluxoRetorno() {
        return fluxoRetorno;
    }

    public void setFluxoRetorno(PAFluxoFase fluxoRetorno) {
        this.fluxoRetorno = fluxoRetorno;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }
    
    
}
