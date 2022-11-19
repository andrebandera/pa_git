package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class DadosPABPMSDadosPontuacaoWrapper {
    
    @XmlElement(name = "tiporegistro", nillable = true)
    private final Integer tipoRegistro = TipoRegistroBPMSEnum.HISTORICO_PONTUACAO.getCodigo();
            
    @XmlElement(name = "numeroautoinfracao", nillable = true)
    private String numeroAutoInfracao;
    
    @XmlElement(name = "codigoinfracao", nillable = true)
    private String codigoInfracao;
    
    @XmlElement(name = "datainfracao", nillable = true)
    private Date dataInfracao;
    
    @XmlElement(name = "numeropontos", nillable = true)
    private String numeroPontos;
    
    @XmlElement(name = "nomecondutor", nillable = true)
    private String nomeCondutor;
    
    @XmlElement(name = "situacaopontuacao", nillable = true)
    private String situacaoPontuacao;
    
    @XmlElement(name = "descricaosituacao", nillable = true)
    private String descricaoSituacao;

    public DadosPABPMSDadosPontuacaoWrapper() {
    }
    
    public Integer getTipoRegistro() {
        return tipoRegistro;
    }

    public String getNumeroAutoInfracao() {
        return numeroAutoInfracao;
    }

    public void setNumeroAutoInfracao(String numeroAutoInfracao) {
        this.numeroAutoInfracao = numeroAutoInfracao;
    }

    public String getCodigoInfracao() {
        return codigoInfracao;
    }

    public void setCodigoInfracao(String codigoInfracao) {
        this.codigoInfracao = codigoInfracao;
    }

    public Date getDataInfracao() {
        return dataInfracao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataInfracao(Date dataInfracao) {
        this.dataInfracao = dataInfracao;
    }

    public String getNumeroPontos() {
        return numeroPontos;
    }

    public void setNumeroPontos(String numeroPontos) {
        this.numeroPontos = numeroPontos;
    }

    public String getNomeCondutor() {
        return nomeCondutor;
    }

    public void setNomeCondutor(String nomeCondutor) {
        this.nomeCondutor = nomeCondutor;
    }

    public String getSituacaoPontuacao() {
        return situacaoPontuacao;
    }

    public void setSituacaoPontuacao(String situacaoPontuacao) {
        this.situacaoPontuacao = situacaoPontuacao;
    }

    public String getDescricaoSituacao() {
        return descricaoSituacao;
    }

    public void setDescricaoSituacao(String descricaoSituacao) {
        this.descricaoSituacao = descricaoSituacao;
    }
}