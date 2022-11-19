package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoNotificacaoEnum;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.ResultLong;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.bca.IBeanIntegracao;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.iface.servico.IDetranGenericService;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.DetranEmailWrapper;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.criteria.ProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.*;
import br.gov.ms.detran.processo.administrativo.enums.PAAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.*;
import br.gov.ms.detran.processo.administrativo.wrapper.pju.DadoProcessoJudicialWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.ConsultaPaWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.InformacaoProvaWrapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * @author desenvolvimento
 * @param <T>
 */
public interface IProcessoAdministrativoService<T extends IBaseEntity> extends IDetranGenericService<T>{

    /**
     * 
     * @param clazzEntidade
     * @param namedQuery
     * @param params
     * @return
     * @throws AppException 
     */
    public List pesquisar(Class clazzEntidade, String namedQuery, Map params) throws AppException;
    
    /**
     * 
     * @param codigoPAAndamentoProcesso
     * @return
     * @throws AppException 
     */
    public IBaseEntity getPAStatusAndamentoAtivoPorStatusEAndamento(Integer codigoPAAndamentoProcesso) throws AppException;
    
    /**
     * 
     * Recupera todos os PA's instaurados conforme critério de regras de status e andamento.
     * 
     * @return 
     */
    public List getInstauradosJson();
    
    /**
     * 
     * @param iCriteriaQueryBuilder
     * @return
     * @throws AppException 
     */
    public List buscarTodosCondutoresParaPA(ICriteriaQueryBuilder iCriteriaQueryBuilder) throws AppException;

    /**
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativo> getListaProcessosAdministrativosAtivos() throws AppException;

    /**
     * Buscar Processo Administrativo pelo numeroProcesso.
     * 
     * @param numeroProcesso
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public ProcessoAdministrativo getProcessoAdministrativoPorNumeroProcessoAtivo(String numeroProcesso) throws AppException;

    /**
     * 
     * @param processoAdministrativo
     * @param tipoNotificacao
     * @return
     * @throws AppException 
     */
    public NotificacaoProcessoAdministrativo notificar(ProcessoAdministrativo processoAdministrativo, TipoFasePaEnum tipoNotificacao) throws AppException;
    
    /**
     * 
     * @param urlReportBirt
     * @param notificacaoPa
     * @throws AppException 
     */
    public void gerarArquivoNotificacaoParaTipoArquivamentoIndevido(String urlReportBirt, NotificacaoProcessoAdministrativo notificacaoPa) throws AppException;
    
    /**
     * 
     * @return
     * @throws AppException 
     */
    public List<NotificacaoProcessoAdministrativo> getListNotificacoesParaTransferenciaSGI() throws AppException;
    
    /**
     * 
     * @param url
     * @throws AppException 
     */
    public void transferirNotificacoesParaSGI(String url)throws AppException;

    /**
     * 
     * @deprecated 
     * @throws AppException 
     */
    public void gerarArquivoCorreio() throws AppException;
    
    /**
     * @deprecated 
     * @throws AppException 
     */
    public void envioArquivoCorreio() throws AppException;

    /**
     * @return
     * @throws AppException 
     */
    public List getNotificados() throws AppException;

    public Integer getCodigoAndamentoPorTipoNotificacaoEOrigemPA(TipoFasePaEnum tipo, IBaseEntity origemApoio) throws AppException;

    /**
     * @param tipo
     * @param origemApoio
     * @return
     * @throws AppException 
     */
    public BaseEntityAtivo getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracao(Enum tipo, BaseEntityAtivo origemApoio) throws AppException;

    /**
     * @throws AppException 
     */
    public void lerDadosArquivoCorreiosAtualizarCorrespondenciaEAndamento() throws AppException;

    /**
     * 
     * @return
     * @throws AppException 
     */
    public List getRetornoAR() throws AppException;

    /**
     * 
     * @param detalheWrapper
     * @param dataRecebimento
     * 
     * @throws AppException 
     */
    public void atualizarCorrespondencia(RetornoARDetalheWrapper detalheWrapper, Date dataRecebimento) throws AppException;
    
    /**
     * 
     * @param wrapper
     * @param urlBaseBirt
     * @return
     * @throws AppException 
     */
    public IBaseEntity gravarArquivoProtocoloControleCnh(MovimentoCnhWrapper wrapper, String urlBaseBirt) throws AppException;

    /**
     * @return
     * @throws AppException 
     */
    public List getInformacoesEntregaCnh() throws AppException;
    
