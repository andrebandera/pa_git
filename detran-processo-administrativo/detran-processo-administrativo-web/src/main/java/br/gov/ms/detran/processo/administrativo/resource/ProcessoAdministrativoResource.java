package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.adm.Contato;
import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoContatoEnum;
import br.gov.ms.detran.comum.core.projeto.wrapper.EntityXMLWrapper;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.util.DetranArquivoHelper;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranHTTPUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.adapter.TelefoneAdapter;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.comum.util.xml.JaxbUtil;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.criteria.ProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoXmlWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.ConsultaPaWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.InformacaoProvaWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.ProcessoAdministrativoDesistenteWrapper;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

@ManagedBean
@Path("processoadministrativos")
public class ProcessoAdministrativoResource extends PaResource<ProcessoAdministrativoWrapper, ProcessoAdministrativoCriteria> {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoResource.class);

    private IApoioService apoioService;
    
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> listSelectItem = new ArrayList<>();

        listSelectItem.add(new ListSelectItem("ativo", DetranWebUtils.getSelectItems(AtivoEnum.class, Boolean.TRUE)));
        listSelectItem.add(new ListSelectItem("tipo", DetranWebUtils.getSelectItems(TipoProcessoEnum.class, Boolean.TRUE)));
        listSelectItem.add(DetranWebUtils.populateItemsComboBox(getProcessoAdministrativoService().getAndamentos(), "", "andamento", "-", "codigo", "descricao"));
        
        List listaCenarios = getProcessoAdministrativoService().getListOrigemInstauracao();
        DetranCollectionUtil.ordenaLista(listaCenarios, "descricao", true);
        listSelectItem.add(DetranWebUtils.populateItemsComboBox(listaCenarios, "", "cenario", "", "descricao"));
        
        listSelectItem.add(new ListSelectItem("origem", DetranWebUtils.getSelectItems(OrigemEnum.class, Boolean.TRUE)));

        return listSelectItem;
    }

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder c) throws Exception {
        
        ProcessoAdministrativoCriteria criteria = (ProcessoAdministrativoCriteria) c;

        if (criteria == null || criteria.isCriteriaEmpty()) {
            carregarListSelectItem(request, criteria, SEARCH_ACTION);
            DetranWebUtils.addErrorMessage("severity.error.criteria.isEmpty", view);
        } else {

            if (null == criteria.getId()) {

                view.setEntity(genericService.pesquisar(criteria));

            } else {

                List lista = genericService.pesquisar(criteria);

                if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {
                    for (Object object : lista) {

                        ProcessoAdministrativoWrapper wrapper = (ProcessoAdministrativoWrapper) object;

                        if (null != criteria.getId()) {
                            wrapper.setNomeCondutor(getApoioService().getNomeDoAtendimento(wrapper.getEntidade().getAtendimento()));
                            wrapper.setPenalidadeProcesso((PAPenalidadeProcesso) processoAdministrativoService.getPenalidadePorPA(wrapper.getEntidade().getId()));
                            wrapper.setPaComplemento((PAComplemento) processoAdministrativoService.getComplementoDoPA(wrapper.getEntidade().getNumeroProcesso()));
                            wrapper.setInformacaoProva((InformacaoProvaWrapper) processoAdministrativoService.getInformacoesProva(wrapper.getEntidade()));
                            PAFluxoFase fluxoFase = (PAFluxoFase) processoAdministrativoService.getFluxoFaseDoProcessoAdministrativoParaConsulta(wrapper.getEntidade());
                            if (fluxoFase != null
                                    && fluxoFase.getPrioridadeFluxoAmparo() != null
                                    && fluxoFase.getPrioridadeFluxoAmparo().getFaseProcessoAdm() != null) {
                                wrapper.setDescricaoFase(fluxoFase.getPrioridadeFluxoAmparo().getFaseProcessoAdm().getDescricao());
                            }
                            wrapper.setDesistenteEnum(processoAdministrativoService.getInformacaoDesistente(wrapper.getEntidade().getId()));
                            
                            TelefoneAdapter telAdapter = new TelefoneAdapter();
                            
                            List<Contato> contatos = apoioService.getContatoPelaPessoaETipoContatoOrderByIDDesc(wrapper.getEntidade().getNumeroDetran(), TipoContatoEnum.TELEFONE_CELULAR);
                            
                            if(contatos!=null && !contatos.isEmpty() && contatos.get(0)!=null){
                                wrapper.setNumeroContato(telAdapter.marshalSemDDD(contatos.get(0).getNumeroContato()));
                            }else{
                                contatos = apoioService.getContatoPelaPessoaETipoContatoOrderByIDDesc(wrapper.getEntidade().getNumeroDetran(), TipoContatoEnum.TELEFONE_FIXO);
                                if(contatos!=null && !contatos.isEmpty() && contatos.get(0)!=null){
                                    wrapper.setNumeroContato(telAdapter.marshalSemDDD(contatos.get(0).getNumeroContato()));
                                }
                            }
                            contatos = apoioService.getContatoPelaPessoaETipoContatoOrderByIDDesc(wrapper.getEntidade().getNumeroDetran(), TipoContatoEnum.E_MAIL);
                            
                            
                            if(contatos!=null && !contatos.isEmpty() && contatos.get(0)!=null){
                                wrapper.setEmail(contatos.get(0).getNumeroContato());
                            }
                            
                            wrapper.setProcessosFilho(processoAdministrativoService.getListaProcessosFilho(wrapper.getEntidade().getId()));
                        }

                        view.addEntity(wrapper);
                    }
                }
            }

            view.setRowcount(genericService.contarPesquisa(criteria));
        }
    }

    @PUT
    @Path("desistentes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response desistentes(@Context HttpServletRequest request, @Context SecurityContext context) throws AppException {

        LOG.debug("Início PA desistentes.");

        List listaJson = new ArrayList();

        try {

            listaJson.addAll(getProcessoAdministrativoService().getProcessoAdministrativoDesistentes());

            return Response.ok(listaJson).build();

        } catch (Exception e) {

            LOG.debug("Erro capturado.", e);
            getControleFalha().gravarFalha(e, "Erro ao recuperar PAs desistentes - WS");

            return Response.status(Response.Status.BAD_REQUEST).entity(listaJson).build();
        }
    }

    @PUT
    @Path("confirmadesistencia")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmaDesistencia(
            @Context HttpServletRequest request, ProcessoAdministrativoDesistenteWrapper wrapper) throws AppException {

        LOG.debug("Início confirmar desistência pelo BPMS.");

        try {

            if (wrapper == null
                    || DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProtocolo())
                    || DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())) {

                DetranWebUtils.applicationMessageException("PA.M1");
            }

            getProcessoAdministrativoService().validarPAParaExecucaoServico(wrapper.getNumeroProcesso(), "confirmadesistencia");
            
            new ExecucaoAndamentoManager().
                    iniciaExecucao(new ExecucaoAndamentoEspecificoWrapper(processoAdministrativoService.getProcessoAdministrativo(wrapper.getNumeroProcesso()),
                                                                     null,
                                                                     null,
                                                                     wrapper));
            
            return Response.ok().build();

        } catch (Exception e) {
            LOG.debug("Tratado", e);
            if(wrapper != null && !DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())){
                getControleFalha().gravarFalhaProcessoAdministrativo(e, 
                                                                     "Erro ao confirmar desistencia pelo bpms", 
                                                                     "SEM CPF",
                                                                     wrapper.getNumeroProcesso());
            }else{
                getControleFalha().gravarFalha(e, "Erro ao confirmar desistencia pelo bpms");
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("informacoesprova")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response informacoesProva(@Context HttpServletRequest request, @Context SecurityContext context) throws AppException {

        LOG.debug("Início informações prova.");

        List<InformacaoProvaWrapper> listaJson = new ArrayList();

        try {

            listaJson.addAll(getProcessoAdministrativoService().getInformacoesProva());

            return Response.ok(listaJson).build();

        } catch (Exception e) {

            LOG.debug("Erro capturado.", e);
            getControleFalha().gravarFalha(e, "Erro ao recuperar informações prova - WS");

            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("confirmainformacoesprovapa")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmaInformacoesProva(@Context HttpServletRequest request, ProcessoAdministrativoWSWrapper wrapper) throws AppException {

        LOG.debug("Início confirmar Informações da prova pelo BPMS.");

        try {

            if (wrapper == null || DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())) {
                DetranWebUtils.applicationMessageException("Número Processo obrigatório.");
            }

            getProcessoAdministrativoService().validarPAParaExecucaoServico(wrapper.getNumeroProcesso(), "confirmainformacoesprovapa");
            
            String serverName = request.getServerName();
            
            new ExecucaoAndamentoManager().
                    iniciaExecucao(new ExecucaoAndamentoEspecificoWrapper(processoAdministrativoService.getProcessoAdministrativo(wrapper.getNumeroProcesso()), 
                                                                     null, 
                                                                     null,
                                                                     wrapper));
            
            return Response.ok().build();

        } catch (Exception e) {
            LOG.debug("Tratado", e);
            if(wrapper != null && !DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())){
                getControleFalha().gravarFalhaProcessoAdministrativo(e, 
                                                                     "Erro ao confirmar informações prova pelo bpms", 
                                                                     "SEM CPF",
                                                                     wrapper.getNumeroProcesso());
            }else{
                getControleFalha().gravarFalha(e, "Erro ao confirmar informações prova pelo bpms");
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("consultapa")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultaPA(@Context HttpServletRequest request, ProcessoAdministrativoWSWrapper entrada) throws AppException {

        LOG.debug("Início consulta PA pelo BPMS.");

        try {
            
            validaEntrada(entrada);

            ConsultaPaWSWrapper wrapper 
                = processoAdministrativoService.montarConsultaPA(entrada.getNumeroProcesso());
            
            return Response.ok(wrapper).build();

        } catch (Exception e) {
            LOG.debug("Tratado", e);
            if(entrada != null && !DetranStringUtil.ehBrancoOuNulo(entrada.getNumeroProcesso())){
                getControleFalha().gravarFalhaProcessoAdministrativo(e, 
                                                                     "Erro ao consultarinformações PA pelo bpms", 
                                                                     "SEM CPF",
                                                                     entrada.getNumeroProcesso());
            }else{
                getControleFalha().gravarFalha(e, "Erro ao consultarinformações PA pelo bpms");
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("emitir")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response emitir(@Context HttpServletRequest request,
            @Context SecurityContext context,
            ProcessoAdministrativoCriteria criteria) throws Exception {

        try {

            if (criteria == null || criteria.isCriteriaEmpty()) {
                carregarListSelectItem(request, criteria, SEARCH_ACTION);
                DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
            }

            view = getModelView();
            view.addEntity(new ProcessoAdministrativoWrapper());

            FormatoRelatorioEnum formato = FormatoRelatorioEnum.PDF;
            Map<String, String> params = new HashMap<>();

            List lista = new ArrayList<>();

            if (null == criteria.getId()) {

                lista = genericService.pesquisar(criteria);

            } else {

                lista = genericService.pesquisar(criteria);

                if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {
                    for (Object object : lista) {

                        ProcessoAdministrativoWrapper wrapper = (ProcessoAdministrativoWrapper) object;

                        if (null != criteria.getId()) {
                            wrapper.setNomeCondutor(getApoioService().getNomeDoAtendimento(wrapper.getEntidade().getAtendimento()));
                        }
                    }
                }
            }

            if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {

                EntityXMLWrapper xmlEntity = new EntityXMLWrapper();

                for (Object object : lista) {

                    StringBuilder sbFiltros = new StringBuilder();
                    if (!DetranStringUtil.ehBrancoOuNulo(criteria.getCpf())) {
                        sbFiltros.append("CPF: ");
                        sbFiltros.append(criteria.getCpf());
                        sbFiltros.append("</br>");
                    }

                    if (criteria.getCnh() != null) {
                        sbFiltros.append("CNH: ");
                        sbFiltros.append(criteria.getCnh());
                        sbFiltros.append("</br>");
                    }

                    if (criteria.getTipo() != null) {
                        sbFiltros.append("Tipo Processo: ");
                        sbFiltros.append(criteria.getTipo().toString());
                        sbFiltros.append("</br>");
                    }

                    if (criteria.getNumeroDetran() != null) {
                        sbFiltros.append("Número Detran: ");
                        sbFiltros.append(criteria.getNumeroDetran());
                        sbFiltros.append("</br>");
                    }

                    if (!DetranStringUtil.ehBrancoOuNulo(criteria.getNumeroProcesso())) {
                        sbFiltros.append("Número Processo: ");
                        sbFiltros.append(criteria.getNumeroProcesso());
                        sbFiltros.append("</br>");
                    }

                    if (!DetranStringUtil.ehBrancoOuNulo(criteria.getNumeroPortaria())) {
                        sbFiltros.append("Número Portaria: ");
                        sbFiltros.append(criteria.getNumeroPortaria());
                        sbFiltros.append("</br>");
                    }

                    if (criteria.getDataProcessamento() != null) {
                        sbFiltros.append("Data Processamento: ");
                        sbFiltros.append(Utils.formatDate(criteria.getDataProcessamento(), "dd/MM/yyyy"));
                        sbFiltros.append("</br>");
                    }

                    if (criteria.getAndamento() != null) {
                        sbFiltros.append("Andamento: ");
                        sbFiltros.append(criteria.getAndamento().getDescricao());
                        sbFiltros.append("</br>");
                    }

                    if (criteria.getAtivo() != null) {
                        sbFiltros.append("Situação: ");
                        sbFiltros.append(criteria.getAtivo().toString());
                        sbFiltros.append("</br>");
                    }

                    xmlEntity.addFiltro("", sbFiltros.toString());

                    xmlEntity.addDado(processoAdministrativoService.montarProcessoAdministrativoXml(object));
                }

                String xml = JaxbUtil.marshal(xmlEntity,
                        EntityXMLWrapper.class,
                        ProcessoAdministrativoXmlWrapper.class);

                String nomeArquivo = Utils.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmss") + "rel_pa.xml";

                DetranArquivoHelper.saveFile(nomeArquivo, xml.getBytes());

                params.put("uid", nomeArquivo);

                String urlRelatorio
                        = DetranReportsUtil.getReportParamsBirtUrl(
                                "consultaprocessoadministrativo",
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

    private void validaEntrada(ProcessoAdministrativoWSWrapper entrada) throws AppException {
        if (entrada == null || DetranStringUtil.ehBrancoOuNulo(entrada.getNumeroProcesso())) {
            DetranWebUtils.applicationMessageException("É necessário informar o número processo.");
        }
    }

    @PUT
    @Path("exportar")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response exportar(
            @Context HttpServletRequest request, @Context SecurityContext context, ProcessoAdministrativoCriteria c) throws AppException {

        try {
            DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);

            Map<String, String> params = new HashMap<>();
            params.put("id_processo", c.getId().toString());
            params.put("identificacao", usuarioLogado.getUsername());
            String urlRelatorio = 
                        DetranReportsUtil.getReportParamsBirtUrl(
                                "processoadministrativocompleto",
                                FormatoRelatorioEnum.PDF.getRptFormat(),
                                params,
                                "relatorios/processoadministrativo/consultas/");
            
            byte[] byteArquivo = DetranHTTPUtil.download(DetranReportsUtil.getReportsBaseBirtUrl(request, true) + urlRelatorio);
            
            view.setObjectResponse(byteArquivo);

            return getResponseOk(request);

        } catch (AppException e) {
            //LOG.error("Ocorreu um erro inesperado.", e);
            DetranWebUtils.addErrorMessage(e, view);
        }

        return getResponseOk(request);
    }
}