/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.ValorReferenciaLegal;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Lillydi
 */
public class ValorReferenciaLegalRepositorio  extends AbstractJpaDAORepository<ValorReferenciaLegal> {

    public ValorReferenciaLegal getValorPenalPorPontuacao(EntityManager em, ApoioOrigemInstauracao apoioOrigemInstauracao, Integer pontuacao) throws AppException {
        Object[] params = {
            apoioOrigemInstauracao.getRegra().name(),
            apoioOrigemInstauracao.getResultadoTipoProcesso().ordinal(),
            pontuacao,
            AtivoEnum.ATIVO.ordinal()
        };
        List<ValorReferenciaLegal> result = getListNamedQuery(em, "ValorReferenciaLegal.getValorPenalPorPontuacao", params);
        if(DetranCollectionUtil.ehNuloOuVazio(result) || result.size() > 1){
            DetranWebUtils.applicationMessageException("Não foi possível encontrar o valor penal.");
        }
        return result.get(0);
    }

}
