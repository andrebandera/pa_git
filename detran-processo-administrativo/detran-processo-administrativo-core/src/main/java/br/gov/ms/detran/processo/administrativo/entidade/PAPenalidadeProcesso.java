package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.UnidadePenalEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
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

/**
 * @author Lillydi
 */
@Entity
@Table(name = "TB_TEA_PAD_PENALIDADE_PROCESSO_PAT")
@NamedQueries({
    @NamedQuery(
        name = "PAPenalidadeProcesso.getTotalPenalidadeAtivaPorCpf",
        query = " SELECT sum((CASE "
                + "             WHEN tea.unidadePenal = 'DIA' then tea.valor "
                + "             WHEN tea.unidadePenal = 'MES' then (tea.valor * 30) "
                + "             ELSE 0 "
                + "           END) / 30) AS valorPenalidade "
                + " FROM PAPenalidadeProcesso tea "
                + "     INNER JOIN tea.processoAdministrativo tdc "
                + " WHERE tdc.cpf = :p0 "
                + "     AND tea.ativo = :p1 "
                + "     AND tdc.ativo = :p1 "
    ),
    @NamedQuery(
            name = "PAPenalidadeProcesso.getPAPenalidadeProcessoMaiorDataFimPorCpfCondutor",
            query = "SELECT new PAPenalidadeProcesso(tea.id,"
                    + "                              tea.dataFimPenalidade, "
                    + "                              tea.versaoRegistro) "
                    + "FROM PAPenalidadeProcesso tea "
                    + "INNER JOIN tea.processoAdministrativo tdc "
                    + "WHERE tdc.cpf = :p0 "
                    + "AND tea.ativo = :p1 "
                    + "AND tdc.ativo = :p1 "
                    + "ORDER BY tea.dataFimPenalidade DESC "),
    @NamedQuery(
            name = "PAPenalidadeProcesso.getPenalidadePorProcessoAdministrativo",
            query = "SELECT tea "
                    + "FROM PAPenalidadeProcesso tea "
                    + "INNER JOIN tea.processoAdministrativo tdc "
                    + "where tdc.id = :p0 "
                    + "AND tea.ativo = :p1"),
    @NamedQuery(
            name = "PAPenalidadeProcesso.getUltimaPenalidadePorControleCnh",
            query = "SELECT tea "
                    + " From PAPenalidadeProcesso tea "
                    + " INNER JOIN tea.processoAdministrativo tdc "
                    + " where EXISTS ("
                    + "             SELECT 1 "
                    + "             FROM MovimentoCnh tdn "
                    + "                 INNER JOIN tdn.protocolo tdm "
                    + "                 INNER JOIN tdm.numeroProcesso tdc2 "
                    + "             where tdc2.id = tdc.id "
                    + "                 and tdn.cnhControle = :p0 "
                    + "                 and tdn.ativo = :p2 "
                    + "                 and tdn.acao = :p1 ) "
                    + "         and tea.ativo = :p2 "
                    + " ORDER BY tea.dataFimPenalidade DESC "),
    @NamedQuery(
            name = "PAPenalidadeProcesso.getUltimaPenalidadePorCPF",
            query = "SELECT tea "
                    + " From PAPenalidadeProcesso tea "
                    + " INNER JOIN tea.processoAdministrativo tdc "
                    + " where tdc.cpf = :p0 "
                    + "         and tea.ativo = :p1 "
                    + " ORDER BY tea.dataFimPenalidade DESC ")
})
public class PAPenalidadeProcesso extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tea_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Tea_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;

    @Column(name = "Tea_Usuario")
    private Long usuario;

    @Column(name = "Tea_Data_Inicio_Penalidade")
    @Temporal(TemporalType.DATE)
    private Date dataInicioPenalidade;

    @Column(name = "Tea_Data_Fim_Penalidade")
    @Temporal(TemporalType.DATE)
    private Date dataFimPenalidade;

    @Column(name = "Tea_Data_Cadastro")
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    
    @Column(name = "Tea_Valor")
    private Integer valor;
    
    @Column(name = "Tea_Unidade_Penal")
    @Enumerated(EnumType.STRING)
    private UnidadePenalEnum unidadePenal;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public PAPenalidadeProcesso() {
    }
    
    public PAPenalidadeProcesso(Long id, Date dataFimPenalidade, Long versaoRegistro) {
        this.id = id;
        this.dataFimPenalidade = dataFimPenalidade;
        super.setVersaoRegistro(versaoRegistro);
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

    public Long getUsuario() {
        return usuario;
    }

    public void setUsuario(Long usuario) {
        this.usuario = usuario;
    }

    public Date getDataInicioPenalidade() {
        return dataInicioPenalidade;
    }

    @XmlElement(name = "dataInicioPenalidade")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataInicioPenalidade(Date dataInicioPenalidade) {
        this.dataInicioPenalidade = dataInicioPenalidade;
    }

    public Date getDataFimPenalidade() {
        return dataFimPenalidade;
    }

    @XmlElement(name = "dataFimPenalidade")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataFimPenalidade(Date dataFimPenalidade) {
        this.dataFimPenalidade = dataFimPenalidade;
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

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public UnidadePenalEnum getUnidadePenal() {
        return unidadePenal;
    }

    public void setUnidadePenal(UnidadePenalEnum unidadePenal) {
        this.unidadePenal = unidadePenal;
    }
}