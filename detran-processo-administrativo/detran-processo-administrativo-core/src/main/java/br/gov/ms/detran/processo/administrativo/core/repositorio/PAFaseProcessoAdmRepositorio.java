/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAFaseProcessoAdm;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Carlos
 */
public class PAFaseProcessoAdmRepositorio extends AbstractJpaDAORepository<PAFaseProcessoAdm> {

    public PAFaseProcessoAdm getPAAndamentoProcessoByDescricao(EntityManager em, String descricao) throws DatabaseException {
        List<PAFaseProcessoAdm> result = getListNamedQuery(em, "PAFaseProcessoAdm.PAFaseProcessoAdmByDescricao", descricao);
        return DetranCollectionUtil.ehNuloOuVazio(result) ? null : result.get(0);
    }

    public PAFaseProcessoAdm getPAAndamentoProcessoAtivoByCodigo(EntityManager em, String codigo) throws DatabaseException {
        return getNamedQuery(em, "PAFaseProcessoAdm.PAFaseProcessoAdmAtivoByCodigo", codigo, AtivoEnum.ATIVO);
    }
    
    public List<PAFaseProcessoAdm> getFluxoPorFaseProcesso(EntityManager em, 
            Integer from, Integer to, Long id, String descricao, AtivoEnum ativo)
            throws DatabaseException {
        Object[] params = {id,
            !DetranStringUtil.ehBrancoOuNulo(descricao) ? "%" + descricao + "%" : null,
            AtivoEnum.ATIVO};
        return getListNamedQuery(em, "PAFaseProcessoAdm.getFluxoNaoUtilizadoPeloFluxoProcesso", from, to, params);

    }

    public Long getCountFluxoPorFaseProcesso(EntityManager em, Long id, String descricao, AtivoEnum ativo)
            throws DatabaseException{
        Object[] params = {id,
            !DetranStringUtil.ehBrancoOuNulo(descricao) ? "%" + descricao + "%" : null,
            AtivoEnum.ATIVO};
        return super.getCountHqlEntitySearch(em, "PAFaseProcessoAdm.getCountFluxoNaoUtilizadoPeloFluxoProcesso", params);
    }
}