    /**
     * 
     * @param usuarioLogado
     * @param entity
     * @param urlBaseBirt
     * @param listaProcessos
     * @return 
     * 
     * @throws AppException 
     */
    public void gravarControleCnhPA(DetranUserDetailsWrapper usuarioLogado, ControleCnhPAWrapper entity, String urlBaseBirt, List<PAControleCnhWrapper> listaProcessos) throws AppException;

    /**
     * 
     * @param idPA
     * @param acao
     * @return
     * @throws AppException 
     */
    public MovimentoCnh getMovimentoProtocoloControleCnh(Long idPA, AcaoEntregaCnhEnum acao)throws AppException;

    /**
     * 
     * @param usuarioLogado
     * @param wrapper
     * @param urlBaseBirt
     * @param situacao
     * @param movimentos
     * @throws AppException 
     */
    public void desbloquearControleCnhPA(DetranUserDetailsWrapper usuarioLogado, 
                                         ControleCnhPAWrapper wrapper, 
                                         String urlBaseBirt,
                                         CnhSituacaoEntrega situacao,
                                         List<MovimentoCnh> movimentos) throws AppException;

    /**
     * @param criteria
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public List<RecursoWrapper> getRecursoPorFiltros(ICriteriaQueryBuilder criteria) throws AppException;

    /**
     * @param id
     * @return
     * @throws AppException 
     */
    public IBaseEntity getMovimentoRecurso(Long id) throws AppException;

    /**
     * @param numeroDocumento
     * @return
     * @throws AppException 
     */
    public List getProcessosAdministrativosPorCpf(String numeroDocumento) throws AppException;

    /**
     * @param entidade
     * @param usuarioLogado
     * @throws AppException 
     */
    public void gravarRecurso(IBaseEntity entidade, Object usuarioLogado) throws AppException;

    /**
     * @param recursoId
     * @return
     * @throws AppException 
     */
    public BooleanEnum getIndiceForaPrazo(Long recursoId) throws AppException;
    
    /**
     * 
     * @return
     * @throws AppException 
     */
    public List getRecursos() throws AppException;

    /**
     * @return
     * @throws AppException 
     */
    public List getListaMotivoAlegacao() throws AppException;

    /**
     * 
     * @param wrapper
     * @throws AppException 
     */
    public void incluiArquivoProtocoloRecurso(RecursoWrapper wrapper) throws AppException;
    
    /**
     * @param wrapper
     * @return
     * @throws AppException 
     */
    public IBaseEntity getProtocoloRecurso(RecursoWrapper wrapper) throws AppException;

    /**
     * @param entidade
     * @return
     * @throws AppException 
     */
    public IBaseEntity getRecursoMovimentoPorRecurso(IBaseEntity entidade) throws AppException;

    /**
     * @param criteria
     * @return
     * @throws AppException 
     */
    public ResultLong getCountRecursoPorFiltros(ICriteriaQueryBuilder criteria) throws AppException;

    /**
     * 
     * @param qtdeAProcessar 
     */
    void gerarPA(Integer qtdeAProcessar);
    
    /**
     * @param usuarioLogado
     * @param wrapper
     * @return
     * @throws AppException 
     */
    public BaseEntityAtivo cancelarRecurso(DetranUserDetailsWrapper usuarioLogado, RecursoWrapper wrapper) throws AppException;

    /**
     * 
     * @param entity
     * @throws AppException 
     */
    public void validarEnderecoAlternativo(PAEnderecoAlternativoWrapper entity) throws AppException;
    
    /**
     * 
     * @param processo
     * @param codigoAndamentoDestino
     * @throws AppException 
     */
    public void alterarParaAndamentoEspecifico(ProcessoAdministrativo processo, Integer codigoAndamentoDestino) throws AppException;
    
    /**
     * 
     * @param processoAdministrativo
     * @param codigoFluxoProcessoDestino
     * @param codigoAndamentoDestino
     * @throws AppException 
     */
    public void alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(
            ProcessoAdministrativo processoAdministrativo, Integer codigoFluxoProcessoDestino, Integer codigoAndamentoDestino) throws AppException;
    
    /**
     * 
     * @param processoAdministrativo
     * @param codigoFluxoProcessoDestino
     * @param codigoAndamentoDestino
     * @param usuarioAlteracao
     * @throws AppException 
     */
    public void alterarParaAndamentoEspecificoEFluxoProcessoEspecifico(
            ProcessoAdministrativo processoAdministrativo, Integer codigoFluxoProcessoDestino, Integer codigoAndamentoDestino,String usuarioAlteracao) throws AppException;

