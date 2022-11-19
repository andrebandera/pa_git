package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.core.projeto.entidade.adm.TemplateProtocolo;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.Protocolo;
import br.gov.ms.detran.processo.administrativo.entidade.Recurso;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoRecurso;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@EntityMapping2(entity = {Recurso.class})
public class ConsultaRecursoWrapper implements IBaseEntity, IEntityResource<Recurso> {

    @XmlElement(nillable = false)
    private Recurso entidade;
    
    private ResultadoRecurso resultado;
    
    private RecursoMovimento movimentoApresentacao;
    
    private TemplateProtocolo templateApresentacao;
    
    private RecursoMovimento movimentoCancelamento;
    
    private TemplateProtocolo templateCancelamento;
    
    private Protocolo protocolo;
    
    private String usuarioMovimentoApresentacao;
    
    private String usuarioResultado;

    public ConsultaRecursoWrapper() {
    }

    @Override
    public Recurso getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(Recurso entidade) {
        this.entidade = entidade;
    }

    @Override
    public Serializable getId() {
        return (this.entidade != null ? this.entidade.getId() : null);
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {

        if (this.entidade == null) {
            this.entidade = new Recurso();
        }

        this.entidade.setId(id);
    }

    public ResultadoRecurso getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoRecurso resultado) {
        this.resultado = resultado;
    }

    public RecursoMovimento getMovimentoApresentacao() {
        return movimentoApresentacao;
    }

    public void setMovimentoApresentacao(RecursoMovimento movimentoApresentacao) {
        this.movimentoApresentacao = movimentoApresentacao;
    }

    public RecursoMovimento getMovimentoCancelamento() {
        return movimentoCancelamento;
    }

    public void setMovimentoCancelamento(RecursoMovimento movimentoCancelamento) {
        this.movimentoCancelamento = movimentoCancelamento;
    }

    public Protocolo getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(Protocolo protocolo) {
        this.protocolo = protocolo;
    }

    public String getSituacaoRecursoLabel(){
        if(entidade != null && entidade != null && entidade.getSituacao() != null){
            return entidade.getSituacao().toString();
        }
        return "";
    }

    public String getUsuarioMovimentoApresentacao() {
        return usuarioMovimentoApresentacao;
    }

    public void setUsuarioMovimentoApresentacao(String usuarioMovimentoApresentacao) {
        this.usuarioMovimentoApresentacao = usuarioMovimentoApresentacao;
    }

    public String getUsuarioResultado() {
        return usuarioResultado;
    }

    public void setUsuarioResultado(String usuarioResultado) {
        this.usuarioResultado = usuarioResultado;
    }

    public TemplateProtocolo getTemplateApresentacao() {
        return templateApresentacao;
    }

    public void setTemplateApresentacao(TemplateProtocolo templateApresentacao) {
        this.templateApresentacao = templateApresentacao;
    }

    public TemplateProtocolo getTemplateCancelamento() {
        return templateCancelamento;
    }

    public void setTemplateCancelamento(TemplateProtocolo templateCancelamento) {
        this.templateCancelamento = templateCancelamento;
    }
}