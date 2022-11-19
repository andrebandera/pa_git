/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.ConsultaCnhControleRecolhimento;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author psg
 */
public class ConsultaCnhControleRecolhimentoRepositorio extends AbstractJpaDAORepository<ConsultaCnhControleRecolhimento> {
 
    public List buscarCnhControleSemValidade(EntityManager em, List codigosAndamentoPA) throws DatabaseException {
        Object[] params = {TipoSituacaoProtocoloEnum.APRESENTACAO.name(), 
                           AcaoEntregaCnhEnum.ENTREGA.ordinal(), 
                           codigosAndamentoPA};
        
        return super.getListNamedQuery(em, "ConsultaCnhControleRecolhimento.buscarCnhControleSemValidade", params);
    }
    
    public List buscarRecolhimentoCnh(EntityManager em, Long idProcessoAdministrativo) throws DatabaseException {
        return super.getListNamedQuery(em, "ConsultaCnhControleRecolhimento.buscarRecolhimentoCnh", idProcessoAdministrativo);
    }
    
}