    /**
     * 
     * @param processo
     * @throws AppException 
     */
    public void proximoAndamento(ProcessoAdministrativo processo) throws AppException;
    
    /**
     * 
     * @param idProcessoAdministrativo
     * @return
     * @throws AppException 
     */
    public IBaseEntity getPAOcorrenciaStatusAtiva(Long idProcessoAdministrativo) throws AppException;
    
    /**
     * @return
     * @throws AppException 
     */
    public List getProcessoAdministrativoBloqueioBCA() throws AppException;

    public IBaseEntity getComplementoDoPA(String numeroProcesso)throws AppException;

    /**
     * 
     * @return
     * @throws AppException 
     */
    public List getProcessoAdministrativoDesistentes() throws AppException;

    /**
     * 
     * @return
     * @throws AppException 
     */
    public List getRecursosCancelados() throws AppException;

    /**
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public List getMovimentaoPa() throws AppException;

    /**
     * 
     * @return
     * @throws AppException 
     */
    public List<InformacaoProvaWrapper> getInformacoesProva() throws AppException;

    /**
     * @param entidade
     * @param usuarioLogado
     * @throws AppException 
     */
    public void gravarMovimentacao(MovimentacaoWrapper entidade, DetranUserDetailsWrapper usuarioLogado) throws AppException;
    
    /**
     * @param paId
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativoInfracao> getProcessoAdministrativoInfracaoPorProcessoId(Long paId) throws AppException;

    /**
     * @param paOcorrenciaStatusAtiva 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public void finalizarAndamentoAtual(IBaseEntity paOcorrenciaStatusAtiva) throws AppException;

    /**
     * @param paId
     * @return
     * @throws AppException 
     */
    public Movimentacao getMovimentacaoSuspensaoDesativaRecente(Long paId) throws AppException;

    /**
     * @param pa
     * @param fluxoFase 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public void alterarParaFluxoEAndamentoEspecificos(ProcessoAdministrativo pa, PAFluxoFase fluxoFase) throws AppException;

    /**
     * 
     * @param id
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativoAgravado getAgravadoPorPAOriginal(Long id) throws AppException;

    /**
     * 
     * @param id
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativoApensado getApensadoPorPAOriginal(Long id)throws AppException;
    
    /**
     * @param e
     * @param cpf
     * @param origemErro
     * @param email 
     */
    public void gravarFalhaEMandarEmail(Exception e, String cpf, String origemErro, DetranEmailWrapper email);

    /**
     * @param numeroProcesso
     * @return
     * @throws AppException 
     */
    public MovimentoCnh getMovimentoCnhParaDesentranhamento(String numeroProcesso) throws AppException;

    /**
     * 
     * @param processo
     * @param codigoAndamentoEspecifico
     * @param idUsuario
     * @return
     * @throws AppException 
     */
    public ParametrosIntegracaoBloqueioBCAWrapper gravarBloqueioBCAEAndamento(ProcessoAdministrativo processo, Integer codigoAndamentoEspecifico, Long idUsuario)throws AppException;

   /**
     * @param id
     * @return
     * @throws AppException 
     */
    public Movimentacao getMovimentacaoAtivaRecentePorProcessoAdministrativo(Long id) throws AppException;

    /**
     * @param pa
     * @param listaAutoInfracoes
     * @param codigosInfracao
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public  void executarIntegracaoAEMNPP12(ProcessoAdministrativo pa, List listaAutoInfracoes, List codigosInfracao) throws AppException;
    
    /**
     * 
     * @return
     * @throws AppException 
     */
    public List getAndamentos()throws AppException;

    /**
     * 
     * @param protocolo
     * @throws AppException 
     */
    public void ajustaProtocoloRecursoGeraCodigoBarra(Protocolo protocolo) throws AppException;
    
    /**
     * @param cpf
     * @return
     * @throws AppException 
     */
    public MovimentoCnh getMovimentoCnhParaDesentranhamentoPorCpfCondutor(String cpf) throws AppException;
    
    /**
     * @param cpf
     * @return
     * @throws AppException 
     */
    public Boolean existeMaisDeUmProcessoAdministrativoParaCondutor(String cpf) throws AppException;

    /**
     * @param correspondenciaId
     * @return
     * @throws AppException 
     */
    public BaseEntityAtivo getCorrespondenciaCorreioDevolucaoPorCorrespondencia(Long correspondenciaId) throws AppException;

