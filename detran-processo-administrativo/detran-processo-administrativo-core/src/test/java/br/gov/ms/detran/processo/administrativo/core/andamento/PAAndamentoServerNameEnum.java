/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.AmbienteUtil;

/**
 *
 * @author desenvolvimento
 */
public interface PAAndamentoServerNameEnum {
    
    String SERVIDOR_LOCALHOST       = "127.0.0.1";
    String SERVIDOR_DESENVOLVIMENTO = "10.9.23.171";
    String SERVIDOR_PRE_HOMOLOGACAO = "10.9.23.173";
    String SERVIDOR_HOMOLOGACAO     = "10.9.23.172";
    String SERVIDOR_PRE_PRODUCAO    = "10.9.23.174";
    

    public static String getServerName() {
        
        if(AmbienteUtil.isProducao()) {
            return SERVIDOR_PRE_PRODUCAO;
        }
        
        return SERVIDOR_LOCALHOST;
    }
}