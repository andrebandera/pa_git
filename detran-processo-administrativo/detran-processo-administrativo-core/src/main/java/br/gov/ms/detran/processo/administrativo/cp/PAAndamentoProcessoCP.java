
package br.gov.ms.detran.processo.administrativo.cp;

import br.gov.ms.detran.comum.projeto.anotacao.resource.Locator;
import br.gov.ms.detran.comum.projeto.anotacao.resource.LocatorType;
import br.gov.ms.detran.comum.projeto.negocio.impl.DetranCondicaoProcessoImpl;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.ReflectUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAAndamentoProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAEtapaAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAStatusAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAEtapaAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatusAndamento;
import br.gov.ms.detran.processo.administrativo.enums.OrigemPlataformaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.PAAndamentoProcessoWrapper;
import java.util.List;

/**
  @author Carlos
 */

@Locator(LocatorType.CONDICAO_PROCESSO)
public class PAAndamentoProcessoCP extends DetranCondicaoProcessoImpl<PAAndamentoProcessoWrapper>{

    @Override
    public void validarCadastro(PAAndamentoProcessoWrapper entity) throws AppException {
        existeCodigoAtivo(entity, false);
        existeDescricaoAtivo(entity);
        aplicarDefaultOrigemPlataforma(entity);
    }

    @Override
    public void validarReativacao(PAAndamentoProcessoWrapper entity) throws AppException {
       existeCodigoAtivo(entity, true);
       existeDescricaoAtivo(entity);
    }

    @Override
    public void validarExclusao(PAAndamentoProcessoWrapper entidade) throws AppException {
        existeRelacionamentoComPAStatusAndamento(entidade.getEntidade());
        existeRelacionamentoComPAFluxoFase(entidade.getEntidade());
        existeRelacionamentoComPAEtapaAndamento(entidade.getEntidade());
    }
    
    
    
    protected void aplicarDefaultOrigemPlataforma(PAAndamentoProcessoWrapper  wrapper) {
        
            if (wrapper.getId() == null) {
                Object entidade = wrapper.getEntidade();
                ReflectUtil.executeMethod(
                            ReflectUtil.getMethod(entidade, "setOrigemPlataforma", OrigemPlataformaEnum.class),entidade, OrigemPlataformaEnum.WEB);
                }
            }
        
    
    private void existeCodigoAtivo(PAAndamentoProcessoWrapper entity, Boolean reativacao) throws DatabaseException, AppException{
        PAAndamentoProcessoRepositorio repo = new PAAndamentoProcessoRepositorio();
        PAAndamentoProcesso duplicado = repo.getPAAndamentoProcessoAtivoByCodigo(getEntityManager(), entity.getEntidade().getCodigo());
            
        if(duplicado != null){
                if(entity.getId() == null){
                    DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entity.getEntidade().getCodigo().toString(), "application.label.codigo");
                    if(!entity.getEntidade().getId().equals(duplicado.getId()) && reativacao)
                DetranWebUtils.applicationMessageException("severity.error.application.duplicadoentityativoexception", "\"", "application.label.codigo");
               }
            }     
    }
 
    private void existeDescricaoAtivo(PAAndamentoProcessoWrapper entidade) throws  AppException{
        PAAndamentoProcessoRepositorio repo = new PAAndamentoProcessoRepositorio();
        PAAndamentoProcesso duplicado = repo.getPAAndamentoProcessoByDescricao(getEntityManager(), entidade.getEntidade().getDescricao());
            
        if (duplicado != null) {
            if(entidade.getId() == null){
            DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entidade.getEntidade().getDescricao(), "application.label.descricao");
            }else{
                
            if(!duplicado.getId().equals(entidade.getId())){
            DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entidade.getEntidade().getDescricao(), "application.label.descricao");
            }
          }
        }
    }

    private void existeRelacionamentoComPAStatusAndamento(PAAndamentoProcesso entidade) throws DatabaseException, AppException {
        PAStatusAndamentoRepositorio repositorio = new PAStatusAndamentoRepositorio();
        List<PAStatusAndamento> resultado = repositorio.getPAStatusAndamentoAtivoPorPAAndamentoProcesso(getEntityManager(), entidade.getId());

        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
    
    private void existeRelacionamentoComPAEtapaAndamento(PAAndamentoProcesso entidade) throws DatabaseException, AppException {
        PAEtapaAndamentoRepositorio repositorio = new PAEtapaAndamentoRepositorio();
        List<PAEtapaAndamento> resultado = repositorio.getPAEtapaAndamentoAtivoPorPAAndamentoProcesso(getEntityManager(), entidade.getId());

        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
    
    private void existeRelacionamentoComPAFluxoFase(PAAndamentoProcesso entidade) throws DatabaseException, AppException {
        PAFluxoFaseRepositorio repositorio = new PAFluxoFaseRepositorio();
        List<PAFluxoFase> resultado = repositorio.getPAFluxoFaseAtivoPorPAAndamentoProcesso(getEntityManager(), entidade.getId());

        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
   
}
