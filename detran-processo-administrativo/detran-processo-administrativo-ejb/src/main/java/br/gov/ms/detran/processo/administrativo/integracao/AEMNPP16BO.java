package br.gov.ms.detran.processo.administrativo.integracao;

import br.gov.ms.detran.processo.administrativo.core.integracao.IntegracaoMainFrameBO;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP16;

public class AEMNPP16BO {
    
    private static final Logger LOG = Logger.getLogger(AEMNPP16BO.class);
    
    public AEMNPP16 executarIntegracaoAEMNPP16(String codigoInfracao) throws AppException {
        
        ParametroEnvioIntegracao parametroEnvioIntegracao = new ParametroEnvioIntegracao();
        
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(codigoInfracao, 5, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));

        IResultadoIntegracao iResultadoIntegracao = new IntegracaoMainFrameBO().executarAEMNPP16(parametroEnvioIntegracao);

        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {

            LOG.info("Erro ao executar AEMNPP16: ", iResultadoIntegracao.getMensagens().values().iterator().next());
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, "AEMNPP16");
        }
        
        iResultadoIntegracao.imprimirResultado();
        
        return (AEMNPP16) iResultadoIntegracao.getResultadoBean();
    }
    
}
