package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.entidade.Recurso;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import java.util.List;
import javax.persistence.EntityManager;

public class RecursoMovimentoRepositorio extends AbstractJpaDAORepository<RecursoMovimento> {
    
    /**
     * 
     * @param em
     * @param numeroProtocolo
     * @param numeroProcessoAdministrativo
     * @return
     * @throws AppException 
     */
    public RecursoMovimento getRecursoMovimentoPorNumeroProtocoloENumeroProcessoAdministrativo(
            EntityManager em, String numeroProtocolo, String numeroProcessoAdministrativo) throws AppException {
        
        if (DetranStringUtil.ehBrancoOuNulo(numeroProtocolo)
                || DetranStringUtil.ehBrancoOuNulo(numeroProcessoAdministrativo)) {
            
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<RecursoMovimento> lRecursoMovimento = 
                getListNamedQuery(
                        em, 
                        "RecursoMovimento.getRecursoMovimentoPorNumeroProtocoloENumeroProcessoAdministrativo", 
                        numeroProcessoAdministrativo,
                        numeroProtocolo, 
                        AtivoEnum.ATIVO);
        
        if (DetranCollectionUtil.ehNuloOuVazio(lRecursoMovimento) || lRecursoMovimento.size() > 1) {
            DetranWebUtils.applicationMessageException("PA.M1");
        }
        
        return lRecursoMovimento.get(0);
    }

    /**
     * @param em
     * @param recursoId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
        public RecursoMovimento getMovimentoRecursoPorRecursoID(EntityManager em, Long recursoId) throws DatabaseException {
        
            List<RecursoMovimento> movimentos = super.getListNamedQuery(em, "RecursoMovimento.getMovimentoRecursoPorRecursoID", recursoId, AtivoEnum.ATIVO);
            return DetranCollectionUtil.ehNuloOuVazio(movimentos) ? null : movimentos.get(0);
    }

    /**
     * @param em
     * @param recursoId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public BooleanEnum getIndiceForaPrazo(EntityManager em, Long recursoId) throws AppException {
        List<RecursoMovimento> movimentos = super.getListNamedQuery(em, "RecursoMovimento.getIndiceForaPrazo", recursoId, AtivoEnum.ATIVO);

        return DetranCollectionUtil.ehNuloOuVazio(movimentos) ? null : movimentos.get(0).getIndiceForaPrazo();
    }
    
    /**
     * 
     * @param em
     * @param recurso
     * @return
     * @throws AppException 
     */
    public RecursoMovimento getRecursoMovimentoPorRecurso(EntityManager em, Recurso recurso) throws AppException {
        
        if(recurso == null || recurso.getId() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<RecursoMovimento> lRecursoMovimento  = 
                getListNamedQuery(em, "RecursoMovimento.getRecursoMovimentoPorRecurso", recurso.getId(), AtivoEnum.ATIVO);
        
        return !DetranCollectionUtil.ehNuloOuVazio(lRecursoMovimento) ? lRecursoMovimento.get(0) : null;
    }
    
    /**
     * @param em
     * @param codigosAndamentos
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public List<RecursoMovimento> getRecursosPorAndamentosESituacao(EntityManager em, List<Integer> codigosAndamentos) throws AppException {
        
        if(DetranCollectionUtil.ehNuloOuVazio(codigosAndamentos)) {
            DetranWebUtils.applicationMessageException("Parâmetro obrigatório inválido.");
        }
        
        List<RecursoMovimento> lRecurso 
            = getListNamedQuery(
                em, 
                "RecursoMovimento.getRecursos", 
                codigosAndamentos,
                SituacaoOcorrenciaEnum.INICIADO,
                AtivoEnum.ATIVO,
                SituacaoRecursoEnum.EM_ANALISE
            );
        
        return lRecurso;
    }

    /**
     * 
     * @param em
     * @return
     * @throws AppException 
     */
    public List<RecursoMovimento> getRecursosCancelados(EntityManager em) throws AppException {
        
        List<Integer> codigosAndamentos = DetranCollectionUtil
            .montaLista(
                PAAndamentoProcessoConstante.CANCELAMENTO_RECURSO.CONFIRMAR_CANCELAMENTO_RECURSO_NOTIFICACAO_ENTREGA_CNH,
                PAAndamentoProcessoConstante.CANCELAMENTO_RECURSO.CONFIRMAR_CANCELAMENTO_RECURSO_NOTIFICACAO_INSTAURACAO,
                PAAndamentoProcessoConstante.CANCELAMENTO_RECURSO.CONFIRMAR_CANCELAMENTO_RECURSO_NOTIFICACAO_PENALIZACAO,
                PAAndamentoProcessoConstante.CANCELAMENTO_RECURSO.CANCELAMENTO_RECURSO_NOT_DESENTRANHAMENTO_BPMS
            );
        
        List<RecursoMovimento> lRecurso 
            = getListNamedQuery(
                em, 
                "RecursoMovimento.getRecursosCancelados", 
                codigosAndamentos,
                SituacaoOcorrenciaEnum.INICIADO,
                AtivoEnum.ATIVO,
                SituacaoRecursoEnum.CANCELADO
            );
        
        return lRecurso;
    }

    /**
     * 
     * @param em
     * @param idPA
     * @return
     * @throws AppException 
     */
    public List<RecursoMovimento> getRecursosCanceladosPorPA(EntityManager em, Long idPA) throws AppException {
        
        List<RecursoMovimento> lRecurso 
            = getListNamedQuery(
                em, 
                "RecursoMovimento.getRecursosCanceladosPorPA", 
                idPA,
                SituacaoRecursoEnum.CANCELADO,
                AtivoEnum.ATIVO
            );
        
        return lRecurso;
    }

    /**
     * 
     * @param em
     * @param recurso
     * @return
     * @throws DatabaseException 
     */
    public List<RecursoMovimento> getRecursoMovimentoPorRecursoETipoProtocolo(
        EntityManager em, Recurso recurso) throws DatabaseException {
        
        List<RecursoMovimento> lista = 
            super.getListNamedQuery(
                em, 
                "RecursoMovimento.getRecursoMovimentoPorRecursoETipoProtocolo", 
                recurso.getId()
            );
        
        return lista;
    }

    public List<RecursoMovimento> getRecursosPorPA(EntityManager em, Long idPA) throws DatabaseException {
        return super.getListNamedQuery(em, 
                                   "RecursoMovimento.getRecursosPorPA", 
                                   idPA, 
                                   AtivoEnum.ATIVO);
    }
    
    /**
     * @param em
     * @param numeroProcesso
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public RecursoMovimento getRecursoParaCancelamentoPorCadastroIndevido(EntityManager em, String numeroProcesso) throws DatabaseException {
        return super.getNamedQuery(em, 
                                   "RecursoMovimento.getRecursoParaCancelamentoPorCadastroIndevido", 
                                   numeroProcesso, 
                                   TipoSituacaoProtocoloEnum.CANCELAMENTO.name());
    }
}