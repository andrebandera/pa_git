package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.wrapper.EntityXMLWrapper;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.WrapperUtil;
import br.gov.ms.detran.comum.util.*;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.comum.util.xml.JaxbUtil;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.criteria.MovimentacaoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.*;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoMotivoEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAStatusEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.*;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.MovimentacaoPaWSWrapper;

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
import java.io.File;
import java.io.IOException;
import java.util.*;

@ManagedBean
@Path("/pa/movimentacaopa")
public class MovimentacaoPAResource extends PaResource<MovimentacaoWrapper, MovimentacaoCriteria>{
    
    private static final Logger LOG = Logger.getLogger(MovimentacaoPAResource.class);
    
    private IApoioService apoioService;
    
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }
    
    @PUT
    @Path("movimentacaopa")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response movimentacaoPA(@Context HttpServletRequest request, @Context SecurityContext context) throws AppException {

        LOG.debug("Início movimentacao PA.");
        
        List listaJson = new ArrayList();

        try {

            listaJson.addAll(processoAdministrativoService.getMovimentaoPa());
            
            return Response.ok(listaJson).build();

        } catch (Exception e) {
            getControleFalha().gravarFalha(e, "Erro ao recuperar movimentacao PA - WS");

            return Response.status(Response.Status.BAD_REQUEST).entity(listaJson).build();
        }
    }
    
    @PUT
    @Path("confirmamovimentacaopa")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmaMovimentacaoPA(@Context HttpServletRequest request, 
                                           MovimentacaoPaWSWrapper wrapper) throws AppException, IOException {

        LOG.debug("Início confirmar movimentação PA pelo BPMS.");
        
        ProcessoAdministrativo pa = null;
        
        try {
            
            validaDadosObrigatorios(wrapper);
            
            processoAdministrativoService.validarPAParaExecucaoServico(wrapper.getNumeroProcessoAdministrativo(), "confirmamovimentacaopa");
            
            pa = (ProcessoAdministrativo) processoAdministrativoService
                        .getProcessoAdministrativoPorNumeroProcessoAtivo(wrapper.getNumeroProcessoAdministrativo());
            
            if (pa == null) {
                throw new Exception("Processo Administrativo não encontrado");
            }
            new ExecucaoAndamentoManager().
                    iniciaExecucao(new ExecucaoAndamentoEspecificoWrapper(pa,
                                                                   null, 
                                                                   null, 
                                                                   wrapper));
            
            return Response.ok().build();

        } catch (Exception e) {
            
            getControleFalha().gravarFalhaProcessoAdministrativo(e, 
                                                                 "MOVIMENTACAO_PA", 
                                                                 pa != null ? pa.getCpf() : null, 
                                                                 wrapper != null ? wrapper.getNumeroProcessoAdministrativo() : null);
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * @param wrapper
     * @throws AppException 
     */
    private void validaDadosObrigatorios(MovimentacaoPaWSWrapper wrapper) throws AppException {
        
        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcessoAdministrativo())) {
            DetranWebUtils.applicationMessageException("Número Processo é obrigatório.");
        }
        
        if (null == wrapper.getAcao()) {
            DetranWebUtils.applicationMessageException("Ação é obrigatório.");
        }
        
        if (null == wrapper.getMotivo()) {
            DetranWebUtils.applicationMessageException("Motivo é obrigatório.");
        }
    }
    
    @PUT
    @Path("buscarProcessoAdministrativo")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response buscarProcessoAdministrativo(@Context HttpServletRequest request, 
                                                 @Context SecurityContext context, 
                                                 String numeroProcesso) throws AppException {
        
        view = getModelView();
        
        try {
            
            if (DetranStringUtil.ehBrancoOuNulo(numeroProcesso)) {
                DetranWebUtils.applicationMessageException("O número do processo é obrigatório.");
                return getResponseOk(request);
            }
            
            numeroProcesso = new NumeroAnoAdapter().unmarshal(numeroProcesso);

            ProcessoAdministrativo pa = (ProcessoAdministrativo) processoAdministrativoService.getProcessoAdministrativoPorNumeroProcessoAtivo(numeroProcesso);
            PAOcorrenciaStatus ocorrenciaAtual = (PAOcorrenciaStatus) processoAdministrativoService.getPAOcorrenciaStatusAtiva(pa.getId());
            
            validar(pa, ocorrenciaAtual);

            MovimentacaoWrapper wrapper = new MovimentacaoWrapper();
            wrapper.setProcessoAdministrativo(pa);
            wrapper.setNomeCondutor(getApoioService().getNomeDoAtendimento(pa.getAtendimento()));
            wrapper.setAndamentoAtual(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getDescricao());

            List<ProcessoAdministrativoInfracao> listaInfracoes = 
                    processoAdministrativoService.getProcessoAdministrativoInfracaoPorProcessoId(pa.getId());

            List<ProcessoAdministrativoInfracaoWrapper> listaInfracoesWrapper = new ArrayList<>();

            if (!DetranCollectionUtil.ehNuloOuVazio(listaInfracoes)) {
                for (ProcessoAdministrativoInfracao infracao : listaInfracoes) {
                    ProcessoAdministrativoInfracaoWrapper infracaoWrapper = new ProcessoAdministrativoInfracaoWrapper();
                    infracaoWrapper.setEntidade(infracao);
                    infracaoWrapper.setReativarPontuacao(Boolean.FALSE);
                    infracaoWrapper.setDescricaoInfracao(getApoioService().getDescricaoInfracao(infracao.getInfracao()));

                    listaInfracoesWrapper.add(infracaoWrapper);
                }
            }

            wrapper.setListaInfracoes(listaInfracoesWrapper);

            view.setObjectResponse(wrapper);
            
        } catch (AppException e) {
            DetranWebUtils.addErrorMessage(e, view);
        }
        
        return getResponseOk(request);
    }
    
    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> listSelectItem = new ArrayList<>();

        listSelectItem.add(new ListSelectItem("acao", DetranWebUtils.getSelectItems(MovimentacaoAcaoEnum.class, 
                                                                                    Boolean.TRUE, 
                                                                                    MovimentacaoAcaoEnum.ARQUIVAR_COM_BLOQUEIO)));
        
        listSelectItem.add(new ListSelectItem("motivoCancelar", DetranWebUtils.getSelectItems(Boolean.TRUE, 
                                                                                              MovimentacaoMotivoEnum.CADASTRAMENTO_INDEVIDO, 
                                                                                              MovimentacaoMotivoEnum.MOTIVO_ADMINISTRATIVO)));
        
        listSelectItem.add(new ListSelectItem("motivoSuspender", DetranWebUtils.getSelectItems(Boolean.TRUE, 
                                                                                              MovimentacaoMotivoEnum.DETERMINACAO_JUDICIAL, 
                                                                                              MovimentacaoMotivoEnum.SEPEN)));
        
        listSelectItem.add(new ListSelectItem("motivoArquivar", DetranWebUtils.getSelectItems(Boolean.TRUE, 
                                                                                              MovimentacaoMotivoEnum.MORTE, 
                                                                                              MovimentacaoMotivoEnum.DETERMINACAO_JUDICIAL,
                                                                                              MovimentacaoMotivoEnum.PENA_CUMPRIDA,
                                                                                              MovimentacaoMotivoEnum.PELO_DIRETOR_PRESIDENTE,
                                                                                              MovimentacaoMotivoEnum.PELO_DIRETOR_ADJUNTO,
                                                                                              MovimentacaoMotivoEnum.MOTIVO_ADMINISTRATIVO,
                                                                                              MovimentacaoMotivoEnum.RECURSO_ACOLHIDO)));
        
        listSelectItem.add(new ListSelectItem("motivoRetirarSuspensao", DetranWebUtils.getSelectItems(Boolean.TRUE, 
                                                                                                    MovimentacaoMotivoEnum.DETERMINACAO_JUDICIAL, 
                                                                                                    MovimentacaoMotivoEnum.SEPEN)));

        return listSelectItem;
    }

    @Override
    public Response novo(HttpServletRequest request, SecurityContext context, MovimentacaoWrapper entity) {
        return super.novo(request, context, entity);
    }
    
    @Override
    protected void executarGravacao(HttpServletRequest request, IBaseEntity entidade) throws AppException {

        DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);

        MovimentacaoWrapper wrapper = (MovimentacaoWrapper) entidade;

        wrapper.setRecursoCanceladoWrapper(new RecursoOnlineCanceladoWrapper(" Arquivar/Cancelar PA. ",
                DetranHTTPUtil.getClientIpAddr(request),
                usuarioLogado.getUsername()));

        validarPAAgravado(wrapper.getProcessoAdministrativo(), wrapper.getEntidade().getMovimentacaoAcao(), wrapper.getEntidade().getMotivo());

        validarPAApensado(wrapper.getProcessoAdministrativo(), wrapper.getEntidade().getMovimentacaoAcao(), wrapper.getEntidade().getMotivo());

        processoAdministrativoService.gravarMovimentacao(wrapper, usuarioLogado);

        new ExecucaoAndamentoManager()
                .iniciaExecucao(
                        new ExecucaoAndamentoEspecificoWrapper(
                        wrapper.getProcessoAdministrativo(), 
                        usuarioLogado.getOperador().getUsuario().getId(), 
                        null,
                        null
                    )
                );
    }
    
    /**
     * 
     * @param pa
     * @param acao
     * @param motivo
     * @throws AppException 
     */
    private void validarPAApensado(ProcessoAdministrativo pa, MovimentacaoAcaoEnum acao, MovimentacaoMotivoEnum motivo) throws AppException {
        
        ProcessoAdministrativoApensado apensado = processoAdministrativoService.getApensadoPorPAOriginal(pa.getId());
        
        if(apensado != null){
            validarAcaoEMotivoPAParaMovimentar(acao, motivo);
        }
    }

    /**
     * 
     * @param acao
     * @param motivo
     * @throws AppException 
     */
    private void validarAcaoEMotivoPAParaMovimentar(MovimentacaoAcaoEnum acao, MovimentacaoMotivoEnum motivo) throws AppException {
        
        List<MovimentacaoAcaoEnum> acoesPermitidas 
            = DetranCollectionUtil
                .montaLista(
                    MovimentacaoAcaoEnum.ARQUIVAR,
                    MovimentacaoAcaoEnum.RETIRAR_SUSPENSAO,
                    MovimentacaoAcaoEnum.SUSPENDER
                );
        
        if(!acoesPermitidas.contains(acao)){
            DetranWebUtils.applicationMessageException("Não é possível realizar a ação. O processo está APENSADO/AGRAVADO com outros processos.");
        }
        
        if(MovimentacaoAcaoEnum.ARQUIVAR.equals(acao) && !MovimentacaoMotivoEnum.PENA_CUMPRIDA.equals(motivo)){
            DetranWebUtils.applicationMessageException("Não é possível realizar a ação. O processo está APENSADO/AGRAVADO com outros processos.");
        }
    }

    /**
     * 
     * @param pa
     * @param acao
     * @param motivo
     * @throws AppException 
     */
    private void validarPAAgravado(ProcessoAdministrativo pa, MovimentacaoAcaoEnum acao, MovimentacaoMotivoEnum motivo) throws AppException {
        
        ProcessoAdministrativoAgravado agravado = processoAdministrativoService.getAgravadoPorPAOriginal(pa.getId());
        
        if(agravado != null){
            validarAcaoEMotivoPAParaMovimentar(acao, motivo);
        }
    }

    /**
     * Validações
     * 
     * @param pa 
     */
    private void validar(ProcessoAdministrativo pa, PAOcorrenciaStatus ocorrenciaAtual) throws AppException {
        
        if (null == pa)
            DetranWebUtils.applicationMessageException("Processo inválido");
        
        if (!PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_SUSPENSAO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())
                && !PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_RETIRADA_DA_SUSPENSAO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
            
            PAFluxoFase pAFluxoFase = (PAFluxoFase) processoAdministrativoService.getFluxoFaseDoProcessoAdministrativo(pa);
            
            List<Integer> listaPaFluxoProcesso = 
                    DetranCollectionUtil.montaLista(PAFluxoProcessoConstante.FLUXO_SUSPENSAO,
                                                    PAFluxoProcessoConstante.FLUXO_CANCELAR,
                                                    PAFluxoProcessoConstante.FLUXO_ARQUIVAR);
            
            PAFluxoAndamento paFluxoAndamento = 
                    (PAFluxoAndamento) processoAdministrativoService
                            .getPaFluxoAndamentoPorPAFluxoFaseEPAFluxoProcesso(pAFluxoFase, listaPaFluxoProcesso);
            
            if (null == paFluxoAndamento) {
                DetranWebUtils.applicationMessageException("Não é possível fazer movimentações para esse processo.");
            }
        }
        
        if (PAStatusEnum.SUSPENSO.getCodigo() == ocorrenciaAtual.getStatusAndamento().getStatus().getCodigo()) {
            
            if (PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_SUSPENSAO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
                DetranWebUtils.applicationMessageException("A informação não foi recebida pelo BPMS.");
            }
            
        } else if (!SituacaoOcorrenciaEnum.INICIADO.equals(ocorrenciaAtual.getSituacao())) {
            DetranWebUtils.applicationMessageException("Andamento está com a situação finalizada. Verificar andamento e fluxo do processo.");
        }
    }

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder c) throws Exception {
        
        MovimentacaoCriteria criteria = (MovimentacaoCriteria) c;
        
        if (null == criteria.getId()) {
            view.setEntity(WrapperUtil.convertEntityToWrapper2(genericService.pesquisar(criteria), getEntityClass()));
        } else {
            
            List lista = genericService.pesquisar(criteria);
            if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {

                for (Object object : lista) {

                    Movimentacao entidade = (Movimentacao) object;

                    MovimentacaoWrapper wrapper = new MovimentacaoWrapper();

                    wrapper.setEntidade(entidade);
                    
                    PAOcorrenciaStatus ocorrenciaAtual = (PAOcorrenciaStatus) processoAdministrativoService.getPAOcorrenciaStatusAtiva(wrapper.getEntidade().getProcessoAdministrativo().getId());
                    wrapper.setAndamentoAtual(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getDescricao());
                        
                    Usuario usuario = (Usuario) getApoioService().getUsuario(wrapper.getEntidade().getUsuario());
                    wrapper.setUsuarioMovimentacao(getApoioService().getNomeUsuarioPeloId(usuario));
                    
                    wrapper.setAndamentoAnterior(wrapper.getEntidade().getFluxoFase().getAndamentoProcesso().getDescricao());

                    wrapper.setIndicativoReativacaoPontuacao(processoAdministrativoService.getIndicativoReativacaoInfracao(entidade.getId(), wrapper.getEntidade().getProcessoAdministrativo().getId()));

                    view.addEntity(wrapper);
                }
            }
        }
        
        view.setRowcount(genericService.contarPesquisa(criteria));
    }
    
    @PUT
    @Path("search2")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response search2(@Context HttpServletRequest request, 
                            @Context SecurityContext context, 
                            MovimentacaoCriteria criteria) throws DatabaseException {

        view = getModelView();

        try {

            if (!view.hasError()) {

                DetranWebUtils.verificarValidation(criteria, view);
                
                if(criteria.getDataFim() != null && criteria.getDataInicio() != null){
                    if(criteria.getDataFim().before(criteria.getDataInicio())){
                        DetranWebUtils.applicationMessageException("Data Fim não pode ser menor que a Data Início.");
                    }
                    if(criteria.getDataFim().after(Utils.addYear(criteria.getDataInicio(), 1))){
                        DetranWebUtils.applicationMessageException("O intervalo não pode ser maior que um ano.");
                    }
                }
                
                List<MovimentacaoWrapper> lista = WrapperUtil.convertEntityToWrapper2(genericService.pesquisar(criteria), getEntityClass());
                if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {

                    for (MovimentacaoWrapper wrapper : lista) {

                        Usuario usuario = (Usuario) getApoioService().getUsuario(wrapper.getEntidade().getUsuario());
                        wrapper.setUsuarioMovimentacao(getApoioService().getNomeUsuarioPeloId(usuario));

                        view.addEntity(wrapper);
                    }
                }
                
                view.setRowcount(genericService.contarPesquisa(criteria));  
                
                if (criteria == null || criteria.isPopulateList()) {
                    carregarListSelectItem(request, criteria, SEARCH_ACTION);
                }

                if (view.getRowcount() != null && view.getRowcount().intValue() <= 0) {
                    if (criteria != null && criteria.getEmptyMessage() != null && criteria.getEmptyMessage()) {
                        DetranWebUtils.addWarningMessage("application.message.consulta.empty", view);
                    }
                }
            }

        } catch (Exception ex) {
            DetranWebUtils.addErrorMessage(ex, view);
        }

        return getResponseOk(request);
    }
    
    @PUT
    @Path("emitir")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response emitir(@Context HttpServletRequest request,
                           @Context SecurityContext context,
                           MovimentacaoCriteria criteria) throws Exception {

        try {

            if (criteria == null || criteria.isCriteriaEmpty()) {
                carregarListSelectItem(request, criteria, SEARCH_ACTION);
                DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
            }

            view = getModelView();
            view.addEntity(new MovimentacaoWrapper());

            FormatoRelatorioEnum formato = FormatoRelatorioEnum.PDF;
            Map<String, String> params = new HashMap<>();
            
            List lista = new ArrayList<>();
            
            DetranWebUtils.verificarValidation(criteria, view);
                
            if (criteria.getDataFim() != null && criteria.getDataInicio() != null) {
                
                if (criteria.getDataFim().before(criteria.getDataInicio())) {
                    DetranWebUtils.applicationMessageException("Data Fim não pode ser menor que a Data Início.");
                }
                
                if (criteria.getDataFim().after(Utils.addYear(criteria.getDataInicio(), 1))) {
                    DetranWebUtils.applicationMessageException("O intervalo não pode ser maior que um ano.");
                }
            }

            lista = WrapperUtil.convertEntityToWrapper2(genericService.pesquisar(criteria), getEntityClass());
            if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {

                for (Object object : lista) {
                    
                    MovimentacaoWrapper wrapper = (MovimentacaoWrapper) object;

                    Usuario usuario = (Usuario) getApoioService().getUsuario(wrapper.getEntidade().getUsuario());
                    wrapper.setUsuarioMovimentacao(getApoioService().getNomeUsuarioPeloId(usuario));

                    view.addEntity(wrapper);
                }
            }

            if (criteria == null || criteria.isPopulateList()) {
                carregarListSelectItem(request, criteria, SEARCH_ACTION);
            }

            if (view.getRowcount() != null && view.getRowcount().intValue() <= 0) {
                if (criteria != null && criteria.getEmptyMessage() != null && criteria.getEmptyMessage()) {
                    DetranWebUtils.addWarningMessage("application.message.consulta.empty", view);
                }
            }

            if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {

                EntityXMLWrapper xmlEntity = new EntityXMLWrapper();
                
                for (Object object : lista) {
                    
                    StringBuilder sbFiltros = new StringBuilder();
                    
                    if (!DetranStringUtil.ehBrancoOuNulo(criteria.getNumeroProcesso())) {
                        sbFiltros.append("Número Processo: ");
                        sbFiltros.append(new NumeroAnoAdapter().marshal(criteria.getNumeroProcesso()));
                        sbFiltros.append("</br>");
                    }
                    
                    if (criteria.getAcao() != null) {
                        sbFiltros.append("Ação: ");
                        sbFiltros.append(criteria.getAcao().toString());
                        sbFiltros.append("</br>");
                    }
                    
                    if (criteria.getDataInicio() != null) {
                        sbFiltros.append("Data Início: ");
                        sbFiltros.append(Utils.formatDate(criteria.getDataInicio(), "dd/MM/yyyy"));
                        sbFiltros.append("</br>");
                    }
                    
                    if (criteria.getDataFim() != null) {
                        sbFiltros.append("Data Processamento: ");
                        sbFiltros.append(Utils.formatDate(criteria.getDataFim(), "dd/MM/yyyy"));
                        sbFiltros.append("</br>");
                    }
                    
                    DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);
                    if (null != usuarioLogado) {
                        sbFiltros.append("Usuário Impressão: ");
                        sbFiltros.append(usuarioLogado.getName());
                        sbFiltros.append("</br>");
                    }
                    
                    xmlEntity.addFiltro("", sbFiltros.toString());
                    
                    xmlEntity.addDado(processoAdministrativoService.montarMovimentacaoPaXml(object));
                }

                String xml = JaxbUtil.marshal(xmlEntity, 
                                              EntityXMLWrapper.class,
                                              MovimentacaoPAXmlWrapper.class);

                String nomeArquivo = Utils.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmss") + "rel_movimentacao_pa.xml";

                DetranArquivoHelper.saveFile(nomeArquivo, xml.getBytes());

                params.put("uid", nomeArquivo);

                String urlRelatorio = 
                        DetranReportsUtil.getReportParamsBirtUrl(
                                "consultamovimentacoespa",
                                formato.getRptFormat(),
                                params,
                                "relatorios/processoadministrativo/consultas/");

                getEntity().setRelatorio(DetranHTTPUtil.download(DetranReportsUtil.getReportsBaseBirtUrl(request, true) + urlRelatorio));

                File relatorio = new File(nomeArquivo);

                if (relatorio.exists()) {
                    relatorio.delete();
                }
            }

        } catch (AppException ex) {
            DetranWebUtils.addErrorMessage(ex, view);
        } catch (Exception ex) {
            DetranWebUtils.addErrorMessage(ex, view);
        }

        return Response.ok().entity(view).build();
    }
}