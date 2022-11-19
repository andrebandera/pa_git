package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.EditalWrapper;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@ManagedBean
@Path("/pa/edital")
public class EditalResource extends PaResource {
    
    private static final Logger LOG = Logger.getLogger(EditalResource.class);
    
    @PUT
    @Path("informaeditalnotificacao")
    @Produces(MediaType.APPLICATION_JSON)
    public Response informaEditalNotificacao(
        @Context HttpServletRequest request, @Context SecurityContext context, EditalWrapper entrada) throws AppException {

        LOG.info("INICIO - Edital Notificação.");
        
        Response response = Response.status(Response.Status.BAD_REQUEST).build();
        
        try {
            
            validaEntradaParaEditalNotificacaoProcessoAdministrativo(entrada);
            
            processoAdministrativoService.validarPAParaExecucaoServico(entrada.getNumeroProcesso(), "informaeditalnotificacao");
             
            Long usuarioID = getProcessoAdministrativoService().buscarIdUsuario(entrada.getUsuario());
            entrada.setProcessoAdministrativo(getProcessoAdministrativoService().getProcessoAdministrativo(entrada.getNumeroProcesso()));
            
            new ExecucaoAndamentoManager()
                    .iniciaExecucao(
                            new ExecucaoAndamentoEspecificoWrapper(
                                    getProcessoAdministrativoService().getProcessoAdministrativo(entrada.getNumeroProcesso()),
                                    usuarioID,
                                    DetranReportsUtil.getReportsBaseBirtUrl(request, true),
                                    entrada
                            )
                    );

            return Response.ok().build();
            
        } catch (Exception e) {
            
            LOG.debug("Tratado", e);
            if(entrada != null && !DetranStringUtil.ehBrancoOuNulo(entrada.getNumeroProcesso())){
                getControleFalha().gravarFalhaProcessoAdministrativo(e, 
                                                                     "Erro ao informar edital notificação - WS", 
                                                                     "SEM CPF",
                                                                     entrada.getNumeroProcesso());
            }else{
                getControleFalha().gravarFalha(e, "Erro ao informar edital notificação - WS");
            }
        } 
        
        LOG.info("FIM - Edital Notificação.");

        return response;
    }

    /**
     * @param entrada
     * @throws AppException 
     */
    private void validaEntradaParaEditalNotificacaoProcessoAdministrativo(EditalWrapper entrada) throws AppException {
        
        if (entrada == null) {
            DetranWebUtils.applicationMessageException("Dados não informados.");
        }
        
        if (DetranStringUtil.ehBrancoOuNulo(entrada.getNumeroProcesso())) {
            DetranWebUtils.applicationMessageException("Número processo não informado.");
        }
        
        if (entrada.getTipo() == null) {
            DetranWebUtils.applicationMessageException("Tipo não informado.");
        }
        
        if (DetranStringUtil.ehBrancoOuNulo(entrada.getNumeroPortaria())) {
            DetranWebUtils.applicationMessageException("Número portaria não informado.");
        }
        
        if (entrada.getDataPublicacaoEdital() == null) {
            DetranWebUtils.applicationMessageException("Data publicação não informada.");
        }
        
        if (DetranStringUtil.ehBrancoOuNulo(entrada.getUsuario())) {
            DetranWebUtils.applicationMessageException("Usuário não informado.");
        }
    }
}