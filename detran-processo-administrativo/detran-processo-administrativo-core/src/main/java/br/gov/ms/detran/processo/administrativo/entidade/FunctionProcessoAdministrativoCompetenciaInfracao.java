package br.gov.ms.detran.processo.administrativo.entidade;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FunctionProcessoAdministrativoCompetenciaInfracao implements Serializable {
    
    @Id
    @Column(name = "id_infracao")
    private Long idInfracao;
    
    @Column(name = "competencia")
    private String codigoCompetencia;
    
    @Column(name = "competencia_descricao")
    private String competenciaDescricao;
    
    @Column(name = "codigo_orgao_autuador_base_nacional")
    private String codigoOrgaoAutuadorBaseNacional;
    
    @Column(name = "uf_orgao_autuador")
    private String ufOrgaoAutuador;
    
    @Column(name = "data_infracao")
    private String dataInfracao;
    
    @Column(name = "regra")
    private String regraInfracaoSeEnquadrou;

    public FunctionProcessoAdministrativoCompetenciaInfracao() {
    }

    public FunctionProcessoAdministrativoCompetenciaInfracao(
        Long idInfracao, String codigoCompetencia, String competenciaDescricao, String codigoOrgaoAutuadorBaseNacional, 
        String ufOrgaoAutuador, String dataInfracao, String regraInfracaoSeEnquadrou) {
        
        this.idInfracao                         = idInfracao;
        this.codigoCompetencia                  = codigoCompetencia;
        this.competenciaDescricao               = competenciaDescricao;
        this.codigoOrgaoAutuadorBaseNacional    = codigoOrgaoAutuadorBaseNacional;
        this.ufOrgaoAutuador                    = ufOrgaoAutuador;
        this.dataInfracao                       = dataInfracao;
        this.regraInfracaoSeEnquadrou           = regraInfracaoSeEnquadrou;
    }

    public Long getIdInfracao() {
        return idInfracao;
    }

    public void setIdInfracao(Long idInfracao) {
        this.idInfracao = idInfracao;
    }

    public String getCodigoCompetencia() {
        return codigoCompetencia;
    }

    public void setCodigoCompetencia(String codigoCompetencia) {
        this.codigoCompetencia = codigoCompetencia;
    }

    public String getCompetenciaDescricao() {
        return competenciaDescricao;
    }

    public void setCompetenciaDescricao(String competenciaDescricao) {
        this.competenciaDescricao = competenciaDescricao;
    }

    public String getCodigoOrgaoAutuadorBaseNacional() {
        return codigoOrgaoAutuadorBaseNacional;
    }

    public void setCodigoOrgaoAutuadorBaseNacional(String codigoOrgaoAutuadorBaseNacional) {
        this.codigoOrgaoAutuadorBaseNacional = codigoOrgaoAutuadorBaseNacional;
    }

    public String getUfOrgaoAutuador() {
        return ufOrgaoAutuador;
    }

    public void setUfOrgaoAutuador(String ufOrgaoAutuador) {
        this.ufOrgaoAutuador = ufOrgaoAutuador;
    }

    public String getDataInfracao() {
        return dataInfracao;
    }

    public void setDataInfracao(String dataInfracao) {
        this.dataInfracao = dataInfracao;
    }

    public String getRegraInfracaoSeEnquadrou() {
        return regraInfracaoSeEnquadrou;
    }

    public void setRegraInfracaoSeEnquadrou(String regraInfracaoSeEnquadrou) {
        this.regraInfracaoSeEnquadrou = regraInfracaoSeEnquadrou;
    }
    
    
}