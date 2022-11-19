/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhControle;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoCnhRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.Protocolo;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class MovimentoCnhBO {
    
    public void gravarMovimentoCnh(EntityManager em, 
                                   CnhControle controle, 
                                   ProcessoAdministrativo processoOriginal, 
                                   Protocolo protocolo,
                                   AcaoEntregaCnhEnum acao) throws AppException {

        MovimentoCnh movimento = new MovimentoCnh();

        movimento.setAtivo(AtivoEnum.ATIVO);
        movimento.setCnhControle(controle.getId());
        movimento.setProtocolo(protocolo);
        movimento.setAcao(acao);
        movimento.setProcessoOriginal(processoOriginal);
        
        new MovimentoCnhRepositorio().insert(em, movimento);

    }
}
