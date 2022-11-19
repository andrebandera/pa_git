package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP07;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.NotificacaoParaIntegracaoAEMNPP07Wrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.TipoMotivoAEMNPP07Enum;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class AEMNPP07Repositorio extends AbstractJpaDAORepository {
    
    private static final Logger LOG = Logger.getLogger(AEMNPP07Repositorio.class);

    /**
     * @param em
     * @param aemnpp07
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public Map<TipoMotivoAEMNPP07Enum, Collection<NotificacaoParaIntegracaoAEMNPP07Wrapper>> getMapQuantidade(EntityManager em, AEMNPP07 aemnpp07) throws AppException {
        
        Map<TipoMotivoAEMNPP07Enum, Collection<NotificacaoParaIntegracaoAEMNPP07Wrapper>> mapPorMotivo = null;
        
        try {
        
            String squery = "select * from FN_TDA_NOTIFICACAO_INTEGRACAO_AEMNPP07(?, ?) ORDER BY 3,1 ";

            Query query 
                = em
                    .createNativeQuery(squery)
                    .setParameter(1, Utils.getDate(aemnpp07.getDataInicio(), "yyyyMMdd"))
                    .setParameter(2, Utils.getDate(aemnpp07.getDataTermino(), "yyyyMMdd"))
                ;

            List<Object[]> result = query.getResultList();
            
            Integer INDICE_ORDEM = 0;
            Integer INDICE_TIPO_NOTIFICACAO = 1;
            Integer INDICE_MOTIVO           = 2;
            Integer INDICE_QUANTIDADE       = 3;

            List<NotificacaoParaIntegracaoAEMNPP07Wrapper> lQuantidade = new ArrayList();
            
            if (!DetranCollectionUtil.ehNuloOuVazio(result)) {

                for (Object[] notificacaoPorTipoEMotivo : result) {

                    NotificacaoParaIntegracaoAEMNPP07Wrapper quantidadePorTipoEMotivo 
                        = new NotificacaoParaIntegracaoAEMNPP07Wrapper(
                            Integer.valueOf(notificacaoPorTipoEMotivo[INDICE_ORDEM].toString()),
                            TipoFasePaEnum.valueOf(notificacaoPorTipoEMotivo[INDICE_TIPO_NOTIFICACAO].toString()),
                            TipoMotivoAEMNPP07Enum.getTipoPorCodigoMotivo(Integer.valueOf(notificacaoPorTipoEMotivo[INDICE_MOTIVO].toString())),
                            Integer.valueOf(notificacaoPorTipoEMotivo[INDICE_QUANTIDADE].toString())
                        );

                    lQuantidade.add(quantidadePorTipoEMotivo);
                }
            }
            
            if(DetranCollectionUtil.ehNuloOuVazio(lQuantidade)) {
                DetranWebUtils.applicationMessageException("Dados inválidos para recuperação.");
            }
            
            /** Transforma List -> Map **/
            mapPorMotivo = transformListToMap(lQuantidade);
            
        } catch(Exception e) {
            LOG.error("Erro sem tratamento.", e);
        }
        
        return mapPorMotivo;
    }

    /**
     * 
     * @param lQuantidade
     * @return 
     */
    private Map<TipoMotivoAEMNPP07Enum, Collection<NotificacaoParaIntegracaoAEMNPP07Wrapper>> transformListToMap(
        List<NotificacaoParaIntegracaoAEMNPP07Wrapper> lQuantidade) {
        
        ImmutableListMultimap<TipoMotivoAEMNPP07Enum, NotificacaoParaIntegracaoAEMNPP07Wrapper> listaMapeada
            = Multimaps.index(lQuantidade, new Function<NotificacaoParaIntegracaoAEMNPP07Wrapper, TipoMotivoAEMNPP07Enum>() {
                @Override
                public TipoMotivoAEMNPP07Enum apply(NotificacaoParaIntegracaoAEMNPP07Wrapper input) {
                    return input.getMotivo();
                }
            });

        ImmutableMap<TipoMotivoAEMNPP07Enum, Collection<NotificacaoParaIntegracaoAEMNPP07Wrapper>> arvoreMapeada 
            = listaMapeada.asMap();

        Predicate<Collection<NotificacaoParaIntegracaoAEMNPP07Wrapper>> predicado 
            = new Predicate<Collection<NotificacaoParaIntegracaoAEMNPP07Wrapper>>() {
                @Override
                public boolean apply(Collection<NotificacaoParaIntegracaoAEMNPP07Wrapper> t) {
                    return !DetranCollectionUtil.ehNuloOuVazio(t);
                }
            };

        return Maps.filterValues(arvoreMapeada, predicado);
    }
}