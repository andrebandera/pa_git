/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.cenarios;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.PontosInfracaoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.StatusPontuacaoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.RegrasGeralInstauracaoCondutorBO;
import br.gov.ms.detran.processo.administrativo.entidade.DadosInfracaoPAD;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author desenvolvimento
 */
public class Instauracao_C1_PermissionadoBO extends InstauracaoCenario implements IInstauracaoCenario {

    private static final Logger LOG = Logger.getLogger(Instauracao_C1_PermissionadoBO.class);

    private final RegrasGeralInstauracaoCondutorBO regraGeralCondutorBO;
    
    private static final Integer PERIODO_PERMISSIONADO = 364;
    
    private static final String INFRACAO_6920 = "6920";

    public Instauracao_C1_PermissionadoBO() {
        super();
        this.regraGeralCondutorBO = new RegrasGeralInstauracaoCondutorBO();
    }

    /**
     *
     * @param em
     * @param wrapper
     * @return
     * @throws AppException
     */
    @Override
    public IBaseEntity executarInstauracao(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {

        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getInfracoes())) {

            List<DadosInfracaoPAD> infracoesReincidente = getInfracoesParaPermissionado(wrapper);

            if (!DetranCollectionUtil.ehNuloOuVazio(infracoesReincidente)) {
                return gravar(em, wrapper, infracoesReincidente);
            }
        }
        
        return null;
    }

    /**
     * 
     * Retorna a lista com as infrações que atendem o cenario permissionado.
     * 
     * @param em
     * @param wrapper
     * @return
     * @throws RegraNegocioException
     * @throws AppException 
     */
    public List getInfracoesParaPermissionado(InstaurarProcessoAdministrativoWrapper wrapper) throws RegraNegocioException, AppException {
        
        List<DadosInfracaoPAD> infracoesPermissionado = new ArrayList();

        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getInfracoes())) {
            List<DadosInfracaoPAD> infracoesComPontuacaoValidas = new ArrayList<>();

            for (DadosInfracaoPAD infracao : wrapper.getInfracoes()) {

                try {

                    /** Infração período permissionado. **/
                    validarRegraInfracaoCometidaPeriodoPermissionado(infracao);
                    
                    /** Infracao 6920. **/                   
                    validarInfracao6920(infracao);

                    infracoesComPontuacaoValidas.add(infracao);

                } catch (RegraNegocioException ex) {
                    //LOG.warn("Montar Conjunto Infracoes Permissionado: {0}", ex.getMessage());
                }
            }

            infracoesPermissionado = getPontuacaoPermissionado(infracoesComPontuacaoValidas);
        }

        return infracoesPermissionado;
    }

    /**
     * 
     * @param em
     * @param infracao
     * @throws RegraNegocioException
     */
    public void validarRegraInfracaoCometidaPeriodoPermissionado(DadosInfracaoPAD infracao) throws RegraNegocioException {

        Date dataValidadePeriodoPermissionado 
            = Utils.addDayMonth(infracao.getCpfCondutor().getDataPrimeiraHabilitacao(), PERIODO_PERMISSIONADO);

        if (!infracao.getDataInfracao().before(dataValidadePeriodoPermissionado)) {
            
            throw new RegraNegocioException(
                "Infração fora do período permissionado. "
                    + "" + infracao.getIsn()
                    + "" + infracao.getCpfCondutor().getCpf()
                    + "" + infracao.getAuto()
                    + "" + infracao.getAutuador()
            );
        }
    }

    /**
     * 
     * @param em
     * @param infracao
     * @throws RegraNegocioException
     */
    public void validarInfracao6920(DadosInfracaoPAD infracao) throws RegraNegocioException {

        if (StatusPontuacaoConstante.ATIVO.equals(infracao.getStatusPontuacao()) 
                && INFRACAO_6920.equals(infracao.getInfracaoCodigo())) {
            
            throw new RegraNegocioException(
                "Infração descartada para status pontuação ativa. "
                    + "" + infracao.getIsn()
                    + "" + infracao.getCpfCondutor().getCpf()
                    + "" + infracao.getAuto()
                    + "" + infracao.getAutuador()
            );
        }
    }

    /**
     * 
     * @param em
     * @param listaInfracoes
     * @return 
     */
    public List<DadosInfracaoPAD> getPontuacaoPermissionado(List<DadosInfracaoPAD> listaInfracoes) {
        
        List<DadosInfracaoPAD> infracoesValidas = new ArrayList();

        Integer pontuacao = 0;

        if (!DetranCollectionUtil.ehNuloOuVazio(listaInfracoes)) {

            Iterator iterator = listaInfracoes.iterator();
            
            do {

                DadosInfracaoPAD item = (DadosInfracaoPAD) iterator.next();

                if (!PontosInfracaoConstante.LEVE.equals(item.getQdePontosInfracao())) {
                    infracoesValidas.add(item);
                    pontuacao += item.getQdePontosInfracao();
                }

            } while (iterator.hasNext() && !validarPontuacao(pontuacao));
        }
        
        return validarPontuacao(pontuacao) ? infracoesValidas : null;
    }

    /**
     * 
     * @param pontuacao
     * @return 
     */
    private Boolean validarPontuacao(Integer pontuacao) {

        return PontosInfracaoConstante.GRAVE.equals(pontuacao)
                || PontosInfracaoConstante.GRAVISSIMA.equals(pontuacao)
                || pontuacao.equals(PontosInfracaoConstante.MEDIA * 2);
    }
}