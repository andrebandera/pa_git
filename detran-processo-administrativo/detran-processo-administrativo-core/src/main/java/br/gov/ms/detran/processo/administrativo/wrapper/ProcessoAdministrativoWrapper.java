package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.entidade.enums.apo.UnidadePenalEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.comum.util.adapter.NumeroAnoAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.PAComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoOcorrenciaEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.InformacaoProvaWrapper;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author yanko.campos
 */
@EntityMapping2(entity = {ProcessoAdministrativo.class})
@XmlRootElement
public class ProcessoAdministrativoWrapper implements IBaseEntity, IEntityResource<ProcessoAdministrativo> {

    private ProcessoAdministrativo entidade;

    private Integer codigoAndamento;
    
    private Integer status;

    private String tipoLabel;

    private String situacaoLabel;
    
    private String descricaoAndamento;
    
    private String nomeCondutor;
    
    private byte[] relatorio;
    
    private String fluxoProcessoDescricao;
    
    private SituacaoOcorrenciaEnum situacaoAndamento;
    
    private String situacaoAndamentoLabel;
    
    private PAPenalidadeProcesso penalidadeProcesso;
    
    private PAComplemento paComplemento;
    
    private PAAndamentoProcesso andamento;
    
    private InformacaoProvaWrapper informacaoProva;
    
    private String dataProvaFormatada;
    
    private String notaProvaFormatada;
    
    private String descricaoFase;
    
    private PAParametroEnum desistenteEnum; 
    
    private String origemLabel;
    
    private String motivoLabel;
    
    private String resMotivoLabel;
    
    private String reqCursoBloqueioLabel;
    
    private String dataFimPenalidadeFormatada;
    
    private Integer quantidadeLote;
    
    private String acaoFuncionalidadeAjustaProcessoAdministrativo;
    
    private String numeroContato;
    
    private String email;
    
    private List<ProcessoAdministrativo> processosFilho;


    public ProcessoAdministrativoWrapper() {
    }

    public ProcessoAdministrativoWrapper(Long id, Integer codigoAndamento) {
        this.setId(id);
        this.codigoAndamento = codigoAndamento;
    }

    public ProcessoAdministrativoWrapper(ProcessoAdministrativo entidade, Integer codigoAndamento) {
        this.entidade = entidade;
        this.codigoAndamento = codigoAndamento;
    }
    
    public ProcessoAdministrativoWrapper(Long id, Integer codigoAndamento, Integer status) {
        this(id, codigoAndamento);
        this.status = status;
    }

    public ProcessoAdministrativoWrapper(ProcessoAdministrativo entidade) {
        this.entidade = entidade;
    }

    public ProcessoAdministrativoWrapper(ProcessoAdministrativo entidade, 
                                         String descricaoAndamento,
                                         String fluxoProcessoDescricao,
                                         SituacaoOcorrenciaEnum situacaoAndamento) {
        this.entidade = entidade;
        this.descricaoAndamento = descricaoAndamento;
        this.fluxoProcessoDescricao = fluxoProcessoDescricao;
        this.situacaoAndamento = situacaoAndamento;
    }
    
    public ProcessoAdministrativoWrapper(
        ProcessoAdministrativo entidade, 
        Integer codigoAndamento, String descricaoAndamento, 
        String fluxoProcessoDescricao, SituacaoOcorrenciaEnum situacaoAndamento) {
        
        this.entidade           = entidade;
        this.codigoAndamento    = codigoAndamento;
        this.descricaoAndamento = descricaoAndamento;
        
        this.fluxoProcessoDescricao = fluxoProcessoDescricao;
        this.situacaoAndamento      = situacaoAndamento;
    }
    
    public ProcessoAdministrativoWrapper(
        ProcessoAdministrativo entidade, 
        Integer codigoAndamento, String descricaoAndamento, 
        String fluxoProcessoDescricao, SituacaoOcorrenciaEnum situacaoAndamento, PAPenalidadeProcesso penalidadeProcesso) {
        
        this.entidade           = entidade;
        this.codigoAndamento    = codigoAndamento;
        this.descricaoAndamento = descricaoAndamento;
        
        this.fluxoProcessoDescricao = fluxoProcessoDescricao;
        this.situacaoAndamento      = situacaoAndamento;
        
        this.penalidadeProcesso = penalidadeProcesso;
    }

