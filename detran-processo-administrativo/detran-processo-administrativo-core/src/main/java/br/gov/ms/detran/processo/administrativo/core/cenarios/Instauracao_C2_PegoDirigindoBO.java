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
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.constantes.StatusPontuacaoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.RegrasGeralInstauracaoCondutorBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.BloqueioBCARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.DadosInfracaoPADRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
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
 * @author desenvolvimento
 */
public class Instauracao_C2_PegoDirigindoBO extends InstauracaoCenario implements IInstauracaoCenario {

    private static final Logger LOG = Logger.getLogger(Instauracao_C2_PegoDirigindoBO.class);

    private final RegrasGeralInstauracaoCondutorBO regraGeralCondutorBO;

    public Instauracao_C2_PegoDirigindoBO() {
        super();
        this.regraGeralCondutorBO = new RegrasGeralInstauracaoCondutorBO();
    }

    @Override
    public List getInfracoes(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws DatabaseException {
        return new DadosInfracaoPADRepositorio().getInfracoesPorCpfEAmparoEIndiceReincidenciaMAZ(em, wrapper.getDadosCondutorPAD().getCpf(), wrapper.getApoioOrigemInstauracao());
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

        DadosCondutorPAD dadosCondutorPAD = wrapper.getDadosCondutorPAD();
        
        List<ProcessoAdministrativoWrapper> pasSuspensao = 
            identificaProcessoAdministrativoOrigem(em, dadosCondutorPAD);

        List<DadosInfracaoPAD> infracoesReincidente = new ArrayList();

        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getInfracoes())) {

            DadosInfracaoPAD retorno = buscarInfracaoParaCondutorPegoDirigindo(em, wrapper, pasSuspensao);

            wrapper.setProcesssoAdministrativoOrigem(pasSuspensao.get(0).getEntidade());

            if (retorno != null) {
                infracoesReincidente.add(retorno);
            }
        }

        if (!DetranCollectionUtil.ehNuloOuVazio(infracoesReincidente)) {
            return gravar(em, wrapper, infracoesReincidente);
        }
        
        return null;
    }

    /**
     * Retorna infrações validas para o cenário reincidente.
     *
     * @param em
     * @param wrapper
     * @param pasSuspensao
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    public DadosInfracaoPAD buscarInfracaoParaCondutorPegoDirigindo(EntityManager em,
            InstaurarProcessoAdministrativoWrapper wrapper, List<ProcessoAdministrativoWrapper> pasSuspensao) throws AppException {

        List<DadosInfracaoPAD> listaInfracoes = wrapper.getInfracoes();

        if (!DetranCollectionUtil.ehNuloOuVazio(listaInfracoes)) {

            for (DadosInfracaoPAD infracao : listaInfracoes) {

                try {

                    regraGeralInfracaoBO.validarRegraInfracaoCometidaAposPeriodoPermissionado(infracao);
                    regraGeralInfracaoBO.validarRegraInfracaoPrescrita(infracao, wrapper.getValorReferenciaMes());

                    if (StatusPontuacaoConstante.ATIVO.equals(infracao.getStatusPontuacao())) {
                        validarAndamentoPAReincidenteC2(pasSuspensao);
                    }

                    regraInfracaoCometidaPeriodoReincidente(em, infracao,pasSuspensao);
                    
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
     * @param infracao
     * @throws RegraNegocioException
     */
    private void regraInfracaoCometidaPeriodoReincidente(EntityManager em, DadosInfracaoPAD infracao, List<ProcessoAdministrativoWrapper> pasJaExistentes) throws RegraNegocioException, AppException {

        List<Long> idsPA = Lists.transform(pasJaExistentes, new Function<ProcessoAdministrativoWrapper, Long>() {
            @Override
            public Long apply(ProcessoAdministrativoWrapper f) {
                return f.getId();
            }
        });
        
        new BloqueioBCARepositorio().validarInfracaoCometidaPeriodoReincidente(em, infracao.getDataInfracao(), idsPA);
    }

    private void validarAndamentoPAReincidenteC2(List<ProcessoAdministrativoWrapper> pasSuspensao) throws RegraNegocioException {

        List<Integer> andamentosNaoPermitidos = 
            DetranCollectionUtil.montaLista(PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.AGUARDAR_GERACAO_NOTIFICACAO_ACOLHIMENTO,
                PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.AGUARDAR_AR_NOTIFICACAO_ACOLHIMENTO,
                PAAndamentoProcessoConstante.EDITAL_NOTIFICACAO_ACOLHIMENTO.AGUARDAR_EDITAL_NOTIFICACAO_ACOLHIMENTO
            );
        
        regraGeralInfracaoBO.validarAndamentoPAReincidente(pasSuspensao, andamentosNaoPermitidos);
    }

    /**
     * 
     * @param em
     * @param dadosCondutorPAD
     * @return
     * @throws AppException 
     */
    private List identificaProcessoAdministrativoOrigem(
        EntityManager em, DadosCondutorPAD dadosCondutorPAD) throws RegraNegocioException, AppException {
        
        List pasOrigem = 
            new ProcessoAdministrativoRepositorio()
                .getProcessoAdministrativoSuspensaoAtivoBloqueioCnhMaisAntigo(em, dadosCondutorPAD.getCpf());

        if(DetranCollectionUtil.ehNuloOuVazio(pasOrigem)) {
            throw new RegraNegocioException("Deve existir PA de suspensão para este condutor.");
        }
        
        return pasOrigem;
    }
}