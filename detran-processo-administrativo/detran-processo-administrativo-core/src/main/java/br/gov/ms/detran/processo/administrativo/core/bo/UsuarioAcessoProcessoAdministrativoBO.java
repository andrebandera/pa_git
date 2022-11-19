package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.PostoAtendimento;
import br.gov.ms.detran.comum.core.projeto.enums.adm.FormaProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.hab.FormaEntregaCnhEnum;
import br.gov.ms.detran.comum.core.projeto.enums.hab.ModoEntregaCnhEnum;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.criteria.UsuarioAcessoProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.wrapper.UsuarioAcessoProcessoAdministrativoWrapper;
import java.util.Date;
import javax.persistence.EntityManager;

public class UsuarioAcessoProcessoAdministrativoBO {
    
    private IApoioService apoioService;

    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    /**
     * @param em
     * @param usuarioLogado
     * @param criteria
     * @return
     * @throws AppException 
     */
    public UsuarioAcessoProcessoAdministrativoWrapper montarUsuarioAcessoWrapper(
        EntityManager em, DetranUserDetailsWrapper usuarioLogado, UsuarioAcessoProcessoAdministrativoCriteria criteria) throws AppException {
        
        UsuarioAcessoProcessoAdministrativoWrapper wrapper 
            = new UsuarioAcessoProcessoAdministrativoWrapper();
        
        Boolean usuarioAcessoPA 
            = getApoioService().getUsuarioEstaNoGrupoSepen(usuarioLogado.getUsuarioLocal().getId());
        
        // Recurso - Fora Prazo
        wrapper.setHabilitarForaPrazo(usuarioAcessoPA);
        wrapper.setForaPrazo(!usuarioAcessoPA ? BooleanEnum.NAO : null);
        
        // Controle CNH - Data inicio.
        wrapper.setHabilitarDataInicio(habilitarDataInicio(em, usuarioAcessoPA, criteria));
        
        // Controle Cnh - Botão Desbloquear.
        wrapper.setApresentarBotaoDesbloquearCnh(usuarioAcessoPA);
        
        // Controle Cnh - Modo Recolhimento CNH.
        wrapper.setHabilitarModoRecolhimentoCnh(usuarioAcessoPA);
        wrapper.setFormaEntregaControle(!usuarioAcessoPA ? FormaEntregaCnhEnum.ESPONTANEA : null);
        
        // Controle Cnh - Tipo Entrega Cnh
        wrapper.setHabilitarTipoEntregaCnh(usuarioAcessoPA);
        wrapper.setFormaEntrega(!usuarioAcessoPA ? ModoEntregaCnhEnum.DOCUMENTO_CNH : null);
        
        // Controle Cnh - Forma Protocolo
        wrapper.setHabilitarFormaProtocolo(usuarioAcessoPA);
        wrapper.setFormaProtocolo(!usuarioAcessoPA ? FormaProtocoloEnum.BALCAO : null);
        
        // Controle Cnh - Posto Atendimento
        wrapper.setHabilitarPostoAtendimento(usuarioAcessoPA && wrapper.getHabilitarDataInicio());
        
        
        wrapper.setPostoAtendimento(recuperarPostoAtendimento(em, usuarioLogado, usuarioAcessoPA, criteria));
        
        return wrapper;
    }

    /**
     * Grupo SEPEN pode modificar o campo "Data Início Penalidade" quando: 
     * 
     * 1 - O condutor NÃO estiver cumprindo Pena (Para verificar se o mesmo está cumprindo Pena, verificar na estrutura TEA ATIVO 
     *      - lembrando que pode existir vários processos cumprindo Pena, e obter destes a Data FIM maior). 
     * 
     * 2 - Se a data de hoje for maior que esta Data Fim o mesmo não está cumprindo mais Pena. 
     * 
     * 3 - Caso não exista nenhum resultado, o mesmo não está cumprindo Pena.
     * 
     * @param usuarioAcessoPA
     * @param criteria
     * @return 
     */
    private Boolean habilitarDataInicio(EntityManager em, 
                                        Boolean usuarioAcessoPA, 
                                        UsuarioAcessoProcessoAdministrativoCriteria criteria) throws DatabaseException {
        
        Boolean habilitar = Boolean.FALSE;
        
        if (usuarioAcessoPA) {
            
            if (null != criteria && !DetranStringUtil.ehBrancoOuNulo(criteria.getCpfCondutor())) {
                
                PAPenalidadeProcesso penalidade = 
                        new PAPenalidadeProcessoRepositorio()
                                .getPAPenalidadeProcessoMaiorDataFimPorCpfCondutor(em, criteria.getCpfCondutor());

                if (penalidade != null ) {

                    if (new Date().after(penalidade.getDataFimPenalidade())) 
                        habilitar = Boolean.TRUE;

                } else {
                    
                    habilitar = Boolean.TRUE;
                    
                }
            }
        }
            
        return habilitar;
    }

    /**
     * Apresentar a informação já cadastrada na TEA Ativa.
     * 
     * @param em
     * @param usuarioLogado
     * @param usuarioAcessoPA
     * @param criteria
     * @return
     * @throws AppException 
     */
    private PostoAtendimento recuperarPostoAtendimento(
        EntityManager em, DetranUserDetailsWrapper usuarioLogado, 
        Boolean usuarioAcessoPA, UsuarioAcessoProcessoAdministrativoCriteria criteria) throws AppException {
        
        PostoAtendimento postoAtendimento = usuarioLogado.getOperador().getPostoAtendimento();
        
//        if (usuarioAcessoPA) {
//            
//            if (null != criteria && !DetranStringUtil.ehBrancoOuNulo(criteria.getCpfCondutor())) {
//                
//                PAPenalidadeProcesso penalidade = 
//                        new PAPenalidadeProcessoRepositorio()
//                                .getPAPenalidadeProcessoMaiorDataFimPorCpfCondutor(em, criteria.getCpfCondutor());
//
//                if (null != penalidade && null != penalidade.getPostoAtendimento())
//                    postoAtendimento = (PostoAtendimento) getApoioService().getPostoAtendimentoPorId(penalidade.getPostoAtendimento());
//            }
//        }
        
        return postoAtendimento;
    }
}