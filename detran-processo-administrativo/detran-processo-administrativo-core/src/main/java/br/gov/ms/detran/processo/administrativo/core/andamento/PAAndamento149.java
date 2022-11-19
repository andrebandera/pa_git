/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.TransferenciaNotificacoesBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento149  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento149.class);

    /**
     * 
     * @param em
     * @param wrapper
     * @return
     * @throws AppException 
     */
    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 149. PA {0}", wrapper.getProcessoAdministrativo().getNumeroProcesso());
        
        RetornoExecucaoAndamentoWrapper retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
        
            NotificacaoProcessoAdministrativo notificacao
                = new NotificacaoProcessoAdministrativoRepositorio()
                    .getNotificacaoPorNumeroProcessoETipo(
                        em, 
                        wrapper.getProcessoAdministrativo().getNumeroProcesso(), 
                        TipoFasePaEnum.PROVIMENTO
                    );
            
            new TransferenciaNotificacoesBO()
                .geraLinhaCorreiosParaNotificacaoLab(
                    em, 
                    notificacao
                );
            
        
        return retornoWrapper;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        new NotificacaoProcessoAdministrativoRepositorio()
            .existeNotificacaoAtivaPorProcessoAdministrativoETipoNotificacao(
                em, 
                wrapper.getProcessoAdministrativo().getId(),
                TipoFasePaEnum.PROVIMENTO
            );
    }
}