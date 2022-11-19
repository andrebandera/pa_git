package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.core.iface.servico.hab.IControleCnhService;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoMovimentoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.Protocolo;
import br.gov.ms.detran.processo.administrativo.entidade.Recurso;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWrapper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;

public class RecursoMovimentoBO {
    
    private IControleCnhService controleCnhService;

    public IControleCnhService getControleCnhService() {
        
        if (controleCnhService == null) {
            controleCnhService = (IControleCnhService) JNDIUtil.lookup("ejb/ControleCnhService");
        }
        
        return controleCnhService;
    }

    /**
     * Se já existir, desativa o antigo e cria o novo. 
     * Se não, apenas cria novo.
     * 
     * @param em
     * @param protocolo
     * @param wrapper
     * @param usuarioLogado 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public void gravar(EntityManager em, 
                       Protocolo protocolo, 
                       RecursoWrapper wrapper, 
                       DetranUserDetailsWrapper usuarioLogado) throws AppException {
        
        RecursoMovimentoRepositorio repo = new RecursoMovimentoRepositorio();
        
        RecursoMovimento recursoMovimentoAntigo = 
                new RecursoMovimentoRepositorio()
                        .getRecursoMovimentoPorRecurso(em, wrapper.getEntidade());
        
        if (recursoMovimentoAntigo != null) {
            
            recursoMovimentoAntigo.setAtivo(AtivoEnum.DESATIVADO);
            repo.update(em, recursoMovimentoAntigo);
        }
        
        RecursoMovimento recursoMovimento = new RecursoMovimento();
        
        recursoMovimento.setRecurso(wrapper.getEntidade());
        recursoMovimento.setProtocolo(protocolo);
        recursoMovimento.setUsuario(usuarioLogado.getUsuarioLocal().getId());
        recursoMovimento.setDataMovimento(wrapper.getDataRecurso() != null ? wrapper.getDataRecurso() : Calendar.getInstance().getTime());
        recursoMovimento.setIndiceForaPrazo(wrapper.getIndiceForaPrazo() != null ? wrapper.getIndiceForaPrazo() : BooleanEnum.NAO);
        recursoMovimento.setAtivo(AtivoEnum.ATIVO);
        recursoMovimento.setMotivoCancelamento(wrapper.getMotivoCancelamento());
        
        new RecursoMovimentoRepositorio().insert(em, recursoMovimento);
    }
    
    public RecursoMovimento getRecursoMovimentoPorRecursoETipoProtocolo(EntityManager em, 
                                                                        Recurso recurso, 
                                                                        TipoSituacaoProtocoloEnum tipoSituacao) 
            throws AppException{
         
        List<RecursoMovimento> lRecursosPorTipo = new ArrayList<>();
    
        List<RecursoMovimento> lRecursoMovimentos = new RecursoMovimentoRepositorio().getRecursoMovimentoPorRecursoETipoProtocolo(em, recurso);
        
         for (RecursoMovimento recursoMovimento : lRecursoMovimentos) {
            if(getControleCnhService().
                    validaTipoTemplateProtocolo(recursoMovimento.getProtocolo().getTemplateProtocolo(), 
                                                tipoSituacao))
            {
                lRecursosPorTipo.add(recursoMovimento);
            }
        }
        
         return DetranCollectionUtil.ehNuloOuVazio(lRecursosPorTipo)? null : lRecursosPorTipo.get(0);
    }
}