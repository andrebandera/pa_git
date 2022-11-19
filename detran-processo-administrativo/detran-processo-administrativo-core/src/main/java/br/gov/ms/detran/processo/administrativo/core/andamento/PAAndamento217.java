/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.integracao.comum.wrapper.AEBNH011;
import br.gov.ms.detran.processo.administrativo.core.bo.MovimentacaoPABO;
import br.gov.ms.detran.processo.administrativo.core.bo.recursoonline.RecursoOnlineBO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEBNH011BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentacaoPARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAAndamentoProcessoEspecificoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IBloqueioBCAService;
import br.gov.ms.detran.processo.administrativo.entidade.*;
import br.gov.ms.detran.processo.administrativo.enums.*;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoOnlineCanceladoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento217 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento217.class);

    private IApoioService apoioService;
    
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 217.");

        AbstractJpaDAORepository jpa = new AbstractJpaDAORepository();

        ProcessoAdministrativo processoAdministrativo = wrapper.getProcessoAdministrativo();

        PAAndamentoProcessoEspecifico processoEspecifico = (PAAndamentoProcessoEspecifico) wrapper.getObjetoWrapper();

        if(processoEspecifico == null){
            processoEspecifico = new PAAndamentoProcessoEspecificoRepositorio().getAndamentoEspecificoPorProcessoAdministrativo(em, wrapper.getProcessoAdministrativo());
        }

        /**
            Gravar Movimentação de arquivamento.
         */
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setProcessoAdministrativo(processoAdministrativo);
        movimentacao.setFluxoFase(new PAFluxoFaseRepositorio().find(em, PAFluxoFase.class, processoEspecifico.getFluxoFaseOrigem()));
        movimentacao.setUsuario(wrapper.getIdUsuario());
        movimentacao.setMovimentacaoAcao(MovimentacaoAcaoEnum.ARQUIVAR);
        movimentacao.setMotivo(MovimentacaoMotivoEnum.PROCESSO_PRESCRITO);
        movimentacao.setObservacao(processoEspecifico.getObservacaoPrescricao());
        movimentacao.setPrescricao(processoEspecifico.getPrescricao());
        movimentacao.setDataInicio(new Date());
        movimentacao.setDataCadastro(new Date());
        movimentacao.setAtivo(AtivoEnum.ATIVO);

        new MovimentacaoPARepositorio().insert(em, movimentacao);
        new MovimentacaoPABO().penalidadeOuBloqueio(em, movimentacao, processoAdministrativo, wrapper.getIdUsuario(), MotivoDesbloqueioCnhEnum.PENA_ANULADA);

        /**
         * Checa situação Bloqueio. *
         */
        BloqueioBCA bloqueio = new BloqueioBCARepositorio().getBloqueioBcaPorPaEAtivo(em, processoAdministrativo.getId());
        
        try {
            if (bloqueio != null && SituacaoBloqueioBCAEnum.FINALIZADO.equals(bloqueio.getSituacao())) {
                AEBNH011 aebnh011
                        = new AEBNH011BO()
                                .executarIntegracaoAEBNH011(processoAdministrativo.getCpf());
                if (aebnh011 == null) {
                    DetranWebUtils.applicationMessageException("processoAdministrativoBCA.M1");
                }
                if (!DetranCollectionUtil.ehNuloOuVazio(aebnh011.getBloqueioLocal())
                    || !DetranCollectionUtil.ehNuloOuVazio(aebnh011.getBloqueioNacional())) {
                    /**
                     * AEMNPP11 - Desbloqueio. *
                     */
                    IBloqueioBCAService bloqueioService = ServiceJndiLocator.<IBloqueioBCAService>lookup("ejb/BloqueioBCAService");
                    bloqueioService.executarDesbloqueioAEMNPP11(wrapper.getProcessoAdministrativo(), null, movimentacao.getObservacao());

                }
            }
        } catch (AppException e) {
            processoEspecifico.setObservacao(e.getParams().length == 0 ? e.getMessage() : e.getParams()[0]);
            getProcessoAdministrativoService().atualizarProcessoEspecifico(processoEspecifico);
            throw e;
        }
        /**
         Atualizar andamento específico com SUCESSO.
         */
        processoEspecifico.setSucesso(Boolean.TRUE);
        jpa.update(em, processoEspecifico);

        new RecursoOnlineBO()
                .cancelarRecursoOnlineEmBackOffice(em,
                        processoAdministrativo,
                        new RecursoOnlineCanceladoWrapper("Arquivamento do Processo.", "", "DETRAN"));

        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO, processoEspecifico.getCodigoFluxoDestino());
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    }
}
