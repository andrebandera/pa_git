package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.projeto.service.DetranAbstractGenericService;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoJson;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoJsonRepositorio;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author desenvolvimento
 * @param <T>
 */
@Stateless(mappedName = "ejb/ExecucaoEspecificaProcessoAdministrativoService")
@Remote(IExecucaoEspecificaProcessoAdministrativoService.class)
public class ExecucaoEspecificaProcessoAdministrativoService<T extends IBaseEntity> extends DetranAbstractGenericService implements IExecucaoEspecificaProcessoAdministrativoService {

    private static final Logger LOG = Logger.getLogger(ExecucaoEspecificaProcessoAdministrativoService.class);

    public ExecucaoEspecificaProcessoAdministrativoService() {
    }

    @Override
    @PersistenceContext(unitName = "DETRAN-PROCESSO-ADMINISTRATIVO-PU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    /**
     * 
     * @param iBaseEntity
     * @throws AppException 
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void alterarSituacaoProcessoAdministrativoJson(IBaseEntity iBaseEntity) throws AppException {

        new ProcessoAdministrativoJsonRepositorio()
            .alterarSituacaoProcessoAdministrativoJson(em, (ProcessoAdministrativoJson) iBaseEntity);
    }
}