package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.adapter.*;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnline;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@EntityMapping2(entity = {RecursoPAOnline.class})
public class BackOfficePaWrapper implements IBaseEntity, IEntityResource<RecursoPAOnline> {

    private RecursoPAOnline entidade;

    private TipoFasePaEnum tipoRecurso;

    private String numeroAutoInfracao;

    private String cpf;

    private String usuario;

    private String cep;

    private String telefoneResidencial;

    private String telefoneCelular;

    private List<RecursoOnlinePaDocumentoWrapper> documentos;

    private String motivoRecusa;

    private String motivoCancelamento;

    private Date dataNotificacao;
    private Date dataPrazoLimite;

    private String ip;
    private String cpfUsuario;
    private String urlReportBirt;

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {

        if (this.entidade == null) {
            this.entidade = new RecursoPAOnline();
        }

        this.entidade.setId(id);
    }

    @Override
    public Long getId() {
        if (entidade != null && entidade.getId() != null) {
            return entidade.getId();
        }
        return null;
    }

    @Override
    public RecursoPAOnline getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(RecursoPAOnline entidade) {
        this.entidade = entidade;
    }

    public String getCpf() {
        return cpf;
    }

    @XmlJavaTypeAdapter(CPFAdapter.class)
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCep() {

        cep = entidade != null ? entidade.getEnderecoCEP() : "";

        return cep;
    }

    @XmlJavaTypeAdapter(CEPAdapter1.class)
    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTelefoneResidencial() {

        telefoneResidencial = entidade != null ? entidade.getTelefoneFixo() : "";

        return telefoneResidencial;
    }

    @XmlJavaTypeAdapter(TelefoneAdapter.class)
    public void setTelefoneResidencial(String telefoneResidencial) {
        this.telefoneResidencial = telefoneResidencial;
    }

    public String getTelefoneCelular() {

        telefoneCelular = entidade != null ? entidade.getCelular() : "";

        return telefoneCelular;
    }

    @XmlJavaTypeAdapter(TelefoneAdapter.class)
    public void setTelefoneCelular(String telefoneCelular) {
        this.telefoneCelular = telefoneCelular;
    }

    public TipoFasePaEnum getTipoRecurso() {
        return tipoRecurso;
    }

    public void setTipoRecurso(TipoFasePaEnum tipoRecurso) {
        this.tipoRecurso = tipoRecurso;
    }

    public List<RecursoOnlinePaDocumentoWrapper> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<RecursoOnlinePaDocumentoWrapper> documentos) {
        this.documentos = documentos;
    }

    public String getSituacaoLabel() {

        if (null != this.entidade && null != this.entidade.getSituacao()) {
            return this.entidade.getSituacao().toString();
        }

        return "";
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNumeroAutoInfracao() {
        return numeroAutoInfracao;
    }

    public void setNumeroAutoInfracao(String numeroAutoInfracao) {
        this.numeroAutoInfracao = numeroAutoInfracao;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getTipoRecursoLabel() {

        if (null != this.entidade && null != this.entidade.getTipo()) {
            return this.entidade.getTipo().toString();
        }

        return "";
    }

    public String getTempestividadeLabel() {

        if (null != this.entidade && null != this.entidade.getIndiceTempestividade()) {
            return this.entidade.getIndiceTempestividade().toString();
        }

        return "";
    }

    @XmlJavaTypeAdapter(DateAdapter.class)
    public Date getDataSituacao() {

        if (null != this.entidade && null != this.entidade.getDataInclusao()) {
            return this.entidade.getDataInclusao();
        }

        return null;
    }

    public Date getDataNotificacao() {
        return dataNotificacao;
    }

    @XmlElement(name = "dataNotificacao")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataNotificacao(Date dataNotificacao) {
        this.dataNotificacao = dataNotificacao;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    public void setCpfUsuario(String cpfUsuario) {
        this.cpfUsuario = cpfUsuario;
    }

    public Date getDataPrazoLimite() {
        return dataPrazoLimite;
    }

    @XmlElement(name = "dataPrazoLimite")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataPrazoLimite(Date dataPrazoLimite) {
        this.dataPrazoLimite = dataPrazoLimite;
    }

    public String getUrlReportBirt() {
        return urlReportBirt;
    }

    public void setUrlReportBirt(String urlReportBirt) {
        this.urlReportBirt = urlReportBirt;
    }
}