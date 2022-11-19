package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaCorreio;
import java.util.List;
import javax.persistence.EntityManager;

public class CorrespondenciaCorreioRepositorio extends AbstractJpaDAORepository<CorrespondenciaCorreio> {

    /**
     * 
     * @param em
     * @param numeroNotificacao
     * @param tipoNotificacaoProcesso
     * @return
     * @throws AppException 
     */
    public CorrespondenciaCorreio getCorrespondenciaCorreioPorNumeroETipoNotificacao(
            EntityManager em, String numeroNotificacao, TipoFasePaEnum tipoNotificacaoProcesso) throws AppException {
        
        if(numeroNotificacao == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<CorrespondenciaCorreio> listaCorrespondenciaCorreio 
            = getListNamedQuery(
                em, 
                "CorrespondenciaCorreio.getCorrespondenciaCorreioPorNumeroETipoNotificacao", 
                numeroNotificacao,
                tipoNotificacaoProcesso,
                AtivoEnum.ATIVO
            );
        
        if(DetranCollectionUtil.ehNuloOuVazio(listaCorrespondenciaCorreio) || listaCorrespondenciaCorreio.size() > 1) {
            DetranWebUtils.applicationMessageException("PA.M1");
        }
        
        return listaCorrespondenciaCorreio.get(0);
    }
    
    
    public CorrespondenciaCorreio getCorrespondenciaCorreioPorProcessoETipo(
            EntityManager em, Long idPA, TipoFasePaEnum tipoNotificacaoProcesso) throws AppException {
        
        if(idPA == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<CorrespondenciaCorreio> listaCorrespondenciaCorreio 
            = getListNamedQuery(
                em, 
                "CorrespondenciaCorreio.getCorrespondenciaCorreioPorProcessoETipo", 
                idPA,
                tipoNotificacaoProcesso,
                AtivoEnum.ATIVO
            );
        
        if(DetranCollectionUtil.ehNuloOuVazio(listaCorrespondenciaCorreio) || listaCorrespondenciaCorreio.size() > 1) {
            DetranWebUtils.applicationMessageException("PA.M1");
        }
        
        return listaCorrespondenciaCorreio.get(0);
    }
    
    /**
     * @param em
     * @param correspondenciaId
     * @return
     * @throws DatabaseException 
     */
    public CorrespondenciaCorreio getCorrespondenciaCorreioPorCorrespondenciaId(
        EntityManager em, Long correspondenciaId) throws DatabaseException {
        
        return 
            super.getNamedQuery(
                em, 
                "CorrespondenciaCorreio.getCorrespondenciaCorreioPorCorrespondenciaId", 
                correspondenciaId, 
                AtivoEnum.ATIVO
            );
    }
    
    /**
     * 
     * @param em
     * @param correspondenciaId
     * @return
     * @throws DatabaseException 
     */
    public CorrespondenciaCorreio getCorrespondenciaCorreioPorCorrespondenciaIdTodaEntidade(
        EntityManager em, Long correspondenciaId) throws DatabaseException {
        
        return 
            super.getNamedQuery(
                em, 
                "CorrespondenciaCorreio.getCorrespondenciaCorreioPorCorrespondenciaIdTodaEntidade", 
                correspondenciaId, 
                AtivoEnum.ATIVO
            );
    }

    /**
     * @param em
     * @param correspondenciaId
     * @return
     * @throws DatabaseException 
     */
    public CorrespondenciaCorreio getCorrespondenciaCorreioParaRetornoAr(EntityManager em, Long correspondenciaId) throws DatabaseException {
        return 
            super.getNamedQuery(
                em, 
                "CorrespondenciaCorreio.getCorrespondenciaCorreioParaRetornoAr", 
                correspondenciaId, 
                AtivoEnum.ATIVO
            );
    }
}
