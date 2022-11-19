package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.persistencia.util.JPAUtil;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.constante.ConstantsPersistence;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAPrioridadeFluxoAmparo;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class PAPrioridadeFluxoAmparoRepositorio extends AbstractJpaDAORepository<PAPrioridadeFluxoAmparo> {

    /**
     *
     * @param em
     * @param fluxoProcesso
     * @return
     * @throws AppException
     */
    public PAPrioridadeFluxoAmparo getPrioridadeFluxoAmparoAtivoPorFluxoProcesso(
            EntityManager em, PAFluxoProcesso fluxoProcesso) throws AppException {

        if (fluxoProcesso == null || fluxoProcesso.getId() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        List<PAPrioridadeFluxoAmparo> lPAPrioridadeFluxoAmparo
                = getListNamedQuery(
                        em,
                        "PAPrioridadeFluxoAmparo.getPrioridadeFluxoAmparoAtivoPorFluxoProcesso",
                        fluxoProcesso.getId(),
                        AtivoEnum.ATIVO
                );

        if (DetranCollectionUtil.ehNuloOuVazio(lPAPrioridadeFluxoAmparo)) {
            DetranWebUtils.applicationMessageException("PA.M1");
        }

        return lPAPrioridadeFluxoAmparo.get(0);
    }

    public List<PAPrioridadeFluxoAmparo> getPAPrioridadeFluxoAmparoAtivoPorPAFaseProcessoAdm(EntityManager em, Long PAFaseProcessoAdmId) throws DatabaseException {
        return super.getListNamedQuery(em, "PAPrioridadeFluxoAmparo.getPAPrioridadeFluxoAmparoAtivoPorPAFaseProcessoAdm",
                PAFaseProcessoAdmId, AtivoEnum.ATIVO);
    }
    
    public PAPrioridadeFluxoAmparo getPAPrioridadeFluxoAmparoPorPAFaseProcessoAdmEPrioridade
        (EntityManager em, Long PAFaseProcessoAdmId, Integer prioridade) throws DatabaseException {
        return super.getNamedQuery(em, "PAPrioridadeFluxoAmparo.getPAPrioridadeFluxoAmparoPorPAFaseProcessoAdmEPrioridade",
                PAFaseProcessoAdmId, prioridade);
    }

    public PAPrioridadeFluxoAmparo getPAPrioridadeFluxoAmparoPosterior
        (EntityManager em, Long PAFaseProcessoAdmId, Integer prioridade) throws DatabaseException {
        return super.getNamedQuery(em, "PAPrioridadeFluxoAmparo.getPAPrioridadeFluxoAmparoPosterior",
                PAFaseProcessoAdmId, prioridade, AtivoEnum.ATIVO);
    }
        
    public List<PAPrioridadeFluxoAmparo> getPAPrioridadeFluxoAmparoAtivoPorPAFluxoProcessoo(
            EntityManager em, Long id) throws DatabaseException {

        return super.getListNamedQuery(em,
                "PAPrioridadeFluxoAmparo.getPAPrioridadeFluxoAmparoAtivoPorPAFluxoProcessoo",
                id, AtivoEnum.ATIVO);
    }

    public Object getUltimaPrioridadeDoFluxoDaFaseProcesso(EntityManager em, Long idPaFluxoProcesso) throws DatabaseException {
        return super.getNamedQuery(em, "PAPrioridadeFluxoAmparo.getUltimoPrioridadeDoFluxoDaFaseProcesso", idPaFluxoProcesso);
    }

    /**
     * Recuperar valor prioridade
     *
     * @param em
     * @param idFluxoProcessoAdm
     * @return
     * @throws DatabaseException
     */
    public Integer recuperarUltimoValorPrioridadePAPrioridadeFluxoAmparo(EntityManager em, Long idFluxoProcessoAdm) throws DatabaseException {

        try {
            Query query = em.createNamedQuery("PAPrioridadeFluxoAmparo.getMaxOrdem");
            Object[] params = new Object[1];
            params[0] = idFluxoProcessoAdm;
            JPAUtil.setParameters(query, params);
            Object result = query.getSingleResult();
            if(query.getSingleResult() == null){
                result = 0;
            }
            return (Integer) result;

        } catch (Exception e) {
            //log.error("Erro ao executar a pesquisa", e);
            throw new DatabaseException(e,
                    ConstantsPersistence.ERRO_AO_EXCLUIR_ENTIDADE_ID.toString(),
                    ConstantsPersistence.ERRO_AO_BUSCAR_ENTIDADE_DESC);
        }

    }
    // PASSOS PARA A ALTERAÇÃO DE PRIORIDADE
    /**
     *
     * @param em
     * @param idPAPrioridadeFluxoAmparo
     * @throws DatabaseException
     */
    public void alterarOrdemPasso1(EntityManager em,
            Long idPAPrioridadeFluxoAmparo) throws DatabaseException {
        super.executeNamedQuery(em, "PAPrioridadeFluxoAmparo.alterarOrdemPasso1", idPAPrioridadeFluxoAmparo);
    }

    /**
     *
     * @param em
     * @param novaOrdem
     * @param idPaFluxoProcesso
     * @throws DatabaseException
     */
    public void alterarOrdemPasso2(EntityManager em,
            Integer novaOrdem, Long idPaFluxoProcesso) throws DatabaseException {
        super.executeNamedQuery(em, "PAPrioridadeFluxoAmparo.alterarOrdemPasso2", novaOrdem, idPaFluxoProcesso);
    }

    /**
     *
     * @param em
     * @param prioridadeRegistroSelecionado
     * @param idPaFluxoProcesso
     * @param novaOrdem
     * @throws DatabaseException
     */
    public void alterarOrdemPasso3(EntityManager em,
            Integer prioridadeRegistroSelecionado,
            Long idPaFluxoProcesso,
            Integer novaOrdem) throws DatabaseException {

        super.executeNamedQuery(em, "PAPrioridadeFluxoAmparo.alterarOrdemPasso3", prioridadeRegistroSelecionado, idPaFluxoProcesso, novaOrdem);
    }

    /**
     *
     * @param em
     * @param novaOrdem
     * @param idPAPrioridadeFluxoAmparo
     * @throws DatabaseException
     */
    public void alterarOrdemPasso4(EntityManager em,
            Integer novaOrdem,
            Long idPAPrioridadeFluxoAmparo) throws DatabaseException {

        super.executeNamedQuery(em, "PAPrioridadeFluxoAmparo.alterarOrdemPasso4", idPAPrioridadeFluxoAmparo, novaOrdem);
    }

    /**
     *
     * @param em
     * @param idPAPrioridadeFluxoAmparo
     * @throws DatabaseException
     */
    public void alterarOrdemPasso5(EntityManager em,
            Long idPAPrioridadeFluxoAmparo) throws DatabaseException {
        super.executeNamedQuery(em, "PAPrioridadeFluxoAmparo.alterarOrdemPasso5", idPAPrioridadeFluxoAmparo);
    }

    /**
     *
     * @param em
     * @param idPaFluxoProcesso
     * @throws DatabaseException
     */
    public void alterarOrdemPasso6(EntityManager em,
            Long idPaFluxoProcesso) throws DatabaseException {
        super.executeNamedQuery(em, "PAPrioridadeFluxoAmparo.alterarOrdemPasso6", idPaFluxoProcesso);
    }

    /**
     *
     * @param em
     * @param idPaFluxoProcesso
     * @param prioridade
     * @return
     * @throws DatabaseException
     */
    public List<PAPrioridadeFluxoAmparo> getFaseFluxoAmparoPorFasesFluxoAmparoEMaioresPrioridadesDESC
        (EntityManager em, Long idPaFluxoProcesso, Integer prioridade) throws DatabaseException {
        return super.getListNamedQuery(em, "PAPrioridadeFluxoAmparo.getFaseFluxoAmparoPorFasesFluxoAmparoEMaioresPrioridadesDESC", 
                idPaFluxoProcesso, prioridade, AtivoEnum.ATIVO);
    }

    /**
     *
     * @param em
     * @param idPaFluxoProcesso
     * @param prioridade
     * @return
     * @throws DatabaseException
     */
    public List<PAPrioridadeFluxoAmparo> getFaseFluxoAmparoPorFasesFluxoAmparoEMaioresPrioridadesASC(
            EntityManager em, Long idPaFluxoProcesso, Integer prioridade) throws DatabaseException {
        return super.getListNamedQuery(em, "PAPrioridadeFluxoAmparo.getFaseFluxoAmparoPorFasesFluxoAmparoEMaioresPrioridadesASC", 
                idPaFluxoProcesso, prioridade, AtivoEnum.ATIVO);
    }

    /**
     *
     * @param em
     * @param paPrioridadeFluxoAmparoId
     * @return
     * @throws DatabaseException
     */
    public PAPrioridadeFluxoAmparo getPAPrioridadeFluxoAmparoPorId
        (EntityManager em, Long paPrioridadeFluxoAmparoId) throws DatabaseException {
        return (PAPrioridadeFluxoAmparo) super.getNamedQuery
        (em, "PAPrioridadeFluxoAmparo.getPAPrioridadeFluxoAmparoPorId", 
                paPrioridadeFluxoAmparoId);
    }
    /**
     *
     * @param em
     * @param idPaFluxoProcesso
     * @param prioridadeMenor
     * @param prioridadeMaior
     * @return
     * @throws DatabaseException
     */
    public List<PAPrioridadeFluxoAmparo> getFasesProcessosPorFasesProcessosEIntervaloDePrioridades
        (EntityManager em, Long idPaFluxoProcesso, Integer prioridadeMenor, Integer prioridadeMaior) throws DatabaseException {
        return super.getListNamedQuery(em, "PAPrioridadeFluxoAmparo.getFasesProcessosPorFasesProcessosEIntervaloDePrioridades",
                idPaFluxoProcesso, prioridadeMenor, prioridadeMaior);
    }
}
