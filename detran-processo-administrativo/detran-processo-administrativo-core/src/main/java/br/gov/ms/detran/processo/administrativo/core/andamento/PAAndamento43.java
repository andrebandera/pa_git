package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.UnidadePenalEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.BloqueioBCABO;
import br.gov.ms.detran.processo.administrativo.core.bo.PAPenalidadeProcessoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ConsultaCnhControleRecolhimentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.ConsultaCnhControleRecolhimento;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoMovimentoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;

public class PAAndamento43 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento43.class);

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(
            EntityManager em, ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {

        LOG.debug("Início Andamento43.");

        if (TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(andamentoEspecificoWrapper.getProcessoAdministrativo().getTipo())) {
            inserirBloqueioEPenalidadeParaPermissionado(em, andamentoEspecificoWrapper);
        } 
        else if (andamentoEspecificoWrapper.getProcessoAdministrativo().isJuridico()) {
            List<ConsultaCnhControleRecolhimento> recolhimentoCnh
                    = new ConsultaCnhControleRecolhimentoRepositorio()
                            .buscarRecolhimentoCnh(em, andamentoEspecificoWrapper.getProcessoAdministrativo().getId());

            DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, andamentoEspecificoWrapper.getProcessoAdministrativo().getId());
            if (BooleanEnum.SIM.equals(pju.getIndicativoPrazoIndeterminado()) || Objects.isNull(recolhimentoCnh) || recolhimentoCnh.isEmpty()) {
                inserirBloqueioParaJuridicoIndeterminadoOuSemCNH(em, andamentoEspecificoWrapper, pju);
            } else {
                inserirBloqueioEPenalidadePorRascunho(em, andamentoEspecificoWrapper);
            }
        } 
        else {
            inserirBloqueioEPenalidadePorRascunho(em, andamentoEspecificoWrapper);
        }

        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

    }

    /**
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException
     */
    private BloqueioBCA gravarBloqueioParaProcesso(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper, Date dataInicio) throws AppException {

        UnidadePenalEnum unidadePenal = UnidadePenalEnum.MES;

        PAComplemento complemento
                = new PAComplementoRepositorio()
                        .getPAComplementoPorParametroEAtivo(em,
                                wrapper.getProcessoAdministrativo(),
                                PAParametroEnum.TEMPO_PENALIDADE);

        if (complemento == null) {
            DetranWebUtils.applicationMessageException("Não foi possível recuperar o complemento do PA.");
        }

        Integer tempoPenalidade = Integer.parseInt(complemento.getValor());

        if (wrapper.getProcessoAdministrativo().isJuridico()) {
            unidadePenal = UnidadePenalEnum.DIA;

            DadoProcessoJudicial dadoJuridico
                    = new DadoProcessoJudicialRepositorio().
                            getDadoProcessoJudicialPorPA(em, wrapper.getProcessoAdministrativo().getId());

            if (dadoJuridico == null || dadoJuridico.getRequisitoCursoBloqueio() == null) {
                DetranWebUtils.applicationMessageException("Dados do Processo Judicial inválidos.");
            }

            dataInicio = dadoJuridico.getDataBloqueio();
        }

        BloqueioBCA bloqueio
                = new BloqueioBCABO()
                        .gravarBloqueioBCA(em,
                                wrapper.getProcessoAdministrativo(),
                                dataInicio,
                                tempoPenalidade,
                                unidadePenal);

        new BloqueioBCABO()
                .gravarMovimentoBloqueioBCA(
                        em,
                        bloqueio,
                        wrapper.getIdUsuario(),
                        TipoMovimentoBloqueioBCAEnum.BLOQUEIO
                );

        return bloqueio;
    }

    private void inserirBloqueioEPenalidadeParaPermissionado(EntityManager em, ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {

        Date dataAtual = Calendar.getInstance().getTime();

        BloqueioBCA bloqueio = gravarBloqueioParaProcesso(em, andamentoEspecificoWrapper, dataAtual);

        new PAPenalidadeProcessoBO()
                .gravarPenalidadeControleCnh(
                        em,
                        andamentoEspecificoWrapper.getProcessoAdministrativo(),
                        andamentoEspecificoWrapper.getIdUsuario(),
                        bloqueio.getDataInicio()
                );
    }

    private void inserirBloqueioParaJuridicoIndeterminadoOuSemCNH(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper, DadoProcessoJudicial pju) throws AppException {
        Date dataBloqueio = pju.getDataBloqueio();

        gravarBloqueioParaProcesso(em, wrapper, dataBloqueio);
    }

    private void inserirBloqueioEPenalidadePorRascunho(EntityManager em, ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {

        BloqueioBCA bloqueio = new BloqueioBCABO().gravarBloqueioBCAPorRascunho(em, andamentoEspecificoWrapper.getProcessoAdministrativo());

        new BloqueioBCABO()
                .gravarMovimentoBloqueioBCA(
                        em,
                        bloqueio,
                        andamentoEspecificoWrapper.getIdUsuario(),
                        TipoMovimentoBloqueioBCAEnum.BLOQUEIO
                );

        //Gravar TEA igual à TDK
        new PAPenalidadeProcessoBO()
                .gravarPenalidadeControleCnh(
                        em,
                        andamentoEspecificoWrapper.getProcessoAdministrativo(),
                        andamentoEspecificoWrapper.getIdUsuario(),
                        bloqueio.getDataInicio()
                );

    }
}
