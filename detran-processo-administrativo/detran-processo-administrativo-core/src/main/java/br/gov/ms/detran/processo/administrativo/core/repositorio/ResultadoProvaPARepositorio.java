package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoProvaPA;
import java.util.List;
import javax.persistence.EntityManager;

public class ResultadoProvaPARepositorio extends AbstractJpaDAORepository<ResultadoProvaPA> {
    
    /**
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    public ResultadoProvaPA getResultadoProvaPAAtivoPorProcessoAdministrativo(EntityManager em, 
                                                                              ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        if (processoAdministrativo == null || processoAdministrativo.getId() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<ResultadoProvaPA> lResultadoProvaPA = 
                getListNamedQuery(em, 
                                  "ResultadoProvaPA.getResultadoProvaPAAtivoPorProcessoAdministrativo", 
                                  processoAdministrativo.getId(), 
                                  AtivoEnum.ATIVO);
        
        return DetranCollectionUtil.ehNuloOuVazio(lResultadoProvaPA) ? null : lResultadoProvaPA.get(0);
    }
    
    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    public ResultadoProvaPA getResultadoProvaPAAtivoPorProcessoAdministrativo2(EntityManager em,
                                                                               ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        if (processoAdministrativo == null || processoAdministrativo.getId() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<ResultadoProvaPA> lResultadoProvaPA = 
                getListNamedQuery(em, 
                                  "ResultadoProvaPA.getResultadoProvaPAAtivoPorProcessoAdministrativo", 
                                  processoAdministrativo.getId(), 
                                  AtivoEnum.ATIVO);
        
        if (lResultadoProvaPA.size() > 1) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return DetranCollectionUtil.ehNuloOuVazio(lResultadoProvaPA) ? null : lResultadoProvaPA.get(0);
    }
}
