/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoProcessoBloqueioPessoaEnum;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author lilly
 */
@Entity
@Table(name = "TB_TEQ_PROCESSO_BLOQUEIO_PESSOA")
@NamedQueries({
    @NamedQuery(name = "ProcessoBloqueioPessoa.getProcessoBloqueioPessoaPorCPFEDataEmAndamento",
            query = "SELECT teq from ProcessoBloqueioPessoa teq where teq.cpf = :p0 and (teq.dataFim is null or teq.dataFim >= :p1) and teq.situacao <> :p2 and teq.ativo = :p3")
})
public class ProcessoBloqueioPessoa extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Teq_ID")
    private Long id;
    
    @Column(name = "Teq_Data_Inicio")
    @Temporal(TemporalType.DATE)
    private Date dataInicio;
    
    @Column(name = "Teq_Data_Fim")
    @Temporal(TemporalType.DATE)
    private Date dataFim;
    
    @Column(name = "Teq_Situacao")
    @Enumerated(EnumType.STRING)
    private SituacaoProcessoBloqueioPessoaEnum situacao;
    
    @Column(name = "Teq_CPF")
    private String cpf;
    
    @Column(name = "Teq_tempo")
    private Integer tempo;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public ProcessoBloqueioPessoa() {
    }

    public ProcessoBloqueioPessoa(Date dataInicio,  String cpf) {
        this.dataInicio = dataInicio;
        this.cpf = cpf;
        this.situacao = SituacaoProcessoBloqueioPessoaEnum.CADASTRADO;
        this.ativo = AtivoEnum.ATIVO;
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

    public SituacaoProcessoBloqueioPessoaEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProcessoBloqueioPessoaEnum situacao) {
        this.situacao = situacao;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Integer getTempo() {
        return tempo;
    }

    public void setTempo(Integer tempo) {
        this.tempo = tempo;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }
}
