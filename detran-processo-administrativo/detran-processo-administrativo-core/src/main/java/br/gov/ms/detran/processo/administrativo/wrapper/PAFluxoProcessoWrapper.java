
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.core.projeto.entidade.inf.AmparoLegal;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Carlos
 */
@XmlRootElement
@EntityMapping2(entity = {PAFluxoProcesso.class})
public class PAFluxoProcessoWrapper implements IBaseEntity, IEntityResource<PAFluxoProcesso>{

    private static final long serialVersionUID = 1L;
    
    private PAFluxoProcesso entidade;
    private AmparoLegal amparoLegal;
    private String ativoLabel;
    
    private String fluxoIndependenteLabel;
    
    public PAFluxoProcessoWrapper(){}
    
    public PAFluxoProcessoWrapper(PAFluxoProcesso entidade){
        this.entidade = entidade;
    }

    public void setId(Long id) {
        if (this.entidade == null) this.entidade = new PAFluxoProcesso();
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
    public PAFluxoProcesso getEntidade() {
        return this.entidade; 
    }

    @Override
    public void setEntidade(PAFluxoProcesso entidade) {
        this.entidade = entidade;
    }

    public AmparoLegal getAmparoLegal() {
        return amparoLegal;
    }

    public void setAmparoLegal(AmparoLegal amparoLegal) {
        this.amparoLegal = amparoLegal;
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

    public String getFluxoIndependenteLabel() {
        fluxoIndependenteLabel = "";
        
        if (null != entidade && null != entidade.getFluxoIndependente())
            fluxoIndependenteLabel = entidade.getFluxoIndependente().toString();
        
        return fluxoIndependenteLabel;
    }

    public void setFluxoIndependenteLabel(String fluxoIndependenteLabel) {
        this.fluxoIndependenteLabel = fluxoIndependenteLabel;
    }
}