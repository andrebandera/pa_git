package br.gov.ms.detran.processo.administrativo.servlet;

import br.gov.ms.detran.comum.core.projeto.entidade.ace.Usuario;
import br.gov.ms.detran.comum.entidade.enums.FormatoRelatorioEnum;
import br.gov.ms.detran.comum.iface.servico.IDetranAutenticacaoService;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.seguranca.springsecurity.session.DBSpringSessionInformation;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranHTTPUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.ace.IAcessoService;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcessoEspecifico;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoBCARelatorioTotalWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * @author desenvolvimento
 */
@ServletSecurity
@WebServlet(name = "ArquivarProcessoPrescritoServlet", urlPatterns = {"/arquivarprocessoprescrito"})
public class ArquivarProcessoPrescritoServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(ArquivarProcessoPrescritoServlet.class);

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

    @PostConstruct
    @Override
    public void init() {
        paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        /** 
         * 
         * 
         * Chamada: http://localhost:8080/detran-processo-administrativo/arquivarprocessoprescrito 
         * 
         * 
         **/
        
        try {
            
            LOG.info("Inicio Arquivar Processos Prescritos.");
            
//            validaToken(req);
            
            Usuario usuarioDetran = (Usuario) getAcessoService().getUsuarioPorLogin("DETRAN");
            
            UsernamePasswordAuthenticationToken preAuthenticatedToken = new UsernamePasswordAuthenticationToken("DETRAN-WEB", null);
            
            SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
            SecurityContextHolder.getContext().setAuthentication(preAuthenticatedToken);
            
            List<PAAndamentoProcessoEspecifico> processosEspecificos = processoAdministrativoService.buscarProcessosEspecificos();
            
            if(!DetranCollectionUtil.ehNuloOuVazio(processosEspecificos)){
                for (PAAndamentoProcessoEspecifico processoEspecifico : processosEspecificos) {
                    try{
                    
                        ProcessoAdministrativo processoAdministrativo = 
                                    processoAdministrativoService.
                                                getProcessoAdministrativo(processoEspecifico.getIdProcessoAdministrativo());
                    
                    if(processoAdministrativo == null){
                        DetranWebUtils.applicationMessageException("Processo Administrativo não encontrado");
                    }
                    
                    new ExecucaoAndamentoManager()
                            .iniciaExecucao(
                                        new ExecucaoAndamentoEspecificoWrapper(
                                                                                processoAdministrativo,
                                                                                usuarioDetran.getId(),
                                                                                null,
                                                                                processoEspecifico
                                                                              )
                                        );
                    
                    }catch(Exception e){
                        LOG.debug("Tratado.", e);
                        paControleFalha.gravarFalha(e, "Erro ao executar arquivamento processo prescrito");
                    }
                }
                
                /*
                Gerar relatório de erros e gravar na TEF.
                */
                String urlBaseBirt = DetranReportsUtil.getReportsBaseBirtUrl(req, true);
                Map<String, String> params = new HashMap<>();
                
                String urlRelatorio
                    = DetranReportsUtil.getReportParamsBirtUrl(
                        "relatorioprescritoserro",
                        FormatoRelatorioEnum.PDF.getRptFormat(),
                        params,
                        "relatorios/processoadministrativo/"
                    );
                
                processoAdministrativoService.registraGravacaoProcessamento(
                    new ProcessoAdministrativoBCARelatorioTotalWrapper(), 
                    "PRESCRICAO", 
                    DetranHTTPUtil.download(urlBaseBirt + urlRelatorio)
                );
            }
            
        } catch (Exception ex) {
            LOG.debug("Tratado.", ex);
            paControleFalha.gravarFalha(ex, "Erro ao executar Arquivamento de Processo Prescrito");
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
            = (DBSpringSessionInformation) 
                iDetranAutenticacaoService.getSessionByPrincipalAndHash(principal, token);

        if (sessao == null || !token.equals(sessao.getHash())) {
            DetranWebUtils.applicationMessageException("Inválido acesso. Token valor: {0}", token);
        }

        iDetranAutenticacaoService.removeSessionByPrincipalAndHash(principal, token);
    }
}