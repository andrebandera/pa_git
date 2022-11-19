package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP89;
import br.gov.ms.detran.processo.administrativo.constantes.StatusAEMNPP89Constante;

public class AEMNPP89BO {
    
    private static final Logger LOG = Logger.getLogger(AEMNPP89BO.class);
    
    private static final Integer TAMANHO_ISN = 10;
    
    public AEMNPP89 executarIntegracaoAEMNPP89(Long isn,
                                               String cpf,
                                               String situacaoEnvio,
                                               String numeroProcesso) throws AppException {
        
        ParametroEnvioIntegracao parametroEnvioIntegracao = new ParametroEnvioIntegracao();
        
        String statusRespostaMainframe = "";
        
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(isn.toString(), TAMANHO_ISN, DetranStringUtil.TipoDadoEnum.NUMERICO));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(cpf, DetranTamanhoConstante.CPF, DetranStringUtil.TipoDadoEnum.NUMERICO));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(situacaoEnvio, 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(statusRespostaMainframe, 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(numeroProcesso, 10, DetranStringUtil.TipoDadoEnum.NUMERICO));
        
        IResultadoIntegracao iResultadoIntegracao = new IntegracaoMainFrameBO().executarAEMNPP89(parametroEnvioIntegracao);
        
        if (iResultadoIntegracao == null) {
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, "AEMNPP89");
        }
        
        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {
            
            LOG.info("Erro ao executar AEMNPP89: {0}", iResultadoIntegracao.getMensagem());
            DetranWebUtils.applicationMessageException("PA.AEMNPP.M1", null, "AEMNPP89 - " + iResultadoIntegracao.getMensagem());
        }
        
        return (AEMNPP89) iResultadoIntegracao.getResultadoBean();
    }
}