package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoNotificacaoEnum;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranHTTPUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.criteria.ProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.ArquivoPA;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.Protocolo;
import br.gov.ms.detran.processo.administrativo.wrapper.DesistenteRecursoInstauracaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoOnlineCanceladoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.ProcessoAdministrativoDesistenteWrapper;

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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author desenvolvimento
 */
@ManagedBean
@Path("/desistenterecursoinstauracaos")
public class DesistenteRecursoInstauracaoResource extends PaResource<DesistenteRecursoInstauracaoWrapper, ProcessoAdministrativoCriteria>{
    
    private static final Logger LOG = Logger.getLogger(DesistenteRecursoInstauracaoResource.class);

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {
        
        List<DesistenteRecursoInstauracaoWrapper> lista = new ArrayList<>();
        ProcessoAdministrativoCriteria c = (ProcessoAdministrativoCriteria) criteria;
        
        
        /** Processos para desistência. */
        List<PAOcorrenciaStatus> ocorrenciasParaDesistencia = processoAdministrativoService.
                                                getOcorrenciaPorCpfEFluxo(c.getCpf(), 
                                                                          PAFluxoProcessoConstante.FLUXO_DESISTENTE_RECURSO_INSTAURACAO_PENALIZACAO);
        for (PAOcorrenciaStatus ocorrencia : ocorrenciasParaDesistencia) {
            lista.add(montarDesistenteWrapper(ocorrencia, false));
        }
        
        /** Processos desistentes. */
        List<PAOcorrenciaStatus> ocorrenciasDesistentes = processoAdministrativoService.getOcorrenciasDesistentesInstPenalizacao(c.getCpf());
        for (PAOcorrenciaStatus ocorrencia : ocorrenciasDesistentes) {
            lista.add(montarDesistenteWrapper(ocorrencia, true));
        }
        
        view.setEntity(lista);
        view.setRowcount(DetranCollectionUtil.ehNuloOuVazio(lista)? 0L: lista.size());
        if(view.getRowcount() != null && view.getRowcount() == 0L && !DetranStringUtil.ehBrancoOuNulo(c.getCpf())){
            DetranWebUtils.addWarningMessage("Não existe processo diponível para Desistência de Recurso Instauração Penalização", view);
            criteria.setEmptyMessage(Boolean.FALSE);
        }
        
    }

    public DesistenteRecursoInstauracaoWrapper montarDesistenteWrapper(PAOcorrenciaStatus ocorrencia, Boolean desistente) throws AppException {
        return new DesistenteRecursoInstauracaoWrapper(ocorrencia, 
                processoAdministrativoService.
                        existeRecursoEmAnalisePorPA(ocorrencia.getProcessoAdministrativo().getId()),
                desistente);
    }
    
    @PUT
    @Path("desistir")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response desistir(@Context HttpServletRequest request,
            @Context SecurityContext context,
            ProcessoAdministrativoCriteria criteria) throws Exception {

        try {

            if (criteria == null || criteria.isCriteriaEmpty()) {
                DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
            }

            DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);

            RecursoOnlineCanceladoWrapper recursoCanceladoWrapper
                    = new RecursoOnlineCanceladoWrapper(" Desistência Recurso Instauração/Penalização",
                    DetranHTTPUtil.getClientIpAddr(request),
                    usuarioLogado.getUsername());

            view = getModelView();

            ProcessoAdministrativo pa = (ProcessoAdministrativo) processoAdministrativoService.
                    buscarEntidadePeloId(ProcessoAdministrativo.class, criteria.getId());

            processoAdministrativoService.executaDesistenciaRecInstPen(pa, usuarioLogado, recursoCanceladoWrapper);

            new ExecucaoAndamentoManager()
                    .iniciaExecucao(
                    new ExecucaoAndamentoEspecificoWrapper(
                        pa, 
                        usuarioLogado.getOperador().getUsuario().getId(), 
                        null,
                        null
                    )
                );


            DetranWebUtils.addInfoMessage("Desistência de Recurso de Instauração e Penalização realizada com sucesso.", view);

        } catch (AppException ex) {
            DetranWebUtils.addErrorMessage(ex, view);
        } catch (Exception ex) {
            DetranWebUtils.addErrorMessage(ex, view);
        }

