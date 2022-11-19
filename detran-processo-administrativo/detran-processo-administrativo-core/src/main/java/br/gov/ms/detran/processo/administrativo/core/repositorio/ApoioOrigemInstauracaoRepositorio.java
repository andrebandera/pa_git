package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.AcaoSistemaPAEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoDataPrescricaoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoPretensaoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.generico.DetranGenericRepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.wrapper.ApoioOrigemInstauracaoWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

import br.gov.ms.detran.processo.administrativo.entidade.RegraInstaurarEnum;

/**
 * @author Lillydi
 */
public class ApoioOrigemInstauracaoRepositorio extends AbstractJpaDAORepository<ApoioOrigemInstauracao> {

    private static final Logger LOGGER = Logger.getLogger(ApoioOrigemInstauracaoRepositorio.class);

    /**
     * @param em
     * @param regra
     * @return
     * @throws DatabaseException 
     */
    public ApoioOrigemInstauracao getApoioOrigemPorRegra(EntityManager em, RegraInstaurarEnum regra) throws DatabaseException {
        return getNamedQuery(em, "ApoioOrigemInstauracao.getApoioOrigemPorRegra", regra);
    }

    /**
     * 
     * @param em
     * @return
     * @throws DatabaseException 
     */
    public List getListOrigemInstauracao(EntityManager em) throws DatabaseException {
        
        List cenariosPJU = DetranCollectionUtil.montaLista(RegraInstaurarEnum.C13,
                                                                   RegraInstaurarEnum.C14);
        
        return getListNamedQuery(em, "ApoioOrigemInstauracao.getListOrigemInstauracao", AtivoEnum.ATIVO, cenariosPJU);
    }
    
    /**
     * 
     * @param em
     * @return 
     */
    public List<ApoioOrigemInstauracaoWrapper> recuperarCenarios(EntityManager em) {

        List<ApoioOrigemInstauracaoWrapper> cenarios = new ArrayList();

        try {

            List<ApoioOrigemInstauracao> origens 
                = new ApoioOrigemInstauracaoRepositorio().getListOrigemInstauracao(em);

            for (ApoioOrigemInstauracao origem : origens) {

                Integer valorReferenciaMes 
                    = getValorReferencia(
                        em, 
                        origem.getAmparoLegal(), 
                        origem.getAcaoSistema(), 
                        TipoDataPrescricaoEnum.PA, 
                        TipoPretensaoEnum.PRETENSAO_PUNITIVA
                    );

                Integer valorReferenciaIntervalo 
                    = getValorReferencia(
                        em, 
                        origem.getAmparoLegal(), 
                        origem.getAcaoSistema(), 
                        TipoDataPrescricaoEnum.INFRACAO, 
                        TipoPretensaoEnum.PERIODO_OU_PERIODO_REINCIDENCIA
                    );

                cenarios.add(
                    new ApoioOrigemInstauracaoWrapper(
                        origem, valorReferenciaMes, valorReferenciaIntervalo
                    )
                );
            }

        } catch (DatabaseException ex) {
            LOGGER.error("Erro ao carregar os cenários", ex);
        }
        
        return cenarios;
    }

