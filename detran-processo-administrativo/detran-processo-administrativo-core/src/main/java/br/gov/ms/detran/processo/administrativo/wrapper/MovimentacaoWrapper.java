package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.Movimentacao;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;

@EntityMapping2(entity = {Movimentacao.class})
@XmlRootElement
public class MovimentacaoWrapper implements IBaseEntity, IEntityResource<Movimentacao> {
    
    private Movimentacao entidade;
    
    private String numeroProcesso;
    
    private ProcessoAdministrativo processoAdministrativo;
    
    private String nomeCondutor;
    
    private String andamentoAtual;

    private List<ProcessoAdministrativoInfracaoWrapper> listaInfracoes;

    private String usuarioMovimentacao;

    private String andamentoAnterior;

    private String motivoLabel;

    private String indicativoReativacaoPontuacao;

    private byte[] relatorio;

    /*
   Usado na parte de cancelamento de um recurso online em backoffice.
    */
    private RecursoOnlineCanceladoWrapper recursoCanceladoWrapper;

    public MovimentacaoWrapper() {

    }

    public MovimentacaoWrapper(Movimentacao entidade, ProcessoAdministrativo processo) {
        this.entidade = entidade;
        this.processoAdministrativo = processo;
    }
    
    @Override
    public Long getId() {
        return entidade != null ? entidade.getId() : null;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {
        if (this.entidade == null) {
            this.entidade = new Movimentacao();
        }
        this.entidade.setId(id);
    }

    @Override
    public Movimentacao getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(Movimentacao entidade) {
        this.entidade = entidade;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public String getNomeCondutor() {
        return nomeCondutor;
    }

    public void setNomeCondutor(String nomeCondutor) {
        this.nomeCondutor = nomeCondutor;
    }
    
    public String getAndamentoAtual() {
        return andamentoAtual;
    }

    public void setAndamentoAtual(String andamentoAtual) {
        this.andamentoAtual = andamentoAtual;
    }

    public List<ProcessoAdministrativoInfracaoWrapper> getListaInfracoes() {
        return listaInfracoes;
    }

    public void setListaInfracoes(List<ProcessoAdministrativoInfracaoWrapper> listaInfracoes) {
        this.listaInfracoes = listaInfracoes;
    }
    
    public String getUsuarioMovimentacao() {
        return usuarioMovimentacao;
    }

    public void setUsuarioMovimentacao(String usuarioMovimentacao) {
        this.usuarioMovimentacao = usuarioMovimentacao;
    }

    public String getAndamentoAnterior() {
        return andamentoAnterior;
    }

    public void setAndamentoAnterior(String andamentoAnterior) {
        this.andamentoAnterior = andamentoAnterior;
    }

    public String getMotivoLabel() {
        motivoLabel = "";
        
        if (null != this.getEntidade() && null != this.getEntidade().getMotivo())
            motivoLabel = this.getEntidade().getMotivo().toString();
            
        return motivoLabel;
    }

    public void setMotivoLabel(String motivoLabel) {
        this.motivoLabel = motivoLabel;
    }
    
     public String getTipoLabel() {

        if (null != this.entidade 
                && this.entidade.getProcessoAdministrativo() != null
                && null != this.entidade.getProcessoAdministrativo().getTipo()) {
            return this.entidade.getProcessoAdministrativo().getTipo().toString();
        }

        return "";
    }

    public void setTipoLabel(String tipoLabel) {

    }

    public byte[] getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(byte[] relatorio) {
        this.relatorio = relatorio;
    }

    public RecursoOnlineCanceladoWrapper getRecursoCanceladoWrapper() {
        return recursoCanceladoWrapper;
    }

    public void setRecursoCanceladoWrapper(RecursoOnlineCanceladoWrapper recursoCanceladoWrapper) {
        this.recursoCanceladoWrapper = recursoCanceladoWrapper;
    }

    public String getIndicativoReativacaoPontuacao() {
        return indicativoReativacaoPontuacao;
    }

    public void setIndicativoReativacaoPontuacao(String indicativoReativacaoPontuacao) {
        this.indicativoReativacaoPontuacao = indicativoReativacaoPontuacao;
    }
}