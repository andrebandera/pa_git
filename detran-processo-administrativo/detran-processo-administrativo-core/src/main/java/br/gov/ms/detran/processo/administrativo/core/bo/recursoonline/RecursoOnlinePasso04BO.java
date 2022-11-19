package br.gov.ms.detran.processo.administrativo.core.bo.recursoonline;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoArquivo;
import br.gov.ms.detran.comum.core.projeto.enums.apo.ModuloTipoArquivoEnum;
import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.TipoExtensaoArquivoEnum;
import br.gov.ms.detran.comum.projeto.service.FabricaJSPASequencial;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranHTTPUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoPAOnlineRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPassoRecursoOnlinePA;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnline;
import br.gov.ms.detran.processo.administrativo.enums.PassoRecursoOnlinePAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoDocumentoRecursoPAOnlineEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoOnlinePaDocumentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoOnlinePaWrapper;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static br.gov.ms.detran.processo.administrativo.constantes.TipoArquivoPAConstante.ANEXOS_RECURSO_ONLINE_PA;

public class RecursoOnlinePasso04BO extends FabricaJSPASequencial implements IPassoRecursoOnlinePA {

    private static final Logger LOG = Logger.getLogger(RecursoOnlinePasso04BO.class);

    private IApoioService apoioService;

    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    @Override
    public RecursoOnlinePaWrapper executa(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {
        LOG.info("Passo 4 - EXECUTA");

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
                        PassoRecursoOnlinePAEnum.PASSO_03);

        new RecursoOnlineBO().validarExucucaoPassoRecursoOnline(em, wrapper, recurso.getProcessoAdministrativo());
    }

    private void validaObrigatoriedade(RecursoOnlinePaWrapper wrapper) throws AppException {

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getProtocolo()) && DetranStringUtil.ehBrancoOuNulo(wrapper.getToken()))
            DetranWebUtils.applicationMessageException("Token e Protocolo não informados.");

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getDescricao())) {
            DetranWebUtils.applicationMessageException("recursoonlinepa.M4", "", "Descrição Recurso");
        }

        validarDocumentosObrigatorios(wrapper);
    }

    private void validarDocumentosObrigatorios(RecursoOnlinePaWrapper wrapper) throws AppException {
        Map<TipoDocumentoRecursoPAOnlineEnum, RecursoOnlinePaDocumentoWrapper> mapValidaDocumento = new HashMap();

        for (RecursoOnlinePaDocumentoWrapper documento : wrapper.getDocumentos()) {
            mapValidaDocumento.put(documento.getTipoDocumento(), documento);
        }

        new RecursoOnlineBO().validaDocumento(mapValidaDocumento, TipoDocumentoRecursoPAOnlineEnum.CNH);
        new RecursoOnlineBO().validaDocumento(mapValidaDocumento, TipoDocumentoRecursoPAOnlineEnum.NOTIFICACAO_PA);
    }


    @Override
    public RecursoOnlinePaWrapper registra(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {
        RecursoPAOnlineRepositorio repo = new RecursoPAOnlineRepositorio();
        RecursoOnlineBO bo = new RecursoOnlineBO();

        bo.desativarPassoRecursoOnline(em, wrapper);

        RecursoPAOnline recursoPasso4 = bo.criarNovaInstanciaRecursoOnline(em, wrapper, PassoRecursoOnlinePAEnum.PASSO_03);

        recursoPasso4.setPasso(PassoRecursoOnlinePAEnum.PASSO_04);
        recursoPasso4.setOrdemPasso(PassoRecursoOnlinePAEnum.PASSO_04.getCodigo());
        recursoPasso4.setIp(wrapper.getIp());

        recursoPasso4.setDescricaoRecurso(wrapper.getDescricao());
        recursoPasso4.setDataRecurso(Calendar.getInstance().getTime());

        repo.insert(em, recursoPasso4);

        new RecursoPAOnlineArquivoBO().gravarArquivosRecursoOnline(em, wrapper, recursoPasso4);

        return retorno(em, recursoPasso4, wrapper);
    }

    @Override
    public RecursoOnlinePaWrapper retorno(EntityManager em, RecursoPAOnline recursoOnline, RecursoOnlinePaWrapper wrapper) throws AppException {

        wrapper.setPasso(PassoRecursoOnlinePAEnum.PASSO_04.ordinal());

        wrapper.setProcessoAdministrativo(null);

        return wrapper;
    }

    public RecursoOnlinePaDocumentoWrapper geraDocumentoFormularioRecursoOnline(EntityManager em, String token, String protocolo, String urlBirt) throws AppException {

        RecursoPAOnline recursoOnlinePasso4 = new RecursoPAOnlineRepositorio().
                getRecursoOnlinePorTokenEProtocoloEPasso(em,
                        token,
                        protocolo,
                        PassoRecursoOnlinePAEnum.PASSO_04);

        byte[] documentoFormulario = getDocumentoFormulario(recursoOnlinePasso4, urlBirt);

        RecursoOnlinePaDocumentoWrapper documento = new RecursoOnlinePaDocumentoWrapper();

        documento.setByteArquivo(documentoFormulario);
        documento.setNomeArquivo(TipoDocumentoRecursoPAOnlineEnum.DOCUMENTO_FORMULARIO.getNomeDocumento());
        documento.setTipoArquivo(TipoExtensaoArquivoEnum.PDF.name().toLowerCase());
        documento.setTipoDocumento(TipoDocumentoRecursoPAOnlineEnum.DOCUMENTO_FORMULARIO);

        TipoArquivo tipoArquivo =
                (TipoArquivo) getApoioService()
                        .getTipoArquivoPorCodigoEModulo(
                                ANEXOS_RECURSO_ONLINE_PA,
                                ModuloTipoArquivoEnum.PA);

        new RecursoPAOnlineArquivoBO().gravar(em, recursoOnlinePasso4, tipoArquivo, documento);

        return documento;
    }

    private byte[] getDocumentoFormulario(RecursoPAOnline recursoOnline, String urlBirt) throws AppException {

        Map<String, String> parametros = new HashMap();
        parametros.put("v_numero_protocolo", recursoOnline.getProtocolo());

        String urlRelatorio
                = DetranReportsUtil.getReportParamsBirtUrl(
                "formulario_recurso_online",
                FormatoRelatorioEnum.PDF.getRptFormat(),
                parametros,
                "relatorios/processoadministrativo/recurso/online/"
        );

        return DetranHTTPUtil.download(urlBirt + urlRelatorio);
    }

}
