package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.TemplateProtocolo;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoNotificacaoEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.entidade.Protocolo;
import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.core.iface.servico.hab.IControleCnhService;
import br.gov.ms.detran.processo.administrativo.wrapper.DesistenciaRecursoInstauracaoPenalizacaoWS;
import java.util.List;
import javax.persistence.EntityManager;

public class ProtocoloRepositorio extends AbstractJpaDAORepository<Protocolo> {

    private IApoioService apoioService;
    
    private IControleCnhService controleCnhService;
    
    public IApoioService getApoioService() {
        
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        
        return apoioService;
    }
    
    public IControleCnhService getControleCnhService() {
        
        if (controleCnhService == null) {
            controleCnhService = (IControleCnhService) JNDIUtil.lookup("ejb/ControleCnhService");
        }
        
        return controleCnhService;
    }
    
    /**
     * @param em
     * @param numeroProtocolo
     * @param tipoSituacaoProtocoloEnum
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public Protocolo getProtocoloPorNumeroProtocoloETipoSituacao(EntityManager em, 
                                                                 String numeroProtocolo, 
                                                                 TipoSituacaoProtocoloEnum tipoSituacaoProtocoloEnum) throws AppException {
        if(DetranStringUtil.ehBrancoOuNulo(numeroProtocolo) || tipoSituacaoProtocoloEnum == null){
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        Protocolo protocolo = super.getNamedQuery(em, 
                                                   "Protocolo.getProtocoloPorNumeroProtocolo",
                                                   numeroProtocolo);
        TemplateProtocolo template = (TemplateProtocolo) getControleCnhService().getTemplateProtocoloPorID(protocolo.getTemplateProtocolo());
        
        if(tipoSituacaoProtocoloEnum.equals(template.getTipoSituacao()))
            return protocolo;
        return null;
    }

    /**
     * 
     * @param em
     * @param paId
     * @return
     * @throws AppException 
     */
    public List<Protocolo> getProcessoAdministrativoDesistentesRecursoInstauracaoPenalizacao(EntityManager em) throws AppException {

        List lDesistencia = 
                super.getListNamedQuery(
                        em, 
                        "Protocolo.getProcessoAdministrativoDesistentesRecursoInstauracaoPenalizacao", 
                        PAAndamentoProcessoConstante.DESISTENCIA.CONFIRMAR_DESISTENCIA_RECURSO_INSTAURACAO_PENALIZACAO,
                        AtivoEnum.ATIVO);
        
        if (!DetranCollectionUtil.ehNuloOuVazio(lDesistencia)) {
            
            for (Object objectDesistencia : lDesistencia) {
                
                DesistenciaRecursoInstauracaoPenalizacaoWS desistente = 
                        (DesistenciaRecursoInstauracaoPenalizacaoWS) objectDesistencia;
                
                TemplateProtocolo template = (TemplateProtocolo) getControleCnhService().getTemplateProtocoloPorID(desistente.getTemplateProtocoloID());
                
                desistente.setObservacao(template.getObservacao());
                desistente.setIdUsuario(template.getUsuario().getId());
                
                Usuario usuario = 
                        (Usuario) getApoioService().getUsuario(desistente.getIdUsuario());
            
                if (usuario != null) {
                    desistente.setNomeUsuario(getApoioService().getNomeUsuarioPeloId(usuario));
                    desistente.setCpfUsuario(getApoioService().getCpfUsuarioPeloId(usuario));
                }
            }
        }
        
        return lDesistencia;
    }

    /**
     * @param em
     * @param paId
     * @param tipoNotificacao
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public Protocolo getProtocoloPorProcessoAdministrativoETipoNotificacao(EntityManager em, 
                                                                           Long paId, 
                                                                           TipoNotificacaoEnum tipoNotificacao) throws DatabaseException {
        
        return super.getNamedQuery(em, "Protocolo.getProtocoloPorProcessoAdministrativoETipoNotificacao", paId, tipoNotificacao.getCodigo());
    }

    /**
     * @param em
     * @param paId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public Protocolo getProtocoloParaCancelamentoPorCadastroIndevido(EntityManager em, Long paId) throws DatabaseException {
        return super.getNamedQuery(em, 
                                   "Protocolo.getProtocoloParaCancelamentoPorCadastroIndevido", 
                                   paId,
                                   TipoSituacaoProtocoloEnum.APRESENTACAO.name());
    }
    
    /**
     * @param em
     * @param numeroProtocolo
     * @return 
     */
    public Protocolo getProtocoloPorNumeroProtocoloETipoNotificacaoDesistente(EntityManager em, 
                                                                              String numeroProtocolo) throws DatabaseException {
        
        List <Integer> tiposNotificacao = 
                DetranCollectionUtil
                        .montaLista(
                                TipoNotificacaoEnum.PROTOCOLO_PA_DESISTENCIA_REC_INST_PEN.getCodigo(),
                                TipoNotificacaoEnum.PROTOCOLO_PA_DESISTENCIA_REC_INST_PEN_PERMISSIONADO.getCodigo());
        
        return super.getNamedQuery(em, 
                                   "Protocolo.getProtocoloPorNumeroProtocoloETipoNotificacaoDesistente", 
                                   numeroProtocolo,
                                   tiposNotificacao,
                                   AtivoEnum.ATIVO);
    }
}