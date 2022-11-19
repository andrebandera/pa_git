/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.pa.instauracao;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.projeto.service.DetranAbstractGenericService;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import static br.gov.ms.detran.comum.util.DetranCollectionUtil.ehNuloOuVazio;
import static br.gov.ms.detran.comum.util.DetranSpringSecurityUtil.SPRING_SECURITY_AUTHENTICATION_KEY;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.jms.DtnJms;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.criteria.DadosCondutorPADCriteria;
import br.gov.ms.detran.processo.administrativo.ejb.IInstauracaoService;
import br.gov.ms.detran.processo.administrativo.entidade.ExecucaoInstauracao;
import static br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoConstante.INSTAURACAO.TAMANHO_BLOCO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.entidade.SituacaoExecucaoInstauracaoEnum.INSTAURANDO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.DadosCondutorPADRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PASegurancaoRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IExecucaoInstauracaoService;
import br.gov.ms.detran.processo.administrativo.ejb.IPAControleFalhaService;
import br.gov.ms.detran.processo.administrativo.wrapper.ApoioOrigemInstauracaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.DadosCondutorWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.InstauracaoBlocoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.InstauracaoBlocoWrapper2;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import static br.gov.ms.detran.comum.util.DetranSpringSecurityUtil.serializeAuthentication;

/**
 *
 * @author Christiano Carrilho.
 */
@Stateless(mappedName = "ejb/InstauracaoService")
@Remote(IInstauracaoService.class)
public class InstauracaoService2 extends DetranAbstractGenericService implements IInstauracaoService {

    @Resource(mappedName = "jms/instaurarprocessoadministrativoQueue")
    private Queue paJmsQueue;

    @Resource(mappedName = "jms/instaurarprocessoadministrativoConnectionFactory")
    private ConnectionFactory paJmsConnectionFactory;

    IPAControleFalhaService falhaService;
    IExecucaoInstauracaoService execucaoService;

    private static final Logger LOG = Logger.getLogger(InstauracaoService2.class);

    @PostConstruct
    public void init() {
        falhaService = ServiceJndiLocator.<IPAControleFalhaService>lookup("ejb/PAControleFalhaService");
        execucaoService = ServiceJndiLocator.<IExecucaoInstauracaoService>lookup("ejb/ExecucaoInstauracaoService");
    }

