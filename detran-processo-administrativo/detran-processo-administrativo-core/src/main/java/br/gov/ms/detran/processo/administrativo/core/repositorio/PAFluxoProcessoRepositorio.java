package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.core.projeto.repositorio.DetranStoredProcedureRepositorio;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.IJSPASequencial;
import br.gov.ms.detran.comum.iface.entidade.ISequencialJSPAEntity;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.persistencia.util.JPATupleUtil;
import br.gov.ms.detran.comum.persistencia.util.JPAUtil;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.criteria.PAFluxoProcessoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoProcessoWrapper;
import java.sql.Connection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.ResultTransformer;

public class PAFluxoProcessoRepositorio extends AbstractJpaDAORepository<PAFluxoProcesso> {

    /**
     * Retorna PAFluxoProcesso buscado por ApoioOrigemInstauracao.id
     * 
     * @param em
     * @param apoioOrigemInstauracaoId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public PAFluxoProcesso getPAFluxoProcessoPorApoioOrigemInstauracao(EntityManager em, 
                                                                       Long apoioOrigemInstauracaoId) throws AppException {
        
        if (apoioOrigemInstauracaoId == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
       
        List<PAFluxoProcesso> lPAFluxoProcesso = 
                super.getListNamedQuery(
                        em, 
                        "PAFluxoProcesso.getPAFluxoProcessoPorApoioOrigemInstauracao", 
                        apoioOrigemInstauracaoId, 
                        AtivoEnum.ATIVO,
                        Boolean.TRUE);
        
        if (DetranCollectionUtil.ehNuloOuVazio(lPAFluxoProcesso) || lPAFluxoProcesso.size() > 1) {
            DetranWebUtils.applicationMessageException("PA.M3");
        }
        
        return lPAFluxoProcesso.get(0);
    }

    /**
     * @param em
     * @param fluxo
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public PAFluxoProcesso getPAFluxoProcessoPorCodigo(EntityManager em, Integer fluxo) throws DatabaseException {
        return super.getNamedQuery(em, "PAFluxoProcesso.getPAFluxoProcessoPorCodigo", fluxo, AtivoEnum.ATIVO);
    }
    
    /**
     * @param em
     * @param descricao
     * @return
     * @throws DatabaseException 
     */
    public PAFluxoProcesso getPAFluxoProcessoByDescricao(EntityManager em, String descricao) throws DatabaseException{
         List<PAFluxoProcesso> result =  getListNamedQuery(em, "PAFluxoProcesso.PAFluxoProcessoByDescricao", descricao);
          return DetranCollectionUtil.ehNuloOuVazio(result) ? null : result.get(0);
    }
    
    /**
     * @param em
     * @param id
     * @return
     * @throws DatabaseException 
     */
    public PAFluxoProcesso getPAFluxoProcessoById(EntityManager em, Long id) throws DatabaseException{
         List<PAFluxoProcesso> result =  getListNamedQuery(em, "PAFluxoProcesso.PAFluxoProcessoById", id);
          return DetranCollectionUtil.ehNuloOuVazio(result) ? null : (PAFluxoProcesso) result.get(0);
    }
    
    /**
     * @param con
     * @param jspaEntity
     * @param entidade
     * @throws DatabaseException 
     */
    public void obterSequencialCodigoStatus(Connection con, ISequencialJSPAEntity jspaEntity, Object entidade) throws DatabaseException {

        DetranStoredProcedureRepositorio jsparepo = new DetranStoredProcedureRepositorio();
        jspaEntity = jsparepo.executarStoredProcedure(con, jspaEntity);

        if (jspaEntity.getRetorno() != null) {
            ((IJSPASequencial) entidade).setSequencial(jspaEntity.getSequencia());
        }
    }
    
