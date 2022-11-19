/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.projeto.generico.DetranGenericRepository;
import static br.gov.ms.detran.comum.util.DetranCollectionUtil.ehNuloOuVazio;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.wrapper.PAAmparoLegalWrapper;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

/**
 *
 * @author desenvolvimento
 */
public class PAInfracaoRepositorio extends DetranGenericRepository {

    /**
     *
     * @param em
     * @param codigoInfracao
     * @return
     * @throws DatabaseException
     */
    public boolean naoExisteAcaoInfracaoPenalidadeAtivoPorCodigoInfracao(EntityManager em, String codigoInfracao) throws DatabaseException {

        String sql
                = "SELECT TOP(1) MBA.Mba_ID "
                + "FROM TB_MBA_ACAO_INFRACAO_PENALIDADE MBA "
                + "JOIN TB_MAA_TABELA_INFRACAO MAA ON MBA.Mba_Tabela_Infracao = MAA.Maa_ID "
                + "WHERE MAA.Maa_Codigo = :p0 AND MAA.Ativo = :p1 AND MBA.Ativo = :p2";

        Object[] params = new Object[]{
            codigoInfracao,
            AtivoEnum.ATIVO.ordinal(),
            AtivoEnum.ATIVO.ordinal()
        };

        List<Object> result = super.getListNativeQuery(em, sql, params);
        return ehNuloOuVazio(result);
    }

    /**
     * 
     * @param em
     * @param infracaoCodigo
     * @param amparoLegal
     * @param acaoInstauracaoEnumOrdinal
     * @param acaoSistemaEnumOrdinal
     * @return
     * @throws DatabaseException 
     */
    public boolean hasAcaoInstauracaoDaInfracao(EntityManager em, 
            String infracaoCodigo, 
            Long amparoLegal, Integer acaoInstauracaoEnumOrdinal, 
            Integer acaoSistemaEnumOrdinal,
            Date dataInfracao) throws DatabaseException {

        Long mbaId = 
                getAcaoInfracaoPenalidadePeloCodigoEAmparoLegal(
                    em, infracaoCodigo, amparoLegal, 
                    acaoInstauracaoEnumOrdinal, acaoSistemaEnumOrdinal, dataInfracao
                );

        return mbaId != null;
    }

    /**
     * 
     * @param em
     * @param infracaoCodigo
     * @param amparoLegal
     * @param acaoInstauracaoEnumOrdinal
     * @param acaoSistemaEnumOrdinal
     * @return
     * @throws DatabaseException 
     */
    public Long getAcaoInfracaoPenalidadePeloCodigoEAmparoLegal(EntityManager em, 
            String infracaoCodigo, Long amparoLegal, Integer acaoInstauracaoEnumOrdinal, 
            Integer acaoSistemaEnumOrdinal,
            Date dataInfracao) throws DatabaseException {

        String sql = 
                "SELECT MBA.Mba_ID " + 
                "FROM TB_MBA_ACAO_INFRACAO_PENALIDADE MBA " + 
                "INNER JOIN TB_MAZ_INFRACAO_PENALIDADE MAZ ON MBA.Mba_Infracao_Penalidade = MAZ.Maz_ID " + 
                "INNER JOIN TB_MBT_AMPARO_LEGAL MBT ON MAZ.Maz_Amparo_Legal = MBT.Mbt_ID " + 
                "INNER JOIN TB_MAA_TABELA_INFRACAO MAA ON MBA.Mba_Tabela_Infracao = MAA.Maa_ID " + 
                "WHERE MAA.Maa_Codigo = :p0 "
                + " AND MAA.Ativo = :p1 "
                  + " AND MBA.Ativo = :p1 "
                  + " AND ((MBA.Mba_Data_Validade_Inicial is null) "
                  + "           OR (MBA.Mba_Data_Validade_Final is null) "
                  + "           OR (:p5 BETWEEN MBA.Mba_Data_Validade_Inicial and MBA.Mba_Data_Validade_Final)) "
                + " AND MBT.Mbt_ID = :p2 "
                + " AND MBA.Mba_Codigo_Acao_Instauracao = :p3 "
                + " AND MAZ.Maz_Acao_Sistema_PA = :p4";

        Object[] params = {
            infracaoCodigo, AtivoEnum.ATIVO.ordinal(), amparoLegal, 
            acaoInstauracaoEnumOrdinal, acaoSistemaEnumOrdinal, dataInfracao
        };

        List<Object> result = super.getListNativeQuery(em, sql, params);

        return (ehNuloOuVazio(result) ? null : Long.parseLong(result.get(0).toString()));
    }

