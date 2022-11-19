/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP12BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentacaoInfracaoPARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentacaoPARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ResultadoRecursoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.Movimentacao;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoRecurso;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoMotivoEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoNotificacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.ResultadoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.MovimentacaoPaWSWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento159 extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento159.class);
    
    private IApoioService apoioService;
    
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 159.");
        
        List<ProcessoAdministrativoInfracao> listaInfracoes = new ArrayList<>();
        
        MovimentacaoPaWSWrapper entrada = (MovimentacaoPaWSWrapper) wrapper.getObjetoWrapper();
        
        getApoioService().desativarEcriarNovaSituacaoAtendimentoCancelado(wrapper.getProcessoAdministrativo().getAtendimento());
        
        Movimentacao movimentacao 
                = new MovimentacaoPARepositorio().
                        getUltimaMovimentacaoAtivaPorProcessoAdministrativo(em, 
                                                                           wrapper.getProcessoAdministrativo().getId());
        
        if (!wrapper.getProcessoAdministrativo().isJuridico()) {
                            
                            if (null != movimentacao) {

                                if (movimentacao.getMovimentacaoAcao().equals(entrada.getAcao())
                                        && movimentacao.getMotivo().equals(entrada.getMotivo())) {

                                    listaInfracoes = new MovimentacaoInfracaoPARepositorio().
                                            getInfracoesMovimentacaoParaReativar(em, wrapper.getProcessoAdministrativo().getId());

                                } else {
                                    DetranWebUtils.applicationMessageException("Registro de movimentação com ação diferente da passada pelo WS.");
                                }

                            } else {
                                
                                listaInfracoes = new ProcessoAdministrativoInfracaoRepositorio().
                                        getInfracoesPorProcessoAdministrativoID(em, wrapper.getProcessoAdministrativo().getId());
                                
                                desativarInfracoesDoProcesso(em, listaInfracoes);
                                
                            }

                            new AEMNPP12BO().
                                prepararExecutarAEMNPP12(listaInfracoes, wrapper.getProcessoAdministrativo());
                        }
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        MovimentacaoPaWSWrapper movimentacaoWrapper = (MovimentacaoPaWSWrapper) wrapper.getObjetoWrapper();

        if (movimentacaoWrapper != null
            && MovimentacaoAcaoEnum.CANCELAR.equals(movimentacaoWrapper.getAcao())) {

            Movimentacao movimentacao
                    = new MovimentacaoPARepositorio().
                            getMovimentacaoPorProcessoAdministrativoEAcaoAtivo(em,
                                                                               wrapper.getProcessoAdministrativo().getId(),
                                                                               MovimentacaoAcaoEnum.CANCELAR);
            
            if (MovimentacaoMotivoEnum.MOTIVO_ADMINISTRATIVO.equals(movimentacaoWrapper.getMotivo())) {

                ResultadoRecurso resultado = new ResultadoRecursoRepositorio().
                                                    getResultadoRecursoPorProcessoEResultadoEAcao(em,
                                                                                                  wrapper.getProcessoAdministrativo().getId(),
                                                                                                  ResultadoRecursoEnum.IRREGULAR,
                                                                                                  RecursoNotificacaoAcaoEnum.CANCELAMENTO);
                if (resultado != null) {
                    if (movimentacao != null) {
                        DetranWebUtils.applicationMessageException("Existe movimentação ativa para o processo.");
                    }
                } else {
                    if (movimentacao == null) {
                        DetranWebUtils.applicationMessageException("Não existe movimentação para este PA.");
                    }
                }
            } else if (MovimentacaoMotivoEnum.CADASTRAMENTO_INDEVIDO.equals(movimentacaoWrapper.getMotivo())) {
                if(movimentacao == null)
                    DetranWebUtils.applicationMessageException("Não existe movimentação para este PA.");
            } else {
                DetranWebUtils.applicationMessageException("Parâmetros inválidos.");
            }
        } else {
            DetranWebUtils.applicationMessageException("Parâmetros inválidos.");
        }
    }

    private void desativarInfracoesDoProcesso(EntityManager em, List<ProcessoAdministrativoInfracao> infracoes) throws AppException{
        ProcessoAdministrativoInfracaoRepositorio infracaoRepositorio = new ProcessoAdministrativoInfracaoRepositorio();
        
        for (ProcessoAdministrativoInfracao infracao : infracoes) {
            infracao.setAtivo(AtivoEnum.DESATIVADO);
            infracaoRepositorio.update(em, infracao);
        }
    }
}
