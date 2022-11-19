/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.ATUALIZAR_BLOQUEIO_BCA_RESOLUCAO_723_2018_VARIOS_PROCESSOS;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusHistoricoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAStatusAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IBloqueioBCAService;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatusHistorico;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcedureProcessoAdministrativoBloqueio;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento241 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento241.class);

    private IBloqueioBCAService bloqueioService;

    public IBloqueioBCAService getBloqueioService() {

        if (bloqueioService == null) {
            bloqueioService = ServiceJndiLocator.<IBloqueioBCAService>lookup("ejb/BloqueioBCAService");
        }

        return bloqueioService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 241.");

        ProcessoAdministrativo processoAdministrativo = wrapper.getProcessoAdministrativo();
        
        try {
            
            List<ProcedureProcessoAdministrativoBloqueio> lProcessoAdministrativoBloqueio 
                = getProcessoAdministrativoService().executaProcedureProcessoAdministrativoBloqueio(wrapper.getProcessoAdministrativo().getCpf(), processoAdministrativo.getId());
                    
            if(DetranCollectionUtil.ehNuloOuVazio(lProcessoAdministrativoBloqueio) || lProcessoAdministrativoBloqueio.size() > 1) {
                DetranWebUtils.applicationMessageException("Processo Administrativo {0} falhou na atualizacao de bloqueio para andamento 241.", null, processoAdministrativo.getId().toString());
            }
                
            for (ProcedureProcessoAdministrativoBloqueio procedureProcessoAdministrativoBloqueio : lProcessoAdministrativoBloqueio) {

                try {

                    LOG.debug("Processo Administrativo ID {0}", procedureProcessoAdministrativoBloqueio.getIdProcesso());

                    retornaProcessoAdministrativoParaAndamentoAnterior(em, processoAdministrativo);

                } catch (Exception e) {

                    LOG.debug("Erro ao executar Bloqueio.", e);

                    getFalhaService()
                        .gravarFalhaProcessoAdministrativo(
                                e,
                                "Andamento 241 - Erro ao executar Bloqueio",
                                processoAdministrativo.getCpf(),
                                processoAdministrativo.getNumeroProcesso()
                        );
                }
            }
            
            return new RetornoExecucaoAndamentoWrapper(processoAdministrativo);

        } catch (Exception e) {

            LOG.debug("Erro ao executar Bloqueio através da integração.", e);

            getFalhaService()
                .gravarFalhaProcessoAdministrativo(
                        e,
                        "Andamento 241 - Erro ao executar Bloqueio",
                        null,
                        processoAdministrativo.getNumeroProcesso()
                );
        }

        LOG.debug("Fim Andamento 241.");

        return null;
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
                .getPAStatusAndamentoAtivoPorStatusEAndamento(em, ATUALIZAR_BLOQUEIO_BCA_RESOLUCAO_723_2018_VARIOS_PROCESSOS);
        
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
