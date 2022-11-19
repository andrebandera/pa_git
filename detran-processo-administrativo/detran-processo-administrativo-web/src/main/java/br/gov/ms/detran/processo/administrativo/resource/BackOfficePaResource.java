package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Grupo;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.SelectItem;
import br.gov.ms.detran.comum.projeto.util.WrapperUtil;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranHTTPUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.ExceptionUtils;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.ace.IAcessoService;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.criteria.BackOfficePaCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnline;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnlineFalha;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.PassoRecursoOnlinePAEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoSituacaoPAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.BackOfficePaWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWrapper;

import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@ManagedBean
@Path("backoffices")
public class BackOfficePaResource extends PaResource<BackOfficePaWrapper, BackOfficePaCriteria> {

    private static final Logger LOG = Logger.getLogger(BackOfficePaResource.class);
    
    private IAcessoService acessoService;
    
    public IAcessoService getAcessoService() {
        
        if (acessoService == null) {
            acessoService = (IAcessoService) JNDIUtil.lookup("ejb/AcessoService");
        }
        
        return acessoService;
    }

    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> listSelectItem = new ArrayList<>();

        List<SelectItem> situacao
            = DetranWebUtils
                .getSelectItems(
                    RecursoSituacaoPAEnum.class,
                    false,
                    RecursoSituacaoPAEnum.CADASTRADO,
                    RecursoSituacaoPAEnum.INICIADO
                );
        
        List<SelectItem> destino = defineFiltroDestino(request);
        
        List<SelectItem> tipoRecurso
            = DetranWebUtils
                .getSelectItems(
                    TipoFasePaEnum.class,
                    false,
                    TipoFasePaEnum.CURSO_EXAME,
                    TipoFasePaEnum.DESENTRANHAMENTO,
                    TipoFasePaEnum.JARI,
                    TipoFasePaEnum.NAO_CONHECIMENTO,
                    TipoFasePaEnum.PROVIMENTO
                );

        listSelectItem.add(new ListSelectItem("situacao", situacao));
        listSelectItem.add(new ListSelectItem("tipoRecurso", tipoRecurso));
        listSelectItem.add(new ListSelectItem("destinoRecurso", destino));
        
        List<SelectItem> indiceTempestividade = DetranWebUtils.getSelectItems(BooleanEnum.class, Boolean.FALSE);
        listSelectItem.add(new ListSelectItem("indiceTempestividade", indiceTempestividade));

