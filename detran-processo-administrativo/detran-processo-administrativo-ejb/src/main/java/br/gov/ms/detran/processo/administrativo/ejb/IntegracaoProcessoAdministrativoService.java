package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.bca.IBeanIntegracao;
import br.gov.ms.detran.comum.iface.servico.IIntegracaoProcessoAdministrativoService;
import br.gov.ms.detran.comum.projeto.service.DetranAbstractGenericService;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.bo.PAServicoBO;
import br.gov.ms.detran.processo.administrativo.core.bo.PAServicoExternoBO;
import br.gov.ms.detran.processo.administrativo.core.bo.ResultadoProvaPABO;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEBNH011BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.integracao.AEMNPP06BO;
import br.gov.ms.detran.processo.administrativo.integracao.AEMNPP07BO;
import br.gov.ms.detran.processo.administrativo.integracao.AEMNPP20BO;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Lillydi
 * @param <T>
 */
@Stateless(mappedName = "ejb/IntegracaoProcessoAdministrativoService")
@Remote(IIntegracaoProcessoAdministrativoService.class)
public class IntegracaoProcessoAdministrativoService<T extends IBaseEntity> extends DetranAbstractGenericService implements IIntegracaoProcessoAdministrativoService {

    IPAControleFalhaService paControleFalha;

    private static final Logger LOGGER = Logger.getLogger(IntegracaoProcessoAdministrativoService.class);

    @Override
    @PersistenceContext(unitName = "DETRAN-PROCESSO-ADMINISTRATIVO-PU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @PostConstruct
    public void init() {
        try {
            paControleFalha = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
        } catch (RuntimeException e) {
            LOGGER.error("Erro localizar o serviço ejb/PAControleFalhaService", e);
        }
    }

    @Override
    public Object processarAEMNPP06(Object wrapper) throws AppException {
        return new AEMNPP06BO(em).processar(wrapper);
    }

    @Override
    public Object executaAEMNPP07(Object wrapper) throws AppException {
        return new AEMNPP07BO(em).executa(wrapper);
    }
    
    @Override
    public void gravarResultadoProvaPA(Long idResultadoLaudoSituacao, Long numeroDetran, Date dataExame) {
        new ResultadoProvaPABO().gravar(em, idResultadoLaudoSituacao, numeroDetran, dataExame);
    }

    @Override
    public boolean existeProcessoAdministrativoAtivoParaExaminador(Long numeroDetran) {

        Boolean existe = Boolean.FALSE;

        try {

            existe 
                = !DetranCollectionUtil.ehNuloOuVazio(
                    new ProcessoAdministrativoRepositorio()
                        .getListProcessoAdministrativosAtivosPorNumeroDetranParaExaminador(em, numeroDetran)
                );

        } catch (DatabaseException ex) {
            if (paControleFalha != null) {
                paControleFalha.gravarFalha(ex, "Numero Detran: "+ numeroDetran.toString() + ". Erro ao validar se existe PA ativo para examinador");
            }
        }

        return existe;
    }

    @Override
    public void gravaPAServico(String objetoEntrada, String servico, Boolean resultado) {
        new PAServicoBO().gravar(em, objetoEntrada, servico, resultado);
    }
    
    @Override
    public Object executaAEMNPP20(Object wrapper) throws AppException {
        return new AEMNPP20BO(em).executa(wrapper);
    }

    @Override
    public boolean validarServicoExternoAtivo(String URL_SERVICO) throws AppException {
        return new PAServicoExternoBO().validarServicoExternoAtivo(em, URL_SERVICO);
    }
    
    @Override
    public Boolean existeProcessoAdministrativoPorNumeroAutoInfracaoECodigoInfracaoECpfEAutuador(
        String numeroAutoInfracao, String codigoInfracao, String cpfCnpj, Long idOrgaoAutuador) throws AppException {
    
        List existeProcesso  
            = new ProcessoAdministrativoRepositorio()
                .getListPAsAtivosPorNumAutoECodigoInfracaoECpfEAutuador(
                    em, 
                    numeroAutoInfracao, 
                    codigoInfracao, 
                    cpfCnpj, 
                    idOrgaoAutuador
                );
        
        return !DetranCollectionUtil.ehNuloOuVazio(existeProcesso);
    }

    @Override
    public IBeanIntegracao buscarBloqueiosCondutor(String cpf) throws AppException {
        return new AEBNH011BO().executarIntegracaoAEBNH011(cpf);
    }
    
    @Override
    public boolean existeProcessoCassacaoPorCPF(String cpf) throws AppException{
        List processos = new ProcessoAdministrativoRepositorio().getProcessosCassacaoPorCPF(em, cpf);
        
        return !DetranCollectionUtil.ehNuloOuVazio(processos);
    }
    
    @Override
    public boolean existeProcessoSuspensaoComBloqueioPorCPF(String cpf)throws AppException{
        
        List processos = new ProcessoAdministrativoRepositorio().getProcessosSuspensaoComBloqueioPorCPF(em, cpf);
        
        return !DetranCollectionUtil.ehNuloOuVazio(processos);
    }
}