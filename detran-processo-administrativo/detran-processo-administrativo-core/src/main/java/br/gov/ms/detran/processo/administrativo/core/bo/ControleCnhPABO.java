package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.ImpressoraUsuario;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.core.projeto.enums.ace.ModeloImpressoraEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.OrigemCadastroEnum;
import br.gov.ms.detran.comum.core.projeto.wrapper.hab.CnhSituacaoEntregaWrapper;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.iface.bca.IBeanIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.adm.IAdministrativoService;
import br.gov.ms.detran.core.iface.servico.hab.IHabilitacaoService;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP93;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.EDITAL_NOTIFICACAO.AGUARDAR_EDITAL_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_AR_NOTIFICACAO_ENTREGA_CNH;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import static br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante.FLUXO_DESISTENCIA_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante.FLUXO_DESISTENCIA_ENTREGA_CNH_COM_RECURSO;
import br.gov.ms.detran.processo.administrativo.core.bo.recursoonline.RecursoOnlineBO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP93BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.*;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.criteria.ProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.criteria.UsuarioAcessoProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.*;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * @author Lillydi
 */
public class ControleCnhPABO {

    private static final Logger LOG = Logger.getLogger(ControleCnhPABO.class);
    
    private static final Integer NUMERO_COPIAS_PROTOCOLO_IMPRESSAO = 2;

    private IHabilitacaoService habilitacaoService;

    private IProcessoAdministrativoService processoAdministrativoService;
    
    private IAdministrativoService administrativoService;
    
    private IPAControleFalhaService falhaService;
    
    public IHabilitacaoService getHabilitacaoService() {

        if (habilitacaoService == null) {
            habilitacaoService = (IHabilitacaoService) JNDIUtil.lookup("ejb/HabilitacaoService");
        }

        return habilitacaoService;
    }

    public IProcessoAdministrativoService getProcessoAdministrativoService() {

        if (processoAdministrativoService == null) {
            processoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }

        return processoAdministrativoService;
    }
    
    public IAdministrativoService getAdministrativoService() {
        
        if (administrativoService == null) {
            administrativoService = (IAdministrativoService) JNDIUtil.lookup("ejb/AdministrativoService");
        }
        
        return administrativoService;
    }

    public IPAControleFalhaService getFalhaService() {
        
        if (falhaService == null) {
            falhaService = (IPAControleFalhaService) JNDIUtil.lookup("ejb/PAControleFalhaService");
        }
        
        return falhaService;
    }

    /**
     * @param em
     * @param usuarioWrapper
     * @param wrapper
     * @param urlBaseBirt
     * @param listaProcessos
     * @return 
     *
     * @throws AppException
     */
    public void gravarControleCnhPA(EntityManager em, 
                                    DetranUserDetailsWrapper usuarioWrapper, 
                                    ControleCnhPAWrapper wrapper, 
                                    String urlBaseBirt,
                                    List<PAControleCnhWrapper> listaProcessos) throws AppException {
        
        validarGravarControleCnhPA(em, wrapper, wrapper.getEntidade().getCnhControlePessoa().getCpfEntrega(), usuarioWrapper);
        
        if (wrapper.getEntidade().getCnhControle().getId() == null) {
            wrapper.getEntidade().setAcao(AcaoEntregaCnhEnum.ENTREGA);
            gravarInformacoesEntregaDevolucaoCnh(wrapper, usuarioWrapper, wrapper.getNumeroDetranCondutor());
        }

        for (PAControleCnhWrapper desistente : wrapper.getProcessosParaDesistenciaSelecionados()) {
            PAComplemento complemento = new PAComplemento(desistente.getProcesso(), PAParametroEnum.DESISTENCIA_ENTREGA_CNH, "1");

            new PAComplementoRepositorio().insert(em, complemento);

            new RecursoOnlineBO().cancelarRecursoOnlineEmBackOffice(em, desistente.getProcesso(), wrapper.getRecursoCanceladoWrapper());

            new PAInicioFluxoBO().gravarInicioFluxo(em, desistente.getProcesso(), FLUXO_DESISTENCIA_ENTREGA_CNH);
        }
        
        for (PAControleCnhWrapper desistenteComRecurso : wrapper.getProcessosParaDesistenciaRecursoSelecionados()) {
            
            Recurso recurso = new RecursoRepositorio().
                    getRecursoAtivoPorProcessoAdministrativoESituacao(em, desistenteComRecurso.getProcesso().getId(), SituacaoRecursoEnum.EM_ANALISE);
            
            new RecursoBO().cancelarRecursoSemValidar(em, usuarioWrapper, new RecursoWrapper(recurso));    
            
            PAComplemento complemento = new PAComplemento(desistenteComRecurso.getProcesso(), PAParametroEnum.DESISTENCIA_ENTREGA_CNH, "1");
            new PAComplementoRepositorio().insert(em, complemento);
            
            new PAInicioFluxoBO().gravarInicioFluxo(em, desistenteComRecurso.getProcesso(), FLUXO_DESISTENCIA_ENTREGA_CNH_COM_RECURSO);
        }
        
        for (PAControleCnhWrapper pjuSelecionado : wrapper.getProcessosJuridicosOutrosAndamentosSelecionados()) {
            
            new AndamentoProcessoAdministrativoManager2()
                    .executarMudancaFluxoEAndamento(
                        em, 
                        pjuSelecionado.getProcesso(), 
                        PAFluxoProcessoConstante.FLUXO_GERAL_JURIDICO, 
                        PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_ENTREGA_CNH
                    );
        }
    }

