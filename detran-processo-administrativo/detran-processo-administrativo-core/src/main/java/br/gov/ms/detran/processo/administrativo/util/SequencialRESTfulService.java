/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.util;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DtnRESTfulClient;
import br.gov.ms.detran.comum.util.exception.AppException;

import javax.ws.rs.core.MediaType;

/**
 *
 * @author Christiano Carrilho
 */
public class SequencialRESTfulService {

    public SequencialRESTfulService() {
    }

    /**
     * 
     * @return
     * @throws AppException 
     */
    public String getNumeroProcesso() throws AppException {

        DtnRESTfulClient client = new DtnRESTfulClient()
                .setRequestMethod("get")
                .addHeaderParams("Content-Type", MediaType.TEXT_PLAIN)
                .setResourcePath("detran-pa-instauracao-web/paresource/seqs/getNumeroProcesso");

        client.execute();

        if (client.getResponseCode() != 200) {
            DetranWebUtils.applicationMessageException("Não foi possível recuperar o número do processo!");
        }

        return client.getResponse();
    }

    /**
     * 
     * @return
     * @throws AppException 
     */
    public String getNumeroPortaria() throws AppException {

        DtnRESTfulClient client = new DtnRESTfulClient()
                .setRequestMethod("get")
                .addHeaderParams("Content-Type", MediaType.TEXT_PLAIN)
                .setResourcePath("detran-pa-instauracao-web/paresource/seqs/getNumeroPortaria");

        client.execute();

        if (client.getResponseCode() != 200) {
            DetranWebUtils.applicationMessageException("Não foi possível recuperar o número da portaria!");
        }

        return client.getResponse();
    }

    public String getNumeroProtocolo() throws AppException {

        DtnRESTfulClient client = new DtnRESTfulClient()
                .setRequestMethod("get")
                .addHeaderParams("Content-Type", MediaType.TEXT_PLAIN)
                .setResourcePath("detran-pa-instauracao-web/paresource/seqs/getNumeroProtocolo")
                ;

        client.execute();

        if (client.getResponseCode() != 200) {
            DetranWebUtils.applicationMessageException("Não foi possível recuperar o número do protocolo.");
        }

        return client.getResponse();
    }
}
