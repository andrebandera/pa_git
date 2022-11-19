package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEBNH011;

/**
 * @author Lillydi
 */
public class AEBNH011BO {

    private static final Logger LOG = Logger.getLogger(AEBNH011BO.class);
    
    /**
     * @param cpf
     * @return
     * @throws AppException 
     */
    public AEBNH011 executarIntegracaoAEBNH011(String cpf) throws AppException {
        
        ParametroEnvioIntegracao parametroEnvioIntegracao = new ParametroEnvioIntegracao();
        
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(cpf, DetranTamanhoConstante.CPF, DetranStringUtil.TipoDadoEnum.NUMERICO));

        IResultadoIntegracao iResultadoIntegracao 
            = new IntegracaoMainFrameBO().executarAEBNH011(parametroEnvioIntegracao);
        
        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {

            LOG.info("Erro ao executar AEBNH011: ", iResultadoIntegracao.getMensagens().values().iterator().next());
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, "AEBNH011");
        }
        
        iResultadoIntegracao.imprimirResultado();
        
        return (AEBNH011) iResultadoIntegracao.getResultadoBean();
    }
}