    /**
     * @param wrapper
     * @param usuarioWrapper
     * @throws AppException
     */
    private void gravarInformacoesEntregaDevolucaoCnh(
        ControleCnhPAWrapper wrapper, DetranUserDetailsWrapper usuarioWrapper, Long numeroDetran) throws AppException {

        CnhSituacaoEntregaWrapper cnhSituacaoEntregaWrapper 
            = new CnhSituacaoEntregaWrapper(
                wrapper.getEntidade(), 
                wrapper.getRepresentanteLegal(),
                usuarioWrapper,
                numeroDetran
            );
        
        cnhSituacaoEntregaWrapper.setSetorOrigem("DT/SEPEN");
        
        wrapper.getEntidade().setOrigemCadastro(OrigemCadastroEnum.PROCESSO_ADMINISTRATIVO);
        
        if (wrapper.getDadosCondutorBCA() != null && !DetranStringUtil.ehBrancoOuNulo(wrapper.getDadosCondutorBCA().getDataValidadeCnh())) {
            cnhSituacaoEntregaWrapper.getEntidade().getCnhControle().setValidadeCnh(Utils.convertDate(wrapper.getDadosCondutorBCA().getDataValidadeCnh(), "ddMMyyyy"));
        }

        getHabilitacaoService().gravaCnhControle(cnhSituacaoEntregaWrapper);
    }

    /**
     * @param em
     * @param usuarioLogado
     * @param wrapper
     * @param urlBaseBirt
     * @param situacao
     * @param movimentos
     *
     * @throws AppException
     */
    public void desbloquearCnh(EntityManager em, 
                               DetranUserDetailsWrapper usuarioLogado, 
                               ControleCnhPAWrapper wrapper, 
                               String urlBaseBirt, 
                               CnhSituacaoEntrega situacao, 
                               List movimentos) throws AppException {

        PAPenalidadeProcesso ultimaPAPenalidade = 
                new PAPenalidadeProcessoRepositorio()
                        .getUltimaPenalidadePorControleCnh(em, situacao.getCnhControle().getId());

        validarDesbloquearCnh(ultimaPAPenalidade, movimentos);
        
        wrapper.getEntidade().setCnhControle(situacao.getCnhControle());
        wrapper.getEntidade().setCnhControlePessoa(situacao.getCnhControlePessoa());
        wrapper.getEntidade().setAcao(AcaoEntregaCnhEnum.DEVOLUCAO);
        wrapper.getEntidade().setDataEntrega(Calendar.getInstance().getTime());

        gravarInformacoesEntregaDevolucaoCnh(wrapper, 
                                             usuarioLogado, 
                                             situacao.getCnhControlePessoa().getPessoaFisica().getId());
    }

