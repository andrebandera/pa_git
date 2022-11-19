package br.gov.ms.detran.processo.administrativo.wrapper;

public class ProcessoAdministrativoBCARelatorioDetalheWrapper {
    
    private String numeroProcesso;
    
    private String sucesso;
    
    private String observacao;

    public ProcessoAdministrativoBCARelatorioDetalheWrapper() {
    }

    public ProcessoAdministrativoBCARelatorioDetalheWrapper(String numeroProcesso, String sucesso, String observacao) {
        
        this.numeroProcesso = numeroProcesso;
        this.sucesso        = sucesso;
        this.observacao     = observacao;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getSucesso() {
        return sucesso;
    }

    public void setSucesso(String sucesso) {
        this.sucesso = sucesso;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}