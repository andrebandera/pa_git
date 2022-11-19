/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEBNH011;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEBNH011BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ParametrosIntegracaoBloqueioBCAWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoBCAWrapper;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import javax.persistence.EntityManager;

/**
 *
 * Andamento = 44
 * 
 * Executa AEMNPP13
 * 
 * @author desenvolvimento
 */
public class ProcessoAdministrativoBCA_LIB_BLOQUEIO_BCA extends ProcessoAdministrativoBCABO implements IProcessoAdministrativoBCA {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoBCA_LIB_BLOQUEIO_BCA.class);

    /**
     * 
     * @param em
     * @param urlPathParaRelatorio
     * @param codigoAndamento 
     */
    @Override
    public void executa(EntityManager em, String urlPathParaRelatorio, Integer codigoAndamento) {
        
        LOG.info("INICIO BLOQUEIO - AEMNPP13");
        
        ImmutableMap<Integer, Collection<ProcessoAdministrativoBCAWrapper>> arvoreMapeada 
            = getProcessosParaBloqueioEDesbloqueio(em, DetranCollectionUtil.montaLista(codigoAndamento));
        
        if(arvoreMapeada != null && !arvoreMapeada.isEmpty()) {
        
            Collection<ProcessoAdministrativoBCAWrapper> processos = arvoreMapeada.get(codigoAndamento);
        
            if(!DetranCollectionUtil.ehNuloOuVazio(processos)) {

                for (ProcessoAdministrativoBCAWrapper processoWrapper : processos) {

                    processoWrapper.setSucessoBloqueio(Boolean.TRUE);

                    try {

                        LOG
                            .info(
                                "AEMNPP13 - PA CPF = {0} PA NÚMERO PROCESSO = {1}", 
                                processoWrapper.getProcessoAdministrativo().getCpf(), 
                                processoWrapper.getProcessoAdministrativo().getNumeroProcesso()
                            );

                        AEBNH011 aebnh011 
                            = new AEBNH011BO()
                                .executarIntegracaoAEBNH011(processoWrapper.getProcessoAdministrativo().getCpf());

                        if (aebnh011 == null) {
                            DetranWebUtils.applicationMessageException("processoAdministrativoBCA.M1");
                        }

                        if (DetranCollectionUtil.ehNuloOuVazio(aebnh011.getBloqueioLocal())
                                && DetranCollectionUtil.ehNuloOuVazio(aebnh011.getBloqueioNacional())) {

                            ParametrosIntegracaoBloqueioBCAWrapper wrapper 
                                = recuperaDadosParaBloqueioBCA(em, processoWrapper);

                            getBloqueioService().executarAEMNPP13(wrapper);

                        } else {

                            getProcessoAdministrativoService()
                                .proximoAndamento(processoWrapper.getProcessoAdministrativo());
                        }

                    } catch(AppException e) {

                        processoWrapper.setSucessoBloqueio(Boolean.FALSE);

                        processoWrapper.setObservacao(e.getParams() != null ? e.getParams()[0] : "Erro sem tratamento.");

                        LOG.debug("Erro ao executar Bloqueio através da integração: AEMNPP13", e);

                        getControleFalha()
                            .gravarFalhaProcessoAdministrativo(
                                e, 
                                "Erro ao executar Bloqueio: AEMNPP13", 
                                null, 
                                processoWrapper != null ? processoWrapper.getProcessoAdministrativo().getNumeroProcesso() : null
                            );

                    } catch(Exception e) {

                        processoWrapper.setSucessoBloqueio(Boolean.FALSE);

                        processoWrapper.setObservacao("Erro sem tratamento.");

                        LOG.debug("Erro ao executar Bloqueio através da integração: AEMNPP13", e);

                        getControleFalha()
                            .gravarFalhaProcessoAdministrativo(
                                e, 
                                "Erro ao executar Bloqueio: AEMNPP13", 
                                null, 
                                processoWrapper != null ? processoWrapper.getProcessoAdministrativo().getNumeroProcesso() : null
                            );

                    }
                }

                registrarExecucaoEGravarArquivoRelatorio(em, urlPathParaRelatorio, "AEMNPP13", processos);

                LOG.info("FIM BLOQUEIO - AEMNPP13");
            }
        }
    }
    
    /**
     * @param em
     * @param processoAdministrativoBCAWrapper
     * @return 
     * @throws AppException 
     */
    private ParametrosIntegracaoBloqueioBCAWrapper recuperaDadosParaBloqueioBCA(
        EntityManager em, ProcessoAdministrativoBCAWrapper processoAdministrativoBCAWrapper) throws AppException {
        
        ParametrosIntegracaoBloqueioBCAWrapper wrapper = new ParametrosIntegracaoBloqueioBCAWrapper();
        
        PAComplemento complemento = 
            new PAComplementoRepositorio()
                .getPAComplementoPorParametroEAtivo(
                    em, 
                    processoAdministrativoBCAWrapper.getProcessoAdministrativo(), 
                    PAParametroEnum.TEMPO_PENALIDADE
                );
        
        BloqueioBCA bloqueio 
            = new BloqueioBCARepositorio()
                .getBloqueioBcaPorPaEAtivo(em, processoAdministrativoBCAWrapper.getProcessoAdministrativo().getId());
        
        new BloqueioBCABO()
            .montarDadosPortaria(em, processoAdministrativoBCAWrapper.getProcessoAdministrativo(), wrapper);
        
        PAPenalidadeProcesso penalidade 
            = new PAPenalidadeProcessoRepositorio().
                getPenalidadePorPA(em, processoAdministrativoBCAWrapper.getProcessoAdministrativo().getId());
        
        if(penalidade != null){
            wrapper.setDataInicioPenalidade(penalidade.getDataInicioPenalidade());
        }

        wrapper.setPaTdcId(processoAdministrativoBCAWrapper.getProcessoAdministrativo().getId());
        wrapper.setCpf(processoAdministrativoBCAWrapper.getProcessoAdministrativo().getCpf());
        wrapper.setNumeroProcesso(processoAdministrativoBCAWrapper.getProcessoAdministrativo().getNumeroProcesso());
        wrapper.setTipoProcesso(processoAdministrativoBCAWrapper.getProcessoAdministrativo().getTipo());
        wrapper.setArtigoIncisoParagrafo(processoAdministrativoBCAWrapper.getArtigoInciso());
        wrapper.setDataBloqueio(bloqueio.getDataInicio());
        wrapper.setDataEntregaCnh(null);
        wrapper.setNumeroRestricaoComBloqueio("0");
        wrapper.setNumeroRestricaoSemBloqueio(bloqueio.getNumeroBloqueioBCA());
        wrapper.setMesesPenalizacao(complemento.getValor());
        
        new BloqueioBCABO()
            .montarDadosInfracao(em, processoAdministrativoBCAWrapper.getProcessoAdministrativo(), wrapper);
        
        return wrapper;
    }
}