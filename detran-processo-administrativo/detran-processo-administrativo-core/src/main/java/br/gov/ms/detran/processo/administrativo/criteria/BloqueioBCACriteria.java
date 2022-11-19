package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;

@CriteriaQuery(query = "SELECT r FROM BloqueioBCA r ", selectCount = "SELECT COUNT(r.id) ")
public class BloqueioBCACriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(name = "r.id", id = "id")
    private Long id;
    
    @Argument(name = "r.processoAdministrativo", id = "processoAdministrativo")
    private ProcessoAdministrativo processoAdministrativo;
    
    public BloqueioBCACriteria() {
//        this.sort.addSortItem("r.situacao");
//        this.sort.addSortItem("r.cpf");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

}