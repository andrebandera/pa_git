package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.core.projeto.repositorio.DetranStoredProcedureRepositorio;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.IJSPASequencial;
import br.gov.ms.detran.comum.iface.entidade.ISequencialJSPAEntity;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatus;
import java.sql.Connection;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Carlos Eduardo
 */
public class PAStatusRepositorio extends AbstractJpaDAORepository<PAStatus>{
    
    public PAStatus getPAStatusAtivoByCodigo(EntityManager em, Integer codigo) throws DatabaseException{
        return getNamedQuery(em, "PAStatus.PAStatusAtivoByCodigo", codigo, AtivoEnum.ATIVO);
    }
    
    public PAStatus getPAStatusAtivoById(EntityManager em, Long id) throws DatabaseException{
        return getNamedQuery(em, "PAStatus.PAStatusAtivoById", id, AtivoEnum.ATIVO);
    }
    
    public PAStatus getPAStatusByDescricao(EntityManager em, String descricao) throws DatabaseException{
         List<PAStatus> result =  getListNamedQuery(em, "PAStatus.PAStatusByDescricao", descricao);
          return DetranCollectionUtil.ehNuloOuVazio(result) ? null : result.get(0);
    }
               
    public void obterSequencialCodigoStatus(Connection con, ISequencialJSPAEntity jspaEntity, Object entidade) throws DatabaseException {

        DetranStoredProcedureRepositorio jsparepo = new DetranStoredProcedureRepositorio();
        jspaEntity = jsparepo.executarStoredProcedure(con, jspaEntity);

        if (jspaEntity.getRetorno() != null) {
            ((IJSPASequencial) entidade).setSequencial(jspaEntity.getSequencia());
        }
    }
    
    public List<PAStatus> getPAStatusPorAndamentoPA(EntityManager em, 
            Integer from, Integer to, Long id, String descricao, AtivoEnum ativo)
            throws DatabaseException {
        Object[] params = {id,
            !DetranStringUtil.ehBrancoOuNulo(descricao) ? "%" + descricao + "%" : null,
            AtivoEnum.ATIVO};
        return getListNamedQuery(em, "PAStatus.getPAStatusNaoUtilizadoPeloAndamentoPA", from, to, params);

    }
    
    public Long getCountPAStatusPorAndamentoPA(EntityManager em, 
            Long id, String descricao, AtivoEnum ativo)
            throws DatabaseException {
        Object[] params = {id,
            !DetranStringUtil.ehBrancoOuNulo(descricao) ? "%" + descricao + "%" : null,
            AtivoEnum.ATIVO};
        return getCountHqlEntitySearch(em, "PAStatus.getCountPAStatusNaoUtilizadoPeloAndamentoPA", params);
    }
}