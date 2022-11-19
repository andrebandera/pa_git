package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.Estado;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.PAEnderecoAlternativo;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Lillydi
 */
@XmlRootElement
@EntityMapping2( entity = {PAEnderecoAlternativo.class} )
public class PAEnderecoAlternativoWrapper  implements IBaseEntity, IEntityResource<PAEnderecoAlternativo> {

    private static final long serialVersionUID = 3704443349309185435L;

    @XmlElement(nillable = false)
    private PAEnderecoAlternativo entidade;
    
    private Estado uf;
    
    @Override
    public Long getId() {
        return (this.entidade != null ? this.entidade.getId() : null);
    }

    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {
        if (this.entidade == null) {
            this.entidade = new PAEnderecoAlternativo();
        }
        
        this.entidade.setId(id);
    }

    @Override
    public PAEnderecoAlternativo getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(PAEnderecoAlternativo entidade) {
        this.entidade = entidade;
    }

    public Estado getUf() {
        return uf;
    }

    public void setUf(Estado uf) {
        this.uf = uf;
    }
}