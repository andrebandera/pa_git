/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoServicoBO;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento116 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento116.class);

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {

        if (null == andamentoEspecificoWrapper
            || andamentoEspecificoWrapper.getProcessoAdministrativo() == null
            || DetranStringUtil.ehBrancoOuNulo(andamentoEspecificoWrapper.getNumeroProcesso())) {

            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 116.");

        new ProcessoAdministrativoServicoBO().geraEgravaArquivoJson(em, wrapper.getProcessoAdministrativo());

        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);

    }
}