    /**
     * @param notificacaoProcessoAdministrativoId
     * @return
     * @throws AppException 
     */
    public BaseEntityAtivo getNotificacaoComplementoPorNotificacao(Long notificacaoProcessoAdministrativoId) throws AppException;

    /**
     * @param notificacaoProcessoAdministrativoId
     * @return
     * @throws AppException 
     */
    public BaseEntityAtivo getEditalProcessoAdministrativoPorNotificacao(Long notificacaoProcessoAdministrativoId) throws AppException;

    /**
     * @param idRecurso
     * @return
     * @throws AppException 
     */
    public BaseEntityAtivo getResultadoRecursoAtivoPorRecurso(Long idRecurso) throws AppException;

    public IBaseEntity getCorrespondenciaCorreioPorCorrespondencia(Long id) throws AppException;

    /**
     * 
     * @param cpf
     * @param cnh
     * @return
     * @throws AppException 
     */
    public IBeanIntegracao validarEntregaCnhParaPA(String cpf, Long cnh) throws AppException;

    public IBaseEntity carregarDadosControleCnh(ProcessoAdministrativoCriteria criteria) throws AppException;

    public IBaseEntity getPenalidadePorPA(Long idPA)throws AppException;
    
    /**
     * @param detranUserDetailsWrapper
     * @param criteria
     * @return
     * @throws AppException 
     */
    public Object montarUsuarioAcessoWrapper(DetranUserDetailsWrapper detranUserDetailsWrapper, DetranAbstractCriteria criteria) throws AppException; 

    /**
     * @param processoAdministrativo
     * @param fluxo
     * @param andamento
     * @throws AppException 
     */
    public void executarMudancaFluxoEAndamento(ProcessoAdministrativo processoAdministrativo, Integer fluxo, Integer andamento) throws AppException;
    
    /**
     * 
     * @param recurso
     * @param tipo
     * @return
     * @throws AppException 
     */
    public IBaseEntity getRecursoMovimentoPorRecursoETipoProtocolo(Recurso recurso, TipoSituacaoProtocoloEnum tipo)throws AppException;

    /**
     * @param pa
     * @param fluxo
     * @throws AppException 
     */
    public void iniciarFluxo(ProcessoAdministrativo pa, Integer fluxo) throws AppException;

    /**
     * @param pa
     * @throws AppException 
     */
    public void proximoAndamentoComAtualFinalizado(ProcessoAdministrativo pa) throws AppException;

    /**
     * @param pa
     * @param mudancaFluxo
     * @param fluxo
     * @throws AppException 
     */
    public void mudancaDeFluxoComAndamentoAtualFinalizado(ProcessoAdministrativo pa, Boolean mudancaFluxo, Integer fluxo) throws AppException;

    /**
     * @param desbloquearControleCnhPA
     * @param acaoEntregaCnhEnum
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public List<MovimentoCnh> getMovimentoPorControleCnhEAcaoEntrega(Long desbloquearControleCnhPA, AcaoEntregaCnhEnum acaoEntregaCnhEnum) throws AppException;

    /**
     * 
     * @param numeroDetran
     * @throws AppException 
     */
    public void checaSeExisteAlgumProcessoAdministrativoQueInvalidaExecutarDesbloqueioCnh(Long numeroDetran) throws AppException;

    /**
     * 
     * @param numeroDetran
     * @throws AppException 
     */
    public void validaSeExisteProcessoAdministrativoValidoParaExecutarDesbloqueioCnh(Long numeroDetran) throws AppException;
    
    /**
     * @param desbloquearControleCnhPA
     * @param acaoEntregaCnhEnum
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public List<MovimentoCnh> getMovimentoPorControleCnhEAcaoEntregaParaDesbloqueio(
        Long desbloquearControleCnhPA, AcaoEntregaCnhEnum acaoEntregaCnhEnum) throws AppException;

    /**
     * @param fluxoProcesso
     * @return
     * @throws AppException 
     */
    public BaseEntityAtivo getPrioridadeFluxoAmparoAtivoPorFluxoProcesso(BaseEntityAtivo fluxoProcesso) throws AppException;

    /**
     * @param pa
     * @return
     * @throws AppException 
     */
    public BaseEntityAtivo getFluxoFaseDoProcessoAdministrativo(BaseEntityAtivo pa) throws AppException;

