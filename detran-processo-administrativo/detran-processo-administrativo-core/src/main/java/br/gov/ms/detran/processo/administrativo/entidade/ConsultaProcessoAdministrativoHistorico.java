package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.util.adapter.DataHoraAdapter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
public class ConsultaProcessoAdministrativoHistorico implements IBaseEntity, Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "TCB_CODIGO")
    private Long codigoAndamento;
    
    @Column(name = "TCB_DESCRICAO")
    private String andamento;
    
    @Column(name = "TDE_SITUACAO")
    private String situacao;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TDE_DATA_INICIO")
    private Date dataInicio;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TDE_DATA_TERMINO")
    private Date dataTermino;
    
    @Column(name = "TCK_DESCRICAO")
    private String fluxo;
    
    @Column(name = "TCV_DESCRICAO")
    private String fase;

    public Long getId() {
        return id;
    }

    public Long getCodigoAndamento() {
        return codigoAndamento;
    }

    public void setCodigoAndamento(Long codigoAndamento) {
        this.codigoAndamento = codigoAndamento;
    }

    public String getAndamento() {
        return andamento;
    }

    public void setAndamento(String andamento) {
        this.andamento = andamento;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    @XmlElement(name = "dataInicio")
    @XmlJavaTypeAdapter(DataHoraAdapter.class)
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }

    @XmlElement(name = "dataTermino")
    @XmlJavaTypeAdapter(DataHoraAdapter.class)
    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    public String getFluxo() {
        return fluxo;
    }

    public void setFluxo(String fluxo) {
        this.fluxo = fluxo;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }
}
