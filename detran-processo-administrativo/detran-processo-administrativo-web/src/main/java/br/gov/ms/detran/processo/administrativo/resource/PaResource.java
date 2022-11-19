package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.rest.DetranAbstractResourceREST;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.processo.administrativo.ejb.IBloqueioBCAService;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ws.rs.Path;

/**
 * @author yanko.campos
 * @param <T>
 * @param <C>
 */
@ManagedBean
@Path("/pa")
public class PaResource<T extends IBaseEntity, C extends ICriteriaQueryBuilder> extends DetranAbstractResourceREST<T, C> {

    IProcessoAdministrativoService processoAdministrativoService;
    
    IPAControleFalhaService paControleFalha;
    
    IBloqueioBCAService bloqueioService;

    @PostConstruct
    public void init() {

        if (processoAdministrativoService == null) {
            processoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }

        if (paControleFalha == null) {
            paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
        }
        
        if (bloqueioService == null) {
            bloqueioService = ServiceJndiLocator.<IBloqueioBCAService>lookup("ejb/BloqueioBCAService");
        }

        super.setGenericService(processoAdministrativoService);
    }

    protected IProcessoAdministrativoService getProcessoAdministrativoService() {
        return processoAdministrativoService;
    }

    protected IPAControleFalhaService getControleFalha() {
        return this.paControleFalha;
    }

    protected IBloqueioBCAService getBloqueioService() {
        return bloqueioService;
    }
}