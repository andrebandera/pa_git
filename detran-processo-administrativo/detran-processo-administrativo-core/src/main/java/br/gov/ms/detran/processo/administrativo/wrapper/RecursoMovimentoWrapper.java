package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoRecurso;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class RecursoMovimentoWrapper implements IBaseEntity, IEntityResource<RecursoMovimento> {
    
    private RecursoMovimento entidade;
    
    private ResultadoRecurso resultadoRecurso;

    public RecursoMovimentoWrapper() {
    }
    
    @Override
    public Long getId() {
        return entidade != null ? entidade.getId() : null;
    }
    
    @XmlElement(name="id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id){
        if(this.entidade == null){
            this.entidade = new RecursoMovimento();
        }
        this.entidade.setId(id);
    }

    @Override
    public RecursoMovimento getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(RecursoMovimento entidade) {
        this.entidade = entidade;
    }

    public ResultadoRecurso getResultadoRecurso() {
        return resultadoRecurso;
    }

    public void setResultadoRecurso(ResultadoRecurso resultadoRecurso) {
        this.resultadoRecurso = resultadoRecurso;
    }
    
    public String getSituacaoRecursoLabel(){
        if(entidade != null && entidade.getRecurso() != null && entidade.getRecurso().getSituacao() != null){
            return entidade.getRecurso().getSituacao().toString();
        }
        return "";
    }

    public void setStituacaoRecursoLabel(String sit){
    }
}