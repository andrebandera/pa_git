package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.criteria.hab.CnhSituacaoEntregaCriteria;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.TemplateProtocolo;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.Municipio;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhControle;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.core.projeto.enums.adm.FormaProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.OrigemCadastroEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.ResponsavelProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.apo.EstadoEnum;
import br.gov.ms.detran.comum.core.projeto.enums.hab.FormaEntregaCnhEnum;
import br.gov.ms.detran.comum.core.projeto.enums.hab.ModoEntregaCnhEnum;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.ResultLong;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.iface.servico.IDetranGenericService;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.util.*;
import br.gov.ms.detran.comum.util.DetranStringUtil.TipoDadoEnum;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.core.iface.servico.hab.IHabilitacaoService;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP93;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.criteria.ControleCnhPACriteria;
import br.gov.ms.detran.processo.administrativo.criteria.ProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.ArquivoPA;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.Protocolo;
import br.gov.ms.detran.processo.administrativo.enums.ValidadeCnhEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.*;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lillydi
 */
@ManagedBean
@Path("controlecnhpas")
public class ControleCnhPAResource extends PaResource<MovimentoCnh, ControleCnhPACriteria> {

    private static final Logger LOG = Logger.getLogger(ControleCnhPAResource.class);
    
    @EJB
    private IProcessoAdministrativoService service;
    
    private IApoioService apoioService;
    
