package br.gov.ms.detran.processo.administrativo.listener;

import br.gov.ms.detran.comum.projeto.web.DetranAbstractWebContextListener;
import br.gov.ms.detran.comum.util.logger.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

/**
 *
 * @author desenvolvimento
 */
@WebListener()
public class ProcessoAdministrativoWebContextListener extends DetranAbstractWebContextListener {
    
    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoWebContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        LOG.debug("###Processo Administrativo### Iniciando o context {0}", sce.getServletContext().getContextPath());

        initialize(
            sce, 
            "br.gov.ms.detran.comum.projeto.negocio",
            "br.gov.ms.detran.processo.administrativo.negocio",
            "br.gov.ms.detran.processo.administrativo.gatilho", 
            "br.gov.ms.detran.processo.administrativo.cp",
            "br.gov.ms.detran.core.regras"
        );
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.debug("###Processo Administrativo### Encerrando o context {0}", sce.getServletContext().getContextPath());
    }
}
