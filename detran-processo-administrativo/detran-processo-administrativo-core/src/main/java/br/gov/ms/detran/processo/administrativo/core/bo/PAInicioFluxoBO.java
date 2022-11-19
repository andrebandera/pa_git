/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAInicioFluxoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAInicioFluxo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAInicioFluxoBO {

    void gravarInicioFluxo(EntityManager em, ProcessoAdministrativo processoAdministrativo, Integer codigoFluxo) throws AppException {
        PAInicioFluxoRepositorio inicioFluxoRepo = new PAInicioFluxoRepositorio();
        
        PAInicioFluxo entidadeBanco = inicioFluxoRepo.
                    getPAInicioFluxoAtivoPorProcessoAdministrativo(em, processoAdministrativo.getId());
        
        if(entidadeBanco != null){
            DetranWebUtils.applicationMessageException("Não é possível gravar o início de um novo fluxo.");
        }
        
        PAFluxoProcesso fluxoProcesso = new PAFluxoProcessoRepositorio().getPAFluxoProcessoPorCodigo(em, codigoFluxo);
        if(fluxoProcesso == null || AtivoEnum.DESATIVADO.equals(fluxoProcesso.getAtivo())){
            DetranWebUtils.applicationMessageException("Não foi possível encontrar o fluxo.");
        }
        
        PAInicioFluxo inicioFluxo = new PAInicioFluxo(processoAdministrativo, fluxoProcesso);
        inicioFluxoRepo.insert(em, inicioFluxo);
    }
    
}
