package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoMotivoEnum;
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
@Table(name = "TB_TEB_PAD_MOVIMENTACAO")
@NamedQueries({
    @NamedQuery(
            name = "Movimentacao.getMovimentacaoPorProcessoAdministrativoEAtivo",
            query = "SELECT new Movimentacao(teb.id, teb.movimentacaoAcao, teb.motivo) "
                    + "FROM Movimentacao teb "
                    + "WHERE teb.processoAdministrativo.id = :p0 "
                    + "AND teb.ativo = :p1 "
                    + "ORDER BY teb.dataInclusao DESC "),
    @NamedQuery(
            name = "Movimentacao.getMovimentacaoPorProcessoAdministrativoEAcaoAtivo",
            query = "SELECT new Movimentacao(teb.id, teb.movimentacaoAcao) "
                    + "FROM Movimentacao teb "
                    + "WHERE teb.processoAdministrativo.id = :p0 "
                    + "AND teb.ativo = :p1 "
                    + "AND teb.movimentacaoAcao = :p2 "
                    + "ORDER BY teb.dataInclusao DESC "),
    @NamedQuery(
            name = "Movimentacao.getMovimentacaoSuspensaoDesativaRecente",
            query = "SELECT teb FROM Movimentacao teb "
                    + "WHERE teb.processoAdministrativo.id = :p0 "
                    + "AND teb.movimentacaoAcao = :p1 "
                    + "AND teb.ativo = :p2 "
                    + "ORDER BY teb.dataAlteracao DESC "),
    @NamedQuery(
            name = "Movimentacao.getMovimentacaoPorProcessoAdministrativoAtivo",
            query = "SELECT teb FROM Movimentacao teb WHERE teb.processoAdministrativo.id = :p0 AND teb.ativo = :p1 "),
    @NamedQuery(
            name = "Movimentacao.getMovimentacoesPorListaAndamento",
            query = "SELECT new Movimentacao("
            + "                      teb.id, "
            + "                      teb.usuario, "
            + "                      teb.processoAdministrativo.id, "
            + "                      teb.processoAdministrativo.numeroProcesso, "
            + "                      teb.movimentacaoAcao, "
            + "                      teb.motivo, "
            + "                      teb.dataCadastro, "
            + "                      teb.observacao) "
            + "FROM Movimentacao teb "
            + "WHERE teb.ativo = :p0 "
            + " AND EXISTS (SELECT tde.id "
            + "             FROM PAOcorrenciaStatus tde "
            + "             WHERE tde.processoAdministrativo.id = teb.processoAdministrativo.id "
            + "                 AND tde.situacao = :p1 "
            + "                 AND tde.ativo = :p0 "
            + "                 AND tde.statusAndamento.andamentoProcesso.codigo in (:p2) "
            + "             ) "
    ),
    @NamedQuery(
            name = "Movimentacao.getMovimentacoesPorPA",
            query = "SELECT new Movimentacao(teb.id, "
                    + "                      teb.usuario, "
                    + "                      teb.processoAdministrativo.id, "
                    + "                      teb.processoAdministrativo.numeroProcesso, "
                    + "                      teb.movimentacaoAcao, "
                    + "                      teb.motivo, "
                    + "                      teb.dataCadastro, "
                    + "                      teb.observacao) "
                    + "FROM Movimentacao teb inner join teb.processoAdministrativo tdc "
                    + "WHERE tdc.id = :p0 ")
})
public class Movimentacao extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Teb_ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Teb_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;
    
    @ManyToOne
    @JoinColumn(name = "Teb_Fluxo_Processo", referencedColumnName = "Tch_ID")
    private PAFluxoFase fluxoFase;
       
    @Column(name = "Teb_Prescricao")
    private Long prescricao;
    
    @Column(name = "Teb_Usuario")
    private Long usuario;
    
    @Column(name = "Teb_Acao")
    @Enumerated(EnumType.STRING)
    private MovimentacaoAcaoEnum movimentacaoAcao;
    
    @Column(name = "Teb_Data_Inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInicio;
    
    @Column(name = "Teb_Motivo")
    @Enumerated(EnumType.STRING)
    private MovimentacaoMotivoEnum motivo;
    
    @Column(name = "Teb_Observacao")
    private String observacao;
    
    @Column(name = "Teb_Data_Cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public Movimentacao() {
    }

    public Movimentacao(Long id, MovimentacaoAcaoEnum movimentacaoAcao) {
        this.id = id;
        this.movimentacaoAcao = movimentacaoAcao;
    }
    
    public Movimentacao(Long id, MovimentacaoAcaoEnum movimentacaoAcao, MovimentacaoMotivoEnum motivo) {
        this(id, movimentacaoAcao);
        this.motivo = motivo;
    }

    public Movimentacao(
        Long id, 
        Long usuario, 
        Long idProcessoAdministrativo,
        String numeroProcesso,
        MovimentacaoAcaoEnum movimentacaoAcao, 
        MovimentacaoMotivoEnum motivo, 
        Date dataCadastro, 
        String observacao) {
        
        this.id = id;
        this.usuario = usuario;
        
        this.setProcessoAdministrativo(new ProcessoAdministrativo(idProcessoAdministrativo, numeroProcesso));
        
        this.movimentacaoAcao = movimentacaoAcao;
        this.motivo = motivo;
        this.dataCadastro = dataCadastro;
        this.observacao = observacao;
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

    public PAFluxoFase getFluxoFase() {
        return fluxoFase;
    }

    public void setFluxoFase(PAFluxoFase fluxoFase) {
        this.fluxoFase = fluxoFase;
    }

    public Long getPrescricao() {
        return prescricao;
    }

    public void setPrescricao(Long prescricao) {
        this.prescricao = prescricao;
    }
    
    public Long getUsuario() {
        return usuario;
    }

    public void setUsuario(Long usuario) {
        this.usuario = usuario;
    }

    public MovimentacaoAcaoEnum getMovimentacaoAcao() {
        return movimentacaoAcao;
    }

    public void setMovimentacaoAcao(MovimentacaoAcaoEnum movimentacaoAcao) {
        this.movimentacaoAcao = movimentacaoAcao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }
    
    @XmlElement(name = "dataInicio")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public MovimentacaoMotivoEnum getMotivo() {
        return motivo;
    }

    public void setMotivo(MovimentacaoMotivoEnum motivo) {
        this.motivo = motivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }
    
    @XmlElement(name = "dataCadastro")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
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