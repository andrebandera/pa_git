package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;

public class AjustaProcessoAdministrativoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    private String numeroProcesso;

    public AjustaProcessoAdministrativoCriteria() {
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }
    
}