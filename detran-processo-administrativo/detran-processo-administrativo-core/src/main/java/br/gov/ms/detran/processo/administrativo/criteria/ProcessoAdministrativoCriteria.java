package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author yanko.campos
 */
@CriteriaQuery(
        query = "SELECT new br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper( "
                + "     pa, "
                + "     ap.codigo, "
                + "     ap.descricao, "
                + "     tck.descricao, "
                + "     tde.situacao, "
                + "     tea"
                + " ) "
                + "FROM PAOcorrenciaStatus tde "
                + "INNER JOIN tde.processoAdministrativo pa "
                + "INNER JOIN tde.statusAndamento sa "
                + "INNER JOIN sa.andamentoProcesso ap "
                + "INNER JOIN tde.fluxoProcesso tck "
                + "LEFT JOIN PAPenalidadeProcesso tea ON pa.id = tea.processoAdministrativo.id AND tea.ativo = 1 "
                + " INNER JOIN pa.origemApoio tdh ", 
        
        selectCount = "SELECT COUNT(pa.id) From PAOcorrenciaStatus tde "
                + "INNER JOIN tde.processoAdministrativo pa "
                + "INNER JOIN tde.statusAndamento sa "
                + "INNER JOIN sa.andamentoProcesso ap "
                + "INNER JOIN tde.fluxoProcesso tck "
                + "LEFT JOIN PAPenalidadeProcesso tea ON pa.id = tea.processoAdministrativo.id AND tea.ativo = 1 "
                + "INNER JOIN pa.origemApoio tdh ")
public class ProcessoAdministrativoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(name = "pa.id", id = "id")
    private Long id;

    @Argument(name = "pa.cpf", id = "cpf")
    private String cpf;

    @Argument(name = "pa.cnh", id = "cnh")
    private Long cnh;

    @Argument(name = "pa.tipo", id = "tipo")
    private TipoProcessoEnum tipo;

    @Argument(name = "pa.numeroDetran", id = "numeroDetran")
    private Long numeroDetran;

    @Argument(name = "pa.numeroProcesso", id = "numeroProcesso")
    private String numeroProcesso;
    
    @Argument(name = "pa.numeroPortaria", id = "numeroPortaria")
    private String numeroPortaria;
    
    @Argument(name = "ap", id = "andamento")
    private PAAndamentoProcesso andamento;

    @Argument(name = "pa.origemInformacao", id = "origemInformacao")
    private String origemInformacao;
    
    @Argument(name = "convert(date, pa.dataProcessamento)", id = "dataProcessamento")
    private Date dataProcessamento;
    
    @Argument(name = "tdh", id = "cenario")
    private ApoioOrigemInstauracao cenario;
    
    @Argument(name = "pa.ativo", id = "ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;
    
    @Argument(name = "pa.origem", id = "origem")
    @Enumerated(EnumType.STRING)
    private OrigemEnum origem;
    
    private Integer totalRegistros;

    public ProcessoAdministrativoCriteria() {
        this.sort.addSortItem("pa.cpf");
    }

    public String getCpf() {
        return cpf;
    }

    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Long getCnh() {
        return cnh;
    }

    public void setCnh(Long cnh) {
        this.cnh = cnh;
    }

    public TipoProcessoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoProcessoEnum tipo) {
        this.tipo = tipo;
    }

    public Long getNumeroDetran() {
        return numeroDetran;
    }

    public void setNumeroDetran(Long numeroDetran) {
        this.numeroDetran = numeroDetran;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    @XmlElement(name = "numeroProcesso")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroPortaria() {
        return numeroPortaria;
    }

    @XmlElement(name = "numeroPortaria")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public void setNumeroPortaria(String numeroPortaria) {
        this.numeroPortaria = numeroPortaria;
    }

    public String getOrigemInformacao() {
        return origemInformacao;
    }

    public void setOrigemInformacao(String origemInformacao) {
        this.origemInformacao = origemInformacao;
    }

    public Date getDataProcessamento() {
        return dataProcessamento;
    }

    @XmlElement(name = "dataProcessamento")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataProcessamento(Date dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PAAndamentoProcesso getAndamento() {
        return andamento;
    }

    public void setAndamento(PAAndamentoProcesso andamento) {
        this.andamento = andamento;
    }

    @Override
    public Boolean isCriteriaEmpty() {
        return DetranStringUtil.ehBrancoOuNulo(cpf)
                && cnh == null
                && DetranStringUtil.ehBrancoOuNulo(numeroPortaria)
                && DetranStringUtil.ehBrancoOuNulo(numeroProcesso)
                && tipo == null
                && numeroDetran == null
                && dataProcessamento == null
                && andamento == null
                && id == null
                && cenario == null
                && origem == null
                ;
    }

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public ApoioOrigemInstauracao getCenario() {
        return cenario;
    }

    public void setCenario(ApoioOrigemInstauracao cenario) {
        this.cenario = cenario;
    }

    public OrigemEnum getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemEnum origem) {
        this.origem = origem;
    }
}