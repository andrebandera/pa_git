package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.NotificacaoProcessoAdministrativoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

public class PAAndamento167 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento167.class);

    @Override
    RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        RetornoExecucaoAndamentoWrapper retornoWrapper = new RetornoExecucaoAndamentoWrapper();

        LOG.debug("Início Andamento 167.");

        NotificacaoProcessoAdministrativo notificacao
                = new NotificacaoProcessoAdministrativoRepositorio()
                        .getNotificacaoPorNumeroProcessoETipo(em,
                                                              wrapper.getProcessoAdministrativo().getNumeroProcesso(),
                                                              TipoFasePaEnum.DESENTRANHAMENTO);

        if (notificacao == null) {
            DetranWebUtils.applicationMessageException("Notificação não encontrada.");
        }

        new NotificacaoProcessoAdministrativoBO().gerarArquivoLab(em, wrapper.getUrlBaseBirt(), notificacao);

        retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);

        return retornoWrapper;
    }

    @Override
    void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

    }
}
