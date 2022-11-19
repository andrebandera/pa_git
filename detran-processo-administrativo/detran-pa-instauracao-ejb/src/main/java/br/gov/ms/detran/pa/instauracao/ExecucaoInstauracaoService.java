/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.pa.instauracao;

import br.gov.ms.detran.comum.projeto.service.DetranAbstractGenericService;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.ejb.IExecucaoInstauracaoService;
import br.gov.ms.detran.processo.administrativo.entidade.ExecucaoInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.SituacaoExecucaoInstauracaoEnum;
import static br.gov.ms.detran.processo.administrativo.entidade.SituacaoExecucaoInstauracaoEnum.INICIADO;
import java.util.Calendar;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Christiano Carrilho.
 */
@Stateless(mappedName = "ejb/ExecucaoInstauracaoService")
@Remote(IExecucaoInstauracaoService.class)
public class ExecucaoInstauracaoService extends DetranAbstractGenericService implements IExecucaoInstauracaoService {

    private static final Logger LOG = Logger.getLogger(ExecucaoInstauracaoService.class);

    @Override
    @PersistenceContext(unitName = "DETRAN-PA-PU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public ExecucaoInstauracao getExecucaoInstauracao(Long execucaoId) throws AppException {
        return (ExecucaoInstauracao) buscarEntidade(ExecucaoInstauracao.class, execucaoId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long criarExecucao(Long execucaoOrigemId) throws AppException {

        ExecucaoInstauracao execucao = new ExecucaoInstauracao();
        execucao.setExecucaoInstauracaoOrigem(getExecucaoInstauracao(execucaoOrigemId));
        execucao.setDataInicio(Calendar.getInstance().getTime());
        execucao.setSituacao(INICIADO);
        execucao.setQuantidadeInstaurado(0L);

        execucao = (ExecucaoInstauracao) gravar(execucao);
        return execucao.getId();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void atualizarExecucao(Long execucaoId, SituacaoExecucaoInstauracaoEnum situacao) throws AppException {

        ExecucaoInstauracao execucao = getExecucaoInstauracao(execucaoId);
        execucao.setSituacao(situacao);
        gravar(execucao);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void finalizarExecucao(Long execucaoId) throws AppException {

        ExecucaoInstauracao execucao = (ExecucaoInstauracao) 
                        buscarEntidade(ExecucaoInstauracao.class, execucaoId);

        execucao.setQuantidadeInstaurado(
            contarPesquisa(ProcessoAdministrativo.class, execucaoId)
        );

        execucao.setDataFim(Calendar.getInstance().getTime());
        execucao.setSituacao(SituacaoExecucaoInstauracaoEnum.FINALIZADO);

        gravar(execucao);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean isInterromperExecucao(Long execucaoId) throws AppException {

        ExecucaoInstauracao execucao = getExecucaoInstauracao(execucaoId);
        boolean interromper = (execucao == null || execucao.getSituacao().equals(SituacaoExecucaoInstauracaoEnum.FINALIZADO));

        if (interromper) {
            LOG.warn("Execução foi interrompida!");
        }

        return interromper;
        
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void finalizarExecucaoOrigem(Long execucaoId) throws AppException {

        ExecucaoInstauracao execucaoInstauracaoOrigem = getExecucaoInstauracao(execucaoId);
        execucaoInstauracaoOrigem.setSituacao(SituacaoExecucaoInstauracaoEnum.FINALIZADO);
        execucaoInstauracaoOrigem.setDataFim(Calendar.getInstance().getTime());

        gravar(execucaoInstauracaoOrigem);
    }
}