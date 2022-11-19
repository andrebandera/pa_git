/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.ParametrizacaoValor;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.projeto.constantes.ParametrizacaoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoCnhRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoBloqueioPessoaRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoRascunhoBloqueioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoBloqueioPessoa;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoRascunhoBloqueio;
import br.gov.ms.detran.processo.administrativo.entidade.Recurso;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoProcessoBloqueioPessoaEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRascunhoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;

/**
 *
 * @author lilly
 */
public class ProcessoRascunhoBloqueioBO {

    private IApoioService apoioService;

    public IApoioService getApoioService() {

        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }

        return apoioService;
    }

    public void validarMudancaSituacaoRascunho(EntityManager em, ProcessoRascunhoBloqueio rascunho, SituacaoRascunhoBloqueioEnum situacao) throws AppException {
        if (rascunho.getPrioridade() > 1) {
            List<ProcessoRascunhoBloqueio> rascunhosPorBloqueioPessoa = new ProcessoRascunhoBloqueioRepositorio().getRascunhosBloqueadosPorBloqueioPessoa(em, rascunho.getProcessoBloqueioPessoa().getId());
            
            boolean existeRascunhoNaoBloqueado
                    = rascunhosPorBloqueioPessoa.stream().
                            filter(item -> item.getPrioridade() < rascunho.getPrioridade()).
                            anyMatch(item -> situacao.equals(item.getSituacao()));
            
            if (existeRascunhoNaoBloqueado) {
                DetranWebUtils.applicationMessageException("Não é possível incluir Bloqueio pois existe PAs para o mesmo CPF na fila para incluir Bloqueio.");
            }
            
        }
    }

    void excluirRascunhosNaoBloqueadosPorBloqueioPessoa(EntityManager em, Long idProcessoBloqueioPessoa) throws DatabaseException {
        ProcessoRascunhoBloqueioRepositorio repo = new ProcessoRascunhoBloqueioRepositorio();
        repo.excluirRascunhosCadastradosPorBloqueioPessoa(em, idProcessoBloqueioPessoa);
    }

    public ProcessoRascunhoBloqueio getUltimoRascunhoBloqueadoPorBloqueioPessoa(EntityManager em, Long idProcessoBloqueioPessoa) throws DatabaseException {
        List<ProcessoRascunhoBloqueio> rascunhos = new ProcessoRascunhoBloqueioRepositorio().getRascunhosBloqueadosPorBloqueioPessoa(em, idProcessoBloqueioPessoa);

        return DetranCollectionUtil.ehNuloOuVazio(rascunhos) ? null : rascunhos.get(0);
    }

    List<ProcessoRascunhoBloqueio> montarRascunhosDoBloqueio(EntityManager em, List<ProcessoAdministrativo> processos, ProcessoBloqueioPessoa bloqueioPessoa, Integer prioridadeAnterior) throws AppException {
        /**
         * Buscar data inicio de cada processo montar rascunhos e prioridades
         */
        List<ProcessoRascunhoBloqueio> rascunhosPriorizados = new ArrayList<>();

        for (ProcessoAdministrativo processo : processos) {
            rascunhosPriorizados.add(montaRascunhoPrePriorizacao(em, processo, bloqueioPessoa));
        }
        priorizarRascunhos(rascunhosPriorizados, prioridadeAnterior);

        return rascunhosPriorizados;
    }

    private void priorizarRascunhos(List<ProcessoRascunhoBloqueio> rascunhosPriorizados, Integer prioridadeAnterior) {
        Comparator<ProcessoRascunhoBloqueio> comparador = (c1, c2) -> {
            return Long.valueOf(c1.getDataInicio().getTime()).compareTo(c2.getDataInicio().getTime());
        };
        Collections.sort(rascunhosPriorizados, comparador);
        for (int i = 0; i < rascunhosPriorizados.size(); i++) {
            rascunhosPriorizados.get(i).setPrioridade(prioridadeAnterior + 1);
            prioridadeAnterior++;
        }
    }

    private ProcessoRascunhoBloqueio montaRascunhoPrePriorizacao(EntityManager em, ProcessoAdministrativo processo, ProcessoBloqueioPessoa bloqueioPessoa) throws AppException {
        Date dataReferencia = getDataReferenciaPorDesistencia(em, processo, Boolean.FALSE);

        if (dataReferencia == null) {
            dataReferencia = getDataReferenciaPorRecurso(em, processo, TipoFasePaEnum.ENTREGA_CNH);
        }

        if (dataReferencia == null) {
            dataReferencia = getDataReferenciaPorNotificacaoPenalidade(em, processo, null);
        }

        return new ProcessoRascunhoBloqueio(processo, bloqueioPessoa, dataReferencia);

    }

    private Date getDataReferenciaPorDesistencia(EntityManager em, ProcessoAdministrativo processo, Boolean isDesentranhamento) throws AppException {
        PAComplemento desistencia = new PAComplementoRepositorio().getComplementoDesistenciaPorPA(em, processo.getId());
        if (desistencia != null) {
            if (PAParametroEnum.DESISTENCIA_15_SGI.equals(desistencia.getParametro()) || PAParametroEnum.DESISTENCIA_REC_INST_PEN.equals(desistencia.getParametro())) {
                Date dataPortaria = new NotificacaoComplementoRepositorio().getDataPenalizacaoPorProcessoAdministrativo(em, processo.getId());
                if (dataPortaria == null) {
                    throw new AppException("Processo possui desistência de instauração ou do SGI mas não possui data de portaria");
                }
                return dataPortaria;
            } else if(!isDesentranhamento) {
                MovimentoCnh movimento = new MovimentoCnhRepositorio().getMovimentoPorProcessoAdministrativoEAcao(em, processo.getId(), AcaoEntregaCnhEnum.ENTREGA);
                if (movimento == null || movimento.getProtocolo() == null || movimento.getProtocolo().getDataProtocolo() == null) {
                    throw new AppException("Processo possui desistência de Entrega CNH mas não possui data de entrega da CNH");
                }
                return movimento.getProtocolo().getDataProtocolo();
            } else {
                throw new AppException("Processo possui desistência de Entrega CNH e não pode estar no fluxo de desentranhamento");
            }
        }
        return null;
    }

    private Date getDataReferenciaPorRecurso(EntityManager em, ProcessoAdministrativo processo, TipoFasePaEnum tipoNotificacao) throws AppException {
         
        RecursoRepositorio recursoRepositorio = new RecursoRepositorio();
        
        Recurso recursoSemResultado = recursoRepositorio.getRecursoAtivoPorProcessoAdministrativoESituacao(em, processo.getId(), SituacaoRecursoEnum.EM_ANALISE);
        
        if(recursoSemResultado != null){
            throw new AppException("Processo possui Recurso Ativo que não foi Julgado.");
        }
        
        NotificacaoProcessoAdministrativo notificacao = new NotificacaoProcessoAdministrativoRepositorio().
                getNotificacaoPorNumeroProcessoETipo(em, processo.getNumeroProcesso(), tipoNotificacao);
        
        Recurso recursoCETRAN = recursoRepositorio.
                getRecursoAtivoPorProcessoAdministrativoTipoDestinoENaoCancelado(em, processo.getId(), TipoFasePaEnum.PENALIZACAO, OrigemDestinoEnum.CETRAN);
        
        if(recursoCETRAN != null){
            if(Objects.isNull(notificacao)){
                throw new AppException("Processo Possui Recurso CETRAN julgado mas não possui a Notificação de " + tipoNotificacao.getTipoFasePaExterna());
            }else if( Objects.isNull(notificacao.getDataPrazoLimite())){
                throw new AppException("Processo Possui Recurso CETRAN julgado e Notificação de " +  tipoNotificacao.getTipoFasePaExterna() + " mas não tem a data do fim do prazo.");
            }
            return Utils.addDayMonth(notificacao.getDataPrazoLimite(), 1);
        }else {
            Recurso recursoJARI = recursoRepositorio.
                    getRecursoAtivoPorProcessoAdministrativoTipoDestinoENaoCancelado(em, processo.getId(), TipoFasePaEnum.PENALIZACAO, OrigemDestinoEnum.JARI);
            if (Objects.nonNull(recursoJARI)) {
                NotificacaoProcessoAdministrativo notificacaoJARI = new NotificacaoProcessoAdministrativoRepositorio().
                    getNotificacaoPorNumeroProcessoETipo(em, processo.getNumeroProcesso(), TipoFasePaEnum.JARI);
                if (Objects.isNull(notificacaoJARI)) {
                    throw new AppException("Processo Possui Recurso JARI julgado mas não possui a Notificação da JARI.");
                } else if (Objects.isNull(notificacaoJARI.getDataPrazoLimite())) {
                    throw new AppException("Processo Possui Recurso JARI julgado e Notificação da JARI mas não tem a data do fim do prazo.");
                }
                return Utils.addDayMonth(notificacaoJARI.getDataPrazoLimite(), 1);
            }
        }
        return null;
    }
    
    private Date getDataReferenciaPorRecursoParaEntrega(EntityManager em, ProcessoAdministrativo processo, Date dataEntrega) throws AppException {

        RecursoRepositorio recursoRepositorio = new RecursoRepositorio();

        Recurso recursoCETRAN = recursoRepositorio.
                getRecursoAtivoPorProcessoAdministrativoTipoDestinoENaoCancelado(em, processo.getId(), TipoFasePaEnum.PENALIZACAO, OrigemDestinoEnum.CETRAN);

        if (recursoCETRAN != null) {
            NotificacaoProcessoAdministrativo notificacaoEntrega = new NotificacaoProcessoAdministrativoRepositorio().
                getNotificacaoPorNumeroProcessoETipo(em, processo.getNumeroProcesso(), TipoFasePaEnum.ENTREGA_CNH);
            
            if (Objects.isNull(notificacaoEntrega)) {
                throw new AppException("Processo Possui Recurso CETRAN julgado mas não possui a Notificação de Entrega de CNH.");
            } else if (Objects.isNull(notificacaoEntrega.getDataPrazoLimite())) {
                throw new AppException("Processo Possui Recurso CETRAN julgado e Notificação de Entrega de CNH mas não tem a data do fim do prazo.");
            }
            
            Date dataReferencia = Utils.addDayMonth(notificacaoEntrega.getDataPrazoLimite(), 1);
            
            if (Objects.isNull(dataEntrega) || dataReferencia.before(dataEntrega)) {
                return dataReferencia;
            }
            return dataEntrega;
        } else {
            Recurso recursoJARI = recursoRepositorio.
                    getRecursoAtivoPorProcessoAdministrativoTipoDestinoENaoCancelado(em, processo.getId(), TipoFasePaEnum.PENALIZACAO, OrigemDestinoEnum.JARI);
            
            if (Objects.nonNull(recursoJARI)) {
                NotificacaoProcessoAdministrativo notificacaoJari = new NotificacaoProcessoAdministrativoRepositorio().
                        getNotificacaoPorNumeroProcessoETipo(em, processo.getNumeroProcesso(), TipoFasePaEnum.JARI);

                if(Objects.isNull(notificacaoJari)) {
                        throw new AppException("Processo Possui Recurso JARI julgado mas não possui a Notificação da JARI");
                }
                if(Objects.isNull(notificacaoJari.getDataPrazoLimite())) {
                    throw new AppException("Processo Possui Recurso JARI julgado e Notificação da JARI mas não tem a data do fim do prazo");
                }
                Date dataReferencia = Utils.addDayMonth(notificacaoJari.getDataPrazoLimite(), 1);
                if (Objects.isNull(dataEntrega) || dataReferencia.before(dataEntrega)) {
                    return dataReferencia;
                }
                return dataEntrega;
                
            }
        }
        return null;
    }

    private Date getDataReferenciaPorNotificacaoPenalidade(EntityManager em, ProcessoAdministrativo processo, Date dataReferenciaParaEntrega) throws AppException {
        NotificacaoProcessoAdministrativo notificacaoPenalizacao = new NotificacaoProcessoAdministrativoRepositorio().
                getNotificacaoPorNumeroProcessoETipo(em, processo.getNumeroProcesso(), TipoFasePaEnum.PENALIZACAO);

        if (Objects.isNull(notificacaoPenalizacao)) {
            throw new AppException("Processo não Possui a Notificação de Penalidade.");
        } else if (Objects.isNull(notificacaoPenalizacao.getDataPrazoLimite())) {
            throw new AppException("Processo Possui Notificação de Penalidade mas não tem a data do fim do prazo.");
        } else {
            Integer prazo = buscarPrazoPenalizacao();
            Date dataReferencia = Utils.addDayMonth(notificacaoPenalizacao.getDataPrazoLimite(), prazo + 1);

            if (Objects.isNull(dataReferenciaParaEntrega) || dataReferencia.before(dataReferenciaParaEntrega)) {
                return dataReferencia;
            }
            return dataReferenciaParaEntrega;

        }
    }

    private Integer buscarPrazoPenalizacao() throws AppException {
        ParametrizacaoValor parametrizacaoValor = (ParametrizacaoValor) getApoioService().getParametroValorPorParametroEnum(ParametrizacaoEnum.TEMPO_EXTRA_PRAZO_DETRAN_PA);

        if (Objects.isNull(parametrizacaoValor) || Objects.isNull(parametrizacaoValor.getValorNumerico())) {
            throw new AppException("Processo Possui Notificação de Penalidade com a data fim do Prazo mas o parâmtero 311 está sem valor.");
        }
        return parametrizacaoValor.getValorNumerico().intValue();
    }

    public void gravar(EntityManager em, ProcessoRascunhoBloqueio rascunho, Date dataInicio, SituacaoRascunhoBloqueioEnum situacao) throws AppException {

        rascunho.setDataInicio(dataInicio);
        rascunho.setSituacao(situacao);
        PAComplemento complemento
                = (PAComplemento) new PAComplementoRepositorio().
                        getPAComplementoPorNumeroPAEParametroEAtivo(em,
                                rascunho.getProcessoAdministrativo().getNumeroProcesso(),
                                PAParametroEnum.TEMPO_PENALIDADE);

        BigDecimal tempo = new BigDecimal(complemento.getValor());

        if (rascunho.getProcessoAdministrativo().isJuridico()) {
            rascunho.setDataFim(Utils.addDayMonth(Utils.addDayMonth(dataInicio, -1), tempo.intValue()));
        } else {
            rascunho.setDataFim(Utils.addMonth(Utils.addDayMonth(dataInicio, -1), tempo.intValue()));
        }
        new ProcessoRascunhoBloqueioRepositorio().insert(em, rascunho);
    }

    void atualizarRascunhoParaBloqueadoWEB(EntityManager em, ProcessoRascunhoBloqueio rascunho) throws DatabaseException {
        rascunho.setSituacao(SituacaoRascunhoBloqueioEnum.BLOQUEADO_BL);

        new ProcessoRascunhoBloqueioRepositorio().update(em, rascunho);

        new ProcessoBloqueioPessoaBO().atualizarBloqueioPessoa(em, rascunho.getProcessoBloqueioPessoa(), SituacaoProcessoBloqueioPessoaEnum.INICIADO);
    }

    List<ProcessoRascunhoBloqueio> montarRascunhosDoBloqueioParaEntregaCnh(EntityManager em, 
                                                                           List<ProcessoAdministrativo> processos, 
                                                                           ProcessoBloqueioPessoa bloqueioPessoa, 
                                                                           Date dataEntregaCnh,
                                                                           Integer prioridadeUltimoBloqueado) 
            throws AppException {
        List<ProcessoRascunhoBloqueio> rascunhosPriorizados = new ArrayList<>();

        for (ProcessoAdministrativo processo : processos) {
            rascunhosPriorizados.add(montaRascunhoPrePriorizacaoEntregaCnh(em, processo, bloqueioPessoa, dataEntregaCnh));
        }
        priorizarRascunhos(rascunhosPriorizados, prioridadeUltimoBloqueado);

        return rascunhosPriorizados;
    }
    
    public ProcessoRascunhoBloqueio montarRascunhoDoBloqueioParaDesentranhamento(EntityManager em, 
                                                                           ProcessoAdministrativo processo, 
                                                                           ProcessoBloqueioPessoa bloqueioPessoa, 
                                                                           Integer prioridadeUltimoBloqueado) 
            throws AppException {
        
        Date dataReferencia = getDataReferenciaPorDesistencia(em, processo, Boolean.TRUE);

        if (dataReferencia == null) {
           dataReferencia = getDataReferenciaPorRecurso(em, processo, TipoFasePaEnum.DESENTRANHAMENTO); 
        }
        if (dataReferencia == null) {
            dataReferencia = getDataReferenciaPorNotificacaoPenalidade(em, processo, null);
        }

        ProcessoRascunhoBloqueio rascunho = new ProcessoRascunhoBloqueio(processo, bloqueioPessoa, dataReferencia);
        rascunho.setPrioridade(prioridadeUltimoBloqueado + 1);
        
        return rascunho;
    }

    private ProcessoRascunhoBloqueio montaRascunhoPrePriorizacaoEntregaCnh(EntityManager em, ProcessoAdministrativo processo, ProcessoBloqueioPessoa bloqueioPessoa, Date dataEntregaCnh) throws AppException {
        Date dataReferencia = getDataReferenciaPorProcessoJuridico(processo, dataEntregaCnh);

        if (dataReferencia == null) {
            dataReferencia = getDataReferenciaPorRecursoParaEntrega(em, processo, dataEntregaCnh);
        }

        if (dataReferencia == null) {
            dataReferencia = getDataReferenciaPorDesistenciaInstauracaoParaEntrega(em, processo, dataEntregaCnh);
        }
        if (dataReferencia == null) {
            dataReferencia = getDataReferenciaPorNotificacaoPenalidade(em, processo, dataEntregaCnh);
        }

        return new ProcessoRascunhoBloqueio(processo, bloqueioPessoa, dataReferencia);
    }

    private Date getDataReferenciaPorProcessoJuridico(ProcessoAdministrativo processo, Date dataEntregaCnh) {

        return (processo.getOrigem().equals(OrigemEnum.JURIDICA)) ? dataEntregaCnh : null;

    }

    public void criarRascunhoBloqueioParaDesentranhamento(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws DatabaseException, AppException {
        
        ProcessoBloqueioPessoaRepositorio bloqueioPessoaRepositorio = new ProcessoBloqueioPessoaRepositorio();
        
        ProcessoBloqueioPessoa bloqueioPessoa = bloqueioPessoaRepositorio.buscarProcessoBloqueioPessoaAtualOuCriarNovo(em, processoAdministrativo.getCpf());
        
        ProcessoRascunhoBloqueio ultimoRascunhoBloqueio = getUltimoRascunhoBloqueadoPorBloqueioPessoa(em, bloqueioPessoa.getId());
        
        ProcessoRascunhoBloqueio rascunho = montarRascunhoDoBloqueioParaDesentranhamento(em, processoAdministrativo, bloqueioPessoa,Objects.isNull(ultimoRascunhoBloqueio) ? 0 : ultimoRascunhoBloqueio.getPrioridade());
        
        
        incluirRascunhoEBloqueioPessoa(em, ultimoRascunhoBloqueio, rascunho, bloqueioPessoa);
    }

    private void incluirRascunhoEBloqueioPessoa( EntityManager em, ProcessoRascunhoBloqueio ultimoRascunhoBloqueio, ProcessoRascunhoBloqueio rascunho, ProcessoBloqueioPessoa bloqueioPessoa) throws AppException, DatabaseException {
        Date dataInicio;
        ProcessoBloqueioPessoaRepositorio bloqueioPessoaRepositorio = new ProcessoBloqueioPessoaRepositorio();
        if(Objects.isNull(ultimoRascunhoBloqueio) || ultimoRascunhoBloqueio.getDataFim().before(rascunho.getDataInicio()))
            dataInicio = rascunho.getDataInicio();
        else {
            dataInicio = Utils.addDayMonth(ultimoRascunhoBloqueio.getDataFim(), 1);
        }
        gravar(em, rascunho, dataInicio, SituacaoRascunhoBloqueioEnum.BLOQUEADO);
        if(SituacaoProcessoBloqueioPessoaEnum.CADASTRADO.equals(bloqueioPessoa.getSituacao())){
            bloqueioPessoa.setDataInicio(dataInicio);
            bloqueioPessoa.setSituacao(SituacaoProcessoBloqueioPessoaEnum.INICIADO);
        }
        bloqueioPessoa.setDataFim(rascunho.getDataFim());
        bloqueioPessoa.setTempo(Utils.getDiferencaEmDiasEntreDatas(bloqueioPessoa.getDataFim(), bloqueioPessoa.getDataInicio()));
        bloqueioPessoaRepositorio.update(em, bloqueioPessoa);
    }

    public void criarRascunhoBloqueioParaJuridico(EntityManager em, ProcessoAdministrativo processoAdministrativo, Date dataInicio) throws AppException{
        
        ProcessoBloqueioPessoaRepositorio bloqueioPessoaRepositorio = new ProcessoBloqueioPessoaRepositorio();
        
        ProcessoBloqueioPessoa bloqueioPessoa = bloqueioPessoaRepositorio.buscarProcessoBloqueioPessoaAtualOuCriarNovo(em, processoAdministrativo.getCpf());
        
        ProcessoRascunhoBloqueio ultimoRascunhoBloqueio = getUltimoRascunhoBloqueadoPorBloqueioPessoa(em, bloqueioPessoa.getId());
        
        if(Objects.nonNull(ultimoRascunhoBloqueio) && Utils.getDataComHoraFinal(ultimoRascunhoBloqueio.getDataFim()).after(Utils.getDataComHoraInicial(dataInicio))) {
            dataInicio = Utils.addDayMonth(ultimoRascunhoBloqueio.getDataFim(), 1);
        }
        
        dataInicio = recuperaDataInicioParaPJU(em, processoAdministrativo.getCpf(), dataInicio);
        
        ProcessoRascunhoBloqueio rascunho = new ProcessoRascunhoBloqueio(processoAdministrativo, bloqueioPessoa, dataInicio);
        rascunho.setPrioridade(Objects.isNull(ultimoRascunhoBloqueio) ? 1 : ultimoRascunhoBloqueio.getPrioridade()+1);
        
        incluirRascunhoEBloqueioPessoa(em, ultimoRascunhoBloqueio, rascunho, bloqueioPessoa);
    }
    
    private Date recuperaDataInicioParaPJU(EntityManager em, String cpf, Date dataInicioPenalidade) throws DatabaseException {
        PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPAPenalidadeProcessoMaiorDataFimPorCpfCondutor(em, cpf);
        if(Objects.isNull(penalidade))
            return dataInicioPenalidade;
        if(Utils.getDataComHoraFinal(penalidade.getDataFimPenalidade()).after(Utils.getDataComHoraInicial(dataInicioPenalidade)))
            return Utils.addDayMonth(penalidade.getDataFimPenalidade(), 1);
        else 
            return dataInicioPenalidade;
    }

    private Date getDataReferenciaPorDesistenciaInstauracaoParaEntrega(EntityManager em, ProcessoAdministrativo processo, Date dataEntregaCnh) throws AppException {
        PAComplemento desistencia = new PAComplementoRepositorio().getComplementoDesistenciaPorPA(em, processo.getId());
        if (desistencia != null) {
            if (PAParametroEnum.DESISTENCIA_15_SGI.equals(desistencia.getParametro()) || PAParametroEnum.DESISTENCIA_REC_INST_PEN.equals(desistencia.getParametro())) {
                Date dataPortaria = new NotificacaoComplementoRepositorio().getDataPenalizacaoPorProcessoAdministrativo(em, processo.getId());
                if (dataPortaria == null) {
                    throw new AppException("Processo possui desistência de instauração ou do SGI mas não possui data de portaria");
                }

                if (dataPortaria.before(dataEntregaCnh)) {
                    return dataPortaria;
                }
                return dataEntregaCnh;
            }
        }
        return null;
    }
}
