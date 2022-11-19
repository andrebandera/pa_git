/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranEmailWrapper;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusHistoricoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAStatusAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevidoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAAndamentoService;
import br.gov.ms.detran.processo.administrativo.entidade.FunctionProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevido;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatusHistorico;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

public class ProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevidoBO extends ProcessoAdministrativoBCABO implements IProcessoAdministrativoBCA {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevidoBO.class);
    
    final Integer CODIGO_FLUXO_PROCESSO_ANDAMENTO_243 = 52;
    final Integer CODIGO_ANDAMENTO_243 = 243;
    
    private IPAAndamentoService executaAndamentoService;

    public IPAAndamentoService getExecutaAndamentoService() {
        
        if (executaAndamentoService == null) {
            executaAndamentoService = (IPAAndamentoService) JNDIUtil.lookup("ejb/PAAndamentoService");
        }
        
        return executaAndamentoService;
    }
    
    @Override
    public void executa(EntityManager em, String urlPathParaRelatorio, Integer codigoAndamento) {
        
        try {
            
            List<FunctionProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevido> lProcessoArquivoIndevido
                = new ProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevidoRepositorio().getFunctionProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevido(em);
            
            if(!DetranCollectionUtil.ehNuloOuVazio(lProcessoArquivoIndevido)) {
                
                final TipoFasePaEnum TIPO_NOTIFICACAO                   = TipoFasePaEnum.ARQUIVAMENTO;
                
                final String url                                        = urlPathParaRelatorio;
                List<NotificacaoProcessoAdministrativo> lNotificacao    = new ArrayList();
                
                for (FunctionProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevido processoArquivamentoIndevido : lProcessoArquivoIndevido) {
                    
                    ProcessoAdministrativo processoAdministrativo 
                        = getProcessoAdministrativoService().getProcessoAdministrativo(processoArquivamentoIndevido.getIdProcessoAdministrativo());
                    
                    try {
                        
                        ExecucaoAndamentoEspecificoWrapper execucao = new ExecucaoAndamentoEspecificoWrapper();
                        
                        execucao.setProcessoAdministrativo(processoAdministrativo);
                        execucao.setObjetoWrapper(TIPO_NOTIFICACAO);
                        execucao.setUrlBaseBirt(url);
                        
                        getProcessoAdministrativoService()
                            .alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(
                                processoAdministrativo, 
                                CODIGO_FLUXO_PROCESSO_ANDAMENTO_243, 
                                CODIGO_ANDAMENTO_243
                            );
                        
                        getExecutaAndamentoService().executaAndamentoSemAlterarAndamentoOuFluxo(execucao);
                        
                        lNotificacao.add((NotificacaoProcessoAdministrativo) execucao.getObjetoWrapper());
                        
                    } catch (Exception e) {

                        LOG.debug("Erro ao executar emissão da notificacão de arquivamento indevido.", e);

                        String causa = "Erro ao executar emissão da notificacão de arquivamento indevido. Processo ID " + processoArquivamentoIndevido.getIdProcessoAdministrativo();

                        getControleFalha().gravarFalhaCondutor(e, causa, processoArquivamentoIndevido.getCpf());
                    } finally {
                        retornaProcessoAdministrativoParaAndamentoAnterior(em, processoAdministrativo);
                    }
                }
                
                if(!DetranCollectionUtil.ehNuloOuVazio(lNotificacao)) {
                
                    String nomeArquivo = "NOT" + TIPO_NOTIFICACAO.getTipoFasePaExterna() + "PA_" + Utils.formatDate(new Date(), "yyyyMMdd_HHmmss");

                    try {

                        String nomeArquivoConferencia = "CONFERENCIA_NOT" + TIPO_NOTIFICACAO.getTipoFasePaExterna() + "PA_" + Utils.formatDate(new Date(), "yyyyMMdd_HHmmss");

                        new TransferenciaNotificacoesBO().gerarLoteEArquivoConferencia(url, nomeArquivoConferencia, TIPO_NOTIFICACAO, lNotificacao);

                        List<byte[]> pdfs = new TransferenciaNotificacoesBO().gerarPdfNotificacoes(nomeArquivo, lNotificacao, TIPO_NOTIFICACAO);

                        new TransferenciaNotificacoesBO().gravarArquivoPasta(nomeArquivo, "ENVIA_PDF_PA_SGI");
                        new TransferenciaNotificacoesBO().gravarArquivoPasta(nomeArquivoConferencia, "ENVIA_PDF_PA_SGI");

                        /** Copia. **/
                        new TransferenciaNotificacoesBO().gravarArquivoPasta(nomeArquivo, "ENVIA_PDF_PA_SGI_BKP");
                        new TransferenciaNotificacoesBO().gravarArquivoPasta(nomeArquivoConferencia, "ENVIA_PDF_PA_SGI_BKP");

                        /** DISPONIBILIZAR PARA SISTEMA EXTERNO (BPMS). **/
                        new TransferenciaNotificacoesBO().gravarArquivoPasta(nomeArquivo, "ENVIA_PDF_PA_SGI_BPMS");
                        new TransferenciaNotificacoesBO().gravarArquivoPasta(nomeArquivoConferencia, "ENVIA_PDF_PA_SGI_BPMS");

                        /** Remover Arquivo do Glassfish. **/
                        File arquivoPdf = new File(nomeArquivo);

                        if (arquivoPdf.exists()) {
                            arquivoPdf.delete();
                        }

                        getProcessoAdministrativoService().enviarEmail(nomeArquivo, TIPO_NOTIFICACAO);

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

        } catch (Exception e) {
            LOG.error("Erro ao executar atualizacao bloqueio BCA.", e);
            getControleFalha().gravarFalhaProcessoAdministrativo(e, "Erro ao executar emissão da notificacão de arquivamento indevido.", null, null);
        } finally {
            LOG.info("Fim execucão");
        }
    }
    
    /**
     * 
     * @param em
     * @param processoAdministrativo 
     */
    private void retornaProcessoAdministrativoParaAndamentoAnterior(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        PAStatusAndamento paStatusAndamentoAtual 
            = new PAStatusAndamentoRepositorio()
                .getPAStatusAndamentoAtivoPorStatusEAndamento(em, CODIGO_ANDAMENTO_243);
        
        PAOcorrenciaStatusHistorico paOcorrenciaStatusHistorico 
            = new PAOcorrenciaStatusHistoricoRepositorio().getUltimoHistoricoDiferentePAStatusAndamentoAtual(em, processoAdministrativo.getId(), paStatusAndamentoAtual.getId());

        PAStatusAndamento statusAndamento 
            = new PAStatusAndamentoRepositorio()
                .find(em, PAStatusAndamento.class, paOcorrenciaStatusHistorico.getIdStatusAndamento());
        
        PAFluxoProcesso fluxoProcesso 
            = new PAFluxoProcessoRepositorio()
                .find(em, PAFluxoProcesso.class, paOcorrenciaStatusHistorico.getIdFluxoProcesso());
        
        getProcessoAdministrativoService()
            .alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(processoAdministrativo, fluxoProcesso.getCodigo(), statusAndamento.getAndamentoProcesso().getCodigo());
    }
}