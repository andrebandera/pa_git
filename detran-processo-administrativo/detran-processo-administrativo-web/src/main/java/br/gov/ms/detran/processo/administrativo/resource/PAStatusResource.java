package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.projeto.anotacao.resource.ResourceConfig;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.SelectItem;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.criteria.PAStatusCriteria;
import br.gov.ms.detran.processo.administrativo.ejb.IPAApoioService;
import br.gov.ms.detran.processo.administrativo.wrapper.PAStatusWrapper;
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
 *
 * @author Carlos Eduardo
 */
@ManagedBean
@Path("pastatuss")
@ResourceConfig(condicaoProcesso = "PAStatusCP")
public class PAStatusResource extends PaResource<PAStatusWrapper, PAStatusCriteria> {

    @EJB(mappedName = "ejb/PAApoioService")
    private IPAApoioService service;

    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> list = new ArrayList<>();
        List<SelectItem> selectItems = DetranWebUtils.getSelectItems(AtivoEnum.class, true);
        list.add(new ListSelectItem("ativo", selectItems));

        return list;
    }

    @Override
    protected void executarGravacao(HttpServletRequest request, IBaseEntity entidade) throws AppException {
        service.gravarStatusAndamento((PAStatusWrapper) entidade);
    }

    @PUT
    @Path("searchStatusPorAndamentoPA")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response searchStatusPorAndamentoPA(
            @Context HttpServletRequest request,
            @Context SecurityContext context,
            PAStatusCriteria criteria) throws DatabaseException, AppException {

        view = getModelView();
        if (criteria.getDescricao() == null || criteria.getDescricao().isEmpty() || criteria.getDescricao().length() < 2) {
            DetranWebUtils.addErrorMessage("É necessário informar ao menos 2 caracteres para pesquisa.", view);
            return getResponseOk(request);
        } else {
            try {

                if (!view.hasError()) {

                    view.setEntity(service.getStatusPorAndamentoPA(criteria));
                    view.setRowcount(service.getCountStatusPorAndamentoPA(criteria));

                    if (criteria == null || criteria.isPopulateList()) {

                        view.getListSelectItem().addAll(populateListWithEnums(request, null, "SEARCH"));
                        view.getListSelectItem().addAll(populateListWithEnums(request, criteria, "SEARCH"));
                    }

                    if (view.getRowcount() != null && view.getRowcount().intValue() <= 0) {

                        if (criteria.getEmptyMessage() != null && criteria.getEmptyMessage()) {
                            if (criteria.getDescricao().length() >= 2) {
                                DetranWebUtils.applicationMessageException("Nenhum registro encontrado com a descrição informada.");
                            }
                            DetranWebUtils.applicationMessageException("Todos os registros ativos foram associados");
                        }
                    }
                }

            } catch (Exception ex) {
                DetranWebUtils.addErrorMessage(ex, view);
            }
        }
        return getResponseOk(request);
    }
}
