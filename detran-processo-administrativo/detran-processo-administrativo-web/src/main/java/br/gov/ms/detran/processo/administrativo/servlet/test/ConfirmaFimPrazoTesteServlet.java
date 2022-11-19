/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.servlet.test;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.logger.Logger;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_INSTAURACAO;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import java.io.IOException;
import java.util.List;
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
@WebServlet(name = "ConfirmaFimPrazoTesteServlet", urlPatterns = {"/fimprazoteste"})
public class ConfirmaFimPrazoTesteServlet extends HttpServlet {
    
    private static final Logger LOG = Logger.getLogger(ConfirmaFimPrazoTesteServlet.class);
    
    @EJB
    private IProcessoAdministrativoService iProcessoAdministrativoService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
        /** 
         * 
         * Chamada: http://localhost:8080/detran-processo-administrativo/fimprazoteste
         * 
         **/
        
        try {

            String ambienteTeste = System.getProperty("DTN_PA_SIM_WS");
             
             if(DetranStringUtil.ehBrancoOuNulo(ambienteTeste) || ambienteTeste.equals("N")){
                 DetranWebUtils.applicationMessageException("Não é possível executar essa ação neste ambiente.");
             }
            
            UsernamePasswordAuthenticationToken preAuthenticatedToken = new UsernamePasswordAuthenticationToken("TESTE_PRAZO", null);
            
            SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
            SecurityContextHolder.getContext().setAuthentication(preAuthenticatedToken);
            
            Long usuarioID = 3625L;
            
            List<ProcessoAdministrativo> lista = iProcessoAdministrativoService.
                    getListProcessosAdministrativosPorAndamento(DetranCollectionUtil.montaLista(CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_INSTAURACAO));
            
            for (ProcessoAdministrativo processoAdministrativo : lista) {
                
                try{
                    
                    NotificaProcessoAdministrativoWrapper wrapper = new NotificaProcessoAdministrativoWrapper(processoAdministrativo.getNumeroProcesso(), TipoFasePaEnum.INSTAURACAO);

                    new ExecucaoAndamentoManager()
                            .iniciaExecucao(
                                    new ExecucaoAndamentoEspecificoWrapper(
                                            iProcessoAdministrativoService.getProcessoAdministrativo(wrapper.getNumeroProcesso()),
                                            usuarioID,
                                            DetranReportsUtil.getReportsBaseBirtUrl(req, true),
                                            wrapper
                                    )
                            );
                }catch(Exception e){
                    LOG.debug("Erro Edital: .", e);
                }
            }
            
        } catch (Exception ex) {
            
            LOG.debug("Tratado.", ex);
        }
    }

    
}
