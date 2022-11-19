package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.EditalProcessoAdministrativo;
import javax.persistence.EntityManager;

public class EditalProcessoAdministrativoRepositorio extends AbstractJpaDAORepository<EditalProcessoAdministrativo> {

    /**
     * @param em
     * @param idNotificacao
     * @return
     * @throws DatabaseException 
     */
    public EditalProcessoAdministrativo getEditalPorNotificacao(EntityManager em, Long idNotificacao) throws DatabaseException {
        return getNamedQuery(em, "getEditalPorNotificacao", idNotificacao, AtivoEnum.ATIVO);
    }
}
