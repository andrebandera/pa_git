/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.enums.apo.EstadoEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.UnidadePenalEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.iface.bca.IBeanIntegracao;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP21A;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP22;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP22A;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP22B;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento242;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP22BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoCnhRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.ConsistenciaBloqueioBCAAtualizacao;
import br.gov.ms.detran.processo.administrativo.entidade.ConsistenciaBloqueioBCAAtualizacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.entidade.ConsistenciaBloqueioBCAAtualizacaoOrigemEnum;
import br.gov.ms.detran.processo.administrativo.entidade.ConsistenciaBloqueioBCAAtualizacaoSituacaoEnum;
import br.gov.ms.detran.processo.administrativo.entidade.FunctionConsistenciaBloqueioBCAAtualizacao;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

public class ConsistenciaBloqueioBCAAtualizacaoBO extends ProcessoAdministrativoBCABO implements IProcessoAdministrativoBCA {

    private static final Logger LOG = Logger.getLogger(ConsistenciaBloqueioBCAAtualizacaoBO.class);

    final Integer CODIGO_FLUXO_PROCESSO_ANDAMENTO_242 = 52;
    final Integer CODIGO_ANDAMENTO_242 = 242;

    @Override
    public void executa(EntityManager em, String urlPathParaRelatorio, Integer codigoAndamento) {

        try {

            List<FunctionConsistenciaBloqueioBCAAtualizacao> lCondutoresComProcessos
                    = new BloqueioBCARepositorio().getFunctionConsistenciaBloqueioBCAAtualizacao(em);

            if (!DetranCollectionUtil.ehNuloOuVazio(lCondutoresComProcessos)) {

                ImmutableMap<String, List<FunctionConsistenciaBloqueioBCAAtualizacao>> arvoreMapeada = getBloqueiosPorCondutor(lCondutoresComProcessos);

                if (arvoreMapeada != null && !arvoreMapeada.isEmpty()) {

                    for (Map.Entry<String, List<FunctionConsistenciaBloqueioBCAAtualizacao>> entry : arvoreMapeada.entrySet()) {

                        String cpfCondutor = entry.getKey();

                        List<ConsistenciaBloqueioBCAAtualizacao> lConsulta = new ArrayList();

                        try {

                            List<FunctionConsistenciaBloqueioBCAAtualizacao> lProcesso = entry.getValue();

                            AEMNPP22 aemnpp22 = executaAEMNPP22(cpfCondutor);

                            if (aemnpp22 != null) {

                                lConsulta.addAll(getBloqueioService().registraConsultaBloqueioLocal(lProcesso, aemnpp22));

                                lConsulta.addAll(getBloqueioService().registraConsultaBloqueioNacional(lProcesso, aemnpp22));
                            }

                            lConsulta.addAll(getBloqueioService().registraConsultaBloqueioWeb(lProcesso, aemnpp22));

                        } catch (Exception e) {

                            LOG.debug("Erro ao executar atualizacao bloqueio BCA.", e);

                            String causa = "Erro ao executar atualizacao bloqueio BCA. CPF " + cpfCondutor;

                            getControleFalha().gravarFalhaCondutor(e, causa, cpfCondutor);

                        } finally {
                            avaliaAtualizacaoBloqueioBCA(em, lConsulta);
                        }
                    }
                }
            }

        } catch (Exception e) {
            LOG.error("Erro ao executar atualizacao bloqueio BCA.", e);
            getControleFalha().gravarFalhaProcessoAdministrativo(e, "Erro ao executar atualizacao bloqueio BCA.", null, null);
        } finally {
            LOG.info("Fim execucão");
        }
    }

    /**
     *
     * @param cpf
     * @return
     */
    private AEMNPP22 executaAEMNPP22(String cpf) throws AppException {

        AEMNPP22 aemnpp22 = new AEMNPP22BO().executarIntegracaoAEMNPP22(cpf);

        if (aemnpp22 == null
                || DetranCollectionUtil.ehNuloOuVazio(aemnpp22.getDadosBloqueiosBaseLocal())
                || DetranCollectionUtil.ehNuloOuVazio(aemnpp22.getDadosBloqueiosBca())) {

            DetranWebUtils.applicationMessageException("Nenhum bloqueio foi encontrado para o processo.");
        }

        return aemnpp22;
    }

    /**
     *
     * @param processos
     * @return
     * @throws AppException
     */
    private ImmutableMap getBloqueiosPorCondutor(List<FunctionConsistenciaBloqueioBCAAtualizacao> processos) throws AppException {

        ImmutableListMultimap<String, FunctionConsistenciaBloqueioBCAAtualizacao> listaMapeada = null;

        if (!DetranCollectionUtil.ehNuloOuVazio(processos)) {

            listaMapeada
                    = Multimaps.index(processos, new Function<FunctionConsistenciaBloqueioBCAAtualizacao, String>() {
                        @Override
                        public String apply(FunctionConsistenciaBloqueioBCAAtualizacao input) {
                            return input.getCpf();
                        }
                    });
        }

        return listaMapeada != null && !listaMapeada.isEmpty() ? listaMapeada.asMap() : null;
    }

