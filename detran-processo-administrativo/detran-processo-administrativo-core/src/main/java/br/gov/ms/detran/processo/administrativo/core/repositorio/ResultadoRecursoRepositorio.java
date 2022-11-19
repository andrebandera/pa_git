package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoRecurso;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoNotificacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.ResultadoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import java.util.List;
import javax.persistence.EntityManager;

public class ResultadoRecursoRepositorio extends AbstractJpaDAORepository<ResultadoRecurso> {

    /**
     * 
     * @param em
     * @param idProcessoAdministrativo
     * @param lResultadoRecursoEnum
     * @return
     * @throws AppException 
     */
    public ResultadoRecurso getResultadoRecursoAtivoPorProcessoAdministrativoEResultado(
        EntityManager em, Long idProcessoAdministrativo, List<ResultadoRecursoEnum> lResultadoRecursoEnum) throws AppException {
        
        if(idProcessoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<ResultadoRecurso> lResultadoRecurso
            = super.getListNamedQuery(
                em, 
                "ResultadoRecurso.getResultadoRecursoAtivoPorProcessoAdministrativoEResultado", 
                idProcessoAdministrativo,
                AtivoEnum.ATIVO,
                lResultadoRecursoEnum
            );
        
        return !DetranCollectionUtil.ehNuloOuVazio(lResultadoRecurso) ? lResultadoRecurso.get(0) : null;
    }

    /**
     * @param em
     * @param idRecurso
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public ResultadoRecurso getResultadoRecursoAtivoPorRecurso(EntityManager em, Long idRecurso) throws AppException {
        
        if(idRecurso == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return super.getNamedQuery(em, "ResultadoRecurso.getResultadoRecursoAtivoPorRecurso", idRecurso, AtivoEnum.ATIVO);
    }
    
    public ResultadoRecurso getResultadoRecursoPorProcessoAdministrativoESituacaoEDestino(EntityManager em, Long idPA, SituacaoRecursoEnum situacao, OrigemDestinoEnum origem) throws DatabaseException, AppException {
        
        List<ResultadoRecurso> lista = getListResultadoRecursoPorProcessoAdministrativoESituacaoEDestino(em, idPA, origem, situacao);
        
        if(DetranCollectionUtil.ehNuloOuVazio(lista) || lista.size() > 1){
            DetranWebUtils.applicationMessageException("Não é possível gravar este recurso.Favor entrar em contato com a DIRTI.");
        }
        return lista.get(0);
    }

    public List<ResultadoRecurso> getListResultadoRecursoPorProcessoAdministrativoESituacaoEDestino(EntityManager em, Long idPA, OrigemDestinoEnum origem, SituacaoRecursoEnum situacao) throws DatabaseException {
        Object[] params = {
            idPA,
                           origem,
                           situacao,
                           AtivoEnum.ATIVO
        };
        List<ResultadoRecurso> lista = getListNamedQuery(em, "ResultadoRecurso.getResultadoRecursoPorProcessoAdministrativoESituacaoEDestino", params);
        return lista;
    }

    public ResultadoRecurso getResultadoRecursoPorProcessoEResultadoEAcao(EntityManager em, Long idPA, ResultadoRecursoEnum resultadoRecursoEnum, RecursoNotificacaoAcaoEnum recursoNotificacaoAcaoEnum) throws DatabaseException {
        
        Object[] params = {
                            idPA, 
                            resultadoRecursoEnum, 
                            recursoNotificacaoAcaoEnum, 
                            AtivoEnum.ATIVO
                          };
        
        return getNamedQuery(em, "ResultadoRecurso.getResultadoRecursoPorProcessoEResultadoEAcao", params);
    }
}