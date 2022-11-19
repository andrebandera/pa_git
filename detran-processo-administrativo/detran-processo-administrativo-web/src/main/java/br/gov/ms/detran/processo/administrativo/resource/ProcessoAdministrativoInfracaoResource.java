package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.adm.OrgaoAutuador;
import br.gov.ms.detran.comum.core.projeto.entidade.inf.AcaoInfracaoPenalidade;
import br.gov.ms.detran.comum.core.projeto.entidade.inf.AmparoLegal;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.WrapperUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.inf.IInfracaoService;
import br.gov.ms.detran.processo.administrativo.criteria.ProcessoAdministrativoInfracaoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracaoSituacaoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoInfracaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.DadosPABPMSInfracaoDespachoWrapper;
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

/**
 * @author yanko.campos
 */
@ManagedBean
@Path("processoadministrativoinfracaos")
public class ProcessoAdministrativoInfracaoResource extends PaResource<ProcessoAdministrativoInfracaoWrapper, ProcessoAdministrativoInfracaoCriteria>{
    
    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoInfracaoResource.class);
    
     private IInfracaoService infracaoService;

    /**
     *
     * @return
     */
    public IInfracaoService getInfracaoService() {
        if (infracaoService == null) {
            infracaoService = (IInfracaoService) JNDIUtil.lookup("ejb/InfracaoService");
        }
        return infracaoService;
    }
    
    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> listSelectItem = new ArrayList<>();

        listSelectItem.add(new ListSelectItem("ativo", DetranWebUtils.getSelectItems(AtivoEnum.class, Boolean.TRUE)));
        listSelectItem.add(new ListSelectItem("situacao", DetranWebUtils.getSelectItems(ProcessoAdministrativoInfracaoSituacaoEnum.class, Boolean.TRUE)));

        return listSelectItem;
    }

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {
        
        List<ProcessoAdministrativoInfracaoWrapper> lista = WrapperUtil.convertEntityToWrapper2(genericService.pesquisar(criteria), getEntityClass());
        
        for (ProcessoAdministrativoInfracaoWrapper wrapper : lista) {
            OrgaoAutuador orgao = (OrgaoAutuador) getInfracaoService().getOrgaoAutuadorPeloID(wrapper.getEntidade().getOrgaoAutuador());
            wrapper.setOrgaoAutuadorLabel(orgao.getNome());
            AcaoInfracaoPenalidade acaoInf = (AcaoInfracaoPenalidade) getInfracaoService().getAcaoInfracaoPenalidadePeloID(wrapper.getEntidade().getInfracao());
            if(acaoInf != null){
                wrapper.setDescricaoInfracao(wrapper.getEntidade().getCodigoInfracao().concat(" ").concat(acaoInf.getInfracao().getDescricao()));
                montarArtigoInfracao(acaoInf, wrapper);
            }
        }
        
        view.setEntity(lista);
        view.setRowcount(genericService.contarPesquisa(criteria));
        if(view.getRowcount() > 0L){
            ProcessoAdministrativoInfracaoCriteria c = (ProcessoAdministrativoInfracaoCriteria) criteria;
            if(c.getProcessoAdministrativo() != null)
                view.setObjectResponse(getProcessoAdministrativoService().getSomaPontuacaoDeInfracoesPA(c.getProcessoAdministrativo().getId()));
        }
    }

    public void montarArtigoInfracao(AcaoInfracaoPenalidade acaoInf, ProcessoAdministrativoInfracaoWrapper wrapper) {
        if(acaoInf.getInfracao() != null && acaoInf.getInfracao().getAmparoLegal() != null){
            AmparoLegal amparo = acaoInf.getInfracao().getAmparoLegal();
            StringBuilder artigo = new StringBuilder(amparo.getArtigo());
            if(!DetranStringUtil.ehBrancoOuNulo(amparo.getInciso()))
                artigo.append(" ").append(amparo.getInciso());
            
            if(!DetranStringUtil.ehBrancoOuNulo(amparo.getParagrafo()))
                artigo.append(" ").append(amparo.getParagrafo());
            if(amparo.getAlineas() != null)
                artigo.append(" ").append(amparo.getAlineas());
            wrapper.setArtigo(artigo.toString());
        }
    }
    
     @PUT
    @Path("dadosinfracaodespacho")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response dadosInfracaoDespacho(@Context HttpServletRequest request, 
                                          ProcessoAdministrativoWSWrapper entrada) throws AppException {

        LOG.debug("Início dados infracao despaccho pelo BPMS.");

        try {
            
            if (entrada == null || DetranStringUtil.ehBrancoOuNulo(entrada.getNumeroProcesso())) {
                DetranWebUtils.applicationMessageException("É necessário informar o número processo.");
            }
            
            List<DadosPABPMSInfracaoDespachoWrapper> lista = 
                    processoAdministrativoService.geraRegistroInfracoesDespacho(entrada.getNumeroProcesso());
            
            return Response.ok(lista).build();

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
}