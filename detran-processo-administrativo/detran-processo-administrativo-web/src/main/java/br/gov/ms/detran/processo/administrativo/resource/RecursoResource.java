package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.ImpressoraUsuario;
import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.TemplateProtocolo;
import br.gov.ms.detran.comum.core.projeto.enums.ace.ModeloImpressoraEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.FormaProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.ResponsavelProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.WrapperUtil;
import br.gov.ms.detran.comum.util.*;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.adm.IAdministrativoService;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.core.iface.servico.hab.IControleCnhService;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.criteria.RecursoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.ArquivoPA;
import br.gov.ms.detran.processo.administrativo.entidade.Recurso;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoRecurso;
import br.gov.ms.detran.processo.administrativo.enums.*;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoRecursoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoCanceladoWrapper;

import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean
@Path("/recursos")
public class RecursoResource extends PaResource<RecursoWrapper, RecursoCriteria> {
    
    private static final Logger LOG = Logger.getLogger(RecursoResource.class);
    
    private static final Integer NUMERO_COPIAS_PROTOCOLO_IMPRESSAO = 1;
    
    private IApoioService apoioService;
    private IControleCnhService controleCnhService;
    private IAdministrativoService administrativoService;
    
    public IAdministrativoService getAdministrativoService() {
        
        if (administrativoService == null) {
            administrativoService = (IAdministrativoService) JNDIUtil.lookup("ejb/AdministrativoService");
        }
        
        return administrativoService;
    }

