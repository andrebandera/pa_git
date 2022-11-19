/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.core.projeto.entidade.inf.AmparoLegal;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Lillydi
 * Alterado: CarlosEduardo - 12/06/2019
 */
@EntityMapping2(entity = {ApoioOrigemInstauracao.class})
@XmlRootElement
public class ApoioOrigemInstauracaoWrapper implements IBaseEntity, IEntityResource<ApoioOrigemInstauracao> {

    private ApoioOrigemInstauracao entidade;

    private Integer valorReferenciaMes;
    
    private AmparoLegal amparoLegal;
    
    private String resultadoMotivoLabel;
    
    private String resultadoTipoProcessoLabel;
    
    private String resultadoAcaoLabel;
    
    private String acaoSistemaLabel;
    
    private Integer valorReferenciaIntervalo;

    public ApoioOrigemInstauracaoWrapper() {
    }

    public ApoioOrigemInstauracaoWrapper(ApoioOrigemInstauracao entidade) {
        this.entidade = entidade;
    }
    
    public ApoioOrigemInstauracaoWrapper(ApoioOrigemInstauracao entidade, Integer valorReferenciaMes, Integer valorReferenciaIntervalo) {
        this.entidade = entidade;
        this.valorReferenciaMes = valorReferenciaMes;
        this.valorReferenciaIntervalo = valorReferenciaIntervalo;
    }

    @Override
    public Long getId() {
        return entidade != null ? entidade.getId() : null;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {
        if (this.entidade == null) {
            this.entidade = new ApoioOrigemInstauracao();
        }
        this.entidade.setId(id);
    }

    @Override
    public ApoioOrigemInstauracao getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(ApoioOrigemInstauracao entidade) {
        this.entidade = entidade;
    }

    public Integer getValorReferenciaMes() {
        return valorReferenciaMes;
    }

    public void setValorReferenciaMes(Integer valorReferenciaMes) {
        this.valorReferenciaMes = valorReferenciaMes;
    }

    public Integer getValorReferenciaIntervalo() {
        return valorReferenciaIntervalo;
    }

    public void setValorReferenciaIntervalo(Integer valorReferenciaIntervalo) {
        this.valorReferenciaIntervalo = valorReferenciaIntervalo;
    }
    
    public String getResultadoMotivoLabel() {
        if (this.entidade != null) {
            if (this.entidade.getResultadoMotivo() != null) {
                resultadoMotivoLabel = this.entidade.getResultadoMotivo().toString();
            }
        }
        
        return resultadoMotivoLabel;
    }

    public void setResultadoMotivoLabel(String resultadoMotivoLabel) {
        this.resultadoMotivoLabel = resultadoMotivoLabel;
    }

    public String getResultadoTipoProcessoLabel() {
        if (this.entidade != null) {
            if (this.entidade.getResultadoTipoProcesso() != null) {
                resultadoTipoProcessoLabel = this.entidade.getResultadoTipoProcesso().toString();
            }
        }
        return resultadoTipoProcessoLabel;
    }

    public void setResultadoTipoProcessoLabel(String resultadoTipoProcessoLabel) {
        this.resultadoTipoProcessoLabel = resultadoTipoProcessoLabel;
    }

    public String getResultadoAcaoLabel() {
        if (this.entidade != null) {
            if (this.entidade.getResultadoAcao() != null) {
                resultadoAcaoLabel = this.entidade.getResultadoAcao().toString();
            }
        }
        
        return resultadoAcaoLabel;
    }

    public void setResultadoAcaoLabel(String resultadoAcaoLabel) {
        this.resultadoAcaoLabel = resultadoAcaoLabel;
    }

    public String getAcaoSistemaLabel() {
        if (this.entidade != null) {
            if (this.entidade.getAcaoSistema()!= null) {
                acaoSistemaLabel = this.entidade.getAcaoSistema().toString();
            }
        }
        return acaoSistemaLabel;
    }

    public void setAcaoSistemaLabel(String acaoSistemaLabel) {
        this.acaoSistemaLabel = acaoSistemaLabel;
    }

    public AmparoLegal getAmparoLegal() {
        return amparoLegal;
    }

    public void setAmparoLegal(AmparoLegal amparoLegal) {
        this.amparoLegal = amparoLegal;
    }
  
}
