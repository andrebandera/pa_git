package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author yanko.campos
 */
public class DadosPABPMSInfracaoLocalWrapper implements IProcessoAdministrativoInfracao{
    
    @XmlElement(name = "tiporegistro", nillable = true)
    private final Integer tipoRegistro = TipoRegistroBPMSEnum.INFRACAO_LOCAL.getCodigo();
    
    @XmlElement(name = "numeroautoinfracao", nillable = true)
    private String numeroAutoInfracao;
    
    @XmlElement(name = "responsavelinfracao", nillable = true)
    private String responsavelInfracao;
    
    @XmlElement(name = "placa", nillable = true)
    private String placa;
    
    @XmlElement(name = "codigomunicipioveiculo", nillable = true)
    private Integer codigoMunicipioVeiculo;
    
    @XmlElement(name = "codigoinfracao", nillable = true)
    private String codigoInfracao;
    
    @XmlElement(name = "competencia", nillable = true)
    private String competencia;
    
    @XmlElement(name = "codigotipoinfracao", nillable = true)
    private String codigoTipoInfracao;
    
    @XmlElement(name = "descricaotipoinfracao", nillable = true)
    private String descricaoTipoInfracao;
    
    @XmlElement(name = "datainfracao", nillable = true)
    private Date dataInfracao;

    @XmlElement(name = "datanotificacaopenalidade", nillable = true)
    private Date dataNotificacaoPenalidade;
    
    @XmlElement(name = "datavencimento", nillable = true)
    private Date dataVencimento;
    
    @XmlElement(name = "situacaonotificacao", nillable = true)
    private String situacaoNotificacao;
    
    @XmlElement(name = "informacaobaixa", nillable = true)
    private String informacaoBaixa;
    
    @XmlElement(name = "numeroguiapagamento", nillable = true)
    private String numeroGuiaPagamento;
    
    @XmlElement(name = "valorpago", nillable = true)
    private String valorPago;
    
    @XmlElement(name = "datapagamento", nillable = true)
    private Date dataPagamento;
    
    @XmlElement(name = "datarelatoriopublicardo", nillable = true)
    private Date dataRelatorioPublicarDo;
    
    @XmlElement(name = "dataemissaodo", nillable = true)
    private Date dataEmissaoDo;
    
    @XmlElement(name = "localinfracao", nillable = true)
    private String localInfracao;
    
    @XmlElement(name = "codigomunicipioinfracao", nillable = true)
    private Integer codigoMunicipioInfracao;
    
    @XmlElement(name = "nomemunicipioinfracao", nillable = true)
    private String nomeMunicipioInfracao;
    
    @XmlElement(name = "velocidadeconsiderada", nillable = true)
    private Integer velocidadeConsiderada;
    
    @XmlElement(name = "tipoautuador", nillable = true)
    private Integer tipoAutuador;
    
    @XmlElement(name = "descricaotipoautuador", nillable = true)
    private String descricaoTipoAutuador;
    
    @XmlElement(name = "nomecondutor", nillable = true)
    private String nomeCondutor;
    
    @XmlElement(name = "infratornotificado", nillable = true)
    private String infratorNotificado;
    
    @XmlElement(name = "cpfcondutor", nillable = true)
    private String cpfCondutor;
    
    @XmlElement(name = "pguregistrocondutor", nillable = true)
    private String pguRegistroCondutor;
    
    @XmlElement(name = "cnhcondutor", nillable = true)
    private Long cnhCondutor;
    
    @XmlElement(name = "descricaomarcaveiculo", nillable = true)
    private String descricaoMarcaVeiculo;
    
    @XmlElement(name = "descricaoparcelamentomulta", nillable = true)
    private String descricaoParcelamentoMulta;

    public DadosPABPMSInfracaoLocalWrapper() {
    }

