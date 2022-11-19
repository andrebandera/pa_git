package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.EditalProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PATipoCorpoAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IBloqueioBCAService;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoService;
import br.gov.ms.detran.processo.administrativo.entidade.EditalProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.PATipoCorpoAndamento;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.EditalWrapper;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.EntityManager;

public class EditalProcessoAdministrativoBO {

    private static final Logger LOG = Logger.getLogger(EditalProcessoAdministrativoBO.class);

    IProcessoAdministrativoService processoAdministrativoService;
    IBloqueioBCAService bloqueioService;

    IProcessoAdministrativoService getProcessoAdministrativoService() {
        if (processoAdministrativoService == null) {
            processoAdministrativoService = (IProcessoAdministrativoService) JNDIUtil.lookup("ejb/ProcessoAdministrativoService");
        }
        return processoAdministrativoService;
    }

    IBloqueioBCAService getBloqueioService() {
        if (bloqueioService == null) {
            bloqueioService = (IBloqueioBCAService) JNDIUtil.lookup("ejb/BloqueioBCAService");
        }
        return bloqueioService;
    }

    /**
     * @param em
     * @param entrada
     * @param urlBaseBirt
     * @return
     * @throws AppException
     */
    public EditalProcessoAdministrativo gravaEditalNotificacaoProcessoAdministrativo(
            EntityManager em, EditalWrapper entrada) throws AppException {

        NotificacaoProcessoAdministrativo notificacao
                = new NotificacaoProcessoAdministrativoRepositorio()
                        .getNotificacaoPorNumeroProcessoETipo(
                                em,
                                entrada.getNumeroProcesso(),
                                entrada.getTipo()
                        );

        if (notificacao == null) {
            DetranWebUtils.applicationMessageException("Notificação não encontrada.");
        }

        if (notificacao.getDataNotificacao() == null || Utils.compareToDate(entrada.getDataPublicacaoEdital(), notificacao.getDataNotificacao()) == -1) {
            DetranWebUtils.applicationMessageException("Data do Edital não pode ser menor que a data da notificação.");
        }

        EditalProcessoAdministrativo editalProcessoAdministrativo = new EditalProcessoAdministrativo();
        editalProcessoAdministrativo.setNotificacaoProcessoAdministrativo(notificacao);
        editalProcessoAdministrativo.setNumeroEdital(Integer.valueOf(entrada.getNumeroPortaria()));
        editalProcessoAdministrativo.setDataPublicacao(entrada.getDataPublicacaoEdital());
        editalProcessoAdministrativo.setAtivo(AtivoEnum.ATIVO);
        editalProcessoAdministrativo.setTerminoPrazoEdital(obterTerminoPrazoEdital(em, entrada));

        return new EditalProcessoAdministrativoRepositorio().insert(em, editalProcessoAdministrativo);

    }

    public void validaEntradaParaEditalNotificacaoProcessoAdministrativo(EditalWrapper entrada) throws AppException {

        if (entrada == null) {
            DetranWebUtils.applicationMessageException("Dados não informados.");
        }

        if (DetranStringUtil.ehBrancoOuNulo(entrada.getNumeroProcesso())) {
            DetranWebUtils.applicationMessageException("Número processo não informado.");
        }

        if (entrada.getTipo() == null) {
            DetranWebUtils.applicationMessageException("Tipo não informado.");
        }

        if (DetranStringUtil.ehBrancoOuNulo(entrada.getNumeroPortaria())) {
            DetranWebUtils.applicationMessageException("Número portaria não informado.");
        }

        if (entrada.getDataPublicacaoEdital() == null) {
            DetranWebUtils.applicationMessageException("Data publicação não informada.");
        }

        if (DetranStringUtil.ehBrancoOuNulo(entrada.getUsuario())) {
            DetranWebUtils.applicationMessageException("Usuário não informado.");
        }
        if (Calendar.getInstance().getTime().before(entrada.getDataPublicacaoEdital())) {
            DetranWebUtils.applicationMessageException("Data de Publicação da Portaria não pode ser maior que a data atual.");
        }
    }

    private Date obterTerminoPrazoEdital(EntityManager em, EditalWrapper entrada) throws AppException {
        PATipoCorpoAndamento tipoCorpoAndamento = null;
        if (entrada.getTipo() == TipoFasePaEnum.JARI) {
            tipoCorpoAndamento = new NotificacaoProcessoAdministrativoBO().obterTipoCorpoAndamentoJari(em, entrada.getProcessoAdministrativo());
        } else {

            tipoCorpoAndamento
                    = new PATipoCorpoAndamentoRepositorio()
                            .getTipoCorpoAndamentoPorTipoNotificacaoEApoioOrigemInstauracao(
                                    em,
                                    entrada.getTipo(),
                                    entrada.getProcessoAdministrativo().getOrigemApoio()
                            );
        }

        if (tipoCorpoAndamento == null) {
            DetranWebUtils.applicationMessageException("TipoCorpoAndamento não encontrado.");
        }

        return Utils.addDayMonth(entrada.getDataPublicacaoEdital(), tipoCorpoAndamento.getPrazoEdital());
    }
}
