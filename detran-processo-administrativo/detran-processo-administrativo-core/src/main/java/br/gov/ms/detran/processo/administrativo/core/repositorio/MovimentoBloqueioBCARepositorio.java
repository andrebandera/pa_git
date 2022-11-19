package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoBloqueioBCA;
import br.gov.ms.detran.processo.administrativo.enums.TipoMovimentoBloqueioBCAEnum;
import java.util.List;
import javax.persistence.EntityManager;

public class MovimentoBloqueioBCARepositorio extends AbstractJpaDAORepository<MovimentoBloqueioBCA> {
    
    public List<MovimentoBloqueioBCA> getBloqueiosPorPA(EntityManager em, Long idPA) throws AppException {
        
        List<MovimentoBloqueioBCA> lista = 
                getListNamedQuery(em, 
                                  "MovimentoBloqueioBCA.getBloqueiosPorPA", 
                                  idPA);
        
        return lista;
    }
    
    /**
     * 
     * @param em
     * @param idProcessoAdministrativo
     * @param tipo
     * @return
     * @throws AppException 
     */
    public BloqueioBCA getBloqueioPorProcessoAdministrativoETipoAtivoParaDesistencia(
            EntityManager em, Long idProcessoAdministrativo, TipoMovimentoBloqueioBCAEnum tipo) throws AppException {
        
        if(idProcessoAdministrativo == null || tipo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<MovimentoBloqueioBCA> lMovimentoBloqueioBCA 
            = getListNamedQuery(em, "MovimentoBloqueioBCA.getBloqueioPorProcessoAdministrativoETipoAtivoParaDesistencia", idProcessoAdministrativo, tipo, AtivoEnum.ATIVO);
        
        if(!DetranCollectionUtil.ehNuloOuVazio(lMovimentoBloqueioBCA) && lMovimentoBloqueioBCA.size() > 1) {
            DetranWebUtils.applicationMessageException("PA.M1");
        }
        
        return !DetranCollectionUtil.ehNuloOuVazio(lMovimentoBloqueioBCA) ? lMovimentoBloqueioBCA.get(0).getBloqueioBCA() : null;
    }

    /**
     * @param em
     * @param bloqueioId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List<MovimentoBloqueioBCA> getMovimentosPorBloqueioEAtivo(EntityManager em, Long bloqueioId) throws DatabaseException {
        return getListNamedQuery(em, "MovimentoBloqueioBCA.getMovimentosPorBloqueioEAtivo", bloqueioId, AtivoEnum.ATIVO);
    }
}