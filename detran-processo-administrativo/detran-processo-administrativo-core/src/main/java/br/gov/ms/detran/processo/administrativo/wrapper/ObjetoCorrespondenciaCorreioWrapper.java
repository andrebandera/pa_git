/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper;

/**
 *
 * @author desenvolvimento
 */
public class ObjetoCorrespondenciaCorreioWrapper {
    
    private Integer tipo;
    
    private Long setorTipo = 0L;
    
    private String identificador;

    private Long objetoSequencia;
    
    private String mensagem;

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Long getSetorTipo() {
        return setorTipo;
    }

    public void setSetorTipo(Long setorTipo) {
        this.setorTipo = setorTipo;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public Long getObjetoSequencia() {
        return objetoSequencia;
    }

    public void setObjetoSequencia(Long objetoSequencia) {
        this.objetoSequencia = objetoSequencia;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}