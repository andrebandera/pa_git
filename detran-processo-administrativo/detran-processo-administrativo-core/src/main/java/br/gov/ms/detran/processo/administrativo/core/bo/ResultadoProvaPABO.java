package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoProvaPA;
import java.util.List;
import javax.persistence.EntityManager;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ResultadoProvaPARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import br.gov.ms.detran.processo.administrativo.enums.RequisitoRecursoBloqueioEnum;
import java.util.Date;

public class ResultadoProvaPABO {

    private static final Logger LOG = Logger.getLogger(ResultadoProvaPABO.class);

    IProcessoAdministrativoService processoAdministrativoService;
    IPAControleFalhaService paControleFalha;

    /**
     *
     * @return
     */
    IProcessoAdministrativoService getProcessoAdministrativoService() {
        if (processoAdministrativoService == null) {
            processoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }
        return processoAdministrativoService;
    }

    /**
     *
     * @return
     */
    IPAControleFalhaService getControleFalha() {
        if (paControleFalha == null) {
            paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
        }
        return paControleFalha;
    }

    /**
     * @param em
     * @param idResultadoLaudoSituacao
     * @param numeroDetran
     * @param dataExame
     */
    public void gravar(EntityManager em, Long idResultadoLaudoSituacao, Long numeroDetran, Date dataExame) {

        try {

            if (idResultadoLaudoSituacao == null || numeroDetran == null || dataExame == null) {
                DetranWebUtils.applicationMessageException("PA.resultadoprovapa.M1");
            }

            ResultadoProvaPA resultadoProvaPA = new ResultadoProvaPA(idResultadoLaudoSituacao, dataExame);

            gravacaoParaProcessosAguardandoRealizacaoCurso(em, numeroDetran, resultadoProvaPA);

            gravacaoParaProcessosComAndamentoDiferenteAguardandoCurso(em, numeroDetran, resultadoProvaPA);

        } catch (Exception ex) {

            LOG.debug("Erro ao gravar Resultado Prova no módulo de Processo Administrativo.", ex);
            getControleFalha().gravarFalha(ex, "Numero Detran: " + numeroDetran.toString() + ". Erro ao gravar resultado prova");
        }
    }

    public void gravacaoParaProcessosComAndamentoDiferenteAguardandoCurso(EntityManager em, Long numeroDetran, ResultadoProvaPA resultado) throws DatabaseException {

        List<ProcessoAdministrativo> listaProcessoAdministrativo
                = new ProcessoAdministrativoRepositorio()
                        .getPAsParaResultadoProvaPorCondutor(em, numeroDetran);

        if (!DetranCollectionUtil.ehNuloOuVazio(listaProcessoAdministrativo)) {

            for (ProcessoAdministrativo processoAdministrativo : listaProcessoAdministrativo) {
                try {

                    validarProcessoAdministrativoNaoPermissionado(processoAdministrativo);

                    validarProcessoAdministrativoPontuacaoComPortariaPenalidade(em, processoAdministrativo, resultado.getDataProva());

                    validarProcessoAdministrativoJuridicoComCurso(em, processoAdministrativo, resultado.getDataProva());

                    gravarResultadoProvaPA(em, processoAdministrativo, resultado);

                } catch (DatabaseException ex) {
                    LOG.debug("Erro ao gravar Resultado Prova no módulo de Processo Administrativo.", ex);
                    getControleFalha().gravarFalhaProcessoAdministrativo(ex,
                            "Erro ao gravar resultado prova(Sem andamento) para o PA: ",
                            processoAdministrativo.getCpf(),
                            processoAdministrativo.getNumeroProcesso());
                } catch (AppException ex) {
                    LOG.info("PA não esta apto a gravar resultado prova.", ex);
                } catch (Exception ex) {
                    LOG.debug("Erro ao gravar Resultado Prova no módulo de Processo Administrativo.", ex);
                    getControleFalha().gravarFalhaProcessoAdministrativo(ex,
                            "Erro ao gravar resultado prova(Sem andamento) para o PA: ",
                            processoAdministrativo.getCpf(),
                            processoAdministrativo.getNumeroProcesso());
                }
            }
        }
    }

