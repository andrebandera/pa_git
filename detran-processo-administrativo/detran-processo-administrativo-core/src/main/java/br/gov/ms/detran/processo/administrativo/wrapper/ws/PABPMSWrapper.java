package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;

public class PABPMSWrapper {
    
    @XmlElement(name = "identificadorprocessoadministrativo", nillable = true)
    private Long idProcessoAdministrativo;
    
    @XmlElement(name = "dadosgeral", nillable = true)
    private DadosPABPMSGeralWrapper dadosGeral;
    
    @XmlElement(name = "dadospa", nillable = true)
    private DadosPABPMSWrapper dadosPA;
    
    @XmlElement(name = "dadosinfracao", nillable = true)
    private List<DadosPABPMSInfracaoWrapper> dadosInfracao;
    
    @XmlElement(name = "dadoscondutor", nillable = true)
    private DadosPABPMSCondutorWrapper dadosCondutor;
    
    @XmlElement(name = "dadospontuacao", nillable = true)
    private List<DadosPABPMSDadosPontuacaoWrapper> dadosPontuacao;
    
    @XmlElement(name = "dadoshistoricopontuacaonome", nillable = true)
    private List<DadosPABPMSHistoricoPontuacaoNomeWrapper> dadosHistoricoPontuacaoNome;
    
    @XmlElement(name = "dadosrecursopontuacao", nillable = true)
    private List<DadosPABPMSRecursosInfracaoWrapper> dadosRecursoPontuacao;
    
    @XmlElement(name = "dadosinfracaolocal", nillable = true)
    private List<DadosPABPMSInfracaoLocalWrapper> dadosInfracaoLocal;
    
    @XmlElement(name = "dadosinfracaorenainf", nillable = true)
    private List<DadosPABPMSInfracaoRenainfWrapper> dadosInfracaoRenainf;
    
    @XmlElement(name = "dadosrenavam", nillable = true)
    private List<DadosPABPMSRenavamWrapper> dadosRenavam;
    
    @XmlElement(name = "dadosdetalhesautuacaoinfracao", nillable = true)
    private List<DadosPABPMSDetalhesAutuacaoInfracaoWrapper> dadosDetalhesAutuacaoInfracao;
    
    @XmlElement(name = "artigodespacho", nillable = true)
    private DadosPABPMSArtigoDespachoWrapper artigoDespacho;
    
    @XmlElement(name = "dadosjuridicos", nillable = true)
    private DadosPABPMSJuridicoWrapper dadosJuridicos;
    
    @XmlElement(name = "erro", nillable = true)
    private DadosPABPMSErroWrapper erro;
    
    public DadosPABPMSGeralWrapper getDadosGeral() {
        return dadosGeral;
    }

    public void setDadosGeral(DadosPABPMSGeralWrapper dadosGeral) {
        this.dadosGeral = dadosGeral;
    }

    public DadosPABPMSWrapper getDadosPA() {
        return dadosPA;
    }

    public void setDadosPA(DadosPABPMSWrapper dadosPA) {
        this.dadosPA = dadosPA;
    }

    public List<DadosPABPMSRenavamWrapper> getDadosRenavam() {
        return dadosRenavam;
    }

    public void setDadosRenavam(List<DadosPABPMSRenavamWrapper> dadosRenavam) {
        this.dadosRenavam = dadosRenavam;
    }

    public List<DadosPABPMSInfracaoWrapper> getDadosInfracao() {
        return dadosInfracao;
    }

    public void setDadosInfracao(List<DadosPABPMSInfracaoWrapper> dadosInfracao) {
        this.dadosInfracao = dadosInfracao;
    }

    public List<DadosPABPMSDadosPontuacaoWrapper> getDadosPontuacao() {
        return dadosPontuacao;
    }

    public void setDadosPontuacao(List<DadosPABPMSDadosPontuacaoWrapper> dadosPontuacao) {
        this.dadosPontuacao = dadosPontuacao;
    }

    public DadosPABPMSCondutorWrapper getDadosCondutor() {
        return dadosCondutor;
    }

    public void setDadosCondutor(DadosPABPMSCondutorWrapper dadosCondutor) {
        this.dadosCondutor = dadosCondutor;
    }

    public List<DadosPABPMSHistoricoPontuacaoNomeWrapper> getDadosHistoricoPontuacaoNome() {
        return dadosHistoricoPontuacaoNome;
    }

    public void setDadosHistoricoPontuacaoNome(List<DadosPABPMSHistoricoPontuacaoNomeWrapper> dadosHistoricoPontuacaoNome) {
        this.dadosHistoricoPontuacaoNome = dadosHistoricoPontuacaoNome;
    }

    public List<DadosPABPMSRecursosInfracaoWrapper> getDadosRecursoPontuacao() {
        return dadosRecursoPontuacao;
    }

    public void setDadosRecursoPontuacao(List<DadosPABPMSRecursosInfracaoWrapper> dadosRecursoPontuacao) {
        this.dadosRecursoPontuacao = dadosRecursoPontuacao;
    }

    public List<DadosPABPMSInfracaoRenainfWrapper> getDadosInfracaoRenainf() {
        return dadosInfracaoRenainf;
    }

    public void setDadosInfracaoRenainf(List<DadosPABPMSInfracaoRenainfWrapper> dadosInfracaoRenainf) {
        this.dadosInfracaoRenainf = dadosInfracaoRenainf;
    }

    public List<DadosPABPMSInfracaoLocalWrapper> getDadosInfracaoLocal() {
        return dadosInfracaoLocal;
    }

    public void setDadosInfracaoLocal(List<DadosPABPMSInfracaoLocalWrapper> dadosInfracaoLocal) {
        this.dadosInfracaoLocal = dadosInfracaoLocal;
    }
    
    public DadosPABPMSArtigoDespachoWrapper getArtigoDespacho() {
        return artigoDespacho;
    }

    public void setArtigoDespacho(DadosPABPMSArtigoDespachoWrapper artigoDespacho) {
        this.artigoDespacho = artigoDespacho;
    }

    public List<DadosPABPMSDetalhesAutuacaoInfracaoWrapper> getDadosDetalhesAutuacaoInfracao() {
        return dadosDetalhesAutuacaoInfracao;
    }

    public void setDadosDetalhesAutuacaoInfracao(List<DadosPABPMSDetalhesAutuacaoInfracaoWrapper> dadosDetalhesAutuacaoInfracao) {
        this.dadosDetalhesAutuacaoInfracao = dadosDetalhesAutuacaoInfracao;
    }

    public DadosPABPMSErroWrapper getErro() {
        return erro;
    }

    public void setErro(DadosPABPMSErroWrapper erro) {
        this.erro = erro;
    }

    public Long getIdProcessoAdministrativo() {
        return idProcessoAdministrativo;
    }

    public void setIdProcessoAdministrativo(Long idProcessoAdministrativo) {
        this.idProcessoAdministrativo = idProcessoAdministrativo;
    }

    public DadosPABPMSJuridicoWrapper getDadosJuridicos() {
        return dadosJuridicos;
    }

    public void setDadosJuridicos(DadosPABPMSJuridicoWrapper dadosJuridicos) {
        this.dadosJuridicos = dadosJuridicos;
    }
}