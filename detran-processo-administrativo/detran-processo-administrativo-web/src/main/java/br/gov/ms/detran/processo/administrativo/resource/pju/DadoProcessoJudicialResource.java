package br.gov.ms.detran.processo.administrativo.resource.pju;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.OrgaoBca;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoDocumento;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.core.projeto.entidade.vei.OrgaoJudicial;
import br.gov.ms.detran.comum.core.projeto.entidade.vei.Tribunal;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.UnidadePenalEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.WrapperUtil;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.core.iface.servico.hab.IHabilitacaoService;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.criteria.pju.DadoProcessoJudicialCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.IdentificacaoRecolhimentoCnhEnum;
import br.gov.ms.detran.processo.administrativo.enums.ParteProcessoJuridicoEnum;
import br.gov.ms.detran.processo.administrativo.enums.RequisitoRecursoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoMedidaEnum;
import br.gov.ms.detran.processo.administrativo.resource.PaResource;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.pju.DadoProcessoJudicialWrapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@ManagedBean
@Path("dadoprocessojudicials")
public class DadoProcessoJudicialResource extends PaResource<DadoProcessoJudicialWrapper, DadoProcessoJudicialCriteria> {
    
    private static final Logger LOG = Logger.getLogger(DadoProcessoJudicialResource.class);
    
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

        listSelectItem.add(new ListSelectItem("ativo", DetranWebUtils.getSelectItems(AtivoEnum.class, Boolean.TRUE)));
        
        List<TipoDocumento> listaTipoDocumento = getApoioService().getTipoDocumentoParaProcessoJudicial();
        listSelectItem.add(DetranWebUtils.populateItemsComboBox(listaTipoDocumento, "", "tipoDocumento", "", "descricao"));
        
