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
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;

/**
 *
 * @author Carlos Eduardo
 */
@CriteriaQuery(
        query = "SELECT NEW br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoAndamentoWrapper(p) FROM PAFluxoAndamento p",
        selectCount = "SELECT COUNT(p.id) "
)
public class PAFluxoAndamentoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(id="id", name="p.id")
    private Long id;
    
    @Argument(id="fluxoFase", name="p.fluxoFase")
    private PAFluxoFase fluxoFase;
    
    @Argument(id="fluxoProcesso", name="p.fluxoProcesso")
    private PAFluxoProcesso fluxoProcesso;
    
    @Argument(id="descricaoFluxoProcesso", name="p.fluxoProcesso.descricao", operand = Operand.CONTAINS)
    private String descricaoFluxoProcesso;
    
    @Argument(id="ativo", name="ativo")
    private AtivoEnum ativo;

    public PAFluxoAndamentoCriteria() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PAFluxoFase getFluxoFase() {
        return fluxoFase;
    }

    public void setFluxoFase(PAFluxoFase fluxoFase) {
        this.fluxoFase = fluxoFase;
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

    public String getDescricaoFluxoProcesso() {
        return descricaoFluxoProcesso;
    }

    public void setDescricaoFluxoProcesso(String descricaoFluxoProcesso) {
        this.descricaoFluxoProcesso = descricaoFluxoProcesso;
    }
}
