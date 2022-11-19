package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.SetorCorrespondencia;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.TemplateProtocolo;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhControle;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhControlePessoa;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.core.projeto.enums.adm.FormaProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.OrigemCadastroEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.ResponsavelProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.hab.FormaEntregaCnhEnum;
import br.gov.ms.detran.comum.core.projeto.enums.hab.ModoEntregaCnhEnum;
import br.gov.ms.detran.comum.core.projeto.wrapper.hab.CnhSituacaoEntregaWrapper;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.projeto.service.FabricaJSPASequencial;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.core.iface.servico.ate.IAtendimentoService;
import br.gov.ms.detran.core.iface.servico.hab.IHabilitacaoService;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP93;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP93BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ApoioOrigemInstauracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoOrigemRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoOrigem;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.RegraInstaurarEnum;
import br.gov.ms.detran.processo.administrativo.enums.IdentificacaoRecolhimentoCnhEnum;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.ParteProcessoJuridicoEnum;
import br.gov.ms.detran.processo.administrativo.enums.RequisitoRecursoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
import br.gov.ms.detran.processo.administrativo.util.SequencialRESTfulService;
import br.gov.ms.detran.processo.administrativo.wrapper.pju.DadoProcessoJudicialWrapper;
import java.util.Date;
import javax.persistence.EntityManager;

public class DadoProcessoJudicialBO extends FabricaJSPASequencial {

    private static final Logger LOG = Logger.getLogger(DadoProcessoJudicialBO.class);

    private final String SIGLA_CORRESPONDENCIA = "DT/SEPEN";

    private IApoioService apoioService;
    private IAtendimentoService atendimentoService;
    private IHabilitacaoService habilitacaoService;

    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    public IAtendimentoService getAtendimentoService() {
        if (atendimentoService == null) {
            atendimentoService = (IAtendimentoService) JNDIUtil.lookup("ejb/AtendimentoService");
        }
        return atendimentoService;
    }

    public IHabilitacaoService getHabilitacaoService() {
        if (habilitacaoService == null) {
            habilitacaoService = (IHabilitacaoService) JNDIUtil.lookup("ejb/HabilitacaoService");
        }
        return habilitacaoService;
    }

