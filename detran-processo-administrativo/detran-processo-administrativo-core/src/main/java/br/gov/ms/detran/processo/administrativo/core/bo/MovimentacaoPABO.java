package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.recursoonline.RecursoOnlineBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.*;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.*;
import br.gov.ms.detran.processo.administrativo.enums.*;
import br.gov.ms.detran.processo.administrativo.wrapper.MovimentacaoPAXmlWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.MovimentacaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoInfracaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoOnlineCanceladoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.MovimentacaoPaWrapper;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovimentacaoPABO {

    private IApoioService apoioService;
    
    private IProcessoAdministrativoService service;

    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }
    
    public IProcessoAdministrativoService getService() {

        if (service == null) {
            service = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }

        return service;
    }

    /**
     * @param em
     * @return
     * @throws AppException
     */
    public List getMovimentaoPa(EntityManager em) throws AppException {

        List<MovimentacaoPaWrapper> listaJson = new ArrayList();

        listaJson.addAll(getMovimentacoesManuaisDoSistema(em));

        listaJson.addAll(getApensadoMovimentacoesAutomaticasDoSistema(em));
        
        listaJson.addAll(getAgravadoMovimentacoesAutomaticasDoSistema(em));

        return listaJson;
    }

    /**
     * @param em
     * @param wrapper
     * @param usuario
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    public void gravarMovimentacao(EntityManager em, MovimentacaoWrapper wrapper, Usuario usuario) throws AppException {

        MovimentacaoPARepositorio repositorio = new MovimentacaoPARepositorio();

        ProcessoAdministrativo pa = 
                new ProcessoAdministrativoRepositorio()
                        .getProcessoAdministrativoPorNumeroProcessoAtivo(em, wrapper.getProcessoAdministrativo().getNumeroProcesso());

        Movimentacao movimentacaoAntiga = repositorio.getMovimentacaoPorProcessoAdministrativoAtivo(em, pa.getId());
        
        PAOcorrenciaStatus ocorrenciaAtual = new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, pa.getId());
        
        Movimentacao entidade = wrapper.getEntidade();

        validar(em, entidade, movimentacaoAntiga, pa, ocorrenciaAtual);

        if (movimentacaoAntiga != null) {
            movimentacaoAntiga.setAtivo(AtivoEnum.DESATIVADO);
            repositorio.update(em, movimentacaoAntiga);
        }

        entidade.setProcessoAdministrativo(pa);
        entidade.setFluxoFase(new PAFluxoFaseRepositorio().getFluxoFaseDoProcessoAdministrativo(em, pa));
        entidade.setUsuario(usuario.getId());
        entidade.setDataInicio(new Date());
        entidade.setDataCadastro(new Date());
        entidade.setAtivo(AtivoEnum.ATIVO);

        repositorio.insert(em, wrapper.getEntidade());

        gravarMovimentacaoInfracaoExecutarAEMNPP12(em, wrapper);

        penalidadeOuBloqueio(em, entidade, pa, usuario.getId(), null);

        tratarRecursoOnlineEmBackOffice(em, pa, wrapper.getRecursoCanceladoWrapper(), entidade.getMovimentacaoAcao());

        alterarAndamentoPA(em, entidade, ocorrenciaAtual);
    }

    private void tratarRecursoOnlineEmBackOffice(EntityManager em,
                                                 ProcessoAdministrativo pa,
                                                 RecursoOnlineCanceladoWrapper recursoCanceladoWrapper,
                                                 MovimentacaoAcaoEnum acao) throws AppException {

        switch (acao) {
            case SUSPENDER:
                new RecursoOnlineBO().suspenderRecursoOnlineEmBackOffice(em, pa, recursoCanceladoWrapper);

                break;
            case CANCELAR:
                recursoCanceladoWrapper.setFuncionalidade("Cancelamento do Processo.");
                new RecursoOnlineBO().cancelarRecursoOnlineEmBackOffice(em, pa, recursoCanceladoWrapper);
            case ARQUIVAR:
                recursoCanceladoWrapper.setFuncionalidade("Arquivamento do Processo.");
                new RecursoOnlineBO().cancelarRecursoOnlineEmBackOffice(em, pa, recursoCanceladoWrapper);

                break;
            case RETIRAR_SUSPENSAO:
                new RecursoOnlineBO().retirarSuspensaoRecursoOnlineEmBackOffice(em, pa, recursoCanceladoWrapper);

                break;
        }

    }

    /**
     * @param em
     */
    private void validar(EntityManager em,
                         Movimentacao movimentacao,
                         Movimentacao movimentacaoAntiga,
                         ProcessoAdministrativo pa,
                         PAOcorrenciaStatus ocorrenciaAtual) throws AppException {
        
        validarSuspender(movimentacao, movimentacaoAntiga);
        
        validarRetirarSuspensao(movimentacao, movimentacaoAntiga);
        
        validarMovimentacaoConfirmada(em, movimentacao, pa, ocorrenciaAtual);
    }

    /**
     * Validar se a movimentação anterior já foi confirmada pelo webservice.
     *
     * @param em
     * @param pa
     */
    private void validarMovimentacaoConfirmada(EntityManager em, 
                                               Movimentacao movimentacao, 
                                               ProcessoAdministrativo pa,
                                               PAOcorrenciaStatus ocorrenciaAtual) throws AppException {
        
        
        if (null != movimentacao.getMovimentacaoAcao()) switch (movimentacao.getMovimentacaoAcao()) {
            case ARQUIVAR:
                
                if (PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_SUSPENSAO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.andamentoinvalido.arquivaroucancelar");
                    
                }
                else if (PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_RETIRADA_DA_SUSPENSAO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.andamentoinvalido.arquivaroucancelar2");
                }
                else if (PAAndamentoProcessoConstante.CANCELAMENTO.CONFIRMAR_CANCELAMENTO_PROCESSO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.andamentoinvalido.arquivaroucancelar3");
                }   break;
            case RETIRAR_SUSPENSAO:
                
                if (!PAAndamentoProcessoConstante.SUSPENSO.PROCESSO_SUSPENSO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.andamentoinvalido.retirarsuspensao");
                }   break;
            case SUSPENDER:
                
                if (PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_RETIRADA_DA_SUSPENSAO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.andamentoinvalido.arquivaroucancelar2");
                }
                else if (PAAndamentoProcessoConstante.CANCELAMENTO.CONFIRMAR_CANCELAMENTO_PROCESSO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.andamentoinvalido.arquivaroucancelar3");
                }
                else if (PAAndamentoProcessoConstante.ARQUIVAMENTO.CONFIRMAR_ARQUIVAMENTO_PROCESSO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.andamentoinvalido.arquivaroucancelar3");
                }   break;
            case CANCELAR:
                
                if (PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_SUSPENSAO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.andamentoinvalido.arquivaroucancelar2");
                }
                else if (PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_RETIRADA_DA_SUSPENSAO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.andamentoinvalido.arquivaroucancelar3");
                }
                else if (PAAndamentoProcessoConstante.ARQUIVAMENTO.CONFIRMAR_ARQUIVAMENTO_PROCESSO.equals(ocorrenciaAtual.getStatusAndamento().getAndamentoProcesso().getCodigo())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.andamentoinvalido.arquivaroucancelar3");
                }   break;
            default:
                break;
        }
    }

    /**
     * Validações para a movimentação de retirar suspensão:
     * 
     * - Movimentação anterior deve ser de SUSPENSAO.
     * 
     * @param movimentacaoAntiga
     * @throws AppException 
     */
    private void validarRetirarSuspensao(Movimentacao movimentacao, Movimentacao movimentacaoAntiga) throws AppException {
        
        if (MovimentacaoAcaoEnum.RETIRAR_SUSPENSAO.equals(movimentacao.getMovimentacaoAcao())) {
            
            if (null == movimentacaoAntiga) {
                DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.retirarsuspensao.suspensaonaoexiste");
            }

            if (null != movimentacaoAntiga && !MovimentacaoAcaoEnum.SUSPENDER.equals(movimentacaoAntiga.getMovimentacaoAcao())) {
                DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.retirarsuspensao.suspensaonaoexiste");
            }
        }
    }

    /**
     * Validações para movimentação SUSPENDER:
     * 
     * - Não pode existir outra movimentação de SUSPENDER.
     * 
     * @param movimentacaoAntiga
     * @throws AppException 
     */
    private void validarSuspender(Movimentacao movimentacao, Movimentacao movimentacaoAntiga) throws AppException {
        
        if (MovimentacaoAcaoEnum.SUSPENDER.equals(movimentacao.getMovimentacaoAcao())) {
            
            if (null != movimentacaoAntiga) {
                
                if (MovimentacaoAcaoEnum.SUSPENDER.equals(movimentacaoAntiga.getMovimentacaoAcao())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.suspender.duplicado");
                }
                
                if (MovimentacaoAcaoEnum.ARQUIVAR.equals(movimentacaoAntiga.getMovimentacaoAcao())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.suspender.arquivar");
                }
                
                if (MovimentacaoAcaoEnum.CANCELAR.equals(movimentacaoAntiga.getMovimentacaoAcao())) {
                    DetranWebUtils.applicationMessageException("severity.error.movimentacaopa.suspender.cancelar");
                }
            }
        }
    }

    /**
     * Executa a gravação da TB_TEC_PAD_MOVIMENTACAO_INFRACAO_PA e a transação
     * AEMNPP12.
     *
     * @param em
     * @param wrapper
     * @throws AppException
     */
    private void gravarMovimentacaoInfracaoExecutarAEMNPP12(EntityManager em, MovimentacaoWrapper wrapper) throws AppException {

        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getListaInfracoes())) {

            for (ProcessoAdministrativoInfracaoWrapper infracaoWrapper : wrapper.getListaInfracoes()) {

                MovimentacaoInfracaoPA movimentacaoInfracao = new MovimentacaoInfracaoPA();

                movimentacaoInfracao.setMovimentacao(wrapper.getEntidade());
                movimentacaoInfracao.setProcessoAdministrativoInfracao(infracaoWrapper.getEntidade());

                if (MovimentacaoAcaoEnum.ARQUIVAR.equals(wrapper.getEntidade().getMovimentacaoAcao())
                    && MovimentacaoMotivoEnum.PENA_CUMPRIDA.equals(wrapper.getEntidade().getMotivo())) {

                    movimentacaoInfracao.setIndicativoReativarPontuacao(BooleanEnum.NAO);

                } else {

                    movimentacaoInfracao
                            .setIndicativoReativarPontuacao(
                                    infracaoWrapper.getReativarPontuacao() ? BooleanEnum.SIM : BooleanEnum.NAO
                            );
                }

                movimentacaoInfracao.setAtivo(AtivoEnum.ATIVO);

                new MovimentacaoInfracaoPARepositorio().insert(em, movimentacaoInfracao);

                if (movimentacaoInfracao.getIndicativoReativarPontuacao().equals(BooleanEnum.SIM)) {
                    desativarProcessoAdministrativoInfracao(em, wrapper.getEntidade().getMovimentacaoAcao(), infracaoWrapper.getEntidade());
                }
            }
        }
    }

    /**
     * @param em
     */
    private void alterarAndamentoPA(EntityManager em, Movimentacao movimentacao, PAOcorrenciaStatus ocorrenciaAtual) throws AppException {

        ProcessoAdministrativo pa = movimentacao.getProcessoAdministrativo();

        if (null != movimentacao.getMovimentacaoAcao()) {
            switch (movimentacao.getMovimentacaoAcao()) {

                case SUSPENDER:

                    new PAInicioFluxoBO().gravarInicioFluxo(em, pa, PAFluxoProcessoConstante.FLUXO_SUSPENSAO);
                    break;

                case RETIRAR_SUSPENSAO:

                    break;

                case CANCELAR:

                    new PAInicioFluxoBO().gravarInicioFluxo(em, pa, PAFluxoProcessoConstante.FLUXO_CANCELAR);

                    break;

                case ARQUIVAR:

                    new PAInicioFluxoBO().gravarInicioFluxo(em, pa, PAFluxoProcessoConstante.FLUXO_ARQUIVAR);

                    break;
                case ARQUIVAR_COM_BLOQUEIO:
                    
                    new PAInicioFluxoBO().gravarInicioFluxo(em, pa, PAFluxoProcessoConstante.FLUXO_ARQUIVAMENTO_COM_BLOQUEIO);
            
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @param em
     * @param pa
     */
    public void penalidadeOuBloqueio(
        EntityManager em, Movimentacao movimentacao,
        ProcessoAdministrativo pa, Long idUsuario, MotivoDesbloqueioCnhEnum motivoDesbloqueio) throws AppException {

        if (MovimentacaoAcaoEnum.ARQUIVAR.equals(movimentacao.getMovimentacaoAcao())
                || MovimentacaoAcaoEnum.CANCELAR.equals(movimentacao.getMovimentacaoAcao())) {

            PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, pa.getId());

            if (null != penalidade) {

                if (penalidade.getDataInicioPenalidade().before(new Date())
                        && penalidade.getDataFimPenalidade().after(new Date())) {

                    penalidade.setAtivo(AtivoEnum.DESATIVADO);
                    new PAPenalidadeProcessoRepositorio().update(em, penalidade);

                    PAPenalidadeProcesso penalidadeNova = new PAPenalidadeProcesso();
                    
                    penalidadeNova.setDataInicioPenalidade(penalidade.getDataInicioPenalidade());
                    penalidadeNova.setAtivo(AtivoEnum.ATIVO);
                    penalidadeNova.setDataCadastro(new Date());
                    penalidadeNova.setDataFimPenalidade(new Date());
                    penalidadeNova.setProcessoAdministrativo(pa);
                    penalidadeNova.setUsuario(idUsuario);
                    penalidadeNova.setUnidadePenal(penalidade.getUnidadePenal());
                    penalidadeNova.setValor(penalidade.getValor());
                    
                    new PAPenalidadeProcessoRepositorio().insert(em, penalidadeNova);
                }
            }

            BloqueioBCA bloqueio = new BloqueioBCARepositorio().getBloqueioBCAporPA(em, pa.getId());

            if (null != bloqueio) {

                if (SituacaoBloqueioBCAEnum.ATIVO.equals(bloqueio.getSituacao())
                        || SituacaoBloqueioBCAEnum.SUSPENSO.equals(bloqueio.getSituacao())) {

                    bloqueio.setDataFim(new Date());
                    bloqueio.setSituacao(SituacaoBloqueioBCAEnum.FINALIZADO);
                    bloqueio.setMotivoDesbloqueio(motivoDesbloqueio);

                    new BloqueioBCARepositorio().update(em, bloqueio);

                    MovimentoBloqueioBCA movimentoBloqueio = new MovimentoBloqueioBCA();
                    movimentoBloqueio.setBloqueioBCA(bloqueio);
                    movimentoBloqueio.setDataBCA(new Date());
                    movimentoBloqueio.setTipo(TipoMovimentoBloqueioBCAEnum.DESBLOQUEIO);
                    movimentoBloqueio.setUsuario(idUsuario);
                    movimentoBloqueio.setAtivo(AtivoEnum.ATIVO);

                    new MovimentoBloqueioBCARepositorio().insert(em, movimentoBloqueio);
                }
            }
        }
    }

    /**
     *
     * @param em
     * @return
     * @throws AppException
     */
    private List<MovimentacaoPaWrapper> getApensadoMovimentacoesAutomaticasDoSistema(EntityManager em) throws AppException {

        List<MovimentacaoPaWrapper> lMovimentacao = new ArrayList();

        List<Integer> andamentos
                = DetranCollectionUtil
                .montaLista(PAAndamentoProcessoConstante.APENSADO.CONFIRMAR_ARQUIVAMENTO_PROCESSO_AGRAVADO
                );

        List<ProcessoAdministrativo> lProcessoAdministrativo
                = new ProcessoAdministrativoRepositorio()
                .getListaProcessoAdministrativoPorAndamentoIniciado(em, andamentos);
        
        if (!DetranCollectionUtil.ehNuloOuVazio(lProcessoAdministrativo)) {

            for (ProcessoAdministrativo apensado : lProcessoAdministrativo) {

                ProcessoAdministrativoApensado processoAdministrativoApensado
                    = new ProcessoAdministrativoApensadoRepositorio()
                        .getProcessoAdministrativoOrigemPorApensado(em, apensado);

                lMovimentacao
                        .add(
                            new MovimentacaoPaWrapper(
                                    apensado.getNumeroProcesso(),
                                    MovimentacaoAcaoEnum.ARQUIVAR,
                                    MovimentacaoMotivoEnum.PROCESSO_APENSADO,
                                    getDataInicioOcorrencia(em, processoAdministrativoApensado.getProcessoAdministrativo().getId()),
                                    getUsuarioResultadoRecurso(em, processoAdministrativoApensado.getProcessoAdministrativo().getId()),
                                    null
                            )
                        );
            }
        }

        return lMovimentacao;
    }

    /**
     * @param em
     * @return
     * @throws AppException 
     */
    private List<MovimentacaoPaWrapper> getAgravadoMovimentacoesAutomaticasDoSistema(EntityManager em) throws AppException {

        List<MovimentacaoPaWrapper> lMovimentacao = new ArrayList();

        List<Integer> andamentos
                = DetranCollectionUtil
                .montaLista(PAAndamentoProcessoConstante.CASSADO.CONFIRMAR_ARQUIVAMENTO_PROCESSO_CASSADO
                );

        List<ProcessoAdministrativo> lProcessoAdministrativo
                = new ProcessoAdministrativoRepositorio()
                .getListaProcessoAdministrativoPorAndamentoIniciado(em, andamentos);
        
        if (!DetranCollectionUtil.ehNuloOuVazio(lProcessoAdministrativo)) {

            for (ProcessoAdministrativo paAgravado : lProcessoAdministrativo) {

                ProcessoAdministrativoAgravamento processoAdministrativoAgravamento
                        = new ProcessoAdministrativoAgravamentoRepositorio()
                        .getProcessoAdministrativoOrigemPorAgravamento(em, paAgravado);
                
                lMovimentacao
                        .add(
                            new MovimentacaoPaWrapper(
                                    paAgravado.getNumeroProcesso(),
                                    MovimentacaoAcaoEnum.ARQUIVAR,
                                    MovimentacaoMotivoEnum.PROCESSO_AGRAVADO,
                                    getDataInicioOcorrencia(em, processoAdministrativoAgravamento.getProcessoAdministrativo().getId()),
                                    getUsuarioResultadoRecurso(em, processoAdministrativoAgravamento.getProcessoAdministrativo().getId()),
                                    null
                            )
                        );
            }
        }

        return lMovimentacao;
    }

    /**
     *
     * @param em
     * @return
     * @throws DatabaseException
     */
    private List<MovimentacaoPaWrapper> getMovimentacoesManuaisDoSistema(EntityManager em) throws AppException {
        return montarMovimentacoes(new MovimentacaoPARepositorio().getMovimentacoesPorListaAndamento(em));
    }

    public List<MovimentacaoPaWrapper> montarMovimentacoes(List<Movimentacao> movimentacoes) throws AppException {
        
        List<MovimentacaoPaWrapper> lMovimentacao = new ArrayList();

        if (!DetranCollectionUtil.ehNuloOuVazio(movimentacoes)) {

            for (Movimentacao movimentacao : movimentacoes) {

                String cpfUsuario = null;

                if (movimentacao.getUsuario() != null) {

                    Usuario usuario = (Usuario) getApoioService().getUsuario(movimentacao.getUsuario());

                    if (usuario != null) {
                        cpfUsuario = getApoioService().getCpfUsuarioPeloId(usuario);
                    }
                }

                lMovimentacao
                        .add(
                                new MovimentacaoPaWrapper(
                                        movimentacao.getProcessoAdministrativo().getNumeroProcesso(),
                                        movimentacao.getMovimentacaoAcao(),
                                        movimentacao.getMotivo(),
                                        movimentacao.getDataCadastro(),
                                        cpfUsuario,
                                        movimentacao.getObservacao()
                                )
                        );
            }
        }

        return lMovimentacao;
    }

    /**
     * @param em
     * @param idProcessoAdministrativo
     * @return
     * @throws AppException 
     */
    private String getUsuarioResultadoRecurso(EntityManager em, Long idProcessoAdministrativo) throws AppException {

        String cpfUsuario = null;
        
        ResultadoRecurso resultadoRecursoPAOrigemParaPAApensado
            = new ResultadoRecursoRepositorio()
                .getResultadoRecursoAtivoPorProcessoAdministrativoEResultado(
                    em,
                    idProcessoAdministrativo,
                    DetranCollectionUtil.montaLista(ResultadoRecursoEnum.PROVIDO, ResultadoRecursoEnum.ACOLHIDO)
                );
        
        if (resultadoRecursoPAOrigemParaPAApensado == null) {

            DetranWebUtils
                .applicationMessageException(
                    "PA.M6",
                    null,
                    idProcessoAdministrativo.toString()
                );
        }

        Usuario usuario 
            = (Usuario) getApoioService().getUsuario(resultadoRecursoPAOrigemParaPAApensado.getUsuario());

        if(usuario != null) {
            cpfUsuario  = getApoioService().getCpfUsuarioPeloId(usuario);
        }
        
        return cpfUsuario;
    }

    /**
     * @param em
     * @param idProcessoAdministrativo
     * @return
     * @throws AppException 
     */
    private Date getDataInicioOcorrencia(EntityManager em, Long idProcessoAdministrativo) throws AppException {
        
        PAOcorrenciaStatus ocorrenciaStatus
            = new PAOcorrenciaStatusRepositorio()
                    .getPAOcorrenciaStatusAtiva(em, idProcessoAdministrativo);
        
        return ocorrenciaStatus.getDataInicio();
    }

    /**
     * 
     * @param em
     * @param movimentacaoAcao
     * @param infracao
     * @throws DatabaseException 
     */
    private void desativarProcessoAdministrativoInfracao(EntityManager em, MovimentacaoAcaoEnum movimentacaoAcao, ProcessoAdministrativoInfracao infracao) throws DatabaseException {
        ProcessoAdministrativoInfracaoRepositorio repositorio = new ProcessoAdministrativoInfracaoRepositorio();
        
        List<MovimentacaoAcaoEnum> acoesPermitidas = DetranCollectionUtil.montaLista(MovimentacaoAcaoEnum.ARQUIVAR,
                                                                                     MovimentacaoAcaoEnum.CANCELAR);
       
        if(acoesPermitidas.contains(movimentacaoAcao)){
            infracao.setAtivo(AtivoEnum.DESATIVADO);
            repositorio.update(em, infracao);
        }
    }

    /**
     * @param wrapper
     * @return 
     */
    public MovimentacaoPAXmlWrapper montarMovimentacaoPaXml(MovimentacaoWrapper wrapper) {
        
        MovimentacaoPAXmlWrapper wrapperXml = new MovimentacaoPAXmlWrapper();
        
        wrapperXml.setNumeroProcesso(new NumeroAnoAdapter().marshal(wrapper.getEntidade().getProcessoAdministrativo().getNumeroProcesso()));
        wrapperXml.setTipoProcesso(wrapper.getEntidade().getProcessoAdministrativo().getTipo().toString());
        wrapperXml.setAcao(wrapper.getEntidade().getMovimentacaoAcao().toString());
        wrapperXml.setDataCadastro(wrapper.getEntidade().getDataCadastro());
        wrapperXml.setUsuario(wrapper.getUsuarioMovimentacao());
        
        return wrapperXml;
    }
    
    public void validarPAAgravado(EntityManager em, ProcessoAdministrativo pa, MovimentacaoAcaoEnum acao, MovimentacaoMotivoEnum motivo) throws AppException {
        
        ProcessoAdministrativoAgravado agravado = new ProcessoAdministrativoAgravadoRepositorio().getAgravadoPorPAOriginal(em, pa.getId());
        
        if(agravado != null){
            validarAcaoEMotivoPAParaMovimentar(acao, motivo);
        }
    }
    
    private void validarAcaoEMotivoPAParaMovimentar(MovimentacaoAcaoEnum acao, MovimentacaoMotivoEnum motivo) throws AppException {
        
        List<MovimentacaoAcaoEnum> acoesPermitidas 
            = DetranCollectionUtil
                .montaLista(
                    MovimentacaoAcaoEnum.ARQUIVAR,
                    MovimentacaoAcaoEnum.RETIRAR_SUSPENSAO,
                    MovimentacaoAcaoEnum.SUSPENDER
                );
        
        if(!acoesPermitidas.contains(acao)){
            DetranWebUtils.applicationMessageException("Não é possível realizar a ação. O processo está APENSADO/AGRAVADO com outros processos.");
        }
        
        if(MovimentacaoAcaoEnum.ARQUIVAR.equals(acao) && !MovimentacaoMotivoEnum.PENA_CUMPRIDA.equals(motivo)){
            DetranWebUtils.applicationMessageException("Não é possível realizar a ação. O processo está APENSADO/AGRAVADO com outros processos.");
        }
    }
    
    public void validarPAApensado(EntityManager em, ProcessoAdministrativo pa, MovimentacaoAcaoEnum acao, MovimentacaoMotivoEnum motivo) throws AppException {
        
        ProcessoAdministrativoApensado apensado = new ProcessoAdministrativoApensadoRepositorio().getApensadoPorPAOriginal(em, pa.getId());
        
        if(apensado != null){
            validarAcaoEMotivoPAParaMovimentar(acao, motivo);
        }
    }
}