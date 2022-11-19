package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranEmailWrapper;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.DtnFTPClient;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.integracao.comum.entidade.layout.ConfiguracaoFtp;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.SituacaoCorrespondenciaConstante;
import br.gov.ms.detran.processo.administrativo.core.repositorio.CorrespondenciaCorreioDevolucaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.CorrespondenciaCorreioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.CorrespondenciaDevolvidaRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.CorrespondenciaRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoARDetalheWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RetornosARWrapper;
import br.gov.ms.detran.protocolo.entidade.Correspondencia;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaCorreio;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaDevolvida;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

public class RetornoARBO {

    private static final Logger LOG = Logger.getLogger(RetornoARBO.class);

    private static final String ENTREGUE = "00";

    private static final String ARQUIVO_CABECALHO = "1";
    private static final String ARQUIVO_DETALHE = "2";

    private static final String PREFIXO_ARQUIVO = "AR_PA";

    private static final String NOME_FTP = "RECEBE_ARQUIVO_PA_CORREIOS";

    IApoioService apoioService;
    IProcessoAdministrativoService processoAdministrativoService;
    IPAControleFalhaService paControleFalha;

    IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    IProcessoAdministrativoService getProcessoAdministrativoService() {
        if (processoAdministrativoService == null) {
            processoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }
        return processoAdministrativoService;
    }

    IPAControleFalhaService getControleFalha() {
        if (paControleFalha == null) {
            paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
        }
        return paControleFalha;
    }

    /**
     *
     * @throws AppException
     */
    public void lerDadosArquivoCorreiosAtualizarCorrespondenciaEAndamento() throws AppException {

        LOG.info("Inicio leitura de dados Retornor AR.");

        List<String> numerosPAsAguardandoRetornoAR = new ArrayList<>();
        try {

            ConfiguracaoFtp configuracaoServer
                    = (ConfiguracaoFtp) getApoioService().getConfiguracaoFtpPorNome(NOME_FTP);

            DtnFTPClient cliente = new DtnFTPClient(configuracaoServer.getConfiguracaoFTP());

            List<String> nomesArquivos
                    = cliente.listaNomesArquivoPorPrefixo(configuracaoServer.getCaminhoDiretorio(), PREFIXO_ARQUIVO);

            if (!DetranCollectionUtil.ehNuloOuVazio(nomesArquivos)) {

                for (String nomeArquivo : nomesArquivos) {

                    Date dataRecebimento = null;
                    File arquivoRetornoAr = new File(nomeArquivo);
                    cliente.download(configuracaoServer.getCaminhoDiretorio().concat(nomeArquivo), arquivoRetornoAr);

                    BufferedReader br = new BufferedReader(new FileReader(arquivoRetornoAr));

                    while (br.ready()) {

                        String linha = br.readLine();

                        String tipoRegistro = linha.substring(0, 1);

                        if (ARQUIVO_DETALHE.equals(tipoRegistro)) {

                            RetornoARDetalheWrapper detalheWrapper = null;

                            try {

                                detalheWrapper = montarDetalheWrapper(linha);

                                getProcessoAdministrativoService()
                                        .atualizarCorrespondencia(detalheWrapper, dataRecebimento);
                                
                                numerosPAsAguardandoRetornoAR.add(detalheWrapper.getNumeroProcesso());

                            } catch (Exception ex) {

                                LOG.debug("Erro ao ler arquivo e atualizar correspondencia.", ex);

                                getControleFalha()
                                        .gravarFalhaProcessoAdministrativo(
                                                ex,
                                                "Erro ao ler arquivo e atualizar correspondencia",
                                                null,
                                                detalheWrapper != null ? detalheWrapper.getNumeroProcesso() : null
                                        );
                            }

                        } else if (ARQUIVO_CABECALHO.equals(tipoRegistro)) {
                            dataRecebimento = Utils.convertDate(linha.substring(7, 15), "ddMMyyyy");
                        }
                    }

                    br.close();
                    cliente.deleteFile(configuracaoServer.getCaminhoDiretorio().concat(nomeArquivo));
//                    mudarArquivoPasta(nomeArquivo);
                }
            }
            
        } catch (AppException | IOException | NumberFormatException ex) {

            LOG.debug("Erro ao abrir arquivo de Retorno AR", ex);

            DetranEmailWrapper email = new DetranEmailWrapper();
            email.setSubject("[DETRAN-WEB Processo Administrativo] Erro Retorno AR.");
            email.setBody("<b> Não foi possível recuperar arquivo retorno AR." + " Favor entrar em contato com a DIRTI. </b>");

            getProcessoAdministrativoService()
                    .gravarFalhaEMandarEmail(
                            new AppException("Não foi possível recuperar arquivo retorno AR."),
                            null,
                            "RETORNO_AR",
                            email);
        }
        
        try {

            getProcessoAdministrativoService().atualizaProcessosAguardandoRetornoAR(numerosPAsAguardandoRetornoAR);

        } catch (AppException e) {
            LOG.debug("Erro ao atualizar processos aguardando retorno do AR.", e);

            getControleFalha()
                    .gravarFalhaProcessoAdministrativo(
                            e,
                            "Erro ao atualizar processos aguardando retorno do AR.",
                            null,
                            null
                    );
        }

        LOG.info("Fim leitura de dados Retorno AR.");

    }

