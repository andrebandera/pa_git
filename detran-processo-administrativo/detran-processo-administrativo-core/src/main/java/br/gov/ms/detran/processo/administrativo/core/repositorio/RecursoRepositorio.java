package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.criteria.RecursoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.Recurso;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import java.util.List;
import javax.persistence.EntityManager;

public class RecursoRepositorio extends AbstractJpaDAORepository<Recurso> {

    /**
     * @param em
     * @param criteria
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List getRecursoPorFiltros(EntityManager em, RecursoCriteria criteria) throws DatabaseException {
        return super.getListNamedQuery(em, "Recurso.getRecursoPorFiltros", 
                                       criteria.getFrom(), 
                                       criteria.getTo(), 
                                       montarParametrosFiltros(criteria));
    }

    /**
     * @param criteria
     * @return 
     */
    private Object[] montarParametrosFiltros(RecursoCriteria criteria) {

        Object[] params = {criteria.getCpf() != null ? criteria.getCpf() : null,
                !DetranStringUtil.ehBrancoOuNulo(criteria.getNumeroProcesso()) ? criteria.getNumeroProcesso() : null,
                criteria.getSituacao() != null ? criteria.getSituacao().name() : null,
                criteria.getAtivo() != null ? criteria.getAtivo().ordinal() : null,
                criteria.getId() != null ? criteria.getId() : null,
                criteria.getTipo() != null ? criteria.getTipo().name() : null,
                criteria.getResultado() != null ? criteria.getResultado().name() : null,
                DetranStringUtil.ehBrancoOuNulo(criteria.getUsuarioResultado()) ? null : "%" + criteria.getUsuarioResultado() + "%",
                criteria.getDataInicioResultado(),
                criteria.getDataFimResultado()
        };
        
        return params;
    }

    public Object getCountRecursoPorFiltros(EntityManager em, RecursoCriteria criteria) throws DatabaseException {
        return super.getNamedQuery(em, "Recurso.getCountRecursoPorFiltros", 
                                   montarParametrosFiltros(criteria));
    }
    
