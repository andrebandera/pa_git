package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntity;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TEF_PROCESSO_ADMINISTRATIVO_BCA")
public class ProcessoAdministrativoBCA extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tef_ID")
    private Long id;
    
    @Column(name = "Tef_Integracao")
    private String integracao;
    
    @Column(name = "Tef_Data")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataProcessamento;

    @Column(name = "Tef_Qtd_Sucesso")
    private Integer quantidadeSucesso;
    
    @Column(name = "Tef_Qtd_Falhou")
    private Integer quantidadeFalhou;
    
    @Lob
    @Column(name = "Tef_Arquivo_Relatorio")
    private byte[] relatorio;
    
    @Column(name = "Tef_Processo_Sucesso")
    private String listaProcessoSucesso;
    
    @Column(name = "Tef_Processo_Falha")
    private String listaProcessoFalha;

    public ProcessoAdministrativoBCA() {
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

    public Date getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(Date dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public Integer getQuantidadeSucesso() {
        return quantidadeSucesso;
    }

    public void setQuantidadeSucesso(Integer quantidadeSucesso) {
        this.quantidadeSucesso = quantidadeSucesso;
    }

    public Integer getQuantidadeFalhou() {
        return quantidadeFalhou;
    }

    public void setQuantidadeFalhou(Integer quantidadeFalhou) {
        this.quantidadeFalhou = quantidadeFalhou;
    }

    public byte[] getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(byte[] relatorio) {
        this.relatorio = relatorio;
    }

    public String getIntegracao() {
        return integracao;
    }

    public void setIntegracao(String integracao) {
        this.integracao = integracao;
    }

    public String getListaProcessoSucesso() {
        return listaProcessoSucesso;
    }

    public void setListaProcessoSucesso(String listaProcessoSucesso) {
        this.listaProcessoSucesso = listaProcessoSucesso;
    }

    public String getListaProcessoFalha() {
        return listaProcessoFalha;
    }

    public void setListaProcessoFalha(String listaProcessoFalha) {
        this.listaProcessoFalha = listaProcessoFalha;
    }
}