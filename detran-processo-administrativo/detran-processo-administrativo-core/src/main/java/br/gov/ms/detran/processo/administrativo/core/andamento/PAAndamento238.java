/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoOrigemRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoOrigem;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class PAAndamento238  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento238.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 238. PA {0}", wrapper.getProcessoAdministrativo().getNumeroProcesso());
        
        PAFluxoOrigem fluxoOrigem  = new PAFluxoOrigemRepositorio().getPAFluxoOrigemPorIDApoioOrigemInstauracao(em, wrapper.getProcessoAdministrativo().getOrigemApoio().getId());
        
        
        return new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO, 
                                                                                                fluxoOrigem.getFluxoProcesso().getCodigo(), 
                                                                                                AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH);
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        NotificaProcessoAdministrativoWrapper entrada = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();
        
        if(entrada == null || !TipoFasePaEnum.JARI.equals(entrada.getTipo())){
            DetranWebUtils.applicationMessageException("Tipo da Notificação inválido.");
        }
    
    }
}