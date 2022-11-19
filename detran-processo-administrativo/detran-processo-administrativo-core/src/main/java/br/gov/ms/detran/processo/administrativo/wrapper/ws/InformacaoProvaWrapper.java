package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class InformacaoProvaWrapper {
    
    @XmlElement(name = "numeroprocessoadministrativo", nillable = true)
    private String numeroProcessoAdministrativo;
    
    @XmlElement(name = "resultado", nillable = true)
    private String resultado;
    
    @XmlElement(name = "nota", nillable = true)
    private BigDecimal nota;
    
    @XmlElement(name = "data", nillable = true)
    private Date data;
    
    @XmlElement(name = "postoatendimento", nillable = true)
    private String postoAtendimento;

    public InformacaoProvaWrapper() {
        
    }

    public InformacaoProvaWrapper(
        String numeroProcessoAdministrativo, String resultado, BigDecimal nota, Date data, String postoAtendimento) {
        
        this.numeroProcessoAdministrativo = numeroProcessoAdministrativo;
        this.resultado  = resultado;
        this.nota       = nota;
        this.data       = data;
        this.postoAtendimento = postoAtendimento;
    }
    
    public String getNumeroProcessoAdministrativo() {
        return numeroProcessoAdministrativo;
    }

    public void setNumeroProcessoAdministrativo(String numeroProcessoAdministrativo) {
        this.numeroProcessoAdministrativo = numeroProcessoAdministrativo;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }

    public Date getData() {
        return data;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setData(Date data) {
        this.data = data;
    }

    public String getPostoAtendimento() {
        return postoAtendimento;
    }

    public void setPostoAtendimento(String postoAtendimento) {
        this.postoAtendimento = postoAtendimento;
    }
}