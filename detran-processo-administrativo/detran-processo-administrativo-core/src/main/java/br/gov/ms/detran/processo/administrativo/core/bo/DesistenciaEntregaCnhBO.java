/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class DesistenciaEntregaCnhBO {

    public RetornoExecucaoAndamentoWrapper definirDesistenteEntregaCnh(EntityManager em, ProcessoAdministrativo processo) throws AppException{
        
        RetornoExecucaoAndamentoWrapper wrapper = null;
        PAComplemento desistente = new PAComplementoRepositorio().getPAComplementoPorParametroEAtivo(em, processo, PAParametroEnum.DESISTENCIA_ENTREGA_CNH);
        
        if(desistente != null){
            wrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO, PAFluxoProcessoConstante.FLUXO_DESISTENCIA_ENTREGA_CNH);
        }

        return  wrapper;
    }
    
}
