package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.core.projeto.enums.apo.EstadoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.ClassificacaoPenalEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.DadosInfracaoPAD;
import br.gov.ms.detran.processo.administrativo.entidade.FunctionProcessoAdministrativoCompetenciaInfracao;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * @author yanko.campos
 */
public class DadosInfracaoPADRepositorio extends AbstractJpaDAORepository<DadosInfracaoPAD> {
    
    private static final Logger LOG = Logger.getLogger(DadosInfracaoPADRepositorio.class);

    /**
     * Retorna todas as infracoes buscadas por CPF.
     *
     * @param em
     * @param cpf
     * @return
     * @throws DatabaseException
     */
    public List<DadosInfracaoPAD> getTodasInfracoesPorCpfInfrator(EntityManager em, String cpf) throws DatabaseException {
        
        return this.getListNamedQuery(em, "DadosInfracaoPAD.getTodasInfracoesPorCpfCondutor", cpf);
        
    }

    /**
     * Retorna lista de infrações buscadas por cpf no período de um ano anterior a data passada.
     *
     * @param em
     * @param cpf
     * @param infracaoCodigo
     * @param dataInfracao
     * @return
     * @throws br.gov.ms.detran.comum.util.exception.AppException
     */
    public List<DadosInfracaoPAD> getAMesmaInfracaoFoiRealizadaEm1AnoOuMenos(
        EntityManager em, String cpf, String infracaoCodigo, Date dataInfracao, Long isn) throws AppException {

        if(dataInfracao == null || DetranStringUtil.ehBrancoOuNulo(cpf) || DetranStringUtil.ehBrancoOuNulo(infracaoCodigo)) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }
        
