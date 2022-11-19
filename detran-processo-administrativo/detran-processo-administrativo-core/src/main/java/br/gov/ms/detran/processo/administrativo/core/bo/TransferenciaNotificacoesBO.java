package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.Municipio;
import br.gov.ms.detran.comum.entidade.enums.apo.TipoExtensaoArquivoEnum;
import br.gov.ms.detran.comum.projeto.constantes.ParametrizacaoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.*;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.integracao.comum.entidade.layout.ConfiguracaoFtp;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.repositorio.CorrespondenciaCorreioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAParametrizacaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.ArquivoPA;
import br.gov.ms.detran.processo.administrativo.entidade.LoteNotificacaoPA;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.protocolo.entidade.Correspondencia;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaCorreio;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import javax.persistence.EntityManager;
import java.io.*;
import java.util.*;

/**
 *
 * @author Lillydi
 */
public class TransferenciaNotificacoesBO {

    private static final Logger LOG = Logger.getLogger(TransferenciaNotificacoesBO.class);
    
    private static final Integer NOTIFICACOES_BLOCO = 500;

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
     * @param em
     * @throws AppException
     */
    public void transferirNotificacoesSGI(EntityManager em,  String url) throws AppException {

        List<NotificacaoProcessoAdministrativo> notificacoes = getProcessoAdministrativoService().getListNotificacoesParaTransferenciaSGI();

        if (!DetranCollectionUtil.ehNuloOuVazio(notificacoes)) {

            Map<TipoFasePaEnum, Collection<NotificacaoProcessoAdministrativo>> mapNotificacaoPorTipo = transformListToMap(notificacoes);

            for (Map.Entry<TipoFasePaEnum, Collection<NotificacaoProcessoAdministrativo>> entry : mapNotificacaoPorTipo.entrySet()) {

                TipoFasePaEnum tipoNotificacao = entry.getKey();
                Collection<NotificacaoProcessoAdministrativo> cNotificacao = entry.getValue();
                
                List<List<NotificacaoProcessoAdministrativo>> blocos = DetranCollectionUtil.particionarLista(new ArrayList<>(cNotificacao), NOTIFICACOES_BLOCO);
                
                for (List<NotificacaoProcessoAdministrativo> lNotificacao : blocos) {
                    
                    String nomeArquivo = "NOT" + tipoNotificacao.getTipoFasePaExterna() + "PA_" + Utils.formatDate(new Date(), "yyyyMMdd_HHmmss");

                    try {

                        String nomeArquivoConferencia = "CONFERENCIA_NOT" + tipoNotificacao.getTipoFasePaExterna() + "PA_" + Utils.formatDate(new Date(), "yyyyMMdd_HHmmss");

                        gerarLoteEArquivoConferencia(url, nomeArquivoConferencia, tipoNotificacao, lNotificacao);

                         List<byte[]> pdfs =gerarPdfNotificacoes(nomeArquivo, lNotificacao, tipoNotificacao);

                        gravarArquivoPasta(nomeArquivo, "ENVIA_PDF_PA_SGI");
                        gravarArquivoPasta(nomeArquivoConferencia, "ENVIA_PDF_PA_SGI");

                        /** Copia. **/
                        gravarArquivoPasta(nomeArquivo, "ENVIA_PDF_PA_SGI_BKP");
                        gravarArquivoPasta(nomeArquivoConferencia, "ENVIA_PDF_PA_SGI_BKP");

                        /** DISPONIBILIZAR PARA SISTEMA EXTERNO (BPMS). **/
                        gravarArquivoPasta(nomeArquivo, "ENVIA_PDF_PA_SGI_BPMS");
                        gravarArquivoPasta(nomeArquivoConferencia, "ENVIA_PDF_PA_SGI_BPMS");

                        /** Remover Arquivo do Glassfish. **/
                        File arquivoPdf = new File(nomeArquivo);

                        if (arquivoPdf.exists()) {
                            arquivoPdf.delete();
                        }

                        getProcessoAdministrativoService().enviarEmail(nomeArquivo, tipoNotificacao);

                        for (NotificacaoProcessoAdministrativo notificacao : lNotificacao) {

                            try {

                                if (TipoFasePaEnum.NAO_CONHECIMENTO.equals(tipoNotificacao)) {

                                    getProcessoAdministrativoService()
                                            .executarMudancaFluxoEAndamento(
                                                    notificacao.getProcessoAdministrativo(), 
                                                    PAFluxoProcessoConstante.FLUXO_NORMAL,
                                                    PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_BLOQUEIO);

                                } else {

                                    getProcessoAdministrativoService().proximoAndamento(notificacao.getProcessoAdministrativo());

                                }

                            } catch (Exception e) {
                                LOG.debug("Sem tratamento.", e);
                                getControleFalha().gravarFalha(e, "Erro ao transferir notificações SGI");
                            }
                        }

                    } catch (Exception ex) {

                        LOG.debug("Sem tratamento.", ex);

                        DetranEmailWrapper email = new DetranEmailWrapper();
                        email.setSubject("[DETRAN-WEB Processo Administrativo] Erro Transferência SGI.");
                        email.setBody("<b> Não foi possível enviar o arquivo "+ nomeArquivo +" para o servidor SGI ou servidor local." + " Favor entrar em contato com a DIRTI. </b>");

                        getProcessoAdministrativoService()
                            .gravarFalhaEMandarEmail(
                                new AppException("Não foi possível enviar o arquivo para o servidor SGI ou servidor local"),
                                null,
                                "TRANSFERENCIA_SGI",
                                email
                            );
                    }
                }
            }
        }
    }

