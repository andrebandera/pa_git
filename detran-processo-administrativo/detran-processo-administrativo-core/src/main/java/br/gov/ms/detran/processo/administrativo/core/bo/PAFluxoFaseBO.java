package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoFaseWrapper;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Carlos Eduardo
 */
public class PAFluxoFaseBO {
    
    PAFluxoFaseRepositorio repo = new PAFluxoFaseRepositorio();
    
    public void trocarPrioridades(EntityManager em, PAFluxoFase entity, Integer novaOrdem) throws DatabaseException{
        
        Integer prioridadeEntity = entity.getPrioridade();
        PAFluxoFase entity2 = repo.getPAFluxoFasePosterior(em, entity.getPrioridadeFluxoAmparo().getId(), novaOrdem);
        
        repo.alterarOrdemPasso1(em, entity.getId());
        repo.alterarOrdemPasso2(em, novaOrdem, entity2.getId());
        repo.alterarOrdemPasso3(em, prioridadeEntity, entity2.getId(), novaOrdem);
        repo.alterarOrdemPasso4(em, novaOrdem, entity.getId());
        repo.alterarOrdemPasso5(em, entity.getId());
        repo.alterarOrdemPasso6(em, entity2.getId());
    }
    
    public PAFluxoFase salvarPAFluxoFase(EntityManager em, PAFluxoFaseWrapper wrapper) throws DatabaseException, AppException {
        //entidade que vem do Wrapper
        PAFluxoFase entidadeWrapper = wrapper.getEntidade();
        //prioridade que foi informada no wrapper para ser inserida no banco
        Integer prioridadeAtual = entidadeWrapper.getPrioridade();
        //Caso irá salvar uma nova entidade com prioridade em uso ou não, entra no IF
        if (entidadeWrapper.getId() == null) {
            //recupera valor maior de prioridade existente + 1
            Integer maiorPrioridade = repo.recuperarUltimoValorPrioridadePAFluxoFase(em, entidadeWrapper.getPrioridadeFluxoAmparo().getId())+1;
            
            if(maiorPrioridade < entidadeWrapper.getPrioridade()  || entidadeWrapper.getPrioridade() <= 0){
                DetranWebUtils.applicationMessageException("Prioridade não permitida pelo sistema.");
            }
                List<PAFluxoFase> paFluxoFase = repo.getPAFluxoFasePorPrioridadeFluxoAmparoEMaioresPrioridadesDESC(em, 
                        entidadeWrapper.getPrioridadeFluxoAmparo().getId(), entidadeWrapper.getPrioridade());
                
                if (!DetranCollectionUtil.ehNuloOuVazio(paFluxoFase)) {
                    for (PAFluxoFase g : paFluxoFase) {
                        repo.executeNamedQuery(em, "PAFluxoFase.alterarPrioridades", g.getPrioridade() + 1, g.getId());
                    }
                }
                
                repo.insert(em, entidadeWrapper);
            }//Caso irá atualizar uma prioridade para uma maior que a anterior ou que a posterior
            else {
            //recupera valor maior de prioridade existente + 1
            Integer maiorPrioridade = repo.recuperarUltimoValorPrioridadePAFluxoFase(em, entidadeWrapper.getPrioridadeFluxoAmparo().getId())+1;
            //entidade que está no banco antes da atualização
            PAFluxoFase entidadeBanco = repo.getPAFluxoFasePorId(em, wrapper.getEntidade().getId());
            //entidade que possui a prioridade informada no wrapper
            PAFluxoFase entity2 = repo.getPAFluxoFasePosterior(em, entidadeWrapper.getPrioridadeFluxoAmparo().getId(),
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
    
    public PAFluxoFase removerFluxoFase(EntityManager em, 
                PAFluxoFase entidade) throws DatabaseException{
        repo.alterarOrdemPasso1(em, entidade.getId());
        
        
        List<PAFluxoFase> paFluxoFase
                = repo.getPAFluxoFasePorPrioridadeFluxoAmparoEMaioresPrioridadesASC(em,
                        entidade.getPrioridadeFluxoAmparo().getId(),entidade.getPrioridade());
            for (PAFluxoFase g : paFluxoFase) {
                repo.executeNamedQuery(em, "PAFluxoFase.alterarPrioridades", g.getPrioridade()-1, g.getId());
                }
            return null;
        }
}
