package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class FunctionProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevido implements Serializable {
    
    @Id
    @Column(name = "ID")
    private Long idProcessoAdministrativo;
    
    @Column(name = "CPF")
    private String cpf;
    
    @Column(name = "NUMERO_PROCESSO")
    private String numeroProcesso;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_PROCESSO")
    private TipoProcessoEnum tipoProcesso;
    
    public FunctionProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevido() {
    }

    public Long getIdProcessoAdministrativo() {
        return idProcessoAdministrativo;
    }

    public void setIdProcessoAdministrativo(Long idProcessoAdministrativo) {
        this.idProcessoAdministrativo = idProcessoAdministrativo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public TipoProcessoEnum getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcessoEnum tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }
}