    /**
     *
     * @return
     */
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }
    
    public IControleCnhService getControleCnhService() {
        
        if (controleCnhService == null) {
            controleCnhService = (IControleCnhService) JNDIUtil.lookup("ejb/ControleCnhService");
        }
        
        return controleCnhService;
    }
    
    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {
        
        List<ListSelectItem> listSelectItem = new ArrayList<>();
        
        try {

            listSelectItem.add(new ListSelectItem("situacao", DetranWebUtils.getSelectItems(SituacaoRecursoEnum.class, Boolean.TRUE)));
            listSelectItem.add(new ListSelectItem("tipo", DetranWebUtils.getSelectItems(Boolean.TRUE, TipoFasePaEnum.ENTREGA_CNH, TipoFasePaEnum.INSTAURACAO, TipoFasePaEnum.PENALIZACAO)));
            listSelectItem.add(new ListSelectItem("resultado", DetranWebUtils.getSelectItems(ResultadoRecursoEnum.class, Boolean.TRUE, ResultadoRecursoEnum.ACOLHIDO, ResultadoRecursoEnum.NAO_ACOLHIDO)));
            listSelectItem.add(new ListSelectItem("ativo", DetranWebUtils.getSelectItems(AtivoEnum.class, Boolean.TRUE)));
            listSelectItem.add(new ListSelectItem("indiceForaPrazo", DetranWebUtils.getSelectItems(BooleanEnum.class, Boolean.TRUE)));
            listSelectItem.add(new ListSelectItem("conhecimento", DetranWebUtils.getSelectItems(BooleanEnum.class, Boolean.TRUE)));

            listSelectItem.addAll(carregarListasNovoEdicao());
        
        } catch (DatabaseException ex) {
            //LOG.error("Ocorreu uma exceção ao gravar o registro.", ex);
            DetranWebUtils.addErrorMessage(ex, view);
        } catch (AppException ex) {
            //LOG.error("Ocorreu uma exceção ao gravar o registro.", ex);
            DetranWebUtils.addErrorMessage(ex, view);
        } catch (Exception ex) {
            LOG.error("Ocorreu uma exceção ao gravar o registro.", ex);
            DetranWebUtils.addErrorMessage("severity.error.application.summary.databaseexception", view);
        }

        return listSelectItem;
    }
    
    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws AppException {
        
        RecursoCriteria c = (RecursoCriteria)criteria;
        
        if(c == null || c.isCriteriaEmpty()) {
            
            carregarListSelectItem(request, c, SEARCH_ACTION);
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<RecursoWrapper> listaPesquisa 
            = WrapperUtil
                .convertEntityToWrapper2(
                    getProcessoAdministrativoService().getRecursoPorFiltros2(criteria), getEntityClass()
                );
        
        if (!DetranCollectionUtil.ehNuloOuVazio(listaPesquisa)) {
            
            for (RecursoWrapper wrapper : listaPesquisa) {
                
                RecursoMovimento movimento 
                    = (RecursoMovimento) 
                        processoAdministrativoService.getMovimentoRecurso(wrapper.getEntidade().getId());
                
                if(movimento != null) {
                
                    ResultadoRecurso resultado 
                        = (ResultadoRecurso) 
                            processoAdministrativoService
                                .getResultadoRecursoAtivoPorRecurso(movimento.getRecurso().getId());

                    if(resultado != null && resultado.getResultado() != null){
                        wrapper.setResultadoLabel(resultado.getResultado().toString());
                        wrapper.setDataResultado(resultado.getData());
                    }

                    if (movimento != null) {
                        wrapper.setDataRecurso(movimento.getDataMovimento());
                        
                        TemplateProtocolo template = (TemplateProtocolo) getControleCnhService().
                                getTemplateProtocoloPorID(movimento.getProtocolo().getTemplateProtocolo());
                        wrapper.setForma(template.getFormaProtocolo());
                        wrapper.setResponsavel(template.getResponsavelProtocolo());
                        wrapper.setObservacao(template.getObservacao());
                        wrapper.setProtocolo(movimento.getProtocolo());
                    }
                }
                
                
                if(SituacaoRecursoEnum.JULGADO.equals(wrapper.getEntidade().getSituacao()) 
                    && DetranStringUtil.ehBrancoOuNulo(wrapper.getResultadoLabel())){
                    wrapper.setResultadoLabel(ResultadoRecursoEnum.IRREGULAR.toString());
                }
                
                wrapper.setNumeroDocumento(getApoioService().getCpfPorAtendimento(wrapper.getEntidade().getProcessoAdministrativo().getAtendimento()));
                wrapper.setIndiceForaPrazo(getProcessoAdministrativoService().getIndiceForaPrazo(wrapper.getEntidade().getId()));
            }
        }
        
        view.setEntity(listaPesquisa);
        view.setRowcount(getProcessoAdministrativoService().getCountRecursoPorFiltros2(criteria).getResultado());
    }
    
    @Override
    protected void executarGravacao(HttpServletRequest request, IBaseEntity entity) throws AppException {

        DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);
        RecursoWrapper wrapper = (RecursoWrapper) entity;

        getProcessoAdministrativoService().validarRecursoOnlineEmBackOffice(wrapper.getEntidade().getProcessoAdministrativo());

        getProcessoAdministrativoService().gravarRecurso(entity, usuarioLogado);

        wrapper.setTipoProtocolo(TipoSituacaoProtocoloEnum.APRESENTACAO);

        geraProtocolo(request, wrapper);

        new ExecucaoAndamentoManager()
                .iniciaExecucao(
                        new ExecucaoAndamentoEspecificoWrapper(
                        getProcessoAdministrativoService().getProcessoAdministrativo(wrapper.getEntidade().getProcessoAdministrativo().getId()), 
                        usuarioLogado.getOperador().getUsuario().getId(), 
                        null,
                        wrapper
                    )
                );
        
        
    }

    @PUT
    @Path("buscarProcessosAdministrativos")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response buscarProcessosAdministrativos(
        @Context HttpServletRequest request, @Context SecurityContext context, String numeroDocumento) throws AppException {

        try {
            
            List<ListSelectItem> listSelectItem = new ArrayList<>();

            if (DetranStringUtil.ehBrancoOuNulo(numeroDocumento)) {
                
                DetranWebUtils.addErrorMessage("O CPF é obrigatório.", view);
                
                return getResponseOk(request);
            }
            
            if (!DetranStringUtil.ehBrancoOuNulo(numeroDocumento)) {
            
                List<ProcessoAdministrativoRecursoWrapper> processos 
                    = getListProcessoAdministrativoPermitidosAbrirRecursoPorCPFCondutor(numeroDocumento);

                listSelectItem.add(DetranWebUtils.populateItemsComboBox(processos, "", "processosAdministrativos","", "processoAdministrativo.numeroProcessoMascarado"));
            }

            listSelectItem.addAll(carregarListasNovoEdicao());

            view.getListSelectItem().addAll(listSelectItem);
            
        } catch (DatabaseException ex) {
            //LOG.error("Ocorreu uma exceção ao gravar o registro.", ex);
            DetranWebUtils.addErrorMessage(ex, view);
        } catch (AppException ex) {
            //LOG.error("Ocorreu uma exceção ao gravar o registro.", ex);
            DetranWebUtils.addErrorMessage(ex, view);
        } catch (Exception ex) {
            LOG.error("Ocorreu uma exceção ao gravar o registro.", ex);
            DetranWebUtils.addErrorMessage("severity.error.application.summary.databaseexception", view);
        }
        
        return getResponseOk(request);
    }
    
    @PUT
    @Path("recursos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recursos(@Context HttpServletRequest request, @Context SecurityContext context) throws AppException {

        LOG.debug("Início recursos.");

        List listaJson = new ArrayList();

        try {

            listaJson.addAll(getProcessoAdministrativoService().getRecursos());
            
            return Response.ok(listaJson).build();

        } catch (Exception e) {

            LOG.debug("Erro capturado.", e);
            getControleFalha().gravarFalha(e, "Erro ao recuperar recursos - WS");

            return Response.status(Response.Status.BAD_REQUEST).entity(listaJson).build();
        }
    }
    
    @PUT
    @Path("confirmarecurso")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmaRecurso(@Context HttpServletRequest request, RecursoWSWrapper wrapper) throws AppException {

        LOG.debug("Início confirmar Recurso pelo BPMS.");

        try {

            validaDadosObrigatorios(wrapper);
            
            processoAdministrativoService.validarPAParaExecucaoServico(wrapper.getNumeroProcesso(), "confirmarecurso");
            
            new ExecucaoAndamentoManager()
                .iniciaExecucao(
                    new ExecucaoAndamentoEspecificoWrapper(
                        getProcessoAdministrativoService().getProcessoAdministrativo(wrapper.getNumeroProcesso()), 
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
                                                                     "Erro ao confirmar recurso pelo bpms", 
                                                                     "SEM CPF",
                                                                     wrapper.getNumeroProcesso());
            }else{
                getControleFalha().gravarFalha(e, "Erro ao confirmar recurso pelo bpms");
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @PUT
    @Path("resultadorecurso")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resultadoRecurso(@Context HttpServletRequest request, RecursoWSWrapper wrapper) throws AppException {

        LOG.debug("Início resultado Recurso pelo BPMS.");
        
        Response response = Response.status(Response.Status.BAD_REQUEST).build();
        
        try {

            validaDadosObrigatorios(wrapper);
            
            validaDadosObrigatoriosParaResultadoRecurso(wrapper);
            
            Usuario usuario = (Usuario) getApoioService().getUsuarioPorCPF(wrapper.getUsuario());
            
            if(usuario != null) {
            
                
            processoAdministrativoService.validarPAParaExecucaoServico(wrapper.getNumeroProcesso(), "resultadorecurso");
            
            new ExecucaoAndamentoManager()
                .iniciaExecucao(
                    new ExecucaoAndamentoEspecificoWrapper(
                        getProcessoAdministrativoService().getProcessoAdministrativo(wrapper.getNumeroProcesso()), 
                        usuario.getId(), 
                        DetranReportsUtil.getReportsBaseBirtUrl(request, true),
                        wrapper
                    )
                );
                
            }else{
                DetranWebUtils.applicationMessageException("Usuário não cadastrado.");
            }
            return Response.ok().build(); 

        } catch (Exception e) {
        
            LOG.debug("Tratado", e);
            
            getControleFalha()
                .gravarFalhaProcessoAdministrativo(
                    e, 
                    "Erro ao executar resultado recurso - WS", 
                    null, 
                    wrapper != null ? wrapper.getNumeroProcesso() : null
                );
        
        }

        return response;
    }
    
    /**
     *
     * @param wrapper
     * @throws AppException
     */
    private void validaDadosObrigatorios(RecursoWSWrapper wrapper) throws AppException {

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso()) 
                || DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProtocolo()) ) {
            
            DetranWebUtils.applicationMessageException("Número Processo e Protocolo são obrigatórios.");
        }
    }
    
    /**
     *
     * @param wrapper
     * @throws AppException
     */
    private void validaDadosObrigatoriosParaResultadoRecurso(RecursoWSWrapper wrapper) throws AppException {
        
        if (wrapper.getResultado() == null) {
            DetranWebUtils.applicationMessageException("Resultado é obrigatório.");
        }

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getUsuario())) {
            DetranWebUtils.applicationMessageException("Usuário é obrigatório.");
        }
        
        if (wrapper.getDataJulgamento() == null) {
            DetranWebUtils.applicationMessageException("Data Julgamento é obrigatório.");
        }

        if (ResultadoRecursoEnum.IRREGULAR.equals(wrapper.getResultado())) {
            
            if(wrapper.getAcao() == null) {
                DetranWebUtils.applicationMessageException("Ação é obrigatório para Resultado IRREGULAR.");
            }
        }
    }

    /**
     * @return
     * @throws AppException 
     */
    private List<ListSelectItem> carregarListasNovoEdicao() throws AppException {
        
        List<ListSelectItem> listSelectItem = new ArrayList<>();
        
        listSelectItem.add(new ListSelectItem("indiceForaPrazo", DetranWebUtils.getSelectItems(BooleanEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(new ListSelectItem("conhecimento", DetranWebUtils.getSelectItems(BooleanEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(DetranWebUtils.populateItemsComboBox(getProcessoAdministrativoService().getListaMotivoAlegacao(), "", "motivos","", "descricao"));
        
        listSelectItem.add(new ListSelectItem("destino", DetranWebUtils.getSelectItems(Boolean.TRUE, OrigemDestinoEnum.CETRAN, OrigemDestinoEnum.JARI, OrigemDestinoEnum.SEPEN)));
        
        listSelectItem.add(new ListSelectItem("forma", DetranWebUtils.getSelectItems(FormaProtocoloEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(new ListSelectItem("responsavel", DetranWebUtils.getSelectItems(ResponsavelProtocoloEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(new ListSelectItem("motivoCancelamento", DetranWebUtils.getSelectItems(MotivoCancelamentoRecursoEnum.class, Boolean.TRUE)));
        
        return listSelectItem;
    }
    
    /**
     * @param request
     * @param wrapper
     * @throws AppException 
     */
    private void geraProtocolo(HttpServletRequest request, RecursoWrapper wrapper) throws AppException {
        
        try {
            
            RecursoMovimento recursoMovimento = 
                (RecursoMovimento) getProcessoAdministrativoService().getRecursoMovimentoPorRecursoETipoProtocolo(wrapper.getEntidade(), wrapper.getTipoProtocolo());

            if (recursoMovimento == null || recursoMovimento.getProtocolo() == null || recursoMovimento.getProtocolo().getId() == null) {
                DetranWebUtils.applicationMessageException("Dados obrigatórios estão inválidos.");
            }
            
            getProcessoAdministrativoService().ajustaProtocoloRecursoGeraCodigoBarra(recursoMovimento.getProtocolo());
            
            // Parametriza nome do arquivo e usuário logado para BIRT recebendo o Byte Arquivo.
            Map<String, String> parametros = new HashMap<>();
            parametros.put("protocoloId", recursoMovimento.getProtocolo().getId().toString());
            
            String nomeRelatorio = "recurso_cancelamento";
            
            if (TipoSituacaoProtocoloEnum.APRESENTACAO.equals(wrapper.getTipoProtocolo())) {
                nomeRelatorio = "recurso_apresentacao";
            }

            wrapper.setByteArquivo(
                DetranHTTPUtil
                    .download(
                        DetranReportsUtil.getReportsBaseBirtUrl(request, Boolean.TRUE)
                            + DetranReportsUtil
                                .getReportParamsBirtUrl(
                                    nomeRelatorio,
                                    FormatoRelatorioEnum.PDF.getRptFormat(),
                                    parametros,
                                    "relatorios/processoadministrativo/recurso/"
                                )
                    )
            );

            if (wrapper.getByteArquivo() == null) {
                DetranWebUtils.applicationMessageException("severity.error.application.summary.databaseexception");
            }

            getProcessoAdministrativoService().incluiArquivoProtocoloRecurso(wrapper);

            DetranWebUtils.addInfoMessage("application.message.operacao.sucesso", view);
            
        } catch (AppException e) {

            //LOG.error("Tratamento falha", e);
            DetranWebUtils.addErrorMessage(e, view);

        } catch (Exception e) {

            LOG.error("Ocorreu um erro inesperado.", e);
            DetranWebUtils.addErrorMessage("severity.error.application.summary.databaseexception", view);
        }
    }
    
    @PUT
    @Path("exportarprotocolo")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response exportarProtocolo(
        @Context HttpServletRequest request, @Context SecurityContext context, RecursoWrapper wrapper) throws AppException {
        
        try {
            
            if (wrapper.getTipoProtocolo() == null) {
                DetranWebUtils.applicationMessageException("Tipo é obrigatório.");
            }
            
            ArquivoPA arquivo = (ArquivoPA) getProcessoAdministrativoService().getProtocoloRecurso(wrapper);
            
            if(arquivo == null || arquivo.getByteArquivo() == null) {
                geraProtocolo(request, wrapper);
            }
            
            arquivo = (ArquivoPA) getProcessoAdministrativoService().getProtocoloRecurso(wrapper);
            
            view.setObjectResponse(arquivo);
            
            return  getResponseOk(request);
        
        } catch (AppException e) {
            //LOG.error("Ocorreu um erro inesperado.", e);
            DetranWebUtils.addErrorMessage(e, view);
        }
        
        return getResponseOk(request);
    }
    
    @POST
    @Path("cancelar")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response cancelar(
        @Context HttpServletRequest request, @Context SecurityContext context, RecursoWrapper wrapper) {

        LOG.debug("Entrada no método cancelar recurso.");

        view = getModelView();

        try {
            
            if (null == wrapper.getMotivoCancelamento()) {
                DetranWebUtils.applicationMessageException("O motivo cancelamento é obrigatório.");
            }

            DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);
            
            
            Recurso recurso = (Recurso) getProcessoAdministrativoService().cancelarRecurso(usuarioLogado, wrapper);
            
            wrapper.setEntidade(recurso);
            wrapper.setTipoProtocolo(TipoSituacaoProtocoloEnum.CANCELAMENTO);
            
            geraProtocolo(request, wrapper);
            
            imprimirProtocolo(usuarioLogado, wrapper);
            
            new ExecucaoAndamentoManager()
                    .iniciaExecucao(
                            new ExecucaoAndamentoEspecificoWrapper(
                                    getProcessoAdministrativoService().getProcessoAdministrativo(wrapper.getEntidade().getProcessoAdministrativo().getId()), 
                                    usuarioLogado.getOperador().getUsuario().getId(), 
                                    null,
                                    wrapper));
            
            view.setRowcount(Long.valueOf(0));
            
            DetranWebUtils.addInfoMessage("application.message.operacao.sucesso", view);

        } catch (DatabaseException ex) {
            //LOG.error("Ocorreu uma exceção ao gravar o registro.", ex);
            DetranWebUtils.addErrorMessage(ex, view);
        } catch (AppException ex) {
            //LOG.error("Ocorreu uma exceção ao gravar o registro.", ex);
            DetranWebUtils.addErrorMessage(ex, view);
        } catch (Exception ex) {
            LOG.error("Ocorreu uma exceção ao gravar o registro.", ex);
            DetranWebUtils.addErrorMessage("severity.error.application.summary.databaseexception", view);
        }

        return getResponseOk(request);
    }

    @PUT
    @Path("cancelados")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelados(
        @Context HttpServletRequest request, @Context SecurityContext context) throws AppException {

        LOG.debug("Início Recursos Cancelados.");

        List listaJson = new ArrayList();

        try {

            listaJson.addAll(getProcessoAdministrativoService().getRecursosCancelados());
            
            return Response.ok(listaJson).build();

        } catch (Exception e) {

            LOG.debug("Erro capturado.", e);
            getControleFalha().gravarFalha(e, "Erro ao recuperar recursos cancelados - WS");

            return Response.status(Response.Status.BAD_REQUEST).entity(listaJson).build();
        }
    }
    
    @PUT
    @Path("confirmacancelamento")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmaCancelamento(
        @Context HttpServletRequest request, RecursoCanceladoWrapper wrapper) throws AppException {

        LOG.debug("Início confirmar cancelamento pelo BPMS.");

        try {
            
            if(wrapper == null 
                    || DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProtocolo()) 
                    || DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())) {
                
                DetranWebUtils.applicationMessageException("PA.M1");
            }
            
            String urlBaseBirt = DetranReportsUtil.getReportsBaseBirtUrl(request, true);
            
            processoAdministrativoService.validarPAParaExecucaoServico(wrapper.getNumeroProcesso(), "confirmacancelamentorecurso");
            
            
            new ExecucaoAndamentoManager()
                .iniciaExecucao(
                    new ExecucaoAndamentoEspecificoWrapper(
                        getProcessoAdministrativoService().getProcessoAdministrativo(wrapper.getNumeroProcesso()), 
                        null, 
                        urlBaseBirt,
                        wrapper
                    )
                );
            

            return Response.ok().build();

        } catch (Exception e) {
            LOG.debug("Tratado", e);
            if(wrapper != null && !DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())){
                getControleFalha().gravarFalhaProcessoAdministrativo(e, 
                                                                     "Erro ao confirmar cancelamento recurso pelo bpms", 
                                                                     "SEM CPF",
                                                                     wrapper.getNumeroProcesso());
            }else{
                getControleFalha().gravarFalha(e, "Erro ao confirmar cancelamento recurso pelo bpms");
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * 
     * @param numeroDocumento
     * @return
     * @throws AppException 
     */
    private List<ProcessoAdministrativoRecursoWrapper> 
        getListProcessoAdministrativoPermitidosAbrirRecursoPorCPFCondutor(String numeroDocumento) throws AppException {
        
        List<ProcessoAdministrativoRecursoWrapper> listaProcessosPermitidos 
            = getProcessoAdministrativoService()
                .getListProcessoAdministrativoPermitidosAbrirRecursoPorCPFCondutor(numeroDocumento);

        
        if (DetranCollectionUtil.ehNuloOuVazio(listaProcessosPermitidos)) {
            DetranWebUtils.addErrorMessage("Nenhum Processo Administrativo para o CPF foi encontrado APTO para abertura de Recurso.", view);
        }
        
        return listaProcessosPermitidos;
    }
        
    /**
     * @param usuarioLogado
     * @param wrapper
     * @throws AppException 
     */
    private void imprimirProtocolo(DetranUserDetailsWrapper usuarioLogado, RecursoWrapper wrapper) throws AppException {
        
        ImpressoraUsuario impressoraUsuario = 
                (ImpressoraUsuario) getAdministrativoService()
                        .getImpressoraDoUsuarioLogado(
                                usuarioLogado.getUsuarioLocal().getNumeroDetran(), 
                                ModeloImpressoraEnum.LASER);

        if (null != impressoraUsuario && null != impressoraUsuario.getImpressora()) {
            
            ArquivoPA arquivo = (ArquivoPA) getProcessoAdministrativoService().getProtocoloRecurso(wrapper);
            
            if (null != arquivo) {
                getAdministrativoService()
                        .imprimirDocumentoNaImpressoDoUsuario(
                                impressoraUsuario.getImpressora(), 
                                arquivo.getByteArquivo(),
                                null,
                                NUMERO_COPIAS_PROTOCOLO_IMPRESSAO);
            }
        }
    }
}