    public ProcessoAdministrativoWrapper(ProcessoAdministrativo entidade, PAPenalidadeProcesso penalidadeProcesso) {
        this.entidade = entidade;
        this.penalidadeProcesso = penalidadeProcesso;
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

    public String getTipoLabel() {
        tipoLabel = "";

        if (null != this.entidade && null != this.entidade.getTipo()) {
            tipoLabel = this.entidade.getTipo().toString();
        }

        return tipoLabel;
    }

    public void setTipoLabel(String tipoLabel) {
        this.tipoLabel = tipoLabel;
    }

    public String getSituacaoLabel() {
        situacaoLabel = "";

        if (null != this.entidade && null != this.entidade.getAtivo()) {
            situacaoLabel = this.entidade.getAtivo().toString();
        }

        return situacaoLabel;
    }

    public void setSituacaoLabel(String situacaoLabel) {
        this.situacaoLabel = situacaoLabel;
    }

    public Integer getCodigoAndamento() {
        return codigoAndamento;
    }

    public void setCodigoAndamento(Integer codigoAndamento) {
        this.codigoAndamento = codigoAndamento;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescricaoAndamento() {
        return codigoAndamento + "-" + descricaoAndamento;
    }

    public void setDescricaoAndamento(String descricaoAndamento) {
        this.descricaoAndamento = descricaoAndamento;
    }

    public String getNomeCondutor() {
        return nomeCondutor;
    }

    public void setNomeCondutor(String nomeCondutor) {
        this.nomeCondutor = nomeCondutor;
    }

    public byte[] getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(byte[] relatorio) {
        this.relatorio = relatorio;
    }

    public String getFluxoProcessoDescricao() {
        return fluxoProcessoDescricao;
    }

    public void setFluxoProcessoDescricao(String fluxoProcessoDescricao) {
        this.fluxoProcessoDescricao = fluxoProcessoDescricao;
    }

    public SituacaoOcorrenciaEnum getSituacaoAndamento() {
        return situacaoAndamento;
    }

    public void setSituacaoAndamento(SituacaoOcorrenciaEnum situacaoAndamento) {
        this.situacaoAndamento = situacaoAndamento;
    }

    public String getSituacaoAndamentoLabel() {
        situacaoAndamentoLabel = "";
        
        if (null != this.getSituacaoAndamento())
            situacaoAndamentoLabel = this.getSituacaoAndamento().toString();
        
        return situacaoAndamentoLabel;
    }

    public void setSituacaoAndamentoLabel(String situacaoAndamentoLabel) {
        this.situacaoAndamentoLabel = situacaoAndamentoLabel;
    }

    public PAPenalidadeProcesso getPenalidadeProcesso() {
        return penalidadeProcesso;
    }

    public void setPenalidadeProcesso(PAPenalidadeProcesso penalidadeProcesso) {
        this.penalidadeProcesso = penalidadeProcesso;
    }

    public PAComplemento getPaComplemento() {
        return paComplemento;
    }

    public void setPaComplemento(PAComplemento paComplemento) {
        this.paComplemento = paComplemento;
    }

    public InformacaoProvaWrapper getInformacaoProva() {
        return informacaoProva;
    }

    public void setInformacaoProva(InformacaoProvaWrapper informacaoProva) {
        this.informacaoProva = informacaoProva;
    }

    public String getDataProvaFormatada() {
        dataProvaFormatada = "";
        
        if (null != this.informacaoProva && null != this.informacaoProva.getData())
            dataProvaFormatada = Utils.formatDate(this.informacaoProva.getData(), "dd/MM/yyyy");
        
        return dataProvaFormatada;
    }

    public void setDataProvaFormatada(String dataProvaFormatada) {
        this.dataProvaFormatada = dataProvaFormatada;
    }

    public String getNotaProvaFormatada() {
        notaProvaFormatada = "";
        
        if (null != this.informacaoProva && null != this.informacaoProva.getNota())
            notaProvaFormatada = this.informacaoProva.getNota().toString().replace(".", ",");
            
        return notaProvaFormatada;
    }

    public void setNotaProvaFormatada(String notaProvaFormatada) {
        this.notaProvaFormatada = notaProvaFormatada;
    }

    public String getDescricaoFase() {
        return descricaoFase;
    }

    public void setDescricaoFase(String descricaoFase) {
        this.descricaoFase = descricaoFase;
    }
    
    @XmlElement(name = "numeroPortariaMascarado")
    @XmlJavaTypeAdapter(NumeroAnoAdapter.class)
    public String getNumeroPortariaMascarado() {
        return entidade == null? "":entidade.getNumeroPortaria();
    }

    public void setNumeroPortariaMascarado(String numeroProcesso) {
    }

    public PAParametroEnum getDesistenteEnum() {
        return desistenteEnum;
    }

    public void setDesistenteEnum(PAParametroEnum desistenteEnum) {
        this.desistenteEnum = desistenteEnum;
    }
    
    public void setDesistenteLabel(String d){}
    
    public String getDesistenteLabel(){
        String label = "Processo sem desistência";
        if(desistenteEnum != null){
            label = desistenteEnum.getLabel();
        }
        return label;
    }

    public String getOrigemLabel() {
        
        origemLabel = "";
        
        if (null != this.entidade && null != this.entidade.getOrigem()) {
            origemLabel = this.entidade.getOrigem().toString();
        }
        
        return origemLabel;
    }

    public void setOrigemLabel(String origemLabel) {
        this.origemLabel = origemLabel;
    }

    public String getMotivoLabel() {
        motivoLabel = "";
        
        if (null != this.entidade 
                && null != this.entidade.getOrigemApoio()
                && null != this.entidade.getOrigemApoio().getMotivo()) {
            motivoLabel = this.entidade.getOrigemApoio().getMotivo().toString();
        }
            
        return motivoLabel;
    }
    
    public void setMotivoLabel(String motivoLabel) {
        this.motivoLabel = motivoLabel;
    }
    
    public String getResMotivoLabel() {
        resMotivoLabel = "";
        
        if (null != this.entidade 
                && null != this.entidade.getOrigemApoio()
                && null != this.entidade.getOrigemApoio().getResultadoMotivo()) {
            resMotivoLabel = this.entidade.getOrigemApoio().getResultadoMotivo().toString();
        }
            
        return resMotivoLabel;
    }

    public void setResMotivoLabel(String resMotivoLabel) {
        this.resMotivoLabel = resMotivoLabel;
    }

    public String getReqCursoBloqueioLabel() {
        return reqCursoBloqueioLabel;
    }

    public void setReqCursoBloqueioLabel(String reqCursoBloqueioLabel) {
        this.reqCursoBloqueioLabel = reqCursoBloqueioLabel;
    }
    
    public String getValorPenalidadeLabel() {
        String label = "";
        String unidade = "Mes(es)";
        if (penalidadeProcesso != null) {
            if(UnidadePenalEnum.DIA.equals(penalidadeProcesso.getUnidadePenal()))
                unidade = "Dia(s)";
            label = penalidadeProcesso.getValor().toString().concat(" ").concat(unidade);
        }else if(paComplemento != null){
            if(entidade.isJuridico())
                unidade = "Dia(s)";
            label = paComplemento.getValor().concat(" ").concat(unidade);
        }
        return label;
    }

    public PAAndamentoProcesso getAndamento() {
        return andamento;
    }

    public void setAndamento(PAAndamentoProcesso andamento) {
        this.andamento = andamento;
    }

    public String getDataFimPenalidadeFormatada() {
        return dataFimPenalidadeFormatada;
    }

    public void setDataFimPenalidadeFormatada(String dataFimPenalidadeFormatada) {
        this.dataFimPenalidadeFormatada = dataFimPenalidadeFormatada;
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

    public String getNumeroContato() {
        return numeroContato;
    }

    public void setNumeroContato(String numeroContato) {
        this.numeroContato = numeroContato;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public List<ProcessoAdministrativo> getProcessosFilho() {
        return processosFilho;
    }

    public void setProcessosFilho(List<ProcessoAdministrativo> processosFilho) {
        this.processosFilho = processosFilho;
    }
    
}