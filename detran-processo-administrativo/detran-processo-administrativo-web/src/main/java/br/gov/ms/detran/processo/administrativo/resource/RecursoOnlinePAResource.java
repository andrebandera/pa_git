package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.entidade.enums.apo.TipoExtensaoArquivoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.ExceptionUtils;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnline;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnlineArquivo;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnlineFalha;
import br.gov.ms.detran.processo.administrativo.enums.ConsultaRecursoPaOnlineSituacaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.PassoRecursoOnlinePAEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoSituacaoPAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoDocumentoRecursoPAOnlineEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoOnlinePaDocumentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoOnlinePaWrapper;

import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.gov.ms.detran.processo.administrativo.enums.RecursoSituacaoPAEnum.*;

@ManagedBean
@Path("/pa/recursoonlinepa")
public class RecursoOnlinePAResource extends PaResource {

    private static final Logger LOG = Logger.getLogger(RecursoOnlinePAResource.class);

    @PUT
    @Path("registra")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registra(
            @Context HttpServletRequest request, @Context SecurityContext context, RecursoOnlinePaWrapper wrapper) throws AppException {

        try {

            LOG.debug("Registra -> RECURSO ONLINE.");

            Boolean passoConsultaAndamento = checaPassoConsultaAndamento(wrapper);

            if (passoConsultaAndamento) {
                view.setEntity(consultaAndamento(wrapper));
            } else {
                view.addEntity(registraPasso(request, wrapper));
            }

            DetranWebUtils.addInfoMessage("application.message.operacao.sucesso", view);

        } catch (DatabaseException e) {

            LOG.error("Ocorreu uma exceção ao registrar.", e);
            DetranWebUtils.addErrorMessage("recursoonlinepa.M1", view);

            registraFalha(e, wrapper);

        } catch (AppException e) {

            LOG.error("Ocorreu uma exceção ao registrar.", e);

            DetranWebUtils.addErrorMessage(e, view);
            registraFalha(e, wrapper);

        } catch (Exception e) {

            LOG.error("Ocorreu uma exceção ao registrar.", e);
            DetranWebUtils.addErrorMessage("recursoonlinepa.M1", view);

            registraFalha(e, wrapper);
        }

        return Response.ok().entity(view).build();
    }

    private RecursoOnlinePaWrapper registraPasso(HttpServletRequest request, RecursoOnlinePaWrapper wrapper) throws AppException {

        RecursoOnlinePaWrapper recursoOnlineWrapper
                = (RecursoOnlinePaWrapper) getProcessoAdministrativoService().registraPassoRecursoOnline(wrapper);

        if (recursoOnlineWrapper == null) {
            DetranWebUtils.applicationMessageException("Operação não processada, informações inválidas.");
        }

        if (PassoRecursoOnlinePAEnum.PASSO_04.equals(PassoRecursoOnlinePAEnum.getPassoPorOrdinal(wrapper.getPasso()))) {

            recursoOnlineWrapper.setFormularioRecurso(
                    getProcessoAdministrativoService()
                            .geraDocumentoFormularioRecursoOnline(
                                    recursoOnlineWrapper.getToken(),
                                    recursoOnlineWrapper.getProtocolo(),
                                    DetranReportsUtil.getReportsBaseBirtUrl(request, Boolean.TRUE)
                            )
            );
        }

        return recursoOnlineWrapper;
    }

