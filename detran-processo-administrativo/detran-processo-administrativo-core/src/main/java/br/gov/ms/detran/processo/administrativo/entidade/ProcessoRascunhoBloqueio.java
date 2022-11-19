/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRascunhoBloqueioEnum;
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

/**
 *
 * @author lilly
 */
@Entity
@Table(name = "TB_TEP_PROCESSO_RASCUNHO_BLOQUEIO")
@NamedQueries({
    @NamedQuery(name = "ProcessoRascunhoBloqueio.excluirRascunhosCadastradosPorBloqueioPessoa",
            query = "DELETE FROM ProcessoRascunhoBloqueio p where p.processoBloqueioPessoa.id = :p0 and p.situacao = :p1"
    ),
    @NamedQuery(name = "ProcessoRascunhoBloqueio.getRascunhosBloqueadosPorBloqueioPessoa",
            query = "SELECT tep FROM ProcessoRascunhoBloqueio tep INNER JOIN tep.processoBloqueioPessoa teq where tep.situacao <> :p1 and teq.id = :p0 and tep.ativo = :p2 order by tep.prioridade desc"),
    @NamedQuery(name = "ProcessoRascunhoBloqueio.getRascunhoPorProcessoAdministrativo",
            query = "SELECT tep FROM ProcessoRascunhoBloqueio tep INNER JOIN tep.processoAdministrativo tdc WHERE tdc.id = :p0 and tep.ativo = :p1")
})
public class ProcessoRascunhoBloqueio extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tep_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Tep_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;

    @ManyToOne
    @JoinColumn(name = "Tep_Processo_Bloqueio_Pessoa", referencedColumnName = "Teq_ID")
    private ProcessoBloqueioPessoa processoBloqueioPessoa;

    @Column(name = "Tep_Data_Inicio")
    @Temporal(TemporalType.DATE)
    private Date dataInicio;

    @Column(name = "Tep_Data_Fim")
    @Temporal(TemporalType.DATE)
    private Date dataFim;

    @Column(name = "Tep_Situacao")
    @Enumerated(EnumType.STRING)
    private SituacaoRascunhoBloqueioEnum situacao;

    @Column(name = "Tep_Prioridade")
    private Integer prioridade;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public ProcessoRascunhoBloqueio() {
    }
    
    public ProcessoRascunhoBloqueio(ProcessoAdministrativo processoAdministrativo, ProcessoBloqueioPessoa processoBloqueioPessoa, Date dataInicio) {
        this.processoAdministrativo = processoAdministrativo;
        this.processoBloqueioPessoa = processoBloqueioPessoa;
        this.dataInicio = dataInicio;
        this.situacao = SituacaoRascunhoBloqueioEnum.NAO_BLOQUEADO;
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

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public ProcessoBloqueioPessoa getProcessoBloqueioPessoa() {
        return processoBloqueioPessoa;
    }

    public void setProcessoBloqueioPessoa(ProcessoBloqueioPessoa processoBloqueioPessoa) {
        this.processoBloqueioPessoa = processoBloqueioPessoa;
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

    public SituacaoRascunhoBloqueioEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoRascunhoBloqueioEnum situacao) {
        this.situacao = situacao;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
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
