package br.gov.ms.detran.processo.administrativo.cp;

import br.gov.ms.detran.comum.projeto.anotacao.resource.Locator;
import br.gov.ms.detran.comum.projeto.anotacao.resource.LocatorType;
import br.gov.ms.detran.comum.projeto.negocio.impl.DetranCondicaoProcessoImpl;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoOrigemRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPrioridadeFluxoAmparoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PATipoCorpoAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoOrigem;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.PAPrioridadeFluxoAmparo;
import br.gov.ms.detran.processo.administrativo.entidade.PATipoCorpoAndamento;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoProcessoWrapper;
import java.util.List;

/**
 * @author Carlos
 */
@Locator(LocatorType.CONDICAO_PROCESSO)
public class PAFluxoProcessoCP extends DetranCondicaoProcessoImpl<PAFluxoProcessoWrapper> {

    @Override
    public void validarCadastro(PAFluxoProcessoWrapper entity) throws AppException {
        existeCodigoAtivo(entity);
        existeDescricaoAtivo(entity);
    }

    @Override
    public void validarExclusao(PAFluxoProcessoWrapper entidade)throws AppException{
        existeRelacionamentoComPAFluxoAndamento(entidade.getEntidade());
        existeRelacionamentoComPAFluxoOrigem(entidade.getEntidade());
        existeRelacionamentoComPAOcorrenciaStatus(entidade.getEntidade());
        existeRelacionamentoComPAPrioridadeFluxoAmparo(entidade.getEntidade());
        existeRelacionamentoComPATipoCorpoAndamentoEdital(entidade.getEntidade());
        existeRelacionamentoComPATipoCorpoAndamentoRecurso(entidade.getEntidade());
    }
    
    /**
     * @param entity
     * @throws DatabaseException
     * @throws AppException 
     */
    private void existeCodigoAtivo(PAFluxoProcessoWrapper entity) throws DatabaseException, AppException {
        PAFluxoProcessoRepositorio repo = new PAFluxoProcessoRepositorio();
        PAFluxoProcesso duplicado = repo.getPAFluxoProcessoPorCodigo(getEntityManager(), entity.getEntidade().getCodigo());

        if (duplicado != null) {
            if (entity.getId() == null) {
                DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entity.getEntidade().getCodigo().toString(), "application.label.codigo");
                if (!duplicado.getId().equals(entity.getId())) {
                    DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entity.getEntidade().getCodigo().toString(), "application.label.codigo");
                }
            }
        }
    }

    /**
     * @param entidade
     * @throws AppException 
     */
    private void existeDescricaoAtivo(PAFluxoProcessoWrapper entidade) throws AppException {
        PAFluxoProcessoRepositorio repo = new PAFluxoProcessoRepositorio();
        PAFluxoProcesso duplicado = repo.getPAFluxoProcessoByDescricao(getEntityManager(), entidade.getEntidade().getDescricao());

        if (duplicado != null) {
            if (entidade.getId() == null) {
                DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entidade.getEntidade().getDescricao(), "application.label.descricao");
            } else {
                if (!duplicado.getId().equals(entidade.getId())) {
                    DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entidade.getEntidade().getDescricao(), "application.label.descricao");
                }
            }
        }
    }

    /**
     * @param entidade
     * @throws DatabaseException
     * @throws AppException 
     */
    private void existeRelacionamentoComPATipoCorpoAndamentoEdital(PAFluxoProcesso entidade) throws DatabaseException, AppException {
        PATipoCorpoAndamentoRepositorio repositorio = new PATipoCorpoAndamentoRepositorio();
        List<PATipoCorpoAndamento> resultado = repositorio.getTipoCorpoAndamentoAtivoPorPAFluxoProcessoEdital(getEntityManager(), entidade.getId());
        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
    
    /**
     * @param entidade
     * @throws DatabaseException
     * @throws AppException 
     */
    private void existeRelacionamentoComPATipoCorpoAndamentoRecurso(PAFluxoProcesso entidade) throws DatabaseException, AppException{
        PATipoCorpoAndamentoRepositorio repositorio = new PATipoCorpoAndamentoRepositorio();
        List <PATipoCorpoAndamento> resultado = repositorio.getTipoCorpoAndamentoAtivoPorPAFluxoProcessoRecurso(getEntityManager(), entidade.getId());
        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    
    }
    
    /**
     * @param entidade
     * @throws DatabaseException
     * @throws AppException 
     */
    private void existeRelacionamentoComPAPrioridadeFluxoAmparo(PAFluxoProcesso entidade) throws DatabaseException, AppException {
        PAPrioridadeFluxoAmparoRepositorio repositorio = new PAPrioridadeFluxoAmparoRepositorio();
        List<PAPrioridadeFluxoAmparo> resultado = repositorio.getPAPrioridadeFluxoAmparoAtivoPorPAFluxoProcessoo(getEntityManager(), entidade.getId());
        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
    
    /**
     * @param entidade
     * @throws DatabaseException
     * @throws AppException 
     */
    private void existeRelacionamentoComPAFluxoOrigem(PAFluxoProcesso entidade) throws DatabaseException, AppException {
        PAFluxoOrigemRepositorio repositorio = new PAFluxoOrigemRepositorio();
        List<PAFluxoOrigem> resultado = repositorio.getPAFluxoOrigemAtivoPorPAFluxoProcesso(getEntityManager(), entidade.getId());
        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
    
    /**
     * @param entidade
     * @throws DatabaseException
     * @throws AppException 
     */
    private void existeRelacionamentoComPAFluxoAndamento(PAFluxoProcesso entidade) throws DatabaseException, AppException {
        PAFluxoAndamentoRepositorio repositorio = new PAFluxoAndamentoRepositorio();
        List<PAFluxoAndamento> resultado = repositorio.getPAFluxoAndamentoAtivoPorPAFluxoProcesso(getEntityManager(), entidade.getId());
        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
    
    /**
     * @param entidade
     * @throws DatabaseException
     * @throws AppException 
     */
    private void existeRelacionamentoComPAOcorrenciaStatus(PAFluxoProcesso entidade) throws DatabaseException, AppException {
        PAOcorrenciaStatusRepositorio repositorio = new PAOcorrenciaStatusRepositorio();
        List<PAOcorrenciaStatus> resultado = repositorio.getPAOcorrenciaStatusAtivoPorPAFluxoProcesso(getEntityManager(), entidade.getId());
        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
}