/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.job;

import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author Christiano Carrilho.
 */
@Singleton
@Startup
public class JobInstauracaoPA {

    private static final Integer QUANTIDADE_PA_A_GERAR = 50000;
    private static final Integer QUANTIDADE_REGISTROS_BUSCADOS_BLOCO = 100;

    @EJB
    private IProcessoAdministrativoService paService;

    private static final Logger LOGGER = Logger.getLogger(JobInstauracaoPA.class);

    @PostConstruct
    public void instaurar() {

        try {

//            paService.gerarPA(QUANTIDADE_PA_A_GERAR);
//            paService.instaurar(QUANTIDADE_REGISTROS_BUSCADOS_BLOCO);

        } catch (Exception ex) {
            // É PRECISO GERAR ALGUM REGISTRO DE LOG AQUI?
            LOGGER.error("Erro", ex);
        }
    }
}