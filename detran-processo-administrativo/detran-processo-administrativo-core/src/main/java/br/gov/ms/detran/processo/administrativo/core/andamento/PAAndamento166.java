package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.bo.NotificacaoProcessoAdministrativoBO;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import javax.persistence.EntityManager;

public class PAAndamento166 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento166.class);

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

        LOG.debug("Início Andamento 166.");

        RetornoExecucaoAndamentoWrapper retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);

        NotificaProcessoAdministrativoWrapper entradaWrapper = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();

        NotificacaoProcessoAdministrativo notificacao = 
                new NotificacaoProcessoAdministrativoBO().notificar(em, wrapper.getProcessoAdministrativo(), TipoFasePaEnum.DESENTRANHAMENTO);
        
        if(entradaWrapper != null){
            new NotificacaoProcessoAdministrativoBO().gravarNotificacaoComplemento(em, entradaWrapper, notificacao);
        }

        return retornoWrapper;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    }
}
