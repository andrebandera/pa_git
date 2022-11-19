package br.gov.ms.detran.processo.administrativo.integracao;

import br.gov.ms.detran.processo.administrativo.core.integracao.IntegracaoMainFrameBO;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP15;
import br.gov.ms.detran.processo.administrativo.wrapper.ParametrosIntegracaoBloqueioBCAWrapper;

/**
 * @author Lillydi
 */
public class AEMNPP15BO {

    private static final Logger LOG = Logger.getLogger(AEMNPP15BO.class);
    
    
    public AEMNPP15 executarIntegracaoAEMNPP15(ParametrosIntegracaoBloqueioBCAWrapper wrapper) throws AppException {
        
        ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();
        
        params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getCpf(), 11, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getCnh() == null ? null : wrapper.getCnh().toString(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getMotivoRestricao(), 1, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getNumeroRestricao(), 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getOrgaoLiberador(), 2, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getPid(), 9, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(wrapper.getSituacaoDesbloqueio(), 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        
        IResultadoIntegracao iResultadoIntegracao = 
                new IntegracaoMainFrameBO().executarAEMNPP15(params);
        
        if (iResultadoIntegracao == null) {
            DetranWebUtils.applicationMessageException("Falha ao executar: AEMNPP15");
        }
        
        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {
            
            LOG.info("Erro ao executar AEMNPP15: {0}", iResultadoIntegracao.getMensagem());
            DetranWebUtils.applicationMessageException("Falha ao executar: AEMNPP15 - ", null, iResultadoIntegracao.getMensagem());
        }
        
        return (AEMNPP15) iResultadoIntegracao.getResultadoBean();
    }
}
