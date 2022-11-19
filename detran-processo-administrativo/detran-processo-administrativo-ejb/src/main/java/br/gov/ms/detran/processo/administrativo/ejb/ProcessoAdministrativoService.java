package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoArquivo;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoNotificacaoEnum;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.apo.ModuloTipoArquivoEnum;
import br.gov.ms.detran.comum.core.projeto.enums.apo.TipoLogradouroEnum;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.ResultLong;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.entidade.enums.adm.SexoEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.TipoExtensaoArquivoEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.bca.IBeanIntegracao;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.projeto.generico.DetranGenericRepository;
import br.gov.ms.detran.comum.projeto.service.DetranAbstractGenericService;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.*;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.ace.IAcessoService;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.core.iface.servico.hab.IControleCnhService;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP93;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import static br.gov.ms.detran.processo.administrativo.constantes.TipoArquivoPAConstante.DOCUMENTO_CONFERENCIA_CORREIOS;
import br.gov.ms.detran.processo.administrativo.core.bo.*;
import br.gov.ms.detran.processo.administrativo.core.bo.recursoonline.RecursoOnlineBO;
import br.gov.ms.detran.processo.administrativo.core.bo.recursoonline.RecursoOnlinePasso04BO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP12BO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP93BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.*;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.criteria.ControleCnhPACriteria;
import br.gov.ms.detran.processo.administrativo.criteria.ProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.criteria.RecursoCriteria;
import br.gov.ms.detran.processo.administrativo.criteria.UsuarioAcessoProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.dto.DtnIntraPJUPessoa;
import br.gov.ms.detran.processo.administrativo.entidade.*;
import br.gov.ms.detran.processo.administrativo.enums.*;
import br.gov.ms.detran.processo.administrativo.wrapper.*;
import br.gov.ms.detran.processo.administrativo.wrapper.pju.DadoProcessoJudicialWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.ConsultaPaWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoOnlinePaWrapper;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaCorreio;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author desenvolvimento
 * @param <T>
 */
