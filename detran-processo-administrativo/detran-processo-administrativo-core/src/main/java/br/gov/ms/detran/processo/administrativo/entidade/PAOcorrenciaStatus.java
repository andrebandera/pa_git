package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.comum.util.adapter.TimestampAdapter;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
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
 * @author Lillydi
 */
@Entity
@Table(name = "TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM")
@NamedQueries({
     @NamedQuery(
        name = "PAOcorrenciaStatus.getPAOcorrenciaStatusAtivaApenasCodigoAndamento",
        query = "SELECT new PAOcorrenciaStatus(tde.id, tcd.id, tcb.id, tcb.codigo) "
                + "FROM PAOcorrenciaStatus tde "
                + "INNER JOIN tde.processoAdministrativo pa "
                + "INNER JOIN tde.statusAndamento tcd "
                + "INNER JOIN tcd.andamentoProcesso tcb "
                + "WHERE pa.id = :p0 "
                + "AND tde.ativo = :p1 "),
    @NamedQuery(
        name = "PAOcorrenciaStatus.searchOcorrenciaAtiva",
        query = "SELECT tde "
                + " FROM PAOcorrenciaStatus tde "
                + " WHERE tde.processoAdministrativo.id = :p0 "
                + "     AND tde.ativo = :p1 "),
    @NamedQuery(
        name = "PAOcorrenciaStatus.getOcorrenciasDesistentesInstPenalizacao",
        query = "SELECT tde "
                + " From PAOcorrenciaStatus tde "
                + "     INNER JOIN tde.processoAdministrativo tdc "
                + " where tdc.cpf = :p0 "
                + "     and tdc.ativo = :p2 "
                + "     and EXISTS("
                + "             SELECT 1 "
                + "             FROM PAComplemento tdl "
                + "             where tdl.processoAdministrativo.id = tdc.id "
                + "                 and tdl.parametro = :p1 "
                + "                 and tdl.ativo = :p2)"),
    @NamedQuery(
        name = "PAOcorrenciaStatus.getOcorrenciaStatusAtivaPorProcessoAdm",
        query = "SELECT tde "
                + " FROM PAOcorrenciaStatus tde "
                + "     INNER JOIN tde.processoAdministrativo pa "
                + " WHERE pa.id = :p0 "
                + "     AND tde.ativo = :p1"),
    @NamedQuery(
        name = "PAOcorrenciaStatus.getOcorrenciaStatusAtivaPorNumeroProcesso",
        query = "SELECT tde "
                + " FROM PAOcorrenciaStatus tde "
                + "     INNER JOIN tde.processoAdministrativo pa "
                + " WHERE pa.numeroProcesso = :p0 "
                + "     AND tde.ativo = :p1"),
    @NamedQuery(
        name = "PAOcorrenciaStatus.validaAndamentoPorFluxo",
        query = "SELECT 1 "
                + " From PAOcorrenciaStatus tde "
                + "     INNER JOIN tde.statusAndamento tcd "
                + "     INNER JOIN tcd.andamentoProcesso tcb "
                + "     INNER JOIN tde.fluxoProcesso tck "
                + " WHERE EXISTS("
                + "         SELECT 1 "
                + "         FROM PAFluxoAndamento tcr "
                + "             INNER JOIN tcr.fluxoFase tch "
                + "             INNER JOIN tch.prioridadeFluxoAmparo tci "
                + "             INNER JOIN tci.fluxoProcesso tck1 "
                + "             INNER JOIN tcr.fluxoProcesso tck2 "
                + "         where "
                + "             tck.id = tck1.id "
                + "             and tch.andamentoProcesso.id = tcb.id "
                + "             and tck2.codigo = :p1 "
                + "             and tcr.ativo = :p2 "
                + "             and tck2.ativo = :p2 "
                + "             and tch.ativo = :p2 "
                + "             and tci.ativo = :p2 "
                + "             and tck1.ativo = :p2) "
                + "     and tde.id = :p0 "),
    @NamedQuery(
        name = "PAOcorrenciaStatus.getOcorrenciaPorCpfEFluxo",
        query = "SELECT new PAOcorrenciaStatus(tde.id, tdc.id, tdc.versaoRegistro, tdc.numeroProcesso, tcb.codigo, tcb.descricao) "
                + " From PAOcorrenciaStatus tde "
                + "     INNER JOIN tde.statusAndamento tcd "
                + "     INNER JOIN tde.processoAdministrativo tdc "
                + "     INNER JOIN tcd.andamentoProcesso tcb "
                + "     INNER JOIN tde.fluxoProcesso tck "
                + " WHERE EXISTS("
                + "         SELECT 1 "
                + "         FROM PAFluxoAndamento tcr "
                + "             INNER JOIN tcr.fluxoFase tch "
                + "             INNER JOIN tch.prioridadeFluxoAmparo tci "
                + "             INNER JOIN tci.fluxoProcesso tck1 "
                + "             INNER JOIN tcr.fluxoProcesso tck2 "
                + "         where "
                + "             tck.id = tck1.id "
                + "             and tch.andamentoProcesso.id = tcb.id "
                + "             and tck2.codigo = :p1 "
                + "             and tcr.ativo = :p2 "
                + "             and tck2.ativo = :p2 "
                + "             and tch.ativo = :p2 "
                + "             and tci.ativo = :p2 "
                + "             and tck1.ativo = :p2) "
                + "     and tdc.cpf = :p0 "),
    
    @NamedQuery(
        name = "PAOcorrenciaStatus.getPAOcorrenciaStatusAtivoPorPAFluxoProcesso",
        query = "SELECT tde FROM PAFluxoAndamento tde WHERE tde.fluxoProcesso.id =:p0 AND tde.ativo = :p1")
})
public class PAOcorrenciaStatus extends BaseEntityAtivo implements Serializable {

