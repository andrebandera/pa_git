package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.DesistenciaRecursoInstauracaoPenalizacaoBO;
import br.gov.ms.detran.processo.administrativo.core.bo.RecursoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProtocoloRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoMovimentoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.Protocolo;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.enums.MotivoCancelamentoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoCanceladoWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

public class PAAndamento145  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento145.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 145.");
        
        RetornoExecucaoAndamentoWrapper retornoWrapper = 
                new DesistenciaRecursoInstauracaoPenalizacaoBO().definirDesistenteRecursoInstPen(em, wrapper.getProcessoAdministrativo());
        
        if (null == retornoWrapper) {
            
            retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO, 
                                                                 PAFluxoProcessoConstante.FLUXO_NORMAL, 
                                                                 PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH);
            
            RecursoMovimento recursoMovimento =
                    new RecursoMovimentoRepositorio()
                            .getRecursoParaCancelamentoPorCadastroIndevido(
                                    em, 
                                    wrapper.getProcessoAdministrativo().getNumeroProcesso());

            if (null != recursoMovimento) {

                if (MotivoCancelamentoRecursoEnum.CADASTRO_INDEVIDO.equals(recursoMovimento.getMotivoCancelamento())) {
                    
                    Protocolo protocolo = 
                            new ProtocoloRepositorio()
                                    .getProtocoloParaCancelamentoPorCadastroIndevido(
                                            em,
                                            wrapper.getProcessoAdministrativo().getId());
                    
                    if (null != protocolo) {
                        
                        List<Protocolo> listaProtocolos = new ArrayList<>();
                        
                        listaProtocolos.add(protocolo);
                        listaProtocolos.add(recursoMovimento.getProtocolo());
                        
                        new RecursoBO()
                                .cancelarRecursoPorCadastroIndevido(
                                        em, 
                                        recursoMovimento, 
                                        listaProtocolos);
                        
                        retornoWrapper.setTipo(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO);
                        retornoWrapper.setCodigoFluxo(protocolo.getFluxoFase().getPrioridadeFluxoAmparo().getFluxoProcesso().getCodigo());
                        retornoWrapper.setCodigoAndamento(protocolo.getFluxoFase().getAndamentoProcesso().getCodigo());
                        
                    }
                }
            }
        }
        
        return retornoWrapper;
        
    }

    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        RecursoCanceladoWrapper entrada = (RecursoCanceladoWrapper) wrapper.getObjetoWrapper();
        
        RecursoMovimento movimento = new RecursoMovimentoRepositorio().
                getRecursoMovimentoPorNumeroProtocoloENumeroProcessoAdministrativo(em, 
                                                                                   entrada.getNumeroProtocolo(), 
                                                                                   entrada.getNumeroProcesso());
        if(!TipoFasePaEnum.PENALIZACAO.equals(movimento.getRecurso().getTipoRecurso())){
            DetranWebUtils.applicationMessageException("Não é possível fazer o cancelamento do recurso informado.");
        }
        
        new RecursoBO().validarProtocoloCancelamento(em, entrada);
    }
}