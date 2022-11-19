package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFaseRetorno;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos Eduardo
 */
@XmlRootElement
@EntityMapping2(entity = {PAFluxoFaseRetorno.class})
public class PAFluxoFaseRetornoWrapper implements IBaseEntity, IEntityResource<PAFluxoFaseRetorno>{
    
    private PAFluxoFaseRetorno entidade;
    
    public PAFluxoFaseRetornoWrapper(){}
    
    public PAFluxoFaseRetornoWrapper(PAFluxoFaseRetorno entidade){
        this.entidade = entidade;
    }
    
    public void setId(Long id) {
        if (this.entidade == null) this.entidade = new PAFluxoFaseRetorno();
        this.entidade.setId(id);
    }
    
    @Override
    public Serializable getId() {
        return (this.entidade != null ? this.entidade.getId() : null);
    }

    @Override
    public PAFluxoFaseRetorno getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(PAFluxoFaseRetorno entidade) {
        this.entidade = entidade;
    }
    
}
