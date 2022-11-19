package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.entidade.BloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.FunctionConsistenciaBloqueioBCAAtualizacao;
import br.gov.ms.detran.processo.administrativo.entidade.FunctionProcessoAdministrativoBloqueio;
import br.gov.ms.detran.processo.administrativo.entidade.ProcedureProcessoAdministrativoBloqueio;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoBloqueioBCAEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoMovimentoBloqueioBCAEnum;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;

public class BloqueioBCARepositorio extends AbstractJpaDAORepository<BloqueioBCA> {

    private static final Logger LOG = Logger.getLogger(BloqueioBCARepositorio.class);

    @Resource(mappedName = "jdbc/detran")
    private DataSource dataSource;

    /**
     * @param em
     * @param idPa
     * @return
     * @throws AppException
     */
    public BloqueioBCA getBloqueioBCAporPA(EntityManager em, Long idPa) throws AppException {
        return getNamedQuery(em, "BloqueioBCA.getBloqueioPorProcessoAdministrativo", idPa, AtivoEnum.ATIVO);
    }

    /**
     * Retorna Bloqueio BCA buscado por PA.id, Situacao e Ativo = 1;
     *
     * @param em
     * @param paId
     * @param situacaoBloqueioBCA
     * @return
     */
    public BloqueioBCA getBloqueioBCAporPaESituacaoEAtivo(EntityManager em, Long paId, SituacaoBloqueioBCAEnum situacaoBloqueioBCA) throws DatabaseException {
        return getNamedQuery(em,
                "BloqueioBCA.getBloqueioBCAporPaESituacaoEAtivo",
                paId,
                situacaoBloqueioBCA,
                AtivoEnum.ATIVO);
    }

    public void validarInfracaoCometidaPeriodoReincidente(EntityManager em, Date dataInfracao, List<Long> idsPA) throws DatabaseException, RegraNegocioException {
        List<BloqueioBCA> bloqueios = getListNamedQuery(em,
                "BloqueioBCA.validarInfracaoCometidaPeriodoReincidente",
                idsPA,
                AtivoEnum.ATIVO,
                dataInfracao,
                SituacaoBloqueioBCAEnum.ATIVO);

        if (DetranCollectionUtil.ehNuloOuVazio(bloqueios)) {
            throw new RegraNegocioException("Infração cometida fora do periodo reincidente.");
        }

    }

    /**
     * @param em
     * @param paId
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException
     */
    public BloqueioBCA getBloqueioBcaPorPaEAtivo(EntityManager em, Long paId) throws DatabaseException {
        return getNamedQuery(em,
                "BloqueioBCA.getBloqueioBcaPorPaEAtivo",
                paId,
                AtivoEnum.ATIVO);
    }

    /**
     * @param em
     * @param codigosAndamentos
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    public List<BloqueioBCA> getBloqueiosPorAndamento(EntityManager em,
            List<Integer> codigosAndamentos) throws AppException {

        if (DetranCollectionUtil.ehNuloOuVazio(codigosAndamentos)) {
            DetranWebUtils.applicationMessageException("Parâmetro obrigatório inválido.");
        }

        List<BloqueioBCA> lista
                = getListNamedQuery(em,
                        "BloqueioBCA.getBloqueiosPorAndamento",
                        codigosAndamentos,
                        SituacaoOcorrenciaEnum.INICIADO,
                        AtivoEnum.ATIVO,
                        SituacaoBloqueioBCAEnum.ATIVO);

        return lista;
    }

    /**
     * Retorna Bloqueio BCA buscado por PA.cpf, Situacao e Ativo = 1;
     *
     * @param em
     * @param paCpf
     * @param situacaoBloqueioBCA
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException
     */
    public List<BloqueioBCA> getBloqueioBCAPorCpfESituacaoEAtivo(EntityManager em, String paCpf, SituacaoBloqueioBCAEnum situacaoBloqueioBCA) throws DatabaseException {
        return getListNamedQuery(em,
                "BloqueioBCA.getBloqueioBcaPorCpfESituacaoEAtivo",
                paCpf,
                situacaoBloqueioBCA,
                AtivoEnum.ATIVO);
    }

