package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.Municipio;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.criteria.NotificacaoProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.EditalProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.NotificacaoProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificadosWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RetornosARWrapper;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaCorreio;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaCorreioDevolucao;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

@ManagedBean
@Path("/pa/notificacao")
public class NotificacaoProcessoAdministrativoResource extends PaResource<NotificacaoProcessoAdministrativoWrapper, NotificacaoProcessoAdministrativoCriteria>{

    private static final Logger LOG = Logger.getLogger(NotificacaoProcessoAdministrativoResource.class);

    private IApoioService apoioService;
    
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    /**
     * PAAndamento15
     * PAAndamento26
     * PAAndamento39
     * PAAndamento46
     * PAAndamento206
     * 
     * http://localhost:8080/detran-processo-administrativo/pa/notificacao/notifica
     *
     * @param request
     * @param context
     * @param entrada
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    @PUT
    @Path("notifica")
    @Produces(MediaType.APPLICATION_JSON)
    public Response notifica(
            @Context HttpServletRequest request, @Context SecurityContext context, NotificaProcessoAdministrativoWrapper entrada) throws AppException {

        LOG.info("Pode notificar.");
        
        try {
            
            processoAdministrativoService.checaQuantidadeDiariaLimiteEmissaoNotificacao();
            
            processoAdministrativoService.validarPAParaExecucaoServico(entrada.getNumeroProcesso(), "notifica");
            
            new ExecucaoAndamentoManager()
                    .iniciaExecucao(
                            new ExecucaoAndamentoEspecificoWrapper(
                                    getProcessoAdministrativoService().getProcessoAdministrativo(entrada.getNumeroProcesso()),
                                    null,
                                    DetranReportsUtil.getReportsBaseBirtUrl(request, true),
                                    entrada
                            )
                    );

            return Response.ok().build();
            
        } catch (Exception e) {
            
            LOG.debug("Tratado", e);
            
            getControleFalha()
                .gravarFalhaProcessoAdministrativo(
                    e, 
                    "Erro ao executar notifica - WS", 
                    null,
                    entrada != null ? entrada.getNumeroProcesso() : null
                );
        }

        return Response.status(Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("notificados")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response notificados(@Context HttpServletRequest request, @Context SecurityContext context) throws AppException {

        LOG.debug("Início notificados.");

        List<NotificadosWrapper> listaJson = new ArrayList();

        try {

            listaJson.addAll(processoAdministrativoService.getNotificados());

            return Response.ok(listaJson).build();

        } catch (Exception e) {

            LOG.debug("Erro capturado.", e);
            getControleFalha().gravarFalha(e, "Erro ao recuperar os notificados - WS");

            listaJson = new ArrayList();
            listaJson.add(gerarWrapperErro("Falha interna. Comunique o suporte."));

            return Response.status(Status.BAD_REQUEST).entity(listaJson).build();
        }
    }

    @PUT
    @Path("retornoar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retornoAR(@Context HttpServletRequest request, @Context SecurityContext context) throws AppException {

        LOG.debug("Início retorno AR.");

        List<RetornosARWrapper> listaJson = new ArrayList();

        try {

            listaJson.addAll(processoAdministrativoService.getRetornoAR());

            return Response.ok(listaJson).build();

        } catch (Exception e) {

            LOG.debug("Erro capturado.", e);
            getControleFalha().gravarFalha(e, "Erro ao recuperar retornos ar - WS");

            listaJson = new ArrayList();
            listaJson.add(new RetornosARWrapper("Falha interna. Comunique o suporte."));

            return Response.status(Status.BAD_REQUEST).entity(listaJson).build();
        }
    }

    @PUT
    @Path("confirmanotificacao")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmaNotificacao(@Context HttpServletRequest request, NotificaProcessoAdministrativoWrapper wrapper) throws AppException {

        LOG.debug("Início confirmar Notificação pelo BPMS.");

        try {

            validaDadosObrigatorios(wrapper);


            processoAdministrativoService.validarPAParaExecucaoServico(wrapper.getNumeroProcesso(), "confirmanotificacao");
            
            new ExecucaoAndamentoManager().
                    iniciaExecucao(
                            new ExecucaoAndamentoEspecificoWrapper(
                                processoAdministrativoService.
                                        getProcessoAdministrativoPorNumeroProcessoAtivo(wrapper.getNumeroProcesso()),
                                null,
                                null,
                                wrapper
                            )
                    );
            
            return Response.ok().build();

        } catch (Exception e) {
            LOG.debug("Tratado", e);
            if(wrapper != null && !DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())){
                getControleFalha().gravarFalhaProcessoAdministrativo(e, 
                                                                     "Erro ao confirmar notificação pelo BPMS", 
                                                                     "SEM CPF",
                                                                     wrapper.getNumeroProcesso());
            }else{
                getControleFalha().gravarFalha(e, "Erro ao confirmar notificação pelo BPMS");
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * @param ex
     * @return
     */
    private NotificadosWrapper gerarWrapperErro(String ex) {

        NotificadosWrapper jsonWrapper = new NotificadosWrapper();

        jsonWrapper.setErro(ex);

        return jsonWrapper;
    }

