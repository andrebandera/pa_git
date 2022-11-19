package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoApensado;
import br.gov.ms.detran.processo.administrativo.enums.PAStatusEnum;
import java.util.List;
import javax.persistence.EntityManager;

public class ProcessoAdministrativoApensadoRepositorio extends AbstractJpaDAORepository<ProcessoAdministrativoApensado> {
 
    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativoApensado getProcessoAdministrativoOrigemPorApensado(
        EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        if(processoAdministrativo == null || processoAdministrativo.getId() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<ProcessoAdministrativoApensado> lApensado 
            = super.getListNamedQuery(
                em, 
                "ProcessoAdministrativoApensado.getProcessoAdministrativoOrigemPorApensado", 
                processoAdministrativo.getId(),
                AtivoEnum.ATIVO
            );
        
        return !DetranCollectionUtil.ehNuloOuVazio(lApensado) ? lApensado.get(0) : null;
    }

    /**
     * @param em
     * @param idPA
     * @return
     * @throws DatabaseException 
     */
    public ProcessoAdministrativoApensado getApensadoPorPAOriginal(EntityManager em, Long idPA) throws DatabaseException {
        
        List<Integer> lStatus = DetranCollectionUtil.montaLista(PAStatusEnum.ARQUIVADO.getCodigo(), 
                                                                PAStatusEnum.CANCELADO.getCodigo());

        Object[] params = {idPA,
                          lStatus,
                          AtivoEnum.ATIVO};
        
        return getNamedQuery(em, "ProcessoAdministrativoApensado.getApensadoPorPAOriginal", params);
    }
}