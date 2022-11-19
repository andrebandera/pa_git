package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.EntregaCnhWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.InformacoesEntregaCnhWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * @author Lillydi
 */
@ManagedBean
@Path("/pa/entregacnh")
public class EntregaCnhResource extends PaResource {

    private static final Logger LOG = Logger.getLogger(EntregaCnhResource.class);

    @PUT
    @Path("confirmaentregacnh")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmaCnhEntregue(@Context HttpServletRequest request, EntregaCnhWrapper wrapper) throws AppException, IOException {

        LOG.debug("Início confirmar Recebimento de Entrega/Devolução de Cnh pelo BPMS.");

        try {

            validaDadosObrigatorios(wrapper);
            
            processoAdministrativoService.validarPAParaExecucaoServico(wrapper.getNumeroProcesso(), "confirmaentregacnh");
            
            new ExecucaoAndamentoManager().
                    iniciaExecucao(new ExecucaoAndamentoEspecificoWrapper(processoAdministrativoService.getProcessoAdministrativo(wrapper.getNumeroProcesso()), 
                                                                     null, 
                                                                     DetranReportsUtil.getReportsBaseBirtUrl(request, true),
                                                                     wrapper));
            
            return Response.ok().build();

        } catch (Exception e) {
            LOG.debug("Tratado", e);
            if(wrapper != null && !DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())){
                getControleFalha().gravarFalhaProcessoAdministrativo(e, 
                                                                     "Erro ao confirmar recebimento de entrega/devolução de cnh pelo bpms", 
                                                                     "SEM CPF",
                                                                     wrapper.getNumeroProcesso());
            }else{
                getControleFalha().gravarFalha(e, "Erro ao confirmar recebimento de entrega/devolução de cnh pelo bpms");
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private void validaDadosObrigatorios(EntregaCnhWrapper wrapper) throws AppException {
        if (wrapper == null || wrapper.getAcao() == null || DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())) {
            DetranWebUtils.applicationMessageException("Número Processo e Ação são obrigatórios");
        }
    }
    
    @PUT
    @Path("entregacnh")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response informacoesEntregaCnh(@Context HttpServletRequest request, @Context SecurityContext context) throws AppException {

        List<InformacoesEntregaCnhWrapper> listaJson = new ArrayList();

        try {
            
            listaJson.addAll(getProcessoAdministrativoService().getInformacoesEntregaCnh());

            return Response.ok(listaJson).build();

        } catch (Exception e) {

            LOG.debug("Erro capturado.", e);
            getControleFalha().gravarFalha(e, "Erro ao recuperar informaçoes de entrega CNH");

            listaJson = new ArrayList();

            return Response.status(Response.Status.BAD_REQUEST).entity(listaJson).build();
        }
    }
}
