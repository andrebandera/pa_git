package br.gov.ms.detran.processo.administrativo.servlet;

import br.gov.ms.detran.comum.util.logger.Logger;
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

/**
 * 
 * 
 */
@ServletSecurity
@WebServlet(name = "ArquivamentoPAsPorCassacaoServlet", urlPatterns = {"/arquivamento"})
public class ArquivamentoPAsPorCassacaoServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(ArquivamentoPAsPorCassacaoServlet.class);
    

    @EJB
    private IProcessoAdministrativoService processoAdministrativoService;

    @PostConstruct
    @Override
    public void init() {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        /** 
         * 
         * 
         * Chamada: http://localhost:8080/detran-processo-administrativo/arquivamento 
         * 
         * 
         **/
        
        try {
            
            LOG.info("INICIO -> Arquivamento para Processo Administrativo.");
            
            
            processoAdministrativoService
                .arquivarProcessosSuspensaoPorCassacao();
            
        } catch (Exception ex) {
            
            LOG.error("Tratado.", ex);
            
        }
        
        LOG.info("FIM -> Arquivamento para Processo Administrativo..");
    }
    
}