    /**
     * @param cpf
     * @param numeroCnh
     * @throws AppException
     */
    private AEMNPP93 validarCnhCondutorNaBCA(String cpf, Long numeroCnh) throws AppException {

        AEMNPP93 aemnpp93 = new AEMNPP93BO().executarIntegracaoAEMNPP93(cpf);

        if (aemnpp93 == null || DetranStringUtil.ehBrancoOuNulo(aemnpp93.getNumeroCnh())) {
            DetranWebUtils.applicationMessageException("controlecnhpa.validar.condutorbca.exception");
        }

        if (!DetranStringUtil.preencherEspaco(numeroCnh.toString(), 20, DetranStringUtil.TipoDadoEnum.NUMERICO)
                .equals(DetranStringUtil.preencherEspaco(aemnpp93.getNumeroCnh(), 20, DetranStringUtil.TipoDadoEnum.NUMERICO))) {
            DetranWebUtils.applicationMessageException("controlecnhpa.validar.numerocnhcondutorpa.exception");
        }
        Date dataValidadePID = Utils.getDate(aemnpp93.getDataValidadePid(), "yyyyMMdd");
        
        Date dataAtual = Calendar.getInstance().getTime();
        
        if(dataValidadePID != null
                && dataValidadePID.before(dataAtual)){
            aemnpp93.setNumeroPid(null);
        }
        
        return aemnpp93;
    }

    public IBeanIntegracao validarEntregaCnhParaPA(EntityManager em, String cpf, Long cnh) throws AppException {

        if (cnh == null || DetranStringUtil.ehBrancoOuNulo(cpf)) {
            DetranWebUtils.applicationMessageException(("severity.error.criteria.isEmpty"));
        }

        validarCnhEmCartorio(em, cpf);
        
        return validarCnhCondutorNaBCA(cpf, cnh);

    }

    /**
     * Validações no momento de desbloquear (devolução) da CNH.
     *
     * @param ultimaPAPenalidade
     * @param movimentos
     * @throws AppException
     */
    private void validarDesbloquearCnh(
        PAPenalidadeProcesso ultimaPAPenalidade, List<MovimentoCnh> movimentos) throws AppException {

        if (ultimaPAPenalidade != null && Utils.getDataComHoraInicial(ultimaPAPenalidade.getDataFimPenalidade()).after(new Date())) {
            DetranWebUtils.applicationMessageException("A CNH do condutor só poderá ser devolvida após o dia {0}", "",
                                                       Utils.formatDate(ultimaPAPenalidade.getDataFimPenalidade(), "dd/MM/yyyy"));
        }

        if (DetranCollectionUtil.ehNuloOuVazio(movimentos)) {
            DetranWebUtils.applicationMessageException("Esta CNH não está bloqueada.");
        }
    }

    /**
     * Validações para gravar ControleCnhPa.
     *
     * 1 - Data Início Penalidade não pode ser maior que a data de hoje quando
     * informada pelo Grupo SEPEN.
     *
     * @param wrapper
     * @throws AppException
     */
    private void validarGravarControleCnhPA(EntityManager em,
                                            ControleCnhPAWrapper wrapper,
                                            String cpf,
                                            DetranUserDetailsWrapper usuarioLogado) throws AppException {

        if (DetranStringUtil.ehBrancoOuNulo(cpf)) {
            DetranWebUtils.applicationMessageException("Processo administrativo não encontrado.");
        }

        UsuarioAcessoProcessoAdministrativoWrapper usuarioAcessoPA
            = new UsuarioAcessoProcessoAdministrativoBO()
                .montarUsuarioAcessoWrapper(
                    em,
                    usuarioLogado,
                    new UsuarioAcessoProcessoAdministrativoCriteria(cpf)
                );

        if (null != usuarioAcessoPA && usuarioAcessoPA.getHabilitarDataInicio()) {
            if (Utils.getDataComHoraInicial(wrapper.getEntidade().getDataEntrega()).after(Utils.getDataComHoraInicial(new Date()))) {
                DetranWebUtils.applicationMessageException("A data de início de penalidade não pode ser maior que a data atual.");
            }
        }
    }
    
