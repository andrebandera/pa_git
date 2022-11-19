package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.processo.administrativo.core.integracao.IntegracaoMainFrameBO;
import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP94;

/**
 * @author Lillydi
 */
public class AEMNPP94BO {

    private static final Logger LOG = Logger.getLogger(AEMNPP94BO.class);
    
    private static final Integer TAMANHO_QTDE_INFRACOES = 4;
    
    public AEMNPP94 executarIntegracaoAEMNPP94(String cpf, Integer situacaoPesquisa, Long qtdInfracoes) throws AppException {

        
        ParametroEnvioIntegracao parametroEnvioIntegracao = new ParametroEnvioIntegracao();

        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(cpf, DetranTamanhoConstante.CPF, DetranStringUtil.TipoDadoEnum.NUMERICO));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(situacaoPesquisa != null ? situacaoPesquisa.toString() : "", 1, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(qtdInfracoes != null ? qtdInfracoes.toString() : "", TAMANHO_QTDE_INFRACOES, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));

        IResultadoIntegracao iResultadoIntegracao = new IntegracaoMainFrameBO().executarAEMNPP94(parametroEnvioIntegracao);

        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {

            LOG.info("Erro ao executar AEMNPP94: ", iResultadoIntegracao.getMensagens().values().iterator().next());
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, "AEMNPP94");
        }
        iResultadoIntegracao.imprimirResultado();
        return (AEMNPP94) iResultadoIntegracao.getResultadoBean();
    }
}