/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.servico.IDetranGenericService;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ExecucaoInstauracao;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Christiano Carrilho.
 * @param <T>
 */
public interface IInstauracaoService<T extends IBaseEntity> extends IDetranGenericService<T> {

    /**
     * 
     * @param execucaoInstauracaoOrigem 
     */
    void instaurar(ExecucaoInstauracao execucaoInstauracaoOrigem);

    /**
     * 
     * @param entidade
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    @Override
    IBaseEntity gravar(IBaseEntity entidade) throws DatabaseException;

    /**
     * 
     * @param request
     * @throws AppException 
     */
    void validaToken(HttpServletRequest request) throws AppException;
}