package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.entidadesistema.wrapper.BaseEntityWrapper;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.projeto.util.ListSelectItem;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@EntityMapping2(entity = {ProcessoAdministrativo.class})
@XmlRootElement
public class AjustaProcessoAdministrativoWrapper extends BaseEntityWrapper implements IBaseEntity, IEntityResource<ProcessoAdministrativo> {

    private ProcessoAdministrativo entidade;
    
    private String numeroProcesso;

    private PAAndamentoProcesso andamento;
    
    private Integer quantidadeLote;
    
    private String acaoFuncionalidadeAjustaProcessoAdministrativo;
    
    private String codigoDescricaoAndamentoAtual;
    
    private String codigoDescricaoFluxoProcessoAtual;
    
    private PAAndamentoProcesso novoAndamento;
    
    private PAFluxoProcesso novoFluxoProcesso;
    
    private TipoProcessoEnum tipoProcesso;
    
    private ListSelectItem listaFluxo;

    public PAAndamentoProcesso getNovoAndamento() {
        return novoAndamento;
    }

    public void setNovoAndamento(PAAndamentoProcesso novoAndamento) {
        this.novoAndamento = novoAndamento;
    }

    public PAFluxoProcesso getNovoFluxoProcesso() {
        return novoFluxoProcesso;
    }

    public void setNovoFluxoProcesso(PAFluxoProcesso novoFluxoProcesso) {
        this.novoFluxoProcesso = novoFluxoProcesso;
    }

    public AjustaProcessoAdministrativoWrapper() {
    }

    public AjustaProcessoAdministrativoWrapper(ProcessoAdministrativo entidade) {
        this.entidade = entidade;
    }

    @Override
    public Long getId() {
        return entidade != null ? entidade.getId() : null;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {
        if (this.entidade == null) {
            this.entidade = new ProcessoAdministrativo();
        }
        this.entidade.setId(id);
    }

    @Override
    public ProcessoAdministrativo getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(ProcessoAdministrativo entidade) {
        this.entidade = entidade;
    }

    public Integer getQuantidadeLote() {
        return quantidadeLote;
    }

    public void setQuantidadeLote(Integer quantidadeLote) {
        this.quantidadeLote = quantidadeLote;
    }

    public String getAcaoFuncionalidadeAjustaProcessoAdministrativo() {
        return acaoFuncionalidadeAjustaProcessoAdministrativo;
    }

    public void setAcaoFuncionalidadeAjustaProcessoAdministrativo(String acaoFuncionalidadeAjustaProcessoAdministrativo) {
        this.acaoFuncionalidadeAjustaProcessoAdministrativo = acaoFuncionalidadeAjustaProcessoAdministrativo;
    }

    public PAAndamentoProcesso getAndamento() {
        return andamento;
    }

    public void setAndamento(PAAndamentoProcesso andamento) {
        this.andamento = andamento;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getCodigoDescricaoAndamentoAtual() {
        return codigoDescricaoAndamentoAtual;
    }

    public void setCodigoDescricaoAndamentoAtual(String codigoDescricaoAndamentoAtual) {
        this.codigoDescricaoAndamentoAtual = codigoDescricaoAndamentoAtual;
    }

    public String getCodigoDescricaoFluxoProcessoAtual() {
        return codigoDescricaoFluxoProcessoAtual;
    }

    public void setCodigoDescricaoFluxoProcessoAtual(String codigoDescricaoFluxoProcessoAtual) {
        this.codigoDescricaoFluxoProcessoAtual = codigoDescricaoFluxoProcessoAtual;
    }

    public TipoProcessoEnum getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcessoEnum tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public ListSelectItem getListaFluxo() {
        return listaFluxo;
    }

    public void setListaFluxo(ListSelectItem listaFluxo) {
        this.listaFluxo = listaFluxo;
    }
    
}