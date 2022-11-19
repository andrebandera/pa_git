package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import static br.gov.ms.detran.comum.projeto.constantes.TipoNumeroSequencialConstante.FLUXO_PROCESSO_PA;
import br.gov.ms.detran.comum.projeto.service.FabricaJSPASequencial;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.core.bo.PAFluxoFaseBO;
import br.gov.ms.detran.processo.administrativo.core.bo.PAPrioridadeFluxoAmparoBO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ApoioOrigemInstauracaoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAStatusRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAStatus;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAAndamentoProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFaseProcessoAdmRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoFaseRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAFluxoProcessoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAPrioridadeFluxoAmparoRepositorio;
import br.gov.ms.detran.processo.administrativo.criteria.ApoioOrigemInstauracaoCriteria;
import br.gov.ms.detran.processo.administrativo.criteria.PAAndamentoProcessoCriteria;
import br.gov.ms.detran.processo.administrativo.criteria.PAFaseProcessoAdmCriteria;
import br.gov.ms.detran.processo.administrativo.criteria.PAFluxoProcessoCriteria;
import br.gov.ms.detran.processo.administrativo.criteria.PAStatusCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAPrioridadeFluxoAmparo;
import br.gov.ms.detran.processo.administrativo.wrapper.PAAndamentoProcessoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoFaseWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.PAFluxoProcessoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.PAPrioridadeFluxoAmparoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.PAStatusWrapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 *
 * @author Carlos Eduardo
 */
@Stateless(mappedName = "ejb/PAApoioService")
@Remote(IPAApoioService.class)
public class PAApoioService implements IPAApoioService {

    @Resource(mappedName = "jdbc/detran")
    private DataSource dataSource;

    @PersistenceContext(unitName = "DETRAN-PROCESSO-ADMINISTRATIVO-PU")
    private EntityManager em;

    @Override
    public PAStatusWrapper gravarStatusAndamento(PAStatusWrapper wrapper) throws AppException {

        PAStatusRepositorio repositorio = new PAStatusRepositorio();
        PAStatus entidade = wrapper.getEntidade();

        if (entidade != null && entidade.getId() == null) {

            repositorio.obterSequencialCodigoStatus(getDataSourceConnection(),
                    new FabricaJSPASequencial().getJSPANumeroSequencial(99),
                    entidade
            );

            repositorio.insert(em, entidade);

        } else {
            entidade = repositorio.update(em, entidade);
        }

        wrapper.setEntidade(entidade);
        return wrapper;
    }

    @Override
    public PAAndamentoProcessoWrapper gravarAndamentoProcesso(PAAndamentoProcessoWrapper wrapper) throws AppException {

        PAAndamentoProcessoRepositorio repositorio = new PAAndamentoProcessoRepositorio();
        PAAndamentoProcesso entidade = wrapper.getEntidade();

        if (entidade != null && entidade.getId() == null) {

            repositorio.obterSequencialCodigoStatus(getDataSourceConnection(),
                    new FabricaJSPASequencial().getJSPANumeroSequencial(100),
                    entidade
            );

            repositorio.insert(em, entidade);

        } else {
            entidade = repositorio.update(em, entidade);
        }

        wrapper.setEntidade(entidade);
        return wrapper;
    }

    @Override
    public PAFluxoProcessoWrapper gravarFluxoProcesso(PAFluxoProcessoWrapper wrapper) throws AppException {

        PAFluxoProcessoRepositorio repositorio = new PAFluxoProcessoRepositorio();
        
        PAFluxoProcesso entidade = wrapper.getEntidade();

        if (entidade != null && entidade.getId() == null) {

            repositorio
                    .obterSequencialCodigoStatus(
                            getDataSourceConnection(),
                            new FabricaJSPASequencial().getJSPANumeroSequencial(FLUXO_PROCESSO_PA),
                            entidade);
            
            repositorio.insert(em, entidade);

        } else {
            
            entidade = repositorio.update(em, entidade);
        }

        wrapper.setEntidade(entidade);
        return wrapper;
    }

