package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.UnidadePenalEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.IdentificacaoRecolhimentoCnhEnum;
import br.gov.ms.detran.processo.administrativo.enums.ParteProcessoJuridicoEnum;
import br.gov.ms.detran.processo.administrativo.enums.RequisitoRecursoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoMedidaEnum;
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
 * @author yanko.campos
 */
@Entity
@Table(name = "TB_TEK_DADO_PROCESSO_JUDICIAL")
@NamedQueries({
    @NamedQuery(
            name = "DadoProcessoJudicial.getAll",
            query = "SELECT tek FROM DadoProcessoJudicial tek "),
    @NamedQuery(name = "DadoProcessoJudicial.getDadoProcessoJudicialPorPA",
            query = "Select tek "
                    + " From DadoProcessoJudicial tek "
                    + "     INNER JOIN tek.processoJudicial tej "
                    + "     INNER JOIN tej.processoAdministrativo tdc "
                    + " where tdc.id = :p0 "
                    + "     and tej.ativo = :p1 "
                    + "     and tek.ativo = :p1 "),
    @NamedQuery(
        name = "DadoProcessoJudicial.getListaProcessoJuridicoCandidatoCidadaoPorAndamento",
        query = "SELECT tdc "
                + " FROM DadoProcessoJudicial tek "
                + "     INNER JOIN tek.processoJudicial tej "
                + "     INNER JOIN tej.processoAdministrativo tdc "
                + " WHERE tdc.ativo = :p0 "
                + "     AND tek.parteProcessoJuridico <> :p2 "
                + "     AND EXISTS(SELECT 1 "
                + "                   FROM PAOcorrenciaStatus tde "
                + "                     INNER JOIN tde.statusAndamento tcd "
                + "                     INNER JOIN tcd.andamentoProcesso tcb "
                + "                   WHERE tde.processoAdministrativo.id = tdc.id "
                + "                     AND tcb.codigo in (:p1) "
                + "                     AND tcb.ativo = :p0 "
                + "                     AND tde.ativo = :p0 "
                + "                     AND tcd.ativo = :p0 ) "
    ),
    @NamedQuery(
        name = "DadoProcessoJudicial.getListaProcessoJuridicoAndamentoERecolhimentoCnh",
        query = "SELECT tdc "
                + " FROM DadoProcessoJudicial tek "
                + "     INNER JOIN tek.processoJudicial tej "
                + "     INNER JOIN tej.processoAdministrativo tdc "
                + " WHERE tdc.ativo = :p0 "
                + "     AND tek.identificacaoRecolhimentoCnh = :p2 "
                + "     AND EXISTS(SELECT 1 "
                + "                   FROM PAOcorrenciaStatus tde "
                + "                     INNER JOIN tde.statusAndamento tcd "
                + "                     INNER JOIN tcd.andamentoProcesso tcb "
                + "                   WHERE tde.processoAdministrativo.id = tdc.id "
                + "                     AND tcb.codigo in (:p1) "
                + "                     AND tcb.ativo = :p0 "
                + "                     AND tde.ativo = :p0 "
                + "                     AND tcd.ativo = :p0 ) "
    ),
    @NamedQuery(
        name = "DadoProcessoJudicial.getPJUComCnhEmCartorioPorCpf",
        query = "SELECT tek "
                + " From DadoProcessoJudicial tek "
                + "     INNER JOIN tek.processoJudicial tej "
                + "     INNER JOIN tej.processoAdministrativo tdc "
                + " where tdc.cpf = :p0 "
                + "     and tek.identificacaoRecolhimentoCnh = :p1 "
                + "     and tdc.ativo = :p2 "
                + "     and tek.ativo = :p2"
                + " AND EXISTS(SELECT 1 "
                + "                   FROM PAOcorrenciaStatus tde "
                + "                     INNER JOIN tde.statusAndamento tcd "
                + "                     INNER JOIN tcd.status tca "
                + "                   WHERE tde.processoAdministrativo.id = tdc.id "
                + "                     AND tca.codigo in (:p3) "
                + "                     AND tca.ativo = :p2 "
                + "                     AND tde.ativo = :p2 "
                + "                     AND tcd.ativo = :p2 )"
    ),
    @NamedQuery(name = "DadoProcessoJudicial.getProcessosJuridicosParaEntregaCartorioPorCPF",
            query = "SELECT tdc "
                    + " From DadoProcessoJudicial tek "
                    + "     INNER JOIN tek.processoJudicial tej "
                    + "     INNER JOIN tej.processoAdministrativo tdc "
                    + " where tdc.cpf = :p0 "
                    + "     and tek.parteProcessoJuridico = :p1 "
                    + "     and tek.identificacaoRecolhimentoCnh = :p2 "
                    + "     and tek.ativo = :p3 "
                    + "     and tej.ativo = :p3 "
                    + "     and tdc.ativo = :p3")
})
public class DadoProcessoJudicial extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tek_ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tek_Processo_Judicial", referencedColumnName = "Tej_ID")
    private ProcessoJudicial processoJudicial;
    
    @Column(name = "Tek_Valor_Autos")
    private String valorAutos;
    
    @Column(name = "Tek_Observacao")
    private String observacao;
    
    @Column(name = "Tek_Parte")
    @Enumerated(EnumType.STRING)
    private ParteProcessoJuridicoEnum parteProcessoJuridico;
    
    @Column(name = "Tek_Formulario_Renach")
    private String formularioRenach;
    
    @Column(name = "Tek_Orgao_Judicial")
    private Long orgaoJudicial;
    
    @Column(name = "Tek_Identificacao_Delito")
    private BooleanEnum identificacaoDelito;
    
    @Column(name = "Tek_Tipo_Documento")
    private Long tipoDocumento;
    
    @Column(name = "Tek_Tipo_Medida")
    @Enumerated(EnumType.STRING)
    private TipoMedidaEnum tipoMedida;
    
    @Column(name = "Tek_Identificacao_Recolhimento_CNH")
    @Enumerated(EnumType.STRING)
    private IdentificacaoRecolhimentoCnhEnum identificacaoRecolhimentoCnh;
    
    @Column(name = "Tek_Requisito_Curso_Bloqueio")
    @Enumerated(EnumType.STRING)
    private RequisitoRecursoBloqueioEnum requisitoCursoBloqueio;
    
    @Column(name = "Tek_Prazo_Penalidade")
    private Integer prazoPenalidade;
    
    @Column(name = "Tek_Usuario_Cadastro")
    private Long usuarioCadastro;
    
    @Column(name = "Tek_Data_Cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;
    
    @Column(name = "Tek_Unidade_Penal")
    @Enumerated(EnumType.STRING)
    private UnidadePenalEnum unidadePenal;
    
    @Column(name = "Tek_Valor_Documento")
    private String valorDocumento;
    
    @Column(name = "Tek_Orgao_BCA")
    private Long orgaoBca;
    
    @Column(name = "Tek_Data_Entrega_CNH")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEntrega;
    
    @Column(name = "Tek_Data_Bloqueio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataBloqueio;
    
    @Column(name = "Tek_Indicativo_Prazo_Indeterminado")
    private BooleanEnum indicativoPrazoIndeterminado;    
    
    @Column(name = "Tek_Data_Inicio_Penalidade")
    @Temporal(TemporalType.DATE)
    private Date dataInicioPenalidade;
    
    @Column(name = "Tek_Segredo_Justica")
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum segredoJustica;
    
    @Column(name = "Tek_Prova")
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum indicativoProva;
    
    public DadoProcessoJudicial() {
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

    public ProcessoJudicial getProcessoJudicial() {
        return processoJudicial;
    }

    public void setProcessoJudicial(ProcessoJudicial processoJudicial) {
        this.processoJudicial = processoJudicial;
    }

    public String getValorAutos() {
        return valorAutos;
    }

    public void setValorAutos(String valorAutos) {
        this.valorAutos = valorAutos;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public ParteProcessoJuridicoEnum getParteProcessoJuridico() {
        return parteProcessoJuridico;
    }

    public void setParteProcessoJuridico(ParteProcessoJuridicoEnum parteProcessoJuridico) {
        this.parteProcessoJuridico = parteProcessoJuridico;
    }

    public String getFormularioRenach() {
        return formularioRenach;
    }

    public void setFormularioRenach(String formularioRenach) {
        this.formularioRenach = formularioRenach;
    }

    public Long getOrgaoJudicial() {
        return orgaoJudicial;
    }

    public void setOrgaoJudicial(Long orgaoJudicial) {
        this.orgaoJudicial = orgaoJudicial;
    }

    public BooleanEnum getIdentificacaoDelito() {
        return identificacaoDelito;
    }

    public void setIdentificacaoDelito(BooleanEnum identificacaoDelito) {
        this.identificacaoDelito = identificacaoDelito;
    }

    public Long getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(Long tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public TipoMedidaEnum getTipoMedida() {
        return tipoMedida;
    }

    public void setTipoMedida(TipoMedidaEnum tipoMedida) {
        this.tipoMedida = tipoMedida;
    }

    public IdentificacaoRecolhimentoCnhEnum getIdentificacaoRecolhimentoCnh() {
        return identificacaoRecolhimentoCnh;
    }

    public void setIdentificacaoRecolhimentoCnh(IdentificacaoRecolhimentoCnhEnum identificacaoRecolhimentoCnh) {
        this.identificacaoRecolhimentoCnh = identificacaoRecolhimentoCnh;
    }

    public RequisitoRecursoBloqueioEnum getRequisitoCursoBloqueio() {
        return requisitoCursoBloqueio;
    }

    public void setRequisitoCursoBloqueio(RequisitoRecursoBloqueioEnum requisitoCursoBloqueio) {
        this.requisitoCursoBloqueio = requisitoCursoBloqueio;
    }

    public Integer getPrazoPenalidade() {
        return prazoPenalidade;
    }

    public void setPrazoPenalidade(Integer prazoPenalidade) {
        this.prazoPenalidade = prazoPenalidade;
    }

    public Long getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(Long usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
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

    public UnidadePenalEnum getUnidadePenal() {
        return unidadePenal;
    }

    public void setUnidadePenal(UnidadePenalEnum unidadePenal) {
        this.unidadePenal = unidadePenal;
    }

    public String getValorDocumento() {
        return valorDocumento;
    }

    public void setValorDocumento(String valorDocumento) {
        this.valorDocumento = valorDocumento;
    }

    public Long getOrgaoBca() {
        return orgaoBca;
    }

    public void setOrgaoBca(Long orgaoBca) {
        this.orgaoBca = orgaoBca;
    }

    public Date getDataEntrega() {
        return dataEntrega;
    }

    @XmlElement(name = "dataEntrega")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataEntrega(Date dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    @XmlElement(name = "dataBloqueio")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public BooleanEnum getIndicativoPrazoIndeterminado() {
        return indicativoPrazoIndeterminado;
    }

    public void setIndicativoPrazoIndeterminado(BooleanEnum indicativoPrazoIndeterminado) {
        this.indicativoPrazoIndeterminado = indicativoPrazoIndeterminado;
    }

    public Date getDataInicioPenalidade() {
        return dataInicioPenalidade;
    }
    
    @XmlElement(name = "dataInicioPenalidade")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataInicioPenalidade(Date dataInicioPenalidade) {
        this.dataInicioPenalidade = dataInicioPenalidade;
    }

    public BooleanEnum getSegredoJustica() {
        return segredoJustica;
    }

    public void setSegredoJustica(BooleanEnum segredoJustica) {
        this.segredoJustica = segredoJustica;
    }

    public BooleanEnum getIndicativoProva() {
        return indicativoProva;
    }

    public void setIndicativoProva(BooleanEnum indicativoProva) {
        this.indicativoProva = indicativoProva;
    }
}