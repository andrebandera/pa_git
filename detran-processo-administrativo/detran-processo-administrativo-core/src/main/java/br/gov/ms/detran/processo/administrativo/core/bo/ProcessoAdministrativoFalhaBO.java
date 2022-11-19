package br.gov.ms.detran.processo.administrativo.core.bo;

import br.gov.ms.detran.comum.projeto.constantes.ParametrizacaoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranEmailUtil;
import br.gov.ms.detran.comum.util.DetranEmailWrapper;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.exception.ExceptionUtils;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAParametrizacaoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativoFalha;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * @author Lillydi
 */
public class ProcessoAdministrativoFalhaBO {

    private static final Logger LOG = Logger.getLogger(ProcessoAdministrativoFalhaBO.class);

    /**
     * Gravar falha com informaçao do cpf do condutor.
     * 
     * @param em
     * @param origemErro
     * @param cpf
     * @param e
     */
    public void gravarFalhaCondutor(EntityManager em, String origemErro, String cpf, Exception e) {
        
        try {
            
            ProcessoAdministrativoFalha processoAdministrativoFalha = gerarCausaDaFalha(origemErro, e);
            processoAdministrativoFalha.setCpf(cpf);
            new AbstractJpaDAORepository().insert(em, processoAdministrativoFalha);
            
        } catch (Exception ex) {
            LOG.error("Erro tratado ao executar gravarErro: ", ex);
        }
    }

    /**
     * Gravar falha e enviar e-mail.
     * 
     * @param em
     * @param origemErro
     * @param cpf
     * @param e
     * @param email 
     */
    public void gravarFalhaEEmail(EntityManager em, String origemErro, String cpf, Exception e, DetranEmailWrapper email) {

        try {
            String url = System.getProperty("DTN_URL_SERVER");
            if(DetranStringUtil.ehBrancoOuNulo(url)){
                url = "SERVIDOR NAO IDENTIFICADO";
            }
            email.setSubject(url.concat(" - ").concat(email.getSubject()));
            enviarEmail(em, email);
            ProcessoAdministrativoFalha processoAdministrativoFalha = gerarCausaDaFalha(origemErro, e);
            processoAdministrativoFalha.setCpf(cpf);
            new AbstractJpaDAORepository().insert(em, processoAdministrativoFalha);
            
        } catch (Exception ex) {
            LOG.error("Erro tratado ao executar gravarErro: ", ex);
        }
    }

    /**
     * Gravar falha apenas com informaçoes de origem do erro e a exception.
     * 
     * @param em
     * @param origemErro
     * @param e
     */
    public void gravarFalhaGenerica(EntityManager em, String origemErro, Exception e) {
        
        try {
            
            ProcessoAdministrativoFalha processoAdministrativoFalha = gerarCausaDaFalha(origemErro, e);
            new AbstractJpaDAORepository().insert(em, processoAdministrativoFalha);
            
        } catch (Exception ex) {
            LOG.error("Erro tratado ao executar gravarErro: ", ex);
        }
    }

    /**
     * Gravar falha com mensagem especifica.
     * 
     * @param em
     * @param cpf
     * @param mensagem
     * @param origem 
     */
    public void gravarFalhaCondutorEspecifica(EntityManager em, String cpf, String mensagem, String origem) {

        try {

            ProcessoAdministrativoFalha falha = new ProcessoAdministrativoFalha();
            falha.setCausa(origem);
            falha.setCpf(cpf);
            falha.setMensagem(DetranStringUtil.preencherEspaco(mensagem, 1000, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO));
            new AbstractJpaDAORepository().insert(em, falha);

        } catch (DatabaseException ex) {
            LOG.error("Erro tratado ao executar gravar falha específica", ex);
        }
    }

    /**
     * Montar a informação da causa da falha.
     * 
     * @param origemErro
     * @param e
     * @return
     */
    private ProcessoAdministrativoFalha gerarCausaDaFalha(String origemErro, Exception e) {
        
        ProcessoAdministrativoFalha processoAdministrativoFalha = new ProcessoAdministrativoFalha();
        
        StringBuilder causa = new StringBuilder("Causa impedimento.");
        causa.append(" - ");
        causa.append("Origem erro:");
        causa.append(origemErro);
        causa.append(": ");
        causa.append(e.getMessage());
        causa.append(" - ");
        causa.append("StackTrace:");
        causa.append(new ExceptionUtils().getStack(e));
        
        processoAdministrativoFalha
            .setCausa(
                DetranStringUtil.preencherEspaco(causa.toString(), 1000, DetranStringUtil.TipoDadoEnum.ALFA_NUMERICO)
            );
        
        return processoAdministrativoFalha;
    }

    private void enviarEmail(EntityManager em, DetranEmailWrapper email) throws AppException {
        
        if (email != null) {

            List<String> parametrosDestinatarios = new PAParametrizacaoRepositorio().getEmailDestinatarioPA(em, ParametrizacaoEnum.LISTA_EMAIL_PROCESSO_ADMINISTRATIVO.getCode());

            if (!DetranCollectionUtil.ehNuloOuVazio(parametrosDestinatarios)) {
                if (DetranStringUtil.ehBrancoOuNulo(email.getSubject())) {
                    email.setSubject("DETRAN-WEB: ProcessoAdministrativo");
                }
                if (DetranStringUtil.ehBrancoOuNulo(email.getBody())) {
                    email.setBody("NÃO FOI POSSÍVEL DEFINIR A MENSAGEM. FAVOR ENTRAR EM CONTATO COM A DIRTI.");
                }
                email.setContent("text/html; charset=\"UTF-8\"");
                email.setTo(parametrosDestinatarios);
                DetranEmailUtil.send(email);
            }
        }
    }

    /**
     * Gravar falha com informaçao do cpf do condutor.
     * 
     * @param em
     * @param origemErro
     * @param cpf
     * @param numeroProcesso
     * @param e
     */
    public void gravarFalhaProcessoAdministrativo(
        EntityManager em, String origemErro, String cpf, String numeroProcesso, Exception e) {
        
        try {
            
            ProcessoAdministrativoFalha processoAdministrativoFalha = gerarCausaDaFalha(origemErro, e);
            
            processoAdministrativoFalha.setNumeroProcessoAdministrativo(numeroProcesso);
            processoAdministrativoFalha.setCpf(cpf);
            
            new AbstractJpaDAORepository().insert(em, processoAdministrativoFalha);
            
        } catch (Exception ex) {
            LOG.error("Erro tratado ao executar gravarErro: ", ex);
        }
    }
}