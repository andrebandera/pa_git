/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.hab.IHabilitacaoService;
import br.gov.ms.detran.processo.administrativo.core.bo.BloqueioBCABO;
import br.gov.ms.detran.processo.administrativo.core.bo.PAPenalidadeProcessoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoRascunhoBloqueioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoRascunhoBloqueio;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRascunhoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoMovimentoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.Date;
import java.util.Objects;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento183 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento183.class);

    private IHabilitacaoService habilitacaoService;

    public IHabilitacaoService getHabilitacaoService() {

        if (habilitacaoService == null) {
            habilitacaoService = (IHabilitacaoService) JNDIUtil.lookup("ejb/HabilitacaoService");
        }

        return habilitacaoService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento183.");

        /**
         * Tem penalidade? SIM Tem Rascunho: Sim Tem Bloqueio? Talvez
         *
         * Se tiver bloqueio * Criar movimentação de alteração * Se Juridico * *
         * Alterar a data fim Bloqueio com a data fim da tea Senão *
         * Permissionado * * Cria bloqueio pela TEA * Demais * * Criar bloqueio
         * pelo rascunho * * Atualizar rascunho
         *
         */
        BloqueioBCARepositorio repo = new BloqueioBCARepositorio();
        BloqueioBCA bloqueio = repo.
                getBloqueioBCAporPaESituacaoEAtivo(em, wrapper.getProcessoAdministrativo().getId(), SituacaoBloqueioBCAEnum.ATIVO);

        PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().
                getPenalidadePorPA(em, wrapper.getProcessoAdministrativo().getId());

        Long idUsuario = wrapper.getIdUsuario();

        if (idUsuario == null && penalidade != null) {
            idUsuario = penalidade.getUsuario();
        }

        if (Objects.nonNull(bloqueio)) {
            new BloqueioBCABO().gravarMovimentoBloqueioBCA(em, bloqueio, idUsuario, TipoMovimentoBloqueioBCAEnum.ALTERACAO);

            if (wrapper.getProcessoAdministrativo().getTipo().equals(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH)) {

                bloqueio.setDataFim(penalidade.getDataFimPenalidade());
                repo.update(em, bloqueio);
            }
        } else {
            if (wrapper.getProcessoAdministrativo().getTipo().equals(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH)) {
                bloqueio = new BloqueioBCABO()
                        .gravarBloqueioParaProcesso(em,
                                wrapper.getProcessoAdministrativo(),
                                penalidade.getDataInicioPenalidade());
                new BloqueioBCABO().gravarMovimentoBloqueioBCA(em, bloqueio, idUsuario, TipoMovimentoBloqueioBCAEnum.BLOQUEIO);
            } else {
                DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, wrapper.getProcessoAdministrativo().getId());
                if(Objects.nonNull(pju) && pju.getIndicativoPrazoIndeterminado().equals(BooleanEnum.SIM)){
                    bloqueio = new BloqueioBCABO()
                        .gravarBloqueioParaProcesso(em,
                                wrapper.getProcessoAdministrativo(),
                                pju.getDataBloqueio());
                }else {
                    ProcessoRascunhoBloqueio rascunho = new ProcessoRascunhoBloqueioRepositorio().getRascunhoPorProcessoAdministrativo(em, wrapper.getProcessoAdministrativo());

                    bloqueio = new BloqueioBCABO()
                        .gravarBloqueioParaProcesso(em,
                                wrapper.getProcessoAdministrativo(),
                                rascunho.getDataInicio());
                    rascunho.setSituacao(SituacaoRascunhoBloqueioEnum.BLOQUEADO_BL);
                    new ProcessoRascunhoBloqueioRepositorio().update(em, rascunho);
                }
                new BloqueioBCABO().gravarMovimentoBloqueioBCA(em, bloqueio, idUsuario, TipoMovimentoBloqueioBCAEnum.BLOQUEIO);
            }
        }

        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);

    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    }

    private BloqueioBCA gravarBloqueioEPenalidade(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper, Date dataInicio) throws AppException {
        BloqueioBCA bloqueio = new BloqueioBCABO()
                .gravarBloqueioParaProcesso(em,
                        wrapper.getProcessoAdministrativo(),
                        dataInicio);

        new PAPenalidadeProcessoBO()
                .gravarPenalidadeControleCnh(em,
                        wrapper.getProcessoAdministrativo(),
                        wrapper.getIdUsuario(),
                        dataInicio);

        return bloqueio;
    }
}
