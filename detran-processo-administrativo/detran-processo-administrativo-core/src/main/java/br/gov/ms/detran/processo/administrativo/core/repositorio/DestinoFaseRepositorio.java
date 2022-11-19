package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.DestinoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import java.util.List;
import javax.persistence.EntityManager;

public class DestinoFaseRepositorio extends AbstractJpaDAORepository<DestinoFase> {

    /**
     * @param em
     * @param fluxoFaseId
     * @param origemDestino
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public DestinoFase getDestinoFasePorFluxoFaseEOrigemDestino(
        EntityManager em, Long fluxoFaseId, OrigemDestinoEnum origemDestino) throws DatabaseException {
        
        return 
            super.getNamedQuery(
                em, 
                "DestinoFase.getDestinoFasePorFluxoFaseEOrigemDestino", 
                fluxoFaseId, 
                origemDestino,
                AtivoEnum.ATIVO
            );
    }
    
    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @param fluxoFase
     * @param origemDestino
     * @return
     * @throws AppException 
     */
    public DestinoFase getDestinoFaseParaAberturaRecurso(
        EntityManager em, ProcessoAdministrativo processoAdministrativo, PAFluxoFase fluxoFase, OrigemDestinoEnum origemDestino) throws AppException {
        
        return getDestinoFasePorFluxoFaseEOrigemDestino(em, fluxoFase.getId(), origemDestino);
    }
    
    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @param origemDestino
     * @return
     * @throws AppException 
     */
    public DestinoFase getDestinoFaseParaAberturaRecurso(
        EntityManager em, ProcessoAdministrativo processoAdministrativo, OrigemDestinoEnum origemDestino) throws AppException {
        
        DestinoFase destinoFase = null;
        
        PAFluxoFase fluxoFase =  
            new PAFluxoFaseRepositorio()
                .getFluxoFaseDoProcessoAdministrativo(
                    em,
                    processoAdministrativo
                );
        
        if(fluxoFase != null) {
            destinoFase = getDestinoFasePorFluxoFaseEOrigemDestino(em, fluxoFase.getId(), origemDestino);
        }
        
        return destinoFase;
    }
    /**
     *
     * @param em
     * @param idFluxoFase
     * @return
     * @throws DatabaseException
     */
    public List<DestinoFase> getDestinoFasePorPAFluxoFaseVinculos(
            EntityManager em, Long idFluxoFase) throws DatabaseException {
        return super.getListNamedQuery(em, "DestinoFase.getDestinoFasePorPAFluxoFaseVinculos", 
                idFluxoFase, AtivoEnum.ATIVO);
    }
}