    /**
     *
     * @param wrapper
     * @throws AppException
     */
    private void validaDadosObrigatorios(NotificaProcessoAdministrativoWrapper wrapper) throws AppException {

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso()) || wrapper.getTipo() == null) {
            DetranWebUtils.applicationMessageException("Número Processo e tipo são obrigatórios.");
        }
    }
    
    /**
     * @param request
     * @param c
     * @throws Exception 
     */
    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder c) throws Exception {
        
        NotificacaoProcessoAdministrativoCriteria criteria = (NotificacaoProcessoAdministrativoCriteria) c;
        
        List lista = genericService.pesquisar(criteria);
        
        if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {
            for (Object object : lista) {
                
                NotificacaoProcessoAdministrativo entidade = (NotificacaoProcessoAdministrativo) object;
                
                NotificacaoProcessoAdministrativoWrapper wrapper = new NotificacaoProcessoAdministrativoWrapper();
                
                wrapper.setEntidade(entidade);
                
                if (null != criteria.getId()) {
                    if (null != entidade.getCorrespondencia())
                        wrapper.setCorrespondenciaCorreioDevolucao(
                                (CorrespondenciaCorreioDevolucao) processoAdministrativoService
                                        .getCorrespondenciaCorreioDevolucaoPorCorrespondencia(entidade.getCorrespondencia().getId()));
                    wrapper.setNotificacaoComplemento((NotificacaoComplemento) processoAdministrativoService.getNotificacaoComplementoPorNotificacao(entidade.getId()));
                    wrapper.setEdital((EditalProcessoAdministrativo) processoAdministrativoService.getEditalProcessoAdministrativoPorNotificacao(entidade.getId()));
                    wrapper.setCorrespondenciaCorreio((CorrespondenciaCorreio) processoAdministrativoService.getCorrespondenciaCorreioPorCorrespondencia(entidade.getCorrespondencia().getId()));
                    
                    if(wrapper.getEntidade().getEndereco() != null
                            && wrapper.getEntidade().getEndereco().getMunicipio() != null){
                        wrapper.setMunicipio((Municipio) getApoioService().getMunicipioId(wrapper.getEntidade().getEndereco().getMunicipio()));
                    }
                }
                
                view.addEntity(wrapper);
            }
        }
        
        view.setRowcount(genericService.contarPesquisa(criteria));
    }

    private boolean jaExisteRetornoAr(ProcessoAdministrativo processo, TipoFasePaEnum tipo) throws AppException {
        
        Boolean retorno = false;
        
        CorrespondenciaCorreio correspondenciaCorreio 
            = (CorrespondenciaCorreio) 
                processoAdministrativoService.getCorrespondenciaCorreioPorProcessoETipo(processo.getId(), tipo);
        
        if(correspondenciaCorreio != null && correspondenciaCorreio.getDataChegadaDestino() != null){
            retorno = true;
        }
        
        return retorno;
    }
    
    
    @PUT
    @Path("terminoprazonotificacao")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response terminoPrazoNotificacao(@Context HttpServletRequest request, @Context SecurityContext context) throws AppException {

        LOG.debug("Início terminoprazonotificacao.");

        List<NotificadosWrapper> listaJson = new ArrayList();

        try {

            listaJson.addAll(processoAdministrativoService.getPAComPrazoNotificacaoTerminado());

            return Response.ok(listaJson).build();

        } catch (Exception e) {

            LOG.debug("Erro capturado.", e);
            getControleFalha().gravarFalha(e, "Erro ao recuperar os notificados - WS");

            listaJson = new ArrayList();
            listaJson.add(gerarWrapperErro("Falha interna. Comunique o suporte."));

            return Response.status(Status.BAD_REQUEST).entity(listaJson).build();
        }
    }
    
    
    @PUT
    @Path("confirmaterminoprazonotificacao")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmaTerminoPrazoNotificacao(@Context HttpServletRequest request, NotificaProcessoAdministrativoWrapper wrapper) throws AppException {

        LOG.debug("Início contifrma Termino Prazo Notificacao pelo BPMS.");

        try {

            validaDadosObrigatorios(wrapper);


            processoAdministrativoService.validarPAParaExecucaoServico(wrapper.getNumeroProcesso(), "confirmaterminoprazonotificacao");
            
            new ExecucaoAndamentoManager().
                    iniciaExecucao(
                            new ExecucaoAndamentoEspecificoWrapper(
                                processoAdministrativoService.
                                        getProcessoAdministrativoPorNumeroProcessoAtivo(wrapper.getNumeroProcesso()),
                                null,
                                null,
                                wrapper
                            )
                    );
            
            return Response.ok().build();

        } catch (Exception e) {
            LOG.debug("Tratado", e);
            if(wrapper != null && !DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())){
                getControleFalha().gravarFalhaProcessoAdministrativo(e, 
                                                                     "Erro ao confirmar notificação pelo BPMS", 
                                                                     "SEM CPF",
                                                                     wrapper.getNumeroProcesso());
            }else{
                getControleFalha().gravarFalha(e, "Erro ao confirmar notificação pelo BPMS");
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}