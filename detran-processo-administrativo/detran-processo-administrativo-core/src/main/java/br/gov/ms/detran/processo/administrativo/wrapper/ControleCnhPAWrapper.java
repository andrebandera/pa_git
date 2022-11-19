package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.core.projeto.wrapper.hab.CnhSituacaoEntregaWrapper;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoBloqueioBCA;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

/**
 * @author Lillydi
 */
public class ControleCnhPAWrapper extends CnhSituacaoEntregaWrapper{

    private MovimentoBloqueioBCA movimentoBloqueioBca;
    
    private String nomeUsuarioAcao;
    
    private Boolean fluxoPrincipal;
    
    private Long numeroDetranCondutor;
    
    private List<PAControleCnhWrapper> processosAguardandoEntrega;
    
    private List<PAControleCnhWrapper> processosJuridicosEntrega;
    
    private List<PAControleCnhWrapper> processosParaDesistencia;
    
    private List<PAControleCnhWrapper> processosParaDesistenciaSelecionados;
    
    private List<PAControleCnhWrapper> processosParaDesistenciaComRecurso;
    
    private List<PAControleCnhWrapper> processosParaDesistenciaRecursoSelecionados;
    
    private List<PAControleCnhWrapper> processosJuridicosOutrosAndamentos;
    
    private List<PAControleCnhWrapper> processosJuridicosOutrosAndamentosSelecionados;
    
    /*
    Variáveis para controle no FRONT-END
    */
    private Date dataFimPenalidade;

    private String tempoPenalidade;

    private String tempoPenalidadePJU;

    private String pidInformada;
    
    private Date dataInicioPenalidade;

    /*
    Usado na consulta PA - aba controle CNH
    */
    private MovimentoCnh movimento;

    /*
    Usado na parte de cancelamento de um recurso online em backoffice.
     */
    private RecursoOnlineCanceladoWrapper recursoCanceladoWrapper;

    public ControleCnhPAWrapper() {
        super();
    }

    public MovimentoBloqueioBCA getMovimentoBloqueioBca() {
        return movimentoBloqueioBca;
    }

    public void setMovimentoBloqueioBca(MovimentoBloqueioBCA movimentoBloqueioBca) {
        this.movimentoBloqueioBca = movimentoBloqueioBca;
    }

    public String getNomeUsuarioAcao() {
        return nomeUsuarioAcao;
    }

    public void setNomeUsuarioAcao(String nomeUsuarioAcao) {
        this.nomeUsuarioAcao = nomeUsuarioAcao;
    }

    public Boolean getFluxoPrincipal() {
        return fluxoPrincipal;
    }

    public void setFluxoPrincipal(Boolean fluxoPrincipal) {
        this.fluxoPrincipal = fluxoPrincipal;
    }

    public String getTempoPenalidade() {
        return tempoPenalidade;
    }

    public void setTempoPenalidade(String tempoPenalidade) {
        this.tempoPenalidade = tempoPenalidade;
    }

    public Long getNumeroDetranCondutor() {
        return numeroDetranCondutor;
    }

    public void setNumeroDetranCondutor(Long numeroDetranCondutor) {
        this.numeroDetranCondutor = numeroDetranCondutor;
    }

    public List<PAControleCnhWrapper> getProcessosAguardandoEntrega() {
        return processosAguardandoEntrega;
    }

    public void setProcessosAguardandoEntrega(List<PAControleCnhWrapper> processosAguardandoEntrega) {
        this.processosAguardandoEntrega = processosAguardandoEntrega;
    }

    public List<PAControleCnhWrapper> getProcessosParaDesistencia() {
        return processosParaDesistencia;
    }

    public void setProcessosParaDesistencia(List<PAControleCnhWrapper> processosParaDesistencia) {
        this.processosParaDesistencia = processosParaDesistencia;
    }

    public List<PAControleCnhWrapper> getProcessosParaDesistenciaComRecurso() {
        return processosParaDesistenciaComRecurso;
    }

    public void setProcessosParaDesistenciaComRecurso(List<PAControleCnhWrapper> processosParaDesistenciaComRecurso) {
        this.processosParaDesistenciaComRecurso = processosParaDesistenciaComRecurso;
    }

    public List<PAControleCnhWrapper> getProcessosParaDesistenciaSelecionados() {
        return processosParaDesistenciaSelecionados;
    }

    public void setProcessosParaDesistenciaSelecionados(List<PAControleCnhWrapper> processosParaDesistenciaSelecionados) {
        this.processosParaDesistenciaSelecionados = processosParaDesistenciaSelecionados;
    }

    public MovimentoCnh getMovimento() {
        return movimento;
    }

    public void setMovimento(MovimentoCnh movimento) {
        this.movimento = movimento;
    }
 
    public Date getDataFimPenalidade() {
        return dataFimPenalidade;
    }

    @XmlElement(name = "dataFimPenalidade")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataFimPenalidade(Date dataFimPenalidade) {
        this.dataFimPenalidade = dataFimPenalidade;
    }

    public List<PAControleCnhWrapper> getProcessosParaDesistenciaRecursoSelecionados() {
        return processosParaDesistenciaRecursoSelecionados;
    }

    public void setProcessosParaDesistenciaRecursoSelecionados(List<PAControleCnhWrapper> processosParaDesistenciaRecursoSelecionados) {
        this.processosParaDesistenciaRecursoSelecionados = processosParaDesistenciaRecursoSelecionados;
    }

    public List<PAControleCnhWrapper> getProcessosJuridicosEntrega() {
        return processosJuridicosEntrega;
    }

    public void setProcessosJuridicosEntrega(List<PAControleCnhWrapper> processosJuridicosEntrega) {
        this.processosJuridicosEntrega = processosJuridicosEntrega;
    }

    public List<PAControleCnhWrapper> getProcessosJuridicosOutrosAndamentos() {
        return processosJuridicosOutrosAndamentos;
    }

    public void setProcessosJuridicosOutrosAndamentos(List<PAControleCnhWrapper> processosJuridicosOutrosAndamentos) {
        this.processosJuridicosOutrosAndamentos = processosJuridicosOutrosAndamentos;
    }

    public String getTempoPenalidadePJU() {
        return tempoPenalidadePJU;
    }

    public void setTempoPenalidadePJU(String tempoPenalidadePJU) {
        this.tempoPenalidadePJU = tempoPenalidadePJU;
    }

    public List<PAControleCnhWrapper> getProcessosJuridicosOutrosAndamentosSelecionados() {
        return processosJuridicosOutrosAndamentosSelecionados;
    }

    public void setProcessosJuridicosOutrosAndamentosSelecionados(List<PAControleCnhWrapper> processosJuridicosOutrosAndamentosSelecionados) {
        this.processosJuridicosOutrosAndamentosSelecionados = processosJuridicosOutrosAndamentosSelecionados;
    }

    public String getPidInformada() {
        return pidInformada;
    }

    public void setPidInformada(String pidInformada) {
        this.pidInformada = pidInformada;
    }

    public RecursoOnlineCanceladoWrapper getRecursoCanceladoWrapper() {
        return recursoCanceladoWrapper;
    }

    public void setRecursoCanceladoWrapper(RecursoOnlineCanceladoWrapper recursoCanceladoWrapper) {
        this.recursoCanceladoWrapper = recursoCanceladoWrapper;
    }
    
    public Date getDataInicioPenalidade() {
        return dataInicioPenalidade;
    }

    @XmlElement(name = "dataInicioPenalidade")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataInicioPenalidade(Date dataInicioPenalidade) {
        this.dataInicioPenalidade = dataInicioPenalidade;
    }
}