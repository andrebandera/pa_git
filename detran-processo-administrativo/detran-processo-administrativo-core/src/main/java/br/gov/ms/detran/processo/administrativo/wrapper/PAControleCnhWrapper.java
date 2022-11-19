package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.OrigemEnum;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class PAControleCnhWrapper {
    
    private static final long serialVersionUID = 3704443849309185433L;
    
    private Long processoAdministrativoId;

    private ProcessoAdministrativo processo;
    
    private String portariaPenalidade;
    
    private String tempoPenalidade;
    
    private BooleanEnum prazoIndeterminado;
    
    public PAControleCnhWrapper() {
    }

    public PAControleCnhWrapper(ProcessoAdministrativo processo, String portariaPenalidade, String tempoPenalidade) {
        this.processo = processo;
        this.portariaPenalidade = portariaPenalidade;
        this.tempoPenalidade = tempoPenalidade;
    }
    
    public PAControleCnhWrapper(ProcessoAdministrativo processo, String portariaPenalidade, String tempoPenalidade, BooleanEnum prazoInd) {
        this(processo, portariaPenalidade, tempoPenalidade);
        this.prazoIndeterminado = prazoInd;
    }

    public ProcessoAdministrativo getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoAdministrativo processo) {
        this.processo = processo;
    }

    public String getPortariaPenalidade() {
        return portariaPenalidade;
    }

    public void setPortariaPenalidade(String portariaPenalidade) {
        this.portariaPenalidade = portariaPenalidade;
    }

    public String getTempoPenalidade() {
        return tempoPenalidade;
    }

    public void setTempoPenalidade(String tempoPenalidade) {
        this.tempoPenalidade = tempoPenalidade;
    }
    
    @XmlElement(name = "numeroPortariaMascarado")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public String getNumeroPortariaMascarado() {
        return portariaPenalidade;
    }

    @XmlElement(name = "numeroPortariaMascarado")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public void setNumeroPortariaMascarado(String numeroProcesso) {
    }

    public Long getProcessoAdministrativoId() {
        processoAdministrativoId = null;
        
        if (null != this.processo && null != this.processo.getId())
            return this.processo.getId();
        
        return processoAdministrativoId;
    }

    public void setProcessoAdministrativoId(Long processoAdministrativoId) {
        this.processoAdministrativoId = processoAdministrativoId;
    }
    
    public String getTempoPenalidadeLabel(){
        if(tempoPenalidade == null)
            return "";
        if(processo == null || processo.getOrigem() == null)
            return tempoPenalidade;
        
        if(OrigemEnum.JURIDICA.equals(processo.getOrigem())){
            return BooleanEnum.SIM.equals(this.prazoIndeterminado) ? "INDETERMINADO" : tempoPenalidade.concat(" Dia(s)");
        }
        
        return tempoPenalidade.concat(" Mes(es)");
    }

    public BooleanEnum getPrazoIndeterminado() {
        return prazoIndeterminado;
    }

    public void setPrazoIndeterminado(BooleanEnum prazoIndeterminado) {
        this.prazoIndeterminado = prazoIndeterminado;
    }
}