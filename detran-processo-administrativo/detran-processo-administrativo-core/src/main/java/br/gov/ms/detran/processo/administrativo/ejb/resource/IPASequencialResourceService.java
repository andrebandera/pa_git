package br.gov.ms.detran.processo.administrativo.ejb.resource;

import br.gov.ms.detran.comum.util.exception.AppException;

public interface IPASequencialResourceService {

    /**
     * 
     * @return
     * @throws AppException 
     */
    String getNumeroPortaria() throws AppException;

    /**
     * 
     * @return
     * @throws AppException 
     */
    String getNumeroProcesso() throws AppException;

    /**
     * 
     * @return
     * @throws AppException 
     */
    String getNumeroBloqueioBCA() throws AppException;
    
    /**
     * 
     * @return
     * @throws AppException 
     */
    String getNumeroProtocolo() throws AppException;
}