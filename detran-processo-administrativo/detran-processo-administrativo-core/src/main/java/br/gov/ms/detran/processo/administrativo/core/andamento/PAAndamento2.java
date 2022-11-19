package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IBloqueioBCAService;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

public class PAAndamento2 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento2.class);

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em,
                                                             ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 2.");

        BloqueioBCA bloqueio
                = new BloqueioBCARepositorio()
                        .getBloqueioBcaPorPaEAtivo(em, wrapper.getProcessoAdministrativo().getId());

        DadoProcessoJudicial dadoJuridico
                = new DadoProcessoJudicialRepositorio()
                        .getDadoProcessoJudicialPorPA(em, wrapper.getProcessoAdministrativo().getId());

        if (bloqueio == null || dadoJuridico == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar penalidade, bloqueio ou dado judicial.");
        }

        IBloqueioBCAService bloqueioService = ServiceJndiLocator.<IBloqueioBCAService>lookup("ejb/BloqueioBCAService");
        bloqueioService.executarAEMNPP17(dadoJuridico, bloqueio, null);

        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    }
}
