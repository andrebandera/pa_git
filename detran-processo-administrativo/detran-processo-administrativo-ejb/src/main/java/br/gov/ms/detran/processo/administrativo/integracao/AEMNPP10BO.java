package br.gov.ms.detran.processo.administrativo.integracao;

import br.gov.ms.detran.processo.administrativo.core.integracao.IntegracaoMainFrameBO;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP10;

public class AEMNPP10BO {
    
    private static final Logger LOG = Logger.getLogger(AEMNPP10BO.class);
    
    public AEMNPP10 executarIntegracaoAEMNPP10(String numeroRegistro) throws AppException {
        
        ParametroEnvioIntegracao parametroEnvioIntegracao = new ParametroEnvioIntegracao();
        
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(numeroRegistro, 11, DetranStringUtil.TipoDadoEnum.NUMERICO));

        IResultadoIntegracao iResultadoIntegracao = new IntegracaoMainFrameBO().executarAEMNPP10(parametroEnvioIntegracao);

        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {

            LOG.info("Erro ao executar AEMNPP10BO: ", iResultadoIntegracao.getMensagens().values().iterator().next());
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, "AEMNPP10");
        }
        
        iResultadoIntegracao.imprimirResultado();
        
        return (AEMNPP10) iResultadoIntegracao.getResultadoBean();
    }
}