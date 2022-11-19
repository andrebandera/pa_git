/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.ms.detran.processo.administrativo.criteria;


import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.processo.administrativo.enums.TipoBuscaEnum;

/**
 *
 * @author Bruno Akiyama
 */
public class RelPrazoNotificacaoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder{
    
    private TipoBuscaEnum tipoBusca;

    public TipoBuscaEnum getTipoBusca() {
        return tipoBusca;
    }

    public void setTipoBusca(TipoBuscaEnum tipoBusca) {
        this.tipoBusca = tipoBusca;
    }


    
    
    
}
