/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.projeto.constantes.CodigoTipoDocumento;
import br.gov.ms.detran.comum.projeto.generico.DetranGenericRepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.wrapper.PADocumentoPessoaWrapper;
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
public class PADocumentoPessoaRepositorio extends DetranGenericRepository {

    public PADocumentoPessoaRepositorio() {
    }

    /**
     * 
     * @param em
     * @param numeroDetran
     * @return
     * @throws DatabaseException 
     */
    public PADocumentoPessoaWrapper getCpfDocumentoPessoa(EntityManager em, Long numeroDetran) throws DatabaseException {

        String sql = "SELECT XAN.Xan_ID AS id, XAN.Xan_Numero_Detran AS numeroDetran, " + 
                    "XAN.Xan_Numero_Documento AS numeroDocumento, " + 
                    "XBT.Xbt_Codigo AS codigoDocumento, XBQ.Xbq_Nome AS nomePrincipalPessoa " +
                    "FROM TB_XAN_IDENTIFICACAO_DOCUMENTO XAN " +
                    "INNER JOIN TB_XBV_IDENTIFICACAO_DOCUMENTO_SEQUENCIA XBV ON XBV.Xbv_Identificacao_Documento = XAN.Xan_ID " +
                    "INNER JOIN TB_XBT_SUBTIPO_DOCUMENTO XBT ON XAN.Xan_SubTipo_Documento = XBT.Xbt_ID " +
                    "INNER JOIN TB_XAC_PESSOA XAC ON XAN.Xan_Numero_Detran = XAC.Xac_ID " +
                    "INNER JOIN TB_XBQ_NOME XBQ ON XBQ.Xbq_Numero_Detran = XAC.Xac_ID AND XBQ.Xbq_Indicativo_Principal = :p3 " +
                    "WHERE XBV.Ativo = :p0 AND XBT.Xbt_Codigo = :p1 AND XAC.Xac_ID = :p2";

        Query query = 
            em.createNativeQuery(sql)
                .setParameter("p0", AtivoEnum.ATIVO.ordinal())
                .setParameter("p1", CodigoTipoDocumento.CPF)
                .setParameter("p2", numeroDetran)
                .setParameter("p3", BooleanEnum.SIM)
            ;

        query
            .unwrap(SQLQuery.class)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("numeroDetran", LongType.INSTANCE)
                .addScalar("numeroDocumento", StringType.INSTANCE)
                .addScalar("codigoDocumento", IntegerType.INSTANCE)
                .addScalar("nomePrincipalPessoa", StringType.INSTANCE)
            .setResultTransformer(Transformers.aliasToBean(PADocumentoPessoaWrapper.class));

        return (PADocumentoPessoaWrapper) query.getSingleResult();
    }
}