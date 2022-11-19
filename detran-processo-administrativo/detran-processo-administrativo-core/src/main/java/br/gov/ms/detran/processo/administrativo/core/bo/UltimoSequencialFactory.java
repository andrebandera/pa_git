/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.ExceptionGeneric;
import br.gov.ms.detran.comum.util.logger.Logger;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 * Classe responsável por recuperar o último sequencial da Z94 conforme 
 * o código informado e gera o próximo número.
 * 
 * @author Christiano Carrilho.
 */
public class UltimoSequencialFactory {

    private static final Logger LOG = Logger.getLogger(UltimoSequencialFactory.class);

    private UltimoSequencialFactory() {
    }

    public static UltimoSequencialFactory getInstance() {
        return NumeroProcessoHolder.INSTANCE;
    }

    private static class NumeroProcessoHolder {
        private static final UltimoSequencialFactory INSTANCE = new UltimoSequencialFactory();
    }

    /**
     * Recupera o último sequencial da Z94 e gera o próximo número conforme o tipo informado.
     * 
     * @param em EntityManager.
     * @param codigo Código do tipo do sequencial.
     * @return Próximo sequencial do tipo informado.
     * @throws AppException 
     */
    public String getUltimoSequencial(EntityManager em, int codigo) throws AppException {

        StringBuilder novoNumero = new StringBuilder();

        try {

            Query query = em.createNativeQuery(
                    "UPDATE TB_Z94_ULTIMO_NUMERO_SEQUENCIAL WITH (ROWLOCK) " +
                            "SET z94_Sequencia = z94_Sequencia + 1, Versao_Registro = Versao_Registro + 1 " +
                            "WHERE Z94_Tipo = (SELECT Z93_ID FROM TB_Z93_TIPO_NUMERO_SEQUENCIAL WHERE Z93_Codigo = :codigo) " + 
                            " AND Z94_ANO = :ano");

            query.setParameter("codigo", Long.parseLong(String.valueOf(codigo)));
            query.setParameter("ano", Utils.getAnoCorrente());
            query.executeUpdate();

            Query query2 = em.createNativeQuery(
                    "SELECT z94_Sequencia " + 
                    "FROM TB_Z94_ULTIMO_NUMERO_SEQUENCIAL " +
                    "WHERE Z94_Tipo = (SELECT Z93_ID FROM TB_Z93_TIPO_NUMERO_SEQUENCIAL " + 
                            "WHERE Z93_Codigo = :codigo)" + 
                    " AND Z94_ANO = :ano");

            query2.setParameter("codigo", Long.parseLong(String.valueOf(codigo)));
            query2.setParameter("ano", Utils.getAnoCorrente());

            Object sequencial = query2.getSingleResult();

            novoNumero
                    .append(Utils.getAnoCorrente())
                    .append(Utils.leftFillsZero(String.valueOf(sequencial), 6));

            LOG.debug("último sequencial gerado: {0}", novoNumero);

            return novoNumero.toString();

        } catch (NoResultException ex) {
            LOG.warn("Não há sequencia para o código {0}", codigo);
        } catch (Exception ex) {
            LOG.error("Erro:", ex);
        }

        throw new ExceptionGeneric("Erro último número sequencial (Z94) - Código " + codigo);
    }
}