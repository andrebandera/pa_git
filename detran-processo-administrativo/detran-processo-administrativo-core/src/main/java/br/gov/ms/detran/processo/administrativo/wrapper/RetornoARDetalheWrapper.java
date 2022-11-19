package br.gov.ms.detran.processo.administrativo.wrapper;

import java.io.Serializable;
import java.util.Date;

public class RetornoARDetalheWrapper implements Serializable {

    private String identificadorInicial;
    private String numeroObjeto;
    private String identificadorFinal;
    private String numeroProcesso;
    private String numeroNotificacao;
    private Date dataEntregaOuDevolucao;
    private String nomeRecebedor;
    private String numeroDocumentoRecebedor;
    private String dataBaixa;
    private String motivoDevolucao;
    private String controleCorreios;

    public RetornoARDetalheWrapper() {
    }

    public String getIdentificadorInicial() {
        return identificadorInicial;
    }

    public void setIdentificadorInicial(String identificadorInicial) {
        this.identificadorInicial = identificadorInicial;
    }

    public String getNumeroObjeto() {
        return numeroObjeto;
    }

    public void setNumeroObjeto(String numeroObjeto) {
        this.numeroObjeto = numeroObjeto;
    }

    public String getIdentificadorFinal() {
        return identificadorFinal;
    }

    public void setIdentificadorFinal(String identificadorFinal) {
        this.identificadorFinal = identificadorFinal;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroNotificacao() {
        return numeroNotificacao;
    }

    public void setNumeroNotificacao(String numeroNotificacao) {
        this.numeroNotificacao = numeroNotificacao;
    }

    public Date getDataEntregaOuDevolucao() {
        return dataEntregaOuDevolucao;
    }

    public void setDataEntregaOuDevolucao(Date dataEntregaOuDevolucao) {
        this.dataEntregaOuDevolucao = dataEntregaOuDevolucao;
    }

    public String getNomeRecebedor() {
        return nomeRecebedor;
    }

    public void setNomeRecebedor(String nomeRecebedor) {
        this.nomeRecebedor = nomeRecebedor;
    }

    public String getNumeroDocumentoRecebedor() {
        return numeroDocumentoRecebedor;
    }

    public void setNumeroDocumentoRecebedor(String numeroDocumentoRecebedor) {
        this.numeroDocumentoRecebedor = numeroDocumentoRecebedor;
    }

    public String getDataBaixa() {
        return dataBaixa;
    }

    public void setDataBaixa(String dataBaixa) {
        this.dataBaixa = dataBaixa;
    }

    public String getMotivoDevolucao() {
        return motivoDevolucao;
    }

    public void setMotivoDevolucao(String motivoDevolucao) {
        this.motivoDevolucao = motivoDevolucao;
    }

    public String getControleCorreios() {
        return controleCorreios;
    }

    public void setControleCorreios(String controleCorreios) {
        this.controleCorreios = controleCorreios;
    }
}