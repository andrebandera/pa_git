package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.comum.core.projeto.enums.adm.FormaProtocoloEnum;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.SituacaoRecursoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class RecursosWrapper {
    
    @XmlElement(name = "numeroprocesso", nillable = true)
    private String numeroProcesso;
    
    @XmlElement(name = "numeroprotocolo", nillable = true)
    private String numeroProtocolo;
    
    @XmlElement(name = "tipo", nillable = true)
    private TipoFasePaEnum tipo;
    
    @XmlElement(name = "situacao", nillable = true)
    private SituacaoRecursoEnum situacao;
    
    @XmlElement(name = "datarecurso", nillable = true)
    private Date dataRecurso;
    
    @XmlElement(name = "observacao", nillable = true)
    private String observacao;
    
    @XmlElement(name = "nomeusuario", nillable = true)
    private String nomeUsuario;
    
    @XmlElement(name = "cpfusuario", nillable = true)
    private String cpfUsuario;
    
    @XmlElement(name = "setordestino", nillable = true)
    private OrigemDestinoEnum setorDestino;
    
    @XmlElement(name = "postoatendimento", nillable = true)
    private String postoAtendimento;
    
    @XmlElement(name = "origemprotocolo", nillable = true)
    private FormaProtocoloEnum origemProtocolo;
    
    @XmlElement(name = "prazo", nillable = true)
    private BooleanEnum indiceForaPrazo;
    
    @XmlElement(name = "erro", nillable = true)
    private String erro;
    
    @XmlElement(name = "cpfrepresentantelegal", nillable = true)
    private String cpfRepresentanteLegal;
    
    @XmlElement(name = "nomerepresentantelegal", nillable = true)
    private String nomeRepresentanteLegal;
    
    @XmlElement(name = "tempestividade", nillable = true)
    private BooleanEnum tempestividade;
    
    @XmlElement(name = "recursoonline", nillable = true)
    private BooleanEnum recursoOnline;

    public RecursosWrapper() {
    }

    public RecursosWrapper(String numeroProcesso, 
                           String numeroProtocolo, 
                           TipoFasePaEnum tipo, 
                           SituacaoRecursoEnum situacao, 
                           Date dataRecurso, 
                           String observacao, 
                           String nomeUsuario, 
                           String cpfUsuario, 
                           OrigemDestinoEnum setorDestino, 
                           String postoAtendimento, 
                           FormaProtocoloEnum origemProtocolo, 
                           BooleanEnum indiceForaPrazo) {
        
        this.numeroProcesso = numeroProcesso;
        this.numeroProtocolo = numeroProtocolo;
        this.tipo = tipo;
        this.situacao = situacao;
        this.dataRecurso = dataRecurso;
        this.observacao = observacao;
        this.nomeUsuario = nomeUsuario;
        this.cpfUsuario = cpfUsuario;
        this.setorDestino = setorDestino;
        this.postoAtendimento = postoAtendimento;
        this.origemProtocolo = origemProtocolo;
        this.indiceForaPrazo = indiceForaPrazo;
    }
    
    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public TipoFasePaEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoFasePaEnum tipo) {
        this.tipo = tipo;
    }

    public SituacaoRecursoEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoRecursoEnum situacao) {
        this.situacao = situacao;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Date getDataRecurso() {
        return dataRecurso;
    }

    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataRecurso(Date dataRecurso) {
        this.dataRecurso = dataRecurso;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    public void setCpfUsuario(String cpfUsuario) {
        this.cpfUsuario = cpfUsuario;
    }

    public OrigemDestinoEnum getSetorDestino() {
        return setorDestino;
    }

    public void setSetorDestino(OrigemDestinoEnum setorDestino) {
        this.setorDestino = setorDestino;
    }

    public String getPostoAtendimento() {
        return postoAtendimento;
    }

    public void setPostoAtendimento(String postoAtendimento) {
        this.postoAtendimento = postoAtendimento;
    }

    public FormaProtocoloEnum getOrigemProtocolo() {
        return origemProtocolo;
    }

    public void setOrigemProtocolo(FormaProtocoloEnum origemProtocolo) {
        this.origemProtocolo = origemProtocolo;
    }

    public BooleanEnum getIndiceForaPrazo() {
        return indiceForaPrazo;
    }

    public void setIndiceForaPrazo(BooleanEnum indiceForaPrazo) {
        this.indiceForaPrazo = indiceForaPrazo;
    }

    public String getCpfRepresentanteLegal() {
        return cpfRepresentanteLegal;
    }

    public void setCpfRepresentanteLegal(String cpfRepresentanteLegal) {
        this.cpfRepresentanteLegal = cpfRepresentanteLegal;
    }

    public String getNomeRepresentanteLegal() {
        return nomeRepresentanteLegal;
    }

    public void setNomeRepresentanteLegal(String nomeRepresentanteLegal) {
        this.nomeRepresentanteLegal = nomeRepresentanteLegal;
    }

    public BooleanEnum getTempestividade() {
        return tempestividade;
    }

    public void setTempestividade(BooleanEnum tempestividade) {
        this.tempestividade = tempestividade;
    }

    public BooleanEnum getRecursoOnline() {
        return recursoOnline;
    }

    public void setRecursoOnline(BooleanEnum recursoOnline) {
        this.recursoOnline = recursoOnline;
    }
}