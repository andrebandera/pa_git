package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.comum.util.adapter.TimestampAdapter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TDG_PAD_PROCESSO_ADMINISTRATIVO_JSON")
@NamedQueries({
    @NamedQuery(
        name = "ProcessoAdministrativoJson.getPAsInstauradosDiario",
        query = "SELECT tdg "
                + " FROM ProcessoAdministrativoJson tdg "
                + " WHERE CONVERT(Date, tdg.dataInicio) = CONVERT(Date, :p0)"
                + "     AND tdg.ativo = :p1 "
    ),
    @NamedQuery(
        name = "ProcessoAdministrativoJson.getPAsInstauradosPendentesEnvioBPMS",
        query = "SELECT tdg "
                + "FROM ProcessoAdministrativoJson tdg "
                + " INNER JOIN tdg.processoAdministrativo tdc "
                + "WHERE  tdg.ativo = :p2 "
                + " AND tdc.ativo = :p2 "
                + " AND EXISTS ("
                + "     SELECT 1 "
                + "     FROM PAOcorrenciaStatus tce "
                + "         INNER JOIN tce.processoAdministrativo pa "
                + "         INNER JOIN tce.statusAndamento tcd "
                + "         INNER JOIN tcd.andamentoProcesso tcb "
                + "         INNER JOIN tcd.status tca "
                + "     WHERE tdc.id = pa.id "
                + "         AND tce.ativo = :p2 "
                + "         AND tca.codigo = :p0 "
                + "         AND tcb.codigo = :p1) "
    ),
    @NamedQuery(
        name = "ProcessoAdministrativoJson.getProcessoAdministrativoJsonPorProcessoAdministrativoAtivo",
        query = "SELECT paj "
                + "FROM ProcessoAdministrativoJson paj "
                + "WHERE paj.processoAdministrativo.id = :p0 "
                + "AND paj.ativo = :p1 "),
    @NamedQuery(
        name = "ProcessoAdministrativoJson.all",
        query = "SELECT tdg FROM ProcessoAdministrativoJson tdg ")
})
public class ProcessoAdministrativoJson extends BaseEntityAtivo implements Serializable  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdg_ID")
    private Long id;

    @Column(name = "Tdg_Situacao")
    @Enumerated(EnumType.STRING)
    private PASituacaoJsonEnum situacao;
    
    @Column(name = "Tdg_Json")
    private String json;
    
    @Column(name = "Tdg_Data_Inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInicio;
    
    @Column(name = "Tdg_Data_Fim")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataFim;
    
    @ManyToOne
    @JoinColumn(name = "Tdg_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public ProcessoAdministrativoJson() {
    }

    public ProcessoAdministrativoJson(String json) {
        this.json = json;
    }

    public ProcessoAdministrativoJson(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public void setId(Serializable id) {
        this.id = (id != null ? Long.valueOf(id.toString()) : null);
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public PASituacaoJsonEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(PASituacaoJsonEnum situacao) {
        this.situacao = situacao;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Date getDataInicio() {
        return dataInicio;
    }
    
    @XmlElement(name = "dataInicio")
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    @XmlElement(name = "dataFim")
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }
}