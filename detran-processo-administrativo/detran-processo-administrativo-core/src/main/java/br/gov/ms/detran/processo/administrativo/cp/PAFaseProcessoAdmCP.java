package br.gov.ms.detran.processo.administrativo.cp;

import br.gov.ms.detran.comum.projeto.anotacao.resource.Locator;
import br.gov.ms.detran.comum.projeto.anotacao.resource.LocatorType;
import br.gov.ms.detran.comum.projeto.negocio.impl.DetranCondicaoProcessoImpl;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFaseProcessoAdmRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPrioridadeFluxoAmparoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAFaseProcessoAdm;
import br.gov.ms.detran.processo.administrativo.entidade.PAPrioridadeFluxoAmparo;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFaseProcessoAdmWrapper;
import java.util.List;

/**
 *
 * @author Carlos
 */
@Locator(LocatorType.CONDICAO_PROCESSO)
public class PAFaseProcessoAdmCP extends DetranCondicaoProcessoImpl<PAFaseProcessoAdmWrapper>{
    
    @Override
    public void validarCadastro(PAFaseProcessoAdmWrapper entity) throws AppException {
        existeCodigoAtivo(entity);
        validaCodigoTamanho(entity);
    }
    
    @Override
    public void validarReativacao(PAFaseProcessoAdmWrapper entity) throws AppException {
       existeCodigoAtivo(entity);
    }
    
    @Override
    public void validarExclusao(PAFaseProcessoAdmWrapper entity) throws AppException {
        existeRelacionamentoComPAPrioridadeFluxoAmparo(entity.getEntidade());
    }

    
    private void existeCodigoAtivo(PAFaseProcessoAdmWrapper entity) throws DatabaseException, AppException{
        PAFaseProcessoAdmRepositorio repo = new PAFaseProcessoAdmRepositorio();
        PAFaseProcessoAdm duplicado = repo.getPAAndamentoProcessoAtivoByCodigo(getEntityManager(), entity.getEntidade().getCodigo());
            
        if(duplicado != null){
                if(entity.getId() == null){
                    DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entity.getEntidade().getCodigo(), "application.label.codigo");
                    if(!duplicado.getId().equals(entity.getId()))
                DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entity.getEntidade().getCodigo(), "application.label.codigo");
               }
            }     
    }
  
    private void existeRelacionamentoComPAPrioridadeFluxoAmparo(PAFaseProcessoAdm entidade) throws DatabaseException, AppException {
        PAPrioridadeFluxoAmparoRepositorio repositorio = new PAPrioridadeFluxoAmparoRepositorio();
        List<PAPrioridadeFluxoAmparo> resultado = repositorio.getPAPrioridadeFluxoAmparoAtivoPorPAFaseProcessoAdm(getEntityManager(), (Long) entidade.getId());

        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
    
    private void validaCodigoTamanho(PAFaseProcessoAdmWrapper entidade) throws AppException{
        String codigo = entidade.getEntidade().getCodigo();
        if(codigo.length() != 6){
            DetranWebUtils.applicationMessageException("O campo código deve conter 6 digitos.");
        }
    }
}
