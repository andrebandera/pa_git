package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoRegistroBPMSEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class DadosPABPMSDetalhesAutuacaoInfracaoWrapper {
    
    @XmlElement(name = "tiporegistro", nillable = true)
    private final Integer tipoRegistro = TipoRegistroBPMSEnum.DETALHES_AUTUACAO_INFRACAO.getCodigo();
    
    @XmlElement(name = "numeroautoinfracao", nillable = true)
    private String numeroAutoInfracao;
    
    @XmlElement(name = "codigoinfracao", nillable = true)
    private String codigoInfracao;

    @XmlElement(name = "orgaoautuador", nillable = true)
    private String orgaoAutuador;
    
    @XmlElement(name = "tipoinfracao", nillable = true)
    private String tipoInfracao;

    @XmlElement(name = "responsavelpontos", nillable = true)
    private String responsavelPontos;

    @XmlElement(name = "placa", nillable = true)
    private String placa;

    @XmlElement(name = "marcamodelo", nillable = true)
    private String marcaModelo;

    @XmlElement(name = "codigomunicipioveiculo", nillable = true)
    private String codMunicipioVeiculo;
    
    @XmlElement(name = "ufmunicipioveiculo", nillable = true)
    private String ufMunicipioVeiculo;

    @XmlElement(name = "descricaoinfracao", nillable = true)
    private String descricaoInfracao;

    @XmlElement(name = "valorinfracao", nillable = true)
    private String valorInfracao;

    @XmlElement(name = "codigotipoinfracao", nillable = true)
    private String codigoTipoInfracao;

    @XmlElement(name = "descricaotipoinfracao", nillable = true)
    private String descricaoTipoInfracao;

    @XmlElement(name = "competencia", nillable = true)
    private String competencia;

    @XmlElement(name = "datainfracao", nillable = true)
    private Date dataInfracao;

    @XmlElement(name = "datalimitedefesa", nillable = true)
    private Date dataLimiteDefesa;

    @XmlElement(name = "dataemissaonotaut", nillable = true)
    private Date dataEmissaoNotAut;

    @XmlElement(name = "datapublicacaodiariooficial", nillable = true)
    private Date dataPublicacaoDiarioOficial;

    @XmlElement(name = "localinfracao", nillable = true)
    private String localInfracao;

    @XmlElement(name = "codigomunicipioinfracao", nillable = true)
    private String codMunicipioInfracao;

    @XmlElement(name = "nomemunicipioinfracao", nillable = true)
    private String nomeMunicipioInfracao;

    @XmlElement(name = "velocidadeconsiderada", nillable = true)
    private String velocidadeConsiderada;

    @XmlElement(name = "tipoautuador", nillable = true)
    private String tipoAutuador;

    @XmlElement(name = "descricaoautuador", nillable = true)
    private String descricaoAutuador;

    @XmlElement(name = "codigolocaleletronico", nillable = true)
    private String codigoLocalEletronico;

    @XmlElement(name = "nomecondutor", nillable = true)
    private String nomeCondutor;
    
    @XmlElement(name = "condutoridentificado", nillable = true)
    private String condutorIdentificado;

    @XmlElement(name = "cpfcondutor", nillable = true)
    private String cpfCondutor;

    @XmlElement(name = "pguregistrocondutor", nillable = true)
    private String pguRegistroCondutor;

    @XmlElement(name = "cnhcondutor", nillable = true)
    private Long cnhCondutor;

    @XmlElement(name = "situacaonotificacao", nillable = true)
    private String situacaoNotificacao;

    @XmlElement(name = "codigorenainfinfracao", nillable = true)
    private String codigoRenainfInfracao;

    @XmlElement(name = "guiapagamento", nillable = true)
    private String guiaPagamento;

    @XmlElement(name = "guiapenalidade", nillable = true)
    private String guiaPenalidade;

    @XmlElement(name = "informacaoexcessopeso", nillable = true)
    private String informacaoExcessoPeso;

    @XmlElement(name = "descricaostatusinfracao", nillable = true)
    private String descricaoStatusInfracao;

    @XmlElement(name = "datacadastroinfracao", nillable = true)
    private Date dataCadastroInfracao;

    @XmlElement(name = "informacaoprocedentes", nillable = true)
    private String informacaoProcedentes;

    @XmlElement(name = "nomeproprietariocondutor", nillable = true)
    private String nomeProprietarioCondutor;

    @XmlElement(name = "pguproprietariocondutor", nillable = true)
    private String pguProprietarioCondutor;

    @XmlElement(name = "numregistroproprietariocondutor", nillable = true)
    private String numRegistroProprietarioCondutor;

    @XmlElement(name = "cpfcnpjproprietariocondutor", nillable = true)
    private String cpfCnpjProprietarioCondutor;

    @XmlElement(name = "numeroobjetonotificacao", nillable = true)
    private String numeroObjetoNotificacao;

    @XmlElement(name = "numeroobjetopenalizacao", nillable = true)
    private String numeroObjetoPenalizacao;

    @XmlElement(name = "situacaoatualnotificacaoar", nillable = true)
    private String situacaoAtualNotificacaoAR;

    @XmlElement(name = "nomerecebedornotificacao", nillable = true)
    private String nomeRecebedorNotificacao;

    @XmlElement(name = "documentorecebedor", nillable = true)
    private String documentoRecebedor;

    @XmlElement(name = "datarecebimentonotificacao", nillable = true)
    private Date dataRecebimentoNotificacao;
    
    @XmlElement(name = "nomeproprietario", nillable = true)
    private String nomeProprietario;

    @XmlElement(name = "endereco", nillable = true)
    private String endereco;

    @XmlElement(name = "numeroendereco", nillable = true)
    private String numeroEndereco;

    @XmlElement(name = "complemento", nillable = true)
    private String complemento;

    @XmlElement(name = "bairro", nillable = true)
    private String bairro;

    @XmlElement(name = "nomemunicipio", nillable = true)
    private String nomeMunicipio;

    @XmlElement(name = "ufmunicipio", nillable = true)
    private String ufMunicipio;

    @XmlElement(name = "cep", nillable = true)
    private String cep;

    @XmlElement(name = "numerodocproprietario", nillable = true)
    private String numeroDocProprietario;

    @XmlElement(name = "situacaorecurso", nillable = true)
    private String situacaoRecurso;

    @XmlElement(name = "resultadorecurso", nillable = true)
    private String resultadoRecurso;

    @XmlElement(name = "nomerequerente", nillable = true)
    private String nomeRequerente;

    @XmlElement(name = "dataprotocolorecurso", nillable = true)
    private Date dataProtocoloRecurso;

    @XmlElement(name = "numeroprocessorecurso", nillable = true)
    private String numeroProcessoRecurso;

    @XmlElement(name = "agenteautuador", nillable = true)
    private String agenteAutuador;

    @XmlElement(name = "usuariocadastro", nillable = true)
    private String usuarioCadastro;

    @XmlElement(name = "datacadastramento", nillable = true)
    private Date dataCadastramento;

    @XmlElement(name = "codigoterminalrede", nillable = true)
    private String codigoTerminalRede;

    @XmlElement(name = "datarelatoriodiariooficial", nillable = true)
    private Date dataRelatorioDiarioOficial;

    @XmlElement(name = "observacaocancelamento", nillable = true)
    private String observacaoCancelamento;

    public DadosPABPMSDetalhesAutuacaoInfracaoWrapper() {
    }

    public DadosPABPMSDetalhesAutuacaoInfracaoWrapper(
        String numeroAutoInfracao, String codigoInfracao, String orgaoAutuador, String tipoInfracao, 
            String responsavelPontos, String placa, String marcaModelo, String codMunicipioVeiculo, 
            String ufMunicipioVeiculo, String descricaoInfracao, String valorInfracao, String codigoTipoInfracao, 
            String descricaoTipoInfracao, String competencia, Date dataInfracao,
            Date dataLimiteDefesa, Date dataEmissaoNotAut, Date dataPublicacaoDiarioOficial, 
            String localInfracao, String codMunicipioInfracao, String nomeMunicipioInfracao, 
            String velocidadeConsiderada, String tipoAutuador, String descricaoAutuador, String codigoLocalEletronico, 
            String nomeCondutor, String condutorIdentificado, String cpfCondutor, String pguRegistroCondutor, 
            Long cnhCondutor, String situacaoNotificacao, String codigoRenainfInfracao, String guiaPagamento, 
            String guiaPenalidade, String informacaoExcessoPeso, String descricaoStatusInfracao, 
            Date dataCadastroInfracao, String informacaoProcedentes, String nomeProprietarioCondutor, 
            String pguProprietarioCondutor, String numRegistroProprietarioCondutor, String cpfCnpjProprietarioCondutor, 
            String numeroObjetoNotificacao, String numeroObjetoPenalizacao, String situacaoAtualNotificacaoAR, 
            String nomeRecebedorNotificacao, String documentoRecebedor, Date dataRecebimentoNotificacao, 
            String nomeProprietario, String endereco, String numeroEndereco, String complemento, String bairro, 
            String nomeMunicipio, String ufMunicipio, String cep, String numeroDocProprietario, String situacaoRecurso, 
            String resultadoRecurso, String nomeRequerente, Date dataProtocoloRecurso, String numeroProcessoRecurso, 
            String agenteAutuador, String usuarioCadastro, Date dataCadastramento, 
            String codigoTerminalRede, Date dataRelatorioDiarioOficial, String observacaoCancelamento) {
        
        this.numeroAutoInfracao = numeroAutoInfracao;
        this.codigoInfracao = codigoInfracao;
        this.orgaoAutuador = orgaoAutuador;
        this.tipoInfracao = tipoInfracao;
        this.responsavelPontos = responsavelPontos;
        this.placa = placa;
        this.marcaModelo = marcaModelo;
        this.codMunicipioVeiculo = codMunicipioVeiculo;
        this.ufMunicipioVeiculo = ufMunicipioVeiculo;
        this.descricaoInfracao = descricaoInfracao;
        this.valorInfracao = valorInfracao;
        this.codigoTipoInfracao = codigoTipoInfracao;
        this.descricaoTipoInfracao = descricaoTipoInfracao;
        this.competencia = competencia;
        this.dataInfracao = dataInfracao;
        this.dataLimiteDefesa = dataLimiteDefesa;
        this.dataEmissaoNotAut = dataEmissaoNotAut;
        this.dataPublicacaoDiarioOficial = dataPublicacaoDiarioOficial;
        this.localInfracao = localInfracao;
        this.codMunicipioInfracao = codMunicipioInfracao;
        this.nomeMunicipioInfracao = nomeMunicipioInfracao;
        this.velocidadeConsiderada = velocidadeConsiderada;
        this.tipoAutuador = tipoAutuador;
        this.descricaoAutuador = descricaoAutuador;
        this.codigoLocalEletronico = codigoLocalEletronico;
        this.nomeCondutor = nomeCondutor;
        this.condutorIdentificado = condutorIdentificado;
        this.cpfCondutor = cpfCondutor;
        this.pguRegistroCondutor = pguRegistroCondutor;
        this.cnhCondutor = cnhCondutor;
        this.situacaoNotificacao = situacaoNotificacao;
        this.codigoRenainfInfracao = codigoRenainfInfracao;
        this.guiaPagamento = guiaPagamento;
        this.guiaPenalidade = guiaPenalidade;
        this.informacaoExcessoPeso = informacaoExcessoPeso;
        this.descricaoStatusInfracao = descricaoStatusInfracao;
        this.dataCadastroInfracao = dataCadastroInfracao;
        this.informacaoProcedentes = informacaoProcedentes;
        this.nomeProprietarioCondutor = nomeProprietarioCondutor;
        this.pguProprietarioCondutor = pguProprietarioCondutor;
        this.numRegistroProprietarioCondutor = numRegistroProprietarioCondutor;
        this.cpfCnpjProprietarioCondutor = cpfCnpjProprietarioCondutor;
        this.numeroObjetoNotificacao = numeroObjetoNotificacao;
        this.numeroObjetoPenalizacao = numeroObjetoPenalizacao;
        this.situacaoAtualNotificacaoAR = situacaoAtualNotificacaoAR;
        this.nomeRecebedorNotificacao = nomeRecebedorNotificacao;
        this.documentoRecebedor = documentoRecebedor;
        this.dataRecebimentoNotificacao = dataRecebimentoNotificacao;
        this.nomeProprietario = nomeProprietario;
        this.endereco = endereco;
        this.numeroEndereco = numeroEndereco;
        this.complemento = complemento;
        this.bairro = bairro;
        this.nomeMunicipio = nomeMunicipio;
        this.ufMunicipio = ufMunicipio;
        this.cep = cep;
        this.numeroDocProprietario = numeroDocProprietario;
        this.situacaoRecurso = situacaoRecurso;
        this.resultadoRecurso = resultadoRecurso;
        this.nomeRequerente = nomeRequerente;
        this.dataProtocoloRecurso = dataProtocoloRecurso;
        this.numeroProcessoRecurso = numeroProcessoRecurso;
        this.agenteAutuador = agenteAutuador;
        this.usuarioCadastro = usuarioCadastro;
        this.dataCadastramento = dataCadastramento;
        this.codigoTerminalRede = codigoTerminalRede;
        this.dataRelatorioDiarioOficial = dataRelatorioDiarioOficial;
        this.observacaoCancelamento = observacaoCancelamento;
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

    public String getCodigoInfracao() {
        return codigoInfracao;
    }

    public void setCodigoInfracao(String codigoInfracao) {
        this.codigoInfracao = codigoInfracao;
    }

    public String getOrgaoAutuador() {
        return orgaoAutuador;
    }

    public void setOrgaoAutuador(String orgaoAutuador) {
        this.orgaoAutuador = orgaoAutuador;
    }

    public String getTipoInfracao() {
        return tipoInfracao;
    }

    public void setTipoInfracao(String tipoInfracao) {
        this.tipoInfracao = tipoInfracao;
    }

    public String getResponsavelPontos() {
        return responsavelPontos;
    }

    public void setResponsavelPontos(String responsavelPontos) {
        this.responsavelPontos = responsavelPontos;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarcaModelo() {
        return marcaModelo;
    }

    public void setMarcaModelo(String marcaModelo) {
        this.marcaModelo = marcaModelo;
    }

    public String getCodMunicipioVeiculo() {
        return codMunicipioVeiculo;
    }

    public void setCodMunicipioVeiculo(String codMunicipioVeiculo) {
        this.codMunicipioVeiculo = codMunicipioVeiculo;
    }

    public String getUfMunicipioVeiculo() {
        return ufMunicipioVeiculo;
    }

    public void setUfMunicipioVeiculo(String ufMunicipioVeiculo) {
        this.ufMunicipioVeiculo = ufMunicipioVeiculo;
    }

    public String getDescricaoInfracao() {
        return descricaoInfracao;
    }

    public void setDescricaoInfracao(String descricaoInfracao) {
        this.descricaoInfracao = descricaoInfracao;
    }

    public String getValorInfracao() {
        return valorInfracao;
    }

    public void setValorInfracao(String valorInfracao) {
        this.valorInfracao = valorInfracao;
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

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }

    public Date getDataInfracao() {
        return dataInfracao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataInfracao(Date dataInfracao) {
        this.dataInfracao = dataInfracao;
    }

    public Date getDataLimiteDefesa() {
        return dataLimiteDefesa;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataLimiteDefesa(Date dataLimiteDefesa) {
        this.dataLimiteDefesa = dataLimiteDefesa;
    }

    public Date getDataEmissaoNotAut() {
        return dataEmissaoNotAut;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataEmissaoNotAut(Date dataEmissaoNotAut) {
        this.dataEmissaoNotAut = dataEmissaoNotAut;
    }

    public Date getDataPublicacaoDiarioOficial() {
        return dataPublicacaoDiarioOficial;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataPublicacaoDiarioOficial(Date dataPublicacaoDiarioOficial) {
        this.dataPublicacaoDiarioOficial = dataPublicacaoDiarioOficial;
    }

    public String getLocalInfracao() {
        return localInfracao;
    }

    public void setLocalInfracao(String localInfracao) {
        this.localInfracao = localInfracao;
    }

    public String getCodMunicipioInfracao() {
        return codMunicipioInfracao;
    }

    public void setCodMunicipioInfracao(String codMunicipioInfracao) {
        this.codMunicipioInfracao = codMunicipioInfracao;
    }

    public String getNomeMunicipioInfracao() {
        return nomeMunicipioInfracao;
    }

    public void setNomeMunicipioInfracao(String nomeMunicipioInfracao) {
        this.nomeMunicipioInfracao = nomeMunicipioInfracao;
    }

    public String getVelocidadeConsiderada() {
        return velocidadeConsiderada;
    }

    public void setVelocidadeConsiderada(String velocidadeConsiderada) {
        this.velocidadeConsiderada = velocidadeConsiderada;
    }

    public String getTipoAutuador() {
        return tipoAutuador;
    }

    public void setTipoAutuador(String tipoAutuador) {
        this.tipoAutuador = tipoAutuador;
    }

    public String getDescricaoAutuador() {
        return descricaoAutuador;
    }

    public void setDescricaoAutuador(String descricaoAutuador) {
        this.descricaoAutuador = descricaoAutuador;
    }

    public String getCodigoLocalEletronico() {
        return codigoLocalEletronico;
    }

    public void setCodigoLocalEletronico(String codigoLocalEletronico) {
        this.codigoLocalEletronico = codigoLocalEletronico;
    }

    public String getNomeCondutor() {
        return nomeCondutor;
    }

    public void setNomeCondutor(String nomeCondutor) {
        this.nomeCondutor = nomeCondutor;
    }

    public String getCondutorIdentificado() {
        return condutorIdentificado;
    }

    public void setCondutorIdentificado(String condutorIdentificado) {
        this.condutorIdentificado = condutorIdentificado;
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

    public String getSituacaoNotificacao() {
        return situacaoNotificacao;
    }

    public void setSituacaoNotificacao(String situacaoNotificacao) {
        this.situacaoNotificacao = situacaoNotificacao;
    }

    public String getCodigoRenainfInfracao() {
        return codigoRenainfInfracao;
    }

    public void setCodigoRenainfInfracao(String codigoRenainfInfracao) {
        this.codigoRenainfInfracao = codigoRenainfInfracao;
    }

    public String getGuiaPagamento() {
        return guiaPagamento;
    }

    public void setGuiaPagamento(String guiaPagamento) {
        this.guiaPagamento = guiaPagamento;
    }

    public String getGuiaPenalidade() {
        return guiaPenalidade;
    }

    public void setGuiaPenalidade(String guiaPenalidade) {
        this.guiaPenalidade = guiaPenalidade;
    }

    public String getInformacaoExcessoPeso() {
        return informacaoExcessoPeso;
    }

    public void setInformacaoExcessoPeso(String informacaoExcessoPeso) {
        this.informacaoExcessoPeso = informacaoExcessoPeso;
    }

    public String getDescricaoStatusInfracao() {
        return descricaoStatusInfracao;
    }

    public void setDescricaoStatusInfracao(String descricaoStatusInfracao) {
        this.descricaoStatusInfracao = descricaoStatusInfracao;
    }

    public Date getDataCadastroInfracao() {
        return dataCadastroInfracao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataCadastroInfracao(Date dataCadastroInfracao) {
        this.dataCadastroInfracao = dataCadastroInfracao;
    }

    public String getInformacaoProcedentes() {
        return informacaoProcedentes;
    }

    public void setInformacaoProcedentes(String informacaoProcedentes) {
        this.informacaoProcedentes = informacaoProcedentes;
    }

    public String getNomeProprietarioCondutor() {
        return nomeProprietarioCondutor;
    }

    public void setNomeProprietarioCondutor(String nomeProprietarioCondutor) {
        this.nomeProprietarioCondutor = nomeProprietarioCondutor;
    }

    public String getPguProprietarioCondutor() {
        return pguProprietarioCondutor;
    }

    public void setPguProprietarioCondutor(String pguProprietarioCondutor) {
        this.pguProprietarioCondutor = pguProprietarioCondutor;
    }

    public String getNumRegistroProprietarioCondutor() {
        return numRegistroProprietarioCondutor;
    }

    public void setNumRegistroProprietarioCondutor(String numRegistroProprietarioCondutor) {
        this.numRegistroProprietarioCondutor = numRegistroProprietarioCondutor;
    }

    public String getCpfCnpjProprietarioCondutor() {
        return cpfCnpjProprietarioCondutor;
    }

    public void setCpfCnpjProprietarioCondutor(String cpfCnpjProprietarioCondutor) {
        this.cpfCnpjProprietarioCondutor = cpfCnpjProprietarioCondutor;
    }

    public String getNumeroObjetoNotificacao() {
        return numeroObjetoNotificacao;
    }

    public void setNumeroObjetoNotificacao(String numeroObjetoNotificacao) {
        this.numeroObjetoNotificacao = numeroObjetoNotificacao;
    }

    public String getNumeroObjetoPenalizacao() {
        return numeroObjetoPenalizacao;
    }

    public void setNumeroObjetoPenalizacao(String numeroObjetoPenalizacao) {
        this.numeroObjetoPenalizacao = numeroObjetoPenalizacao;
    }

    public String getSituacaoAtualNotificacaoAR() {
        return situacaoAtualNotificacaoAR;
    }

    public void setSituacaoAtualNotificacaoAR(String situacaoAtualNotificacaoAR) {
        this.situacaoAtualNotificacaoAR = situacaoAtualNotificacaoAR;
    }

    public String getNomeRecebedorNotificacao() {
        return nomeRecebedorNotificacao;
    }

    public void setNomeRecebedorNotificacao(String nomeRecebedorNotificacao) {
        this.nomeRecebedorNotificacao = nomeRecebedorNotificacao;
    }

    public String getDocumentoRecebedor() {
        return documentoRecebedor;
    }

    public void setDocumentoRecebedor(String documentoRecebedor) {
        this.documentoRecebedor = documentoRecebedor;
    }

    public Date getDataRecebimentoNotificacao() {
        return dataRecebimentoNotificacao;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataRecebimentoNotificacao(Date dataRecebimentoNotificacao) {
        this.dataRecebimentoNotificacao = dataRecebimentoNotificacao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getNomeMunicipio() {
        return nomeMunicipio;
    }

    public void setNomeMunicipio(String nomeMunicipio) {
        this.nomeMunicipio = nomeMunicipio;
    }

    public String getUfMunicipio() {
        return ufMunicipio;
    }

    public void setUfMunicipio(String ufMunicipio) {
        this.ufMunicipio = ufMunicipio;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumeroDocProprietario() {
        return numeroDocProprietario;
    }

    public void setNumeroDocProprietario(String numeroDocProprietario) {
        this.numeroDocProprietario = numeroDocProprietario;
    }

    public String getSituacaoRecurso() {
        return situacaoRecurso;
    }

    public void setSituacaoRecurso(String situacaoRecurso) {
        this.situacaoRecurso = situacaoRecurso;
    }

    public String getResultadoRecurso() {
        return resultadoRecurso;
    }

    public void setResultadoRecurso(String resultadoRecurso) {
        this.resultadoRecurso = resultadoRecurso;
    }

    public String getNomeRequerente() {
        return nomeRequerente;
    }

    public void setNomeRequerente(String nomeRequerente) {
        this.nomeRequerente = nomeRequerente;
    }

    public Date getDataProtocoloRecurso() {
        return dataProtocoloRecurso;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataProtocoloRecurso(Date dataProtocoloRecurso) {
        this.dataProtocoloRecurso = dataProtocoloRecurso;
    }

    public String getNumeroProcessoRecurso() {
        return numeroProcessoRecurso;
    }

    public void setNumeroProcessoRecurso(String numeroProcessoRecurso) {
        this.numeroProcessoRecurso = numeroProcessoRecurso;
    }

    public String getAgenteAutuador() {
        return agenteAutuador;
    }

    public void setAgenteAutuador(String agenteAutuador) {
        this.agenteAutuador = agenteAutuador;
    }

    public String getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(String usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public Date getDataCadastramento() {
        return dataCadastramento;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataCadastramento(Date dataCadastramento) {
        this.dataCadastramento = dataCadastramento;
    }

    public String getCodigoTerminalRede() {
        return codigoTerminalRede;
    }

    public void setCodigoTerminalRede(String codigoTerminalRede) {
        this.codigoTerminalRede = codigoTerminalRede;
    }

    public Date getDataRelatorioDiarioOficial() {
        return dataRelatorioDiarioOficial;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataRelatorioDiarioOficial(Date dataRelatorioDiarioOficial) {
        this.dataRelatorioDiarioOficial = dataRelatorioDiarioOficial;
    }

    public String getObservacaoCancelamento() {
        return observacaoCancelamento;
    }

    public void setObservacaoCancelamento(String observacaoCancelamento) {
        this.observacaoCancelamento = observacaoCancelamento;
    }

    public String getNomeProprietario() {
        return nomeProprietario;
    }

    public void setNomeProprietario(String nomeProprietario) {
        this.nomeProprietario = nomeProprietario;
    }
}