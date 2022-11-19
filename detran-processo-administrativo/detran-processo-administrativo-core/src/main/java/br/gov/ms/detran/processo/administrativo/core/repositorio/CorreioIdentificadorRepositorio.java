/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.protocolo.entidade.CorreioIdentificador;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class CorreioIdentificadorRepositorio extends AbstractJpaDAORepository<CorreioIdentificador> {
    
    /**
     * 
     * @param em
     * @param idCorreioIdentificador
     * @return
     * @throws AppException 
     */
    public String getIdentificadorPorIdCorreioIdentificador(EntityManager em, String idCorreioIdentificador) throws AppException {

        if(DetranStringUtil.ehBrancoOuNulo(idCorreioIdentificador)) {
            DetranWebUtils.applicationMessageException("Correio identificador inválido.");
        }
        
        CorreioIdentificador correioIdentificador
            = super.find(em, CorreioIdentificador.class, Long.valueOf(idCorreioIdentificador));
        
        if(correioIdentificador == null) {
            DetranWebUtils.applicationMessageException("Correio identificador inválido.");
        }
        
        return correioIdentificador.getIdentificador();
    }
}