    /**
     *
     * @param em
     * @param lProcesso
     * @param aemnpp22
     * @return
     * @throws DatabaseException
     */
    public List<ConsistenciaBloqueioBCAAtualizacao> registraConsultaBloqueioLocal(EntityManager em, List<FunctionConsistenciaBloqueioBCAAtualizacao> lProcesso, AEMNPP22 aemnpp22) throws DatabaseException {

        List<ConsistenciaBloqueioBCAAtualizacao> lConsulta = new ArrayList();

        if (!DetranCollectionUtil.ehNuloOuVazio(aemnpp22.getDadosBloqueiosBaseLocal())) {

            for (Object objectAEMNPP22A : aemnpp22.getDadosBloqueiosBaseLocal()) {

                AEMNPP22A aemnpp22a = (AEMNPP22A) objectAEMNPP22A;

                ConsistenciaBloqueioBCAAtualizacao consistenciaBloqueioBCAAtualizacao = new ConsistenciaBloqueioBCAAtualizacao();

                consistenciaBloqueioBCAAtualizacao.setRegistro(aemnpp22.getRegistro());
                consistenciaBloqueioBCAAtualizacao.setCpf(aemnpp22.getCpf());
                consistenciaBloqueioBCAAtualizacao.setMunicipio(aemnpp22.getMunicipio());
                consistenciaBloqueioBCAAtualizacao.setNome(aemnpp22.getNome());
                consistenciaBloqueioBCAAtualizacao.setMensagem(aemnpp22.getMensagemBaseLocal());
                consistenciaBloqueioBCAAtualizacao.setQte(aemnpp22.getQteLocal());

                consistenciaBloqueioBCAAtualizacao.setAcao(ConsistenciaBloqueioBCAAtualizacaoAcaoEnum.CONSULTA);
                consistenciaBloqueioBCAAtualizacao.setAtivo(AtivoEnum.ATIVO);
                consistenciaBloqueioBCAAtualizacao.setOrigem(ConsistenciaBloqueioBCAAtualizacaoOrigemEnum.LOCAL);
                consistenciaBloqueioBCAAtualizacao.setSituacao(ConsistenciaBloqueioBCAAtualizacaoSituacaoEnum.CONSULTADO);

                consistenciaBloqueioBCAAtualizacao.setCodigoBloqueio(aemnpp22a.getCodigoBloqueio());
                consistenciaBloqueioBCAAtualizacao.setCodigoUfBloqueio(aemnpp22a.getCodigoUfBloqueio());
                consistenciaBloqueioBCAAtualizacao.setDataBloqueio(aemnpp22a.getDataBloqueio());
                consistenciaBloqueioBCAAtualizacao.setDataInicioPenalidade(aemnpp22a.getDataInicioPenalidade());
                consistenciaBloqueioBCAAtualizacao.setDescricaoBloqueio(aemnpp22a.getDescricaoBloqueio());
                consistenciaBloqueioBCAAtualizacao.setDocumentoGeradorBloqueio(aemnpp22a.getDocumentoGeradorBloqueio());
                consistenciaBloqueioBCAAtualizacao.setQtdPenalidadeBloqueio(aemnpp22a.getQtePenalidadeBloqueio());
                consistenciaBloqueioBCAAtualizacao.setQteTotalPenalidades(aemnpp22a.getQteTotalPenalidades());
                consistenciaBloqueioBCAAtualizacao.setRecolhimentoCnh(aemnpp22a.getRecolhimentoCnh());
                consistenciaBloqueioBCAAtualizacao.setRequisitoCurso(aemnpp22a.getRequisitoCurso());
                consistenciaBloqueioBCAAtualizacao.setRequisitoExame(aemnpp22a.getRequisitoExame());
                consistenciaBloqueioBCAAtualizacao.setTipoDecisao(aemnpp22a.getTipoDecisao());
                consistenciaBloqueioBCAAtualizacao.setTipoPrazoPenalidade(aemnpp22a.getTipoPrazoPenalidade());
                consistenciaBloqueioBCAAtualizacao.setUfBloqueio(aemnpp22a.getUfBloqueio());

                lConsulta.add((ConsistenciaBloqueioBCAAtualizacao) new AbstractJpaDAORepository().insert(em, consistenciaBloqueioBCAAtualizacao));
            }
        }

        return lConsulta;
    }