@Stateless(mappedName = "ejb/ProcessoAdministrativoService")
@Remote(IProcessoAdministrativoService.class)
public class ProcessoAdministrativoService<T extends IBaseEntity> extends DetranAbstractGenericService implements IProcessoAdministrativoService {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoService.class);

    private IControleCnhService controleCnhService;

    private IApoioService apoioService;
    
    private IAcessoService acessoService;

    @Override
    @PersistenceContext(unitName = "DETRAN-PROCESSO-ADMINISTRATIVO-PU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    public IControleCnhService getControleCnhService() {
        if (controleCnhService == null) {
            controleCnhService = ServiceJndiLocator.<IControleCnhService>lookup("ejb/ControleCnhService");
        }
        return controleCnhService;
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

    public ProcessoAdministrativoService() {
    }

    @Override
    public List pesquisar(Class clazzEntidade, String namedQuery, Map params) throws AppException {

        if (DetranStringUtil.ehBrancoOuNulo(namedQuery)) {
            DetranWebUtils.applicationMessageException("Parâmetros inválidos.");
        }

        return new ProcessoAdministrativoRepositorio()
                .getListNamedQuery(em, clazzEntidade.getSimpleName() + namedQuery, params);
    }

    @Override
    public IBaseEntity getPAStatusAndamentoAtivoPorStatusEAndamento(Integer codigoPAAndamentoProcesso) throws AppException {
        return new PAStatusAndamentoRepositorio().getPAStatusAndamentoAtivoPorStatusEAndamento(em, codigoPAAndamentoProcesso);
    }

    @Override
    public List getInstauradosJson() {
        return new ProcessoAdministrativoServicoBO().getInstauradosJson(em);
    }

    @Override
    public List buscarTodosCondutoresParaPA(ICriteriaQueryBuilder iCriteriaQueryBuilder) throws AppException {
        return new DetranGenericRepository().getListNamedQuery(em, "DadosCondutorPAD.findAll");
    }

    @Override
    public List getListaProcessosAdministrativosAtivos() throws AppException {
        return new ProcessoAdministrativoRepositorio().getListaProcessosAdministrativosAtivos(em);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IBaseEntity gravar(IBaseEntity entidade) throws DatabaseException {
        return super.gravar(entidade);
    }

    @Override
    public ProcessoAdministrativo getProcessoAdministrativoPorNumeroProcessoAtivo(String numeroProcesso) throws AppException {
        return new ProcessoAdministrativoRepositorio().getProcessoAdministrativoPorNumeroProcessoAtivo(em, numeroProcesso);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public NotificacaoProcessoAdministrativo notificar(ProcessoAdministrativo processoAdministrativo, TipoFasePaEnum tipoNotificacao) throws AppException {
        return new NotificacaoProcessoAdministrativoBO().notificar(em, processoAdministrativo, tipoNotificacao);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gerarArquivoNotificacaoParaTipoArquivamentoIndevido(String urlReportBirt, NotificacaoProcessoAdministrativo notificacaoPa) throws AppException {
        new NotificacaoProcessoAdministrativoBO().gerarArquivoLab(em, urlReportBirt, notificacaoPa);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<NotificacaoProcessoAdministrativo> getListNotificacoesParaTransferenciaSGI() throws AppException {
        
        List andamentos
            = DetranCollectionUtil
                .montaLista(PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.TRANSFERIR_SGI_NOTIFICACAO_INSTAURACAO,
                PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.TRANSFERIR_SGI_NOTIFICACAO_PENALIZACAO,
                PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.TRANSFERIR_SGI_NOTIFICACAO_ENTREGA_CNH,
                PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.TRANSFERIR_SGI_NOTIFICACAO_ACOLHIMENTO,
                PAAndamentoProcessoConstante.NOTIFICACAO_NAO_CONHECIMENTO.TRANSFERIR_SGI_NOTIFICACAO_NAO_CONHECIMENTO,
                PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.TRANSFERIR_SGI_NOTIFICACAO_DESENTRANHAMENTO,
                PAAndamentoProcessoConstante.NOTIFICACAO_JARI.TRANSFERIR_SGI_NOTIFICACAO_JARI,
                PAAndamentoProcessoConstante.NOTIFICACAO_CURSO_EXAME.TRANSFERIR_SGI_NOTIFICACAO_CURSO_EXAME
                );
        
        return new NotificacaoProcessoAdministrativoRepositorio().getNotificadosPorAndamentoESituacao(em, andamentos);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void transferirNotificacoesParaSGI(String url) throws AppException {
        new TransferenciaNotificacoesBO().transferirNotificacoesSGI(em, url);
    }

    @Override
    @Deprecated
    public void gerarArquivoCorreio() throws AppException {
        new TransferenciaNotificacoesBO().gerarArquivoCorreio(em);
    }

    @Override
    @Deprecated
    public void envioArquivoCorreio() throws AppException {
        new TransferenciaNotificacoesBO().envioArquivoCorreio(em);
    }

    @Override
    public List getNotificados() throws AppException {
        return new NotificacaoProcessoAdministrativoBO().getNotificados(em);
    }

    @Override
    public Integer getCodigoAndamentoPorTipoNotificacaoEOrigemPA(TipoFasePaEnum tipo, IBaseEntity origemApoio) throws AppException {

        return new PATipoCorpoAndamentoRepositorio().
                getCodigoAndamentoPorTipoNotificacaoEOrigemPA(
                        em,
                        tipo,
                        (ApoioOrigemInstauracao) origemApoio);

    }

    @Override
    public BaseEntityAtivo getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracao(Enum tipo, BaseEntityAtivo origemApoio) throws AppException {
        return new PATipoCorpoAndamentoRepositorio()
                .getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracao(em,
                        (TipoFasePaEnum) tipo,
                        (ApoioOrigemInstauracao) origemApoio);
    }

    @Override
    public void lerDadosArquivoCorreiosAtualizarCorrespondenciaEAndamento() throws AppException {
        new RetornoARBO().lerDadosArquivoCorreiosAtualizarCorrespondenciaEAndamento();
    }

    @Override
    public List getRetornoAR() throws AppException {
        return new RetornoARBO().getRetornoAR(em);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void atualizarCorrespondencia(RetornoARDetalheWrapper detalheWrapper, Date dataRecebimento) throws AppException {
        new RetornoARBO().atualizarCorrespondencia(em, detalheWrapper, dataRecebimento);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IBaseEntity gravarArquivoProtocoloControleCnh(MovimentoCnhWrapper wrapper, String urlBaseBirt) throws AppException {
        return new ProtocoloBO().gravarArquivoProtocoloControleCnh(em, wrapper, urlBaseBirt);
    }

    @Override
    public List getInformacoesEntregaCnh() throws AppException {
        return new EntregaCnhBO().getInformacoesEntregaCnh(em);
    }

    @Override
    public MovimentoCnh getMovimentoProtocoloControleCnh(Long idPA, AcaoEntregaCnhEnum acao) throws AppException {
        return new MovimentoCnhRepositorio().getMovimentoPorProcessoAdministrativoEAcao(em, idPA, acao);

    }

    @Override
    public void desbloquearControleCnhPA(DetranUserDetailsWrapper usuarioLogado,
            ControleCnhPAWrapper wrapper,
            String urlBaseBirt,
            CnhSituacaoEntrega situacao,
            List movimentos) throws AppException {

        new ControleCnhPABO().desbloquearCnh(em, usuarioLogado, wrapper, urlBaseBirt, situacao, movimentos);
    }

    @Override
    public List getRecursoPorFiltros(ICriteriaQueryBuilder c) throws AppException {
        RecursoCriteria criteria = (RecursoCriteria) c;
        return new RecursoRepositorio().getRecursoPorFiltros(em, criteria);
    }

    @Override
    public IBaseEntity getMovimentoRecurso(Long id) throws AppException {

        RecursoMovimento entidade = new RecursoMovimentoRepositorio().getMovimentoRecursoPorRecursoID(em, id);

        return entidade;
    }

    @Override
    public List getProcessosAdministrativosPorCpf(String numeroDocumento) throws AppException {
        return new ProcessoAdministrativoRepositorio().getListProcessoAdministrativosAtivosPorCPFEntidadeCompleta(em, numeroDocumento);
    }

    @Override
    public void gravarRecurso(IBaseEntity entidade, Object usuariloLogado) throws AppException {
        new RecursoBO().gravar(em, (RecursoWrapper) entidade, (DetranUserDetailsWrapper) usuariloLogado);
    }

    @Override
    public BooleanEnum getIndiceForaPrazo(Long recursoId) throws AppException {
        return new RecursoMovimentoRepositorio().getIndiceForaPrazo(em, recursoId);
    }

    @Override
    public List getRecursos() throws AppException {
        return new RecursoBO().getRecursos(em);
    }

    @Override
    public List getListaMotivoAlegacao() throws AppException {
        return new MotivoAlegacaoRepositorio().getListaMotivoAlegacao(em);
    }

    @Override
    public void incluiArquivoProtocoloRecurso(RecursoWrapper wrapper) throws AppException {
        new ProtocoloBO().incluiArquivoProtocoloRecurso(em, wrapper);
    }

    @Override
    public IBaseEntity getProtocoloRecurso(RecursoWrapper wrapper) throws AppException {
        return new ProtocoloBO().getProtocoloRecurso(em, wrapper);
    }

    @Override
    public IBaseEntity getRecursoMovimentoPorRecurso(IBaseEntity entidade) throws AppException {
        return new RecursoMovimentoRepositorio().getRecursoMovimentoPorRecurso(em, (Recurso) entidade);
    }

    @Override
    public ResultLong getCountRecursoPorFiltros(ICriteriaQueryBuilder c) throws AppException {
        RecursoCriteria criteria = (RecursoCriteria) c;
        return (ResultLong) new RecursoRepositorio().getCountRecursoPorFiltros(em, criteria);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gerarPA(Integer qtdeAProcessar) {

        try {

            LOG.debug("Limpando TDB ...");
            em.createNativeQuery("DELETE FROM TB_TDB_PAD_DADOS_INFRACAO WHERE Usuario_Inclusao = :usuarioInclusao")
                    .setParameter("usuarioInclusao", "SP_TDA")
                    .executeUpdate();

            LOG.debug("Limpando TDA ...");
            em.createNativeQuery("DELETE FROM TB_TDA_PAD_DADOS_CONDUTOR WHERE Usuario_Inclusao = :usuarioInclusao")
                    .setParameter("usuarioInclusao", "SP_TDA")
                    .executeUpdate();

            LOG.debug("Gerando TDB e TDA ...");

            Query queryIns1 = em.createNativeQuery(
                    "INSERT into TB_TDA_PAD_DADOS_CONDUTOR (Tda_Numero_CPF,Tda_Nome_Condutor,Tda_Sexo,"
                    + "Tda_Numero_CNH,Tda_Numero_PGU,Tda_Numero_Registro, "
                    + "Tda_Data_Habilitacao_Definitiva,Tda_Data_Primeira_Habilitacao, "
                    + "Tda_Numero_Permissionado,Versao_Registro, Usuario_Inclusao,Data_Inclusao) "
                    + "SELECT TOP(:qtdePA) Pad_Numero_CPF, Pad_Nome_Condutor, "
                    + "case when pad_sexo = 1 then 0 else 1 end as Pad_Sexo, "
                    + "Pad_Numero_CNH, Pad_Numero_PGU, Pad_Numero_Registro, "
                    + "Pad_Data_Habilitacao_Definitiva, Pad_Data_Primeira_Habilitacao, "
                    + "MAX(Pad_Numero_CNH_Permissionado), 0, :usuarioInclusao, :dataInclusao "
                    + "From TB_PAD_PONTUACAO PAD "
                    + "WHERE not exists(select 1 from dbo.TB_TDA_PAD_DADOS_CONDUTOR tda where tda.Tda_Numero_CPF = PAD.Pad_Numero_CPF) "
                    + "AND not exists(select 1 from dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc where tdc.Tdc_Cpf = PAD.Pad_Numero_CPF) "
                    + "GROUP BY Pad_Numero_CPF,Pad_Nome_Condutor, pad_sexo, Pad_Numero_CNH, Pad_Numero_PGU, Pad_Numero_Registro, "
                    + "Pad_Data_Habilitacao_Definitiva,Pad_Data_Primeira_Habilitacao");

            int rows = queryIns1
                    .setParameter("usuarioInclusao", "SP_TDA")
                    .setParameter("dataInclusao", new Date())
                    .setParameter("qtdePA", qtdeAProcessar)
                    .executeUpdate();

            LOG.debug("Registros afetados: {0}", rows);

            Query queryIns2 = em.createNativeQuery(
                    "INSERT INTO TB_TDB_PAD_DADOS_INFRACAO(Tdb_Cpf_Condutor,Tdb_Isn,Tdb_Auto,Tdb_Infracao, "
                    + "Tdb_Data_Infracao,Tdb_Autuador,Tdb_Origem_Infracao, "
                    + "Tdb_Qte_Pontos_Infracao,Tdb_Placa,Tdb_Origem_Informacao_Pontuacao,Tdb_Situacao_Pontuacao, "
                    + "Tdb_Status_Pontuacao,Versao_Registro,Usuario_Inclusao,Data_Inclusao) "
                    + "SELECT Pad_Numero_CPF, Pad_Isn, Pad_Auto, Pad_Infracao, Pad_Data_Infracao, Pad_Autuador, Pad_Origem_Infracao, "
                    + "Pad_Qte_Pontos_Infracao, Pad_Placa, Pad_Origem_Informacao_Pontuacao, Pad_Situacao_Pontuacao, "
                    + "Pad_Status_Pontuacao, 0, :usuarioInclusao, :dataInclusao "
                    + "From TB_PAD_PONTUACAO "
                    + "where EXISTS (select 1 from TB_TDA_PAD_DADOS_CONDUTOR WHERE Pad_Numero_CPF = Tda_Numero_CPF)");

            int rows2 = queryIns2
                    .setParameter("usuarioInclusao", "SP_TDA")
                    .setParameter("dataInclusao", new Date())
                    .executeUpdate();

            LOG.debug("Registros afetados: {0}", rows2);

        } catch (Exception ex) {
            LOG.error("Erro", ex);
        }
    }

    @Override
    public BaseEntityAtivo cancelarRecurso(DetranUserDetailsWrapper usuarioLogado, RecursoWrapper wrapper) throws AppException {
        return new RecursoBO().cancelarRecurso(em, usuarioLogado, wrapper);
    }

    @Override
    public void validarEnderecoAlternativo(PAEnderecoAlternativoWrapper wrapper) throws AppException {
        new PAEnderecoAlternativoRepositorio().validarEnderecoJaExistenteParaPA(em, wrapper);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void alterarParaAndamentoEspecifico(ProcessoAdministrativo processo, Integer codigoAndamentoDestino) throws AppException {
        new AndamentoProcessoAdministrativoManager().alterarParaAndamentoEspecifico(em, processo, codigoAndamentoDestino);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(
            ProcessoAdministrativo processoAdministrativo, Integer codigoFluxoProcessoDestino, Integer codigoAndamentoDestino) throws AppException {
        new AndamentoProcessoAdministrativoManager().alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(em, processoAdministrativo, codigoFluxoProcessoDestino, codigoAndamentoDestino);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(
            ProcessoAdministrativo processoAdministrativo, Integer codigoFluxoProcessoDestino, Integer codigoAndamentoDestino, String usuarioAlteracao) throws AppException {
        new AndamentoProcessoAdministrativoManager().alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(em, processoAdministrativo, codigoFluxoProcessoDestino, codigoAndamentoDestino,usuarioAlteracao);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void proximoAndamento(ProcessoAdministrativo processo) throws AppException {
        new AndamentoProcessoAdministrativoManager2().proximoAndamento(em, processo.getId(), null);
    }

    @Override
    public IBaseEntity getPAOcorrenciaStatusAtiva(Long idProcessoAdministrativo) throws AppException {
        return new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, idProcessoAdministrativo);
    }

    @Override
    public List getProcessoAdministrativoBloqueioBCA() throws AppException {
        return new BloqueioBCABO().getBloqueiosBCA(em);
    }

    @Override
    public IBaseEntity getComplementoDoPA(String numeroProcesso) throws AppException {
        return new PAComplementoRepositorio().getPAComplementoPorNumeroPAEParametroEAtivo(em, numeroProcesso, PAParametroEnum.TEMPO_PENALIDADE);
    }

    @Override
    public List getProcessoAdministrativoDesistentes() throws AppException {
        return new ProcessoAdministrativoBO().getProcessoAdministrativoDesistentes(em);
    }

    @Override
    public List getRecursosCancelados() throws AppException {
        return new RecursoBO().getRecursosCancelados(em);
    }

    @Override
    public List getMovimentaoPa() throws AppException {
        return new MovimentacaoPABO().getMovimentaoPa(em);
    }

    @Override
    public List getInformacoesProva() throws AppException {
        return new ProcessoAdministrativoBO().getInformacoesProva(em);
    }

    @Override
    public void gravarMovimentacao(MovimentacaoWrapper wrapper, DetranUserDetailsWrapper usuarioLogado) throws AppException {
        new MovimentacaoPABO().gravarMovimentacao(em, wrapper, usuarioLogado.getOperador().getUsuario());
    }

    @Override
    public List getProcessoAdministrativoInfracaoPorProcessoId(Long paId) throws AppException {
        return new ProcessoAdministrativoInfracaoRepositorio().getInfracoesPorProcessoAdministrativoID(em, paId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void finalizarAndamentoAtual(IBaseEntity paOcorrenciaStatusAtiva) throws AppException {
        new AndamentoProcessoAdministrativoManager().finalizarAndamentoAtual(em, (PAOcorrenciaStatus) paOcorrenciaStatusAtiva);
    }

    @Override
    public Movimentacao getMovimentacaoSuspensaoDesativaRecente(Long paId) throws AppException {
        return new MovimentacaoPARepositorio().getMovimentacaoSuspensaoDesativaRecente(em, paId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void alterarParaFluxoEAndamentoEspecificos(ProcessoAdministrativo pa, PAFluxoFase fluxoFase) throws AppException {
        new AndamentoProcessoAdministrativoManager().alterarParaFluxoEAndamentoEspecificos(em, pa, fluxoFase);
    }

    @Override
    public ProcessoAdministrativoAgravado getAgravadoPorPAOriginal(Long id) throws AppException {
        return new ProcessoAdministrativoAgravadoRepositorio().getAgravadoPorPAOriginal(em, id);
    }

    @Override
    public ProcessoAdministrativoApensado getApensadoPorPAOriginal(Long id) throws AppException {
        return new ProcessoAdministrativoApensadoRepositorio().getApensadoPorPAOriginal(em, id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gravarFalhaEMandarEmail(Exception e, String cpf, String origemErro, DetranEmailWrapper email) {
        new ProcessoAdministrativoFalhaBO().gravarFalhaEEmail(em, origemErro, cpf, e, email);
    }

    @Override
    public MovimentoCnh getMovimentoCnhParaDesentranhamento(String numeroProcesso) throws AppException {
        return new MovimentoCnhRepositorio().getMovimentoCnhParaDesentranhamento(em, numeroProcesso);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ParametrosIntegracaoBloqueioBCAWrapper gravarBloqueioBCAEAndamento(ProcessoAdministrativo processo,
            Integer codigoAndamentoEspecifico,
            Long idUsuario) throws AppException {

        if (codigoAndamentoEspecifico == null) {

            new AndamentoProcessoAdministrativoManager2().proximoAndamento(em, processo.getId(), null);

        } else {

            new AndamentoProcessoAdministrativoManager2().executarMudancaFluxoEAndamento(em,
                    processo,
                    PAFluxoProcessoConstante.FLUXO_NORMAL,
                    codigoAndamentoEspecifico);

        }

        return new BloqueioBCABO().gravarBloqueioBCA(em, processo, idUsuario);
    }

    @Override
    public Movimentacao getMovimentacaoAtivaRecentePorProcessoAdministrativo(Long paId) throws AppException {
        return new MovimentacaoPARepositorio().getUltimaMovimentacaoAtivaPorProcessoAdministrativo(em, paId);
    }

    @Override
    public void executarIntegracaoAEMNPP12(ProcessoAdministrativo pa, List listaAutoInfracoes, List codigosInfracao) throws AppException {

        new AEMNPP12BO().executarIntegracaoAEMNPP12(pa,
                listaAutoInfracoes,
                codigosInfracao);
    }

    @Override
    public List getAndamentos() throws AppException {
        return new PAAndamentoProcessoRepositorio().getAndamentosEmUso(em);
    }

    @Override
    public void ajustaProtocoloRecursoGeraCodigoBarra(Protocolo protocolo) throws AppException {
        new ProtocoloBO().ajustaProtocoloRecursoGeraCodigoBarra(em, protocolo);
    }

    @Override
    public MovimentoCnh getMovimentoCnhParaDesentranhamentoPorCpfCondutor(String cpf) throws AppException {
        return new MovimentoCnhRepositorio().getMovimentoCnhParaDesentranhamentoPorCpfCondutor(em, cpf);
    }

    @Override
    public Boolean existeMaisDeUmProcessoAdministrativoParaCondutor(String cpf) throws AppException {
        return new ProcessoAdministrativoRepositorio().existeMaisDeUmProcessoAdministrativoParaCondutor(em, cpf);
    }

    @Override
    public BaseEntityAtivo getCorrespondenciaCorreioDevolucaoPorCorrespondencia(Long correspondenciaId) throws AppException {
        return new CorrespondenciaCorreioDevolucaoRepositorio().getCorrespondenciaCorreioDevolucaoPorCorrespondencia(em, correspondenciaId);
    }

    @Override
    public BaseEntityAtivo getNotificacaoComplementoPorNotificacao(Long notificacaoProcessoAdministrativoId) throws AppException {
        return new NotificacaoComplementoRepositorio().getComplementoPorNotificacao(em, notificacaoProcessoAdministrativoId);
    }

    @Override
    public BaseEntityAtivo getEditalProcessoAdministrativoPorNotificacao(Long notificacaoProcessoAdministrativoId) throws AppException {
        return new EditalProcessoAdministrativoRepositorio().getEditalPorNotificacao(em, notificacaoProcessoAdministrativoId);
    }

    @Override
    public BaseEntityAtivo getResultadoRecursoAtivoPorRecurso(Long idRecurso) throws AppException {
        return new ResultadoRecursoRepositorio().getResultadoRecursoAtivoPorRecurso(em, idRecurso);
    }

    @Override
    public IBaseEntity getCorrespondenciaCorreioPorCorrespondencia(Long id) throws AppException {
        return new CorrespondenciaCorreioRepositorio().getCorrespondenciaCorreioPorCorrespondenciaIdTodaEntidade(em, id);
    }

    @Override
    public IBeanIntegracao validarEntregaCnhParaPA(String cpf, Long cnh) throws AppException {
        return new ControleCnhPABO().validarEntregaCnhParaPA(em, cpf, cnh);
    }

    @Override
    public IBaseEntity carregarDadosControleCnh(ProcessoAdministrativoCriteria criteria) throws AppException {
        return new ControleCnhPABO().carregarDadosControleCnh(em, criteria);
    }

    @Override
    public IBaseEntity getPenalidadePorPA(Long idPA) throws AppException {
        return new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, idPA);
    }

    @Override
    public Object montarUsuarioAcessoWrapper(DetranUserDetailsWrapper usuarioLogado, DetranAbstractCriteria criteria) throws AppException {
        return new UsuarioAcessoProcessoAdministrativoBO().montarUsuarioAcessoWrapper(em, usuarioLogado, (UsuarioAcessoProcessoAdministrativoCriteria) criteria);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executarMudancaFluxoEAndamento(ProcessoAdministrativo processoAdministrativo, Integer fluxo, Integer andamento) throws AppException {
        new AndamentoProcessoAdministrativoManager2()
                .executarMudancaFluxoEAndamento(
                        em,
                        processoAdministrativo,
                        fluxo,
                        andamento);
    }

    @Override
    public IBaseEntity getRecursoMovimentoPorRecursoETipoProtocolo(Recurso recurso, TipoSituacaoProtocoloEnum tipo) throws AppException {
        return new RecursoMovimentoBO()
                .getRecursoMovimentoPorRecursoETipoProtocolo(em, recurso, tipo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void iniciarFluxo(ProcessoAdministrativo pa, Integer fluxo) throws AppException {
        new AndamentoProcessoAdministrativoManager2().iniciarFluxo(em, pa, fluxo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void proximoAndamentoComAtualFinalizado(ProcessoAdministrativo pa) throws AppException {
        new AndamentoProcessoAdministrativoManager().proximoAndamentoComAtualFinalizado(em, pa);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void mudancaDeFluxoComAndamentoAtualFinalizado(ProcessoAdministrativo pa, Boolean mudancaFluxo, Integer fluxo) throws AppException {
        new AndamentoProcessoAdministrativoManager().mudancaDeFluxoComAndamentoAtualFinalizado(em, pa, mudancaFluxo, fluxo);
    }

    @Override
    public List getMovimentoPorControleCnhEAcaoEntrega(Long cnhControleId, AcaoEntregaCnhEnum acaoEntregaCnhEnum) throws AppException {
        return new MovimentoCnhRepositorio().getMovimentoPorControleCnhEAcaoEntrega(em, cnhControleId, AcaoEntregaCnhEnum.ENTREGA);
    }

    @Override
    public void checaSeExisteAlgumProcessoAdministrativoQueInvalidaExecutarDesbloqueioCnh(Long numeroDetran) throws AppException {
        new ProcessoAdministrativoRepositorio()
                .checaSeExisteAlgumProcessoAdministrativoQueInvalidaExecutarDesbloqueioCnh(em, numeroDetran);
    }

    @Override
    public void validaSeExisteProcessoAdministrativoValidoParaExecutarDesbloqueioCnh(Long numeroDetran) throws AppException {
        new ProcessoAdministrativoRepositorio()
                .validaSeExisteProcessoAdministrativoValidoParaExecutarDesbloqueioCnh(em, numeroDetran);
    }

    @Override
    public List getMovimentoPorControleCnhEAcaoEntregaParaDesbloqueio(
            Long cnhControleId, AcaoEntregaCnhEnum acaoEntregaCnhEnum) throws AppException {

        return new MovimentoCnhRepositorio()
                .getMovimentoPorControleCnhEAcaoEntregaParaDesbloqueio(em, cnhControleId, AcaoEntregaCnhEnum.ENTREGA);
    }

    @Override
    public BaseEntityAtivo getPrioridadeFluxoAmparoAtivoPorFluxoProcesso(BaseEntityAtivo fluxoProcesso) throws AppException {
        return new PAPrioridadeFluxoAmparoRepositorio().getPrioridadeFluxoAmparoAtivoPorFluxoProcesso(em, (PAFluxoProcesso) fluxoProcesso);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BaseEntityAtivo getFluxoFaseDoProcessoAdministrativo(BaseEntityAtivo pa) throws AppException {
        return new PAFluxoFaseRepositorio().getFluxoFaseDoProcessoAdministrativo(em, (ProcessoAdministrativo) pa);
    }

    @Override
    public BaseEntityAtivo getFluxoFaseDoProcessoAdministrativoParaConsulta(BaseEntityAtivo pa) throws AppException {
        return new PAFluxoFaseRepositorio().getFluxoFaseDoProcessoAdministrativoParaConsulta(em, (ProcessoAdministrativo) pa);
    }

    @Override
    public BaseEntityAtivo getPaFluxoAndamentoPorPAFluxoFaseEPAFluxoProcesso(BaseEntityAtivo pAFluxoFase, List listaPaFluxoProcesso) throws AppException {
        return new PAFluxoAndamentoRepositorio()
                .getPaFluxoAndamentoPorPAFluxoFaseEPAFluxoProcesso(em, (PAFluxoFase) pAFluxoFase, listaPaFluxoProcesso);
    }

    @Override
    public IBaseEntity getCorrespondenciaCorreioPorProcessoETipo(Long id, TipoFasePaEnum tipo) throws AppException {
        return new CorrespondenciaCorreioRepositorio().getCorrespondenciaCorreioPorProcessoETipo(em, id, tipo);
    }

    @Override
    public List<ProcessoAdministrativoRecursoWrapper>
            getListProcessoAdministrativoPermitidosAbrirRecursoPorCPFCondutor(String numeroDocumento) throws AppException {

        return new ProcessoAdministrativoRepositorio()
                .getListProcessoAdministrativoPermitidosAbrirRecursoPorCPFCondutor(em, numeroDocumento);
    }

    @Override
    public Object montarProcessoAdministrativoXml(Object wrapper) throws AppException {
        return new ProcessoAdministrativoBO().montarProcessoAdministrativoXml((ProcessoAdministrativoWrapper) wrapper);
    }

    /**
     * @param idUsuario
     * @param urlMenuAplicacao
     * @return
     * @throws AppException
     */
    @Override
    public Boolean validaSePermiteUsuarioCadastrarDataRecursoRetroativa(Long idUsuario, String urlMenuAplicacao) throws AppException {
        return new RecursoBO()
                .validaSePermiteUsuarioCadastrarDataRecursoRetroativa(
                        em,
                        idUsuario,
                        urlMenuAplicacao
                );
    }

    /**
     * @param numeroProcesso
     * @param usuarioLogado
     * @throws AppException
     */
    @Override
    public void obterInformacoesProvaInfrator(String numeroProcesso, Object usuarioLogado) throws AppException {
        new InformacoesProvaInfratorBaseNacionalBO().obterInformacoesProvaInfrator(em, numeroProcesso, (DetranUserDetailsWrapper) usuarioLogado);
    }

    @Override
    public ConsultaPaWSWrapper montarConsultaPA(String numeroProcesso) throws AppException {
        return new ConsultaPABO().montarConsultaPA(em, numeroProcesso);
    }

    @Override
    public List<PAAndamentoProcesso> getListaPAAndamentoProcessoPorListaCodigo(List codigos) throws AppException {
        return new PAAndamentoProcessoRepositorio().getListaPAAndamentoProcessoPorListaCodigo(em, codigos);
    }

    @Override
    public Object getInformacoesProva(BaseEntityAtivo pa) throws AppException {
        return new ProcessoAdministrativoBO().getInformacoesProva(em, (ProcessoAdministrativo) pa);
    }

    @Override
    public BaseEntityAtivo getPAStatusAndamentoPorAndamentoProcesso(BaseEntityAtivo andamentoProcesso) throws AppException {
        return new PAStatusAndamentoRepositorio().getPAStatusAndamentoPorAndamentoProcesso(em, (PAAndamentoProcesso) andamentoProcesso);
    }

    @Override
    public void checaBCAParaProcessoAdministrativo(String urlPathParaRelatorio) throws AppException {

        new ProcessoAdministrativoBCABO()
                .executaAnaliseEExecucaoBloqueioEDesbloqueioParaProcessoAdministrativo(em, urlPathParaRelatorio);
    }

    @Override
    public Object montarMovimentacaoPaXml(Object object) throws AppException {
        return new MovimentacaoPABO().montarMovimentacaoPaXml((MovimentacaoWrapper) object);
    }

    @Override
    public void validarPAParaExecucaoServico(String numeroProcesso, String servico) throws AppException {
        new PAMovimentacaoFaseBO().validarPAParaExecucaoServico(em, numeroProcesso, servico);
    }

    @Override
    public ParametrosIntegracaoBloqueioBCAWrapper montarWrapperIntegracaoAEMNPP13(ProcessoAdministrativo processoAdministrativo) throws AppException {
        return new BloqueioBCABO().montarParametrosIntegracaoBloqueioBCAWrapper(em, processoAdministrativo);
    }

    @Override
    public Long buscarIdUsuario(String usuario) throws AppException {
        Long usuarioId = new PAPessoaRepositorio().buscarUsuarioCadastrado(em, usuario);

        if (usuarioId == null) {
            DetranWebUtils.applicationMessageException("Usuário não encontrado.");
        }
        return usuarioId;
    }

    @Override
    public ParametrosIntegracaoBloqueioBCAWrapper gravarBloqueioBCA(ProcessoAdministrativo processo, Long idUsuario) throws AppException {

        new AndamentoProcessoAdministrativoManager2().proximoAndamento(em, processo.getId(), null);

        return new BloqueioBCABO().gravarBloqueioBCA(em, processo, idUsuario);

    }

    @Override
    public List getOcorrenciaPorCpfEFluxo(String cpf, Integer codigoFluxo) throws AppException {
        return new PAOcorrenciaStatusRepositorio().getOcorrenciaPorCpfEFluxo(em, cpf, codigoFluxo);
    }

    @Override
    public BooleanEnum existeRecursoEmAnalisePorPA(Long idPA) throws AppException {
        Recurso recurso = new RecursoRepositorio().getRecursoAtivoPorProcessoAdministrativoESituacao(em, idPA, SituacaoRecursoEnum.EM_ANALISE);
        return recurso == null ? BooleanEnum.NAO : BooleanEnum.SIM;
    }

    @Override
    public Object montarParametrosIntegracaoBloqueioBCAWrapper(BaseEntityAtivo processoAdministrativo) throws AppException {
        return new BloqueioBCABO().montarParametrosIntegracaoBloqueioBCAWrapper(em, (ProcessoAdministrativo) processoAdministrativo);
    }

    @Override
    public Recurso getRecursoPorPAESituacao(Long id, SituacaoRecursoEnum situacaoRecursoEnum) throws AppException {
        return new RecursoRepositorio().getRecursoAtivoPorProcessoAdministrativoESituacao(em, id, SituacaoRecursoEnum.EM_ANALISE);
    }

    @Override
    public List getProcessoAdministrativoDesistentesRecursoInstauracaoPenalizacao() throws AppException {
        return new ProtocoloRepositorio().getProcessoAdministrativoDesistentesRecursoInstauracaoPenalizacao(em);
    }

    @Override
    public void validarServicoExternoMesmoNomeAtivo(PAServicoExterno entidade) throws AppException {
        PAServicoExterno servico = new PAServicoExternoRepositorio().getPAServicoExternoPorNomeAtivo(em, entidade.getNome());
        if (servico != null) {
            DetranWebUtils.applicationMessageException("severity.error.application.duplicadoexception", "", "nome");
        }
    }

    @Override
    public List getListOrigemInstauracao() throws AppException {
        return new ApoioOrigemInstauracaoRepositorio().findAll(em, ApoioOrigemInstauracao.class);
    }

    @Override
    public List getOcorrenciasDesistentesInstPenalizacao(String cpf) throws AppException {
        return new PAOcorrenciaStatusRepositorio().getOcorrenciasDesistentesInstPenalizacao(em, cpf);
    }

    @Override
    public Protocolo getProtocoloPorIdPAETipoNotificacao(Long idPA, TipoNotificacaoEnum tipoNotificacaoEnum) throws AppException {
        return new ProtocoloRepositorio().getProtocoloPorProcessoAdministrativoETipoNotificacao(em, idPA, tipoNotificacaoEnum);
    }

    @Override
    public IBaseEntity gravarArquivoProtocoloDesistenciaInstPen(Protocolo protocolo, String urlBaseBirt) throws AppException {
        return new ProtocoloBO().gravarArquivoProtocoloDesistenciaInstPen(em, protocolo, urlBaseBirt);
    }

    @Override
    public PAParametroEnum getInformacaoDesistente(Long id) throws AppException {
        PAComplemento complementoDesistente = new PAComplementoRepositorio().getComplementoDesistenciaPorPA(em, id);

        return complementoDesistente == null ? null : complementoDesistente.getParametro();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List carregarListaProcessosParaControleCnh(ControleCnhPAWrapper wrapper) throws AppException {
        return new ControleCnhPABO().carregarListaProcessosParaControleCnh(em, wrapper);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gravarControleCnhPA(DetranUserDetailsWrapper usuarioLogado, ControleCnhPAWrapper entity, String urlBaseBirt, List listaProcessos) throws AppException {
        new ControleCnhPABO().gravarControleCnhPA(em, usuarioLogado, entity, urlBaseBirt, listaProcessos);
        
        new ProcessoBloqueioPessoaBO().iniciaExecucaoEntregaCnh(em, listaProcessos, new Date());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<ArquivoPA> gravarProtocolo(DetranUserDetailsWrapper usuarioLogado, List listaMovimentos, String urlBaseBirt) throws AppException {
        return new ControleCnhPABO().gravarProtocolo(usuarioLogado, listaMovimentos, urlBaseBirt);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void imprimirProtocoloControleCnh(DetranUserDetailsWrapper usuarioLogado, List listaArquivos) throws AppException {
        new ControleCnhPABO().imprimirProtocoloControleCnh(usuarioLogado, (List<ArquivoPA>) listaArquivos);
    }

    @Override
    public ProcessoAdministrativo getProcessoAdministrativo(Long idProcessoAdministrativo) throws AppException {
        return new ProcessoAdministrativoRepositorio().getProcessoAdministrativoPorId(em, idProcessoAdministrativo);
    }

    @Override
    public ProcessoAdministrativo getProcessoAdministrativo(String numeroProcesso) throws AppException {
        return new ProcessoAdministrativoRepositorio().getProcessoAdministrativoPorNumeroProcessoSemAtivo(em, numeroProcesso);
    }

    @Override
    public ProcessoAdministrativo getProcessoAdministrativoAtivoPorPAAndamentoCodigo(PAAndamentoEnum andamento) throws AppException {
        return new ProcessoAdministrativoRepositorio().getProcessoAdministrativoAtivoPorPAAndamentoCodigo(em, andamento);
    }

    @Override
    public void executarAndamento48paraEntregaCnh(BaseEntityAtivo pa) throws AppException {
        new ProcessoAdministrativoBO().executarAndamento48paraEntregaCnh(em, (ProcessoAdministrativo) pa);
    }

    @Override
    public List buscarProcessosAdministrativosNoAndamento48ComCnhEntregue() throws AppException {

        return new ProcessoAdministrativoRepositorio().getProcessosAdministrativosAndamento48paraCnhSituacaoEntrega(em);

    }

    @Override
    public List buscarProcessosAdministrativosNoAndamento48ComPenaCumprida() throws AppException {

        return new ProcessoAdministrativoRepositorio().buscarProcessosAdministrativosNoAndamento48ComPenaCumprida(em);

    }

    @Override
    public List getProcessosAdministrativosParaProcessoJuridico(String cpf) throws AppException {
        return new ProcessoAdministrativoRepositorio().getProcessosAdministrativosParaProcessoJuridico(em, cpf);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BaseEntityAtivo gravarDadoProcessoJudicial(DadoProcessoJudicialWrapper wrapper, Object usuarioLogado, String urlBaseBirt) throws AppException {
        DadoProcessoJudicial processoJudicial = new DadoProcessoJudicialBO().gravarDadoProcessoJudicial(em,
                wrapper,
                (DetranUserDetailsWrapper) usuarioLogado,
                urlBaseBirt);
        if(processoJudicial.getIndicativoPrazoIndeterminado().equals(BooleanEnum.NAO) && !processoJudicial.getIdentificacaoRecolhimentoCnh().equals(IdentificacaoRecolhimentoCnhEnum.INEXISTENTE))
            new ProcessoRascunhoBloqueioBO().criarRascunhoBloqueioParaJuridico(em, processoJudicial.getProcessoJudicial().getProcessoAdministrativo(), processoJudicial.getDataInicioPenalidade());
        
        return processoJudicial;
    }

    @Override
    public ObjetoCorrespondenciaCorreioWrapper getObjetoCorrespondenciaCorreio(Enum tipoLogradouroEnum) throws AppException {

        TipoLogradouroEnum tipEnum = (TipoLogradouroEnum) tipoLogradouroEnum;

        return new NotificacaoProcessoAdministrativoRepositorio()
                .getObjetoCorrespondenciaCorreio(
                        em,
                        tipEnum.getLabel());

    }

    @Override
    public BaseEntityAtivo gravarCorrespondencia(BaseEntityAtivo processoAdministrativo,
            Object wrapperCorreio,
            Enum tipoNotificacaoProcesso) throws AppException {

        return new NotificacaoProcessoAdministrativoBO().
                gravarCorrespondencia(em,
                        (ProcessoAdministrativo) processoAdministrativo,
                        (TipoFasePaEnum) tipoNotificacaoProcesso);
    }

    @Override
    public List geraRegistroInfracoesDespacho(String numeroProcesso) throws AppException {
        return new ProcessoAdministrativoInfracaoBO().geraRegistroInfracoesDespacho(em, numeroProcesso);
    }

    @Override
    public Object getSomaPontuacaoDeInfracoesPA(Long idPA) throws AppException {
        return new ProcessoAdministrativoInfracaoRepositorio().getSomaPontuacaoDeInfracoesPA(em, idPA);
    }

    @Override
    public void atualizaProcessosAguardandoRetornoAR(List listaNumeroPA) throws AppException {
        new RetornoARBO().atualizaProcessosAguardandoRetornoAR(em, listaNumeroPA);
    }

    @Override
    public void executaDesistenciaRecInstPen(ProcessoAdministrativo pa,
            DetranUserDetailsWrapper usuarioLogado,
            RecursoOnlineCanceladoWrapper recursoOnlineCanceladoWrapper) throws AppException {

        new DesistenciaRecursoInstauracaoPenalizacaoBO().executaDesistenciaRecInstPen(em, pa, usuarioLogado, recursoOnlineCanceladoWrapper);
    }

    @Override
    public void arquivarProcessosSuspensaoPorCassacao() throws AppException {
        new ArquivamentoPASuspensaoBO().executa(em);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executaArquivamentoPASuspensaoPorProcessoCassacao(ProcessoAdministrativo processo, Usuario usuarioDetran) throws AppException {
        new ArquivamentoPASuspensaoBO().executaPorProcesso(em, processo, usuarioDetran);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void arquivarProcessoPorCassacao(EntityManager em, ProcessoAdministrativo processoSuspensao, ProcessoAdministrativo processoCassacao, Usuario usuarioDetran) throws AppException {
        new ArquivamentoPASuspensaoBO().arquivarProcesso(em, processoSuspensao, processoCassacao, usuarioDetran);
    }

    @Override
    public BaseEntityAtivo getMovimentoEntregaPorCpf(String cpf, Enum acaoEntregaCnhEnum) throws AppException {
        return new MovimentoCnhRepositorio().getMovimentoEntregaPorCpf(em, cpf, (AcaoEntregaCnhEnum) acaoEntregaCnhEnum);
    }

    @Override
    public DadoProcessoJudicial getDadoProcessoJudicialPorPA(ProcessoAdministrativo pa) throws AppException {
        return new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, pa.getId());
    }

    @Override
    public List getProcessosJuridicosParaEntregaCartorioPorCPF(String cpf) throws AppException {

        List processosAptos = new ArrayList();

        ProcessoAdministrativoRepositorio paRepo = new ProcessoAdministrativoRepositorio();

        List<ProcessoAdministrativo> pjuParaCartorio = new DadoProcessoJudicialRepositorio().getProcessosJuridicosParaEntregaCartorioPorCPF(em, cpf);

        pjuParaCartorio.forEach(pju -> {
            try {
                if (paRepo.validarPAAptoIniciarFluxo(em,
                        pju.getId(),
                        PAFluxoProcessoConstante.FLUXO_CARTORIO_JUDICIARIO)) {
                    processosAptos.add(pju);
                }
            } catch (AppException ex) {
                java.util.logging.Logger.getLogger(ProcessoAdministrativoService.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        return processosAptos;
    }

    @Override
    public void gravarCnhEntregueCartorio(DadoProcessoJudicialWrapper wrapper) throws AppException {
        new CnhEntregueCartorioBO().gravarEntregaCnhEmCartorio(em, wrapper);
    }

    @Override
    public List getListProcessosAdministrativosPorAndamento(List montaLista) throws AppException {
        return new ProcessoAdministrativoRepositorio().getListaProcessoAdministrativoPorAndamento(em, montaLista);
    }

    @Override
    public List getListaProcessoJuridicoAndamentoERecolhimentoCnh(List listaAndamentos) throws AppException {
        return new DadoProcessoJudicialRepositorio().getListaProcessoJuridicoAndamentoERecolhimentoCnh(em, listaAndamentos, IdentificacaoRecolhimentoCnhEnum.CARTORIO_JUDICIARIO);
    }

    @Override
    public void obterInformacoesProvaParaPJUCartorio(ProcessoAdministrativo processo, Long idUsuario) throws AppException {

        ResultadoProvaPA resultadoProvaPA = new InformacoesProvaInfratorBaseNacionalBO().
                obterInformacoesProvaInfratorBaseLocalWeb(em, processo);

        /**
         * Buscar informações da prova na BASE NACIONAL
         */
        if (null == resultadoProvaPA || resultadoProvaPA.getResultadoLaudoSituacao() == null) {

            resultadoProvaPA = new InformacoesProvaInfratorBaseNacionalBO().
                    obterInformacoesProvaInfratorBaseNacional(em, processo, idUsuario);

        }
        if (resultadoProvaPA != null) {
            new ResultadoProvaPABO().validarEGravarResultadoProva(em, processo, resultadoProvaPA);
        }
    }

    @Override
    public PAInicioFluxo getInicioFluxoAtivoPA(Long idPA) throws AppException {
        return new PAInicioFluxoRepositorio().getPAInicioFluxoAtivoPorProcessoAdministrativo(em, idPA);
    }

    @Override
    public Long getCondutorViaAEMNPP93(String cpf) throws AppException {

        Long numeroDetran = null;
        try {
            AEMNPP93 aemnpp93 = new AEMNPP93BO().executarIntegracaoAEMNPP93(cpf);

            if (aemnpp93 != null) {
                Integer sexo = null;
                if (!DetranStringUtil.ehBrancoOuNulo(aemnpp93.getSexo()) && !aemnpp93.getSexo().startsWith("*")) {
                    sexo = aemnpp93.getSexo().substring(0, 1) == "M"
                            ? SexoEnum.MASCULINO.ordinal()
                            : SexoEnum.FEMININO.ordinal();
                }
                numeroDetran = new DtnIntraPJUPessoa().gravarPessoa(
                        cpf, aemnpp93.getNomeCondutor(), aemnpp93.getNomeMae(),
                        aemnpp93.getNomePai(), aemnpp93.getDataNascimento(), sexo
                );
            }

        } catch (Exception e) {
            LOG.error(e);
        }

        return numeroDetran;
    }

    @Override
    public List buscarProcessosEspecificos() throws AppException {
        AbstractJpaDAORepository jpa = new AbstractJpaDAORepository();

        Object[] params = {
            PAAndamentoEnum.ANDAMENTO_217.getCodigo(),
            Boolean.FALSE
        };

        return jpa
                .getListNamedQuery(
                        em,
                        "PAAndamentoProcessoEspecifico.getListPAAndamentoProcessoEspecifico",
                        //                    1, 1, 
                        params
                );
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void atualizarProcessoEspecifico(PAAndamentoProcessoEspecifico processoEspecifico) throws AppException {
        AbstractJpaDAORepository jpa = new AbstractJpaDAORepository();
        jpa.update(em, processoEspecifico);
    }

    @Override
    public void registraGravacaoProcessamento(ProcessoAdministrativoBCARelatorioTotalWrapper wrapper, String integracao, byte[] pdf) throws AppException {
        new ProcessoAdministrativoBCABO().registraGravacaoProcessamento(em, wrapper, integracao, pdf);
    }

    @Override
    public List getAndamentosAutomaticos() throws AppException {
        return new PAAndamentoProcessoRepositorio().getAndamentosAutomaticos(em);
    }

    @Override
    public List getListProcessosAdministrativosPorAndamentoEmLote(List andamentos, Integer qtdPA) throws AppException {
        return new ProcessoAdministrativoRepositorio().getListaProcessoAdministrativoPorAndamentoEmLote(em, andamentos, qtdPA);
    }

    @Override
    public List getListaProcessoAdministrativoPorAndamentoETipoProcessoEmLote(List andamentos, Integer quantidadeProcessoAdministrativo, TipoProcessoEnum tipoProcesso) throws AppException {
        return new ProcessoAdministrativoRepositorio().getListaProcessoAdministrativoPorAndamentoETipoProcessoEmLote(em, andamentos, quantidadeProcessoAdministrativo, tipoProcesso);
    }

    @Override
    public NotificacaoProcessoAdministrativoWrapper buscarDadosRetornoAR(String objetoCorreios) throws AppException {

        NotificacaoProcessoAdministrativoWrapper wrapper = new NotificacaoProcessoAdministrativoWrapper();

        if (DetranStringUtil.ehBrancoOuNulo(objetoCorreios)) {
            DetranWebUtils.applicationMessageException("Objeto Correios não informado.");
        }

        NotificacaoProcessoAdministrativo notificacao = new NotificacaoProcessoAdministrativoRepositorio().getNotificacaoPorObjetoCorreios(em, objetoCorreios);

        if (notificacao == null) {
            DetranWebUtils.applicationMessageException("Não foi possível encontrar notificação correspondente.");
        }

        CorrespondenciaCorreio correspondenciaCorreio
                = new CorrespondenciaCorreioRepositorio()
                        .getCorrespondenciaCorreioPorNumeroETipoNotificacao(
                                em,
                                notificacao.getNumeroNotificacao(),
                                notificacao.getTipoNotificacaoProcesso()
                        );

        if (correspondenciaCorreio.getCorrespondencia() != null && correspondenciaCorreio.getCorrespondencia().getDataRecebimento() != null) {
            DetranWebUtils.applicationMessageException("Já existe um retorno de AR cadastrado para esta notificação.");
        }

        wrapper.setEntidade(notificacao);
        wrapper.setCorrespondenciaCorreio(correspondenciaCorreio);

        return wrapper;

    }

    @Override
    public List buscarCnhControleSemValidade() throws AppException {
        List<Integer> codigosAndamentoPA = new ProcessoAdministrativoBO().getCodesAndamentoProcessoPA();
        return new ConsultaCnhControleRecolhimentoRepositorio().buscarCnhControleSemValidade(em, codigosAndamentoPA);
    }

    @Override
    public List getMovimentoCnhComCnhControleValido(ICriteriaQueryBuilder criteria) throws AppException {
        ControleCnhPACriteria c = (ControleCnhPACriteria) criteria;
        return new MovimentoCnhRepositorio().getMovimentoCnhComCnhControleValido(em, c);
    }

    @Override
    public List getMovimentoCnhComCnhControleVencido(ICriteriaQueryBuilder criteria) throws AppException {
        ControleCnhPACriteria c = (ControleCnhPACriteria) criteria;
        return new MovimentoCnhRepositorio().getMovimentoCnhComCnhControleVencido(em, c);
    }

    @Override
    public Object getCountMovimentoCnhComCnhControleValido(ICriteriaQueryBuilder criteria) throws AppException {
        ControleCnhPACriteria c = (ControleCnhPACriteria) criteria;
        return new MovimentoCnhRepositorio().getCountMovimentoCnhComCnhControleValido(em, c);
    }

    @Override
    public Object getCountMovimentoCnhComCnhControleVencido(ICriteriaQueryBuilder criteria) throws AppException {
        ControleCnhPACriteria c = (ControleCnhPACriteria) criteria;
        return new MovimentoCnhRepositorio().getCountMovimentoCnhComCnhControleVencido(em, c);
    }

    @Override
    public void preArquivarProcessosPrescritos() throws AppException {

        em.createNativeQuery(
                "{call SP_TDG_PRE_ARQUIVAR_PROCESSOS_PRESCRITOS()}")
                .executeUpdate();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public LoteNotificacaoPA gerarLoteNotificacao(String nomeArquivoConferencia, TipoFasePaEnum tipoNotificacao, List lNotificacoes) throws AppException {
        DetranGenericRepository genericRepository = new DetranGenericRepository();

        LoteNotificacaoPA lote = new LoteNotificacaoPA(tipoNotificacao, nomeArquivoConferencia, lNotificacoes.size());

        genericRepository.insert(em, lote);

        for (Object n : lNotificacoes) {
            NotificacaoProcessoAdministrativo notificacao = (NotificacaoProcessoAdministrativo) n;
            notificacao.setLote(lote);
            genericRepository.update(em, notificacao);
        }

        return lote;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gerarArquivoConferenciaLote(LoteNotificacaoPA lote, String url) throws AppException, FileNotFoundException, IOException {

        DetranGenericRepository genericRepository = new DetranGenericRepository();

        Map<String, String> parametros = new HashMap<>();

        parametros.put("id_lote", lote.getId().toString());

        byte[] download = DetranHTTPUtil
                .download(
                        url + DetranReportsUtil.getReportParamsBirtUrl(
                                "conferencia_notificacao",
                                FormatoRelatorioEnum.PDF.getRptFormat(),
                                parametros,
                                "relatorios/processoadministrativo/notificacao/"
                        )
                );
        File arquivoPdfGerado = new File(lote.getNome());
        try (FileOutputStream fos = new FileOutputStream(arquivoPdfGerado)) {
            fos.write(download);
        }

        ArquivoPA arquivo = new ArquivoPA();

        arquivo.setAtivo(AtivoEnum.ATIVO);
        arquivo.setByteArquivo(download);
        arquivo.setDescricao("Arquivo conferência de notificações " + lote.getNome());
        arquivo.setExtensao(TipoExtensaoArquivoEnum.PDF);
        arquivo.setTabela("TB_TDI_LOTE_NOTIFICACAO_PA");
        arquivo.setIdTabela(lote.getId());

        TipoArquivo tipoArquivo
                = (TipoArquivo) getApoioService()
                        .getTipoArquivoPorCodigoEModulo(
                                DOCUMENTO_CONFERENCIA_CORREIOS,
                                ModuloTipoArquivoEnum.PA);

        arquivo.setTipoArquivo(tipoArquivo.getId());

        genericRepository.insert(em, arquivo);

        lote.setArquivo(arquivo);
        genericRepository.update(em, lote);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void enviarEmail(String nomeArquivo, TipoFasePaEnum tipoNotificacao) throws AppException {
        new TransferenciaNotificacoesBO().enviarEmail(em, nomeArquivo, tipoNotificacao);
    }

    @Override
    public List getPAComPrazoNotificacaoTerminado() throws AppException {
        return new NotificacaoProcessoAdministrativoBO().getPAComPrazoNotificacaoTerminado(em);
    }

    @Override
    public List getListProcessosComPrazoNotificacaoExpirado() throws AppException {
        return new ProcessoAdministrativoRepositorio().getListProcessosComPrazoNotificacaoExpirado(em);
    }

    @Override
    public Object registraPassoRecursoOnline(Object wrapper) throws AppException {

        RecursoOnlinePaWrapper recursoOnlinePaWrapper = (RecursoOnlinePaWrapper) wrapper;

        return new RecursoOnlineBO()
                .getExecutaPasso(PassoRecursoOnlinePAEnum.getPassoPorOrdinal(recursoOnlinePaWrapper.getPasso())).executa(em, recursoOnlinePaWrapper);
    }

    @Override
    public void registraFalhaRecursoOnline(RecursoPAOnlineFalha falha) {
        try {
            new DetranGenericRepository().insert(em, falha);
        } catch (DatabaseException e) {
            LOG.error("Ocorreu uma exceção ao registrar Tabela de falha TB_TEO_RECURSO_PA_ONLINE_FALHA.", e);
        }
    }

    @Override
    public RecursoOnlinePaDocumentoWrapper geraDocumentoFormularioRecursoOnline(String token, String protocolo, String urlBirt) throws AppException {
        return new RecursoOnlinePasso04BO().geraDocumentoFormularioRecursoOnline(em, token, protocolo, urlBirt);
    }

    @Override
    public RecursoPAOnline getRecursoOnlineMaisRecenteTokenOuProtocolo(String token, String protocolo) throws DatabaseException {
        return new RecursoPAOnlineRepositorio().getRecursoOnlineMaisRecentePorTokenEProtocolo(em, token, protocolo);
    }

    @Override
    public RecursoPAOnlineArquivo getFormularioAssinadoDoRecursoOnline(RecursoPAOnline recursoOnline) throws AppException {
        return new RecursoPAOnlineArquivoRepositorio().getFormularioAssinadoDoRecursoOnline(em, recursoOnline.getId());
    }

    @Override
    public List<RecursoPAOnlineArquivo> buscarDocumentosDoRecursoOnline(Long idrecursoOnline) throws AppException {

        return new RecursoPAOnlineArquivoRepositorio().getListArquivosPorRecursoOnline(em, idrecursoOnline);

    }

    @Override
    public Date getDataNotificacaoDoRecursoOnline(RecursoPAOnline entidade) throws AppException {

        NotificacaoProcessoAdministrativo notificacao
                = new NotificacaoProcessoAdministrativoRepositorio()
                        .getNotificacaoPorProcessoAdministrativoETipoNotificacao(em, entidade.getProcessoAdministrativo().getId(), entidade.getTipo());

        return notificacao.getDataNotificacao();
    }

    @Override
    public void recusarRecurso(BackOfficePaWrapper wrapper) throws AppException {
        new RecursoOnlineBO().recusarRecursoOnline(em, wrapper);
    }

    @Override
    public RecursoWrapper gravarRecursoOnline(BackOfficePaWrapper wrapper, DetranUserDetailsWrapper usuarioLogado) throws AppException {
        RecursoPAOnline recursoEfetivado = new RecursoOnlineBO().efetivarRecursoOnline(em, wrapper);
        
        RecursoWrapper recursoWrapper = new RecursoOnlineBO().montarRecursoWrapper(wrapper);
        
        new RecursoBO().gravar(em, recursoWrapper, usuarioLogado);
        recursoEfetivado.setRecurso(recursoWrapper.getEntidade());
        
        new RecursoPAOnlineRepositorio().update(em, recursoEfetivado);
        
        recursoWrapper.setRecursoEfetivado(recursoEfetivado);
 
        return recursoWrapper;
    }
    
    @Override
    public void gravarArquivoProtocoloRecursoOnline(RecursoWrapper wrapper) throws AppException{
        wrapper.setByteArquivo(new RecursoBO().getArquivoProtocoloRecursoOnline(wrapper));
        incluiArquivoProtocoloRecurso(wrapper);
    }

    @Override
    public void validarRecursoOnlineEmBackOffice(ProcessoAdministrativo processoAdministrativo) throws AppException {
        new RecursoOnlineBO().validarRecursoOnlineEmBackOffice(em, processoAdministrativo);
    }

    @Override
    public void enviarArquivosRecursoOnlineParaFTP(RecursoPAOnline recursoEfetivado, String urlReportBirt) throws AppException {
        new RecursoOnlineBO().enviarArquivosRecursoOnlineParaFTP(em, recursoEfetivado, urlReportBirt);
    }

    public String getIndicativoReativacaoInfracao(Long movimentacaoId, Long paId) throws DatabaseException {
        return new MovimentacaoInfracaoPARepositorio().getIndicativoReativacaoInfracao(em, movimentacaoId, paId);
    }

    @Override
    public void checaQuantidadeDiariaLimiteEmissaoNotificacao() throws AppException {
        new NotificacaoProcessoAdministrativoBO().checaQuantidadeDiariaLimiteEmissaoNotificacao(em);
    }

    @Override
    public Date getDataNotificacaoDoRecursoOnline2(RecursoPAOnline entidade) throws AppException {

        NotificacaoProcessoAdministrativo notificacao
                = new NotificacaoProcessoAdministrativoRepositorio()
                        .getListNotificacaoPorProcessoAdministrativo(em, entidade.getProcessoAdministrativo().getId()).get(0);

        return notificacao.getDataNotificacao();
    }

    @Override
    public Date getDataPrazoLimiteNotificacaoDoRecursoOnline(RecursoPAOnline entidade) throws AppException {

        NotificacaoProcessoAdministrativo notificacao
                = new NotificacaoProcessoAdministrativoRepositorio()
                        .getListNotificacaoPorProcessoAdministrativo(em, entidade.getProcessoAdministrativo().getId()).get(0);

        return notificacao.getDataPrazoLimite();
    }

    @Override
    public List getRecursoPorFiltros2(ICriteriaQueryBuilder c) throws AppException {
        RecursoCriteria criteria = (RecursoCriteria) c;
        return new RecursoRepositorio().getRecursoPorFiltros2(em, criteria);
    }

    @Override
    public ResultLong getCountRecursoPorFiltros2(ICriteriaQueryBuilder c) throws AppException {
        RecursoCriteria criteria = (RecursoCriteria) c;
        return (ResultLong) new RecursoRepositorio().getCountRecursoPorFiltros2(em, criteria);
    }

    @Override
    public List getFluxosProcessosAtivos() throws AppException {
        return new PAFluxoProcessoRepositorio().getListaFluxoProcessoAtivo(em);
    }

    @Override
    public List getAndamentosAtivos() throws AppException {
        return new PAAndamentoProcessoRepositorio().getAndamentosAtivos(em);
    }
    
    @Override
    public List getAndamentosAtivosPorFluxo(Integer codigo) throws AppException {
        return new PAAndamentoProcessoRepositorio().getAndamentosAtivosPorFluxo(codigo,em);
    }

    @Override
    public PAFluxoFase getPAFluxoFasePorFluxoProcessoEAndamento(PAFluxoProcesso fluxoProcesso, PAAndamentoProcesso andamento) throws AppException {

        return new PAFluxoFaseRepositorio()
                .getPAFluxoFasePorAndamentoProcessoEPrioridadeFluxoAmparo(
                        em,
                        andamento,
                        fluxoProcesso
                );
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<ProcedureProcessoAdministrativoBloqueio> executaProcedureProcessoAdministrativoBloqueio(String cpf, Long idProcessoAdministrativo) throws AppException {
        return new BloqueioBCARepositorio().executaProcedureProcessoAdministrativoBloqueio(em, cpf, idProcessoAdministrativo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void registrarExecucaoEGravarArquivoRelatorio(String urlPathParaRelatorio, String integracao, Collection lProcessoAdministrativoTotalPorBloco) throws AppException {
        new ProcessoAdministrativoBCABO().registrarExecucaoEGravarArquivoRelatorio(em, urlPathParaRelatorio, integracao, lProcessoAdministrativoTotalPorBloco);
    }

    @Override
//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processarInclusaoBloqueio() throws AppException {
        ProcessoBloqueioPessoaBO bo = new ProcessoBloqueioPessoaBO();

        bo.iniciaExecucao(em);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processarBloqueioPessoa(String cpf, List processos){
         ProcessoBloqueioPessoaBO bo = new ProcessoBloqueioPessoaBO();
         bo.processarBloqueioPessoa(em, cpf, processos);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void iniciaExecucao(ProcessoAdministrativo processoAdministrativo) {
        try {
            Usuario usuarioDetran = (Usuario) getAcessoService().getUsuarioPorLogin("DETRAN");
            new ExecucaoAndamentoManager().iniciaExecucao(
                    new ExecucaoAndamentoEspecificoWrapper(
                            processoAdministrativo,
                            usuarioDetran.getId(),
                            null, null
                    )
            );
        } catch (AppException e) {
            LOG.debug(e);
        }
    }

    @Override
    public List<ConsultaProcessoAdministrativoHistorico> getProcessoAdministrativoHistorico(Long id, Integer from, Integer to) throws AppException{
        return new ProcessoAdministrativoHistoricoRepositorio().getProcessoAdministrativoHistorico(em, id, from, to);
    }
    
    @Override
    public Long getCountProcessoAdministrativoHistorico(Long id) throws AppException{
        return new ProcessoAdministrativoHistoricoRepositorio().contarProcessoAdministrativoHistorico(em, id);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public NotificacaoProcessoAdministrativo getNotificacaoPorNumeroProcessoETipo(String numeroProcesso, TipoFasePaEnum tipoNotificacao) throws AppException {
        
        return 
            new NotificacaoProcessoAdministrativoRepositorio().getNotificacaoPorNumeroProcessoETipo(
                    em, 
                    numeroProcesso, 
                    tipoNotificacao
                );
    }
    
    @Override
    public List<PAFluxoProcesso> getListaPAFluxoProcessoPorProcesso(String numeroProcesso) throws DatabaseException{
        return new PAFluxoProcessoRepositorio().getListaPAFluxoProcessoPorProcesso(em, numeroProcesso);
    }
    
    @Override
    public Date buscarDataFimUltimaPenalidadePorCPF(String cpf) throws AppException {
        ProcessoBloqueioPessoa bloqueio = new ProcessoBloqueioPessoaRepositorio().getProcessoBloqueioPessoaPorCPFEDataEmAndamento(em, cpf, new Date());
        if(bloqueio != null){
            ProcessoRascunhoBloqueio ultimoRascunhoBlqueado = new ProcessoRascunhoBloqueioBO().getUltimoRascunhoBloqueadoPorBloqueioPessoa(em, bloqueio.getId());
            if(Objects.nonNull(ultimoRascunhoBlqueado)){
                return Utils.compareToDate(ultimoRascunhoBlqueado.getDataFim(), new Date()) >= 0 ? ultimoRascunhoBlqueado.getDataFim() : null;
            }
        }else {
            PAPenalidadeProcesso ultimaPenalidade = new PAPenalidadeProcessoRepositorio().getPAPenalidadeProcessoMaiorDataFimPorCpfCondutor(em, cpf);
            return Objects.nonNull(ultimaPenalidade) && Utils.compareToDate(ultimaPenalidade.getDataFimPenalidade(), new Date()) >= 0 ? ultimaPenalidade.getDataFimPenalidade(): null;
        }
        return null;
    }
    
    @Override
    public void enviarEmailPrazoNotificacoes() throws AppException {
        EnviarEmailPrazoNotificacoesBO bo = new EnviarEmailPrazoNotificacoesBO();
        bo.iniciaExecucao(em);
    }
    
    @Override
    public List<ProcessoAdministrativo> getListaProcessosFilho(Long idProcessoAdministrativo) throws AppException{
        return new ProcessoAdministrativoRepositorio().getListaProcessosFilho(em, idProcessoAdministrativo);
    }
}