    /**
     * @param em
     * @param detalheWrapper
     * @param dataRecebimento
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    public void atualizarCorrespondencia(EntityManager em, RetornoARDetalheWrapper detalheWrapper, Date dataRecebimento) throws AppException {

        LOG.info("Inicio atualizacao correspondência.");

        NotificacaoProcessoAdministrativo notificacao
                = new NotificacaoProcessoAdministrativoRepositorio()
                        .getNotificacaoPorNumeroNotificacaoENumeroProcesso(
                                em,
                                detalheWrapper.getNumeroNotificacao(),
                                detalheWrapper.getNumeroProcesso()
                        );

        if (notificacao == null) {
            DetranWebUtils.applicationMessageException("Notificação inexistente para o Processo Administrativo.");
        }

        CorrespondenciaCorreio correspondenciaCorreio
                = new CorrespondenciaCorreioRepositorio()
                        .getCorrespondenciaCorreioPorNumeroETipoNotificacao(
                                em,
                                notificacao.getNumeroNotificacao(),
                                notificacao.getTipoNotificacaoProcesso()
                        );

        correspondenciaCorreio.setDataChegadaDestino(detalheWrapper.getDataEntregaOuDevolucao());
        correspondenciaCorreio.setNomeRecebedor(detalheWrapper.getNomeRecebedor());
        correspondenciaCorreio.setDocumentoRecebedor(detalheWrapper.getNumeroDocumentoRecebedor());
        correspondenciaCorreio.setAtivo(AtivoEnum.ATIVO);

        correspondenciaCorreio.getCorrespondencia().setDataRecebimento(dataRecebimento);
        new CorrespondenciaRepositorio().update(em, correspondenciaCorreio.getCorrespondencia());

        new CorrespondenciaCorreioRepositorio().update(em, correspondenciaCorreio);

        if (ENTREGUE.equals(detalheWrapper.getMotivoDevolucao())) {

            correspondenciaCorreio.getCorrespondencia().setSituacao(SituacaoCorrespondenciaConstante.RECEBIDA_PELO_DESTINATARIO);
            new CorrespondenciaRepositorio().update(em, correspondenciaCorreio.getCorrespondencia());

        } else {

            correspondenciaCorreio.getCorrespondencia().setSituacao(SituacaoCorrespondenciaConstante.DEVOLVIDA_AO_REMETENTE);
            new CorrespondenciaRepositorio().update(em, correspondenciaCorreio.getCorrespondencia());

            CorrespondenciaDevolvida correspondenciaDevolvida = new CorrespondenciaDevolvida();
            correspondenciaDevolvida.setCorrespondencia(correspondenciaCorreio.getCorrespondencia());
            correspondenciaDevolvida.setDataDevolucao(detalheWrapper.getDataEntregaOuDevolucao());

            correspondenciaDevolvida
                    .setMotivoDevolucao(
                            new CorrespondenciaCorreioDevolucaoRepositorio()
                                    .getCorrespondenciaCorreioDevolucaoPorMotivoAtivo(
                                            em,
                                            Integer.valueOf(detalheWrapper.getMotivoDevolucao())
                                    )
                    );

            correspondenciaDevolvida.setObservacao(null);
            correspondenciaDevolvida.setAtivo(AtivoEnum.ATIVO);

            new CorrespondenciaDevolvidaRepositorio().insert(em, correspondenciaDevolvida);
        }

        LOG.info("Fim atualizacao correspondencia.");
    }

    /**
     * @param em
     * @return
     * @throws AppException
     */
    public List getRetornoAR(EntityManager em) throws AppException {

        List<RetornosARWrapper> listaJson = new ArrayList();

        List<Integer> andamentosNotificados
                = DetranCollectionUtil.montaLista(PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.CONFIRMAR_RETORNO_AR_NOTIFICACAO_INSTAURACAO,
                                                  PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.CONFIRMAR_RETORNO_AR_NOTIFICACAO_PENALIZACAO,
                                                  PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.CONFIRMAR_RETORNO_AR_NOTIFICACAO_ENTREGA_CNH,
                                                  PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.CONFIRMAR_RETORNO_AR_NOTIFICACAO_ACOLHIMENTO,
                                                  PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.CONFIRMAR_RETORNO_AR_NOTIFICACAO_DESENTRANHAMENTO,
                                                  PAAndamentoProcessoConstante.NOTIFICACAO_JARI.CONFIRMAR_RETORNO_AR_NOTIFICACAO_JARI,
                                                  PAAndamentoProcessoConstante.NOTIFICACAO_CURSO_EXAME.CONFIRMAR_RETORNO_AR_NOTIFICACAO_CURSO_EXAME
                );

        List<NotificacaoProcessoAdministrativo> notificados
                = new NotificacaoProcessoAdministrativoRepositorio()
                        .getNotificadosPorAndamentoESituacaoParaRetornoAR(em, andamentosNotificados);

        if (!DetranCollectionUtil.ehNuloOuVazio(notificados)) {

            for (NotificacaoProcessoAdministrativo notificado : notificados) {

                RetornosARWrapper retornoAR = montarRetornoARWrapper(em, notificado);

                PAOcorrenciaStatus paOcorrenciaStatusAtiva
                        = new PAOcorrenciaStatusRepositorio().
                                getPAOcorrenciaStatusAtivaApenasCodigoAndamento(em, notificado.getProcessoAdministrativo().getId());

                CorrespondenciaCorreio correspondenciaCorreio
                        = new CorrespondenciaCorreioRepositorio().
                                getCorrespondenciaCorreioParaRetornoAr(em, notificado.getCorrespondencia().getId());

                retornoAR.setAndamentoAtual(paOcorrenciaStatusAtiva.getStatusAndamento().getAndamentoProcesso().getCodigo());
                retornoAR.setCodigoRetornoAR(notificado.getObjetoCorreio());
                retornoAR.setTipo(notificado.getTipoNotificacaoProcesso());
                retornoAR.setNumeroProcesso(notificado.getProcessoAdministrativo().getNumeroProcesso());

                retornoAR.setResultadoEntrega(0);

                if (SituacaoCorrespondenciaConstante.RECEBIDA_PELO_DESTINATARIO.equals(notificado.getCorrespondencia().getSituacao())) {
                    retornoAR.setResultadoEntrega(1);
                }

                retornoAR.setDataEntrega(correspondenciaCorreio.getDataChegadaDestino());
                retornoAR.setDocumentoRecebedor(correspondenciaCorreio.getDocumentoRecebedor());
                retornoAR.setNomeRecebedor(correspondenciaCorreio.getNomeRecebedor());

                listaJson.add(retornoAR);
            }
        }

        return listaJson;
    }

