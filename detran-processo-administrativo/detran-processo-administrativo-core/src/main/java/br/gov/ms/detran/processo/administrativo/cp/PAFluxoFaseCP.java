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
import br.gov.ms.detran.processo.administrativo.core.repositorio.DestinoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRetornoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PATipoCorpoAndamentoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.DestinoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFaseRetorno;
import br.gov.ms.detran.processo.administrativo.entidade.PATipoCorpoAndamento;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoFaseWrapper;
import java.util.List;

/**
 *
 * @author Carlos Eduardo
 */
@Locator(LocatorType.CONDICAO_PROCESSO)
public class PAFluxoFaseCP extends DetranCondicaoProcessoImpl<PAFluxoFaseWrapper> {
    
    @Override
    public void validarExclusao(PAFluxoFaseWrapper entity) throws AppException {
        existeRelacionamentoComPATipoCorpoAndamento(entity.getEntidade());
        existeRelacionamentoComPAFluxoFaseRetorno(entity.getEntidade());
        existeRelacionamentoComPAFluxoAndamento(entity.getEntidade());
        existeRelacionamentoComDestinoFase(entity.getEntidade());
    }
    
    private void existeRelacionamentoComPATipoCorpoAndamento(PAFluxoFase entidade) throws DatabaseException, AppException {
        PATipoCorpoAndamentoRepositorio repositorio = new PATipoCorpoAndamentoRepositorio();
        List<PATipoCorpoAndamento> resultado = repositorio.getPATipoCorpoAndamentoPorPAFluxoFaseVinculos(getEntityManager(), entidade.getId());

        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui vínculo ativo!");
        }
    }
    
    private void existeRelacionamentoComPAFluxoFaseRetorno(PAFluxoFase entidade) throws DatabaseException, AppException {
        PAFluxoFaseRetornoRepositorio repositorio = new PAFluxoFaseRetornoRepositorio();
        List<PAFluxoFaseRetorno> resultado = repositorio.getPAFluxoFaseRetornoPorPAFluxoFaseVinculos(getEntityManager(), entidade.getId());

        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui vínculo ativo!");
        }
    }
    
    private void existeRelacionamentoComPAFluxoAndamento(PAFluxoFase entidade) throws DatabaseException, AppException {
        PAFluxoAndamentoRepositorio repositorio = new PAFluxoAndamentoRepositorio();
        List<PAFluxoAndamento> resultado = repositorio.getPAFluxoAndamentoPorPAFluxoFaseVinculos(getEntityManager(), entidade.getId());
        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui vínculo ativo!");
        }
    }
    
    private void existeRelacionamentoComDestinoFase(PAFluxoFase entidade) throws DatabaseException, AppException {
        DestinoFaseRepositorio repositorio = new DestinoFaseRepositorio();
        List<DestinoFase> resultado = repositorio.getDestinoFasePorPAFluxoFaseVinculos(getEntityManager(), entidade.getId());
        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui vínculo ativo!");
        }
    }
}
