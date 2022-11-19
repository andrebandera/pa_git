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

/**
 *
 * @author desenvolvimento
 */
@CriteriaQuery(query = "SELECT r FROM PAServicoExterno r ", selectCount = "SELECT COUNT(r.id) ")
public class PAServicoExternoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(name = "r.id", id = "id")
    private Long id;
    
    @Argument(name = "r.nome", id = "nome", operand = Operand.CONTAINS)
    private String nome;
    
    @Argument(name = "r.ativo", id = "ativo")
    private AtivoEnum ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }
}
