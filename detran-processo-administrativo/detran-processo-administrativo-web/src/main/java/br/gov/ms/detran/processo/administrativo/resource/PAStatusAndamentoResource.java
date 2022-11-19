/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.criteria.PAStatusAndamentoCriteria;
import br.gov.ms.detran.processo.administrativo.ejb.IPAApoioService;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatus;
import br.gov.ms.detran.processo.administrativo.wrapper.PAStatusAndamentoWrapper;
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
@Path("pastatusandamentos")
public class PAStatusAndamentoResource extends PaResource<PAStatusAndamentoWrapper, PAStatusAndamentoCriteria> {
    @EJB(mappedName = "ejb/PAApoioService")
    private IPAApoioService service;
    
    @Override
    protected void executarGravacao(@Context HttpServletRequest request, IBaseEntity entidade) throws AppException {
        
        PAStatusAndamentoWrapper wrapper = (PAStatusAndamentoWrapper) entidade;
        PAStatus status;
        status = service.getPAStatusAtivoPorId(wrapper.getEntidade().getStatus().getId());
        wrapper.getEntidade().getStatus().setVersaoRegistro(status.getVersaoRegistro());
        super.executarGravacao(request, entidade);
    }
}
