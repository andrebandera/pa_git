package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.adapter.CPFAdapter;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.Recurso;
import br.gov.ms.detran.comum.core.projeto.entidade.adm.RepresentanteLegal;
import br.gov.ms.detran.comum.core.projeto.enums.adm.FormaProtocoloEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.ResponsavelProtocoloEnum;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.TipoSituacaoProtocoloEnum;
import br.gov.ms.detran.processo.administrativo.entidade.Protocolo;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnline;
import br.gov.ms.detran.processo.administrativo.enums.MotivoCancelamentoRecursoEnum;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@EntityMapping2(entity = {Recurso.class})
public class RecursoWrapper implements IBaseEntity, IEntityResource<Recurso> {
    
    @XmlElement(nillable = false)
    private Recurso entidade;
    
    @XmlJavaTypeAdapter(CPFAdapter.class)
    private String numeroDocumento;
    
    private BooleanEnum indiceForaPrazo;
    
    private OrigemDestinoEnum destino;
    
    private FormaProtocoloEnum forma;
    
    private Protocolo protocolo;
    
    private ResponsavelProtocoloEnum responsavel;
    
    private String observacao;
    
    private RepresentanteLegal representanteLegal;
    
    private Date dataRecurso = Calendar.getInstance().getTime();

    private Date dataResultado;

    private String situacaoLabel;
    
    private String tipoLabel;
    
    private byte[] byteArquivo;
    
    private TipoSituacaoProtocoloEnum tipoProtocolo;
    
    private UsuarioAcessoProcessoAdministrativoWrapper usuarioAcesso;
    
    private String resultadoLabel;
    
    private Boolean desabilitadoDataRecurso;
    
    private MotivoCancelamentoRecursoEnum motivoCancelamento;
    
    private String urlReportBirt;
    
    private RecursoPAOnline recursoEfetivado;
    
    public RecursoWrapper() {
    }

    public RecursoWrapper(Recurso entidade) {
        this.entidade = entidade;
        forma = FormaProtocoloEnum.SISTEMA;
        responsavel = ResponsavelProtocoloEnum.DETRAN;
    }
    public RecursoWrapper(Recurso entidade, String obs) {
        this.entidade = entidade;
        forma = FormaProtocoloEnum.SISTEMA;
        responsavel = ResponsavelProtocoloEnum.DETRAN;
        observacao = obs;
    }

    @Override
    public Recurso getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(Recurso entidade) {
        this.entidade = entidade;
    }

    @Override
    public Serializable getId() {
        return (this.entidade != null ? this.entidade.getId() : null);
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {

        if (this.entidade == null) {
            this.entidade = new Recurso();
        }

        this.entidade.setId(id);
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getSituacaoLabel() {
        situacaoLabel = "";
        
        if (null != this.entidade && null != this.entidade.getSituacao())
            situacaoLabel = this.entidade.getSituacao().toString();
        
        return situacaoLabel;
    }

    public void setSituacaoLabel(String situacaoLabel) {
        this.situacaoLabel = situacaoLabel;
    }

    public String getTipoLabel() {
        tipoLabel = "";
        
        if (null != this.entidade && null != this.entidade.getTipoRecurso())
            tipoLabel = this.entidade.getTipoRecurso().toString();
        
        return tipoLabel;
    }

    public void setTipoLabel(String tipoLabel) {
        this.tipoLabel = tipoLabel;
    }

    
    public Date getDataRecurso() {
        return dataRecurso;
    }

    @XmlElement(name = "dataRecurso")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataRecurso(Date dataRecurso) {
        this.dataRecurso = dataRecurso;
    }

    public Date getDataResultado() {
        return dataResultado;
    }

    @XmlElement(name = "dataResultado")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataResultado(Date dataResultado) {
        this.dataResultado = dataResultado;
    }

    public BooleanEnum getIndiceForaPrazo() {
        return indiceForaPrazo;
    }

    public void setIndiceForaPrazo(BooleanEnum indiceForaPrazo) {
        this.indiceForaPrazo = indiceForaPrazo;
    }

    public OrigemDestinoEnum getDestino() {
        return destino;
    }

    public void setDestino(OrigemDestinoEnum destino) {
        this.destino = destino;
    }

    public FormaProtocoloEnum getForma() {
        return forma;
    }

    public void setForma(FormaProtocoloEnum forma) {
        this.forma = forma;
    }

    public RepresentanteLegal getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(RepresentanteLegal representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public byte[] getByteArquivo() {
        return byteArquivo;
    }

    public void setByteArquivo(byte[] byteArquivo) {
        this.byteArquivo = byteArquivo;
    }

    public TipoSituacaoProtocoloEnum getTipoProtocolo() {
        return tipoProtocolo;
    }

    public void setTipoProtocolo(TipoSituacaoProtocoloEnum tipoProtocolo) {
        this.tipoProtocolo = tipoProtocolo;
    }

    public UsuarioAcessoProcessoAdministrativoWrapper getUsuarioAcesso() {
        return usuarioAcesso;
    }

    public void setUsuarioAcesso(UsuarioAcessoProcessoAdministrativoWrapper usuarioAcesso) {
        this.usuarioAcesso = usuarioAcesso;
    }

    public String getResultadoLabel() {
        return resultadoLabel;
    }

    public void setResultadoLabel(String resultadoLabel) {
        this.resultadoLabel = resultadoLabel;
    }

    public Boolean getDesabilitadoDataRecurso() {
        return desabilitadoDataRecurso;
    }

    public void setDesabilitadoDataRecurso(Boolean desabilitadoDataRecurso) {
        this.desabilitadoDataRecurso = desabilitadoDataRecurso;
    }

    public ResponsavelProtocoloEnum getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(ResponsavelProtocoloEnum responsavel) {
        this.responsavel = responsavel;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Protocolo getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(Protocolo protocolo) {
        this.protocolo = protocolo;
    }

    public MotivoCancelamentoRecursoEnum getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(MotivoCancelamentoRecursoEnum motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getUrlReportBirt() {
        return urlReportBirt;
    }

    public void setUrlReportBirt(String urlReportBirt) {
        this.urlReportBirt = urlReportBirt;
    }

    public RecursoPAOnline getRecursoEfetivado() {
        return recursoEfetivado;
    }

    public void setRecursoEfetivado(RecursoPAOnline recursoEfetivado) {
        this.recursoEfetivado = recursoEfetivado;
    }
}