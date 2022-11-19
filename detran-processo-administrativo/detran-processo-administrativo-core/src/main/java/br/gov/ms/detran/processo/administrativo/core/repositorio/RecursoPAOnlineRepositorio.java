package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnline;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.PassoRecursoOnlinePAEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoSituacaoPAEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;

import javax.persistence.EntityManager;
import java.util.List;

public class RecursoPAOnlineRepositorio extends AbstractJpaDAORepository<RecursoPAOnline> {

    public RecursoPAOnline getRecursoOnlineEfetivadoPorPAETipoEDestino(EntityManager em, Long idPa, TipoFasePaEnum tipoRecurso, OrigemDestinoEnum destinoRecurso) throws AppException {

        List<RecursoSituacaoPAEnum> lSituacoes
                = DetranCollectionUtil.
                montaLista(RecursoSituacaoPAEnum.BACKOFFICE, RecursoSituacaoPAEnum.EFETIVADO, RecursoSituacaoPAEnum.RECUSADO_EXPIROU_PRAZO);

        return getNamedQuery(em, "RecursoPAOnline.getRecursoOnlineEfetivadoPorPAETipoEDestino", idPa, tipoRecurso, destinoRecurso, lSituacoes, AtivoEnum.ATIVO);
    }

    public RecursoPAOnline getRecursoOnlinePorTokenEProtocoloEPasso(EntityManager em, String token, String protocolo, PassoRecursoOnlinePAEnum passo) throws DatabaseException {
        return getNamedQuery(em, "RecursoPAOnline.getRecursoOnlinePorTokenEProtocoloEPasso", token, protocolo, passo, AtivoEnum.ATIVO);
    }

    public RecursoPAOnline getRecursoOnlineMaisRecentePorTokenEProtocolo(EntityManager em, String token, String protocolo) throws DatabaseException {
        List<RecursoPAOnline> list = getListNamedQuery(em, "RecursoPAOnline.getRecursoOnlineMaisRecentePorTokenEProtocolo", 0, 1, token, protocolo, AtivoEnum.ATIVO);
        return !DetranCollectionUtil.ehNuloOuVazio(list) ? list.get(0) : null;
    }

    public RecursoPAOnline getRecursoOnlinePorPaESituacao(EntityManager em, Long idPA, RecursoSituacaoPAEnum situacao) throws DatabaseException {
        return getNamedQuery(em, "RecursoPAOnline.getRecursoOnlinePorPaESituacao", idPA, situacao, AtivoEnum.ATIVO);
    }
    
    /**
     * 
     * @param em
     * @param idRecurso
     * @return
     * @throws DatabaseException 
     */
    public RecursoPAOnline getRecursoOnlinePorRecurso(EntityManager em, Long idRecurso) throws DatabaseException {
        return getNamedQuery(em, "RecursoPAOnline.getRecursoOnlinePorRecurso", idRecurso, AtivoEnum.ATIVO);
    }
}
