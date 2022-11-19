/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource.job;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.scheduler.ejbtimer.JobWrapper;
import br.gov.ms.detran.comum.scheduler.ejbtimer.job.DetranAbstractJob;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.ace.IAcessoService;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_REALIZACAO_PROVA_CURSO;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import java.util.List;

/**
 *
 * @author Lillydi.
 */
public class PAJobAtualizarInfoProvaPJUCartorio extends DetranAbstractJob {

    private static final Logger LOG = Logger.getLogger(PAJobAtualizarInfoProvaPJUCartorio.class);
    
    private IProcessoAdministrativoService processoAdministrativoService;

    IPAControleFalhaService falhaService;
    
    private IAcessoService acessoService;
    

    public IProcessoAdministrativoService getProcessoAdministrativoService() {

        if (processoAdministrativoService == null) {
            processoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }

        return processoAdministrativoService;
    }
    
    public IPAControleFalhaService getFalhaService() {
        
        if (falhaService == null) {
            falhaService = (IPAControleFalhaService) JNDIUtil.lookup("ejb/PAControleFalhaService");
        }
        
        return falhaService;
    }
    
    public IAcessoService getAcessoService() {

        if (acessoService == null) {
            acessoService = (IAcessoService) JNDIUtil.lookup("ejb/AcessoService");
        }

        return acessoService;
    }

    @Override
    protected void doJob(JobWrapper job) {

        try {
            
            LOG.info("Inicio Entrega Cnh.");
            
            
            Usuario usuarioDetran = (Usuario) getAcessoService().getUsuarioPorLogin("DETRAN");
            
            List<ProcessoAdministrativo> listaProcessos = getProcessoAdministrativoService().
                                                            getListaProcessoJuridicoAndamentoERecolhimentoCnh(
                                                                        DetranCollectionUtil.
                                                                                montaLista(AGUARDAR_REALIZACAO_PROVA_CURSO)
                                                            );
            
            if (!DetranCollectionUtil.ehNuloOuVazio(listaProcessos)) {
                
                for (ProcessoAdministrativo processo : listaProcessos) {
                    
                    try {
                    
                        getProcessoAdministrativoService().obterInformacoesProvaParaPJUCartorio(processo, usuarioDetran.getId());

                        new ExecucaoAndamentoManager()
                                .iniciaExecucao(
                                        new ExecucaoAndamentoEspecificoWrapper(
                                                processo,
                                                usuarioDetran.getId(),
                                                null,
                                                null
                                        )
                                );

                    } catch (AppException e) {
                        LOG.debug("Tratado.", e);
                    }
                }
            }
            
        } catch (AppException ex) {
            LOG.debug("Tratado.", ex);
            getFalhaService().gravarFalha(ex, "Erro ao executar Retorno AR");
        }
    }
}