        return 
            this
                .getListNamedQuery(
                    em, 
                    "DadosInfracaoPAD.getAMesmaInfracaoFoiRealizadaEm1AnoOuMenos",
                    cpf,
                    infracaoCodigo,
                    Utils.subtraiAno(dataInfracao, 1),
                    dataInfracao,
                    isn
                );
    }

    /**
     * @param em
     * @param cpfCondutor
     * @param origem
     * @param classificacaoPenal
     * @return
     * @throws DatabaseException 
     */
    public List<DadosInfracaoPAD> getInfracoesParaCenarioPontuacao(EntityManager em, 
                                                                   String cpfCondutor, 
                                                                   ApoioOrigemInstauracao origem,
                                                                   ClassificacaoPenalEnum classificacaoPenal) throws DatabaseException {
        
        Object[] params = {cpfCondutor,
                           origem.getAmparoLegal(),
                           origem.getMotivo() == null? null : origem.getMotivo().ordinal(),
                           classificacaoPenal == null? null : classificacaoPenal.ordinal(),
                           AtivoEnum.ATIVO.ordinal()};

        return this.getListNamedQuery(em, "DadosInfracaoPAD.getInfracoesPorPontuacao", params);
    }
    
    /**
     * @param em
     * @param cpfCondutor
     * @param origem
     * @return
     * @throws DatabaseException 
     */
    public List<DadosInfracaoPAD> getInfracoesPorCpfEAmparoLegal(EntityManager em, 
                                                                 String cpfCondutor, 
                                                                 ApoioOrigemInstauracao origem) throws DatabaseException {
        
        Object[] params = {cpfCondutor,
                           origem.getAmparoLegal(),
                           AtivoEnum.ATIVO.ordinal()};

        return this.getListNamedQuery(em, "DadosInfracaoPAD.getInfracoesPorCpfEAmparoLegal", params);
    }
    
    /**
     * @param em
     * @param cpfCondutor
     * @param origem
     * @return
     * @throws DatabaseException 
     */
    public List<DadosInfracaoPAD> getInfracoesPara_C5(
        EntityManager em, String cpfCondutor, ApoioOrigemInstauracao origem) throws DatabaseException {
        
        Object[] params 
            = {
                cpfCondutor,
                origem.getAmparoLegal(),
                AtivoEnum.ATIVO.ordinal(),
                origem.getIndiceHistoricoInfracao() == null? null : origem.getIndiceHistoricoInfracao().ordinal()
            };
        
        return this.getListNamedQuery(em, "DadosInfracaoPAD.getInfracoesParaEspecificada_R3_3", params);
    }
    
    public List<DadosInfracaoPAD> getInfracoesPorCpfEAmparoEIndiceReincidenciaMAZ(EntityManager em, 
                                                                    String cpfCondutor, 
                                                                    ApoioOrigemInstauracao origem) throws DatabaseException {
        Object[] params = {cpfCondutor,
                           origem.getAmparoLegal(),
                           origem.getIndiceReincidenciaMAZ() == null? null : origem.getIndiceReincidenciaMAZ().ordinal(),
                           AtivoEnum.ATIVO.ordinal()};
        
        return this.getListNamedQuery(em, "DadosInfracaoPAD.getInfracoesPorCpfEAmparoEIndiceReincidenciaMAZ", params);
    }

    /**
     * 
     * @param em
     * @param cpf
     * @param origem
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public List getInfracoesPorCpfEAmparoEAcaoEHistoricoInfracao(EntityManager em, String cpf, ApoioOrigemInstauracao origem) throws DatabaseException {
        Object[] params = {cpf,
                           origem.getAmparoLegal(),
                           origem.getIndiceHistoricoInfracao() == null ? null :origem.getIndiceHistoricoInfracao().ordinal(),
                           origem.getResultadoAcao() == null? null : origem.getResultadoAcao().ordinal(),
                           AtivoEnum.ATIVO.ordinal()};
        
        return this.getListNamedQuery(em, "DadosInfracaoPAD.getInfracoesPorCpfEAmparoEParaCenario3", params);
    }

    /**
     * 
     * @param em
     * @param dadosInfracaoPAD
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.DatabaseException 
     */
    public boolean verificaInfracaoEhEspecificadaEAutuadaCompetenciaDetranOuOrgaoDelegou(EntityManager em, DadosInfracaoPAD dadosInfracaoPAD) throws DatabaseException {
        
        String codigoTipoEmpresa                    = "7";
        List<String> competenciaEstadualMunicipal   = DetranCollectionUtil.montaLista("0", "1");
        List<String> competenciaFederalRodoviaria   = DetranCollectionUtil.montaLista("2", "3");
        
        Object[] params 
            = {
                dadosInfracaoPAD.getInfracaoCodigo(),
                codigoTipoEmpresa,
                ClassificacaoPenalEnum.ESPECIFICADA.ordinal(),
                competenciaEstadualMunicipal,
                EstadoEnum.MS.name(),
                Utils.getDate("12/04/2021", "dd/MM/yyyy"),
                competenciaFederalRodoviaria,
                AtivoEnum.ATIVO.ordinal(),
                dadosInfracaoPAD.getCpfCondutor().getCpf()
            };
        
        return !DetranCollectionUtil.ehNuloOuVazio(this.getListNamedQuery(em, "DadosInfracaoPAD.verificaInfracaoEhEspecificadaEAutuadaCompetenciaDetranOuOrgaoDelegou", params));
    }
    
    /**
     * 
     * @param em
     * @param cpfCondutor
     * @param autoInfracao
     * @return
     * @throws DatabaseException 
     */
    public Boolean verificaInfracaoEhEspecificadaEAutuadaCompetenciaDetranOuOrgaoDelegou(EntityManager em, String cpfCondutor, String autoInfracao) throws DatabaseException {
    
        List<FunctionProcessoAdministrativoCompetenciaInfracao> lResultado = Collections.emptyList();

        try {

            String squery = "SELECT * FROM dbo.FN_TDC_REGRA_INFRACAO_COMPETENCIA_INSTAURACAO(?, ?) x";

            Query q = em.createNativeQuery(squery, FunctionProcessoAdministrativoCompetenciaInfracao.class).setParameter(1, cpfCondutor).setParameter(2, autoInfracao);

            lResultado = q.getResultList();

        } catch (Exception e) {
            LOG.error("Impedimento ao checar infracão atende regra de competencia.", e);
        }

        return !DetranCollectionUtil.ehNuloOuVazio(lResultado);
    }
}