package br.gov.ms.detran.processo.administrativo.wrapper;

import java.util.List;

public class RetornoARWrapper {
    
    String nomeArquivo;
    
    String tipoRegistro;
    String dataEnviado;
    
    List<RetornoARDetalheWrapper> detalhes;
    
    String quantidadeDeRegistrosDetalhe;

    public RetornoARWrapper() {
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getDataEnviado() {
        return dataEnviado;
    }

    public void setDataEnviado(String dataEnviado) {
        this.dataEnviado = dataEnviado;
    }

    public String getQuantidadeDeRegistrosDetalhe() {
        return quantidadeDeRegistrosDetalhe;
    }

    public void setQuantidadeDeRegistrosDetalhe(String quantidadeDeRegistrosDetalhe) {
        this.quantidadeDeRegistrosDetalhe = quantidadeDeRegistrosDetalhe;
    }

    public List<RetornoARDetalheWrapper> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(List<RetornoARDetalheWrapper> detalhes) {
        this.detalhes = detalhes;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }
}