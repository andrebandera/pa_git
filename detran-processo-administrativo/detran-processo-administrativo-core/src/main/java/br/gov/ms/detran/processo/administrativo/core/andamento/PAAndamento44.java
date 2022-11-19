/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.BloqueioBCABO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoRascunhoBloqueioRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IBloqueioBCAService;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoRascunhoBloqueio;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRascunhoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ParametrosIntegracaoBloqueioBCAWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ConsultaCnhControleRecolhimentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ConsultaCnhControleRecolhimento;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento44 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento45.class);

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento44.");

        IBloqueioBCAService bloqueioService = ServiceJndiLocator.<IBloqueioBCAService>lookup("ejb/BloqueioBCAService");

        ParametrosIntegracaoBloqueioBCAWrapper integracaoWrapper = new BloqueioBCABO().montarParametrosIntegracaoBloqueioBCAWrapper(em, wrapper.getProcessoAdministrativo());

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

        bloqueioService.executarAEMNPP13(integracaoWrapper, false);
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    private boolean processoPossuiPrioridadeValida(ProcessoRascunhoBloqueio rascunhoDoPa, EntityManager em) throws AppException {
        if (rascunhoDoPa.getPrioridade() > 1) {
            List<ProcessoRascunhoBloqueio> rascunhosPorBloqueioPessoa
                    = new ProcessoRascunhoBloqueioRepositorio().getRascunhosBloqueadosPorBloqueioPessoa(em, rascunhoDoPa.getProcessoBloqueioPessoa().getId());

            boolean existeRascunhoPrioritario
                    = rascunhosPorBloqueioPessoa.stream().
                            filter(item -> item.getPrioridade() < rascunhoDoPa.getPrioridade()).
                            anyMatch(item -> (SituacaoRascunhoBloqueioEnum.NAO_BLOQUEADO.equals(item.getSituacao())
                            || SituacaoRascunhoBloqueioEnum.BLOQUEADO_BL.equals(item.getSituacao())));

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
        if (processoAdministrativo.getTipo().equals(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH)) {
            return false;
        }
        if (processoAdministrativo.isJuridico()) {
            List<ConsultaCnhControleRecolhimento> recolhimentoCnh
                    = new ConsultaCnhControleRecolhimentoRepositorio().buscarRecolhimentoCnh(em, processoAdministrativo.getId());

            DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, processoAdministrativo.getId());

            if (BooleanEnum.SIM.equals(pju.getIndicativoPrazoIndeterminado()) || Objects.isNull(recolhimentoCnh)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    }
}
