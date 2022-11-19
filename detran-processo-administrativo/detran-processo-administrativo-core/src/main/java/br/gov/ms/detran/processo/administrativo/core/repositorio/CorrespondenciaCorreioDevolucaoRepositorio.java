package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaCorreioDevolucao;
import javax.persistence.EntityManager;

public class CorrespondenciaCorreioDevolucaoRepositorio extends AbstractJpaDAORepository<CorrespondenciaCorreioDevolucao> {

    /**
     * @param em
     * @param motivo
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public CorrespondenciaCorreioDevolucao getCorrespondenciaCorreioDevolucaoPorMotivoAtivo(EntityManager em, Integer motivo) throws DatabaseException {
        return super.getNamedQuery(em, 
                                   "CorrespondenciaCorreioDevolucao.getCorrespondenciaCorreioDevolucaoPorMotivoAtivo", 
                                   motivo, 
                                   AtivoEnum.ATIVO);
    }

    /**
     * Retorna CorrespondenciaCorreioDevolucao buscado por Correspondencia.id
     * 
     * @param em
     * @param correspondenciaId
     * @return
     * @throws DatabaseException 
     */
    public CorrespondenciaCorreioDevolucao getCorrespondenciaCorreioDevolucaoPorCorrespondencia(EntityManager em, Long correspondenciaId) throws DatabaseException {
        return super.getNamedQuery(em, 
                                   "CorrespondenciaCorreioDevolucao.getCorrespondenciaCorreioDevolucaoPorCorrespondencia", 
                                   correspondenciaId);
    }
}
