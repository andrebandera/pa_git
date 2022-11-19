package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ConsultaCnhControleRecolhimentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoRascunhoBloqueioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IBloqueioBCAService;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.ConsultaCnhControleRecolhimento;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoRascunhoBloqueio;
import br.gov.ms.detran.processo.administrativo.enums.IdentificacaoRecolhimentoCnhEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRascunhoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;

public class PAAndamento187 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento187.class);

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 187.");

        PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().
                getPenalidadePorPA(em, wrapper.getProcessoAdministrativo().getId());

        BloqueioBCA bloqueio = new BloqueioBCARepositorio().
                getBloqueioBcaPorPaEAtivo(em, wrapper.getProcessoAdministrativo().getId());

        DadoProcessoJudicial dadoJuridico
                = new DadoProcessoJudicialRepositorio().
                        getDadoProcessoJudicialPorPA(em, wrapper.getProcessoAdministrativo().getId());

        if (penalidade == null || bloqueio == null || dadoJuridico == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar penalidade, bloqueio ou dado judicial.");
        }

        atualizarRascunhoBloqueio(em, wrapper);
        IBloqueioBCAService bloqueioService = ServiceJndiLocator.<IBloqueioBCAService>lookup("ejb/BloqueioBCAService");
        bloqueioService.executarAEMNPP17(dadoJuridico, bloqueio, penalidade);

        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    private void atualizarRascunhoBloqueio(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        if (existeRascunhoParaAtualizar(em, wrapper.getProcessoAdministrativo())) {
            ProcessoRascunhoBloqueio rascunhoDoPa = new ProcessoRascunhoBloqueioRepositorio().getRascunhoPorProcessoAdministrativo(em, wrapper.getProcessoAdministrativo());

            if (Objects.isNull(rascunhoDoPa) || !SituacaoRascunhoBloqueioEnum.BLOQUEADO_BL.equals(rascunhoDoPa.getSituacao())) {
                DetranWebUtils.applicationMessageException("Processo não possui rascunho de bloqueio ou sua situação é diferente de Bloqueio por Entrega de CNH.");
            }

            if (!processoPossuiPrioridadeValida(rascunhoDoPa, em)) {
                DetranWebUtils.applicationMessageException("O condutor possui processo com prioridade menor que não foi cadastrado na Base Nacional.");
            }

            atualizarRascunhoBloqueio(rascunhoDoPa, em);
        }
    }

    private boolean processoPossuiPrioridadeValida(ProcessoRascunhoBloqueio rascunhoDoPa, EntityManager em) throws AppException {
        if (rascunhoDoPa.getPrioridade() > 1) {
            List<ProcessoRascunhoBloqueio> rascunhosPorBloqueioPessoa
                    = new ProcessoRascunhoBloqueioRepositorio().getRascunhosBloqueadosPorBloqueioPessoa(em, rascunhoDoPa.getProcessoBloqueioPessoa().getId());

            boolean existeRascunhoPrioritario
                    = rascunhosPorBloqueioPessoa.stream().
                            filter(item -> item.getPrioridade() < rascunhoDoPa.getPrioridade()).
                            anyMatch(item -> (SituacaoRascunhoBloqueioEnum.BLOQUEADO_BL.equals(item.getSituacao())));

            if (existeRascunhoPrioritario) {
                return false;
            }
        }
        return true;
    }

    private void atualizarRascunhoBloqueio(ProcessoRascunhoBloqueio rascunhoDoPa, EntityManager em) throws AppException {
        rascunhoDoPa.setSituacao(SituacaoRascunhoBloqueioEnum.BLOQUEADO_BCA);
        new ProcessoRascunhoBloqueioRepositorio().update(em, rascunhoDoPa);
    }

    private boolean existeRascunhoParaAtualizar(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        if (processoAdministrativo.isJuridico()) {
            List<ConsultaCnhControleRecolhimento> recolhimentoCnh
                    = new ConsultaCnhControleRecolhimentoRepositorio().buscarRecolhimentoCnh(em, processoAdministrativo.getId());

            DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, processoAdministrativo.getId());

            if ((!Objects.isNull(recolhimentoCnh) && !recolhimentoCnh.isEmpty())
                    || pju.getIdentificacaoRecolhimentoCnh().getCodigo().equals(IdentificacaoRecolhimentoCnhEnum.CARTORIO_JUDICIARIO.getCodigo())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

    }

}
