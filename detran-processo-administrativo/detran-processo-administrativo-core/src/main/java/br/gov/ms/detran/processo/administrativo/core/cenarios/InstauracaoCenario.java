package br.gov.ms.detran.processo.administrativo.core.cenarios;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.AcaoSistemaPAEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.generico.DetranGenericRepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoBO;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoInfracaoBO;
import br.gov.ms.detran.processo.administrativo.core.bo.RegrasGeralInstauracaoInfracaoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.*;
import br.gov.ms.detran.processo.administrativo.entidade.*;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author desenvolvimento
 */
public abstract class InstauracaoCenario implements IInstauracaoCenario {

    private static final Logger LOG = Logger.getLogger(InstauracaoCenario.class);

    public static final Integer PONTUACAO_MAXIMA = 39;

    protected final RegrasGeralInstauracaoInfracaoBO regraGeralInfracaoBO;

    public InstauracaoCenario() {
        this.regraGeralInfracaoBO = new RegrasGeralInstauracaoInfracaoBO();
    }

    /**
     *
     * @param em
     * @param wrapper
     * @return
     * @throws AppException
     */
    @Override
    public IBaseEntity instaurar(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {

        wrapper.setInfracoes(getInfracoesValidasRegraGeral(em, wrapper, getInfracoes(em, wrapper)));

        ProcessoAdministrativo processoAdministrativo = (ProcessoAdministrativo) executarInstauracao(em, wrapper);

        if (processoAdministrativo != null) {
            aposGravacao(em, wrapper, processoAdministrativo);
        }

        return processoAdministrativo;
    }

    /**
     *
     * @param em
     * @param wrapper
     * @param infracoes
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.RegraNegocioException
     * @throws AppException
     */
    public ProcessoAdministrativo gravar(EntityManager em, 
                                         InstaurarProcessoAdministrativoWrapper wrapper, 
                                         List<DadosInfracaoPAD> infracoes) throws RegraNegocioException, AppException {

        ProcessoAdministrativo processoAdministrativo = new ProcessoAdministrativoBO().gravar(em, wrapper);

        PAFluxoProcesso fluxoProcesso = 
                new PAFluxoProcessoRepositorio()
                        .getPAFluxoProcessoPorApoioOrigemInstauracao(em, wrapper.getApoioOrigemInstauracao().getId());

        if (fluxoProcesso == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.parametroinvalido2");
        }

        /**
         * Ao instaurar um P.A, sistema deverá vincular um status ao Processo. *
         */
        new PAOcorrenciaStatusRepositorio()
                .incluir(em,
                         processoAdministrativo,
                         PAAndamentoProcessoConstante.INSTAURACAO.INSTAURAR_PROCESSO,
                         SituacaoOcorrenciaEnum.INICIADO,
                         fluxoProcesso);
        
        PAOcorrenciaStatus ocorrenciaAtual = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, processoAdministrativo.getId());
        
        if (ocorrenciaAtual == null || 
                !PAAndamentoProcessoConstante.INSTAURACAO.INSTAURAR_PROCESSO
                        .equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
            
            DetranWebUtils.applicationMessageException("O andamento do Processo Administrativo é diferente do Andamento informado.");
        }

        //new AndamentoProcessoAdministrativoManager2().proximoAndamento(em, processoAdministrativo.getId(), ocorrenciaAtual);

        /**
         * Regras Infrações associadas a instauração do P.A. *
         */
        new ProcessoAdministrativoInfracaoBO()
                .geraInfracao(em,
                              processoAdministrativo,
                              infracoes,
                              wrapper.getApoioOrigemInstauracao());

        gravarComplementoPA(em, processoAdministrativo, infracoes, wrapper);

        return processoAdministrativo;
    }