    public DadosPABPMSInfracaoLocalWrapper(String numeroAutoInfracao, 
                                         String responsavelInfracao, 
                                         String placa, 
                                         Integer codigoMunicipioVeiculo, 
                                         String codigoInfracao, 
                                         String competencia, 
                                         String codigoTipoInfracao, 
                                         String descricaoTipoInfracao, 
                                         Date dataInfracao, 
                                         Date dataNotificacaoPenalidade, 
                                         Date dataVencimento, 
                                         String situacaoNotificacao, 
                                         String informacaoBaixa, 
                                         String numeroGuiaPagamento, 
                                         String valorPago, 
                                         Date dataPagamento, 
                                         Date dataRelatorioPublicarDo, 
                                         Date dataEmissaoDo, 
                                         String localInfracao, 
                                         Integer codigoMunicipioInfracao, 
                                         String nomeMunicipioInfracao, 
                                         Integer velocidadeConsiderada, 
                                         Integer tipoAutuador,
                                         String descricaoTipoAutuador, 
                                         String nomeCondutor, 
                                         String infratorNotificado, 
                                         String cpfCondutor, 
                                         String pguRegistroCondutor, 
                                         Long cnhCondutor, 
                                         String descricaoMarcaVeiculo, 
                                         String descricaoParcelamentoMulta) {
        
        this.numeroAutoInfracao = numeroAutoInfracao;
        this.responsavelInfracao = responsavelInfracao;
        this.placa = placa;
        this.codigoMunicipioVeiculo = codigoMunicipioVeiculo;
        this.codigoInfracao = codigoInfracao;
        this.competencia = competencia;
        this.codigoTipoInfracao = codigoTipoInfracao;
        this.descricaoTipoInfracao = descricaoTipoInfracao;
        this.dataInfracao = dataInfracao;
        this.dataNotificacaoPenalidade = dataNotificacaoPenalidade;
        this.dataVencimento = dataVencimento;
        this.situacaoNotificacao = situacaoNotificacao;
        this.informacaoBaixa = informacaoBaixa;
        this.numeroGuiaPagamento = numeroGuiaPagamento;
        this.valorPago = valorPago;
        this.dataPagamento = dataPagamento;
        this.dataRelatorioPublicarDo = dataRelatorioPublicarDo;
        this.dataEmissaoDo = dataEmissaoDo;
        this.localInfracao = localInfracao;
        this.codigoMunicipioInfracao = codigoMunicipioInfracao;
        this.nomeMunicipioInfracao = nomeMunicipioInfracao;
        this.velocidadeConsiderada = velocidadeConsiderada;
        this.tipoAutuador = tipoAutuador;
        this.descricaoTipoAutuador = descricaoTipoAutuador;
        this.nomeCondutor = nomeCondutor;
        this.infratorNotificado = infratorNotificado;
        this.cpfCondutor = cpfCondutor;
        this.pguRegistroCondutor = pguRegistroCondutor;
        this.cnhCondutor = cnhCondutor;
        this.descricaoMarcaVeiculo = descricaoMarcaVeiculo;
        this.descricaoParcelamentoMulta = descricaoParcelamentoMulta;
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

    public String getResponsavelInfracao() {
        return responsavelInfracao;
    }

    public void setResponsavelInfracao(String responsavelInfracao) {
        this.responsavelInfracao = responsavelInfracao;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Integer getCodigoMunicipioVeiculo() {
        return codigoMunicipioVeiculo;
    }

    public void setCodigoMunicipioVeiculo(Integer codigoMunicipioVeiculo) {
        this.codigoMunicipioVeiculo = codigoMunicipioVeiculo;
    }

    public String getCodigoInfracao() {
        return codigoInfracao;
    }

    public void setCodigoInfracao(String codigoInfracao) {
        this.codigoInfracao = codigoInfracao;
    }

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }

    public String getCodigoTipoInfracao() {
        return codigoTipoInfracao;
    }

    public void setCodigoTipoInfracao(String codigoTipoInfracao) {
        this.codigoTipoInfracao = codigoTipoInfracao;
    }

    public String getDescricaoTipoInfracao() {
        return descricaoTipoInfracao;
    }

    public void setDescricaoTipoInfracao(String descricaoTipoInfracao) {
        this.descricaoTipoInfracao = descricaoTipoInfracao;
    }

    @XmlElement(name = "datainfracao", nillable = true)
    @Override
    public Date getDataInfracao() {
        return dataInfracao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataInfracao(Date dataInfracao) {
        this.dataInfracao = dataInfracao;
    }

    public Date getDataNotificacaoPenalidade() {
        return dataNotificacaoPenalidade;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataNotificacaoPenalidade(Date dataNotificacaoPenalidade) {
        this.dataNotificacaoPenalidade = dataNotificacaoPenalidade;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getSituacaoNotificacao() {
        return situacaoNotificacao;
    }

    public void setSituacaoNotificacao(String situacaoNotificacao) {
        this.situacaoNotificacao = situacaoNotificacao;
    }

    public String getInformacaoBaixa() {
        return informacaoBaixa;
    }

    public void setInformacaoBaixa(String informacaoBaixa) {
        this.informacaoBaixa = informacaoBaixa;
    }

    public String getNumeroGuiaPagamento() {
        return numeroGuiaPagamento;
    }

    public void setNumeroGuiaPagamento(String numeroGuiaPagamento) {
        this.numeroGuiaPagamento = numeroGuiaPagamento;
    }

    public String getValorPago() {
        return valorPago;
    }

    public void setValorPago(String valorPago) {
        this.valorPago = valorPago;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Date getDataRelatorioPublicarDo() {
        return dataRelatorioPublicarDo;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataRelatorioPublicarDo(Date dataRelatorioPublicarDo) {
        this.dataRelatorioPublicarDo = dataRelatorioPublicarDo;
    }

    public Date getDataEmissaoDo() {
        return dataEmissaoDo;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataEmissaoDo(Date dataEmissaoDo) {
        this.dataEmissaoDo = dataEmissaoDo;
    }

    public String getLocalInfracao() {
        return localInfracao;
    }

    public void setLocalInfracao(String localInfracao) {
        this.localInfracao = localInfracao;
    }

    @Override
    public Integer getMunicipioInfracao() {
        return codigoMunicipioInfracao;
    }

    public void setCodigoMunicipioInfracao(Integer codigoMunicipioInfracao) {
        this.codigoMunicipioInfracao = codigoMunicipioInfracao;
    }

    public String getNomeMunicipioInfracao() {
        return nomeMunicipioInfracao;
    }

    public void setNomeMunicipioInfracao(String nomeMunicipioInfracao) {
        this.nomeMunicipioInfracao = nomeMunicipioInfracao;
    }

    public Integer getVelocidadeConsiderada() {
        return velocidadeConsiderada;
    }

    public void setVelocidadeConsiderada(Integer velocidadeConsiderada) {
        this.velocidadeConsiderada = velocidadeConsiderada;
    }

    public Integer getTipoAutuador() {
        return tipoAutuador;
    }

    public void setTipoAutuador(Integer tipoAutuador) {
        this.tipoAutuador = tipoAutuador;
    }

    public String getDescricaoTipoAutuador() {
        return descricaoTipoAutuador;
    }

    public void setDescricaoTipoAutuador(String descricaoTipoAutuador) {
        this.descricaoTipoAutuador = descricaoTipoAutuador;
    }

    public String getNomeCondutor() {
        return nomeCondutor;
    }

    public void setNomeCondutor(String nomeCondutor) {
        this.nomeCondutor = nomeCondutor;
    }

    public String getInfratorNotificado() {
        return infratorNotificado;
    }

    public void setInfratorNotificado(String infratorNotificado) {
        this.infratorNotificado = infratorNotificado;
    }

    public String getCpfCondutor() {
        return cpfCondutor;
    }

    public void setCpfCondutor(String cpfCondutor) {
        this.cpfCondutor = cpfCondutor;
    }

    public String getPguRegistroCondutor() {
        return pguRegistroCondutor;
    }

    public void setPguRegistroCondutor(String pguRegistroCondutor) {
        this.pguRegistroCondutor = pguRegistroCondutor;
    }

    public Long getCnhCondutor() {
        return cnhCondutor;
    }

    public void setCnhCondutor(Long cnhCondutor) {
        this.cnhCondutor = cnhCondutor;
    }

    public String getDescricaoMarcaVeiculo() {
        return descricaoMarcaVeiculo;
    }

    public void setDescricaoMarcaVeiculo(String descricaoMarcaVeiculo) {
        this.descricaoMarcaVeiculo = descricaoMarcaVeiculo;
    }

    public String getDescricaoParcelamentoMulta() {
        return descricaoParcelamentoMulta;
    }

    public void setDescricaoParcelamentoMulta(String descricaoParcelamentoMulta) {
        this.descricaoParcelamentoMulta = descricaoParcelamentoMulta;
    }

    @XmlElement(name = "autoinfracao", nillable = true)
    @Override
    public String getAutoInfracao() {
        return this.numeroAutoInfracao;
    }

    @Override
    public String getLocal() {
        return this.localInfracao;
    }
}