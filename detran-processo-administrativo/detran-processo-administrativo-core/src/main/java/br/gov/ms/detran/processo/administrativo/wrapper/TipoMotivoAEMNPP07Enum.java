/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;

/**
 *
 * @author desenvolvimento
 */
public enum TipoMotivoAEMNPP07Enum {
    
    MUDOU_SE(1),
    NAO_EXISTE_NRO_INDICADO(2),
    RECUSOU_SE(3),
    ENDERECO_INCOMPLETO(4),
    DESTINATARIO_DESCONHECIDO(5),
    AUSENTE_E_OUTROS_CODIGOS(6),
    AGUARDANDO_ENTREGA(7),
    ENTREGUES(8);
    
    private final Integer codigoMotivo;

    private TipoMotivoAEMNPP07Enum(Integer codigoMotivo) {
        this.codigoMotivo = codigoMotivo;
    }
    
    /**
     * 
     * @param codigoMotivo
     * @return
     * @throws AppException 
     */
    public static TipoMotivoAEMNPP07Enum getTipoPorCodigoMotivo(Integer codigoMotivo) throws AppException {
        
        for (TipoMotivoAEMNPP07Enum tEnum : TipoMotivoAEMNPP07Enum.values()) {
            if(tEnum.codigoMotivo.equals(codigoMotivo)){
                return tEnum;
            }
        }
        
        DetranWebUtils.applicationMessageException("PA.TipoMotivoAEMNPP07Enum.M1");
        
        return null;
    }
}
