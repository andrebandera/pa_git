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
public class PAAndamento156  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento156.class);

    /**
     * 
     * @param em
     * @param wrapper
     * @return
     * @throws AppException 
     */
    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 156. PA {0}", wrapper.getProcessoAdministrativo().getNumeroProcesso());
        
        RetornoExecucaoAndamentoWrapper retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
        
        try {
            
            NotificacaoProcessoAdministrativo notificacao
                = new NotificacaoProcessoAdministrativoRepositorio()
                    .getNotificacaoPorNumeroProcessoETipo(
                        em, 
                        wrapper.getProcessoAdministrativo().getNumeroProcesso(), 
                        TipoFasePaEnum.NAO_CONHECIMENTO
                    );
            
            new TransferenciaNotificacoesBO()
                .geraLinhaCorreiosParaNotificacaoLab(
                    em, 
                    notificacao
                );
            
        } catch (Exception e) {
            
            LOG.debug("Andamento 156 - erro capturado.", e);
            
            getFalhaService()
                .gravarFalhaEspecifica(
                    wrapper.getProcessoAdministrativo().getCpf(),
                    "Processo Admnistrativo:" + wrapper.getProcessoAdministrativo().getNumeroProcesso(),
                    "PAAndamento156"
                );
            
            throw e;
            
        } finally {
            LOG.debug("Fim Andamento 156. PA {0}", wrapper.getProcessoAdministrativo().getNumeroProcesso());
        }
        
        return retornoWrapper;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        new NotificacaoProcessoAdministrativoRepositorio()
            .existeNotificacaoAtivaPorProcessoAdministrativoETipoNotificacao(
                em, 
                wrapper.getProcessoAdministrativo().getId(),
                TipoFasePaEnum.NAO_CONHECIMENTO
            );
    }
}