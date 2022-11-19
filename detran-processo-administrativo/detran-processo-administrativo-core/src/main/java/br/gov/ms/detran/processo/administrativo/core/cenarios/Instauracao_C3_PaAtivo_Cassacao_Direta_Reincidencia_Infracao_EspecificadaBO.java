package br.gov.ms.detran.processo.administrativo.core.cenarios;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.constantes.StatusPontuacaoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.RegrasGeralInstauracaoCondutorBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.DadosInfracaoPADRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DadosInfracaoPAD;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * @author yanko.campos
 */
public class Instauracao_C3_PaAtivo_Cassacao_Direta_Reincidencia_Infracao_EspecificadaBO extends InstauracaoCenario implements IInstauracaoCenario {
    
    private static final Logger LOG = Logger.getLogger(Instauracao_C3_PaAtivo_Cassacao_Direta_Reincidencia_Infracao_EspecificadaBO.class);
    
    private final RegrasGeralInstauracaoCondutorBO regraGeralCondutorBO;
    
    public Instauracao_C3_PaAtivo_Cassacao_Direta_Reincidencia_Infracao_EspecificadaBO() {
        super();
        this.regraGeralCondutorBO = new RegrasGeralInstauracaoCondutorBO();
    }
    
    @Override
    public List getInfracoes(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws DatabaseException {
        return new DadosInfracaoPADRepositorio().getInfracoesPorCpfEAmparoEAcaoEHistoricoInfracao(em, wrapper.getDadosCondutorPAD().getCpf(), wrapper.getApoioOrigemInstauracao());
    }

    @Override
    public IBaseEntity executarInstauracao(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper) throws AppException {
        
        List<ProcessoAdministrativoWrapper> pasSuspensao = 
            regraGeralCondutorBO
                .getProcessosAdministrativosSuspensaoAtivosComAndamentoPorCPF(em, wrapper.getDadosCondutorPAD());
        
        Map<String, Collection<DadosInfracaoPAD>> mapInfracoes = regraGeralInfracaoBO.gerarMapInfracoes(wrapper);

        for (Map.Entry<String, Collection<DadosInfracaoPAD>> entry : mapInfracoes.entrySet()) {

            List<DadosInfracaoPAD> infracoesValidadas = regraGeralInfracaoBO.filtrarInfracoesValidasRegrasGerais(em, entry.getValue(), wrapper);

            if (infracoesValidadas.size() > 1) {

                for (DadosInfracaoPAD infracao : infracoesValidadas) {
                    try {
                        if (StatusPontuacaoConstante.ATIVO.equals(infracao.getStatusPontuacao())) {
                            regraGeralInfracaoBO.validarAndamentoPAReincidenteCenarios(pasSuspensao);
                        }
                        
                        List<DadosInfracaoPAD> infracoes = regraGeralInfracaoBO.montaInfracoesParaPA(infracao, wrapper, infracoesValidadas);
                        return gravar(em, wrapper, infracoes);
                    } catch (RegraNegocioException e) {
                        //LOG.warn("Validação infração: {0}", e.getMessage());
                    }
                }
            }
        }

        return null;
    }
    
    /**
     * 
     * @param em
     * @param wrapper
     * @return
     * @throws AppException 
     */
    private List<DadosInfracaoPAD> buscarInfracoes(EntityManager em, InstaurarProcessoAdministrativoWrapper wrapper, List<ProcessoAdministrativoWrapper> pasSuspensao) throws AppException {

        List<DadosInfracaoPAD> listaInfracoes = wrapper.getInfracoes();

        if (!DetranCollectionUtil.ehNuloOuVazio(listaInfracoes)) {

            for (DadosInfracaoPAD infracao : listaInfracoes) {

                try {
                    DadosInfracaoPAD infracaoReincidente = null;
                    
                    // Validação aqui da WEB.
                    regraGeralInfracaoBO.validarRegraInfracaoCometidaAposPeriodoPermissionado(infracao);
                    regraGeralInfracaoBO.validarRegraInfracaoPrescrita(infracao, wrapper.getValorReferenciaMes());
                    
                    // Validações do cenário
                    validarCondutorPegoDirigindo(em, infracao); 
                    
                    if (StatusPontuacaoConstante.ATIVO.equals(wrapper.getApoioOrigemInstauracao().getStatusInfracao())){
                        regraGeralInfracaoBO.validarAndamentoPAReincidenteCenarios(pasSuspensao);
                    }
                    
                    infracaoReincidente = regraGeralInfracaoBO.validaInfracaoPeriodoMaior4AnosEOutraInfracaoPeriodo1Ano(wrapper.getDadosCondutorPAD(), infracao);
                    
                    if(infracaoReincidente == null)
                        infracaoReincidente = regraGeralInfracaoBO.validaAMesmaInfracaoFoiRealizadaEm1AnoOuMenos(em, infracao, wrapper.getValorReferenciaMes());
                    if(infracaoReincidente ==null){
                        throw new RegraNegocioException("Não existe outra infração com o mesmo codigo no periodo de 1 ano.");
                    }
                    
                    return DetranCollectionUtil.montaLista(infracao, infracaoReincidente);

                } catch (RegraNegocioException ex) {
                    //LOG.warn("Montar Conjunto Infracoes Reincidente: {0}", ex.getMessage());
                }
            }
        }

        return null;
    }

    /**
     * @param em
     * @param infracao 
     */
    private void validarCondutorPegoDirigindo(EntityManager em, DadosInfracaoPAD infracao) throws RegraNegocioException {
        
        Boolean validado = Boolean.FALSE;
        
        if (validado)
            throw new RegraNegocioException("Condutor pego dirigindo.");
       
    }
}