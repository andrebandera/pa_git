package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.CONFIRMAR_ARQUIVAMENTO_RECURSO_PROVIDO;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import static br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante.CODIGO_PA_FLUXO_PROVIMENTO;
import br.gov.ms.detran.processo.administrativo.core.bo.RecursoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MotivoAlegacaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoMovimentoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.MotivoAlegacao;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoNotificacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.ResultadoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

public class PAAndamento142  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento142.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, 
                                                             ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 142.");
        
        RetornoExecucaoAndamentoWrapper retorno = null;
        
        RecursoWSWrapper entrada = (RecursoWSWrapper) wrapper.getObjetoWrapper();
        
        RecursoMovimento recursoMovimento = 
            new RecursoMovimentoRepositorio()
                .getRecursoMovimentoPorNumeroProtocoloENumeroProcessoAdministrativo(
                    em, 
                    entrada.getNumeroProtocolo(), 
                    wrapper.getProcessoAdministrativo().getNumeroProcesso()
                );
        if(recursoMovimento == null || !TipoFasePaEnum.PENALIZACAO.equals(recursoMovimento.getRecurso().getTipoRecurso())){
            DetranWebUtils.applicationMessageException("Não foi possível recuperar recurso.");
        }
        
        if (OrigemDestinoEnum.JARI.equals(recursoMovimento.getRecurso().getDestinoFase().getOrigemDestino())) {
            
            if(entrada.getComissaoAnalise() == null){
                DetranWebUtils.applicationMessageException("Comissão Análise é obrigatório para Recurso de Penalização.");
            }
            
            if(ResultadoRecursoEnum.NAO_CONHECIMENTO.equals(entrada.getResultado())){
                if(entrada.getMotivoNaoConhecimento() == null){
                    DetranWebUtils.applicationMessageException("Motivo de não conhecimento é obrigatório.");
                }
                MotivoAlegacao motivo = new MotivoAlegacaoRepositorio().getMotivoPorCodigo(em, entrada.getMotivoNaoConhecimento());
                if(motivo == null){
                    DetranWebUtils.applicationMessageException("Motivo de não conhecimento informado não está cadastrado.");
                }
            }
        }
        
        if (!OrigemDestinoEnum.JARI.equals(recursoMovimento.getRecurso().getDestinoFase().getOrigemDestino())) {
            if (DetranStringUtil.ehBrancoOuNulo(entrada.getParecer())) {
                DetranWebUtils.applicationMessageException("Parecer é obrigatório.");
            }
        }
        
        entrada.setIdUsuario(wrapper.getIdUsuario());

        new RecursoBO().gravarResultadoRecurso(em, entrada, recursoMovimento.getRecurso());
        
        if (OrigemDestinoEnum.JARI.equals(recursoMovimento.getRecurso().getDestinoFase().getOrigemDestino())) {
            retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO,
                                                          PAFluxoProcessoConstante.FLUXO_JARI);
        } else {

            switch (entrada.getResultado()) {
                case ACOLHIDO:
                    new RecursoBO().acoesApensamentoAgravamento(em, wrapper.getProcessoAdministrativo());
                    retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                                                                                CODIGO_PA_FLUXO_PROVIMENTO,
                                                                                                                CONFIRMAR_ARQUIVAMENTO_RECURSO_PROVIDO);
                    break;

                case PROVIDO:
                    new RecursoBO().acoesApensamentoAgravamento(em, wrapper.getProcessoAdministrativo());
                    retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                                                                                 CODIGO_PA_FLUXO_PROVIMENTO,
                                                                                                                CONFIRMAR_ARQUIVAMENTO_RECURSO_PROVIDO);
                    break;

                case IMPROVIDO:
                    retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                                  PAFluxoProcessoConstante.FLUXO_NORMAL,
                                                                  PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH);
                    break;

                case NAO_ACOLHIDO:
                    retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                                  PAFluxoProcessoConstante.FLUXO_NORMAL,
                                                                  PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH);
                    break;

                case IRREGULAR:
                    if (RecursoNotificacaoAcaoEnum.CANCELAMENTO.equals(entrada.getAcao())) {

                        retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO,
                                                                      PAFluxoProcessoConstante.CODIGO_PA_FLUXO_ARQUIVAMENTO_CANCELAMENTO_ADMINISTRATIVO);

                    } else if (RecursoNotificacaoAcaoEnum.RENOTIFICAR.equals(entrada.getAcao())) {

                        new RecursoBO().desativarRecursosDoPAParaRenotificar(em, wrapper.getProcessoAdministrativo(), TipoFasePaEnum.PENALIZACAO);

                        retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                                      PAFluxoProcessoConstante.FLUXO_NORMAL,
                                                                      PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.AGUARDAR_GERACAO_NOTIFICACAO_PENALIZACAO);
                    }
                    break;

                case NAO_CONHECIMENTO:
                    retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                                  PAFluxoProcessoConstante.FLUXO_NORMAL,
                                                                  PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH);
                    break;
            }

        }
        
        return retorno;
        
    }
    
    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
    }
}