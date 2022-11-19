package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * @author Lillydi
 */
public class PAPenalidadeProcessoRepositorio  extends AbstractJpaDAORepository<PAPenalidadeProcesso> {

    /**
     * Retorna PAPenalidadeProcesso buscado por ProcessoAdministrativo.id e Ativo = 1.
     * 
     * @param em
     * @param idPa
     * @return
     * @throws AppException 
     */
    public PAPenalidadeProcesso getPenalidadePorPA(EntityManager em, Long idPa) throws AppException {
        return  getNamedQuery(em, "PAPenalidadeProcesso.getPenalidadePorProcessoAdministrativo", idPa, AtivoEnum.ATIVO);
    }

    public PAPenalidadeProcesso getUltimaPenalidadePorControleCnh(EntityManager em, Long idControle) throws DatabaseException {
        Object[] params = {
            idControle,
            AcaoEntregaCnhEnum.ENTREGA,
            AtivoEnum.ATIVO
        };
        
        List<PAPenalidadeProcesso> retorno = getListNamedQuery(em, "PAPenalidadeProcesso.getUltimaPenalidadePorControleCnh", params);
         
        return DetranCollectionUtil.ehNuloOuVazio(retorno)? null : retorno.get(0);
    }

    /**
     * Recupera a PAPenalidadeProcesso com maior data fim penalidade buscada pelo CPF do condutor.
     * 
     * @param em
     * @param cpfCondutor
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public PAPenalidadeProcesso getPAPenalidadeProcessoMaiorDataFimPorCpfCondutor(EntityManager em, String cpfCondutor) throws DatabaseException {
        
        List<PAPenalidadeProcesso> lista = super.getListNamedQuery(em, 
                                                                   "PAPenalidadeProcesso.getPAPenalidadeProcessoMaiorDataFimPorCpfCondutor", 
                                                                   cpfCondutor, 
                                                                   AtivoEnum.ATIVO);
        
        return !DetranCollectionUtil.ehNuloOuVazio(lista) ? lista.get(0) : null;
    }

    /**
     * 
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException 
     */
    public BigDecimal getTotalPenalidadeAtivaPorCpf(EntityManager em, String cpf) throws DatabaseException {
        
        Long totalPenalidade 
            = super.getCountHqlEntitySearch(em, "PAPenalidadeProcesso.getTotalPenalidadeAtivaPorCpf", cpf, AtivoEnum.ATIVO);
        
        return totalPenalidade != null ? new BigDecimal(totalPenalidade) : BigDecimal.ZERO;
    }

    public PAPenalidadeProcesso getUltimaPenalidadePorCPF(EntityManager em, String cpf) throws DatabaseException {
        List<PAPenalidadeProcesso> retorno = getListNamedQuery(em, "PAPenalidadeProcesso.getUltimaPenalidadePorCPF", cpf, AtivoEnum.ATIVO);
         
        return DetranCollectionUtil.ehNuloOuVazio(retorno)? null : retorno.get(0);
    }
}