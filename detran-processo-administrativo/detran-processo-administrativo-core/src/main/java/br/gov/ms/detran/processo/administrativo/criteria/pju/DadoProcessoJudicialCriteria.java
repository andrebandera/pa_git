package br.gov.ms.detran.processo.administrativo.criteria.pju;

import br.gov.ms.detran.comum.core.projeto.entidade.vei.Tribunal;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@CriteriaQuery(
        query = "SELECT tek From DadoProcessoJudicial tek " 
                    + "     INNER JOIN tek.processoJudicial tej "
                    + "     INNER JOIN tej.processoAdministrativo tdc ", 
        
        selectCount = "SELECT count(tek.id) From DadoProcessoJudicial tek "
                        + "     INNER JOIN tek.processoJudicial tej "
                        + "     INNER JOIN tej.processoAdministrativo tdc ")
public class DadoProcessoJudicialCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    
    @Argument(name = "tek.id", id = "id")
    private Long id;

    @Argument(name = "tdc.id", id = "idPA")
    private Long idPA;
    
    @Argument(name = "tej.isn", id = "isn")
    private Integer isn;

    @Argument(name = "tek.ativo", id = "ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;
    
    private Tribunal tribunal;
    
    private String cpf;

    public DadoProcessoJudicialCriteria() {
        this.ativo = AtivoEnum.ATIVO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPA() {
        return idPA;
    }

    public void setIdPA(Long idPA) {
        this.idPA = idPA;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public Integer getIsn() {
        return isn;
    }

    public void setIsn(Integer isn) {
        this.isn = isn;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Tribunal getTribunal() {
        return tribunal;
    }

    public void setTribunal(Tribunal tribunal) {
        this.tribunal = tribunal;
    }
}