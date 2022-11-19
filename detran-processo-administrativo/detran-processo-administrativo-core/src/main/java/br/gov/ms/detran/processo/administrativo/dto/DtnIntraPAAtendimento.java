/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.dto;

import br.gov.ms.detran.comum.projeto.intranet.DtnIntranetConnector;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Christiano Carrilho.
 */
public class DtnIntraPAAtendimento extends DtnIntranetConnector {

    private static final Logger LOGGER = Logger.getLogger(DtnIntraPAAtendimento.class);

    public Long gravarAtendimento(HttpServletRequest request, Long numeroDetran) throws AppException {

        return getDetranWebIntranetConnector(request)
                .resourcePath("/detran/dtnintraweb/apo/atd/gravarAtdPA")
                .rsParams("numeroDetran", numeroDetran)
                .httpMethod("POST")
                .connect()
                .getResponseForObject(Long.class);
    }

    public Long gravarAtendimento(Long numeroDetran) throws AppException {
        return gravarAtendimento(null, numeroDetran);
    }
}