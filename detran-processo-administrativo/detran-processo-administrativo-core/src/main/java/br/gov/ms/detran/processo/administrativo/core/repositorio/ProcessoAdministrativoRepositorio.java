package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoNotificacaoEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.MotivoPenalidadeEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.persistencia.util.JPAUtil;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.DestinoFaseBO;
import br.gov.ms.detran.processo.administrativo.core.bo.RecursoBO;
import br.gov.ms.detran.processo.administrativo.entidade.*;
import br.gov.ms.detran.processo.administrativo.enums.*;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoBCAWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoRecursoWrapper;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

/**
 * @author Lillydi
 */
public class ProcessoAdministrativoRepositorio extends AbstractJpaDAORepository<ProcessoAdministrativo> {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoRepositorio.class);
    
    private static final Integer PRIMEIRO_RESULTADO_CONSULTA = 1;
    private static final Integer ATIVO = 1;
    private static final Integer SUSPENSO = 24;
    
    /**
     * 
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public List<ProcessoAdministrativo> getProcessoAdministrativoAtivosParaPermissionado(EntityManager em, String cpf) throws DatabaseException {
        Object[] params= {
            cpf,
            DetranCollectionUtil.montaLista(PAStatusEnum.ARQUIVADO.getCodigo(), PAStatusEnum.CANCELADO.getCodigo()),
            AtivoEnum.ATIVO
        };
        return getListNamedQuery(em, "ProcessoAdministrativo.getProcessoAdministrativoAtivosParaPermissionado", params );
    }

    /**
     * 
     * @param em
     * @param numeroDetran
     * @return
     * @throws DatabaseException 
     */
    public List<ProcessoAdministrativo> getPAsParaResultadoProvaPorCondutor(EntityManager em, Long numeroDetran) throws DatabaseException {
        Object[] params= {
            numeroDetran,
            PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_REALIZACAO_PROVA_CURSO,
            FaseProcessoEnum.CANCELAMENTO_COMUM.getCodigoAteParte2() + "%",
            FaseProcessoEnum.ARQUIVAMENTO_COMUM.getCodigoAteParte2() + "%",
            AtivoEnum.ATIVO.ordinal(),
            DetranCollectionUtil.montaLista(22,23) // Status ARQUIVADO E CANCELADO
        };
        return getListNamedQuery(em, "ProcessoAdministrativo.getPAsParaResultadoProvaPorCondutor", params );
    }

    /**
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public List getProcessosAdministrativosSuspensaoAtivos(EntityManager em, String cpf) throws DatabaseException {
        Object[] params= {
            cpf,
            PAStatusEnum.getStatusNaoPermitidosParaBuscaPAsAtivos(),
            AtivoEnum.ATIVO,
            DetranCollectionUtil.montaLista(TipoProcessoEnum.SUSPENSAO, TipoProcessoEnum.SUSPENSAO_JUDICIAL),
            null
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getProcessosAdministrativosSuspensaoAtivos", params );
    }
    
    /**
     * @param em
     * @param cpf
     * @param motivo
     * @return
     * @throws DatabaseException 
     */
    public List getProcessosAdministrativosSuspensaoAtivosPorMotivo(EntityManager em, String cpf, MotivoPenalidadeEnum motivo) throws DatabaseException {
        Object[] params= {
            cpf,
            PAStatusEnum.getStatusNaoPermitidosParaBuscaPAsAtivos(),
            AtivoEnum.ATIVO,
            DetranCollectionUtil.montaLista(TipoProcessoEnum.SUSPENSAO, TipoProcessoEnum.SUSPENSAO_JUDICIAL),
            motivo
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getProcessosAdministrativosSuspensaoAtivos", params );
    }
    
    /**
     * 
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public List getProcessosAdministrativosSuspensaoAtivosComAndamentoPorCPF(EntityManager em, String cpf) throws DatabaseException {
        
        Object[] params = {
            cpf,
            PAStatusEnum.getStatusNaoPermitidosParaBuscaPAsAtivos(),
            AtivoEnum.ATIVO,
            DetranCollectionUtil.montaLista(TipoProcessoEnum.SUSPENSAO, TipoProcessoEnum.SUSPENSAO_JUDICIAL)
        };
        
        return 
            getListNamedQuery(
                em, 
                "ProcessoAdministrativo.getProcessosAdministrativosSuspensaoAtivosComAndamentoPorCPF", 
                params);
    }
    
    /**
     * 
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public List<ProcessoAdministrativo> getListProcessoAdministrativoAtivoCassacao(EntityManager em, String cpf) throws DatabaseException {
        
        List<Integer> lStatus 
            = PAStatusEnum.getStatusNaoPermitidosParaBuscaPAsAtivos();
        
        List<TipoProcessoEnum> lTipoProcesso 
            = DetranCollectionUtil
                .montaLista(
                    TipoProcessoEnum.CASSACAO,
                    TipoProcessoEnum.CASSACAO_JUDICIAL,
                    TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH
                );
        
        Object[] params= {
            cpf,
            lStatus,
            lTipoProcesso,
            AtivoEnum.ATIVO
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListProcessoAdministrativoAtivoParaCondutorReincidente", params);
    }
    
    /**
     * 
     * Busca um lista de entidades, através de uma consulta nomeada (namedquery).
     *
     * @param namedQuery : Nome da consulta nomeada.
     * @param params : parametros da consulta.
     * @param em
     * @return : retorna uma lista de entidade.
     * @throws DatabaseException : classe de exceção a ser lançada qdo houver erro.
     */
    public List getListNamedQuery(EntityManager em, String namedQuery, Map<String, Object> params) throws DatabaseException {

        List lista = null;
        
        try {
            
            Query query = em.createNamedQuery(namedQuery);
            
            if (params != null) {
                JPAUtil.setParameters(query, params);
            }
            
            lista = query.getResultList();
            
        } catch (Exception e) {
            
            LOG.error("Erro ao executar a pesquisa {0}", e, namedQuery);
            throw new DatabaseException(e, e.getLocalizedMessage());
        }
        
        return lista;
    }
    
    /**
     * Retorna lista de processos administrativos de suspensão buscados por cpf.
     * 
     * @param em
     * @param cpf
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List<ProcessoAdministrativo> getListProcessoAdministrativoSuspensaoEmAndamento(EntityManager em, String cpf) throws DatabaseException {
        
        List<Integer> lStatus = DetranCollectionUtil.montaLista(PAStatusEnum.ARQUIVADO.getCodigo(), PAStatusEnum.CANCELADO.getCodigo());
        
        List<TipoProcessoEnum> lTipoProcesso = DetranCollectionUtil.montaLista(TipoProcessoEnum.SUSPENSAO,
                                                                               TipoProcessoEnum.SUSPENSAO_JUDICIAL);
        
        Object[] params= {cpf,
                          lStatus,
                          lTipoProcesso,
                          AtivoEnum.ATIVO};
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListProcessoAdministrativoSuspensaoEmAndamento", params);
    }

    /**
     * @param em
     * @return
     * @throws DatabaseException 
     */
    public List getListaProcessosAdministrativosAtivos(EntityManager em) throws DatabaseException {
        return super.getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessosAdministrativosAtivos", AtivoEnum.ATIVO);
    }

    /**
     * @param em
     * @param numeroProcesso
     * @return
     * @throws DatabaseException 
     */
    public ProcessoAdministrativo getProcessoAdministrativoPorNumeroProcessoAtivo(EntityManager em, String numeroProcesso) throws AppException {
        
        if (DetranStringUtil.ehBrancoOuNulo(numeroProcesso)) {
            DetranWebUtils.applicationMessageException("severity.error.processoadministrativo.invalido", "", numeroProcesso);
        }
        
        ProcessoAdministrativo processoAdministrativo = 
                getNamedQuery(em, 
                              "ProcessoAdministrativo.getProcessoAdministrativoPorNumeroProcessoAtivo", 
                              numeroProcesso, 
                              AtivoEnum.ATIVO);
        
        if (processoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.processoadministrativo.nao.encontrado", "", numeroProcesso);
        }
        
        return processoAdministrativo;
    }
    
    /**
     * @param em
     * @param numeroProcesso
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativo getProcessoAdministrativoPorNumeroProcessoSemAtivo(EntityManager em, 
                                                                                     String numeroProcesso) throws AppException {
        
        if (DetranStringUtil.ehBrancoOuNulo(numeroProcesso)) {
            DetranWebUtils.applicationMessageException("severity.error.processoadministrativo.invalido", "", numeroProcesso);
        }
        
        ProcessoAdministrativo processoAdministrativo = 
                getNamedQuery(em, 
                              "ProcessoAdministrativo.getProcessoAdministrativoPorNumeroProcessoSemAtivo", 
                              numeroProcesso);
        
        if (processoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.processoadministrativo.nao.encontrado", "", numeroProcesso);
        }
        
        return processoAdministrativo;
    }

    /**
     * @param em
     * @param infracaoCodigo
     * @param idsPA
     * @return
     * @throws DatabaseException 
     */
    public Boolean artigoInfracaoDiferenteArtigoInfracaoPAReincidente(EntityManager em, String infracaoCodigo, List<Long> idsPA) throws DatabaseException {
        List<ProcessoAdministrativo> lista = getListNamedQuery(em, "ProcessoAdministrativo.validarArtigoInfracaoDiferenteArtigoInfracaoPAReincidente", infracaoCodigo, idsPA, AtivoEnum.ATIVO.ordinal());
        return !DetranCollectionUtil.ehNuloOuVazio(lista);
    }
    
    /**
     * @param em
     * @param infracaoCodigo
     * @param idsPA
     * @return
     * @throws DatabaseException 
     */
    public Boolean artigoInfracaoIgualArtigoInfracaoPAReincidente(EntityManager em, String infracaoCodigo, List<Long> idsPA) throws DatabaseException {
        List<ProcessoAdministrativo> lista = getListNamedQuery(em, "ProcessoAdministrativo.validarArtigoInfracaoIgualArtigoInfracaoPAReincidente", infracaoCodigo, idsPA, AtivoEnum.ATIVO.ordinal());
        return !DetranCollectionUtil.ehNuloOuVazio(lista);
    }
    
    /**
     * @param em
     * @param numAuto
     * @param codigoInfracao
     * @param cpf
     * @param idAutuador
     * @return
     * @throws DatabaseException 
     */
    public List getListPAsAtivosPorNumAutoECodigoInfracaoECpfEAutuador(EntityManager em, 
                                                                       String numAuto, 
                                                                       String codigoInfracao, 
                                                                       String cpf, 
                                                                       Long idAutuador) throws DatabaseException {
        
        Object[] params= {numAuto,
                          codigoInfracao == null? null : codigoInfracao + "%",
                          DetranStringUtil.ehBrancoOuNulo(cpf) ? null : cpf,
                          idAutuador,
                          AtivoEnum.ATIVO,
                          FaseProcessoEnum.CANCELAMENTO_COMUM.getCodigoAteParte2() + "%",
                          FaseProcessoEnum.ARQUIVAMENTO_COMUM.getCodigoAteParte2() + "%"};
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListPAsAtivosPorNumAutoECodigoInfracaoECpfEAutuador", params);
    }
    
    /**
     * 
     * @param em
     * @param numAuto
     * @param codigoInfracao
     * @param cpf
     * @param idAutuador
     * @return
     * @throws DatabaseException 
     */
    public List getListPAsAtivosSemSuspensaoPorNumAutoECodigoInfracaoECpfEAutuador(EntityManager em, 
                                                                       String numAuto, 
                                                                       String codigoInfracao, 
                                                                       String cpf, 
                                                                       Long idAutuador) throws DatabaseException {
        
        Object[] params= {numAuto,
                          codigoInfracao == null? null : codigoInfracao + "%",
                          DetranStringUtil.ehBrancoOuNulo(cpf) ? null : cpf,
                          idAutuador,
                          AtivoEnum.ATIVO,
                          FaseProcessoEnum.CANCELAMENTO_COMUM.getCodigoAteParte2() + "%",
                          FaseProcessoEnum.SUSPENSAO_DO_PROCESSO.getCodigoAteParte2() + "%",
                          FaseProcessoEnum.ARQUIVAMENTO_COMUM.getCodigoAteParte2() + "%"};
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListPAsAtivosSemSuspensaoPorNumAutoECodigoInfracaoECpfEAutuador", params);
    }
    
    /**
     * @param em
     * @param numAuto
     * @param codigoInfracao
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public Object getProcessosAdministrativosPorCpfEAutoECodigoInfracaoComAndamento(EntityManager em, String numAuto, String codigoInfracao, String cpf) throws DatabaseException{
        
        Object[] params= {cpf,
                          codigoInfracao + "%",
                          numAuto,
                          AtivoEnum.ATIVO};
        List<ProcessoAdministrativo> list = getListNamedQuery(em, "ProcessoAdministrativo.getProcessosAdministrativosPorCpfEAutoECodigoInfracaoComAndamento", params);
        return DetranCollectionUtil.ehNuloOuVazio(list)? null : list.get(0);
    }
    
    /**
     * 
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public List getListProcessoAdministrativosAtivosPorCPFEntidadeCompleta(EntityManager em, String cpf) throws DatabaseException{
        List<Integer> lStatus = DetranCollectionUtil.montaLista(PAStatusEnum.ARQUIVADO.getCodigo(), PAStatusEnum.CANCELADO.getCodigo());
        
        Object[] params= {cpf,
                          lStatus,
                          AtivoEnum.ATIVO};
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListProcessoAdministrativosAtivosPorCPFEntidadeCompleta", params);
    }

    /**
     * Consulta de PA's ativos para determinado CPF para validar Examinador.
     *
     * @param em
     * @param numeroDetran
     * @return
     * @throws DatabaseException
     */
    public List getListProcessoAdministrativosAtivosPorNumeroDetranParaExaminador(EntityManager em, Long numeroDetran) throws DatabaseException {
        List<Integer> lStatus = DetranCollectionUtil.montaLista(ATIVO, SUSPENSO);

        Object[] params = {numeroDetran,
                          FaseProcessoEnum.ARQUIVAMENTO_COMUM.getCodigoAteParte2() + "%",
                          FaseProcessoEnum.CANCELAMENTO_COMUM.getCodigoAteParte2() + "%",
                          lStatus,
                          AtivoEnum.ATIVO};

        return getListNamedQuery(em, "ProcessoAdministrativo.getListProcessoAdministrativosAtivosPorNumeroDetranParaExaminador", params);
    }

    /**
     * @param em
     * @param cpf
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List 
        getProcessoAdministrativoSuspensaoAtivoBloqueioCnhMaisAntigo(EntityManager em, String cpf) throws DatabaseException {
        
        List<Integer> lStatus 
            = PAStatusEnum.getStatusNaoPermitidosParaBuscaPAsAtivos();
        
        List<TipoProcessoEnum> lTipoProcesso 
            = DetranCollectionUtil
                .montaLista(
                    TipoProcessoEnum.SUSPENSAO,
                    TipoProcessoEnum.SUSPENSAO_JUDICIAL
                );
        
        List lista = 
            super.getListNamedQuery(
                em, 
                "ProcessoAdministrativo.getProcessoAdministrativoSuspensaoAtivoBloqueioCnhMaisAntigo", 
                AtivoEnum.ATIVO,
                TipoMovimentoBloqueioBCAEnum.BLOQUEIO,
                cpf,
                lStatus,
                lTipoProcesso
            );
        
        return lista;
    }

    /**
     * @param em
     * @param cpf
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public ProcessoAdministrativo getProcessoAdministrativoSuspensaoAtivoPontuacaoUltimo(EntityManager em, String cpf) throws DatabaseException {
        
        List<ProcessoAdministrativo> lista= getProcessoAdministrativoSuspensaoAtivo(cpf, em);
        
        ProcessoAdministrativo processoAdministrativo = null;
        
        if(!DetranCollectionUtil.ehNuloOuVazio(lista)) {
            
            if(Utils.compareToDate(lista.get(0).getDataProcessamento(), Utils.subtraiAno(new Date(), 1)) <= 0) {
                processoAdministrativo = lista.get(0);
            }
        }
        
        return processoAdministrativo;
    }

    public List<ProcessoAdministrativo> getProcessoAdministrativoSuspensaoAtivo(String cpf, EntityManager em) throws DatabaseException {
        Object[] params= {
            cpf,
            PAStatusEnum.getStatusNaoPermitidosParaBuscaPAsAtivos(),
            AtivoEnum.ATIVO,
            DetranCollectionUtil.montaLista(TipoProcessoEnum.SUSPENSAO, TipoProcessoEnum.SUSPENSAO_JUDICIAL),
            MotivoPenalidadeEnum.QTD_PONTOS
        };
        List<ProcessoAdministrativo> lista =
                super.getListNamedQuery(em,
                                        "ProcessoAdministrativo.getProcessoAdministrativoSuspensaoAtivoPontuacaoUltimo",
                                        0,
                                        PRIMEIRO_RESULTADO_CONSULTA,
                                        params);
        return lista;
    }

    /**
     * 
     * @param em
     * @param numeroDetran
     * @param codigoAndamento
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativo> 
        getListaProcessoAdministrativoPorNumeroDetranEAndamentoESituacaoIniciado(EntityManager em, Long numeroDetran, Integer codigoAndamento) throws AppException {
        
        Object[] params = {
            numeroDetran,
            AtivoEnum.ATIVO,
            SituacaoOcorrenciaEnum.INICIADO,
            codigoAndamento
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessoAdministrativoPorNumeroDetranEAndamentoESituacaoIniciado", params);
    }
        
    public List
        getPAControleCnhWrapperPorCpfEAndamentoESituacaoIniciado(
                EntityManager em, String cpf, Integer codigoAndamento) throws AppException {
        
        Object[] params = {
            cpf,
            AtivoEnum.ATIVO,
            SituacaoOcorrenciaEnum.INICIADO,
            codigoAndamento,
            PAParametroEnum.TEMPO_PENALIDADE,
            OrigemEnum.PONTUACAO,
            TipoFasePaEnum.INSTAURACAO,
            RegraInstaurarEnum.C1
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getPAControleCnhWrapperPorCpfEAndamentoESituacaoIniciado", params);
    }
     
    public List
        getPAControleCnhWrapperPJUPorCpfEAndamentoESituacaoIniciado(
                EntityManager em, String cpf, Integer codigoAndamento) throws AppException {
        
        Object[] params = {
            cpf,
            AtivoEnum.ATIVO,
            SituacaoOcorrenciaEnum.INICIADO,
            codigoAndamento,
            PAParametroEnum.TEMPO_PENALIDADE
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getPAControleCnhWrapperPJUPorCpfEAndamentoESituacaoIniciado", params);
    }
     
    /**
     * 
     * @param em
     * @param cpf
     * @param codigoFluxo
     * @return
     * @throws AppException 
     */
    public List getPAControleCnhWrapperPorFluxoECpf(EntityManager em, String cpf, Integer codigoFluxo) throws AppException {
        
        if (DetranStringUtil.ehBrancoOuNulo(cpf) || codigoFluxo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        return getListNamedQuery(em, 
                                 "ProcessoAdministrativo.getPAControleCnhWrapperPorFluxoECpf", 
                                 cpf, 
                                 codigoFluxo, 
                                 AtivoEnum.ATIVO, 
                                 PAParametroEnum.TEMPO_PENALIDADE, TipoFasePaEnum.INSTAURACAO, RegraInstaurarEnum.C1);
    }
    
    public Boolean validarPAAptoIniciarFluxo(EntityManager em, Long idPA, Integer codigoFluxo) throws AppException {
        
        if (idPA == null || codigoFluxo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        return getNamedQuery(em, 
                                 "ProcessoAdministrativo.validarPAAptoIniciarFluxo", 
                                 idPA, 
                                 codigoFluxo, 
                                 AtivoEnum.ATIVO) != null;
    }
        
    /**
     * 
     * @param em
     * @param andamentos
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativo> 
        getListaProcessoAdministrativoPorAndamentoIniciado(EntityManager em, List<Integer> andamentos) throws AppException {
        
        if(DetranCollectionUtil.ehNuloOuVazio(andamentos)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
            
        Object[] params = {
            AtivoEnum.ATIVO,
            SituacaoOcorrenciaEnum.INICIADO,
            andamentos
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamentoIniciado", params);
    }
        
    /**
     * 
     * @param em
     * @param andamentos
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativo
        getProcessoAdministrativoPorNumeroEAndamentoEIniciado(EntityManager em, String numeroProcesso, List<Integer> andamentos) throws AppException {
        
        if(DetranCollectionUtil.ehNuloOuVazio(andamentos) || DetranStringUtil.ehBrancoOuNulo(numeroProcesso)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
            
        Object[] params = {
            AtivoEnum.ATIVO,
            SituacaoOcorrenciaEnum.INICIADO,
            andamentos,
            numeroProcesso
        };
        
        List<ProcessoAdministrativo> lProcessoAdministrativo 
            = getListNamedQuery(em, "ProcessoAdministrativo.getProcessoAdministrativoPorNumeroEAndamentoEIniciado", params);
        
        if(!DetranCollectionUtil.ehNuloOuVazio(lProcessoAdministrativo) && lProcessoAdministrativo.size() > 1) {
            throw new AppException("Processo Administrativo inválido.");
        }
        
        return !DetranCollectionUtil.ehNuloOuVazio(lProcessoAdministrativo) ? lProcessoAdministrativo.get(0) : null;
    }

    /**
     * 
     * @param em
     * @param numeroAuto
     * @param codigoInfracao
     * @param cpf
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List getListPAsFaseArquivamentoPorNumAutoECodigoInfracaoECpf(EntityManager em, 
                                                                       String numeroAuto, 
                                                                       String codigoInfracao, 
                                                                       String cpf) throws DatabaseException {
        Object[] params= {numeroAuto,
                          codigoInfracao + "%",
                          cpf,
                          FaseProcessoEnum.ARQUIVAMENTO_COMUM.getCodigoAteParte2() + "%"};
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListPAsFaseArquivamentoPorNumAutoECodigoInfracaoECpf", params);
    }

    /**
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public List getListPAsAtivosBloqueio(EntityManager em, String cpf) throws DatabaseException {
        return getListNamedQuery(em, "ProcessoAdministrativo.getListPAsAtivosBloqueio", cpf);
    }

    /**
     * 
     * @param em
     * @param cpf
     * @return
     * @throws AppException 
     */
    public Boolean existeMaisDeUmProcessoAdministrativoParaCondutor(EntityManager em, String cpf) throws AppException {
        
        if(DetranStringUtil.ehBrancoOuNulo(cpf)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<ProcessoAdministrativo> processos
            = super.getListNamedQuery(
                em, 
                "ProcessoAdministrativo.existeMaisDeUmProcessoAdministrativoParaCondutor", 
                cpf,
                PAStatusEnum.ATIVO.getCodigo(), 
                AtivoEnum.ATIVO
            );
        
        return !DetranCollectionUtil.ehNuloOuVazio(processos) && processos.size() > 1;
    }

    public void checaSeExisteAlgumProcessoAdministrativoQueInvalidaExecutarDesbloqueioCnh(EntityManager em, Long numeroDetran) {
        //FIXME ???
    }

    /**
     * 
     * @param em
     * @param numeroDetran
     * @throws AppException 
     */
    public void validaSeExisteProcessoAdministrativoValidoParaExecutarDesbloqueioCnh(EntityManager em, Long numeroDetran) throws AppException {
        
        if(numeroDetran == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<ProcessoAdministrativo> lProcessoValido 
            = super.getListNamedQuery(em, 
                "ProcessoAdministrativo.validaSeExisteProcessoAdministrativoValidoParaExecutarDesbloqueioCnh", 
                numeroDetran,
                AtivoEnum.ATIVO,
                PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_PROTOCOLO_DEVOLUCAO_CNH
            );
        
        if(DetranCollectionUtil.ehNuloOuVazio(lProcessoValido)) {
            DetranWebUtils.applicationMessageException("desbloqueioCnh.M1");
        }
    }

    /**
     * 
     * @param em
     * @param numeroDocumento
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativoRecursoWrapper> 
        getListProcessoAdministrativoPermitidosAbrirRecursoPorCPFCondutor(EntityManager em, String numeroDocumento) throws AppException {

        if(DetranStringUtil.ehBrancoOuNulo(numeroDocumento)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<ProcessoAdministrativoRecursoWrapper> listaProcessosPermitidos = new ArrayList();
            
        List<ProcessoAdministrativo> processos 
            = getListProcessoAdministrativosAtivosPorCPFEntidadeCompleta(em, numeroDocumento);

        if (DetranCollectionUtil.ehNuloOuVazio(processos)) {
            DetranWebUtils.applicationMessageException("recursoPa.validar.aberturarecurso.M1");
        }

        for (ProcessoAdministrativo processoAdministrativo : processos) {

            ProcessoAdministrativoRecursoWrapper aberturaRecurso = validarProcessoAptoAberturaRecurso(em, processoAdministrativo);

            if(aberturaRecurso != null)
                listaProcessosPermitidos.add(aberturaRecurso);
        }

        if (DetranCollectionUtil.ehNuloOuVazio(listaProcessosPermitidos)) {
            DetranWebUtils.applicationMessageException("recursoPa.validar.aberturarecurso.M1");
        }

        return listaProcessosPermitidos;
    }

    public ProcessoAdministrativoRecursoWrapper validarProcessoAptoAberturaRecurso(EntityManager em, ProcessoAdministrativo processoAdministrativo) {

        try {

            PAFluxoFase fluxoFase =
                    new PAFluxoFaseRepositorio()
                            .getFluxoFaseDoProcessoAdministrativo(em,
                                    processoAdministrativo
                            );

            TipoFasePaEnum tipoRecurso
                    = new RecursoBO().defineTipoRecurso(em, processoAdministrativo, fluxoFase);

            if (tipoRecurso != null) {

                validarRecurso(em, processoAdministrativo, tipoRecurso);

                OrigemDestinoEnum origemDestino
                        = new DestinoFaseBO().getDefineOrigemDestinoParaProcessoAdministrativo(em, processoAdministrativo, tipoRecurso);

                if (origemDestino != null) {

                    DestinoFase destinoFase
                            = new DestinoFaseRepositorio().getDestinoFaseParaAberturaRecurso(em, processoAdministrativo, fluxoFase, origemDestino);

                    if (destinoFase != null) {

                        ProcessoAdministrativoRecursoWrapper processoAdministrativoRecursoWrapper
                                = new ProcessoAdministrativoRecursoWrapper(processoAdministrativo, origemDestino);

                        processoAdministrativoRecursoWrapper.setTipoRecurso(tipoRecurso);

                        return processoAdministrativoRecursoWrapper;
                    }
                }
            }

        } catch (AppException e) {
            LOG.info("Processo Administrativo não está APTO para abertura de Recurso.", e);
        }
        return null;
    }

    /**
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException
     */
    public List<ProcessoAdministrativo>
    getListProcessoAdministrativosAtivosPorCPFParaAberturaRecurso(EntityManager em, String cpf) throws DatabaseException {
        
        List<Integer> lStatus = DetranCollectionUtil.montaLista(PAStatusEnum.ARQUIVADO.getCodigo(), PAStatusEnum.CANCELADO.getCodigo());
        
        Object[] params= {cpf, lStatus, AtivoEnum.ATIVO};
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListProcessoAdministrativosAtivosPorCPFParaAberturaRecurso", params);
    }
        
    /**
     * @param em
     * @param cpf
     * @param codigoAndamento
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativo> getListaProcessoAdministrativoPorCpfEAndamento(EntityManager em,
                                                                                       String cpf, 
                                                                                       Integer codigoAndamento) throws AppException {
        
        Object[] params = {
            cpf,
            AtivoEnum.ATIVO,
            codigoAndamento
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessoAdministrativoPorCpfEAndamento", params);
    }

    /**
     * Retorna lista de Processoas Administrativos buscados por CPF que não estão nas fases de Arquivamento e Cancelamento
     * @param em
     * @param cpf
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List<ProcessoAdministrativo> getListProcessoAdministrativoPorCpfSemFaseArquivamentoECancelamento(EntityManager em, 
                                                                                                            String cpf) throws DatabaseException {
        
        Object[] params = {cpf,
                          FaseProcessoEnum.ARQUIVAMENTO_COMUM.getCodigoAteParte2() + "%",
                          FaseProcessoEnum.CANCELAMENTO_COMUM.getCodigoAteParte2() + "%",
                          AtivoEnum.ATIVO};

        return getListNamedQuery(em, 
                                 "ProcessoAdministrativo.getListProcessoAdministrativoPorCpfSemFaseArquivamentoECancelamento", 
                                 params);
    }
    
    /**
     * 
     * @param em
     * @param andamentos
     * @return
     * @throws AppException 
     */
    public List 
        getListaProcessoAdministrativoPorAndamentoIniciadoParaBCA(EntityManager em, List<Integer> andamentos) throws AppException {
        
        if(DetranCollectionUtil.ehNuloOuVazio(andamentos)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
            
        Object[] parametros = {
            AtivoEnum.ATIVO,
            SituacaoOcorrenciaEnum.INICIADO,
            andamentos
        };
        
        return 
            getListNamedQuery(
                em, 
                "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamentoIniciadoParaBCA", 
                parametros
            );
    }
        
    /**
     * 
     * @param em
     * @param cpf
     * @param idProcessoAdministrativo
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativoBCAWrapper getProcessoAdministrativoParaBCAPorCPFeProcessoAdministrativo(EntityManager em, String cpf, Long idProcessoAdministrativo) throws AppException {
        
        Object[] parametros = {
            AtivoEnum.ATIVO,
            SituacaoOcorrenciaEnum.INICIADO,
            cpf,
            idProcessoAdministrativo
        };
        
        List lProcesso = 
            getListNamedQuery(
                em, 
                "ProcessoAdministrativo.getProcessoAdministrativoParaBCAPorCPFeProcessoAdministrativo", 
                parametros
            );
        
        return !DetranCollectionUtil.ehNuloOuVazio(lProcesso) ? (ProcessoAdministrativoBCAWrapper) lProcesso.get(0) : null;
    }
    
    /**
     * 
     * @param em
     * @param andamentos
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativo> 
        getListaProcessoAdministrativoPorAndamento(EntityManager em, List<Integer> andamentos) throws AppException {
        
        if(DetranCollectionUtil.ehNuloOuVazio(andamentos)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
            
        Object[] params = {
            AtivoEnum.ATIVO,
            andamentos
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamento", params);
    }
        
    /**
     * 
     * @param em
     * @param andamentos
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativo 
        getProcessoAdministrativoPorAndamentoEIdProcessoAdministrativo(EntityManager em, List<Integer> andamentos, Long idProcessoAdministrativo) throws AppException {
        
        if(DetranCollectionUtil.ehNuloOuVazio(andamentos)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
            
        Object[] params = {
            AtivoEnum.ATIVO,
            andamentos,
            idProcessoAdministrativo
        };
        
        return getNamedQuery(em, "ProcessoAdministrativo.getProcessoAdministrativoPorAndamentoEIdProcessoAdministrativo", params);
    }

    /**
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public List<ProcessoAdministrativo> getListaProcessosAdministrativosAtivosOuSuspensosPorCpfParaAEMNPP06(EntityManager em, String cpf) throws DatabaseException {
        
        List<Integer> lStatus = DetranCollectionUtil.montaLista(PAStatusEnum.ATIVO.getCodigo(), 
                                                                PAStatusEnum.SUSPENSO.getCodigo());
        
        Object[] params= {cpf,
                          lStatus,
                          AtivoEnum.ATIVO};
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessosAdministrativosAtivosOuSuspensosPorCpfParaAEMNPP06", params);
    }
    
    /**
     * 
     * @param em
     * @param idProcessoAdministrativo
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativo 
        getProcessoAdministrativoPorId(EntityManager em, Long idProcessoAdministrativo) throws AppException {
        
        if(idProcessoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
            
        return 
            getNamedQuery(
                em, 
                "ProcessoAdministrativo.getProcessoAdministrativoPorId", 
                idProcessoAdministrativo
            );
    }
        
    /**
     * 
     * @param em
     * @param andamento
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativo
        getProcessoAdministrativoAtivoPorPAAndamentoCodigo(EntityManager em, PAAndamentoEnum andamento) throws AppException {
        
        if(andamento == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
            
        Object[] params = {
            AtivoEnum.ATIVO,
            andamento.getCodigo()
        };
        
        List<ProcessoAdministrativo> lProcessoAdministrativo 
            = getListNamedQuery(
                em, 
                "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamento", 
                params
            );

        return DetranCollectionUtil.ehNuloOuVazio(lProcessoAdministrativo)? null : lProcessoAdministrativo.get(0);
    }

    /**
     * @param em
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List<ProcessoAdministrativo> getProcessosAdministrativosAndamento48paraCnhSituacaoEntrega(EntityManager em) throws DatabaseException {
        
        Object[] params= {
            AtivoEnum.ATIVO.ordinal(),
            AcaoEntregaCnhEnum.ENTREGA.ordinal(),
            AcaoEntregaCnhEnum.DEVOLUCAO.ordinal(),
            PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_ENTREGA_CNH,
            SituacaoOcorrenciaEnum.INICIADO.name()
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getProcessosAdministrativosAndamento48paraCnhSituacaoEntrega", params );
    }
    
    public List<ProcessoAdministrativo> buscarProcessosAdministrativosNoAndamento48ComPenaCumprida(EntityManager em) throws DatabaseException {
        
        Object[] params= {
            PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_ENTREGA_CNH,
            AtivoEnum.ATIVO,
            Calendar.getInstance().getTime()
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.buscarProcessosAdministrativosNoAndamento48ComPenaCumprida", params );
    }
        
    public List<ProcessoAdministrativo> getProcessoAdministrativoPorIDApoioOrigemInstauraucao(
        EntityManager em, Long id) throws DatabaseException {
        return super.getListNamedQuery(em, 
          "ProcessoAdministrativo.getProcessoAdministrativoPorIDApoioOrigemInstauraucao", 
                id, AtivoEnum.ATIVO);
    }

    /**
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public List<ProcessoAdministrativo> getProcessosAdministrativosParaProcessoJuridico(EntityManager em, String cpf) throws DatabaseException {
        return super.getListNamedQuery(em, "ProcessoAdministrativo.getProcessosAdministrativosParaProcessoJuridico", cpf);
    }

    public List<ProcessoAdministrativo> getProcessosAdministrativosCassacaoComBloqueio(EntityManager em) throws DatabaseException {
        Object[] params= {
            DetranCollectionUtil.montaLista(TipoProcessoEnum.CASSACAO,
                                            TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH),
            SituacaoBloqueioBCAEnum.ATIVO,
            AtivoEnum.ATIVO,
            PAParametroEnum.ARQUIVAMENTO_PROCESSADO
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getProcessosAdministrativosCassacaoComBloqueio", params );
    }

    public List<ProcessoAdministrativo> getPASuspensaoSemPenalidadePorCPF(EntityManager em, String cpf) throws DatabaseException {
        Object[] params= {
            TipoProcessoEnum.SUSPENSAO,
            cpf,
            AtivoEnum.ATIVO,
            DetranCollectionUtil.montaLista(PAStatusEnum.ATIVO.getCodigo(),
                                            PAStatusEnum.SUSPENSO.getCodigo()),
            FaseProcessoEnum.ARQUIVAMENTO_COMUM.getCodigoAteParte2() + "%",
            FaseProcessoEnum.CANCELAMENTO_COMUM.getCodigoAteParte2() + "%",
            PAAndamentoProcessoConstante.ESPECIFICO_CORRECAO.ARQUIVAR_PROCESSO_PRESCRITO
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getPASuspensaoSemPenalidadePorCPF", params );
    }

    public List<ProcessoAdministrativo> getListaProcessoJuridicoCandidatoCidadaoPorAndamento(EntityManager em, List<Integer> andamentos) throws AppException {
        if(DetranCollectionUtil.ehNuloOuVazio(andamentos)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
            
        Object[] params = {
            AtivoEnum.ATIVO,
            andamentos,
            ParteProcessoJuridicoEnum.CONDUTOR
        };
        
        return getListNamedQuery(em, "DadoProcessoJudicial.getListaProcessoJuridicoCandidatoCidadaoPorAndamento", params);
    }

    private void validarRecurso(EntityManager em, ProcessoAdministrativo processoAdministrativo, TipoFasePaEnum tipoRecurso) throws AppException {
        if (TipoFasePaEnum.INSTAURACAO.equals(tipoRecurso)) {

            List<ResultadoRecurso> listRecursos = new ResultadoRecursoRepositorio().
                    getListResultadoRecursoPorProcessoAdministrativoESituacaoEDestino(em,
                                                                                      processoAdministrativo.getId(),
                                                                                      OrigemDestinoEnum.SEPEN,
                                                                                      SituacaoRecursoEnum.JULGADO);
            for (ResultadoRecurso resultado : listRecursos) {
                if (resultado != null && !ResultadoRecursoEnum.IRREGULAR.equals(resultado.getResultado())) {
                    DetranWebUtils.applicationMessageException("Condutor já possui recurso para esta fase.");
                }
            }
            if (RegraInstaurarEnum.C1.equals(processoAdministrativo.getOrigemApoio().getRegra())) {
                listRecursos = new ResultadoRecursoRepositorio().
                        getListResultadoRecursoPorProcessoAdministrativoESituacaoEDestino(em,
                                                                                          processoAdministrativo.getId(),
                                                                                          OrigemDestinoEnum.JARI,
                                                                                          SituacaoRecursoEnum.JULGADO);
                if (!DetranCollectionUtil.ehNuloOuVazio(listRecursos)) {
                    DetranWebUtils.applicationMessageException("Condutor já possui recurso para esta fase.");
                }
            }
        }
    }

    public List getListaProcessoAdministrativoPorAndamentoEmLote(EntityManager em, List<Integer> andamentos, Integer qtdPA) throws AppException{
        if(DetranCollectionUtil.ehNuloOuVazio(andamentos)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
            
        Object[] params = {
            AtivoEnum.ATIVO,
            andamentos
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamento", 0, qtdPA, params);
    }
    
    /**
     * 
     * @param em
     * @param andamentos
     * @param qtdPA
     * @param tipoProcesso
     * @return
     * @throws AppException 
     */
    public List getListaProcessoAdministrativoPorAndamentoETipoProcessoEmLote(EntityManager em, List<Integer> andamentos, Integer qtdPA, TipoProcessoEnum tipoProcesso) throws AppException{
        
        if(DetranCollectionUtil.ehNuloOuVazio(andamentos)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        Object[] params = {
            AtivoEnum.ATIVO,
            andamentos,
            tipoProcesso != null ? tipoProcesso.ordinal() : null
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamentoETipoProcessoEmLote", 0, qtdPA, params);
    }

    public List getProcessosSuspensaoComBloqueioPorCPF(EntityManager em, String cpf) throws DatabaseException {
        
        Object[] params = {
            cpf,
            PAStatusEnum.ATIVO.getCodigo(),
            DetranCollectionUtil.montaLista(TipoProcessoEnum.SUSPENSAO, TipoProcessoEnum.SUSPENSAO_JUDICIAL),
            SituacaoBloqueioBCAEnum.ATIVO,
            AtivoEnum.ATIVO
        };

        return getListNamedQuery(em, "ProcessoAdministrativo.getProcessosSuspensaoComBloqueioPorCPF", params);
    }

    public List getProcessosCassacaoPorCPF(EntityManager em, String cpf) throws DatabaseException {
        
        Object[] params = {
            cpf,
            PAStatusEnum.ATIVO.getCodigo(),
            DetranCollectionUtil.montaLista(TipoProcessoEnum.CASSACAO, TipoProcessoEnum.CASSACAO_JUDICIAL, TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH)
        };
        
        return getListNamedQuery(em, "ProcessoAdministrativo.getProcessosCassacaoPorCPF", params);
    }
    
      public List verificaSeExisteProcessoAdmPorNumeroAutoEOraoAutuador(EntityManager em, Long orgaoAutuador, String numeroAuto) throws DatabaseException {
        
        Object[] params = {
            numeroAuto,
            orgaoAutuador
        };

        return getListNamedQuery(em, "ProcessoAdministrativo.verificaSeExisteProcessoAdmPorNumeroAutoEOraoAutuador", params);
    }
      
      public List<ProcessoAdministrativo> getListProcessosComPrazoNotificacaoExpirado(
        EntityManager em) throws AppException {
        
          List codigosAndamentos = DetranCollectionUtil.montaLista(PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_INSTAURACAO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_PENALIZACAO,
                    PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_ENTREGA_CNH,
                    PAAndamentoProcessoConstante.NOTIFICACAO_JARI.AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_JARI,
                    PAAndamentoProcessoConstante.NOTIFICACAO_CETRAN.AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_CETRAN
                );
        
        return 
            getListNamedQuery(
                    em,
                    "ProcessoAdministrativo.getListProcessosComPrazoNotificacaoExpirado",
                    codigosAndamentos,
                    SituacaoOcorrenciaEnum.INICIADO,
                    AtivoEnum.ATIVO,
                    Calendar.getInstance().getTime(),
                    RecursoSituacaoPAEnum.BACKOFFICE
            );
    }
      
    public List<ProcessoAdministrativo> getProcessosAndamento140ComPenaCumpridaParaWSEntregaCNH(EntityManager em) throws DatabaseException{
        return getListNamedQuery(em, "ProcessoAdministrativo.getProcessosAndamento140ComPenaCumpridaParaWSEntregaCNH", new Object[]{});
    }

    public List<ProcessoAdministrativo> getListaProcessoAdministrativoPorAndamentoETipo(EntityManager em, List<Integer> andamentos, List<TipoProcessoEnum> tipos)
            throws DatabaseException {

        Object[] params = {
            AtivoEnum.ATIVO,
            SituacaoOcorrenciaEnum.INICIADO,
            andamentos,
            tipos
        };

        return getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamentoETipo", params);
    }
    
    public List<ProcessoAdministrativo> getListProcessosAdministrativosAptosIncluirBloqueioAndamento43(EntityManager em) throws DatabaseException {
        
        List codigosAndamentos = DetranCollectionUtil.montaLista(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.INCLUIR_BLOQUEIO_WEB);
        
        
        List<ProcessoAdministrativo> pasSuspensaoCassacao43 = 
                getListaProcessoAdministrativoPorAndamentoETipo(em, 
                                                                codigosAndamentos,
                                                                DetranCollectionUtil.montaLista(TipoProcessoEnum.CASSACAO, TipoProcessoEnum.SUSPENSAO));
        
        List<ProcessoAdministrativo>pjusSuspensaoCassacao43 = 
                getListaProcessosJuridicosPorAndamentoTipoERecolhimentoCnh(em,
                                                                           codigosAndamentos,
                                                                           DetranCollectionUtil.montaLista(
                                                                                   TipoProcessoEnum.CASSACAO_JUDICIAL, 
                                                                                   TipoProcessoEnum.SUSPENSAO_JUDICIAL),
                                                                           IdentificacaoRecolhimentoCnhEnum.DETRAN
                );
        List<ProcessoAdministrativo> processos = new ArrayList<>();
        processos.addAll(pasSuspensaoCassacao43);
        processos.addAll(pjusSuspensaoCassacao43);
        
        return processos;
    }
    
    public List<ProcessoAdministrativo> getListProcessosAdministrativosAptosIncluirBloqueioAndamento183(EntityManager em) throws DatabaseException {
        
        List<ProcessoAdministrativo> pasSuspensaoCassacao183 = 
                getListaProcessoAdministrativoPorAndamentoTipoETipoNotificacao(em, 
                                                                PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.ATUALIZAR_BLOQUEIO_WEB,
                                                                DetranCollectionUtil.montaLista(TipoProcessoEnum.CASSACAO, TipoProcessoEnum.SUSPENSAO),
                                                                TipoFasePaEnum.DESENTRANHAMENTO);
        
        List<ProcessoAdministrativo>pjusSuspensaoCassacao183 = 
                getListaProcessosJuridicosPorAndamentoTipoESemPenalidade(em,
                                                                           PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.ATUALIZAR_BLOQUEIO_WEB,
                                                                           DetranCollectionUtil.montaLista(
                                                                                   TipoProcessoEnum.CASSACAO_JUDICIAL, 
                                                                                   TipoProcessoEnum.SUSPENSAO_JUDICIAL));
        List<ProcessoAdministrativo> processos = new ArrayList<>();
        processos.addAll(pasSuspensaoCassacao183);
        processos.addAll(pjusSuspensaoCassacao183);
        
        return processos;
    }

    private List<ProcessoAdministrativo> getListaProcessosJuridicosPorAndamentoTipoERecolhimentoCnh(
            EntityManager em, 
            List codigosAndamentos, 
            List<TipoProcessoEnum> tipos, 
            IdentificacaoRecolhimentoCnhEnum recolhimentoCNH) throws DatabaseException {
        
        Object[] params = {
            AtivoEnum.ATIVO,
            SituacaoOcorrenciaEnum.INICIADO,
            codigosAndamentos,
            tipos,
            recolhimentoCNH
        };

        return getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessosJuridicosPorAndamentoTipoERecolhimentoCnh", params);
    }

    private List<ProcessoAdministrativo> getListaProcessosJuridicosPorAndamentoTipoESemPenalidade(
            EntityManager em, 
            Integer codigoAndamento, 
            List<TipoProcessoEnum> tipos) throws DatabaseException {
        Object[] params = {
            AtivoEnum.ATIVO,
            SituacaoOcorrenciaEnum.INICIADO,
            codigoAndamento,
            tipos
        };

        return getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessosJuridicosPorAndamentoTipoESemPenalidade", params);
    }

    private List<ProcessoAdministrativo> getListaProcessoAdministrativoPorAndamentoTipoETipoNotificacao(
            EntityManager em, 
            Integer codigoAndamento, 
            List<TipoProcessoEnum> tipos, 
            TipoFasePaEnum tipoNotificacao) throws DatabaseException {
        Object[] params = {
            AtivoEnum.ATIVO,
            SituacaoOcorrenciaEnum.INICIADO,
            codigoAndamento,
            tipos,
            tipoNotificacao
        };

        return getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessoAdministrativoPorAndamentoTipoETipoNotificacao", params);
    }
    
    public List<ProcessoAdministrativo> getListaProcessosFilho(EntityManager em, Long idProcessoAdministrativo) throws DatabaseException {
        Object[] params = {
            idProcessoAdministrativo
        };

        return getListNamedQuery(em, "ProcessoAdministrativo.getListaProcessosFilho", params);
    }
    
}
