/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoRascunhoBloqueio;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRascunhoBloqueioEnum;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author lilly
 */
public class ProcessoRascunhoBloqueioRepositorio extends AbstractJpaDAORepository<ProcessoRascunhoBloqueio> {

    public void excluirRascunhosCadastradosPorBloqueioPessoa(EntityManager em, Long idProcessoBloqueioPessoa) throws DatabaseException {
         super.executeNamedQuery(em, "ProcessoRascunhoBloqueio.excluirRascunhosCadastradosPorBloqueioPessoa", idProcessoBloqueioPessoa, SituacaoRascunhoBloqueioEnum.NAO_BLOQUEADO);
    }

    public List<ProcessoRascunhoBloqueio> getRascunhosBloqueadosPorBloqueioPessoa(EntityManager em, Long idProcessoBloqueioPessoa) throws DatabaseException {
        return getListNamedQuery(em, "ProcessoRascunhoBloqueio.getRascunhosBloqueadosPorBloqueioPessoa", idProcessoBloqueioPessoa, SituacaoRascunhoBloqueioEnum.NAO_BLOQUEADO, AtivoEnum.ATIVO);
    }

    public ProcessoRascunhoBloqueio getRascunhoPorProcessoAdministrativo(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws DatabaseException {
        return getNamedQuery(em, "ProcessoRascunhoBloqueio.getRascunhoPorProcessoAdministrativo", processoAdministrativo.getId(), AtivoEnum.ATIVO);
    }
    
    public Date getRascunhoPorId(EntityManager em, Long id) throws DatabaseException {
        
        ProcessoRascunhoBloqueio rascunho = getNamedQuery(em, "ProcessoRascunhoBloqueio.getRascunhoPorProcessoAdministrativo", id, AtivoEnum.ATIVO);
        
        return rascunho != null ? rascunho.getDataInicio() : null;
    }
    
}
