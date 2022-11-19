/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoNotificacaoEnum;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.projeto.constantes.ParametrizacaoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.core.iface.servico.hab.IHabilitacaoService;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.MovimentoCnhBO;
import br.gov.ms.detran.processo.administrativo.core.bo.PAPenalidadeProcessoBO;
import br.gov.ms.detran.processo.administrativo.core.bo.ProtocoloBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoCnhRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoRascunhoBloqueioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProtocoloRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoRascunhoBloqueio;
import br.gov.ms.detran.processo.administrativo.entidade.Protocolo;
import br.gov.ms.detran.processo.administrativo.entidade.RegraInstaurarEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento48 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento48.class);

    private IHabilitacaoService habilitacaoService;

    public IHabilitacaoService getHabilitacaoService() {

        if (habilitacaoService == null) {
            habilitacaoService = (IHabilitacaoService) JNDIUtil.lookup("ejb/HabilitacaoService");
        }

        return habilitacaoService;
    }

    IApoioService apoioService;

    public IApoioService getApoioService() {

        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }

        return apoioService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 48.");

        RetornoExecucaoAndamentoWrapper retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);

        ProcessoAdministrativo processoOriginal = null;

        Date dataAtual = Calendar.getInstance().getTime();

        CnhSituacaoEntrega entrega
                = (CnhSituacaoEntrega) getHabilitacaoService().
                        getCnhSituacaoEntregaSemAcaoDeDevolucaoDaCnhPorNumeroDetran(wrapper.getProcessoAdministrativo().getNumeroDetran());

        if (entrega == null) {
            validarPrazoPermissionado(wrapper, em);
            PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, wrapper.getProcessoAdministrativo().getId());

            if (penalidade == null) {
                DetranWebUtils.applicationMessageException("Não foi possível encontrar penalidade para o processo.");
            }

            if (penalidade.getDataFimPenalidade().after(dataAtual)) {
                DetranWebUtils.applicationMessageException("A penalidade ainda não foi cumprida.");
            }

            if (TipoProcessoEnum.SUSPENSAO.equals(wrapper.getProcessoAdministrativo().getTipo())) {
                retorno.setTipo(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO);
                retorno.setCodigoFluxo(PAFluxoProcessoConstante.FLUXO_NORMAL);
                retorno.setCodigoAndamento(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_REALIZACAO_PROVA_CURSO);
            }

            if (TipoProcessoEnum.CASSACAO.equals(wrapper.getProcessoAdministrativo().getTipo())) {
                retorno.setTipo(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO);
                retorno.setCodigoFluxo(PAFluxoProcessoConstante.FLUXO_NORMAL);
                retorno.setCodigoAndamento(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.DESBLOQUEAR_WEB);
            }

            if (TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(wrapper.getProcessoAdministrativo().getTipo())) {
                retorno.setTipo(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO);
                retorno.setCodigoFluxo(PAFluxoProcessoConstante.FLUXO_GERAL_PERMISSIONADO);
                retorno.setCodigoAndamento(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.DESBLOQUEAR_WEB);
            }
        } else {
            if (!entrega.getSetorCorrespondencia().getSigla().equals("DT/SEPEN")) {
                DetranWebUtils.applicationMessageException("A CNH encontra-se em outro setor do DETRAN.");
            }

            PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, wrapper.getProcessoAdministrativo().getId());
            
            if (Objects.isNull(penalidade)) {
                if (wrapper.getProcessoAdministrativo().getTipo().equals(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH)) {
                    new PAPenalidadeProcessoBO()
                            .gravarPenalidadeControleCnh(
                                    em,
                                    wrapper.getProcessoAdministrativo(),
                                    wrapper.getIdUsuario(),
                                    entrega.getDataEntrega()
                            );
                } else {
                    ProcessoRascunhoBloqueio rascunho = new ProcessoRascunhoBloqueioRepositorio().getRascunhoPorProcessoAdministrativo(em, wrapper.getProcessoAdministrativo());
                    new PAPenalidadeProcessoBO()
                            .gravarPenalidadeControleCnh(
                                    em,
                                    wrapper.getProcessoAdministrativo(),
                                    wrapper.getIdUsuario(),
                                    rascunho.getDataInicio());
                }
            }

            MovimentoCnh entregaMovimento = new MovimentoCnhRepositorio().
                    getMovimentoPorProcessoAdministrativoEAcao(em, wrapper.getProcessoAdministrativo().getId(), AcaoEntregaCnhEnum.ENTREGA);
            if (entregaMovimento != null) {
                entregaMovimento.getProtocolo().setAtivo(AtivoEnum.DESATIVADO);
                entregaMovimento.setAtivo(AtivoEnum.DESATIVADO);
                new MovimentoCnhRepositorio().update(em, entregaMovimento);
                new ProtocoloRepositorio().update(em, entregaMovimento.getProtocolo());
            }

            Protocolo protocolo = new ProtocoloBO()
                    .gravar(
                            em,
                            new PAFluxoFaseRepositorio().getFluxoFaseDoProcessoAdministrativo(em, wrapper.getProcessoAdministrativo()),
                            getTipoProtocoloEntrega(wrapper.getProcessoAdministrativo()),
                            wrapper.getProcessoAdministrativo(),
                            entrega.getTemplateProtocolo());

            new MovimentoCnhBO().gravarMovimentoCnh(em, entrega.getCnhControle(), processoOriginal, protocolo, AcaoEntregaCnhEnum.ENTREGA);
        }

        return retorno;
    }

    public ProcessoAdministrativo getProcessoOriginal(EntityManager em, ProcessoAdministrativo paUltimaPenalidade) throws AppException {
        ProcessoAdministrativo processoOriginal;
        MovimentoCnh movimento
                = new MovimentoCnhRepositorio().
                        getMovimentoPorProcessoAdministrativoEAcao(em, paUltimaPenalidade.getId(), AcaoEntregaCnhEnum.ENTREGA);
        processoOriginal = movimento.getProcessoOriginal() == null ? movimento.getProtocolo().getNumeroProcesso() : movimento.getProcessoOriginal();
        return processoOriginal;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

    }

    private TipoNotificacaoEnum getTipoProtocoloEntrega(ProcessoAdministrativo processoAdministrativo) {

        TipoNotificacaoEnum tipo = TipoNotificacaoEnum.PROTOCOLO_PA_ENTREGA_CNH;

        if (RegraInstaurarEnum.C1.equals(processoAdministrativo.getOrigemApoio().getRegra())) {
            return TipoNotificacaoEnum.PROTOCOLO_PA_ENTREGA_CNH_PERMISSIONADO;
        }
        return tipo;
    }

    private void validarPrazoPermissionado(ExecucaoAndamentoEspecificoWrapper wrapper, EntityManager em) throws AppException {

        if (!TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(wrapper.getProcessoAdministrativo().getTipo())) {
            return;
        }

        PAOcorrenciaStatus ocorrenciaAtual = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, wrapper.getProcessoAdministrativo().getId());

        if (ocorrenciaAtual == null
                || !ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo()
                        .equals(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_ENTREGA_CNH)) {
            DetranWebUtils.applicationMessageException("O andamento do Processo Administrativo é diferente do Andamento 48.");
        }

        Date dataAtual = Calendar.getInstance().getTime();

        BigDecimal valorNumericoPrazo = getApoioService()
                .getValorNumericoDaParametrizacaoValorPorCodigo(ParametrizacaoEnum.PRAZO_ANDAMENTO_PERMISSIONADOS.getCode());

        Date dataPrazo = Utils.addDayMonth(ocorrenciaAtual.getDataInicio(), valorNumericoPrazo.intValue());

        if (dataPrazo.after(dataAtual)) {
            DetranWebUtils.applicationMessageException("PA dentro do prazo permissionado de 30 dias.");
        }
    }
}
