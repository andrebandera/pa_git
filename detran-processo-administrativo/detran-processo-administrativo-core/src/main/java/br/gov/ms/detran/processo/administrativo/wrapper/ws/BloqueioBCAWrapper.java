package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoBloqueioBCAEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class BloqueioBCAWrapper {
    
    @XmlElement(name = "numeroprocesso", nillable = true)
    String numeroProcesso;
    
    @XmlElement(name = "situacao", nillable = true)
    SituacaoBloqueioBCAEnum situacao;
    
    @XmlElement(name = "datainicio", nillable = true)
    Date dataInicio;
    
    @XmlElement(name = "datafim", nillable = true)
    Date dataFim;
    
    @XmlElement(name = "prazopenalidade", nillable = true)
    String prazoPenalidade;
    
    @XmlElement(name = "motivobloqueio", nillable = true)
    String motivoBloqueio;

    public BloqueioBCAWrapper() {
    }

    public BloqueioBCAWrapper(
        String numeroProcesso, SituacaoBloqueioBCAEnum situacao, Date dataInicio, 
        Date dataFim, String prazoPenalidade, String motivoBloqueio) {
        
        this.numeroProcesso     = numeroProcesso;
        this.situacao           = situacao;
        this.dataInicio         = dataInicio;
        this.dataFim            = dataFim;
        this.prazoPenalidade    = prazoPenalidade;
        this.motivoBloqueio     = motivoBloqueio;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcessoAdministrativo) {
        this.numeroProcesso = numeroProcessoAdministrativo;
    }

    public SituacaoBloqueioBCAEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoBloqueioBCAEnum situacao) {
        this.situacao = situacao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }
    
    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }
    
    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getPrazoPenalidade() {
        return prazoPenalidade;
    }

    public void setPrazoPenalidade(String prazoPenalidade) {
        this.prazoPenalidade = prazoPenalidade;
    }

    public String getMotivoBloqueio() {
        return motivoBloqueio;
    }

    public void setMotivoBloqueio(String motivoBloqueio) {
        this.motivoBloqueio = motivoBloqueio;
    }
}