    private void registraFalha(Exception e, RecursoOnlinePaWrapper wrapper) {

        try {
            RecursoPAOnlineFalha falha
                    = new RecursoPAOnlineFalha(
                    new ExceptionUtils().getStack(e),
                    wrapper.getNumeroProcesso(),
                    wrapper.getCpf(),
                    "Falha registrar"
            );

            getProcessoAdministrativoService().registraFalhaRecursoOnline(falha);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean checaPassoConsultaAndamento(RecursoOnlinePaWrapper wrapper) throws AppException {

        Boolean consultaAndamento = Boolean.FALSE;

        if (!DetranStringUtil.ehBrancoOuNulo(wrapper.getProtocolo())) {

            RecursoPAOnline recursoOnlineMaisRecente
                    = getProcessoAdministrativoService()
                    .getRecursoOnlineMaisRecenteTokenOuProtocolo(wrapper.getToken(), wrapper.getProtocolo());

            if (recursoOnlineMaisRecente == null) {
                DetranWebUtils.applicationMessageException("recursoonlinepa.M10", null, wrapper.getProtocolo());
            }

            if (PassoRecursoOnlinePAEnum.PASSO_FINAL.equals(recursoOnlineMaisRecente.getPasso())) {

                List<RecursoSituacaoPAEnum> lSituacao = new ArrayList();

                lSituacao.add(EFETIVADO);
                lSituacao.add(BACKOFFICE);
                lSituacao.add(RECUSADO_EXPIROU_PRAZO);

                consultaAndamento = lSituacao.contains(recursoOnlineMaisRecente.getSituacao());
            }
        }

        return consultaAndamento;
    }

    private List<RecursoOnlinePaWrapper> consultaAndamento(RecursoOnlinePaWrapper wrapper) throws AppException {

        List<RecursoOnlinePaWrapper> lConsultaAndamento = new ArrayList();

        try {

            LOG.debug("Status -> RECURSO ONLINE.");

            RecursoPAOnline recursoOnline = getProcessoAdministrativoService()
                    .getRecursoOnlineMaisRecenteTokenOuProtocolo(wrapper.getToken(), wrapper.getProtocolo());

            RecursoOnlinePaWrapper consultaWrapper = montarDadosConsulta(recursoOnline);

            RecursoPAOnlineArquivo formularioAssinado = getProcessoAdministrativoService().getFormularioAssinadoDoRecursoOnline(recursoOnline);
            if (formularioAssinado != null) {
                RecursoOnlinePaDocumentoWrapper docWrapper = new RecursoOnlinePaDocumentoWrapper();
                docWrapper.setByteArquivo(formularioAssinado.getArquivoPA().getByteArquivo());
                docWrapper.setNomeArquivo(TipoDocumentoRecursoPAOnlineEnum.DOCUMENTO_FORMULARIO_ASSINADO.getNomeDocumento());
                docWrapper.setTipoArquivo(TipoExtensaoArquivoEnum.PDF.name().toLowerCase());
                docWrapper.setTipoDocumento(TipoDocumentoRecursoPAOnlineEnum.DOCUMENTO_FORMULARIO_ASSINADO);

                consultaWrapper.setFormularioRecursoAssinado(docWrapper);
            }


            if (consultaWrapper != null) {
                lConsultaAndamento.add(consultaWrapper);
            }

            if (DetranCollectionUtil.ehNuloOuVazio(lConsultaAndamento)) {
                DetranWebUtils.addErrorMessage("recursoonlinepa.M10", null, wrapper.getProtocolo());
            }

        } catch (DatabaseException e) {

            LOG.error("Ocorreu uma exceção ao obter andamento.", e);
            DetranWebUtils.addErrorMessage("recursoonlinepa.M1", view);

            registraFalha(e, null);

        } catch (AppException e) {

            LOG.error("Ocorreu uma exceção ao obter andamento.", e);
            DetranWebUtils.addErrorMessage(e, view);

            registraFalha(e, null);

        } catch (Exception e) {

            LOG.error("Ocorreu uma exceção ao obter andamento.", e);
            DetranWebUtils.addErrorMessage("recursoonlinepa.M1", view);

            registraFalha(e, null);
        }

        return lConsultaAndamento;
    }

    private RecursoOnlinePaWrapper montarDadosConsulta(RecursoPAOnline recursoOnline) {

        RecursoOnlinePaWrapper wrapper = new RecursoOnlinePaWrapper();

        Map<RecursoSituacaoPAEnum, ConsultaRecursoPaOnlineSituacaoEnum> mapAndamento = new HashMap();
        mapAndamento.put(RECUSADO_EXPIROU_PRAZO, ConsultaRecursoPaOnlineSituacaoEnum.RECUSADO_EXPIROU_PRAZO);
        mapAndamento.put(BACKOFFICE, ConsultaRecursoPaOnlineSituacaoEnum.EM_ANALISE);
        mapAndamento.put(EFETIVADO, ConsultaRecursoPaOnlineSituacaoEnum.PROTOCOLADO);

        if (recursoOnline != null) {
            wrapper.setAndamento(mapAndamento.get(recursoOnline.getSituacao()));
            wrapper.setConsultaAndamento(true);
            wrapper.setProtocolo(recursoOnline.getProtocolo());
            wrapper.setNumeroProcesso(recursoOnline.getNumeroProcesso());
            wrapper.setRequerente(recursoOnline.getRequerente());
            wrapper.setDataRecurso(recursoOnline.getDataRecurso());
            wrapper.setTipoRecurso(recursoOnline.getTipo());
        }


        return wrapper;
    }
}
