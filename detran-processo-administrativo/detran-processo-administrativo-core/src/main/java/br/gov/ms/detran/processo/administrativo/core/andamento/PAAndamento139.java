/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ConsultaCnhControleRecolhimentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ConsultaCnhControleRecolhimento;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.enums.IdentificacaoRecolhimentoCnhEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento139  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento139.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 139.");
        
        RetornoExecucaoAndamentoWrapper retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
        
        List<ConsultaCnhControleRecolhimento> recolhimentoCnh
                = new ConsultaCnhControleRecolhimentoRepositorio().
                buscarRecolhimentoCnh(em, wrapper.getProcessoAdministrativo().getId());
        
        if(DetranCollectionUtil.ehNuloOuVazio(recolhimentoCnh)){
            retorno.setTipo(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO);
            retorno.setCodigoFluxo(PAFluxoProcessoConstante.FLUXO_NORMAL);
            retorno.setCodigoAndamento(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.DESBLOQUEAR_WEB);
        } else if(wrapper.getProcessoAdministrativo().isJuridico()){
            
            DadoProcessoJudicial pju = new DadoProcessoJudicialRepositorio().getDadoProcessoJudicialPorPA(em, wrapper.getProcessoAdministrativo().getId());
            
            if(pju == null){
                DetranWebUtils.applicationMessageException("Não foi possível encontrar o processo jurídico.");
            }
            
            if(IdentificacaoRecolhimentoCnhEnum.CARTORIO_JUDICIARIO.equals(pju.getIdentificacaoRecolhimentoCnh())){
                retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO,
                                                              PAFluxoProcessoConstante.FLUXO_CUMPRIMENTO_PENA);
            
            }else {
                retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                              PAFluxoProcessoConstante.FLUXO_GERAL_JURIDICO,
                                                              PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_PROTOCOLO_DEVOLUCAO_CNH);
            }
        }
        
        return retorno;
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {}
}