package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.core.repositorio.AndamentoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRetornoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoOrigemRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPrioridadeFluxoAmparoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAStatusAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.AndamentoProcessoAdministrativoConsulta;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFaseRetorno;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.PAPrioridadeFluxoAmparo;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
import java.util.Calendar;
import javax.persistence.EntityManager;

public class AndamentoProcessoAdministrativoManager2 {

    private PAOcorrenciaStatus ocorrenciaAtual;

    /**
     * Alterar para o próximo andamento.Andamento atual deve estar INICIADO.
     *
     * @param em
     * @param paId
     * @param ocorrenciaAtual
     * @throws AppException
     */
    public void proximoAndamento(EntityManager em, Long paId, PAOcorrenciaStatus ocorrenciaAtual) throws AppException {

        if (null == paId) {
            DetranWebUtils.applicationMessageException("Erro ao recuperar processo administrativo.");
        }

        if (ocorrenciaAtual == null) {
            ocorrenciaAtual = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, paId);
        }

        finalizarAndamentoAtual(em, ocorrenciaAtual);
        iniciarProximoAndamento(em, ocorrenciaAtual);

        em.flush();
    }

    /**
     * Iniciar fluxo.
     *
     * @param em
     * @param processo
     * @param codigoPAFluxoProcesso
     * @throws AppException
     */
    public void iniciarFluxo(EntityManager em, ProcessoAdministrativo processo, Integer codigoPAFluxoProcesso) throws AppException {

        if (null == processo || null == processo.getId()) {
            DetranWebUtils.applicationMessageException("Erro ao recuperar processo administrativo.");
        }

        if (null == codigoPAFluxoProcesso) {
            DetranWebUtils.applicationMessageException("Código do fluxo não informado");
        }

        if (ocorrenciaAtual == null) {
            ocorrenciaAtual = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, processo.getId());
        }

        finalizarAndamentoAtual(em, ocorrenciaAtual);

        PAFluxoAndamento fluxoAndamento
                = new PAFluxoAndamentoRepositorio()
                        .getFluxoAndamentoPorPAEFluxoProcesso(em, processo.getId(), codigoPAFluxoProcesso);

        if (fluxoAndamento == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar fluxo alternativo para andamento atual do PA.");
        }

        PAFluxoProcesso fluxoMudanca = fluxoAndamento.getFluxoProcesso();

        if (fluxoMudanca == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar fluxo alternativo para andamento atual do PA.");
        }

        new PAFluxoOrigemRepositorio()
                .validaMudancaFluxoPorOrigemEFluxoProcesso(em,
                        ocorrenciaAtual.getProcessoAdministrativo().getOrigemApoio(),
                        fluxoMudanca);

        PAPrioridadeFluxoAmparo prioridadeFluxoAmparoDestino
                = new PAPrioridadeFluxoAmparoRepositorio()
                        .getPrioridadeFluxoAmparoAtivoPorFluxoProcesso(em, fluxoMudanca);

        if (prioridadeFluxoAmparoDestino == null || prioridadeFluxoAmparoDestino.getId() == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar fluxo alternativo para andamento atual do PA.");
        }

        PAFluxoFase fluxoFaseDestino
                = new PAFluxoFaseRepositorio()
                        .getFluxoFasePorPrioridadeFluxoAmparoOrdenadoAscendentePorPrioridade(em, prioridadeFluxoAmparoDestino);

        if (fluxoFaseDestino == null
                || fluxoFaseDestino.getId() == null
                || fluxoFaseDestino.getAndamentoProcesso() == null
                || fluxoFaseDestino.getAndamentoProcesso().getId() == null) {

            DetranWebUtils.applicationMessageException("Não foi possível encontrar fluxo alternativo para andamento atual do PA.");
        }

        PAStatusAndamento statusAndamento
                = new PAStatusAndamentoRepositorio()
                        .getPAStatusAndamentoAtivoPorStatusEAndamento(
                                em,
                                fluxoFaseDestino.getAndamentoProcesso().getCodigo());

        if (statusAndamento == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.parametroinvalido2");
        }

        executarMudancaFluxo(em, ocorrenciaAtual, fluxoMudanca, statusAndamento);

    }

    /**
     * Finalizar o andamento atual.
     *
     * @param em
     * @param ocorrenciaAtual
     * @throws AppException
     */
    private void finalizarAndamentoAtual(EntityManager em, PAOcorrenciaStatus ocorrenciaAtual) throws AppException {

        PAOcorrenciaStatusRepositorio repo = new PAOcorrenciaStatusRepositorio();

        if (!SituacaoOcorrenciaEnum.INICIADO.equals(ocorrenciaAtual.getSituacao())) {
            DetranWebUtils.applicationMessageException("A situação do andamento é diferente de INICIADO.");
        }

        ocorrenciaAtual.setSituacao(SituacaoOcorrenciaEnum.FINALIZADO);
        ocorrenciaAtual.setDataTermino(Calendar.getInstance().getTime());

        repo.update(em, ocorrenciaAtual);

        em.flush();
    }

    /**
     * @param em
     * @param ocorrenciaAtual
     * @throws AppException
     */
    private void iniciarProximoAndamento(EntityManager em, PAOcorrenciaStatus ocorrenciaAtual) throws AppException {

        PAOcorrenciaStatusRepositorio repo = new PAOcorrenciaStatusRepositorio();

        AndamentoProcessoAdministrativoConsulta proximoAndamento
                = new AndamentoProcessoAdministrativoRepositorio()
                        .getProximoAndamento(em, ocorrenciaAtual.getProcessoAdministrativo());

        if (proximoAndamento == null || proximoAndamento.getCodigoAndamentoProcesso() == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar o próximo andamento do PA.");
        }

        PAStatusAndamento statusAndamento
                = new PAStatusAndamentoRepositorio()
                        .getPAStatusAndamentoAtivoPorStatusEAndamento(
                                em,
                                proximoAndamento.getCodigoAndamentoProcesso().intValue());

        ocorrenciaAtual.setStatusAndamento(statusAndamento);
        ocorrenciaAtual.setSituacao(SituacaoOcorrenciaEnum.INICIADO);
        ocorrenciaAtual.setDataInicio(Calendar.getInstance().getTime());
        ocorrenciaAtual.setDataTermino(null);

        repo.update(em, ocorrenciaAtual);

        em.flush();
    }

    /**
     * @param em
     * @param ocorrenciaAtual
     * @param fluxoMudanca
     * @param statusAndamento
     * @throws DatabaseException
     */
    private void executarMudancaFluxo(EntityManager em,
            PAOcorrenciaStatus ocorrenciaAtual,
            PAFluxoProcesso fluxoProcesso,
            PAStatusAndamento statusAndamento) throws DatabaseException {

        ocorrenciaAtual.setFluxoProcesso(fluxoProcesso);
        ocorrenciaAtual.setStatusAndamento(statusAndamento);

        ocorrenciaAtual.setSituacao(SituacaoOcorrenciaEnum.INICIADO);
        ocorrenciaAtual.setDataInicio(Calendar.getInstance().getTime());
        ocorrenciaAtual.setDataTermino(null);

        new PAOcorrenciaStatusRepositorio().update(em, ocorrenciaAtual);

        em.flush();
    }

    public void executarMudancaFluxoEAndamento(EntityManager em, ProcessoAdministrativo pa, Integer codigoFluxo, Integer codigoAndamento) throws AppException {

        if (ocorrenciaAtual == null) {
            ocorrenciaAtual = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, pa.getId());
        }

        finalizarAndamentoAtual(em, ocorrenciaAtual);

        PAFluxoFase fluxoFaseOrigem
                = new PAFluxoFaseRepositorio()
                        .getPAFluxoFasePorAndamentoProcessoEPrioridadeFluxoAmparo(
                                em,
                                ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso(),
                                ocorrenciaAtual.getFluxoProcesso());

        PAFluxoFaseRetorno fluxoFaseRetorno = null;

        fluxoFaseRetorno
                = new PAFluxoFaseRetornoRepositorio()
                        .getPAFluxoFaseRetornoPorFluxoFaseAtualEAndamento(
                                em,
                                fluxoFaseOrigem.getId(),
                                codigoFluxo,
                                codigoAndamento);

        if (fluxoFaseRetorno == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar fluxo retorno para o PA.");
        }

        PAFluxoFase fluxoFaseDestino = fluxoFaseRetorno.getFluxoRetorno();

        PAPrioridadeFluxoAmparo prioridadeFluxoAmparoRetorno = fluxoFaseDestino.getPrioridadeFluxoAmparo();

        if (prioridadeFluxoAmparoRetorno.getFluxoProcesso() == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar fluxo processo para o PA.");
        }

        new PAFluxoOrigemRepositorio()
                .validaMudancaFluxoPorOrigemEFluxoProcesso(
                        em,
                        ocorrenciaAtual.getProcessoAdministrativo().getOrigemApoio(),
                        prioridadeFluxoAmparoRetorno.getFluxoProcesso());

        PAStatusAndamento statusAndamento
                = new PAStatusAndamentoRepositorio()
                        .getPAStatusAndamentoAtivoPorStatusEAndamento(
                                em,
                                fluxoFaseDestino.getAndamentoProcesso().getCodigo());

        if (statusAndamento == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.parametroinvalido2");
        }

        executarMudancaFluxo(em, ocorrenciaAtual, prioridadeFluxoAmparoRetorno.getFluxoProcesso(), statusAndamento);

    }
}
