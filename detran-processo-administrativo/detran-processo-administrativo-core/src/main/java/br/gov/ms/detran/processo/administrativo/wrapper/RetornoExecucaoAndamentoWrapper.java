/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;

/**
 *
 * @author desenvolvimento
 */
public class RetornoExecucaoAndamentoWrapper {
    
    private TipoRetornoAndamentoEnum tipo;
    
    private Integer codigoFluxo;
    
    private Integer codigoAndamento;
    
    private ProcessoAdministrativo processoAdministrativo;

    public RetornoExecucaoAndamentoWrapper() {
    }

    public RetornoExecucaoAndamentoWrapper(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum tipo) {
        this.tipo = tipo;
    }

    public RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum tipo, Integer codigoFluxo) {
        this(tipo);
        this.codigoFluxo = codigoFluxo;
    }

    public RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum tipo, Integer codigoFluxo, Integer codigoAndamento) {
        this.tipo               = tipo;
        this.codigoFluxo        = codigoFluxo;
        this.codigoAndamento    = codigoAndamento;
    }

    public TipoRetornoAndamentoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoRetornoAndamentoEnum tipo) {
        this.tipo = tipo;
    }

    public Integer getCodigoFluxo() {
        return codigoFluxo;
    }

    public void setCodigoFluxo(Integer codigoFluxo) {
        this.codigoFluxo = codigoFluxo;
    }

    public Integer getCodigoAndamento() {
        return codigoAndamento;
    }

    public void setCodigoAndamento(Integer codigoAndamento) {
        this.codigoAndamento = codigoAndamento;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }
}
