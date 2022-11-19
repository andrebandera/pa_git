package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author yanko.campos
 */
@EntityMapping2(entity = {ProcessoAdministrativoInfracao.class})
@XmlRootElement
public class ProcessoAdministrativoInfracaoWrapper implements IBaseEntity, IEntityResource<ProcessoAdministrativoInfracao> {

    private ProcessoAdministrativoInfracao entidade;
    
    private Boolean reativarPontuacao;
    
    private String descricaoInfracao;
    
    private String origemInfracaoLabel;
    
    private String orgaoAutuadorLabel;
    private String artigo;

    public ProcessoAdministrativoInfracaoWrapper() {
    }
    
    @Override
    public Long getId() {
        return entidade != null ? entidade.getId() : null;
    }
    
    @XmlElement(name="id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id){
        if(this.entidade == null){
            this.entidade = new ProcessoAdministrativoInfracao();
        }
        this.entidade.setId(id);
    }

    @Override
    public ProcessoAdministrativoInfracao getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(ProcessoAdministrativoInfracao entidade) {
        this.entidade = entidade;
    }

    public Boolean getReativarPontuacao() {
        return reativarPontuacao;
    }

    public void setReativarPontuacao(Boolean reativarPontuacao) {
        this.reativarPontuacao = reativarPontuacao;
    }

    public String getDescricaoInfracao() {
        return descricaoInfracao;
    }

    public void setDescricaoInfracao(String descricaoInfracao) {
        this.descricaoInfracao = descricaoInfracao;
    }

    public String getOrigemInfracaoLabel() {
        origemInfracaoLabel = "";
        
        if (null != this.entidade) {
            if (null != this.entidade.getOrigemInfracao()) {
                if (1L == this.entidade.getOrigemInfracao()) {
                    origemInfracaoLabel = "Infração/Autuação Local ";
                } else if (2L == this.entidade.getOrigemInfracao()) {
                    origemInfracaoLabel = "Infração Renainf ";
                } else if (3L == this.entidade.getOrigemInfracao()) {
                    origemInfracaoLabel = "Infração Renainf Pontuação";
                } else {
                    origemInfracaoLabel = this.entidade.getOrigemInfracao().toString() + " - Origem não definida";
                }
            }
        }
        
        return origemInfracaoLabel;
    }

    public void setOrigemInfracaoLabel(String origemInfracaoLabel) {
        this.origemInfracaoLabel = origemInfracaoLabel;
    }

    public String getOrgaoAutuadorLabel() {
        return orgaoAutuadorLabel;
    }

    public void setOrgaoAutuadorLabel(String orgaoAutuadorLabel) {
        this.orgaoAutuadorLabel = orgaoAutuadorLabel;
    }

    public String getArtigo() {
        return artigo;
    }

    public void setArtigo(String artigo) {
        this.artigo = artigo;
    }
}