    public List<PAFluxoProcessoWrapper> getPAFluxoProcessoPorFiltros(EntityManager em, PAFluxoProcessoCriteria c) throws DatabaseException {
        Query query = em.createNativeQuery(
                "SELECT DISTINCT tck.Tck_ID AS idTck, "
                + "tck.Tck_Codigo "
                + "FROM dbo.TB_TCH_PAD_FLUXO_FASE tch "
                + "INNER JOIN dbo.TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO tci ON tci.Tci_ID = tch.Tch_Prioridade_Fluxo_Amparo "
                + "INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb ON tcb.Tcb_ID = tch.Tch_Andamento "
                + "INNER JOIN dbo.TB_TCK_PAD_FLUXO_PROCESSO tck ON tck.Tck_ID = tci.Tci_Fluxo_Processo "
                + "WHERE (:p0 IS NULL OR tck.Tck_Codigo = :p0) "
                + "AND (tck.Tck_Descricao LIKE :p1) "
                + "AND (:p2 IS NULL OR tck.Tck_Fluxo_Independente = :p2) "
                + "AND (:p3 IS NULL OR tck.Ativo = :p3) "
                + "AND (:p4 IS NULL OR tcb.Tcb_Codigo = :p4) "
                + "AND (tcb.Tcb_Descricao LIKE :p5) "
                + "AND tch.Ativo = 1 "
                + "AND tci.Ativo = 1 "
                + "AND tcb.Ativo = 1 "
                + "ORDER BY tck.Tck_Codigo ");
        
        JPAUtil.setParameters(query, montaParametrosFiltros(c));
        
        if(c.getFrom() != null && c.getTo() != null) {
            query.setFirstResult(c.getFrom()-1).setMaxResults(c.getTo());
        }
        
        query.unwrap(SQLQuery.class).setResultTransformer(new ResultTransformer() {

            @Override
            public Object transformTuple(Object[] os, String[] strings) {
                JPATupleUtil tupleUtil = new JPATupleUtil(os, strings);
                
                Long idTck = tupleUtil.getTupleValue("idTck", Long.TYPE);
                
                PAFluxoProcessoWrapper wrapper = new PAFluxoProcessoWrapper(em.find(PAFluxoProcesso.class, idTck));
                
                return wrapper;
            }

            @Override
            public List transformList(List list) {
                return list;
            }
        });
        
        return query.getResultList();
    }
    
    public Long getCountPAFluxoProcessoPorFiltros(EntityManager em, PAFluxoProcessoCriteria c) throws DatabaseException {
        Query query = em.createNativeQuery(
                "SELECT COUNT (DISTINCT tck.Tck_ID) "
                + "FROM dbo.TB_TCH_PAD_FLUXO_FASE tch "
                + "INNER JOIN dbo.TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO tci ON tci.Tci_ID = tch.Tch_Prioridade_Fluxo_Amparo "
                + "INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb ON tcb.Tcb_ID = tch.Tch_Andamento "
                + "INNER JOIN dbo.TB_TCK_PAD_FLUXO_PROCESSO tck ON tck.Tck_ID = tci.Tci_Fluxo_Processo "
                + "WHERE (:p0 IS NULL OR tck.Tck_Codigo = :p0) "
                + "AND (tck.Tck_Descricao LIKE :p1) "
                + "AND (:p2 IS NULL OR tck.Tck_Fluxo_Independente = :p2) "
                + "AND (:p3 IS NULL OR tck.Ativo = :p3) "
                + "AND (:p4 IS NULL OR tcb.Tcb_Codigo = :p4) "
                + "AND (tcb.Tcb_Descricao LIKE :p5) "
                + "AND tch.Ativo = 1 "
                + "AND tci.Ativo = 1 "
                + "AND tcb.Ativo = 1 ");
        
        JPAUtil.setParameters(query, montaParametrosFiltros(c));
        
        Integer count = (Integer) query.getSingleResult();
        
        return (count != null ? count.longValue() : null);
    }

    private Object[] montaParametrosFiltros(PAFluxoProcessoCriteria c) {
        Object [] params = {
            c.getCodigo(),
            DetranStringUtil.ehBrancoOuNulo(c.getDescricao()) ? "%" : "%"+c.getDescricao()+"%",
            c.getFluxoIndependente() == null ? null : c.getFluxoIndependente().ordinal(),
            c.getAtivo() == null ? null : c.getAtivo().ordinal(),
            c.getCodigoAndamento() == null ? null : c.getCodigoAndamento(),
            DetranStringUtil.ehBrancoOuNulo(c.getDescricaoAndamento()) ? "%" : "%"+c.getDescricaoAndamento()+"%"
        };
        
        return params;
    }
    
    /**
     * 
     * @param em
     * @return
     * @throws AppException 
     */
    public List<PAFluxoProcesso> getListaFluxoProcessoAtivo(EntityManager em) throws AppException {
        
        final Integer PA_FLUXO_PROCESSO_MAXIMO = 2999;
        
        return getListNamedQuery(em, "PAFluxoProcesso.getListaPAFluxoProcessoAtivo", PA_FLUXO_PROCESSO_MAXIMO, AtivoEnum.ATIVO);
    }
    
    public List<PAFluxoProcesso> getListaPAFluxoProcessoPorProcesso(EntityManager em, String numeroProcesso) throws DatabaseException{
        Object[] params = {
            numeroProcesso,
            AtivoEnum.ATIVO
        };
        return getListNamedQuery(em, "PAFluxoProcesso.getListaPAFluxoProcessoPorProcesso", params);
    }
}