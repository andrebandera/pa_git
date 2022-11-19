/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento243 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento243.class);

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 243.");
        
        RetornoExecucaoAndamentoWrapper retorno = new RetornoExecucaoAndamentoWrapper();

        ProcessoAdministrativo processoAdministrativo   = wrapper.getProcessoAdministrativo();
        TipoFasePaEnum tipoNotificacao                  = (TipoFasePaEnum) wrapper.getObjetoWrapper();
        
        try {
            
            NotificacaoProcessoAdministrativo notificacao 
                = getProcessoAdministrativoService().getNotificacaoPorNumeroProcessoETipo(
                    processoAdministrativo.getNumeroProcesso(), 
                    tipoNotificacao
                );
                    
            if(notificacao == null) {
                getProcessoAdministrativoService().notificar(processoAdministrativo, tipoNotificacao);
            }
            
            notificacao 
                = getProcessoAdministrativoService().getNotificacaoPorNumeroProcessoETipo(
                    processoAdministrativo.getNumeroProcesso(), 
                    tipoNotificacao
                );
            
            getProcessoAdministrativoService().gerarArquivoNotificacaoParaTipoArquivamentoIndevido(wrapper.getUrlBaseBirt(), notificacao);

            notificacao 
                = getProcessoAdministrativoService().getNotificacaoPorNumeroProcessoETipo(
                    processoAdministrativo.getNumeroProcesso(), 
                    tipoNotificacao
                );
            
            wrapper.setObjetoWrapper(notificacao);
            
            retorno.setProcessoAdministrativo(processoAdministrativo);

        } catch (Exception e) {

            LOG.debug("Erro ao executar emissão da notificacão de arquivamento indevido.", e);

            getFalhaService()
                .gravarFalhaProcessoAdministrativo(
                        e,
                        "Andamento 243 - Erro ao executar emissão da notificacão de arquivamento indevido",
                        null,
                        processoAdministrativo.getNumeroProcesso()
                );
        }

        LOG.debug("Fim Andamento 243.");

        return retorno;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

    }
    
}