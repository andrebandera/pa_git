package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.bo.NotificacaoProcessoAdministrativoBO;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import javax.persistence.EntityManager;

public class PAAndamento206 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento206.class);

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

        LOG.debug("Início Andamento 206.");

        RetornoExecucaoAndamentoWrapper retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);

        NotificaProcessoAdministrativoWrapper entradaWrapper = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();

        new NotificacaoProcessoAdministrativoBO().notificarJARI(em, entradaWrapper, wrapper.getProcessoAdministrativo(), TipoFasePaEnum.JARI);

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
    }
}
