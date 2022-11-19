/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.constantes.CodigoTipoDocumento;
import br.gov.ms.detran.comum.projeto.generico.DetranGenericRepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import static br.gov.ms.detran.comum.util.DetranCollectionUtil.ehNuloOuVazio;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Christiano Carrilho.
 */
public class PAPessoaRepositorio extends DetranGenericRepository {

    /**
     * 
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public boolean isCondutorFalecido(EntityManager em, String cpf) throws DatabaseException {

        String sql = 
                "SELECT XAQ.Xaq_Indicador_Falecimento " +
                "FROM TB_XAN_IDENTIFICACAO_DOCUMENTO XAN " +
                "INNER JOIN TB_XAQ_PESSOA_FISICA XAQ ON XAN.Xan_Numero_Detran = XAQ.Xaq_Numero_Detran " +
                "INNER JOIN TB_XBT_SUBTIPO_DOCUMENTO XBT ON XAN.Xan_SubTipo_Documento = XBT.Xbt_ID " +
                "WHERE XBT.Xbt_Codigo = 0 AND XAN.Xan_Numero_Documento LIKE :p0 AND XBT.Ativo = :p1";

        Object[] params = {
            cpf, AtivoEnum.ATIVO.ordinal()
        };

        List<Object> result = super.getListNativeQuery(em, sql, params);
        if (DetranCollectionUtil.ehNuloOuVazio(result)) return false;

        Object indicativoFalecimento = result.get(0);
        if (indicativoFalecimento == null) return false;

        return Boolean.parseBoolean(indicativoFalecimento.toString());
    }

    /**
     * 
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public Long buscarUsuarioCadastrado(EntityManager em, String cpf) throws DatabaseException {

        String sql = 
                "SELECT XEA.Xea_ID " +
                "FROM TB_XAN_IDENTIFICACAO_DOCUMENTO XAN " +
                "INNER JOIN TB_XAQ_PESSOA_FISICA XAQ ON XAN.Xan_Numero_Detran = XAQ.Xaq_Numero_Detran " +
                "INNER JOIN TB_XBT_SUBTIPO_DOCUMENTO XBT ON XAN.Xan_SubTipo_Documento = XBT.Xbt_ID " +
                "INNER JOIN TB_XEA_CA_USUARIO XEA ON XEA.Xea_Numero_Detran = XAQ.Xaq_Numero_Detran " +
                "WHERE XBT.Xbt_Codigo = :p0 AND XAN.Xan_Numero_Documento LIKE :p1 AND XBT.Ativo = :p2 AND XEA.Ativo = :p2";

        Object[] params = {
            CodigoTipoDocumento.CPF,
            cpf,
            AtivoEnum.ATIVO.ordinal()
        };

        List<Object> result = super.getListNativeQuery(em, sql, params);
        if (ehNuloOuVazio(result)) return null;
        Object usuario = result.get(0);

        return usuario != null ? Long.parseLong(usuario.toString()) : null;
    }

    /**
     * 
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public Long buscarNumeroDetranPessoaPeloCPF(EntityManager em, String cpf) throws DatabaseException {

        String sql = "SELECT XAC.Xac_ID " +
                    "FROM TB_XAN_IDENTIFICACAO_DOCUMENTO XAN " +
                    "INNER JOIN TB_XAC_PESSOA XAC ON XAN.Xan_Numero_Detran = XAC.Xac_ID " +
                    "INNER JOIN TB_XBT_SUBTIPO_DOCUMENTO XBT ON XAN.Xan_SubTipo_Documento = XBT.Xbt_ID " +
                    "WHERE XBT.Xbt_Codigo = :p0 AND XAN.Xan_Numero_Documento LIKE :p1";

        Object[] params = {
            CodigoTipoDocumento.CPF,
            cpf
        };

        List<Object> result = super.getListNativeQuery(em, sql, params);
        return ehNuloOuVazio(result) ? null : Long.parseLong(result.get(0).toString());
    }
}