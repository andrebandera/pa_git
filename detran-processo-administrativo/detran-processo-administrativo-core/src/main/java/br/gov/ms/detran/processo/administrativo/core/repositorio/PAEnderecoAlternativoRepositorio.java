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
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAEnderecoAlternativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.PAEnderecoAlternativoWrapper;

import javax.persistence.EntityManager;
import java.util.List;

/**
 *
 * @author Lillydi
 */
public class PAEnderecoAlternativoRepositorio extends AbstractJpaDAORepository<PAEnderecoAlternativo> {

    public void validarEnderecoJaExistenteParaPA(EntityManager em, PAEnderecoAlternativoWrapper wrapper) throws AppException {
        Long paJaExistente = getCountHqlEntitySearch(em, "PAEnderecoAlternativo.validarEnderecoJaExistenteParaPA", wrapper.getEntidade().getProcessoAdministrativo().getNumeroProcesso(), wrapper.getId(), AtivoEnum.ATIVO);
        if(paJaExistente != null && paJaExistente > 0){
            DetranWebUtils.applicationMessageException("Já existe Endereço Alternativo cadastrado para este Processo Administrativo.");
        }
    }

    public PAEnderecoAlternativo getEnderecoAtivoPorPA(EntityManager em, Long idPA) throws DatabaseException {

        List<PAEnderecoAlternativo> retorno = getListNamedQuery(em, "PAEnderecoAlternativo getEnderecoAtivoPorPA", idPA, AtivoEnum.ATIVO);

        return DetranCollectionUtil.ehNuloOuVazio(retorno) ? null : retorno.get(0);
    }


    public PAEnderecoAlternativo getEnderecoPorPAETipoNotificacao(EntityManager em, Long idPA, TipoFasePaEnum tipoRecurso) throws DatabaseException {

        return getNamedQuery(em, "PAEnderecoAlternativo.getEnderecoPorPAETipoNotificacao", idPA, tipoRecurso, AtivoEnum.ATIVO);
    }
}
