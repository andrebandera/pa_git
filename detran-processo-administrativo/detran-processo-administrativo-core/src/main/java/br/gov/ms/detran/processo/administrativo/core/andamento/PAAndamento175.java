/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.bo.MovimentacaoPABO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP12BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentacaoPARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.Movimentacao;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoMotivoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.MovimentacaoPaWSWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento175 extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento175.class);
    
    private IApoioService apoioService;
    
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 175.");
        
        getApoioService().desativarEcriarNovaSituacaoAtendimentoFechadoNaoConcluido(wrapper.getProcessoAdministrativo().getAtendimento());
        
        if (!wrapper.getProcessoAdministrativo().isJuridico())
            new AEMNPP12BO().
                    prepararExecutarAEMNPP12(new ProcessoAdministrativoInfracaoRepositorio().
                                                        getInfracoesPorProcessoAdministrativoID(em, wrapper.getProcessoAdministrativo().getId()), 
                                             wrapper.getProcessoAdministrativo());
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    
        MovimentacaoPaWSWrapper movimentacaoWrapper = (MovimentacaoPaWSWrapper) wrapper.getObjetoWrapper();
        
        if(movimentacaoWrapper == null
                || !MovimentacaoAcaoEnum.ARQUIVAR.equals(movimentacaoWrapper.getAcao())
                || !MovimentacaoMotivoEnum.PROCESSO_APENSADO.equals(movimentacaoWrapper.getMotivo())){
            DetranWebUtils.applicationMessageException("Parâmetros de entrada inválidos.");
        }
//        
         Movimentacao movimentacao 
                = new MovimentacaoPARepositorio().
                        getUltimaMovimentacaoAtivaPorProcessoAdministrativo(em, 
                                                                           wrapper.getProcessoAdministrativo().getId());
        if(movimentacao == null
                || !movimentacao.getMovimentacaoAcao().equals(movimentacaoWrapper.getAcao())
                || !movimentacao.getMotivo().equals(movimentacaoWrapper.getMotivo())){
            DetranWebUtils.applicationMessageException("Parâmetros de entrada inválidos.");
        }
        
        new MovimentacaoPABO().validarPAApensado(em, 
                                                 wrapper.getProcessoAdministrativo(), 
                                                 movimentacaoWrapper.getAcao(), 
                                                 movimentacaoWrapper.getMotivo());
    }
    
}
