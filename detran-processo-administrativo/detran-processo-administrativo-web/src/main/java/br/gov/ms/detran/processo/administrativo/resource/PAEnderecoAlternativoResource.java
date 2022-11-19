package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.Estado;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.projeto.util.WrapperUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.criteria.PAEnderecoAlternativoCriteria;
import br.gov.ms.detran.processo.administrativo.enums.TipoEnvolvidoPAEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.PAEnderecoAlternativoWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

/**
 * @author Lillydi
 */
@ManagedBean
@Path("enderecoalternativos")
public class PAEnderecoAlternativoResource extends PaResource<PAEnderecoAlternativoWrapper, PAEnderecoAlternativoCriteria> {

    private static final Logger LOG = Logger.getLogger(PAEnderecoAlternativoResource.class);
    
    @Override
    protected List<ListSelectItem> getListWithEntity(HttpServletRequest request) throws AppException {
        
        List<ListSelectItem> listSelectItem = new ArrayList<>();
                
        IApoioService service = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        
        List<Estado> estados = service.getListEstado();
        listSelectItem.add(DetranWebUtils.populateItemsComboBox(estados, "", "estado", "", "nome"));
        
        listSelectItem.add(new ListSelectItem("tipoEnvolvido", DetranWebUtils.getSelectItems(TipoEnvolvidoPAEnum.class, Boolean.TRUE, TipoEnvolvidoPAEnum.CONDUTOR_SISTEMA)));
        
        return listSelectItem;
    }
    
    @Override
    protected void executarSearch(HttpServletRequest request, ICriteriaQueryBuilder criteria) throws Exception {
        
        PAEnderecoAlternativoCriteria c = (PAEnderecoAlternativoCriteria) criteria;
        if(c == null
                || (DetranStringUtil.ehBrancoOuNulo(c.getCpf())
                    && DetranStringUtil.ehBrancoOuNulo(c.getNumeroProcesso())
                    && c.getId() == null)){
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        view.setEntity(WrapperUtil.convertEntityToWrapper2(genericService.pesquisar(criteria), PAEnderecoAlternativoWrapper.class));
        view.setRowcount(genericService.contarPesquisa(criteria));
    }    

    @Override
    protected void validarCadastro(HttpServletRequest request) throws AppException {
        super.validarCadastro(request);
        
        processoAdministrativoService.validarEnderecoAlternativo(getEntity());
        
        PAEnderecoAlternativoWrapper wrapper = getEntity();
        
        if (wrapper.getEntidade().getMunicipio() == null) {
            DetranWebUtils.applicationMessageException("severity.error.enderecoalternativo.municipio.obrigatorio");
        }
    }

    @Override
    protected void validarReativacao(HttpServletRequest request) throws AppException {
        super.validarReativacao(request);
        processoAdministrativoService.validarEnderecoAlternativo(getEntity());
    }
}