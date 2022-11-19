package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.UnidadePenalEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.MotivoBloqueioCnhEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.cenarios.IUltimoSequencialService;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoBloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoInfracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoRascunhoBloqueioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoBloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoBloqueioPessoa;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoRascunhoBloqueio;
import br.gov.ms.detran.processo.administrativo.entidade.Recurso;
import br.gov.ms.detran.processo.administrativo.entidade.RegraInstaurarEnum;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRascunhoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoMovimentoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ParametrosIntegracaoBloqueioBCAWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoBCAWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.BloqueioBCAWrapper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javax.persistence.EntityManager;



public class BloqueioBCABO {

    private static final Logger LOG = Logger.getLogger(BloqueioBCABO.class);

    IPAControleFalhaService paControleFalha;

    protected IPAControleFalhaService getControleFalha() {
        if (paControleFalha == null) {
            paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
        }

        return this.paControleFalha;
    }

    private final Map<TipoProcessoEnum, MotivoBloqueioCnhEnum> mapMotivoBloqueio;
    private ProcessoAdministrativoFalhaBO falhaBO = new ProcessoAdministrativoFalhaBO();

    public BloqueioBCABO() {
        this.mapMotivoBloqueio = new TreeMap<>();
        mapMotivoBloqueio.put(TipoProcessoEnum.SUSPENSAO, MotivoBloqueioCnhEnum.SUSPENSAO_DO_DIREITO_DE_DIRIGIR);
        mapMotivoBloqueio.put(TipoProcessoEnum.SUSPENSAO_JUDICIAL, MotivoBloqueioCnhEnum.MEDIDA_ADMINISTRATIVA);
        mapMotivoBloqueio.put(TipoProcessoEnum.CASSACAO, MotivoBloqueioCnhEnum.CASSACAO_CNH);
        mapMotivoBloqueio.put(TipoProcessoEnum.CASSACAO_JUDICIAL, MotivoBloqueioCnhEnum.DELITO_TRANSITO);
        mapMotivoBloqueio.put(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH, MotivoBloqueioCnhEnum.PERMISSIONARIO_PENALIZADO_APOS_EXPEDICAO_CNH);
    }

    /**
     * @param em
     * @return
     * @throws AppException
     */
    public List getBloqueiosBCA(EntityManager em) throws AppException {

        List<MovimentoBloqueioBCA> movimentoBloqueios = new ArrayList<>();

        List<BloqueioBCA> bloqueios
                = new BloqueioBCARepositorio().getBloqueiosPorAndamento(em, DetranCollectionUtil.montaLista(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_BLOQUEIO));

        for (BloqueioBCA bloqueio : bloqueios) {

            try {

                List<MovimentoBloqueioBCA> movimentosPorBloqueio = new MovimentoBloqueioBCARepositorio().getMovimentosPorBloqueioEAtivo(em, bloqueio.getId());

                if (DetranCollectionUtil.ehNuloOuVazio(movimentosPorBloqueio)) {
                    DetranWebUtils.applicationMessageException("Não foi possível recuperar informações sobre o Bloqueio.");
                }

                movimentoBloqueios.add(movimentosPorBloqueio.get(0));

            } catch (Exception e) {
                LOG.debug("Erro capturado", e);
                getControleFalha().gravarFalha(e, "Erro ao recuperar bloqueis BCA - WS");
            }
        }

        return montarBloqueiosBCA(em, movimentoBloqueios);
    }

