/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.servico.IDetranGenericService;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.entidade.ExecucaoInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.SituacaoExecucaoInstauracaoEnum;

/**
 *
 * @author Christiano Carrilho.
 * @param <T>
 */
public interface IExecucaoInstauracaoService<T extends IBaseEntity> extends IDetranGenericService<T> {

    /**
     * 
     * @param execucaoOrigemId
     * @return
     * @throws AppException 
     */
    Long criarExecucao(Long execucaoOrigemId) throws AppException;

    /**
     * 
     * @param execucaoId
     * @param situacao
     * @throws AppException 
     */
    void atualizarExecucao(Long execucaoId, SituacaoExecucaoInstauracaoEnum situacao) throws AppException;

    /**
     * 
     * @param execucaoId
     * @throws AppException 
     */
    void finalizarExecucao(Long execucaoId) throws AppException;

    /**
     * 
     * @param execucaoId
     * @return
     * @throws AppException 
     */
    boolean isInterromperExecucao(Long execucaoId) throws AppException;

    /**
     * 
     * @param execucaoId
     * @throws AppException 
     */
    void finalizarExecucaoOrigem(Long execucaoId) throws AppException;

    /**
     * 
     * @param execucaoId
     * @return
     * @throws AppException 
     */
    ExecucaoInstauracao getExecucaoInstauracao(Long execucaoId) throws AppException;
}