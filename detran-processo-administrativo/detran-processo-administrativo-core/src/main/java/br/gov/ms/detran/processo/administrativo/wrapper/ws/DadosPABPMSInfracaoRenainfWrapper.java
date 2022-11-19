package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.ejb.IProcessoAdministrativoInfracao;
import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class DadosPABPMSInfracaoRenainfWrapper implements IProcessoAdministrativoInfracao{
    
    @XmlElement(name = "tiporegistro", nillable = true)
    private final Integer tipoRegistro = TipoRegistroBPMSEnum.INFRACAO_RENAINF.getCodigo();

    @XmlElement(name = "datacadastramento", nillable = true)
    private Date dataCadastramento;

    @XmlElement(name = "ufautuador", nillable = true)
    private String ufAutuador;
    
    @XmlElement(name = "statusinfracao", nillable = true)
    private String statusInfracao;
    
    @XmlElement(name = "exigivel", nillable = true)
    private String exigivel;
    
    @XmlElement(name = "codigoautuador", nillable = true)
    private String codigoAutuador;
    
    @XmlElement(name = "descricaoautuador", nillable = true)
    private String descricaoAutuador;
    
    @XmlElement(name = "numeroautoinfracao", nillable = true)
    private String numeroAutoInfracao;
    
    @XmlElement(name = "codigorenainf", nillable = true)
    private String codigoRenainf;
    
    @XmlElement(name = "codigoinfracao", nillable = true)
    private String codigoInfracao;
    
    @XmlElement(name = "descricaoinfracao", nillable = true)
    private String descricaoInfracao;
    
    @XmlElement(name = "datainfracao", nillable = true)
    private Date dataInfracao;
    
    @XmlElement(name = "placaveiculo", nillable = true)
    private String placaVeiculo;
    
    @XmlElement(name = "codigopais", nillable = true)
    private String codigoPais;
    
    @XmlElement(name = "informacoescondutor", nillable = true)
    private String informacoesCondutor;
    
    @XmlElement(name = "localinfracao", nillable = true)
    private String localInfracao;
    
    @XmlElement(name = "codigomunicipio", nillable = true)
    private String codigoMunicipio;
    
    @XmlElement(name = "nomemunicipio", nillable = true)
    private String nomeMunicipio;
    
    @XmlElement(name = "medicaoreal", nillable = true)
    private String medicaoReal;
    
    @XmlElement(name = "limitepermitido", nillable = true)
    private String limitePermitido;
    
    @XmlElement(name = "medicaoconsiderada", nillable = true)
    private String medicaoConsiderada;
    
    @XmlElement(name = "valorinfracao", nillable = true)
    private String valorInfracao;
    
    @XmlElement(name = "codigounidade", nillable = true)
    private String codigoUnidade;
    
    @XmlElement(name = "observacoesgerais", nillable = true)
    private String observacoesGerais;
    
    @XmlElement(name = "mensagemrenainf", nillable = true)
    private String mensagemRenainf;
    
    @XmlElement(name = "orgaoautuador", nillable = true)
    private String orgaoAutuador;
    
    @XmlElement(name = "numeroauto", nillable = true)
    private String numeroAuto;
    
    @XmlElement(name = "horainfracao", nillable = true)
    private String horaInfracao;
    
    @XmlElement(name = "veiculouf", nillable = true)
    private String veiculoUf;
    
    @XmlElement(name = "mensagemcondutor", nillable = true)
    private String mensagemCondutor;
    
    @XmlElement(name = "unidademedida", nillable = true)
    private String unidadeMedida;
    
    @XmlElement(name = "renavam", nillable = true)
    private String renavam;
    
    @XmlElement(name = "codigomunicipioveiculo", nillable = true)
    private String codigoMunicipioVeiculo;
    
    @XmlElement(name = "municipiouf", nillable = true)
    private String municipioUf;
    
    @XmlElement(name = "codigomarca", nillable = true)
    private String codigoMarca;
    
    @XmlElement(name = "nomemarca", nillable = true)
    private String nomeMarca;
    
    @XmlElement(name = "codigocorveiculo", nillable = true)
    private String codigoCorVeiculo;
    
    @XmlElement(name = "nomecor", nillable = true)
    private String nomeCor;
    
    @XmlElement(name = "codigoespecie", nillable = true)
    private String codigoEspecie;
    
    @XmlElement(name = "nomeespecie", nillable = true)
    private String nomeEspecie;
    
    @XmlElement(name = "codigocarroceria", nillable = true)
    private String codigoCarroceria;
    
    @XmlElement(name = "nomecarroceria", nillable = true)
    private String nomeCarroceria;
    
    @XmlElement(name = "codigocategoria", nillable = true)
    private String codigoCategoria;
    
    @XmlElement(name = "nomecategoria", nillable = true)
    private String nomeCategoria;
    
    @XmlElement(name = "tipoveiculo", nillable = true)
    private String tipoVeiculo;
    
    @XmlElement(name = "nometipoveiculo", nillable = true)
    private String nomeTipoVeiculo;
    
    @XmlElement(name = "codigorestricao1", nillable = true)
    private String codigoRestricao1;
    
    @XmlElement(name = "descricaorestricao1", nillable = true)
    private String descricaoRestricao1;
    
    @XmlElement(name = "codigorestricao2", nillable = true)
    private String codigoRestricao2;
    
    @XmlElement(name = "descricaorestricao2", nillable = true)
    private String descricaoRestricao2;
    
    @XmlElement(name = "codigorestricao3", nillable = true)
    private String codigoRestricao3;
    
    @XmlElement(name = "descricaorestricao3", nillable = true)
    private String descricaoRestricao3;
    
    @XmlElement(name = "codigorestricao4", nillable = true)
    private String codigoRestricao4;
    
    @XmlElement(name = "descricaorestricao4", nillable = true)
    private String descricaoRestricao4;
    
    @XmlElement(name = "indicacaorestricaorenajud", nillable = true)
    private String indicacaoRestricaoRenajud;
    
    @XmlElement(name = "descricaoindicativorestricaorenajud", nillable = true)
    private String descricaoIndicativoRestricaoRenajud;
    
    @XmlElement(name = "indicativoroubofurto", nillable = true)
    private String indicativoRouboFurto;
    
    @XmlElement(name = "descricaoindicativoroubofurto", nillable = true)
    private String descricaoIndicativoRouboFurto;
    
    @XmlElement(name = "indicativorestricaorenavam", nillable = true)
    private String indicativoRestricaoRenavam;
    
    @XmlElement(name = "descricaorestricaorenavam", nillable = true)
    private String descricaoRestricaoRenavam;
    
    @XmlElement(name = "codigoorigempossuidor", nillable = true)
    private String codigoOrigemPossuidor;
    
    @XmlElement(name = "descricaoorigempossuidor", nillable = true)
    private String descricaoOrigemPossuidor;
    
    @XmlElement(name = "nomeproprietario", nillable = true)
    private String nomeProprietario;
    
    @XmlElement(name = "tipodocumentoproprietario", nillable = true)
    private String tipoDocumentoProprietario;
    
    @XmlElement(name = "descricaotipodocumentoproprietario", nillable = true)
    private String descricaoTipoDocumentoProprietario;
    
    @XmlElement(name = "numerodocumentoproprietario", nillable = true)
    private String numeroDocumentoProprietario;
    
    @XmlElement(name = "dataemissaonotificacaoautuacao", nillable = true)
    private Date dataEmissaoNotificacaoAutuacao;
    
    @XmlElement(name = "datalimitedefesanotificacaoautuacao", nillable = true)
    private Date dataLimiteDefesaNotificacaoAutuacao;
    
    @XmlElement(name = "dataaceitenotificacaoautuacao", nillable = true)
    private Date dataAceiteNotificacaoAutuacao;
    
    @XmlElement(name = "notificacaoautuacaoedital", nillable = true)
    private String notificacaoAutuacaoEdital;
    
    @XmlElement(name = "datanotificacaoautuacaoedital", nillable = true)
    private Date dataNotificacaoAutuacaoEdital;
    
    @XmlElement(name = "numeronotificacaopenalidade", nillable = true)
    private String numeroNotificacaoPenalidade;
    
    @XmlElement(name = "descricaopenalidade", nillable = true)
    private String descricaoPenalidade;
    
    @XmlElement(name = "dataemissaonotificacaopenalidade", nillable = true)
    private Date dataEmissaoNotificacaoPenalidade;
    
    @XmlElement(name = "datavencimentonotificacaopenalidade", nillable = true)
    private Date dataVencimentoNotificacaoPenalidade;
    
    @XmlElement(name = "dataaceitenotificacaopenalidade", nillable = true)
    private Date dataAceiteNotificacaoPenalidade;
    
    @XmlElement(name = "notificacaopenalidadeedital", nillable = true)
    private String notificacaoPenalidadeEdital;
    
    @XmlElement(name = "datanotificacaopenalidadeedital", nillable = true)
    private Date dataNotificacaoPenalidadeEdital;
    
    @XmlElement(name = "descricaoocorrencia", nillable = true)
    private String descricaoOcorrencia;
    
    @XmlElement(name = "origemocorrencia", nillable = true)
    private String origemOcorrencia;
    
    @XmlElement(name = "numeroprocesso", nillable = true)
    private String numeroProcesso;
    
    @XmlElement(name = "dataocorrencia", nillable = true)
    private Date dataOcorrencia;
    
    @XmlElement(name = "dataregistroocorrencia", nillable = true)
    private Date dataRegistroOcorrencia;
    
    @XmlElement(name = "nomeinfrator", nillable = true)
    private String nomeInfrator;

    @XmlElement(name = "modelocnhinfrator", nillable = true)
    private String modeloCnhInfrator;
    
    @XmlElement(name = "descricaocnhinfrator", nillable = true)
    private String descricaoCnhInfrator;
    
    @XmlElement(name = "numeroregistrocnhinfrator", nillable = true)
    private String numeroRegistroCnhInfrator;
    
    @XmlElement(name = "ufexpedicaocnhinfrator", nillable = true)
    private String ufExpedicaoCnhInfrator;
    
    @XmlElement(name = "dataapresentacaoinfrator", nillable = true)
    private Date dataApresentacaoInfrator;
    
    @XmlElement(name = "statusanalisepontuacao", nillable = true)
    private String statusAnalisePontuacao;
    
    @XmlElement(name = "descricaostatusanalisepontuacao", nillable = true)
    private String descricaoStatusAnalisePontuacao;
    
    @XmlElement(name = "modelocnhinfratorpontuacao", nillable = true)
    private String modeloCnhInfratorPontuacao;
        
    @XmlElement(name = "descricaocnhinfratorpontuacao", nillable = true)
    private String descricaoCnhInfratorPontuacao;
    
    @XmlElement(name = "numeroregistrocnhinfratorpontuacao", nillable = true)
    private String numeroRegistroCnhInfratorPontuacao;
    
    @XmlElement(name = "ufemissaopontuacao", nillable = true)
    private String ufEmissaoPontuacao;
    
    @XmlElement(name = "dataindicacaopontuacao", nillable = true)
    private Date dataIndicacaoPontuacao;
    
    @XmlElement(name = "uforigemdesvinculacao", nillable = true)
    private String ufOrigemDesvinculacao;
    
    @XmlElement(name = "datadesvinculacao", nillable = true)
    private Date dataDesvinculacao;
    
    @XmlElement(name = "dataaceitedesvinculacaouforigem", nillable = true)
    private Date dataAceiteDesvinculacaoUFOrigem;
    
    @XmlElement(name = "motivodesvinculacao", nillable = true)
    private String motivoDesvinculacao;
    
    @XmlElement(name = "descricaomotivodesvinculacao", nillable = true)
    private String descricaoMotivoDesvinculacao;

    public DadosPABPMSInfracaoRenainfWrapper() {
    }

    public Integer getTipoRegistro() {
        return tipoRegistro;
    }

    public Date getDataCadastramento() {
        return dataCadastramento;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataCadastramento(Date dataCadastramento) {
        this.dataCadastramento = dataCadastramento;
    }

    public String getUfAutuador() {
        return ufAutuador;
    }

    public void setUfAutuador(String ufAutuador) {
        this.ufAutuador = ufAutuador;
    }

    public String getStatusInfracao() {
        return statusInfracao;
    }

    public void setStatusInfracao(String statusInfracao) {
        this.statusInfracao = statusInfracao;
    }

    public String getExigivel() {
        return exigivel;
    }

    public void setExigivel(String exigivel) {
        this.exigivel = exigivel;
    }

    public String getCodigoAutuador() {
        return codigoAutuador;
    }

    public void setCodigoAutuador(String codigoAutuador) {
        this.codigoAutuador = codigoAutuador;
    }

    public String getDescricaoAutuador() {
        return descricaoAutuador;
    }

    public void setDescricaoAutuador(String descricaoAutuador) {
        this.descricaoAutuador = descricaoAutuador;
    }

    public String getNumeroAutoInfracao() {
        return numeroAutoInfracao;
    }

    public void setNumeroAutoInfracao(String numeroAutoInfracao) {
        this.numeroAutoInfracao = numeroAutoInfracao;
    }

    public String getCodigoRenainf() {
        return codigoRenainf;
    }

    public void setCodigoRenainf(String codigoRenainf) {
        this.codigoRenainf = codigoRenainf;
    }

    public String getCodigoInfracao() {
        return codigoInfracao;
    }

    public void setCodigoInfracao(String codigoInfracao) {
        this.codigoInfracao = codigoInfracao;
    }

    public String getDescricaoInfracao() {
        return descricaoInfracao;
    }

    public void setDescricaoInfracao(String descricaoInfracao) {
        this.descricaoInfracao = descricaoInfracao;
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

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }

    public String getInformacoesCondutor() {
        return informacoesCondutor;
    }

    public void setInformacoesCondutor(String informacoesCondutor) {
        this.informacoesCondutor = informacoesCondutor;
    }

    public String getLocalInfracao() {
        return localInfracao;
    }

    public void setLocalInfracao(String localInfracao) {
        this.localInfracao = localInfracao;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public String getNomeMunicipio() {
        return nomeMunicipio;
    }

    public void setNomeMunicipio(String nomeMunicipio) {
        this.nomeMunicipio = nomeMunicipio;
    }

    public String getMedicaoReal() {
        return medicaoReal;
    }

    public void setMedicaoReal(String medicaoReal) {
        this.medicaoReal = medicaoReal;
    }

    public String getLimitePermitido() {
        return limitePermitido;
    }

    public void setLimitePermitido(String limitePermitido) {
        this.limitePermitido = limitePermitido;
    }

    public String getMedicaoConsiderada() {
        return medicaoConsiderada;
    }

    public void setMedicaoConsiderada(String medicaoConsiderada) {
        this.medicaoConsiderada = medicaoConsiderada;
    }

    public String getValorInfracao() {
        return valorInfracao;
    }

    public void setValorInfracao(String valorInfracao) {
        this.valorInfracao = valorInfracao;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getObservacoesGerais() {
        return observacoesGerais;
    }

    public void setObservacoesGerais(String observacoesGerais) {
        this.observacoesGerais = observacoesGerais;
    }

    public String getMensagemRenainf() {
        return mensagemRenainf;
    }

    public void setMensagemRenainf(String mensagemRenainf) {
        this.mensagemRenainf = mensagemRenainf;
    }

    public String getOrgaoAutuador() {
        return orgaoAutuador;
    }

    public void setOrgaoAutuador(String orgaoAutuador) {
        this.orgaoAutuador = orgaoAutuador;
    }

    public String getNumeroAuto() {
        return numeroAuto;
    }

    public void setNumeroAuto(String numeroAuto) {
        this.numeroAuto = numeroAuto;
    }

    public String getHoraInfracao() {
        return horaInfracao;
    }

    public void setHoraInfracao(String horaInfracao) {
        this.horaInfracao = horaInfracao;
    }

    public String getVeiculoUf() {
        return veiculoUf;
    }

    public void setVeiculoUf(String veiculoUf) {
        this.veiculoUf = veiculoUf;
    }

    public String getMensagemCondutor() {
        return mensagemCondutor;
    }

    public void setMensagemCondutor(String mensagemCondutor) {
        this.mensagemCondutor = mensagemCondutor;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public String getRenavam() {
        return renavam;
    }

    public void setRenavam(String renavam) {
        this.renavam = renavam;
    }

    public String getCodigoMunicipioVeiculo() {
        return codigoMunicipioVeiculo;
    }

    public void setCodigoMunicipioVeiculo(String codigoMunicipioVeiculo) {
        this.codigoMunicipioVeiculo = codigoMunicipioVeiculo;
    }

    public String getMunicipioUf() {
        return municipioUf;
    }

    public void setMunicipioUf(String municipioUf) {
        this.municipioUf = municipioUf;
    }

    public String getCodigoMarca() {
        return codigoMarca;
    }

    public void setCodigoMarca(String codigoMarca) {
        this.codigoMarca = codigoMarca;
    }

    public String getNomeMarca() {
        return nomeMarca;
    }

    public void setNomeMarca(String nomeMarca) {
        this.nomeMarca = nomeMarca;
    }

    public String getCodigoCorVeiculo() {
        return codigoCorVeiculo;
    }

    public void setCodigoCorVeiculo(String codigoCorVeiculo) {
        this.codigoCorVeiculo = codigoCorVeiculo;
    }

    public String getNomeCor() {
        return nomeCor;
    }

    public void setNomeCor(String nomeCor) {
        this.nomeCor = nomeCor;
    }

    public String getCodigoEspecie() {
        return codigoEspecie;
    }

    public void setCodigoEspecie(String codigoEspecie) {
        this.codigoEspecie = codigoEspecie;
    }

    public String getNomeEspecie() {
        return nomeEspecie;
    }

    public void setNomeEspecie(String nomeEspecie) {
        this.nomeEspecie = nomeEspecie;
    }

    public String getCodigoCarroceria() {
        return codigoCarroceria;
    }

    public void setCodigoCarroceria(String codigoCarroceria) {
        this.codigoCarroceria = codigoCarroceria;
    }

    public String getNomeCarroceria() {
        return nomeCarroceria;
    }

    public void setNomeCarroceria(String nomeCarroceria) {
        this.nomeCarroceria = nomeCarroceria;
    }

    public String getCodigoCategoria() {
        return codigoCategoria;
    }

    public void setCodigoCategoria(String codigoCategoria) {
        this.codigoCategoria = codigoCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public String getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(String tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getNomeTipoVeiculo() {
        return nomeTipoVeiculo;
    }

    public void setNomeTipoVeiculo(String nomeTipoVeiculo) {
        this.nomeTipoVeiculo = nomeTipoVeiculo;
    }

    public String getCodigoRestricao1() {
        return codigoRestricao1;
    }

    public void setCodigoRestricao1(String codigoRestricao1) {
        this.codigoRestricao1 = codigoRestricao1;
    }

    public String getDescricaoRestricao1() {
        return descricaoRestricao1;
    }

    public void setDescricaoRestricao1(String descricaoRestricao1) {
        this.descricaoRestricao1 = descricaoRestricao1;
    }

    public String getCodigoRestricao2() {
        return codigoRestricao2;
    }

    public void setCodigoRestricao2(String codigoRestricao2) {
        this.codigoRestricao2 = codigoRestricao2;
    }

    public String getDescricaoRestricao2() {
        return descricaoRestricao2;
    }

    public void setDescricaoRestricao2(String descricaoRestricao2) {
        this.descricaoRestricao2 = descricaoRestricao2;
    }

    public String getCodigoRestricao3() {
        return codigoRestricao3;
    }

    public void setCodigoRestricao3(String codigoRestricao3) {
        this.codigoRestricao3 = codigoRestricao3;
    }

    public String getDescricaoRestricao3() {
        return descricaoRestricao3;
    }

    public void setDescricaoRestricao3(String descricaoRestricao3) {
        this.descricaoRestricao3 = descricaoRestricao3;
    }

    public String getCodigoRestricao4() {
        return codigoRestricao4;
    }

    public void setCodigoRestricao4(String codigoRestricao4) {
        this.codigoRestricao4 = codigoRestricao4;
    }

    public String getDescricaoRestricao4() {
        return descricaoRestricao4;
    }

    public void setDescricaoRestricao4(String descricaoRestricao4) {
        this.descricaoRestricao4 = descricaoRestricao4;
    }

    public String getIndicacaoRestricaoRenajud() {
        return indicacaoRestricaoRenajud;
    }

    public void setIndicacaoRestricaoRenajud(String indicacaoRestricaoRenajud) {
        this.indicacaoRestricaoRenajud = indicacaoRestricaoRenajud;
    }

    public String getDescricaoIndicativoRestricaoRenajud() {
        return descricaoIndicativoRestricaoRenajud;
    }

    public void setDescricaoIndicativoRestricaoRenajud(String descricaoIndicativoRestricaoRenajud) {
        this.descricaoIndicativoRestricaoRenajud = descricaoIndicativoRestricaoRenajud;
    }

    public String getIndicativoRouboFurto() {
        return indicativoRouboFurto;
    }

    public void setIndicativoRouboFurto(String indicativoRouboFurto) {
        this.indicativoRouboFurto = indicativoRouboFurto;
    }

    public String getDescricaoIndicativoRouboFurto() {
        return descricaoIndicativoRouboFurto;
    }

    public void setDescricaoIndicativoRouboFurto(String descricaoIndicativoRouboFurto) {
        this.descricaoIndicativoRouboFurto = descricaoIndicativoRouboFurto;
    }

    public String getIndicativoRestricaoRenavam() {
        return indicativoRestricaoRenavam;
    }

    public void setIndicativoRestricaoRenavam(String indicativoRestricaoRenavam) {
        this.indicativoRestricaoRenavam = indicativoRestricaoRenavam;
    }

    public String getDescricaoRestricaoRenavam() {
        return descricaoRestricaoRenavam;
    }

    public void setDescricaoRestricaoRenavam(String descricaoRestricaoRenavam) {
        this.descricaoRestricaoRenavam = descricaoRestricaoRenavam;
    }

    public String getCodigoOrigemPossuidor() {
        return codigoOrigemPossuidor;
    }

    public void setCodigoOrigemPossuidor(String codigoOrigemPossuidor) {
        this.codigoOrigemPossuidor = codigoOrigemPossuidor;
    }

    public String getDescricaoOrigemPossuidor() {
        return descricaoOrigemPossuidor;
    }

    public void setDescricaoOrigemPossuidor(String descricaoOrigemPossuidor) {
        this.descricaoOrigemPossuidor = descricaoOrigemPossuidor;
    }

    public String getNomeProprietario() {
        return nomeProprietario;
    }

    public void setNomeProprietario(String nomeProprietario) {
        this.nomeProprietario = nomeProprietario;
    }

    public String getTipoDocumentoProprietario() {
        return tipoDocumentoProprietario;
    }

    public void setTipoDocumentoProprietario(String tipoDocumentoProprietario) {
        this.tipoDocumentoProprietario = tipoDocumentoProprietario;
    }

    public String getDescricaoTipoDocumentoProprietario() {
        return descricaoTipoDocumentoProprietario;
    }

    public void setDescricaoTipoDocumentoProprietario(String descricaoTipoDocumentoProprietario) {
        this.descricaoTipoDocumentoProprietario = descricaoTipoDocumentoProprietario;
    }

    public String getNumeroDocumentoProprietario() {
        return numeroDocumentoProprietario;
    }

    public void setNumeroDocumentoProprietario(String numeroDocumentoProprietario) {
        this.numeroDocumentoProprietario = numeroDocumentoProprietario;
    }

    public Date getDataEmissaoNotificacaoAutuacao() {
        return dataEmissaoNotificacaoAutuacao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataEmissaoNotificacaoAutuacao(Date dataEmissaoNotificacaoAutuacao) {
        this.dataEmissaoNotificacaoAutuacao = dataEmissaoNotificacaoAutuacao;
    }

    public Date getDataLimiteDefesaNotificacaoAutuacao() {
        return dataLimiteDefesaNotificacaoAutuacao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataLimiteDefesaNotificacaoAutuacao(Date dataLimiteDefesaNotificacaoAutuacao) {
        this.dataLimiteDefesaNotificacaoAutuacao = dataLimiteDefesaNotificacaoAutuacao;
    }

    public Date getDataAceiteNotificacaoAutuacao() {
        return dataAceiteNotificacaoAutuacao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataAceiteNotificacaoAutuacao(Date dataAceiteNotificacaoAutuacao) {
        this.dataAceiteNotificacaoAutuacao = dataAceiteNotificacaoAutuacao;
    }

    public String getNotificacaoAutuacaoEdital() {
        return notificacaoAutuacaoEdital;
    }

    public void setNotificacaoAutuacaoEdital(String notificacaoAutuacaoEdital) {
        this.notificacaoAutuacaoEdital = notificacaoAutuacaoEdital;
    }

    public Date getDataNotificacaoAutuacaoEdital() {
        return dataNotificacaoAutuacaoEdital;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataNotificacaoAutuacaoEdital(Date dataNotificacaoAutuacaoEdital) {
        this.dataNotificacaoAutuacaoEdital = dataNotificacaoAutuacaoEdital;
    }

    public String getNumeroNotificacaoPenalidade() {
        return numeroNotificacaoPenalidade;
    }

    public void setNumeroNotificacaoPenalidade(String numeroNotificacaoPenalidade) {
        this.numeroNotificacaoPenalidade = numeroNotificacaoPenalidade;
    }

    public String getDescricaoPenalidade() {
        return descricaoPenalidade;
    }

    public void setDescricaoPenalidade(String descricaoPenalidade) {
        this.descricaoPenalidade = descricaoPenalidade;
    }

    public Date getDataEmissaoNotificacaoPenalidade() {
        return dataEmissaoNotificacaoPenalidade;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataEmissaoNotificacaoPenalidade(Date dataEmissaoNotificacaoPenalidade) {
        this.dataEmissaoNotificacaoPenalidade = dataEmissaoNotificacaoPenalidade;
    }

    public Date getDataVencimentoNotificacaoPenalidade() {
        return dataVencimentoNotificacaoPenalidade;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataVencimentoNotificacaoPenalidade(Date dataVencimentoNotificacaoPenalidade) {
        this.dataVencimentoNotificacaoPenalidade = dataVencimentoNotificacaoPenalidade;
    }

    public Date getDataAceiteNotificacaoPenalidade() {
        return dataAceiteNotificacaoPenalidade;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataAceiteNotificacaoPenalidade(Date dataAceiteNotificacaoPenalidade) {
        this.dataAceiteNotificacaoPenalidade = dataAceiteNotificacaoPenalidade;
    }

    public String getNotificacaoPenalidadeEdital() {
        return notificacaoPenalidadeEdital;
    }

    public void setNotificacaoPenalidadeEdital(String notificacaoPenalidadeEdital) {
        this.notificacaoPenalidadeEdital = notificacaoPenalidadeEdital;
    }

    public Date getDataNotificacaoPenalidadeEdital() {
        return dataNotificacaoPenalidadeEdital;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataNotificacaoPenalidadeEdital(Date dataNotificacaoPenalidadeEdital) {
        this.dataNotificacaoPenalidadeEdital = dataNotificacaoPenalidadeEdital;
    }

    public String getDescricaoOcorrencia() {
        return descricaoOcorrencia;
    }

    public void setDescricaoOcorrencia(String descricaoOcorrencia) {
        this.descricaoOcorrencia = descricaoOcorrencia;
    }

    public String getOrigemOcorrencia() {
        return origemOcorrencia;
    }

    public void setOrigemOcorrencia(String origemOcorrencia) {
        this.origemOcorrencia = origemOcorrencia;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public Date getDataRegistroOcorrencia() {
        return dataRegistroOcorrencia;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataRegistroOcorrencia(Date dataRegistroOcorrencia) {
        this.dataRegistroOcorrencia = dataRegistroOcorrencia;
    }

    public String getNomeInfrator() {
        return nomeInfrator;
    }

    public void setNomeInfrator(String nomeInfrator) {
        this.nomeInfrator = nomeInfrator;
    }

    public String getModeloCnhInfrator() {
        return modeloCnhInfrator;
    }

    public void setModeloCnhInfrator(String modeloCnhInfrator) {
        this.modeloCnhInfrator = modeloCnhInfrator;
    }

    public String getDescricaoCnhInfrator() {
        return descricaoCnhInfrator;
    }

    public void setDescricaoCnhInfrator(String descricaoCnhInfrator) {
        this.descricaoCnhInfrator = descricaoCnhInfrator;
    }

    public String getNumeroRegistroCnhInfrator() {
        return numeroRegistroCnhInfrator;
    }

    public void setNumeroRegistroCnhInfrator(String numeroRegistroCnhInfrator) {
        this.numeroRegistroCnhInfrator = numeroRegistroCnhInfrator;
    }

    public String getUfExpedicaoCnhInfrator() {
        return ufExpedicaoCnhInfrator;
    }

    public void setUfExpedicaoCnhInfrator(String ufExpedicaoCnhInfrator) {
        this.ufExpedicaoCnhInfrator = ufExpedicaoCnhInfrator;
    }

    public Date getDataApresentacaoInfrator() {
        return dataApresentacaoInfrator;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataApresentacaoInfrator(Date dataApresentacaoInfrator) {
        this.dataApresentacaoInfrator = dataApresentacaoInfrator;
    }

    public String getStatusAnalisePontuacao() {
        return statusAnalisePontuacao;
    }

    public void setStatusAnalisePontuacao(String statusAnalisePontuacao) {
        this.statusAnalisePontuacao = statusAnalisePontuacao;
    }

    public String getDescricaoStatusAnalisePontuacao() {
        return descricaoStatusAnalisePontuacao;
    }

    public void setDescricaoStatusAnalisePontuacao(String descricaoStatusAnalisePontuacao) {
        this.descricaoStatusAnalisePontuacao = descricaoStatusAnalisePontuacao;
    }

    public String getModeloCnhInfratorPontuacao() {
        return modeloCnhInfratorPontuacao;
    }

    public void setModeloCnhInfratorPontuacao(String modeloCnhInfratorPontuacao) {
        this.modeloCnhInfratorPontuacao = modeloCnhInfratorPontuacao;
    }

    public String getDescricaoCnhInfratorPontuacao() {
        return descricaoCnhInfratorPontuacao;
    }

    public void setDescricaoCnhInfratorPontuacao(String descricaoCnhInfratorPontuacao) {
        this.descricaoCnhInfratorPontuacao = descricaoCnhInfratorPontuacao;
    }

    public String getNumeroRegistroCnhInfratorPontuacao() {
        return numeroRegistroCnhInfratorPontuacao;
    }

    public void setNumeroRegistroCnhInfratorPontuacao(String numeroRegistroCnhInfratorPontuacao) {
        this.numeroRegistroCnhInfratorPontuacao = numeroRegistroCnhInfratorPontuacao;
    }

    public String getUfEmissaoPontuacao() {
        return ufEmissaoPontuacao;
    }

    public void setUfEmissaoPontuacao(String ufEmissaoPontuacao) {
        this.ufEmissaoPontuacao = ufEmissaoPontuacao;
    }

    public Date getDataIndicacaoPontuacao() {
        return dataIndicacaoPontuacao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataIndicacaoPontuacao(Date dataIndicacaoPontuacao) {
        this.dataIndicacaoPontuacao = dataIndicacaoPontuacao;
    }

    public String getUfOrigemDesvinculacao() {
        return ufOrigemDesvinculacao;
    }

    public void setUfOrigemDesvinculacao(String ufOrigemDesvinculacao) {
        this.ufOrigemDesvinculacao = ufOrigemDesvinculacao;
    }

    public Date getDataDesvinculacao() {
        return dataDesvinculacao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataDesvinculacao(Date dataDesvinculacao) {
        this.dataDesvinculacao = dataDesvinculacao;
    }

    public Date getDataAceiteDesvinculacaoUFOrigem() {
        return dataAceiteDesvinculacaoUFOrigem;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataAceiteDesvinculacaoUFOrigem(Date dataAceiteDesvinculacaoUFOrigem) {
        this.dataAceiteDesvinculacaoUFOrigem = dataAceiteDesvinculacaoUFOrigem;
    }

    public String getMotivoDesvinculacao() {
        return motivoDesvinculacao;
    }

    public void setMotivoDesvinculacao(String motivoDesvinculacao) {
        this.motivoDesvinculacao = motivoDesvinculacao;
    }

    public String getDescricaoMotivoDesvinculacao() {
        return descricaoMotivoDesvinculacao;
    }

    public void setDescricaoMotivoDesvinculacao(String descricaoMotivoDesvinculacao) {
        this.descricaoMotivoDesvinculacao = descricaoMotivoDesvinculacao;
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
    
    @XmlElement(name = "codigomunicipioinfracao", nillable = true)
    @Override
    public Integer getMunicipioInfracao() {
        return !DetranStringUtil.ehBrancoOuNulo(codigoMunicipio) ? Integer.parseInt(codigoMunicipio) : null;
    }
}