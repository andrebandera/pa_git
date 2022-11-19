/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;

/**
 *
 * @author Lillydi
 */
public interface IPAAndamentoService {

    public void executa(ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException;

    public void iniciaExecucao(ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper)throws AppException;
    
    public RetornoExecucaoAndamentoWrapper executaAndamentoSemAlterarAndamentoOuFluxo(ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException;
}