package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.Notificacao;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoCorpoTexto;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.PATipoCorpoAndamento;
import br.gov.ms.detran.processo.administrativo.enums.ComissaoAnaliseEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import java.util.List;
import javax.persistence.EntityManager;

public class PATipoCorpoAndamentoRepositorio extends AbstractJpaDAORepository<PATipoCorpoAndamento> {

    /**
     * Retorna PATipoCorpoAndamento buscado por tipoNotificacaoEnum e ApoioOrigemInstauracao.id
     * 
     * @param em
     * @param tipoNotificacao
     * @param origemApoio
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public PATipoCorpoAndamento getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracao(
        EntityManager em, TipoFasePaEnum tipoNotificacao, ApoioOrigemInstauracao origemApoio) throws DatabaseException {
        
        return 
            super.getNamedQuery(
                em, 
                "PATipoCorpoAndamento.getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracao", 
                tipoNotificacao, 
                origemApoio.getId(),
                AtivoEnum.ATIVO
            );
    }
    
    /**
     * Retorna PATipoCorpoAndamento buscado por tipoNotificacaoEnum e ApoioOrigemInstauracao.id
     * 
     * @param em
     * @param tipoNotificacao
     * @param origemApoio
     * @param comissaoAnalise
     * @param tipoCorpoTexto
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public PATipoCorpoAndamento getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracaoEComissaoAnaliseETipoCorpoTexto(
        EntityManager em, TipoFasePaEnum tipoNotificacao, ApoioOrigemInstauracao origemApoio, ComissaoAnaliseEnum comissaoAnalise, TipoCorpoTexto tipoCorpoTexto) throws DatabaseException {
        
        return 
            super.getNamedQuery(
                em, 
                "PATipoCorpoAndamento.getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracaoEComissaoAnaliseETipoCorpoTexto", 
                tipoNotificacao, 
                origemApoio.getId(),
                AtivoEnum.ATIVO,
                comissaoAnalise,
                tipoCorpoTexto.getId()
            );
    }
    
    public PATipoCorpoAndamento getPATipoCorpoAndamentoAtivoPorPA(
        EntityManager em, Long idPA) throws DatabaseException {
        
        return 
            super.getNamedQuery(
                em, 
                "PATipoCorpoAndamento.getPATipoCorpoAndamentoAtivoPorPA", 
                idPA, 
                AtivoEnum.ATIVO
            );
    }
    
    public PATipoCorpoAndamento getPATipoCorpoAndamentoPorIDRecurso(
        EntityManager em, Long idRecurso) throws DatabaseException {
        
        return 
            super.getNamedQuery(
                em, 
                "PATipoCorpoAndamento.getPATipoCorpoAndamentoPorIDRecurso", 
                idRecurso, 
                AtivoEnum.ATIVO
            );
    }
    
    
    public List<PATipoCorpoAndamento> getTipoCorpoAndamentoAtivoPorPAFluxoProcessoEdital(
        EntityManager em, Long id) throws DatabaseException {
        return super.getListNamedQuery(em, 
          "PATipoCorpoAndamento.getPATipoCorpoAndamentoAtivoPorPAFluxoProcessoEdital", 
                id, AtivoEnum.ATIVO);
    }
    
    public List<PATipoCorpoAndamento> getTipoCorpoAndamentoAtivoPorPAFluxoProcessoRecurso(
        EntityManager em, Long id) throws DatabaseException {
        return super.getListNamedQuery(em, 
          "PATipoCorpoAndamento.getPATipoCorpoAndamentoAtivoPorPAFluxoProcessoRecurso", 
                id, AtivoEnum.ATIVO);
    }
    
    public List<PATipoCorpoAndamento> getTipoCorpoAndamentoPorIDApoioOrigemInstauraucao(
        EntityManager em, Long id) throws DatabaseException {
        return super.getListNamedQuery(em, 
          "PATipoCorpoAndamento.getPATipoCorpoAndamentoPorIDApoioOrigemInstauraucao", 
                id, AtivoEnum.ATIVO);
    }
    
    public Integer getCodigoAndamentoPorTipoNotificacaoEOrigemPA(EntityManager em, TipoFasePaEnum tipo, IBaseEntity origemApoio) throws AppException {
        
        PATipoCorpoAndamento tipoCorpoAndamento = 
                getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracao(
                                em, 
                                tipo, 
                                (ApoioOrigemInstauracao) origemApoio);
        
        if (tipoCorpoAndamento == null
                || tipoCorpoAndamento.getFluxoFase() == null
                || tipoCorpoAndamento.getFluxoFase().getAndamentoProcesso() == null) {
            
            DetranWebUtils.applicationMessageException("Não foi possível recuperar o andamento do tipo notificação {0}", "", tipo.name());
        }
        
        return tipoCorpoAndamento.getFluxoFase().getAndamentoProcesso().getCodigo();
    }
    
    /**
     *
     * @param em
     * @param idFluxoFase
     * @return
     * @throws DatabaseException
     */
    public List<PATipoCorpoAndamento> getPATipoCorpoAndamentoPorPAFluxoFaseVinculos(
            EntityManager em, Long idFluxoFase) throws DatabaseException {
        return super.getListNamedQuery(em, "PATipoCorpoAndamento.getPATipoCorpoAndamentoPorPAFluxoFaseVinculos", 
                idFluxoFase, AtivoEnum.ATIVO);
    }
}