package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ArquivoPA;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import javax.persistence.EntityManager;

public class ArquivoPARepositorio extends AbstractJpaDAORepository<ArquivoPA> {

    public byte[] getByteArquivoProtocoloRecursoOnline(EntityManager em, String numeroProcesso) throws DatabaseException {

        StringBuilder sql = new StringBuilder();
        sql.append(" Select top 1 tct.Tct_Byte as Tct_Byte   ");
        sql.append(" from dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc  ");
        sql.append(" inner join dbo.TB_TDP_PAD_RECURSO tdp on tdp.TDP_PROCESSO_ADMINISTRATIVO = tdc.TDC_ID   ");
        sql.append(" inner join dbo.TB_TDM_PAD_PROTOCOLO tdm on tdm.TDM_NUMERO_PROCESSO  = tdc.TDC_ID   ");
        sql.append(" inner join dbo.TB_TEM_RECURSO_PA_ONLINE tem on tem.TEM_RECURSO  = tdp.TDP_ID   ");
        sql.append(" inner join dbo.TB_TCT_ARQUIVO_PA tct on tdm.TDM_ARQUIVO_PA  = tct.TCT_ID   ");
        sql.append(" where tdc.TDC_NUMERO_PROCESSO = :p0   ");
        sql.append(" and tem.TEM_TIPO = TDP.TDP_TIPO   ");
        sql.append(" and tdp.TDP_SITUACAO  = :p1 ");
       
        Object result = super.getNativeQuery(
                em,
                sql.toString(),
                new Object[]{numeroProcesso, SituacaoRecursoEnum.EM_ANALISE.name()});

        return result != null ? (byte[]) result : null;
    }
}
