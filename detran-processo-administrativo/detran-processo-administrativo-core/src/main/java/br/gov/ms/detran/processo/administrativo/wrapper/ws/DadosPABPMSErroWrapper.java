package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author yanko.campos
 */
public class DadosPABPMSErroWrapper {
    
    @XmlElement(name = "tiporegistro", nillable = true)
    private final Integer tipoRegistro = TipoRegistroBPMSEnum.ERRO.getCodigo();
    
    @XmlElement(name = "mensagemerro", nillable = true)
    private String mensagemErro;

    public DadosPABPMSErroWrapper() {
    }
    
    public DadosPABPMSErroWrapper(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    public Integer getTipoRegistro() {
        return tipoRegistro;
    }
}