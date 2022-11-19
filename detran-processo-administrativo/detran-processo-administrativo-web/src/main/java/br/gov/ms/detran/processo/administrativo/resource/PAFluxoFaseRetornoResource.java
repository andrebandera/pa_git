package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.projeto.anotacao.resource.ResourceConfig;
import br.gov.ms.detran.processo.administrativo.criteria.PAFluxoFaseRetornoCriteria;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoFaseRetornoWrapper;
import javax.annotation.ManagedBean;
import javax.ws.rs.Path;

/**
 *
 * @author Carlos Eduardo
 */
@ManagedBean
@Path("pafluxofaseretornos")
@ResourceConfig(condicaoProcesso = "PAFluxoFaseRetornoCP")
public class PAFluxoFaseRetornoResource extends PaResource<PAFluxoFaseRetornoWrapper, PAFluxoFaseRetornoCriteria>{
    
}
