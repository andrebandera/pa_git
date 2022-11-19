package br.gov.ms.detran.processo.administrativo.servlet;

import br.gov.ms.detran.comum.iface.servico.IDetranAutenticacaoService;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.seguranca.springsecurity.session.DBSpringSessionInformation;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http://localhost:8080/detran-processo-administrativo/cnhsituacaoentrega
 * 
 * @author desenvolvimento
 */
@ServletSecurity
@WebServlet(name = "CnhSituacaoEntregaServlet", urlPatterns = {"/cnhsituacaoentrega"})
public class CnhSituacaoEntregaServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(CnhSituacaoEntregaServlet.class);

    @EJB
    private IProcessoAdministrativoService service;

    IPAControleFalhaService paControleFalha;

    @PostConstruct
    @Override
    public void init() {
        paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        try {
            
            LOG.info("Inicio Entrega Cnh.");
            
            validaToken(req);
            
            List<ProcessoAdministrativo> listaProcessos = 
                    service.buscarProcessosAdministrativosNoAndamento48ComCnhEntregue();
            
            if (!DetranCollectionUtil.ehNuloOuVazio(listaProcessos)) {
                
                for (ProcessoAdministrativo processo : listaProcessos) {
                    
                    try {

                        service.executarAndamento48paraEntregaCnh(processo);

                    } catch (AppException e) {
                        LOG.debug("Tratado.", e);
                        paControleFalha.gravarFalhaProcessoAdministrativo(e, "CnhSituacaoEntregaServlet", processo.getCpf(), processo.getNumeroProcesso());
                    }
                }
            }
            
        } catch (AppException ex) {
            LOG.debug("Tratado.", ex);
            paControleFalha.gravarFalha(ex, "Erro ao executar Retorno AR");
        }
    }
    
    /**
     * 
     * @param request
     * @throws AppException 
     */
    public void validaToken(HttpServletRequest request) throws AppException {

        String token = request.getHeader("token");

        LOG.info("Token: {0}", token);

        if (DetranStringUtil.ehBrancoOuNulo(token)) {
            DetranWebUtils.applicationMessageException("Inválido acesso.");
        }

        IDetranAutenticacaoService iDetranAutenticacaoService
            = (IDetranAutenticacaoService) JNDIUtil.lookup("ejb/DetranAutenticacaoService");

        String principal = "SP_TDF";

        DBSpringSessionInformation sessao
            = (DBSpringSessionInformation) 
                iDetranAutenticacaoService.getSessionByPrincipalAndHash(principal, token);

        if (sessao == null || !token.equals(sessao.getHash())) {
            DetranWebUtils.applicationMessageException("Inválido acesso. Token valor: {0}", token);
        }

        iDetranAutenticacaoService.removeSessionByPrincipalAndHash(principal, token);
    }
}