    /**
     *
     * @param em
     * @param wrapper
     * @return
     * @throws AppException
     */
    public abstract IBaseEntity executarInstauracao(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException;

    /**
     *
     * @param em
     * @param wrapper
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.RegraNegocioException
     * @throws DatabaseException
     */
    public List getInfracoes(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws RegraNegocioException, AppException {

        return new DadosInfracaoPADRepositorio()
                .getInfracoesPorCpfEAmparoLegal(
                        em,
                        wrapper.getDadosCondutorPAD().getCpf(),
                        wrapper.getApoioOrigemInstauracao()
                );
    }

    public List getInfracoesValidasRegraGeral(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper, List<DadosInfracaoPAD> infracoes) throws AppException {
        List infracoesValidadas = new ArrayList();
        for (DadosInfracaoPAD infracao : infracoes) {
            try {
                regraGeralInfracaoBO.validarRegraNaoPodeExistirInfracaoEmProcessoAdministrativo(em, infracao);
                regraGeralInfracaoBO.regraStatusPontuacao(infracao, wrapper.getApoioOrigemInstauracao());
                regraGeralInfracaoBO.validarRegraOrigemInfracao(infracao, wrapper.getApoioOrigemInstauracao());
                regraGeralInfracaoBO.validarRegraInfracaoRevogada(infracao);

                infracoesValidadas.add(infracao);
            } catch (RegraNegocioException ex) {
                //LOG.warn("RegraNegocioException: {0}", ex.getMessage());
            }
        }
        return infracoesValidadas;
    }

    protected void gravarComplementoPA(EntityManager em,  ProcessoAdministrativo processoAdministrativo, List<DadosInfracaoPAD> infracoes, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {

        if (!DetranCollectionUtil.ehNuloOuVazio(infracoes)) {

            BigDecimal valorPenal = new PAInfracaoRepositorio().getValorPenalAcaoInfracaoPenalidade(em, 
                    infracoes.get(0).getInfracaoCodigo(),
                    wrapper.getApoioOrigemInstauracao().getAmparoLegal(),
                    wrapper.getApoioOrigemInstauracao().getResultadoAcao().ordinal(),
                    wrapper.getApoioOrigemInstauracao().getAcaoSistema().ordinal(),
                    infracoes.get(0).getDataInfracao());

            if (valorPenal == null) {
                DetranWebUtils.applicationMessageException("Ação Penalidade inválida.");
            }

            PAComplemento complemento = new PAComplemento();
            complemento.setAtivo(AtivoEnum.ATIVO);
            complemento.setParametro(PAParametroEnum.TEMPO_PENALIDADE);
            complemento.setProcessoAdministrativo(processoAdministrativo);
            complemento.setValor(String.valueOf(valorPenal.intValue()));

            new AbstractJpaDAORepository().insert(em, complemento);
        }
    }

    /**
     * @param processoAdministrativo
     */
    private void aposGravacao(EntityManager em,
            InstaurarProcessoAdministrativoWrapper wrapper,
            ProcessoAdministrativo processoAdministrativo) throws DatabaseException, RegraNegocioException {

        if (processoAdministrativo != null 
                && processoAdministrativo.getOrigemApoio() != null 
                && processoAdministrativo.getOrigemApoio().getAcaoSistema() != null
                && !AcaoSistemaPAEnum.NOVO.equals(processoAdministrativo.getOrigemApoio().getAcaoSistema())) {

            if (wrapper.getProcesssoAdministrativoOrigem() == null) {
                throw new RegraNegocioException("Não é possível apensar ou agravar sem processo administrativo de origem.");
            }

            if (AcaoSistemaPAEnum.NOVO_APENSADO.equals(processoAdministrativo.getOrigemApoio().getAcaoSistema())) {

                ProcessoAdministrativoCassacao paCassacao = new ProcessoAdministrativoCassacao();
                paCassacao.setProcessoAdministrativo(processoAdministrativo);
                paCassacao.setAtivo(AtivoEnum.ATIVO);
                
                new ProcessoAdministrativoCassacaoRepositorio().insert(em, paCassacao);

                ProcessoAdministrativoApensado paApensado = new ProcessoAdministrativoApensado();
                paApensado.setProcessoAdministrativoCassacao(paCassacao);
                paApensado.setProcessoAdministrativo(wrapper.getProcesssoAdministrativoOrigem());
                paApensado.setAtivo(AtivoEnum.ATIVO);
                
                new ProcessoAdministrativoApensadoRepositorio().insert(em, paApensado);
            
            } else if (AcaoSistemaPAEnum.NOVO_AGRAVADO.equals(processoAdministrativo.getOrigemApoio().getAcaoSistema())) {

                DetranGenericRepository repo = new DetranGenericRepository();

                ProcessoAdministrativoAgravado paAgravado = new ProcessoAdministrativoAgravado();
                paAgravado.setProcessoAdministrativo(processoAdministrativo);
                paAgravado.setAtivo(AtivoEnum.ATIVO);
                repo.insert(em, paAgravado);
                
                ProcessoAdministrativoAgravamento paAgravamento = new ProcessoAdministrativoAgravamento();
                paAgravamento.setProcessoAdministrativoAgravado(paAgravado);
                paAgravamento.setProcessoAdministrativo(wrapper.getProcesssoAdministrativoOrigem());
                paAgravamento.setAtivo(AtivoEnum.ATIVO);
                repo.insert(em, paAgravamento);
            }
        }
    }
    
    /**
     *
     * @param em
     * @param dadosInfracaoPAD
     * 
     * @return
     * 
     * @throws br.gov.ms.detran.comum.util.exception.RegraNegocioException
     * @throws DatabaseException
     */
    public Boolean verificaInfracaoEhEspecificadaEAutuadaCompetenciaDetranOuOrgaoDelegou(EntityManager em, DadosInfracaoPAD dadosInfracaoPAD) throws RegraNegocioException, AppException {
        
        return 
            new DadosInfracaoPADRepositorio()
                .verificaInfracaoEhEspecificadaEAutuadaCompetenciaDetranOuOrgaoDelegou(
                    em, 
                    dadosInfracaoPAD.getCpfCondutor().getCpf(), 
                    dadosInfracaoPAD.getAuto()
                );
    }
}