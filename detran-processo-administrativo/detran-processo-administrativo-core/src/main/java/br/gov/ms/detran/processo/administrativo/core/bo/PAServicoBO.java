package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.PAServico;
import java.util.Calendar;
import javax.persistence.EntityManager;

public class PAServicoBO {
    
    private static final Logger LOG = Logger.getLogger(PAServicoBO.class);
    
    IProcessoAdministrativoService processoAdministrativoService;
    IPAControleFalhaService paControleFalha;

    IProcessoAdministrativoService getProcessoAdministrativoService() {
        if (processoAdministrativoService == null) {
            processoAdministrativoService = ServiceJndiLocator.<IProcessoAdministrativoService>lookup("ejb/ProcessoAdministrativoService");
        }
        return processoAdministrativoService;
    }

    IPAControleFalhaService getControleFalha() {
        if (paControleFalha == null) {
            paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
        }
        return paControleFalha;
    }
    
    /**
     * 
     * @param em
     * @param objetoEntrada
     * @param servico
     * @param resultado 
     */
    public void gravar(EntityManager em, String objetoEntrada, String servico, Boolean resultado) {
        
        try {
            
            PAServico pAServico = new PAServico();

            pAServico.setDataServico(Calendar.getInstance().getTime());
            pAServico.setEntrada(objetoEntrada);
            pAServico.setServico(servico);
            pAServico.setResultado(resultado);
            
            pAServico.setDefineUsuarioSessao(Boolean.FALSE);
            pAServico.setUsuarioInclusao("SERVONLINE_PROCESSO_ADMINISTRATIVO");

            new AbstractJpaDAORepository().insert(em, pAServico);
        
        } catch(Exception e) {
            LOG.debug("Erro sem tratamento.", e);
            getControleFalha().gravarFalha(e, "Erro ao gravar serviço WS executado");
        }
    }
}
