package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.criteria.ProcessoAdministrativoHistoricoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.ConsultaProcessoAdministrativoHistorico;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

@ManagedBean
@Path("processoadministrativohistoricos")
public class ProcessoAdministrativoHistoricoResource extends PaResource<ConsultaProcessoAdministrativoHistorico, ProcessoAdministrativoHistoricoCriteria> {

    private static final Logger log = Logger.getLogger(ProcessoAdministrativoHistoricoResource.class);

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {
        view = getModelView();
        
        ProcessoAdministrativoHistoricoCriteria historicoCriteria = (ProcessoAdministrativoHistoricoCriteria) criteria;
        List listaHistoricos = processoAdministrativoService.getProcessoAdministrativoHistorico(
                historicoCriteria.getProcessoAdministrativo().getId(),
                historicoCriteria.getFrom(),
                historicoCriteria.getTo());
        
        view.setEntity(listaHistoricos);
        view.setRowcount(processoAdministrativoService.getCountProcessoAdministrativoHistorico(historicoCriteria.getProcessoAdministrativo().getId()));

        if (view.getRowcount() != null && view.getRowcount().intValue() <= 0) {
            log.info("Nenhum resultado retornado para a consulta com criteria {0}.", criteria.toString());
            DetranWebUtils.addWarningMessage("application.message.consulta.empty", view);
        } else if (view.getRowcount() > 0) {
            view.setListSelectItem(populateListWithEnums(request, "SEARCH"));
        }
    }
}
