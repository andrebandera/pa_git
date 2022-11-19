package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.Notificacao;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.ParametrizacaoValor;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoNotificacaoEnum;
import br.gov.ms.detran.comum.projeto.constantes.ParametrizacaoEnum;
import br.gov.ms.detran.comum.util.DetranEmailUtil;
import br.gov.ms.detran.comum.util.DetranEmailWrapper;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;

public class EnviarEmailPrazoNotificacoesBO {

    private static final Logger LOG = Logger.getLogger(EnviarEmailPrazoNotificacoesBO.class);
    IApoioService apoioService;

    public IApoioService getApoioService() {

        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }

        return apoioService;
    }

    public void iniciaExecucao(EntityManager em) throws AppException {
        DetranEmailWrapper email
                = new DetranEmailWrapper(
                        montarListaDestinatarios(em),
                        "PRAZO NOTIFICAÇÕES",
                        obterBody(em),
                        "text/html; charset=\"UTF-8\"");

        DetranEmailUtil.send(email);
    }

    private List montarListaDestinatarios(EntityManager em) throws AppException {
        List<String> listaEmails = new ArrayList<>();

        ParametrizacaoValor parametro
                = (ParametrizacaoValor) getApoioService().getParametroValorPorParametroEnum(ParametrizacaoEnum.EMAIL_RELATORIO_PRAZO_NOTIFICACAO);

        String[] emails = parametro.getValorAlphaNumerico().split(";");

        if (emails != null && emails.length > 0) {
            listaEmails.addAll(Arrays.asList(emails));
        }

        return listaEmails;
    }

    private String obterBody(EntityManager em) throws AppException {
        Notificacao body = (Notificacao) getApoioService().getEditalCorpoPorTipo(TipoNotificacaoEnum.PA_EMAIL_RELATORIO_PRAZO_NOTIFICACAO);
        return body.getTextoPadrao();
    }
}
