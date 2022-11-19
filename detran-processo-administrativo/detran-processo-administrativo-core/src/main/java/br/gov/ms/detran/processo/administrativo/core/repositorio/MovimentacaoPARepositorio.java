package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.entidade.Movimentacao;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
import java.util.List;
import javax.persistence.EntityManager;

public class MovimentacaoPARepositorio extends AbstractJpaDAORepository<Movimentacao> {

    /**
     * @param em
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List<Movimentacao> getMovimentacoesPorListaAndamento(EntityManager em) throws DatabaseException {
        
        List<Integer> lAndamento
                = DetranCollectionUtil
                .montaLista(
                        PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_SUSPENSAO,
                        PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_RETIRADA_DA_SUSPENSAO,
                        PAAndamentoProcessoConstante.CANCELAMENTO.CONFIRMAR_CANCELAMENTO_PROCESSO,
                        PAAndamentoProcessoConstante.ARQUIVAMENTO.CONFIRMAR_ARQUIVAMENTO_PROCESSO
                );

        List<Movimentacao> lMovimentacao
                = super.getListNamedQuery(
                        em,
                        "Movimentacao.getMovimentacoesPorListaAndamento",
                        AtivoEnum.ATIVO,
                        SituacaoOcorrenciaEnum.INICIADO,
                        lAndamento
                );

        return lMovimentacao;
    }

    public List<Movimentacao> getMovimentacoesPorPA(EntityManager em, Long idPA) throws DatabaseException {
        return super.getListNamedQuery(em, "Movimentacao.getMovimentacoesPorPA",idPA);
    }

    /**
     * @param em
     * @param processoAdministrativoId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public Movimentacao getMovimentacaoPorProcessoAdministrativoAtivo(EntityManager em, Long processoAdministrativoId) throws DatabaseException {
        return super.getNamedQuery(em, 
                                   "Movimentacao.getMovimentacaoPorProcessoAdministrativoAtivo", 
                                   processoAdministrativoId, 
                                   AtivoEnum.ATIVO);
    }

    /**
     * @param em
     * @param processoAdministrativoId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public Movimentacao getMovimentacaoSuspensaoDesativaRecente(EntityManager em, Long processoAdministrativoId) throws DatabaseException {
        List<Movimentacao> lista = 
                super.getListNamedQuery(em, 
                                        "Movimentacao.getMovimentacaoSuspensaoDesativaRecente", 
                                        processoAdministrativoId,
                                        MovimentacaoAcaoEnum.SUSPENDER,
                                        AtivoEnum.DESATIVADO);
        
        return DetranCollectionUtil.ehNuloOuVazio(lista) ? null : lista.get(0);
    }

    /**
     * @param em
     * @param paId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public Movimentacao getUltimaMovimentacaoAtivaPorProcessoAdministrativo(EntityManager em, Long paId) throws DatabaseException {
        
        List<Movimentacao> lista = 
                super.getListNamedQuery(em, 
                                        "Movimentacao.getMovimentacaoPorProcessoAdministrativoEAtivo", 
                                        paId, 
                                        AtivoEnum.ATIVO);
        
        return DetranCollectionUtil.ehNuloOuVazio(lista) ? null : lista.get(0);
    }
    
    /**
     * @param em
     * @param processoAdministrativoId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public Movimentacao getMovimentacaoPorProcessoAdministrativoEAcaoAtivo(EntityManager em, Long processoAdministrativoId, MovimentacaoAcaoEnum acao) throws DatabaseException {
        return super.getNamedQuery(em, 
                                   "Movimentacao.getMovimentacaoPorProcessoAdministrativoEAcaoAtivo", 
                                   processoAdministrativoId, 
                                   AtivoEnum.ATIVO,
                                   acao);
    }
}