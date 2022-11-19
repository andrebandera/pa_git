/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoCorpoTexto;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.SelectItem;
import br.gov.ms.detran.comum.rest.DetranAbstractREST;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.criteria.PATipoCorpoAndamentoCriteria;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.PATipoCorpoAndamentoWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

/**
 *
 * @author Carlos Eduardo
 */
@ManagedBean
@Path("patipocorpoandamentos")
public class PATipoCorpoAndamentoResource extends PaResource<PATipoCorpoAndamentoWrapper, PATipoCorpoAndamentoCriteria>{
    
    public IApoioService getApoioService() {
        return (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
    }
    
    private static final Logger log = Logger.getLogger(DetranAbstractREST.class);
    
    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> list = new ArrayList<>();
        //Select Option Tipo Notificação Processo 
        List<SelectItem> listaTipoNotificacaoProcesso = DetranWebUtils.getSelectItems(TipoFasePaEnum.class, true);
        list.add(new ListSelectItem("tipoNotificacaoProcesso", listaTipoNotificacaoProcesso));
        return list;
    }

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {
        super.executarSearch(request, criteria);
        if (!DetranCollectionUtil.ehNuloOuVazio(view.getEntity())) {
            for (PATipoCorpoAndamentoWrapper wrapper : view.getEntity()) {
                wrapper.setTipoCorpoTexto((TipoCorpoTexto) getApoioService().getTipoCorpoTextoById(wrapper.getEntidade().getTipoCorpoTexto()));
            }
            view.setRowcount(genericService.contarPesquisa(criteria));
        }
    }
}
