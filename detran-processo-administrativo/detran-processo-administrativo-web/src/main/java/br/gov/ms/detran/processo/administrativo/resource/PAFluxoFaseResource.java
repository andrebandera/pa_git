/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.projeto.anotacao.resource.ResourceConfig;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.SelectItem;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.criteria.PAFluxoFaseCriteria;
import br.gov.ms.detran.processo.administrativo.ejb.IPAApoioService;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.enums.TipoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoFaseWrapper;
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
@Path("pafluxofases")
@ResourceConfig(condicaoProcesso = "PAFluxoFaseCP")
public class PAFluxoFaseResource extends PaResource<PAFluxoFaseWrapper, PAFluxoFaseCriteria> {
    
    @EJB(mappedName = "ejb/PAApoioService")
    private IPAApoioService service;
    
    @Override
    protected void executarDepoisDeCarregar(HttpServletRequest request) throws DatabaseException {
        PAFluxoFase entity = getEntity().getEntidade();
        Integer resultado =  service.recuperarUltimoValorPrioridadePorPerfil(entity.getPrioridadeFluxoAmparo().getId(), entity);

        Integer prioridade = 0;

        if (resultado != null) {
            prioridade = resultado;
        }

        entity.setPrioridade(prioridade + 1);
    }
    
    @Override
    protected void executarGravacao(HttpServletRequest request, IBaseEntity entidade) throws AppException, DatabaseException {
        service.salvarFluxoFase(entidade);
    }
    
     @Override
    protected void executarRemocao() throws AppException {
        
        Long id =(Long)getEntity().getId();
        PAFluxoFase entidade = service.getPAFluxoFasePorId(id);
        service.removerPorPerfil(entidade);
        super.executarRemocao();
    }
    
    @PUT
    @Path("aumentarprioridade")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response moverPrioridadeParaCima(@Context HttpServletRequest request,
    										@Context SecurityContext context,
    										PAFluxoFaseWrapper entity) throws DatabaseException {
        PAFluxoFase entidade = entity.getEntidade();
    	Integer maior = service.recuperarUltimoValorPrioridadePorPerfil(entidade.getPrioridadeFluxoAmparo().getId(), entidade);
        Integer novaOrdem = entidade.getPrioridade();
    	
    	if (novaOrdem == 1) {
            novaOrdem = maior;
        } else {
            novaOrdem--;
        }
    	//service.desativarPAPrioridadeFluxoAmparo(entity, novaOrdem);
        
        service.trocarPrioridadesFluxoFase(entidade, novaOrdem);
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
            PAFluxoFaseWrapper entity) throws DatabaseException {
    	PAFluxoFase entidade = entity.getEntidade();
    	Integer maior = service.recuperarUltimoValorPrioridadePorPerfil(entidade.getPrioridadeFluxoAmparo().getId(), entidade);
        
        Integer novaOrdem = entidade.getPrioridade();
        
         if (novaOrdem == maior) {
        	 novaOrdem = 1;
         } else {
        	 novaOrdem++;
         }
         
         service.trocarPrioridadesFluxoFase(entidade, novaOrdem);
         return getResponseOk();
    }
    
    
    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> list = new ArrayList<>();
        //Select Option TipoAndamento
        List<SelectItem> listaTipoAndamento = DetranWebUtils.getSelectItems(TipoAndamentoEnum.class, true);
        list.add(new ListSelectItem("tipoAndamento", listaTipoAndamento));
        return list;
    }
    
    
}
