/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.pa.instauracao.service;

import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.ejb.ISequencialService;
import br.gov.ms.detran.processo.administrativo.ejb.resource.IPASequencialResourceService;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Christiano Carrilho
 */
@Path("seqs")
@Stateless
@Remote(IPASequencialResourceService.class)
public class PASequencialResourceService implements IPASequencialResourceService {

    @GET
    @Path("getNumeroProcesso")
    @Produces(MediaType.TEXT_PLAIN)
    @Override
    public synchronized String getNumeroProcesso() throws AppException {

        ISequencialService sequencialService = JNDIUtil.lookup(ISequencialService.class, "ejb/SequencialService");

        return sequencialService.getNumeroProcesso();
    }

    @GET
    @Path("getNumeroPortaria")
    @Produces(MediaType.TEXT_PLAIN)
    @Override
    public synchronized String getNumeroPortaria() throws AppException {

        ISequencialService sequencialService = JNDIUtil.lookup(ISequencialService.class, "ejb/SequencialService");

        return sequencialService.getNumeroPortaria();
    }

    @GET
    @Path("getNumeroBloqueioBCA")
    @Produces(MediaType.TEXT_PLAIN)
    @Override
    public synchronized String getNumeroBloqueioBCA() throws AppException {
        
        ISequencialService sequencialService = JNDIUtil.lookup(ISequencialService.class, "ejb/SequencialService");

        return sequencialService.getNumeroBloqueioBCA();
    }
    
    @GET
    @Path("getNumeroProtocolo")
    @Produces(MediaType.TEXT_PLAIN)
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized String getNumeroProtocolo() throws AppException {
        
        ISequencialService sequencialService = JNDIUtil.lookup(ISequencialService.class, "ejb/SequencialService");

        return sequencialService.getNumeroProtocolo();
    }
}