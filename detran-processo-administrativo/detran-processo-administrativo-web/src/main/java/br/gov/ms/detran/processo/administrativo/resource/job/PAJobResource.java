/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource.job;

import br.gov.ms.detran.comum.scheduler.ejbtimer.JobResponse;
import br.gov.ms.detran.comum.scheduler.ejbtimer.JobWrapper;
import br.gov.ms.detran.comum.util.logger.Logger;
import javax.annotation.ManagedBean;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Christiano Carrilho.
 */
@ManagedBean
@Path("jobs")
public class PAJobResource {

    private static final Logger LOGGER = Logger.getLogger(PAJobResource.class);

    @POST
    @Path("situacaoentregacnhs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response situacaoEntregaCnh(JobWrapper wrapper) {

        PAJobCnhSituacaoEntrega job = new PAJobCnhSituacaoEntrega();
        job.execute(wrapper);

        return Response.ok().entity(new JobResponse().setMensagem("Executado com sucesso!")).build();
    }
    
    @POST
    @Path("checapenacumpridapjucartorio")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checaPenaCumpridaPJUCartorio(JobWrapper wrapper) {

        PAJobCumprimentoPenaPJUCartorio job = new PAJobCumprimentoPenaPJUCartorio();
        job.execute(wrapper);

        return Response.ok().entity(new JobResponse().setMensagem("Executado com sucesso!")).build();
    }
    
    @POST
    @Path("atualizainformacaoprovapjucartorio")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response atualizaInformacaoProvaPJUCartorio(JobWrapper wrapper) {

        PAJobAtualizarInfoProvaPJUCartorio job = new PAJobAtualizarInfoProvaPJUCartorio();
        job.execute(wrapper);

        return Response.ok().entity(new JobResponse().setMensagem("Executado com sucesso!")).build();
    }
    
    @POST
    @Path("arquivapasuspensaoporcassacao")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response arquivaPASuspensaoPorCassacao(JobWrapper wrapper) {

        PAJobArquivarPASuspensaoPorCassacao job = new PAJobArquivarPASuspensaoPorCassacao();
        job.execute(wrapper);

        return Response.ok().entity(new JobResponse().setMensagem("Executado com sucesso!")).build();
    }
    
    @POST
    @Path("executarandamentosautomaticos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response executarAndamentosAutomaticos(JobWrapper wrapper) {

        PAJobExecutarAndamentoAutomatico job = new PAJobExecutarAndamentoAutomatico();
        job.execute(wrapper);

        return Response.ok().entity(new JobResponse().setMensagem("Executado com sucesso!")).build();
    }
    
    @POST
    @Path("verificarterminoprazonotificacao")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response verificarTerminoPrazoNotificacao(JobWrapper wrapper) {

        PAJobVerificarTerminoPrazoNotificacao job = new PAJobVerificarTerminoPrazoNotificacao();
        job.execute(wrapper);

        return Response.ok().entity(new JobResponse().setMensagem("Executado com sucesso!")).build();
    }
    
    @POST
    @Path("arquivarprocessosprescritos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response arquivarProcessosPrescritos(JobWrapper wrapper) {

        PAJobArquivarProcessosPrescritos job = new PAJobArquivarProcessosPrescritos();
        job.execute(wrapper);

        return Response.ok().entity(new JobResponse().setMensagem("Executado com sucesso!")).build();
    }
    
    @POST
    @Path("incluirbloqueiopessoa")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response incluirBloqueioPessoa(JobWrapper wrapper) {

        PAJobIncluirRascunhoBloqueio job = new PAJobIncluirRascunhoBloqueio();
        job.execute(wrapper);

        return Response.ok().entity(new JobResponse().setMensagem("Executado com sucesso!")).build();
    }
    
    @POST
    @Path("enviaremailprazonotificacoes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response enviarEmailPrazoNotificacoes(JobWrapper wrapper) {

        PAJobEnviarEmailPrazoNotificacoes job = new PAJobEnviarEmailPrazoNotificacoes();
        job.execute(wrapper);

        return Response.ok().entity(new JobResponse().setMensagem("Executado com sucesso!")).build();
    }
}