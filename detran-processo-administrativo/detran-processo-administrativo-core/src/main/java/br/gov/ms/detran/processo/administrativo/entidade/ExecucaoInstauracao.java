package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntity;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TED_EXECUCAO_INSTAURACAO")
@NamedQueries({
    @NamedQuery(
        name = "ExecucaoInstauracao.atualizaQuantidadeInstauradosParaExecucaoInstauracao",
        query = "UPDATE ExecucaoInstauracao SET quantidadeInstaurado = :p1 WHERE id = :p0"
    )
})
public class ExecucaoInstauracao extends BaseEntity implements Serializable {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Ted_ID")
    private Long id;
    
    @Column(name = "Ted_Situacao")
    @Enumerated(EnumType.STRING)
    private SituacaoExecucaoInstauracaoEnum situacao;
    
    @Column(name = "Ted_Data_Inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInicio;
    
    @Column(name = "Ted_Data_Fim")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataFim;
    
    @Column(name = "Ted_Quantidade")
    private Long quantidadeInstaurado;
    
    @ManyToOne
    @JoinColumn(name = "Ted_Execucao_Instauracao_Origem", referencedColumnName = "Ted_ID")
    private ExecucaoInstauracao execucaoInstauracaoOrigem;
    
    public ExecucaoInstauracao() {
    }
    
    @Override
    public Long getId() {
        return id;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public void setId(Serializable id) {
        this.id = (id != null ? Long.valueOf(id.toString()) : null);
    }

    public SituacaoExecucaoInstauracaoEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoExecucaoInstauracaoEnum situacao) {
        this.situacao = situacao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Long getQuantidadeInstaurado() {
        return quantidadeInstaurado;
    }

    public void setQuantidadeInstaurado(Long quantidadeInstaurado) {
        this.quantidadeInstaurado = quantidadeInstaurado;
    }

    public ExecucaoInstauracao getExecucaoInstauracaoOrigem() {
        return execucaoInstauracaoOrigem;
    }

    public void setExecucaoInstauracaoOrigem(ExecucaoInstauracao execucaoInstauracaoOrigem) {
        this.execucaoInstauracaoOrigem = execucaoInstauracaoOrigem;
    }
}