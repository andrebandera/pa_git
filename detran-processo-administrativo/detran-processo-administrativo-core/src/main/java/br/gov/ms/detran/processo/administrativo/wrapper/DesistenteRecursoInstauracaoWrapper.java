/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author desenvolvimento
 */
@EntityMapping2(entity = {PAOcorrenciaStatus.class})
@XmlRootElement
public class DesistenteRecursoInstauracaoWrapper implements IBaseEntity, IEntityResource<PAOcorrenciaStatus> {

    private PAOcorrenciaStatus entidade;

    private BooleanEnum temRecurso;

    private Boolean desistente;

    public DesistenteRecursoInstauracaoWrapper() {
    }

    public DesistenteRecursoInstauracaoWrapper(PAOcorrenciaStatus entidade, BooleanEnum temRecurso, Boolean desistente) {
        this.entidade = entidade;
        this.temRecurso = temRecurso;
        this.desistente = desistente;
    }

    @Override
    public Long getId() {
        return entidade != null ? entidade.getId() : null;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {
        if (this.entidade == null) {
            this.entidade = new PAOcorrenciaStatus();
        }
        this.entidade.setId(id);
    }

    @Override
    public PAOcorrenciaStatus getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(PAOcorrenciaStatus entidade) {
        this.entidade = entidade;
    }

    public BooleanEnum getTemRecurso() {
        return temRecurso;
    }

    public void setTemRecurso(BooleanEnum temRecurso) {
        this.temRecurso = temRecurso;
    }

    public Boolean getDesistente() {
        return desistente;
    }

    public void setDesistente(Boolean desistente) {
        this.desistente = desistente;
    }

    public String getTemRecursoLabel() {
        return temRecurso == null ? "" : temRecurso.toString();
    }
    
    public String getDesistenteLabel() {
        return desistente ? "Sim" :  "Não";
    }

    public String getAndamentoLabel() {
        String label = "";

        if (entidade != null
                && entidade.getStatusAndamento() != null
                && entidade.getStatusAndamento().getAndamentoProcesso() != null
                && entidade.getStatusAndamento().getAndamentoProcesso().getCodigo() != null
                && entidade.getStatusAndamento().getAndamentoProcesso().getDescricao() != null) {

            label = entidade.getStatusAndamento().getAndamentoProcesso().getCodigo().toString().
                    concat(" - ").
                    concat(entidade.getStatusAndamento().getAndamentoProcesso().getDescricao());
        }
        return label;
    }

    public void setAndamentoLabel(String label) {
    }
}
