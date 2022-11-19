/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPrioridadeFluxoAmparoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAPrioridadeFluxoAmparo;
import br.gov.ms.detran.processo.administrativo.wrapper.PAPrioridadeFluxoAmparoWrapper;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Carlos Eduardo
 */
public class PAPrioridadeFluxoAmparoBO {
    
    PAPrioridadeFluxoAmparoRepositorio repo = new PAPrioridadeFluxoAmparoRepositorio();
    
    public void trocarPrioridades(EntityManager em, PAPrioridadeFluxoAmparo entity, Integer novaOrdem) throws DatabaseException{
        
        Integer prioridadeEntity = entity.getPrioridade();
        PAPrioridadeFluxoAmparo entity2 = repo.getPAPrioridadeFluxoAmparoPosterior(em, entity.getFluxoProcesso().getId() , novaOrdem);
        
        repo.alterarOrdemPasso1(em, entity.getId());
        repo.alterarOrdemPasso2(em, novaOrdem, entity2.getId());
        repo.alterarOrdemPasso3(em, prioridadeEntity, entity2.getId(), novaOrdem);
        repo.alterarOrdemPasso4(em, novaOrdem, entity.getId());
        repo.alterarOrdemPasso5(em, entity.getId());
        repo.alterarOrdemPasso6(em, entity2.getId());
    }
    
    public PAPrioridadeFluxoAmparo salvarPAPrioridadeFluxoAmparo(EntityManager em, PAPrioridadeFluxoAmparoWrapper wrapper) throws DatabaseException, AppException {
        
        //entidade que vem do Wrapper
        PAPrioridadeFluxoAmparo entidadeWrapper = wrapper.getEntidade();
        //prioridade que foi informada no wrapper para ser inserida no banco
        Integer prioridadeAtual = entidadeWrapper.getPrioridade();
        //Caso irá salvar uma nova entidade com prioridade em uso ou não, entra no IF
        if (entidadeWrapper.getId() == null){
           //recupera valor maior de prioridade existente + 1
            Integer maiorPrioridade = repo.recuperarUltimoValorPrioridadePAPrioridadeFluxoAmparo(em, entidadeWrapper.getFluxoProcesso().getId())+1;
            if(maiorPrioridade < entidadeWrapper.getPrioridade()  || entidadeWrapper.getPrioridade() <= 0){
                DetranWebUtils.applicationMessageException("Prioridade não permitida pelo sistema.");
            }
                List<PAPrioridadeFluxoAmparo> paPrioridadeFluxoAmparo
                = repo.getFaseFluxoAmparoPorFasesFluxoAmparoEMaioresPrioridadesDESC(
                        em, entidadeWrapper.getFluxoProcesso().getId(), entidadeWrapper.getPrioridade());
            for (PAPrioridadeFluxoAmparo g : paPrioridadeFluxoAmparo) {
                    repo.executeNamedQuery(em, "PAPrioridadeFluxoAmparo.alterarPrioridades", g.getPrioridade() + 1, g.getId());
            }
                repo.insert(em, entidadeWrapper);
            }//Caso irá atualizar uma prioridade para uma maior que a anterior ou que a posterior
            else {
            //recupera valor maior de prioridade existente + 1
            Integer maiorPrioridade = repo.recuperarUltimoValorPrioridadePAPrioridadeFluxoAmparo(em, entidadeWrapper.getFluxoProcesso().getId())+1;
            //entidade que está no banco antes da atualização
            PAPrioridadeFluxoAmparo entidadeBanco = repo.getPAPrioridadeFluxoAmparoPorId(em, wrapper.getEntidade().getId());
            //entidade que possui a prioridade informada no wrapper
            PAPrioridadeFluxoAmparo entity2 = repo.getPAPrioridadeFluxoAmparoPosterior(em, entidadeWrapper.getFluxoProcesso().getId(),
                prioridadeAtual);
            
            if(maiorPrioridade < entidadeWrapper.getPrioridade()  || entidadeWrapper.getPrioridade() == 0){
                DetranWebUtils.applicationMessageException("Prioridade não permitida pelo sistema.");
            }
            if(entity2 != null){
            repo.alterarOrdemPasso1(em, entidadeWrapper.getId());
            repo.alterarOrdemPasso2(em, prioridadeAtual, entity2.getId());
            repo.alterarOrdemPasso3(em, entidadeBanco.getPrioridade(), entity2.getId(), prioridadeAtual);
            repo.alterarOrdemPasso4(em, prioridadeAtual, entidadeWrapper.getId());
            repo.alterarOrdemPasso5(em, entidadeWrapper.getId());
            repo.alterarOrdemPasso6(em, entity2.getId());
            
            repo.update(em, entidadeWrapper);
            }else{
            
             if(entidadeWrapper.getPrioridade() >= maiorPrioridade){
                DetranWebUtils.applicationMessageException("Informe uma prioridade dentro da sequência existente.");
            }
            repo.update(em, entidadeWrapper);
            }
        }

        return null;
    }
    
    public void removerPAPrioridadeFluxoAmparo(EntityManager em, 
                PAPrioridadeFluxoAmparo entidade) throws DatabaseException{
        repo.alterarOrdemPasso1(em, entidade.getId());
        List<PAPrioridadeFluxoAmparo> paPrioridadeFluxoAmparo
                = repo.getFaseFluxoAmparoPorFasesFluxoAmparoEMaioresPrioridadesASC(
                        em, entidade.getFluxoProcesso().getId(), entidade.getPrioridade());
            for (PAPrioridadeFluxoAmparo g : paPrioridadeFluxoAmparo) {
                repo.executeNamedQuery(em, "PAPrioridadeFluxoAmparo.alterarPrioridades", g.getPrioridade()-1, g.getId());
                }
        }
}
