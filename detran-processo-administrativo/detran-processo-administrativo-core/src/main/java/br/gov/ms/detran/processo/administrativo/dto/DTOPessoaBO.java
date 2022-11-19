/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.dto;

import br.gov.ms.detran.comum.dto.ResponseDTO;
import br.gov.ms.detran.comum.dto.apo.PAPessoaDTO;
import static br.gov.ms.detran.comum.projeto.util.DetranWebUtils.applicationMessageException;
import static br.gov.ms.detran.comum.util.DetranCollectionUtil.ehNuloOuVazio;
import br.gov.ms.detran.comum.util.DtnRESTfulClient;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Christiano Carrilho.
 * @deprecated Esta classe será removida. Serviços DTO serão substituídos pelo serviços Intranet.
 * Utilizar {@link DTOIntraPAPessoa} ou {@link DTOIntraPJUPessoa}.
 */
public class DTOPessoaBO {

    private static final Logger LOGGER = Logger.getLogger(DTOPessoaBO.class);

    /**
     *
     * 
     * @param cpf
     * @param nomeCondutor
     * @param sexoEnumOrdinal
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     * @deprecated Utilizar {@link DTOIntraPAPessoa#gravarPessoa(java.lang.String, java.lang.String, java.lang.Integer)} 
     * ou {@link DTOIntraPJUPessoa#gravarPessoa(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)}.
     */
    public Long gravarPessoa(String cpf, String nomeCondutor, Integer sexoEnumOrdinal) throws AppException {

        if (cpf == null) {
            LOGGER.error("CPF inválido (NULO)");
            return null;
        }

        if (nomeCondutor == null) {
            LOGGER.error("Nome condutor inválido (NULO)");
            return null;
        }

        DtnRESTfulClient client = new DtnRESTfulClient();
        client
                .addHeaderParams("cpf", cpf)
                .addHeaderParams("nomeCondutor", nomeCondutor)
                .addHeaderParams("sexo", sexoEnumOrdinal)
                .addHeaderParams("Access-Control-Allow-Origin", "*/detran-processo-administrativo-web*, */detran-pa-instauracao-web*")
                .setContentType(MediaType.APPLICATION_JSON, "UTF-8")
                .setRequestMethod("PUT")
                .setResourcePath("/dtn-apo-dto/rs-dto/pessoas/gravarPessoaPA")
                .execute();

        if (client.isResponseCodeOK()) {

            ResponseDTO<PAPessoaDTO> dto = client.getResponseForObject(
                    ResponseDTO.class, PAPessoaDTO.class
            );

            if (!ehNuloOuVazio(dto.getEntity())) {
                return dto.getEntity().get(0).getNumeroDetran();
            }
        }

        LOGGER.error("Não foi possível gravar pessoa para CPF {0} - {1}", cpf, nomeCondutor);
        applicationMessageException("Não foi possível gravar pessoa para CPF {0} - {1}", null, cpf, nomeCondutor);

        return null;
    }

    /**
     * 
     * @param cpf
     * @param nomeCondutor
     * @param nomeMae
     * @param nomePai
     * @param dataNascimento
     * @param sexoEnumOrdinal
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     * @deprecated Utilizar {@link DTOIntraPAPessoa#gravarPessoa(java.lang.String, java.lang.String, java.lang.Integer)} 
     * ou {@link DTOIntraPJUPessoa#gravarPessoa(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)}.
     */
    public Long gravarPessoaCondutorPJU(String cpf, 
                                        String nomeCondutor,
                                        String nomeMae,
                                        String nomePai,
                                        String dataNascimento,
                                        Integer sexoEnumOrdinal) throws AppException {

        if (cpf == null) {
            LOGGER.error("CPF inválido (NULO)");
            return null;
        }

        if (nomeCondutor == null) {
            LOGGER.error("Nome condutor inválido (NULO)");
            return null;
        }

        DtnRESTfulClient client = new DtnRESTfulClient();
        client
                .addHeaderParams("cpf", cpf)
                .addHeaderParams("nomeCondutor", nomeCondutor)
                .addHeaderParams("nomeMae", nomeMae)
                .addHeaderParams("nomePai", nomePai)
                .addHeaderParams("dataNascimento", dataNascimento)
                .addHeaderParams("sexo", sexoEnumOrdinal)
                .addHeaderParams("Access-Control-Allow-Origin", "*/detran-processo-administrativo-web*, */detran-pa-instauracao-web*")
                .setContentType(MediaType.APPLICATION_JSON, "UTF-8")
                .setRequestMethod("PUT")
                .setResourcePath("/dtn-apo-dto/rs-dto/pessoas/gravarPessoaCondutorPJU")
                .execute();

        if (client.isResponseCodeOK()) {

            ResponseDTO<PAPessoaDTO> dto = client.getResponseForObject(
                    ResponseDTO.class, PAPessoaDTO.class
            );

            if (!ehNuloOuVazio(dto.getEntity())) {
                return dto.getEntity().get(0).getNumeroDetran();
            }
        }

        LOGGER.error("Não foi possível gravar pessoa para CPF {0} - {1}", cpf, nomeCondutor);
        applicationMessageException("Não foi possível gravar pessoa para CPF {0} - {1}", null, cpf, nomeCondutor);

        return null;
    }
}