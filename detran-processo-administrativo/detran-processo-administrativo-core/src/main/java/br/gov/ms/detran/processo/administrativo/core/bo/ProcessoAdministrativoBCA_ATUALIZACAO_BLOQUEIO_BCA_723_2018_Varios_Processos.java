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
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento241;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.FunctionProcessoAdministrativoBloqueio;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoBCAWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

public class ProcessoAdministrativoBCA_ATUALIZACAO_BLOQUEIO_BCA_723_2018_Varios_Processos extends ProcessoAdministrativoBCABO implements IProcessoAdministrativoBCA {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoBCA_ATUALIZACAO_BLOQUEIO_BCA_723_2018_Varios_Processos.class);
    
    final Integer CODIGO_FLUXO_PROCESSO_ANDAMENTO_241 = 52;
    final Integer CODIGO_ANDAMENTO_241 = 241;
    
    final Integer EXECUTAR_EM_BLOCOS_DE_CONDUTORES = 100;
    
    @Override
    public void executa(EntityManager em, String urlPathParaRelatorio, Integer codigoAndamento) {
        
        try {
            
            LOG.info("INICIO ATUALIZACAO BLOQUEIO BCA 723/2018 - Varios Processos");
            
            List<FunctionProcessoAdministrativoBloqueio> lProcessoAdministrativoBloqueioPorCPF 
                = new BloqueioBCARepositorio().getFunctionProcessoAdministrativoBloqueio(em, null);
            
            if(!DetranCollectionUtil.ehNuloOuVazio(lProcessoAdministrativoBloqueioPorCPF)) {

                ImmutableMap<String, List<FunctionProcessoAdministrativoBloqueio>> arvoreMapeada = getProcessosPorCPF(lProcessoAdministrativoBloqueioPorCPF);

                if(arvoreMapeada != null && !arvoreMapeada.isEmpty()) {
                    
                    List<List<String>> lBlocosCondutores = DetranCollectionUtil.particionarLista(arvoreMapeada.keySet().asList(), EXECUTAR_EM_BLOCOS_DE_CONDUTORES);
                    
                    for (List<String> blocoCondutores : lBlocosCondutores) {
                        
                        Collection<ProcessoAdministrativoBCAWrapper> lProcessoAdministrativoTotalPorBloco = new ArrayList();
                        
                        for (String cpfCondutor : blocoCondutores) {
                            
                            Collection<ProcessoAdministrativoBCAWrapper> lProcessoAdministrativoWrapper = alteraAndamentoProcessoParaAtenderRegraNegocioBloqueio723(em, cpfCondutor, arvoreMapeada.get(cpfCondutor));
                            
                            if(DetranCollectionUtil.ehNuloOuVazio(lProcessoAdministrativoWrapper)) {
                                DetranWebUtils.applicationMessageException("CPF ou Processo Administrativo não encontrado para execucao do andamento 241.");
                            }

                            for (ProcessoAdministrativoBCAWrapper processoWrapper : lProcessoAdministrativoWrapper) {

                                try {

                                    processoWrapper.setSucessoBloqueio(Boolean.TRUE);

                                    RetornoExecucaoAndamentoWrapper retorno 
                                        = new PAAndamento241().executaEspecifico(em, new ExecucaoAndamentoEspecificoWrapper(processoWrapper.getProcessoAdministrativo()));

                                    if(retorno == null) {
                                        DetranWebUtils.applicationMessageException("Processo Administrativo {0} falhou na atualizacao de bloqueio web para andamento 241.", null, processoWrapper.getProcessoAdministrativo().getId().toString());
                                    }
                                    
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
                                } finally {
                                    lProcessoAdministrativoTotalPorBloco.add(processoWrapper);
                                }
                            }
                        }
                        
                        getProcessoAdministrativoService()
                            .registrarExecucaoEGravarArquivoRelatorio(urlPathParaRelatorio, "Andamento_241", lProcessoAdministrativoTotalPorBloco);
                    }
                }
            }
                        
            LOG.info("FIM ATUALIZACAO BLOQUEIO BCA 723/2018 - Varios Processos");
            
        } catch (Exception e) {
            LOG.error("Erro ao executar Bloqueio através da integração.", e);
            getControleFalha().gravarFalhaProcessoAdministrativo(e, "Erro ao executar Bloqueio.", null, null);
        }
    }

    /**
     * 
     * @param em
     * @param cpfCondutor
     * @param lProcesso
     * @return
     * @throws AppException 
     */
    private Collection<ProcessoAdministrativoBCAWrapper> alteraAndamentoProcessoParaAtenderRegraNegocioBloqueio723(EntityManager em, String cpfCondutor, Collection<FunctionProcessoAdministrativoBloqueio> lProcesso) throws AppException {
        
        Collection<ProcessoAdministrativoBCAWrapper> lProcessoAdministrativoWrapper = new ArrayList();
        
        ProcessoAdministrativoRepositorio processoAdministrativoRepositorio = new ProcessoAdministrativoRepositorio(); 

        try {

            for (FunctionProcessoAdministrativoBloqueio functionProcessoAdministrativoBloqueio : lProcesso) {

                try {

                    ProcessoAdministrativoBCAWrapper processoAdministrativoBCAWrapper 
                        = processoAdministrativoRepositorio
                            .getProcessoAdministrativoParaBCAPorCPFeProcessoAdministrativo(
                                em, 
                                cpfCondutor, 
                                functionProcessoAdministrativoBloqueio.getIdProcesso()
                            );

                    if(processoAdministrativoBCAWrapper != null) {

                        getProcessoAdministrativoService()
                            .alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(
                                processoAdministrativoBCAWrapper.getProcessoAdministrativo(), 
                                CODIGO_FLUXO_PROCESSO_ANDAMENTO_241, 
                                CODIGO_ANDAMENTO_241
                            );

                        processoAdministrativoBCAWrapper.setCodigoPrograma("Andamento_241");

                        lProcessoAdministrativoWrapper.add(processoAdministrativoBCAWrapper);
                    }

                } catch (Exception e) {

                    LOG.debug("Erro ao alterar andamento.", e);

                    getControleFalha()
                        .gravarFalhaProcessoAdministrativo(
                            e,
                            "Alterar andamento processo: " + functionProcessoAdministrativoBloqueio.getNumeroProcesso(),
                            null,
                            null
                        );
                }
            }

        } catch (Exception e) {

            LOG.debug("Erro ao alterar andamento.", e);

            getControleFalha()
                .gravarFalhaProcessoAdministrativo(
                    e,
                    "Impedimento ao executar Bloqueio Web para CPF: " + cpfCondutor,
                    null,
                    null
                );
        }
        
        return lProcessoAdministrativoWrapper;
    }
            
    
    /**
     * 
     * @param processos
     * @return
     * @throws AppException 
     */
    private ImmutableMap getProcessosPorCPF(List<FunctionProcessoAdministrativoBloqueio> processos) throws AppException {
        
        ImmutableListMultimap<String, FunctionProcessoAdministrativoBloqueio> listaMapeada = null;
        
        if(!DetranCollectionUtil.ehNuloOuVazio(processos)) {
            
            listaMapeada
                = Multimaps.index(processos, new Function<FunctionProcessoAdministrativoBloqueio, String>() {
                    @Override
                    public String apply(FunctionProcessoAdministrativoBloqueio input) {
                        return input.getCpf();
                    }
                });
        }
        
        return listaMapeada != null && !listaMapeada.isEmpty() ? listaMapeada.asMap() : null;
    }
}