package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.TemplateProtocolo;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.criteria.RecursoCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.Recurso;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoRecurso;
import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.core.iface.servico.hab.IControleCnhService;
import br.gov.ms.detran.processo.administrativo.wrapper.ConsultaRecursoWrapper;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

@ManagedBean
@Path("/consultarecursos")
public class ConsultaRecursoResource  extends PaResource<ConsultaRecursoWrapper, RecursoCriteria> {
    
    private static final Logger LOG = Logger.getLogger(ConsultaRecursoResource.class);
    
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
    
    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder c) throws Exception {
        
        RecursoCriteria criteria = (RecursoCriteria) c;
        
        List lista = genericService.pesquisar(criteria);
        
        if (!DetranCollectionUtil.ehNuloOuVazio(lista)) {
            
            for (Object object : lista) {
                
                Recurso entidade = (Recurso) object;
                
                ConsultaRecursoWrapper wrapper = new ConsultaRecursoWrapper();
                
                wrapper.setEntidade(entidade);
                
                wrapper.setResultado((ResultadoRecurso) processoAdministrativoService.getResultadoRecursoAtivoPorRecurso(entidade.getId()));
                
                if (null != wrapper.getResultado()&& null != wrapper.getResultado().getUsuario()) {
                        
                    Usuario usuario = (Usuario) getApoioService().getUsuario(wrapper.getResultado().getUsuario());

                    wrapper.setUsuarioResultado(getApoioService().getNomeUsuarioPeloId(usuario));
                }
                
                if (criteria.getId() == null) {

                    //recuperar protocolo
                    RecursoMovimento movimentoRecurso = 
                            (RecursoMovimento) processoAdministrativoService.getMovimentoRecurso(entidade.getId());
                    
                    if(movimentoRecurso != null){
                         wrapper.setProtocolo(movimentoRecurso.getProtocolo());
                        wrapper.setTemplateApresentacao((TemplateProtocolo) getControleCnhService()
                                                                .getTemplateProtocoloPorID(movimentoRecurso.getProtocolo().getTemplateProtocolo()));
                    }
                    
                    
                } else {
                    
                    wrapper.setMovimentoApresentacao(
                            (RecursoMovimento) processoAdministrativoService
                                    .getRecursoMovimentoPorRecursoETipoProtocolo(entidade, 
                                                                                 TipoSituacaoProtocoloEnum.APRESENTACAO));
                    
                    if (null != wrapper.getMovimentoApresentacao() && null != wrapper.getMovimentoApresentacao().getUsuario()) {
                        
                        Usuario usuario = (Usuario) getApoioService().getUsuario(wrapper.getMovimentoApresentacao().getUsuario());
                        
                        wrapper.setUsuarioMovimentoApresentacao(getApoioService().getNomeUsuarioPeloId(usuario));
                    }
                    
                    if(wrapper.getMovimentoApresentacao() != null){
                        wrapper.setTemplateApresentacao((TemplateProtocolo) getControleCnhService()
                                                                .getTemplateProtocoloPorID(wrapper.getMovimentoApresentacao().getProtocolo().getTemplateProtocolo()));
                    }
                    
                    wrapper.setMovimentoCancelamento(
                            (RecursoMovimento) processoAdministrativoService
                                    .getRecursoMovimentoPorRecursoETipoProtocolo(entidade, 
                                                                                 TipoSituacaoProtocoloEnum.CANCELAMENTO));
                    if(wrapper.getMovimentoCancelamento()!= null){
                        wrapper.setTemplateCancelamento((TemplateProtocolo) getControleCnhService()
                                                                .getTemplateProtocoloPorID(wrapper.getMovimentoCancelamento().getProtocolo().getTemplateProtocolo()));
                    }
                }
                
                view.addEntity(wrapper);
                
            }
        }
        
        view.setRowcount(genericService.contarPesquisa(criteria));
    }
}