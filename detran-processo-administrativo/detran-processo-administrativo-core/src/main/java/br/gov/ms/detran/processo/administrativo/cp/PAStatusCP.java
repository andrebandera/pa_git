
package br.gov.ms.detran.processo.administrativo.cp;

import br.gov.ms.detran.comum.projeto.anotacao.resource.Locator;
import br.gov.ms.detran.comum.projeto.anotacao.resource.LocatorType;
import br.gov.ms.detran.comum.projeto.negocio.impl.DetranCondicaoProcessoImpl;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAStatusAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatus;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import br.gov.ms.detran.processo.administrativo.wrapper.PAStatusWrapper;
import java.util.List;

/**
 *
 * @author Carlos Eduardo
 */

@Locator(LocatorType.CONDICAO_PROCESSO)
public class PAStatusCP extends DetranCondicaoProcessoImpl<PAStatusWrapper> {
    
    @Override
    public void validarCadastro(PAStatusWrapper entity) throws AppException {
        existeCodigoAtivo(entity,false);
        existeDescricaoAtivo(entity);
    }

    @Override
    public void validarExclusao(PAStatusWrapper entity) throws AppException {
        existeRelacionamentoComPAStatusAndamento(entity.getEntidade());
    }
          

    @Override
    public void validarReativacao(PAStatusWrapper entity) throws AppException {
        existeCodigoAtivo(entity,true);
        existeDescricaoAtivo(entity);
    }    
    
    
    private void existeCodigoAtivo(PAStatusWrapper entity, Boolean reativacao) throws DatabaseException, AppException{
        PAStatusRepositorio repo = new PAStatusRepositorio();
        PAStatus duplicado = repo.getPAStatusAtivoByCodigo(getEntityManager(), entity.getEntidade().getCodigo());
            
        if(duplicado != null){
                if(entity.getId() == null){
                    DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entity.getEntidade().getCodigo().toString(), "application.label.codigo");
                    if(!entity.getEntidade().getId().equals(duplicado.getId()) && reativacao)
                DetranWebUtils.applicationMessageException("severity.error.application.duplicadoentityativoexception", "\"", "application.label.codigo");
               }
            }     
    }
 
    private void existeDescricaoAtivo(PAStatusWrapper entidade) throws  AppException{
        PAStatusRepositorio repo = new PAStatusRepositorio();
        PAStatus duplicado = repo.getPAStatusByDescricao(getEntityManager(), entidade.getEntidade().getDescricao());
            
        if (duplicado != null) {
            if(entidade.getId() == null){
            DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entidade.getEntidade().getDescricao(), "application.label.descricao");
            }else{
            if(duplicado.getId() != entidade.getId()){
            DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entidade.getEntidade().getDescricao(), "application.label.descricao");
            }
          }
        }
    }
    
    
   private void existeRelacionamentoComPAStatusAndamento(PAStatus entidade) throws DatabaseException, AppException {
        PAStatusAndamentoRepositorio repositorio = new PAStatusAndamentoRepositorio();
        List<PAStatusAndamento> resultado = repositorio.getPAStatusAndamentoAtivoPorPAStatus(getEntityManager(), entidade.getId());

        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
    
}
