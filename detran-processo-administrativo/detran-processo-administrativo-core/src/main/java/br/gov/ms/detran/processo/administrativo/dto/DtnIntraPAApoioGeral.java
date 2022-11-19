/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.dto;

import br.gov.ms.detran.comum.entidadesistema.wrapper.inf.OrgaoAutuadorIntraWrapper;
import br.gov.ms.detran.comum.intranet.DtnComumIntraApoioGeral;
import br.gov.ms.detran.comum.projeto.intranet.DtnIntranetConnector;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;

/**
 *
 * @author Christiano Carrilho.
 */
public class DtnIntraPAApoioGeral extends DtnIntranetConnector {

    public static final Logger LOGGER = Logger.getLogger(DtnIntraPAApoioGeral.class);

    /**
     * 
     * @param codigoAutuador
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public Long getIdAutuadorPeloCodigo(Integer codigoAutuador) throws AppException {

        DtnComumIntraApoioGeral intra = new DtnComumIntraApoioGeral();
        OrgaoAutuadorIntraWrapper orgao = intra.getOrgaoAutuadorPeloCodigo(codigoAutuador);

        return orgao.getId();
    }

    /**
     * 
     * @param acaoInfracaoPenalidadeId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public String getDescricaoInfracao(Long acaoInfracaoPenalidadeId) throws AppException {

        DtnComumIntraApoioGeral intra = new DtnComumIntraApoioGeral();
        return intra.getDescricaoInfracao(acaoInfracaoPenalidadeId);
    }

    public String getTextoPadraoNotificacaoPorTipoCorpoTexto(Long codigoTipoCorpoTexto) throws AppException {

        String corpoTexto = null;

        if(codigoTipoCorpoTexto == null){
            DetranWebUtils.applicationMessageException("severity.error.criteria.parametroinvalido2");
        }

        try {

            corpoTexto = (String) getDetranWebIntranetConnector(null)
                    .resourcePath("/detran/dtnintraweb/apo/geral/getTextoPadraoNotificacaoPorTipoCorpoTexto")
                    .rsParams("codigoTipoCorpoTexto", codigoTipoCorpoTexto)
                    .connect()
                    .getResponse();

        } catch (AppException e) {
            throw e;
        } catch (Exception ex) {
            LOGGER.error("Erro inesperado ao executar serviço intranet", ex);
            DetranWebUtils.applicationMessageException("Não foi possível definir o corpo de texto.");
        }

        return corpoTexto;
    }
}