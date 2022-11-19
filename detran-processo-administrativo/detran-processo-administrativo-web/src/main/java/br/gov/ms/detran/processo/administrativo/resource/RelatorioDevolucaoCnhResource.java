/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranHTTPUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.criteria.ControleCnhPACriteria;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.ControleCnhPAWrapper;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Lillydi
 */
@ManagedBean
@Path("relatoriodevolucaocnhs")
public class RelatorioDevolucaoCnhResource extends PaResource<MovimentoCnh, ControleCnhPACriteria> {

    private static final Logger LOG = Logger.getLogger(RelatorioDevolucaoCnhResource.class);
    
    @PUT
    @Path("emitir")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response emitir(
            @Context HttpServletRequest request, @Context SecurityContext context, ControleCnhPAWrapper wrapper) throws AppException {

        try {

                String urlBaseBirt = DetranReportsUtil.getReportsBaseBirtUrl(request, true);
            byte[] byteArquivo = DetranHTTPUtil
                .download(
                    urlBaseBirt 
                        + DetranReportsUtil.getReportParamsBirtUrl(
                            "relatorio_devolucao_cnh",
                            FormatoRelatorioEnum.PDF.getRptFormat(),
                            null,
                            "relatorios/processoadministrativo/consultas/"
                        )
                );

            view.setObjectResponse(byteArquivo);

            return getResponseOk(request);

        } catch (AppException e) {
            //LOG.error("Ocorreu um erro inesperado.", e);
            DetranWebUtils.addErrorMessage(e, view);
        }

        return getResponseOk(request);
    }
}
