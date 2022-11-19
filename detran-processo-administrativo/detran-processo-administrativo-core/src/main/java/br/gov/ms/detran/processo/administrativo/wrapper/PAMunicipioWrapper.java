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
public class PAMunicipioWrapper implements Serializable {

    private Long id;
    private Integer codigo;
    private String nome;

    public PAMunicipioWrapper() {
    }

    public PAMunicipioWrapper(Long id, Integer codigo, String nome) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}