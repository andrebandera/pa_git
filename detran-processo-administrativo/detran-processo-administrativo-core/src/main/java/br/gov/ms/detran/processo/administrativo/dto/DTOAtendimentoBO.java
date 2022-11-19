/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.dto;

import br.gov.ms.detran.comum.dto.ResponseDTO;
import br.gov.ms.detran.comum.dto.apo.PAAtendimentoDTO;
import static br.gov.ms.detran.comum.projeto.util.DetranWebUtils.applicationMessageException;
import static br.gov.ms.detran.comum.util.DetranCollectionUtil.ehNuloOuVazio;
import br.gov.ms.detran.comum.util.DtnRESTfulClient;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Christiano Carrilho.
 * @deprecated Esta classe será removida. Utilizar {@link DTOIntraAtendimento}.
 */
public class DTOAtendimentoBO {

    private static final Logger LOGGER = Logger.getLogger(DTOAtendimentoBO.class);

    /**
     * 
     * @param numeroDetran
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     * @deprecated Utilizar {@link DTOIntraAtendimento#gravarAtendimento(java.lang.Long)}.
     */
    public Long gravarAtendimento(Long numeroDetran) throws AppException {

        if (numeroDetran == null) {
            LOGGER.error("Número do DETRAN inválido (NULO)");
            return null;
        }

        DtnRESTfulClient client = new DtnRESTfulClient();
        client.
                addHeaderParams("numeroDetran", numeroDetran)
                .addHeaderParams("Access-Control-Allow-Origin", "*/detran-processo-administrativo-web*, */detran-pa-instauracao-web*")
                .setContentType(MediaType.APPLICATION_JSON, "UTF-8")
                .setRequestMethod("PUT")
                .setResourcePath("/dtn-apo-dto/rs-dto/atds/gravarAtdPA")
                .execute();

        if (client.isResponseCodeOK()) {

            ResponseDTO<PAAtendimentoDTO> dto = client.getResponseForObject(
                    ResponseDTO.class, PAAtendimentoDTO.class
            );

            if (!ehNuloOuVazio(dto.getEntity())) {
                return dto.getEntity().get(0).getAtendimentoId();
            }
        }

        LOGGER.error(
            "Não foi possível gravar atendimento para Número DETRAN {0}", numeroDetran
        );

        applicationMessageException(
            "Não foi possível gravar atendimento para Número DETRAN {0}", 
            null, new String[]{numeroDetran.toString()}
        );

        return null;
    }
}