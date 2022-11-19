/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoCorpoTexto;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.processo.administrativo.entidade.PATipoCorpoAndamento;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos Eduardo
 */
@XmlRootElement
@EntityMapping2(entity = {PATipoCorpoAndamento.class})
public class PATipoCorpoAndamentoWrapper implements IBaseEntity, IEntityResource<PATipoCorpoAndamento>{
    
    private PATipoCorpoAndamento entidade;
    private TipoCorpoTexto tipoCorpoTexto;
    
    public PATipoCorpoAndamentoWrapper(){}
    
    public PATipoCorpoAndamentoWrapper(PATipoCorpoAndamento entidade){
        this.entidade = entidade;
    }
    
    public void setId(Long id) {
        if (this.entidade == null) this.entidade = new PATipoCorpoAndamento();
        this.entidade.setId(id);
    }
    
    @Override
    public Serializable getId() {
        return (this.entidade != null ? this.entidade.getId() : null);
    }

    @Override
    public PATipoCorpoAndamento getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(PATipoCorpoAndamento entidade) {
        this.entidade = entidade;
    }

    public TipoCorpoTexto getTipoCorpoTexto() {
        return tipoCorpoTexto;
    }

    public void setTipoCorpoTexto(TipoCorpoTexto tipoCorpoTexto) {
        this.tipoCorpoTexto = tipoCorpoTexto;
    }
}
