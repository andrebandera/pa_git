package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.criteria.RecursoMovimentoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoRecurso;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoMovimentoWrapper;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

@ManagedBean
@Path("recursomovimentos")
public class RecursoMovimentoResource extends PaResource<RecursoMovimentoWrapper, RecursoMovimentoCriteria> {
    
    private static final Logger LOG = Logger.getLogger(RecursoMovimentoResource.class);
    
    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder c) throws Exception {
        
        RecursoMovimentoCriteria criteria = (RecursoMovimentoCriteria) c;
        
        List lista = genericService.pesquisar(criteria);
        
        if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {
            for (Object object : lista) {
                
                RecursoMovimento entidade = (RecursoMovimento) object;
                
                RecursoMovimentoWrapper wrapper = new RecursoMovimentoWrapper();
                
                wrapper.setEntidade(entidade);
                
//                if (null != criteria.getId()) {
                    wrapper.setResultadoRecurso((ResultadoRecurso) processoAdministrativoService.getResultadoRecursoAtivoPorRecurso(entidade.getRecurso().getId()));
//                }
                
                view.addEntity(wrapper);
            }
        }
        
        view.setRowcount(genericService.contarPesquisa(criteria));
    }
}