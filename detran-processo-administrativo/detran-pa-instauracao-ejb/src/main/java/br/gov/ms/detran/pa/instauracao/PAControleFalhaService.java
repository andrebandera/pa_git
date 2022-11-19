/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.pa.instauracao;

import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoFalhaBO;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Christiano Carrilho.
 */
@Stateless(mappedName = "ejb/PAControleFalhaService")
@Remote(IPAControleFalhaService.class)
public class PAControleFalhaService implements IPAControleFalhaService {

    @PersistenceContext(unitName = "DETRAN-PA-PU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gravarFalhaCondutor(Exception e, String origemErro, String cpf) {
        new ProcessoAdministrativoFalhaBO().gravarFalhaCondutor(em, origemErro, cpf, e);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gravarFalha(Exception e, String origemErro) {
        new ProcessoAdministrativoFalhaBO().gravarFalhaGenerica(em, origemErro, e);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gravarFalhaEspecifica(String cpf, String mensagem, String origem) {
        new ProcessoAdministrativoFalhaBO().gravarFalhaCondutorEspecifica(em, cpf, mensagem, origem);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gravarFalhaProcessoAdministrativo(Exception e, String origemErro, String cpf, String numeroProcesso) {
        new ProcessoAdministrativoFalhaBO().gravarFalhaProcessoAdministrativo(em, origemErro, cpf, numeroProcesso, e);
    }
}