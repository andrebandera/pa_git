package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAStatusEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * @author Lillydi
 */
public class PAOcorrenciaStatusRepositorio extends AbstractJpaDAORepository<PAOcorrenciaStatus> {

    /**
     *
     * Recupera o Status ativo de um Processo Administrativo.
     *
     * @param em
     * @param idProcessoAdm
     * @return
     * @throws AppException
     */
    public PAOcorrenciaStatus getPAOcorrenciaStatusAtiva(EntityManager em, Long idProcessoAdm) throws AppException {

        if (idProcessoAdm == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        List<PAOcorrenciaStatus> ocorrencias
                = getListNamedQuery(em, "PAOcorrenciaStatus.getOcorrenciaStatusAtivaPorProcessoAdm", idProcessoAdm, AtivoEnum.ATIVO);

        if (DetranCollectionUtil.ehNuloOuVazio(ocorrencias) || ocorrencias.size() > 1) {
            DetranWebUtils.applicationMessageException("PA Ocorrência Status está inválida.");
        }

        return ocorrencias.get(0);
    }
    
    public PAOcorrenciaStatus getPAOcorrenciaStatusAtivaPorNumeroProcesso(EntityManager em, String numeroProcesso) throws AppException {

        if (DetranStringUtil.ehBrancoOuNulo(numeroProcesso)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        List<PAOcorrenciaStatus> ocorrencias
                = getListNamedQuery(em, "PAOcorrenciaStatus.getOcorrenciaStatusAtivaPorNumeroProcesso", numeroProcesso, AtivoEnum.ATIVO);

        if (DetranCollectionUtil.ehNuloOuVazio(ocorrencias) || ocorrencias.size() > 1) {
            DetranWebUtils.applicationMessageException("PA Ocorrência Status está inválida.");
        }

        return ocorrencias.get(0);
    }
    
    public void validarAndamentoPorFluxo(EntityManager em, PAOcorrenciaStatus ocorrencia, Integer codigoFluxo) throws AppException {

        if (ocorrencia == null || codigoFluxo == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        
        List list = getListNamedQuery(em, "PAOcorrenciaStatus.validaAndamentoPorFluxo", ocorrencia.getId(), codigoFluxo, AtivoEnum.ATIVO);

        if (DetranCollectionUtil.ehNuloOuVazio(list) || list.size() > 1) {
            DetranWebUtils.applicationMessageException("O Andamento {0} não permite o Recebimento da CNH'", "", 
                    ocorrencia.getStatusAndamento().getAndamentoProcesso().getDescricao());
        }

    }
    
    /**
     * Grava um Status para o Processo Administrativo informado.
     *
     * @param em
     * @param pa
     * @param codigoAndamento
     * @param situacao
     * @param fluxoProcesso
     * 
     * @throws DatabaseException 
     */
    public void incluir(EntityManager em, 
                        ProcessoAdministrativo pa,
                        Integer codigoAndamento, 
                        SituacaoOcorrenciaEnum situacao, 
                        PAFluxoProcesso fluxoProcesso) throws AppException {
        
        incluir(em, pa, PAStatusEnum.ATIVO, codigoAndamento, situacao, fluxoProcesso);
    }

    /**
     *
     * Grava um Status para o Processo Administrativo informado.
     *
     * @param em
     * @param pa
     * @param status
     * @param codigoAndamento
     * @param situacao
     * @param fluxoProcesso
     * 
     * @throws DatabaseException 
     */
    public void incluir(EntityManager em, 
                        ProcessoAdministrativo pa, 
                        PAStatusEnum status, 
                        Integer codigoAndamento, 
                        SituacaoOcorrenciaEnum situacao, 
                        PAFluxoProcesso fluxoProcesso) throws AppException {
        
        validar(pa, status, codigoAndamento);
        
        PAStatusAndamento statusAndamento = 
            new PAStatusAndamentoRepositorio()
                .getPAStatusAndamentoAtivoPorStatusEAndamento(em, codigoAndamento);
        
        if (statusAndamento == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.parametroinvalido2");
        }
        
        insert(em, new PAOcorrenciaStatus(pa, statusAndamento, situacao, fluxoProcesso));
    }

    /**
     * Desativa o Status informado.
     *
     * @param em
     * @param oldOcorrencia
     *
     * @throws DatabaseException
     */
    public void desativar(EntityManager em, PAOcorrenciaStatus oldOcorrencia) throws DatabaseException {

        oldOcorrencia.setAtivo(AtivoEnum.DESATIVADO);
        oldOcorrencia.setDataTermino(new Date());

        update(em, oldOcorrencia);
    }

    /**
     * @param pa
     * @param status
     * @throws AppException
     */
    private void validar(ProcessoAdministrativo pa, 
                         PAStatusEnum status, 
                         Integer codigoAndamento) throws AppException {
        
        if(pa == null || pa.getId() == null ||status == null || codigoAndamento == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.parametroinvalido2");
        }
    }

    /**
     * Retorna lista de PAOcorrenciaStatus (objeto apenas com código do andamento)
     * 
     * @param em
     * @param idProcessoAdm
     * @return
     * @throws AppException 
     */
    public PAOcorrenciaStatus getPAOcorrenciaStatusAtivaApenasCodigoAndamento(EntityManager em, Long idProcessoAdm) throws AppException {
        
        if (idProcessoAdm == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        List<PAOcorrenciaStatus> ocorrencias = 
                getListNamedQuery(em, 
                                  "PAOcorrenciaStatus.getPAOcorrenciaStatusAtivaApenasCodigoAndamento", 
                                  idProcessoAdm, 
                                  AtivoEnum.ATIVO);

        if (DetranCollectionUtil.ehNuloOuVazio(ocorrencias) || ocorrencias.size() > 1) {
            DetranWebUtils.applicationMessageException("PA Ocorrência Status está inválida.");
        }

        return ocorrencias.get(0);
    }

    public List getOcorrenciaPorCpfEFluxo(EntityManager em, String cpf, Integer codigoFluxo) throws DatabaseException {
        return getListNamedQuery(em, "PAOcorrenciaStatus.getOcorrenciaPorCpfEFluxo", cpf, codigoFluxo, AtivoEnum.ATIVO);
    }

    public List getOcorrenciasDesistentesInstPenalizacao(EntityManager em, String cpf) throws AppException{
        Object[] params={
                         cpf,
                         PAParametroEnum.DESISTENCIA_REC_INST_PEN,
                         AtivoEnum.ATIVO
        };
        
        return getListNamedQuery(em, "PAOcorrenciaStatus.getOcorrenciasDesistentesInstPenalizacao", params);
    }
    
    public List<PAOcorrenciaStatus> getPAOcorrenciaStatusAtivoPorPAFluxoProcesso(
            EntityManager em, Long id) throws DatabaseException {

        return super.getListNamedQuery(em, 
                "PAOcorrenciaStatus.getPAOcorrenciaStatusAtivoPorPAFluxoProcesso", 
                id, AtivoEnum.ATIVO);
    }
}