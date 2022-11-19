package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.criteria.BloqueioBCACriteria;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWSWrapper;
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
 * @author desenvolvimento
 */
@ManagedBean
@Path("/pa/bloqueiobca")
public class BloqueioBCAResource extends PaResource<BloqueioBCA, BloqueioBCACriteria>  {
    
    private static final Logger LOG = Logger.getLogger(BloqueioBCAResource.class);
    
    @PUT
    @Path("bloqueiosbca")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response bloqueiosBca(@Context HttpServletRequest request, @Context SecurityContext context) throws AppException {

        LOG.debug("Início Bloqueios BCA.");

        List listaJson = new ArrayList();

        try {

            listaJson.addAll(getProcessoAdministrativoService().getProcessoAdministrativoBloqueioBCA());
            
            return Response.ok(listaJson).build();

        } catch (Exception e) {

            LOG.debug("Erro capturado.", e);
            getControleFalha().gravarFalha(e, "Erro ao recuperar bloqueis BCA - WS");

            return Response.status(Response.Status.BAD_REQUEST).entity(listaJson).build();
        }
    }
    
    @PUT
    @Path("confirmabloqueiobca")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmaBloqueioBca(
        @Context HttpServletRequest request, ProcessoAdministrativoWSWrapper wrapper) throws AppException {

        LOG.debug("Início confirmar Bloqueio BCA pelo BPMS.");

        try {

            validaDadosObrigatorios(wrapper);
            
            processoAdministrativoService.validarPAParaExecucaoServico(wrapper.getNumeroProcesso(), "confirmabloqueiobca");
            
            new ExecucaoAndamentoManager().
                    iniciaExecucao(new ExecucaoAndamentoEspecificoWrapper(processoAdministrativoService.getProcessoAdministrativo(wrapper.getNumeroProcesso()), 
                                                                     null, 
                                                                     DetranReportsUtil.getReportsBaseBirtUrl(request, true),
                                                                     wrapper));

            return Response.ok().build();
            
        } catch (Exception e) {
            
            LOG.debug("Tratado", e);

            getControleFalha()
                .gravarFalhaProcessoAdministrativo(
                    e, 
                    "Erro ao confirmar bloqueio BCA pelo BPMS", 
                    null, 
                    wrapper != null ? wrapper.getNumeroProcesso() : null
                );
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    /**
     * @param wrapper
     * @throws AppException 
     */
    private void validaDadosObrigatorios(ProcessoAdministrativoWSWrapper wrapper) throws AppException {

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())) {
            DetranWebUtils.applicationMessageException("Número Processo é obrigatório.");
        }
    }

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {
        super.executarSearch(request, criteria);
        for(BloqueioBCA bloqueioBCA : view.getEntity()){
            if(SituacaoBloqueioBCAEnum.ATIVO.equals(bloqueioBCA.getSituacao())){
                bloqueioBCA.setDataFim(null);
            }
        }
    }
}