package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.processo.administrativo.core.integracao.IntegracaoMainFrameBO;
import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP96;
import java.util.List;

/**
 * @author yanko.campos
 */
public class AEMNPP96BO {
    
    private static final Logger LOG = Logger.getLogger(AEMNPP96BO.class);
    
    private static final Integer TAMANHO_LISTA_EXTRATOS = 350;
    private static final Integer TAMANHO_EXTRATO = 14;
    private static final Integer TOTAL_EXTRATOS_POR_INTEGRACAO = 25;

    public AEMNPP96 executarIntegracaoAEMNPP96(String cpf, List<String> extratos) throws AppException {

        if (extratos.size() > TOTAL_EXTRATOS_POR_INTEGRACAO) {
            DetranWebUtils.applicationMessageException("Não é possível mandar mais de 10 extratos para o mainframe de uma só vez.");
        }
        
        ParametroEnvioIntegracao parametroEnvioIntegracao = new ParametroEnvioIntegracao();

        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(cpf, DetranTamanhoConstante.CPF, DetranStringUtil.TipoDadoEnum.NUMERICO));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(montaListaParametros(extratos, TAMANHO_EXTRATO), TAMANHO_LISTA_EXTRATOS, DetranStringUtil.TipoDadoEnum.ALFA));

        IResultadoIntegracao iResultadoIntegracao = new IntegracaoMainFrameBO().executarAEMNPP96(parametroEnvioIntegracao);

        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {

            LOG.info("Erro ao executar AEMNPP96: ", iResultadoIntegracao.getMensagens().values().iterator().next());
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, "AEMNPP96");
        }
        iResultadoIntegracao.imprimirResultado();
        return (AEMNPP96) iResultadoIntegracao.getResultadoBean();
    }

    private String montaListaParametros(List<String> lista, Integer tamanho) {
        
        StringBuilder extratosMontados = new StringBuilder();

        for (String extrato : lista) {
            extratosMontados.append(DetranStringUtil.preencherEspaco(extrato, tamanho, DetranStringUtil.TipoDadoEnum.ALFA));
        }
        return extratosMontados.toString();
    }
}