    public void gravacaoParaProcessosAguardandoRealizacaoCurso(EntityManager em, Long numeroDetran, ResultadoProvaPA resultado) throws AppException {
        List<ProcessoAdministrativo> listaProcessoAdministrativo
                = new ProcessoAdministrativoRepositorio()
                        .getListaProcessoAdministrativoPorNumeroDetranEAndamentoESituacaoIniciado(em,
                                numeroDetran,
                                PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_REALIZACAO_PROVA_CURSO);

        if (!DetranCollectionUtil.ehNuloOuVazio(listaProcessoAdministrativo)) {

            for (ProcessoAdministrativo processoAdministrativo : listaProcessoAdministrativo) {

                try {

                    validarEGravarResultadoProva(em, processoAdministrativo, resultado);

                    new AndamentoProcessoAdministrativoManager2()
                            .proximoAndamento(em, processoAdministrativo.getId(), null);

                } catch (DatabaseException ex) {
                    LOG.debug("Erro ao gravar Resultado Prova no módulo de Processo Administrativo.", ex);
                    getControleFalha().gravarFalhaProcessoAdministrativo(ex,
                            "Erro ao gravar resultado prova(Sem andamento) para o PA: ",
                            processoAdministrativo.getCpf(),
                            processoAdministrativo.getNumeroProcesso());
                } catch (AppException ex) {
                    LOG.info("PA não esta apto a gravar resultado prova.", ex);
                    getControleFalha().gravarFalhaProcessoAdministrativo(ex,
                            "Erro ao gravar resultado prova(Sem andamento) para o PA: ",
                            processoAdministrativo.getCpf(),
                            processoAdministrativo.getNumeroProcesso());
                } catch (Exception ex) {
                    LOG.debug("Erro ao gravar Resultado Prova no módulo de Processo Administrativo.", ex);
                    getControleFalha().gravarFalhaProcessoAdministrativo(ex,
                            "Erro ao gravar resultado prova(Sem andamento) para o PA: ",
                            processoAdministrativo.getCpf(),
                            processoAdministrativo.getNumeroProcesso());
                }
            }
        }
    }

    public void validarEGravarResultadoProva(EntityManager em, ProcessoAdministrativo processoAdministrativo, ResultadoProvaPA resultado) throws DatabaseException, AppException {
        validarProcessoAdministrativoPontuacaoComPortariaPenalidade(em, processoAdministrativo, resultado.getDataProva());
        
        validarProcessoAdministrativoJuridicoComCurso(em, processoAdministrativo, resultado.getDataProva());
        
        if (!existeResultadoProvaParaPa(em, processoAdministrativo)) {
            gravarResultadoProvaPA(em, processoAdministrativo, resultado);
        }
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @param idResultadoLaudoSituacao
     * @throws DatabaseException
     */
    private void gravarResultadoProvaPA(EntityManager em, ProcessoAdministrativo processoAdministrativo, ResultadoProvaPA resultado) throws DatabaseException {
        ResultadoProvaPA resultadoProvaPA = new ResultadoProvaPA();

        resultadoProvaPA.setProcessoAdministrativo(processoAdministrativo);
        resultadoProvaPA.setResultadoLaudoSituacao(resultado.getResultadoLaudoSituacao());
        resultadoProvaPA.setDataCadastro(resultado.getDataCadastro());
        resultadoProvaPA.setDataProva(resultado.getDataProva());
        resultadoProvaPA.setMunicipio(resultado.getMunicipio());
        resultadoProvaPA.setUsuario(resultado.getUsuario());
        resultadoProvaPA.setTipoResultadoExame(resultado.getTipoResultadoExame());

        resultadoProvaPA.setAtivo(AtivoEnum.ATIVO);

        new AbstractJpaDAORepository().insertOrUpdate(em, resultadoProvaPA);
    }

    /**
     * Validação para Permissionados.
     *
     * @param processoAdministrativo
     * @throws AppException
     */
    private void validarProcessoAdministrativoNaoPermissionado(ProcessoAdministrativo processoAdministrativo) throws AppException {
        if (TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(processoAdministrativo.getTipo())) {
            DetranWebUtils.applicationMessageException("Não há gravação de resultado de prova para PA de permissionado.");
        }
    }

    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @throws AppException
     */
    private void validarProcessoAdministrativoPontuacaoComPortariaPenalidade(EntityManager em, ProcessoAdministrativo processoAdministrativo, Date dataExame) throws AppException {
        if (OrigemEnum.PONTUACAO.equals(processoAdministrativo.getOrigem())) {
            Date dataPenalizacao = new NotificacaoComplementoRepositorio().getDataPenalizacaoPorProcessoAdministrativo(em, processoAdministrativo.getId());
            if (dataPenalizacao == null || dataPenalizacao.after(dataExame)) {
                DetranWebUtils.applicationMessageException("Processo sem portaria de penalização, não será vinculado ao resultado de prova.");
            }
        }

    }

    /**
     * Validação para Processos Judiciais.
     *
     * @param em
     * @param processoAdministrativo
     * @throws AppException
     */
    private void validarProcessoAdministrativoJuridicoComCurso(EntityManager em, ProcessoAdministrativo processoAdministrativo, Date dataExame) throws AppException {
        if (OrigemEnum.JURIDICA.equals(processoAdministrativo.getOrigem())) {
            DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, processoAdministrativo.getId());
                if(pju == null || pju.getProcessoJudicial().getProcessoAdministrativo().getDataProcessamento().after(dataExame)){
                    DetranWebUtils.applicationMessageException("Processo aberto apsó a realização da Prova.");
                }
            
            if (RequisitoRecursoBloqueioEnum.SEM_CURSO.equals(pju.getRequisitoCursoBloqueio())) {
                DetranWebUtils.applicationMessageException("Processo sem requisito de curso.");
            }
        }
    }

    private boolean existeResultadoProvaParaPa(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws DatabaseException {
        ResultadoProvaPA resultado;
        try {
            resultado = new ResultadoProvaPARepositorio().getResultadoProvaPAAtivoPorProcessoAdministrativo(em, processoAdministrativo);

        } catch (AppException ex) {
            throw new DatabaseException(ex);
        }
        return resultado != null;
    }
}
