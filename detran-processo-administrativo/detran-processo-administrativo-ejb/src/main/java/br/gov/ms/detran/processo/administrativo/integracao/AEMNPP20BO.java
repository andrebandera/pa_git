/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.integracao;

import br.gov.ms.detran.comum.constantes.CodigoGrupoServico;
import br.gov.ms.detran.comum.projeto.constantes.DetranTamanhoConstante;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.util.MapIntegracaoMainframeBuilder2;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP20;
import br.gov.ms.detran.integracao.comum.wrapper.EnvioMainframeWrapper;
import br.gov.ms.detran.integracao.comum.wrapper.MainFrameServiceWrapper;
import br.gov.ms.detran.processo.administrativo.core.repositorio.AEMNPP20Repositorio;
import java.util.List;
import javax.persistence.EntityManager;

import static br.gov.ms.detran.comum.util.DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO;
import static br.gov.ms.detran.comum.util.DetranStringUtil.preencherEspaco;

public class AEMNPP20BO {
    
    private static final Logger LOG = Logger.getLogger(AEMNPP20BO.class);

    private final EntityManager em;
    
    private static final Integer MAXIMO_NUMERO_INFRACAO     = 100;
    
    private static final Integer TAMANHO_ENVIO_INFRACOES    = 400;
    
    public AEMNPP20BO(EntityManager em) {
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
        
        AEMNPP20 aemnpp20 = new AEMNPP20();
        builder.build(aemnpp20);

        EnvioMainframeWrapper retorno 
            = new EnvioMainframeWrapper(
                wrapper.getCodigoPrograma(),
                CodigoGrupoServico.VEICULO,
                new ParametroEnvioIntegracao()
            );
        
        try {
            
            List<String> lInfracao = new AEMNPP20Repositorio().getInfracoesEspecificadas(em);
            
            if(!DetranCollectionUtil.ehNuloOuVazio(lInfracao) && lInfracao.size() > MAXIMO_NUMERO_INFRACAO) {
                
                retorno.setCodigoRetorno("001");
                retorno.setMsgErro(preencherEspaco("QUANTIDADE DE INFRACOES INVALIDAS.", 89, ALFA_NUMERICO));
                
            } else {
            
                StringBuilder infracoes = new StringBuilder();

                for (String infracao : lInfracao) {
                    infracoes.append(infracao);
                }

                ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();

                params
                    .adicionarParametro(
                        DetranStringUtil.preencherEspaco(infracoes.toString(), TAMANHO_ENVIO_INFRACOES, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));

                retorno = new EnvioMainframeWrapper(wrapper.getCodigoPrograma(), CodigoGrupoServico.VEICULO, params);
                retorno.setCodigoRetorno("000");
                retorno.setMsgErro(DetranStringUtil.preencherEspaco("", DetranTamanhoConstante.MSG_ERR0, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
            }
            
        } catch (AppException e) {

            //LOG.error("Tratado.", e);

            retorno.setCodigoRetorno("999");
            retorno.setMsgErro(preencherEspaco("NAO FOI POSSIVEL PROCESSAR A SOLICITACAO", 89, ALFA_NUMERICO));
        }

        return retorno;
    }
}