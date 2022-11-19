package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.core.projeto.wrapper.apo.NotificacaoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.DesistenciaRecursoInstauracaoPenalizacaoWS;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.List;

/**
 * @author Lillydi
 */
public class ConsultaPaWSWrapper implements Serializable {

    @XmlElement(name = "geral", nillable = true)
    private ConsultaGeralPAWrapper geral;

    @XmlElement(name = "instauracao", nillable = true)
    private PABPMSWrapper instauracao;

    @XmlElement(name = "notificacoes", nillable = true)
    private List<NotificacaoWrapper> notificacoes;
    
    @XmlElement(name = "recursos", nillable = true)
    private List<RecursosWrapper> recursos;
    
    @XmlElement(name = "recursoscancelados", nillable = true)
    private List<RecursoCanceladoRetornoWSWrapper> recursosCancelados;
    
    @XmlElement(name = "bloqueiosbca", nillable = true)
    private List<BloqueioBCAWrapper> bloqueiosBca;
    
    @XmlElement(name = "entregacnh", nillable = true)
    private List<InformacoesEntregaCnhWrapper> entregaCnh;
    
    @XmlElement(name = "informacaoprova", nillable = true)
    private InformacaoProvaWrapper informacaoProva;
    
    @XmlElement(name = "movimentacoes", nillable = true)
    private List<MovimentacaoPaWrapper> movimentacoes;
    
    @XmlElement(name = "desistenciarecursoinstauracaopenalizacao", nillable = true)
    private DesistenciaRecursoInstauracaoPenalizacaoWS desistenciaRecursoInstauracaoPenalizacao;
    
    @XmlElement(name = "desistente", nillable = true)
    ProcessoAdministrativoDesistenteRetornoWSWrapper desistente;
    
    public ConsultaPaWSWrapper() {
    }

    public ConsultaGeralPAWrapper getGeral() {
        return geral;
    }

    public void setGeral(ConsultaGeralPAWrapper geral) {
        this.geral = geral;
    }

    public PABPMSWrapper getInstauracao() {
        return instauracao;
    }

    public void setInstauracao(PABPMSWrapper instauracao) {
        this.instauracao = instauracao;
    }

    public List<NotificacaoWrapper> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(List<NotificacaoWrapper> notificacoes) {
        this.notificacoes = notificacoes;
    }
    
    public List<RecursosWrapper> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursosWrapper> recursos) {
        this.recursos = recursos;
    }

    public List<RecursoCanceladoRetornoWSWrapper> getRecursosCancelados() {
        return recursosCancelados;
    }

    public void setRecursosCancelados(List<RecursoCanceladoRetornoWSWrapper> recursosCancelados) {
        this.recursosCancelados = recursosCancelados;
    }

    public List<BloqueioBCAWrapper> getBloqueiosBca() {
        return bloqueiosBca;
    }

    public void setBloqueiosBca(List<BloqueioBCAWrapper> bloqueiosBca) {
        this.bloqueiosBca = bloqueiosBca;
    }    

    public List<InformacoesEntregaCnhWrapper> getEntregaCnh() {
        return entregaCnh;
    }

    public void setEntregaCnh(List<InformacoesEntregaCnhWrapper> entregaCnh) {
        this.entregaCnh = entregaCnh;
    }

    public InformacaoProvaWrapper getInformacaoProva() {
        return informacaoProva;
    }

    public void setInformacaoProva(InformacaoProvaWrapper informacaoProva) {
        this.informacaoProva = informacaoProva;
    }

    public List<MovimentacaoPaWrapper> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<MovimentacaoPaWrapper> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }

    public DesistenciaRecursoInstauracaoPenalizacaoWS getDesistenciaRecursoInstauracaoPenalizacao() {
        return desistenciaRecursoInstauracaoPenalizacao;
    }

    public void setDesistenciaRecursoInstauracaoPenalizacao(DesistenciaRecursoInstauracaoPenalizacaoWS desistenciaRecursoInstauracaoPenalizacao) {
        this.desistenciaRecursoInstauracaoPenalizacao = desistenciaRecursoInstauracaoPenalizacao;
    }

    public ProcessoAdministrativoDesistenteRetornoWSWrapper getDesistente() {
        return desistente;
    }

    public void setDesistente(ProcessoAdministrativoDesistenteRetornoWSWrapper desistente) {
        this.desistente = desistente;
    }
}