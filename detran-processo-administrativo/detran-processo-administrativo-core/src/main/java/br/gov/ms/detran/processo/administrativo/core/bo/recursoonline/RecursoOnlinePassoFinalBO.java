package br.gov.ms.detran.processo.administrativo.core.bo.recursoonline;

import br.gov.ms.detran.comum.projeto.service.FabricaJSPASequencial;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoPAOnlineRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPassoRecursoOnlinePA;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnline;
import br.gov.ms.detran.processo.administrativo.enums.PassoRecursoOnlinePAEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoSituacaoPAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoDocumentoRecursoPAOnlineEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoOnlinePaDocumentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoOnlinePaWrapper;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RecursoOnlinePassoFinalBO extends FabricaJSPASequencial implements IPassoRecursoOnlinePA {

    private static final Logger LOG = Logger.getLogger(RecursoOnlinePassoFinalBO.class);

    @Override
    public RecursoOnlinePaWrapper executa(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {
        LOG.info("Passo Final - EXECUTA");

        valida(em, wrapper);

        return registra(em, wrapper);
    }

    @Override
    public void valida(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {

        validaObrigatoriedade(wrapper);

        RecursoPAOnline recurso
                = new RecursoPAOnlineRepositorio().
                getRecursoOnlinePorTokenEProtocoloEPasso(em,
                        wrapper.getToken(),
                        wrapper.getProtocolo(),
                        PassoRecursoOnlinePAEnum.PASSO_04);

        new RecursoOnlineBO().validarExucucaoPassoRecursoOnline(em, wrapper, recurso.getProcessoAdministrativo());
    }

    private void validaObrigatoriedade(RecursoOnlinePaWrapper wrapper) throws AppException {
        Map<TipoDocumentoRecursoPAOnlineEnum, RecursoOnlinePaDocumentoWrapper> mapValidaDocumento = new HashMap();

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getProtocolo()) && DetranStringUtil.ehBrancoOuNulo(wrapper.getToken())) {
            DetranWebUtils.applicationMessageException("Token e Protocolo não informados.");
        }

        if (wrapper.getFormularioRecursoAssinado() == null || wrapper.getFormularioRecursoAssinado().getTipoDocumento() == null) {
            DetranWebUtils.applicationMessageException("severity.error.application.obrigatorio",
                    null, new String[]{"Documento: Formulário Assinado" });
        }

        mapValidaDocumento.put(wrapper.getFormularioRecursoAssinado().getTipoDocumento(), wrapper.getFormularioRecursoAssinado());

        new RecursoOnlineBO().validaDocumento(mapValidaDocumento, TipoDocumentoRecursoPAOnlineEnum.DOCUMENTO_FORMULARIO_ASSINADO);
    }

    @Override
    public RecursoOnlinePaWrapper registra(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {
        RecursoPAOnlineRepositorio repo = new RecursoPAOnlineRepositorio();
        RecursoOnlineBO bo = new RecursoOnlineBO();

        bo.desativarPassoRecursoOnline(em, wrapper);

        RecursoPAOnline recursoPassFinal = bo.criarNovaInstanciaRecursoOnline(em, wrapper, PassoRecursoOnlinePAEnum.PASSO_04);

        recursoPassFinal.setPasso(PassoRecursoOnlinePAEnum.PASSO_FINAL);
        recursoPassFinal.setOrdemPasso(PassoRecursoOnlinePAEnum.PASSO_FINAL.getCodigo());
        recursoPassFinal.setSituacao(RecursoSituacaoPAEnum.BACKOFFICE);
        recursoPassFinal.setIp(wrapper.getIp());

        recursoPassFinal.setDataRecurso(Calendar.getInstance().getTime());

        repo.insert(em, recursoPassFinal);

        new RecursoPAOnlineArquivoBO().gravarFormularioAssinado(em, wrapper, recursoPassFinal);

        copiarDocumentosPasso4(em, recursoPassFinal);

        new RecursoOnlineBO().envioEmailAberturaRecurso(em, recursoPassFinal);

        return retorno(em, recursoPassFinal, wrapper);
    }

    private void copiarDocumentosPasso4(EntityManager em, RecursoPAOnline recursoPassFinal) throws DatabaseException {

        RecursoPAOnline recursoPasso4
                = new RecursoPAOnlineRepositorio().
                getRecursoOnlinePorTokenEProtocoloEPasso(em,
                        recursoPassFinal.getToken(),
                        recursoPassFinal.getProtocolo(),
                        PassoRecursoOnlinePAEnum.PASSO_04);

        new RecursoOnlineBO().copiarDocumentos(em, recursoPassFinal, recursoPasso4);
    }

    @Override
    public RecursoOnlinePaWrapper retorno(EntityManager em, RecursoPAOnline recursoOnline, RecursoOnlinePaWrapper wrapper) throws AppException {
        wrapper.setPasso(PassoRecursoOnlinePAEnum.PASSO_FINAL.ordinal());

        return wrapper;
    }
}
