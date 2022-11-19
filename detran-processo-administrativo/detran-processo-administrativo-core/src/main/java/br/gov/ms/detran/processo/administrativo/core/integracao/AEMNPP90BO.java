package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP90;
import java.util.List;

/**
 *
 * @author Lillydi
 */
public class AEMNPP90BO {

    private static final Logger LOG = Logger.getLogger(AEMNPP90BO.class);
    private static final Integer TAMANHO_LISTA_EXTRATOS = 350;
    private static final Integer TOTAL_EXTRATOS_POR_INTEGRACAO = 25;
    private static final Integer TAMANHO_EXTRATO = 14;

    public AEMNPP90 executarIntegracaoAEMNPP90(String cpf, List<String> extratos) throws AppException {

        if (extratos.size() > TOTAL_EXTRATOS_POR_INTEGRACAO) {
            DetranWebUtils.applicationMessageException("Não é possível mandar mais de 30 extratos para o mainframe de uma só vez.");
        }
        ParametroEnvioIntegracao parametroEnvioIntegracao = new ParametroEnvioIntegracao();

        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(cpf, DetranTamanhoConstante.CPF, DetranStringUtil.TipoDadoEnum.NUMERICO));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(montaListaExtratos(extratos), TAMANHO_LISTA_EXTRATOS, DetranStringUtil.TipoDadoEnum.ALFA));

        IResultadoIntegracao iResultadoIntegracao = new IntegracaoMainFrameBO().executarAEMNPP90(parametroEnvioIntegracao);

        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {

            LOG.info("Erro ao executar AEMNPP90: ", iResultadoIntegracao.getMensagens().values().iterator().next());
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, "AEMNPP90");
        }
        iResultadoIntegracao.imprimirResultado();
        return (AEMNPP90) iResultadoIntegracao.getResultadoBean();
    }

    private String montaListaExtratos(List<String> extratos) {
        StringBuilder extratosMontados = new StringBuilder();

        for (String extrato : extratos) {
            extratosMontados.append(DetranStringUtil.preencherEspaco(extrato, TAMANHO_EXTRATO, DetranStringUtil.TipoDadoEnum.ALFA));
        }
        return extratosMontados.toString();
    }
}