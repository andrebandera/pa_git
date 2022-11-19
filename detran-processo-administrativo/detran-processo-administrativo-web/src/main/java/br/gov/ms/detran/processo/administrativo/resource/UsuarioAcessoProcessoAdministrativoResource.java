package br.gov.ms.detran.processo.administrativo.resource;

import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.criteria.UsuarioAcessoProcessoAdministrativoCriteria;
import br.gov.ms.detran.processo.administrativo.wrapper.UsuarioAcessoProcessoAdministrativoWrapper;
import java.util.Calendar;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author desenvolvimento
 */
@ManagedBean
@Path("usuarioacessopa")
public class UsuarioAcessoProcessoAdministrativoResource 
    extends PaResource<UsuarioAcessoProcessoAdministrativoWrapper, UsuarioAcessoProcessoAdministrativoCriteria> {
    
    private static final Logger LOG = Logger.getLogger(UsuarioAcessoProcessoAdministrativoResource.class);
    
    @PUT
    @Path("verificarpermissao")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response verificarPermissao(
        @Context HttpServletRequest request, @Context SecurityContext context, UsuarioAcessoProcessoAdministrativoCriteria criteria) throws AppException {

        view = getModelView();

        UsuarioAcessoProcessoAdministrativoWrapper wrapper = 
            (UsuarioAcessoProcessoAdministrativoWrapper) processoAdministrativoService
                .montarUsuarioAcessoWrapper(
                    (DetranUserDetailsWrapper) getPrincipal(request),
                    criteria
                );
        
        checaExistePermissaoDoUsuarioParaAlterarDataRecurso(request, criteria, wrapper);
        
        view.setObjectResponse(wrapper);

        return getResponseOk(request);
    }
    
    /**
     * 
     * @param request
     * @throws AppException 
     */
    private void checaExistePermissaoDoUsuarioParaAlterarDataRecurso(
        HttpServletRequest request, UsuarioAcessoProcessoAdministrativoCriteria criteria, UsuarioAcessoProcessoAdministrativoWrapper wrapper) throws AppException {
        
        Boolean desabilitaDataRecurso = Boolean.TRUE;

        try {
            
            if(!DetranStringUtil.ehBrancoOuNulo(criteria.getFuncionalidadeURL())) {

                if("/".equals(criteria.getFuncionalidadeURL().substring(0, 1))) {
                    criteria.setFuncionalidadeURL(criteria.getFuncionalidadeURL().substring(1));
                }

                DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);

                desabilitaDataRecurso 
                    = processoAdministrativoService
                        .validaSePermiteUsuarioCadastrarDataRecursoRetroativa(
                            usuarioLogado.getUsuarioLocal().getId(), 
                            criteria.getFuncionalidadeURL()
                        );

                LOG.debug("Desabilitado: {0}", desabilitaDataRecurso);

                wrapper.setDesabilitaDataRecurso(desabilitaDataRecurso);
            }

        } catch(Exception e) {
            LOG.error("Erro inesperado ao identificar permissão para habilitar preenchimento do campo Data Recurso.", e);
        }
        
        wrapper.setDesabilitaDataRecurso(desabilitaDataRecurso);
        wrapper.setDataRecurso(Calendar.getInstance().getTime());
    }
}