    /**
     *
     * @param em
     * @param lProcesso
     * @param aemnpp22
     * @return
     * @throws DatabaseException
     */
    public List<ConsistenciaBloqueioBCAAtualizacao> registraConsultaBloqueioNacional(EntityManager em, List<FunctionConsistenciaBloqueioBCAAtualizacao> lProcesso, AEMNPP22 aemnpp22) throws DatabaseException {

        List<ConsistenciaBloqueioBCAAtualizacao> lConsulta = new ArrayList();

        if (!DetranCollectionUtil.ehNuloOuVazio(aemnpp22.getDadosBloqueiosBca())) {

            for (Object objectAEMNPP22B : aemnpp22.getDadosBloqueiosBca()) {

                AEMNPP22B aemnpp22b = (AEMNPP22B) objectAEMNPP22B;

                ConsistenciaBloqueioBCAAtualizacao consistenciaBloqueioBCAAtualizacao = new ConsistenciaBloqueioBCAAtualizacao();

                consistenciaBloqueioBCAAtualizacao.setRegistro(aemnpp22.getRegistro());
                consistenciaBloqueioBCAAtualizacao.setCpf(aemnpp22.getCpf());
                consistenciaBloqueioBCAAtualizacao.setMunicipio(aemnpp22.getMunicipio());
                consistenciaBloqueioBCAAtualizacao.setNome(aemnpp22.getNome());
                consistenciaBloqueioBCAAtualizacao.setMensagem(aemnpp22.getMensagemBca());

                consistenciaBloqueioBCAAtualizacao.setAcao(ConsistenciaBloqueioBCAAtualizacaoAcaoEnum.CONSULTA);
                consistenciaBloqueioBCAAtualizacao.setAtivo(AtivoEnum.ATIVO);
                consistenciaBloqueioBCAAtualizacao.setOrigem(ConsistenciaBloqueioBCAAtualizacaoOrigemEnum.NACIONAL);
                consistenciaBloqueioBCAAtualizacao.setSituacao(ConsistenciaBloqueioBCAAtualizacaoSituacaoEnum.CONSULTADO);
                consistenciaBloqueioBCAAtualizacao.setQte(aemnpp22.getQteBca());

                consistenciaBloqueioBCAAtualizacao.setCodigoBloqueio(aemnpp22b.getCodigoBloqueio());
                consistenciaBloqueioBCAAtualizacao.setCodigoUfBloqueio(aemnpp22b.getCodigoUfBloqueio());
                consistenciaBloqueioBCAAtualizacao.setDataBloqueio(aemnpp22b.getDataBloqueio());
                consistenciaBloqueioBCAAtualizacao.setDataInicioPenalidade(aemnpp22b.getDataInicioPenalidade());
                consistenciaBloqueioBCAAtualizacao.setDescricaoBloqueio(aemnpp22b.getDescricaoBloqueio());
                consistenciaBloqueioBCAAtualizacao.setDocumentoGeradorBloqueio(aemnpp22b.getDocumentoGeradorBloqueio());
                consistenciaBloqueioBCAAtualizacao.setQtdPenalidadeBloqueio(aemnpp22b.getQtePenalidadeBloqueio());
                consistenciaBloqueioBCAAtualizacao.setQteTotalPenalidades(aemnpp22b.getQteTotalPenalidades());
                consistenciaBloqueioBCAAtualizacao.setRecolhimentoCnh(aemnpp22b.getRecolhimentoCnh());
                consistenciaBloqueioBCAAtualizacao.setRequisitoCurso(aemnpp22b.getRequisitoCurso());
                consistenciaBloqueioBCAAtualizacao.setRequisitoExame(aemnpp22b.getRequisitoExame());
                consistenciaBloqueioBCAAtualizacao.setTipoDecisao(aemnpp22b.getTipoDecisao());
                consistenciaBloqueioBCAAtualizacao.setTipoPrazoPenalidade(aemnpp22b.getTipoPrazoPenalidade());
                consistenciaBloqueioBCAAtualizacao.setUfBloqueio(aemnpp22b.getUfBloqueio());

                lConsulta.add((ConsistenciaBloqueioBCAAtualizacao) new AbstractJpaDAORepository().insert(em, consistenciaBloqueioBCAAtualizacao));
            }
        }

        return lConsulta;
    }

