package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaTipo;

import javax.persistence.EntityManager;
import java.util.List;

public class CorrespondenciaTipoRepositorio extends AbstractJpaDAORepository<CorrespondenciaTipo> {

    public Long getIdCorrespondenciaTipoPorCodigoAtivo(EntityManager em, Integer codigo) throws DatabaseException {
        List<CorrespondenciaTipo> ct =
                super.getListNamedQuery(em, "CorrespondenciaTipo.getIdCorrespondenciaTipoPorCodigoAtivo", codigo, AtivoEnum.ATIVO);

        return DetranCollectionUtil.ehNuloOuVazio(ct) ? null : ct.get(0).getId();
    }


}
