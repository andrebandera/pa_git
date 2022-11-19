/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.constantes.ParametrizacaoEnum;
import br.gov.ms.detran.comum.projeto.generico.DetranGenericRepository;
import static br.gov.ms.detran.comum.util.DetranCollectionUtil.montaLista;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Christiano Carrilho.
 */
public class PAParametrizacaoRepositorio extends DetranGenericRepository {

    /**
     * 
     * @param em
     * @param codigo
     * @return
     * @throws DatabaseException 
     */
    public List<String> getEmailDestinatarioPA(EntityManager em, Integer codigo) throws DatabaseException {

        String sql = 
                "SELECT Z99.Z99_Valor_Alfanumerico " +
                "FROM TB_Z99_PARAMETRIZACAO_VALOR Z99 " +
                "INNER JOIN TB_Z98_PARAMETRIZACAO Z98 ON Z99.Z99_Parametro = Z98.Z98_ID " +
                "WHERE Z98.Z98_Codigo = :p0 AND Z99.Ativo = :p1";

        Object[] params = {
            codigo, AtivoEnum.ATIVO.ordinal()
        };

        Object result = super.getNativeQuery(em, sql, params);
        if (result == null) return new ArrayList<>();
        String[] emails = result.toString().split(";");

        return montaLista(emails);
    }
    
    /**
     * 
     * @param em
     * @return
     * @throws DatabaseException 
     */
    public BigDecimal getQuantidadeLimiteDiarioEmissaoNotificacao(EntityManager em) throws DatabaseException {

        String sql = 
                "SELECT Z99.Z99_Valor_Numerico " +
                "FROM TB_Z99_PARAMETRIZACAO_VALOR Z99 " +
                "   INNER JOIN TB_Z98_PARAMETRIZACAO Z98 ON Z99.Z99_Parametro = Z98.Z98_ID " +
                "WHERE Z98.Z98_Codigo = :p0 "
                + " AND Z99.Ativo = :p1 "
            ;

        Object[] params = {
            ParametrizacaoEnum.QUANTIDADE_LIMITE_DIARIO_EMISSAO_NOTIFICACAO_PA.getCode(), AtivoEnum.ATIVO.ordinal()
        };

        Object result = super.getNativeQuery(em, sql, params);
        
        return (BigDecimal) result;
    }
}