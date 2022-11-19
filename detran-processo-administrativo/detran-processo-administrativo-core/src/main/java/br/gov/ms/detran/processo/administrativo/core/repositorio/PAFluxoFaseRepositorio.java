package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.persistencia.util.JPAUtil;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.constante.ConstantsPersistence;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAPrioridadeFluxoAmparo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class PAFluxoFaseRepositorio extends AbstractJpaDAORepository<PAFluxoFase> {

    /**
     * 
     * @param em
     * @param andamentoProcesso
     * @param fluxoProcesso
     * @return
     * @throws AppException 
     */
    public PAFluxoFase getPAFluxoFasePorAndamentoProcessoEPrioridadeFluxoAmparo(
        EntityManager em, PAAndamentoProcesso andamentoProcesso, PAFluxoProcesso fluxoProcesso) throws AppException {
        
        if(andamentoProcesso == null || andamentoProcesso.getId() == null
                || fluxoProcesso == null || fluxoProcesso.getId() == null) {
            
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<PAFluxoFase> lPAFluxoFase 
            = getListNamedQuery(
                em, 
                "PAFluxoFase.getPAFluxoFasePorAndamentoProcessoEPrioridadeFluxoAmparo", 
                fluxoProcesso.getId(),
                andamentoProcesso.getId(),
                AtivoEnum.ATIVO
            );
        
        if(DetranCollectionUtil.ehNuloOuVazio(lPAFluxoFase) || lPAFluxoFase.size() > 1) {
            DetranWebUtils.applicationMessageException("PA.M1");
        }
                
        return lPAFluxoFase.get(0);
    }

    /**
     * @param em
     * @param processoAdministrativo
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public PAFluxoFase getFluxoFaseDoProcessoAdministrativo(
            EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        if(processoAdministrativo == null || processoAdministrativo.getId() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        PAOcorrenciaStatus ocorrencia 
            = new PAOcorrenciaStatusRepositorio()
                .getPAOcorrenciaStatusAtiva(em, processoAdministrativo.getId());
        
        if(ocorrencia == null 
                || ocorrencia.getStatusAndamento() == null
                || ocorrencia.getStatusAndamento().getId() == null
                || ocorrencia.getStatusAndamento().getAndamentoProcesso() == null
                || ocorrencia.getStatusAndamento().getAndamentoProcesso().getId() == null) {
            
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<PAFluxoFase> lPAFluxoFase 
            = super.getListNamedQuery(
                em, 
                "PAFluxoFase.getFluxoFaseDoProcessoAdministrativo", 
                ocorrencia.getStatusAndamento().getAndamentoProcesso().getId(),
                ocorrencia.getFluxoProcesso().getId(),
                AtivoEnum.ATIVO
            );
        
        
        
        if(DetranCollectionUtil.ehNuloOuVazio(lPAFluxoFase) || lPAFluxoFase.size() > 1) {
            DetranWebUtils.applicationMessageException("PA.M1");
        }
        
        return lPAFluxoFase.get(0);
    }
    
    public PAFluxoFase getFluxoFaseDoProcessoAdministrativoParaConsulta(
            EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        if(processoAdministrativo == null || processoAdministrativo.getId() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        PAOcorrenciaStatus ocorrencia 
            = new PAOcorrenciaStatusRepositorio()
                .getPAOcorrenciaStatusAtiva(em, processoAdministrativo.getId());
        
        if(ocorrencia == null 
                || ocorrencia.getStatusAndamento() == null
                || ocorrencia.getStatusAndamento().getId() == null
                || ocorrencia.getStatusAndamento().getAndamentoProcesso() == null
                || ocorrencia.getStatusAndamento().getAndamentoProcesso().getId() == null) {
            
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<PAFluxoFase> lPAFluxoFase 
            = super.getListNamedQuery(
                em, 
                "PAFluxoFase.getFluxoFaseDoProcessoAdministrativo", 
                ocorrencia.getStatusAndamento().getAndamentoProcesso().getId(),
                ocorrencia.getFluxoProcesso().getId(),
                AtivoEnum.ATIVO
            );
        
        
        return DetranCollectionUtil.ehNuloOuVazio(lPAFluxoFase) ? null: lPAFluxoFase.get(0);
            
    }
    /**
     * 
     * @param em
     * @param prioridadeFluxoAmparo
     * @return
     * @throws AppException 
     */
    public PAFluxoFase getFluxoFasePorPrioridadeFluxoAmparoOrdenadoAscendentePorPrioridade(
        EntityManager em, PAPrioridadeFluxoAmparo prioridadeFluxoAmparo) throws AppException {
        
        if(prioridadeFluxoAmparo == null || prioridadeFluxoAmparo.getId() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<PAFluxoFase> lPAFluxoFase 
            = getListNamedQuery(
                em, 
                "PAFluxoFase.getFluxoFasePorPrioridadeFluxoAmparoOrdenadoAscendentePorPrioridade", 
                prioridadeFluxoAmparo.getId(),
                AtivoEnum.ATIVO
            );
        
        if(DetranCollectionUtil.ehNuloOuVazio(lPAFluxoFase)) {
            DetranWebUtils.applicationMessageException("PA.M1");
        }
                
        return lPAFluxoFase.get(0);
    }

    public List<PAFluxoFase> getPAFluxoFaseAtivoPorPAAndamentoProcesso(
            EntityManager em, Long PAAndamentoProcessoId) throws DatabaseException {
        return super.getListNamedQuery(em, 
                "PAFluxoFase.getPAFluxoFaseAtivoPorPAAndamentoProcesso", 
                PAAndamentoProcessoId, AtivoEnum.ATIVO);
    }
    
    /**
     * Recuperar valor prioridade
     *
     * @param em
     * @param idPrioridadeFluxoAmparo
     * @return
     * @throws DatabaseException
     */
    public Integer recuperarUltimoValorPrioridadePAFluxoFase(EntityManager em, Long idPrioridadeFluxoAmparo) throws DatabaseException {

        try {
            Query query = em.createNamedQuery("PAFluxoFase.getMaxOrdem");
            Object[] params = new Object[1];
            params[0] = idPrioridadeFluxoAmparo;
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
    
    public PAFluxoFase getPAFluxoFasePosterior
        (EntityManager em, Long andamentoProcessoAdmId, Integer prioridade) throws DatabaseException {
        return super.getNamedQuery(em, "PAFluxoFase.getPAFluxoFasePosterior",
                andamentoProcessoAdmId, prioridade, AtivoEnum.ATIVO);
    }
    /**
     *
     * @param em
     * @param idPAFluxoFase
     * @throws DatabaseException
     */
    public void alterarOrdemPasso1(EntityManager em,
            Long idPAFluxoFase) throws DatabaseException {
        super.executeNamedQuery(em, "PAFluxoFase.alterarOrdemPasso1", idPAFluxoFase);
    }

    /**
     *
     * @param em
     * @param novaOrdem
     * @param idPrioridadeFluxoAmparo
     * @throws DatabaseException
     */
    public void alterarOrdemPasso2(EntityManager em,
            Integer novaOrdem, Long idPrioridadeFluxoAmparo) throws DatabaseException {
        super.executeNamedQuery(em, "PAFluxoFase.alterarOrdemPasso2", novaOrdem, idPrioridadeFluxoAmparo);
    }

    /**
     *
     * @param em
     * @param prioridadeRegistroSelecionado
     * @param idPrioridadeFluxoAmparo
     * @param novaOrdem
     * @throws DatabaseException
     */
    public void alterarOrdemPasso3(EntityManager em,
            Integer prioridadeRegistroSelecionado,
            Long idPrioridadeFluxoAmparo,
            Integer novaOrdem) throws DatabaseException {

        super.executeNamedQuery(em, "PAFluxoFase.alterarOrdemPasso3", prioridadeRegistroSelecionado, idPrioridadeFluxoAmparo, novaOrdem);
    }

    /**
     *
     * @param em
     * @param novaOrdem
     * @param idPAFluxoFase
     * @throws DatabaseException
     */
    public void alterarOrdemPasso4(EntityManager em,
            Integer novaOrdem,
            Long idPAFluxoFase) throws DatabaseException {

        super.executeNamedQuery(em, "PAFluxoFase.alterarOrdemPasso4", idPAFluxoFase, novaOrdem);
    }

    /**
     *
     * @param em
     * @param idPAFluxoFase
     * @throws DatabaseException
     */
    public void alterarOrdemPasso5(EntityManager em,
            Long idPAFluxoFase) throws DatabaseException {
        super.executeNamedQuery(em, "PAFluxoFase.alterarOrdemPasso5", idPAFluxoFase);
    }

    /**
     *
     * @param em
     * @param idPrioridadeFluxoAmparo
     * @throws DatabaseException
     */
    public void alterarOrdemPasso6(EntityManager em,
            Long idPrioridadeFluxoAmparo) throws DatabaseException {
        super.executeNamedQuery(em, "PAFluxoFase.alterarOrdemPasso6", idPrioridadeFluxoAmparo);
    }
    
    /**
     *
     * @param em
     * @param paFluxoFaseId
     * @return
     * @throws DatabaseException
     */
    public PAFluxoFase getPAFluxoFasePorId(
            EntityManager em, Long paFluxoFaseId) throws DatabaseException {
        return  super.getNamedQuery(em, "PAFluxoFase.getPAFluxoFasePorId", paFluxoFaseId);
    }
    
    /**
     *
     * @param em
     * @param idPrioridadeFluxoAmparo
     * @param prioridade
     * @return
     * @throws DatabaseException
     */
    public List<PAFluxoFase> getPAFluxoFasePorPrioridadeFluxoAmparoEMaioresPrioridadesDESC(
            EntityManager em, Long idPrioridadeFluxoAmparo, Integer prioridade) throws DatabaseException {
        return super.getListNamedQuery(em, "PAFluxoFase.getPAFluxoFasePorPrioridadeFluxoAmparoEMaioresPrioridadesDESC", 
                idPrioridadeFluxoAmparo, prioridade,AtivoEnum.ATIVO );
    }

    /**
     *
     * @param em
     * @param idPrioridadeFluxoAmparo
     * @param prioridade
     * @return
     * @throws DatabaseException
     */
    public List<PAFluxoFase> getPAFluxoFasePorPrioridadeFluxoAmparoEMaioresPrioridadesASC(
            EntityManager em, Long idPrioridadeFluxoAmparo, Integer prioridade) throws DatabaseException {
        return super.getListNamedQuery(em, "PAFluxoFase.getPAFluxoFasePorPrioridadeFluxoAmparoEMaioresPrioridadesASC", 
                idPrioridadeFluxoAmparo, prioridade, AtivoEnum.ATIVO);
    }
    /**
     *
     * @param em
     * @param idPrioridadeFluxoAmparo
     * @return
     * @throws DatabaseException
     */
    public List<PAFluxoFase> getPAFluxoFasePorPrioridadeFluxoAmparoVinculos(
            EntityManager em, Long idPrioridadeFluxoAmparo) throws DatabaseException {
        return super.getListNamedQuery(em, "PAFluxoFase.getPAFluxoFasePorPrioridadeFluxoAmparoVinculos", 
                idPrioridadeFluxoAmparo, AtivoEnum.ATIVO);
    }
}