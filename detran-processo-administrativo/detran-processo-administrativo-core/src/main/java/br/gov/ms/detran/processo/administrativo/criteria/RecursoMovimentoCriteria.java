package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;

@CriteriaQuery(query = "SELECT p FROM RecursoMovimento p ", selectCount = "SELECT COUNT(p.id) ")
public class RecursoMovimentoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(name = "p.id", id = "id")
    private Long id;
    
    @Argument(name = "p.recurso.processoAdministrativo", id = "processoAdministrativo")
    private ProcessoAdministrativo processoAdministrativo;
    
    @Argument(name = "p.ativo", id = "ativo")
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public RecursoMovimentoCriteria() {
        this.sort.addSortItem("p.id", Boolean.FALSE);
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

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }
}