    /**
     * 
     * @param em
     * @param infracaoCodigo
     * @param amparoLegal
     * @param acaoInstauracaoEnumOrdinal
     * @param acaoSistemaEnumOrdinal
     * @return
     * @throws DatabaseException 
     */
    public BigDecimal getValorPenalAcaoInfracaoPenalidade(EntityManager em, 
            String infracaoCodigo, Long amparoLegal, Integer acaoInstauracaoEnumOrdinal, 
            Integer acaoSistemaEnumOrdinal, Date dataInfracao) throws DatabaseException {

        String sql = 
                "SELECT MBA.Mba_Valor_Penal " + 
                "FROM TB_MBA_ACAO_INFRACAO_PENALIDADE MBA " + 
                "INNER JOIN TB_MAZ_INFRACAO_PENALIDADE MAZ ON MBA.Mba_Infracao_Penalidade = MAZ.Maz_ID " + 
                "INNER JOIN TB_MBT_AMPARO_LEGAL MBT ON MAZ.Maz_Amparo_Legal = MBT.Mbt_ID " + 
                "INNER JOIN TB_MAA_TABELA_INFRACAO MAA ON MBA.Mba_Tabela_Infracao = MAA.Maa_ID " + 
                "WHERE MAA.Maa_Codigo = :p0 "
                + " AND MAA.Ativo = :p1 "
                + " AND MBA.Ativo = :p1 "
                + " AND MBT.Mbt_ID = :p2 "
                + " AND ((MBA.Mba_Data_Validade_Inicial is null) "
                + "           OR (MBA.Mba_Data_Validade_Final is null) "
                + "           OR (:p5 BETWEEN MBA.Mba_Data_Validade_Inicial and MBA.Mba_Data_Validade_Final)) "
                + " AND MBA.Mba_Codigo_Acao_Instauracao = :p3 "
                + " AND MAZ.Maz_Acao_Sistema_PA = :p4";

        Object[] params = {
            infracaoCodigo, AtivoEnum.ATIVO.ordinal(), amparoLegal, 
            acaoInstauracaoEnumOrdinal, acaoSistemaEnumOrdinal, dataInfracao
        };

        List<Object> result = super.getListNativeQuery(em, sql, params);

        return (ehNuloOuVazio(result) ? null : BigDecimal.valueOf(Double.parseDouble(result.get(0).toString())));
    }

    /**
     *
     * @param em
     * @param amparoLegalId
     * @return
     * @throws DatabaseException 
     */
    public PAAmparoLegalWrapper getAmparoLegalComArtigoIncisoParagrafoPorId(EntityManager em, Long amparoLegalId) throws DatabaseException {

        String sql = "SELECT Mbt_ID as id, Mbt_Artigo as artigo, Mbt_Paragrafo as paragrafo, Mbt_Inciso as inciso FROM TB_MBT_AMPARO_LEGAL WHERE Mbt_ID = :p0";

        Query query = em.createNativeQuery(sql).setParameter("p0", amparoLegalId);

        query.unwrap(SQLQuery.class)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("artigo", StringType.INSTANCE)
                .addScalar("paragrafo", StringType.INSTANCE)
                .addScalar("inciso", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(PAAmparoLegalWrapper.class));

        return (PAAmparoLegalWrapper) query.getSingleResult();
    }
    public PAAmparoLegalWrapper getAmparoLegalComArtigoIncisoParagrafoPorCodigoInfracao(EntityManager em,
                                                                                        String codigoInfracao) throws DatabaseException {

        String sql = "SELECT mbt.Mbt_ID as id, mbt.Mbt_Artigo as artigo, mbt.Mbt_Paragrafo as paragrafo, mbt.Mbt_Inciso as inciso " +
                "FROM TB_MBT_AMPARO_LEGAL mbt " +
                "   inner join TB_MAA_TABELA_INFRACAO maa on maa.Maa_Amparo_Legal = mbt.Mbt_ID " +
                "WHERE maa.Maa_Codigo = :p0";

        Query query = em.createNativeQuery(sql).setParameter("p0", codigoInfracao);

        query.unwrap(SQLQuery.class)
                .addScalar("id", LongType.INSTANCE)
                .addScalar("artigo", StringType.INSTANCE)
                .addScalar("paragrafo", StringType.INSTANCE)
                .addScalar("inciso", StringType.INSTANCE)
                .setResultTransformer(Transformers.aliasToBean(PAAmparoLegalWrapper.class));

        return (PAAmparoLegalWrapper) query.getSingleResult();
    }

    /**
     * 
     * @param em
     * @param infracaoCodigo
     * @param amparoLegal
     * @param acaoInstauracaoEnumOrdinal
     * @param acaoSistemaEnumOrdinal
     * @return
     * @throws DatabaseException 
     */
    public boolean isTipoProcessoDeSuspensao(EntityManager em, 
            String infracaoCodigo, Long amparoLegal, Integer acaoInstauracaoEnumOrdinal, 
            Integer acaoSistemaEnumOrdinal) throws DatabaseException {

        String sql = "SELECT MBB.Mbb_ID " +
                    "FROM TB_MBB_MOTIVO_PENALIDADE MBB " +
                    "INNER JOIN TB_MAZ_INFRACAO_PENALIDADE MAZ ON MBB.Mbb_Infracao_Penalidade = MAZ.Maz_ID " +
                    "WHERE MAZ.Maz_ID IN (" +
                    "SELECT MAZ.Maz_ID " +
                    "FROM TB_MBA_ACAO_INFRACAO_PENALIDADE MBA " +
                    "INNER JOIN TB_MAZ_INFRACAO_PENALIDADE MAZ ON MBA.Mba_Infracao_Penalidade = MAZ.Maz_ID " +
                    "INNER JOIN TB_MBT_AMPARO_LEGAL MBT ON MAZ.Maz_Amparo_Legal = MBT.Mbt_ID " +
                    "INNER JOIN TB_MAA_TABELA_INFRACAO MAA ON MBA.Mba_Tabela_Infracao = MAA.Maa_ID " +
                    "WHERE MAA.Maa_Codigo = :p0 AND MAA.Ativo = :p1 AND MBT.Mbt_ID = :p2 AND MBA.Mba_Codigo_Acao_Instauracao = :p3 AND MAZ.Maz_Acao_Sistema_PA = :p4)" +
                    "AND MBB.Mbb_Tipo_Processo = :p5";

        Object[] params = {
            infracaoCodigo, 
            AtivoEnum.ATIVO.ordinal(), 
            amparoLegal, 
            acaoInstauracaoEnumOrdinal, 
            acaoSistemaEnumOrdinal,
            TipoProcessoEnum.SUSPENSAO.ordinal()
        };

        List<Object> result = super.getListNativeQuery(em, sql, params);

        return !ehNuloOuVazio(result);
    }
}
