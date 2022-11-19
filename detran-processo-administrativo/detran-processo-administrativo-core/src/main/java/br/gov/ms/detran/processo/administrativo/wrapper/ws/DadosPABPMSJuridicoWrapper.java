package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class DadosPABPMSJuridicoWrapper {

    @XmlElement(name = "valorautos", nillable = true)
    private String valorAutos;
    
    @XmlElement(name = "parte", nillable = true)
    private String parte;
    
    @XmlElement(name = "formulariorenach", nillable = true)
    private String formularioRenach;
    
    @XmlElement(name = "orgaojudicial", nillable = true)
    private String orgaoJudicial;
    
    @XmlElement(name = "tribunal", nillable = true)
    private String tribunal;
    
    @XmlElement(name = "indicativodelito", nillable = true)
    private String indicativoDelito;
    
    @XmlElement(name = "delitos", nillable = true)
    private List<DadosPABPMSJuridicoArtigoDelitoWrapper> delitos;
    
    @XmlElement(name = "tipomedida", nillable = true)
    private String tipoMedida;
    
    @XmlElement(name = "identificacaorecolhimentocnh", nillable = true)
    private String identificacaoRecolhimentoCnh;
    
    @XmlElement(name = "requisitocursobloqueio", nillable = true)
    private String requisitoCursoBloqueio;
    
    @XmlElement(name = "orgaobca", nillable = true)
    private String orgaoBca;
    
    @XmlElement(name = "codigoorgaobca", nillable = true)
    private String codigoOrgaoBca;
    
    @XmlElement(name = "usuario", nillable = true)
    private String usuario;
    
    @XmlElement(name = "datacadastro", nillable = true)
    private String dataCadastro;
    
    @XmlElement(name = "observacao", nillable = true)
    private String observacao;
    
    @XmlElement(name = "segredojustica", nillable = true)
    private String segredoJustica;
    
    public DadosPABPMSJuridicoWrapper() {
    }
    
    public String getValorAutos() {
        return valorAutos;
    }

    public void setValorAutos(String valorAutos) {
        this.valorAutos = valorAutos;
    }

    public String getParte() {
        return parte;
    }

    public void setParte(String parte) {
        this.parte = parte;
    }

    public String getFormularioRenach() {
        return formularioRenach;
    }

    public void setFormularioRenach(String formularioRenach) {
        this.formularioRenach = formularioRenach;
    }

    public String getOrgaoJudicial() {
        return orgaoJudicial;
    }

    public void setOrgaoJudicial(String orgaoJudicial) {
        this.orgaoJudicial = orgaoJudicial;
    }

    public String getTribunal() {
        return tribunal;
    }

    public void setTribunal(String tribunal) {
        this.tribunal = tribunal;
    }

    public String getIndicativoDelito() {
        return indicativoDelito;
    }

    public void setIndicativoDelito(String indicativoDelito) {
        this.indicativoDelito = indicativoDelito;
    }

    public List<DadosPABPMSJuridicoArtigoDelitoWrapper> getDelitos() {
        return delitos;
    }

    public void setDelitos(List<DadosPABPMSJuridicoArtigoDelitoWrapper> delitos) {
        this.delitos = delitos;
    }

    public String getTipoMedida() {
        return tipoMedida;
    }

    public void setTipoMedida(String tipoMedida) {
        this.tipoMedida = tipoMedida;
    }

    public String getIdentificacaoRecolhimentoCnh() {
        return identificacaoRecolhimentoCnh;
    }

    public void setIdentificacaoRecolhimentoCnh(String identificacaoRecolhimentoCnh) {
        this.identificacaoRecolhimentoCnh = identificacaoRecolhimentoCnh;
    }

    public String getRequisitoCursoBloqueio() {
        return requisitoCursoBloqueio;
    }

    public void setRequisitoCursoBloqueio(String requisitoCursoBloqueio) {
        this.requisitoCursoBloqueio = requisitoCursoBloqueio;
    }

    public String getOrgaoBca() {
        return orgaoBca;
    }

    public void setOrgaoBca(String orgaoBca) {
        this.orgaoBca = orgaoBca;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getCodigoOrgaoBca() {
        return codigoOrgaoBca;
    }

    public void setCodigoOrgaoBca(String codigoOrgaoBca) {
        this.codigoOrgaoBca = codigoOrgaoBca;
    }

    public String getSegredoJustica() {
        return segredoJustica;
    }

    public void setSegredoJustica(String segredoJustica) {
        this.segredoJustica = segredoJustica;
    }
}