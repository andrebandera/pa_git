package br.gov.ms.detran.processo.administrativo.servlet;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhControle;
import br.gov.ms.detran.comum.iface.servico.IDetranAutenticacaoService;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.seguranca.springsecurity.session.DBSpringSessionInformation;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.ace.IAcessoService;
import br.gov.ms.detran.core.iface.servico.hab.IHabilitacaoService;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP93;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP93BO;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.ConsultaCnhControleRecolhimento;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author psg
 */
@ServletSecurity
@WebServlet(name = "ConfirmacaoRecolhimentoCnhServlet", urlPatterns = {"/confirmacaorecolhimentocnh"})
public class ConfirmacaoRecolhimentoCnhServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(ConfirmacaoRecolhimentoCnhServlet.class);

    @EJB
    private IProcessoAdministrativoService processoAdministrativoService;

    IPAControleFalhaService paControleFalha;

    private IAcessoService acessoService;

    public IAcessoService getAcessoService() {

        if (acessoService == null) {
            acessoService = (IAcessoService) JNDIUtil.lookup("ejb/AcessoService");
        }

        return acessoService;
    }

    public IHabilitacaoService getHabilitacaoService() {
        return (IHabilitacaoService) JNDIUtil.lookup("ejb/HabilitacaoService");
    }

    @PostConstruct
    @Override
    public void init() {
        paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /**
         * Chamada:
         * http://localhost:8080/detran-processo-administrativo/confirmacaorecolhimentocnh
         *
         */
        try {

            LOG.info("Inicio Confirmação Recolhimento CNH (Validade CNH).");

//            validaToken(req);
            Usuario usuarioDetran = (Usuario) getAcessoService().getUsuarioPorLogin("DETRAN");
            UsernamePasswordAuthenticationToken preAuthenticatedToken = new UsernamePasswordAuthenticationToken("Validade_CNH_27755", null);
            SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
            SecurityContextHolder.getContext().setAuthentication(preAuthenticatedToken);

            List<ConsultaCnhControleRecolhimento> listaCnhControle = processoAdministrativoService.buscarCnhControleSemValidade();

            if (!DetranCollectionUtil.ehNuloOuVazio(listaCnhControle)) {
                listaCnhControle.forEach(item -> {
                    try {
                        AEMNPP93 aemnpp93 = new AEMNPP93BO().executarIntegracaoAEMNPP93(item.getNumeroDocumento());

                        if (aemnpp93 == null || DetranStringUtil.ehBrancoOuNulo(aemnpp93.getNumeroCnh())) {
                            DetranWebUtils.applicationMessageException("controlecnhpa.validar.condutorbca.exception");
                        }

                        if (!DetranStringUtil.ehBrancoOuNulo(aemnpp93.getDataValidadeCnh()) 
                                && item.getNumeroCnh().equals(Long.parseLong(aemnpp93.getNumeroCnh()))) {
                            CnhControle cnhControle = (CnhControle) getHabilitacaoService().getCnhControleByID(item.getCnhControleId());

                            if (cnhControle != null) {
                                cnhControle.setValidadeCnh(Utils.convertDate(aemnpp93.getDataValidadeCnh(), "ddMMyyyy"));
                                getHabilitacaoService().gravarCnhControle(cnhControle);
                            } else {
                                LOG.info("###CONFIRMACAO RECOLHIMENTO CNH### CnhControle não encontrado para o ID: {0} CNH: {1}", 
                                        item.getCnhControleId(), item.getNumeroCnh());
                                DetranWebUtils.applicationMessageException("Controle CNH não encontrado");
                            }
                                
                        } else {
                            LOG.info("###CONFIRMACAO RECOLHIMENTO CNH### Número CNH diferente ou Data Validade vazia/nula. Cnh Retorno: {0} - Cnh Base Local: {1} - CPF: {2} - DataValidadeCnh: ", 
                                    aemnpp93.getNumeroCnh(), item.getNumeroCnh(), item.getNumeroDocumento(), aemnpp93.getDataValidadeCnh());
                            DetranWebUtils.applicationMessageException("Não foi possível atualizar a data validade CNH.");
                        }

                    } catch (AppException ex) {
                        LOG.debug("Tratado.", ex);
                        paControleFalha.gravarFalha(ex, "Erro ao executar Confirmação Recolhimento Cnh.");
                    }
                });
            }
            
            LOG.info("Fim Confirmação Recolhimento CNH (Validade CNH).");
            
        } catch (Exception ex) {
            LOG.debug("Tratado.", ex);
            paControleFalha.gravarFalha(ex, "Erro ao executar Confirmação Recolhimento Cnh.");
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

        String principal = "SP_TDG";

        DBSpringSessionInformation sessao
                = (DBSpringSessionInformation) iDetranAutenticacaoService.getSessionByPrincipalAndHash(principal, token);

        if (sessao == null || !token.equals(sessao.getHash())) {
            DetranWebUtils.applicationMessageException("Inválido acesso. Token valor: {0}", token);
        }

        iDetranAutenticacaoService.removeSessionByPrincipalAndHash(principal, token);
    }
}