    @Id
    @Column(name = "Tde_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "Tde_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    @ManyToOne
    private ProcessoAdministrativo processoAdministrativo;

    @JoinColumn(name = "Tde_Status_Andamento", referencedColumnName = "Tcd_ID")
    @ManyToOne
    private PAStatusAndamento statusAndamento;
    
    @Column(name = "Tde_Data_Inicio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInicio;

    @Column(name = "Tde_Data_Termino")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataTermino;
    
    @Column(name = "Tde_Situacao")
    @Enumerated(EnumType.STRING)
    private SituacaoOcorrenciaEnum situacao;
    
    @JoinColumn(name = "Tde_Fluxo_Processo", referencedColumnName = "Tck_ID")
    @ManyToOne
    private PAFluxoProcesso fluxoProcesso;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public PAOcorrenciaStatus() {
        
    }
    
    public PAOcorrenciaStatus(Long id, Long statusAndamentoId, Long andamentoProcessoId, Integer andamentoProcessoCodigo) {
        this(id, new PAStatusAndamento(statusAndamentoId, new PAAndamentoProcesso(andamentoProcessoId, andamentoProcessoCodigo)));
    }
    
    public PAOcorrenciaStatus(Long id, PAStatusAndamento statusAndamento) {
        this.id = id;
        this.statusAndamento = statusAndamento;
    }

    public PAOcorrenciaStatus(ProcessoAdministrativo processoAdministrativo, PAStatusAndamento statusAndamento) {
        this.processoAdministrativo = processoAdministrativo;
        this.statusAndamento = statusAndamento;
        this.dataInicio = new Date();
        this.ativo = AtivoEnum.ATIVO;
    }

    public PAOcorrenciaStatus(ProcessoAdministrativo pa, 
                              PAStatusAndamento statusAndamento, 
                              SituacaoOcorrenciaEnum situacao) {
        
        this.processoAdministrativo = pa;
        this.statusAndamento = statusAndamento;
        this.situacao = situacao;
        this.dataInicio = new Date();
        this.ativo = AtivoEnum.ATIVO;
    }

    public PAOcorrenciaStatus(ProcessoAdministrativo pa, 
                              PAStatusAndamento statusAndamento, 
                              SituacaoOcorrenciaEnum situacao, 
                              PAFluxoProcesso fluxoProcesso) {
        
        this.processoAdministrativo = pa;
        this.statusAndamento = statusAndamento;
        this.situacao = situacao;
        this.fluxoProcesso = fluxoProcesso;
        this.dataInicio = new Date();
        this.ativo = AtivoEnum.ATIVO;
    }
    
    public PAOcorrenciaStatus(Long id, 
                              Long idProcesso, 
                              Long vrProcesso, 
                              String numeroProcesso, 
                              Integer codigoAndamento, 
                              String descricaoAndamento){

        this.id = id;
        this.processoAdministrativo = new ProcessoAdministrativo(idProcesso, numeroProcesso, vrProcesso);
        this.statusAndamento = new PAStatusAndamento();
        this.statusAndamento.setAndamentoProcesso(new PAAndamentoProcesso());
        this.statusAndamento.getAndamentoProcesso().setCodigo(codigoAndamento);
        this.statusAndamento.getAndamentoProcesso().setDescricao(descricaoAndamento);
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

    public PAStatusAndamento getStatusAndamento() {
        return statusAndamento;
    }

    public void setStatusAndamento(PAStatusAndamento statusAndamento) {
        this.statusAndamento = statusAndamento;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    @XmlElement(name = "dataInicio")
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }
    
    @XmlElement(name = "dataTermino")
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public SituacaoOcorrenciaEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoOcorrenciaEnum situacao) {
        this.situacao = situacao;
    }

    public PAFluxoProcesso getFluxoProcesso() {
        return fluxoProcesso;
    }

    public void setFluxoProcesso(PAFluxoProcesso fluxoProcesso) {
        this.fluxoProcesso = fluxoProcesso;
    }
}