        return listSelectItem;
    }
    
    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {

        DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);
        
        BackOfficePaCriteria c = (BackOfficePaCriteria) criteria;
        
        c.setPasso(PassoRecursoOnlinePAEnum.PASSO_FINAL);
        c.setAtivo(AtivoEnum.ATIVO);
        
        c.setListaDestinoExcluir(
            getDestinoPorGrupoQueDevemSerExcluidosDaListagem(usuarioLogado.getUsuarioLocal().getId())
        );

        view
            .setEntity(
                    WrapperUtil.convertEntityToWrapper2(genericService.pesquisar(criteria), getEntityClass())
            );

        view.setRowcount(genericService.contarPesquisa(criteria));
    }

    @Override
    protected void executarEditar(Serializable id) throws Exception {
        super.executarEditar(id);
        getEntity().setDataNotificacao(getProcessoAdministrativoService().getDataNotificacaoDoRecursoOnline2(getEntity().getEntidade()));
        getEntity().setDataPrazoLimite(getProcessoAdministrativoService().getDataPrazoLimiteNotificacaoDoRecursoOnline(getEntity().getEntidade()));
        getEntity().setDocumentos(getProcessoAdministrativoService().buscarDocumentosDoRecursoOnline((Long) id));
    }

    @POST
    @Path("recusar")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response recusar(
            @Context HttpServletRequest request, @Context SecurityContext context, BackOfficePaWrapper wrapper) {

        try {

            DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);

            wrapper.setCpfUsuario(usuarioLogado.getUsername());
            wrapper.setIp(DetranHTTPUtil.getClientIpAddr(request));

            getProcessoAdministrativoService().recusarRecurso(wrapper);
            LOG.info("Recusa:" + wrapper.getMotivoRecusa());

        } catch (DatabaseException e) {

            LOG.error("Ocorreu uma exceção ao registrar.", e);
            DetranWebUtils.addErrorMessage("recursoonlinepa.M1", view);

            registraFalha(e, wrapper);

        } catch (AppException e) {

            LOG.error("Ocorreu uma exceção ao registrar.", e);
            DetranWebUtils.addErrorMessage(e, view);

            registraFalha(e, wrapper);

        } catch (Exception e) {

            LOG.error("Ocorreu uma exceção ao registrar.", e);
            DetranWebUtils.addErrorMessage("recursoonlinepa.M1", view);

            registraFalha(e, wrapper);
        }

        return getResponseOk(request);
    }

    @Override
    protected void executarGravacao(HttpServletRequest request, IBaseEntity entidade) throws AppException {

        BackOfficePaWrapper wrapper = (BackOfficePaWrapper) entidade;

        try {
            DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);
            String urlReportBirt = DetranReportsUtil.getReportsBaseBirtUrl(request, true);

            wrapper.setCpfUsuario(usuarioLogado.getUsername());
            wrapper.setIp(DetranHTTPUtil.getClientIpAddr(request));

            wrapper.setUsuario(usuarioLogado.getUsuarioLocal().getId().toString());
            wrapper.setUrlReportBirt(urlReportBirt);

            RecursoWrapper recursoWrapper = getProcessoAdministrativoService().gravarRecursoOnline(wrapper, usuarioLogado);
            getProcessoAdministrativoService().gravarArquivoProtocoloRecursoOnline(recursoWrapper);

            getProcessoAdministrativoService().enviarArquivosRecursoOnlineParaFTP(recursoWrapper.getRecursoEfetivado(), urlReportBirt);


            new ExecucaoAndamentoManager()
                    .iniciaExecucao(
                            new ExecucaoAndamentoEspecificoWrapper(
                                    getProcessoAdministrativoService().getProcessoAdministrativo(wrapper.getEntidade().getProcessoAdministrativo().getId()),
                                    usuarioLogado.getOperador().getUsuario().getId(),
                                    urlReportBirt,
                                    wrapper
                            )
                    );

        } catch (DatabaseException e) {

            LOG.error("Ocorreu uma exceção ao registrar.", e);
            DetranWebUtils.addErrorMessage("recursoonlinepa.M1", view);

            registraFalha(e, wrapper);

        } catch (AppException e) {

            LOG.error("Ocorreu uma exceção ao registrar.", e);
            DetranWebUtils.addErrorMessage(e, view);

            registraFalha(e, wrapper);

        } catch (Exception e) {

            LOG.error("Ocorreu uma exceção ao registrar.", e);
            DetranWebUtils.addErrorMessage("recursoonlinepa.M1", view);

            registraFalha(e, wrapper);
        }
    }

    /**
     * @param e
     * @param wrapper
     */
    private void registraFalha(Exception e, BackOfficePaWrapper wrapper) {

        RecursoPAOnlineFalha falha
                = new RecursoPAOnlineFalha(new ExceptionUtils().getStack(e),
                wrapper.getEntidade() != null ? wrapper.getEntidade().getNumeroProcesso() : "",
                wrapper.getEntidade() != null ? wrapper.getEntidade().getCpf() : "",
                "Erro na Funcionalidade BackOffice");

        getProcessoAdministrativoService().registraFalhaRecursoOnline(falha);
    }

    /**
     * 
     * @param request
     * @return
     * @throws AppException 
     */
    private List<SelectItem> defineFiltroDestino(HttpServletRequest request) throws AppException {
        
        List<SelectItem> destino = new ArrayList();
        
        DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);
        
        destino.addAll(
            DetranWebUtils
                .getSelectItems(
                    OrigemDestinoEnum.class,
                    false,
                    getDestinoPorGrupoQueDevemSerExcluidosDaListagem(usuarioLogado.getUsuarioLocal().getId()).toArray()
                )
        );
        
        return destino;
    }
    
    /**
     * 
     * @param idUsuarioLogado
     * @param descricaoGrupo
     * @param origem
     * @return
     * @throws AppException 
     */
    private List<OrigemDestinoEnum> getDestinoPorGrupoQueDevemSerExcluidosDaListagem(Long idUsuarioLogado) throws AppException {
        
        List<OrigemDestinoEnum> lOrigem = Arrays.asList(OrigemDestinoEnum.values());
        
        List<Grupo> lGrupo = null;
        
        String PROCESSO_ADMINISTRATIVO_BACKOFFICE_SEPEN = "PROCESSO ADMINISTRATIVO BACKOFFICE SEPEN";
        
        lGrupo = 
            getAcessoService()
                .getGruposDoUsuarioLogado(
                    BigDecimal.ZERO.intValue(), 
                    BigDecimal.ONE.intValue(), 
                    idUsuarioLogado, 
                    PROCESSO_ADMINISTRATIVO_BACKOFFICE_SEPEN
                );
        
        if(!DetranCollectionUtil.ehNuloOuVazio(lGrupo)) {
        
            lOrigem = DetranCollectionUtil.montaLista(OrigemDestinoEnum.ORGAO_AUTUADOR, OrigemDestinoEnum.PODER_JUDICIARIO, OrigemDestinoEnum.CETRAN);
        
        }else {
        
            String PROCESSO_ADMINISTRATIVO_BACKOFFICE_JARI = "PROCESSO ADMINISTRATIVO BACKOFFICE JARI";

            lGrupo =
                getAcessoService()
                    .getGruposDoUsuarioLogado(
                        BigDecimal.ZERO.intValue(), 
                        BigDecimal.ONE.intValue(), 
                        idUsuarioLogado, 
                        PROCESSO_ADMINISTRATIVO_BACKOFFICE_JARI
                    );

            if(!DetranCollectionUtil.ehNuloOuVazio(lGrupo)) {
                lOrigem = DetranCollectionUtil.montaLista(OrigemDestinoEnum.ORGAO_AUTUADOR, OrigemDestinoEnum.PODER_JUDICIARIO, OrigemDestinoEnum.JARI, OrigemDestinoEnum.SEPEN);
            }
        }
        
        if(DetranCollectionUtil.ehNuloOuVazio(lOrigem)) {
            lOrigem = new ArrayList();
        }
        
        return lOrigem;
    }
}