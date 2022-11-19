/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

public enum ProcessoAdministrativoInfracaoSituacaoEnum {
    
    CADASTRADO,
    PENDENTE,
    CONFIRMADO
    ;
    
    @Override
    public String toString() {
        return DetranWebUtils.getMessageByKey(getClass().getSimpleName() + "." + this.name());
    }
}