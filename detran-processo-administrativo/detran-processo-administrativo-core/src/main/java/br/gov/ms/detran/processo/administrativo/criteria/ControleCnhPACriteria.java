package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.PostoAtendimento;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.adapter.CPFAdapter;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.ValidadeCnhEnum;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Lillydi
 */
@CriteriaQuery(query = "SELECT m FROM MovimentoCnh m ", selectCount = "SELECT COUNT(m.id) ")
public class ControleCnhPACriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder{

    @Argument(name = "m.id", id = "id")
    private Long id;

    @Argument(name = "m.protocolo.numeroProcesso.numeroProcesso", id = "numeroProcesso")
    private String numeroProcesso;
    
    @Argument(name = "m.protocolo.numeroProcesso", id = "processoAdministrativo")
    private ProcessoAdministrativo processoAdministrativo;
    
    @Argument(name = "m.protocolo.numeroProcesso.cpf", id = "cpf")
    private String cpfEntrega;

    private String nomeEntrega;

    @Argument(name = "m.protocolo.numeroProcesso.numeroRegistro", id = "numeroRegistro")
    private String numeroRegistro;
    
    private List<Long> controleCnhs;

    @Argument(name = "m.protocolo.numeroProcesso.cnh", id = "cnh")
    private Long numeroCnh;

    @Argument(name = "m.protocolo.postoAtendimento", id = "postoAtendimento")
    private PostoAtendimento postoAtendimento;
    
    @Argument(name = "m.acao", id = "acao")
    private AcaoEntregaCnhEnum acao;
    
    private Long controleCnhId;
    
    private ValidadeCnhEnum validadeCnh;
    
    @Argument(name = "m.ativo", id = "ativo")
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public ControleCnhPACriteria() {
        super();
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

    public String getCpfEntrega() {
        return cpfEntrega;
    }

    @XmlJavaTypeAdapter(CPFAdapter.class)
    public void setCpfEntrega(String cpfEntrega) {
        this.cpfEntrega = cpfEntrega;
    }

    public String getNomeEntrega() {
        return nomeEntrega;
    }

    public void setNomeEntrega(String nomeEntrega) {
        this.nomeEntrega = nomeEntrega;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public List<Long> getControleCnhs() {
        return controleCnhs;
    }

    public void setControleCnhs(List<Long> controleCnhs) {
        this.controleCnhs = controleCnhs;
    }

    public Long getNumeroCnh() {
        return numeroCnh;
    }

    public void setNumeroCnh(Long numeroCnh) {
        this.numeroCnh = numeroCnh;
    }

    public PostoAtendimento getPostoAtendimento() {
        return postoAtendimento;
    }

    public void setPostoAtendimento(PostoAtendimento postoAtendimento) {
        this.postoAtendimento = postoAtendimento;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public Long getControleCnhId() {
        return controleCnhId;
    }

    public void setControleCnhId(Long controleCnhId) {
        this.controleCnhId = controleCnhId;
    }

    public AcaoEntregaCnhEnum getAcao() {
        return acao;
    }

    public void setAcao(AcaoEntregaCnhEnum acao) {
        this.acao = acao;
    }

    public ValidadeCnhEnum getValidadeCnh() {
        return validadeCnh;
    }

    public void setValidadeCnh(ValidadeCnhEnum validadeCnh) {
        this.validadeCnh = validadeCnh;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }
}