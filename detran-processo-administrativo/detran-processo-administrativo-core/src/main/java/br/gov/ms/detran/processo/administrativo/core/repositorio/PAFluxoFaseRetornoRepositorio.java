package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFaseRetorno;
import java.util.List;
import javax.persistence.EntityManager;

public class PAFluxoFaseRetornoRepositorio extends AbstractJpaDAORepository<PAFluxoFaseRetorno> {

    /**
     * @param em
     * @param fluxoFaseAtualId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public PAFluxoFaseRetorno getPAFluxoFaseRetornoPorFluxoFaseAtual(EntityManager em, Long fluxoFaseAtualId) throws DatabaseException {
        return super.getNamedQuery(em, "PAFluxoFaseRetorno.getPAFluxoFaseRetornoPorFluxoFaseAtual", fluxoFaseAtualId);
    }
    
    /**
     * @param em
     * @param fluxoFaseAtualId
     * @param codigoAndamento
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public PAFluxoFaseRetorno getPAFluxoFaseRetornoPorFluxoFaseAtualEAndamento(EntityManager em, 
                                                                               Long fluxoFaseAtualId, 
                                                                               Integer codigoFluxo,
                                                                               Integer codigoAndamento) throws DatabaseException {
        
        return super.getNamedQuery(em, 
                                   "PAFluxoFaseRetorno.getPAFluxoFaseRetornoPorFluxoFaseAtualEAndamento", 
                                   fluxoFaseAtualId,
                                   codigoAndamento,
                                   codigoFluxo);
        
    }
    
    /**
     *
     * @param em
     * @param idFluxoFase
     * @return
     * @throws DatabaseException
     */
    public List<PAFluxoFaseRetorno> getPAFluxoFaseRetornoPorPAFluxoFaseVinculos(
            EntityManager em, Long idFluxoFase) throws DatabaseException {
        return super.getListNamedQuery(em, "PAFluxoFaseRetorno.getPAFluxoFaseRetornoPorPAFluxoFaseVinculos", 
                idFluxoFase, AtivoEnum.ATIVO);
    }
}