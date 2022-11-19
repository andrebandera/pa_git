/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.generico.DetranGenericRepository;
import static br.gov.ms.detran.comum.util.DetranCollectionUtil.ehNuloOuVazio;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.wrapper.PAMunicipioWrapper;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

/**
 *
 * @author Christiano Carrilho.
 */
public class PAApoioRepositorio extends DetranGenericRepository {

    /**
     * 
     * @param em
     * @param codigo
     * @return
     * @throws DatabaseException 
     */
    public Long getOrgaoAutuadorIdPeloCodigo(EntityManager em, Integer codigo) throws DatabaseException {

        String sql = "SELECT XAK.Xak_ID "
                    + " FROM TB_XAK_ORGAO_AUTUADOR XAK "
                    + " WHERE XAK.Xak_Codigo_Orgao_Autuador = :p0 "
                    + "     AND XAK.Ativo = :p1 ";

        Object[] params = {codigo, AtivoEnum.ATIVO.ordinal()};

        List<Object> result = super.getListNativeQuery(em, sql, params);
        return ehNuloOuVazio(result) ? null : Long.parseLong(result.get(0).toString());
    }

    /**
     * 
     * @param em
     * @param id
     * @return
     * @throws DatabaseException 
     */
    public Integer getOrgaoAutuadorPeloID(EntityManager em, Long id) throws DatabaseException {

        String sql = "SELECT XAK.Xak_Codigo_Orgao_Autuador FROM TB_XAK_ORGAO_AUTUADOR XAK WHERE XAK.Xak_ID  = :p0 ";
        List<Object> result = super.getListNativeQuery(em, sql, new Object[]{id});
        return ehNuloOuVazio(result) ? null : Integer.parseInt(result.get(0).toString());
    }

    /**
     * 
     * @param em
     * @param codigo
     * @return
     * @throws DatabaseException 
     */
    public PAMunicipioWrapper getMunicipioPeloCodigo(EntityManager em, Integer codigo) throws DatabaseException {

        String sql = "SELECT Xab_ID as id, Xab_Municipio as codigo, Xab_Nome as nome FROM TB_XAB_MUNICIPIO WHERE Xab_Municipio = :p0";
        Query query = em.createNativeQuery(sql).setParameter("p0", codigo);

        query.unwrap(SQLQuery.class)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("codigo", IntegerType.INSTANCE)
                .addScalar("nome", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(PAMunicipioWrapper.class));

        return (PAMunicipioWrapper) query.getSingleResult();
    }
}