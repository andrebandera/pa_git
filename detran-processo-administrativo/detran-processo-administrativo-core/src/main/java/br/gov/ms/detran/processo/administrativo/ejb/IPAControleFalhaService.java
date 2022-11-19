package br.gov.ms.detran.processo.administrativo.ejb;

/**
 * @author Christiano Carrilho.
 */
public interface IPAControleFalhaService {

    /**
     * @param e
     * @param origemErro
     * @param cpf 
     */
    public void gravarFalhaCondutor(Exception e, String origemErro, String cpf);

    /**
     * @param e 
     * @param origemErro 
     */
    public void gravarFalha(Exception e, String origemErro);

    /**
     * @param cpf
     * @param mensagem
     * @param origem 
     */
    public void gravarFalhaEspecifica(String cpf, String mensagem, String origem);
    
    /**
     * @param e
     * @param origemErro
     * @param cpf
     * @param numeroProcesso 
     */
    public void gravarFalhaProcessoAdministrativo(Exception e, String origemErro, String cpf, String numeroProcesso);
}