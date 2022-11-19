/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.WrapperUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.criteria.PAServicoExternoCriteria;
import br.gov.ms.detran.processo.administrativo.wrapper.PAServicoExternoWrapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

/**
 *
 * @author desenvolvimento
 */
@ManagedBean
@Path("/servicoexternos")
public class PAServicoExternoResource extends PaResource<PAServicoExternoWrapper, PAServicoExternoCriteria> {

    private static final Logger LOG = Logger.getLogger(RecursoResource.class);

    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {
        List<ListSelectItem> listSelectItem = new ArrayList<>();

        listSelectItem.add(new ListSelectItem("ativo", DetranWebUtils.getSelectItems(AtivoEnum.class, Boolean.TRUE)));

        return listSelectItem;
    }

    @Override
    protected void executarAntesDeGravar(HttpServletRequest request) throws AppException {
        getEntity().getEntidade().setData(new Date());
        getEntity().getEntidade().setAtivo(AtivoEnum.ATIVO);
        
    }

    @Override
    protected void validarCadastro(HttpServletRequest request) throws AppException {
        super.validarCadastro(request); //To change body of generated methods, choose Tools | Templates.
        processoAdministrativoService.validarServicoExternoMesmoNomeAtivo(getEntity().getEntidade());
        
    }

    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {
        view.setEntity(WrapperUtil.convertEntityToWrapper2(
                genericService.pesquisar(criteria), getEntityClass()));

        view.setRowcount(genericService.contarPesquisa(criteria));
    }

    @Override
    protected void validarReativacao(HttpServletRequest request) throws AppException {
        super.validarReativacao(request); //To change body of generated methods, choose Tools | Templates.
        processoAdministrativoService.validarServicoExternoMesmoNomeAtivo(getEntity().getEntidade());
    }
    
}
