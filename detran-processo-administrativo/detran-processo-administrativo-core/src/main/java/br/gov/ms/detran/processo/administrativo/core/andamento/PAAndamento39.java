package br.gov.ms.detran.processo.administrativo.core.andamento;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.constantes.PAFluxoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.DesistenciaEntregaCnhBO;
import br.gov.ms.detran.processo.administrativo.core.bo.NotificacaoProcessoAdministrativoBO;
import br.gov.ms.detran.processo.administrativo.core.bo.recursoonline.RecursoOnlineBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.MovimentoCnhRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.RegraInstaurarEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoRetornoAndamentoEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.RetornoExecucaoAndamentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;

import javax.persistence.EntityManager;

public class PAAndamento39 extends ExecucaoAndamento {

    private static final Logger LOG = Logger.getLogger(PAAndamento39.class);

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

        LOG.debug("Início Andamento 39.");

        RetornoExecucaoAndamentoWrapper retornoWrapper
                = new DesistenciaEntregaCnhBO().definirDesistenteEntregaCnh(em, wrapper.getProcessoAdministrativo());

        if (null == retornoWrapper) {

            NotificaProcessoAdministrativoWrapper entradaWrapper = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();

            if (entradaWrapper == null
                || DetranStringUtil.ehBrancoOuNulo(entradaWrapper.getNumeroProcesso())
                || entradaWrapper.getTipo() == null) {

                DetranWebUtils.applicationMessageException("Número Processo e tipo da Notificação são obrigatórios.");

            }

            MovimentoCnh movimentoCnh = verificaSeNotificaParaDesentranhamento(entradaWrapper, wrapper.getProcessoAdministrativo(), em);

            if (TipoFasePaEnum.DESENTRANHAMENTO.equals(entradaWrapper.getTipo())) {

                retornoWrapper
                        = new RetornoExecucaoAndamentoWrapper(
                                TipoRetornoAndamentoEnum.INICIA_FLUXO,
                                PAFluxoProcessoConstante.FLUXO_DESENTRANHAMENTO_CODIGO);

            } else {

                NotificacaoProcessoAdministrativo notificacao
                        = new NotificacaoProcessoAdministrativoBO().notificar(em, wrapper.getProcessoAdministrativo(), TipoFasePaEnum.ENTREGA_CNH);

                if(!wrapper.getProcessoAdministrativo().isJuridico())
                    new NotificacaoProcessoAdministrativoBO().gravarNotificacaoComplemento(em, entradaWrapper, notificacao);

                retornoWrapper = new RetornoExecucaoAndamentoWrapper(TipoRetornoAndamentoEnum.PROXIMO_ANDAMENTO);
            }

        }

        return retornoWrapper;

    }

    @Override
    public void validaEspecifico(EntityManager em, ExecucaoAndamentoEspecificoWrapper wrapper) throws AppException {

        NotificaProcessoAdministrativoWrapper entradaWrapper = (NotificaProcessoAdministrativoWrapper) wrapper.getObjetoWrapper();

        if (entradaWrapper == null
            || DetranStringUtil.ehBrancoOuNulo(entradaWrapper.getNumeroProcesso())
            || entradaWrapper.getTipo() == null) {

            DetranWebUtils.applicationMessageException("Número Processo e tipo da Notificação são obrigatórios.");

        }

        if (!TipoFasePaEnum.ENTREGA_CNH.equals(entradaWrapper.getTipo())) {
            DetranWebUtils.applicationMessageException("Tipo da Notificação inválido.");
        }

        if (wrapper.getProcessoAdministrativo().isPontuacao()
                && !RegraInstaurarEnum.C1.equals(wrapper.getProcessoAdministrativo().getOrigemApoio().getRegra())
                && (entradaWrapper.getDataPublicacaoPortaria() == null || DetranStringUtil.ehBrancoOuNulo(entradaWrapper.getNumeroPortaria()))) {

            DetranWebUtils.applicationMessageException("Número Portaria e sua data de publicação são obrigatórios.");
        }

        new RecursoOnlineBO().validarRecursoOnlineEmBackOffice(em, wrapper.getProcessoAdministrativo());
    }

    /**
     * @param entrada
     * @return
     * @throws AppException
     */
    private MovimentoCnh verificaSeNotificaParaDesentranhamento(NotificaProcessoAdministrativoWrapper entrada,
                                                                ProcessoAdministrativo processoAdministrativo,
                                                                EntityManager em) throws AppException {

        MovimentoCnh movimentoCnh = null;

        if (TipoFasePaEnum.ENTREGA_CNH.equals(entrada.getTipo())) {

            if (new ProcessoAdministrativoRepositorio().existeMaisDeUmProcessoAdministrativoParaCondutor(em, processoAdministrativo.getCpf())) {

                movimentoCnh
                        = new MovimentoCnhRepositorio()
                                .getMovimentoCnhParaDesentranhamentoPorCpfCondutor(
                                        em,
                                        processoAdministrativo.getCpf());
            }

            if (movimentoCnh != null) {
                entrada.setTipo(TipoFasePaEnum.DESENTRANHAMENTO);
            }
        }

        return movimentoCnh;
    }
}
