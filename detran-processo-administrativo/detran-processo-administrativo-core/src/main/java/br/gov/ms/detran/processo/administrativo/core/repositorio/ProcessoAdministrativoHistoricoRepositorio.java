package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ConsultaProcessoAdministrativoHistorico;
import java.util.List;
import javax.persistence.EntityManager;

public class ProcessoAdministrativoHistoricoRepositorio extends AbstractJpaDAORepository<ConsultaProcessoAdministrativoHistorico> {

    public ProcessoAdministrativoHistoricoRepositorio() {
    }

    public List<ConsultaProcessoAdministrativoHistorico> getProcessoAdministrativoHistorico(
            EntityManager em,
            Long id,
            Integer from,
            Integer to) throws DatabaseException {

        return super.getListNativeQuery(em, ConsultaProcessoAdministrativoHistorico.class,
                "Select  "
                + "tde.Tde_His_ID as ID, "
                + "tcb.TCB_CODIGO as TCB_CODIGO,  "
                + "tcb.TCB_DESCRICAO as TCB_DESCRICAO, "
                + "tde.TDE_SITUACAO as TDE_SITUACAO,  "
                + "tde.TDE_DATA_INICIO as TDE_DATA_INICIO,  "
                + "tde.TDE_DATA_TERMINO as TDE_DATA_TERMINO, "
                + "tck.TCK_ID as TCK_ID,  "
                + "tck.TCK_DESCRICAO as TCK_DESCRICAO,  "
                + "tcv.TCV_ID as TCV_ID,  "
                + "tcv.TCV_DESCRICAO as TCV_DESCRICAO  "
                + "from dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM_HIS tde "
                + "inner join dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc on tde.Tde_Processo_Administrativo = tdc.Tdc_ID "
                + "INNER JOIN dbo.TB_TCD_PAD_STATUS_ANDAMENTO tcd  on tde.Tde_Status_Andamento = tcd.Tcd_ID "
                + "INNER join dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb on tcd.Tcd_Andamento_Processo = tcb.Tcb_ID "
                + "inner join dbo.TB_TCH_PAD_FLUXO_FASE tch on tch.Tch_Andamento = tcb.Tcb_ID  "
                + "inner join dbo.TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO tci on tci.Tci_ID = tch.Tch_Prioridade_Fluxo_Amparo  "
                + "inner join dbo.TB_TCV_FASE_PROCESSO_ADM tcv on tcv.Tcv_ID = tci.Tci_Fase_Processo_Adm "
                + "inner join dbo.TB_TCK_PAD_FLUXO_PROCESSO tck on tck.Tck_ID = tde.Tde_Fluxo_Processo  "
                + "inner join dbo.TB_TCL_PAD_FLUXO_ORIGEM tcl on tcl.Tcl_Fluxo_Processo = tck.Tck_ID  "
                + "inner join dbo.TB_TDH_PAD_APOIO_ORIGEM_INSTAURACAO tdh on tdh.TDH_ID  = tcl.TCL_ORIGEM_INSTAURACAO  "
                + "where tdc.Tdc_ID = :p0 "
                + "and tdh.TDH_RES_TIPO_PROCESSO  = tdc.TDC_TIPO_PROCESSO  "
                + "and tdc.TDC_APOIO_ORIGEM_INSTAURACAO  = tdh.TDH_ID  "
                + "and tci.TCI_FLUXO_PROCESSO  = tde.TDE_FLUXO_PROCESSO  "
                + "order by tde.TDE_HIS_ID DESC ",
                from, to,
                new Object[]{id});
    }

    public Long contarProcessoAdministrativoHistorico(
            EntityManager em,
            Long id) throws DatabaseException {

        return super.getCountEntitySearch(em,
                "Select  "
                + "count(tde.Tde_His_ID) as contagem "
                + "from dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM_HIS tde "
                + "inner join dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc on tde.Tde_Processo_Administrativo = tdc.Tdc_ID "
                + "INNER JOIN dbo.TB_TCD_PAD_STATUS_ANDAMENTO tcd  on tde.Tde_Status_Andamento = tcd.Tcd_ID "
                + "INNER join dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb on tcd.Tcd_Andamento_Processo = tcb.Tcb_ID "
                + "inner join dbo.TB_TCH_PAD_FLUXO_FASE tch on tch.Tch_Andamento = tcb.Tcb_ID  "
                + "inner join dbo.TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO tci on tci.Tci_ID = tch.Tch_Prioridade_Fluxo_Amparo  "
                + "inner join dbo.TB_TCV_FASE_PROCESSO_ADM tcv on tcv.Tcv_ID = tci.Tci_Fase_Processo_Adm "
                + "inner join dbo.TB_TCK_PAD_FLUXO_PROCESSO tck on tck.Tck_ID = tde.Tde_Fluxo_Processo  "
                + "inner join dbo.TB_TCL_PAD_FLUXO_ORIGEM tcl on tcl.Tcl_Fluxo_Processo = tck.Tck_ID  "
                + "inner join dbo.TB_TDH_PAD_APOIO_ORIGEM_INSTAURACAO tdh on tdh.TDH_ID  = tcl.TCL_ORIGEM_INSTAURACAO  "
                + "where tdc.Tdc_ID = :p0 "
                + "and tdh.TDH_RES_TIPO_PROCESSO  = tdc.TDC_TIPO_PROCESSO  "
                + "and tdc.TDC_APOIO_ORIGEM_INSTAURACAO  = tdh.TDH_ID  "
                + "and tci.TCI_FLUXO_PROCESSO  = tde.TDE_FLUXO_PROCESSO  ",
                new Object[]{id});
    }
}
