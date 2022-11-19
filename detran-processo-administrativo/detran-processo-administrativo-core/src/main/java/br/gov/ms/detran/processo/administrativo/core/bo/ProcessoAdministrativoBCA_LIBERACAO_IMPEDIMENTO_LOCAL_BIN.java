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
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoBCAWrapper;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import javax.persistence.EntityManager;

/**
 * 
 * Andamento = 70
 * 
 * Executa AEMNPP11 - Desbloqueio
 * 
 * @author desenvolvimento
 */
public class ProcessoAdministrativoBCA_LIBERACAO_IMPEDIMENTO_LOCAL_BIN extends ProcessoAdministrativoBCABO implements IProcessoAdministrativoBCA {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoBCA_LIBERACAO_IMPEDIMENTO_LOCAL_BIN.class);

    @Override
    public void executa(EntityManager em, String urlPathParaRelatorio, Integer codigoAndamento) {

        LOG.info("INICIO DESBLOQUEIO - AEMNPP11");
        
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
                                "AEMNPP11 - PA CPF = {0} PA NÚMERO PROCESSO = {1}", 
                                processoWrapper.getProcessoAdministrativo().getCpf(), 
                                processoWrapper.getProcessoAdministrativo().getNumeroProcesso()
                            );

                        AEBNH011 aebnh011 
                            = new AEBNH011BO()
                                .executarIntegracaoAEBNH011(processoWrapper.getProcessoAdministrativo().getCpf());

                        if (aebnh011 == null) {
                            DetranWebUtils.applicationMessageException("processoAdministrativoBCA.M1");
                        }

                        if (!DetranCollectionUtil.ehNuloOuVazio(aebnh011.getBloqueioLocal())
                                || !DetranCollectionUtil.ehNuloOuVazio(aebnh011.getBloqueioNacional())) {

                            getBloqueioService().executarAEMNPP11(processoWrapper.getProcessoAdministrativo(), "");

                        } else {

                            getProcessoAdministrativoService()
                                .proximoAndamento(processoWrapper.getProcessoAdministrativo());
                        }

                    } catch(AppException e) {

                        processoWrapper.setSucessoBloqueio(Boolean.FALSE);

                        processoWrapper.setObservacao(e.getParams() != null ? e.getParams()[0] : "Erro sem tratamento.");

                        LOG.debug("Erro ao executar Bloqueio através da integração: AEMNPP11", e);

                        getControleFalha()
                            .gravarFalhaProcessoAdministrativo(
                                e, 
                                "Erro ao executar Desbloqueio: AEMNPP11", 
                                null, 
                                processoWrapper != null ? processoWrapper.getProcessoAdministrativo().getNumeroProcesso() : null
                            );

                    } catch(Exception e) {

                        LOG.debug("Erro ao executar Desbloqueio através da integração: AEMNPP11", e);

                        processoWrapper.setSucessoBloqueio(Boolean.FALSE);

                        processoWrapper.setObservacao("Erro sem tratamento.");

                        getControleFalha()
                            .gravarFalhaProcessoAdministrativo(
                                e, 
                                "Erro ao executar Desbloqueio: AEMNPP11", 
                                null, 
                                processoWrapper != null ? processoWrapper.getProcessoAdministrativo().getNumeroProcesso() : null
                            );
                    }
                }
            }

            registrarExecucaoEGravarArquivoRelatorio(em, urlPathParaRelatorio, "AEMNPP11", processos);

            LOG.info("FIM DESBLOQUEIO - AEMNPP11");
        }
    }
}