    /**
     * 
     * @param em
     * @param amparoId
     * @param acaoSistema
     * @param tipoData
     * @param tipoPretensao
     * @return 
     */
    Integer getValorReferencia(EntityManager em, Long amparoId, AcaoSistemaPAEnum acaoSistema, 
            TipoDataPrescricaoEnum tipoData, TipoPretensaoEnum tipoPretensao) {

        try {

            String sql = "SELECT MBC.Mbc_Valor_Referencia_Mes "
                    + "FROM TB_MBC_PRESCRICAO MBC "
                    + "JOIN TB_MAZ_INFRACAO_PENALIDADE MAZ ON MBC.Mbc_Infracao_Penalidade = MAZ.Maz_ID "
                    + "JOIN TB_MBT_AMPARO_LEGAL MBT ON MAZ.Maz_Amparo_Legal = MBT.Mbt_ID "
                    + "WHERE MBT.Mbt_ID = :p0 "
                    + "AND MAZ.Maz_Acao_Sistema_PA = :p1 "
                    + "AND MBC.Mbc_Tipo_Data = :p2 "
                    + "AND MBC.Mbc_Tipo_Pretensao = :p3 "
                    + "AND MBC.Ativo = :p4";
            
            Object[] params = new Object[]{
                amparoId, 
                acaoSistema.ordinal(), 
                tipoData.ordinal(),
                tipoPretensao.ordinal(),
                AtivoEnum.ATIVO.ordinal()
            };
            
            DetranGenericRepository repositorio = new DetranGenericRepository();
            Object result = repositorio.getNativeQuery(em, sql, params);

            return result != null ? Integer.parseInt(result.toString()) : null;

        } catch (DatabaseException ex) {
            LOGGER.error("Erro ao carregar os cenários", ex);
        }

        return null;
    }

    /**
     * @param em
     * @param amparoId
     * @param acaoSistema
     * @return 
     */
    public Integer getValorReferenciaIntervaloPrescricaoPorInfracao(EntityManager em, Long amparoId, AcaoSistemaPAEnum acaoSistema) {
        return getValorReferencia(em, amparoId, acaoSistema, TipoDataPrescricaoEnum.INFRACAO, TipoPretensaoEnum.PERIODO_OU_PERIODO_REINCIDENCIA);
    }

    /**
     * @param em
     * @param amparoId
     * @param acaoSistema
     * @return 
     */
    public Integer getValorReferenciaPrescricaoPorInfracao(EntityManager em, Long amparoId, AcaoSistemaPAEnum acaoSistema) {
        return getValorReferencia(em, amparoId, acaoSistema, TipoDataPrescricaoEnum.PA, TipoPretensaoEnum.PRETENSAO_PUNITIVA);
    }
    
    public ApoioOrigemInstauracao getApoioOrigemPorDescricao(EntityManager em, String descricao) throws DatabaseException{
         List<ApoioOrigemInstauracao> result =  getListNamedQuery(em, "ApoioOrigemInstauracao.getApoioOrigemPorDescricao", descricao);
          return DetranCollectionUtil.ehNuloOuVazio(result) ? null : result.get(0);
    }
    
    public ApoioOrigemInstauracao getApoioOrigemInstauracaoById(EntityManager em, Long id) throws DatabaseException{
         List<ApoioOrigemInstauracao> result =  getListNamedQuery(em, "ApoioOrigemInstauracao.ApoioOrigemInstauracaoById", id);
          return DetranCollectionUtil.ehNuloOuVazio(result) ? null : (ApoioOrigemInstauracao) result.get(0);
    }
    
    public List<ApoioOrigemInstauracao> getApoioOrigemInstauracaoPorFluxoProcesso(EntityManager em, 
            Integer from, Integer to, Long id, String descricao, AtivoEnum ativo)
            throws DatabaseException {
        Object[] params = {id,
            !DetranStringUtil.ehBrancoOuNulo(descricao) ? "%" + descricao + "%" : null,
            AtivoEnum.ATIVO};
        return getListNamedQuery(em, "ApoioOrigemInstauracao.getApoioOrigemInstauracaoNaoUtilizadoPeloFluxoProcesso", from, to, params);

    }
    
    public Long getCountApoioOrigemInstauracaoPorFluxoProcesso(EntityManager em, 
            Long id, String descricao, AtivoEnum ativo)
            throws DatabaseException {
        Object[] params = {id,
            !DetranStringUtil.ehBrancoOuNulo(descricao) ? "%" + descricao + "%" : null,
            AtivoEnum.ATIVO};
        return getCountHqlEntitySearch(em, "ApoioOrigemInstauracao.getCountApoioOrigemInstauracaoNaoUtilizadoPeloFluxoProcesso", params);
    }
    
}