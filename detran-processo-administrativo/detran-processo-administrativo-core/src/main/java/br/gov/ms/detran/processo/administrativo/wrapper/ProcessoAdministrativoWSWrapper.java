package br.gov.ms.detran.processo.administrativo.wrapper;

public class ProcessoAdministrativoWSWrapper {

    private String numeroProcesso;

    public ProcessoAdministrativoWSWrapper() {
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public ProcessoAdministrativoWSWrapper(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }
}