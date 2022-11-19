/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.DtnRESTfulClient;
import br.gov.ms.detran.comum.util.JsonJacksonUtils;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.PAAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoWrapper;
import javax.ws.rs.core.Response;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamentoRule implements TestRule {
    
    private static final Logger LOG = Logger.getLogger(PAAndamentoRule.class);

    private final PAAndamentoEnum andamento;
    
    private ProcessoAdministrativo processoAdministrativo;
    
    private PAAndamentoProcessoAdministrativoValidoEnum paAndamentoValido;
    
    public PAAndamentoRule(PAAndamentoEnum andamento) {
        this.andamento = andamento;
    }
    
    @Override
    public Statement apply(Statement stmnt, Description d) {
        
        return new Statement() {
            
            @Override
            public void evaluate() throws Throwable {
                
                try {
                    
                    LOG.info("Processo Administrativo para {0}", andamento);

                    String urlPAAndamento 
                        = "/detran-processo-administrativo/resource/execucaoandamentos/paandamento";

                    DtnRESTfulClient clientPAAndamento = new DtnRESTfulClient();
                    
                    clientPAAndamento
                        .setServerName(PAAndamentoServerNameEnum.getServerName())
                        .setResourcePath(urlPAAndamento)
                        .setContentType("application/json")
                        .setRequestMethod("POST")
                        .addHeaderParams("login", "00330851101")
                        .addHeaderParams("postoAtendimento", "TESTE_PA_ANDAMENTO")
                        .setData(new ExecucaoAndamentoWrapper(andamento))
                        .execute();

                    LOG.info("**** HTTP code: {0}", clientPAAndamento.getResponseCode());
                    
                    if(Response.Status.OK.getStatusCode() != clientPAAndamento.getResponseCode()) {
                        
                        LOG.info("Processo Administrativo não encontrado para {0}.", andamento);
                        
                        paAndamentoValido = PAAndamentoProcessoAdministrativoValidoEnum.FALHA_AO_BUSCAR_PA_PARA_ANDAMENTO;
                        DetranWebUtils.applicationMessageException("Processo Administrativo não encontrado para {0}.", andamento.name());
                    
                    } else {

                        String retornoPAAndamento = clientPAAndamento.getResponse();

                        if(!DetranStringUtil.ehBrancoOuNulo(retornoPAAndamento)){
                            processoAdministrativo 
                                = JsonJacksonUtils
                                    .forObject(
                                        retornoPAAndamento, 
                                        ProcessoAdministrativo.class
                                    );
                        }

                        if(processoAdministrativo == null) {
                            
                            paAndamentoValido = PAAndamentoProcessoAdministrativoValidoEnum.NAO_EXISTE_PA_NO_ANDAMENTO;
//                            DetranWebUtils.applicationMessageException("Processo Administrativo não encontrado para {0}.", andamento);
                        } else {
                        
                            LOG.debug("Número Processo Administrativo: {0}", processoAdministrativo.getNumeroProcesso());

                            paAndamentoValido = PAAndamentoProcessoAdministrativoValidoEnum.PA_ENCONTRADO;
                        }
                    }

                    stmnt.evaluate();
                    
                } finally {
                    LOG.info("Fim recuperação Processo Administrativo para {0}.", andamento);
                }
            }
        };
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public PAAndamentoProcessoAdministrativoValidoEnum getPaAndamentoValido() {
        return paAndamentoValido;
    }
}