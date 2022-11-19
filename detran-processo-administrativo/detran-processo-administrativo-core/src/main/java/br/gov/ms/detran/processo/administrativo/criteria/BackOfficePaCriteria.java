package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.adapter.CPFAdapter;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.comum.util.querybuilder.Operand;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.PassoRecursoOnlinePAEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoSituacaoPAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

@CriteriaQuery(
        query = "SELECT tem FROM RecursoPAOnline tem ",
        selectCount = "SELECT COUNT(tem.id)"
)
public class BackOfficePaCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {

    @Argument(name = "tem.passo", id = "passo")
    private PassoRecursoOnlinePAEnum passo;

    @Argument(name = "tem.situacao", id = "situacao")
    private RecursoSituacaoPAEnum situacao;

    @Argument(name = "tem.destino", id = "destinoRecurso")
    private OrigemDestinoEnum destinoRecurso;

    @Argument(name = "tem.cpf", id = "cpf")
    private String cpf;

    @Argument(name = "CONVERT(date, tem.dataRecurso)", id = "dataInicio", operand = Operand.GREATER_EQUAL)
    private Date dataInicio;

    @Argument(name = "CONVERT(date, tem.dataRecurso)", id = "dataFim", operand = Operand.LESSER_EQUAL)
    private Date dataFim;

    @Argument(name = "tem.protocolo", id = "protocolo")
    private String protocolo;

    @Argument(name = "tem.numeroProcesso", id = "numeroProcesso")
    private String numeroProcesso;

    @Argument(name = "tem.tipo", id = "tipoRecurso")
    private TipoFasePaEnum tipoRecurso;

    @Argument(name = "tem.ativo", id = "ativo")
    private AtivoEnum ativo = AtivoEnum.ATIVO;
    
    @Argument(name = "tem.destino", id = "destino", operand = Operand.NOT_IN)
    private List<OrigemDestinoEnum> listaDestinoExcluir;

    public BackOfficePaCriteria() {
        this.sort.addSortItem("tem.dataRecurso");
    }

    public PassoRecursoOnlinePAEnum getPasso() {
        return passo;
    }

    public void setPasso(PassoRecursoOnlinePAEnum passo) {
        this.passo = passo;
    }

    public RecursoSituacaoPAEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(RecursoSituacaoPAEnum situacao) {
        this.situacao = situacao;
    }

    public String getCpf() {
        return cpf;
    }

    @XmlJavaTypeAdapter(CPFAdapter.class)
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    @XmlElement(name = "dataInicio")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    @XmlElement(name = "dataFim")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public TipoFasePaEnum getTipoRecurso() {
        return tipoRecurso;
    }

    public void setTipoRecurso(TipoFasePaEnum tipoRecurso) {
        this.tipoRecurso = tipoRecurso;
    }

    public OrigemDestinoEnum getDestinoRecurso() {
        return destinoRecurso;
    }

    public void setDestinoRecurso(OrigemDestinoEnum destinoRecurso) {
        this.destinoRecurso = destinoRecurso;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    @XmlElement(name = "numeroProcesso")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public List<OrigemDestinoEnum> getListaDestinoExcluir() {
        return listaDestinoExcluir;
    }

    public void setListaDestinoExcluir(List<OrigemDestinoEnum> listaDestinoExcluir) {
        this.listaDestinoExcluir = listaDestinoExcluir;
    }
}