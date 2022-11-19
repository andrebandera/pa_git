package br.gov.ms.detran.pa.instauracao.web;

import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.ejb.IInstauracaoService;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.entidade.ExecucaoInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.SituacaoExecucaoInstauracaoEnum;
import java.io.IOException;
import java.util.Calendar;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ServletSecurity
@WebServlet(name = "InstauracaoServlet", urlPatterns = {"/instauracaopa"})
public class InstauracaoServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(InstauracaoServlet.class);

    @EJB
    private IInstauracaoService instauracaoService;

    IPAControleFalhaService getFalhaService() {
        return ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     **/
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LOG.info("INÍCIO");

        try {

            instauracaoService.validaToken(request);

            instauracaoService.instaurar(
                gravaExecucaoInstauracao()
            );

        } catch (Exception e) {
            LOG.debug("Capturado", e);
            getFalhaService().gravarFalha(e, "Erro ao instaurar");
        }

        LOG.info("FIM");
    }

    /**
     * 
     * @return
     * @throws DatabaseException 
     */
    private ExecucaoInstauracao gravaExecucaoInstauracao() throws DatabaseException {

        ExecucaoInstauracao execucaoInstauracao = new ExecucaoInstauracao();

        execucaoInstauracao.setDataInicio(Calendar.getInstance().getTime());
        execucaoInstauracao.setSituacao(SituacaoExecucaoInstauracaoEnum.INICIADO);
        execucaoInstauracao.setQuantidadeInstaurado(0L);
        
        return (ExecucaoInstauracao) instauracaoService.gravar(execucaoInstauracao);
    }
}