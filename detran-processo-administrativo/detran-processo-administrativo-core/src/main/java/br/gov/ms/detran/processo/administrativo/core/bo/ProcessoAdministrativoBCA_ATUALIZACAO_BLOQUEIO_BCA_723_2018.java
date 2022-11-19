/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento240;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoBCAWrapper;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import javax.persistence.EntityManager;

public class ProcessoAdministrativoBCA_ATUALIZACAO_BLOQUEIO_BCA_723_2018 extends ProcessoAdministrativoBCABO implements IProcessoAdministrativoBCA {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoBCA_ATUALIZACAO_BLOQUEIO_BCA_723_2018.class);

    @Override
    public void executa(EntityManager em, String urlPathParaRelatorio, Integer codigoAndamento) {
        
        LOG.info("INICIO ATUALIZACAO BLOQUEIO BCA 723/2018 - AEMNPP22");
        
        ImmutableMap<Integer, Collection<ProcessoAdministrativoBCAWrapper>> arvoreMapeada 
            = getProcessosParaBloqueioEDesbloqueio(em, DetranCollectionUtil.montaLista(codigoAndamento));
        
        if(arvoreMapeada != null && !arvoreMapeada.isEmpty()) {
        
            Collection<ProcessoAdministrativoBCAWrapper> processos = arvoreMapeada.get(codigoAndamento);
        
            if (!DetranCollectionUtil.ehNuloOuVazio(processos)) {

                for (ProcessoAdministrativoBCAWrapper processoWrapper : processos) {

                    try {

                        processoWrapper.setSucessoBloqueio(Boolean.TRUE);
                        new PAAndamento240().executaEspecifico(em, new ExecucaoAndamentoEspecificoWrapper(processoWrapper.getProcessoAdministrativo()));

                    } catch (Exception e) {

                        LOG.debug("Erro ao executar Bloqueio através da integração.", e);

                        processoWrapper.setSucessoBloqueio(Boolean.FALSE);

                        getControleFalha()
                            .gravarFalhaProcessoAdministrativo(
                                e,
                                "Erro ao executar Bloqueio: " + processoWrapper.getCodigoPrograma(),
                                null,
                                processoWrapper.getProcessoAdministrativo().getNumeroProcesso()
                            );
                    }
                }
            }

            registrarExecucaoEGravarArquivoRelatorio(em, urlPathParaRelatorio, "AEMNPP21 - AEMNPP22", processos);
        }
        
        LOG.info("FIM ATUALIZACAO BLOQUEIO BCA 723/2018");
    }
}