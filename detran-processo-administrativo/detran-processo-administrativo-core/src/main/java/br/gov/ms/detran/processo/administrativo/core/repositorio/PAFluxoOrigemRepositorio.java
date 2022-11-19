package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoOrigem;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import java.util.List;
import javax.persistence.EntityManager;

public class PAFluxoOrigemRepositorio extends AbstractJpaDAORepository<PAFluxoOrigem> {
    
    /**
     * 
     * @param em
     * @param apoioOrigemInstauracao
     * @param pAFluxoProcesso
     * @throws AppException 
     */
    public void validaMudancaFluxoPorOrigemEFluxoProcesso(
        EntityManager em, ApoioOrigemInstauracao apoioOrigemInstauracao, PAFluxoProcesso pAFluxoProcesso) throws AppException {

        if(apoioOrigemInstauracao == null 
                || apoioOrigemInstauracao.getId() == null
                || pAFluxoProcesso == null 
                || pAFluxoProcesso.getId() == null) {
            
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        PAFluxoOrigem lOrigem 
            = getFluxoOrigemPorOrigemEFluxoProcesso(em, apoioOrigemInstauracao, pAFluxoProcesso);
        
        if(lOrigem == null) {
            DetranWebUtils.applicationMessageException("PA.M2");
        }
    }

    public PAFluxoOrigem getFluxoOrigemPorOrigemEFluxoProcesso(
            EntityManager em, ApoioOrigemInstauracao apoioOrigemInstauracao, PAFluxoProcesso pAFluxoProcesso) throws AppException {
       
        List<PAFluxoOrigem> lOrigem
                = getListNamedQuery(
                        em,
                        "PAFluxoOrigem.getPAFluxoOrigemPorOrigemEFluxoProcesso",
                        apoioOrigemInstauracao.getId(),
                        pAFluxoProcesso.getId(),
                        AtivoEnum.ATIVO
                );
        if(DetranCollectionUtil.ehNuloOuVazio(lOrigem) || lOrigem.size() > 1) {
            DetranWebUtils.applicationMessageException("PA.M2");
        }
        return lOrigem.get(0);
    }
    
    public List<PAFluxoOrigem> getPAFluxoOrigemAtivoPorPAFluxoProcesso(
            EntityManager em, Long id) throws DatabaseException {

        return super.getListNamedQuery(em, 
                "PAFluxoOrigem.getPAFluxoOrigemAtivoPorPAFluxoProcesso", 
                id, AtivoEnum.ATIVO);
    }
    
    public PAFluxoOrigem getPAFluxoOrigemPorIDApoioOrigemInstauracao(
            EntityManager em, Long id) throws DatabaseException {
          List<PAFluxoOrigem> result = getListNamedQuery(em, 
                "PAFluxoOrigem.getPAFluxoOrigemPorIDApoioOrigemInstauracao", 
                id, AtivoEnum.ATIVO);
        return DetranCollectionUtil.ehNuloOuVazio(result) ? null : result.get(0);
    }

    /**
     * @param em
     * @param apoioOrigemInstauracaoId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public PAFluxoOrigem getPAFluxoOrigemPorApoioOrigemInstauracaoParaProcessoJudicial(EntityManager em, 
                                                                                       Long apoioOrigemInstauracaoId) throws DatabaseException {
        return super.getNamedQuery(em, "PAFluxoOrigem.getPAFluxoOrigemPorApoioOrigemInstauracaoParaProcessoJudicial", 
                                   apoioOrigemInstauracaoId,
                                   AtivoEnum.ATIVO,
                                   Boolean.TRUE);
    }
}