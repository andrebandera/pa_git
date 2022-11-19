package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.entidade.FunctionProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevido;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class ProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevidoRepositorio extends AbstractJpaDAORepository<FunctionProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevido> {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevidoRepositorio.class);

    /**
     *
     * @param em
     * @return
     * @throws AppException
     */
    public List<FunctionProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevido> getFunctionProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevido(EntityManager em) throws AppException {

        List<FunctionProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevido> lResultado = Collections.emptyList();

        try {

            String squery = "SELECT * FROM dbo.FN_TDE_PROCESSO_ARQUIVAMENTO_INDEVIDO() x";

            Query q = em.createNativeQuery(squery, FunctionProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevido.class);

            lResultado = q.getResultList();

        } catch (Exception e) {
            LOG.error("Impedimento consulta processos arquivamento indevido.", e);
        }

        return lResultado;
    }
}
