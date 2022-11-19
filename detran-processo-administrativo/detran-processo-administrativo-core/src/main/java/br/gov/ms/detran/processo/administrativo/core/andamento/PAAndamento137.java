/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.ParteProcessoJuridicoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento137 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento137.class);

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        LOG.debug("Início Andamento 137.");

        RetornoExecucaoAndamentoWrapper retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
        
        if(wrapper.getProcessoAdministrativo().isJuridico()){
            retorno = defineRetornoParaProcessoJuridico(em, wrapper.getProcessoAdministrativo());
        }
        
        return retorno;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    }

    private RetornoExecucaoAndamentoWrapper defineRetornoParaProcessoJuridico(EntityManager em, ProcessoAdministrativo processoAdministrativo) throws AppException {
        
        RetornoExecucaoAndamentoWrapper retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
        
        DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, processoAdministrativo.getId());
        
        if(pju==null){
            DetranWebUtils.applicationMessageException("Não foi possível recuperar os dados do Processo Judicial.");
        }
        
        if(!ParteProcessoJuridicoEnum.CONDUTOR.equals(pju.getParteProcessoJuridico())){
            retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO, PAFluxoProcessoConstante.FLUXO_CANDIDATO_CIDADAO);
        }
        
        return retorno;
    }
}
