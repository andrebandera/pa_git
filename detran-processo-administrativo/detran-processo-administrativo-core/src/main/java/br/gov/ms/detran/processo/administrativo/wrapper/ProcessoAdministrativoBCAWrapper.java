package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;

public class ProcessoAdministrativoBCAWrapper {
    
    private ProcessoAdministrativo processoAdministrativo;
    
    private Integer codigoAndamento;
    
    private String artigoInciso;
    
    private Boolean sucessoBloqueio;
    
    private String observacao;
    
    private String codigoPrograma;
    
    public ProcessoAdministrativoBCAWrapper() {
    }

    public ProcessoAdministrativoBCAWrapper(ProcessoAdministrativo processoAdministrativo, String artigoInciso) {
        this.processoAdministrativo = processoAdministrativo;
        this.artigoInciso = artigoInciso;
    }

    public ProcessoAdministrativoBCAWrapper(
        Long idProcessoAdministrativo, String numeroProcesso, 
        String cpf, Long cnh, Integer codigoAndamento, TipoProcessoEnum tipo, String artigoInciso) {
        
        this.processoAdministrativo 
            = new ProcessoAdministrativo(
                idProcessoAdministrativo,
                numeroProcesso,
                cpf,
                cnh,
                tipo
            );
        
        this.codigoAndamento    = codigoAndamento;
        this.artigoInciso       = artigoInciso;
    }
    public ProcessoAdministrativoBCAWrapper(
        Long idProcessoAdministrativo, String numeroProcesso, 
        String cpf, Long cnh, Integer codigoAndamento, TipoProcessoEnum tipo, String artigoInciso, ApoioOrigemInstauracao apoio, OrigemEnum origem) {
        
        this(idProcessoAdministrativo, numeroProcesso, cpf, cnh, codigoAndamento, tipo, artigoInciso);
        this.processoAdministrativo.setOrigemApoio(apoio);
        this.processoAdministrativo.setOrigem(origem);
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public Integer getCodigoAndamento() {
        return codigoAndamento;
    }

    public void setCodigoAndamento(Integer codigoAndamento) {
        this.codigoAndamento = codigoAndamento;
    }

    public String getArtigoInciso() {
        return artigoInciso;
    }

    public void setArtigoInciso(String artigoInciso) {
        this.artigoInciso = artigoInciso;
    }

    public Boolean getSucessoBloqueio() {
        return sucessoBloqueio;
    }

    public void setSucessoBloqueio(Boolean sucessoBloqueio) {
        this.sucessoBloqueio = sucessoBloqueio;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getCodigoPrograma() {
        return codigoPrograma;
    }

    public void setCodigoPrograma(String codigoPrograma) {
        this.codigoPrograma = codigoPrograma;
    }
}