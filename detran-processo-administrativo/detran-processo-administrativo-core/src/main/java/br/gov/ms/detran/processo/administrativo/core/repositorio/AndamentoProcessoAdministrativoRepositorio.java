package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.entidade.AndamentoProcessoAdministrativoConsulta;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import java.util.List;
import javax.persistence.EntityManager;

public class AndamentoProcessoAdministrativoRepositorio extends AbstractJpaDAORepository<AndamentoProcessoAdministrativoConsulta> {
    
    /**
     * 
     * @param em
     * @param pa
     * @return
     * @throws AppException 
     */
    public AndamentoProcessoAdministrativoConsulta getProximoAndamento(EntityManager em, ProcessoAdministrativo pa) throws AppException {

        if(pa == null || pa.getId() == null) {
            DetranWebUtils.applicationMessageException("Parâmetro obrigatório inválido.");
        }
        
        List<AndamentoProcessoAdministrativoConsulta> lAndamentos 
            = super.getListNamedQuery(em, "AndamentoProcessoAdministrativoConsulta.getProximoAndamento", pa.getId(), AtivoEnum.ATIVO.ordinal());

        if(DetranCollectionUtil.ehNuloOuVazio(lAndamentos)) {
            return null;
            
        }
        
        return lAndamentos.get(0);
    }
}