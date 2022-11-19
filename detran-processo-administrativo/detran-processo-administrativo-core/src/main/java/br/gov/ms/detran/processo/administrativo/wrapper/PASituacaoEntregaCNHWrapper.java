/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Christiano Carrilho.
 */
public class PASituacaoEntregaCNHWrapper implements Serializable {

    /**
     * Bac_ID.
     */
    private Long id;

    /**
     * BAC.Bac_Acao.
     */
    private Integer acao;

    /**
     * Bac_Data.
     */
    private Date data;

    /**
     * Bac_CA_Usuario.
     */
    private Long usuarioId;

    /**
     * Bab_ID.
     */
    private Long controleId;

    /**
     * Bab_Numero_CNH.
     */
    private Long numeroCNH;

    public PASituacaoEntregaCNHWrapper() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAcao() {
        return acao;
    }

    public void setAcao(Integer acao) {
        this.acao = acao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getControleId() {
        return controleId;
    }

    public void setControleId(Long controleId) {
        this.controleId = controleId;
    }

    public Long getNumeroCNH() {
        return numeroCNH;
    }

    public void setNumeroCNH(Long numeroCNH) {
        this.numeroCNH = numeroCNH;
    }

}