/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.processo.administrativo.core.integracao.IntegracaoMainFrameBO;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP92;

/**
 *
 * @author Lillydi
 */
public class AEMNPP92BO {
    
    private static final Logger LOG = Logger.getLogger(AEMNPP92BO.class);
    private static final int TAMANHO_AUTO = 10;
    private static final int TAMANHO_SITUACAO = 1;
    private static final int TAMANHO_QTD = 4;

    public AEMNPP92 executarIntegracaoAEMNPP92(
        String numeroAuto, Integer situacaoPesquisa, Integer qtdInfracoes) throws AppException {
        
        ParametroEnvioIntegracao parametroEnvioIntegracao = new ParametroEnvioIntegracao();

        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(numeroAuto, TAMANHO_AUTO, DetranStringUtil.TipoDadoEnum.NUMERICO));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(situacaoPesquisa.toString(), TAMANHO_SITUACAO, DetranStringUtil.TipoDadoEnum.ALFA));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(qtdInfracoes.toString(), TAMANHO_QTD, DetranStringUtil.TipoDadoEnum.ALFA));

        IResultadoIntegracao iResultadoIntegracao 
            = new IntegracaoMainFrameBO().executarAEMNPP92(parametroEnvioIntegracao);
        
        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {

            LOG.info("Erro ao executar AEMNPP92: ", iResultadoIntegracao.getMensagens().values().iterator().next());
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, "AEMNPP92");
        }
        
        iResultadoIntegracao.imprimirResultado();
        
        return (AEMNPP92) iResultadoIntegracao.getResultadoBean();
    }
}
