/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.core.projeto.repositorio.DetranStoredProcedureRepositorio;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.IJSPASequencial;
import br.gov.ms.detran.comum.iface.entidade.ISequencialJSPAEntity;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.persistencia.util.JPATupleUtil;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;
import br.gov.ms.detran.processo.administrativo.enums.TipoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.PAAndamentoProcessoWrapper;
import java.sql.Connection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.ResultTransformer;

/**
 *
 * @author Lillydi
 */
public class PAAndamentoProcessoRepositorio extends AbstractJpaDAORepository<PAAndamentoProcesso> {

    public List getAndamentosEmUso(EntityManager em) throws DatabaseException {
        return getListNamedQuery(em, "PAAndamentoProcesso.getAndamentosEmUso");
    }

    /**
     * 
     * @param em
     * @param codigos
     * @return
     * @throws AppException 
     */
    public List<PAAndamentoProcesso> getListaPAAndamentoProcessoPorListaCodigo(EntityManager em, List<Integer> codigos) throws AppException {
        
        if(DetranCollectionUtil.ehNuloOuVazio(codigos)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return super.getListNamedQuery(em, "PAAndamentoProcesso.getListaPAAndamentoProcessoPorListaCodigo", codigos);
    }
    
    public PAAndamentoProcesso getPAAndamentoProcessoByDescricao(EntityManager em, String descricao) throws DatabaseException{
         List<PAAndamentoProcesso> result =  getListNamedQuery(em, "PAAndamentoProcesso.PAAndamentoProcessoByDescricao", descricao);
          return DetranCollectionUtil.ehNuloOuVazio(result) ? null : result.get(0);
    }
     
    public PAAndamentoProcesso getPAAndamentoProcessoAtivoByCodigo(EntityManager em, Integer codigo) throws DatabaseException{
        return getNamedQuery(em, "PAAndamentoProcesso.PAAndamentoProcessoAtivoByCodigo", codigo, AtivoEnum.ATIVO);
    }
    
    public void obterSequencialCodigoStatus(Connection con, ISequencialJSPAEntity jspaEntity, Object entidade) throws DatabaseException {

        DetranStoredProcedureRepositorio jsparepo = new DetranStoredProcedureRepositorio();
        jspaEntity = jsparepo.executarStoredProcedure(con, jspaEntity);

        if (jspaEntity.getRetorno() != null) {
            ((IJSPASequencial) entidade).setSequencial(jspaEntity.getSequencia());
        }
    }
    
    public List<PAAndamentoProcesso> getAndamentoPorFluxoFase(EntityManager em, 
            Integer from, Integer to, Long id, String descricao, AtivoEnum ativo)
            throws DatabaseException {
        Object[] params = {id,
            !DetranStringUtil.ehBrancoOuNulo(descricao) ? "%" + descricao + "%" : null,
            AtivoEnum.ATIVO};
        return getListNamedQuery(em, "PAAndamentoProcesso.getAndamentoNaoUtilizadoPeloFluxoFase", from, to, params);

    }

    public Long getCountAndamentoNaoUtilizadoPeloFaseFluxo(EntityManager em, Long id, String descricao, AtivoEnum ativo)
            throws DatabaseException{
        Object[] params = {id,
            !DetranStringUtil.ehBrancoOuNulo(descricao) ? "%" + descricao + "%" : null,
            AtivoEnum.ATIVO};
        return super.getCountHqlEntitySearch(em, "PAAndamentoProcesso.getCountAndamentoNaoUtilizadoPeloFluxoFase", params);
    }

    public List getAndamentosAutomaticos(EntityManager em) throws DatabaseException {
        Object[] params = {TipoAndamentoEnum.AUTOMATICO, AtivoEnum.ATIVO};
        
        return getListNamedQuery(em, "PAAndamentoProcesso.getAndamentosAutomaticos", params);
    }
    
    public List<PAAndamentoProcessoWrapper> getAndamentoPorDescricao(EntityManager em, String descricaoAndamento, Integer from, Integer to) throws DatabaseException {
        Query query = em.createNativeQuery(
                "SELECT "
                + "tcb.Tcb_Codigo as codigoAndamento, "
                + "tcb.Tcb_Descricao as descricaoAndamento "
                + "FROM dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb "
                + "WHERE tcb.Tcb_Descricao LIKE :p0 "
                + "ORDER BY tcb.Tcb_Codigo ASC "
        );
        
        query.setParameter("p0", DetranStringUtil.ehBrancoOuNulo(descricaoAndamento) ? "%" : "%" + descricaoAndamento + "%");
        
        if(from != null && to != null){
            query.setFirstResult(from-1).setMaxResults(to);
        }
        
        query.unwrap(SQLQuery.class).setResultTransformer(new ResultTransformer() {

            @Override
            public Object transformTuple(Object[] os, String[] strings) {
                JPATupleUtil tupleUtil = new JPATupleUtil(os, strings);
                
                Integer codigoAndamento = tupleUtil.getTupleValue("codigoAndamento", Integer.TYPE);
                String descricaoAndamento = tupleUtil.getTupleValue("descricaoAndamento", String.class);
                
                PAAndamentoProcessoWrapper wrapper = new PAAndamentoProcessoWrapper(new PAAndamentoProcesso());
                
                wrapper.getEntidade().setCodigo(codigoAndamento);
                wrapper.getEntidade().setDescricao(descricaoAndamento);
                
                return wrapper;
            }

            @Override
            public List transformList(List list) {
                return list;
            }
        });
          
        return query.getResultList();
    }
    
    public Long getCountAndamentoPorDescricao(EntityManager em, String descricaoAndamento) throws DatabaseException {
        Query query = em.createNativeQuery(
                "SELECT COUNT(*) AS resultado "
                + "FROM dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb "
                + "WHERE tcb.Tcb_Descricao LIKE :p0 "
        );
        
        query.setParameter("p0", DetranStringUtil.ehBrancoOuNulo(descricaoAndamento) ? "%" : "%" + descricaoAndamento + "%");
        
        Integer count = (Integer) query.getSingleResult();
        
        return (count != null ? count.longValue() : 0);
    }
    
    /**
     * 
     * @param em
     * @return
     * @throws DatabaseException 
     */
    public List getAndamentosAtivos(EntityManager em) throws DatabaseException {
        
        final Integer ANDAMENTO_MAXIMO = 2999;
        
        Object[] params = {ANDAMENTO_MAXIMO, AtivoEnum.ATIVO};
        
        return getListNamedQuery(em, "PAAndamentoProcesso.getAndamentosAtivos", params);
    }
    
    /**
     * 
     * Busca Andamento por fluxo
     * 
     * @param fluxoId
     * @param em
     * @return
     * @throws DatabaseException 
     */
    public List getAndamentosAtivosPorFluxo(Integer codigo, EntityManager em) throws DatabaseException {
        
        Object[] params = {codigo, AtivoEnum.ATIVO};
        
        return getListNamedQuery(em, "PAAndamentoProcesso.getAndamentosAtivosPorFluxo", params);
    }
}
