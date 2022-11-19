package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoComplemento;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

public class NotificacaoComplementoRepositorio extends AbstractJpaDAORepository<NotificacaoComplemento> {
    
    /**
     * 
     * @param em
     * @param idProcessoAdministrativo
     * @param tipoNotificacao
     * @return
     * @throws AppException 
     */
    public NotificacaoComplemento getInformacaoEditalPenalidadeParaProcessoAdministrativoParaNotificacaoPorTipo(
        EntityManager em, Long idProcessoAdministrativo, TipoFasePaEnum tipoNotificacao) throws AppException {
        
        if(idProcessoAdministrativo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<NotificacaoComplemento> lNotificacaoComplemento 
            = super.getListNamedQuery(
                em, 
                "NotificacaoComplemento.getInformacaoEditalPenalidadeParaProcessoAdministrativoParaNotificacaoPorTipo", 
                idProcessoAdministrativo, 
                AtivoEnum.ATIVO,
                tipoNotificacao
            );
        
        return !DetranCollectionUtil.ehNuloOuVazio(lNotificacaoComplemento) ? lNotificacaoComplemento.get(0) : null;
    }
    
    public List<NotificacaoComplemento> getListNotificacaoComplementoPorProcessoAdministrativo(EntityManager em, Long idProcesso)throws AppException{
        if(idProcesso == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return super.getListNamedQuery(
                em, 
                "NotificacaoComplemento.getListNotificacaoComplementoPorProcessoAdministrativo", 
                idProcesso, 
                AtivoEnum.ATIVO
            );
    }
    
    /**
     * @param em
     * @param idNotificacao
     * @return
     * @throws AppException 
     */
    public NotificacaoComplemento getComplementoPorNotificacao(EntityManager em, Long idNotificacao) throws AppException {
        
        if (idNotificacao == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return super.getNamedQuery(em, "NotificacaoComplemento.getComplementoPorNotificacao", idNotificacao, AtivoEnum.ATIVO);
    }

    /**
     * @param em
     * @param processoAdministrativoId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public Date getDataPenalizacaoPorProcessoAdministrativo(EntityManager em, Long processoAdministrativoId) throws AppException {
        
        if (null == processoAdministrativoId) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<NotificacaoComplemento> lista = 
                super.getListNamedQuery(em, 
                                        "NotificacaoComplemento.getDataPenalizacaoPorProcessoAdministrativo", 
                                        processoAdministrativoId, 
                                        AtivoEnum.ATIVO);
        
        NotificacaoComplemento notificacao = !DetranCollectionUtil.ehNuloOuVazio(lista) ? lista.get(0) : null;
        
        return notificacao != null ? notificacao.getData() : null;
    }

    public Date getMenorDataPenalizacaoDosPAs(EntityManager em, List<Long> idsPA) throws AppException {
        if (DetranCollectionUtil.ehNuloOuVazio(idsPA)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<NotificacaoComplemento> lista = 
                super.getListNamedQuery(em, 
                                        "NotificacaoComplemento.getMenorDataPenalizacaoDosPAs", 
                                        idsPA,
                                        AtivoEnum.ATIVO);
        
        NotificacaoComplemento notificacao = !DetranCollectionUtil.ehNuloOuVazio(lista) ? lista.get(0) : null;
        
        return notificacao != null ? notificacao.getData() : null;
    }
}