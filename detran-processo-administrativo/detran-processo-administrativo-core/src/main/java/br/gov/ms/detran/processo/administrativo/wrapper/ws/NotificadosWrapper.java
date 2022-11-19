package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class NotificadosWrapper {
    
    @XmlElement(name = "numeroprocessoadministrativo", nillable = true)
    String numeroProcessoAdministrativo;
    
    @XmlElement(name = "numeronotificacao", nillable = true)
    String numeroNotificacao;
    
    @XmlElement(name = "objetocorreio", nillable = true)
    String objetoCorreio;
    
    @XmlElement(name = "erro", nillable = true)
    private String erro;
    
    @XmlElement(name = "tipo", nillable = true)
    private TipoFasePaEnum tipo;
    
    @XmlElement(name = "datafim", nillable = true)
     @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    private Date dataFim;

    @XmlElement(name = "datanotificacao", nillable = true)
     @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    private Date dataNotificacao;

    @XmlElement(name = "lote", nillable = true)
    private String lote;
    
    public NotificadosWrapper() {
        
    }
    
    public NotificadosWrapper(String numeroProcesso, String numeroNotificacao, String objetoCorreio, TipoFasePaEnum tipo) {
        this.numeroProcessoAdministrativo = numeroProcesso;
        this.numeroNotificacao = numeroNotificacao;
        this.objetoCorreio = objetoCorreio;
        this.tipo = tipo;
    }
    public NotificadosWrapper(String numeroProcesso, String numeroNotificacao, String objetoCorreio, TipoFasePaEnum tipo, Date dataFim, String lote, Date dataNotificacao) {
        this(numeroProcesso, numeroNotificacao, objetoCorreio, tipo);
        this.dataFim = dataFim;
        this.lote = lote;
        this.dataNotificacao = dataNotificacao;
    }

    public NotificadosWrapper(String numeroProcessoAdministrativo, String numeroNotificacao, TipoFasePaEnum tipo, Date dataFim) {
        this.numeroProcessoAdministrativo = numeroProcessoAdministrativo;
        this.numeroNotificacao = numeroNotificacao;
        this.tipo = tipo;
        this.dataFim = dataFim;
    }
    

    public String getNumeroProcessoAdministrativo() {
        return numeroProcessoAdministrativo;
    }

    public void setNumeroProcessoAdministrativo(String numeroProcessoAdministrativo) {
        this.numeroProcessoAdministrativo = numeroProcessoAdministrativo;
    }

    public String getNumeroNotificacao() {
        return numeroNotificacao;
    }

    public void setNumeroNotificacao(String numeroNotificacao) {
        this.numeroNotificacao = numeroNotificacao;
    }

    public String getObjetoCorreio() {
        return objetoCorreio;
    }

    public void setObjetoCorreio(String objetoCorreio) {
        this.objetoCorreio = objetoCorreio;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public TipoFasePaEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoFasePaEnum tipo) {
        this.tipo = tipo;
    }

    public Date getDataFim() {
        return dataFim;
    }
    
    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Date getDataNotificacao() {
        return dataNotificacao;
    }

    public void setDataNotificacao(Date dataNotificaco) {
        this.dataNotificacao = dataNotificaco;
    }
}