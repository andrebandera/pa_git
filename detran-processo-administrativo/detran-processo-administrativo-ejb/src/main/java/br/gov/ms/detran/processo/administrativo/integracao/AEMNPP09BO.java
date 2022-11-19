package br.gov.ms.detran.processo.administrativo.integracao;

import br.gov.ms.detran.processo.administrativo.core.integracao.IntegracaoMainFrameBO;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP09;

public class AEMNPP09BO {
    
    private static final Logger LOG = Logger.getLogger(AEMNPP09BO.class);
    
    /**
     * @param opcao
     * @param numeroAuto
     * @param codigoInfracao
     * @return
     * @throws AppException 
     */
    public AEMNPP09 executarIntegracaoAEMNPP09(Integer opcao,
                                               String numeroAuto,
                                               Integer codigoInfracao) throws AppException {
        
        ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();
        
        params.adicionarParametro(DetranStringUtil.preencherEspaco(opcao != null ? opcao.toString() : "", 2, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(numeroAuto, 10, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(codigoInfracao != null ? codigoInfracao.toString() : "", 4, DetranStringUtil.TipoDadoEnum.NUMERICO));
                
        IResultadoIntegracao iResultadoIntegracao = new IntegracaoMainFrameBO().executarAEMNPP09(params);
        
        if (iResultadoIntegracao == null) {
            DetranWebUtils.applicationMessageException("Falha ao executar: AEMNPP09");
        }
        
        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {
            
            LOG.info("Erro ao executar AEMNPP09: {0}", iResultadoIntegracao.getMensagem());
            DetranWebUtils.applicationMessageException("Falha ao executar: AEMNPP09 - ", null, iResultadoIntegracao.getMensagem());
        }
        
        return (AEMNPP09) iResultadoIntegracao.getResultadoBean();
    }
}