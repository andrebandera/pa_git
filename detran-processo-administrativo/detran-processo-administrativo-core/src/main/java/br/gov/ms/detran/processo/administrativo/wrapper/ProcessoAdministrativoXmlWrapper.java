package br.gov.ms.detran.processo.administrativo.wrapper;

import java.util.Date;

public class ProcessoAdministrativoXmlWrapper {
    
    String cpf;
    
    Long cnh;
    
    String tipoProcesso;
    
    Long numeroDetran;
    
    String numeroProcesso;
    
    String numeroPortaria;
    
    String andamento;
    
    Date dataProcessamento;
    
    String situacao;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Long getCnh() {
        return cnh;
    }

    public void setCnh(Long cnh) {
        this.cnh = cnh;
    }

    public String getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(String tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public Long getNumeroDetran() {
        return numeroDetran;
    }

    public void setNumeroDetran(Long numeroDetran) {
        this.numeroDetran = numeroDetran;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroPortaria() {
        return numeroPortaria;
    }

    public void setNumeroPortaria(String numeroPortaria) {
        this.numeroPortaria = numeroPortaria;
    }

    public String getAndamento() {
        return andamento;
    }

    public void setAndamento(String andamento) {
        this.andamento = andamento;
    }

    public Date getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(Date dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}