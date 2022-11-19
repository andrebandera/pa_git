package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.comum.util.querybuilder.Operand;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@CriteriaQuery(
        query = "SELECT distinct new LoteNotificacaoPA("
                + " l.id, "
                + " l.tipo, "
                + " l.nome, "
                + " l.dataGeracao, "
                + " l.qtdNotificacoes, "
                + " l.ativo, "
                + " l.versaoRegistro) " +
                " FROM NotificacaoProcessoAdministrativo p inner join p.lote l inner join p.processoAdministrativo tdc ",
        selectCount = "SELECT COUNT(distinct l.id) "
)
public class LoteNotificacaoPACriteria  extends DetranAbstractCriteria implements ICriteriaQueryBuilder {

    @Argument(id="id", name="l.id")
    private Long id;

    @Argument(id="dataInicio", name="l.dataGeracao", operand = Operand.GREATER_EQUAL)
    private Date dataInicio;

    @Argument(id="dataFim", name="l.dataGeracao", operand = Operand.LESSER_EQUAL)
    private Date dataFim;

    @Argument(id="nome", name="l.nome")
    private String nome;

    @Argument(id="tipo", name="l.tipo")
    private TipoFasePaEnum tipo;

    @Argument(id="objetoCorreios", name="p.objetoCorreio")
    private String objetoCorreios;
    
    @Argument(id="numeroProcesso", name="tdc.numeroProcesso")
    private String numeroProcesso;
    
    @Argument(id="numeroNotificacao", name="p.numeroNotificacao")
    private String numeroNotificacao;

    public LoteNotificacaoPACriteria() {
        this.sort.addSortItem("l.dataGeracao", false);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    @XmlElement(name = "dataInicio")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    @XmlElement(name = "dataFim")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoFasePaEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoFasePaEnum tipo) {
        this.tipo = tipo;
    }

    public String getObjetoCorreios() {
        return objetoCorreios;
    }

    public void setObjetoCorreios(String objetoCorreios) {
        this.objetoCorreios = objetoCorreios;
    }

    @Override
    public Boolean isCriteriaEmpty() {
        return this.id == null
                && this.dataFim == null
                && this.dataInicio == null
                && DetranStringUtil.ehBrancoOuNulo(this.nome)
                && DetranStringUtil.ehBrancoOuNulo(this.objetoCorreios)
                && DetranStringUtil.ehBrancoOuNulo(this.numeroProcesso)
                && DetranStringUtil.ehBrancoOuNulo(this.numeroNotificacao)
                && this.tipo == null;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    @XmlElement(name = "numeroProcesso")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroNotificacao() {
        return numeroNotificacao;
    }

    @XmlElement(name = "numeroNotificacao")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public void setNumeroNotificacao(String numeroNotificacao) {
        this.numeroNotificacao = numeroNotificacao;
    }
}