    /**
     * 
     * @param nomeArquivo
     * @param lNotificacao
     * @param tipoNotificacao
     * @return
     * @throws AppException
     * @throws IOException 
     */
    public List<byte[]> gerarPdfNotificacoes( String nomeArquivo, List<NotificacaoProcessoAdministrativo> lNotificacao, TipoFasePaEnum tipoNotificacao) throws AppException, IOException{
        
        List<byte[]> pdfs = new ArrayList();
        
        /** Capa. **/
        pdfs.add(desenhaCapaNotificacoes(nomeArquivo));
        
        /** Por tipo. **/
        Integer contador = 0;
        
        for (NotificacaoProcessoAdministrativo notificacao : lNotificacao) {
            
            try {
                
                contador++;
                
                pdfs.add(adicionarContadorPagina(notificacao.getArquivo().getByteArquivo(), contador));
                
            } catch (Exception e) {
                LOG.debug("Sem tratamento.", e);
                getControleFalha().gravarFalha(e, "Erro ao transferir notificações SGI");
            }
        }
        
        /** Lista pdfs contem a capa - logo terá pelo menos um byte de pdf na lista de pdfs. **/
        if(pdfs.size() == 1 || (pdfs.size() - 1) != lNotificacao.size()) {
            
            throw new AppException(
                    "Transferência SGI para Tipo Notificação: {0} inválida.",
                    new String [] {tipoNotificacao.name()}
            );
        }
        
        DetranArquivoHelper.agruparArquivosPdf(nomeArquivo, pdfs);
        
        return pdfs;
    }

    /**
     * @param nomeArquivo
     *
     * @throws AppException
     */
    public void gravarArquivoPasta(String nomeArquivo, String nomeFtp) throws AppException {

        ConfiguracaoFtp configuracaoServer = (ConfiguracaoFtp) getApoioService().getConfiguracaoFtpPorNome(nomeFtp);

        if (configuracaoServer == null) {
            DetranWebUtils.applicationMessageException("Não foi encontrada uma configuração de envio via FTP para o servidor " + nomeFtp);
        }

        DtnFTPClient clienteServidorLocal = new DtnFTPClient(configuracaoServer.getConfiguracaoFTP());

        boolean doneServer 
            = clienteServidorLocal
                .upload(
                    nomeArquivo, 
                    configuracaoServer.getCaminhoDiretorio().concat(nomeArquivo).concat(TipoExtensaoArquivoEnum.PDF.toString()
                ), 
                true
            );

        if (!doneServer) {
            LOG.warn("Não foi possível enviar o arquivo {0} para o servidor local", nomeArquivo);
        }
    }

