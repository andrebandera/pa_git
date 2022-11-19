package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.ws.pix.bb.swagger.client.StringUtil;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranSpringSecurityUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.criteria.AjustaProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.AjustaProcessoAdministrativoWrapper;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import br.gov.ms.detran.processo.administrativo.ejb.IPAAndamentoService;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAInicioFluxo;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ManagedBean
@Path("execucaoandamentos")
public class ExecucaoAndamentoEspecificoResource extends PaResource<AjustaProcessoAdministrativoWrapper, AjustaProcessoAdministrativoCriteria> {

    private static final Logger LOG = Logger.getLogger(ExecucaoAndamentoEspecificoResource.class);

    @EJB
    private IPAAndamentoService iAndamentoServiceLab;

    public IPAAndamentoService getPAAndamentoServiceLab() {

        if (iAndamentoServiceLab == null) {
            iAndamentoServiceLab = (IPAAndamentoService) JNDIUtil.lookup("ejb/PAAndamentoService");
        }

        return iAndamentoServiceLab;
    }

    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> listSelectItem = new ArrayList<>();

        List<PAAndamentoProcesso> andamentos = getProcessoAdministrativoService().getAndamentosAutomaticos();
        listSelectItem.add(DetranWebUtils.populateItemsComboBox(andamentos, "", "andamento", " - ", "codigo", "descricao"));

        listSelectItem.add(new ListSelectItem("tipoProcesso", DetranWebUtils.getSelectItems(TipoProcessoEnum.class, Boolean.TRUE)));

