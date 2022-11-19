/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.PAServicoExterno;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author desenvolvimento
 */
@XmlRootElement
@EntityMapping2(entity = {PAServicoExterno.class})
public class PAServicoExternoWrapper implements IBaseEntity, IEntityResource<PAServicoExterno> {

    @XmlElement(nillable = false)
    private PAServicoExterno entidade;

    public PAServicoExternoWrapper() {
    }

    @Override
    public PAServicoExterno getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(PAServicoExterno entidade) {
        this.entidade = entidade;
    }

    @Override
    public Serializable getId() {
        return (this.entidade != null ? this.entidade.getId() : null);
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {

        if (this.entidade == null) {
            this.entidade = new PAServicoExterno();
        }

        this.entidade.setId(id);
    }
}
