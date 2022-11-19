package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoAgravamento;
import java.util.List;
import javax.persistence.EntityManager;

public class ProcessoAdministrativoAgravamentoRepositorio extends AbstractJpaDAORepository<ProcessoAdministrativoAgravamento> {
 
    /**
     * 
     * @param em
     * @param processoAdministrativo
     * @return
     * @throws AppException 
     */
    public ProcessoAdministrativoAgravamento getProcessoAdministrativoOrigemPorAgravamento(
        EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        if(processoAdministrativo == null || processoAdministrativo.getId() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<ProcessoAdministrativoAgravamento> lAgravamento
            = super.getListNamedQuery(
                em, 
                "ProcessoAdministrativoAgravamento.getProcessoAdministrativoOrigemPorAgravamento", 
                processoAdministrativo.getId(),
                AtivoEnum.ATIVO
            );
        
        return !DetranCollectionUtil.ehNuloOuVazio(lAgravamento) ? lAgravamento.get(0) : null;
    }

    /**
     * @param em
     * @param paId
     * @return
     * @throws DatabaseException 
     */
    public ProcessoAdministrativoAgravamento getPorProcessoAdministrativo(EntityManager em, Long paId) throws DatabaseException {
        return super.getNamedQuery(em, "ProcessoAdministrativoAgravamento.getPorProcessoAdministrativo", paId, AtivoEnum.ATIVO);
    }

    /**
     * @param em
     * @param paId
     * @return
     * @throws DatabaseException 
     */
    public ProcessoAdministrativoAgravamento getPorProcessoAdministrativoAgravado(EntityManager em, Long paId) throws DatabaseException {
        return super.getNamedQuery(em, "ProcessoAdministrativoAgravamento.getPorProcessoAdministrativoAgravado", paId, AtivoEnum.ATIVO);
    }
}