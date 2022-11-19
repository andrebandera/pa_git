package br.gov.ms.detran.processo.administrativo.servlet;

import br.gov.ms.detran.comum.iface.servico.IDetranAutenticacaoService;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.seguranca.springsecurity.session.DBSpringSessionInformation;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Lillydi
 */
@ServletSecurity
@WebServlet(name = "TransferenciaSGIServlet", urlPatterns = {"/transferencianotificacao"})
public class TransferenciaSGIServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(TransferenciaSGIServlet.class);

    @EJB
    private IProcessoAdministrativoService iProcessoAdministrativoService;

    IPAControleFalhaService paControleFalha;

    @PostConstruct
    @Override
    public void init() {
        paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
        /** 
         * Procedure: SP_TDB_PAD_TRANSFERIR_NOTIFICACOES_SGI_CORREIO invoca servlet.
         * 
         * Chamada: http://localhost:8080/detran-processo-administrativo/transferencianotificacao 
         * 
         **/
        
        try {

            validaToken(req);
            
            UsernamePasswordAuthenticationToken preAuthenticatedToken = new UsernamePasswordAuthenticationToken("SP_TDB", null);
            
            SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
            SecurityContextHolder.getContext().setAuthentication(preAuthenticatedToken);
            
            String urlBaseBirt = DetranReportsUtil.getReportsBaseBirtUrl(req, true);

            iProcessoAdministrativoService.transferirNotificacoesParaSGI(urlBaseBirt);
            
        } catch (Exception ex) {
            
            LOG.debug("Tratado.", ex);
            paControleFalha.gravarFalha(ex, "Erro ao executar Transferencia SGI");
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

        String principal = "SP_TDB_PAD";

        DBSpringSessionInformation sessao
            = (DBSpringSessionInformation) 
                iDetranAutenticacaoService.getSessionByPrincipalAndHash(principal, token);

        if (sessao == null || !token.equals(sessao.getHash())) {
            DetranWebUtils.applicationMessageException("Inválido acesso. Token valor: {0}", token);
        }

        iDetranAutenticacaoService.removeSessionByPrincipalAndHash(principal, token);
    }
}