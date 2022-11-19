/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.cp;

import br.gov.ms.detran.comum.projeto.anotacao.resource.Locator;
import br.gov.ms.detran.comum.projeto.anotacao.resource.LocatorType;
import br.gov.ms.detran.comum.projeto.negocio.impl.DetranCondicaoProcessoImpl;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ApoioOrigemInstauracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoOrigemRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PATipoCorpoAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoOrigem;
import br.gov.ms.detran.processo.administrativo.entidade.PATipoCorpoAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.wrapper.ApoioOrigemInstauracaoWrapper;
import java.util.List;

/**
 * @author Carlos
 */
@Locator(LocatorType.CONDICAO_PROCESSO)
public class ApoioOrigemInstauracaoCP extends DetranCondicaoProcessoImpl<ApoioOrigemInstauracaoWrapper> {

    @Override
    public void validarCadastro(ApoioOrigemInstauracaoWrapper entity) throws AppException {
        existeRegra(entity);
        existeDescricaoAtivo(entity);
    }

    @Override
    public void validarExclusao(ApoioOrigemInstauracaoWrapper entity) throws AppException {
        existeRelacionamentoComPAFluxoOrigem(entity.getEntidade());
        existeRelacionamentoComPATipoCorpoAndamento(entity.getEntidade());
        existeRelacionamentoComProcessoAdministrativo(entity.getEntidade());
    }
   
    private void existeRegra(ApoioOrigemInstauracaoWrapper entity) throws DatabaseException, AppException {
        ApoioOrigemInstauracaoRepositorio repo = new ApoioOrigemInstauracaoRepositorio();
        ApoioOrigemInstauracao duplicado = repo.getApoioOrigemPorRegra(getEntityManager(), entity.getEntidade().getRegra());
        if (duplicado != null) {
            if (entity.getId() == null) {
                DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entity.getEntidade().getRegra().name(), "regra");
                if (!duplicado.getId().equals(entity.getId())) {
                    DetranWebUtils.applicationMessageException("severity.error.application.exist", "\"", entity.getEntidade().getRegra().name(), "regra");
                }
            }
        }
    }

    private void existeDescricaoAtivo(ApoioOrigemInstauracaoWrapper entidade) throws AppException {
        ApoioOrigemInstauracaoRepositorio repo = new ApoioOrigemInstauracaoRepositorio();
        ApoioOrigemInstauracao duplicado = repo.getApoioOrigemPorDescricao(getEntityManager(), entidade.getEntidade().getDescricao());

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
    
    
    private void existeRelacionamentoComPATipoCorpoAndamento(ApoioOrigemInstauracao entidade) throws DatabaseException, AppException {
        PATipoCorpoAndamentoRepositorio repositorio = new PATipoCorpoAndamentoRepositorio();
        List<PATipoCorpoAndamento> resultado = repositorio.getTipoCorpoAndamentoPorIDApoioOrigemInstauraucao(getEntityManager(), entidade.getId());

        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
    
    private void existeRelacionamentoComPAFluxoOrigem(ApoioOrigemInstauracao entidade) throws DatabaseException, AppException {
        PAFluxoOrigemRepositorio repositorio = new PAFluxoOrigemRepositorio();
        PAFluxoOrigem resultado = repositorio.getPAFluxoOrigemPorIDApoioOrigemInstauracao(getEntityManager(), entidade.getId());

        if (resultado != null) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
    
    private void existeRelacionamentoComProcessoAdministrativo(ApoioOrigemInstauracao entidade) throws DatabaseException, AppException {
        ProcessoAdministrativoRepositorio repositorio = new ProcessoAdministrativoRepositorio();
        List<ProcessoAdministrativo> resultado = repositorio.getProcessoAdministrativoPorIDApoioOrigemInstauraucao(getEntityManager(), entidade.getId());

        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui relacionamento ativo!");
        }
    }
}
