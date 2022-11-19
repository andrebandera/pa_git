package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.wrapper.ConfirmaInstauracaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import java.io.IOException;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@ManagedBean
@Path("/pa/andamento")
public class AndamentoProcessoAdministrativoResource extends PaResource {

    private static final Logger LOG = Logger.getLogger(AndamentoProcessoAdministrativoResource.class);
    
    @PUT
    @Path("confirmainstauracao")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmaInstauracao(@Context HttpServletRequest request, ConfirmaInstauracaoWrapper wrapper) throws AppException, IOException {
        
        LOG.debug("Início confirmar instauração BPMS.");
        
        try {
            
            validaDadosObrigatorios(wrapper);
            
            processoAdministrativoService.validarPAParaExecucaoServico(wrapper.getNumeroProcesso(), "finalizainstauracao");
            
            ProcessoAdministrativo processoAdministrativo = 
                    processoAdministrativoService.getProcessoAdministrativoPorNumeroProcessoAtivo(wrapper.getNumeroProcesso());
                    
            String urlBaseBirt = DetranReportsUtil.getReportsBaseBirtUrl(request, true);
            
            new ExecucaoAndamentoManager().iniciaExecucao(new ExecucaoAndamentoEspecificoWrapper(processoAdministrativo,
                                                                                                 null, 
                                                                                                 urlBaseBirt, 
                                                                                                 null));
            
            return Response.ok().build();
            
        } catch (Exception e) {
            LOG.debug("Tratado", e);
            if(wrapper != null && !DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())){
                getControleFalha().gravarFalhaProcessoAdministrativo(e, 
                                                                     "Erro ao confirmar instauração BPMS", 
                                                                     "SEM CPF", 
                                                                     wrapper.getNumeroProcesso());
            }else{
                getControleFalha().gravarFalha(e, "Erro ao confirmar instauração BPMS");
            }
        }
        
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * @param wrapper
     * @throws AppException 
     */
    private void validaDadosObrigatorios(ConfirmaInstauracaoWrapper wrapper) throws AppException {
        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())) {
            DetranWebUtils.applicationMessageException("Número Processo é obrigatório.");
        }
    }
    
    @PUT
    @Path("confirmaretornoar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmaRetornoAR(@Context HttpServletRequest request, NotificaProcessoAdministrativoWrapper wrapper) throws AppException, IOException {
        
        LOG.debug("Início confirmar Retorno AR pelo BPMS.");
        
        Response response = Response.ok().build();
        
        try {

            validaObrigatoriosNotificacao(wrapper);

            String urlBaseBirt = DetranReportsUtil.getReportsBaseBirtUrl(request, true);
            
            ProcessoAdministrativo processoAdministrativo = processoAdministrativoService.getProcessoAdministrativoPorNumeroProcessoAtivo(wrapper.getNumeroProcesso());
            
            processoAdministrativoService.validarPAParaExecucaoServico(wrapper.getNumeroProcesso(), "confirmaretornoar");
            
            new ExecucaoAndamentoManager().iniciaExecucao(new ExecucaoAndamentoEspecificoWrapper(processoAdministrativo, null, urlBaseBirt, wrapper));
            
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST).build(); 
            LOG.debug("Tratado", e);
            if(wrapper != null && !DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())){
                getControleFalha().gravarFalhaProcessoAdministrativo(e, 
                                                                     "Erro ao confirmar retorno AR bpms", 
                                                                     "SEM CPF",
                                                                     wrapper.getNumeroProcesso());
            }else{
                getControleFalha().gravarFalha(e, "Erro ao confirmar retorno AR bpms");
            }
            
        } 

        return response;
    }
    
    /**
     *
     * @param wrapper
     * @throws AppException
     */
    private void validaObrigatoriosNotificacao(NotificaProcessoAdministrativoWrapper wrapper) throws AppException {

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso()) || wrapper.getTipo() == null) {
            DetranWebUtils.applicationMessageException("Número Processo e tipo são obrigatórios.");
        }
    }
}