package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.NotificacaoProcessoAdministrativoBO;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

public class PAAndamento40 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento40.class);

    /**
     * @param em
     * @param wrapper
     * @return
     * @throws AppException
     */
    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em,
                                                             ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 40.");

        RetornoExecucaoAndamentoWrapper retornoWrapper
                = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);

        NotificacaoProcessoAdministrativo notificacao
                = new NotificacaoProcessoAdministrativoBO()
                        .notificar(
                                em,
                                wrapper.getProcessoAdministrativo(), TipoFasePaEnum.ENTREGA_CNH);

        LOG.debug("Fim Andamento 40 - GERAR NOTIFICACAO ENTREGA CNH");

        return retornoWrapper;
    }

    /**
     * @param em
     * @param wrapper
     * @throws AppException
     */
    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    }

}