    /**
     * @param nomeArquivo
     * @return
     * @throws IOException
     */
    public byte[] desenhaCapaNotificacoes(String nomeArquivo) throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(stream));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        com.itextpdf.kernel.geom.Rectangle[] rect =
                new com.itextpdf.kernel.geom.Rectangle[]{
                        new Rectangle(
                                270,
                                250,
                                340,
                                110)};

        // For canvas usage one should create a page
        pdfDoc.addNewPage();
        new PdfCanvas(pdfDoc.getFirstPage())
                .setLineWidth(2.0f)
                .setStrokeColor(ColorConstants.BLACK)
                .rectangle(rect[0])
                .stroke();
        doc.setRenderer(new ColumnDocumentRenderer(doc, rect));
        Paragraph p = new Paragraph(nomeArquivo);
        p.setFontSize(24);
        p.setTextAlignment(TextAlignment.CENTER);
        p.setFont(font);
        doc.add(p);
        pdfDoc.addNewPage();
        doc.close();

        return stream.toByteArray();
    }

    /**
     * @deprecated 
     * @param em
     * @throws AppException
     */
    public void gerarArquivoCorreio(EntityManager em) throws AppException {

        List andamentos
            = DetranCollectionUtil
                .montaLista(PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_INSTAURACAO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_PENALIZACAO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_ENTREGA_CNH,
                    PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_ACOLHIMENTO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_NAO_CONHECIMENTO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_NAO_CONHECIMENTO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_DESENTRANHAMENTO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_JARI.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_JARI,
                    PAAndamentoProcessoConstante.NOTIFICACAO_CURSO_EXAME.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_CURSO_EXAME
                );

        NotificacaoProcessoAdministrativoRepositorio npaRepo = new NotificacaoProcessoAdministrativoRepositorio();
        ProcessoAdministrativoRepositorio paRepo = new ProcessoAdministrativoRepositorio();

        List<NotificacaoProcessoAdministrativo> notificacoes
            = npaRepo.getNotificadosPorAndamentoESituacao(em, andamentos);

        if (!DetranCollectionUtil.ehNuloOuVazio(notificacoes)) {

            for (NotificacaoProcessoAdministrativo notificacao : notificacoes) {

                try {

                     new ExecucaoAndamentoManager().
                        iniciaExecucao(
                                new ExecucaoAndamentoEspecificoWrapper(paRepo.
                                                                        getProcessoAdministrativoPorNumeroProcessoSemAtivo(em, 
                                                                                                                           notificacao.getProcessoAdministrativo().getNumeroProcesso()) ,
                                                                       null,
                                                                       null,
                                                                       null
                            )
                    );

                } catch (Exception e) {
                    
                    LOG.debug("Erro ao montar a notificação para o correios do condutor.", e);
                    
                    getControleFalha().gravarFalha(e, "Erro ao montar a notificação para o correios do condutor");
                }
            }
        }
    }
    
    /**
     *
     * @param em
     * @throws AppException
     */
    public void gerarArquivoCorreiosLab(EntityManager em) throws AppException {

        List andamentos
            = DetranCollectionUtil
                .montaLista(PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_INSTAURACAO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_PENALIZACAO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_ENTREGA_CNH,
                    PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_ACOLHIMENTO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_NAO_CONHECIMENTO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_NAO_CONHECIMENTO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_DESENTRANHAMENTO
                );

        ExecucaoAndamentoManager execucaoAndamentoManager = new ExecucaoAndamentoManager();

        List<ProcessoAdministrativo> processos
            = new ProcessoAdministrativoRepositorio()
                .getListaProcessoAdministrativoPorAndamentoIniciado(em, andamentos);
                    
        if (!DetranCollectionUtil.ehNuloOuVazio(processos)) {

            for (ProcessoAdministrativo processoAdministrativo : processos) {

                try {
                    
                        execucaoAndamentoManager.iniciaExecucao(
                            new ExecucaoAndamentoEspecificoWrapper(processoAdministrativo)
                        );

//                    String linhaCorreios = geraLinhaCorreiosParaNotificacao(em, notificacao);
//
//                    notificacao.setLinhaCorreios(linhaCorreios);
//                    
//                    npaRepo.update(em, notificacao);
//
//                    getProcessoAdministrativoService().proximoAndamento(notificacao.getProcessoAdministrativo());

                } catch (Exception e) {
                    
                    LOG.debug("Erro ao montar a notificação para o correios do condutor.", e);
                    
                    getControleFalha()
                        .gravarFalhaProcessoAdministrativo(
                            e, 
                            "Erro ao montar a notificação para o correios do condutor",
                            processoAdministrativo.getCpf(),
                            processoAdministrativo.getNumeroProcesso()
                        );
                }
            }
        }
    }
