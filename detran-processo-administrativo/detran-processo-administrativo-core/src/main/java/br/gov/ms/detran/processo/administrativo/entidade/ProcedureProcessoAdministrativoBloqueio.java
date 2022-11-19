package br.gov.ms.detran.processo.administrativo.entidade;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProcedureProcessoAdministrativoBloqueio implements Serializable {
    
    @Id
    @Column(name = "id_processo")
    private Long idProcesso;
    
    @Column(name = "numero_processo")
    private String numeroProcesso;
    
    @Column(name = "cpf")
    private String cpf;
    
    @Column(name = "data_inicio")
    private String dataInicio;
    
    @Column(name = "data_fim")
    private String dataFim;
    
    @Column(name = "valor_prazo_cumprimento_pena")
    private Integer valorPrazoCumprimentoPena;
    
    @Column(name = "origem_processo")
    private String origem;

    public ProcedureProcessoAdministrativoBloqueio() {
    }

    public ProcedureProcessoAdministrativoBloqueio(Long idProcesso, String numeroProcesso, String cpf, String dataInicio, String dataFim, Integer valorPrazoCumprimentoPena, String origem) {
        this.idProcesso = idProcesso;
        this.numeroProcesso = numeroProcesso;
        this.cpf = cpf;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.valorPrazoCumprimentoPena = valorPrazoCumprimentoPena;
        this.origem = origem;
    }

    public Long getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public Integer getValorPrazoCumprimentoPena() {
        return valorPrazoCumprimentoPena;
    }

    public void setValorPrazoCumprimentoPena(Integer valorPrazoCumprimentoPena) {
        this.valorPrazoCumprimentoPena = valorPrazoCumprimentoPena;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }
}