    /**
     * @param em
     * @param wrapper
     * @param usuarioLogado
     * @param urlBaseBirt
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    public DadoProcessoJudicial gravarDadoProcessoJudicial(EntityManager em,
            DadoProcessoJudicialWrapper wrapper,
            DetranUserDetailsWrapper usuarioLogado,
            String urlBaseBirt) throws AppException {

        validarGravar(wrapper);

        ProcessoAdministrativo pa = gravarProcessoAdministrativo(em, wrapper);

        wrapper.getEntidade().setProcessoJudicial(gravarProcessoJudicial(em, wrapper, pa, usuarioLogado));
        wrapper.getEntidade().setTipoDocumento(wrapper.getTipoDocumento().getId());
        wrapper.getEntidade().setOrgaoJudicial(wrapper.getOrgaoJudicialEntidade().getId());
        wrapper.getEntidade().setOrgaoBca(wrapper.getOrgaoBcaEntidade().getId());

        if (RequisitoRecursoBloqueioEnum.CURSO_EXAMES.equals(wrapper.getEntidade().getRequisitoCursoBloqueio())) {
            wrapper.getEntidade().setIndicativoProva(BooleanEnum.SIM);
        }

        if (RequisitoRecursoBloqueioEnum.SEM_CURSO.equals(wrapper.getEntidade().getRequisitoCursoBloqueio())) {
            wrapper.getEntidade().setIndicativoProva(BooleanEnum.NAO);
        }

        gravarPAComplemento(em, wrapper, wrapper.getEntidade().getProcessoJudicial().getProcessoAdministrativo());

        gravarPAOcorrenciaStatus(em, wrapper, pa);

        if (IdentificacaoRecolhimentoCnhEnum.DETRAN.equals(wrapper.getEntidade().getIdentificacaoRecolhimentoCnh())) {
            gravarCnhControle(em, wrapper, usuarioLogado, urlBaseBirt, pa);
        }

        wrapper.getEntidade().setUsuarioCadastro(wrapper.getEntidade().getProcessoJudicial().getUsuarioCadastro());
        wrapper.getEntidade().setDataCadastro(new Date());

        return new DadoProcessoJudicialRepositorio().insertOrUpdate(em, wrapper.getEntidade());
    }

    /**
     * @param wrapper
     * @throws AppException
     */
    private void validarGravar(DadoProcessoJudicialWrapper wrapper) throws AppException {

        if (null == wrapper.getTipoDocumento()) {
            DetranWebUtils.applicationMessageException("Tipo documento é obrigatório.");
        }

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getEntidade().getValorDocumento())) {
            DetranWebUtils.applicationMessageException("Valor documento é obrigatório.");
        }

        if (null == wrapper.getEntidade().getParteProcessoJuridico()) {
            DetranWebUtils.applicationMessageException("Parte é obrigatório.");
        } else {
            if (ParteProcessoJuridicoEnum.CANDIDATO.equals(wrapper.getEntidade().getParteProcessoJuridico())) {
                if (DetranStringUtil.ehBrancoOuNulo(wrapper.getEntidade().getFormularioRenach())) {
                    DetranWebUtils.applicationMessageException("Formulario Renach é obrigatório.");
                }
            }
        }

        if (null == wrapper.getEntidade().getTipoMedida()) {
            DetranWebUtils.applicationMessageException("Tipo medida é obrigatório.");
        }

        if (null == wrapper.getEntidade().getSegredoJustica()) {
            DetranWebUtils.applicationMessageException("Segredo Justiça é obrigatório.");
        }

        if (null == wrapper.getEntidade().getValorAutos()) {
            DetranWebUtils.applicationMessageException("Valor autos é obrigatório.");
        }

        if (null == wrapper.getOrgaoJudicialEntidade()) {
            DetranWebUtils.applicationMessageException("Órgão judicial é obrigatório.");
        }

        if (null == wrapper.getOrgaoBcaEntidade()) {
            DetranWebUtils.applicationMessageException("Órgão BCA é obrigatório.");
        }

        if (null == wrapper.getEntidade().getRequisitoCursoBloqueio()) {
            DetranWebUtils.applicationMessageException("Requisito curso bloqueio é obrigatório.");
        }

        if (null == wrapper.getEntidade().getUnidadePenal()) {
            DetranWebUtils.applicationMessageException("Unidade penal é obrigatório.");
        }

        if (null == wrapper.getEntidade().getIndicativoPrazoIndeterminado()
                || BooleanEnum.NAO.equals(wrapper.getEntidade().getIndicativoPrazoIndeterminado())) {

            if (null == wrapper.getEntidade().getPrazoPenalidade()) {
                DetranWebUtils.applicationMessageException("Prazo penalidade é obrigatório.");
            }
        }

        if (null == wrapper.getEntidade().getIdentificacaoRecolhimentoCnh()) {

            DetranWebUtils.applicationMessageException("Identificação Recolhimento Cnh é obrigatório.");

        } else if (!IdentificacaoRecolhimentoCnhEnum.INEXISTENTE.equals(wrapper.getEntidade().getIdentificacaoRecolhimentoCnh())) {

            if (null == wrapper.getEntidade().getDataInicioPenalidade()) {

                DetranWebUtils.applicationMessageException("Data início penalidade é obrigatória.");

            }
        }

        if (!IdentificacaoRecolhimentoCnhEnum.INEXISTENTE.equals(wrapper.getEntidade().getIdentificacaoRecolhimentoCnh())
                && null == wrapper.getEntidade().getDataEntrega()) {

            DetranWebUtils.applicationMessageException("Data entrega é obrigatório.");

        } else if (!ParteProcessoJuridicoEnum.CONDUTOR.equals(wrapper.getEntidade().getParteProcessoJuridico())
                && null == wrapper.getEntidade().getDataInicioPenalidade()) {

            DetranWebUtils.applicationMessageException("Data início penalidade é obrigatória.");

        } else {

            if (!IdentificacaoRecolhimentoCnhEnum.INEXISTENTE.equals(wrapper.getEntidade().getIdentificacaoRecolhimentoCnh())
                    && Utils.compareToDate(wrapper.getEntidade().getDataEntrega(), new Date()) == 1) {
                DetranWebUtils.applicationMessageException("A Data Entrega deve ser Menor ou Igual a Data Atual.");
            }

//            if (!IdentificacaoRecolhimentoCnhEnum.INEXISTENTE.equals(wrapper.getEntidade().getIdentificacaoRecolhimentoCnh())
//                    && Utils.compareToDate(wrapper.getEntidade().getDataInicioPenalidade(), new Date()) == 1) {
//                DetranWebUtils.applicationMessageException("Não é possível incluir data futura na BCA.");
//            }
        }

        if (null == wrapper.getEntidade().getDataBloqueio()) {
            DetranWebUtils.applicationMessageException("Data bloqueio é obrigatório.");
        } else {

            if (Utils.compareToDate(wrapper.getEntidade().getDataBloqueio(), new Date()) == 1) {
                DetranWebUtils.applicationMessageException("A data bloqueio deve ser menor ou igual a data atual.");
            }
        }

        if (null == wrapper.getEntidade().getIdentificacaoDelito()) {
            DetranWebUtils.applicationMessageException("Identificação delito é obrigatório.");
        }

        if (IdentificacaoRecolhimentoCnhEnum.DETRAN.equals(wrapper.getEntidade().getIdentificacaoRecolhimentoCnh())
                || IdentificacaoRecolhimentoCnhEnum.CARTORIO_JUDICIARIO.equals(wrapper.getEntidade().getIdentificacaoRecolhimentoCnh())) {
            if (wrapper.getEntidade().getDataBloqueio().after(wrapper.getEntidade().getDataInicioPenalidade())) {
                DetranWebUtils.applicationMessageException("Campo Data Bloqueio deve ser menor ou igual ao campo Data Início Penalidade.");
            }
            if (wrapper.getEntidade().getDataEntrega().after(wrapper.getEntidade().getDataInicioPenalidade())) {
                DetranWebUtils.applicationMessageException("Campo Data Entrega CNH deve ser menor ou igual ao campo Data Início Penalidade.");
            }
        }

        if (wrapper.getEntidade().getRequisitoCursoBloqueio() != RequisitoRecursoBloqueioEnum.SEM_CURSO && wrapper.getEntidade().getIndicativoProva() == null) {
            DetranWebUtils.applicationMessageException("Campo Indicativo Prova é obrigatório.");
        }
    }

    /**
     * @param em
     * @param wrapper
     * @return
     */
    private ProcessoJudicial gravarProcessoJudicial(EntityManager em,
            DadoProcessoJudicialWrapper wrapper,
            ProcessoAdministrativo pa,
            DetranUserDetailsWrapper usuarioLogado) throws AppException {

        ProcessoJudicial processoJudicial = new ProcessoJudicial();

        processoJudicial.setProcessoAdministrativo(pa);
        processoJudicial.setDataCadastro(new Date());

        processoJudicial.setUsuarioCadastro(usuarioLogado.getUsuarioLocal().getId());
        processoJudicial.setAtivo(AtivoEnum.ATIVO);

        new ProcessoJudicialRepositorio().insert(em, processoJudicial);

        return processoJudicial;
    }

    /**
     * @param em
     * @param wrapper
     * @return
     */
    private ProcessoAdministrativo gravarProcessoAdministrativo(EntityManager em, DadoProcessoJudicialWrapper wrapper) throws AppException {

        ProcessoAdministrativo processoAdministrativo = new ProcessoAdministrativo();

        processoAdministrativo.setCpf(Utils.removerMascaraCPF(wrapper.getCpf()));

        if (!ParteProcessoJuridicoEnum.CANDIDATO.equals(wrapper.getEntidade().getParteProcessoJuridico())
                && !ParteProcessoJuridicoEnum.CIDADAO.equals(wrapper.getEntidade().getParteProcessoJuridico())) {

            AEMNPP93 aemnpp93 = new AEMNPP93BO().executarIntegracaoAEMNPP93(wrapper.getCpf());

            if (aemnpp93 != null) {

                if (DetranStringUtil.ehBrancoOuNulo(aemnpp93.getNumeroCnh())
                        || DetranStringUtil.ehBrancoOuNulo(aemnpp93.getNumeroRegistro())) {
                    DetranWebUtils.applicationMessageException("Erro ao buscar informaçoes da CNH pela AEMNPP93.");
                }

                processoAdministrativo.setCnh(new Long(aemnpp93.getNumeroCnh()));

                String numeroRegistroCnh = aemnpp93.getNumeroRegistro();

                processoAdministrativo.setNumeroRegistro(numeroRegistroCnh);
            }
        }

        processoAdministrativo.setTipo(wrapper.getTipoProcesso());
        processoAdministrativo.setDataProcessamento(new Date());

        ApoioOrigemInstauracao apoio
                = new ApoioOrigemInstauracaoRepositorio()
                        .getApoioOrigemPorRegra(
                                em,
                                TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(wrapper.getTipoProcesso()) ? RegraInstaurarEnum.C13
                                : TipoProcessoEnum.CASSACAO_JUDICIAL.equals(wrapper.getTipoProcesso()) ? RegraInstaurarEnum.C14
                                : null);

        processoAdministrativo.setOrigemApoio(apoio);
        processoAdministrativo.setNumeroDetran(getApoioService().getNumeroDetranPorCpf(processoAdministrativo.getCpf()));
        processoAdministrativo.setAtendimento(getAtendimentoService().gravarAtendimentoProcessoAdministrativo(processoAdministrativo.getNumeroDetran()));

        processoAdministrativo.setOrigem(OrigemEnum.JURIDICA);
        processoAdministrativo.setNumeroProcesso(new SequencialRESTfulService().getNumeroProcesso());
        processoAdministrativo.setNumeroPortaria(new SequencialRESTfulService().getNumeroPortaria());

        return new ProcessoAdministrativoRepositorio().insertOrUpdate(em, processoAdministrativo);
    }

    /**
     * @param em
     * @param wrapper
     */
    private void gravarPAComplemento(EntityManager em, DadoProcessoJudicialWrapper wrapper, ProcessoAdministrativo pa) throws DatabaseException {

        PAComplemento complemento = new PAComplemento();

        complemento.setAtivo(AtivoEnum.ATIVO);
        complemento.setParametro(PAParametroEnum.TEMPO_PENALIDADE);
        complemento.setProcessoAdministrativo(pa);
        complemento.setValor(BooleanEnum.SIM.equals(wrapper.getEntidade().getIndicativoPrazoIndeterminado()) ? "1"
                : wrapper.getEntidade().getPrazoPenalidade().toString());

        new PAComplementoRepositorio().insert(em, complemento);
    }

    /**
     * @param em
     * @param wrapper
     * @param pa
     */
    private void gravarPAOcorrenciaStatus(EntityManager em, DadoProcessoJudicialWrapper wrapper, ProcessoAdministrativo pa) throws DatabaseException, AppException {

        PAFluxoOrigem fluxoOrigem
                = new PAFluxoOrigemRepositorio()
                        .getPAFluxoOrigemPorApoioOrigemInstauracaoParaProcessoJudicial(
                                em,
                                pa.getOrigemApoio().getId());

        new PAOcorrenciaStatusRepositorio()
                .incluir(em,
                        pa,
                        PAAndamentoProcessoConstante.INSTAURACAO.INSTAURAR_PROCESSO,
                        SituacaoOcorrenciaEnum.INICIADO,
                        fluxoOrigem.getFluxoProcesso());

    }

    /**
     * @param em
     * @param wrapper
     * @param usuarioLogado
     * @param urlBaseBirt
     * @throws AppException
     */
    private void gravarCnhControle(EntityManager em,
            DadoProcessoJudicialWrapper wrapper,
            DetranUserDetailsWrapper usuarioLogado,
            String urlBaseBirt,
            ProcessoAdministrativo pa) throws AppException {
        CnhSituacaoEntrega cnhSituacaoEntrega = new CnhSituacaoEntrega();
        Usuario usuario = (Usuario) getApoioService().getUsuario(usuarioLogado.getUsuarioLocal().getId());

        CnhSituacaoEntrega cnhEntregaExistente = (CnhSituacaoEntrega) getHabilitacaoService().getCnhSituacaoEntregaSemAcaoDeDevolucaoDaCnhPorNumeroDetran(
                wrapper.getNumeroDetran()
        );

        if (cnhEntregaExistente == null) {

            cnhSituacaoEntrega.setAcao(AcaoEntregaCnhEnum.ENTREGA);
            cnhSituacaoEntrega.setAtivo(AtivoEnum.ATIVO);
            cnhSituacaoEntrega.setDataEntrega(new Date());
            cnhSituacaoEntrega.setFormaEntrega(FormaEntregaCnhEnum.ESPONTANEA);
            cnhSituacaoEntrega.setModoEntrega(ModoEntregaCnhEnum.DOCUMENTO_CNH);
            cnhSituacaoEntrega.setMotivo(null);
            cnhSituacaoEntrega.setObservacao(null);
            cnhSituacaoEntrega.setPostoAtendimento(usuarioLogado.getOperador().getPostoAtendimento());
            cnhSituacaoEntrega.setSetorCorrespondencia((SetorCorrespondencia) getApoioService().buscarSetorCorrespondencia(SIGLA_CORRESPONDENCIA));
            cnhSituacaoEntrega.setUsuario(usuario);

            CnhControle cnhControle = new CnhControle();
            cnhControle.setMunicipio(null);            
            cnhControle.setNumeroCnh(pa.getCnh());
            cnhControle.setAtivo(AtivoEnum.ATIVO);

            if (wrapper.getEntidade() != null
                    && IdentificacaoRecolhimentoCnhEnum.DETRAN.equals(wrapper.getEntidade().getIdentificacaoRecolhimentoCnh())) {

                if (!DetranStringUtil.ehBrancoOuNulo(pa.getNumeroRegistro())) {
                    cnhControle.setNumeroRegistro(pa.getNumeroRegistro());
                } else {
                    DetranWebUtils.applicationMessageException("Número Registro do processo administrativo não encontrado/informado. Verifique.");
                }

                if (cnhSituacaoEntrega.getPostoAtendimento() != null && cnhSituacaoEntrega.getPostoAtendimento().getId() != null) {
                    cnhControle.setPostoAtendimentoDevolucao(cnhSituacaoEntrega.getPostoAtendimento());
                } else {
                    DetranWebUtils.applicationMessageException("O Posto de Atendimento é obrigatório.");
                }
            }

            cnhSituacaoEntrega.setCnhControle(cnhControle);

            CnhControlePessoa cnhControlePessoa = new CnhControlePessoa();
            cnhControlePessoa.setNomeEntrega(getApoioService().getNomePessoaPorCpf(pa.getCpf()));
            cnhControlePessoa.setCpfEntrega(pa.getCpf());
            cnhControlePessoa.setAtivo(AtivoEnum.ATIVO);

            cnhSituacaoEntrega.setCnhControlePessoa(cnhControlePessoa);

            TemplateProtocolo templateProtocolo = new TemplateProtocolo();
            templateProtocolo.setDataProtocolo(new Date());
            templateProtocolo.setFormaProtocolo(FormaProtocoloEnum.BALCAO);
            templateProtocolo.setPostoAtendimento(usuarioLogado.getOperador().getPostoAtendimento());
            templateProtocolo.setResponsavelProtocolo(ResponsavelProtocoloEnum.DETRAN);
            templateProtocolo.setTipoSituacao(TipoSituacaoProtocoloEnum.APRESENTACAO);
            templateProtocolo.setUsuario(usuario);
            templateProtocolo.setArquivo(null);
            templateProtocolo.setByteBarra(null);
            templateProtocolo.setCorpoTexto(null);
            templateProtocolo.setNumeroProtocolo(null);
            templateProtocolo.setObservacao(null);

            cnhSituacaoEntrega.setTemplateProtocolo(templateProtocolo);

        } else {

            cnhSituacaoEntrega = cnhEntregaExistente;

            if (wrapper.getEntidade() != null
                    && IdentificacaoRecolhimentoCnhEnum.DETRAN.equals(wrapper.getEntidade().getIdentificacaoRecolhimentoCnh())) {

                if (cnhSituacaoEntrega.getCnhControle() != null) {

                    if (!DetranStringUtil.ehBrancoOuNulo(cnhSituacaoEntrega.getCnhControle().getNumeroRegistro())) {
                        cnhSituacaoEntrega.getCnhControle().setNumeroRegistro(pa.getNumeroRegistro());
                    } else {
                        DetranWebUtils.applicationMessageException("Número Registro do Processo Administrativo não encontrado/informado. Verifique.");
                    }
                    if (cnhSituacaoEntrega.getCnhControle().getPostoAtendimentoDevolucao() != null) {
                        cnhSituacaoEntrega.getCnhControle().setPostoAtendimentoDevolucao(cnhEntregaExistente.getPostoAtendimento());
                    } else {
                        DetranWebUtils.applicationMessageException("O Posto de Atendimento é obrigatório.");
                    }
                }
            }
        }

        CnhSituacaoEntregaWrapper cnhSituacaoEntregaWrapper
                = new CnhSituacaoEntregaWrapper(
                        cnhSituacaoEntrega,
                        null,
                        usuarioLogado,
                        getApoioService().getNumeroDetranPorCpf(wrapper.getCpf()));

        cnhSituacaoEntregaWrapper.setSetorOrigem("DT/SEPEN");

        if (IdentificacaoRecolhimentoCnhEnum.DETRAN.equals(wrapper.getEntidade().getIdentificacaoRecolhimentoCnh()) && cnhSituacaoEntregaWrapper.getSetorOrigem().equals("DT/SEPEN")) {
            cnhSituacaoEntregaWrapper.getEntidade().setOrigemCadastro(OrigemCadastroEnum.PROCESSO_JURIDICO);
        }

        getHabilitacaoService().gravaCnhControle(cnhSituacaoEntregaWrapper);
    }
}
