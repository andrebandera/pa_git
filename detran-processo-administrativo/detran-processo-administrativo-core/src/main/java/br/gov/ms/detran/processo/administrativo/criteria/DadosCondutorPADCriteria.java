package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;

@CriteriaQuery(query = "SELECT dc FROM DadosCondutorPAD dc ", selectCount = "SELECT COUNT(dc.id) ")
public class DadosCondutorPADCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {

    private String usuarioInclusao;

    public DadosCondutorPADCriteria() {
    }
    
    public DadosCondutorPADCriteria(Integer from, Integer to) {
        super.setFrom(from);
        super.setTo(to);
    }

    public String getUsuarioInclusao() {
        return usuarioInclusao;
    }

    public void setUsuarioInclusao(String usuarioInclusao) {
        this.usuarioInclusao = usuarioInclusao;
    }
}