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
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAPrioridadeFluxoAmparo;
import br.gov.ms.detran.processo.administrativo.wrapper.PAPrioridadeFluxoAmparoWrapper;
import java.util.List;

/**
 *
 * @author Carlos Eduardo
 */
@Locator(LocatorType.CONDICAO_PROCESSO)
public class PAPrioridadeFluxoAmparoCP extends DetranCondicaoProcessoImpl<PAPrioridadeFluxoAmparoWrapper> {

    @Override
    public void validarExclusao(PAPrioridadeFluxoAmparoWrapper entity) throws AppException {
        existeRelacionamentoComPAFluxoFase(entity.getEntidade());
    }
    
    private void existeRelacionamentoComPAFluxoFase(PAPrioridadeFluxoAmparo entidade) throws DatabaseException, AppException {
        PAFluxoFaseRepositorio repositorio = new PAFluxoFaseRepositorio();
        List<PAFluxoFase> resultado = repositorio.getPAFluxoFasePorPrioridadeFluxoAmparoVinculos(getEntityManager(), entidade.getId());

        if (!DetranCollectionUtil.ehNuloOuVazio(resultado)) {
            DetranWebUtils.applicationMessageException("Não é possivel excluir! Possui vínculo ativo!");
        }
    }
}
