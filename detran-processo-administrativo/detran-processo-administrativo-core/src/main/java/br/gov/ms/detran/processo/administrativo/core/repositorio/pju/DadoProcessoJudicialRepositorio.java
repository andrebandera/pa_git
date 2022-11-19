package br.gov.ms.detran.processo.administrativo.core.repositorio.pju;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.enums.IdentificacaoRecolhimentoCnhEnum;
import br.gov.ms.detran.processo.administrativo.enums.PAStatusEnum;
import br.gov.ms.detran.processo.administrativo.enums.ParteProcessoJuridicoEnum;
import java.util.List;
import javax.persistence.EntityManager;

public class DadoProcessoJudicialRepositorio  extends AbstractJpaDAORepository<DadoProcessoJudicial> {

    public DadoProcessoJudicialRepositorio() {
    }
    
    /**
     * @param em
     * @param idPA
     * @return
     * @throws DatabaseException 
     */
    public DadoProcessoJudicial getDadoProcessoJudicialPorPA(EntityManager em, Long idPA) throws DatabaseException {
        return getNamedQuery(em, "DadoProcessoJudicial.getDadoProcessoJudicialPorPA", idPA, AtivoEnum.ATIVO);
    }

    public List getProcessosJuridicosParaEntregaCartorioPorCPF(EntityManager em, String cpf) throws DatabaseException {
        Object[] params = {
            cpf,
            ParteProcessoJuridicoEnum.CONDUTOR,
            IdentificacaoRecolhimentoCnhEnum.INEXISTENTE,
            AtivoEnum.ATIVO
        };
        return getListNamedQuery(em, "DadoProcessoJudicial.getProcessosJuridicosParaEntregaCartorioPorCPF", params);
    }
    
    public List getListaProcessoJuridicoAndamentoERecolhimentoCnh(EntityManager em, List<Integer> andamentos, IdentificacaoRecolhimentoCnhEnum recolhimentoCnhEnum) throws DatabaseException {
        Object[] params = {
            AtivoEnum.ATIVO,
            andamentos,
            recolhimentoCnhEnum,
        };
        return getListNamedQuery(em, "DadoProcessoJudicial.getListaProcessoJuridicoAndamentoERecolhimentoCnh", params);
    }

    public DadoProcessoJudicial getPJUComCnhEmCartorioPorCpf(EntityManager em, String cpf) throws DatabaseException {
        Object[] params = {
            cpf,
            IdentificacaoRecolhimentoCnhEnum.CARTORIO_JUDICIARIO,
            AtivoEnum.ATIVO,
            PAStatusEnum.ATIVO.getCodigo()
        };
        List<DadoProcessoJudicial> lista = getListNamedQuery(em, "DadoProcessoJudicial.getPJUComCnhEmCartorioPorCpf", params);
        
        return DetranCollectionUtil.ehNuloOuVazio(lista)? null : lista.get(0);
    }
}