/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.cenarios;

import br.gov.ms.detran.comum.entidade.enums.inf.MotivoPenalidadeEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.StatusPontuacaoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.RegrasGeralInstauracaoCondutorBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.DadosInfracaoPADRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import br.gov.ms.detran.processo.administrativo.entidade.DadosInfracaoPAD;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Lillydi
 */
public class Instauracao_C6_SuspensaoDiferenteArtigoBO extends InstauracaoCenario implements IInstauracaoCenario {

    private static final Logger LOG = Logger.getLogger(Instauracao_C6_SuspensaoDiferenteArtigoBO.class);

    private final RegrasGeralInstauracaoCondutorBO regraGeralCondutorBO;

    public Instauracao_C6_SuspensaoDiferenteArtigoBO() {
        super();
        this.regraGeralCondutorBO = new RegrasGeralInstauracaoCondutorBO();
    }
    
    @Override
    public IBaseEntity executarInstauracao(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {

        DadosCondutorPAD dadosCondutorPAD = wrapper.getDadosCondutorPAD();
        
        List<DadosInfracaoPAD> infracoes = new ArrayList();

        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getInfracoes())) {

            List<ProcessoAdministrativoWrapper> pasSuspensao = 
            new ProcessoAdministrativoRepositorio()
                    .getProcessosAdministrativosSuspensaoAtivosPorMotivo(em, 
                                                                         dadosCondutorPAD.getCpf(), 
                                                                         MotivoPenalidadeEnum.INFRACOES_ESPECIFICADAS);
            List<Long> pasJaExistentes 
                = validarPASuspensaoAtivoParaEspecificada(pasSuspensao);

            DadosInfracaoPAD dadosInfracaoPAD = buscarInfracao(em, wrapper, pasSuspensao, pasJaExistentes);
            
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

    /**
     * @param em
     * @param wrapper
     * @return
     * @throws AppException
     */
    private DadosInfracaoPAD buscarInfracao(
        EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper,
        List<ProcessoAdministrativoWrapper> pasSuspensao, List<Long> pasJaExistentes) throws AppException {

        List<DadosInfracaoPAD> listaInfracoes = wrapper.getInfracoes();

        if (!DetranCollectionUtil.ehNuloOuVazio(listaInfracoes)) {

            for (DadosInfracaoPAD infracao : listaInfracoes) {

                try {

                    /** Infração cometida apos periodo permissionado e não prescrita. **/
                    regraGeralInfracaoBO.validarRegraInfracaoCometidaAposPeriodoPermissionado(infracao);
                    regraGeralInfracaoBO.validarRegraInfracaoPrescrita(infracao, wrapper.getValorReferenciaMes());
                    
                    /** Checa andamentos(46,50 e 51) apenas se Status X. **/
                    if (StatusPontuacaoConstante.ATIVO.equals(infracao.getStatusPontuacao())) {
                        regraGeralInfracaoBO.validarAndamentoPAReincidenteCenarios(pasSuspensao);
                    }
                    
                    validarArtigoInfracaoDiferenteArtigoInfracaoPAReincidente(
                        em, infracao.getInfracaoCodigo(), pasJaExistentes
                    );
                    
                    regraGeralInfracaoBO.validarCodigoAcaoInstauracao(em, infracao, wrapper);

                    return infracao;

                } catch (RegraNegocioException ex) {
                    //LOG.warn("Montar Conjunto Infracoes Reincidente: {0}", ex.getMessage());
                }
            }
        }

        return null;
    }
    
    private List<Long> validarPASuspensaoAtivoParaEspecificada(List<ProcessoAdministrativoWrapper> pasJaExistentes) throws DatabaseException, RegraNegocioException {
        if (DetranCollectionUtil.ehNuloOuVazio(pasJaExistentes)) {
            throw new RegraNegocioException("Não existe P.A de suspensão para este cenário.");
        }
        return Lists.transform(pasJaExistentes, new Function<ProcessoAdministrativoWrapper, Long>() {
            @Override
            public Long apply(ProcessoAdministrativoWrapper f) {
                return f.getId();
            }
        });
    }
    
    private void validarArtigoInfracaoDiferenteArtigoInfracaoPAReincidente(EntityManager em, String infracaoCodigo, List<Long> idsPA) throws RegraNegocioException, AppException {
        if (!new ProcessoAdministrativoRepositorio().artigoInfracaoDiferenteArtigoInfracaoPAReincidente(em, infracaoCodigo, idsPA)) {
            throw new RegraNegocioException("Amparo legal do PA é igual ao da infração.");
        }
    }
}