package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author yanko.campos
 */
public class DadosPABPMSHistoricoPontuacaoNomeWrapper {
    
    @XmlElement(name = "tiporegistro", nillable = true)
    private final Integer tipoRegistro = TipoRegistroBPMSEnum.HISTORICO_PONTUACAO_NOME.getCodigo();
    
    @XmlElement(name = "pgucondutor", nillable = true)
    private String pguCondutor;
    
    @XmlElement(name = "registrocondutor", nillable = true)
    private String registroCondutor;
    
    @XmlElement(name = "nome", nillable = true)
    private String nome;
    
    @XmlElement(name = "numeroautoinfracao", nillable = true)
    private String numeroAutoInfracao;
    
    @XmlElement(name = "codigoinfracao", nillable = true)
    private String codigoInfracao;
    
    @XmlElement(name = "dataoinfracao", nillable = true)
    private Date dataInfracao;
    
    @XmlElement(name = "numeropontosinfracao", nillable = true)
    private Integer numeroPontosInfracao;
    
    @XmlElement(name = "datavencimento", nillable = true)
    private Date dataVencimento;
    
    @XmlElement(name = "artigoinciso", nillable = true)
    private String artigoInciso;
    
    @XmlElement(name = "origeminformacao", nillable = true)
    private String origemInformacao;
    
    @XmlElement(name = "descricaosituacao", nillable = true)
    private String descricaoSituacao;

    public DadosPABPMSHistoricoPontuacaoNomeWrapper() {
    }

    public DadosPABPMSHistoricoPontuacaoNomeWrapper(String pguCondutor, 
                                                   String registroCondutor, 
                                                   String nome, 
                                                   String numeroAutoInfracao, 
                                                   String codigoInfracao, 
                                                   Date dataInfracao, 
                                                   Integer numeroPontosInfracao, 
                                                   Date dataVencimento, 
                                                   String artigoInciso, 
                                                   String origemInformacao, 
                                                   String descricaoSituacao) {
        this.pguCondutor = pguCondutor;
        this.registroCondutor = registroCondutor;
        this.nome = nome;
        this.numeroAutoInfracao = numeroAutoInfracao;
        this.codigoInfracao = codigoInfracao;
        this.dataInfracao = dataInfracao;
        this.numeroPontosInfracao = numeroPontosInfracao;
        this.dataVencimento = dataVencimento;
        this.artigoInciso = artigoInciso;
        this.origemInformacao = origemInformacao;
        this.descricaoSituacao = descricaoSituacao;
    }

    public String getPguCondutor() {
        return pguCondutor;
    }

    public void setPguCondutor(String pguCondutor) {
        this.pguCondutor = pguCondutor;
    }

    public String getRegistroCondutor() {
        return registroCondutor;
    }

    public void setRegistroCondutor(String registroCondutor) {
        this.registroCondutor = registroCondutor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumeroAutoInfracao() {
        return numeroAutoInfracao;
    }

    public void setNumeroAutoInfracao(String numeroAutoInfracao) {
        this.numeroAutoInfracao = numeroAutoInfracao;
    }

    public String getCodigoInfracao() {
        return codigoInfracao;
    }

    public void setCodigoInfracao(String codigoInfracao) {
        this.codigoInfracao = codigoInfracao;
    }

    public Date getDataInfracao() {
        return dataInfracao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataInfracao(Date dataInfracao) {
        this.dataInfracao = dataInfracao;
    }

    public Integer getNumeroPontosInfracao() {
        return numeroPontosInfracao;
    }

    public void setNumeroPontosInfracao(Integer numeroPontosInfracao) {
        this.numeroPontosInfracao = numeroPontosInfracao;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getArtigoInciso() {
        return artigoInciso;
    }

    public void setArtigoInciso(String artigoInciso) {
        this.artigoInciso = artigoInciso;
    }

    public String getOrigemInformacao() {
        return origemInformacao;
    }

    public void setOrigemInformacao(String origemInformacao) {
        this.origemInformacao = origemInformacao;
    }

    public String getDescricaoSituacao() {
        return descricaoSituacao;
    }

    public void setDescricaoSituacao(String descricaoSituacao) {
        this.descricaoSituacao = descricaoSituacao;
    }

    public Integer getTipoRegistro() {
        return tipoRegistro;
    }

    public static void main(String ...args) {
        
        DadosPABPMSHistoricoPontuacaoNomeWrapper wrapper = new DadosPABPMSHistoricoPontuacaoNomeWrapper();
        
        wrapper.setArtigoInciso("ARTIGO");
        wrapper.setCodigoInfracao("00");
        wrapper.setDataInfracao(new Date());
        wrapper.setDataVencimento(new Date());
        wrapper.setDescricaoSituacao("DESCRICAO_SITUACAO");
        wrapper.setNome("NOME");
        wrapper.setNumeroAutoInfracao("000001");
        wrapper.setNumeroPontosInfracao(4);
        wrapper.setOrigemInformacao("Origem Informação");
        wrapper.setPguCondutor("000012");
        wrapper.setRegistroCondutor("REGISTRO_CONDUTOR");
        
        System.out.println(DetranStringUtil.getInstance().toJson(wrapper));
    }
}