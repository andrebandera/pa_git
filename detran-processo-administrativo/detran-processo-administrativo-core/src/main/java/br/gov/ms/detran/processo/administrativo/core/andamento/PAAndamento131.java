/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.DesistenciaEntregaCnhBO;
import br.gov.ms.detran.processo.administrativo.core.bo.NotificacaoProcessoAdministrativoBO;
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
public class PAAndamento131 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento131.class);

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(
            EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        RetornoExecucaoAndamentoWrapper retornoWrapper = new RetornoExecucaoAndamentoWrapper();

        LOG.debug("Início Andamento 131.");

        retornoWrapper = new DesistenciaEntregaCnhBO().definirDesistenteEntregaCnh(em, wrapper.getProcessoAdministrativo());

        if (retornoWrapper == null) {

            NotificacaoProcessoAdministrativo notificacao
                    = new NotificacaoProcessoAdministrativoRepositorio()
                            .getNotificacaoPorProcessoAdministrativoETipoNotificacao(
                                    em,
                                    wrapper.getProcessoAdministrativo().getId(),
                                    TipoFasePaEnum.ENTREGA_CNH
                            );

            if (notificacao == null) {
                DetranWebUtils.applicationMessageException("Notificação não encontrada.");
            }

            new NotificacaoProcessoAdministrativoBO()
                    .gerarArquivoLab(
                            em,
                            wrapper.getUrlBaseBirt(),
                            notificacao
                    );

            retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
        }

        return retornoWrapper;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    }
}
