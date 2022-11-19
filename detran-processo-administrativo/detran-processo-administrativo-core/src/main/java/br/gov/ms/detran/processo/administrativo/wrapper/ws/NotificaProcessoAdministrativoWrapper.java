package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class NotificaProcessoAdministrativoWrapper implements IBaseEntity{

    private String numeroProcesso;

    private TipoFasePaEnum tipo;

    private Date dataPublicacaoPortaria;

    private String numeroPortaria;

    private Integer situacaoRetornoAR;

    private MovimentoCnh movimento;

    public NotificaProcessoAdministrativoWrapper(String numeroProcesso, TipoFasePaEnum tipoFasePaEnum) {
        this.numeroProcesso = numeroProcesso;
        this.tipo = tipoFasePaEnum;
    }

    public NotificaProcessoAdministrativoWrapper() {
    }
    
    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public TipoFasePaEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoFasePaEnum tipo) {
        this.tipo = tipo;
    }

    @Override
    public Serializable getId() {
        return numeroProcesso;
    }

    public Date getDataPublicacaoPortaria() {
        return dataPublicacaoPortaria;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataPublicacaoPortaria(Date dataPublicacaoPortaria) {
        this.dataPublicacaoPortaria = dataPublicacaoPortaria;
    }

    public String getNumeroPortaria() {
        return numeroPortaria;
    }

    public void setNumeroPortaria(String numeroPortaria) {
        this.numeroPortaria = numeroPortaria;
    }

    public Integer getSituacaoRetornoAR() {
        return situacaoRetornoAR;
    }

    public void setSituacaoRetornoAR(Integer situacaoRetornoAR) {
        this.situacaoRetornoAR = situacaoRetornoAR;
    }

    public MovimentoCnh getMovimento() {
        return movimento;
    }

    public void setMovimento(MovimentoCnh movimento) {
        this.movimento = movimento;
    }
}