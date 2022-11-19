package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.EditalProcessoAdministrativoBO;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.EditalWrapper;
import javax.persistence.EntityManager;

public class PAAndamento51 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento51.class);

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em,
                                                             ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 51.");

        RetornoExecucaoAndamentoWrapper retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                                                             PAFluxoProcessoConstante.CODIGO_PA_FLUXO_PROVIMENTO,
                                                                                             PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.CONFIRMAR_ARQUIVAMENTO_RECURSO_PROVIDO);

        new EditalProcessoAdministrativoBO().gravaEditalNotificacaoProcessoAdministrativo(em, (EditalWrapper) wrapper.getObjetoWrapper());

        return retornoWrapper;

    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        EditalWrapper entrada = (EditalWrapper) wrapper.getObjetoWrapper();

        new EditalProcessoAdministrativoBO().
                validaEntradaParaEditalNotificacaoProcessoAdministrativo(entrada);

        if (wrapper.getIdUsuario() == null) {
            DetranWebUtils.applicationMessageException("Usuário não informado.");
        }

        if (!TipoFasePaEnum.PROVIMENTO.equals(entrada.getTipo())) {
            DetranWebUtils.applicationMessageException("Não é possível gravar Edital no andamento Atual do PA.");
        }
    }
}
