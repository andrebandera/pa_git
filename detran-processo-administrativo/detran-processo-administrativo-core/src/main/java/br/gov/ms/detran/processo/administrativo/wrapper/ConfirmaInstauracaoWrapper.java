package br.gov.ms.detran.processo.administrativo.wrapper;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

public class ConfirmaInstauracaoWrapper implements Serializable {

    private String numeroProcesso;
    private String andamento;
    private String status;
    
    private Integer codigo;

    private String mensagem;
    public ConfirmaInstauracaoWrapper() {
    }
    
    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getAndamento() {
        return andamento;
    }

    public void setAndamento(String andamento) {
        this.andamento = andamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}