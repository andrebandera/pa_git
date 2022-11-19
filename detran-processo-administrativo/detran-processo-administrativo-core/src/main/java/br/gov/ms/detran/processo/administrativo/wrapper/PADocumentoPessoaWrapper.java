/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import java.io.Serializable;

/**
 *
 * @author Christiano Carrilho.
 */
public class PADocumentoPessoaWrapper implements Serializable {

    /**
     * Xan_ID.
     */
    private Long id;

    /**
     * Xan_Numero_Detran.
     */
    private Long numeroDetran;

    /**
     * Xan_Numero_Documento.
     */
    private String numeroDocumento;

    /**
     * Xbt_Codigo.
     */
    private Integer codigoDocumento;

    /**
     * Xbq_Nome.
     */
    private String nomePrincipalPessoa;

    public PADocumentoPessoaWrapper() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumeroDetran() {
        return numeroDetran;
    }

    public void setNumeroDetran(Long numeroDetran) {
        this.numeroDetran = numeroDetran;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Integer getCodigoDocumento() {
        return codigoDocumento;
    }

    public void setCodigoDocumento(Integer codigoDocumento) {
        this.codigoDocumento = codigoDocumento;
    }

    public String getNomePrincipalPessoa() {
        return nomePrincipalPessoa;
    }

    public void setNomePrincipalPessoa(String nomePrincipalPessoa) {
        this.nomePrincipalPessoa = nomePrincipalPessoa;
    }
}