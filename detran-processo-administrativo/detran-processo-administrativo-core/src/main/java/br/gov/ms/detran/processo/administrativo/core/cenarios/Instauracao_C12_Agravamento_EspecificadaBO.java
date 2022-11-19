/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.core.cenarios;

import br.gov.ms.detran.comum.entidade.enums.inf.MotivoPenalidadeEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.StatusPontuacaoConstante;
import br.gov.ms.detran.processo.administrativo.core.repositorio.DadosInfracaoPADRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import br.gov.ms.detran.processo.administrativo.entidade.DadosInfracaoPAD;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Lillydi
 */
public class Instauracao_C12_Agravamento_EspecificadaBO  extends InstauracaoCenario implements IInstauracaoCenario {

    private static final Logger LOG = Logger.getLogger(Instauracao_C12_Agravamento_EspecificadaBO.class);
    
        
    @Override
    public IBaseEntity executarInstauracao(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {

        DadosCondutorPAD dadosCondutorPAD = wrapper.getDadosCondutorPAD();

        List<ProcessoAdministrativoWrapper> pasSuspensao = 
            new ProcessoAdministrativoRepositorio()
                .getProcessosAdministrativosSuspensaoAtivosPorMotivo(
                    em, 
                    dadosCondutorPAD.getCpf(), 
                    MotivoPenalidadeEnum.INFRACOES_ESPECIFICADAS
                );

        if (DetranCollectionUtil.ehNuloOuVazio(pasSuspensao)) {
            throw new RegraNegocioException("Não existe PA por infrações especificadas para este condutor.");
        }

        List<DadosInfracaoPAD> infracoes = new ArrayList();

        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getInfracoes())) {

            DadosInfracaoPAD dadosInfracaoPAD = buscarInfracao(em, wrapper, pasSuspensao);

            if (dadosInfracaoPAD != null) {
                
                if(verificaInfracaoEhEspecificadaEAutuadaCompetenciaDetranOuOrgaoDelegou(em, dadosInfracaoPAD)) {
                    infracoes.add(dadosInfracaoPAD);
                }
            }
        }

        if (!DetranCollectionUtil.ehNuloOuVazio(infracoes)) {
            return gravar(em, wrapper, infracoes);
        }

        return null;
    }
    
     @Override
    public List getInfracoes(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws DatabaseException {
        return new DadosInfracaoPADRepositorio().getInfracoesPorCpfEAmparoEAcaoEHistoricoInfracao(em, wrapper.getDadosCondutorPAD().getCpf(), wrapper.getApoioOrigemInstauracao());
    }

    /**
     * @param em
     * @param wrapper
     * @return
     * @throws AppException
     */
    private DadosInfracaoPAD buscarInfracao(EntityManager em, 
                                            InstaurarProcessoAdministrativoWrapper wrapper,
                                            List<ProcessoAdministrativoWrapper> pasSuspensao) throws AppException {

        List<DadosInfracaoPAD> listaInfracoes = wrapper.getInfracoes();

        if (!DetranCollectionUtil.ehNuloOuVazio(listaInfracoes)) {

            for (DadosInfracaoPAD infracao : listaInfracoes) {

                try {
                    
                    regraGeralInfracaoBO.validarRegraInfracaoCometidaAposPeriodoPermissionado(infracao);
                    regraGeralInfracaoBO.validarRegraInfracaoPrescrita(infracao, wrapper.getValorReferenciaMes());
                    regraGeralInfracaoBO.validarCodigoAcaoInstauracao(em, infracao, wrapper);

                    if (StatusPontuacaoConstante.ATIVO.equals(infracao.getStatusPontuacao())) {
                        regraGeralInfracaoBO.validarAndamentoPAReincidenteCenarios(pasSuspensao);
                    }

                    wrapper
                        .setProcesssoAdministrativoOrigem(
                            identificaProcessoAdministrativoOrigem(em, infracao, pasSuspensao)
                        );
                    
                    return infracao;

                } catch (RegraNegocioException ex) {
                    //LOG.warn("Montar Conjunto Infracoes Reincidente: {0}", ex.getMessage());
                }
            }
        }

        return null;
    }

    /**
     * 
     * @param em
     * @param dadosCondutorPAD
     * @param infracao
     * @param pasSuspensao
     * @return
     * @throws AppException 
     */
    private ProcessoAdministrativo identificaProcessoAdministrativoOrigem(
        EntityManager em, DadosInfracaoPAD infracao, List<ProcessoAdministrativoWrapper> pasSuspensao) throws AppException {
        
        ProcessoAdministrativo paOrigem = 
            regraGeralInfracaoBO.validarMesmoCodigoInfracaoPeriodo1Ano(em, infracao, pasSuspensao);
        
        if (paOrigem == null) {
            DetranWebUtils.applicationMessageException("PA.M4");
        }
        
        return paOrigem;
    }
}
