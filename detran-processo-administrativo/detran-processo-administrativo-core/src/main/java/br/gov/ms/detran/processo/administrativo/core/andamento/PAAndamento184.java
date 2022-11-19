/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_REALIZACAO_PROVA_CURSO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoBloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoRascunhoBloqueioRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoBloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoRascunhoBloqueio;
import br.gov.ms.detran.processo.administrativo.enums.MotivoDesbloqueioCnhEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRascunhoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoMovimentoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento184  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento184.class);
    
    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 184.");
        
        BloqueioBCA bloqueio
                = new BloqueioBCARepositorio()
                        .getBloqueioBCAporPA(em, wrapper.getProcessoAdministrativo().getId());

        if(!TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(wrapper.getProcessoAdministrativo().getTipo())){
            ProcessoRascunhoBloqueio rascunho =  getRascunhoProcessoAptoADesbloqueio(em, wrapper.getProcessoAdministrativo());
            if(Objects.nonNull(rascunho)){
                rascunho.setSituacao(SituacaoRascunhoBloqueioEnum.DESBLOQUEADO);
                new ProcessoRascunhoBloqueioRepositorio().update(em, rascunho);
            }
        }

        bloqueio.setDataFim(new Date());
        bloqueio.setMotivoDesbloqueio(MotivoDesbloqueioCnhEnum.PENA_CUMPRIDA);
        bloqueio.setSituacao(SituacaoBloqueioBCAEnum.FINALIZADO);

        new BloqueioBCARepositorio().update(em, bloqueio);

        MovimentoBloqueioBCA movimentacaoBloqueio = new MovimentoBloqueioBCA();
        movimentacaoBloqueio.setBloqueioBCA(bloqueio);
        movimentacaoBloqueio.setDataBCA(new Date());
        movimentacaoBloqueio.setTipo(TipoMovimentoBloqueioBCAEnum.DESBLOQUEIO);
        movimentacaoBloqueio.setUsuario(wrapper.getIdUsuario());
        movimentacaoBloqueio.setAtivo(AtivoEnum.ATIVO);

        new MovimentoBloqueioBCARepositorio().insert(em, movimentacaoBloqueio);

        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {}

    private ProcessoRascunhoBloqueio getRascunhoProcessoAptoADesbloqueio(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        ProcessoRascunhoBloqueio rascunho = new ProcessoRascunhoBloqueioRepositorio().getRascunhoPorProcessoAdministrativo(em, processoAdministrativo);
        
        List andamento48 = new ProcessoAdministrativoRepositorio().getListaProcessoAdministrativoPorCpfEAndamento(em, processoAdministrativo.getCpf(), AGUARDAR_ENTREGA_CNH);
        List andamento60 = new ProcessoAdministrativoRepositorio().getListaProcessoAdministrativoPorCpfEAndamento(em, processoAdministrativo.getCpf(), AGUARDAR_REALIZACAO_PROVA_CURSO);
        
        if(!Objects.isNull(andamento48) && !andamento48.isEmpty()){
            throw new AppException("Existem PAs parados no andamento 48 para o condutor");
        }
        if(!Objects.isNull(andamento60) && !andamento60.isEmpty()){
            throw new AppException("Existem PAs parados no andamento 60 para o condutor");
        }
        
        return rascunho;
    }
}