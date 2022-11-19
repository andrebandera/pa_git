package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.criteria.ControleCnhPACriteria;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.wrapper.ControleCnhPAWrapper;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

/**
 *
 * @author desenvolvimento
 */
@ManagedBean
@Path("processoadministrativocontrolecnhs")
public class ProcessoAdministrativoControleCnhResource extends PaResource<ControleCnhPAWrapper, ControleCnhPACriteria> {
    
    private IApoioService apoioService;
    
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    
    }
    
    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder c) throws Exception {

        ControleCnhPACriteria criteria = (ControleCnhPACriteria) c;
        
        criteria.setAcao(null);
        
        List lista = genericService.pesquisar(criteria);
        
        if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {
            
            for (Object object : lista) {
                
                MovimentoCnh movimento = (MovimentoCnh) object;
                
                List<CnhSituacaoEntrega> listaSituacaoEntrega = 
                        (List<CnhSituacaoEntrega>) getApoioService().getCnhSituacaoEntregaPorCnhControle(
                                movimento.getCnhControle(),
                                movimento.getAcao());
                
                if (!DetranCollectionUtil.ehNuloOuVazio(listaSituacaoEntrega)) {
                    for (CnhSituacaoEntrega cnhSituacaoEntrega : listaSituacaoEntrega) {
                        
                        ControleCnhPAWrapper wrapper = new ControleCnhPAWrapper();
                        wrapper.setMovimento(movimento);

                        wrapper.setEntidade(cnhSituacaoEntrega);
                        
                        wrapper.setAcao(cnhSituacaoEntrega.getAcao());

                        if (null != wrapper.getEntidade())
                            wrapper.setNomeUsuarioAcao(getApoioService().getNomeUsuarioPeloId(wrapper.getEntidade().getUsuario()));

                        view.addEntity(wrapper);
                    }
                }
            }
        }
        
        view.setRowcount(genericService.contarPesquisa(criteria));
    }
}