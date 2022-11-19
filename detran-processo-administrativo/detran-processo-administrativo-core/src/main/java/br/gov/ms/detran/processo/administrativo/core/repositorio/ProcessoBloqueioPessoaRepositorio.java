/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoBloqueioPessoa;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoProcessoBloqueioPessoaEnum;
import java.util.Date;
import javax.persistence.EntityManager;

/**
 *
 * @author lilly
 */
public class ProcessoBloqueioPessoaRepositorio extends AbstractJpaDAORepository<ProcessoBloqueioPessoa> {
    
    private static final Logger LOG = Logger.getLogger(ProcessoBloqueioPessoaRepositorio.class);

    public ProcessoBloqueioPessoa buscarProcessoBloqueioPessoaAtualOuCriarNovo(EntityManager em, String cpf) throws DatabaseException {
        
        ProcessoBloqueioPessoa bloqueio = getProcessoBloqueioPessoaPorCPFEDataEmAndamento(em, cpf, new Date());
        
        if(bloqueio == null){
            bloqueio = new ProcessoBloqueioPessoa(new Date(), cpf);
            insert(em, bloqueio);
        }
        
        return bloqueio;
    }

    public ProcessoBloqueioPessoa getProcessoBloqueioPessoaPorCPFEDataEmAndamento(EntityManager em, String cpf, Date dataFim) throws DatabaseException {
        return getNamedQuery(em, "ProcessoBloqueioPessoa.getProcessoBloqueioPessoaPorCPFEDataEmAndamento", cpf, dataFim, SituacaoProcessoBloqueioPessoaEnum.FINALIZADO, AtivoEnum.ATIVO);
    }
}
