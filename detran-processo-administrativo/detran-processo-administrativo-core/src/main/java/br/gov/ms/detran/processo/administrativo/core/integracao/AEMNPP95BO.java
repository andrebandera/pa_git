package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.processo.administrativo.core.integracao.IntegracaoMainFrameBO;
import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP95;

/**
 *
 * @author Lillydi
 */
public class AEMNPP95BO {

    private static final Logger LOG = Logger.getLogger(AEMNPP95BO.class);
    
    /**
     * 
     * @param cpf
     * @return
     * @throws AppException 
     */
    public AEMNPP95 executarIntegracaoAEMNPP95(String cpf) throws AppException {
        
        ParametroEnvioIntegracao parametroEnvioIntegracao = new ParametroEnvioIntegracao();

        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(cpf, DetranTamanhoConstante.CPF, DetranStringUtil.TipoDadoEnum.NUMERICO));

        IResultadoIntegracao iResultadoIntegracao = new IntegracaoMainFrameBO().executarAEMNPP95(parametroEnvioIntegracao);

        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {

            LOG.info("Erro ao executar AEMNPP95: ", iResultadoIntegracao.getMensagens().values().iterator().next());
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, "AEMNPP95");
        }
        iResultadoIntegracao.imprimirResultado();
        return (AEMNPP95) iResultadoIntegracao.getResultadoBean();
    }
}