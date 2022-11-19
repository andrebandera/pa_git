/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.ParametrizacaoValor;
import br.gov.ms.detran.comum.core.projeto.wrapper.EntityXMLWrapper;
import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.constantes.ParametrizacaoEnum;
import br.gov.ms.detran.comum.util.DetranArquivoHelper;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranHTTPUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.comum.util.xml.JaxbUtil;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IBloqueioBCAService;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoBCA;
import br.gov.ms.detran.processo.administrativo.enums.ProcessoAdministrativoBCAEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoBCARelatorioDetalheWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoBCARelatorioTotalWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoBCAWrapper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class ProcessoAdministrativoBCABO {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoBCABO.class);

    private IBloqueioBCAService bloqueioService;
    private IProcessoAdministrativoService processoAdministrativoService;
    private IPAControleFalhaService paControleFalha;

    private IApoioService apoioService;
    
    protected IBloqueioBCAService getBloqueioService() {
        
        if(bloqueioService == null) {
            bloqueioService = ServiceJndiLocator.<IBloqueioBCAService>lookup("ejb/BloqueioBCAService");
        }
        
        return bloqueioService;
    }

    protected IProcessoAdministrativoService getProcessoAdministrativoService() {
        if (processoAdministrativoService == null) {
            processoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }
        return processoAdministrativoService;
    }
    
    protected IPAControleFalhaService getControleFalha() {
        if (paControleFalha == null) {
            paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
        }
        return paControleFalha;
    }
    
    protected IApoioService getApoioService() {
        
        if (apoioService == null) {
            apoioService = ServiceJndiLocator.<IApoioService>lookup("ejb/ApoioService");
        }
        
        return apoioService;
    }

    /**
     * 
     * @param em
     * @param urlPathParaRelatorio
     */
    public void executaAnaliseEExecucaoBloqueioEDesbloqueioParaProcessoAdministrativo(EntityManager em, String urlPathParaRelatorio)  {
        
        try {
        
            ParametrizacaoValor valorParametro 
                = (ParametrizacaoValor) getApoioService().getParametroValorPorCodigo(Long.valueOf(ParametrizacaoEnum.PROCESSO_ADMINISTRATIVO_BLOQUEIO_BCA_ANDAMENTOS.getCode()));

            List<Integer> andamentos 
                = Arrays.asList(valorParametro.getValorAlphaNumerico().split(",")).stream().map(Integer::parseInt).collect(Collectors.toList());

            for (Integer codigoAndamento : andamentos) {

                try {

                    ProcessoAdministrativoBCAEnum.getImplementacaoAndamentoBCA(Integer.valueOf(codigoAndamento)).executa(em, urlPathParaRelatorio, codigoAndamento);

                } catch(Exception e) {
                    LOG.debug("Implementacao andamento {0} inexistente", e, codigoAndamento);
                    getControleFalha().gravarFalha(e, "Implementacao andamento inexistente");
                }
            }
        
        } catch(Exception e) {
            LOG.debug("Execucao andamento invalida.", e);
            getControleFalha().gravarFalha(e, "Execucao andamento invalida.");
        }
    }

    /**
     * 
     * @param em
     * @param andamentos
     * @return
     */
    protected ImmutableMap getProcessosParaBloqueioEDesbloqueio(EntityManager em, List<Integer> andamentos) {
        
        ImmutableListMultimap<Integer, ProcessoAdministrativoBCAWrapper> listaMapeada = null;
        
        try {

            List lProcessoAdministrativo 
                = new ProcessoAdministrativoRepositorio()
                    .getListaProcessoAdministrativoPorAndamentoIniciadoParaBCA(em, andamentos);

            if(!DetranCollectionUtil.ehNuloOuVazio(lProcessoAdministrativo)) {

                listaMapeada
                    = Multimaps.index(lProcessoAdministrativo, new Function<ProcessoAdministrativoBCAWrapper, Integer>() {
                        @Override
                        public Integer apply(ProcessoAdministrativoBCAWrapper input) {
                            return input.getCodigoAndamento();
                        }
                    });
            }
       
        } catch(Exception e) {
            LOG.debug("Recuperacaoo processo para andamento invalida.", e);
            getControleFalha().gravarFalha(e, "Recuperacaoo processo para andamento invalida.");
        }
        
        return listaMapeada != null && !listaMapeada.isEmpty() ? listaMapeada.asMap() : null;
    }
    
    /**
     * 
     * @param integracao
     * @param urlPathParaRelatorio
     * @param processos 
     * @param em 
     */
    public void registrarExecucaoEGravarArquivoRelatorio(
        EntityManager em, String urlPathParaRelatorio, String integracao, Collection<ProcessoAdministrativoBCAWrapper> processos) {
        
        try {
        
            ProcessoAdministrativoBCARelatorioTotalWrapper relatorioTotal 
                = new ProcessoAdministrativoBCARelatorioTotalWrapper(
                    integracao,
                    Calendar.getInstance().getTime(),
                    0,
                    0
                );
            

            relatorioTotal.setProcessos(new ArrayList());

            for (ProcessoAdministrativoBCAWrapper processoWrapper : processos) {
                
                if(processoWrapper.getSucessoBloqueio()) {
                    relatorioTotal.setListaProcessoSucesso(relatorioTotal.getListaProcessoSucesso() + processoWrapper.getProcessoAdministrativo().getId() + ",");
                } else {
                    relatorioTotal.setListaProcessoFalha(relatorioTotal.getListaProcessoFalha() + processoWrapper.getProcessoAdministrativo().getId() + ",");
                }
                
                ProcessoAdministrativoBCARelatorioDetalheWrapper detalhe 
                    = new ProcessoAdministrativoBCARelatorioDetalheWrapper(
                        processoWrapper.getProcessoAdministrativo().getNumeroProcesso(),
                        processoWrapper.getSucessoBloqueio() ? "SIM" : "NÃO",
                        processoWrapper.getObservacao()
                    );
                
                relatorioTotal
                    .setQuantidadeFalhou(
                        !processoWrapper.getSucessoBloqueio() ? relatorioTotal.getQuantidadeFalhou() + 1 : relatorioTotal.getQuantidadeFalhou()
                    );
                
                relatorioTotal
                    .setQuantidadeSucesso(
                        processoWrapper.getSucessoBloqueio() ? relatorioTotal.getQuantidadeSucesso() + 1 : relatorioTotal.getQuantidadeSucesso()
                    );

                relatorioTotal.getProcessos().add(detalhe);
            }
            
            List<ProcessoAdministrativoBCARelatorioTotalWrapper> lista = new ArrayList();
            lista.add(relatorioTotal);
            
            if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {

                EntityXMLWrapper xmlEntity = new EntityXMLWrapper();

                xmlEntity
                    .addFiltro(
                        "Data Processamento",
                        Utils.formatDate(Calendar.getInstance().getTime(), "dd/MM/yyyy")
                    );

                for (ProcessoAdministrativoBCARelatorioTotalWrapper wrapper : lista) {
                    xmlEntity.addDado(wrapper);
                }
                
                String xml 
                    = JaxbUtil
                        .marshal(
                            xmlEntity,
                            EntityXMLWrapper.class,
                            ProcessoAdministrativoBCARelatorioTotalWrapper.class,
                            ProcessoAdministrativoBCARelatorioDetalheWrapper.class
                        );

                String nomeArquivo 
                    = Utils.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmss") + "_relatorio_pa_bca.xml";
                
                DetranArquivoHelper.saveFile(nomeArquivo, xml.getBytes());

                Map<String, String> params = new HashMap<>();
                params.put("uid", nomeArquivo);
                
                String urlRelatorio
                    = DetranReportsUtil.getReportParamsBirtUrl(
                        "relatoriopabca",
                        FormatoRelatorioEnum.PDF.getRptFormat(),
                        params,
                        "relatorios/processoadministrativo/"
                    );
                
                registraGravacaoProcessamento(
                    em, 
                    relatorioTotal, 
                    integracao, 
                    DetranHTTPUtil.download(urlPathParaRelatorio + urlRelatorio)
                );
                
                File relatorio = new File(nomeArquivo);

                if (relatorio.exists()) {
                    relatorio.delete();
                }
            }
        
        } catch(Exception e) {
                    
            LOG.debug("Erro ao gerar Relatório Bloqueio/Desbloqueio para integração: {0}", e);

            getControleFalha()
                .gravarFalha(
                    e, 
                    "Erro ao gerar Relatório Bloqueio/Desbloqueio para integração: " + integracao
                );
        }
    }

    /**
     * 
     * @param em
     * @param relatorioTotal
     * @param integracao
     * @param arquivoPDF
     * @throws DatabaseException 
     */
    public void registraGravacaoProcessamento(
        EntityManager em, ProcessoAdministrativoBCARelatorioTotalWrapper relatorioTotal, String integracao, byte[] arquivoPDF) throws DatabaseException {
        
        ProcessoAdministrativoBCA paBCA = new ProcessoAdministrativoBCA();
                
        paBCA.setRelatorio(arquivoPDF);
        paBCA.setDataProcessamento(Calendar.getInstance().getTime());
        paBCA.setIntegracao(integracao);
        paBCA.setQuantidadeFalhou(relatorioTotal.getQuantidadeFalhou());
        paBCA.setQuantidadeSucesso(relatorioTotal.getQuantidadeSucesso());
        paBCA.setListaProcessoFalha(relatorioTotal.getListaProcessoFalha());
        paBCA.setListaProcessoSucesso(relatorioTotal.getListaProcessoSucesso());
        
        new AbstractJpaDAORepository().insert(em, paBCA);
    }
}