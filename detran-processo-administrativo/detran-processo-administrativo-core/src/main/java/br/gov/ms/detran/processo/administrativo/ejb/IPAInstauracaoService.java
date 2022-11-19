package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.servico.IDetranGenericService;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import br.gov.ms.detran.processo.administrativo.wrapper.ApoioOrigemInstauracaoWrapper;
import java.util.List;

/**
 * @author desenvolvimento
 * @param <T>
 */
public interface IPAInstauracaoService<T extends IBaseEntity> extends IDetranGenericService<T>{

    void validarCondutorParaInstaurarPA(DadosCondutorPAD dadosCondutorPAD) throws RegraNegocioException, AppException;

    IBaseEntity instaurar(Object defineTipoCenarioInstaurar, IBaseEntity iBaseEntity) throws AppException;

    IBaseEntity buscarExecucaoInstauracaoPeloId(Long id) throws DatabaseException;

    void atualizarInfracoes(Object jsonWrapper, IBaseEntity iBaseEntity) throws AppException;

    public List<ApoioOrigemInstauracaoWrapper> recuperarCenarios();

    public BaseEntityAtivo getPAOcorrenciaStatusAtiva(Long processoId) throws AppException;

    public void proximoAndamento(Long id, BaseEntityAtivo ocorrenciaAtual) throws AppException;
}