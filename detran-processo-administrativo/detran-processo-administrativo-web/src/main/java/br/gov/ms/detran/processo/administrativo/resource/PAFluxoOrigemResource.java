
package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.criteria.PAFluxoOrigemCriteria;
import br.gov.ms.detran.processo.administrativo.ejb.IPAApoioService;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoOrigemWrapper;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

/**
 *
 * @author Carlos Eduardo
 */
@ManagedBean
@Path("pafluxoorigems")
public class PAFluxoOrigemResource extends PaResource<PAFluxoOrigemWrapper, PAFluxoOrigemCriteria> {
    
    @EJB(mappedName = "ejb/PAApoioService")
    private IPAApoioService service;

    
    
    @Override
    protected void executarGravacao(@Context HttpServletRequest request, IBaseEntity entidade) throws AppException {
        
        PAFluxoOrigemWrapper wrapper = (PAFluxoOrigemWrapper) entidade;
        ApoioOrigemInstauracao apoio;
        apoio = service.getApoioOrigemInstauracaoById(wrapper.getEntidade().getOrigemInstauracao().getId());
        wrapper.getEntidade().getOrigemInstauracao().setVersaoRegistro(apoio.getVersaoRegistro());
        if(wrapper.getEntidade().getIndiceFluxoInicial() == null){
            DetranWebUtils.applicationMessageException("É necessário informar Indicativo Fluxo Inicial");
        }
        super.executarGravacao(request, entidade);
    }
    
    
    
    
    
}
