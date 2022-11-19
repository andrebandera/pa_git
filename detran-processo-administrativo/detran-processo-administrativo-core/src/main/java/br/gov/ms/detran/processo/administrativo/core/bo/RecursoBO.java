package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.constantes.CodigoRegraNegocio;
import br.gov.ms.detran.comum.core.projeto.entidade.ace.MenuAplicacao;
import br.gov.ms.detran.comum.core.projeto.entidade.ace.RegraNegocio;
import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.RepresentanteLegal;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.TemplateProtocolo;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.Notificacao;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoArquivo;
import br.gov.ms.detran.comum.core.projeto.enums.adm.ResponsavelProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.apo.ModuloTipoArquivoEnum;
import br.gov.ms.detran.comum.core.projeto.wrapper.ace.MenuComponenteWrapper2;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.TipoExtensaoArquivoEnum;
import br.gov.ms.detran.comum.iface.regra.IRetornoRegraNegocio;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.*;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.ace.IAcessoService;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.core.iface.servico.hab.IControleCnhService;
import br.gov.ms.detran.core.iface.servico.regra.IRegraNegocioService;
import br.gov.ms.detran.core.util.ParametroAdicional;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import static br.gov.ms.detran.processo.administrativo.constantes.TipoArquivoPAConstante.PROTOCOLO_PA;
import br.gov.ms.detran.processo.administrativo.core.repositorio.*;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.entidade.*;
import br.gov.ms.detran.processo.administrativo.enums.*;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoCanceladoRetornoWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoCanceladoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursosWrapper;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecursoBO {

    private static final Logger LOG = Logger.getLogger(RecursoBO.class);

    private IAcessoService acessoService;
    private IApoioService apoioService;
    private IRegraNegocioService regraNegocioService;

    private IPAControleFalhaService falhaService;
    private IControleCnhService controleCnhService;

    public IPAControleFalhaService getFalhaService() {

        if (falhaService == null) {
            falhaService = (IPAControleFalhaService) JNDIUtil.lookup("ejb/PAControleFalhaService");
        }

        return falhaService;
    }

    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    public IAcessoService getAcessoService() {
        if (acessoService == null) {
            acessoService = (IAcessoService) JNDIUtil.lookup("ejb/AcessoService");
        }
        return acessoService;
    }

    private IRegraNegocioService getRegraNegocioService() {
        if (regraNegocioService == null) {
            regraNegocioService = (IRegraNegocioService) JNDIUtil.lookup("ejb/RegraNegocioService");
        }
        return regraNegocioService;
    }

    public IControleCnhService getControleCnhService() {

        if (controleCnhService == null) {
            controleCnhService = (IControleCnhService) JNDIUtil.lookup("ejb/ControleCnhService");
        }

        return controleCnhService;
    }

    /**
     * Gravação do Recurso, RecursoMovimento e Protocolo.
     *
     * @param em
     * @param wrapper
     * @param usuarioLogado
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    public void gravar(EntityManager em, RecursoWrapper wrapper, DetranUserDetailsWrapper usuarioLogado) throws AppException {

        Protocolo protocolo
                = gravarProtocolo(
                        em,
                        usuarioLogado,
                        wrapper,
                        TipoSituacaoProtocoloEnum.APRESENTACAO,
                        (Notificacao) getApoioService().getEditalCorpoPorTipo44()
                );

        // Tarefa #7093 - Remover campos da funcionalidade.
        wrapper.getEntidade().setSituacao(SituacaoRecursoEnum.EM_ANALISE);

        wrapper.getEntidade().setAtivo(AtivoEnum.ATIVO);
        wrapper.getEntidade().setDataRecurso(Calendar.getInstance().getTime());

        wrapper
                .getEntidade()
                .setDestinoFase(
                        new DestinoFaseRepositorio()
                                .getDestinoFaseParaAberturaRecurso(
                                        em,
                                        wrapper.getEntidade().getProcessoAdministrativo(),
                                        wrapper.getDestino()
                                )
                );

        /**
         * Define o tipo de recurso baseado destino fase e andamento do pa. *
         */
        wrapper
                .getEntidade()
                .setTipoRecurso(
                        defineTipoRecurso(
                                em,
                                wrapper.getEntidade().getProcessoAdministrativo(),
                                wrapper.getEntidade().getDestinoFase().getFluxoFase()
                        )
                );

        /**
         * Restrição - Define Data Recurso. *
         */
        validaDataRecurso(em, wrapper);

        wrapper.getEntidade().setDataRecurso(wrapper.getDataRecurso());
        new RecursoRepositorio().insertOrUpdate(em, wrapper.getEntidade());

        new RecursoMovimentoBO().gravar(em, protocolo, wrapper, usuarioLogado);

        /**
         * Andamento/Fluxo. *
         */
        PATipoCorpoAndamento pATipoCorpoAndamento = new PATipoCorpoAndamentoRepositorio().
                getPATipoCorpoAndamentoAtivoPorPA(em, wrapper.getEntidade().getProcessoAdministrativo().getId());

        new PAInicioFluxoBO().gravarInicioFluxo(em,
                wrapper.getEntidade().getProcessoAdministrativo(),
                pATipoCorpoAndamento.getFluxoProcessoRecurso().getCodigo());

        wrapper.setProtocolo(protocolo);
    }

    /**
     * @param em
     * @param usuarioLogado
     * @param wrapper
     * @return
     * @throws AppException
     */
    private Protocolo gravarProtocolo(EntityManager em,
            DetranUserDetailsWrapper usuarioLogado,
            RecursoWrapper wrapper,
            TipoSituacaoProtocoloEnum tipoSituacao,
            Notificacao editalCorpo) throws AppException {

        PAFluxoFase fluxoFase
                = new PAFluxoFaseRepositorio()
                        .getFluxoFaseDoProcessoAdministrativo(
                                em,
                                wrapper.getEntidade().getProcessoAdministrativo());

        Protocolo protocolo = new Protocolo();

        TemplateProtocolo template = (TemplateProtocolo) getControleCnhService().
                gravarTemplateProtocoloRecurso(
                        usuarioLogado.getOperador().getPostoAtendimento(),
                        usuarioLogado.getOperador().getUsuario(),
                        wrapper.getForma(),
                        tipoSituacao,
                        wrapper.getResponsavel(),
                        wrapper.getObservacao(),
                        Calendar.getInstance().getTime()
                );

        protocolo.setFluxoFase(fluxoFase);
        protocolo.setEditalCorpo(editalCorpo.getId());
        protocolo.setNumeroProtocolo(new ProtocoloBO().recuperarNumeroProtocolo());
        protocolo.setNumeroProcesso(wrapper.getEntidade().getProcessoAdministrativo());
        protocolo.setByteCodigoBarra(DetranCodigoBarraHelper.codigoBarra2de5(DetranStringUtil.preencherEspaco(protocolo.getNumeroProtocolo(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO)));
        protocolo.setAtivo(AtivoEnum.ATIVO);
        protocolo.setTemplateProtocolo(template.getId());

        protocolo = new ProtocoloRepositorio().insertOrUpdate(em, protocolo);

        if (ResponsavelProtocoloEnum.REPRESENTANTE_LEGAL.equals(wrapper.getResponsavel())) {

            getControleCnhService().gravarRepresentanteLegal(template, wrapper.getRepresentanteLegal());
        }

        return protocolo;
    }

    public byte[] getArquivoProtocoloRecursoOnline(RecursoWrapper wrapper) throws AppException {

        Map<String, String> parametros = new HashMap<>();
        parametros.put("protocoloId", wrapper.getProtocolo().getId().toString());

        return DetranHTTPUtil.download(
                wrapper.getUrlReportBirt()
                + DetranReportsUtil
                        .getReportParamsBirtUrl(
                                "recurso_apresentacao",
                                FormatoRelatorioEnum.PDF.getRptFormat(),
                                parametros,
                                "relatorios/processoadministrativo/recurso/"
                        ));
    }

    public ResultadoRecurso gravarResultadoRecurso(EntityManager em, RecursoWSWrapper wrapper, Recurso recurso) throws DatabaseException {

        ResultadoRecurso resultadoRecurso = new ResultadoRecurso();
        resultadoRecurso.setAtivo(AtivoEnum.ATIVO);
        resultadoRecurso.setData(wrapper.getDataJulgamento());
        resultadoRecurso.setRecurso(recurso);
        resultadoRecurso.setResultado(wrapper.getResultado());
        resultadoRecurso.setUsuario(wrapper.getIdUsuario());
        resultadoRecurso.setParecer(wrapper.getParecer());
        resultadoRecurso.setAcao(wrapper.getAcao());
        resultadoRecurso.setComissaoAnalise(wrapper.getComissaoAnalise());

        if (wrapper.getMotivoNaoConhecimento() != null) {
            resultadoRecurso.setMotivo(new MotivoAlegacaoRepositorio().getMotivoPorCodigo(em, wrapper.getMotivoNaoConhecimento()));
        }

        recurso.setSituacao(SituacaoRecursoEnum.JULGADO);

        new ResultadoRecursoRepositorio().insert(em, resultadoRecurso);
        new RecursoRepositorio().update(em, recurso);

        return resultadoRecurso;
    }

    /**
     * @param em
     * @return
     * @throws AppException
     */
    public List getRecursos(EntityManager em) throws AppException {

        List<RecursosWrapper> listaJson = new ArrayList();

        List<Integer> andamentosRecursos
                = DetranCollectionUtil
                        .montaLista(
                                PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_INSTAURACAO.INCLUIR_RECURSO_NOTIFICACAO_INSTAURACAO,
                                PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_PENALIZACAO.INCLUIR_RECURSO_NOTIFICACAO_PENALIZACAO,
                                PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_ENTREGA_CNH.INCLUIR_RECURSO_NOTIFICACAO_ENTREGA_CNH,
                                PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_DESENTRANHAMENTO.RECURSO_NOT_DESENTRANHAMENTO
                        );

        List<RecursoMovimento> lRecursoMovimento
                = new RecursoMovimentoRepositorio()
                        .getRecursosPorAndamentosESituacao(em, andamentosRecursos);

        if (!DetranCollectionUtil.ehNuloOuVazio(lRecursoMovimento)) {

            for (RecursoMovimento recursoMovimento : lRecursoMovimento) {

                RecursosWrapper recursoWrapper = preencherRecursoWrapper(em, recursoMovimento);

                listaJson.add(recursoWrapper);
            }
        }

        return listaJson;
    }

    public RecursosWrapper preencherRecursoWrapper(EntityManager em, RecursoMovimento recursoMovimento) throws AppException {
        String nomeUsuario = null;
        String cpfUsuario = null;
        /**
         * Usuário -> CPF e NOME. *
         */
        if (recursoMovimento.getUsuario() != null) {

            Usuario usuario = (Usuario) getApoioService().getUsuario(recursoMovimento.getUsuario());

            if (usuario != null) {

                nomeUsuario = getApoioService().getNomeUsuarioPeloId(usuario);
                cpfUsuario = getApoioService().getCpfUsuarioPeloId(usuario);
            }
        }
        /**
         * Posto Atendimento. *
         */
        String postoAtendimentoNome = null;

        TemplateProtocolo template = (TemplateProtocolo) getControleCnhService().
                getTemplateProtocoloPorID(recursoMovimento.getProtocolo().getTemplateProtocolo());
        if (template != null) {
            postoAtendimentoNome
                    = template.getPostoAtendimento().getDescricao();
        }
        OrigemDestinoEnum origem = null;
        if (recursoMovimento.getRecurso().getDestinoFase() != null) {
            origem = recursoMovimento.getRecurso().getDestinoFase().getOrigemDestino();
        }
        RecursosWrapper recursoWrapper
                = new RecursosWrapper(
                        recursoMovimento.getRecurso().getProcessoAdministrativo().getNumeroProcesso(),
                        recursoMovimento.getProtocolo().getNumeroProtocolo(),
                        recursoMovimento.getRecurso().getTipoRecurso(),
                        recursoMovimento.getRecurso().getSituacao(),
                        recursoMovimento.getRecurso().getDataRecurso(),
                        template.getObservacao(),
                        nomeUsuario,
                        cpfUsuario,
                        origem,
                        postoAtendimentoNome,
                        template.getFormaProtocolo(),
                        recursoMovimento.getIndiceForaPrazo()
                );
        if (recursoMovimento.getProtocolo() != null) {

            RepresentanteLegal representanteLegal
                    = (RepresentanteLegal) getControleCnhService()
                            .getRepresentanteLegalPorTemplateProtocolo(template.getId());

            if (representanteLegal != null) {
                recursoWrapper.setCpfRepresentanteLegal(representanteLegal.getCpf());
                recursoWrapper.setNomeRepresentanteLegal(representanteLegal.getNome());
            }
        }

        /**
         * Tempestividade e Recurso Online*
         */
        recursoWrapper.setTempestividade(BooleanEnum.NAO);
        recursoWrapper.setRecursoOnline(BooleanEnum.NAO);

        if (recursoMovimento.getRecurso() != null) {

            RecursoPAOnline recursoPAOnline = new RecursoPAOnlineRepositorio().getRecursoOnlinePorRecurso(em, recursoMovimento.getRecurso().getId());

            if (recursoPAOnline != null) {

                /**
                 * Tempestividade *
                 */
                recursoWrapper.setTempestividade(recursoPAOnline.getIndiceTempestividade());

                /**
                 * Recurso Online *
                 */
                recursoWrapper.setRecursoOnline(BooleanEnum.SIM);
            }
        }

        return recursoWrapper;
    }

    /**
     * @param em
     * @param usuarioLogado
     * @param wrapper
     * @return
     * @throws DatabaseException
     * @throws AppException
     */
    public Recurso cancelarRecurso(
            EntityManager em, DetranUserDetailsWrapper usuarioLogado, RecursoWrapper wrapper) throws AppException {

        wrapper.setEntidade(new RecursoRepositorio().find(em, Recurso.class, wrapper.getEntidade().getId()));

        validarCancelar(em, wrapper);

        PATipoCorpoAndamento pATipoCorpoAndamento = new PATipoCorpoAndamentoRepositorio().
                getPATipoCorpoAndamentoPorIDRecurso(em, wrapper.getEntidade().getId());

        new PAInicioFluxoBO().gravarInicioFluxo(em,
                wrapper.getEntidade().getProcessoAdministrativo(),
                pATipoCorpoAndamento.getFluxoProcessoRecursoCancelado().getCodigo());

        return cancelarRecursoSemValidar(em, usuarioLogado, wrapper);
    }

    /**
     * @param em
     * @param usuarioLogado
     * @param wrapper
     * @return
     * @throws AppException
     * @throws DatabaseException
     */
    public Recurso cancelarRecursoSemValidar(EntityManager em, DetranUserDetailsWrapper usuarioLogado, RecursoWrapper wrapper) throws AppException, DatabaseException {

        Protocolo protocolo
                = gravarProtocolo(
                        em,
                        usuarioLogado,
                        wrapper,
                        TipoSituacaoProtocoloEnum.CANCELAMENTO,
                        (Notificacao) getApoioService().getEditalCorpoPorTipo45()
                );

        wrapper.getEntidade().setSituacao(SituacaoRecursoEnum.CANCELADO);

        new RecursoRepositorio().update(em, wrapper.getEntidade());

        new RecursoMovimentoBO().gravar(em, protocolo, wrapper, usuarioLogado);

        return wrapper.getEntidade();
    }

    /**
     * @param wrapper
     */
    private void validarCancelar(EntityManager em, RecursoWrapper wrapper) throws AppException {

        if (!SituacaoRecursoEnum.EM_ANALISE.equals(wrapper.getEntidade().getSituacao())) {
            DetranWebUtils.applicationMessageException("O recurso está com situação diferente de em análise. Não é possivel cancelar.");
        }

        PAOcorrenciaStatus ocorrenciaAtual
                = new PAOcorrenciaStatusRepositorio()
                        .getPAOcorrenciaStatusAtiva(em, wrapper.getEntidade().getProcessoAdministrativo().getId());

        if (ocorrenciaAtual == null) {
            DetranWebUtils.applicationMessageException("Erro ao recuperar o andamento atual do processo administrativo.");
        }

        if (!PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_INSTAURACAO.INCLUIR_RESULTADO_RECURSO_NOTIFICACAO_INSTAURACAO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())
                && !PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_PENALIZACAO.INCLUIR_RESULTADO_RECURSO_NOTIFICACAO_PENALIZACAO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())
                && !PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_ENTREGA_CNH.INCLUIR_RESULTADO_RECURSO_NOTIFICACAO_ENTREGA_CNH.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())
                && !PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_DESENTRANHAMENTO.RECURSO_NOT_DESENTRANHAMENTO_RESULTADO_BPMS.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {

            DetranWebUtils.applicationMessageException("Não é possível cancelar o recurso. Aguardando atualização BPMS.");
        }
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @param paFluxoFase
     * @return
     * @throws AppException
     */
    public TipoFasePaEnum defineTipoRecurso(
            EntityManager em, ProcessoAdministrativo processoAdministrativo, PAFluxoFase paFluxoFase) throws AppException {

        if (processoAdministrativo == null || paFluxoFase == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        PATipoCorpoAndamento pATipoCorpoAndamento = new PATipoCorpoAndamentoRepositorio().
                getPATipoCorpoAndamentoAtivoPorPA(em, processoAdministrativo.getId());

        return pATipoCorpoAndamento != null ? pATipoCorpoAndamento.getTipoNotificacaoProcesso() : null;
    }

    /**
     * @param em
     * @return
     * @throws AppException
     */
    public List<RecursoCanceladoRetornoWSWrapper> getRecursosCancelados(EntityManager em) throws AppException {

        List<RecursoMovimento> recursoMovimentosCancelados = new RecursoMovimentoRepositorio().getRecursosCancelados(em);

        List<RecursoMovimento> recursosCancelados = new ArrayList<>();
        for (RecursoMovimento recursoMovimento : recursoMovimentosCancelados) {
            if (getControleCnhService().
                    validaTipoTemplateProtocolo(recursoMovimento.getProtocolo().getTemplateProtocolo(),
                            TipoSituacaoProtocoloEnum.CANCELAMENTO)) {
                recursosCancelados.add(recursoMovimento);
            }
        }

        List<RecursoCanceladoRetornoWSWrapper> lRecursoCancelado = montarRecursosCancelados(em, recursosCancelados);

        return lRecursoCancelado;
    }

    /**
     * @param em
     * @param recursoMovimentosCancelados
     * @return
     * @throws AppException
     */
    public List<RecursoCanceladoRetornoWSWrapper> montarRecursosCancelados(EntityManager em,
            List<RecursoMovimento> recursoMovimentosCancelados) throws AppException {

        List<RecursoCanceladoRetornoWSWrapper> lRecursoCancelado = new ArrayList();

        for (RecursoMovimento recursoMovimento : recursoMovimentosCancelados) {

            try {

                RecursoMovimento recursoMovimentoApresentacao
                        = new RecursoMovimentoBO()
                                .getRecursoMovimentoPorRecursoETipoProtocolo(em,
                                        recursoMovimento.getRecurso(),
                                        TipoSituacaoProtocoloEnum.APRESENTACAO);

                Usuario usuario = (Usuario) getApoioService().getUsuario(recursoMovimento.getUsuario());

                TemplateProtocolo template = (TemplateProtocolo) getControleCnhService().
                        getTemplateProtocoloPorID(recursoMovimento.getProtocolo().getTemplateProtocolo());

                RecursoCanceladoRetornoWSWrapper wrapper
                        = new RecursoCanceladoRetornoWSWrapper(
                                recursoMovimento.getRecurso().getProcessoAdministrativo().getNumeroProcesso(),
                                recursoMovimento.getProtocolo().getNumeroProtocolo(),
                                recursoMovimento.getDataMovimento(),
                                recursoMovimentoApresentacao.getProtocolo().getNumeroProtocolo(),
                                template.getObservacao(),
                                recursoMovimento.getMotivoCancelamento() != null ? recursoMovimento.getMotivoCancelamento().name() : "");

                wrapper.setCpfUsuarioCancelou(getApoioService().getCpfUsuarioPeloId(usuario));
                wrapper.setNomeUsuario(getApoioService().getNomeUsuarioPeloId(usuario));

                if (recursoMovimento.getRecurso() != null && recursoMovimento.getRecurso().getTipoRecurso() != null) {
                    wrapper.setTipo(recursoMovimento.getRecurso().getTipoRecurso().name());
                }

                RepresentanteLegal representanteLegal
                        = (RepresentanteLegal) getControleCnhService().getRepresentanteLegalPorTemplateProtocolo(template.getId());

                if (representanteLegal != null) {
                    wrapper.setCpfRepresentanteLegal(representanteLegal.getCpf());
                    wrapper.setNomeRepresentanteLegal(representanteLegal.getNome());
                }

                lRecursoCancelado.add(wrapper);

            } catch (AppException e) {
                getFalhaService()
                        .gravarFalhaEspecifica(
                                recursoMovimento.getRecurso().getProcessoAdministrativo().getCpf(),
                                "Falha ao recuperar o recurso cancelado para o PA " + recursoMovimento.getRecurso().getProcessoAdministrativo().getNumeroProcesso(),
                                "WS-cancelados");
            }
        }

        return lRecursoCancelado;
    }

    /**
     * @param em
     * @param wrapper
     * @return
     * @throws AppException
     */
    public Protocolo validarProtocoloCancelamento(EntityManager em, RecursoCanceladoWrapper wrapper) throws AppException {
        Protocolo protocoCancelamento
                = new ProtocoloRepositorio()
                        .getProtocoloPorNumeroProtocoloETipoSituacao(em,
                                wrapper.getNumeroProtocolo(),
                                TipoSituacaoProtocoloEnum.CANCELAMENTO);
        if (protocoCancelamento == null) {
            DetranWebUtils.applicationMessageException("confirmacancelamentorecurso.validar.protocolonaoencontrado.exception");
        }
        if (!protocoCancelamento.getNumeroProcesso().getNumeroProcesso().equals(wrapper.getNumeroProcesso())) {
            DetranWebUtils.applicationMessageException("confirmacancelamentorecurso.validar.numeroprotocolo.exception");
        }
        return protocoCancelamento;
    }

    /**
     * Realizar as ações quando o recurso é ACOLHIDO ou PROVIDO para Processo
     * Administrativo que APENSADO ou AGRAVADO.
     *
     * @param em
     * @param processo
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    public void acoesApensamentoAgravamento(EntityManager em, ProcessoAdministrativo processo) throws AppException {

        recursoAcolhidoProvidoParaProcessoAdministrativoApensamento(em, processo);

        recursoAcolhidoProvidoParaProcessoAdministrativoAgravamento(em, processo);

    }

    /**
     * Realizar as ações quando o recurso é ACOLHIDO ou PROVIDO para Processo
     * Administrativo que APENSADO.
     *
     * Mudança para o fluxo FLUXO_ARQUIVAMENTO_APENSADO = 22.
     *
     * @param em
     */
    private void recursoAcolhidoProvidoParaProcessoAdministrativoApensamento(EntityManager em,
            ProcessoAdministrativo processo) throws AppException {

        ProcessoAdministrativoApensado paApensado
                = new ProcessoAdministrativoApensadoRepositorio()
                        .getApensadoPorPAOriginal(
                                em,
                                processo.getId());

        if (paApensado != null) {

            new AndamentoProcessoAdministrativoManager2()
                    .iniciarFluxo(em,
                            paApensado.getProcessoAdministrativoCassacao().getProcessoAdministrativo(),
                            PAFluxoProcessoConstante.FLUXO_ARQUIVAMENTO_APENSADO);
        }
    }

    /**
     * Realizar as ações quando o recurso é ACOLHIDO ou PROVIDO para Processo
     * Administrativo que AGRAVADO.
     *
     * @param em
     * @throws DatabaseException
     */
    private void recursoAcolhidoProvidoParaProcessoAdministrativoAgravamento(EntityManager em,
            ProcessoAdministrativo processo) throws DatabaseException {

        ProcessoAdministrativoAgravamentoRepositorio paAgravamentoRepo = new ProcessoAdministrativoAgravamentoRepositorio();

        ProcessoAdministrativoAgravamento paAgravamento
                = paAgravamentoRepo.getPorProcessoAdministrativo(em, processo.getId());

        if (paAgravamento != null) {

            ProcessoAdministrativoAgravamento noAcima
                    = paAgravamentoRepo.getPorProcessoAdministrativoAgravado(
                            em,
                            processo.getId());

            if (noAcima == null) { // NÓ PRINCIPAL.

                clonarComplemento(em, paAgravamento);

            } else { // ELE TEM UM NÓ ACIMA

                // Verificar se tem um nó abaixo
                ProcessoAdministrativoAgravamento noAbaixo
                        = paAgravamentoRepo
                                .getPorProcessoAdministrativo(
                                        em,
                                        paAgravamento.getProcessoAdministrativoAgravado().getProcessoAdministrativo().getId());

                if (noAbaixo != null) {// NÓ DO MEIO É EXCLUIDO E CRIADO UM NOVO REGISTRO LIGANDO O NÓ ACIMA E O NÓ ABAIXO.

                    desativarEstruturasAgravamento(em, paAgravamento);

                    ProcessoAdministrativoAgravamento novoAgravamento = new ProcessoAdministrativoAgravamento();
                    novoAgravamento.setProcessoAdministrativo(noAcima.getProcessoAdministrativo());
                    novoAgravamento.setProcessoAdministrativoAgravado(paAgravamento.getProcessoAdministrativoAgravado());
                    paAgravamentoRepo.insert(em, novoAgravamento);

                } else { // ÚLTIMO NÓ

                    desativarEstruturasAgravamento(em, paAgravamento);

                }
            }
        }
    }

    /**
     */
    private void desativarEstruturasAgravamento(EntityManager em,
            ProcessoAdministrativoAgravamento processoAdministrativoAgravamento) throws DatabaseException {

        processoAdministrativoAgravamento.getProcessoAdministrativoAgravado().setAtivo(AtivoEnum.DESATIVADO);
        new ProcessoAdministrativoAgravadoRepositorio().update(em, processoAdministrativoAgravamento.getProcessoAdministrativoAgravado());

        processoAdministrativoAgravamento.setAtivo(AtivoEnum.DESATIVADO);
        new ProcessoAdministrativoAgravamentoRepositorio().update(em, processoAdministrativoAgravamento);
    }

    /**
     * O complemento do nó abaixo é desativado e criado um novo com as mesmas
     * informações do nó principal.
     *
     * @param em
     * @param paAgravamento
     */
    private void clonarComplemento(EntityManager em, ProcessoAdministrativoAgravamento paAgravamento) throws DatabaseException {

        // BUSCAR COMPLENTO DO NÓ ABAIXO E DESATIVAR.
        PAComplemento complementoFilho
                = new PAComplementoRepositorio()
                        .getComplementoPorPA(
                                em,
                                paAgravamento.getProcessoAdministrativoAgravado().getProcessoAdministrativo().getId());

        complementoFilho.setAtivo(AtivoEnum.DESATIVADO);

        new PAComplementoRepositorio().update(em, complementoFilho);

        // BUSCAR COMPLEMENTO DO NÓ PRINCIPAL PARA COPIAR PARA O NOVO COMPLEMENTO DO NÓ ABAIXO, QUE SE TORNA PRINCIPAL.
        PAComplemento complementoPai
                = new PAComplementoRepositorio()
                        .getComplementoPorPA(
                                em,
                                paAgravamento.getProcessoAdministrativo().getId());

        PAComplemento complementoFilhoNovo = new PAComplemento();

        complementoFilhoNovo.setParametro(complementoPai.getParametro());
        complementoFilhoNovo.setProcessoAdministrativo(paAgravamento.getProcessoAdministrativoAgravado().getProcessoAdministrativo());
        complementoFilhoNovo.setValor(complementoPai.getValor());
        complementoFilhoNovo.setAtivo(AtivoEnum.ATIVO);

        new PAComplementoRepositorio().insert(em, complementoFilhoNovo);

        // DESATIVAR AS ESTRUTURAS DE AGRAVAMENTO.
        desativarEstruturasAgravamento(em, paAgravamento);
    }

    public void desativarRecursosDoPAParaRenotificar(EntityManager em, ProcessoAdministrativo processo, TipoFasePaEnum tipoRecurso) throws AppException, DatabaseException {
        PAFluxoFase fluxoFase = new PAFluxoFaseRepositorio().getFluxoFaseDoProcessoAdministrativo(em, processo);

        if (fluxoFase == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        List<Recurso> lRecursos
                = new RecursoRepositorio()
                        .getRecursosDoProcessoAdministrativoNaMesmaFase(
                                em,
                                processo.getId(),
                                fluxoFase.getPrioridadeFluxoAmparo().getFaseProcessoAdm().getCodigo(),
                                tipoRecurso
                        );

        if (!DetranCollectionUtil.ehNuloOuVazio(lRecursos)) {

            for (Recurso recurso : lRecursos) {

                ResultadoRecurso resultadoRecurso
                        = new ResultadoRecursoRepositorio().getResultadoRecursoAtivoPorRecurso(em, recurso.getId());

                if (resultadoRecurso != null) {

                    resultadoRecurso.setAtivo(AtivoEnum.DESATIVADO);
                    new ResultadoRecursoRepositorio().update(em, resultadoRecurso);
                }

                recurso.setAtivo(AtivoEnum.DESATIVADO);
                new RecursoRepositorio().update(em, recurso);

                RecursoMovimento movimento = new RecursoMovimentoRepositorio().getRecursoMovimentoPorRecurso(em, recurso);

                if (movimento != null) {

                    movimento.setAtivo(AtivoEnum.DESATIVADO);
                    new RecursoMovimentoRepositorio().update(em, movimento);

                    Protocolo protocolo = movimento.getProtocolo();

                    if (protocolo != null) {

                        protocolo.setAtivo(AtivoEnum.DESATIVADO);
                        new ProtocoloRepositorio().update(em, protocolo);
                    }
                }
            }
        }

        NotificacaoProcessoAdministrativo notificacao
                = new NotificacaoProcessoAdministrativoRepositorio()
                        .getNotificacaoPorNumeroProcessoETipo(
                                em,
                                processo.getNumeroProcesso(),
                                tipoRecurso
                        );

        if (notificacao == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar a notificação para a fase do recurso.");
        }

        notificacao.setAtivo(AtivoEnum.DESATIVADO);
        new NotificacaoProcessoAdministrativoRepositorio().update(em, notificacao);

        ArquivoPA arquivo = notificacao.getArquivo();

        if (arquivo != null) {
            arquivo.setAtivo(AtivoEnum.DESATIVADO);
            new ArquivoPARepositorio().update(em, arquivo);
        }

        PAEnderecoAlternativo endereco = notificacao.getEndereco();

        if (endereco != null && TipoEnvolvidoPAEnum.CONDUTOR_SISTEMA.equals(endereco.getTipoEnvolvido())) {
            endereco.setAtivo(AtivoEnum.DESATIVADO);
            new PAEnderecoAlternativoRepositorio().update(em, endereco);
        }

        NotificacaoComplemento complemento
                = new NotificacaoComplementoRepositorio().getComplementoPorNotificacao(em, notificacao.getId());

        if (null != complemento) {
            complemento.setAtivo(AtivoEnum.DESATIVADO);
            new NotificacaoComplementoRepositorio().update(em, complemento);
        }

        EditalProcessoAdministrativo edital
                = new EditalProcessoAdministrativoRepositorio().getEditalPorNotificacao(em, notificacao.getId());

        if (edital != null) {
            edital.setAtivo(AtivoEnum.DESATIVADO);
            new EditalProcessoAdministrativoRepositorio().update(em, edital);
        }
    }

    /**
     *
     * @param em
     * @param idUsuario
     * @param urlMenuAplicacao
     * @return
     * @throws AppException
     */
    public Boolean validaSePermiteUsuarioCadastrarDataRecursoRetroativa(
            EntityManager em, Long idUsuario, String urlMenuAplicacao) throws AppException {

        if (idUsuario == null || DetranStringUtil.ehBrancoOuNulo(urlMenuAplicacao)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        MenuAplicacao menuAplicacao
                = (MenuAplicacao) getAcessoService().getMenuAplicacaoPorUrlPath(urlMenuAplicacao);

        if (menuAplicacao == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        List<Long> permissoes
                = getAcessoService()
                        .getPermissaoPorUsuarioAndMenuAplicacao(idUsuario, menuAplicacao.getCodigo());

        return DetranCollectionUtil.ehNuloOuVazio(permissoes) || executaRegraDeNegocioParaDataRecurso(permissoes, menuAplicacao);
    }

    /**
     *
     * @param em
     * @param wrapper
     * @throws AppException
     */
    private void validaDataRecurso(EntityManager em, RecursoWrapper wrapper) throws AppException {

        PAOcorrenciaStatus ocorrenciaAtual
                = new PAOcorrenciaStatusRepositorio()
                        .getPAOcorrenciaStatusAtiva(em, wrapper.getEntidade().getProcessoAdministrativo().getId());

        if (null != ocorrenciaAtual) {

            if (PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {

                PAComplemento complemento
                        = new PAComplementoRepositorio()
                                .getPAComplementoPorParametroEAtivo(
                                        em,
                                        wrapper.getEntidade().getProcessoAdministrativo(),
                                        PAParametroEnum.DESISTENCIA_REC_INST_PEN);

                if (null != complemento) {
                    DetranWebUtils.applicationMessageException("Não é permitido criar recurso para este processo administrativo devido a desistência.");
                }
            }
        }

        if (wrapper == null || wrapper.getDataRecurso() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.parametroinvalido2");
        }

        if (wrapper.getDesabilitadoDataRecurso() && Utils.compareToDate(wrapper.getDataRecurso(), Calendar.getInstance().getTime()) != 0) {
            DetranWebUtils.applicationMessageException("recursoPa.validar.aberturarecurso.M4");
        }

        if (Utils.compareToDate(wrapper.getDataRecurso(), Calendar.getInstance().getTime()) > 0) {
            DetranWebUtils.applicationMessageException("recursoPa.validar.aberturarecurso.M2");
        }

        NotificacaoProcessoAdministrativo notificacaoProcessoAdministrativoDaFase
                = new NotificacaoProcessoAdministrativoRepositorio()
                        .getNotificacaoPorProcessoAdministrativoETipoNotificacao(
                                em,
                                wrapper.getEntidade().getProcessoAdministrativo().getId(),
                                wrapper.getEntidade().getTipoRecurso());

        if (notificacaoProcessoAdministrativoDaFase == null) {
            DetranWebUtils.applicationMessageException("Notificação do processo administrativo não encontrada.");
        }

        LOG.debug("Data Notificação: {0}", notificacaoProcessoAdministrativoDaFase.getDataNotificacao());

        if (Utils.compareToDate(wrapper.getDataRecurso(), notificacaoProcessoAdministrativoDaFase.getDataNotificacao()) < 0) {
            DetranWebUtils.applicationMessageException("recursoPa.validar.aberturarecurso.M3");
        }

        if (OrigemDestinoEnum.CETRAN.equals(wrapper.getDestino())) {

            ResultadoRecurso resultado = new ResultadoRecursoRepositorio().
                    getResultadoRecursoPorProcessoAdministrativoESituacaoEDestino(em,
                            wrapper.getEntidade().getProcessoAdministrativo().getId(),
                            SituacaoRecursoEnum.JULGADO,
                            OrigemDestinoEnum.JARI
                    );

            if (wrapper.getEntidade().getDataRecurso().before(resultado.getData())) {
                DetranWebUtils.applicationMessageException("recursoPa.validar.aberturarecurso.M5");
            }
        }
    }

    /**
     *
     * @param idPermissoes
     * @param menuAplicacao
     * @return
     * @throws AppException
     */
    private Boolean executaRegraDeNegocioParaDataRecurso(
            List<Long> idPermissoes, MenuAplicacao menuAplicacao) throws AppException {

        Boolean desabilitaAlteracaoDataRecurso = Boolean.TRUE;

        List<RegraNegocio> lRegraNegocio
                = getAcessoService()
                        .getMenuComponentesParaUsuarioPorPermissaoEMenuAplicacao(
                                idPermissoes,
                                menuAplicacao.getId()
                        );

        if (!DetranCollectionUtil.ehNuloOuVazio(lRegraNegocio)) {

            for (RegraNegocio regraNegocio : lRegraNegocio) {

                if (CodigoRegraNegocio.VALIDACAO_COMPONENTE_CAMPO_HABILITADO.equals(regraNegocio.getCodigo())) {

                    ParametroAdicional params = new ParametroAdicional();

                    IRetornoRegraNegocio retorno
                            = getRegraNegocioService()
                                    .executarRegraNegocio(
                                            regraNegocio.getClasseJava(),
                                            new MenuComponenteWrapper2(),
                                            params.getParams()
                                    );

                    if (!DetranCollectionUtil.ehNuloOuVazio(retorno.getEntidade())) {

                        MenuComponenteWrapper2 menuComponente
                                = (MenuComponenteWrapper2) retorno.getEntidade().get(0);

                        return menuComponente.getDisabled();
                    }
                }
            }
        }

        return desabilitaAlteracaoDataRecurso;
    }

    /**
     * Desativar:
     *
     * TB_TCT_ARQUIVO_PA; TB_TDM_PAD_PROTOCOLO; TB_XHV_TEMPLATE_PROTOCOLO;
     * TB_XGR_ARQUIVO; TB_TDQ_PAD_RECURSO_MOVIMENTO; TB_TDP_PAD_RECURSO;
     *
     * @param em
     * @param listaProtocolos
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    public void cancelarRecursoPorCadastroIndevido(EntityManager em,
            RecursoMovimento recursoMovimento,
            List<Protocolo> listaProtocolos) throws AppException {

        if (!DetranCollectionUtil.ehNuloOuVazio(listaProtocolos)) {

            for (Protocolo protocolo : listaProtocolos) {

                // TCT
                protocolo.getArquivoPa().setAtivo(AtivoEnum.DESATIVADO);
                new ArquivoPARepositorio().update(em, protocolo.getArquivoPa());

                // TDM
                protocolo.setAtivo(AtivoEnum.DESATIVADO);
                new ProtocoloRepositorio().update(em, protocolo);

                // XHV e XGR
                getControleCnhService().desativarTemplateProtocoloEArquivo(protocolo.getTemplateProtocolo());
            }
        }

        // TDQ
        recursoMovimento.setAtivo(AtivoEnum.DESATIVADO);
        new RecursoMovimentoRepositorio().update(em, recursoMovimento);

        // TDP
        recursoMovimento.getRecurso().setAtivo(AtivoEnum.DESATIVADO);
        new RecursoRepositorio().update(em, recursoMovimento.getRecurso());

    }
}