    /**
     * 
     * @param pa
     * @return
     * @throws AppException 
     */    
    public BaseEntityAtivo getFluxoFaseDoProcessoAdministrativoParaConsulta(BaseEntityAtivo pa) throws AppException;

    /**
     * @param pAFluxoFase
     * @param listaPaFluxoProcesso
     * @return
     * @throws AppException 
     */
    public BaseEntityAtivo getPaFluxoAndamentoPorPAFluxoFaseEPAFluxoProcesso(BaseEntityAtivo pAFluxoFase, List<Integer> listaPaFluxoProcesso) throws AppException;

    /**
     * 
     * @param id
     * @param tipo
     * @return
     * @throws AppException 
     */
    public IBaseEntity getCorrespondenciaCorreioPorProcessoETipo(Long id, TipoFasePaEnum tipo)throws AppException;

    /**
     * 
     * @param numeroDocumento
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativoRecursoWrapper> 
        getListProcessoAdministrativoPermitidosAbrirRecursoPorCPFCondutor(String numeroDocumento) throws AppException;
    
    /**
     * 
     * @param idUsuario
     * @param urlMenuAplicacao
     * @return
     * @throws AppException 
     */
    public Boolean validaSePermiteUsuarioCadastrarDataRecursoRetroativa(Long idUsuario, String urlMenuAplicacao) throws AppException;

    /**
     * @param wrapper
     * @return
     * @throws AppException 
     */
    public Object montarProcessoAdministrativoXml(Object wrapper) throws AppException;

    /**
     * @param numeroProcesso
     * @param usuarioLogado
     * @throws AppException 
     */
    public void obterInformacoesProvaInfrator(String numeroProcesso, Object usuarioLogado) throws AppException;

    /**
     * @param numeroProcesso
     * @return
     * @throws AppException 
     */
    public ConsultaPaWSWrapper montarConsultaPA(String numeroProcesso)throws AppException;

    /**
     * @param pa
     * @return
     * @throws AppException 
     */
    public Object getInformacoesProva(BaseEntityAtivo pa) throws AppException;

    /**
     * @param andamentoProcesso
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public BaseEntityAtivo getPAStatusAndamentoPorAndamentoProcesso(BaseEntityAtivo andamentoProcesso) throws AppException;

    /**
     * 
     * @param urlPathParaRelatorio
     * @params urlPathParaRelatorio
     * @throws AppException 
     */
    public void checaBCAParaProcessoAdministrativo(String urlPathParaRelatorio) throws AppException;

    /**
     * @param object
     * @return
     * @throws AppException 
     */
    public Object montarMovimentacaoPaXml(Object object) throws AppException;

    /**
     * 
     * @param numeroProcesso
     * @param servico
     * @throws AppException 
     */
    public void validarPAParaExecucaoServico(String numeroProcesso, String servico)throws AppException;

    /**
     * 
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    public ParametrosIntegracaoBloqueioBCAWrapper montarWrapperIntegracaoAEMNPP13(ProcessoAdministrativo processoAdministrativo)throws AppException;

    /**
     * 
     * @param usuario
     * @return
     * @throws AppException 
     */
    public Long buscarIdUsuario(String usuario)throws AppException;

    /**
     * 
     * @param processo
     * @param idUsuario
     * @return
     * @throws AppException 
     */
    public ParametrosIntegracaoBloqueioBCAWrapper gravarBloqueioBCA(ProcessoAdministrativo processo, Long idUsuario)throws AppException;

    /**
     * 
     * @param codigos
     * @return
     * @throws AppException 
     */
    public List<PAAndamentoProcesso> getListaPAAndamentoProcessoPorListaCodigo(List<Integer> codigos) throws AppException;

    /**
     * 
     * @param cpf
     * @param codigoFluxo
     * @return
     * @throws AppException 
     */
    public List<PAOcorrenciaStatus> getOcorrenciaPorCpfEFluxo(String cpf, Integer codigoFluxo)throws AppException;

    /**
     * 
     * @param idPA
     * @return
     * @throws AppException 
     */
    public BooleanEnum existeRecursoEmAnalisePorPA(Long idPA)throws AppException;

    /**
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    public Object montarParametrosIntegracaoBloqueioBCAWrapper(BaseEntityAtivo processoAdministrativo) throws AppException;

    /**
     * 
     * @param id
     * @param situacaoRecursoEnum
     * @return
     * @throws AppException 
     */
    public Recurso getRecursoPorPAESituacao(Long id, SituacaoRecursoEnum situacaoRecursoEnum)throws AppException;
    
