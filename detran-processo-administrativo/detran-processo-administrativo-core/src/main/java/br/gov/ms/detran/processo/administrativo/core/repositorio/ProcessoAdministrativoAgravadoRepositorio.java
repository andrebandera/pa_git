/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoAgravado;
import javax.persistence.EntityManager;

/**
 *
 * @author Lillydi
 */
public class ProcessoAdministrativoAgravadoRepositorio extends AbstractJpaDAORepository<ProcessoAdministrativoAgravado> {

    public ProcessoAdministrativoAgravado getAgravadoPorPAOriginal(EntityManager em, Long idPA) throws DatabaseException {
        return getNamedQuery(em, "ProcessoAdministrativoAgravado.getAgravadoPorPAOriginal", idPA);
    }

}
