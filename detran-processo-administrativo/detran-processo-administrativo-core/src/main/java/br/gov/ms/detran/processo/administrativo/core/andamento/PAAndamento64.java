package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoNotificacaoEnum;
import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.MovimentoCnhBO;
import br.gov.ms.detran.processo.administrativo.core.bo.ProtocoloBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ConsultaCnhControleRecolhimentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoCnhRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPenalidadeProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProtocoloRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ResultadoProvaPARepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ConsultaCnhControleRecolhimento;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.Protocolo;
import br.gov.ms.detran.processo.administrativo.entidade.ResultadoProvaPA;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

public class PAAndamento64 extends ExecucaoAndamento {
    
    private static final Logger LOG = Logger.getLogger(PAAndamento64.class);
    
    private IApoioService apoioService;
    
    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, 
                                                             ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
        
        LOG.debug("Início Andamento 64.");
        
        RetornoExecucaoAndamentoWrapper retorno = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
        
        List<ConsultaCnhControleRecolhimento> recolhimentoCnh = 
                new ConsultaCnhControleRecolhimentoRepositorio().
                buscarRecolhimentoCnh(em, wrapper.getProcessoAdministrativo().getId());
        
        PAPenalidadeProcesso penalidade = new PAPenalidadeProcessoRepositorio().getPenalidadePorPA(em, wrapper.getProcessoAdministrativo().getId());
        Date dataAtual = Calendar.getInstance().getTime();
        
        if (!DetranCollectionUtil.ehNuloOuVazio(recolhimentoCnh)) {
            
            MovimentoCnh movimentoEntrega
                    = new MovimentoCnhRepositorio()
                    .getMovimentoPorProcessoAdministrativoEAcao(
                            em,
                            wrapper.getProcessoAdministrativo().getId(),
                            AcaoEntregaCnhEnum.ENTREGA);

            if (null == movimentoEntrega) {
                DetranWebUtils.applicationMessageException("Erro ao recuperar a entrega da CNH.");
            }

            CnhSituacaoEntrega situacaoDevolucao
                    = (CnhSituacaoEntrega) getApoioService()
                    .getSituacaoEntregaCnh(
                            movimentoEntrega.getCnhControle(),
                            AcaoEntregaCnhEnum.DEVOLUCAO);
            
            if (situacaoDevolucao == null) {
                DetranWebUtils.applicationMessageException("A CNH ainda não foi devolvida ao condutor.");
            }
            
            MovimentoCnh entregaMovimento = new MovimentoCnhRepositorio().
                    getMovimentoPorProcessoAdministrativoEAcao(em, wrapper.getProcessoAdministrativo().getId(), AcaoEntregaCnhEnum.DEVOLUCAO);
            if (entregaMovimento != null) {
                entregaMovimento.getProtocolo().setAtivo(AtivoEnum.DESATIVADO);
                entregaMovimento.setAtivo(AtivoEnum.DESATIVADO);
                new MovimentoCnhRepositorio().update(em, entregaMovimento);
                new ProtocoloRepositorio().update(em, entregaMovimento.getProtocolo());
            }

            Protocolo protocolo
                    = new ProtocoloBO()
                    .gravar(
                            em,
                            new PAFluxoFaseRepositorio().getFluxoFaseDoProcessoAdministrativo(em, wrapper.getProcessoAdministrativo()),
                            TipoNotificacaoEnum.PROTOCOLO_PA_DEVOLUCAO_CNH,
                            wrapper.getProcessoAdministrativo(),
                            situacaoDevolucao.getTemplateProtocolo());

            new MovimentoCnhBO()
                    .gravarMovimentoCnh(
                            em,
                            situacaoDevolucao.getCnhControle(),
                            movimentoEntrega.getProcessoOriginal(),
                            protocolo,
                            AcaoEntregaCnhEnum.DEVOLUCAO);
        }
        
        if(DetranCollectionUtil.ehNuloOuVazio(recolhimentoCnh)){
            if(penalidade.getDataFimPenalidade().after(dataAtual)){
                DetranWebUtils.applicationMessageException("A penalidade ainda não foi cumprida.");
            } 
            
            if(TipoProcessoEnum.SUSPENSAO.equals(wrapper.getProcessoAdministrativo().getTipo())){
                ResultadoProvaPA resultado
                        = new ResultadoProvaPARepositorio()
                        .getResultadoProvaPAAtivoPorProcessoAdministrativo(em, wrapper.getProcessoAdministrativo());

                if (resultado == null) {
                    DetranWebUtils.applicationMessageException("Não existe prova para este PA.");
                }
            }
            
            retorno.setTipo(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO);
            retorno.setCodigoFluxo(PAFluxoProcessoConstante.FLUXO_NORMAL);
            retorno.setCodigoAndamento(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.DESBLOQUEAR_WEB);
        }
        
        return retorno;
    }

    @Override
    public void validaEspecifico(EntityManager em,ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {
    
    }
}