        return Response.ok().entity(view).build();
    }
    
    @PUT
    @Path("exportarprotocolo")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response exportarProtocolo(
            @Context HttpServletRequest request, @Context SecurityContext context, Long  idPA) throws AppException {

        try {

            Protocolo protocolo = processoAdministrativoService.getProtocoloPorIdPAETipoNotificacao(idPA, TipoNotificacaoEnum.PROTOCOLO_PA_DESISTENCIA_REC_INST_PEN);
            if(protocolo == null){
                DetranWebUtils.applicationMessageException("Não foi possível recuperar o protocolo de desistência.");
            }
            ArquivoPA arquivoPA = protocolo.getArquivoPa();

            if (arquivoPA == null || arquivoPA.getByteArquivo() == null) {

                String urlBaseBirt = DetranReportsUtil.getReportsBaseBirtUrl(request, true);

                arquivoPA 
                    = (ArquivoPA) processoAdministrativoService.gravarArquivoProtocoloDesistenciaInstPen(protocolo, urlBaseBirt);
            }

            view.setObjectResponse(arquivoPA);

            return getResponseOk(request);

        } catch (AppException e) {
            //LOG.error("Ocorreu um erro inesperado.", e);
            DetranWebUtils.addErrorMessage(e, view);
        }

        return getResponseOk(request);
    }
    
    /**
     * WS - BPMS
     * @param request
     * @param context
     * @return
     * @throws AppException 
     */
    @PUT
    @Path("desistentes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response desistentes(@Context HttpServletRequest request, @Context SecurityContext context) throws AppException {

        LOG.debug("Início PA desistentes recurso instauração e penalização.");

        List listaJson = new ArrayList();

        try {

            listaJson
                .addAll(
                    getProcessoAdministrativoService()
                        .getProcessoAdministrativoDesistentesRecursoInstauracaoPenalizacao()
                );

            return Response.ok(listaJson).build();

        } catch (Exception e) {

            LOG.debug("Erro capturado.", e);
            getControleFalha().gravarFalha(e, "Erro ao recuperar PAs desistentes - WS");

            return Response.status(Response.Status.BAD_REQUEST).entity(listaJson).build();
        }
    }
    
    /**
     * WS - BPMS
     * @param request
     * @param wrapper
     * @return
     * @throws AppException 
     */
    @PUT
    @Path("confirmadesistencia")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmaDesistencia(@Context HttpServletRequest request, 
                                        ProcessoAdministrativoDesistenteWrapper wrapper) throws AppException {

        LOG.debug("Início confirmar desistência pelo BPMS.");

        try {

            if (wrapper == null
                    || DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProtocolo())
                    || DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())) {

                DetranWebUtils.applicationMessageException("PA.M1");
            }

            getProcessoAdministrativoService().validarPAParaExecucaoServico(wrapper.getNumeroProcesso(), "confirmadesistenterecursoinstauracaopenalizacao");
            
            new ExecucaoAndamentoManager().
                    iniciaExecucao(new ExecucaoAndamentoEspecificoWrapper(processoAdministrativoService.getProcessoAdministrativo(wrapper.getNumeroProcesso()),
                                                                     null,
                                                                     null,
                                                                     wrapper));

            return Response.ok().build();

        } catch (Exception e) {
            
            LOG.debug("Tratado", e);
            
            if(wrapper != null && !DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())){
                
                getControleFalha()
                    .gravarFalhaProcessoAdministrativo(
                        e, 
                        "Erro ao confirmar desistencia pelo bpms", 
                        "SEM CPF",
                        wrapper.getNumeroProcesso()
                    );
                
            } else {
                getControleFalha().gravarFalha(e, "Erro ao confirmar desistencia pelo bpms");
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}