    /**
     * @param em
     * @param criteria
     * @return
     * @throws AppException 
     */
    public ControleCnhPAWrapper carregarDadosControleCnh(EntityManager em, ProcessoAdministrativoCriteria criteria) throws AppException {
        
        ProcessoAdministrativoRepositorio paRepo = new ProcessoAdministrativoRepositorio();
        
        ControleCnhPAWrapper wrapper = new ControleCnhPAWrapper();
        
        CnhSituacaoEntrega entidade = new CnhSituacaoEntrega();
        entidade.setDataEntrega(Calendar.getInstance().getTime());
        
        wrapper.setProcessosAguardandoEntrega(paRepo.getPAControleCnhWrapperPorCpfEAndamentoESituacaoIniciado(em, 
                                                                                                              criteria.getCpf(),
                                                                                                              AGUARDAR_ENTREGA_CNH));
        
        wrapper.setProcessosParaDesistencia(paRepo.getPAControleCnhWrapperPorFluxoECpf(em, 
                                                                                       criteria.getCpf(), 
                                                                                       FLUXO_DESISTENCIA_ENTREGA_CNH));
        
        wrapper.setProcessosParaDesistenciaComRecurso(paRepo.getPAControleCnhWrapperPorFluxoECpf(em, 
                                                                                       criteria.getCpf(), 
                                                                                       FLUXO_DESISTENCIA_ENTREGA_CNH_COM_RECURSO));
        
        List pjuOutrosAndamentos = paRepo.getPAControleCnhWrapperPJUPorCpfEAndamentoESituacaoIniciado(em, 
                                                                                                      criteria.getCpf(),
                                                                                                      AGUARDAR_AR_NOTIFICACAO_ENTREGA_CNH);
        
        pjuOutrosAndamentos.addAll(paRepo.getPAControleCnhWrapperPJUPorCpfEAndamentoESituacaoIniciado(em, 
                                                                                                      criteria.getCpf(),
                                                                                                      AGUARDAR_EDITAL_NOTIFICACAO_ENTREGA_CNH));
        
        wrapper.setProcessosJuridicosOutrosAndamentos(pjuOutrosAndamentos);
        
        wrapper.setProcessosJuridicosEntrega(paRepo.getPAControleCnhWrapperPJUPorCpfEAndamentoESituacaoIniciado(em, 
                                                                                                                criteria.getCpf(),
                                                                                                                AGUARDAR_ENTREGA_CNH));
        
       
        
        if (DetranCollectionUtil.ehNuloOuVazio(wrapper.getProcessosAguardandoEntrega()) 
                && DetranCollectionUtil.ehNuloOuVazio(wrapper.getProcessosParaDesistencia())
                && DetranCollectionUtil.ehNuloOuVazio(wrapper.getProcessosJuridicosEntrega())
                && DetranCollectionUtil.ehNuloOuVazio(wrapper.getProcessosJuridicosOutrosAndamentos())
                && DetranCollectionUtil.ehNuloOuVazio(wrapper.getProcessosParaDesistenciaComRecurso())) {
            
            DetranWebUtils.applicationMessageException("controlecnhpa.validar.processonaoestafaseentrega.exception");
        }
        
        wrapper.setNumeroDetranCondutor(recuperarNumeroDetran(wrapper));
        
        CnhSituacaoEntrega entrega
                = (CnhSituacaoEntrega) getHabilitacaoService().
                        getCnhSituacaoEntregaSemAcaoDeDevolucaoDaCnhPorNumeroDetran(wrapper.getNumeroDetranCondutor());
        
       if(entrega != null && !entrega.getSetorCorrespondencia().getSigla().equals("DT/SEPEN")){
            DetranWebUtils.applicationMessageException("A CNH encontra-se em outro setor do DETRAN. {0}","", entrega.getSetorCorrespondencia().getSigla());
        }
        
        if (entrega != null && entrega.getCnhControle() != null) {
            
            entidade.setCnhControle(entrega.getCnhControle());
            
            PAPenalidadeProcesso ultimaPenalidade = new PAPenalidadeProcessoRepositorio().getUltimaPenalidadePorControleCnh(em, entrega.getCnhControle().getId());
            
                if (ultimaPenalidade != null) {

    //            controle.setPostoAtendimentoDevolucao(ultimaPenalidade.getPostoAtendimento());

                if (ultimaPenalidade.getDataFimPenalidade().after(Utils.getDataComHoraInicial(entidade.getDataEntrega()))) {
                   entidade.setDataEntrega(Utils.addDayMonth(ultimaPenalidade.getDataFimPenalidade(), 1));
                }
            }
        }

        Long tempoPA = (Long) new PAComplementoRepositorio().getSomaTempoPenalidadeDoCondutor(em, criteria.getCpf(), OrigemEnum.PONTUACAO);
        Long tempoPJU = (Long) new PAComplementoRepositorio().getSomaTempoPenalidadeEmPJUDoCondutor(em, criteria.getCpf(), OrigemEnum.JURIDICA);
        Date dataFim = Utils.addMonth(entidade.getDataEntrega(), tempoPA == null? 0:tempoPA.intValue());
        dataFim = Utils.addDayMonth(dataFim, tempoPJU == null? 0:tempoPJU.intValue());
        
        wrapper.setDataFimPenalidade(dataFim);
        
        List<BloqueioBCA> bloqueios = new BloqueioBCARepositorio().getBloqueioBCAPorCpfESituacaoEAtivo(em, criteria.getCpf(), SituacaoBloqueioBCAEnum.ATIVO);
        BloqueioBCA bloqueioMaisAntigo = DetranCollectionUtil.ehNuloOuVazio(bloqueios) ? null : bloqueios.get(bloqueios.size()-1);
        
        wrapper.setDataInicioPenalidade(bloqueioMaisAntigo != null ? bloqueioMaisAntigo.getDataInicio() : entidade.getDataEntrega());

        wrapper.setTempoPenalidade(tempoPA == null? "0":tempoPA.toString());
        wrapper.setTempoPenalidadePJU(tempoPJU == null? "0":tempoPJU.toString());

        wrapper.setEntidade(entidade);
        
        return wrapper;
    }
    