        return listSelectItem;
    }

    @PUT
    @Path("buscarlistaandamentos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarListaAndamentos(@Context HttpServletRequest request, @Context SecurityContext context, PAFluxoProcesso novoFluxoProcesso) throws AppException {

        ListSelectItem listSelectItem;

        try {
            List<PAAndamentoProcesso> lAndamento = getProcessoAdministrativoService().getAndamentosAtivosPorFluxo(novoFluxoProcesso.getCodigo());
            listSelectItem = DetranWebUtils.populateItemsComboBox(lAndamento, "", "novoAndamento", " - ", "codigo", "descricao");

            view.setObjectResponse(listSelectItem);

            return getResponseOk(request);

        } catch (AppException e) {

            LOG.debug("Erro capturado.", e);
            DetranWebUtils.applicationMessageException("Erro ao buscar lista de Novos Andamentos.");

            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @Override
    protected void executarGravacao(HttpServletRequest request, IBaseEntity entidade) throws AppException {

        AjustaProcessoAdministrativoWrapper wrapper = (AjustaProcessoAdministrativoWrapper) entidade;
        DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);

        try {
                wrapper.setNumeroProcesso(retiraMascara(wrapper.getNumeroProcesso()));
            if (wrapper == null || DetranStringUtil.ehBrancoOuNulo(wrapper.getAcaoFuncionalidadeAjustaProcessoAdministrativo())) {
                DetranWebUtils.applicationMessageException("Ação inválida.");
            }

            switch (wrapper.getAcaoFuncionalidadeAjustaProcessoAdministrativo()) {

                case "ALTERAR_ANDAMENTO":
                    alterarAndamento(wrapper, usuarioLogado);
                    break;
                case "EXECUTAR_ANDAMENTO":
                    executarAndamento(request, wrapper, usuarioLogado);
                    break;
                default:
                    DetranWebUtils.applicationMessageException("Ação inválida.");
            }

        } catch (Exception ex) {
            DetranWebUtils.addErrorMessage(ex, view);
        }
    }

    public String retiraMascara(String numeroProcesso){
        return new NumeroAnoAdapter().unmarshal(numeroProcesso);
    }
    
    /**
     *
     * @param pa
     * @param usuarioLogado
     * @param request
     * @throws AppException
     */
    private void executarAndamento(ProcessoAdministrativo pa, DetranUserDetailsWrapper usuarioLogado, HttpServletRequest request, String usuarioAlteracao) throws AppException {

        PAFluxoFase fluxoFase
                = (PAFluxoFase) getProcessoAdministrativoService()
                        .getFluxoFaseDoProcessoAdministrativo(pa);

        PAInicioFluxo inicioFluxo = getProcessoAdministrativoService().getInicioFluxoAtivoPA(pa.getId());

        if (TipoAndamentoEnum.AUTOMATICO.equals(fluxoFase.getTipoAndamento()) || inicioFluxo != null) {

            new ExecucaoAndamentoManager().iniciaExecucao(
                    new ExecucaoAndamentoEspecificoWrapper(
                            pa,
                            usuarioLogado.getOperador().getUsuario().getId(),
                            DetranReportsUtil.getReportsBaseBirtUrl(request, true),
                            null,
                            usuarioAlteracao
                    )
            );
        } else {
            DetranWebUtils.applicationMessageException("Não é possível executar o andamento deste Processo pois o mesmo não é automático.");
        }
    }

    @PUT
    @Path("buscarprocessoadministrativo")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response buscarProcessodministrativo(
            @Context HttpServletRequest request, @Context SecurityContext context, AjustaProcessoAdministrativoCriteria criteria) throws DatabaseException {

        view = getModelView();

        try {

            if (DetranStringUtil.ehBrancoOuNulo(criteria.getNumeroProcesso())) {
                DetranWebUtils.applicationMessageException("Nº Processo obrigatório.");
            }

            ProcessoAdministrativo processoAdministrativo
                    = getProcessoAdministrativoService().getProcessoAdministrativo(retiraMascara(criteria.getNumeroProcesso()));

            PAOcorrenciaStatus paOcorrenciaStatus = (PAOcorrenciaStatus) getProcessoAdministrativoService().getPAOcorrenciaStatusAtiva(processoAdministrativo.getId());

            AjustaProcessoAdministrativoWrapper wrapper = new AjustaProcessoAdministrativoWrapper();

            wrapper.setCodigoDescricaoFluxoProcessoAtual(paOcorrenciaStatus.getFluxoProcesso().getCodigo() + " - " + paOcorrenciaStatus.getFluxoProcesso().getDescricao());
            wrapper.setCodigoDescricaoAndamentoAtual(paOcorrenciaStatus.getStatusAndamento().getAndamentoProcesso().getCodigo() + "-" + paOcorrenciaStatus.getStatusAndamento().getAndamentoProcesso().getDescricao());

            wrapper.setListaFluxo(buscaListaFluxo(processoAdministrativo.getNumeroProcesso()));

            view.setObjectResponse(wrapper);

        } catch (Exception ex) {
            LOG.error("Ocorreu uma exceção para pesquisar os registros.", ex);
            DetranWebUtils.addErrorMessage(ex, view);
        }

        return getResponseOk(request);
    }

    public ListSelectItem buscaListaFluxo(String numeroProcesso) throws DatabaseException {
        ListSelectItem listSelectItem;

        List<PAFluxoProcesso> lFluxoProcesso = getProcessoAdministrativoService().getListaPAFluxoProcessoPorProcesso(numeroProcesso);
        listSelectItem = DetranWebUtils.populateItemsComboBox(lFluxoProcesso, "", "novoFluxoProcesso", " - ", "codigo", "descricao");
        
        return listSelectItem;
    }

    /**
     * http://localhost:8080/detran-processo-administrativo/execucaoandamentos/executa
     *
     * @param request
     * @param context
     * @param entrada
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    @POST
    @Path("executa")
    @Produces(MediaType.APPLICATION_JSON)
    public Response executa(
            @Context HttpServletRequest request, @Context SecurityContext context, ExecucaoAndamentoWrapper entrada) throws AppException {

        try {

            DetranUserDetailsWrapper usuarioLogado
                    = (DetranUserDetailsWrapper) getPrincipal(request);

            ProcessoAdministrativo processoAdministrativo
                    = getProcessoAdministrativoService().getProcessoAdministrativo(entrada.getNumeroProcesso());

            new ExecucaoAndamentoManager()
                    .iniciaExecucao(
                            new ExecucaoAndamentoEspecificoWrapper(
                                    processoAdministrativo,
                                    usuarioLogado.getOperador().getUsuario().getId(),
                                    DetranReportsUtil.getReportsBaseBirtUrl(request, Boolean.TRUE),
                                    entrada.getObjetoEntrada()
                            )
                    );

            return Response.ok().build();

        } catch (Exception e) {

            LOG.debug("Tratado", e);

            getControleFalha()
                    .gravarFalhaProcessoAdministrativo(
                            e,
                            "Erro ao andamento especifico - WS",
                            null,
                            entrada != null ? entrada.getNumeroProcesso() : null
                    );
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * http://localhost:8080/detran-processo-administrativo/execucaoandamentos/executa
     *
     * @param request
     * @param context
     * @param entrada
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    @POST
    @Path("executaespecifico")
    @Produces(MediaType.APPLICATION_JSON)
    public Response executaEspecifico(
            @Context HttpServletRequest request, @Context SecurityContext context, ExecucaoAndamentoWrapper entrada) throws AppException {

        try {

            DetranUserDetailsWrapper usuarioLogado
                    = (DetranUserDetailsWrapper) getPrincipal(request);

            ProcessoAdministrativo processoAdministrativo
                    = getProcessoAdministrativoService().getProcessoAdministrativo(entrada.getNumeroProcesso());

            getPAAndamentoServiceLab()
                    .executa(
                            new ExecucaoAndamentoEspecificoWrapper(
                                    processoAdministrativo,
                                    usuarioLogado.getOperador().getUsuario().getId(),
                                    DetranReportsUtil.getReportsBaseBirtUrl(request, Boolean.TRUE),
                                    entrada.getObjetoEntrada()
                            )
                    );

            return Response.ok().build();

        } catch (Exception e) {

            LOG.debug("Tratado", e);

            getControleFalha()
                    .gravarFalhaProcessoAdministrativo(
                            e,
                            "Erro ao andamento especifico - WS",
                            null,
                            entrada != null ? entrada.getNumeroProcesso() : null
                    );
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * http://localhost:8080/detran-processo-administrativo/execucaoandamentos/paandamento
     *
     * @param request
     * @param context
     * @param entrada
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    @POST
    @Path("paandamento")
    @Produces(MediaType.APPLICATION_JSON)
    public Response paandamento(
            @Context HttpServletRequest request, @Context SecurityContext context, ExecucaoAndamentoWrapper entrada) throws AppException {

        try {

            LOG.info("Andamento: {0}", entrada.getAndamento());

            ProcessoAdministrativo processoAdministrativo
                    = getProcessoAdministrativoService()
                            .getProcessoAdministrativoAtivoPorPAAndamentoCodigo(entrada.getAndamento());

            return Response.ok(processoAdministrativo).build();

        } catch (Exception e) {

            LOG.debug("Tratado", e);

            getControleFalha()
                    .gravarFalhaProcessoAdministrativo(
                            e,
                            "Erro ao andamento especifico - WS",
                            null,
                            entrada != null ? entrada.getNumeroProcesso() : null
                    );
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     *
     * @param wrapper
     * @param usuarioLogado
     * @throws AppException
     */
    private void alterarAndamento(AjustaProcessoAdministrativoWrapper wrapper, DetranUserDetailsWrapper usuarioLogado) throws AppException {

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())) {
            DetranWebUtils.applicationMessageException("Nº Processo obrigatório.");
        }

        if (wrapper.getNovoAndamento() == null || wrapper.getNovoFluxoProcesso() == null) {
            DetranWebUtils.applicationMessageException("Novo Fluxo Processo e Novo Andamento são obrigatórios.");
        }

        ProcessoAdministrativo processoAdministrativo
                = getProcessoAdministrativoService().getProcessoAdministrativo(wrapper.getNumeroProcesso());

        getProcessoAdministrativoService()
                .alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(
                        processoAdministrativo,
                        wrapper.getNovoFluxoProcesso().getCodigo(),
                        wrapper.getNovoAndamento().getCodigo(),
                        wrapper.getUsuarioAlteracao()
                );
    }

    /**
     *
     * @param request
     * @param wrapper
     * @param usuarioLogado
     * @throws AppException
     */
    private void executarAndamento(HttpServletRequest request, AjustaProcessoAdministrativoWrapper wrapper, DetranUserDetailsWrapper usuarioLogado) throws AppException {

        Authentication authenticationPrincipal = SecurityContextHolder.getContext().getAuthentication();

        try {

            Authentication authentication = DetranSpringSecurityUtil.deserialize(DetranSpringSecurityUtil.serializeAuthentication("AJUSTA_PA"));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (wrapper.getAndamento() == null) {

                if (DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())) {
                    DetranWebUtils.applicationMessageException("Nº Processo obrigatório.");
                }

                ProcessoAdministrativo pa = getProcessoAdministrativoService().getProcessoAdministrativo(wrapper.getNumeroProcesso());

                executarAndamento(pa, usuarioLogado, request,wrapper.getUsuarioAlteracao());

            } else {

                if (wrapper.getQuantidadeLote() == null || wrapper.getQuantidadeLote() < 5 || wrapper.getQuantidadeLote() > 1000) {
                    DetranWebUtils.applicationMessageException("Quantidade Lote deve ser um valor entre 5 e 1000.");
                }

                List<ProcessoAdministrativo> processos
                        = getProcessoAdministrativoService()
                                .getListaProcessoAdministrativoPorAndamentoETipoProcessoEmLote(
                                        DetranCollectionUtil.montaLista(wrapper.getAndamento().getCodigo()),
                                        wrapper.getQuantidadeLote(),
                                        wrapper.getTipoProcesso()
                                );

                for (ProcessoAdministrativo processo : processos) {

                    try {

                        executarAndamento(processo, usuarioLogado, request,wrapper.getUsuarioAlteracao());

                    } catch (Exception e) {

                        LOG.debug("Processo Administrativo {0} interrompida.", processo.getNumeroProcessoMascarado(), e);

                        getControleFalha()
                                .gravarFalhaProcessoAdministrativo(
                                        e,
                                        "Erro ao executar andamento em loteBloqueio",
                                        null,
                                        processo.getNumeroProcesso()
                                );
                    }
                }
            }
        } finally {
            SecurityContextHolder.getContext().setAuthentication(authenticationPrincipal);
        }
    }
}