    public RetornosARWrapper montarRetornoARWrapper(EntityManager em, NotificacaoProcessoAdministrativo notificado) throws DatabaseException, AppException {
        RetornosARWrapper retornoAR = new RetornosARWrapper();

        PAOcorrenciaStatus paOcorrenciaStatusAtiva
                = new PAOcorrenciaStatusRepositorio().
                        getPAOcorrenciaStatusAtiva(em, notificado.getProcessoAdministrativo().getId());

        CorrespondenciaCorreio correspondenciaCorreio
                = new CorrespondenciaCorreioRepositorio().
                        getCorrespondenciaCorreioPorCorrespondenciaIdTodaEntidade(em, notificado.getCorrespondencia().getId());

        retornoAR.setAndamentoAtual(paOcorrenciaStatusAtiva.getStatusAndamento().getAndamentoProcesso().getCodigo());
        retornoAR.setCodigoRetornoAR(notificado.getObjetoCorreio());
        retornoAR.setTipo(notificado.getTipoNotificacaoProcesso());
        retornoAR.setNumeroProcesso(notificado.getProcessoAdministrativo().getNumeroProcesso());
        retornoAR.setResultadoEntrega(0);
        retornoAR.setDataEntrega(correspondenciaCorreio.getDataChegadaDestino());
        retornoAR.setDocumentoRecebedor(correspondenciaCorreio.getDocumentoRecebedor());
        retornoAR.setNomeRecebedor(correspondenciaCorreio.getNomeRecebedor());

        if (SituacaoCorrespondenciaConstante.RECEBIDA_PELO_DESTINATARIO.equals(notificado.getCorrespondencia().getSituacao())) {
            retornoAR.setResultadoEntrega(1);
        }

        return retornoAR;
    }

