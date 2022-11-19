/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos Eduardo
 */
@XmlRootElement
@EntityMapping2(entity = {PAFluxoFase.class})
public class PAFluxoFaseWrapper implements IBaseEntity, IEntityResource<PAFluxoFase>{
    
    private PAFluxoFase entidade;
    
    private String andamentoProcessoLabel;
    
    private String fluxoProcessoLabel;
    
    private String faseProcessoAdmLabel;
    
    public PAFluxoFaseWrapper(){}
    
    public PAFluxoFaseWrapper(PAFluxoFase entidade){
        this.entidade = entidade;
    }
    
    public void setId(Long id) {
        if (this.entidade == null) this.entidade = new PAFluxoFase();
        this.entidade.setId(id);
    }
    
    @Override
    public Serializable getId() {
        return (this.entidade != null ? this.entidade.getId() : null);
    }

    @Override
    public PAFluxoFase getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(PAFluxoFase entidade) {
        this.entidade = entidade;
    }

    public String getAndamentoProcessoLabel() {
        if(entidade.getAndamentoProcesso() == null){
            return null;
        }
        andamentoProcessoLabel = entidade.getAndamentoProcesso().getCodigo().toString() 
                + "-" + entidade.getAndamentoProcesso().getDescricao();
        return andamentoProcessoLabel;
    }

    public void setAndamentoProcessoLabel(String andamentoProcesso) {
        this.andamentoProcessoLabel = andamentoProcesso;
    }

    public String getFluxoProcessoLabel() {
        if(entidade.getPrioridadeFluxoAmparo().getFluxoProcesso() == null){
            return null;
        }
        fluxoProcessoLabel = entidade.getPrioridadeFluxoAmparo().getFluxoProcesso().getCodigo().toString()
                +"-"+entidade.getPrioridadeFluxoAmparo().getFluxoProcesso().getDescricao();
        return fluxoProcessoLabel;
    }

    public void setFluxoProcessoLabel(String fluxoProcessoLabel) {
        this.fluxoProcessoLabel = fluxoProcessoLabel;
    }

    public String getFaseProcessoAdmLabel() {
        if(entidade.getPrioridadeFluxoAmparo().getFaseProcessoAdm() == null){
            return null;
        }
        faseProcessoAdmLabel = entidade.getPrioridadeFluxoAmparo().getFaseProcessoAdm().getCodigo()
                +"-"+entidade.getPrioridadeFluxoAmparo().getFaseProcessoAdm().getDescricao();
        
        
        return faseProcessoAdmLabel;
    }

    public void setFaseProcessoAdmLabel(String faseProcessoAdmLabel) {
        this.faseProcessoAdmLabel = faseProcessoAdmLabel;
    }
    
    
    
}
