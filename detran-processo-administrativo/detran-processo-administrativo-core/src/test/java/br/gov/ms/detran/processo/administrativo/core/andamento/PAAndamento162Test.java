/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.DtnRESTfulClient;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoMotivoEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.MovimentacaoPaWSWrapper;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento162Test {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento162Test.class);
    
    public PAAndamento162Test() {
    }
    
    @Rule
    public PAAndamentoRule myPAAndamentoRuleTest = new PAAndamentoRule(PAAndamentoEnum.ANDAMENTO_162);
    
    @Test
    public void testExecutaEspecifico() {
        
        try {
            
            if(PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());

                MovimentacaoPaWSWrapper wrapper = new MovimentacaoPaWSWrapper();
                wrapper.setAcao(MovimentacaoAcaoEnum.RETIRAR_SUSPENSAO);
                wrapper.setMotivo(MovimentacaoMotivoEnum.SEPEN);
                wrapper.setNumeroProcessoAdministrativo(myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());

                DtnRESTfulClient clientExecuta 
                    = executaTeste(wrapper);
                
                Assert.assertTrue(Response.Status.OK.getStatusCode() == clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }

    @Test
    public void testExecutaEspecificoAcaoErrada() {
        
        try {
            
            if(PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());

                MovimentacaoPaWSWrapper wrapper = new MovimentacaoPaWSWrapper();
                wrapper.setAcao(MovimentacaoAcaoEnum.CANCELAR);
                wrapper.setMotivo(MovimentacaoMotivoEnum.MOTIVO_ADMINISTRATIVO);
                wrapper.setNumeroProcessoAdministrativo(myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());

                DtnRESTfulClient clientExecuta 
                    = executaTeste(wrapper);
                
                Assert.assertTrue(Response.Status.OK.getStatusCode() != clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }

    private DtnRESTfulClient executaTeste(MovimentacaoPaWSWrapper wrapper) throws AppException {
        
        ExecucaoAndamentoWrapper entrada
                = new ExecucaoAndamentoWrapper(
                        myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso(),
                        PAAndamentoEnum.ANDAMENTO_162,
                        wrapper
                );
        
        String urlExecuta = "/detran-processo-administrativo/resource/execucaoandamentos/executaespecifico";
        
        DtnRESTfulClient clientExecuta = new DtnRESTfulClient();
        
        clientExecuta
                .setServerName(PAAndamentoServerNameEnum.getServerName())
                .setResourcePath(urlExecuta)
                .setContentType("application/json")
                .setRequestMethod("POST")
                .addHeaderParams("login", "00330851101")
                .addHeaderParams("postoAtendimento", "TESTE_ANDAMENTO")
                .setData(entrada)
                .execute();
        
        LOG.info("**** HTTP code: {0}", clientExecuta.getResponseCode());
       
        return clientExecuta;
    }
}