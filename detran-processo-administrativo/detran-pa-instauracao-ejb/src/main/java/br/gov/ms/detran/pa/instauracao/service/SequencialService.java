package br.gov.ms.detran.pa.instauracao.service;

import static br.gov.ms.detran.comum.projeto.constantes.TipoNumeroSequencialConstante.BLOQUEIO_BCA_PA;
import static br.gov.ms.detran.comum.projeto.constantes.TipoNumeroSequencialConstante.NUMERO_PORTARIA_INSTAURACAO_PA;
import static br.gov.ms.detran.comum.projeto.constantes.TipoNumeroSequencialConstante.NUMERO_PROCESSO_ADMINISTRATIVO;
import static br.gov.ms.detran.comum.projeto.constantes.TipoNumeroSequencialConstante.PROTOCOLO_PA;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.core.bo.UltimoSequencialFactory;
import br.gov.ms.detran.processo.administrativo.ejb.ISequencialService;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless(mappedName = "ejb/SequencialService")
@Remote(ISequencialService.class)
public class SequencialService implements ISequencialService {

    @PersistenceContext(unitName = "DETRAN-PA-PU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized String getNumeroProcesso() throws AppException {

        return UltimoSequencialFactory.getInstance().getUltimoSequencial(em, NUMERO_PROCESSO_ADMINISTRATIVO);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized String getNumeroPortaria() throws AppException {
        return UltimoSequencialFactory.getInstance().getUltimoSequencial(em, NUMERO_PORTARIA_INSTAURACAO_PA);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized String getNumeroBloqueioBCA() throws AppException {
        return UltimoSequencialFactory.getInstance().getUltimoSequencial(em, BLOQUEIO_BCA_PA);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized String getNumeroProtocolo() throws AppException {
        return UltimoSequencialFactory.getInstance().getUltimoSequencial(em, PROTOCOLO_PA);
    }
}
