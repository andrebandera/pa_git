package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import java.io.Serializable;

public class ControleCnhPAWrapper implements IBaseEntity {
    
    private Long idProtocolo;
    
    byte[] byteArquivo;

    public Long getIdProtocolo() {
        return idProtocolo;
    }

    public void setIdProtocolo(Long idProtocolo) {
        this.idProtocolo = idProtocolo;
    }

    public byte[] getByteArquivo() {
        return byteArquivo;
    }

    public void setByteArquivo(byte[] byteArquivo) {
        this.byteArquivo = byteArquivo;
    }
    
    @Override
    public Serializable getId() {
        return null;
    }
}