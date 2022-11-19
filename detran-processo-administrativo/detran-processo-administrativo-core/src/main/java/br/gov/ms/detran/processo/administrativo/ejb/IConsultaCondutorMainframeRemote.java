/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.util.exception.AppException;

/**
 *
 * @author Christiano Carrilho.
 */
public interface IConsultaCondutorMainframeRemote {

    /**
     * Executa transação 575 - Consulta Prontuário Condutor na Base Nacional e
     * retorna o motivo da cassação da CNH do condutor.
     * 
     * Retorna nulo se não a CNH não estiver cassada.
     * 
     * @param cpf CPF do condutor.
     * @return Motivo da cassação: [Código] - [Descrição]
     * @throws AppException 
     */
    String getMotivoCassacaoCondutor(String cpf) throws AppException;
}