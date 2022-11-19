package br.gov.ms.detran.processo.administrativo.core.repositorio.pju;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJuridicoDelito;
import java.util.List;
import javax.persistence.EntityManager;

public class DadoProcessoJudicialDelitoRepositorio  extends AbstractJpaDAORepository<DadoProcessoJuridicoDelito> {

    /**
     * @param em
     * @param dadoProcessoJudicialId
     * @return
     * @throws DatabaseException 
     */
    public List<DadoProcessoJuridicoDelito> getDadoProcessoJudicialDelitoPorDadoProcessoJudicial(EntityManager em, 
                                                                                           Long dadoProcessoJudicialId) throws DatabaseException {
        return getListNamedQuery(em, "DadoProcessoJuridicoDelito.getDadoProcessoJudicialDelitoPorDadoProcessoJudicial", dadoProcessoJudicialId);
    }
}