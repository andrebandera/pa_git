/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.projeto.generico.DetranGenericRepository;
import static br.gov.ms.detran.comum.util.DetranCollectionUtil.ehNuloOuVazio;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAHabilitacaoRepositorio extends DetranGenericRepository {

    /**
     * 
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public Long getCnhSituacaoEntregaSemAcaoDeDevolucaoDaCnhParaCpfCondutor(EntityManager em, String cpf) throws DatabaseException {

        String sql = "SELECT BAB.Bab_ID " +
                    "FROM TB_BAC_CNH_SITUACAO_ENTREGA BAC " +
                    "INNER JOIN TB_BAB_CNH_CONTROLE BAB ON BAC.Bac_CNH_Controle = BAB.Bab_ID " +
                    "INNER JOIN TB_BAA_CNH_CONTROLE_PESSOA BAA ON BAC.Bac_Pessoa_Nome = BAA.Baa_ID " +
                    "WHERE BAA.Baa_CPF_Entrega = :p0 AND BAC.Ativo = :p1 AND BAB.Ativo = :p1 AND BAA.Ativo = :p1 AND BAC.Bac_Acao = :p2 " +
                    "AND NOT EXISTS (" +
                    "	SELECT 1 FROM dbo.TB_BAC_CNH_SITUACAO_ENTREGA BAC1  " +
"                    	WHERE bac1.Bac_CNH_Controle = bac.Bac_CNH_Controle AND BAC1.Ativo = :p1 AND BAC1.Bac_Acao = :p3)";

        Object[] params = {
            cpf,
            AtivoEnum.ATIVO.ordinal(),
            AcaoEntregaCnhEnum.ENTREGA.ordinal(),
            AcaoEntregaCnhEnum.DEVOLUCAO.ordinal()
        };

        List<Object> result = super.getListNativeQuery(em, sql, params);
        return ehNuloOuVazio(result) ? null : Long.parseLong(result.get(0).toString());
    }
}