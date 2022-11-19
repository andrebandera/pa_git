/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Lillydi
 */
public class DadosPABPMSInfracaoWrapper implements IProcessoAdministrativoInfracao{
    
    @XmlElement(name = "tiporegistro", nillable = true)
    private final Integer tipoRegistro = TipoRegistroBPMSEnum.DADOS_INFRACAO.getCodigo();
    
    @XmlElement(name = "numeroautoinfracao", nillable = true)
    private String numeroAutoInfracao;
    
    @XmlElement(name = "placa", nillable = true)
    private String placa;
    
    @XmlElement(name = "codigoinfracao", nillable = true)
    private String codigoInfracao;
    
    @XmlElement(name = "datainfracao", nillable = true)
    private Date dataInfracao;
    
    @XmlElement(name = "dataemissapenalidade", nillable = true)
    private Date dataEmissaoPenalidade;
    
    @XmlElement(name = "datavencimentopagamento", nillable = true)
    private Date dataVencimentoPagamento;
    
    @XmlElement(name = "localinfracao", nillable = true)
    private String localInfracao;
    
    @XmlElement(name = "codigomunicipioinfracao", nillable = true)
    private String codigoMunicipioInfracao;
    
    @XmlElement(name = "nomemunicipioinfracao", nillable = true)
    private String nomeMunicipioInfracao;
    
    @XmlElement(name = "codigoautuador", nillable = true)
    private String codigoAutuador;
    
    @XmlElement(name = "descricaoautuador", nillable = true)
    private String descricaoAutuador;
    
    @XmlElement(name = "qtdpontosinfracao", nillable = true)
    private Integer qtdPontosInfracao;
    
    @XmlElement(name = "descricaoinfracao", nillable = true)
    private String descricaoInfracao;

    public DadosPABPMSInfracaoWrapper() {
    }

    public Integer getTipoRegistro() {
        return tipoRegistro;
    }

    public String getNumeroAutoInfracao() {
        return numeroAutoInfracao;
    }

    public void setNumeroAutoInfracao(String numeroAutoInfracao) {
        this.numeroAutoInfracao = numeroAutoInfracao;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
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

    public Date getDataEmissaoPenalidade() {
        return dataEmissaoPenalidade;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataEmissaoPenalidade(Date dataEmissaoPenalidade) {
        this.dataEmissaoPenalidade = dataEmissaoPenalidade;
    }

    public Date getDataVencimentoPagamento() {
        return dataVencimentoPagamento;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataVencimentoPagamento(Date dataVencimentoPagamento) {
        this.dataVencimentoPagamento = dataVencimentoPagamento;
    }

    public String getLocalInfracao() {
        return localInfracao;
    }

    public void setLocalInfracao(String localInfracao) {
        this.localInfracao = localInfracao;
    }

    public String getCodigoMunicipioInfracao() {
        return codigoMunicipioInfracao;
    }

    public void setCodigoMunicipioInfracao(String codigoMunicipioInfracao) {
        this.codigoMunicipioInfracao = codigoMunicipioInfracao;
    }

    public String getNomeMunicipioInfracao() {
        return nomeMunicipioInfracao;
    }

    public void setNomeMunicipioInfracao(String nomeMunicipioInfracao) {
        this.nomeMunicipioInfracao = nomeMunicipioInfracao;
    }

    public String getCodigoAutuador() {
        return codigoAutuador;
    }

    public void setCodigoAutuador(String codigoAutuador) {
        this.codigoAutuador = codigoAutuador;
    }

    public Integer getQtdPontosInfracao() {
        return qtdPontosInfracao;
    }

    public void setQtdPontosInfracao(Integer qtdPontosInfracao) {
        this.qtdPontosInfracao = qtdPontosInfracao;
    }

    public String getDescricaoInfracao() {
        return descricaoInfracao;
    }

    public void setDescricaoInfracao(String descricaoInfracao) {
        this.descricaoInfracao = descricaoInfracao;
    }
    
//
//    public static void main(String... args){
//        
//        DadosPABPMSInfracaoWrapper teste = new DadosPABPMSInfracaoWrapper();
//        teste.setAutoInfracao("auto", nillable = true);
//        teste.setCodigoAutuador("codigoautuador", nillable = true);
//        teste.setCodigoInfracao("codigoinfracao", nillable = true);
//        teste.setCodigoMunicipioInfracao("cod mun infracao", nillable = true);
//        teste.setDataEmissaoPenalidade(new Date());
//        teste.setDataInfracao(new Date());
//        teste.setDataVencimentoPagamento(new Date());
//        teste.setDescricaoInfracao("infracao", nillable = true);
//        teste.setLocalInfracao("localinfracao", nillable = true);
//        teste.setNomeMunicipioInfracao("municipio", nillable = true);
//        teste.setPlaca("ooi3900", nillable = true);
//        teste.setQtdPontosInfracao(20);
//        teste.setTipoRegistro(TipoRegistroBPMSEnum.DADOS_INFRACAO.getCodigo());
//        
//        System.out.println(DetranStringUtil.getInstance().toJson(teste));
//        
//    }

    public String getDescricaoAutuador() {
        return descricaoAutuador;
    }

    public void setDescricaoAutuador(String descricaoAutuador) {
        this.descricaoAutuador = descricaoAutuador;
    }

    @Override
    public String getAutoInfracao() {
        return numeroAutoInfracao;
    }

    @Override
    public String getLocal() {
        return localInfracao;
    }

    @Override
    public Integer getMunicipioInfracao() {
        return Integer.parseInt(codigoMunicipioInfracao);
    }
}
