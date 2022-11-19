/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.core.bo.ConsultaCondutorMainframeBO;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 *
 * @author desenvolvimento
 */
@Stateless(mappedName = "ejb/ConsultaCondutorMainframeService")
@Remote(IConsultaCondutorMainframeRemote.class)
public class ConsultaCondutorMainframeService implements IConsultaCondutorMainframeRemote {

    @Override
    public String getMotivoCassacaoCondutor(String cpf) throws AppException {
        return new ConsultaCondutorMainframeBO().getMotivoCassacao(cpf);
    }
}