    /**
     * @param wrapper
     * @return 
     */
    private Long recuperarNumeroDetran(ControleCnhPAWrapper wrapper) {
        
        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getProcessosAguardandoEntrega())) {
            return wrapper.getProcessosAguardandoEntrega().get(0).getProcesso().getNumeroDetran();
        }
        
        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getProcessosParaDesistencia())) {
            return wrapper.getProcessosParaDesistencia().get(0).getProcesso().getNumeroDetran();
        }
        
        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getProcessosParaDesistenciaComRecurso())) {
            return wrapper.getProcessosParaDesistenciaComRecurso().get(0).getProcesso().getNumeroDetran();
        }
        
        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getProcessosJuridicosEntrega())) {
            return wrapper.getProcessosJuridicosEntrega().get(0).getProcesso().getNumeroDetran();
        }
        
        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getProcessosJuridicosOutrosAndamentos())) {
            return wrapper.getProcessosJuridicosOutrosAndamentos().get(0).getProcesso().getNumeroDetran();
        }
        
        return null;
    }

    
    private List<Long> filtrarIdPA(List<PAControleCnhWrapper> processos){
        return Lists.transform(processos, new Function<PAControleCnhWrapper, Long>() {
            @Override
            public Long apply(PAControleCnhWrapper f) {
                return f.getProcesso().getId();
            }
        });
    }
    
    /**
     * Carregar lista de processos administrativos para ControleCnh.
     * 
     * @param em
     * @param wrapper
     * @return
     * @throws AppException 
     */
    public List carregarListaProcessosParaControleCnh(EntityManager em, ControleCnhPAWrapper wrapper) throws AppException {
        
        List<PAControleCnhWrapper> pasAguardandoEntrega = 
                new ProcessoAdministrativoRepositorio().
                        getPAControleCnhWrapperPorCpfEAndamentoESituacaoIniciado(em, 
                                wrapper.getDadosCondutorBCA().getCpf(), 
                                PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_ENTREGA_CNH);
        
        pasAguardandoEntrega.addAll(new ProcessoAdministrativoRepositorio().
                        getPAControleCnhWrapperPJUPorCpfEAndamentoESituacaoIniciado(em, 
                                wrapper.getDadosCondutorBCA().getCpf(), 
                                PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_ENTREGA_CNH));

        List<PAControleCnhWrapper> processos = new ArrayList<>();
        processos.addAll(pasAguardandoEntrega);
        processos.addAll(wrapper.getProcessosParaDesistenciaSelecionados());
        processos.addAll(wrapper.getProcessosParaDesistenciaRecursoSelecionados());
        
        processos.addAll(wrapper.getProcessosJuridicosOutrosAndamentosSelecionados());
        
        if (DetranCollectionUtil.ehNuloOuVazio(processos)) {
            DetranWebUtils.applicationMessageException("É obrigatório selecionar pelo menos um processo administrativo.");
        }
        
        DetranCollectionUtil.ordenaLista(processos, "processo.tipo", false);
        
        return processos;
    }

    /**
     * Gravar protocolo.
     * 
     * @param usuarioLogado
     * @param listaMovimentoProcessoAdministrativoParaImpressao
     * @param urlBaseBirt
     * @return
     * @throws AppException 
     */
    public List<ArquivoPA> gravarProtocolo(DetranUserDetailsWrapper usuarioLogado, 
                                           List<MovimentoCnh> listaMovimentoProcessoAdministrativoParaImpressao, 
                                           String urlBaseBirt) throws AppException {
        
        List<ArquivoPA> listaArquivos = new ArrayList<>();
        
        if (!DetranCollectionUtil.ehNuloOuVazio(listaMovimentoProcessoAdministrativoParaImpressao)) {
            
            for (MovimentoCnh movimentoCnh : listaMovimentoProcessoAdministrativoParaImpressao) {
                
                ArquivoPA arquivoPa = 
                        (ArquivoPA) getProcessoAdministrativoService()
                            .gravarArquivoProtocoloControleCnh(new MovimentoCnhWrapper(movimentoCnh), urlBaseBirt);
                
                listaArquivos.add(arquivoPa);
            }
        }
        
        return listaArquivos;
    }

    /**
     * Imprimir os protocolos do controle cnh direto na impressora, duas vias.
     * 
     * @param usuarioLogado
     * @param listaArquivos
     * @throws AppException 
     */
    public void imprimirProtocoloControleCnh(DetranUserDetailsWrapper usuarioLogado, 
                                             List<ArquivoPA> listaArquivos) throws AppException {
        
        LOG.info("Quantidade para impressão: {0}", listaArquivos.size());
            
        if (usuarioLogado.getUsuarioLocal().getNumeroDetran() == null) {
            DetranWebUtils.applicationMessageException("Erro ao recuperar informações do usuário.");
        }

        ImpressoraUsuario impressoraUsuario = 
                (ImpressoraUsuario) getAdministrativoService()
                        .getImpressoraDoUsuarioLogado(
                                usuarioLogado.getUsuarioLocal().getNumeroDetran(), 
                                ModeloImpressoraEnum.LASER);

        if (null != impressoraUsuario && null != impressoraUsuario.getImpressora()) {
            if (!DetranCollectionUtil.ehNuloOuVazio(listaArquivos)) {

                for (ArquivoPA arquivo : listaArquivos) {

                    getAdministrativoService()
                            .imprimirDocumentoNaImpressoDoUsuario(
                                    impressoraUsuario.getImpressora(), 
                                    arquivo.getByteArquivo(),
                                    null,
                                    NUMERO_COPIAS_PROTOCOLO_IMPRESSAO);
                }
            }
        }
    }

    private void validarCnhEmCartorio(EntityManager em, String cpf) throws AppException{
        DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getPJUComCnhEmCartorioPorCpf(em, cpf);
       if(pju != null){
           DetranWebUtils.applicationMessageException("Verificar Processo Judicial {0}, CNH em CARTÓRIO", 
                                                      "", 
                                                      pju.getProcessoJudicial().getProcessoAdministrativo().getNumeroProcessoMascarado());
       }
    }
}
