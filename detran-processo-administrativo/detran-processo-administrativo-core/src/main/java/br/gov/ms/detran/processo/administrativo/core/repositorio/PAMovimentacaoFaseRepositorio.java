/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAMovimentacaoFase;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAMovimentacaoFaseRepositorio extends AbstractJpaDAORepository<PAMovimentacaoFase> {

    public List<PAMovimentacaoFase> getMovimentacaoFasePorServico(EntityManager em, String servico) throws DatabaseException {
        return getListNamedQuery(em, "PAMovimentacaoFase.getMovimentacaoFasePorServico", servico, AtivoEnum.ATIVO);
    }
    
}
