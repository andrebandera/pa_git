package br.gov.ms.detran.pa.instauracao;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.projeto.service.DetranAbstractGenericService;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.RegraNegocioException;
import br.gov.ms.detran.processo.administrativo.core.bo.AndamentoProcessoAdministrativoManager2;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoInfracaoBO;
import br.gov.ms.detran.processo.administrativo.core.bo.RegrasGeralInstauracaoCondutorBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ApoioOrigemInstauracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ExecucaoInstauracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAOcorrenciaStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPAInstauracaoService;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import br.gov.ms.detran.processo.administrativo.entidade.ExecucaoInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.enums.InstauracaoCenarioEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ApoioOrigemInstauracaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.InstaurarProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.PABPMSWrapper;
import java.util.List;
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
@Stateless(mappedName = "ejb/PAInstauracaoService")
@Remote(IPAInstauracaoService.class)
public class PAInstauracaoService extends DetranAbstractGenericService implements IPAInstauracaoService {

    @Override
    @PersistenceContext(unitName = "DETRAN-PA-PU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void validarCondutorParaInstaurarPA(DadosCondutorPAD dadosCondutorPAD) throws RegraNegocioException, AppException {
        new RegrasGeralInstauracaoCondutorBO().regrasCondutorGeral(em, dadosCondutorPAD);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IBaseEntity instaurar(Object cenarioInstaurar, IBaseEntity iBaseEntity) throws AppException {

        InstauracaoCenarioEnum cenario = (InstauracaoCenarioEnum) cenarioInstaurar;
        return cenario.getInstauracaoCenario().instaurar(em, (InstaurarProcessoAdministrativoWrapper) iBaseEntity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IBaseEntity buscarExecucaoInstauracaoPeloId(Long id) throws DatabaseException {
        return new ExecucaoInstauracaoRepositorio().find(em, ExecucaoInstauracao.class, id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void atualizarInfracoes(Object jsonWrapper, IBaseEntity iBaseEntity) throws AppException {
        new ProcessoAdministrativoInfracaoBO().atualizarInfracoes(em, (PABPMSWrapper)jsonWrapper, iBaseEntity);
    }

    @Override
    public List<ApoioOrigemInstauracaoWrapper> recuperarCenarios() {
        return new ApoioOrigemInstauracaoRepositorio().recuperarCenarios(em);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IBaseEntity gravar(IBaseEntity entidade) throws DatabaseException {
        return super.gravar(entidade);
    }

    @Override
    public BaseEntityAtivo getPAOcorrenciaStatusAtiva(Long processoId) throws AppException {
        return new PAOcorrenciaStatusRepositorio().getPAOcorrenciaStatusAtiva(em, processoId);
    }

    @Override
    public void proximoAndamento(Long id, BaseEntityAtivo ocorrenciaAtual) throws AppException {
        new AndamentoProcessoAdministrativoManager2().proximoAndamento(em, id, (PAOcorrenciaStatus) ocorrenciaAtual);
    }
}