    /**
     * 
     * @return
     * @throws AppException 
     */
    public List getProcessoAdministrativoDesistentesRecursoInstauracaoPenalizacao() throws AppException;

    /**
     * 
     * @param entidade
     * @throws AppException 
     */
    public void validarServicoExternoMesmoNomeAtivo(PAServicoExterno entidade)throws AppException;
    
    /**
     * @return
     * @throws AppException 
     */
    public List getListOrigemInstauracao() throws AppException;

    public List<PAOcorrenciaStatus> getOcorrenciasDesistentesInstPenalizacao(String cpf)throws AppException;

    public Protocolo getProtocoloPorIdPAETipoNotificacao(Long idPA, TipoNotificacaoEnum tipoNotificacaoEnum)throws AppException;
    
    public IBaseEntity gravarArquivoProtocoloDesistenciaInstPen(Protocolo protocolo, String urlBaseBirt)throws AppException;

    public PAParametroEnum getInformacaoDesistente(Long id)throws AppException;

    /**
     * Carregar lista com processos administrativos para ControleCnh.
     * 
     * @param wrapper
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public List<PAControleCnhWrapper> carregarListaProcessosParaControleCnh(ControleCnhPAWrapper wrapper) throws AppException;

    /**
     * Gravar protocolo.
     * 
     * @param usuarioLogado
     * @param listaMovimentos
     * @param urlBaseBirt
     * @return 
     * @throws AppException 
     */
    public List<ArquivoPA> gravarProtocolo(DetranUserDetailsWrapper usuarioLogado, List<MovimentoCnh> listaMovimentos, String urlBaseBirt) throws AppException;

    /**
     * Imprimir os protocolos do controle cnh direto na impressora, duas vias.
     * 
     * @param usuarioLogado
     * @param listaArquivos
     * @throws AppException 
     */
    public void imprimirProtocoloControleCnh(DetranUserDetailsWrapper usuarioLogado, List<ArquivoPA> listaArquivos) throws AppException;

    /**
     * @param numeroProcesso
     * @return
     * @throws AppException 
     */
    public List geraRegistroInfracoesDespacho(String numeroProcesso) throws AppException;
    
    /**
     * 
     * @param idProcessoAdministrativo
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativo getProcessoAdministrativo(Long idProcessoAdministrativo) throws AppException;
    
    /**
     * 
     * @param numeroProcesso
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativo getProcessoAdministrativo(String numeroProcesso) throws AppException;
    
    /**
     * 
     * @param andamento
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativo getProcessoAdministrativoAtivoPorPAAndamentoCodigo(PAAndamentoEnum andamento) throws AppException;

    /**
     * @param pa
     * @throws AppException 
     */
    public void executarAndamento48paraEntregaCnh(BaseEntityAtivo pa) throws AppException;

    /**
     * @return
     * @throws AppException 
     */
    public List buscarProcessosAdministrativosNoAndamento48ComCnhEntregue() throws AppException;
    
    /**
     * 
     * @return
     * @throws AppException 
     */
    public List buscarProcessosAdministrativosNoAndamento48ComPenaCumprida() throws AppException;

    /**
     * @param cpf
     * @return
     * @throws AppException 
     */
    public List getProcessosAdministrativosParaProcessoJuridico(String cpf) throws AppException;

    /**
     * @param wrapper
     * @param usuarioLogado
     * @param urlBaseBirt
     * @return 
     * @throws AppException 
     */
    public BaseEntityAtivo gravarDadoProcessoJudicial(DadoProcessoJudicialWrapper wrapper, Object usuarioLogado, String urlBaseBirt) throws AppException;

    /**
     * @param tipoLogradouroEnum
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public Object getObjetoCorrespondenciaCorreio(Enum tipoLogradouroEnum) throws AppException;

    /**
     * @param processoAdministrativo
     * @param wrapperCorreio
     * @param tipoNotificacaoProcesso
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public BaseEntityAtivo gravarCorrespondencia(BaseEntityAtivo processoAdministrativo, Object wrapperCorreio, Enum tipoNotificacaoProcesso) throws AppException;

    /**
     * 
     * @param listaNumeroPA
     * @throws AppException 
     */
    public void atualizaProcessosAguardandoRetornoAR(List listaNumeroPA)throws AppException;

    public void executaDesistenciaRecInstPen(ProcessoAdministrativo pa,
                                             DetranUserDetailsWrapper usuarioLogado,
                                             RecursoOnlineCanceladoWrapper recursoOnlineCanceladoWrapper) throws AppException;

