package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranDateUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;

/**
 * @author yanko.campos
 */
public class DadosCondutorPADBO {

    public DadosCondutorPADBO() {

    }

    /**
     * Valida se a Cnh do condutor é definitiva.
     *
     * @param dadosCondutorPAD
     * @throws AppException
     */
    public void validaCnhDefinitiva(DadosCondutorPAD dadosCondutorPAD) throws AppException {

        if (null == dadosCondutorPAD) {
            DetranWebUtils.applicationMessageException("Dados do Condutor Inválidos.");
        }

        if (null == dadosCondutorPAD.getDataHabilitacaoDefinitiva()) {
            DetranWebUtils.applicationMessageException("Cnh Definitiva do Condutor Inválida.");
        }

        if (!DetranDateUtil.ehDataValida(dadosCondutorPAD.getDataHabilitacaoDefinitiva())) {
            DetranWebUtils.applicationMessageException("Data Cnh Definitiva do Condutor Inválida.");
        }
    }
}