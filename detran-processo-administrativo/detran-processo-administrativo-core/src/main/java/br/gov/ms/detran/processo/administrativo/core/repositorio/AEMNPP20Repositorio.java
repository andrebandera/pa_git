package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class AEMNPP20Repositorio extends AbstractJpaDAORepository {
    
    private static final Logger LOG = Logger.getLogger(AEMNPP20Repositorio.class);

    /**
     * 
     * @param em
     * @return
     * @throws AppException 
     */
    public List getInfracoesEspecificadas(EntityManager em) throws AppException {
        
        StringBuilder sql = new StringBuilder();
        
        sql
            .append("select distinct SUBSTRING(Maa_Codigo, 1, 4) ")
            .append("from TB_MAA_TABELA_INFRACAO maa ")
            .append("	inner join TB_MBA_ACAO_INFRACAO_PENALIDADE mba on mba.Mba_Tabela_Infracao = maa.Maa_ID and maa.Ativo = 1 ")
            .append("	inner join TB_MAZ_INFRACAO_PENALIDADE maz on maz.Maz_ID = mba.Mba_Infracao_Penalidade ")
            .append("	inner join TB_MBT_AMPARO_LEGAL mbt on mbt.Mbt_ID = maz.Maz_Amparo_Legal ")
            .append("where (Mbt_ID in (372,369,370) ")
            .append("			or maa_codigo in (50291, 50292, 50293, 50450, 51851, 54521, 59670, 65992, 66371, 66372, 69120, 73662, 74550, 74630)) ")
            .append("	and Maa.Ativo = 1 ")
            .append("	and mbt.Ativo = 1 ");
        
        Query query2 = em.createNativeQuery(sql.toString());

        return query2.getResultList();
    }
}