package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.RecursoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoMovimentoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoMovimento;
import br.gov.ms.detran.processo.administrativo.enums.RecursoNotificacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import javax.persistence.EntityManager;

public class PAAndamento143  extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento143.class);
    

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, 
                                                             ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 143.");
        
        RetornoExecucaoAndamentoWrapper retorno = null;
        
        RecursoWSWrapper entrada = (RecursoWSWrapper) wrapper.getObjetoWrapper();
        
        RecursoMovimento recursoMovimento = 
            new RecursoMovimentoRepositorio()
                .getRecursoMovimentoPorNumeroProtocoloENumeroProcessoAdministrativo(
                    em, 
                    entrada.getNumeroProtocolo(), 
                    wrapper.getProcessoAdministrativo().getNumeroProcesso()
                );
        if(recursoMovimento == null || !TipoFasePaEnum.ENTREGA_CNH.equals(recursoMovimento.getRecurso().getTipoRecurso())){
            DetranWebUtils.applicationMessageException("Não foi possível recuperar recurso.");
        }
        
        entrada.setIdUsuario(wrapper.getIdUsuario());
        
        if (DetranStringUtil.ehBrancoOuNulo(entrada.getParecer())) {
            DetranWebUtils.applicationMessageException("Parecer é obrigatório.");
        }

        new RecursoBO().gravarResultadoRecurso(em, entrada, recursoMovimento.getRecurso());
        
        switch (entrada.getResultado()) {
            case ACOLHIDO:
                new RecursoBO().acoesApensamentoAgravamento(em, wrapper.getProcessoAdministrativo());
                retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO,
                                                              PAFluxoProcessoConstante.CODIGO_PA_FLUXO_PROVIMENTO);
                break;
            
            case PROVIDO:
                new RecursoBO().acoesApensamentoAgravamento(em, wrapper.getProcessoAdministrativo());
                retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO,
                                                              PAFluxoProcessoConstante.CODIGO_PA_FLUXO_PROVIMENTO);
                break;
            
            case IMPROVIDO:
                retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                              PAFluxoProcessoConstante.FLUXO_NORMAL,
                                                              PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.INCLUIR_BLOQUEIO_WEB);
                break;
            
            case NAO_ACOLHIDO:
                retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                              PAFluxoProcessoConstante.FLUXO_NORMAL,
                                                              PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.INCLUIR_BLOQUEIO_WEB);
                break;
            
            case IRREGULAR:
                if (RecursoNotificacaoAcaoEnum.CANCELAMENTO.equals(entrada.getAcao())) {
                
                    retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO,
                                                                  PAFluxoProcessoConstante.CODIGO_PA_FLUXO_ARQUIVAMENTO_CANCELAMENTO_ADMINISTRATIVO);
                
                } else if (RecursoNotificacaoAcaoEnum.RENOTIFICAR.equals(entrada.getAcao())) {

                    new RecursoBO().desativarRecursosDoPAParaRenotificar(em, wrapper.getProcessoAdministrativo(), TipoFasePaEnum.ENTREGA_CNH);
                    
                    retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO,
                                                                  PAFluxoProcessoConstante.FLUXO_NORMAL,
                                                                  PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH);
                }
                break;
        
            case NAO_CONHECIMENTO:
                retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.INICIA_FLUXO,
                                                              PAFluxoProcessoConstante.CODIGO_PA_FLUXO_NOTIFICACAO_NAO_CONHECIMENTO);
                break;
        }


        return retorno;
        
        
    }
    
    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
    }
}