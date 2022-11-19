package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.DtnRESTfulClient;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.enums.PAAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.EditalWrapper;
import java.util.Calendar;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class PAAndamento22Test {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento22Test.class);
    
    public PAAndamento22Test() {
    }
    
    @Rule
    public PAAndamentoRule myPAAndamentoRuleTest = new PAAndamentoRule(PAAndamentoEnum.ANDAMENTO_22);
    
    @Test
    public void testExecutaEspecifico() {
        
        try {
            
            if(PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());
                
                
                EditalWrapper entrada = new EditalWrapper(myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso(), 
                                                          TipoFasePaEnum.INSTAURACAO, 
                                                          "2019000005", 
                                                          Calendar.getInstance().getTime(), 
                                                          null, 
                                                          "03649524155");
                

                DtnRESTfulClient clientExecuta = executaTeste(entrada);
                
                Assert.assertTrue(Response.Status.OK.getStatusCode() == clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }
    
    @Test
    public void testExecutaEspecifico_FaseIncorreta() {
        
        try {
            
            if(PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());
                
                
                EditalWrapper entrada = new EditalWrapper(myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso(), 
                                                          TipoFasePaEnum.PENALIZACAO, 
                                                          "2019000005", 
                                                          Calendar.getInstance().getTime(), 
                                                          null, 
                                                          "03649524155");
                

                DtnRESTfulClient clientExecuta = executaTeste(entrada);
                
                Assert.assertTrue(Response.Status.BAD_REQUEST.getStatusCode() == clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }
    
    @Test
    public void testExecutaEspecifico_EntradaNula() {
        
        try {
            
            if(PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());
                
                
                DtnRESTfulClient clientExecuta = executaTeste(null);
                
                Assert.assertTrue(Response.Status.BAD_REQUEST.getStatusCode() == clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }
    
    @Test
    public void testExecutaEspecifico_DataPortariaNula() {
        
        try {
            
            if(PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());
                
                
                EditalWrapper entrada = new EditalWrapper(myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso(), 
                                                          TipoFasePaEnum.INSTAURACAO, 
                                                          "2019000005", 
                                                          null, 
                                                          null, 
                                                          "03649524155");
                

                DtnRESTfulClient clientExecuta = executaTeste(entrada);
                
                Assert.assertTrue(Response.Status.BAD_REQUEST.getStatusCode() == clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }
    
    @Test
    public void testExecutaEspecifico_NumeroPortariaNulo() {
        
        try {
            
            if(PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());
                
                
                EditalWrapper entrada = new EditalWrapper(myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso(), 
                                                          TipoFasePaEnum.INSTAURACAO, 
                                                          null, 
                                                          Calendar.getInstance().getTime(), 
                                                          null, 
                                                          "03649524155");
                

                DtnRESTfulClient clientExecuta = executaTeste(entrada);
                
                Assert.assertTrue(Response.Status.BAD_REQUEST.getStatusCode() == clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }
    
    @Test
    public void testExecutaEspecifico_UsuarioNulo() {
        
        try {
            
            if(PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());
                
                
                EditalWrapper entrada = new EditalWrapper(myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso(), 
                                                          TipoFasePaEnum.INSTAURACAO, 
                                                          "2019000005", 
                                                          Calendar.getInstance().getTime(), 
                                                          null, 
                                                          null);
                

                DtnRESTfulClient clientExecuta = executaTeste(entrada);
                
                Assert.assertTrue(Response.Status.BAD_REQUEST.getStatusCode() == clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }
    
    @Test
    public void testExecutaEspecifico_DataPortariaFutura() {
        
        try {
            
            if(PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());
                
                
                EditalWrapper entrada = new EditalWrapper(myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso(), 
                                                          TipoFasePaEnum.INSTAURACAO, 
                                                          "2019000005", 
                                                          Utils.addDayMonth(Calendar.getInstance().getTime(), 2), 
                                                          null, 
                                                          "03649524155");
                

                DtnRESTfulClient clientExecuta = executaTeste(entrada);
                
                Assert.assertTrue(Response.Status.BAD_REQUEST.getStatusCode() == clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }
    
    private DtnRESTfulClient executaTeste(Object wrapper) throws AppException {
        
        ExecucaoAndamentoWrapper entrada = 
                new ExecucaoAndamentoWrapper(
                        myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso(),
                        PAAndamentoEnum.ANDAMENTO_22,
                        wrapper);
        
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