/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhControle;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP93;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Lillydi
 */
@EntityMapping2(entity = {ApoioOrigemInstauracao.class})
@XmlRootElement
public class PAPenalidadeProcessoWrapper  implements IBaseEntity, IEntityResource<PAPenalidadeProcesso> {

    private PAPenalidadeProcesso entidade;
    
    private String tempoPenalidade;
    
    private String observacao;
    
    private Date dataInicio;
    
    private CnhControle controle;
    
    private AEMNPP93 dadosCondutorBCA;
    
    private String portariaPenalidade;

    public PAPenalidadeProcessoWrapper() {
    }
    @Override
    public Long getId() {
        return entidade != null ? entidade.getId() : null;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {
        if (this.entidade == null) {
            this.entidade = new PAPenalidadeProcesso();
        }
        this.entidade.setId(id);
    }

    @Override
    public PAPenalidadeProcesso getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(PAPenalidadeProcesso entidade) {
        this.entidade = entidade;
    }

    public String getTempoPenalidade() {
        return tempoPenalidade;
    }

    public void setTempoPenalidade(String tempoPenalidade) {
        this.tempoPenalidade = tempoPenalidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public CnhControle getControle() {
        return controle;
    }

    public void setControle(CnhControle controle) {
        this.controle = controle;
    }

    public AEMNPP93 getDadosCondutorBCA() {
        return dadosCondutorBCA;
    }

    public void setDadosCondutorBCA(AEMNPP93 dadosCondutorBCA) {
        this.dadosCondutorBCA = dadosCondutorBCA;
    }

    public String getPortariaPenalidade() {
        return portariaPenalidade;
    }

    public void setPortariaPenalidade(String portariaPenalidade) {
        this.portariaPenalidade = portariaPenalidade;
    }
}
