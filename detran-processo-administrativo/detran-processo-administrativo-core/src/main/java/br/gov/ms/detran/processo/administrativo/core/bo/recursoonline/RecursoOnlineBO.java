package br.gov.ms.detran.processo.administrativo.core.bo.recursoonline;

import br.gov.ms.detran.comum.core.projeto.enums.adm.FormaProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.ResponsavelProtocoloEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.TipoExtensaoArquivoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.*;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.ExceptionUtils;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.constantes.TipoCorpoTextoPAConstante;
import br.gov.ms.detran.processo.administrativo.core.FtpHelper;
import br.gov.ms.detran.processo.administrativo.core.repositorio.*;
import br.gov.ms.detran.processo.administrativo.dto.DtnIntraPAApoioGeral;
import br.gov.ms.detran.processo.administrativo.ejb.IPassoRecursoOnlinePA;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.*;
import br.gov.ms.detran.processo.administrativo.enums.PassoRecursoOnlinePAEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoSituacaoPAEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoDocumentoRecursoPAOnlineEnum;
import br.gov.ms.detran.processo.administrativo.util.ProcessoAdministrativoUtil;
import br.gov.ms.detran.processo.administrativo.wrapper.*;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoOnlinePaWrapper;

import javax.persistence.EntityManager;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class RecursoOnlineBO {

    private static final Logger LOG = Logger.getLogger(RecursoOnlineBO.class);

    private final Map<PassoRecursoOnlinePAEnum, IPassoRecursoOnlinePA> mapPasso;

    private IApoioService apoioService;

    private IProcessoAdministrativoService processoAdministrativoService;

    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    public IProcessoAdministrativoService getProcessoAdministrativoService() {
        if (processoAdministrativoService == null) {
            processoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }
        return processoAdministrativoService;
    }

    public RecursoOnlineBO() {

        mapPasso = new HashMap();

        mapPasso.put(PassoRecursoOnlinePAEnum.PASSO_01, new RecursoOnlinePasso01BO());
        mapPasso.put(PassoRecursoOnlinePAEnum.PASSO_02, new RecursoOnlinePasso02BO());
        mapPasso.put(PassoRecursoOnlinePAEnum.PASSO_03, new RecursoOnlinePasso03BO());
        mapPasso.put(PassoRecursoOnlinePAEnum.PASSO_04, new RecursoOnlinePasso04BO());
        mapPasso.put(PassoRecursoOnlinePAEnum.PASSO_FINAL, new RecursoOnlinePassoFinalBO());
    }

    public IPassoRecursoOnlinePA getExecutaPasso(PassoRecursoOnlinePAEnum passo) throws AppException {

        if (mapPasso == null || passo == null) {
            DetranWebUtils.applicationMessageException("Execução inválida.");
        }

        return mapPasso.get(passo);
    }

    public void envioEmailIdentificacaoTokenProtocolo(EntityManager em, RecursoPAOnline recursoOnline) throws AppException {
        String tituloEmail = "Detran MS - Recurso " + recursoOnline.getTipo().toString() + " - Identificação";

        enviarEmail(em, recursoOnline, tituloEmail, TipoCorpoTextoPAConstante.EMAIL_TOKEN_PROTOCOLO_RECURSO_ONLINE_PA);
    }

    private StringBuilder montarCorpoEmailRecursoOnline(EntityManager em, RecursoPAOnline recursoOnline, Long tipoCorpoTexto) throws AppException {

        String corpoEmail = getCorpoTextoParaEmail(tipoCorpoTexto);

        StringBuilder body = new StringBuilder();

        body
                .append("<br>")
                .append(corpoEmail
                        .replaceAll("#tipo_recurso#", recursoOnline.getTipo() == null ? "" : recursoOnline.getTipo().toString())
                        .replaceAll("#destino#", recursoOnline.getDestino() == null ? "" : recursoOnline.getDestino().toString())
                        .replaceAll("#numero_token#", recursoOnline.getToken())
                        .replaceAll("#numero_protocolo#", recursoOnline.getProtocolo())
                        .replaceAll("#data#", Utils.formatDate(recursoOnline.getDataRecurso(), "dd/MM/yyyy"))
                        .replaceAll("#numero_processo#",
                                DetranStringUtil.ehBrancoOuNulo(recursoOnline.getNumeroProcesso())
                                ? "" : recursoOnline.getNumeroProcesso().substring(4) + "/" + recursoOnline.getNumeroProcesso().substring(0, 4))
                        .replaceAll("#endereco#", getEndereco(recursoOnline))
                        .replaceAll("#cidade_uf#", getMunicipioUF(recursoOnline))
                        .replaceAll("#cpf#", Utils.formatCPF(recursoOnline.getCpf()))
                        .replaceAll("#requerente#", recursoOnline.getRequerente())
                        .replaceAll("#motivo#", recursoOnline.getMotivoRecusa() == null ? "" : recursoOnline.getMotivoRecusa())
                        .replaceAll("#motivo_cancelamento#", recursoOnline.getMotivoCancelamento() == null ? "" : recursoOnline.getMotivoCancelamento()))
                .append("<br>");
        return body;
    }

    private String getMunicipioUF(RecursoPAOnline recursoOnline) throws AppException {

        StringBuilder municipioUF = new StringBuilder();

        municipioUF
                .append(recursoOnline.getMunicipio())
                .append(" - ")
                .append(recursoOnline.getUf());
        return municipioUF.toString();
    }

    private String getEndereco(RecursoPAOnline recursoOnline) {

        StringBuilder endereco = new StringBuilder();
        if (DetranStringUtil.ehBrancoOuNulo(recursoOnline.getEndereco())) {
            endereco.append("Nenhum endereço encontrado.");
        } else {

            endereco.append(recursoOnline.getEndereco());
            if (!DetranStringUtil.ehBrancoOuNulo(recursoOnline.getEnderecoNumero())) {
                endereco.append(", ").append(recursoOnline.getEnderecoNumero());
            }
            if (!DetranStringUtil.ehBrancoOuNulo(recursoOnline.getEnderecoComplemento())) {
                endereco.append(" ").append(recursoOnline.getEnderecoComplemento());
            }
            if (!DetranStringUtil.ehBrancoOuNulo(recursoOnline.getEnderecoBairro())) {
                endereco.append(" - ").append(recursoOnline.getEnderecoBairro());
            }
        }
        return endereco.toString();
    }

    private void envioEmail(String titulo, String corpoEmail, String[] emails) throws AppException {

        DetranEmailWrapper emailWrapper
                = new DetranEmailWrapper(
                        Arrays.asList(emails),
                        titulo,
                        corpoEmail,
                        "text/html; charset=\"UTF-8\""
                );

        try {

            emailWrapper.setFrom("nao-responder@detran.ms.gov.br");

            DetranEmailUtil.sendNaoResponder(emailWrapper);

        } catch (Exception ex) {

            LOG.error("Ocorreu um erro inesperado ao enviar Email.", ex);

            getProcessoAdministrativoService()
                    .registraFalhaRecursoOnline(new RecursoPAOnlineFalha(new ExceptionUtils().getStack(ex),
                            null,
                            null,
                            "Erro ao enviar email para " + emails.toString()));

            try {

                emailWrapper.setFrom("mailsystem@detran.ms.gov.br");

                DetranEmailUtil.sendReturnError(emailWrapper);

            } catch (Exception e) {

                LOG.error("Ocorreu um erro inesperado ao enviar Email.", e);
                DetranWebUtils.applicationMessageException("Operação não concluída, em breve será normalizado.");
            }
        }
    }

    private String getCorpoTextoParaEmail(Long CORPO_TEXTO) throws AppException {

        String texto = new DtnIntraPAApoioGeral().getTextoPadraoNotificacaoPorTipoCorpoTexto(CORPO_TEXTO);

        if (DetranStringUtil.ehBrancoOuNulo(texto)) {
            DetranWebUtils.applicationMessageException("recursoonlinepa.M1");
        }

        return texto;
    }

    public void desativarPassoRecursoOnline(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {
        RecursoPAOnlineRepositorio repo = new RecursoPAOnlineRepositorio();
        RecursoPAOnline recursoOnline = repo.
                getRecursoOnlinePorTokenEProtocoloEPasso(em,
                        wrapper.getToken(),
                        wrapper.getProtocolo(),
                        PassoRecursoOnlinePAEnum.getPassoPorOrdinal(wrapper.getPasso()));

        if (recursoOnline != null) {
            recursoOnline.setAtivo(AtivoEnum.DESATIVADO);
            repo.update(em, recursoOnline);
            new RecursoPAOnlineArquivoBO().desativarArquivosRecursoOnline(em, recursoOnline);
        }
    }

    public RecursoPAOnline criarNovaInstanciaRecursoOnline(EntityManager em, RecursoOnlinePaWrapper wrapper, PassoRecursoOnlinePAEnum passoAnterior)
            throws AppException {
        RecursoPAOnline novoPasso = new RecursoPAOnline();

        RecursoPAOnline recursoPassoAnterior = new RecursoPAOnlineRepositorio().
                getRecursoOnlinePorTokenEProtocoloEPasso(em,
                        wrapper.getToken(),
                        wrapper.getProtocolo(),
                        passoAnterior);

        ReflectUtil.copyProperties(novoPasso, recursoPassoAnterior);
        novoPasso.setId(null);
        novoPasso.setVersaoRegistro(null);
        novoPasso.setUsuarioAlteracao(null);
        novoPasso.setUsuarioInclusao(null);
        novoPasso.setDataAlteracao(null);
        novoPasso.setDataInclusao(null);
        novoPasso.setAtivo(AtivoEnum.ATIVO);

        return novoPasso;
    }

    public void validaSeJaExisteRecursoEfetivado(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {
        RecursoPAOnline recursoPAOnline = new RecursoPAOnlineRepositorio().
                getRecursoOnlineEfetivadoPorPAETipoEDestino(em,
                        wrapper.getProcessoAdministrativo().getId(),
                        wrapper.getTipoRecurso(),
                        wrapper.getDestinoRecurso());

        if (recursoPAOnline != null) {
            DetranWebUtils.applicationMessageException("Já existe um recurso com a situação [" + recursoPAOnline.getSituacao().toString() + "] para este Processo Administrativo.");
        }
    }

    public void validarExucucaoPassoRecursoOnline(EntityManager em, RecursoOnlinePaWrapper wrapper, ProcessoAdministrativo processoAdministrativo) throws AppException {

        ProcessoAdministrativoRecursoWrapper aberturaRecurso = new ProcessoAdministrativoRepositorio().validarProcessoAptoAberturaRecurso(em, processoAdministrativo);

        if (aberturaRecurso == null) {
            DetranWebUtils.applicationMessageException("Não é possível abrir recurso para o processo administrativo informado.");
        }

        wrapper.setDestinoRecurso(aberturaRecurso.getOrigemDestino());
        wrapper.setTipoRecurso(aberturaRecurso.getTipoRecurso());
        wrapper.setProcessoAdministrativo(aberturaRecurso.getProcessoAdministrativo());

        validaSeJaExisteRecursoEfetivado(em, wrapper);
    }

    public void validaDocumento(Map<TipoDocumentoRecursoPAOnlineEnum, RecursoOnlinePaDocumentoWrapper> mapValidaDocumento, TipoDocumentoRecursoPAOnlineEnum tipo) throws AppException {
        if (mapValidaDocumento.get(tipo) == null
                || mapValidaDocumento.get(tipo).getByteArquivo() == null) {

            DetranWebUtils.applicationMessageException("severity.error.application.obrigatorio",
                    null, new String[]{"Documento: " + tipo.name()});
        }
    }

    public void envioEmailAberturaRecurso(EntityManager em, RecursoPAOnline recursoOnline) throws AppException {
        String tituloEmail = "Detran MS - Recurso " + recursoOnline.getTipo().toString() + " - Solicitação Iniciada";

        enviarEmail(em, recursoOnline, tituloEmail, TipoCorpoTextoPAConstante.EMAIL_SOLICITACAO_ABERTURA_RECURSO_ONLINE_BACKOFFICE_PA);
    }

    public void envioEmailRecursoRecusado(EntityManager em, RecursoPAOnline recursoOnline) throws AppException {
        String tituloEmail = "Detran MS - Solicitação de Recurso de " + recursoOnline.getTipo().toString() + " Recusada";

        enviarEmail(em, recursoOnline, tituloEmail, TipoCorpoTextoPAConstante.EMAIL_RESULTADO_BACKOFFICE_RECUSADO_PA);
    }

    public List<RecursoOnlinePaDocumentoWrapper> montarDocumentoWrapper(List<RecursoPAOnlineArquivo> arquivos) {
        List<RecursoOnlinePaDocumentoWrapper> lWrapper = new ArrayList<>();
        for (RecursoPAOnlineArquivo arquivo : arquivos) {
            RecursoOnlinePaDocumentoWrapper docWrapper = new RecursoOnlinePaDocumentoWrapper(arquivo);
            docWrapper.setTipoDocumento(arquivo.getTipoDocumento());
            docWrapper.setNomeArquivo(arquivo.getTipoDocumento().getNomeDocumento());
            docWrapper.setTipoArquivo(arquivo.getArquivoPA().getExtensao().name().toLowerCase());
            docWrapper.setByteArquivo(arquivo.getArquivoPA().getByteArquivo());
            lWrapper.add(docWrapper);
        }

        return lWrapper;
    }

    public void recusarRecursoOnline(EntityManager em, BackOfficePaWrapper wrapper) throws AppException {
        RecursoPAOnlineRepositorio repositorio = new RecursoPAOnlineRepositorio();
        RecursoPAOnline recursoBackOffice = repositorio.find(em, RecursoPAOnline.class, wrapper.getEntidade().getId());

        Date novaDataLimite = atualizarDataLimiteRecurso(em, recursoBackOffice);

        RecursoPAOnline recursoRecusado = desativarRecursoBackOffice(em, recursoBackOffice);

        recursoRecusado.setDataPrazoLimite(novaDataLimite);
        recursoRecusado.setSituacao(RecursoSituacaoPAEnum.RECUSADO);
        recursoRecusado.setMotivoRecusa(wrapper.getMotivoRecusa());
        recursoRecusado.setIp(wrapper.getIp());
        recursoRecusado.setCpfUsuario(wrapper.getCpfUsuario());
        recursoRecusado.setDataResposta(Calendar.getInstance().getTime());

        repositorio.insert(em, recursoRecusado);

        copiarDocumentos(em, recursoRecusado, recursoBackOffice);

        envioEmailRecursoRecusado(em, recursoRecusado);

    }

    private Date atualizarDataLimiteRecurso(EntityManager em, RecursoPAOnline recursoBackOffice) throws DatabaseException {
        Date novaData = recursoBackOffice.getDataPrazoLimite();

        if (recursoBackOffice.getDataRecurso().before(recursoBackOffice.getDataPrazoLimite())) {
            NotificacaoProcessoAdministrativoRepositorio notificacaoRepo = new NotificacaoProcessoAdministrativoRepositorio();
            Integer diasEmBackOffice = Utils.getDiferencaEmDiasEntreDatas(recursoBackOffice.getDataPrazoLimite(), recursoBackOffice.getDataRecurso());
            NotificacaoProcessoAdministrativo notificacao
                    = notificacaoRepo
                            .getNotificacaoPorProcessoAdministrativoETipoNotificacao(em,
                                    recursoBackOffice.getProcessoAdministrativo().getId(),
                                    recursoBackOffice.getTipo());

            novaData = Utils.addDayMonth(Calendar.getInstance().getTime(), diasEmBackOffice);
            notificacao.setDataPrazoLimite(novaData);

            notificacaoRepo.update(em, notificacao);

        }
        return novaData;
    }

    public void copiarDocumentos(EntityManager em, RecursoPAOnline recursoNovo, RecursoPAOnline recursoAntigo) throws DatabaseException {
        RecursoPAOnlineArquivoRepositorio arquivoRepo = new RecursoPAOnlineArquivoRepositorio();

        List<RecursoPAOnlineArquivo> lArquivosRecursoAntigo = arquivoRepo.getListArquivosPorRecursoOnline(em, recursoAntigo.getId());

        for (RecursoPAOnlineArquivo arquivo : lArquivosRecursoAntigo) {

            RecursoPAOnlineArquivo novoArquivo = new RecursoPAOnlineArquivo();

            novoArquivo.setArquivoPA(arquivo.getArquivoPA());
            novoArquivo.setRecursoOnline(recursoNovo);
            novoArquivo.setAtivo(AtivoEnum.ATIVO);
            novoArquivo.setDescricao(arquivo.getDescricao());
            novoArquivo.setTipoDocumento(arquivo.getTipoDocumento());
            novoArquivo.setObservacao(arquivo.getObservacao());

            arquivoRepo.insert(em, novoArquivo);

        }
    }

    public RecursoPAOnline efetivarRecursoOnline(EntityManager em, BackOfficePaWrapper wrapper) throws AppException {
        RecursoPAOnlineRepositorio repositorio = new RecursoPAOnlineRepositorio();

        RecursoPAOnline recursoEfetivado = desativarRecursoBackOffice(em, wrapper.getEntidade());

        recursoEfetivado.setSituacao(RecursoSituacaoPAEnum.EFETIVADO);
        recursoEfetivado.setIp(wrapper.getIp());
        recursoEfetivado.setCpfUsuario(wrapper.getCpfUsuario());
        recursoEfetivado.setDataResposta(Calendar.getInstance().getTime());

        repositorio.insert(em, recursoEfetivado);

        copiarDocumentos(em, recursoEfetivado, wrapper.getEntidade());

        envioEmailRecursoEfetivado(em, recursoEfetivado);

        return recursoEfetivado;

    }

    private void envioEmailRecursoEfetivado(EntityManager em, RecursoPAOnline recursoEfetivado) throws AppException {
        String tituloEmail = "Detran MS - Solicitação de Recurso de " + recursoEfetivado.getTipo().toString() + " - Aprovada";

        enviarEmail(em, recursoEfetivado, tituloEmail, TipoCorpoTextoPAConstante.EMAIL_RESULTADO_BACKOFFICE_APROVADO_PA);
    }

    public RecursoWrapper montarRecursoWrapper(BackOfficePaWrapper wrapper) {

        RecursoWrapper recursoWrapper = new RecursoWrapper();
        Recurso recurso = new Recurso();
        recurso.setDataRecurso(wrapper.getEntidade().getDataRecurso());
        recurso.setSituacao(SituacaoRecursoEnum.EM_ANALISE);
        recurso.setTipoRecurso(wrapper.getEntidade().getTipo());
        recurso.setProcessoAdministrativo(wrapper.getEntidade().getProcessoAdministrativo());

        recursoWrapper.setEntidade(recurso);
        recursoWrapper.setDataRecurso(wrapper.getEntidade().getDataRecurso());
        recursoWrapper.setDesabilitadoDataRecurso(false);
        recursoWrapper.setDestino(wrapper.getEntidade().getDestino());
        recursoWrapper.setForma(FormaProtocoloEnum.SISTEMA);
        recursoWrapper.setResponsavel(ResponsavelProtocoloEnum.CONDUTOR);
        recursoWrapper.setIndiceForaPrazo(BooleanEnum.SIM.equals(wrapper.getEntidade().getIndiceTempestividade()) ? BooleanEnum.NAO : BooleanEnum.SIM);
        recursoWrapper.setObservacao("PROTOCOLADO POR EFETIVAÇÃO DO RECURSO ONLINE");
        recursoWrapper.setUrlReportBirt(wrapper.getUrlReportBirt());

        return recursoWrapper;

    }

    public void enviarArquivosRecursoOnlineParaFTP(EntityManager em, RecursoPAOnline recursoEfetivado, String urlReportBirt) throws AppException {

        List<byte[]> pdfs = new ArrayList<>();
        Map<String, String> parametros = new HashMap();

        RecursoMovimento movimento = new RecursoMovimentoRepositorio().getRecursoMovimentoPorRecurso(em, recursoEfetivado.getRecurso());
        String nomeArquivo = movimento.getProtocolo().getNumeroProtocolo();

        parametros.put("v_id_recurso", recursoEfetivado.getId().toString());

        String urlRelatorio
                = DetranReportsUtil
                        .getReportParamsBirtUrl(
                                "imagens_recurso_online",
                                FormatoRelatorioEnum.PDF.getRptFormat(),
                                parametros,
                                "relatorios/processoadministrativo/recurso/online/"
                        );

        pdfs.add(DetranHTTPUtil.download(urlReportBirt + urlRelatorio));

        List<RecursoPAOnlineArquivo> lArquivosRecursoAntigo
                = new RecursoPAOnlineArquivoRepositorio().
                        getListArquivosPorRecursoOnline(em, recursoEfetivado.getId());

        for (RecursoPAOnlineArquivo arquivo : lArquivosRecursoAntigo) {
            if (TipoExtensaoArquivoEnum.PDF.equals(arquivo.getArquivoPA().getExtensao())) {
                pdfs.add(arquivo.getArquivoPA().getByteArquivo());
            }
        }

        byte[] protocoloRecursoOnline = new ArquivoPARepositorio().getByteArquivoProtocoloRecursoOnline(em, recursoEfetivado.getNumeroProcesso());
        if (protocoloRecursoOnline != null) {
            pdfs.add(protocoloRecursoOnline);
        }

        String pathArquivo = getPathArquivo(nomeArquivo);

        ProcessoAdministrativoUtil.agruparArquivosPdf(pathArquivo, pdfs);

        FtpHelper.gravarArquivoPasta(pathArquivo, nomeArquivo, "FTP_PA_RECURSO_ONLINE_BPMS", TipoExtensaoArquivoEnum.PDF);

        FtpHelper.gravarArquivoPasta(pathArquivo, nomeArquivo, "FTP_PA_RECURSO_ONLINE_BPMS_BCKP", TipoExtensaoArquivoEnum.PDF);

        /**
         * Remover Arquivo do Glassfish. *
         */
        File arquivoPdf = new File(pathArquivo);

        if (arquivoPdf.exists()) {
            arquivoPdf.delete();
        }
    }

    public Boolean validarExistenciaRecursoOnlineEmBackOfficeParaPA(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws DatabaseException {

        RecursoPAOnline recursoEmBackOffice
                = new RecursoPAOnlineRepositorio()
                        .getRecursoOnlinePorPaESituacao(em, processoAdministrativo.getId(), RecursoSituacaoPAEnum.BACKOFFICE);

        return recursoEmBackOffice != null;
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @throws AppException
     */
    public void validarRecursoOnlineEmBackOffice(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        Boolean existeRecursoOnlineEmBackOffice
                = new RecursoOnlineBO()
                        .validarExistenciaRecursoOnlineEmBackOfficeParaPA(em, processoAdministrativo);
        if (existeRecursoOnlineEmBackOffice) {
            DetranWebUtils.applicationMessageException("Existe solicitação de recurso na situação Backoffice.");
        }
    }

    public void cancelarRecursoOnlineEmBackOffice(EntityManager em, ProcessoAdministrativo processoAdministrativo, RecursoOnlineCanceladoWrapper recursoOnlineCanceladoWrapper) throws AppException {

        RecursoPAOnlineRepositorio repositorio = new RecursoPAOnlineRepositorio();
        RecursoPAOnline recursoBackOffice
                = repositorio.getRecursoOnlinePorPaESituacao(em, processoAdministrativo.getId(), RecursoSituacaoPAEnum.BACKOFFICE);

        if (recursoBackOffice != null) {

            RecursoPAOnline recursoCancelado = desativarRecursoBackOffice(em, recursoBackOffice);

            recursoCancelado.setSituacao(RecursoSituacaoPAEnum.CANCELADO);
            recursoCancelado.setMotivoCancelamento("O sistema cancelou o backoffice por " + recursoOnlineCanceladoWrapper.getFuncionalidade());
            recursoCancelado.setIp(recursoOnlineCanceladoWrapper.getIp());
            recursoCancelado.setCpfUsuario(recursoOnlineCanceladoWrapper.getCpfUsuario());

            repositorio.insert(em, recursoCancelado);

            copiarDocumentos(em, recursoCancelado, recursoBackOffice);

            envioEmailRecursoCancelado(em, recursoCancelado);
        }
    }

    public void suspenderRecursoOnlineEmBackOffice(EntityManager em, ProcessoAdministrativo processoAdministrativo, RecursoOnlineCanceladoWrapper recursoOnlineCanceladoWrapper) throws AppException {

        RecursoPAOnlineRepositorio repositorio = new RecursoPAOnlineRepositorio();
        RecursoPAOnline recursoBackOffice
                = repositorio.getRecursoOnlinePorPaESituacao(em, processoAdministrativo.getId(), RecursoSituacaoPAEnum.BACKOFFICE);

        if (recursoBackOffice != null) {

            RecursoPAOnline recursoCancelado = desativarRecursoBackOffice(em, recursoBackOffice);

            recursoCancelado.setSituacao(RecursoSituacaoPAEnum.SUSPENSO);
            recursoCancelado.setIp(recursoOnlineCanceladoWrapper.getIp());
            recursoCancelado.setCpfUsuario(recursoOnlineCanceladoWrapper.getCpfUsuario());

            repositorio.insert(em, recursoCancelado);

            copiarDocumentos(em, recursoCancelado, recursoBackOffice);
        }
    }

    private RecursoPAOnline desativarRecursoBackOffice(EntityManager em, RecursoPAOnline recursoOnlineAnterior) throws AppException {
        RecursoPAOnlineRepositorio repositorio = new RecursoPAOnlineRepositorio();
        RecursoPAOnline novoRecurso
                = criarNovaInstanciaRecursoOnline(em,
                        new RecursoOnlinePaWrapper(recursoOnlineAnterior.getToken(), recursoOnlineAnterior.getProtocolo()),
                        PassoRecursoOnlinePAEnum.PASSO_FINAL);

        recursoOnlineAnterior.setAtivo(AtivoEnum.DESATIVADO);
        repositorio.update(em, recursoOnlineAnterior);

        new RecursoPAOnlineArquivoBO().desativarArquivosRecursoOnline(em, recursoOnlineAnterior);

        return novoRecurso;
    }

    private void envioEmailRecursoCancelado(EntityManager em, RecursoPAOnline recursoCancelado) throws AppException {
        String tituloEmail = "Detran MS - Solicitação de Recurso de " + recursoCancelado.getTipo().toString() + " - Cancelada";

        enviarEmail(em, recursoCancelado, tituloEmail, TipoCorpoTextoPAConstante.EMAIL_SOLICITACAO_ABERTURA_RECURSO_ONLINE_PA_CANCELADA);
    }

    private void enviarEmail(EntityManager em, RecursoPAOnline recursoCancelado, String tituloEmail, Long tipoCorpoTexto) throws AppException {
        StringBuilder body = montarCorpoEmailRecursoOnline(em,
                recursoCancelado,
                tipoCorpoTexto);

        String[] emails = {recursoCancelado.getEmail()};

        envioEmail(tituloEmail, body.toString(), emails);
    }

    public void retirarSuspensaoRecursoOnlineEmBackOffice(EntityManager em, ProcessoAdministrativo pa, RecursoOnlineCanceladoWrapper recursoOnlineWrapper) throws AppException {

        RecursoPAOnlineRepositorio repositorio = new RecursoPAOnlineRepositorio();
        RecursoPAOnline recursoBackOfficeSuspenso
                = repositorio.getRecursoOnlinePorPaESituacao(em, pa.getId(), RecursoSituacaoPAEnum.SUSPENSO);

        if (recursoBackOfficeSuspenso != null) {

            RecursoPAOnline recursoAtivo = desativarRecursoBackOffice(em, recursoBackOfficeSuspenso);

            recursoAtivo.setSituacao(RecursoSituacaoPAEnum.BACKOFFICE);
            recursoAtivo.setIp(recursoOnlineWrapper.getIp());
            recursoAtivo.setCpfUsuario(recursoOnlineWrapper.getCpfUsuario());

            repositorio.insert(em, recursoAtivo);

            copiarDocumentos(em, recursoAtivo, recursoBackOfficeSuspenso);
        }
    }

    private String getPathArquivo(String nomeArquivo) {
        Path currentRelativePath = Paths.get("");
        String url = currentRelativePath.toAbsolutePath().toString();
        return url + "/" + nomeArquivo + ".pdf";
    }
}