    @Override
    @PersistenceContext(unitName = "DETRAN-PA-PU")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void instaurar(ExecucaoInstauracao execucaoOrigem) {

        Integer INICIO_BLOCO = 0;

        DtnJms jms = null;

        try {

            if (execucaoOrigem == null) {
                throw new AppException("Execução Instauração inválida.");
            }

            iniciarExecucaoInstauracaoOrigem(execucaoOrigem.getId());
            jms = iniciarServicoFilaInstauracao();

            DadosCondutorPADCriteria criteria = new DadosCondutorPADCriteria(
                    INICIO_BLOCO, TAMANHO_BLOCO_INSTAURACAO
            );

            InstauracaoBlocoWrapper2 blocoWrapper;

            LOG.info("Enviando para processamento a execução {0}", execucaoOrigem.getId());

            do {

                LOG.info("Envio para instaurar. Indice: {0} - Bloco de tamanho: {1}.", INICIO_BLOCO, TAMANHO_BLOCO_INSTAURACAO);

                criteria.setFrom(INICIO_BLOCO);
                blocoWrapper = montarBlocoExecucaoCondutores(criteria);

                if (!ehNuloOuVazio(blocoWrapper.getCondutores())) {

                    Long execucaoBlocoId = criarExecucaoDoBloco(execucaoOrigem.getId());

                    if (execucaoBlocoId != null) {
                        blocoWrapper.setExecucaoId(execucaoBlocoId);
                    }

                    jms.sendMessage(blocoWrapper, "SP_TDA");
                    INICIO_BLOCO += TAMANHO_BLOCO_INSTAURACAO;
                }

            } while (!ehNuloOuVazio(blocoWrapper.getCondutores())
                    && blocoWrapper.getCondutores().size() == TAMANHO_BLOCO_INSTAURACAO);

            LOG.info("Finalizado o envio do processamento a execução {0}", execucaoOrigem.getId());

            finalizarExecucaoInstauracaoOrigem(execucaoOrigem.getId());

        } catch (AppException ex) {
            LOG.error("Sem tratamento.", ex);
            falhaService.gravarFalha(ex, "Erro ao instaurar");
        } catch (Exception ex) {
            LOG.error("Sem tratamento.", ex);
            falhaService.gravarFalha(ex, "Erro ao instaurar");
        } finally {
            finalizarServicoFilaInstauracao(jms);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IBaseEntity gravar(IBaseEntity entidade) throws DatabaseException {
        return super.gravar(entidade);
    }

    /**
     * Gravar as informações de execução da instauração que originou o processo.
     * 
     * @param execucaoInstauracaoOrigem Informações de quem originou a execução da instauração.
     * @return Informações da execução de origem gravada.
     * @throws AppException 
     */
    void iniciarExecucaoInstauracaoOrigem(Long execucaoOrigemId) throws AppException {
        execucaoService.atualizarExecucao(execucaoOrigemId, INSTAURANDO);
    }

    /**
     * 
     * @param execucaoInstauracaoOrigem
     * @throws DatabaseException 
     */
    void finalizarExecucaoInstauracaoOrigem(Long execucaoOrigemId) throws AppException {
        execucaoService.finalizarExecucaoOrigem(execucaoOrigemId);
    }

    /**
     * Recupera os dados que serão processados na instauração do PA conforme o
     * tamanho do bloco de execução configurado.
     * 
     * @param criteria Informações dos critérios de busca dos dados que serão processados.
     * @param origensWrapper Lista dos cenários.
     * @return Instância das informações do bloco que será processado.
     * @throws AppException 
     */
    InstauracaoBlocoWrapper recuperarBlocoExecucaoInstauracao(
            DadosCondutorPADCriteria criteria,
            List<ApoioOrigemInstauracaoWrapper> origensWrapper) throws AppException {

        return new InstauracaoBlocoWrapper(
                new DadosCondutorPADRepositorio().getCondutoresParaPA(em, criteria),
                origensWrapper
        );
    }

    InstauracaoBlocoWrapper2 montarBlocoExecucaoCondutores(DadosCondutorPADCriteria criteria) throws AppException {

        List<DadosCondutorWrapper> condutores = 
                em.createNamedQuery("DadosCondutorPAD.findAllId")
                        .setFirstResult(criteria.getFrom())
                        .setMaxResults(criteria.getTo())
                        .getResultList();

        return new InstauracaoBlocoWrapper2(condutores);
    }

    /**
     * Grava as informações da execução do bloco recuperado associando-o à execução de origem.
     * 
     * @param execucaoOrigemId Informações de quem originou a execução da instauração.
     */
    Long criarExecucaoDoBloco(Long execucaoOrigemId) throws AppException {
        return execucaoService.criarExecucao(execucaoOrigemId);
    }

    /**
     * Abre conexão com o serviço de fila JMS que receberá a execução da instauração.
     * 
     * @return Instância da conexão da fila JMS.
     * @throws AppException 
     */
    DtnJms iniciarServicoFilaInstauracao() throws AppException {
        DtnJms jms = new DtnJms();
        jms.start(
                "jms/instaurarprocessoadministrativoConnectionFactory",
                "jms/instaurarprocessoadministrativoQueue"
        );
        return jms;
    }

    /**
     * 
     * @param jms 
     */
    void finalizarServicoFilaInstauracao(DtnJms jms) {
        try {
            if (jms != null) {
                jms.close();
            }
        } catch (AppException ex) {
            //LOG.error("Erro ao fechar conexão com JMS", ex);
        }
    }

    private Message createMessage(Session session, Serializable messageData, String property) throws JMSException {

        ObjectMessage om = session.createObjectMessage();
        om.setObject(messageData);
        om.setStringProperty(SPRING_SECURITY_AUTHENTICATION_KEY, serializeAuthentication(property));
        
        return om;
    }

    @Override
    public void validaToken(HttpServletRequest request) throws AppException {
        
        String token = request.getHeader("token");

        if(DetranStringUtil.ehBrancoOuNulo(token)) {
            DetranWebUtils.applicationMessageException("Inválido acesso.");
        }

        String principal = "SP_TDA_PAD";

        String hash = 
                new PASegurancaoRepositorio().getSessionByPrincipalAndHash(
                    em, principal, token
                );

        if (hash == null) {
            DetranWebUtils.applicationMessageException("Inválido acesso. Token valor: {0}", token);
        }

        new PASegurancaoRepositorio().removeSessionByPrincipalAndHash(
            em, principal, token
        );
    }
}