package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import java.io.Serializable;

public class RelProcessosPrescritosWrapper implements IBaseEntity {
    
    byte[] byteArquivo;

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