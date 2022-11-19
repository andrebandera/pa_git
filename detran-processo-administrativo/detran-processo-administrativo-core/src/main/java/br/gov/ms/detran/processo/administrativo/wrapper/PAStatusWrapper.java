
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatus;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos Eduardo
 */
@XmlRootElement
@EntityMapping2(entity = {PAStatus.class})
public class PAStatusWrapper implements IBaseEntity, IEntityResource<PAStatus> {
    
    private static final long serialVersionUID = 1L;
    
    private PAStatus entidade;
    private String ativoLabel;
    
    public PAStatusWrapper(){}
    
    public PAStatusWrapper(PAStatus entidade){
        this.entidade = entidade;
    }

    public void setId(Long id) {
        if (this.entidade == null) this.entidade = new PAStatus();
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
    public PAStatus getEntidade() {
        return this.entidade; 
    }

    @Override
    public void setEntidade(PAStatus entidade) {
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
