/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource.job;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.scheduler.ejbtimer.JobWrapper;
import br.gov.ms.detran.comum.scheduler.ejbtimer.job.DetranAbstractJob;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.core.iface.servico.ace.IAcessoService;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcessoEspecifico;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 *
 * @author desenvolvimento
 */
public class PAJobArquivarProcessosPrescritos extends DetranAbstractJob {
    
     private IProcessoAdministrativoService processoAdministrativoService;
     
     private IPAControleFalhaService paControleFalha;
     
     private IAcessoService acessoService;
    
    public IAcessoService getAcessoService() {

        if (acessoService == null) {
            acessoService = (IAcessoService) JNDIUtil.lookup("ejb/AcessoService");
        }

        return acessoService;
    }
    
    public IProcessoAdministrativoService getProcessoAdministrativoService() {

        if (processoAdministrativoService == null) {
            processoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }

        return processoAdministrativoService;
    }
    
    public IPAControleFalhaService getPAControleFalha() {
        
        if (paControleFalha == null) {
            paControleFalha = (IPAControleFalhaService) JNDIUtil.lookup("ejb/PAControleFalhaService");
        }
        
        return paControleFalha;
    }

    @Override
    protected void doJob(JobWrapper job) {
        
        try {
            Usuario usuarioDetran = (Usuario) getAcessoService().getUsuarioPorLogin("DETRAN");
            
            UsernamePasswordAuthenticationToken preAuthenticatedToken = new UsernamePasswordAuthenticationToken("DETRAN-WEB", null);
            
            SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
            SecurityContextHolder.getContext().setAuthentication(preAuthenticatedToken);

            getProcessoAdministrativoService().preArquivarProcessosPrescritos();
            
            List<PAAndamentoProcessoEspecifico> processosEspecificos = getProcessoAdministrativoService().buscarProcessosEspecificos();
            
            if(!DetranCollectionUtil.ehNuloOuVazio(processosEspecificos)){
                for (PAAndamentoProcessoEspecifico processoEspecifico : processosEspecificos) {
                    try{
                    
                        ProcessoAdministrativo processoAdministrativo = 
                                    getProcessoAdministrativoService().
                                                getProcessoAdministrativo(processoEspecifico.getIdProcessoAdministrativo());
                    
                    if(processoAdministrativo == null){
                        DetranWebUtils.applicationMessageException("Processo Administrativo não encontrado");
                    }
                    
                    new ExecucaoAndamentoManager()
                            .iniciaExecucao(
                                        new ExecucaoAndamentoEspecificoWrapper(
                                                                                processoAdministrativo,
                                                                                usuarioDetran.getId(),
                                                                                null,
                                                                                processoEspecifico
                                                                              )
                                        );
                    
                    }catch(Exception e){
                        getPAControleFalha().gravarFalha(e, "Erro ao executar arquivamento processo prescrito");
                    }
                }
                
                /*
                Gerar relatório de erros e gravar na TEF.
                
                String urlBaseBirt = DetranReportsUtil.getReportsBaseBirtUrl(req, true);
                Map<String, String> params = new HashMap<>();
                
                String urlRelatorio
                    = DetranReportsUtil.getReportParamsBirtUrl(
                        "relatorioprescritoserro",
                        FormatoRelatorioEnum.PDF.getRptFormat(),
                        params,
                        "relatorios/processoadministrativo/"
                    );
                
                processoAdministrativoService.registraGravacaoProcessamento(
                    new ProcessoAdministrativoBCARelatorioTotalWrapper(), 
                    "PRESCRICAO", 
                    DetranHTTPUtil.download(urlBaseBirt + urlRelatorio)
                );
                */
            }
            
        } catch (Exception ex) {
            getPAControleFalha().gravarFalha(ex, "Erro ao executar Arquivamento de Processo Prescrito");
        }
    }
}
