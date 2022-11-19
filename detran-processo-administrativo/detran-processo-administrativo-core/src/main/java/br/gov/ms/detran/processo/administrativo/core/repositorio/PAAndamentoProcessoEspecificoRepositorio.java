/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcessoEspecifico;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamentoProcessoEspecificoRepositorio extends AbstractJpaDAORepository<PAAndamentoProcessoEspecifico> {

    public PAAndamentoProcessoEspecifico getAndamentoEspecificoPorProcessoAdministrativo(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws DatabaseException {
        return getNamedQuery(em, "PAAndamentoProcessoEspecifico.getAndamentoEspecificoPorProcessoAdministrativo", processoAdministrativo.getId());
    }
    
}