//
//    /**
//     * 
//     * @param em
//     * @param notificacao
//     * @return 
//     * @throws AppException 
//     */
//    public String geraLinhaCorreiosParaNotificacao(EntityManager em, NotificacaoProcessoAdministrativo notificacao) throws AppException {
//        
//        StringBuilder notificacaoMontada = new StringBuilder();
//        
//        String endereco
//            = notificacao.getEndereco().getLogradouro()
//                .concat(" ")
//                .concat(
//                    notificacao.getEndereco().getNumero()
//                        .concat(" ")
//                        .concat(notificacao.getEndereco().getComplemento())
//                );
//        
//        notificacaoMontada
//            // DETRAN
//            .append(DetranStringUtil.preencherEspaco("13", 2, DetranStringUtil.TipoDadoEnum.NUMERICO))
//            //OBJETO-CORREIO
//            .append(DetranStringUtil.preencherEspaco(notificacao.getObjetoCorreio(), 13, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
//            .append(DetranStringUtil.preencherEspaco(notificacao.getProcessoAdministrativo().getNumeroProcesso(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO))
//            .append(DetranStringUtil.preencherEspaco(notificacao.getNumeroNotificacao().toString(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO))
//            .append(DetranStringUtil.preencherEspaco(notificacao.getCorrespondencia().getDestinatario(), 44, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
//            .append(DetranStringUtil.preencherEspaco(endereco, 35, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
//            .append(DetranStringUtil.preencherEspaco(notificacao.getEndereco().getBairro(), 22, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
//            .append(DetranStringUtil.preencherEspaco(notificacao.getEndereco().getCep().toString(), 8, DetranStringUtil.TipoDadoEnum.NUMERICO))
//        ;
//        
//        Municipio municipio 
//            = (Municipio) getApoioService().getMunicipioComNomeEstadoPorId(notificacao.getEndereco().getMunicipio());
//        
//        notificacaoMontada
//            .append(DetranStringUtil.preencherEspaco(municipio.getNome(), 30, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
//            .append(DetranStringUtil.preencherEspaco(municipio.getEstado().getSigla(), 2, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
//            .append(DetranStringUtil.preencherEspaco(getDataPostagem(em, notificacao.getCorrespondencia()), 8, DetranStringUtil.TipoDadoEnum.NUMERICO))
//        ;
//        
//        return notificacaoMontada.toString();
//    }
    
    /**
     * 
     * @param em
     * @param notificacao
     * @throws AppException 
     */
    public void geraLinhaCorreiosParaNotificacaoLab(
        EntityManager em, NotificacaoProcessoAdministrativo notificacao) throws AppException {
    
        StringBuilder notificacaoMontada = new StringBuilder();
        
        String endereco
            = notificacao.getEndereco().getLogradouro()
                .concat(" ")
                .concat(
                    DetranStringUtil.ehBrancoOuNulo(notificacao.getEndereco().getNumero())? "": notificacao.getEndereco().getNumero())
                        .concat(" ")
                        .concat(
                                DetranStringUtil.ehBrancoOuNulo(notificacao.getEndereco().getComplemento())? "":notificacao.getEndereco().getComplemento()
                );
        
        notificacaoMontada
            // DETRAN
            .append(DetranStringUtil.preencherEspaco("13", 2, DetranStringUtil.TipoDadoEnum.NUMERICO))
            //OBJETO-CORREIO
            .append(DetranStringUtil.preencherEspaco(notificacao.getObjetoCorreio(), 13, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
            .append(DetranStringUtil.preencherEspaco(notificacao.getProcessoAdministrativo().getNumeroProcesso(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO))
            .append(DetranStringUtil.preencherEspaco(notificacao.getNumeroNotificacao().toString(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO))
            .append(DetranStringUtil.preencherEspaco(notificacao.getCorrespondencia().getDestinatario(), 44, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
            .append(DetranStringUtil.preencherEspaco(endereco, 35, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
            .append(DetranStringUtil.preencherEspaco(notificacao.getEndereco().getBairro(), 22, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
            .append(DetranStringUtil.preencherEspaco(notificacao.getEndereco().getCep().toString(), 8, DetranStringUtil.TipoDadoEnum.NUMERICO))
        ;
        
        Municipio municipio 
            = (Municipio) getApoioService().getMunicipioComNomeEstadoPorId(notificacao.getEndereco().getMunicipio());
        
        notificacaoMontada
            .append(DetranStringUtil.preencherEspaco(municipio.getNome(), 30, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
            .append(DetranStringUtil.preencherEspaco(municipio.getEstado().getSigla(), 2, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
            .append(DetranStringUtil.preencherEspaco(getDataPostagem(em, notificacao.getCorrespondencia()), 8, DetranStringUtil.TipoDadoEnum.NUMERICO))
        ;
        
        notificacao.setLinhaCorreios(notificacaoMontada.toString());
        
        new NotificacaoProcessoAdministrativoRepositorio().update(em, notificacao);
    }

    /**
     *
     * @param em
     * @param correspondencia
     * @return
     * @throws AppException
     */
    private String getDataPostagem(EntityManager em, Correspondencia correspondencia) throws AppException {

        CorrespondenciaCorreio correspondenciaCorreio 
            = new CorrespondenciaCorreioRepositorio().getCorrespondenciaCorreioPorCorrespondenciaId(em, correspondencia.getId());
        
        if (correspondenciaCorreio == null || correspondenciaCorreio.getDataPostagem() == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar a data postagem.");
        }
        
        return Utils.formatDate(correspondenciaCorreio.getDataPostagem(), "yyyyMMdd");
    }

    /**
     * @deprecated 
     * @param em
     * @throws AppException
     */
    public void envioArquivoCorreio(EntityManager em) throws AppException {

        NotificacaoProcessoAdministrativoRepositorio npaRepo = new NotificacaoProcessoAdministrativoRepositorio();

        List andamentos
                = DetranCollectionUtil
                .montaLista(PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_INSTAURACAO,
                        PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_PENALIZACAO,
                        PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_ENTREGA_CNH,
                        PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_ACOLHIMENTO,
                        PAAndamentoProcessoConstante.NOTIFICACAO_NAO_CONHECIMENTO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_NAO_CONHECIMENTO,
                        PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_DESENTRANHAMENTO,
                        PAAndamentoProcessoConstante.NOTIFICACAO_JARI.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_JARI,
                        PAAndamentoProcessoConstante.NOTIFICACAO_CURSO_EXAME.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_CURSO_EXAME
                );

        List<NotificacaoProcessoAdministrativo> notificacoes = npaRepo.getNotificadosPorAndamentoESituacao(em, andamentos);

        if (!DetranCollectionUtil.ehNuloOuVazio(notificacoes)) {

            Map<TipoFasePaEnum, Collection<NotificacaoProcessoAdministrativo>> mapNotificacaoPorTipo = transformListToMap(notificacoes);

            for (Map.Entry<TipoFasePaEnum, Collection<NotificacaoProcessoAdministrativo>> entry : mapNotificacaoPorTipo.entrySet()) {

                try {

                    TipoFasePaEnum tipoNotificacao = entry.getKey();
                    Collection<NotificacaoProcessoAdministrativo> lNotificacao = entry.getValue();

                    String nomeArquivo = "DADOSAR_DETRAN_PA" + tipoNotificacao.getTipoFasePaExterna() + "_" + Utils.formatDate(new Date(), "yyyyMMddHHmmss");

                    DetranArquivoTexto txt = new DetranArquivoTexto(nomeArquivo);

                    txt.iniciarArquivoParaEscrita();

                    /**
                     * Por tipo. *
                     */
                    for (NotificacaoProcessoAdministrativo notificacao : lNotificacao) {

                        try {

                            txt.gravarNoArquivoParaWindows(notificacao.getLinhaCorreios());

                            getProcessoAdministrativoService().proximoAndamento(notificacao.getProcessoAdministrativo());

                        } catch (Exception e) {
                            LOG.warn("Não foi possível enviar o arquivo {0} para o correioss", nomeArquivo);
                            DetranEmailWrapper email = new DetranEmailWrapper();
                            email.setSubject("[DETRAN-WEB Processo Administrativo] Erro Transferência Correios");
                            email.setBody("<b> Não foi possível enviar o arquivo " + nomeArquivo + " para o servidor dos correioss."
                                    + " Favor entrar em contato com a DIRTI. </b>");

                            getProcessoAdministrativoService()
                                    .gravarFalhaEMandarEmail(
                                            new AppException("Não foi possível enviar o arquivo " + nomeArquivo + " para o servidor correios"),
                                            null,
                                            "TRANSFERENCIA_CORREIOS",
                                            email);
                        }
                    }

                    txt.encerrarEscrita();

                    ConfiguracaoFtp configuracaoCorreios = (ConfiguracaoFtp) getApoioService().getConfiguracaoFtpPorNome("ENVIA_CORREIO");

                    if (configuracaoCorreios == null) {
                        DetranWebUtils.applicationMessageException("Não foi encontrada uma configuração de envio via FTP para o servidor CORREIOS");
                    }

                    ConfiguracaoFtp configuracaoServer = (ConfiguracaoFtp) getApoioService().getConfiguracaoFtpPorNome("ENVIA_CORREIO_BKP");

                    if (configuracaoServer == null) {
                        DetranWebUtils.applicationMessageException("Não foi encontrada uma configuração de envio via FTP para o servidor ASSI_FTP ");
                    }

                    DtnFTPClient clienteCorreios = new DtnFTPClient(configuracaoCorreios.getConfiguracaoFTP());
                    DtnFTPClient clienteServidorLocal = new DtnFTPClient(configuracaoServer.getConfiguracaoFTP());

                    boolean doneCorreios = clienteCorreios.upload(nomeArquivo, configuracaoCorreios.getCaminhoDiretorio().concat(nomeArquivo).concat(TipoExtensaoArquivoEnum.TEXT.toString()), true);
                    boolean doneServer = clienteServidorLocal.upload(nomeArquivo, configuracaoServer.getCaminhoDiretorio().concat(nomeArquivo).concat(TipoExtensaoArquivoEnum.TEXT.toString()), true);
                    
                    if (doneCorreios && doneServer) {

                        //Remover Arquivo do Glassfish
                        File arquivoCorreios = new File(nomeArquivo);

                        if (arquivoCorreios.exists()) {
                            arquivoCorreios.delete();
                        }
                    }

                    if (!doneCorreios) {
                        LOG.warn("Não foi possível enviar o arquivo {0} para o correioss", nomeArquivo);

                        DetranEmailWrapper email = new DetranEmailWrapper();
                        email.setSubject("[DETRAN-WEB Processo Administrativo] Erro Transferência Correios");
                        email.setBody("<b> Não foi possível enviar o arquivo " + nomeArquivo + " para o servidor dos correioss."
                                + " Favor entrar em contato com a DIRTI. </b>");

                        getProcessoAdministrativoService()
                                .gravarFalhaEMandarEmail(new AppException("Não foi possível enviar o arquivo " + nomeArquivo + " para o servidor correios"),
                                        null,
                                        "TRANSFERENCIA_CORREIOS",
                                        email);
                    }

                    if (!doneServer) {
                        LOG.warn("Não foi possível enviar o arquivo {0} para o servidor local", nomeArquivo);

                        DetranEmailWrapper email = new DetranEmailWrapper();
                        email.setSubject("[DETRAN-WEB Processo Administrativo] Erro Transferência Servidor Local");
                        email.setBody("<b> Não foi possível enviar o arquivo " + nomeArquivo + " para o servidor local."
                                + " Favor entrar em contato com a DIRTI. </b>");

                        getProcessoAdministrativoService()
                                .gravarFalhaEMandarEmail(new AppException("Não foi possível enviar o arquivo " + nomeArquivo + " para o servidor local"),
                                        null,
                                        "TRANSFERENCIA_CORREIOS",
                                        email);
                    }

                } catch (Exception ex) {

                    LOG.debug("Sem tratamento.", ex);

                    DetranEmailWrapper email = new DetranEmailWrapper();
                    email.setSubject("[DETRAN-WEB Processo Administrativo] Erro Transferência Correios.");
                    email.setBody("<b> Não foi possível enviar o arquivo para o servidor local e/ou servidor correioss."
                            + " Favor entrar em contato com a DIRTI. </b>");

                    getProcessoAdministrativoService()
                            .gravarFalhaEMandarEmail(new AppException("Não foi possível enviar o arquivo para o servidor local"),
                                    null,
                                    "TRANSFERENCIA_CORREIOS",
                                    email);
                }
            }
        }
    }
    
    /**
     *
     * @param em
     * @throws AppException
     */
    public void enviaArquivoCorreioLab(EntityManager em) throws AppException {

        List andamentos
            = DetranCollectionUtil
                .montaLista(PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_INSTAURACAO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_PENALIZACAO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_ENTREGA_CNH,
                    PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_ACOLHIMENTO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_NAO_CONHECIMENTO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_NAO_CONHECIMENTO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_DESENTRANHAMENTO
                );

        List<NotificacaoProcessoAdministrativo> notificacoes 
            = new NotificacaoProcessoAdministrativoRepositorio()
                .getNotificadosPorAndamentoESituacao(em, andamentos);

        if (!DetranCollectionUtil.ehNuloOuVazio(notificacoes)) {

            Map<TipoFasePaEnum, Collection<NotificacaoProcessoAdministrativo>> mapNotificacaoPorTipo = transformListToMap(notificacoes);

            ExecucaoAndamentoManager execucaoAndamentoManager = new ExecucaoAndamentoManager();
            
            for (Map.Entry<TipoFasePaEnum, Collection<NotificacaoProcessoAdministrativo>> entry : mapNotificacaoPorTipo.entrySet()) {

                try {

                    TipoFasePaEnum tipoNotificacao = entry.getKey();
                    Collection<NotificacaoProcessoAdministrativo> lNotificacao = entry.getValue();

                    String nomeArquivo = "DADOSAR_DETRAN_PA" + tipoNotificacao.getTipoFasePaExterna() + "_" + Utils.formatDate(new Date(), "yyyyMMddHHmmss");

                    DetranArquivoTexto txt = new DetranArquivoTexto(nomeArquivo);

                    txt.iniciarArquivoParaEscrita();

                    /** Por tipo. **/
                    for (NotificacaoProcessoAdministrativo notificacao : lNotificacao) {

                        try {

                            txt.gravarNoArquivoParaWindows(notificacao.getLinhaCorreios());

                            execucaoAndamentoManager.iniciaExecucao(
                                new ExecucaoAndamentoEspecificoWrapper(notificacao.getProcessoAdministrativo())
                            );
                            
//                            getProcessoAdministrativoService().proximoAndamento(notificacao.getProcessoAdministrativo());

                        } catch (Exception e) {
                            
                            LOG.debug("Não foi possível enviar o arquivo {0} para o correioss", e, nomeArquivo);
                            
                            DetranEmailWrapper email = new DetranEmailWrapper();
                            
                            email.setSubject("[DETRAN-WEB Processo Administrativo] Erro Transferência Correios");
                            email.setBody("<b> Não foi possível enviar o arquivo " + nomeArquivo + " para o servidor dos correioss."
                                    + " Favor entrar em contato com a DIRTI. </b>");

                            getProcessoAdministrativoService()
                                .gravarFalhaEMandarEmail(
                                    new AppException("Não foi possível enviar o arquivo " + nomeArquivo + " para o servidor correios"),
                                    null,
                                    "TRANSFERENCIA_CORREIOS",
                                    email
                                );
                        }
                    }

                    txt.encerrarEscrita();

                    ConfiguracaoFtp configuracaoCorreios = (ConfiguracaoFtp) getApoioService().getConfiguracaoFtpPorNome("ENVIA_CORREIO");

                    if (configuracaoCorreios == null) {
                        DetranWebUtils.applicationMessageException("Não foi encontrada uma configuração de envio via FTP para o servidor CORREIOS");
                    }

                    ConfiguracaoFtp configuracaoServer = (ConfiguracaoFtp) getApoioService().getConfiguracaoFtpPorNome("ENVIA_CORREIO_BKP");

                    if (configuracaoServer == null) {
                        DetranWebUtils.applicationMessageException("Não foi encontrada uma configuração de envio via FTP para o servidor ASSI_FTP ");
                    }

                    DtnFTPClient clienteCorreios = new DtnFTPClient(configuracaoCorreios.getConfiguracaoFTP());
                    DtnFTPClient clienteServidorLocal = new DtnFTPClient(configuracaoServer.getConfiguracaoFTP());

                    boolean doneCorreios = clienteCorreios.upload(nomeArquivo, configuracaoCorreios.getCaminhoDiretorio().concat(nomeArquivo).concat(TipoExtensaoArquivoEnum.TEXT.toString()), true);
                    boolean doneServer = clienteServidorLocal.upload(nomeArquivo, configuracaoServer.getCaminhoDiretorio().concat(nomeArquivo).concat(TipoExtensaoArquivoEnum.TEXT.toString()), true);
                    
                    if (doneCorreios && doneServer) {

                        //Remover Arquivo do Glassfish
                        File arquivoCorreios = new File(nomeArquivo);

                        if (arquivoCorreios.exists()) {
                            arquivoCorreios.delete();
                        }
                    }

                    if (!doneCorreios) {
                        
                        LOG.warn("Não foi possível enviar o arquivo {0} para o correioss", nomeArquivo);

                        DetranEmailWrapper email = new DetranEmailWrapper();
                        email.setSubject("[DETRAN-WEB Processo Administrativo] Erro Transferência Correios");
                        email.setBody("<b> Não foi possível enviar o arquivo " + nomeArquivo + " para o servidor dos correioss."
                                + " Favor entrar em contato com a DIRTI. </b>");

                        getProcessoAdministrativoService()
                            .gravarFalhaEMandarEmail(
                                new AppException("Não foi possível enviar o arquivo " + nomeArquivo + " para o servidor correios"),
                                null,
                                "TRANSFERENCIA_CORREIOS",
                                email
                            );
                    }

                    if (!doneServer) {
                        
                        LOG.warn("Não foi possível enviar o arquivo {0} para o servidor local", nomeArquivo);

                        DetranEmailWrapper email = new DetranEmailWrapper();
                        
                        email.setSubject("[DETRAN-WEB Processo Administrativo] Erro Transferência Servidor Local");
                        email.setBody("<b> Não foi possível enviar o arquivo " + nomeArquivo + " para o servidor local."
                                + " Favor entrar em contato com a DIRTI. </b>");

                        getProcessoAdministrativoService()
                            .gravarFalhaEMandarEmail(
                                new AppException("Não foi possível enviar o arquivo " + nomeArquivo + " para o servidor local"),
                                null,
                                "TRANSFERENCIA_CORREIOS",
                                email
                            );
                    }

                } catch (Exception ex) {

                    LOG.debug("Sem tratamento.", ex);

                    DetranEmailWrapper email = new DetranEmailWrapper();
                    
                    email.setSubject("[DETRAN-WEB Processo Administrativo] Erro Transferência Correios.");
                    email.setBody("<b> Não foi possível enviar o arquivo para o servidor local e/ou servidor correioss."
                            + " Favor entrar em contato com a DIRTI. </b>");

                    getProcessoAdministrativoService()
                        .gravarFalhaEMandarEmail(
                            new AppException("Não foi possível enviar o arquivo para o servidor local"),
                            null,
                            "TRANSFERENCIA_CORREIOS",
                            email
                        );
                }
            }
        }
    }

    public List<byte[]> mergeNotificacoes(List<ArquivoPA> notificacoes) throws AppException {

        List<byte[]> pdfs = Lists.transform(notificacoes, new Function<ArquivoPA, byte[]>() {
            @Override
            public byte[] apply(ArquivoPA f) {
                return f.getByteArquivo();
            }
        });

        return pdfs;
    }

    /**
     * @param
     * @return
     */
    private Map<TipoFasePaEnum, Collection<NotificacaoProcessoAdministrativo>> transformListToMap(
            List<NotificacaoProcessoAdministrativo> notificacoes) {

        ImmutableListMultimap<TipoFasePaEnum, NotificacaoProcessoAdministrativo> listaMapeada
                = Multimaps.index(notificacoes, new Function<NotificacaoProcessoAdministrativo, TipoFasePaEnum>() {
                    @Override
                    public TipoFasePaEnum apply(NotificacaoProcessoAdministrativo input) {
                        return input.getTipoNotificacaoProcesso();
                    }
                });

        ImmutableMap<TipoFasePaEnum, Collection<NotificacaoProcessoAdministrativo>> arvoreMapeada = listaMapeada.asMap();

        Predicate<Collection<NotificacaoProcessoAdministrativo>> predicado = new Predicate<Collection<NotificacaoProcessoAdministrativo>>() {
            @Override
            public boolean apply(Collection<NotificacaoProcessoAdministrativo> t) {
                return !DetranCollectionUtil.ehNuloOuVazio(t);
            }
        };

        return Maps.filterValues(arvoreMapeada, predicado);
    }

    /**
     * 
     * @param url
     * @param nomeArquivoConferencia
     * @param tipoNotificacao
     * @param lNotificacoes
     * @throws AppException
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void gerarLoteEArquivoConferencia(
                                                        String url, 
                                                        String nomeArquivoConferencia, 
                                                        TipoFasePaEnum tipoNotificacao, 
                                                        List<NotificacaoProcessoAdministrativo> lNotificacoes
        ) throws AppException, FileNotFoundException, IOException {
        
        /**
        * gerar pdf da conferencia
         * atualizar lote
         */
        
        LoteNotificacaoPA lote = getProcessoAdministrativoService().gerarLoteNotificacao(nomeArquivoConferencia, tipoNotificacao, lNotificacoes);
        
        getProcessoAdministrativoService().gerarArquivoConferenciaLote(lote, url);
        
    }

    /**
     * Sempre que for disponibilizado um arquivo para impressão gerar e-mail para os seguintes endereços:
     * 
     * apsilva@fazenda.ms.gov.br
     * planet_ms@fazenda.ms.gov.br
     * mbusanello@fazenda.ms.gov.br
     * omishima@fazenda.ms.gov.br
     * 
     * @param em
     * @param nomeArquivo
     * @throws DatabaseException 
     */
    public void enviarEmail(EntityManager em, String nomeArquivo, TipoFasePaEnum tipoNotificacao) throws DatabaseException {
        
        DetranEmailWrapper email = new DetranEmailWrapper();
        
        List<String> parametrosDestinatarios = 
                new PAParametrizacaoRepositorio()
                        .getEmailDestinatarioPA(em, ParametrizacaoEnum.LISTA_EMAIL_NOTIFICACOES_PA_ENVIO_SGI.getCode());
        
        if (!DetranCollectionUtil.ehNuloOuVazio(parametrosDestinatarios)) {
            
            if (DetranStringUtil.ehBrancoOuNulo(email.getSubject())) {
                email.setSubject("Notificação de PA - " + nomeArquivo);
            }
            
            if (DetranStringUtil.ehBrancoOuNulo(email.getBody())) {
                
                StringBuilder corpo = new StringBuilder();
                
                corpo.append("FAVOR IMPRIMIR O ARQUIVO EM PDF - NOTIFICAÇÃO DE PA <br/>");
                corpo.append("NOTIFICAÇÃO DE ");
                corpo.append(tipoNotificacao.toString().toUpperCase());
                corpo.append(" PROCESSO ADMINISTRATIVO <br/>");
                corpo.append("ENVIAR PARA O DETRAN - BLOCO 05 <br/> <br/>");
                corpo.append("AOS CUIDADOS DA SEPEN <br/> <br/>");
                corpo.append("PARA PREPARAÇÃO: FAVOR SERRILHAR E ENVELOPAR.");
                
                email.setBody(corpo.toString());
            }
            
            email.setContent("text/html; charset=\"UTF-8\"");
            email.setTo(parametrosDestinatarios);
            DetranEmailUtil.send(email);
        }
    }
    
    
    private byte[] adicionarContadorPagina(byte[] byteArquivo, Integer contador) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArquivo);
        
        try {
            PdfReader reader = new PdfReader(inputStream);
            PdfDocument pdfDoc = new PdfDocument(reader.setUnethicalReading(true), new PdfWriter(stream));
            Document doc = new Document(pdfDoc);

            int numberOfPages = pdfDoc.getNumberOfPages();
            for (int i = 1; i <= numberOfPages; i++) {

                // Write aligned text to the specified by parameters point
                doc.showTextAligned(new Paragraph(String.format("%s", contador)),
                        30, 30, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
            }

            doc.close();
            
            return stream.toByteArray();
            
        } catch (IOException e) {
            return byteArquivo;
        }

    }
}
