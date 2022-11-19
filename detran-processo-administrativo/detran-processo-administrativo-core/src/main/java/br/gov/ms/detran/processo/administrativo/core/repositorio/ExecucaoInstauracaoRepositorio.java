package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.entidade.ExecucaoInstauracao;
import javax.persistence.EntityManager;

public class ExecucaoInstauracaoRepositorio extends AbstractJpaDAORepository<ExecucaoInstauracao> {

    /**
     * 
     * @param em
     * @param idExecucaoInstauracao
     * @param quantidadeInstaurados
     * @throws AppException 
     */
    public void atualizaQuantidadeInstauradosParaExecucaoInstauracao(
        EntityManager em, Long idExecucaoInstauracao, Long quantidadeInstaurados) throws AppException {
        
        if(idExecucaoInstauracao == null || quantidadeInstaurados == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        super
            .executeNamedQuery(
                em, 
                "ExecucaoInstauracao.atualizaQuantidadeInstauradosParaExecucaoInstauracao", 
                idExecucaoInstauracao,
                quantidadeInstaurados
            );
    }
}