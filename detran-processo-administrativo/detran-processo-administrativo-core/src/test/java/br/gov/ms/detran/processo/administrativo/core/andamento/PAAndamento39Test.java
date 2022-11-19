package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.DtnRESTfulClient;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.enums.PAAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class PAAndamento39Test {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento39Test.class);
    
    public PAAndamento39Test() {
    }
    
    @Rule
    public PAAndamentoRule myPAAndamentoRuleTest = new PAAndamentoRule(PAAndamentoEnum.ANDAMENTO_39);
    
    @Test
    public void testExecutaEspecifico() {
        
        try {
            
            if(PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());
                
                NotificaProcessoAdministrativoWrapper entrada = 
                        new NotificaProcessoAdministrativoWrapper(
                                myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso(),
                                TipoFasePaEnum.ENTREGA_CNH);
                
                DtnRESTfulClient clientExecuta = executaTeste(entrada, PAAndamentoEnum.ANDAMENTO_39);
                
                Assert.assertTrue(Response.Status.OK.getStatusCode() == clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }
    
    @Test
    public void testExecutaEspecificoTipoDiferente_INSTAURACAO() {
        
        try {
            
            if(PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());

                NotificaProcessoAdministrativoWrapper entrada = 
                        new NotificaProcessoAdministrativoWrapper(
                                myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso(),
                                TipoFasePaEnum.INSTAURACAO);
                
                DtnRESTfulClient clientExecuta = executaTeste(entrada, PAAndamentoEnum.ANDAMENTO_39);
                
                
                Assert.assertTrue(Response.Status.OK.getStatusCode() != clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }
    
    @Test
    public void testExecutaEspecificoSemWrapper() {
        
        try {
            
            if(PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());

                DtnRESTfulClient clientExecuta = executaTeste(null, PAAndamentoEnum.ANDAMENTO_39);
                
                Assert.assertTrue(Response.Status.OK.getStatusCode() != clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }
    
    private DtnRESTfulClient executaTeste(Object wrapper, PAAndamentoEnum andamentoEnum) throws AppException {
        
        ExecucaoAndamentoWrapper entrada = 
                new ExecucaoAndamentoWrapper(
                        myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso(),
                        andamentoEnum,
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