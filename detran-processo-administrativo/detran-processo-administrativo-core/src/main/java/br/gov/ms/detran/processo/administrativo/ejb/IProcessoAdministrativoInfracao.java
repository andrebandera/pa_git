/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.ejb;

import java.util.Date;

/**
 *
 * @author desenvolvimento
 */
public interface IProcessoAdministrativoInfracao {

    public String getAutoInfracao();
    
    public String getLocal();
    
    public Integer getMunicipioInfracao();
    
    public Date getDataInfracao();
}
