/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoRascunhoBloqueioBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoRascunhoBloqueioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoRascunhoBloqueio;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.Objects;
import javax.persistence.EntityManager;

/**
 *
 * @author lilly
 */
public class PAAndamento244 extends ExecucaoAndamento {

    @Override
    RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, wrapper.getProcessoAdministrativo().getId());
        if(wrapper.getProcessoAdministrativo().getOrigem().equals(OrigemEnum.PONTUACAO)){
            new ProcessoRascunhoBloqueioBO().criarRascunhoBloqueioParaDesentranhamento(em, wrapper.getProcessoAdministrativo());
        } 
        else if(Objects.nonNull(pju) && pju.getIndicativoPrazoIndeterminado().equals(BooleanEnum.NAO)){
            ProcessoRascunhoBloqueio rascunho = new ProcessoRascunhoBloqueioRepositorio().getRascunhoPorProcessoAdministrativo(em, wrapper.getProcessoAdministrativo());
            if(Objects.nonNull(rascunho)){
                new ProcessoRascunhoBloqueioBO().
                        criarRascunhoBloqueioParaJuridico(em, wrapper.getProcessoAdministrativo(),  pju.getDataInicioPenalidade());
            }
        } 

        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }

    @Override
    void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException { }

    

}
