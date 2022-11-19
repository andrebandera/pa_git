/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.ExceptionUtils;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.ace.IAcessoService;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoBloqueioPessoaRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoRascunhoBloqueioRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoBloqueioPessoa;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoRascunhoBloqueio;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoProcessoBloqueioPessoaEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRascunhoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.PAControleCnhWrapper;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;

/**
 *
 * @author lilly
 */
public class ProcessoBloqueioPessoaBO {
    
    private static final Logger LOG = Logger.getLogger(ArquivamentoPASuspensaoBO.class);

    private IProcessoAdministrativoService service;
    
    private IAcessoService acessoService;

    private IPAControleFalhaService falhaService;
    
    public IProcessoAdministrativoService getService() {

        if (service == null) {
            service = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }

        return service;
    }

    public IAcessoService getAcessoService() {

        if (acessoService == null) {
            acessoService = (IAcessoService) JNDIUtil.lookup("ejb/AcessoService");
        }

        return acessoService;
    }

    public IPAControleFalhaService getFalhaService() {

        if (falhaService == null) {
            falhaService = (IPAControleFalhaService) JNDIUtil.lookup("ejb/PAControleFalhaService");
        }

        return falhaService;
    }

    public void iniciaExecucao(EntityManager em) throws AppException{
        
        List<ProcessoAdministrativo> processosAptos43 = new ProcessoAdministrativoRepositorio().
                getListProcessosAdministrativosAptosIncluirBloqueioAndamento43(em);
        
//        List<ProcessoAdministrativo> processosAptos183 = new ProcessoAdministrativoRepositorio().
//                getListProcessosAdministrativosAptosIncluirBloqueioAndamento183(em);
//        
//        processosAptos43.addAll(processosAptos183);
        
        Map<String, List<ProcessoAdministrativo>> mapaPorCPF = processosAptos43.stream().collect(Collectors.groupingBy(ProcessoAdministrativo::getCpf));
        
        for (Map.Entry<String, List<ProcessoAdministrativo>> entrada : mapaPorCPF.entrySet()) {
                String cpf = entrada.getKey();
                List<ProcessoAdministrativo> processos = entrada.getValue();
                
                getService().processarBloqueioPessoa(cpf, processos);
            
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processarBloqueioPessoa(EntityManager em, String cpf, List processos) {
        ProcessoRascunhoBloqueioBO rascunhoBO = new ProcessoRascunhoBloqueioBO();
        
        Date dataInicio;
        ProcessoBloqueioPessoaRepositorio bloqueioPessoaRepositorio = new ProcessoBloqueioPessoaRepositorio();
        
        try{
            
            ProcessoBloqueioPessoa bloqueioPessoa = bloqueioPessoaRepositorio.buscarProcessoBloqueioPessoaAtualOuCriarNovo(em, cpf);

            rascunhoBO.excluirRascunhosNaoBloqueadosPorBloqueioPessoa(em, bloqueioPessoa.getId());

            ProcessoRascunhoBloqueio ultimoRascunhoBloqueio = rascunhoBO.getUltimoRascunhoBloqueadoPorBloqueioPessoa(em, bloqueioPessoa.getId());

            List<ProcessoRascunhoBloqueio> listRascunhoBloqueio = rascunhoBO.
                            montarRascunhosDoBloqueio(em, processos, 
                                                        bloqueioPessoa, Objects.isNull(ultimoRascunhoBloqueio) ? 0 : ultimoRascunhoBloqueio.getPrioridade());

            if(SituacaoProcessoBloqueioPessoaEnum.INICIADO.equals(bloqueioPessoa.getSituacao())){
                dataInicio = Utils.addDayMonth(ultimoRascunhoBloqueio.getDataFim(), 1);
            }else{
                PAPenalidadeProcesso ultimaPenalidade = new PAPenalidadeProcessoRepositorio().getUltimaPenalidadePorCPF(em, bloqueioPessoa.getCpf());
                dataInicio = listRascunhoBloqueio.get(0).getDataInicio();
                if(Objects.nonNull(ultimaPenalidade) && Utils.compareBetweenToDate(ultimaPenalidade.getDataInicioPenalidade(), ultimaPenalidade.getDataFimPenalidade(), dataInicio)){
                    dataInicio = Utils.addDayMonth(ultimaPenalidade.getDataFimPenalidade(), 1);
                }
                bloqueioPessoa.setDataInicio(dataInicio);
            }
            Boolean bloqueioExpirado = false;
            for (int i = 0;( i < listRascunhoBloqueio.size() && !bloqueioExpirado); i++) {
                ProcessoRascunhoBloqueio rascunho = listRascunhoBloqueio.get(i);

                validarRascunhoJaCadastrado(em, rascunho);

                rascunhoBO.gravar(em, rascunho, dataInicio, SituacaoRascunhoBloqueioEnum.NAO_BLOQUEADO);
                dataInicio = Utils.addDayMonth(rascunho.getDataFim(), 1);
                if(rascunho.getDataFim().before(Calendar.getInstance().getTime())){
                    bloqueioExpirado = true;
                }
            }
            bloqueioPessoa.setDataFim(Utils.addDayMonth(dataInicio, -1));
            bloqueioPessoa.setTempo(Utils.getDiferencaEmDiasEntreDatas(bloqueioPessoa.getDataFim(), bloqueioPessoa.getDataInicio()));
            bloqueioPessoaRepositorio.update(em, bloqueioPessoa);
        } catch (Exception e) {
                    LOG.debug("Erro ao incluir rascunho pessoa.", e);

                getFalhaService()
                        .gravarFalhaEspecifica(cpf,
                                e.getMessage() + " - " + new ExceptionUtils().getStack(e),
                                "PA_INCLUIR_BLOQUEIO_PESSOA"
                        );
            }
        
    }

    void atualizarBloqueioPessoa(EntityManager em, ProcessoBloqueioPessoa processoBloqueioPessoa, SituacaoProcessoBloqueioPessoaEnum situacao) throws DatabaseException {
        processoBloqueioPessoa.setSituacao(situacao);
        new ProcessoBloqueioPessoaRepositorio().update(em, processoBloqueioPessoa);
    }

    private void validarRascunhoJaCadastrado(EntityManager em, ProcessoRascunhoBloqueio rascunho) throws AppException {
        ProcessoRascunhoBloqueio rascunhoBanco = new ProcessoRascunhoBloqueioRepositorio().getRascunhoPorProcessoAdministrativo(em, rascunho.getProcessoAdministrativo());
        
        if(Objects.nonNull(rascunhoBanco)){
            DetranWebUtils.applicationMessageException("Já existe rascunho cadastrado para este PA.");
        }
    }

    public void iniciaExecucaoEntregaCnh(EntityManager em, List<PAControleCnhWrapper> listaProcessos, Date dataEntregaCnh) throws AppException {
        ProcessoRascunhoBloqueioBO rascunhoBO = new ProcessoRascunhoBloqueioBO();
        
        Date dataInicio;
        ProcessoBloqueioPessoaRepositorio bloqueioPessoaRepositorio = new ProcessoBloqueioPessoaRepositorio();
        List<ProcessoAdministrativo> processosFiltrados = listaProcessos.stream().
                                                map(item -> item.getProcesso()).
                                                filter(item -> !item.getTipo().equals(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH)).
                                                filter(item -> {
                                                    try {
                                                        return (!rascunhoJaCadastradoEBloqueado(em, item) && !existePenalidadeParaPA(em, item));
                                                    } catch (AppException e) {
                                                    }
                                                    return false;
                                                }).
                                                collect(Collectors.toList());
        if(processosFiltrados.isEmpty())
            return;
        
        ProcessoBloqueioPessoa bloqueioPessoa = bloqueioPessoaRepositorio.buscarProcessoBloqueioPessoaAtualOuCriarNovo(em, processosFiltrados.get(0).getCpf());
        rascunhoBO.excluirRascunhosNaoBloqueadosPorBloqueioPessoa(em, bloqueioPessoa.getId());
        
        ProcessoRascunhoBloqueio ultimoRascunhoBloqueio = rascunhoBO.getUltimoRascunhoBloqueadoPorBloqueioPessoa(em, bloqueioPessoa.getId());
        
        List<ProcessoRascunhoBloqueio> listRascunhoBloqueio = rascunhoBO.montarRascunhosDoBloqueioParaEntregaCnh(em, processosFiltrados, bloqueioPessoa, dataEntregaCnh, Objects.isNull(ultimoRascunhoBloqueio) ? 0 : ultimoRascunhoBloqueio.getPrioridade());
        
        if(SituacaoProcessoBloqueioPessoaEnum.INICIADO.equals(bloqueioPessoa.getSituacao())){
            dataInicio = Utils.addDayMonth(ultimoRascunhoBloqueio.getDataFim(), 1);
        }else{
            dataInicio = listRascunhoBloqueio.get(0).getDataInicio();
            bloqueioPessoa.setDataInicio(dataInicio);
        }
        for (int i = 0; i < listRascunhoBloqueio.size(); i++) {
            ProcessoRascunhoBloqueio rascunho = listRascunhoBloqueio.get(i);
            
            if(dataInicio.before(rascunho.getDataInicio()))
                dataInicio = rascunho.getDataInicio();
            
            rascunhoBO.gravar(em, rascunho, dataInicio, SituacaoRascunhoBloqueioEnum.BLOQUEADO);
            dataInicio = Utils.addDayMonth(rascunho.getDataFim(), 1);
        }
        bloqueioPessoa.setDataFim(Utils.addDayMonth(dataInicio, -1));
        bloqueioPessoa.setTempo(Utils.getDiferencaEmDiasEntreDatas(bloqueioPessoa.getDataFim(), bloqueioPessoa.getDataInicio()));
        bloqueioPessoa.setSituacao(SituacaoProcessoBloqueioPessoaEnum.INICIADO);
        bloqueioPessoaRepositorio.update(em, bloqueioPessoa);
        
    }

    private boolean rascunhoJaCadastradoEBloqueado(EntityManager em, ProcessoAdministrativo pa) throws AppException {
        ProcessoRascunhoBloqueio rascunhoBD = new ProcessoRascunhoBloqueioRepositorio().getRascunhoPorProcessoAdministrativo(em, pa);
        return !Objects.isNull(rascunhoBD) && !rascunhoBD.getSituacao().equals(SituacaoRascunhoBloqueioEnum.NAO_BLOQUEADO);
    }

    private boolean existePenalidadeParaPA(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, processoAdministrativo.getId());
        
        return Objects.nonNull(penalidade) && AtivoEnum.ATIVO.equals(penalidade.getAtivo());
    }
    
}
