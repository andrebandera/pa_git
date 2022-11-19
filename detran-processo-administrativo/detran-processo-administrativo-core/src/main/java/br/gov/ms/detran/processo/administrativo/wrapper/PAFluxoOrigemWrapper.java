/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoOrigem;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Carlos Eduardo
 */
@XmlRootElement
@EntityMapping2(entity = {PAFluxoOrigem.class})
public class PAFluxoOrigemWrapper implements IBaseEntity, IEntityResource<PAFluxoOrigem>{

    private PAFluxoOrigem entidade;
    
    private String indiceFluxoInicialLabel;
    
    public PAFluxoOrigemWrapper(){}
    
    public PAFluxoOrigemWrapper(PAFluxoOrigem entidade){
        this.entidade=entidade;
    }
    
    public void setId(Long id) {
        if (this.entidade == null) this.entidade = new PAFluxoOrigem();
        this.entidade.setId(id);
    }
        
    @Override
    public Serializable getId() {
        return (this.entidade != null ? this.entidade.getId() : null);
    }

    @Override
    public PAFluxoOrigem getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(PAFluxoOrigem entidade) {
        this.entidade = entidade;
    }

    public String getIndiceFluxoInicialLabel() {
        if (this.entidade != null) {
            if (this.entidade.getIndiceFluxoInicial() != null) {
                if(entidade.getIndiceFluxoInicial() == true){
                indiceFluxoInicialLabel = "Sim";
            }else{
                indiceFluxoInicialLabel = "Não";
                }
            }
        }
        
        return indiceFluxoInicialLabel;
    }

    public void setIndiceFluxoInicialLabel(String indiceFluxoInicialLabel) {
        this.indiceFluxoInicialLabel = indiceFluxoInicialLabel;
    }
    
    
    
    
}
