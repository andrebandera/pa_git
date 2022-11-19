package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.core.projeto.entidade.inf.AmparoLegal;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracaoSituacaoEnum;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * @author yanko.campos
 */
public class ProcessoAdministrativoInfracaoRepositorio extends AbstractJpaDAORepository<ProcessoAdministrativoInfracao> {

    /**
     * Retorna todas as infracoes de todos os processos administrativos.
     * 
     * @param em
     * @return
     * @throws DatabaseException 
     */
    public List<ProcessoAdministrativoInfracao> getTodasInfracoesDeTodosProcessosAdministrativos(EntityManager em) throws DatabaseException {
        return this.getListNamedQuery(em, "ProcessoAdministrativoInfracao.findAll");
    }
    
    /**
     * 
     * @param em
     * @return
     * @throws DatabaseException 
     */
    public List<ProcessoAdministrativoInfracao> getInfracaoPAReincidenteMesmoArtigo(EntityManager em, String infracaoCodigo, List<Long> idsPA) throws DatabaseException {
        return this.getListNamedQuery(em, "ProcessoAdministrativoInfracao.getInfracaoPAReincidenteMesmoArtigo", infracaoCodigo, idsPA, AtivoEnum.ATIVO.ordinal());
    }

    public List<ProcessoAdministrativoInfracao> getInfracaoPAReincidenteMesmaInfracao(EntityManager em, String infracaoCodigo, List<Long> idsPA) throws DatabaseException {
        return this.getListNamedQuery(em, "ProcessoAdministrativoInfracao.getInfracaoPAReincidenteMesmaInfracao", infracaoCodigo, idsPA, AtivoEnum.ATIVO.ordinal());
    }

    /**
     * 
     * @param em
     * @param isn
     * @param cpf
     * @param auto
     * @param autuador
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List<ProcessoAdministrativoInfracao> getInfracaoPorInfracaoNumeroAutoOrgaoAutuador(EntityManager em, 
                                                                                            String codigoInfracao, 
                                                                                            String auto, 
                                                                                            Long autuador) throws AppException {
        
        if (DetranStringUtil.ehBrancoOuNulo(codigoInfracao)) {
            DetranWebUtils.applicationMessageException("Código Infração não pode ser nulo para realizar a consulta.");
        }
        
        if (DetranStringUtil.ehBrancoOuNulo(auto)) {
            DetranWebUtils.applicationMessageException("Auto não pode ser nulo para realizar a consulta.");
        }
        
        if (autuador == null) {
            DetranWebUtils.applicationMessageException("Autuador não pode ser nulo para realizar a consulta.");
        }
        
        return this.getListNamedQuery(em, 
                                      "ProcessoAdministrativoInfracao.getInfracaoPorInfracaoNumeroAutoOrgaoAutuador",
                                      codigoInfracao,
                                      auto,
                                      autuador, AtivoEnum.ATIVO);
    }
    
    /**
     * 
     * @param iBaseEntity
     * @param em
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativoInfracao> getInfracoesPorProcessoParaAEMNPP89(EntityManager em, IBaseEntity iBaseEntity) throws AppException {
        
        if(iBaseEntity == null || iBaseEntity.getId()== null) {
            DetranWebUtils.applicationMessageException("Processo Administrativo inválido.");
        }
        
        ProcessoAdministrativo processo = (ProcessoAdministrativo) iBaseEntity;
        
        List<ProcessoAdministrativoInfracaoSituacaoEnum> lSituacao 
            = DetranCollectionUtil
                .montaLista(
                    ProcessoAdministrativoInfracaoSituacaoEnum.CADASTRADO, 
                    ProcessoAdministrativoInfracaoSituacaoEnum.PENDENTE
                );
        
        return 
            getListNamedQuery(
                em, 
                "ProcessoAdministrativoInfracao.getInfracoesPorProcessoParaAEMNPP89",
                processo.getId(),
                lSituacao);
    }

    /**
     * Retorna lista de objeto com id e infração buscados por ProcessoAdministrativo.
     * 
     * @param em
     * @param processoAdministrativoId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public List<ProcessoAdministrativoInfracao> getInfracoesPorProcessoAdministrativo(EntityManager em, 
                                                                                      Long processoAdministrativoId) throws AppException {
        
        if (null == processoAdministrativoId) {
            DetranWebUtils.applicationMessageException("Isn não pode ser nulo para realizar a consulta.");
        }
        
        return this.getListNamedQuery(em, "ProcessoAdministrativoInfracao.getInfracoesPorProcessoAdministrativo", processoAdministrativoId);
    }

    public Integer getSomaPontuacaoDeInfracoesPA(EntityManager em, Long id) throws DatabaseException {
        Long somatorio = getCountHqlEntitySearch(em, "ProcessoAdministrativoInfracao.getSomaPontuacaoDeInfracoesPA", id, AtivoEnum.ATIVO);
        
        return somatorio == null ? null : somatorio.intValue();
    }

    /**
     * Retorna infracoes apenas com Extrato (auto) e Orgao autuador, buscados pelo processoAdministrativoId.
     * 
     * @param em
     * @param processoAdministrativoId
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativoInfracao> getExtratoEAutuadorECodigoInfracao(EntityManager em, Long processoAdministrativoId) throws AppException {
        return this.getListNamedQuery(em, "ProcessoAdministrativoInfracao.getExtratoEAutuadorECodigoInfracao", processoAdministrativoId);
    }
    
    /**
     * @param em
     * @param processoAdministrativoId
     * @return
     * @throws AppException 
     */
    public List<ProcessoAdministrativoInfracao> getInfracoesPorProcessoAdministrativoID(EntityManager em, Long processoAdministrativoId) throws AppException {
        return this.getListNamedQuery(em, "ProcessoAdministrativoInfracao.getInfracoesPorProcessoAdministrativoID", processoAdministrativoId);
    }

