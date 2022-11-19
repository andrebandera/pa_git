/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.SelectItem;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.criteria.DestinoFaseCriteria;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.DestinoFaseWrapper;
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
@Path("destinofases")
public class DestinoFaseResource extends PaResource<DestinoFaseWrapper, DestinoFaseCriteria> {
    
    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> list = new ArrayList<>();
        //Select Option Tipo Notificação Processo 
        List<SelectItem> origemDestino = DetranWebUtils.getSelectItems(OrigemDestinoEnum.class, true);
        list.add(new ListSelectItem("origemDestino", origemDestino));
        return list;
    }
}
