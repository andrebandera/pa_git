/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.processo.administrativo.criteria.PAFluxoAndamentoCriteria;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoAndamentoWrapper;
import javax.annotation.ManagedBean;
import javax.ws.rs.Path;

/**
 *
 * @author Carlos Eduardo
 */
@ManagedBean
@Path("pafluxoandamentos")
public class PAFluxoAndamentoResource extends PaResource<PAFluxoAndamentoWrapper, PAFluxoAndamentoCriteria>{
    
}
