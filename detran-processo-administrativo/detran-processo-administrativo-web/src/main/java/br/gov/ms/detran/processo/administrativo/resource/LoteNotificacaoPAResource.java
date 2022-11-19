package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.criteria.LoteNotificacaoPACriteria;
import br.gov.ms.detran.processo.administrativo.entidade.LoteNotificacaoPA;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;

import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@Path("/lotenotificacaos")
public class LoteNotificacaoPAResource  extends PaResource<LoteNotificacaoPA, LoteNotificacaoPACriteria>{

    private static final Logger LOG = Logger.getLogger(LoteNotificacaoPAResource.class);

    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> listSelectItem = new ArrayList<>();

        listSelectItem.add(new ListSelectItem("tipo", DetranWebUtils.getSelectItems(TipoFasePaEnum.class, Boolean.TRUE)));

        return listSelectItem;
    }


    @PUT
    @Path("exportar")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response exportar(
            @Context HttpServletRequest request, @Context SecurityContext context, LoteNotificacaoPACriteria c) throws AppException {

        try {

            LoteNotificacaoPA lote = (LoteNotificacaoPA) getProcessoAdministrativoService().buscarEntidadePeloId(LoteNotificacaoPA.class, c.getId());

            view.setObjectResponse(lote.getArquivo().getByteArquivo());

            return getResponseOk(request);

        } catch (AppException e) {
            //LOG.error("Ocorreu um erro inesperado.", e);
            DetranWebUtils.addErrorMessage(e, view);
        }

        return getResponseOk(request);
    }

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {

        if(criteria.isCriteriaEmpty()){
            carregarListSelectItem(request, null, SEARCH_ACTION);
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        if(validar((LoteNotificacaoPACriteria) criteria)){
            super.executarSearch(request, criteria);
        }
    }
    
    public Boolean validar(LoteNotificacaoPACriteria criteria) throws AppException{
        if(criteria.getId() != null){
            return true;
        }
        
        if(criteria.getDataInicio()!=null && criteria.getDataFim()!=null && criteria.getDataInicio().after(criteria.getDataFim())){
            DetranWebUtils.applicationMessageException("A Data Lote Fim deve ser maior que a Data Lote Início.");
        }
        
        return true;
    }
}
