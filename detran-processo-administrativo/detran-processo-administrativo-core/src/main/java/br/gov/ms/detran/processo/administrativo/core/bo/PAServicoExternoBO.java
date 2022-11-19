package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAServicoExternoRepositorio;
import javax.persistence.EntityManager;

public class PAServicoExternoBO {
    
    private static final Logger LOG = Logger.getLogger(PAServicoExternoBO.class);

    /**
     * @param em
     * @param URL_SERVICO
     * @return
     * @throws DatabaseException 
     */
    public Boolean validarServicoExternoAtivo(EntityManager em, String URL_SERVICO) throws DatabaseException {
        
        return null != new PAServicoExternoRepositorio().getPAServicoExternoPorNomeAtivo(em, URL_SERVICO);
        
    }
}