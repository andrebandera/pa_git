/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.util;

import br.gov.ms.detran.comum.entidade.enums.inf.AcaoSistemaPAEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoDataPrescricaoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoPretensaoEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.generico.DetranGenericRepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ApoioOrigemInstauracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.wrapper.ApoioOrigemInstauracaoWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Christiano Carrilho.
 */
public class CenariosFactory {

    static CenariosFactory INSTANCE;
    List<ApoioOrigemInstauracaoWrapper> cenarios;

    private static final Logger LOGGER = Logger.getLogger(CenariosFactory.class);

    private CenariosFactory(EntityManager em) {
        recuperarCenarios(em);
    }

    /**
     * 
     * @param em 
     */
    final void recuperarCenarios(EntityManager em) {

        this.cenarios = new ArrayList();

        try {

            List<ApoioOrigemInstauracao> origens = new ApoioOrigemInstauracaoRepositorio().getListOrigemInstauracao(em);

            for (ApoioOrigemInstauracao origem : origens) {

                Integer valorReferenciaMes =
                        getValorReferencia(em, 
                                origem.getAmparoLegal(), 
                                origem.getAcaoSistema(), 
                                TipoDataPrescricaoEnum.PA, 
                                TipoPretensaoEnum.PRETENSAO_PUNITIVA
                        );

                Integer valorReferenciaIntervalo =
                        getValorReferencia(em, 
                                origem.getAmparoLegal(), 
                                origem.getAcaoSistema(), 
                                TipoDataPrescricaoEnum.INFRACAO, 
                                TipoPretensaoEnum.PERIODO_OU_PERIODO_REINCIDENCIA
                        );

                this.cenarios.add(
                    new ApoioOrigemInstauracaoWrapper(
                        origem, valorReferenciaMes, valorReferenciaIntervalo
                    )
                );
            }

        } catch (DatabaseException ex) {
            LOGGER.error("Erro ao carregar os cenários", ex);
        }
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

    Integer getValorReferenciaIntervaloPrescricaoPorInfracao(EntityManager em, Long amparoId, AcaoSistemaPAEnum acaoSistema) {
        return getValorReferencia(em, amparoId, acaoSistema, TipoDataPrescricaoEnum.INFRACAO, TipoPretensaoEnum.PERIODO_OU_PERIODO_REINCIDENCIA);
    }

    Integer getValorReferenciaPrescricaoPorInfracao(EntityManager em, Long amparoId, AcaoSistemaPAEnum acaoSistema) {
        return getValorReferencia(em, amparoId, acaoSistema, TipoDataPrescricaoEnum.PA, TipoPretensaoEnum.PRETENSAO_PUNITIVA);
    }

    public static CenariosFactory getInstance(EntityManager em) {
        if (INSTANCE == null) {
            INSTANCE = new CenariosFactory(em);
        }
        return INSTANCE;
    }

    public List<ApoioOrigemInstauracaoWrapper> getCenarios() {
        return this.cenarios;
    }
}
