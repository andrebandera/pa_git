/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import java.util.Date;

/**
 *
 * @author desenvolvimento
 */
public class ProcessoAdministrativoDesistenteWrapper {
    
    private String numeroProcesso;
    
    private String numeroProtocolo;
    
    public ProcessoAdministrativoDesistenteWrapper() {
    }

    public ProcessoAdministrativoDesistenteWrapper(
        String numeroProcesso, String numeroProtocolo, Date dataDesistencia) {
        
        this.numeroProcesso     = numeroProcesso;
        this.numeroProtocolo    = numeroProtocolo;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }
}