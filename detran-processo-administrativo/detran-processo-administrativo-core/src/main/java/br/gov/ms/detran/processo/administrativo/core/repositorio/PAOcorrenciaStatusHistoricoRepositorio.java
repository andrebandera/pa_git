package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatusHistorico;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import java.util.List;
import javax.persistence.EntityManager;

public class PAOcorrenciaStatusHistoricoRepositorio extends AbstractJpaDAORepository<PAOcorrenciaStatusHistorico> {

    /**
     * 
     * @param em
     * @param idProcessoAdministrativo
     * @return
     * @throws AppException 
     */
    public PAOcorrenciaStatusHistorico getUltimoHistorico(EntityManager em, Long idProcessoAdministrativo) throws AppException {

        if (idProcessoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        List<PAOcorrenciaStatusHistorico> ocorrencias
                = getListNamedQuery(em, "PAOcorrenciaStatusHistorico.getUltimoHistorico", idProcessoAdministrativo);

        if (DetranCollectionUtil.ehNuloOuVazio(ocorrencias)) {
            DetranWebUtils.applicationMessageException("PA Ocorrência Status está inválida.");
        }
        
        return ocorrencias.get(0);
    }
    
    /**
     * 
     * @param em
     * @param idProcessoAdministrativo
     * @param idPAStatusAndamento
     * @return
     * @throws AppException 
     */
    public PAOcorrenciaStatusHistorico getUltimoHistoricoDiferentePAStatusAndamentoAtual(EntityManager em, Long idProcessoAdministrativo, Long idPAStatusAndamento) throws AppException {

        if (idProcessoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        List<PAOcorrenciaStatusHistorico> ocorrencias
                = getListNamedQuery(em, "PAOcorrenciaStatusHistorico.getUltimoHistoricoDiferentePAStatusAndamentoAtual", idProcessoAdministrativo, idPAStatusAndamento);

        if (DetranCollectionUtil.ehNuloOuVazio(ocorrencias)) {
            DetranWebUtils.applicationMessageException("PA Ocorrência Status está inválida.");
        }
        
        return ocorrencias.get(0);
    }
}