    /**
     *
     * @param em
     * @param lProcesso
     * @param aemnpp22
     * @return
     * @throws AppException
     */
    public List<ConsistenciaBloqueioBCAAtualizacao> registraConsultaBloqueioWeb(EntityManager em, List<FunctionConsistenciaBloqueioBCAAtualizacao> lProcesso, AEMNPP22 aemnpp22) throws AppException {

        List<ConsistenciaBloqueioBCAAtualizacao> lConsulta = new ArrayList();

        if (!DetranCollectionUtil.ehNuloOuVazio(lProcesso)) {

            for (FunctionConsistenciaBloqueioBCAAtualizacao functionConsistenciaBloqueioBCAAtualizacao : lProcesso) {

                List<BloqueioBCA> lBloqueio
                        = new BloqueioBCARepositorio().getBloqueioBCAPorCpfESituacaoEAtivo(em, functionConsistenciaBloqueioBCAAtualizacao.getCpf(), SituacaoBloqueioBCAEnum.ATIVO);

                if (!DetranCollectionUtil.ehNuloOuVazio(lBloqueio)) {

                    BigDecimal totalPenalidade
                            = new PAPenalidadeProcessoRepositorio().getTotalPenalidadeAtivaPorCpf(em, functionConsistenciaBloqueioBCAAtualizacao.getCpf());

                    for (BloqueioBCA bloqueio : lBloqueio) {

                        if (functionConsistenciaBloqueioBCAAtualizacao.getIdProcessoAdministrativo().equals(bloqueio.getProcessoAdministrativo().getId())) {

                            MovimentoCnh recolhimento
                                    = new MovimentoCnhRepositorio().getMovimentoPorProcessoAdministrativoEAcao(em, bloqueio.getProcessoAdministrativo().getId(), AcaoEntregaCnhEnum.ENTREGA);

                            PAPenalidadeProcesso penalidade
                                    = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, bloqueio.getProcessoAdministrativo().getId());

                            ConsistenciaBloqueioBCAAtualizacao consistenciaBloqueioBCAAtualizacao = new ConsistenciaBloqueioBCAAtualizacao();

                            consistenciaBloqueioBCAAtualizacao.setRegistro(bloqueio.getProcessoAdministrativo().getNumeroRegistro());
                            consistenciaBloqueioBCAAtualizacao.setCpf(bloqueio.getProcessoAdministrativo().getCpf());

                            if (bloqueio.getProcessoAdministrativo().getMunicipioCondutor() != null) {
                                consistenciaBloqueioBCAAtualizacao.setMunicipio(bloqueio.getProcessoAdministrativo().getMunicipioCondutor().toString());
                            }

                            consistenciaBloqueioBCAAtualizacao.setAcao(ConsistenciaBloqueioBCAAtualizacaoAcaoEnum.CONSULTA);
                            consistenciaBloqueioBCAAtualizacao.setAtivo(AtivoEnum.ATIVO);
                            consistenciaBloqueioBCAAtualizacao.setOrigem(ConsistenciaBloqueioBCAAtualizacaoOrigemEnum.WEB);
                            consistenciaBloqueioBCAAtualizacao.setSituacao(ConsistenciaBloqueioBCAAtualizacaoSituacaoEnum.CONSULTADO);

                            consistenciaBloqueioBCAAtualizacao.setQte(String.valueOf(lBloqueio.size()));
                            consistenciaBloqueioBCAAtualizacao.setDataBloqueio(Utils.formatDate(bloqueio.getDataInicio(), "yyyyMMdd"));
                            consistenciaBloqueioBCAAtualizacao.setCodigoBloqueio(recuperaCodigoBloqueio(bloqueio.getProcessoAdministrativo().getTipo()));

                            if (penalidade != null) {
                                consistenciaBloqueioBCAAtualizacao.setDataInicioPenalidade(Utils.formatDate(penalidade.getDataInicioPenalidade(), "yyyyMMdd"));
                                consistenciaBloqueioBCAAtualizacao.setQtdPenalidadeBloqueio(penalidade.getValor() != null ? penalidade.getValor().toString() : null);
                                consistenciaBloqueioBCAAtualizacao.setQteTotalPenalidades(totalPenalidade.toString());
                                consistenciaBloqueioBCAAtualizacao.setTipoPrazoPenalidade(recuperaTipoPrazo(penalidade.getUnidadePenal()));
                            }

                            consistenciaBloqueioBCAAtualizacao.setRecolhimentoCnh("2");

                            if (recolhimento != null) {
                                consistenciaBloqueioBCAAtualizacao.setRecolhimentoCnh("1");
                            }

                            consistenciaBloqueioBCAAtualizacao.setCodigoBloqueio("");
                            consistenciaBloqueioBCAAtualizacao.setDescricaoBloqueio(montaDescricaoBloqueio(bloqueio.getProcessoAdministrativo()));
                            consistenciaBloqueioBCAAtualizacao.setDocumentoGeradorBloqueio(bloqueio.getProcessoAdministrativo().getNumeroProcesso());

                            //aemnpp22
                            consistenciaBloqueioBCAAtualizacao.setNome(aemnpp22.getNome());
                            consistenciaBloqueioBCAAtualizacao.setRequisitoCurso(recuperaRequisitoCurso(bloqueio.getProcessoAdministrativo(), aemnpp22));
                            consistenciaBloqueioBCAAtualizacao.setRequisitoExame(recuperaRequisitoExame(bloqueio.getProcessoAdministrativo(), aemnpp22));

                            consistenciaBloqueioBCAAtualizacao.setUfBloqueio(EstadoEnum.MS.name());
                            consistenciaBloqueioBCAAtualizacao.setCodigoUfBloqueio("12");

                            consistenciaBloqueioBCAAtualizacao.setTipoDecisao("2");

                            if (bloqueio.getProcessoAdministrativo().isJuridico()) {
                                consistenciaBloqueioBCAAtualizacao.setTipoDecisao("1");
                            }

                            consistenciaBloqueioBCAAtualizacao.setMensagem("CONSULTA WEB COM SUCESSO");

                            lConsulta.add((ConsistenciaBloqueioBCAAtualizacao) new AbstractJpaDAORepository().insert(em, consistenciaBloqueioBCAAtualizacao));
                        }
                    }
                }
            }
        }
        return lConsulta;
    }

    /**
     *
     * @param em
     * @param lConsulta
     * @throws AppException
     */
    private void avaliaAtualizacaoBloqueioBCA(EntityManager em, List<ConsistenciaBloqueioBCAAtualizacao> lConsulta) throws AppException {

        if (!DetranCollectionUtil.ehNuloOuVazio(lConsulta)) {

            ImmutableMap<String, List<ConsistenciaBloqueioBCAAtualizacao>> mapChaveUnicaPorCondutor = getChaveUnicaPorCondutor(lConsulta);

            if (mapChaveUnicaPorCondutor != null && !mapChaveUnicaPorCondutor.isEmpty()) {

                for (Map.Entry<String, List<ConsistenciaBloqueioBCAAtualizacao>> entry : mapChaveUnicaPorCondutor.entrySet()) {

                    for (ConsistenciaBloqueioBCAAtualizacao consistenciaBloqueioBCAAtualizacao : entry.getValue()) {

                        ProcessoAdministrativo processoAdministrativo
                                = getProcessoAdministrativoService().getProcessoAdministrativo(consistenciaBloqueioBCAAtualizacao.getId());

                        getProcessoAdministrativoService()
                                .alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(
                                        processoAdministrativo,
                                        CODIGO_FLUXO_PROCESSO_ANDAMENTO_242,
                                        CODIGO_ANDAMENTO_242
                                );

//                        executaAEMNPP21(em, processoAdministrativo, null, null);
                        RetornoExecucaoAndamentoWrapper retorno
                                = new PAAndamento242().executaEspecifico(em, new ExecucaoAndamentoEspecificoWrapper(processoAdministrativo));

                        if (retorno == null || retorno.getProcessoAdministrativo() == null) {
                            DetranWebUtils.applicationMessageException("Processo Administrativo {0} falhou na atualizacao de bloqueio web para andamento 242.", null, processoAdministrativo.getId().toString());
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param em
     * @param processoAdministrativo
     * @param aemnpp22
     * @param dataInicioPrimeiroProcessoAdministrativoParaEnvioBCA
     */
    private void executaAEMNPP21(EntityManager em, ProcessoAdministrativo processoAdministrativo, AEMNPP22 aemnpp22, Date dataInicioPrimeiroProcessoAdministrativoParaEnvioBCA) throws AppException {

        ParametroEnvioIntegracao parametroEnvioIntegracao
                = montaParamsAEMNPP21InclusaoOuSubstituicao(em, processoAdministrativo, aemnpp22, dataInicioPrimeiroProcessoAdministrativoParaEnvioBCA);

        IBeanIntegracao iBeanIntegracao = getBloqueioService().executaGravacaoBloqueioBCA(parametroEnvioIntegracao);
    }

    /**
     *
     * @param em
     * @param processo
     * @param aemnpp22
     * @param dataInicioPrimeiroProcessoAdministrativoParaEnvioBCA
     * @return
     * @throws AppException
     */
    public ParametroEnvioIntegracao montaParamsAEMNPP21InclusaoOuSubstituicao(EntityManager em, ProcessoAdministrativo processo, AEMNPP22 aemnpp22, Date dataInicioPrimeiroProcessoAdministrativoParaEnvioBCA) throws AppException {

        ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();

        AEMNPP22A primeiroProcesso = DetranCollectionUtil.ehNuloOuVazio(aemnpp22.getDadosBloqueiosBaseLocal()) ? null : (AEMNPP22A) aemnpp22.getDadosBloqueiosBaseLocal().get(0);

        PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, processo.getId());

        ProcessoAdministrativoInfracao processoAdministrativoInfracao = null;

        if (!TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(processo.getTipo()) && !TipoProcessoEnum.CASSACAO_JUDICIAL.equals(processo.getTipo())) {

            List<ProcessoAdministrativoInfracao> infracoes = new ProcessoAdministrativoInfracaoRepositorio().getInfracoesPorProcessoAdministrativoID(em, processo.getId());

            if (DetranCollectionUtil.ehNuloOuVazio(infracoes)) {
                DetranWebUtils.applicationMessageException("Não foi possível encontrar infração para o processo!");
            }

            processoAdministrativoInfracao = infracoes.get(0);
        }

        MovimentoCnh recolhimento = new MovimentoCnhRepositorio().getMovimentoPorProcessoAdministrativoEAcao(em, processo.getId(), AcaoEntregaCnhEnum.ENTREGA);

        params.adicionarParametro(DetranStringUtil.preencherEspaco(processo.getCpf(), DetranTamanhoConstante.CPF, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(processo.getTipo().getTipoProcessoMainframe(), 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(Utils.formatDate(dataInicioPrimeiroProcessoAdministrativoParaEnvioBCA, "yyyyMMdd"), 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(Utils.formatDate(dataInicioPrimeiroProcessoAdministrativoParaEnvioBCA, "yyyyMMdd"), 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaCodigoRestricao(processo.getTipo()), 1, DetranStringUtil.TipoDadoEnum.NUMERICO));

        if (processoAdministrativoInfracao != null) {
            params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaArtigo(em, aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo(), processoAdministrativoInfracao.getId()), 26, DetranStringUtil.TipoDadoEnum.ALFA));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaInfracao(processoAdministrativoInfracao, aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo()), 4, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaNumeroAutoInfracao(processoAdministrativoInfracao, aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo()), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
        } else {
            params.adicionarParametro(DetranStringUtil.preencherEspaco("", 26, DetranStringUtil.TipoDadoEnum.ALFA));
            params.adicionarParametro(DetranStringUtil.preencherEspaco("9999", 4, DetranStringUtil.TipoDadoEnum.NUMERICO));
            params.adicionarParametro(DetranStringUtil.preencherEspaco("", 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
        }

        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaCodigoBloqueio(processo.getTipo()), 1, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("MS", 2, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("12", 2, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(processo.isJuridico() ? "1" : "2", 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recolhimento != null ? "1" : "2", 1, DetranStringUtil.TipoDadoEnum.NUMERICO));

        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaRequisitoCurso(processo, aemnpp22), 1, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaRequisitoExame(processo, aemnpp22), 1, DetranStringUtil.TipoDadoEnum.ALFA));

        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaTipoPrazo(penalidade.getUnidadePenal()), 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(primeiroProcesso != null ? primeiroProcesso.getQtePenalidadeBloqueio() : penalidade.getValor().toString(), 5, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaQtdTotalPenalidade(aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo(), penalidade.getValor()), 5, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(recuperaNumeroRestricao(aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo()), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(montaDescricaoBloqueio(processo), 120, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("RES. 723/2018", 120, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("PROC." + processo.getNumeroProcesso().substring(4) + "/" + processo.getNumeroProcesso().substring(0, 4), 50, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(montaCampoProcessos(em, processo.getCpf()), 1000, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 2, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 2, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 8, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 30, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 120, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(defineAtualizacaoBaseLocal(aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo()), 1, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(defineAtualizacaoBca(aemnpp22.getDadosBloqueiosBca(), processo.getTipo()), 1, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("S".equals(defineAtualizacaoBca(aemnpp22.getDadosBloqueiosBca(), processo.getTipo())) ? "S" : "N", 1, DetranStringUtil.TipoDadoEnum.ALFA));

        return params;
    }

    private String recuperaCodigoRestricao(TipoProcessoEnum tipo) {

        String codigoRestricao = "";

        if (TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)) {
            codigoRestricao = "1";
        }

        if (TipoProcessoEnum.SUSPENSAO.equals(tipo)) {
            codigoRestricao = "2";
        }

        if (TipoProcessoEnum.CASSACAO.equals(tipo) || TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo)) {
            codigoRestricao = "3";
        }

        if (TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(tipo)) {
            codigoRestricao = "8";
        }

        return codigoRestricao;
    }

    private String recuperaArtigo(EntityManager em, List dadosBloqueioBaseLocal, TipoProcessoEnum tipo, Long idInfracao) throws DatabaseException {

        Object[] amparoLegal = (Object[]) new ProcessoAdministrativoInfracaoRepositorio().getAmparoLegalPorInfracaoId(em, idInfracao);

        String mbtArtigo = amparoLegal[2] == null ? "" : amparoLegal[2].toString();
        String mbtInciso = amparoLegal[4] == null ? "" : amparoLegal[4].toString();

        String artigo = mbtArtigo + mbtInciso;

        if (TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo) || TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)) {
            artigo = "0";
        } else {
            if (!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueioBaseLocal)) {
                for (Object bloqueio : dadosBloqueioBaseLocal) {
                    AEMNPP22A aemnpp22a = (AEMNPP22A) bloqueio;

                    String motivo = recuperaMotivo(tipo);

                    if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                        artigo = aemnpp22a.getArtigo();

                        break;
                    }
                }
            }

        }

        return artigo;
    }

    private String recuperaInfracao(ProcessoAdministrativoInfracao infracao, List dadosBloqueioBaseLocal, TipoProcessoEnum tipo) {
        String infracaoRetorno = infracao.getCodigoInfracao().substring(0, 4);

        if (TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo) || TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)) {
            infracaoRetorno = "9";
        } else {
            if (!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueioBaseLocal)) {
                for (Object bloqueio : dadosBloqueioBaseLocal) {
                    AEMNPP22A aemnpp22a = (AEMNPP22A) bloqueio;

                    String motivo = recuperaMotivo(tipo);

                    if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                        infracaoRetorno = aemnpp22a.getInfracao();

                        break;
                    }
                }
            }
        }

        return infracaoRetorno;
    }

    private String recuperaNumeroAutoInfracao(ProcessoAdministrativoInfracao infracao, List dadosBloqueioBaseLocal, TipoProcessoEnum tipo) {
        String numeroAutoInfracao = infracao.getAutoInfracao();

        if (TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo)
                || TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)) {
            numeroAutoInfracao = "";
        }

        if (TipoProcessoEnum.CASSACAO.equals(tipo)
                || TipoProcessoEnum.SUSPENSAO.equals(tipo)
                || TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(tipo)) {

            if (!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueioBaseLocal)) {
                for (Object bloqueio : dadosBloqueioBaseLocal) {
                    AEMNPP22A aemnpp22a = (AEMNPP22A) bloqueio;

                    String motivo = recuperaMotivo(tipo);

                    if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                        numeroAutoInfracao = aemnpp22a.getNumeroAutoInfracao();

                        break;
                    }
                }
            }
        }

        return numeroAutoInfracao;
    }

    private String recuperaCodigoBloqueio(TipoProcessoEnum tipo) {

        String codigoBloqueio = "";

        if (TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(tipo)) {
            codigoBloqueio = "A";
        }

        if (TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)) {
            codigoBloqueio = "D";
        }

        if (TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo)) {
            codigoBloqueio = "E";
        }

        return codigoBloqueio;
    }

    /**
     *
     * @param aemnpp22
     * @return
     */
    private String recuperaRequisitoCurso(ProcessoAdministrativo processo, AEMNPP22 aemnpp22) throws AppException {

        String requisitoCursoLocal = getRequisitoCursoBaseLocal(aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo());
        String requisitoCursoBCA = getRequisitoCursoBca(aemnpp22.getDadosBloqueiosBca(), processo.getTipo());

        if (!requisitoCursoLocal.equals(requisitoCursoBCA)) {
            DetranWebUtils.applicationMessageException("Tratativa regra de negócio requisito curso inválida.");
        }

        return requisitoCursoLocal;
    }

    /**
     *
     * @param aemnpp22
     * @return
     */
    private String recuperaRequisitoExame(ProcessoAdministrativo processo, AEMNPP22 aemnpp22) throws AppException {

        String requisitoExameLocal = getRequisitoExameBaseLocal(aemnpp22.getDadosBloqueiosBaseLocal(), processo.getTipo());
        String requisitoExameBCA = getRequisitoExameBca(aemnpp22.getDadosBloqueiosBca(), processo.getTipo());

        if (!requisitoExameLocal.equals(requisitoExameBCA)) {
            DetranWebUtils.applicationMessageException("Tratativa regra de negócio requisito exame inválida.");
        }

        return requisitoExameLocal;
    }

    /**
     *
     * @param unidadePenal
     * @return
     */
    private String recuperaTipoPrazo(UnidadePenalEnum unidadePenal) {

        String tipoPrazo = "";

        if (UnidadePenalEnum.DIA.equals(unidadePenal)) {
            tipoPrazo = "1";
        }

        if (UnidadePenalEnum.MES.equals(unidadePenal)) {
            tipoPrazo = "2";
        }

        if (UnidadePenalEnum.ANO.equals(unidadePenal)) {
            tipoPrazo = "3";
        }

        return tipoPrazo;
    }

    private String recuperaQtdTotalPenalidade(List dadosBloqueioBaseLocal, TipoProcessoEnum tipo, Integer valorPenalidade) {
        Integer qtdTotalPenalidade = valorPenalidade;

        if (!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueioBaseLocal)) {
            for (Object bloqueio : dadosBloqueioBaseLocal) {
                AEMNPP22A aemnpp22a = (AEMNPP22A) bloqueio;

                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                    qtdTotalPenalidade += Integer.parseInt(aemnpp22a.getQteTotalPenalidades());
                }
            }
        }

        return qtdTotalPenalidade.toString();
    }

    private String recuperaNumeroRestricao(List<Object> dadosBloqueioBaseLocal, TipoProcessoEnum tipo) {
        String numeroRestricao = "1";

        if (!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueioBaseLocal)) {
            for (Object bloqueio : dadosBloqueioBaseLocal) {
                AEMNPP22A aemnpp22a = (AEMNPP22A) bloqueio;

                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                    numeroRestricao = aemnpp22a.getNumeroRestricao();
                }
            }
        }

        return numeroRestricao;
    }

    private String montaDescricaoBloqueio(ProcessoAdministrativo processo) {
        String numeroPortaria
                = processo.getNumeroPortaria() != null
                ? processo.getNumeroPortaria().substring(4) + " / " + processo.getNumeroPortaria().substring(0, 4)
                : "";

        String descricaoBloqueio = "PORTARIA PENALIZACAO " + numeroPortaria;

        return descricaoBloqueio;
    }

    /**
     *
     * @param em
     * @param cpf
     * @return
     * @throws AppException
     */
    private List<AEMNPP21A> recuperaProcessos(EntityManager em, String cpf) throws AppException {

        List<AEMNPP21A> processosRetorno = new ArrayList();

        List<ProcessoAdministrativo> processos = new ProcessoAdministrativoRepositorio().getListProcessoAdministrativosAtivosPorCPFEntidadeCompleta(em, cpf);

        if (!DetranCollectionUtil.ehNuloOuVazio(processos)) {

            int n = processos.size() < 21 ? processos.size() : 21;

            for (int i = 0; i < n; i++) {

                ProcessoAdministrativo processo = processos.get(i);

                AEMNPP21A aemnpp21a = new AEMNPP21A();

                aemnpp21a.setDescricaoProcesso(DetranStringUtil.preencherEspaco(montaDescricaoProcesso(em, processo), 50, DetranStringUtil.TipoDadoEnum.ALFA));

                processosRetorno.add(aemnpp21a);
            }
        }

        return processosRetorno;
    }

    /**
     *
     * @param em
     * @param processo
     * @return
     * @throws AppException
     */
    private String montaDescricaoProcesso(EntityManager em, ProcessoAdministrativo processo) throws AppException {

        String descricaoProcesso;

        PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, processo.getId());

        String numeroProcesso = processo.getNumeroProcesso().substring(4) + "/" + processo.getNumeroProcesso().substring(0, 4);
        String unidadeMedida = penalidade.getUnidadePenal() == null ? "" : penalidade.getUnidadePenal().name();

        if (TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(processo.getTipo()) || TipoProcessoEnum.CASSACAO_JUDICIAL.equals(processo.getTipo())) {

            descricaoProcesso
                    = "PROC."
                    + numeroProcesso
                    + " "
                    + penalidade.getValor().toString()
                    + unidadeMedida;

        } else {

            List<NotificacaoComplemento> complementos
                    = new NotificacaoComplementoRepositorio()
                            .getListNotificacaoComplementoPorProcessoAdministrativo(em, processo.getId());

            if (DetranCollectionUtil.ehNuloOuVazio(complementos)) {
                DetranWebUtils.applicationMessageException("Não foi possível encontrar a portaria deste PA.");
            }

            String dataPortaria = Utils.formatDate(complementos.get(0).getData(), "dd/MM/yyyy");
            String numeroPortaria = complementos.get(0).getNumeroPortaria().substring(4) + "/" + complementos.get(0).getNumeroPortaria().substring(0, 4);

            descricaoProcesso
                    = "PROC."
                    + numeroProcesso
                    + " PORT."
                    + numeroPortaria
                    + " DE "
                    + dataPortaria
                    + " "
                    + penalidade.getValor().toString()
                    + unidadeMedida;
        }

        return descricaoProcesso;
    }

    private String montaCampoProcessos(EntityManager em, String cpf) throws AppException {

        String campoProcessos = "";

        List<AEMNPP21A> processos = recuperaProcessos(em, cpf);

        for (AEMNPP21A aemnpp21a : processos) {
            campoProcessos += aemnpp21a.getDescricaoProcesso();
        }

        return campoProcessos;
    }

    /**
     *
     * @param dadosBloqueiosBaseLocal
     * @param tipo
     * @return
     */
    private String getRequisitoExameBaseLocal(List<Object> dadosBloqueiosBaseLocal, TipoProcessoEnum tipo) {

        String requisitoExame = "N";

        if (!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueiosBaseLocal)) {

            for (Object aemnpp22aObject : dadosBloqueiosBaseLocal) {

                AEMNPP22A aemnpp22a = (AEMNPP22A) aemnpp22aObject;

                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                    requisitoExame = aemnpp22a.getRequisitoExame();
                    return requisitoExame;
                }
            }
        }

        return requisitoExame;
    }

    /**
     *
     * @param dadosBloqueiosBaseLocal
     * @param tipo
     * @return
     */
    private String getRequisitoCursoBaseLocal(List<Object> dadosBloqueiosBaseLocal, TipoProcessoEnum tipo) {

        String requisitoCurso = "N";

        if (!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueiosBaseLocal)) {

            for (Object aemnpp22aObject : dadosBloqueiosBaseLocal) {

                AEMNPP22A aemnpp22a = (AEMNPP22A) aemnpp22aObject;

                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                    requisitoCurso = aemnpp22a.getRequisitoCurso();
                    return requisitoCurso;
                }
            }
        }

        return requisitoCurso;
    }

    private String defineAtualizacaoBaseLocal(List<Object> dadosBloqueiosBaseLocal, TipoProcessoEnum tipo) {

        String atualizacao = "I";

        if (!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueiosBaseLocal)) {

            for (Object bloqueio : dadosBloqueiosBaseLocal) {

                AEMNPP22A aemnpp22a = (AEMNPP22A) bloqueio;

                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22a.getCodigoBloqueio())) {
                    atualizacao = "S";
                    break;
                }
            }
        }

        return atualizacao;
    }

    /**
     *
     * @param dadosBloqueiosBca
     * @param tipo
     * @return
     */
    private String getRequisitoExameBca(List<Object> dadosBloqueiosBca, TipoProcessoEnum tipo) {

        String requisitoExame = "N";

        if (!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueiosBca)) {

            for (Object aemnpp22bObject : dadosBloqueiosBca) {

                AEMNPP22B aemnpp22b = (AEMNPP22B) aemnpp22bObject;

                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22b.getCodigoBloqueio())) {
                    requisitoExame = aemnpp22b.getRequisitoExame();
                    return requisitoExame;
                }
            }
        }

        return requisitoExame;
    }

    /**
     *
     * @param dadosBloqueiosBca
     * @param tipo
     * @return
     */
    private String getRequisitoCursoBca(List<Object> dadosBloqueiosBca, TipoProcessoEnum tipo) {

        String requisitoCurso = "N";

        if (!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueiosBca)) {

            for (Object aemnpp22bObject : dadosBloqueiosBca) {

                AEMNPP22B aemnpp22b = (AEMNPP22B) aemnpp22bObject;

                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22b.getCodigoBloqueio())) {
                    requisitoCurso = aemnpp22b.getRequisitoCurso();
                    return requisitoCurso;
                }
            }
        }

        return requisitoCurso;
    }

    /**
     *
     * @param dadosBloqueiosBca
     * @param tipo
     * @return
     */
    private String defineAtualizacaoBca(List<Object> dadosBloqueiosBca, TipoProcessoEnum tipo) {

        String atualizacao = "I";

        if (!DetranCollectionUtil.ehNuloOuVazio(dadosBloqueiosBca)) {

            for (Object bloqueio : dadosBloqueiosBca) {

                AEMNPP22B aemnpp22b = (AEMNPP22B) bloqueio;

                String motivo = recuperaMotivo(tipo);

                if (motivo.equals(aemnpp22b.getCodigoBloqueio())) {
                    atualizacao = "S";
                    break;
                }
            }
        }

        return atualizacao;
    }

    private String recuperaMotivo(TipoProcessoEnum tipo) {

        String motivo = "";

        if (TipoProcessoEnum.SUSPENSAO.equals(tipo)) {
            motivo = "2";
        }

        if (TipoProcessoEnum.CASSACAO.equals(tipo)) {
            motivo = "3";
        }

        if (TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(tipo)) {
            motivo = "A";
        }

        if (TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(tipo)) {
            motivo = "D";
        }

        if (TipoProcessoEnum.CASSACAO_JUDICIAL.equals(tipo)) {
            motivo = "E";
        }

        return motivo;
    }

    /**
     *
     * @param lConsulta
     * @return
     * @throws AppException
     */
    private ImmutableMap getChaveUnicaPorCondutor(List<ConsistenciaBloqueioBCAAtualizacao> lConsulta) {

        ImmutableListMultimap<String, ConsistenciaBloqueioBCAAtualizacao> listaMapeada = null;

        if (!DetranCollectionUtil.ehNuloOuVazio(lConsulta)) {

            listaMapeada
                    = Multimaps.index(lConsulta, new Function<ConsistenciaBloqueioBCAAtualizacao, String>() {
                        @Override
                        public String apply(ConsistenciaBloqueioBCAAtualizacao input) {
                            return input.getCpf() + input.getCodigoBloqueio() + input.getUfBloqueio();
                        }
                    });
        }

        return listaMapeada != null && !listaMapeada.isEmpty() ? listaMapeada.asMap() : null;
    }
}
