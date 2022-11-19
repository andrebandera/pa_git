package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import java.util.List;
import javax.persistence.EntityManager;

public class PAStatusAndamentoRepositorio extends AbstractJpaDAORepository<PAStatusAndamento> {
    
    /**
     * 
     * @param em
     * @param codigoPAAndamentoProcesso
     * @return
     * @throws AppException 
     */
    public PAStatusAndamento getPAStatusAndamentoAtivoPorStatusEAndamento(EntityManager em, Integer codigoPAAndamentoProcesso) throws AppException {
        
        if(codigoPAAndamentoProcesso == null) {
            DetranWebUtils.applicationMessageException("Parâmetros obrigatórios estão inválidos.");
        }
        
        Object[] params = {
                codigoPAAndamentoProcesso, 
                AtivoEnum.ATIVO
        };
        
        List<PAStatusAndamento> lStatusAndamento 
            = getListNamedQuery(
                em, 
                "PAStatusAndamento.getPAStatusAndamentoAtivoPorStatusEAndamento", 
                params
            );
        
        if(DetranCollectionUtil.ehNuloOuVazio(lStatusAndamento) || lStatusAndamento.size() > 1) {
            DetranWebUtils.applicationMessageException("Status Andamento está inválido.");
        }
        
        return lStatusAndamento.get(0);
    }

    /**
     * @param em
     * @param paId
     * @return 
     */
    public PAStatusAndamento getStatusPorProcessoAdministrativo(EntityManager em, Long paId) throws DatabaseException {
        return super.getNamedQuery(em, "PAStatusAndamento.getStatusPorProcessoAdministrativo", paId, AtivoEnum.ATIVO);
    }

    /**
     * @param em
     * @param andamentoProcessoId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public PAStatusAndamento getPAStatusAndamentoPorPAAndamentoProcessoAtivo(EntityManager em, Long andamentoProcessoId) throws DatabaseException {
        return super.getNamedQuery(em, "PAStatusAndamento.getPAStatusAndamentoPorPAAndamentoProcessoAtivo", andamentoProcessoId, AtivoEnum.ATIVO);
    }

    /**
     * @param em
     * @param paAndamentoProcesso
     * @return
     * @throws DatabaseException 
     */
    public PAStatusAndamento getPAStatusAndamentoPorAndamentoProcesso(EntityManager em, PAAndamentoProcesso paAndamentoProcesso) throws DatabaseException {
        return super.getNamedQuery(em, "PAStatusAndamento.getPAStatusAndamentoPorAndamentoProcesso", paAndamentoProcesso.getId(), AtivoEnum.ATIVO);
    }
    
    public List<PAStatusAndamento> getPAStatusAndamentoAtivoPorPAStatus(EntityManager em, Long PAStatusId) throws DatabaseException {
        return super.getListNamedQuery(em, "PAStatusAndamento.getPAStatusAndamentoAtivoPorPAStatus", PAStatusId, AtivoEnum.ATIVO);
    }

    public List<PAStatusAndamento> getPAStatusAndamentoAtivoPorPAAndamentoProcesso(
            EntityManager em, Long id) throws DatabaseException {

        return super.getListNamedQuery(em, 
                "PAStatusAndamento.getPAStatusAndamentoAtivoPorPAAndamentoProcesso", 
                id, AtivoEnum.ATIVO);
    }
}