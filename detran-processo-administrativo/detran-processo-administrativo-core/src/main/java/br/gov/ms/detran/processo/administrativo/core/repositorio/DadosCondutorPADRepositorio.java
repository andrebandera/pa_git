package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.criteria.DadosCondutorPADCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import java.util.List;
import javax.persistence.EntityManager;

public class DadosCondutorPADRepositorio extends AbstractJpaDAORepository<DadosCondutorPAD> {

    /**
     * 
     * @param em
     * @param criteria
     * @return
     * @throws DatabaseException 
     */
    public List<DadosCondutorPAD> getCondutoresParaPA(EntityManager em, DadosCondutorPADCriteria criteria) throws DatabaseException {
        return super.getListNamedQuery(em, "DadosCondutorPAD.findAllByUsuarioInclusao", criteria.getFrom(), criteria.getTo());
    }
}