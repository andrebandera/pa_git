package br.gov.ms.detran.processo.administrativo.core.integracao;

import br.gov.ms.detran.processo.administrativo.enums.MotivoIntegracaoPAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoPAIntegracaoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP12;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.entidade.RegraInstaurarEnum;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lillydi
 */
public class AEMNPP12BO {

    private static final Logger LOG = Logger.getLogger(AEMNPP12BO.class);
    
    
    public void executarIntegracaoAEMNPP12(ProcessoAdministrativo pa, List<String> listaAutoInfracoes, List<String> codigosInfracao) throws AppException {
        
        String motivo = getMotivoParaAEMNPP12(pa);
        TipoPAIntegracaoEnum tipoProcesso = getTipoProcessoParaAEMNPP12(pa);
        
        switch(tipoProcesso){
            
            case PERMISSIONADO:
                executarIntegracaoAEMNPP12(pa.getCpf(), tipoProcesso.getTipo(), motivo, listaAutoInfracoes, "");
                break;

            case ESPECIFICADA:
                for (int i = 0; i < listaAutoInfracoes.size(); i++) {
                    executarIntegracaoAEMNPP12(pa.getCpf(), tipoProcesso.getTipo(), motivo, DetranCollectionUtil.montaLista(listaAutoInfracoes.get(i)), codigosInfracao.get(i));
                }
                break;

            case PONTUACAO:
                List<List<String>> lista10infracoes = Lists.partition(listaAutoInfracoes, 10);
                for (List<String> sublista : lista10infracoes) {
                    executarIntegracaoAEMNPP12(pa.getCpf(), tipoProcesso.getTipo(), motivo, sublista, "");
                }
                 break;      
        }
        
    }
    
