package br.gov.ms.detran.processo.administrativo.entidade;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

@Entity
@NamedNativeQueries({
    @NamedNativeQuery(
            name = "AndamentoProcessoAdministrativoConsulta.getProximoAndamento",
            query = "SELECT "
            + "	TOP(1)  "
            + "                  Tcb1.Tcb_Codigo AS CODIGO_ANDAMENTO_PROCESSO,  "
            + "                  Tcb1.Tcb_Descricao AS DESCRICAO_ANDAMENTO_PROCESSO,  "
            + "                  tcv.Tcv_Codigo AS CODIGO_FASE,  "
            + "                  tcv.Tcv_Descricao AS DESCRICAO_FASE,  "
            + "                  tci.Tci_Prioridade AS PRIORIDADE_FLUXO_AMPARO,  "
            + "                  tch.Tch_Prioridade AS PRIORIDADE_FLUXO_FASE,  "
            + "                  Tde.Tde_Status_Andamento AS STATUS_PROCESSO,  "
            + "                  LEAD(Tcb1.Tcb_Codigo, 1,0) OVER (ORDER BY tci.Tci_Prioridade,tcv.Tcv_Codigo, tch.Tch_Prioridade, Tcb1.Tcb_Codigo) AS ANDAMENTO_PROCESSO_CODIGO_PROXIMO  "
            + "                  FROM dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde  "
            + "                  INNER JOIN dbo.TB_TCD_PAD_STATUS_ANDAMENTO tcd on tde.Tde_Status_Andamento = tcd.Tcd_ID AND tcd.ativo = :p1 "
            + "                  INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb on tcd.Tcd_Andamento_Processo = tcb.Tcb_ID AND tcb.ativo = :p1 "
            + "                  INNER JOIN dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc on tde.Tde_Processo_Administrativo = tdc.Tdc_ID AND tdc.ativo = :p1 "
            + "                  INNER JOIN dbo.TB_TCK_PAD_FLUXO_PROCESSO tck on tde.Tde_Fluxo_Processo = tck.Tck_ID AND tck.ativo = :p1 "
            + "                  INNER JOIN dbo.TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO tci on tck.Tck_ID = tci.Tci_Fluxo_Processo AND tci.ativo = :p1 "
            + "                  INNER JOIN dbo.TB_TCV_FASE_PROCESSO_ADM tcv on tcv.Tcv_ID = tci.Tci_Fase_Processo_Adm AND tcv.ativo = :p1 "
            + "                  INNER JOIN dbo.TB_TCH_PAD_FLUXO_FASE tch on tci.Tci_ID = tch.Tch_Prioridade_Fluxo_Amparo AND tch.ativo = :p1 "
            + "                  INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb1 on tch.Tch_Andamento = tcb1.Tcb_ID AND tcb1.ativo = :p1 "
            + "                  WHERE tde.ativo = :p1 "
            + "                     AND tdc.Tdc_ID = :p0 "
            + "                     AND ( "
            + "				  tci.Tci_Prioridade > (SELECT min(c1.Tci_Prioridade) "
            + "                                               FROM dbo.TB_TCH_PAD_FLUXO_FASE a1  "
            + "                                                 INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO b1 ON a1.Tch_Andamento = b1.Tcb_ID AND b1.ativo = :p1 "
            + "                                                 INNER JOIN dbo.TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO c1 ON c1.Tci_ID = a1.Tch_Prioridade_Fluxo_Amparo AND c1.ativo = :p1 "
            + "                                                 INNER JOIN dbo.TB_TCV_FASE_PROCESSO_ADM d1 ON d1.Tcv_ID = c1.Tci_Fase_Processo_Adm AND d1.ativo = :p1 "
            + "                                               WHERE b1.Tcb_ID = tcb.Tcb_ID AND a1.ativo = :p1 and c1.Tci_Fluxo_Processo = tck.Tck_ID ) "
            + "               OR"
            + "               (tci.Tci_Prioridade = (SELECT min(c1.Tci_Prioridade) "
            + "                                               FROM dbo.TB_TCH_PAD_FLUXO_FASE a1  "
            + "                                               INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO b1 ON a1.Tch_Andamento = b1.Tcb_ID AND b1.ativo = :p1 "
            + "                                               INNER JOIN dbo.TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO c1 ON c1.Tci_ID = a1.Tch_Prioridade_Fluxo_Amparo AND c1.ativo = :p1 "
            + "                                               INNER JOIN dbo.TB_TCV_FASE_PROCESSO_ADM d1 ON d1.Tcv_ID = c1.Tci_Fase_Processo_Adm AND d1.ativo = :p1 "
            + "                                                   WHERE b1.Tcb_ID = tcb.Tcb_ID AND a1.ativo = :p1 and c1.Tci_Fluxo_Processo = tck.Tck_ID) and "
            + "                  tch.Tch_Prioridade > (SELECT min(a.Tch_Prioridade) "
            + "                                               FROM dbo.TB_TCH_PAD_FLUXO_FASE a  "
            + "                                               INNER JOIN dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO b ON a.Tch_Andamento = b.Tcb_ID AND b.ativo = :p1 "
            + "                                               INNER JOIN dbo.TB_TCI_PAD_PRIORIDADE_FLUXO_AMPARO c1 ON c1.Tci_ID = a.Tch_Prioridade_Fluxo_Amparo AND c1.ativo = 1 "
            + "                                               WHERE b.Tcb_ID = tcb.Tcb_ID AND a.ativo = :p1 and c1.Tci_Fluxo_Processo = tck.Tck_ID)))"
            + "                  ORDER BY tci.Tci_Prioridade, tcv.Tcv_Codigo, tch.Tch_Prioridade ",
            resultClass = AndamentoProcessoAdministrativoConsulta.class)
})
public class AndamentoProcessoAdministrativoConsulta implements Serializable {

