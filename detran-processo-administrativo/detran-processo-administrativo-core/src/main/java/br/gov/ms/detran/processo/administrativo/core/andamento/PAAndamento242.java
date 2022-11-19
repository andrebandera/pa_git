/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.ATUALIZACAO_BLOQUEIO_BCA;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusHistoricoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAStatusAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IBloqueioBCAService;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatusHistorico;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento242 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento242.class);

    private IBloqueioBCAService bloqueioService;

    public IBloqueioBCAService getBloqueioService() {

        if (bloqueioService == null) {
            bloqueioService = ServiceJndiLocator.<IBloqueioBCAService>lookup("ejb/BloqueioBCAService");
        }

        return bloqueioService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 242.");

        ProcessoAdministrativo processoAdministrativo = wrapper.getProcessoAdministrativo();

        try {

            LOG.debug("Processo Administrativo ID {0}", wrapper.getProcessoAdministrativo().getId());

            retornaProcessoAdministrativoParaAndamentoAnterior(em, processoAdministrativo);

        } catch (Exception e) {

            LOG.debug("Erro ao executar Bloqueio.", e);

            getFalhaService()
                .gravarFalhaProcessoAdministrativo(
                    e,
                    "Andamento 242 - Erro ao executar Bloqueio",
                    processoAdministrativo.getCpf(),
                    processoAdministrativo.getNumeroProcesso()
                );
        }
            
        LOG.debug("Fim Andamento 242.");
        
        return new RetornoExecucaoAndamentoWrapper(processoAdministrativo);
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

    }
    
    /**
     * 
     * @param em
     * @param processoAdministrativo 
     */
    private void retornaProcessoAdministrativoParaAndamentoAnterior(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        PAStatusAndamento paStatusAndamentoAtual 
            = new PAStatusAndamentoRepositorio()
                .getPAStatusAndamentoAtivoPorStatusEAndamento(em, ATUALIZACAO_BLOQUEIO_BCA);
        
        PAOcorrenciaStatusHistorico paOcorrenciaStatusHistorico 
            = new PAOcorrenciaStatusHistoricoRepositorio().getUltimoHistoricoDiferentePAStatusAndamentoAtual(em, processoAdministrativo.getId(), paStatusAndamentoAtual.getId());

        PAStatusAndamento statusAndamento 
            = new PAStatusAndamentoRepositorio()
                .find(em, PAStatusAndamento.class, paOcorrenciaStatusHistorico.getIdStatusAndamento());
        
        PAFluxoProcesso fluxoProcesso 
            = new PAFluxoProcessoRepositorio()
                .find(em, PAFluxoProcesso.class, paOcorrenciaStatusHistorico.getIdFluxoProcesso());
        
        getProcessoAdministrativoService().alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(processoAdministrativo, fluxoProcesso.getCodigo(), statusAndamento.getAndamentoProcesso().getCodigo());
    }
}
