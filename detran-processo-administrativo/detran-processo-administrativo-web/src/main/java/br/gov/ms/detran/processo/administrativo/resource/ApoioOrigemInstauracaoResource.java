/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.inf.AmparoLegal;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.AcaoInstauracaoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.AcaoSistemaPAEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.MotivoPenalidadeEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.projeto.anotacao.resource.ResourceConfig;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.SelectItem;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.core.iface.servico.inf.IInfracaoService;
import br.gov.ms.detran.processo.administrativo.criteria.ApoioOrigemInstauracaoCriteria;
import br.gov.ms.detran.processo.administrativo.ejb.IPAApoioService;
import br.gov.ms.detran.processo.administrativo.wrapper.ApoioOrigemInstauracaoWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * @author Carlos Eduardo
 */
@ManagedBean
@Path("apoioorigeminstauracaos")
@ResourceConfig(condicaoProcesso = "ApoioOrigemInstauracaoCP")
public class ApoioOrigemInstauracaoResource extends PaResource<ApoioOrigemInstauracaoWrapper, ApoioOrigemInstauracaoCriteria> {

    @EJB(mappedName = "ejb/PAApoioService")
    private IPAApoioService service;

    private IInfracaoService infracaoService;

    public IInfracaoService getInfracaoService() {
        if (infracaoService == null) {
            infracaoService = (IInfracaoService) JNDIUtil.lookup("ejb/InfracaoService");
        }
        return infracaoService;
    }

    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {

        List<ListSelectItem> list = new ArrayList<>();
        //Select Option TipoProcesso 
        List<SelectItem> listaTipoProcesso = DetranWebUtils.getSelectItems(TipoProcessoEnum.class, true);
        list.add(new ListSelectItem("resultadoTipoProcesso", listaTipoProcesso));
        //Select Option TipoProcesso Reincidencia
        List<SelectItem> listaTipoProcessoReincidencia = DetranWebUtils.getSelectItems(TipoProcessoEnum.class, true);
        list.add(new ListSelectItem("tipoProcessoReincidencia", listaTipoProcessoReincidencia));
        //Select Option Motivo Resultado
        List<SelectItem> listaMotivoR = DetranWebUtils.getSelectItems(MotivoPenalidadeEnum.class, true);
        list.add(new ListSelectItem("resultadoMotivo", listaMotivoR));
        //Select Option Motivo
        List<SelectItem> listaMotivo = DetranWebUtils.getSelectItems(MotivoPenalidadeEnum.class, true);
        list.add(new ListSelectItem("motivo", listaMotivo));
        //Select Option AçãoResultado
        List<SelectItem> listaAcao = DetranWebUtils.getSelectItems(AcaoInstauracaoEnum.class, true);
        list.add(new ListSelectItem("resultadoAcao", listaAcao));

        List<SelectItem> listaBooleanEnum = DetranWebUtils.getSelectItems(BooleanEnum.class, true);
        list.add(new ListSelectItem("indiceHistoricoInfracao", listaBooleanEnum));

        List<SelectItem> listaReincidencia = DetranWebUtils.getSelectItems(BooleanEnum.class, true);
        list.add(new ListSelectItem("reincidencia", listaReincidencia));

        List<SelectItem> listaReincidenciaMAZ = DetranWebUtils.getSelectItems(BooleanEnum.class, true);
        list.add(new ListSelectItem("indiceReincidenciaMAZ", listaReincidenciaMAZ));
        //Select Option AçãoSistema
        List<SelectItem> listaAcaoSistema = DetranWebUtils.getSelectItems(AcaoSistemaPAEnum.class, true);
        list.add(new ListSelectItem("acaoSistema", listaAcaoSistema));
        //Select Option Ativo
        List<SelectItem> selectItems = DetranWebUtils.getSelectItems(AtivoEnum.class, true);
        list.add(new ListSelectItem("ativo", selectItems));

        return list;
    }

    @PUT
    @Path("buscarApoioOrigemeAmparoLegal")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response buscarApoioOrigemeAmparoLegal(@Context HttpServletRequest request, @Context SecurityContext context, ApoioOrigemInstauracaoCriteria criteria) throws DatabaseException, AppException {

        view = getModelView();
        ApoioOrigemInstauracaoWrapper wrapper = new ApoioOrigemInstauracaoWrapper();
        wrapper.setEntidade(service.getApoioOrigemInstauracaoById(criteria.getId()));
        try {

            AmparoLegal amparo = (AmparoLegal) getInfracaoService().getAmparoLegalPorId(wrapper.getEntidade().getAmparoLegal());
            wrapper.setAmparoLegal(amparo);

            if (wrapper.getAmparoLegal() != null) {
                view.addEntity(wrapper);

            }

        } catch (Exception e) {
            return null;
        }
        view.getListSelectItem().addAll(populateListWithEnums(request, "SEARCH"));
        return getResponseOk();
    }

    @PUT
    @Path("searchOrigemInstauracaoPorFluxoProcesso")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response searchOrigemInstauracaoPorFluxoProcesso(
            @Context HttpServletRequest request,
            @Context SecurityContext context,
            ApoioOrigemInstauracaoCriteria criteria) throws DatabaseException, AppException {

        view = getModelView();

        if (criteria.getDescricao() == null || criteria.getDescricao().isEmpty() || criteria.getDescricao().length() < 2) {
            DetranWebUtils.addErrorMessage("É necessário informar ao menos 2 caracteres para pesquisa.", view);
        } else {
            try {

                if (!view.hasError()) {
                    view.setEntity(service.getOrigemInstauracaoPorFluxoProcesso(criteria));
                    view.setRowcount(service.getCountOrigemInstauracaoPorFluxoProcesso(criteria));

                    if (criteria == null || criteria.isPopulateList()) {

                        view.getListSelectItem().addAll(populateListWithEnums(request, null, "SEARCH"));
                        view.getListSelectItem().addAll(populateListWithEnums(request, criteria, "SEARCH"));
                    }

                    if (view.getRowcount() != null && view.getRowcount().intValue() <= 0) {

                        if (criteria.getEmptyMessage() != null && criteria.getEmptyMessage()) {
                            if(criteria.getDescricao().length() >= 2){
                            DetranWebUtils.applicationMessageException("Nenhum registro encontrado com a descrição informada.");
                            }
                            DetranWebUtils.applicationMessageException("Todos os registros ativos já foram associados");
                            return getResponseOk(request);
                        }
                    }
                }

            } catch (Exception ex) {
                DetranWebUtils.addErrorMessage(ex, view);
            }
        }
        return getResponseOk(request);
    }

}