    /**
     * 
     * @param em
     * @param idProcessoAdministrativo
     * @param situacao
     * @return
     * @throws AppException 
     */
    public Recurso getRecursoAtivoPorProcessoAdministrativoESituacao(
        EntityManager em, Long idProcessoAdministrativo, SituacaoRecursoEnum situacao) throws AppException {
        
        if(idProcessoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<Recurso> lRecurso 
            = super.getListNamedQuery(
                em, 
                "Recurso.getRecursoPorProcessoAdministrativoESituacao", 
                idProcessoAdministrativo,
                AtivoEnum.ATIVO,
                situacao
            );
        
        return !DetranCollectionUtil.ehNuloOuVazio(lRecurso) ? lRecurso.get(0) : null;
    }

    /**
     * 
     * @param em
     * @param idProcessoAdministrativo
     * @return
     * @throws DatabaseException 
     */
    public Boolean naoExisteRecursoPenalizacaoComDestinoJARI(EntityManager em, Long idProcessoAdministrativo) throws DatabaseException {
        
        List<Recurso> recursos 
            = super.getListNamedQuery(
                em, 
                "Recurso.naoExisteRecursoPenalizacaoComDestinoJARI", 
                idProcessoAdministrativo, 
                TipoFasePaEnum.PENALIZACAO, 
                OrigemDestinoEnum.JARI,
                AtivoEnum.ATIVO
            );
        
        return DetranCollectionUtil.ehNuloOuVazio(recursos);
    }

    /**
     * 
     * @param em
     * @param idProcessoAdministrativo
     * @param tipoRecurso
     * @return
     * @throws AppException 
     */
    public Recurso getRecursoMaisRecentePorProcessoEFase(
        EntityManager em, Long idProcessoAdministrativo, TipoFasePaEnum tipoRecurso) throws AppException {
        
        if(idProcessoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<Recurso> lRecurso 
            = super.getListNamedQuery(
                em, 
                "Recurso.getRecursoMaisRecentePorProcessoEFase", 
                idProcessoAdministrativo,
                AtivoEnum.ATIVO,
                tipoRecurso
            );
        
        return !DetranCollectionUtil.ehNuloOuVazio(lRecurso) ? lRecurso.get(0) : null;
    }
    
    /**
     * 
     * @param em
     * @param idProcessoAdministrativo
     * @param codigoPAFluxoFase
     * @param tipoRecurso
     * @return
     * @throws AppException 
     */
    public List<Recurso> getRecursosDoProcessoAdministrativoNaMesmaFase(
        EntityManager em, Long idProcessoAdministrativo, String codigoPAFluxoFase, TipoFasePaEnum tipoRecurso) throws AppException {
        
        if(idProcessoAdministrativo == null || DetranStringUtil.ehBranco(codigoPAFluxoFase) || tipoRecurso == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return 
            super.getListNamedQuery(
                em, 
                "Recurso.getRecursosDoProcessoAdministrativoNaMesmaFase", 
                idProcessoAdministrativo,
                codigoPAFluxoFase.substring(0, 2) + "%",
                tipoRecurso,
                AtivoEnum.ATIVO
            );
    }
    
    /**
     * @param em
     * @param criteria
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List getRecursoPorFiltros2(EntityManager em, RecursoCriteria criteria) throws DatabaseException {
        return super.getListNamedQuery(em, "Recurso.getRecursoPorFiltros2", 
                                       criteria.getFrom(), 
                                       criteria.getTo(), 
                                       montarParametrosFiltros2(criteria));
    }
    
    /**
     * @param criteria
     * @return 
     */
    private Object[] montarParametrosFiltros2(RecursoCriteria criteria) {

        Object[] params = {criteria.getCpf() != null ? criteria.getCpf() : null,
                !DetranStringUtil.ehBrancoOuNulo(criteria.getNumeroProcesso()) ? criteria.getNumeroProcesso() : null,
                criteria.getSituacao() != null ? criteria.getSituacao().name() : null,
                AtivoEnum.ATIVO.ordinal(),
                criteria.getId() != null ? criteria.getId() : null,
                criteria.getTipo() != null ? criteria.getTipo().name() : null,
                criteria.getResultado() != null ? criteria.getResultado().name() : null,
                DetranStringUtil.ehBrancoOuNulo(criteria.getUsuarioResultado()) ? null : "%" + criteria.getUsuarioResultado() + "%",
                criteria.getDataInicioResultado(),
                criteria.getDataFimResultado(),
                criteria.getDataInicioProtolo(),
                criteria.getDataFimProtocolo(),
                criteria.getDestino() != null ? criteria.getDestino().ordinal() : null
        };
        
        return params;
    }
    
    public Object getCountRecursoPorFiltros2(EntityManager em, RecursoCriteria criteria) throws DatabaseException {
        return super.getNamedQuery(em, "Recurso.getCountRecursoPorFiltros2", 
                                   montarParametrosFiltros2(criteria));
    }
    
    /**
     * 
     * @param em
     * @param idProcessoAdministrativo
     * @param tipo
     * @param destino
     * @return
     * @throws AppException 
     */
    public Recurso getRecursoAtivoPorProcessoAdministrativoTipoDestinoENaoCancelado(
        EntityManager em, Long idProcessoAdministrativo, TipoFasePaEnum tipo, OrigemDestinoEnum destino) throws AppException {
        
        if(idProcessoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<Recurso> lRecurso 
            = super.getListNamedQuery(
                em, 
                "Recurso.getRecursoPorProcessoAdministrativoTipoDestinoENaoCancelado", 
                idProcessoAdministrativo,
                AtivoEnum.ATIVO,
                SituacaoRecursoEnum.CANCELADO,
                tipo,
                destino
            );
        
        return !DetranCollectionUtil.ehNuloOuVazio(lRecurso) ? lRecurso.get(0) : null;
    }
}