package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.MotivoAlegacao;
import java.util.List;
import javax.persistence.EntityManager;

public class MotivoAlegacaoRepositorio extends AbstractJpaDAORepository<MotivoAlegacao> {

    /**
     * @param em
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List getListaMotivoAlegacao(EntityManager em) throws DatabaseException {
        return super.getListNamedQuery(em, "MotivoAlegacao.getListaMotivoAlegacao", AtivoEnum.ATIVO);
    }

    public MotivoAlegacao getMotivoPorCodigo(EntityManager em, Integer motivoNaoConhecimento) throws DatabaseException {
        return getNamedQuery(em, "MotivoAlegacao.getMotivoPorCodigo", motivoNaoConhecimento, AtivoEnum.ATIVO);
    }
}