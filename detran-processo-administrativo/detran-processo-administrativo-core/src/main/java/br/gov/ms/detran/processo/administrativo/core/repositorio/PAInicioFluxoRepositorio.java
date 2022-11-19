package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.entidade.PAInicioFluxo;
import javax.persistence.EntityManager;

public class PAInicioFluxoRepositorio extends AbstractJpaDAORepository<PAInicioFluxo> {
    
    public PAInicioFluxo getPAInicioFluxoAtivoPorProcessoAdministrativo(EntityManager em, Long idPA)throws AppException{
        
        return getNamedQuery(em, "PAInicioFluxo.getPAInicioFluxoAtivoPorProcessoAdministrativo", idPA, AtivoEnum.ATIVO);
    }
}