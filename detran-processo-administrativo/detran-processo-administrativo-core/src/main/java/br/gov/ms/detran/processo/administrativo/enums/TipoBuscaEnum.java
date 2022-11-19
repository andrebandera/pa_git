/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

/**
 *
 * @author Bruno Akiyama
 */
public enum TipoBuscaEnum {
    SEM_RECURSO(1),
    COM_RECURSO(2),
    PRAZO_ESGOTADO(3);
    
    private Integer valorTipoBusca;
    
    private TipoBuscaEnum(Integer valorTipoBusca) {
        this.valorTipoBusca = valorTipoBusca;
    }

    public Integer getValorTipoBusca() {
        return valorTipoBusca;
    }

    public void setValorTipoBusca(Integer valorTipoBusca) {
        this.valorTipoBusca = valorTipoBusca;
    }
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}
