/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.cenarios;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public interface IInstauracaoCenario {

    public IBaseEntity instaurar(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException;
}
