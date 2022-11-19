/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoBloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoBloqueioBCA;
import br.gov.ms.detran.processo.administrativo.enums.MotivoDesbloqueioCnhEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoMovimentoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.Date;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento210  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento210.class);
    
    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 210.");


        BloqueioBCA bloqueio
                = new BloqueioBCARepositorio()
                        .getBloqueioBCAporPA(em, wrapper.getProcessoAdministrativo().getId());

        bloqueio.setDataFim(new Date());
        bloqueio.setMotivoDesbloqueio(MotivoDesbloqueioCnhEnum.PENA_ANULADA);
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
}