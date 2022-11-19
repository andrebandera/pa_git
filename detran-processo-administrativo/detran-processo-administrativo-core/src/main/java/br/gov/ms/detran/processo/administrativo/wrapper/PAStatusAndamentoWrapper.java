/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos
 */
@XmlRootElement
@EntityMapping2(entity = {PAStatusAndamento.class})
public class PAStatusAndamentoWrapper implements IBaseEntity, IEntityResource<PAStatusAndamento> {

    private PAStatusAndamento entidade;
    
    public PAStatusAndamentoWrapper(){}
    
    public PAStatusAndamentoWrapper(PAStatusAndamento entidade){
        this.entidade=entidade;
    }
    
    public void setId(Long id) {
        if (this.entidade == null) this.entidade = new PAStatusAndamento();
        this.entidade.setId(id);
    }
    
    @Override
    public Serializable getId() {
        return (this.entidade != null ? this.entidade.getId() : null);
    }

    @Override
    public PAStatusAndamento getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(PAStatusAndamento entidade) {
        this.entidade = entidade;
    }
    
}
