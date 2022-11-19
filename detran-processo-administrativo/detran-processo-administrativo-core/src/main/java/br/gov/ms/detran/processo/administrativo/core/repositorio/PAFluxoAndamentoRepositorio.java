package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import java.util.List;
import javax.persistence.EntityManager;

public class PAFluxoAndamentoRepositorio extends AbstractJpaDAORepository<PAFluxoAndamento> {

    /**
     * 
     * Retorna o fluxo de mudança para um determinado PA baseado no seu andamento atual.
     * 
     * @param em
     * @param idProcessoAdm
     * @param codigoPAFluxoProcesso
     * @return
     * @throws AppException 
     */
    public PAFluxoAndamento getFluxoAndamentoPorPAEFluxoProcesso(EntityManager em, Long idProcessoAdm, Integer codigoPAFluxoProcesso) throws AppException {
        
        if (idProcessoAdm == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<PAFluxoAndamento> lPAFluxoAndamento = 
                getListNamedQuery(em, 
                                  "PAFluxoAndamento.getFluxoAndamentoPorPAEFluxoProcesso", 
                                  idProcessoAdm, 
                                  AtivoEnum.ATIVO, 
                                  codigoPAFluxoProcesso);

        if (DetranCollectionUtil.ehNuloOuVazio(lPAFluxoAndamento)) {
            DetranWebUtils.applicationMessageException("Fluxo andamento não encontrado para o processo administrativo.");
        } else if (lPAFluxoAndamento.size() > 1) {
            DetranWebUtils.applicationMessageException("Encontrado mais de um fluxo andamento para o processo administrativo.");
        }
        
        return lPAFluxoAndamento.get(0);
    }

    /**
     * @param em
     * @param paFluxoFase
     * @param listaPaFluxoProcesso
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public BaseEntityAtivo getPaFluxoAndamentoPorPAFluxoFaseEPAFluxoProcesso(EntityManager em, 
                                                                             PAFluxoFase paFluxoFase, 
                                                                             List listaPaFluxoProcesso) throws DatabaseException {
        List<PAFluxoAndamento> lPAFluxoAndamento = 
                getListNamedQuery(em, 
                                  "PAFluxoAndamento.getPaFluxoAndamentoPorPAFluxoFaseEPAFluxoProcesso", 
                                  paFluxoFase.getId(), 
                                  listaPaFluxoProcesso);

        return DetranCollectionUtil.ehNuloOuVazio(lPAFluxoAndamento) ? null : lPAFluxoAndamento.get(0);
    }
    
    public List<PAFluxoAndamento> getPAFluxoAndamentoAtivoPorPAFluxoProcesso(
            EntityManager em, Long id) throws DatabaseException {

        return super.getListNamedQuery(em, 
                "PAFluxoAndamento.getPAFluxoAndamentoAtivoPorPAFluxoProcesso", 
                id, AtivoEnum.ATIVO);
    }
    /**
     *
     * @param em
     * @param idFluxoFase
     * @return
     * @throws DatabaseException
     */
    public List<PAFluxoAndamento> getPAFluxoAndamentoPorPAFluxoFaseVinculos(
            EntityManager em, Long idFluxoFase) throws DatabaseException {
        return super.getListNamedQuery(em, "PAFluxoAndamento.getPAFluxoAndamentoPorPAFluxoFaseVinculos", 
                idFluxoFase, AtivoEnum.ATIVO);
    }
}