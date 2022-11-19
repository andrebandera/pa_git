package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentacaoInfracaoPA;
import java.util.List;
import javax.persistence.EntityManager;

public class MovimentacaoInfracaoPARepositorio extends AbstractJpaDAORepository<MovimentacaoInfracaoPA> {

    public List getInfracoesMovimentacaoParaReativar(EntityManager em, Long paId) throws DatabaseException {
        return super.getListNamedQuery(em, "MovimentacaoInfracaoPA.getInfracoesMovimentacaoParaReativar", paId, BooleanEnum.SIM, AtivoEnum.DESATIVADO);
    }

    public String getIndicativoReativacaoInfracao(EntityManager em, Long movimentacaoId, Long paId) throws DatabaseException {
        List <MovimentacaoInfracaoPA> list =
                super.getListNamedQuery(em,"MovimentacaoInfracaoPA.getMovimentacaoInfracaoPAPorMovimentacaoEProcessoAdministrativo", movimentacaoId, paId, AtivoEnum.ATIVO);
        if(DetranCollectionUtil.ehNuloOuVazio(list) || null == list.get(0).getIndicativoReativarPontuacao()){
            return  null;
        }
        return list.get(0).getIndicativoReativarPontuacao().equals(BooleanEnum.NAO) ? "Não".toUpperCase() : "Sim".toUpperCase();
    }
}
