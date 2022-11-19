package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;

public class UsuarioAcessoProcessoAdministrativoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    private Long processoAdministrativoId;
    
    private String cpfCondutor;
    
    private String funcionalidadeURL;

    public UsuarioAcessoProcessoAdministrativoCriteria() {
    }
    
    public UsuarioAcessoProcessoAdministrativoCriteria(String cpf) {
        this.cpfCondutor = cpf;
    }

    public Long getProcessoAdministrativoId() {
        return processoAdministrativoId;
    }

    public void setProcessoAdministrativoId(Long processoAdministrativoId) {
        this.processoAdministrativoId = processoAdministrativoId;
    }

    public String getCpfCondutor() {
        return cpfCondutor;
    }

    public void setCpfCondutor(String cpfCondutor) {
        this.cpfCondutor = cpfCondutor;
    }

    public String getFuncionalidadeURL() {
        return funcionalidadeURL;
    }

    public void setFuncionalidadeURL(String funcionalidadeURL) {
        this.funcionalidadeURL = funcionalidadeURL;
    }
}