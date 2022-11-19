package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.PostoAtendimento;
import br.gov.ms.detran.comum.core.projeto.enums.hab.FormaEntregaCnhEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.comum.core.projeto.enums.hab.ModoEntregaCnhEnum;
import br.gov.ms.detran.comum.core.projeto.enums.adm.FormaProtocoloEnum;
import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author desenvolvimento
 */
public class UsuarioAcessoProcessoAdministrativoWrapper implements IBaseEntity {
    
    private ProcessoAdministrativo processoAdministrativo;
    
    private Boolean habilitarForaPrazo;
    
    private BooleanEnum foraPrazo;
    
    private Boolean habilitarDataInicio;
    
    private Boolean apresentarBotaoDesbloquearCnh;
    
    private Boolean habilitarModoRecolhimentoCnh;
    
    private Boolean habilitarTipoEntregaCnh;
    
    private Boolean habilitarFormaProtocolo;
    
    private FormaProtocoloEnum formaProtocolo;
    
    private ModoEntregaCnhEnum formaEntrega;
    
    private FormaEntregaCnhEnum formaEntregaControle;
    
    private Boolean habilitarPostoAtendimento;
    
    private PostoAtendimento postoAtendimento;
    
    private Boolean desabilitaDataRecurso;
    
    private Date dataRecurso;
    
    @Override
    public Serializable getId() {
        return (this.processoAdministrativo != null ? this.processoAdministrativo.getId() : null);
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {

        if (this.processoAdministrativo == null) {
            this.processoAdministrativo = new ProcessoAdministrativo();
        }

        this.processoAdministrativo.setId(id);
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public Boolean getHabilitarDataInicio() {
        return habilitarDataInicio;
    }

    public void setHabilitarDataInicio(Boolean habilitarDataInicio) {
        this.habilitarDataInicio = habilitarDataInicio;
    }

    public Boolean getApresentarBotaoDesbloquearCnh() {
        return apresentarBotaoDesbloquearCnh;
    }

    public void setApresentarBotaoDesbloquearCnh(Boolean apresentarBotaoDesbloquearCnh) {
        this.apresentarBotaoDesbloquearCnh = apresentarBotaoDesbloquearCnh;
    }

    public Boolean getHabilitarModoRecolhimentoCnh() {
        return habilitarModoRecolhimentoCnh;
    }

    public void setHabilitarModoRecolhimentoCnh(Boolean habilitarModoRecolhimentoCnh) {
        this.habilitarModoRecolhimentoCnh = habilitarModoRecolhimentoCnh;
    }

    public Boolean getHabilitarTipoEntregaCnh() {
        return habilitarTipoEntregaCnh;
    }

    public void setHabilitarTipoEntregaCnh(Boolean habilitarTipoEntregaCnh) {
        this.habilitarTipoEntregaCnh = habilitarTipoEntregaCnh;
    }

    public Boolean getHabilitarFormaProtocolo() {
        return habilitarFormaProtocolo;
    }

    public void setHabilitarFormaProtocolo(Boolean habilitarFormaProtocolo) {
        this.habilitarFormaProtocolo = habilitarFormaProtocolo;
    }

    public FormaProtocoloEnum getFormaProtocolo() {
        return formaProtocolo;
    }

    public void setFormaProtocolo(FormaProtocoloEnum formaProtocolo) {
        this.formaProtocolo = formaProtocolo;
    }

    public ModoEntregaCnhEnum getFormaEntrega() {
        return formaEntrega;
    }

    public void setFormaEntrega(ModoEntregaCnhEnum formaEntrega) {
        this.formaEntrega = formaEntrega;
    }

    public FormaEntregaCnhEnum getFormaEntregaControle() {
        return formaEntregaControle;
    }

    public void setFormaEntregaControle(FormaEntregaCnhEnum formaEntregaControle) {
        this.formaEntregaControle = formaEntregaControle;
    }

    public Boolean getHabilitarForaPrazo() {
        return habilitarForaPrazo;
    }

    public void setHabilitarForaPrazo(Boolean habilitarForaPrazo) {
        this.habilitarForaPrazo = habilitarForaPrazo;
    }

    public BooleanEnum getForaPrazo() {
        return foraPrazo;
    }

    public void setForaPrazo(BooleanEnum foraPrazo) {
        this.foraPrazo = foraPrazo;
    }

    public Boolean getHabilitarPostoAtendimento() {
        return habilitarPostoAtendimento;
    }

    public void setHabilitarPostoAtendimento(Boolean habilitarPostoAtendimento) {
        this.habilitarPostoAtendimento = habilitarPostoAtendimento;
    }

    public PostoAtendimento getPostoAtendimento() {
        return postoAtendimento;
    }

    public void setPostoAtendimento(PostoAtendimento postoAtendimento) {
        this.postoAtendimento = postoAtendimento;
    }

    public Boolean getDesabilitaDataRecurso() {
        return desabilitaDataRecurso;
    }

    public void setDesabilitaDataRecurso(Boolean desabilitaDataRecurso) {
        this.desabilitaDataRecurso = desabilitaDataRecurso;
    }

    public Date getDataRecurso() {
        return dataRecurso;
    }

    @XmlElement(name = "dataRecurso")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataRecurso(Date dataRecurso) {
        this.dataRecurso = dataRecurso;
    }
}