/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author desenvolvimento
 */
public class DadosPABPMSGeralWrapper {
    
    @XmlElement(name = "tiporegistro")
    private Integer tipoRegistro = TipoRegistroBPMSEnum.DADOS_GERAL.getCodigo();
    
    @XmlElement(name = "numeroprocesso", nillable = true)
    private String numeroProcesso;
    
    @XmlElement(name = "numeroportaria", nillable = true)
    private String numeroPortaria;
    
    @XmlElement(name = "descricaotipoprocesso", nillable = true)
    private String descricaoTipoProcesso;
    
    @XmlElement(name = "cpfcondutor", nillable = true)
    private String cpfCondutor;

    public Integer getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(Integer tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroPortaria() {
        return numeroPortaria;
    }

    public void setNumeroPortaria(String numeroPortaria) {
        this.numeroPortaria = numeroPortaria;
    }

    public String getDescricaoTipoProcesso() {
        return descricaoTipoProcesso;
    }

    public void setDescricaoTipoProcesso(String descricaoTipoProcesso) {
        this.descricaoTipoProcesso = descricaoTipoProcesso;
    }

    public String getCpfCondutor() {
        return cpfCondutor;
    }

    public void setCpfCondutor(String cpfCondutor) {
        this.cpfCondutor = cpfCondutor;
    }
    
    public static void main(String ...args) {
        
        DadosPABPMSGeralWrapper cabecalho = new DadosPABPMSGeralWrapper();
        
        cabecalho.setCpfCondutor("XXX-XXX-XXX-XX");
        cabecalho.setDescricaoTipoProcesso("AAAAAAAAAA");
        cabecalho.setNumeroPortaria("123456");
        cabecalho.setNumeroProcesso("123456");
        cabecalho.setTipoRegistro(TipoRegistroBPMSEnum.DADOS_GERAL.getCodigo());
        
        System.out.println(DetranStringUtil.getInstance().toJson(cabecalho));
    }
}