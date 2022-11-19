package br.gov.ms.detran.processo.administrativo.core.cenarios;

import br.gov.ms.detran.comum.util.exception.AppException;

public interface IUltimoSequencialService {

    String getNumeroPortaria() throws AppException;
    
    String getNumeroProcesso() throws AppException;
    
    String getNumeroBloqueioBCA() throws AppException;
}