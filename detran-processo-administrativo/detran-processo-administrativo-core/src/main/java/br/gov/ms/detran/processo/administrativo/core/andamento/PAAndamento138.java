package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
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
import br.gov.ms.detran.processo.administrativo.core.repositorio.AndamentoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoBloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoCnhRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.pju.DadoProcessoJudicialRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.AndamentoProcessoAdministrativoConsulta;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.enums.IdentificacaoRecolhimentoCnhEnum;
import br.gov.ms.detran.processo.administrativo.enums.RequisitoRecursoBloqueioEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoMovimentoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.EntregaCnhWrapper;
import java.util.List;
import javax.persistence.EntityManager;

public class PAAndamento138 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento138.class);
    
    private IApoioService apoioService;

    public IApoioService getApoioService() {

        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }

        return apoioService;
    }

    @Override
    public RetornoExecucaoAndamentoWrapper executaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {

        RetornoExecucaoAndamentoWrapper wrapper = 
                new RetornoExecucaoAndamentoWrapper(andamentoEspecificoWrapper.getProcessoAdministrativo());

        LOG.debug("Andamento 138 - EXECUTADO");

        wrapper.setTipo(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);

        defineAcaoProcessoAdministrativo(em, wrapper);

        defineAcaoProcessoJuridico(em, wrapper);

        return wrapper;
    }

    /**
     * @param em
     * @param wrapper
     * @throws AppException 
     */
    private void defineAcaoProcessoAdministrativo(EntityManager em, RetornoExecucaoAndamentoWrapper wrapper) throws AppException {

        if (wrapper.getProcessoAdministrativo().isPontuacao()) {

            // Se for desentranhamento, não existe próximo andamento. Executa mudança de fluxo e andamento.
            AndamentoProcessoAdministrativoConsulta proximoAndamento = 
                    new AndamentoProcessoAdministrativoRepositorio()
                            .getProximoAndamento(em, wrapper.getProcessoAdministrativo());
            
            if (TipoProcessoEnum.CASSACAO.equals(wrapper.getProcessoAdministrativo().getTipo())) {
                wrapper.setTipo(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO);
                wrapper.setCodigoFluxo(PAFluxoProcessoConstante.FLUXO_NORMAL);
                wrapper.setCodigoAndamento(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_PROTOCOLO_DEVOLUCAO_CNH);
            }

            if (TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(wrapper.getProcessoAdministrativo().getTipo())) {
                wrapper.setTipo(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO);
                wrapper.setCodigoFluxo(PAFluxoProcessoConstante.FLUXO_GERAL_PERMISSIONADO);
                wrapper.setCodigoAndamento(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_PROTOCOLO_DEVOLUCAO_CNH);
            }

            if (proximoAndamento == null || proximoAndamento.getCodigoAndamentoProcesso() == null) {
                if(TipoProcessoEnum.SUSPENSAO.equals(wrapper.getProcessoAdministrativo().getTipo())){
                    wrapper.setTipo(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO);
                    wrapper.setCodigoFluxo(PAFluxoProcessoConstante.FLUXO_NORMAL);
                    wrapper.setCodigoAndamento(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_REALIZACAO_PROVA_CURSO);

                }
            }
                
        }
    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper andamentoEspecificoWrapper) throws AppException {

        EntregaCnhWrapper ecWrapper = (EntregaCnhWrapper) andamentoEspecificoWrapper.getObjetoWrapper();

        if (ecWrapper == null || !AcaoEntregaCnhEnum.ENTREGA.equals(ecWrapper.getAcao())) {
            DetranWebUtils.applicationMessageException("Ação inválida.");
        }
    }

    /**
     * @param em
     * @param wrapper
     * @throws AppException 
     */
    private void defineAcaoProcessoJuridico(EntityManager em, RetornoExecucaoAndamentoWrapper wrapper) throws AppException {

        if (wrapper.getProcessoAdministrativo().isJuridico()) {

            DadoProcessoJudicial dadoJuridico = 
                    new DadoProcessoJudicialRepositorio().
                            getDadoProcessoJudicialPorPA(em, wrapper.getProcessoAdministrativo().getId());

            if (dadoJuridico == null || dadoJuridico.getRequisitoCursoBloqueio() == null) {
                DetranWebUtils.applicationMessageException("Dados do Processo Judicial inválidos.");
            }
            
            MovimentoCnh movimentoCnh = 
                    new MovimentoCnhRepositorio()
                            .getMovimentoCnhParaDesentranhamentoPorCpfCondutor(em, wrapper.getProcessoAdministrativo().getCpf());
            
            if (IdentificacaoRecolhimentoCnhEnum.DETRAN.equals(dadoJuridico.getIdentificacaoRecolhimentoCnh())) {
                
                defineFluxoCnhDETRAN(em, wrapper, movimentoCnh, dadoJuridico);
                
            } else if (!RequisitoRecursoBloqueioEnum.SEM_CURSO.equals(dadoJuridico.getRequisitoCursoBloqueio())) {
                
                wrapper.setTipo(TipoRetornoAndamentoEnum.INICIA_FLUXO);
                wrapper.setCodigoFluxo(PAFluxoProcessoConstante.CURSO_EXAME);
                
            }
        }
    }

    public void defineFluxoCnhDETRAN(EntityManager em, RetornoExecucaoAndamentoWrapper wrapper, MovimentoCnh movimentoCnh, DadoProcessoJudicial dadoJuridico) throws AppException {
        BloqueioBCA bloqueioBCA
                = new MovimentoBloqueioBCARepositorio()
                        .getBloqueioPorProcessoAdministrativoETipoAtivoParaDesistencia(
                                em,
                                wrapper.getProcessoAdministrativo().getId(),
                                TipoMovimentoBloqueioBCAEnum.BLOQUEIO);

        if (null != bloqueioBCA) {

            List cnhSituacaoEntrega
                    = getApoioService()
                            .getCnhSituacaoEntregaPorCnhControle(
                                    movimentoCnh.getCnhControle(),
                                    AcaoEntregaCnhEnum.ENTREGA);

            if (!DetranCollectionUtil.ehNuloOuVazio(cnhSituacaoEntrega)) {

                if (((CnhSituacaoEntrega) cnhSituacaoEntrega.get(0)).getDataEntrega().before(bloqueioBCA.getDataInicio())) {

                    wrapper.setTipo(TipoRetornoAndamentoEnum.INICIA_FLUXO);
                    wrapper.setCodigoFluxo(PAFluxoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO_JURIDICO);
                    
                } else if (RequisitoRecursoBloqueioEnum.SEM_CURSO.equals(dadoJuridico.getRequisitoCursoBloqueio())) {

                    wrapper.setTipo(TipoRetornoAndamentoEnum.MUDANCA_FLUXO_ANDAMENTO);
                    wrapper.setCodigoFluxo(PAFluxoProcessoConstante.FLUXO_GERAL_JURIDICO);
                    wrapper.setCodigoAndamento(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_PROTOCOLO_DEVOLUCAO_CNH);

                } else if (!RequisitoRecursoBloqueioEnum.SEM_CURSO.equals(dadoJuridico.getRequisitoCursoBloqueio())) {

                    wrapper.setTipo(TipoRetornoAndamentoEnum.INICIA_FLUXO);
                    wrapper.setCodigoFluxo(PAFluxoProcessoConstante.NOTIFICACAO_CURSO_EXAME);

                }
            }
        }
    }
}