/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEBNH011;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEBNH011BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoBloqueioPessoaRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoRascunhoBloqueioRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IBloqueioBCAService;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoRascunhoBloqueio;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoProcessoBloqueioPessoaEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRascunhoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.Objects;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento70  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento70.class);
    
    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 70.");
        
        if(!TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(wrapper.getProcessoAdministrativo().getTipo())) {
            ProcessoRascunhoBloqueio rascunho = new ProcessoRascunhoBloqueioRepositorio().getRascunhoPorProcessoAdministrativo(em, wrapper.getProcessoAdministrativo());
            if(Objects.nonNull(rascunho) && !rascunho.getProcessoBloqueioPessoa().getSituacao().equals(SituacaoProcessoBloqueioPessoaEnum.FINALIZADO)) {
                if(!rascunho.getSituacao().equals(SituacaoRascunhoBloqueioEnum.DESBLOQUEADO)) {
                    throw new AppException("Não é possível finalizar o Bloqueio da Pessoa pois o Rascunho está diferente de DESBLOQUEADO.");
                }
                rascunho.getProcessoBloqueioPessoa().setSituacao(SituacaoProcessoBloqueioPessoaEnum.FINALIZADO);
                new ProcessoBloqueioPessoaRepositorio().update(em, rascunho.getProcessoBloqueioPessoa());
            }
        }

        AEBNH011 aebnh011
                = new AEBNH011BO()
                        .executarIntegracaoAEBNH011(wrapper.getProcessoAdministrativo().getCpf());

        if (aebnh011 == null) {
            DetranWebUtils.applicationMessageException("processoAdministrativoBCA.M1");
        }

        if (!DetranCollectionUtil.ehNuloOuVazio(aebnh011.getBloqueioLocal())
            || !DetranCollectionUtil.ehNuloOuVazio(aebnh011.getBloqueioNacional())) {

            IBloqueioBCAService bloqueioService = ServiceJndiLocator.<IBloqueioBCAService>lookup("ejb/BloqueioBCAService");
            bloqueioService.executarAEMNPP11(wrapper.getProcessoAdministrativo(), null, Boolean.FALSE);

        }

        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {}
}