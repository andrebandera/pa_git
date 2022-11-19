package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.criteria.ControleCnhPACriteria;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.enums.PAStatusEnum;
import java.util.List;
import javax.persistence.EntityManager;

public class MovimentoCnhRepositorio extends AbstractJpaDAORepository<MovimentoCnh> {

    /**
     * @param em
     * @return
     * @throws DatabaseException 
     */
    public List<MovimentoCnh> getListEntregaCnh(EntityManager em) throws DatabaseException {
        
        Object[] params = {
            AcaoEntregaCnhEnum.ENTREGA,
            PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_ENTREGA_CNH,
            AcaoEntregaCnhEnum.DEVOLUCAO,
            PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_DESBLOQUEIO,
            AtivoEnum.ATIVO
        };
        return super.getListNamedQuery(em, "MovimentoCnh.getListEntregaCnh", params);
    }
    
    /**
     * 
     * @param em
     * @param idControleCnh
     * @param acao
     * @return
     * @throws DatabaseException 
     */
    public List<MovimentoCnh> getMovimentoPorControleCnhEAcaoEntrega(EntityManager em, Long idControleCnh, AcaoEntregaCnhEnum acao) throws AppException {
        
        if(idControleCnh == null || acao == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return getListNamedQuery(em, "MovimentoCnh.getMovimentoPorControleCnhETipo", idControleCnh, acao.ordinal());
    }
    
    /**
     * 
     * @param em
     * @return
     * @throws AppException 
     */
    public List getDesistentes(EntityManager em)throws AppException{
        
        Object[] params = {
            PAAndamentoProcessoConstante.DESISTENCIA.CONFIRMAR_DESISTENCIA_ENTREGA_CNH,
            AtivoEnum.ATIVO
        };
        return getListNamedQuery(em, "MovimentoCnh.getDesistentes", params);
    }

    /**
     * @param em
     * @param numeroProcesso
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public MovimentoCnh getMovimentoCnhPorNumeroProcessoAdministrativo(EntityManager em, String numeroProcesso) throws DatabaseException {
        return getNamedQuery(em, "MovimentoCnh.getMovimentoCnhPorNumeroProcessoAdministrativo", numeroProcesso, AtivoEnum.ATIVO);
    }

    /**
     * @param em
     * @param numeroProcesso
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public MovimentoCnh getMovimentoCnhParaDesentranhamento(EntityManager em, String numeroProcesso) throws DatabaseException {
        return getNamedQuery(em, "MovimentoCnh.getMovimentoCnhParaDesentranhamento", numeroProcesso);
    }
    
    /**
     * @param em
     * @param cpf
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public MovimentoCnh getMovimentoCnhParaDesentranhamentoPorCpfCondutor(EntityManager em, String cpf) throws AppException {

        Long cnhControleId = new PAHabilitacaoRepositorio().getCnhSituacaoEntregaSemAcaoDeDevolucaoDaCnhParaCpfCondutor(em, cpf);
        MovimentoCnh movimentoCnh = null;

        if(cnhControleId != null) {

            List<MovimentoCnh> lMovimentoCnh 
                = getListNamedQuery(
                    em, 
                    "MovimentoCnh.getMovimentoCnhParaDesentranhamentoPorCpfCondutor", 
                    cnhControleId,
                    "DT/SEPEN",
                    AtivoEnum.ATIVO.ordinal()
                );
            
            movimentoCnh = !DetranCollectionUtil.ehNuloOuVazio(lMovimentoCnh) ? lMovimentoCnh.get(0) : null;
        }
        
        return movimentoCnh;
    }

    /**
     * @param em
     * @param idPA
     * @param acao
     * @return
     * @throws AppException 
     */
    public MovimentoCnh getMovimentoPorProcessoAdministrativoEAcao(EntityManager em, Long idPA, AcaoEntregaCnhEnum acao)throws AppException{
        return getNamedQuery(em, "MovimentoCnh.getMovimentoPorProcessoAdministrativoEAcao", idPA, acao, AtivoEnum.ATIVO);
    }
    
    /**
     * 
     * @param em
     * @param idControleCnh
     * @param acao
     * @return
     * @throws DatabaseException 
     */
    public List<MovimentoCnh> getMovimentoPorControleCnhEAcaoEntregaParaDesbloqueio(
        EntityManager em, Long idControleCnh, AcaoEntregaCnhEnum acao) throws AppException {
        
        if(idControleCnh == null || acao == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        List<Integer> lStatus 
            = DetranCollectionUtil
                .montaLista(
                    PAStatusEnum.CANCELADO.getCodigo(), 
                    PAStatusEnum.SUSPENSO.getCodigo(), 
                    PAStatusEnum.ARQUIVADO.getCodigo()
                );
        
        List<Integer> lAndamento 
            = DetranCollectionUtil
                .montaLista(PAAndamentoProcessoConstante.CANCELAMENTO.CONFIRMAR_CANCELAMENTO_PROCESSO,
                    PAAndamentoProcessoConstante.SUSPENSO.PROCESSO_SUSPENSO,
                    PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_RETIRADA_DA_SUSPENSAO,
                    PAAndamentoProcessoConstante.ARQUIVAMENTO.CONFIRMAR_ARQUIVAMENTO_PROCESSO,
                    PAAndamentoProcessoConstante.APENSADO.CONFIRMAR_ARQUIVAMENTO_PROCESSO_AGRAVADO,
                    PAAndamentoProcessoConstante.CASSADO.CONFIRMAR_ARQUIVAMENTO_PROCESSO_CASSADO
                );
        
        List<MovimentoCnh> lMovimentoCnh 
            = getListNamedQuery(
                em, 
                "MovimentoCnh.getMovimentoPorControleCnhEAcaoEntregaParaDesbloqueio", 
                idControleCnh, 
                acao.ordinal(),
                lAndamento,
                lStatus,
                AtivoEnum.ATIVO.ordinal()
            );
        
        return lMovimentoCnh;
    }

    /**
     * Recupera o ID do CnhControle buscado por ProcessoAdministrativo para ação ENTREGA.
     * 
     * @param em
     * @param processoAdministrativoId
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public Long getCnhControlePorProcessoAdministrativoEAcaoEntrega(EntityManager em, 
                                                                    Long processoAdministrativoId) throws DatabaseException {
        
        MovimentoCnh movimento = super.getNamedQuery(em, 
                                                     "MovimentoCnh.getCnhControlePorProcessoAdministrativoEAcaoEntrega", 
                                                     processoAdministrativoId,
                                                     AcaoEntregaCnhEnum.ENTREGA,
                                                     AtivoEnum.ATIVO);
        
        return movimento != null ? movimento.getCnhControle() : null;
    }
    
    /**
     * @param em
     * @param idPA
     * @return
     * @throws AppException 
     */
    public List<MovimentoCnh> getListaMovimentoCnhPorProcessoAdministrativo(EntityManager em, Long idPA)throws AppException{
        return getListNamedQuery(em, "MovimentoCnh.getListaMovimentoCnhPorProcessoAdministrativo", idPA);
    }

    /**
     * @param em
     * @param cpf
     * @param acaoEntregaCnhEnum
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public MovimentoCnh getMovimentoEntregaPorCpf(EntityManager em, String cpf, AcaoEntregaCnhEnum acaoEntregaCnhEnum) throws AppException {
        
        List<MovimentoCnh> lista = 
                getListNamedQuery(em, "MovimentoCnh.getMovimentoEntregaPorCpf", cpf, acaoEntregaCnhEnum);
        
        return !DetranCollectionUtil.ehNuloOuVazio(lista) ? lista.get(0) : null;
        
    }
    
    /**
     * 
     * @param em
     * @param paId
     * @return
     * @throws AppException 
     */
    public Object getDesistentePorProcessoAdministrativo(EntityManager em, Long paId) throws AppException {
        
        Object[] params = {
            PAAndamentoProcessoConstante.DESISTENCIA.CONFIRMAR_DESISTENCIA_ENTREGA_CNH,
            AtivoEnum.ATIVO,
            paId
        };
        
        return getNamedQuery(em, "MovimentoCnh.getDesistentePorProcessoAdministrativo", params);
    }
    
    public List getMovimentoCnhComCnhControleValido(EntityManager em, ControleCnhPACriteria criteria) throws DatabaseException {
        
        Object[] params = { criteria.getNumeroProcesso(),
                            criteria.getCpfEntrega(),
                            criteria.getNumeroRegistro(),
                            criteria.getNumeroCnh(),
                            criteria.getAcao().ordinal(), 
                            AtivoEnum.ATIVO };
        
        return super.getListNamedQuery(em, "MovimentoCnh.getMovimentoCnhComCnhControleValido", criteria.getFrom(), criteria.getTo(), params);
    }
   
    public Object getCountMovimentoCnhComCnhControleValido(EntityManager em, ControleCnhPACriteria criteria) throws DatabaseException {
        
        Object[] params = { criteria.getNumeroProcesso(),
                            criteria.getCpfEntrega(),
                            criteria.getNumeroRegistro(),
                            criteria.getNumeroCnh(),
                            criteria.getAcao().ordinal(), 
                            AtivoEnum.ATIVO };
        
        return super.getNamedQuery(em, "MovimentoCnh.getCountMovimentoCnhComCnhControleValido", params);
    }
    
     public List getMovimentoCnhComCnhControleVencido(EntityManager em, ControleCnhPACriteria criteria) throws DatabaseException {
        
        Object[] params = { criteria.getNumeroProcesso(),
                            criteria.getCpfEntrega(),
                            criteria.getNumeroRegistro(),
                            criteria.getNumeroCnh(),
                            criteria.getAcao().ordinal(), 
                            AtivoEnum.ATIVO };
         
        return super.getListNamedQuery(em, "MovimentoCnh.getMovimentoCnhComCnhControleVencido", criteria.getFrom(), criteria.getTo(), params);
    }
    
    public Object getCountMovimentoCnhComCnhControleVencido(EntityManager em, ControleCnhPACriteria criteria) throws DatabaseException {
        
        Object[] params = { criteria.getNumeroProcesso(),
                            criteria.getCpfEntrega(),
                            criteria.getNumeroRegistro(),
                            criteria.getNumeroCnh(),
                            criteria.getAcao().ordinal(), 
                            AtivoEnum.ATIVO };
        
        return super.getNamedQuery(em, "MovimentoCnh.getCountMovimentoCnhComCnhControleVencido", params);
    }
}