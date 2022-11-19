package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.servico.IDetranGenericService;
import br.gov.ms.detran.comum.util.exception.AppException;

/**
 *
 * @author desenvolvimento
 * @param <T>
 */
public interface IExecucaoEspecificaProcessoAdministrativoService<T extends IBaseEntity> extends IDetranGenericService<T>{
    
    /**
     * 
     * @param iBaseEntity
     * @throws AppException 
     */
    public void alterarSituacaoProcessoAdministrativoJson(IBaseEntity iBaseEntity) throws AppException;
}