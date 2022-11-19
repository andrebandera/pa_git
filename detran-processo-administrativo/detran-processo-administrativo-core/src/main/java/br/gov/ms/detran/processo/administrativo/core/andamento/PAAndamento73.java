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
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentacaoPARepositorio;
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
public class PAAndamento73 extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento73.class);
    
    private IApoioService apoioService;
    
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 73.");
        
        getApoioService().desativarEcriarNovaSituacaoAtendimentoFechadoConcluido(wrapper.getProcessoAdministrativo().getAtendimento());
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    
        MovimentacaoPaWSWrapper movimentacaoWrapper = (MovimentacaoPaWSWrapper) wrapper.getObjetoWrapper();
        if(movimentacaoWrapper == null
                || !MovimentacaoAcaoEnum.ARQUIVAR.equals(movimentacaoWrapper.getAcao())
                || !MovimentacaoMotivoEnum.RECURSO_ACOLHIDO.equals(movimentacaoWrapper.getMotivo())){
            DetranWebUtils.applicationMessageException("Parâmetros de entrada inválidos.");
        }
        
         Movimentacao movimentacao 
                = new MovimentacaoPARepositorio().
                        getMovimentacaoPorProcessoAdministrativoEAcaoAtivo(em, 
                                                                           wrapper.getProcessoAdministrativo().getId(), 
                                                                           MovimentacaoAcaoEnum.ARQUIVAR);
        if(movimentacao != null){
            DetranWebUtils.applicationMessageException("Existe movimentação para este PA.");
        }
    }
}