    public void arquivarProcessosSuspensaoPorCassacao()throws AppException;

    public void executaArquivamentoPASuspensaoPorProcessoCassacao(ProcessoAdministrativo processo, Usuario usuarioDetran)throws AppException;

    public void arquivarProcessoPorCassacao(EntityManager em, ProcessoAdministrativo processoSuspensao, ProcessoAdministrativo processoCassacao, Usuario usuarioDetran)throws AppException;

    /**
     * @param cpf
     * @param acaoEntregaCnhEnum
     * @return 
     */
    public BaseEntityAtivo getMovimentoEntregaPorCpf(String cpf, Enum acaoEntregaCnhEnum) throws AppException;

    public DadoProcessoJudicial getDadoProcessoJudicialPorPA(ProcessoAdministrativo pa)throws AppException;

    public List<ProcessoAdministrativo> getProcessosJuridicosParaEntregaCartorioPorCPF(String cpf)throws AppException;

    public void gravarCnhEntregueCartorio(DadoProcessoJudicialWrapper wrapper)throws AppException;

    public List<ProcessoAdministrativo> getListProcessosAdministrativosPorAndamento(List<Integer> montaLista)throws AppException;

    public List<ProcessoAdministrativo> getListaProcessoJuridicoAndamentoERecolhimentoCnh(List<Integer> montaLista)throws AppException;

    public void obterInformacoesProvaParaPJUCartorio(ProcessoAdministrativo processo, Long idUsuario)throws AppException;

    public Object getSomaPontuacaoDeInfracoesPA(Long id) throws AppException;

    public PAInicioFluxo getInicioFluxoAtivoPA(Long idPA) throws AppException;

    public Long getCondutorViaAEMNPP93(String cpf)throws  AppException;

    public List<PAAndamentoProcessoEspecifico> buscarProcessosEspecificos()throws AppException;

    public void atualizarProcessoEspecifico(PAAndamentoProcessoEspecifico processoEspecifico)throws AppException;

    public void registraGravacaoProcessamento(ProcessoAdministrativoBCARelatorioTotalWrapper wrapper, String integracao, byte[] pdf) throws AppException;
    
    public List getAndamentosAutomaticos()throws AppException;

    public List getListProcessosAdministrativosPorAndamentoEmLote(List<Integer> andamentos, Integer qtdPA) throws AppException;
    
    public List getListaProcessoAdministrativoPorAndamentoETipoProcessoEmLote(List andamentos, Integer quantidadeProcessoAdministrativo, TipoProcessoEnum tipoProcesso) throws AppException;

    public NotificacaoProcessoAdministrativoWrapper buscarDadosRetornoAR(String objetoCorreios)throws AppException;
    
    public List buscarCnhControleSemValidade() throws AppException;
    
    public List getMovimentoCnhComCnhControleValido(ICriteriaQueryBuilder criteria) throws AppException;
    
    public List getMovimentoCnhComCnhControleVencido(ICriteriaQueryBuilder criteria) throws AppException;
    
    public Object getCountMovimentoCnhComCnhControleValido(ICriteriaQueryBuilder criteria) throws AppException;
    
    public Object getCountMovimentoCnhComCnhControleVencido(ICriteriaQueryBuilder criteria) throws AppException;

    public void preArquivarProcessosPrescritos() throws AppException;

    public LoteNotificacaoPA gerarLoteNotificacao(String nomeArquivoConferencia, TipoFasePaEnum tipoNotificacao, List<NotificacaoProcessoAdministrativo> lNotificacoes) throws AppException;

    public void gerarArquivoConferenciaLote(LoteNotificacaoPA lote, String url) throws AppException, FileNotFoundException, IOException;

    public void enviarEmail(String nomeArquivo, TipoFasePaEnum tipoNotificacao) throws AppException;

    public List getPAComPrazoNotificacaoTerminado() throws AppException;

    public List<ProcessoAdministrativo> getListProcessosComPrazoNotificacaoExpirado() throws AppException;

    Object registraPassoRecursoOnline(Object wrapper) throws AppException;

    void registraFalhaRecursoOnline(RecursoPAOnlineFalha falha);

    RecursoOnlinePaDocumentoWrapper geraDocumentoFormularioRecursoOnline(String token, String protocolo, String reportsBaseBirtUrl) throws AppException;

    RecursoPAOnline getRecursoOnlineMaisRecenteTokenOuProtocolo(String token, String protocolo) throws DatabaseException;

