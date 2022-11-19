package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.projeto.anotacao.resource.ResourceConfig;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.criteria.PAPrioridadeFluxoAmparoCriteria;
import br.gov.ms.detran.processo.administrativo.ejb.IPAApoioService;
import br.gov.ms.detran.processo.administrativo.entidade.PAPrioridadeFluxoAmparo;
import br.gov.ms.detran.processo.administrativo.wrapper.PAPrioridadeFluxoAmparoWrapper;
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
@Path("paprioridadefluxoamparos")
@ResourceConfig(condicaoProcesso = "PAPrioridadeFluxoAmparoCP")
public class PAPrioridadeFluxoAmparoResource extends PaResource<PAPrioridadeFluxoAmparoWrapper, PAPrioridadeFluxoAmparoCriteria> {
    
    @EJB(mappedName = "ejb/PAApoioService")
    private IPAApoioService service;
    
    
    @Override
    protected void executarDepoisDeCarregar(HttpServletRequest request) throws DatabaseException {
        PAPrioridadeFluxoAmparo entity = getEntity().getEntidade();
        Integer resultado =  service.recuperarUltimoValorPrioridadePorPerfil(entity.getFluxoProcesso().getId(), entity);

        Integer prioridade = 0;

        if (resultado != null) {
            prioridade = resultado;
        }

        entity.setPrioridade(prioridade + 1);
    }

    @Override
    protected void executarGravacao(HttpServletRequest request, IBaseEntity entidade) throws AppException, DatabaseException {
        service.salvarPrioridadeFluxoAmparo(entidade);
    }
    
    @Override
    protected void executarRemocao() throws AppException {
        Long id =(Long)getEntity().getId();
        PAPrioridadeFluxoAmparo entidade = service.getPAPrioridadeFluxoAmparoPorId(id);
        service.removerPorPerfil(entidade);
//        service.removerPAFluxoFaseVinculos(id);
        super.executarRemocao();
    }
    
    @PUT
    @Path("aumentarprioridade")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response moverPrioridadeParaCima(@Context HttpServletRequest request,
    										@Context SecurityContext context,
    										PAPrioridadeFluxoAmparoWrapper entity) throws DatabaseException {
        PAPrioridadeFluxoAmparo entidade = entity.getEntidade();
    	Integer maior = service.recuperarUltimoValorPrioridadePorPerfil(entidade.getFluxoProcesso().getId(), entidade);
        Integer novaOrdem = entidade.getPrioridade();
    	
    	if (novaOrdem == 1) {
            novaOrdem = maior;
        } else {
            novaOrdem--;
        }
    	//service.desativarPAPrioridadeFluxoAmparo(entity, novaOrdem);
        
        service.trocarPrioridadesPAPrioridadeFluxoAmparo(entidade, novaOrdem);
       // service.ativarPAPrioridadeFluxoAmparo(entity, novaOrdem);
        return getResponseOk();
    }
    
    @PUT
    @Path("diminuirprioridade")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response moverPrioridadeParaBaixo(
            @Context HttpServletRequest request,
            @Context SecurityContext context,
            PAPrioridadeFluxoAmparoWrapper entity) throws DatabaseException {
    	PAPrioridadeFluxoAmparo entidade = entity.getEntidade();
    	Integer maior = service.recuperarUltimoValorPrioridadePorPerfil(entidade.getFluxoProcesso().getId(), entidade);
        Integer novaOrdem = entidade.getPrioridade();
        
         if (novaOrdem == maior) {
        	 novaOrdem = 1;
         } else {
        	 novaOrdem++;
         }
         service.trocarPrioridadesPAPrioridadeFluxoAmparo(entidade, novaOrdem);
         return getResponseOk();
    }
    
    
}
