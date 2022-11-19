package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.comum.util.querybuilder.Operand;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoAcaoEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@CriteriaQuery(query = "SELECT teb FROM Movimentacao teb ", selectCount = "SELECT COUNT(teb.id) ")
public class MovimentacaoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(name = "teb.id", id = "id")
    private Long id;
    
    @Argument(name = "teb.processoAdministrativo", id = "processoAdministrativo")
    private ProcessoAdministrativo processoAdministrativo;
    
    @Argument(name = "teb.processoAdministrativo.numeroProcesso", id = "numeroProcesso")
    private String numeroProcesso;
    
    @Argument(name = "teb.movimentacaoAcao", id = "acao")
    private MovimentacaoAcaoEnum acao;
    
    @Argument(name = "teb.dataCadastro", id = "dataCadastroInicio", operand = Operand.GREATER_EQUAL)
    private Date dataInicio;
    
    @Argument(name = "teb.dataCadastro", id = "dataCadastroFim", operand = Operand.LESSER_EQUAL)
    private Date dataFim;

    public MovimentacaoCriteria() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }
    
    @XmlElement(name = "numeroProcesso")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public MovimentacaoAcaoEnum getAcao() {
        return acao;
    }

    public void setAcao(MovimentacaoAcaoEnum acao) {
        this.acao = acao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    @XmlElement(name = "dataInicio")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataInicio(Date dataIncio) {
        this.dataInicio = dataIncio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    @XmlElement(name = "dataFim")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }
}