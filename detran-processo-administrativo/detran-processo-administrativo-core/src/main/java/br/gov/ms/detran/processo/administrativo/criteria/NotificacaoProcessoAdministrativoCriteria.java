package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.processo.administrativo.entidade.LoteNotificacaoPA;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;

@CriteriaQuery(query = "SELECT a FROM NotificacaoProcessoAdministrativo a ", selectCount = "SELECT COUNT(a.id) ")
public class NotificacaoProcessoAdministrativoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(name = "a.id", id = "id")
    private Long id;
    
    @Argument(name = "a.processoAdministrativo", id = "processoAdministrativo")
    private ProcessoAdministrativo processoAdministrativo;

    @Argument(name = "a.lote", id = "lote")
    private LoteNotificacaoPA loteNotificacao;

    @Argument(name = "a.objetoCorreio", id = "objetoCorreio")
    private String objetoCorreios;

    public NotificacaoProcessoAdministrativoCriteria() {
        this.sort.addSortItem("a.id", false);
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

    public String getObjetoCorreios() {
        return objetoCorreios;
    }

    public void setObjetoCorreios(String objetoCorreios) {
        this.objetoCorreios = objetoCorreios;
    }

    public LoteNotificacaoPA getLoteNotificacao() {
        return loteNotificacao;
    }

    public void setLoteNotificacao(LoteNotificacaoPA loteNotificacao) {
        this.loteNotificacao = loteNotificacao;
    }
}