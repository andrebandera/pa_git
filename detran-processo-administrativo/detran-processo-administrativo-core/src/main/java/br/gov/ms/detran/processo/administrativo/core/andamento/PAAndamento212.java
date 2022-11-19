/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.UnidadePenalEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.Calendar;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento212  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento212.class);
    
    private static final Integer DATA_INDETERMINADA = 1;

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 212.");
        
        DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, wrapper.getProcessoAdministrativo().getId());
        if(pju == null){
            DetranWebUtils.applicationMessageException("Não foi possível recuperar os dados judiciais do processo.");
        }
        
        PAPenalidadeProcesso penalidade = new PAPenalidadeProcesso();
        
        penalidade.setAtivo(AtivoEnum.ATIVO);
        penalidade.setProcessoAdministrativo(wrapper.getProcessoAdministrativo());
        penalidade.setUsuario(pju.getUsuarioCadastro());
        penalidade.setDataInicioPenalidade(pju.getDataInicioPenalidade());
        penalidade.setDataCadastro(Calendar.getInstance().getTime());
        penalidade.setValor(pju.getPrazoPenalidade());
        penalidade.setUnidadePenal(UnidadePenalEnum.DIA);
        
        if(BooleanEnum.NAO.equals(pju.getIndicativoPrazoIndeterminado())){
            penalidade.setDataFimPenalidade(Utils.addDayMonth(penalidade.getDataInicioPenalidade(), penalidade.getValor()));
        }else{
            penalidade.setDataFimPenalidade(Utils.addDayMonth(penalidade.getDataInicioPenalidade(), DATA_INDETERMINADA));
            penalidade.setValor(DATA_INDETERMINADA);
        }
        
        new PAPenalidadeProcessoRepositorio().insert(em, penalidade);
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
    }


    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    }
}