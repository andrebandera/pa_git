/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.projeto.generico.DetranGenericRepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Christiano Carrilho.
 */
public class PASegurancaoRepositorio extends DetranGenericRepository {

    /**
     * 
     * @param em
     * @param principal
     * @param hash
     * @return
     * @throws DatabaseException 
     */
    public String getSessionByPrincipalAndHash(EntityManager em, String principal, String hash) throws DatabaseException {

        String sql = "SELECT Xfv_Hash FROM dbo.TB_XFV_SPRING_SESSION_INFORMATION WHERE Xfv_Principal = :p0 AND Xfv_Hash = :p1";

        Object[] params = {
            principal, hash
        };

        List<Object> result = super.getListNativeQuery(em, sql, params);
        return DetranCollectionUtil.ehNuloOuVazio(result) ? null : result.get(0).toString();
    }

    /**
     * 
     * @param em
     * @param principal
     * @param hash
     * @throws DatabaseException 
     */
    public void removeSessionByPrincipalAndHash(EntityManager em, String principal, String hash) throws DatabaseException {

        String sql = "DELETE FROM TB_XFV_SPRING_SESSION_INFORMATION WHERE Xfv_Principal = :p0 AND Xfv_Hash = :p1";

        Object[] params = {
            principal, hash
        };

        super.executeNativeQuery(em, sql, params);
    }
}