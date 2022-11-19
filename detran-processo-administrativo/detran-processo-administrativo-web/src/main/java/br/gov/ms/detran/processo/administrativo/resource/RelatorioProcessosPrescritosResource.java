package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranHTTPUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.criteria.RelProcessoPrescritoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.Movimentacao;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoMotivoEnum;
import java.util.Date;
import java.util.HashMap;
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
 * @author Roberto Oliveira
 */
@ManagedBean
@Path("relatorioprocessoprescritos")
public class RelatorioProcessosPrescritosResource extends PaResource<Movimentacao, RelProcessoPrescritoCriteria> {

    private static final Integer TREZENTOS_E_SESSENTA_E_CINCO_DIAS = 365;

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {

        if (criteria.isCriteriaEmpty()) {
            DetranWebUtils.applicationMessageException("Favor preencher os campo de filtro!");

        } else {

            RelProcessoPrescritoCriteria c = (RelProcessoPrescritoCriteria) criteria;

            validarSearch(c);
            
            c.setMotivo(MovimentacaoMotivoEnum.PROCESSO_PRESCRITO);

            view.setEntity(genericService.pesquisar(c));

            view.setRowcount(genericService.contarPesquisa(c));
        }
    }

    private void validarSearch(RelProcessoPrescritoCriteria c) throws Exception {

        if (c.getDataEntrada() == null || c.getDataSaida() == null) {
            DetranWebUtils.applicationMessageException("É necessário informar o período!");
        }

        if (c.getDataEntrada().after(c.getDataSaida())) {
            DetranWebUtils.applicationMessageException("Data Inicial não pode ser menor que a Data Final!");
        }

        if (c.getDataEntrada().after(new Date()) || c.getDataSaida().after(new Date())) {
            DetranWebUtils.applicationMessageException("As datas informadas não podem ser maiores que a data atual!");
        }

        if (Utils.addDayMonth(c.getDataEntrada(), TREZENTOS_E_SESSENTA_E_CINCO_DIAS).before(c.getDataSaida())) {
            DetranWebUtils.applicationMessageException("O período informado não pode ser maior que 365 dias!");
        }
    }

    @PUT
    @Path("emitir")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response emitir(
            @Context HttpServletRequest request, @Context SecurityContext context, RelProcessoPrescritoCriteria criteria) throws AppException {

        try {

            Map<String, String> params = new HashMap<>();

            params.put("v_data_inicio", Utils.formatDate(criteria.getDataEntrada(), "YYYY-MM-dd"));
            params.put("v_data_fim", Utils.formatDate(criteria.getDataSaida(), "YYYY-MM-dd"));

            String urlReport = DetranReportsUtil.getReportParamsBirtUrl(
                    "relatorio_processos_prescritos",
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
