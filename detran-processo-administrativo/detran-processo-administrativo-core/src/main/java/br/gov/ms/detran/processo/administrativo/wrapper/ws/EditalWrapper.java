package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class EditalWrapper {
    
    private ProcessoAdministrativo processoAdministrativo;
    
    private String numeroProcesso;
    
    private TipoFasePaEnum tipo;
    
    private String numeroPortaria;
    
    private Date dataPublicacaoEdital;
    
    private MovimentoCnh movimento;
    
    private String usuario;

    public EditalWrapper() {
    }

    public EditalWrapper(String numeroProcesso, 
                         TipoFasePaEnum tipo, 
                         String numeroPortaria, 
                         Date dataPublicacaoEdital, 
                         MovimentoCnh movimento, 
                         String usuario) {
        this.numeroProcesso = numeroProcesso;
        this.tipo = tipo;
        this.numeroPortaria = numeroPortaria;
        this.dataPublicacaoEdital = dataPublicacaoEdital;
        this.movimento = movimento;
        this.usuario = usuario;
    }

    
    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public TipoFasePaEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoFasePaEnum tipo) {
        this.tipo = tipo;
    }

    public String getNumeroPortaria() {
        return numeroPortaria;
    }

    public void setNumeroPortaria(String numeroPortaria) {
        this.numeroPortaria = numeroPortaria;
    }

    public Date getDataPublicacaoEdital() {
        return dataPublicacaoEdital;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataPublicacaoEdital(Date dataPublicacaoEdital) {
        this.dataPublicacaoEdital = dataPublicacaoEdital;
    }

    public MovimentoCnh getMovimento() {
        return movimento;
    }

    public void setMovimento(MovimentoCnh movimento) {
        this.movimento = movimento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }
    
}