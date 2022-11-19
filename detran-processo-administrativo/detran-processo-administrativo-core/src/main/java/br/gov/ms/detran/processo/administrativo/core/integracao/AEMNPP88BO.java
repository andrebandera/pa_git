package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP88;
import java.util.Date;

public class AEMNPP88BO {
    
    private static final Logger LOG = Logger.getLogger(AEMNPP88BO.class);
    
    public AEMNPP88 executarIntegracaoAEMNPP88(
        String nomeCondutor, Date dataPesquisa, String extrato, Date dataInfracao, String cpf) throws AppException {
        
        String dataPesquisaFormatada = Utils.formatDate(dataPesquisa, "yyyyMMdd");
        String dataInfracaoFormatada = Utils.formatDate(dataInfracao, "yyyyMMdd");
        
        ParametroEnvioIntegracao parametroEnvioIntegracao = new ParametroEnvioIntegracao();
        
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(nomeCondutor, 40, DetranStringUtil.TipoDadoEnum.ALFA));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(dataPesquisaFormatada, DetranTamanhoConstante.DATA, DetranStringUtil.TipoDadoEnum.NUMERICO));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(extrato, 14, DetranStringUtil.TipoDadoEnum.ALFA));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(dataInfracaoFormatada, DetranTamanhoConstante.DATA, DetranStringUtil.TipoDadoEnum.NUMERICO));
        parametroEnvioIntegracao.adicionarParametro(DetranStringUtil.preencherEspaco(cpf, DetranTamanhoConstante.CPF, DetranStringUtil.TipoDadoEnum.NUMERICO));
        
        IResultadoIntegracao iResultadoIntegracao = 
                new IntegracaoMainFrameBO().executarAEMNPP88(parametroEnvioIntegracao);
        
        if (iResultadoIntegracao == null) {
            DetranWebUtils.applicationMessageException("Falha ao executar: AEMNPP88");
        }
        
        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {
            
            LOG.info("Erro ao executar AEMNPP88: {0}", iResultadoIntegracao.getMensagem());
            DetranWebUtils.applicationMessageException("Falha ao executar: AEMNPP88 - ", null, iResultadoIntegracao.getMensagem());
        }
        
        return (AEMNPP88) iResultadoIntegracao.getResultadoBean();
    }
    
}
