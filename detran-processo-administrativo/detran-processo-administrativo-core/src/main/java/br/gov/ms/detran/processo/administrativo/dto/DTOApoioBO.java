/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.dto;

import br.gov.ms.detran.comum.dto.ResponseDTO;
import br.gov.ms.detran.comum.dto.apo.PAAutuadorDTO;
import br.gov.ms.detran.comum.dto.apo.PADescricaoInfracaoDTO;
import static br.gov.ms.detran.comum.util.DetranCollectionUtil.ehNuloOuVazio;
import br.gov.ms.detran.comum.util.DtnRESTfulClient;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;

/**
 *
 * @author Christiano Carrilho.
 * @deprecated Esta classe será removida. Utilizar {@link DTOIntraApoioGeral}.
 */
public class DTOApoioBO {

    public static final Logger LOGGER = Logger.getLogger(DTOApoioBO.class);

    /**
     * 
     * @param codigoAutuador
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     * @deprecated Utilizar {@link DTOIntraApoioGeral#getIdAutuadorPeloCodigo(java.lang.Integer)}
     */
    public Long getIdAutuadorPeloCodigo(Integer codigoAutuador) throws AppException {

        if (codigoAutuador == null) {
            LOGGER.error("Código do autuador inválido (NULO)");
            return null;
        }

        DtnRESTfulClient client = new DtnRESTfulClient();
        client
                .addHeaderParams("codautuador", codigoAutuador)
                .addHeaderParams("Access-Control-Allow-Origin", "*/detran-processo-administrativo-web*, */detran-pa-instauracao-web*")
                .setContentType("application/json", "UTF-8")
                .setRequestMethod("PUT")
                .setResourcePath("/dtn-apo-dto/rs-dto/apo/getIdAutuadorPeloCodigo")
                .execute();

        Long autuadorId = null;

        if (client.isResponseCodeOK()) {
            ResponseDTO<PAAutuadorDTO> dto = client.getResponseForObject(ResponseDTO.class, PAAutuadorDTO.class);
            if (ehNuloOuVazio(dto.getEntity())) {
                autuadorId = dto.getEntity().get(0).getId();
            }
        }

        return autuadorId;
    }

    /**
     * 
     * @param acaoInfracaoPenalidadeId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     * @deprecated Utilizar {@link DTOIntraApoioGeral#getDescricaoInfracao(java.lang.Long)}
     */
    public String getDescricaoInfracao(Long acaoInfracaoPenalidadeId) throws AppException {

        DtnRESTfulClient client = new DtnRESTfulClient();
        client
                .addHeaderParams("id", acaoInfracaoPenalidadeId)
                .setContentType("application/json", "UTF-8")
                .setRequestMethod("PUT")
                .setResourcePath("/dtn-apo-dto/rs-dto/apo/getDescricaoInfracao")
                .execute();

        String descricao = null;

        if (client.isResponseCodeOK()) {

            ResponseDTO<PADescricaoInfracaoDTO> dto = client.getResponseForObject(
                    ResponseDTO.class, PADescricaoInfracaoDTO.class
            );

            if (ehNuloOuVazio(dto.getEntity())) {
                descricao = dto.getEntity().get(0).getDescricao();
            }
        }

        return descricao;
    }
}