    public AEMNPP12 executarIntegracaoAEMNPP12(String cpf, 
                                               String tipoProcesso,
                                               String motivo,
                                               List<String> autos,
                                               String codigoInfracao) throws AppException {
        
        if (DetranCollectionUtil.ehNuloOuVazio(autos) || autos.size() > 10) {
            DetranWebUtils.applicationMessageException("Não é possível executar a AEMNPP12 pois os autos estão inconsistentes.");
        }
        
        ParametroEnvioIntegracao params = new ParametroEnvioIntegracao();
        
        params.adicionarParametro(DetranStringUtil.preencherEspaco(cpf, 11, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(tipoProcesso, 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(motivo, 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(montarAutos(autos), 100, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco(codigoInfracao, 4, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 1, DetranStringUtil.TipoDadoEnum.NUMERICO));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("", 80, DetranStringUtil.TipoDadoEnum.ALFA));
        params.adicionarParametro(DetranStringUtil.preencherEspaco("A", 1, DetranStringUtil.TipoDadoEnum.ALFA));
        
        IResultadoIntegracao iResultadoIntegracao = 
                new IntegracaoMainFrameBO().executarAEMNPP12(params);
        
        if (iResultadoIntegracao == null) {
            DetranWebUtils.applicationMessageException("Falha ao executar: AEMNPP12");
        }
        
        if ((iResultadoIntegracao == null || iResultadoIntegracao.getResultadoBean() == null)
                && null != iResultadoIntegracao.getMensagens()
                && !iResultadoIntegracao.getMensagens().isEmpty()) {
            
            LOG.info("Erro ao executar AEMNPP12: {0}", iResultadoIntegracao.getMensagem());
            DetranWebUtils.applicationMessageException("Falha ao executar: AEMNPP12 - ", null, iResultadoIntegracao.getMensagem());
        }
        
        return (AEMNPP12) iResultadoIntegracao.getResultadoBean();
    }
    
    /**
     * @param lista
     * @return 
     */
    private String montarAutos(List<String> lista) {
        StringBuilder autos = new StringBuilder();
        for (String auto : lista) {
            autos.append(DetranStringUtil.preencherEspaco(auto, 10, DetranStringUtil.TipoDadoEnum.ALFA));
        }
        return autos.toString();
    }
    
    /**
     *
     * @param processoAdministrativo
     * @return
     * @throws AppException
     */
    public TipoPAIntegracaoEnum getTipoProcessoParaAEMNPP12(ProcessoAdministrativo processoAdministrativo) throws AppException {

        TipoPAIntegracaoEnum tipoProcesso = null;

        if (processoAdministrativo == null
                || processoAdministrativo.getTipo() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        if (RegraInstaurarEnum.C1.equals(processoAdministrativo.getOrigemApoio().getRegra())) {
            tipoProcesso = TipoPAIntegracaoEnum.PERMISSIONADO;
        }

        if (RegraInstaurarEnum.C10.equals(processoAdministrativo.getOrigemApoio().getRegra())
                || RegraInstaurarEnum.C11.equals(processoAdministrativo.getOrigemApoio().getRegra())) {
            tipoProcesso = TipoPAIntegracaoEnum.PONTUACAO;
        }

        if (tipoProcesso == null) {
            tipoProcesso = TipoPAIntegracaoEnum.ESPECIFICADA;
        }

        return tipoProcesso;
    }
    
    /**
     *
     * @param processoAdministrativo
     * @return
     * @throws AppException
     */
    public String getMotivoParaAEMNPP12(ProcessoAdministrativo processoAdministrativo) throws AppException {

        MotivoIntegracaoPAEnum motivoIntegracao = null;

        if (processoAdministrativo == null
                || processoAdministrativo.getOrigemApoio().getResultadoTipoProcesso() == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        if (TipoProcessoEnum.SUSPENSAO.equals(processoAdministrativo.getOrigemApoio().getResultadoTipoProcesso())
                || TipoProcessoEnum.SUSPENSAO_JUDICIAL.equals(processoAdministrativo.getOrigemApoio().getResultadoTipoProcesso())) {

            motivoIntegracao = MotivoIntegracaoPAEnum.SUSPENSAO;
        }

        if (TipoProcessoEnum.CASSACAO.equals(processoAdministrativo.getOrigemApoio().getResultadoTipoProcesso())
                || TipoProcessoEnum.CASSACAO_PERMIS_E_CANC_CNH.equals(processoAdministrativo.getOrigemApoio().getResultadoTipoProcesso())) {

            if ("263-I".equals(processoAdministrativo.getOrigemApoio().getArtigoInciso())) {
                motivoIntegracao = MotivoIntegracaoPAEnum.ARTIGO_263_I;
            }

            if ("263-II".equals(processoAdministrativo.getOrigemApoio().getArtigoInciso())) {
                motivoIntegracao = MotivoIntegracaoPAEnum.ARTIGO_263_II;
            }

            if ("148§3".equals(processoAdministrativo.getOrigemApoio().getArtigoInciso())) {
                motivoIntegracao = MotivoIntegracaoPAEnum.ARTIGO_148;
            }
        }

        if (motivoIntegracao == null) {
            DetranWebUtils.applicationMessageException("PA.M1");
        }

        return motivoIntegracao.getMotivo();
    }
    
    public void prepararExecutarAEMNPP12(List<ProcessoAdministrativoInfracao> listaInfracoes, 
                                          ProcessoAdministrativo pa) throws AppException {
        
        if (!pa.isJuridico()) {
            
            List<String> listaAutoInfracoes = new ArrayList<>();
            List<String> codigosInfracao = new ArrayList<>();

            if (!DetranCollectionUtil.ehNuloOuVazio(listaInfracoes)) {
                
                for (ProcessoAdministrativoInfracao infracao : listaInfracoes) {
                    
                    listaAutoInfracoes.add(infracao.getAutoInfracao());
                    codigosInfracao.add(infracao.getCodigoInfracao());
                }
                
                executarIntegracaoAEMNPP12(pa,
                                           listaAutoInfracoes,
                                           codigosInfracao);
            }
        }
    }
}