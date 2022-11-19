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
import br.gov.ms.detran.integracao.comum.wrapper.AEBNH011B;
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
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * Andamento = 45
 * 
 * Executa AEMNPP14
 * 
 * @author desenvolvimento
 */
public class ProcessoAdministrativoBCA_LIBERACAO_BLOQUEIO_BCA_ENTREGA extends ProcessoAdministrativoBCABO implements IProcessoAdministrativoBCA {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoBCA_LIBERACAO_BLOQUEIO_BCA_ENTREGA.class);

    @Override
    public void executa(EntityManager em, String urlPathParaRelatorio, Integer codigoAndamento) {
        
        LOG.info("INICIO BLOQUEIO - AEMNPP14");
        
        ImmutableMap<Integer, Collection<ProcessoAdministrativoBCAWrapper>> arvoreMapeada 
            = getProcessosParaBloqueioEDesbloqueio(em, DetranCollectionUtil.montaLista(codigoAndamento));
        
        if(arvoreMapeada != null && !arvoreMapeada.isEmpty()) {
        
            Collection<ProcessoAdministrativoBCAWrapper> processos = arvoreMapeada.get(codigoAndamento);

            if(!DetranCollectionUtil.ehNuloOuVazio(processos)) {

                if(!DetranCollectionUtil.ehNuloOuVazio(processos)) {

                    for (ProcessoAdministrativoBCAWrapper processoWrapper : processos) {

                        processoWrapper.setSucessoBloqueio(Boolean.TRUE);

                        try {

                            LOG
                                .info(
                                    "AEMNPP14 - PA CPF = {0} PA NÚMERO PROCESSO = {1}", 
                                    processoWrapper.getProcessoAdministrativo().getCpf(), 
                                    processoWrapper.getProcessoAdministrativo().getNumeroProcesso()
                                );

                            Boolean permitidoProximoAndamento = Boolean.FALSE;

                            AEBNH011 aebnh011 
                                = new AEBNH011BO()
                                    .executarIntegracaoAEBNH011(processoWrapper.getProcessoAdministrativo().getCpf());

                            if (aebnh011 == null) {
                                DetranWebUtils.applicationMessageException("processoAdministrativoBCA.M1");
                            }

                            ParametrosIntegracaoBloqueioBCAWrapper wrapper 
                                = recuperaDadosParaBloqueioBCA(em, processoWrapper);

                            if (DetranCollectionUtil.ehNuloOuVazio(aebnh011.getBloqueioLocal())
                                    && DetranCollectionUtil.ehNuloOuVazio(aebnh011.getBloqueioNacional())) {

                                getBloqueioService().executarAEMNPP14(wrapper);

                            } else {

                                if (!DetranCollectionUtil.ehNuloOuVazio(aebnh011.getBloqueioLocal())) {

                                    permitidoProximoAndamento 
                                        = checaBloqueio(em, processoWrapper, wrapper, aebnh011.getBloqueioLocal());

                                } else {

                                    if (!DetranCollectionUtil.ehNuloOuVazio(aebnh011.getBloqueioNacional())) {

                                        permitidoProximoAndamento 
                                            = checaBloqueio(em, processoWrapper, wrapper, aebnh011.getBloqueioNacional());
                                    }
                                }
                            }

                            if(permitidoProximoAndamento) {

                                getProcessoAdministrativoService()
                                    .proximoAndamento(processoWrapper.getProcessoAdministrativo());
                            }

                        } catch(AppException e) {

                        processoWrapper.setSucessoBloqueio(Boolean.FALSE);

                        processoWrapper.setObservacao(e.getParams() != null ? e.getParams()[0] : "Erro sem tratamento.");

                        LOG.debug("Erro ao executar Bloqueio através da integração: AEMNPP14", e);

                        getControleFalha()
                                .gravarFalhaProcessoAdministrativo(
                                    e, 
                                    "Erro ao executar Bloqueio: AEMNPP14", 
                                    null, 
                                    processoWrapper != null ? processoWrapper.getProcessoAdministrativo().getNumeroProcesso() : null
                                );

                    } catch(Exception e) {

                            LOG.debug("Erro ao executar Bloqueio através da integração: AEMNPP14", e);

                            getControleFalha()
                                .gravarFalhaProcessoAdministrativo(
                                    e, 
                                    "Erro ao executar Bloqueio: AEMNPP14", 
                                    null, 
                                    processoWrapper != null ? processoWrapper.getProcessoAdministrativo().getNumeroProcesso() : null
                                );
                        }
                    }

                    registrarExecucaoEGravarArquivoRelatorio(em, urlPathParaRelatorio, "AEMNPP14", processos);

                    LOG.info("FIM BLOQUEIO - AEMNPP14");
                }
            }
        }
    }
    
    /**
     * 
     * @param em
     * @param processoBCAWrapper
     * @return
     * @throws AppException 
     */
    public ParametrosIntegracaoBloqueioBCAWrapper recuperaDadosParaBloqueioBCA(
        EntityManager em, ProcessoAdministrativoBCAWrapper processoBCAWrapper) throws AppException {
    
        ParametrosIntegracaoBloqueioBCAWrapper wrapper 
            = new ParametrosIntegracaoBloqueioBCAWrapper();
        
        PAPenalidadeProcesso penalidade 
            = new PAPenalidadeProcessoRepositorio()
                .getPenalidadePorPA(em, processoBCAWrapper.getProcessoAdministrativo().getId());
        
        BloqueioBCA bloqueio 
            = new BloqueioBCARepositorio()
                .getBloqueioBCAporPA(em, processoBCAWrapper.getProcessoAdministrativo().getId());
        
        if(penalidade == null || bloqueio == null){
            DetranWebUtils.applicationMessageException("Não foi possível localizar a penalidade e/ou bloqueio para este ProcessoAdministrativo.");
        }
        
        wrapper.setDataInicioPenalidade(penalidade.getDataInicioPenalidade());
        wrapper.setDataBloqueio(bloqueio.getDataInicio());
        wrapper.setNumeroRestricao(bloqueio.getNumeroBloqueioBCA());
        
        PAComplemento complemento = 
            new PAComplementoRepositorio()
                .getPAComplementoPorParametroEAtivo(
                    em, processoBCAWrapper.getProcessoAdministrativo(), PAParametroEnum.TEMPO_PENALIDADE
                );

        wrapper.setCpf(processoBCAWrapper.getProcessoAdministrativo().getCpf());
        wrapper.setPaTdcId(processoBCAWrapper.getProcessoAdministrativo().getId());
        wrapper.setTipoProcesso(processoBCAWrapper.getProcessoAdministrativo().getTipo());
        wrapper.setNumeroProcesso(processoBCAWrapper.getProcessoAdministrativo().getNumeroProcesso());
        wrapper.setArtigoIncisoParagrafo(processoBCAWrapper.getArtigoInciso());
        wrapper.setMesesPenalizacao(complemento.getValor());
        
        new BloqueioBCABO().montarDadosPortaria(em, processoBCAWrapper.getProcessoAdministrativo(), wrapper);
        new BloqueioBCABO().montarDadosInfracao(em, processoBCAWrapper.getProcessoAdministrativo(), wrapper);
        
        return wrapper;
    }

    /**
     * @param processoWrapper
     * @param wrapper
     * @param lAEBNH011B
     * @return
     * @throws AppException 
     */
    private Boolean checaBloqueio(EntityManager em, 
                                  ProcessoAdministrativoBCAWrapper processoWrapper, 
                                  ParametrosIntegracaoBloqueioBCAWrapper wrapper, 
                                  List<Object> lAEBNH011B) throws AppException {
        
        Boolean jaBloqueadoCorretamente = Boolean.FALSE;
        
        Boolean existeBloqueioPorMotivo = Boolean.FALSE;
        
        for (Object objeto : lAEBNH011B) {

            AEBNH011B AEBNH011B = (AEBNH011B) objeto;
            
            if (processoWrapper.getProcessoAdministrativo().getTipo().getMotivoMainframe().getMotivo()
                    .equals(AEBNH011B.getMotivoBloqueio())) {
                
                existeBloqueioPorMotivo = Boolean.TRUE;
            
            }
        }

        if (!existeBloqueioPorMotivo) {
            
            getBloqueioService().executarAEMNPP14(wrapper);
            
        } else {
            
            Long tempoPenalizacaoWeb = (Long) new PAComplementoRepositorio().getSomaTempoPenalidadeTodosProcessosAtivos(em, wrapper.getCpf());
            Long tempoPenalizacaoBca = new Long(((AEBNH011B) lAEBNH011B.get(0)).getQteTotalTempoPenalizacao());

            if (tempoPenalizacaoWeb > tempoPenalizacaoBca) {
                
                getBloqueioService().executarAEMNPP14(wrapper);
                
            } else if (tempoPenalizacaoWeb < tempoPenalizacaoBca) {
                
                DetranWebUtils.applicationMessageException("Bloqueio PA WEB com tempo de penalização menor do que o bloqueio na BCA.");
                
            } else {
                
                jaBloqueadoCorretamente = Boolean.TRUE;
                
            }
        }
        
        return jaBloqueadoCorretamente;
    }
}