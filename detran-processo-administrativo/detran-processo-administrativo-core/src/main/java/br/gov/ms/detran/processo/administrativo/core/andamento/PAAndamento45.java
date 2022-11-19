/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.BloqueioBCABO;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoRascunhoBloqueioBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAComplementoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoRascunhoBloqueioRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IBloqueioBCAService;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoRascunhoBloqueio;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRascunhoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento45  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento45.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        
        LOG.debug("Início Andamento45.");
        
        IBloqueioBCAService bloqueioService = ServiceJndiLocator.<IBloqueioBCAService>lookup("ejb/BloqueioBCAService");
        
        if (existeRascunhoParaAtualizar(em, wrapper.getProcessoAdministrativo())) {
            ProcessoRascunhoBloqueio rascunho = new ProcessoRascunhoBloqueioRepositorio().getRascunhoPorProcessoAdministrativo(em, wrapper.getProcessoAdministrativo());

            if (Objects.isNull(rascunho) || !SituacaoRascunhoBloqueioEnum.BLOQUEADO_BL.equals(rascunho.getSituacao())) {
                DetranWebUtils.applicationMessageException("Processo não possui rascunho de bloqueio ou sua situação é diferente de Bloqueio por Entrega de CNH.");
            }

            new ProcessoRascunhoBloqueioBO().validarMudancaSituacaoRascunho(em, rascunho, SituacaoRascunhoBloqueioEnum.BLOQUEADO_BL);
            
            rascunho.setSituacao(SituacaoRascunhoBloqueioEnum.BLOQUEADO_BCA);
            new ProcessoRascunhoBloqueioRepositorio().update(em, rascunho);
            
        }
        bloqueioService.executarAEMNPP14(new BloqueioBCABO().montarWrapperAEMNPP14(em, wrapper.getProcessoAdministrativo()), Boolean.FALSE);
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {}

    private boolean existeRascunhoParaAtualizar(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        if(processoAdministrativo.getTipo().equals(TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH))
            return false;
        if(processoAdministrativo.getOrigem().equals(OrigemEnum.PONTUACAO)){
            PAComplemento desistenciaEntrega = new PAComplementoRepositorio().getPAComplementoPorParametroEAtivo(em, processoAdministrativo, PAParametroEnum.DESISTENCIA_ENTREGA_CNH);
            if(Objects.isNull(desistenciaEntrega)){
                return false;
            }
        }
        return true;
    }
}