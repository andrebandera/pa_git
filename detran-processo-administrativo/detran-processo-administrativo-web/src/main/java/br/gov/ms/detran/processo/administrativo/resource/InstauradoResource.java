package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * REST Web Service
 *
 * @author desenvolvimento
 */
@ManagedBean
@Path("/pa/instaurados")
public class InstauradoResource extends PaResource {

    private static final Logger LOG = Logger.getLogger(InstauradoResource.class);

    /**
     * http://localhost:8080/detran-processo-administrativo/pa/instaurados/pendentes
     * 
     * @param request
     * @param context
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    @PUT
    @Path("pendentes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pendentes(@Context HttpServletRequest request, @Context SecurityContext context) throws AppException {
        
        try {
            
            return Response.ok(getProcessoAdministrativoService().getInstauradosJson()).build();
            
        } catch (Exception e) {
            LOG.debug("Erro capturado.", e);
            getControleFalha().gravarFalha(e, "Erro ao recuperar PAs pendentes - WS");
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}