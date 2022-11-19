package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ProcessoAdministrativoBCARelatorioTotalWrapper implements Serializable {

    private String integracao;

    private Date dataProcessamento;

    private Integer quantidadeSucesso;

    private Integer quantidadeFalhou;

    private List<ProcessoAdministrativoBCARelatorioDetalheWrapper> processos;
    
    private String listaProcessoSucesso;
    
    private String listaProcessoFalha;
    
    public ProcessoAdministrativoBCARelatorioTotalWrapper() {
    }

    public ProcessoAdministrativoBCARelatorioTotalWrapper(
        String integracao, Date dataProcessamento, Integer quantidadeSucesso, Integer quantidadeFalhou) {
        
        this.integracao         = integracao;
        this.dataProcessamento  = dataProcessamento;
        this.quantidadeSucesso  = quantidadeSucesso;
        this.quantidadeFalhou   = quantidadeFalhou;
    }
    
    public String getListaProcessoSucesso() {
        
        if(DetranStringUtil.ehBrancoOuNulo(listaProcessoSucesso)) {
            listaProcessoSucesso = "";
        }
        
        return listaProcessoSucesso;
    }

    public void setListaProcessoSucesso(String listaProcessoSucesso) {
        this.listaProcessoSucesso = listaProcessoSucesso;
    }

    public String getListaProcessoFalha() {
        
        if(DetranStringUtil.ehBrancoOuNulo(listaProcessoFalha)) {
            listaProcessoFalha = "";
        }
        
        return listaProcessoFalha;
    }

    public void setListaProcessoFalha(String listaProcessoFalha) {
        this.listaProcessoFalha = listaProcessoFalha;
    }
    
    public String getIntegracao() {
        return integracao;
    }

    public void setIntegracao(String integracao) {
        this.integracao = integracao;
    }

    public Date getDataProcessamento() {
        return dataProcessamento;
    }

    @XmlElement(name = "dataProcessamento")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataProcessamento(Date dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public Integer getQuantidadeSucesso() {
        return quantidadeSucesso;
    }

    public void setQuantidadeSucesso(Integer quantidadeSucesso) {
        this.quantidadeSucesso = quantidadeSucesso;
    }

    public Integer getQuantidadeFalhou() {
        return quantidadeFalhou;
    }

    public void setQuantidadeFalhou(Integer quantidadeFalhou) {
        this.quantidadeFalhou = quantidadeFalhou;
    }

    public List<ProcessoAdministrativoBCARelatorioDetalheWrapper> getProcessos() {
        return processos;
    }

    public void setProcessos(List<ProcessoAdministrativoBCARelatorioDetalheWrapper> processos) {
        this.processos = processos;
    }
}