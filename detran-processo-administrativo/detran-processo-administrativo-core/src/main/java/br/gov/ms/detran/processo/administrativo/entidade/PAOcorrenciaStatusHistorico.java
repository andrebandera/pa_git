package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.comum.util.adapter.TimestampAdapter;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM_HIS")
@NamedQueries({
    @NamedQuery(
        name = "PAOcorrenciaStatusHistorico.getUltimoHistoricoDiferentePAStatusAndamentoAtual",
        query = "SELECT tde "
                + " FROM PAOcorrenciaStatusHistorico tde "
                + " WHERE tde.idProcessoAdministrativo = :p0 "
                + "     AND tde.idStatusAndamento <> :p1 "
                + " ORDER BY tde.id DESC"
    ),
    @NamedQuery(
        name = "PAOcorrenciaStatusHistorico.getUltimoHistorico",
        query = "SELECT tde FROM PAOcorrenciaStatusHistorico tde WHERE tde.idProcessoAdministrativo = :p0 ORDER BY tde.id DESC"
    )
})
public class PAOcorrenciaStatusHistorico extends BaseEntityAtivo implements Serializable {

    @Id
    @Column(name = "Tde_His_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "Tde_ID")
    private Long idPAOcorrenciaStatus;

    @Column(name = "Tde_Processo_Administrativo")
    private Long idProcessoAdministrativo;

    @Column(name = "Tde_Status_Andamento")
    private Long idStatusAndamento;
    
    @Column(name = "Tde_Data_Inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInicio;

    @Column(name = "Tde_Data_Termino")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataTermino;
    
    @Column(name = "Tde_Situacao")
    @Enumerated(EnumType.STRING)
    private SituacaoOcorrenciaEnum situacao;
    
    @Column(name = "Tde_Fluxo_Processo")
    private Long idFluxoProcesso;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public PAOcorrenciaStatusHistorico() {
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

    public Date getDataInicio() {
        return dataInicio;
    }

    @XmlElement(name = "dataInicio")
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }
    
    @XmlElement(name = "dataTermino")
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public SituacaoOcorrenciaEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoOcorrenciaEnum situacao) {
        this.situacao = situacao;
    }

    public Long getIdPAOcorrenciaStatus() {
        return idPAOcorrenciaStatus;
    }

    public void setIdPAOcorrenciaStatus(Long idPAOcorrenciaStatus) {
        this.idPAOcorrenciaStatus = idPAOcorrenciaStatus;
    }

    public Long getIdProcessoAdministrativo() {
        return idProcessoAdministrativo;
    }

    public void setIdProcessoAdministrativo(Long idProcessoAdministrativo) {
        this.idProcessoAdministrativo = idProcessoAdministrativo;
    }

    public Long getIdStatusAndamento() {
        return idStatusAndamento;
    }

    public void setIdStatusAndamento(Long idStatusAndamento) {
        this.idStatusAndamento = idStatusAndamento;
    }

    public Long getIdFluxoProcesso() {
        return idFluxoProcesso;
    }

    public void setIdFluxoProcesso(Long idFluxoProcesso) {
        this.idFluxoProcesso = idFluxoProcesso;
    }
}