package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.inf.AmparoLegal;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.anotacao.resource.ResourceConfig;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.SelectItem;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.inf.IInfracaoService;
import br.gov.ms.detran.processo.administrativo.criteria.PAFluxoProcessoCriteria;
import br.gov.ms.detran.processo.administrativo.ejb.IPAApoioService;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoProcessoWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * @author Carlos
 */
@ManagedBean
@Path("pafluxoprocessos")
@ResourceConfig(condicaoProcesso = "PAFluxoProcessoCP")
public class PAFluxoProcessoResource extends PaResource<PAFluxoProcessoWrapper, PAFluxoProcessoCriteria>{
    
    @EJB(mappedName = "ejb/PAApoioService")
    private IPAApoioService service;
    
    private IInfracaoService infracaoService;
    
    public IInfracaoService getInfracaoService() {
        if (infracaoService == null) {
            infracaoService = (IInfracaoService) JNDIUtil.lookup("ejb/InfracaoService");
        }
        return infracaoService;
    }
    
    private static final Logger log = Logger.getLogger(PAFluxoProcessoResource.class);

    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> list = new ArrayList<>();
        List<SelectItem> selectItems = DetranWebUtils.getSelectItems(AtivoEnum.class, true);
        list.add(new ListSelectItem("ativo", selectItems));
        
        list.add(new ListSelectItem("fluxoIndependente", DetranWebUtils.getSelectItems(BooleanEnum.class, true)));
        
        return list;
    }

    @Override
    protected void executarGravacao(HttpServletRequest request, IBaseEntity entidade) throws AppException {
        service.gravarFluxoProcesso((PAFluxoProcessoWrapper) entidade);
    }

    /**
     * Método seleciona a entidade fluxoprocesso e amparoLegal setando-as para a função de visualização.
     * 
     * @param criteria
     * @return
     * @throws DatabaseException
     * @throws AppException 
     */
    @PUT
    @Path("buscarFluxoProcessoeAmparoLegal")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response buscarFluxoProcessoeAmparoLegal(@Context HttpServletRequest request,
                                                    @Context SecurityContext context,
                                                    PAFluxoProcessoCriteria criteria) throws DatabaseException, AppException {
        
        view = getModelView();
        
        PAFluxoProcessoWrapper wrapper = new PAFluxoProcessoWrapper();
        wrapper.setEntidade(service.getPAFluxoProcessoById(criteria.getId().longValue()));
        
        try{ 
            
            view.getListSelectItem().addAll(populateListWithEnums(request, "SEARCH"));
            
            AmparoLegal amparo = (AmparoLegal) getInfracaoService().getAmparoLegalPorId(wrapper.getEntidade().getAmparoLegal());
            wrapper.setAmparoLegal(amparo);
            
            if (wrapper.getAmparoLegal() != null){    
                view.addEntity(wrapper);
            }
        
        } catch(Exception e){
            log.error("###PAFluxoProcessoResource-buscarFluxoProcessoeAmparoLegal### Amparo Legal com valor nulo - AmparoLegalId: " + wrapper.getEntidade().getAmparoLegal());
            return null;
        }
        
        return getResponseOk();
    }

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {
        if(criteria != null){
            view.setEntity(service.getPAFluxoProcessoPorFiltros(criteria));
            view.setRowcount(service.getCountPAFluxoProcessoPorFiltros(criteria));
        }
    }
}