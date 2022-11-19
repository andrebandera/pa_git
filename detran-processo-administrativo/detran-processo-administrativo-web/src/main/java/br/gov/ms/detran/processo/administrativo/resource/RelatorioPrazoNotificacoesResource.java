
package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.util.DetranHTTPUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.criteria.RelPrazoNotificacaoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoBuscaEnum;
import java.util.ArrayList;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Bruno Akiyama
 */
@ManagedBean
@Path("relatorioprazonotificacoes")
public class RelatorioPrazoNotificacoesResource extends PaResource<NotificacaoProcessoAdministrativo, RelPrazoNotificacaoCriteria>{
    
    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> listSelectItem = new ArrayList<>();
        
        listSelectItem.add(new ListSelectItem("tipoBusca", DetranWebUtils.getSelectItems(TipoBuscaEnum.class,Boolean.TRUE)));
        
        return listSelectItem;
    }
    
    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {
        RelPrazoNotificacaoCriteria crit = (RelPrazoNotificacaoCriteria) criteria;
    }
    
    public void validarFiltro(RelPrazoNotificacaoCriteria criteria){
        if(criteria.getTipoBusca()== null){
            DetranWebUtils.addErrorMessage("Tipo Busca é obrigatório.", view);
        }
    }
    
    @PUT
    @Path("emitir")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response emitir(
            @Context HttpServletRequest request, @Context SecurityContext context, RelPrazoNotificacaoCriteria criteria) throws AppException {

        try {
            validarFiltro(criteria);

            Map<String, String> params = new HashMap<>();

            params.put("tipoBusca", criteria.getTipoBusca().getValorTipoBusca().toString());

            String urlReport = DetranReportsUtil.getReportParamsBirtUrl(
                    "relatorio_prazo_notificacoes",
                    FormatoRelatorioEnum.PDF.getRptFormat(),
                    params,
                    "relatorios/processoadministrativo/consultas/");

            byte[] byteArquivo = DetranHTTPUtil.download(DetranReportsUtil.getReportsBaseBirtUrl(request, true) + urlReport);

            view.setObjectResponse(byteArquivo);

            return getResponseOk(request);

        } catch (AppException e) {
            DetranWebUtils.addErrorMessage(e, view);
        }

        return getResponseOk(request);
    }
    
}