    /**
     * Montar wrapper conforme dados do detalhe do arquivo.
     *
     * @param linha
     * @return
     */
    private RetornoARDetalheWrapper montarDetalheWrapper(String linha) {

        RetornoARDetalheWrapper detalheWrapper = new RetornoARDetalheWrapper();

        detalheWrapper.setIdentificadorInicial(!DetranStringUtil.ehBrancoOuNulo(linha.substring(1, 3)) ? linha.substring(1, 3).trim() : "");
        detalheWrapper.setNumeroObjeto(!DetranStringUtil.ehBrancoOuNulo(linha.substring(3, 12)) ? linha.substring(3, 12).trim() : "");
        detalheWrapper.setIdentificadorFinal(!DetranStringUtil.ehBrancoOuNulo(linha.substring(12, 14)) ? linha.substring(12, 14).trim() : "");
        detalheWrapper.setNumeroProcesso(!DetranStringUtil.ehBrancoOuNulo(linha.substring(14, 24)) ? linha.substring(14, 24).trim() : "");
        detalheWrapper.setNumeroNotificacao(!DetranStringUtil.ehBrancoOuNulo(linha.substring(24, 34)) ? linha.substring(24, 34).trim() : null);
        detalheWrapper.setDataEntregaOuDevolucao(!DetranStringUtil.ehBrancoOuNulo(linha.substring(34, 42)) ? Utils.getDate(linha.substring(34, 42), "ddMMyyyy") : null);
        detalheWrapper.setNomeRecebedor(!DetranStringUtil.ehBrancoOuNulo(linha.substring(42, 82)) ? linha.substring(42, 82).trim() : "");
        detalheWrapper.setNumeroDocumentoRecebedor(!DetranStringUtil.ehBrancoOuNulo(linha.substring(82, 97)) ? linha.substring(82, 97).trim() : "");
        detalheWrapper.setDataBaixa(!DetranStringUtil.ehBrancoOuNulo(linha.substring(97, 105)) ? linha.substring(97, 105).trim() : "");
        detalheWrapper.setMotivoDevolucao(!DetranStringUtil.ehBrancoOuNulo(linha.substring(105, 107)) ? linha.substring(105, 107).trim() : "");
        detalheWrapper.setControleCorreios(!DetranStringUtil.ehBrancoOuNulo(linha.substring(107, 115)) ? linha.substring(107, 115).trim() : "");

        return detalheWrapper;
    }

    /**
     * @param nomeArquivo
     * @throws AppException
     */
    private void mudarArquivoPasta(String nomeArquivo) throws AppException {

        ConfiguracaoFtp configuracaoServer = (ConfiguracaoFtp) getApoioService().getConfiguracaoFtpPorNome("ASSI_FTP");

        if (configuracaoServer == null) {
            DetranWebUtils.applicationMessageException("Não foi encontrada uma configuração de envio via FTP para o servidor ASSI_FTP.");
        }

        DtnFTPClient clienteServidorLocal = new DtnFTPClient(configuracaoServer.getConfiguracaoFTP());

        String novoNomeArquivo = criarNomeArquivo(nomeArquivo, configuracaoServer, clienteServidorLocal);

        boolean doneServer = clienteServidorLocal.upload(nomeArquivo, configuracaoServer.getCaminhoDiretorio().concat(novoNomeArquivo), true);

        if (doneServer) {

            //Remover Arquivo do Glassfish
            File arquivoPdf = new File(nomeArquivo);

            if (arquivoPdf.exists()) {
                arquivoPdf.delete();
            }
        }

        if (!doneServer) {
            LOG.warn("Não foi possível enviar o arquivo {0} para o servidor local", nomeArquivo);
        }
    }

