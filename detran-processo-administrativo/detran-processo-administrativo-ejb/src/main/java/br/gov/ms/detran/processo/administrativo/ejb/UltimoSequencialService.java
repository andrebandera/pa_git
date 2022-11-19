package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.processo.administrativo.core.cenarios.IUltimoSequencialService;
import static br.gov.ms.detran.comum.projeto.constantes.TipoNumeroSequencialConstante.BLOQUEIO_BCA_PA;
import static br.gov.ms.detran.comum.projeto.constantes.TipoNumeroSequencialConstante.NUMERO_PORTARIA_INSTAURACAO_PA;
import static br.gov.ms.detran.comum.projeto.constantes.TipoNumeroSequencialConstante.NUMERO_PROCESSO_ADMINISTRATIVO;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.core.bo.UltimoSequencialFactory;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Christiano Carrilho.
 */
@Stateless(mappedName = "ejb/UltimoSequencialService")
@Remote(IUltimoSequencialService.class)
public class UltimoSequencialService implements IUltimoSequencialService {

    @PersistenceContext(unitName = "DETRAN-PROCESSO-ADMINISTRATIVO-PU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized String getNumeroProcesso() throws AppException {

        return UltimoSequencialFactory.getInstance()
                .getUltimoSequencial(em, NUMERO_PROCESSO_ADMINISTRATIVO);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized String getNumeroPortaria() throws AppException {

        return UltimoSequencialFactory.getInstance()
                .getUltimoSequencial(em, NUMERO_PORTARIA_INSTAURACAO_PA);
    }

    @Override
    public synchronized String getNumeroBloqueioBCA() throws AppException {
        return UltimoSequencialFactory.getInstance()
                .getUltimoSequencial(em, BLOQUEIO_BCA_PA);
    }
}
