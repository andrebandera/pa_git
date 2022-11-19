package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAServicoExterno;
import javax.persistence.EntityManager;

public class PAServicoExternoRepositorio extends AbstractJpaDAORepository<PAServicoExterno> {

    /**
     * Buscar PAServicoExterno pelo nome e ativo = 1.
     * 
     * @param em
     * @param URL_SERVICO
     * @return
     * @throws DatabaseException 
     */
    public PAServicoExterno getPAServicoExternoPorNomeAtivo(EntityManager em, String URL_SERVICO) throws DatabaseException {
        return super.getNamedQuery(em, 
                                   "PAServicoExterno.getPAServicoExternoPorNomeAtivo", 
                                   URL_SERVICO, 
                                   AtivoEnum.ATIVO);
    }
}