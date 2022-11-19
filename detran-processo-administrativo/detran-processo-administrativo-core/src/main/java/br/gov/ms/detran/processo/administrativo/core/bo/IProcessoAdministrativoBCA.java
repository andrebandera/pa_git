/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public interface IProcessoAdministrativoBCA {
    
    public void executa(EntityManager em, String urlPathParaRelatorio, Integer codigoAndamento);
}