    RecursoPAOnlineArquivo getFormularioAssinadoDoRecursoOnline(RecursoPAOnline recursoOnline) throws AppException;

    List<RecursoPAOnlineArquivo> buscarDocumentosDoRecursoOnline(Long id) throws AppException;

    Date getDataNotificacaoDoRecursoOnline(RecursoPAOnline entidade) throws AppException;

    void recusarRecurso(BackOfficePaWrapper wrapper) throws AppException;

    RecursoWrapper gravarRecursoOnline(BackOfficePaWrapper wrapper, DetranUserDetailsWrapper usuarioLogado) throws AppException;

    void validarRecursoOnlineEmBackOffice(ProcessoAdministrativo processoAdministrativo) throws AppException;

    void enviarArquivosRecursoOnlineParaFTP(RecursoPAOnline recursoEfetivado, String urlReportBirt) throws AppException;

    String getIndicativoReativacaoInfracao(Long movimentacaoId, Long paId) throws DatabaseException;

    public void checaQuantidadeDiariaLimiteEmissaoNotificacao() throws AppException;
    
    Date getDataNotificacaoDoRecursoOnline2(RecursoPAOnline entidade) throws AppException;
    
    Date getDataPrazoLimiteNotificacaoDoRecursoOnline(RecursoPAOnline entidade) throws AppException;
    
    /**
     * @param criteria
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public List<RecursoWrapper> getRecursoPorFiltros2(ICriteriaQueryBuilder criteria) throws AppException;
    
    /**
     * @param criteria
     * @return
     * @throws AppException 
     */
    public ResultLong getCountRecursoPorFiltros2(ICriteriaQueryBuilder criteria) throws AppException;

    /**
     * 
     * @return
     * @throws AppException 
     */
    public List<PAAndamentoProcesso> getFluxosProcessosAtivos() throws AppException;

    /**
     * 
     * @return
     * @throws AppException 
     */
    public List<PAAndamentoProcesso> getAndamentosAtivos() throws AppException;
   
    public List<PAAndamentoProcesso> getAndamentosAtivosPorFluxo(Integer codigo) throws AppException;
    
    /**
     * 
     * @param fluxoProcesso
     * @param andamento
     * @return
     * @throws AppException 
     */
    public PAFluxoFase getPAFluxoFasePorFluxoProcessoEAndamento(PAFluxoProcesso fluxoProcesso, PAAndamentoProcesso andamento) throws AppException;

    /**
     * 
     * @param cpf
     * @param idProcessoAdministrativo
     * @return
     * @throws AppException 
     */
    public List<ProcedureProcessoAdministrativoBloqueio> executaProcedureProcessoAdministrativoBloqueio(String cpf, Long idProcessoAdministrativo) throws AppException;

    /**
     * 
     * @param urlPathParaRelatorio
     * @param integracao
     * @param lProcessoAdministrativoTotalPorBloco
     * @throws AppException 
     */
    public void registrarExecucaoEGravarArquivoRelatorio(String urlPathParaRelatorio, String integracao, Collection lProcessoAdministrativoTotalPorBloco) throws AppException;
    
    public List<ConsultaProcessoAdministrativoHistorico> getProcessoAdministrativoHistorico(Long id, Integer from, Integer to) throws AppException;
    public Long getCountProcessoAdministrativoHistorico(Long id) throws AppException;
    public void gravarArquivoProtocoloRecursoOnline(RecursoWrapper wrapper) throws AppException;

    public void processarInclusaoBloqueio() throws AppException;

    public void processarBloqueioPessoa(String cpf, List<ProcessoAdministrativo> processos);

    public void iniciaExecucao(ProcessoAdministrativo processoAdministrativo);
    
    /**
     * 
     * @param numeroProcesso
     * @param tipoNotificacao
     * @return
     * @throws AppException 
     */
    public NotificacaoProcessoAdministrativo getNotificacaoPorNumeroProcessoETipo(String numeroProcesso, TipoFasePaEnum tipoNotificacao) throws AppException;
    
    public List<PAFluxoProcesso> getListaPAFluxoProcessoPorProcesso(String numeroProcesso) throws DatabaseException;

    public Date buscarDataFimUltimaPenalidadePorCPF(String cpf) throws AppException;

    public void enviarEmailPrazoNotificacoes() throws AppException;

    
    public List<ProcessoAdministrativo> getListaProcessosFilho(Long idProcessoAdministrativo) throws AppException;
}