    /**
     *
     * @param em
     * @param bloqueios
     * @return
     * @throws AppException
     */
    public List<BloqueioBCAWrapper> montarBloqueiosBCA(EntityManager em, List<MovimentoBloqueioBCA> bloqueios) throws AppException {

        List<BloqueioBCAWrapper> listaJson = new ArrayList();

        if (!DetranCollectionUtil.ehNuloOuVazio(bloqueios)) {

            for (MovimentoBloqueioBCA bloqueio : bloqueios) {

                try {

                    PAComplemento complemento
                            = new PAComplementoRepositorio()
                                    .getPAComplementoPorParametroEAtivo(
                                            em, bloqueio.getBloqueioBCA().getProcessoAdministrativo(), PAParametroEnum.TEMPO_PENALIDADE
                                    );

                    listaJson
                            .add(
                                    new BloqueioBCAWrapper(
                                            bloqueio.getBloqueioBCA().getProcessoAdministrativo().getNumeroProcesso(),
                                            bloqueio.getBloqueioBCA().getSituacao(),
                                            bloqueio.getBloqueioBCA().getDataInicio(),
                                            bloqueio.getBloqueioBCA().getDataFim(),
                                            complemento == null ? "NAO_DEFINIDO" : complemento.getValor(),
                                            bloqueio.getBloqueioBCA().getMotivoBloqueio() != null ? bloqueio.getBloqueioBCA().getMotivoBloqueio().name() : null
                                    )
                            );

                } catch (Exception e) {
                    LOG.debug("Erro capturado", e);
                    getControleFalha().gravarFalha(e, "Erro na montagem do bloqueio para retorno - WS");
                }
            }
        }

        return listaJson;
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @param bloqueio
     * @param complemento
     * @return
     * @throws AppException
     */
    private ParametrosIntegracaoBloqueioBCAWrapper montarParametrosIntegracaoBloqueioBCAWrapper(
            EntityManager em, ProcessoAdministrativo processoAdministrativo, BloqueioBCA bloqueio, PAComplemento complemento) throws AppException {

        ParametrosIntegracaoBloqueioBCAWrapper wrapper = new ParametrosIntegracaoBloqueioBCAWrapper();
        
        Date dataInicioPenalidade;
        
        if(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(processoAdministrativo.getTipo()) || isPJUIndeterminado(em, processoAdministrativo)) {
            List<BloqueioBCA> bloqueios = new BloqueioBCARepositorio().getBloqueioBCAPorCpfESituacaoEAtivo(em, processoAdministrativo.getCpf(), SituacaoBloqueioBCAEnum.ATIVO);
            dataInicioPenalidade = bloqueios.get(bloqueios.size()-1).getDataInicio();
        } else {
            ProcessoRascunhoBloqueio rascunho = new ProcessoRascunhoBloqueioRepositorio().getRascunhoPorProcessoAdministrativo(em, processoAdministrativo);
            ProcessoBloqueioPessoa bloqueioPessoa = rascunho.getProcessoBloqueioPessoa();
            dataInicioPenalidade = bloqueioPessoa.getDataInicio();
        }
        
        montarDadosPortaria(em, processoAdministrativo, wrapper);
        montarDadosInfracao(em, processoAdministrativo, wrapper);

        wrapper.setPaTdcId(processoAdministrativo.getId());
        wrapper.setCpf(processoAdministrativo.getCpf());
        wrapper.setNumeroProcesso(processoAdministrativo.getNumeroProcesso());
        wrapper.setTipoProcesso(processoAdministrativo.getTipo());
        wrapper.setArtigoIncisoParagrafo(processoAdministrativo.getOrigemApoio().getArtigoInciso());
        wrapper.setDataBloqueio(bloqueio.getDataInicio());
        wrapper.setDataEntregaCnh(null);
        wrapper.setNumeroRestricao(bloqueio.getNumeroBloqueioBCA());
        wrapper.setNumeroRestricaoComBloqueio("0");
        wrapper.setNumeroRestricaoSemBloqueio(bloqueio.getNumeroBloqueioBCA());
        wrapper.setMesesPenalizacao(complemento.getValor());
        wrapper.setDataInicioPenalidade(dataInicioPenalidade);

        return wrapper;
    }

    /**
     *
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException
     */
    public ParametrosIntegracaoBloqueioBCAWrapper montarParametrosIntegracaoBloqueioBCAWrapper(EntityManager em,
            ProcessoAdministrativo processoAdministrativo) throws AppException {

        PAComplemento complemento
                = new PAComplementoRepositorio()
                        .getPAComplementoPorParametroEAtivo(em,
                                processoAdministrativo,
                                PAParametroEnum.TEMPO_PENALIDADE);

        BloqueioBCA bloqueio = new BloqueioBCARepositorio().getBloqueioBcaPorPaEAtivo(em, processoAdministrativo.getId());

        return montarParametrosIntegracaoBloqueioBCAWrapper(em, processoAdministrativo, bloqueio, complemento);

    }

    /**
     * @param em
     * @param processoAdministrativo
     * @param wrapper
     * @throws AppException
     */
    public void montarDadosInfracao(
            EntityManager em, ProcessoAdministrativo processoAdministrativo, ParametrosIntegracaoBloqueioBCAWrapper wrapper) throws AppException {

        List<ProcessoAdministrativoInfracao> infracoes
                = new ProcessoAdministrativoInfracaoRepositorio()
                        .getInfracoesPorProcessoAdministrativoID(em, processoAdministrativo.getId());

        if (!DetranCollectionUtil.ehNuloOuVazio(infracoes)) {
            wrapper.setAutoInfracao(infracoes.get(0).getAutoInfracao());
            wrapper.setCodigoInfracao(infracoes.size() > 1 ? "9999" : infracoes.get(0).getCodigoInfracao());
        }
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @param wrapper
     * @throws AppException
     */
    public void montarDadosPortaria(EntityManager em, ProcessoAdministrativo processoAdministrativo, ParametrosIntegracaoBloqueioBCAWrapper wrapper) throws AppException {

        if (!RegraInstaurarEnum.C1.equals(processoAdministrativo.getOrigemApoio().getRegra())) {

            if (!TipoProcessoEnum.CASSACAO_JUDICIAL.equals(processoAdministrativo.getTipo()) && !TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(processoAdministrativo.getTipo())) {

                List<NotificacaoComplemento> complementos
                        = new NotificacaoComplementoRepositorio()
                                .getListNotificacaoComplementoPorProcessoAdministrativo(em, processoAdministrativo.getId());

                if (DetranCollectionUtil.ehNuloOuVazio(complementos)) {
                    DetranWebUtils.applicationMessageException("Não foi possível encontrar a portaria deste PA.");
                }

                wrapper.setNumeroPortaria(complementos.get(0).getNumeroPortaria());
                wrapper.setDataPublicacaoPortaria(complementos.get(0).getData());
            }
        }
    }

    /**
     * @param em
     * @param bloqueio
     * @param idUsuario
     * @param tipo
     * @throws DatabaseException
     */
    public void gravarMovimentoBloqueioBCA(EntityManager em,
            BloqueioBCA bloqueio,
            Long idUsuario,
            TipoMovimentoBloqueioBCAEnum tipo) throws DatabaseException {

        MovimentoBloqueioBCA movimentoBloqueioBCA = new MovimentoBloqueioBCA();

        movimentoBloqueioBCA.setBloqueioBCA(bloqueio);
        movimentoBloqueioBCA.setTipo(tipo);
        movimentoBloqueioBCA.setUsuario(idUsuario);
        movimentoBloqueioBCA.setDataBCA(new Date());
        movimentoBloqueioBCA.setAtivo(AtivoEnum.ATIVO);

        new MovimentoBloqueioBCARepositorio().insert(em, movimentoBloqueioBCA);
    }

    /**
     * @param em * @param processoAdministrativo
     * @param complemento
     * @return
     * @throws AppException *
     */
    @Deprecated
    public BloqueioBCA gravarBloqueioBCA(EntityManager em, ProcessoAdministrativo processoAdministrativo, PAComplemento complemento) throws AppException {

        if (null == complemento || DetranStringUtil.ehBrancoOuNulo(complemento.getValor())) {
            DetranWebUtils.applicationMessageException("Erro ao buscar o complemento do processo administrativo.");
        }

        Date dataInicio = Calendar.getInstance().getTime();

        Date dataFim = Utils.addMonth(dataInicio, Integer.valueOf(complemento.getValor()));
        BloqueioBCA bloqueio = gravarBloqueioBCA(em, processoAdministrativo, dataInicio, dataFim, null);
        return bloqueio;
    }

    /**
     *
     * @param em
     * @param processoAdministrativo
     * @param dataInicio
     * @param valor
     * @param unidade
     * @return
     * @throws AppException
     */
    public BloqueioBCA gravarBloqueioBCA(EntityManager em,
            ProcessoAdministrativo processoAdministrativo,
            Date dataInicio,
            Integer valor,
            UnidadePenalEnum unidade) throws AppException {

        if (valor == null) {
            DetranWebUtils.applicationMessageException("Erro ao buscar o complemento do processo administrativo.");
        }

        Date dataFim;

        if (UnidadePenalEnum.DIA.equals(unidade)) {
            dataFim = Utils.addDayMonth(dataInicio, valor);
        } else {
            dataFim = Utils.addMonth(dataInicio, valor);
        }

        BloqueioBCA bloqueio = gravarBloqueioBCA(em, processoAdministrativo, dataInicio, dataFim, null);
        return bloqueio;
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @param dataInicio
     * @param dataFim
     * @param usuarioInclusao
     * @return
     * @throws AppException
     */
    public BloqueioBCA gravarBloqueioBCA(EntityManager em,
            ProcessoAdministrativo processoAdministrativo,
            Date dataInicio,
            Date dataFim,
            String usuarioInclusao) throws AppException {

        BloqueioBCA bloqueioAntigo = new BloqueioBCARepositorio().getBloqueioBcaPorPaEAtivo(em, processoAdministrativo.getId());

        if (null != bloqueioAntigo) {

            bloqueioAntigo.setAtivo(AtivoEnum.DESATIVADO);
            bloqueioAntigo.setSituacao(SituacaoBloqueioBCAEnum.FINALIZADO);
            new BloqueioBCARepositorio().update(em, bloqueioAntigo);

            List<MovimentoBloqueioBCA> listaMovimento
                    = new MovimentoBloqueioBCARepositorio()
                            .getMovimentosPorBloqueioEAtivo(em, bloqueioAntigo.getId());

            if (!DetranCollectionUtil.ehNuloOuVazio(listaMovimento)) {
                for (MovimentoBloqueioBCA movimento : listaMovimento) {
                    movimento.setAtivo(AtivoEnum.DESATIVADO);

                    new MovimentoBloqueioBCARepositorio().update(em, movimento);
                }
            }
        }

        return gravarBloqueioBca(em, processoAdministrativo, dataInicio, dataFim, usuarioInclusao);
    }

    /**
     * Gravação do registro na tabela TDK.
     *
     * @param em
     * @param processoAdministrativo
     * @param dataInicio
     * @param dataFim
     * @param usuarioInclusao
     * @return
     * @throws DatabaseException
     * @throws AppException
     */
    public BloqueioBCA gravarBloqueioBca(EntityManager em,
            ProcessoAdministrativo processoAdministrativo,
            Date dataInicio,
            Date dataFim,
            String usuarioInclusao) throws DatabaseException, AppException {

        BloqueioBCA bloqueio = new BloqueioBCA();

        if (!DetranStringUtil.ehBrancoOuNulo(usuarioInclusao)) {
            bloqueio.setDefineUsuarioSessao(false);
            bloqueio.setUsuarioInclusao(usuarioInclusao);
        }

        IUltimoSequencialService service = (IUltimoSequencialService) JNDIUtil.lookup("ejb/UltimoSequencialService");

        bloqueio.setNumeroBloqueioBCA(service.getNumeroBloqueioBCA());
        bloqueio.setProcessoAdministrativo(processoAdministrativo);
        bloqueio.setSituacao(SituacaoBloqueioBCAEnum.ATIVO);
        bloqueio.setDataInicio(dataInicio);
        bloqueio.setDataFim(Utils.addDayMonth(dataFim, -1));
        bloqueio.setAtivo(AtivoEnum.ATIVO);
        bloqueio.setMotivoBloqueio(mapMotivoBloqueio.get(processoAdministrativo.getTipo()));

        return new BloqueioBCARepositorio().insert(em, bloqueio);
    }

    /**
     * Quando ocorrer o bloqueio, deve verificar se existem outros processos em
     * paralelo antes da Fase Cumprimento de Pena. Caso exista, esses processos
     * serão Arquivados por Causa da Cassação. Com isso, os andamentos serão
     * finalizados e modificados para Fluxo 'Arquivamento Cassacao' (23).
     *
     * Alinhamento feito com analista Bruno sobre como validar "Fase Cumprimento
     * de Pena": Verificar BloqueioBCA (TB_TDK_BLOQUEIO_BCA), com Situação =
     * 'Ativo' e Ativo = 1. Se existir: Sem ação Se não existir: Verificar se
     * existe PAPenalidadeProcesso (TB_TEA_PAD_PENALIDADE_PROCESSO_PAT) com
     * Ativo = 1 Se existir: Sem ação. Se não existir: Mudança de fluxo.
     *
     * @param em
     * @param pa
     * @throws AppException
     */
    public void arquivarProcessosParalelos(EntityManager em, ProcessoAdministrativo pa) throws AppException {

        List<ProcessoAdministrativo> listaPas
                = new ProcessoAdministrativoRepositorio()
                        .getListProcessoAdministrativosAtivosPorCPFEntidadeCompleta(em, pa.getCpf());

        if (!DetranCollectionUtil.ehNuloOuVazio(listaPas)) {

            for (ProcessoAdministrativo paParalelo : listaPas) {

                if (!paParalelo.getId().equals(pa.getId())) {

                    BloqueioBCA bloqueio
                            = new BloqueioBCARepositorio()
                                    .getBloqueioBCAporPaESituacaoEAtivo(em,
                                            paParalelo.getId(),
                                            SituacaoBloqueioBCAEnum.ATIVO);

                    if (bloqueio == null) {

                        PAPenalidadeProcesso penalidade
                                = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, paParalelo.getId());

                        if (penalidade == null) {

                            new AndamentoProcessoAdministrativoManager2()
                                    .iniciarFluxo(em, paParalelo, PAFluxoProcessoConstante.FLUXO_ARQUIVAMENTO_CASSACAO);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param processoAdministrativo
     * @param msgExecucao
     * @return
     */
    private String getMensagemErroBloqueioBCA(ProcessoAdministrativo processoAdministrativo, String msgExecucao) {
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Algo ocorreu ao incluir Bloqueio na BCA. Número Processo: ");
        mensagem.append(processoAdministrativo.getNumeroProcesso());
        mensagem.append(". CPF condutor: ");
        mensagem.append(processoAdministrativo.getCpf());
        mensagem.append(". Mensagem retornada pela trasação AEMNPP13: ");
        mensagem.append(msgExecucao);
        return mensagem.toString();
    }

    /**
     * Bloquear condutor pela funcionalidade Controle Cnh PA.
     *
     * @param em
     * @param processo
     * @param fluxoPrincipal
     * @param idUsuario
     * @param penalidade
     * @return
     * @throws AppException
     */
    public BloqueioBCA bloquearCnhBCA(EntityManager em,
            ProcessoAdministrativo processo,
            boolean fluxoPrincipal,
            Long idUsuario,
            PAPenalidadeProcesso penalidade) throws AppException {

        BloqueioBCA bloqueio;

        if (penalidade == null) {
            penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, processo.getId());
        }

        if (fluxoPrincipal) {

            bloqueio = new BloqueioBCARepositorio().getBloqueioBCAporPA(em, processo.getId());

            gravarMovimentoBloqueioBCA(em, bloqueio, idUsuario, TipoMovimentoBloqueioBCAEnum.ALTERACAO);

            bloqueio.setDataFim(penalidade.getDataFimPenalidade());

            new BloqueioBCARepositorio().update(em, bloqueio);

        } else {

            bloqueio = gravarBloqueioBCA(em, processo, penalidade.getDataInicioPenalidade(), penalidade.getDataFimPenalidade(), penalidade.getUsuarioInclusao());

            gravarMovimentoBloqueioBCA(em, bloqueio, idUsuario, TipoMovimentoBloqueioBCAEnum.BLOQUEIO);
        }

        return bloqueio;
    }

    /**
     *
     * @param em
     * @param controleWrapper
     * @param bloqueio
     * @return
     * @throws AppException
     */
    public ParametrosIntegracaoBloqueioBCAWrapper montarWrapperAEMNPP14(
            EntityManager em, Date dataInicioPenalidade, ProcessoAdministrativo processo, BloqueioBCA bloqueio) throws AppException {

        ParametrosIntegracaoBloqueioBCAWrapper wrapper = new ParametrosIntegracaoBloqueioBCAWrapper();

        wrapper.setDataInicioPenalidade(dataInicioPenalidade);
        wrapper.setDataBloqueio(bloqueio.getDataInicio());
        wrapper.setNumeroRestricao(bloqueio.getNumeroBloqueioBCA());

        PAComplemento complemento
                = new PAComplementoRepositorio()
                        .getPAComplementoPorParametroEAtivo(em, processo, PAParametroEnum.TEMPO_PENALIDADE);

        wrapper.setCpf(processo.getCpf());
        wrapper.setPaTdcId(processo.getId());
        wrapper.setTipoProcesso(processo.getTipo());
        wrapper.setNumeroProcesso(processo.getNumeroProcesso());
        wrapper.setArtigoIncisoParagrafo(processo.getOrigemApoio().getArtigoInciso());
        wrapper.setMesesPenalizacao(complemento.getValor());

        montarDadosPortaria(em, processo, wrapper);
        montarDadosInfracao(em, processo, wrapper);

        return wrapper;
    }

    /**
     *
     * @param em
     * @param processo
     * @return
     * @throws AppException
     */
    public ParametrosIntegracaoBloqueioBCAWrapper montarWrapperAEMNPP14(
            EntityManager em, ProcessoAdministrativo processo) throws AppException {

        ParametrosIntegracaoBloqueioBCAWrapper wrapper = new ParametrosIntegracaoBloqueioBCAWrapper();

        PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, processo.getId());

        BloqueioBCA bloqueio = new BloqueioBCARepositorio().getBloqueioBCAporPA(em, processo.getId());

        Date dataInicioPenalidade;
        
        if (TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(processo.getTipo()) || isPJUIndeterminado(em, processo)) {
            List<BloqueioBCA> bloqueios = new BloqueioBCARepositorio().getBloqueioBCAPorCpfESituacaoEAtivo(em, processo.getCpf(), SituacaoBloqueioBCAEnum.ATIVO);
            dataInicioPenalidade = bloqueios.get(bloqueios.size() - 1).getDataInicio();
        } else {
            ProcessoRascunhoBloqueio rascunho = new ProcessoRascunhoBloqueioRepositorio().getRascunhoPorProcessoAdministrativo(em, processo);
            ProcessoBloqueioPessoa bloqueioPessoa = rascunho.getProcessoBloqueioPessoa();
            dataInicioPenalidade = bloqueioPessoa.getDataInicio();
        }

        if (penalidade == null || bloqueio == null ) {
            DetranWebUtils.applicationMessageException("Não foi possível localizar a penalidade e/ou bloqueio para este ProcessoAdministrativo.");
        }

        wrapper.setDataInicioPenalidade(dataInicioPenalidade);
        wrapper.setDataBloqueio(bloqueio.getDataInicio());
        wrapper.setNumeroRestricao(bloqueio.getNumeroBloqueioBCA());

        PAComplemento complemento
                = new PAComplementoRepositorio()
                        .getPAComplementoPorParametroEAtivo(em, processo, PAParametroEnum.TEMPO_PENALIDADE);

        wrapper.setCpf(processo.getCpf());
        wrapper.setPaTdcId(processo.getId());
        wrapper.setTipoProcesso(processo.getTipo());
        wrapper.setNumeroProcesso(processo.getNumeroProcesso());
        wrapper.setArtigoIncisoParagrafo(processo.getOrigemApoio().getArtigoInciso());
        wrapper.setMesesPenalizacao(complemento.getValor());

        montarDadosPortaria(em, processo, wrapper);
        montarDadosInfracao(em, processo, wrapper);

        return wrapper;
    }

    /**
     * @param em
     * @param processoAdministrativoBCAWrapper
     * @return
     * @throws AppException
     */
    public ParametrosIntegracaoBloqueioBCAWrapper recuperaDadosParaBloqueioBCA(
            EntityManager em, ProcessoAdministrativoBCAWrapper processoAdministrativoBCAWrapper) throws AppException {

        ParametrosIntegracaoBloqueioBCAWrapper wrapper = null;

        PAComplemento complemento
                = new PAComplementoRepositorio()
                        .getPAComplementoPorParametroEAtivo(
                                em,
                                processoAdministrativoBCAWrapper.getProcessoAdministrativo(),
                                PAParametroEnum.TEMPO_PENALIDADE
                        );

        BloqueioBCA bloqueio
                = new BloqueioBCARepositorio()
                        .getBloqueioBcaPorPaEAtivo(em, processoAdministrativoBCAWrapper.getProcessoAdministrativo().getId());

        if (bloqueio != null) {

            wrapper = new ParametrosIntegracaoBloqueioBCAWrapper();

            PAPenalidadeProcesso penalidade
                    = new PAPenalidadeProcessoRepositorio().
                            getPenalidadePorPA(em, processoAdministrativoBCAWrapper.getProcessoAdministrativo().getId());

            if (penalidade != null) {
                wrapper.setDataInicioPenalidade(penalidade.getDataInicioPenalidade());
            }

            wrapper.setDataBloqueio(bloqueio.getDataInicio());
            wrapper.setNumeroRestricao(bloqueio.getNumeroBloqueioBCA());
            wrapper.setNumeroRestricaoComBloqueio("0");
            wrapper.setNumeroRestricaoSemBloqueio(bloqueio.getNumeroBloqueioBCA());

            wrapper.setPaTdcId(processoAdministrativoBCAWrapper.getProcessoAdministrativo().getId());
            wrapper.setCpf(processoAdministrativoBCAWrapper.getProcessoAdministrativo().getCpf());
            wrapper.setNumeroProcesso(processoAdministrativoBCAWrapper.getProcessoAdministrativo().getNumeroProcesso());
            wrapper.setTipoProcesso(processoAdministrativoBCAWrapper.getProcessoAdministrativo().getTipo());
            wrapper.setArtigoIncisoParagrafo(processoAdministrativoBCAWrapper.getArtigoInciso());
            wrapper.setMesesPenalizacao(complemento.getValor());

            montarDadosPortaria(em, processoAdministrativoBCAWrapper.getProcessoAdministrativo(), wrapper);
            montarDadosInfracao(em, processoAdministrativoBCAWrapper.getProcessoAdministrativo(), wrapper);
        }

        return wrapper;
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @param idUsuario
     * @return
     * @throws DatabaseException
     */
    public ParametrosIntegracaoBloqueioBCAWrapper gravarBloqueioBCA(EntityManager em,
            ProcessoAdministrativo processoAdministrativo,
            Long idUsuario) throws AppException {

        PAComplemento complemento
                = new PAComplementoRepositorio()
                        .getPAComplementoPorParametroEAtivo(em,
                                processoAdministrativo,
                                PAParametroEnum.TEMPO_PENALIDADE);

        BloqueioBCA bloqueio = gravarBloqueioBCA(em, processoAdministrativo, complemento);

        gravarMovimentoBloqueioBCA(em, bloqueio, idUsuario, TipoMovimentoBloqueioBCAEnum.BLOQUEIO);

        ParametrosIntegracaoBloqueioBCAWrapper wrapper
                = montarParametrosIntegracaoBloqueioBCAWrapper(em,
                        processoAdministrativo,
                        bloqueio,
                        complemento);

        montarDadosInfracao(em, processoAdministrativo, wrapper);

        return wrapper;
    }

    public BloqueioBCA gravarBloqueioParaProcesso(EntityManager em, ProcessoAdministrativo processoAdministrativo, Date dataInicio) throws AppException {

        UnidadePenalEnum unidadePenal = UnidadePenalEnum.MES;

        PAComplemento complemento
                = new PAComplementoRepositorio()
                        .getPAComplementoPorParametroEAtivo(em,
                                processoAdministrativo,
                                PAParametroEnum.TEMPO_PENALIDADE);

        if (complemento == null) {
            DetranWebUtils.applicationMessageException("Não foi possível recuperar o complemento do PA.");
        }

        Integer tempoPenalidade = Integer.parseInt(complemento.getValor());

        if (processoAdministrativo.isJuridico()) {
            unidadePenal = UnidadePenalEnum.DIA;

            DadoProcessoJudicial dadoJuridico
                    = new DadoProcessoJudicialRepositorio().
                            getDadoProcessoJudicialPorPA(em, processoAdministrativo.getId());

            if (dadoJuridico == null || dadoJuridico.getRequisitoCursoBloqueio() == null) {
                DetranWebUtils.applicationMessageException("Dados do Processo Judicial inválidos.");
            }

            dataInicio = dadoJuridico.getDataBloqueio();
        }

        BloqueioBCA bloqueio
                = new BloqueioBCABO()
                        .gravarBloqueioBCA(em,
                                processoAdministrativo,
                                dataInicio,
                                tempoPenalidade,
                                unidadePenal);

        return bloqueio;
    }

    public Date defineDataInicioBloqueio(EntityManager em, ProcessoAdministrativo pa, PAParametroEnum tipoDesistencia) throws AppException {

        Date dataInicio;
        BloqueioBCA bloqueioMaisRecente = null;

        List<BloqueioBCA> bloqueios
                = new BloqueioBCARepositorio()
                        .getBloqueioBCAPorCpfESituacaoEAtivo(
                                em,
                                pa.getCpf(),
                                SituacaoBloqueioBCAEnum.ATIVO
                        );

        if (!DetranCollectionUtil.ehNuloOuVazio(bloqueios)) {
            bloqueioMaisRecente = bloqueios.get(0);
        }

        PAComplemento desistencia
                = new PAComplementoRepositorio().getPAComplementoPorParametroEAtivo(em, pa, tipoDesistencia);

        if (desistencia != null) {

            desistencia.setDataInclusao(Utils.getDate24Hrs(desistencia.getDataInclusao()));

            if (PAParametroEnum.DESISTENCIA_REC_INST_PEN.equals(tipoDesistencia)) {
                dataInicio = new NotificacaoComplementoRepositorio().getDataPenalizacaoPorProcessoAdministrativo(em, pa.getId());
            } else {
                dataInicio = validaDataInicioBloqueio(em, pa, bloqueioMaisRecente, desistencia);
            }
            if (bloqueioMaisRecente != null && bloqueioMaisRecente.getDataFim().after(dataInicio)) {
                dataInicio = Utils.addDayMonth(bloqueioMaisRecente.getDataFim(), 1);
            }
        } else {
            dataInicio = validaDataInicioBloqueio(em, pa, bloqueioMaisRecente, desistencia);
        }

        return dataInicio;
    }

    private Date validaDataInicioBloqueio(EntityManager em, ProcessoAdministrativo pa, BloqueioBCA bloqueioMaisRecente, PAComplemento desistencia) throws AppException {
        Date dataInicio;

        dataInicio = cenarioComRecursoCetran(em, pa, bloqueioMaisRecente, desistencia);

        if (dataInicio == null) {
            dataInicio = cenarioComRecursoJari(em, pa, bloqueioMaisRecente, desistencia);
        }
        if (dataInicio == null) {
            dataInicio = cenarioSemRecurso(em, pa, bloqueioMaisRecente, desistencia);
        }

        return dataInicio;
    }

    private Date cenarioComRecursoCetran(EntityManager em, ProcessoAdministrativo pa, BloqueioBCA bloqueioMaisRecente, PAComplemento desistencia) throws AppException {
        Recurso recursoDestinoCetran
                = new RecursoRepositorio().
                        getRecursoAtivoPorProcessoAdministrativoTipoDestinoENaoCancelado(
                                em,
                                pa.getId(),
                                TipoFasePaEnum.PENALIZACAO,
                                OrigemDestinoEnum.CETRAN
                        );

        if (teveRecurso(recursoDestinoCetran)) {

            NotificacaoProcessoAdministrativo notificacao
                    = new NotificacaoProcessoAdministrativoRepositorio().
                            getNotificacaoPorNumeroProcessoETipo(
                                    em,
                                    pa.getNumeroProcesso(),
                                    TipoFasePaEnum.ENTREGA_CNH
                            );

            if (notificacao != null) {
                if (teveEntregaCnhComDesistenciaAposRecurso(desistencia, notificacao, recursoDestinoCetran)) {
                    return regraDeBloqueioC2(notificacao);

                } else if (teveEntregaCnhComDesistenciaAntesDoRecurso(desistencia, notificacao, recursoDestinoCetran)) {
                    return regraDeBloqueioC3(desistencia);

                } else if (bloqueioMaisRecente != null && bloqueioMaisRecente.getDataFim().after(notificacao.getDataPrazoLimite())) {
                    return Utils
                            .addDayMonth(bloqueioMaisRecente.getDataFim(), 1);
                }
            }
        }

        return null;
    }

    private Date cenarioComRecursoJari(EntityManager em, ProcessoAdministrativo pa, BloqueioBCA bloqueioMaisRecente, PAComplemento desistencia) throws AppException {
        Recurso recursoDestinoJari
                = new RecursoRepositorio().
                        getRecursoAtivoPorProcessoAdministrativoTipoDestinoENaoCancelado(
                                em,
                                pa.getId(),
                                TipoFasePaEnum.PENALIZACAO,
                                OrigemDestinoEnum.JARI);

        if (teveRecurso(recursoDestinoJari)) {

            NotificacaoProcessoAdministrativo notificacao = new NotificacaoProcessoAdministrativoRepositorio().
                    getNotificacaoPorNumeroProcessoETipo(
                            em,
                            pa.getNumeroProcesso(),
                            TipoFasePaEnum.JARI);

            if (notificacao != null) {
                if (teveEntregaCnhComDesistenciaAposRecurso(desistencia, notificacao, recursoDestinoJari)) {
                    return regraDeBloqueioC2(notificacao);

                } else if (teveEntregaCnhComDesistenciaAntesDoRecurso(desistencia, notificacao, recursoDestinoJari)) {
                    return regraDeBloqueioC3(desistencia);

                } else if (bloqueioMaisRecente != null && bloqueioMaisRecente.getDataFim().after(notificacao.getDataPrazoLimite())) {
                    return Utils.addDayMonth(bloqueioMaisRecente.getDataFim(), 1);
                }
            }
        }

        return null;
    }

    private Date cenarioSemRecurso(EntityManager em, ProcessoAdministrativo pa, BloqueioBCA bloqueioMaisRecente, PAComplemento desistencia) throws AppException {
        NotificacaoProcessoAdministrativo notificacao = new NotificacaoProcessoAdministrativoRepositorio().
                getNotificacaoPorNumeroProcessoETipo(
                        em,
                        pa.getNumeroProcesso(),
                        TipoFasePaEnum.PENALIZACAO);

        if (notificacao != null) {
            notificacao.setDataPrazoLimite(Utils.getDate24Hrs(notificacao.getDataPrazoLimite()));

            if (desistencia != null && desistencia.getDataInclusao().after(notificacao.getDataPrazoLimite())) {
                return regraDeBloqueioC1(notificacao);

            } else if (desistencia != null
                    && (desistencia.getDataInclusao().before(notificacao.getDataPrazoLimite())
                    || desistencia.getDataInclusao().equals(notificacao.getDataPrazoLimite()))) {
                return regraDeBloqueioC3(desistencia);

            } else if (bloqueioMaisRecente != null && bloqueioMaisRecente.getDataFim().after(notificacao.getDataPrazoLimite())) {
                return Utils.addDayMonth(bloqueioMaisRecente.getDataFim(), 1);
            }
        }

        return Calendar.getInstance().getTime();
    }

    private boolean teveRecurso(Recurso recurso) {
        return recurso != null;
    }

    private boolean teveEntregaCnhComDesistenciaAposRecurso(PAComplemento desistencia, NotificacaoProcessoAdministrativo notificacao, Recurso recurso) {
        return desistencia != null
                && (desistencia.getDataInclusao().after(Utils.getDate24Hrs(notificacao.getDataPrazoLimite()))
                || recurso.getSituacao() == SituacaoRecursoEnum.JULGADO);
    }

    private boolean teveEntregaCnhComDesistenciaAntesDoRecurso(PAComplemento desistencia, NotificacaoProcessoAdministrativo notificacao, Recurso recurso) {
        return desistencia != null
                && (desistencia.getDataInclusao().before(Utils.getDate24Hrs(notificacao.getDataPrazoLimite()))
                || desistencia.getDataInclusao().equals(Utils.getDate24Hrs(notificacao.getDataPrazoLimite()))
                || recurso.getSituacao() == SituacaoRecursoEnum.CANCELADO);
    }

    private Date regraDeBloqueioC1(NotificacaoProcessoAdministrativo notificacao) {
        return Utils.addDayMonth(notificacao.getDataPrazoLimite(), 15);
    }

    private Date regraDeBloqueioC2(NotificacaoProcessoAdministrativo notificacao) {
        return Utils.addDayMonth(notificacao.getDataPrazoLimite(), 1);
    }

    private Date regraDeBloqueioC3(PAComplemento desistencia) {
        return desistencia.getDataInclusao();
    }

    public BloqueioBCA gravarBloqueioBCAPorRascunho(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        ProcessoRascunhoBloqueio rascunho = new ProcessoRascunhoBloqueioRepositorio().getRascunhoPorProcessoAdministrativo(em, processoAdministrativo);

        if (Objects.isNull(rascunho) || !SituacaoRascunhoBloqueioEnum.NAO_BLOQUEADO.equals(rascunho.getSituacao())) {
            DetranWebUtils.applicationMessageException("Processo não possui rascunho de bloqueio ou sua situação é diferente de Não Bloqueado.");
        }

        if (rascunho.getPrioridade() > 1) {
            List<ProcessoRascunhoBloqueio> rascunhosPorBloqueioPessoa = new ProcessoRascunhoBloqueioRepositorio().getRascunhosBloqueadosPorBloqueioPessoa(em, rascunho.getProcessoBloqueioPessoa().getId());

            boolean existeRascunhoNaoBloqueado
                    = rascunhosPorBloqueioPessoa.stream().
                            filter(item -> item.getPrioridade() < rascunho.getPrioridade()).
                            anyMatch(item -> SituacaoRascunhoBloqueioEnum.NAO_BLOQUEADO.equals(item.getSituacao()));

            if (existeRascunhoNaoBloqueado) {
                DetranWebUtils.applicationMessageException("Não é possível incluir Bloqueio pois existe PAs para o mesmo CPF na fila para incluir Bloqueio.");
            }

        }

        BloqueioBCA bloqueio = gravarBloqueioBCA(em, processoAdministrativo, rascunho.getDataInicio(), Utils.addDayMonth(rascunho.getDataFim(), 1), null);

        new ProcessoRascunhoBloqueioBO().atualizarRascunhoParaBloqueadoWEB(em, rascunho);

        return bloqueio;
    }

    private boolean isPJUIndeterminado(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws DatabaseException {
        DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, processoAdministrativo.getId());
        
        return pju != null && pju.getIndicativoPrazoIndeterminado().equals(BooleanEnum.SIM);
    }
}
