/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAEtapaAndamento;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Carlos
 */
public class PAEtapaAndamentoRepositorio extends AbstractJpaDAORepository<PAEtapaAndamento>{
    
    public List<PAEtapaAndamento> getPAEtapaAndamentoAtivoPorPAAndamentoProcesso(EntityManager em, Long PAAndamentoProcessoId) throws DatabaseException {
        return super.getListNamedQuery(em, "PAEtapaAndamento.getPAEtapaAndamentoAtivoPorPAAndamentoProcesso", PAAndamentoProcessoId, AtivoEnum.ATIVO);
    }
}