    /**
     * Retorna ProcessoAdministrativoInfracao buscado por numeroAuto e codigoInfracao.
     * 
     * @param em
     * @param numeroAuto
     * @param codigoInfracao
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public ProcessoAdministrativoInfracao getInfracaoPorNumeroAutoECodigoInfracao(
        EntityManager em, String numeroAuto, String codigoInfracao) throws DatabaseException {
        
        List<ProcessoAdministrativoInfracao> lInfracao 
            = super.getListNamedQuery(
                em, 
                "ProcessoAdministrativoInfracao.getInfracaoPorNumeroAutoECodigoInfracao", 
                numeroAuto, 
                codigoInfracao + "%"
            );
        
        return !DetranCollectionUtil.ehNuloOuVazio(lInfracao) ? lInfracao.get(0) : null;
    }
    
     /**
     * Retorna ProcessoAdministrativoInfracao buscado por numeroAuto e codigoInfracao.
     * 
     * @param em
     * @param numeroAuto
     * @param codigoInfracao
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public ProcessoAdministrativoInfracao getInfracaoPorNumeroAutoECodigoInfracaoOrderByAtivo(
        EntityManager em, String numeroAuto, String codigoInfracao) throws DatabaseException {
        
        List<ProcessoAdministrativoInfracao> lInfracao 
            = super.getListNamedQuery(
                em, 
                "ProcessoAdministrativoInfracao.getInfracaoPorNumeroAutoECodigoInfracaoOrderByAtivo", 
                numeroAuto, 
                codigoInfracao + "%"
            );
        
        return !DetranCollectionUtil.ehNuloOuVazio(lInfracao) ? lInfracao.get(0) : null;
    }
    
    public Object getAmparoLegalPorInfracaoId(EntityManager em, Long idInfracao) throws DatabaseException {
        return super.getNamedQuery(em, "ProcessoAdministrativoInfracao.getAmparoLegalPorInfracaoId", idInfracao);
    }
}