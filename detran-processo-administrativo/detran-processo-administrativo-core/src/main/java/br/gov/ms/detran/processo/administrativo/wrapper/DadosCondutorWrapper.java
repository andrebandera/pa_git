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
public class DadosCondutorWrapper implements Serializable {

    private Long id;

    public DadosCondutorWrapper(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}