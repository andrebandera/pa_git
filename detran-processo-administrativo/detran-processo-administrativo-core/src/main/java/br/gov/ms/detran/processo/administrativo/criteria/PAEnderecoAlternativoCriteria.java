package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Lillydi
 */
@CriteriaQuery(query = "SELECT m FROM PAEnderecoAlternativo m ", selectCount = "SELECT COUNT(m.id) ")
public class PAEnderecoAlternativoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder{

    @Argument(name = "m.id", id = "id")
    private Long id;

    @Argument(name = "m.processoAdministrativo.numeroProcesso", id = "numeroProcesso")
    private String numeroProcesso;
    
    @Argument(name = "m.processoAdministrativo.cpf", id = "cpf")
    private String cpf;

    public PAEnderecoAlternativoCriteria() {
        this.sort.addSortItem("m.processoAdministrativo.numeroProcesso", Boolean.TRUE);
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}