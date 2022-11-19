
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos Eduardo
 */
@XmlRootElement
@EntityMapping2(entity = {PAAndamentoProcesso.class})
public class PAAndamentoProcessoWrapper implements IBaseEntity, IEntityResource<PAAndamentoProcesso> {

    
    private static final long serialVersionUID = 1L;
    
    private PAAndamentoProcesso entidade;
    private String ativoLabel;
    private String origemPlataforma;

    public PAAndamentoProcessoWrapper() {
    }
            
    public PAAndamentoProcessoWrapper(PAAndamentoProcesso entidade){
        this.entidade = entidade;
    }
    
    public void setId(Long id){
        if(this.entidade == null) this.entidade = new PAAndamentoProcesso();
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
    public PAAndamentoProcesso getEntidade() {
        return this.entidade;
    }

    @Override
    public void setEntidade(PAAndamentoProcesso entidade) {
        this.entidade = entidade;
    }
    
    public String getAtivoLabel() {
        if (this.getEntidade() != null 
                && this.getEntidade().getAtivo() != null) {
            ativoLabel = this.getEntidade().getAtivo().toString();
        }
        return ativoLabel;
    }
    
    public void setAtivoLabel(String ativoLabel){
        this.ativoLabel = ativoLabel;
    }
    
    public String getOrigemPlataforma() {
        if (this.getEntidade() != null 
                && this.getEntidade().getOrigemPlataforma()!= null) {
            origemPlataforma = this.getEntidade().getOrigemPlataforma().toString();
        }
        return origemPlataforma;
    }
    
    public void setOrigemPlataforma(String origemPlataforma){
        this.origemPlataforma = origemPlataforma;
    }
    
    
}
