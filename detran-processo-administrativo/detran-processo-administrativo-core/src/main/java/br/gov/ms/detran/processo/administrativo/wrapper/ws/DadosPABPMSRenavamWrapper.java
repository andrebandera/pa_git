package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author yanko.campos
 */
public class DadosPABPMSRenavamWrapper {
    
    @XmlElement(name = "tiporegistro")
    private final Integer tipoRegistro = TipoRegistroBPMSEnum.LOG_TRANSACAO_RENAVAM.getCodigo();
    
    @XmlElement(name = "codigotransacao", nillable = true)
    private String codigoTransacao;
    
    @XmlElement(name = "descricaotransacao", nillable = true)
    private String descricaoTransacao;
    
    @XmlElement(name = "retornotransacao", nillable = true)
    private String retornoTransacao;
    
    @XmlElement(name = "programanatural", nillable = true)
    private String programaNatural;
    
    @XmlElement(name = "datatransacao", nillable = true)
    private Date dataTransacao;

    @XmlElement(name = "descricaoretorno", nillable = true)
    private String descricaoRetorno;

    public DadosPABPMSRenavamWrapper() {
    }
    
    public DadosPABPMSRenavamWrapper(String codigoTransacao, 
                                   String descricaoTransacao, 
                                   String retornoTransacao, 
                                   String programaNatural, 
                                   Date dataTransacao, 
                                   String descricaoRetorno) {
        
        this.codigoTransacao = codigoTransacao;
        this.descricaoTransacao = descricaoTransacao;
        this.retornoTransacao = retornoTransacao;
        this.programaNatural = programaNatural;
        this.dataTransacao = dataTransacao;
        this.descricaoRetorno = descricaoRetorno;
    }

    public String getCodigoTransacao() {
        return codigoTransacao;
    }

    public void setCodigoTransacao(String codigoTransacao) {
        this.codigoTransacao = codigoTransacao;
    }

    public String getDescricaoTransacao() {
        return descricaoTransacao;
    }

    public void setDescricaoTransacao(String descricaoTransacao) {
        this.descricaoTransacao = descricaoTransacao;
    }

    public String getRetornoTransacao() {
        return retornoTransacao;
    }

    public void setRetornoTransacao(String retornoTransacao) {
        this.retornoTransacao = retornoTransacao;
    }

    public String getProgramaNatural() {
        return programaNatural;
    }

    public void setProgramaNatural(String programaNatural) {
        this.programaNatural = programaNatural;
    }

    public Date getDataTransacao() {
        return dataTransacao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataTransacao(Date dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public String getDescricaoRetorno() {
        return descricaoRetorno;
    }

    public void setDescricaoRetorno(String descricaoRetorno) {
        this.descricaoRetorno = descricaoRetorno;
    }

    public Integer getTipoRegistro() {
        return tipoRegistro;
    }

    public static void main(String ...args) {
        
        DadosPABPMSRenavamWrapper wrapper = new DadosPABPMSRenavamWrapper();
        
        wrapper.setCodigoTransacao("00");
        wrapper.setDescricaoTransacao("ZEROZERO");
        wrapper.setRetornoTransacao("RETORNOTRANSACAO");
        wrapper.setProgramaNatural("AEMNPP99");
        wrapper.setDataTransacao(new Date());
        wrapper.setDescricaoRetorno("DESCRICAORETORNO");
        
        System.out.println(DetranStringUtil.getInstance().toJson(wrapper));
    }
}