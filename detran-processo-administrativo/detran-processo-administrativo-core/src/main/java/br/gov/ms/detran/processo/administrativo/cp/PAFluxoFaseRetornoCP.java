package br.gov.ms.detran.processo.administrativo.cp;

import br.gov.ms.detran.comum.projeto.anotacao.resource.Locator;
import br.gov.ms.detran.comum.projeto.anotacao.resource.LocatorType;
import br.gov.ms.detran.comum.projeto.negocio.impl.DetranCondicaoProcessoImpl;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoFaseRetornoWrapper;

/**
 *
 * @author CarlosEduardo
 */
@Locator(LocatorType.CONDICAO_PROCESSO)
public class PAFluxoFaseRetornoCP  extends DetranCondicaoProcessoImpl<PAFluxoFaseRetornoWrapper> {

    @Override
    public void validarCadastro(PAFluxoFaseRetornoWrapper entity) throws AppException {
        paFluxoFaseDuplicadoNoDestino(entity);
    }
    private void paFluxoFaseDuplicadoNoDestino(PAFluxoFaseRetornoWrapper wrapper) throws AppException{
        if(wrapper.getEntidade().getFluxoAtual().equals(wrapper.getEntidade().getFluxoRetorno())){
            DetranWebUtils.applicationMessageException("Não é permitido cadastrar Fluxo Atual igual a Fluxo Destino.");
        }
    }
    
}
