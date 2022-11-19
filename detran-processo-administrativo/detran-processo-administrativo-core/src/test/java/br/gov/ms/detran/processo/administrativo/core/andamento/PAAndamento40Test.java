package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.util.DtnRESTfulClient;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.enums.PAAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import java.util.Date;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class PAAndamento40Test {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento40Test.class);
    
    public PAAndamento40Test() {
    }
    
    @Rule
    public PAAndamentoRule myPAAndamentoRuleTest = new PAAndamentoRule(PAAndamentoEnum.ANDAMENTO_40);
    
    @Test
//    @AssertTrue
    public void testExecutaEspecifico() {
        
        try {
            
            if (PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO.equals(myPAAndamentoRuleTest.getPaAndamentoValido())) {
            
                LOG.debug("Número Processo Administrativo: {0}", myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());

                NotificaProcessoAdministrativoWrapper entradaJson = new NotificaProcessoAdministrativoWrapper();

                entradaJson.setNumeroPortaria("123456");
                entradaJson.setTipo(TipoFasePaEnum.ENTREGA_CNH);
                entradaJson.setDataPublicacaoPortaria(new Date());
                entradaJson.setNumeroProcesso(myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso());

                ExecucaoAndamentoWrapper entrada = 
                        new ExecucaoAndamentoWrapper(
                                myPAAndamentoRuleTest.getProcessoAdministrativo().getNumeroProcesso(), 
                                PAAndamentoEnum.ANDAMENTO_40, 
                                entradaJson);

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
                
//                String retornoExecuta = clientExecuta.getResponse();

                Assert.assertTrue(Response.Status.OK.getStatusCode() == clientExecuta.getResponseCode());
            }
            
        } catch (AppException ex) {
            Assert.fail("Execução falhou.");
        }
    }
}