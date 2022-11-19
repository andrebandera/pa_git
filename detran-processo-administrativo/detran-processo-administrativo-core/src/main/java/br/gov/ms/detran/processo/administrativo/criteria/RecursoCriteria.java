package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.ResultadoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@CriteriaQuery(query = "SELECT r FROM Recurso r ", selectCount = "SELECT COUNT(r.id) ")
public class RecursoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(name = "r.id", id = "id")
    private Long id;
    
    @Argument(name = "r.processoAdministrativo", id = "processoAdministrativo")
    private ProcessoAdministrativo processoAdministrativo;
    
    private String cpf;
    
    private String numeroProcesso;
    
    private SituacaoRecursoEnum situacao;
    
    private TipoFasePaEnum tipo;
    
    private ResultadoRecursoEnum resultado;

    private  String usuarioResultado;

    private Date dataInicioResultado;

    private Date dataFimResultado;

    private Boolean recursoComResultado;
    
    private Date dataInicioProtolo;
    
    private Date dataFimProtocolo;
    
    private OrigemDestinoEnum destino;
    
    private AtivoEnum ativo;
    
    public RecursoCriteria() {
        this.sort.addSortItem("r.situacao");
//        this.sort.addSortItem("r.cpf");
    }

    public SituacaoRecursoEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoRecursoEnum situacao) {
        this.situacao = situacao;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    @XmlElement(name = "numeroProcesso")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoFasePaEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoFasePaEnum tipo) {
        this.tipo = tipo;
    }

    public ResultadoRecursoEnum getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoRecursoEnum resultado) {
        this.resultado = resultado;
    }

    @Override
    public Boolean isCriteriaEmpty() {
        return DetranStringUtil.ehBrancoOuNulo(cpf)
                && DetranStringUtil.ehBrancoOuNulo(numeroProcesso)
                && DetranStringUtil.ehBrancoOuNulo(usuarioResultado)
                && situacao == null
                && dataInicioResultado == null
                && dataFimResultado == null
                && tipo == null
                && resultado == null
                && dataInicioProtolo == null
                && dataFimProtocolo == null
                && destino == null;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public String getUsuarioResultado() {
        return usuarioResultado;
    }

    public void setUsuarioResultado(String usuarioResultado) {
        this.usuarioResultado = usuarioResultado;
    }

    public Date getDataInicioResultado() {
        return dataInicioResultado;
    }

    @XmlElement(name = "dataInicioResultado")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataInicioResultado(Date dataInicioResultado) {
        this.dataInicioResultado = dataInicioResultado;
    }

    public Date getDataFimResultado() {
        return dataFimResultado;
    }

    @XmlElement(name = "dataFimResultado")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataFimResultado(Date dataFimResultado) {
        this.dataFimResultado = dataFimResultado;
    }

    public Boolean getRecursoComResultado() {
        return (resultado != null
                || dataFimResultado != null
                || dataInicioResultado != null
                || !DetranStringUtil.ehBrancoOuNulo(usuarioResultado)) ? true : null;
    }

    public void setRecursoComResultado(Boolean recursoComResultado) {
        this.recursoComResultado = recursoComResultado;
    }

    public Date getDataInicioProtolo() {
        return dataInicioProtolo;
    }

    @XmlElement(name = "dataInicioProtocolo")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataInicioProtolo(Date dataInicioProtolo) {
        this.dataInicioProtolo = dataInicioProtolo;
    }

    public Date getDataFimProtocolo() {
        return dataFimProtocolo;
    }
    
    @XmlElement(name = "dataFimProtocolo")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataFimProtocolo(Date dataFimProtocolo) {
        this.dataFimProtocolo = dataFimProtocolo;
    }

    public OrigemDestinoEnum getDestino() {
        return destino;
    }

    public void setDestino(OrigemDestinoEnum destino) {
        this.destino = destino;
    }
}