    /**
     * @param nomeArquivo
     * @return
     */
    private String criarNomeArquivo(String nomeArquivo, ConfiguracaoFtp configuracaoServer, DtnFTPClient clienteServidorLocal) throws AppException {

        Integer numeroNomeArquivo = 1;

        String novoNomeArquivo = nomeArquivo;

        while (clienteServidorLocal.existeArquivoMesmoNome(configuracaoServer.getCaminhoDiretorio(), novoNomeArquivo)) {

            String nome = nomeArquivo.substring(0, nomeArquivo.indexOf("."));
            String extensao = nomeArquivo.substring(nomeArquivo.indexOf("."), nomeArquivo.length());
            novoNomeArquivo = nome + "_" + numeroNomeArquivo.toString() + extensao;

            numeroNomeArquivo++;
        }

        return novoNomeArquivo;
    }

    /**
     *
     * @param em
     * @param idProcessoAdministrativo
     * @param tipo
     * @throws AppException
     */
    public void validarExistenciaRetornoARPorProcessoAdministrativoETipo(EntityManager em, Long idProcessoAdministrativo, TipoFasePaEnum tipo) throws AppException {

        List<Integer> situacoesValidas = DetranCollectionUtil.montaLista(SituacaoCorrespondenciaConstante.RECEBIDA_PELO_DESTINATARIO,
                                                                         SituacaoCorrespondenciaConstante.DEVOLVIDA_AO_REMETENTE);

        Correspondencia correspondencia = new CorrespondenciaRepositorio().getCorrespondenciaPorProcessoAdministrativoETipo(em, idProcessoAdministrativo, tipo);

        if (correspondencia == null
            || correspondencia.getSituacao() == null
            || !situacoesValidas.contains(correspondencia.getSituacao())) {

            DetranWebUtils.applicationMessageException("Aguardando retorno do AR.");
        }
    }

    public void atualizaProcessosAguardandoRetornoAR(EntityManager em, List<String> listaNumeroPA) throws AppException {
        

        List andamentos
                = DetranCollectionUtil
                        .montaLista(PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.AGUARDAR_AR_NOTIFICACAO_INSTAURACAO,
                                    PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.AGUARDAR_AR_NOTIFICACAO_PENALIZACAO,
                                    PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_AR_NOTIFICACAO_ENTREGA_CNH,
                                    PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.AGUARDAR_AR_NOTIFICACAO_ACOLHIMENTO,
                                    PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.AGUARDAR_AR_NOTIFICACAO_DESENTRANHAMENTO,
                                    PAAndamentoProcessoConstante.NOTIFICACAO_CURSO_EXAME.AGUARDAR_AR_NOTIFICACAO_CURSO_EXAME,
                                    PAAndamentoProcessoConstante.NOTIFICACAO_JARI.AGUARDAR_AR_NOTIFICACAO_JARI
                        );
        
        processarListaPAsAguardandoRetornoAR(listaNumeroPA, em, andamentos);
        
//        processarTodosPAsAguardandoRetornoAR(em, andamentos);

    }

    private void processarListaPAsAguardandoRetornoAR(List<String> listaNumeroPA, EntityManager em, List andamentos) {
        listaNumeroPA.forEach((numeroPA) -> {
            try {
                ProcessoAdministrativo pa = new ProcessoAdministrativoRepositorio().
                        getProcessoAdministrativoPorNumeroEAndamentoEIniciado(em, numeroPA, andamentos);
                if(pa != null){
                    new ExecucaoAndamentoManager().
                            iniciaExecucao(
                                    new ExecucaoAndamentoEspecificoWrapper(pa,
                                            null,
                                            null,
                                            null
                                    )
                            );
                }
                
            } catch (Exception e) {
                
                LOG.info("Erro ao executar andamento de processo aguardando retorno do AR.", e);
            }
        });
    }

    private void processarTodosPAsAguardandoRetornoAR(EntityManager em, List andamentos) throws AppException {
        List<ProcessoAdministrativo> processosAguardandoRetornoAR = new ProcessoAdministrativoRepositorio().getListaProcessoAdministrativoPorAndamento(em, andamentos);
        processosAguardandoRetornoAR.forEach((processo) -> {
            try {
                
                new ExecucaoAndamentoManager().
                        iniciaExecucao(
                                new ExecucaoAndamentoEspecificoWrapper(processo,
                                        null,
                                        null,
                                        null
                                )
                        );
                
            } catch (Exception e) {
                
                LOG.info("Erro ao executar andamento de processo aguardando retorno do AR.", e);
            }
        });
    }
}
