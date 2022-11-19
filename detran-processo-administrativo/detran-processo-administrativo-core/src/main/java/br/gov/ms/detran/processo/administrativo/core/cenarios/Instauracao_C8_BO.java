/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.cenarios;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.StatusPontuacaoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.RegrasGeralInstauracaoCondutorBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.DadosInfracaoPADRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import br.gov.ms.detran.processo.administrativo.entidade.DadosInfracaoPAD;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Lillydi
 */
public class Instauracao_C8_BO extends InstauracaoCenario implements IInstauracaoCenario {

    private static final Logger LOG = Logger.getLogger(Instauracao_C8_BO.class);

    private final RegrasGeralInstauracaoCondutorBO regraGeralCondutorBO;

    public Instauracao_C8_BO() {
        super();
        this.regraGeralCondutorBO = new RegrasGeralInstauracaoCondutorBO();
    }
    
    @Override
    public IBaseEntity executarInstauracao(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {

        DadosCondutorPAD dadosCondutorPAD = wrapper.getDadosCondutorPAD();
        
        regraGeralCondutorBO.regraCondutorNaoPossuiPAAtivoDeSuspensaoPorEspecificada(em, dadosCondutorPAD);

        List<ProcessoAdministrativoWrapper> pasSuspensao = 
            regraGeralCondutorBO
                .getPAAtivoDeSuspensaoPorPontuacao(em, dadosCondutorPAD);

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
        return new DadosInfracaoPADRepositorio().getInfracoesPorCpfEAmparoLegal(em, wrapper.getDadosCondutorPAD().getCpf(), wrapper.getApoioOrigemInstauracao());
    }
    
    private DadosInfracaoPAD buscarInfracao(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper, List<ProcessoAdministrativoWrapper> pasSuspensao) throws AppException {

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

                    

                    return infracao;

                } catch (RegraNegocioException ex) {
                    //LOG.warn("Montar Conjunto Infracoes Reincidente: {0}", ex.getMessage());
                }
            }
        }

        return null;
    }
}