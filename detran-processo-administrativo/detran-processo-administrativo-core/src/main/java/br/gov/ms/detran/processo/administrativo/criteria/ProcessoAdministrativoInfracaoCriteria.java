package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;

/**
 * @author yanko.campos
 */
@CriteriaQuery(query = "SELECT p FROM ProcessoAdministrativoInfracao p ", selectCount = "SELECT COUNT(p.id) ")
public class ProcessoAdministrativoInfracaoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(name = "p.id", id = "id")
    private Long id;
    
    @Argument(name = "p.processoAdministrativo", id = "processoAdministrativo")
    private ProcessoAdministrativo processoAdministrativo;
    
    private String numeroProcesso;

    public ProcessoAdministrativoInfracaoCriteria() {
        this.sort.addSortItem("p.id");
    }
    
    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
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

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }
}