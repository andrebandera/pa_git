/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.integracao;

import br.gov.ms.detran.comum.constantes.CodigoGrupoServico;
import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.util.MapIntegracaoMainframeBuilder2;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP07;
import br.gov.ms.detran.integracao.comum.wrapper.EnvioMainframeWrapper;
import br.gov.ms.detran.integracao.comum.wrapper.MainFrameServiceWrapper;
import br.gov.ms.detran.processo.administrativo.core.repositorio.AEMNPP07Repositorio;
import br.gov.ms.detran.processo.administrativo.wrapper.NotificacaoParaIntegracaoAEMNPP07Wrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.TipoMotivoAEMNPP07Enum;
import java.util.Collection;
import java.util.Map;
import javax.persistence.EntityManager;

import static br.gov.ms.detran.comum.util.DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO;
import static br.gov.ms.detran.comum.util.DetranStringUtil.preencherEspaco;

public class AEMNPP07BO {
    
    private static final Logger LOG = Logger.getLogger(AEMNPP07BO.class);

    private final EntityManager em;
    
    public AEMNPP07BO(EntityManager em) {
        this.em = em;
    }

    /**
     * 
     * @param obj
     * @return 
     */
    public Object executa(Object obj) {
        
        MainFrameServiceWrapper wrapper = (MainFrameServiceWrapper) obj;
        MapIntegracaoMainframeBuilder2 builder = new MapIntegracaoMainframeBuilder2(wrapper.getResultado());
        AEMNPP07 aemnpp07 = new AEMNPP07();
        builder.build(aemnpp07);

        EnvioMainframeWrapper retorno 
            = new EnvioMainframeWrapper(
                wrapper.getCodigoPrograma(),
                CodigoGrupoServico.VEICULO,
                new ParametroEnvioIntegracao()
            );
        
        try {
            
            Map<TipoMotivoAEMNPP07Enum, Collection<NotificacaoParaIntegracaoAEMNPP07Wrapper>> mapPorMotivo
                = new AEMNPP07Repositorio().getMapQuantidade(em, aemnpp07);
                
            StringBuilder totalGeralPorTipoMotivo = new StringBuilder();
            
            for (Map.Entry<TipoMotivoAEMNPP07Enum, Collection<NotificacaoParaIntegracaoAEMNPP07Wrapper>> entry : mapPorMotivo.entrySet()) {

                Collection<NotificacaoParaIntegracaoAEMNPP07Wrapper> lQuantidadePorMotivo = entry.getValue();

                StringBuilder tipoMotivoPorTipoNotificacao = new StringBuilder();

                for (NotificacaoParaIntegracaoAEMNPP07Wrapper AEMNPP07Wrapper : lQuantidadePorMotivo) {

                    tipoMotivoPorTipoNotificacao
                        .append(preencherEspaco(AEMNPP07Wrapper.getQuantidade().toString(), 5, DetranStringUtil.TipoDadoEnum.NUMERICO));
                }
                
                if(tipoMotivoPorTipoNotificacao.length() != 35) {
                    
                    tipoMotivoPorTipoNotificacao
                        .append(
                            preencherEspaco(
                                "", 
                                35 - tipoMotivoPorTipoNotificacao.length(), 
                                DetranStringUtil.TipoDadoEnum.NUMERICO
                            )
                        );
                }
                
                totalGeralPorTipoMotivo.append(tipoMotivoPorTipoNotificacao);
            }
            
            if(DetranStringUtil.ehBrancoOuNulo(totalGeralPorTipoMotivo.toString()) 
                    || totalGeralPorTipoMotivo.length() != (35 * mapPorMotivo.size())) {
                    
                DetranWebUtils.applicationMessageException("Falha ao montar string para AEMNPP07.");
            }
            
            ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();
            
            params
                .adicionarParametro(
                    DetranStringUtil.preencherEspaco(aemnpp07.getDataInicio(), 8, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
                .adicionarParametro(
                    DetranStringUtil.preencherEspaco(aemnpp07.getDataTermino(), 8, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO))
                .adicionarParametro(
                    DetranStringUtil.preencherEspaco(totalGeralPorTipoMotivo.toString(), 280, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
            
            retorno = new EnvioMainframeWrapper(wrapper.getCodigoPrograma(), CodigoGrupoServico.VEICULO, params);
            retorno.setCodigoRetorno("000");
            retorno.setMsgErro(DetranStringUtil.preencherEspaco("", DetranTamanhoConstante.MSG_ERR0, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
            
        } catch (AppException e) {

            //LOG.error("Tratado.", e);

            retorno.setCodigoRetorno("999");
            retorno.setMsgErro(preencherEspaco("NAO FOI POSSIVEL PROCESSAR A SOLICITACAO", 89, ALFA_NUMERICO));
        }

        return retorno;
    }
}