    /**
     * Método que busca a menor data do processo de um determinado CPF.Retorna a
     * menor data buscado por PA.cpf, tdz_tipo="DESBLOQUEIO" e Ativo = 1;
     *
     * (Buscando a HQL na Classe BloqueioBCA)
     *
     * @param em
     * @param paCpf
     * @return min = menor data
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException
     */
    @Deprecated
    public Date getBloqueioBcaPorCpfTipoEAtivoHQL(EntityManager em, String paCpf) throws DatabaseException {

        Query query = em.createNamedQuery("BloqueioBCA.getBloqueioBcaPorCpfTipoEAtivo")
                .setParameter("p0", TipoMovimentoBloqueioBCAEnum.DESBLOQUEIO)
                .setParameter("p1", paCpf)
                .setParameter("p2", AtivoEnum.ATIVO);

        query.setFirstResult(0).setMaxResults(1);

        BloqueioBCA bloqueio = (BloqueioBCA) query.getSingleResult();

        Date min = null;

        if (bloqueio != null) {
            min = bloqueio.getDataInicio();
        }

        return min;

    }

    /**
     *
     * @param em
     * @param cpf
     * @param idProcessoAdministrativo
     * @return
     * @throws AppException
     */
    public List<ProcedureProcessoAdministrativoBloqueio> executaProcedureProcessoAdministrativoBloqueio(EntityManager em, String cpf, Long idProcessoAdministrativo) throws AppException {

        List<ProcedureProcessoAdministrativoBloqueio> lResultado = Collections.emptyList();

        try {

            String squery = "{ call SP_TDH_AJUSTA_PROCESSO_ADMINISTRATIVO_BLOQUEIO_RES_723(?,?) }";

            Query q = em.createNativeQuery(squery, ProcedureProcessoAdministrativoBloqueio.class).setParameter(1, cpf).setParameter(2, idProcessoAdministrativo);

            lResultado = q.getResultList();

        } catch (Exception e) {
            LOG.error("Impedimento no ajuste para o CPF {0}", cpf, e);
        }

        return lResultado;
    }

    /**
     *
     * @param em
     * @param cpf
     * @return
     * @throws AppException
     */
    public List<FunctionProcessoAdministrativoBloqueio> getFunctionProcessoAdministrativoBloqueio(EntityManager em, String cpf) throws AppException {

        List<FunctionProcessoAdministrativoBloqueio> lResultado = Collections.emptyList();

        try {

            String squery = "SELECT * FROM dbo.FN_TDB_CONSULTA_PROCESSO_ADMINISTRATIVO_BLOQUEIO_RES_723(?) x";

            Query q = em.createNativeQuery(squery, FunctionProcessoAdministrativoBloqueio.class).setParameter(1, cpf);

            lResultado = q.getResultList();

        } catch (Exception e) {
            LOG.error("Impedimento na consulta de bloqueio.", e);
        }

        return lResultado;
    }
    
    /**
     *
     * @param em
     * @return
     * @throws AppException
     */
    public List<FunctionConsistenciaBloqueioBCAAtualizacao> getFunctionConsistenciaBloqueioBCAAtualizacao(EntityManager em) throws AppException {

        List<FunctionConsistenciaBloqueioBCAAtualizacao> lResultado = Collections.emptyList();

        try {

            String squery = "SELECT * FROM dbo.FN_TDD_CONSISTENCIA_BLOQUEIO_BCA_ATUALIZACAO() x";

            Query q = em.createNativeQuery(squery, FunctionConsistenciaBloqueioBCAAtualizacao.class);

            lResultado = q.getResultList();

        } catch (Exception e) {
            LOG.error("Impedimento consulta processos para atualizacao na BCA.", e);
        }

        return lResultado;
    }
}