    private IHabilitacaoService habilitacaoService;
    
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }
    
    public IHabilitacaoService getHabilitacaoService() {
        if (habilitacaoService == null) {
            habilitacaoService = (IHabilitacaoService) JNDIUtil.lookup("ejb/HabilitacaoService");
        }
        return habilitacaoService;
    }
    
    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {
        List<ListSelectItem> listSelectItem = new ArrayList<>();

        IHabilitacaoService service = (IHabilitacaoService) JNDIUtil.lookup("ejb/HabilitacaoService");

        List<Municipio> municipiosMS = service.buscarMunicipioPelaUF(EstadoEnum.MS.name());
        listSelectItem.add(DetranWebUtils.populateItemsComboBox(municipiosMS, "", "municipios", "", "nome"));

        listSelectItem.add(new ListSelectItem("formaEntrega", DetranWebUtils.getSelectItems(FormaEntregaCnhEnum.class, Boolean.TRUE)));

        listSelectItem.add(new ListSelectItem("movimentoFormaEntrega", DetranWebUtils.getSelectItems(ModoEntregaCnhEnum.class, Boolean.TRUE)));

        listSelectItem.add(new ListSelectItem("responsavel", DetranWebUtils.getSelectItems(ResponsavelProtocoloEnum.class, Boolean.TRUE)));

        listSelectItem.add(new ListSelectItem("forma", DetranWebUtils.getSelectItems(FormaProtocoloEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(new ListSelectItem("validadeCnh", DetranWebUtils.getSelectItems(ValidadeCnhEnum.class, Boolean.TRUE)));

        return listSelectItem;
    }

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {

        ControleCnhPACriteria controleCriteria = (ControleCnhPACriteria) criteria;

        IDetranGenericService service = (IDetranGenericService) JNDIUtil.lookup("ejb/DetranGenericService");
        
        if (controleCriteria == null || controleCriteria.isCriteriaEmpty()) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        if (controleCriteria != null) {
            
            controleCriteria.setAcao(AcaoEntregaCnhEnum.ENTREGA);
            controleCriteria.setAtivo(AtivoEnum.ATIVO);
            
            List<MovimentoCnh> movimentos = new ArrayList<>();
            List listaCnh = new ArrayList();
            
            if (controleCriteria.getValidadeCnh() != null) {
                
                movimentos = verificaCnhControlesValidosOuVencidos(controleCriteria);
                listaCnh = tratarListagemMovimentoCnhWrapper(movimentos, service);
                
                view.setEntity(listaCnh);
                view.setRowcount(countCnhControlesValidosOuVencidos(controleCriteria));
                
            } else {
                
                movimentos = processoAdministrativoService.pesquisar(controleCriteria);
                listaCnh = tratarListagemMovimentoCnhWrapper(movimentos, service);

                view.setEntity(listaCnh);
                view.setRowcount(processoAdministrativoService.contarPesquisa(controleCriteria));
            }
        
        }
    }

    private List tratarListagemMovimentoCnhWrapper(List<MovimentoCnh> movimentos, IDetranGenericService service1) throws AppException {
        List listaCnh = new ArrayList<>();
        
        for (MovimentoCnh movimento : movimentos) {
            movimento.getProtocolo().setArquivoPa(null);
            movimento.getProtocolo().setByteCodigoBarra(null);
            
            CnhSituacaoEntregaCriteria crit = new CnhSituacaoEntregaCriteria();
            crit.setSiglaSetor("DT/SEPEN");
            crit.setControleCnhs(DetranCollectionUtil.montaLista(movimento.getCnhControle()));
            
            PAPenalidadeProcesso penalidade = (PAPenalidadeProcesso) processoAdministrativoService.
                    getPenalidadePorPA(movimento.getProtocolo().getNumeroProcesso().getId());
            
            listaCnh.add(new MovimentoCnhWrapper(movimento, definirSituacaoEntrega(service1.pesquisar(crit)), penalidade == null ? null : penalidade.getDataFimPenalidade()));
        }
        
        return listaCnh;
    }


    @POST
    @Path("salvar")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response salvar(@Context HttpServletRequest request, @Context SecurityContext context, ControleCnhPAWrapper wrapper) {

        LOG.debug("Entrada no método gravar.");

        view = getModelView();

        try {

            DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);
            String urlBaseBirt = DetranReportsUtil.getReportsBaseBirtUrl(request, true);
            
            if(wrapper.getDadosCondutorBCA() != null && !DetranStringUtil.ehBrancoOuNulo(wrapper.getDadosCondutorBCA().getNumeroPid())){
                if (DetranStringUtil.ehBrancoOuNulo(wrapper.getPidInformada())
                        || !DetranStringUtil.preencherEspaco(wrapper.getPidInformada(), 20, TipoDadoEnum.NUMERICO)
                        .equalsIgnoreCase(DetranStringUtil.preencherEspaco(wrapper.getDadosCondutorBCA().getNumeroPid(), 20, TipoDadoEnum.NUMERICO))) {
                    DetranWebUtils.applicationMessageException("Número de PID informado diferente da PID atual.");
                }
            }

            wrapper.getEntidade().getCnhControle().setNumeroPid(wrapper.getPidInformada());

            wrapper.getEntidade().setOrigemCadastro(OrigemCadastroEnum.PROCESSO_ADMINISTRATIVO);

            wrapper.setRecursoCanceladoWrapper(new RecursoOnlineCanceladoWrapper(" Recolhimento da CNH.",
                    DetranHTTPUtil.getClientIpAddr(request),
                    usuarioLogado.getUsername()));

            List<PAControleCnhWrapper> listaProcessos =
                    getProcessoAdministrativoService()
                            .carregarListaProcessosParaControleCnh(wrapper);

            getProcessoAdministrativoService().gravarControleCnhPA(usuarioLogado, wrapper, urlBaseBirt, listaProcessos);
            
            for (PAControleCnhWrapper paWrapper : listaProcessos) {
                try {
                    
                    new ExecucaoAndamentoManager().iniciaExecucao(
                            new ExecucaoAndamentoEspecificoWrapper(
                                    paWrapper.getProcesso(),
                                    usuarioLogado.getOperador().getUsuario().getId(),
                                    null, null
                            )
                    );
                } catch (AppException e) {
                    //LOG.error("Erro ao executar andamento");
                }
            }

            try{

                List<ArquivoPA> listaArquivos
                        = getProcessoAdministrativoService()
                                .gravarProtocolo(usuarioLogado,
                                                 getListaProcessosParaImpressao(
                                                         wrapper.getNumeroDetranCondutor(),
                                                         wrapper.getEntidade().getCnhControle().getNumeroCnh()),
                                                 urlBaseBirt);

                getProcessoAdministrativoService().imprimirProtocoloControleCnh(usuarioLogado, listaArquivos);
            } catch (AppException e) {
                LOG.info("Erro ao imprimir processos");
            }

            /**
             * Recarrega as lista de dependencias na view *
             */
            view.getListSelectItem().addAll(populateListWithEnums(request, null, "SAVE"));
            view.setRowcount(Long.valueOf(0));
            view.addEntity(new MovimentoCnh());

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

    @POST
    @Path("desbloquear")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response desbloquear(@Context HttpServletRequest request, @Context SecurityContext context, ControleCnhPAWrapper wrapper) {

        LOG.debug("Entrada no método desbloquear.");

        view = getModelView();
        
        try {

            DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);
            
            CnhSituacaoEntrega situacaoEntrega = 
                    (CnhSituacaoEntrega) getApoioService()
                            .getSituacaoEntregaCnh(
                                    wrapper.getEntidade().getCnhControle().getId(), 
                                    AcaoEntregaCnhEnum.ENTREGA);
                    
            /** 1 **/
            getProcessoAdministrativoService()
                    .checaSeExisteAlgumProcessoAdministrativoQueInvalidaExecutarDesbloqueioCnh(
                            situacaoEntrega.getCnhControlePessoa().getPessoaFisica().getId());
            
            /** 2 **/
            getProcessoAdministrativoService()
                    .validaSeExisteProcessoAdministrativoValidoParaExecutarDesbloqueioCnh(
                            situacaoEntrega.getCnhControlePessoa().getPessoaFisica().getId());
            
            List<MovimentoCnh> movimentos = 
                    getProcessoAdministrativoService()
                            .getMovimentoPorControleCnhEAcaoEntregaParaDesbloqueio(
                                    situacaoEntrega.getCnhControle().getId(), 
                                    AcaoEntregaCnhEnum.ENTREGA);
                    
            getProcessoAdministrativoService().desbloquearControleCnhPA(usuarioLogado, 
                                                                        wrapper, 
                                                                        DetranReportsUtil.getReportsBaseBirtUrl(request, true),
                                                                        situacaoEntrega,
                                                                        movimentos);
            
            for (MovimentoCnh movimento : movimentos) {
                
                try {
                    new ExecucaoAndamentoManager().iniciaExecucao(
                            new ExecucaoAndamentoEspecificoWrapper(
                                    movimento.getProtocolo().getNumeroProcesso(), 
                                    null, 
                                    null,
                                    null));
                } catch (AppException e) {
                    DetranWebUtils.addErrorMessage(e, view);
                }
            }
            
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
    @Path("exportarprotocolo")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response exportarProtocolo(@Context HttpServletRequest request, 
                                      @Context SecurityContext context, 
                                      MovimentoCnhWrapper wrapper) throws AppException {

        try {

            MovimentoCnh movimentoCnh 
                = (MovimentoCnh) 
                    processoAdministrativoService
                        .buscarEntidadePeloId(MovimentoCnh.class, wrapper.getEntidade().getId());

            if (wrapper.getEntidade().getAcao().equals(AcaoEntregaCnhEnum.DEVOLUCAO)) {
                
                movimentoCnh 
                    = processoAdministrativoService
                        .getMovimentoProtocoloControleCnh(
                            movimentoCnh.getProtocolo().getNumeroProcesso().getId(), 
                            wrapper.getEntidade().getAcao()
                        );
                
                if(movimentoCnh == null) {
                    DetranWebUtils.applicationMessageException("Não existe Protocolo de Devolução.");
                }
            }

            ArquivoPA arquivoPA = movimentoCnh.getProtocolo().getArquivoPa();

            if (arquivoPA == null || arquivoPA.getByteArquivo() == null) {

                String urlBaseBirt = DetranReportsUtil.getReportsBaseBirtUrl(request, true);

                arquivoPA 
                    = (ArquivoPA) getProcessoAdministrativoService()
                        .gravarArquivoProtocoloControleCnh(new MovimentoCnhWrapper(movimentoCnh), urlBaseBirt);
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
     * 
     * @param situacoes
     * @return 
     */
    private CnhSituacaoEntrega definirSituacaoEntrega(List<CnhSituacaoEntrega> situacoes) throws DatabaseException {
        if (DetranCollectionUtil.ehNuloOuVazio(situacoes)) {
            return null;
        }
        for (CnhSituacaoEntrega situacao : situacoes) {
            CnhSituacaoEntrega ultimaCnhSituacaoEntrega 
                        = (CnhSituacaoEntrega) getHabilitacaoService()
                            .getUltimaCnhSituacaoEntregaPorCnhControle(
                                    situacao.getCnhControle().getId()
                            );
            if (AcaoEntregaCnhEnum.DEVOLUCAO.equals(ultimaCnhSituacaoEntrega.getAcao())) {
                return ultimaCnhSituacaoEntrega;
            }
        }
        return situacoes.get(0);
    }
    
    @Override
    protected void executarDepoisDeCarregar(HttpServletRequest request) throws AppException {
        
        super.executarDepoisDeCarregar(request);
        
        getEntity().setProtocolo(new Protocolo());
        
    }
    
    @PUT
    @Path("integrarinfoprova")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response integrarInfoProva(@Context HttpServletRequest request, 
                                      @Context SecurityContext context, 
                                      String numeroProcesso) throws AppException {

        view = getModelView();
        
        try {
            
            DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);

            processoAdministrativoService.obterInformacoesProvaInfrator(numeroProcesso, usuarioLogado);

            DetranWebUtils.addInfoMessage("infracoesprovainfratorbasenacional.sucess", view);
            
        } catch (AppException e) {
            //LOG.error("Ocorreu uma exceção ao recuperar informações da prova.", e);
            DetranWebUtils.addErrorMessage(e, view);
        }

        return getResponseOk(request);
    }
    
    @PUT
    @Path("carregardadoscontrolecnh")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response carregarDadosControleCnh(@Context HttpServletRequest request,
                                             @Context SecurityContext context,
                                             ProcessoAdministrativoCriteria criteria) throws DatabaseException {

        view = getModelView();

        ControleCnhPAWrapper wrapper = new ControleCnhPAWrapper();
        
        try {
            
            AEMNPP93 aemnpp93 = (AEMNPP93) service.validarEntregaCnhParaPA(criteria.getCpf(), criteria.getCnh());
             
            wrapper = (ControleCnhPAWrapper) service.carregarDadosControleCnh(criteria);
            wrapper.setDadosCondutorBCA(aemnpp93);

            if (view.getRowcount() != null && view.getRowcount().intValue() <= 0) {
                
                if (criteria != null && criteria.getEmptyMessage() != null && criteria.getEmptyMessage()) {
                    DetranWebUtils.addWarningMessage("application.message.consulta.empty", view);
                }
                
            }

        } catch (Exception ex) {
            LOG.error("Ocorreu uma exceção para pesquisar os registros.", ex);
            DetranWebUtils.addErrorMessage(ex, view);
        }
        
        view.setObjectResponse(wrapper);
        return getResponseOk(request);
    }

    /**
     * @param numeroDetranCondutor
     * @param numeroCnh
     * @return
     * @throws AppException 
     */
    private List<MovimentoCnh> getListaProcessosParaImpressao(Long numeroDetranCondutor, Long numeroCnh) throws AppException {
        
        CnhControle controleCnh = 
                (CnhControle) getApoioService()
                        .getCnhControlePorNumeroDetran(
                                numeroDetranCondutor);
        
        if (null == controleCnh) {
            DetranWebUtils.applicationMessageException("Não foi possível recuperar informações de entrega cnh.");
        }
        
        return (List<MovimentoCnh>) service.getMovimentoPorControleCnhEAcaoEntrega(controleCnh.getId(), AcaoEntregaCnhEnum.ENTREGA);
    }

    @Override
    protected void executarNovo(HttpServletRequest request) throws AppException {
        super.executarNovo(request); //To change body of generated methods, choose Tools | Templates.
        
        DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);
        
        ControleCnhPAWrapper wrapper = new ControleCnhPAWrapper();
        wrapper.setEntidade(new CnhSituacaoEntrega());
        wrapper.getEntidade().setTemplateProtocolo(new TemplateProtocolo());
        
        wrapper.getEntidade().setPostoAtendimento(usuarioLogado.getOperador().getPostoAtendimento());
        wrapper.getEntidade().setModoEntrega(ModoEntregaCnhEnum.DOCUMENTO_CNH);
        
        wrapper.getEntidade().getTemplateProtocolo().setFormaProtocolo(FormaProtocoloEnum.BALCAO);
        wrapper.getEntidade().getTemplateProtocolo().setResponsavelProtocolo(ResponsavelProtocoloEnum.CONDUTOR);
        
        
        if(getEntity() != null && getEntity().getCnhControle() != null){
            
            List<CnhSituacaoEntrega> listaBAC = getApoioService().getCnhSituacaoEntregaPorCnhControle(getEntity().getCnhControle(), AcaoEntregaCnhEnum.ENTREGA);
            
            if(!DetranCollectionUtil.ehNuloOuVazio(listaBAC)){
        
                CnhSituacaoEntrega cnhSituacaoEntrega = listaBAC.get(0);
                
                wrapper.getEntidade().setPostoAtendimento(cnhSituacaoEntrega.getPostoAtendimento());
                wrapper.getEntidade().setModoEntrega(cnhSituacaoEntrega.getModoEntrega());
                
                wrapper.getEntidade().getTemplateProtocolo().setFormaProtocolo(cnhSituacaoEntrega.getTemplateProtocolo().getFormaProtocolo());
                wrapper.getEntidade().getTemplateProtocolo().setResponsavelProtocolo(cnhSituacaoEntrega.getTemplateProtocolo().getResponsavelProtocolo());
        
            }
            
        }
        
        view.setObjectResponse(wrapper);
        
    }
    
    private List verificaCnhControlesValidosOuVencidos(ControleCnhPACriteria controleCriteria) throws AppException {
        List<MovimentoCnh> movimentos = new ArrayList<>();
        
        if (ValidadeCnhEnum.VALIDA.equals(controleCriteria.getValidadeCnh())) {
            movimentos = processoAdministrativoService.getMovimentoCnhComCnhControleValido(controleCriteria);
        } else {
            movimentos = processoAdministrativoService.getMovimentoCnhComCnhControleVencido(controleCriteria);
        }
        
        return movimentos;
    }
    
    private Long countCnhControlesValidosOuVencidos(ControleCnhPACriteria controleCriteria) throws AppException {
        ResultLong resultado = new ResultLong();
        
        if (ValidadeCnhEnum.VALIDA.equals(controleCriteria.getValidadeCnh())) {
            resultado = (ResultLong) processoAdministrativoService.getCountMovimentoCnhComCnhControleValido(controleCriteria);
        } else {
            resultado = (ResultLong) processoAdministrativoService.getCountMovimentoCnhComCnhControleVencido(controleCriteria);
        }
        
        return resultado == null ? 0L : resultado.getResultado();
    }
}
