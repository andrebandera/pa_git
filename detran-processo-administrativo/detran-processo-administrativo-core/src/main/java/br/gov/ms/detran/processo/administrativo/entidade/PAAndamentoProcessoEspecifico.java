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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TEG_ANDAMENTO_PROCESSO_ESPECIFICO")
@NamedQueries({
    @NamedQuery(
        name= "PAAndamentoProcessoEspecifico.getListPAAndamentoProcessoEspecifico", 
        query = "SELECT teg "
                + "FROM PAAndamentoProcessoEspecifico teg "
                + "WHERE teg.codigoAndamentoAtual in (:p0) "
                + " AND teg.sucesso = :p1 "
    ),
    @NamedQuery(name = "PAAndamentoProcessoEspecifico.getAndamentoEspecificoPorProcessoAdministrativo",
            query = "SELECT teg From PAAndamentoProcessoEspecifico teg where teg.idProcessoAdministrativo = :p0")
})
public class PAAndamentoProcessoEspecifico extends BaseEntity implements Serializable {

    @Id
    @Column(name = "Teg_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "Teg_Processo_Administrativo")
    private Long idProcessoAdministrativo;
    
    @Column(name = "Teg_Codigo_Andamento_Origem")
    private Integer codigoAndamentoOrigem;
    
    @Column(name = "Teg_Codigo_Andamento_Atual")
    private Integer codigoAndamentoAtual;
    
    @Column(name = "Teg_Codigo_Andamento_Destino")
    private Integer codigoAndamentoDestino;
    
    @Column(name = "Teg_Codigo_Fluxo_Destino")
    private Integer codigoFluxoDestino;
    
    @Column(name = "Teg_Fluxo_Fase_Origem")
    private Long fluxoFaseOrigem;
    
    @Column(name = "Teg_Sucesso")
    private Boolean sucesso;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "Teg_Data_Cadastro")
    private Date dataCadastro;
    
    @Column(name = "Teg_Observacao")
    private String observacao;
    
    @Column(name = "Teg_Observacao_Prescricao")
    private String observacaoPrescricao;
    
    @Column(name = "Teg_Prescricao")
    private Long prescricao;
    
    public PAAndamentoProcessoEspecifico() {
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

    public Integer getCodigoAndamentoOrigem() {
        return codigoAndamentoOrigem;
    }

    public void setCodigoAndamentoOrigem(Integer codigoAndamentoOrigem) {
        this.codigoAndamentoOrigem = codigoAndamentoOrigem;
    }

    public Integer getCodigoAndamentoDestino() {
        return codigoAndamentoDestino;
    }

    public void setCodigoAndamentoDestino(Integer codigoAndamentoDestino) {
        this.codigoAndamentoDestino = codigoAndamentoDestino;
    }

    public Integer getCodigoAndamentoAtual() {
        return codigoAndamentoAtual;
    }

    public void setCodigoAndamentoAtual(Integer codigoAndamentoAtual) {
        this.codigoAndamentoAtual = codigoAndamentoAtual;
    }

    public Boolean getSucesso() {
        return sucesso;
    }

    public void setSucesso(Boolean sucesso) {
        this.sucesso = sucesso;
    }

    public Long getIdProcessoAdministrativo() {
        return idProcessoAdministrativo;
    }

    public void setIdProcessoAdministrativo(Long idProcessoAdministrativo) {
        this.idProcessoAdministrativo = idProcessoAdministrativo;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Integer getCodigoFluxoDestino() {
        return codigoFluxoDestino;
    }

    public void setCodigoFluxoDestino(Integer codigoFluxoDestino) {
        this.codigoFluxoDestino = codigoFluxoDestino;
    }

    public Long getFluxoFaseOrigem() {
        return fluxoFaseOrigem;
    }

    public void setFluxoFaseOrigem(Long fluxoFaseOrigem) {
        this.fluxoFaseOrigem = fluxoFaseOrigem;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getObservacaoPrescricao() {
        return observacaoPrescricao;
    }

    public void setObservacaoPrescricao(String observacaoPrescricao) {
        this.observacaoPrescricao = observacaoPrescricao;
    }

    public Long getPrescricao() {
        return prescricao;
    }

    public void setPrescricao(Long prescricao) {
        this.prescricao = prescricao;
    }
}