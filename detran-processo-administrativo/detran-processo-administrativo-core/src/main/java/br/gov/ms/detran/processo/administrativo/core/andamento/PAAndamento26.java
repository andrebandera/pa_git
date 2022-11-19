package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.bo.NotificacaoProcessoAdministrativoBO;
import br.gov.ms.detran.processo.administrativo.core.bo.recursoonline.RecursoOnlineBO;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;

import javax.persistence.EntityManager;

public class PAAndamento26 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento26.class);

    private IApoioService apoioService;

    public IApoioService getApoioService() {

        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }

        return apoioService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em,
                                                             ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 26.");

        RetornoExecucaoAndamentoWrapper retornoWrapper
                = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);

        NotificaProcessoAdministrativoWrapper entradaWrapper = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();

        NotificacaoProcessoAdministrativo notificacao = 
                new NotificacaoProcessoAdministrativoBO().notificar(em, wrapper.getProcessoAdministrativo(), TipoFasePaEnum.PENALIZACAO);

        new NotificacaoProcessoAdministrativoBO().gravarNotificacaoComplemento(em, entradaWrapper, notificacao);

        return retornoWrapper;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        NotificaProcessoAdministrativoWrapper entradaWrapper = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();

        if (entradaWrapper == null
            || DetranStringUtil.ehBrancoOuNulo(entradaWrapper.getNumeroProcesso())
            || entradaWrapper.getTipo() == null) {

            DetranWebUtils.applicationMessageException("Número Processo e tipo da Notificação são obrigatórios.");

        }

        if (!TipoFasePaEnum.PENALIZACAO.equals(entradaWrapper.getTipo())) {
            DetranWebUtils.applicationMessageException("Tipo da Notificação inválido.");
        }

        if (wrapper.getProcessoAdministrativo().isPontuacao()
                && (entradaWrapper.getDataPublicacaoPortaria() == null || DetranStringUtil.ehBrancoOuNulo(entradaWrapper.getNumeroPortaria()))) {

            DetranWebUtils.applicationMessageException("Número Portaria e sua data de publicação são obrigatórios.");
        }

        new RecursoOnlineBO().validarRecursoOnlineEmBackOffice(em, wrapper.getProcessoAdministrativo());
    }
}
