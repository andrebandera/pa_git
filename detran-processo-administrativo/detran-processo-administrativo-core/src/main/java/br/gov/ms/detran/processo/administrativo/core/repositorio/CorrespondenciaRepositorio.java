package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.protocolo.entidade.Correspondencia;
import javax.persistence.EntityManager;

public class CorrespondenciaRepositorio extends AbstractJpaDAORepository<Correspondencia> {

    /**
     * @param em
     * @param numeroNotificacao
     * @param numeroProcesso
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public Correspondencia getCorrespondenciaPorNumeroNotificacaoENumeroProcesso(
        EntityManager em, String numeroNotificacao, String numeroProcesso) throws AppException {
        
        if(numeroNotificacao == null || DetranStringUtil.ehBrancoOuNulo(numeroProcesso)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return 
            getNamedQuery(
                em, 
                "Correspondencia.getCorrespondenciaPorNumeroNotificacaoOuNumeroProcesso", 
                numeroNotificacao,
                numeroProcesso
            );
    }
    
    /**
     * 
     * @param em
     * @param idProcessoAdministrativo
     * @param tipo
     * @return
     * @throws AppException 
     */
    public Correspondencia getCorrespondenciaPorProcessoAdministrativoETipo(
                                                                            EntityManager em, 
                                                                            Long idProcessoAdministrativo, 
                                                                            TipoFasePaEnum tipo) throws AppException {
        
        if(idProcessoAdministrativo == null || tipo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return 
            getNamedQuery(
                em, 
                "Correspondencia.getCorrespondenciaPorProcessoAdministrativoETipo", 
                idProcessoAdministrativo,
                tipo,
                AtivoEnum.ATIVO
            );
    }
}