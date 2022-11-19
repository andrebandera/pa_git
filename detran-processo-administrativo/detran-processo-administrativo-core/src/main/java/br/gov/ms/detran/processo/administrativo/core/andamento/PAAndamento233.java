/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.NotificacaoProcessoAdministrativoBO;
import br.gov.ms.detran.processo.administrativo.core.bo.recursoonline.RecursoOnlineBO;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;

import javax.persistence.EntityManager;

import static br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum.PENALIZACAO;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento233 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento233.class);

    @Override
    RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {


        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        new NotificacaoProcessoAdministrativoBO().validarNotificacaoDataLimiteExpirada(em, wrapper, PENALIZACAO);

        new RecursoOnlineBO().validarRecursoOnlineEmBackOffice(em, wrapper.getProcessoAdministrativo());
    }

}