    Connection getDataSourceConnection() throws AppException {
        try {
            return dataSource.getConnection();
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }
    
    @Override
    public PAFluxoProcesso getPAFluxoProcessoById(Long id) throws AppException {
        return new PAFluxoProcessoRepositorio().getPAFluxoProcessoById(em, id); 
    }
    
    
    @Override
    public ApoioOrigemInstauracao getApoioOrigemInstauracaoById(Long id) throws AppException{
        return new ApoioOrigemInstauracaoRepositorio().getApoioOrigemInstauracaoById(em, id);
    }
    
    @Override
    public List getAndamentoPorPrioridadeFluxoAmparo(Object criteria) throws AppException{
        PAAndamentoProcessoCriteria c = (PAAndamentoProcessoCriteria) criteria;
        return new PAAndamentoProcessoRepositorio().getAndamentoPorFluxoFase
        (em, c.getFrom(), c.getTo(),c.getPrioridadeFluxoAmparo().getId(), c.getDescricao(), AtivoEnum.ATIVO);   
    }
    
    @Override
    public Long getCountAndamentoPorPrioridadeFluxoAmparo(Object criteria) throws AppException{
        PAAndamentoProcessoCriteria c = (PAAndamentoProcessoCriteria) criteria;
        return new PAAndamentoProcessoRepositorio().getCountAndamentoNaoUtilizadoPeloFaseFluxo
        (em,c.getPrioridadeFluxoAmparo().getId(), c.getDescricao(), AtivoEnum.ATIVO);
    }
    @Override
    public List getOrigemInstauracaoPorFluxoProcesso(Object criteria) throws AppException {
        ApoioOrigemInstauracaoCriteria c = (ApoioOrigemInstauracaoCriteria) criteria;
        return new ApoioOrigemInstauracaoRepositorio().getApoioOrigemInstauracaoPorFluxoProcesso
        (em, c.getFrom(), c.getTo(),c.getFluxoProcesso().getId(), c.getDescricao(), AtivoEnum.ATIVO);
    }
    
    @Override
    public Long getCountOrigemInstauracaoPorFluxoProcesso(Object criteria) throws AppException {
        ApoioOrigemInstauracaoCriteria c = (ApoioOrigemInstauracaoCriteria) criteria;
        return new ApoioOrigemInstauracaoRepositorio().getCountApoioOrigemInstauracaoPorFluxoProcesso
        (em, c.getFluxoProcesso().getId(), c.getDescricao(), AtivoEnum.ATIVO);
    }
    
    @Override
    public PAStatus getPAStatusAtivoPorId(Long id) throws AppException{
        return new PAStatusRepositorio().getPAStatusAtivoById(em, id);
    }
    
    @Override
    public List getStatusPorAndamentoPA(Object criteria) throws AppException {
        PAStatusCriteria c = (PAStatusCriteria) criteria;
        return new PAStatusRepositorio().getPAStatusPorAndamentoPA(em, c.getFrom(), c.getTo(), 
                c.getAndamentoProcesso().getId(), c.getDescricao(), AtivoEnum.ATIVO);
    }
    
    @Override
    public Long getCountStatusPorAndamentoPA(Object criteria) throws AppException {
        PAStatusCriteria c = (PAStatusCriteria) criteria;
        return new PAStatusRepositorio().getCountPAStatusPorAndamentoPA(em, c.getAndamentoProcesso().getId(), c.getDescricao(), AtivoEnum.ATIVO);
    }
    
    @Override
    public List getFluxoPorFaseProcesso(Object criteria) throws AppException {
        PAFaseProcessoAdmCriteria c = (PAFaseProcessoAdmCriteria) criteria;
        return new PAFaseProcessoAdmRepositorio().getFluxoPorFaseProcesso(em, c.getFrom(), c.getTo(), 
                c.getFluxoProcesso().getId(), c.getDescricao(), AtivoEnum.ATIVO);
    }
    
    @Override
    public Long getCountFluxoPorFaseProcesso(Object criteria) throws AppException {
        PAFaseProcessoAdmCriteria c = (PAFaseProcessoAdmCriteria) criteria;
        return new PAFaseProcessoAdmRepositorio().getCountFluxoPorFaseProcesso(
                em, c.getFluxoProcesso().getId(), c.getDescricao(), AtivoEnum.ATIVO);
    }
    
    @Override
    public Object getUltimaPrioridadeDoFluxoDaFaseProcesso(Long faseProcessoAdmId) throws DatabaseException {
        PAPrioridadeFluxoAmparoRepositorio repositorio = new PAPrioridadeFluxoAmparoRepositorio();
        return repositorio.getUltimaPrioridadeDoFluxoDaFaseProcesso(em, faseProcessoAdmId);
    }
    
    @Override
    public Integer recuperarUltimoValorPrioridadePorPerfil(Long idPerfil, Object entity) throws DatabaseException{
        Integer ordem=null;
        
        if(entity instanceof PAPrioridadeFluxoAmparo){
            ordem = new PAPrioridadeFluxoAmparoRepositorio().recuperarUltimoValorPrioridadePAPrioridadeFluxoAmparo(em, idPerfil);
        }
        if(entity instanceof PAFluxoFase){
            ordem = new PAFluxoFaseRepositorio().recuperarUltimoValorPrioridadePAFluxoFase(em, idPerfil);
        }
        return ordem;
    }

    @Override
    public void salvarPrioridadeFluxoAmparo(Object entity) throws DatabaseException, AppException {
        PAPrioridadeFluxoAmparoWrapper wrapper = (PAPrioridadeFluxoAmparoWrapper) entity;
        new PAPrioridadeFluxoAmparoBO().salvarPAPrioridadeFluxoAmparo(em, wrapper);
    }
    
    @Override
    public void salvarFluxoFase(Object entity) throws DatabaseException, AppException {
        PAFluxoFaseWrapper wrapper = (PAFluxoFaseWrapper) entity;
        new PAFluxoFaseBO().salvarPAFluxoFase(em, wrapper);
    }
    
    @Override
    public PAPrioridadeFluxoAmparo getPAPrioridadeFluxoAmparoAtivoPorPAFaseProcessoAdm(Long id) throws DatabaseException{
        return (PAPrioridadeFluxoAmparo) new PAPrioridadeFluxoAmparoRepositorio().getPAPrioridadeFluxoAmparoAtivoPorPAFaseProcessoAdm(em, id);
    }
    
    @Override
    public PAPrioridadeFluxoAmparo getPAPrioridadeFluxoAmparoPorId(Long id) throws DatabaseException{
        return new PAPrioridadeFluxoAmparoRepositorio().getPAPrioridadeFluxoAmparoPorId(em, id);
    }
    
    @Override
    public PAFluxoFase getPAFluxoFasePorId(Long id) throws DatabaseException{
        return new PAFluxoFaseRepositorio().getPAFluxoFasePorId(em, id);
    }
    
    @Override
    public void trocarPrioridadesPAPrioridadeFluxoAmparo(Object entity, Integer novaOrdem) throws DatabaseException{
        PAPrioridadeFluxoAmparo entidade = (PAPrioridadeFluxoAmparo) entity;
        new PAPrioridadeFluxoAmparoBO().trocarPrioridades(em, entidade, novaOrdem);
    }
    
    @Override
    public void trocarPrioridadesFluxoFase(Object entity, Integer novaOrdem) throws DatabaseException{
        PAFluxoFase entidade = (PAFluxoFase) entity;
        new PAFluxoFaseBO().trocarPrioridades(em, entidade, novaOrdem);
    }
    
    //Este método remove por perfil do objeto e altera as prioridade necessárias em tabelas que possuem prioridade.
    @Override
    public void removerPorPerfil(Object entity) throws DatabaseException{
        
        if(entity instanceof PAPrioridadeFluxoAmparo){
            PAPrioridadeFluxoAmparo entidade = (PAPrioridadeFluxoAmparo)entity;
        new PAPrioridadeFluxoAmparoBO().removerPAPrioridadeFluxoAmparo(em, entidade);
        }
        
        if(entity instanceof PAFluxoFase){
            PAFluxoFase entidade = (PAFluxoFase) entity;
        new PAFluxoFaseBO().removerFluxoFase(em, entidade);
        }
    }
    
    @Override
    public List getAndamentoPorDescricao(Object criteria) throws DatabaseException {
        PAAndamentoProcessoCriteria c = (PAAndamentoProcessoCriteria) criteria;
        return new PAAndamentoProcessoRepositorio().getAndamentoPorDescricao(em, c.getDescricao(), c.getFrom(), c.getTo());
    }
    
    @Override
    public Long getCountAndamentoPorDescricao(Object criteria) throws DatabaseException {
        PAAndamentoProcessoCriteria c = (PAAndamentoProcessoCriteria) criteria;
        return new PAAndamentoProcessoRepositorio().getCountAndamentoPorDescricao(em, c.getDescricao());
    }
    
    @Override
    public List getPAFluxoProcessoPorFiltros(Object criteria) throws DatabaseException {
        PAFluxoProcessoCriteria c = (PAFluxoProcessoCriteria) criteria;
        return new PAFluxoProcessoRepositorio().getPAFluxoProcessoPorFiltros(em, c);
    }
    
    @Override
    public Long getCountPAFluxoProcessoPorFiltros(Object criteria) throws DatabaseException {
        PAFluxoProcessoCriteria c = (PAFluxoProcessoCriteria) criteria;
        return new PAFluxoProcessoRepositorio().getCountPAFluxoProcessoPorFiltros(em, c);
    }
}