    @Id
    @Column(name = "CODIGO_ANDAMENTO_PROCESSO")
    private Long codigoAndamentoProcesso;

    @Column(name = "DESCRICAO_ANDAMENTO_PROCESSO")
    private String descricaoAndamentoProcesso;

    @Column(name = "CODIGO_FASE")
    private Integer codigoFase;

    @Column(name = "DESCRICAO_FASE")
    private String descricaoFrase;

    @Column(name = "PRIORIDADE_FLUXO_AMPARO")
    private Integer prioridadeFluxoAmparo;

    @Column(name = "PRIORIDADE_FLUXO_FASE")
    private Integer prioridadeFluxoFase;

    @Column(name = "STATUS_PROCESSO")
    private Integer statusProcesso;

    @Column(name = "ANDAMENTO_PROCESSO_CODIGO_PROXIMO")
    private Integer codigoProximoAndamentoProcesso;

//    public Long getId() {
//        return id;
//    }
//
//    @XmlElement(name = "id")
//    @XmlJavaTypeAdapter(LongAdapter.class)
//    public void setId(Serializable id) {
//        this.id = (id != null ? Long.valueOf(id.toString()) : null);
//    }

    public Long getCodigoAndamentoProcesso() {
        return codigoAndamentoProcesso;
    }

    public void setCodigoAndamentoProcesso(Long codigoAndamentoProcesso) {
        this.codigoAndamentoProcesso = codigoAndamentoProcesso;
    }

    public String getDescricaoAndamentoProcesso() {
        return descricaoAndamentoProcesso;
    }

    public void setDescricaoAndamentoProcesso(String descricaoAndamentoProcesso) {
        this.descricaoAndamentoProcesso = descricaoAndamentoProcesso;
    }

    public Integer getCodigoFase() {
        return codigoFase;
    }

    public void setCodigoFase(Integer codigoFase) {
        this.codigoFase = codigoFase;
    }

    public String getDescricaoFrase() {
        return descricaoFrase;
    }

    public void setDescricaoFrase(String descricaoFrase) {
        this.descricaoFrase = descricaoFrase;
    }

    public Integer getPrioridadeFluxoAmparo() {
        return prioridadeFluxoAmparo;
    }

    public void setPrioridadeFluxoAmparo(Integer prioridadeFluxoAmparo) {
        this.prioridadeFluxoAmparo = prioridadeFluxoAmparo;
    }

    public Integer getPrioridadeFluxoFase() {
        return prioridadeFluxoFase;
    }

    public void setPrioridadeFluxoFase(Integer prioridadeFluxoFase) {
        this.prioridadeFluxoFase = prioridadeFluxoFase;
    }

    public Integer getStatusProcesso() {
        return statusProcesso;
    }

    public void setStatusProcesso(Integer statusProcesso) {
        this.statusProcesso = statusProcesso;
    }

    public Integer getCodigoProximoAndamentoProcesso() {
        return codigoProximoAndamentoProcesso;
    }

    public void setCodigoProximoAndamentoProcesso(Integer codigoProximoAndamentoProcesso) {
        this.codigoProximoAndamentoProcesso = codigoProximoAndamentoProcesso;
    }
}
