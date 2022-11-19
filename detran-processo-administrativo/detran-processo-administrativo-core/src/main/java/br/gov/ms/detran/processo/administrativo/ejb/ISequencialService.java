package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.util.exception.AppException;

public interface ISequencialService {

    String getNumeroPortaria() throws AppException;
    
    String getNumeroProcesso() throws AppException;
    
    String getNumeroBloqueioBCA() throws AppException;
    
    String getNumeroProtocolo() throws AppException;
}