        listSelectItem.add(new ListSelectItem("parte", DetranWebUtils.getSelectItems(ParteProcessoJuridicoEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(new ListSelectItem("tipoMedida", DetranWebUtils.getSelectItems(TipoMedidaEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(new ListSelectItem("requisitoCursoBloqueio", DetranWebUtils.getSelectItems(RequisitoRecursoBloqueioEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(new ListSelectItem("unidadePenal", DetranWebUtils.getSelectItems(UnidadePenalEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(new ListSelectItem("identificacaoRecolhimentoCnh", DetranWebUtils.getSelectItems(IdentificacaoRecolhimentoCnhEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(new ListSelectItem("identificacaoDelito", DetranWebUtils.getSelectItems(BooleanEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(new ListSelectItem("prazoIndeterminado", DetranWebUtils.getSelectItems(BooleanEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(new ListSelectItem("segredoJustica", DetranWebUtils.getSelectItems(BooleanEnum.class, Boolean.TRUE)));
        
        listSelectItem.add(new ListSelectItem("indicativoProva", DetranWebUtils.getSelectItems(BooleanEnum.class, Boolean.TRUE)));
        
        List<Tribunal> listaTribunal = getApoioService().getListaTribunalAtivo();
        listSelectItem.add(DetranWebUtils.populateItemsComboBox(listaTribunal, "", "tribunal", "", "descricao"));
        
        List<Tribunal> listaOrgaoBca = getApoioService().getListaOrgaoBca();
        listSelectItem.add(DetranWebUtils.populateItemsComboBox(listaOrgaoBca, "", "orgaoBca", " - ", "codigo", "descricao"));
        
        listSelectItem.add(new ListSelectItem("tipoProcesso", DetranWebUtils.getSelectItems(true, 
                                                                                            TipoProcessoEnum.SUSPENSAO_JUDICIAL,
                                                                                            TipoProcessoEnum.CASSACAO_JUDICIAL)));
        
        return listSelectItem;
    }

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {
        
        List lista = WrapperUtil.convertEntityToWrapper2(genericService.pesquisar(criteria), getEntityClass());
        
        if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {
            
            for (Object object : lista) {
                
                DadoProcessoJudicialWrapper wrapper = (DadoProcessoJudicialWrapper) object;
                
                Usuario usuario = (Usuario) getApoioService().getUsuario(wrapper.getEntidade().getUsuarioCadastro());
                if (usuario!= null)
                    wrapper.setUsuario(getApoioService().getNomeUsuarioPeloId(usuario));
                
                if (null != wrapper.getEntidade().getOrgaoBca()) {
                    OrgaoBca orgaoBca = (OrgaoBca) getApoioService().getOrgaoBcaPorID(wrapper.getEntidade().getOrgaoBca());
                    if (orgaoBca != null)
                        wrapper.setOrgaoBCA(orgaoBca.getDescricao());
                }
                
                if (null != wrapper.getEntidade().getOrgaoJudicial()) {
                    OrgaoJudicial orgaoJudicial = (OrgaoJudicial) getApoioService().getOrgaoJudicialPorID(wrapper.getEntidade().getOrgaoJudicial());
                    if (orgaoJudicial != null && orgaoJudicial.getTribunal() != null) {
                        wrapper.setOrgaoJudicial(orgaoJudicial.getDescricao());
                        wrapper.setTribunal(orgaoJudicial.getTribunal().getDescricao());
                    }
                }
                if(wrapper.getEntidade().getTipoDocumento() != null){
                    wrapper.setTipoDocumento((TipoDocumento) getApoioService().getTipoDocumentoById(wrapper.getEntidade().getTipoDocumento()));
                }
                
                view.addEntity(wrapper);
            }
        }
        
        view.setRowcount(genericService.contarPesquisa(criteria));
    }

    @Override
     protected void executarGravacao(HttpServletRequest request, IBaseEntity entidade) throws AppException {
        
        DadoProcessoJudicialWrapper wrapper = (DadoProcessoJudicialWrapper) entidade;
        
        DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);
        String urlBaseBirt = DetranReportsUtil.getReportsBaseBirtUrl(request, true);
        
        DadoProcessoJudicial dadoProcessoJudicial = 
                (DadoProcessoJudicial) getProcessoAdministrativoService().gravarDadoProcessoJudicial(wrapper, usuarioLogado, urlBaseBirt);
        
        new ExecucaoAndamentoManager()
                .iniciaExecucao(
                        new ExecucaoAndamentoEspecificoWrapper(
                                dadoProcessoJudicial.getProcessoJudicial().getProcessoAdministrativo(), 
                                null, 
                                null,
                                null));
        
    }
    
    @PUT
    @Path("carregarprocessos")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response carregarprocessos(@Context HttpServletRequest request,
                                      @Context SecurityContext context,
                                      DadoProcessoJudicialCriteria criteria) throws DatabaseException {

        view = getModelView();

        DadoProcessoJudicialWrapper wrapper = new DadoProcessoJudicialWrapper();
        
        List<ProcessoAdministrativoWrapper> listaWrapper = new ArrayList<>();
        
        try {
            
            criteria.setCpf(DetranStringUtil.retiraMascara(criteria.getCpf()));
            
            List<ProcessoAdministrativo> listaProcessos = 
                    getProcessoAdministrativoService().getProcessosAdministrativosParaProcessoJuridico(criteria.getCpf());
            
            wrapper.setNumeroDetran(getApoioService().getNumeroDetranPorCpf(criteria.getCpf()));
            
            CnhSituacaoEntrega entrega
                = (CnhSituacaoEntrega) getHabilitacaoService().
                        getCnhSituacaoEntregaSemAcaoDeDevolucaoDaCnhPorNumeroDetran(wrapper.getNumeroDetran());
            
            if(entrega != null && !entrega.getSetorCorrespondencia().getSigla().equals("DT/SEPEN")){
                DetranWebUtils.applicationMessageException("A CNH encontra-se em outro setor do DETRAN. {0}","", entrega.getSetorCorrespondencia().getSigla());
            }
            Date dataUltimaPenalidade = getProcessoAdministrativoService().buscarDataFimUltimaPenalidadePorCPF(criteria.getCpf());
            wrapper.setDataInicioPenalidade(Objects.nonNull(dataUltimaPenalidade)? Utils.addDayMonth(dataUltimaPenalidade, 1): null);
            
            if (!DetranCollectionUtil.ehNuloOuVazio(listaProcessos)) {
                
                for (ProcessoAdministrativo pa : listaProcessos) {
                    
                    ProcessoAdministrativoWrapper wrapperPa = new ProcessoAdministrativoWrapper();
                    wrapperPa.setEntidade(pa);
                    
                    PAPenalidadeProcesso penalidadeProcesso = 
                            (PAPenalidadeProcesso) getProcessoAdministrativoService().getPenalidadePorPA(pa.getId());
                    wrapperPa.setPenalidadeProcesso(penalidadeProcesso);
                    
                    if(penalidadeProcesso != null)
                        wrapperPa.setDataFimPenalidadeFormatada(Utils.formatDate(penalidadeProcesso.getDataFimPenalidade(), "dd/MM/yyyy"));
                    
                    PAOcorrenciaStatus ocorrenciaStatus = 
                            (PAOcorrenciaStatus) getProcessoAdministrativoService().getPAOcorrenciaStatusAtiva(pa.getId());
                    
                    if (null != ocorrenciaStatus) {
                        wrapperPa.setCodigoAndamento(ocorrenciaStatus.getStatusAndamento().getAndamentoProcesso().getCodigo());
                        wrapperPa.setDescricaoAndamento(ocorrenciaStatus.getStatusAndamento().getAndamentoProcesso().getDescricao());
                    }
                    if(pa.isJuridico()){
                        DadoProcessoJudicial pju = getProcessoAdministrativoService().getDadoProcessoJudicialPorPA(pa);
                        if(pju != null && pju.getRequisitoCursoBloqueio() != null){
                            wrapperPa.setReqCursoBloqueioLabel(pju.getRequisitoCursoBloqueio().toString());
                        }
                        if(pju!= null && BooleanEnum.SIM.equals(pju.getIndicativoPrazoIndeterminado())){
                            wrapperPa.setDataFimPenalidadeFormatada("INDETERMINADA");
                        }
                    }
                    
                    listaWrapper.add(wrapperPa);
                }
            }
            
            wrapper.setProcessosAdministrativosParaProcessoJuridico(listaWrapper);
            
            
            if(wrapper.getNumeroDetran() == null){
                Long numeroDetran = getProcessoAdministrativoService().getCondutorViaAEMNPP93(criteria.getCpf());
                if(numeroDetran != null){
                    wrapper.setNumeroDetran(numeroDetran);
                    wrapper.setIsCondutor(Boolean.TRUE);
                }
            }
            
            MovimentoCnh movimentoEntrega = 
                    (MovimentoCnh) getProcessoAdministrativoService()
                            .getMovimentoCnhParaDesentranhamentoPorCpfCondutor(criteria.getCpf());
            
            if (null != movimentoEntrega) {
                wrapper.setCnhEntregue(Boolean.TRUE);
                DadoProcessoJudicial entidade = new DadoProcessoJudicial();
                entidade.setIdentificacaoRecolhimentoCnh(IdentificacaoRecolhimentoCnhEnum.DETRAN);
                wrapper.setEntidade(entidade);
                Date dataEntregaCnh = getApoioService().getCnhSituacaoEntregaPorMovimentoCnh(movimentoEntrega.getId());
                String dataStr = null;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                if(dataEntregaCnh!=null){
                    dataStr = sdf.format(dataEntregaCnh);
                }
                
                wrapper.setDataEntregaCnh(dataStr);
            } else {
                wrapper.setCnhEntregue(Boolean.FALSE);
            }
            
            if (null == wrapper.getNumeroDetran()) {
                DetranWebUtils.addErrorMessage("Pessoa não cadastrada para no sistema.", view);
            }
            

        } catch (Exception ex) {
            LOG.error("Ocorreu uma exceção para pesquisar os registros.", ex);
            DetranWebUtils.addErrorMessage(ex, view);
        }
        
        return getResponseOk(wrapper);
    }
    
    @PUT
    @Path("buscarOrgaoJudicial")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response buscarOrgaoJudicial(@Context HttpServletRequest request,
                                        @Context SecurityContext context,
                                        DadoProcessoJudicialCriteria criteria) throws DatabaseException {

        view = getModelView();

        List<OrgaoJudicial> listaOrgaoJudicial = new ArrayList<>();
        
        try {
            
            listaOrgaoJudicial = getApoioService().getOrgaoJudicialPorTribunal(criteria.getTribunal().getId());
            
            if (DetranCollectionUtil.ehNuloOuVazio(listaOrgaoJudicial)) {
                DetranWebUtils.addErrorMessage("Nenhum órgão judicial encontrado para este tribunal.", view);
            }

        } catch (Exception ex) {
            LOG.error("Ocorreu uma exceção para pesquisar os registros.", ex);
            DetranWebUtils.addErrorMessage(ex, view);
        }
        
        return getResponseOk(listaOrgaoJudicial);
    }
    
}
