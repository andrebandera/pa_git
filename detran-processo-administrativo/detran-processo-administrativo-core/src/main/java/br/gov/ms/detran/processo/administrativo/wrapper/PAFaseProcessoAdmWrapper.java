/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.processo.administrativo.entidade.PAFaseProcessoAdm;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos
 */
@XmlRootElement
@EntityMapping2(entity = {PAFaseProcessoAdm.class})
public class PAFaseProcessoAdmWrapper implements IBaseEntity, IEntityResource<PAFaseProcessoAdm> {
    private static final long serialVersionUID = 1L;
    
    private PAFaseProcessoAdm entidade;
    private String ativoLabel;
    
    public PAFaseProcessoAdmWrapper(){}
    
    public PAFaseProcessoAdmWrapper(PAFaseProcessoAdm entidade){
        this.entidade = entidade;
    }
    
    public void setId(Long id) {
        if (this.entidade == null) this.entidade = new PAFaseProcessoAdm();
        this.entidade.setId(id);
    }
    
    @Override
    public Serializable getId() {
        if(this.entidade != null){
            return this.entidade.getId();
        } 
        return null;
    }

    @Override
    public PAFaseProcessoAdm getEntidade() {
        return this.entidade;
    }

    @Override
    public void setEntidade(PAFaseProcessoAdm entidade) {
        this.entidade = entidade;
    }
    
        public String getAtivoLabel() {
        if (this.getEntidade() != null 
                && this.getEntidade().getAtivo() != null) {
            ativoLabel = this.getEntidade().getAtivo().toString();
        }
        return ativoLabel;
    }

    public void setAtivoLabel(String ativoLabel) {
        this.ativoLabel = ativoLabel;
    }
}
