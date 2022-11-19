package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.processo.administrativo.core.integracao.IntegracaoMainFrameBO;
import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP99;

/**
 * @author yanko.campos
 */
public class AEMNPP99BO {
    
    private static final Logger LOG = Logger.getLogger(AEMNPP99BO.class);
    private static final Integer TAMANHO_EXTRATO = 14;
    private static final Integer TAMANHO_AUTUADOR = 6;
    
    public AEMNPP99 executarIntegracaoAEMNPP99(String extrato,
                                               String autuador,
                                               String autuadorUf,
                                               String placa,
                                               String placaUf) throws AppException {

        ParametroEnvioIntegracao parametroEnvioIntegracao = new ParametroEnvioIntegracao();

        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(extrato, TAMANHO_EXTRATO, DetranStringUtil.TipoDadoEnum.ALFA));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(autuador, TAMANHO_AUTUADOR, DetranStringUtil.TipoDadoEnum.NUMERICO));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(autuadorUf, DetranTamanhoConstante.UF, DetranStringUtil.TipoDadoEnum.ALFA));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(placa, DetranTamanhoConstante.PLACA, DetranStringUtil.TipoDadoEnum.ALFA));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(placaUf, DetranTamanhoConstante.UF, DetranStringUtil.TipoDadoEnum.ALFA));

        IResultadoIntegracao iResultadoIntegracao = new IntegracaoMainFrameBO().executarAEMNPP99(parametroEnvioIntegracao);

        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {

            LOG.info("Erro ao executar AEMNPP99: " + iResultadoIntegracao.getMensagens().values().iterator().next());
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, "AEMNPP99");
        }
        iResultadoIntegracao.imprimirResultado();
        return (AEMNPP99) iResultadoIntegracao.getResultadoBean();
    }
}