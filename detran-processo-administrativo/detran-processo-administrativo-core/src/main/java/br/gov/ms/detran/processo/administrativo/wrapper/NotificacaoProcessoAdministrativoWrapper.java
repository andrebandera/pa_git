package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.Municipio;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.EditalProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoComplemento;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaCorreio;
import br.gov.ms.detran.protocolo.entidade.CorrespondenciaCorreioDevolucao;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@EntityMapping2(entity = {NotificacaoProcessoAdministrativo.class})
@XmlRootElement
public class NotificacaoProcessoAdministrativoWrapper implements IBaseEntity, IEntityResource<NotificacaoProcessoAdministrativo> {
    
    private NotificacaoProcessoAdministrativo entidade;
    
    private CorrespondenciaCorreioDevolucao correspondenciaCorreioDevolucao;
    
    private NotificacaoComplemento notificacaoComplemento;
    
    private EditalProcessoAdministrativo edital;
    
    private CorrespondenciaCorreio correspondenciaCorreio;
    
    private Municipio municipio;

    public NotificacaoProcessoAdministrativoWrapper() {
    }
    
    @Override
    public Long getId() {
        return entidade != null ? entidade.getId() : null;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {
        if (this.entidade == null) {
            this.entidade = new NotificacaoProcessoAdministrativo();
        }
        this.entidade.setId(id);
    }

    @Override
    public NotificacaoProcessoAdministrativo getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(NotificacaoProcessoAdministrativo entidade) {
        this.entidade = entidade;
    }

    public CorrespondenciaCorreioDevolucao getCorrespondenciaCorreioDevolucao() {
        return correspondenciaCorreioDevolucao;
    }

    public void setCorrespondenciaCorreioDevolucao(CorrespondenciaCorreioDevolucao correspondenciaCorreioDevolucao) {
        this.correspondenciaCorreioDevolucao = correspondenciaCorreioDevolucao;
    }

    public NotificacaoComplemento getNotificacaoComplemento() {
        return notificacaoComplemento;
    }

    public void setNotificacaoComplemento(NotificacaoComplemento notificacaoComplemento) {
        this.notificacaoComplemento = notificacaoComplemento;
    }

    public EditalProcessoAdministrativo getEdital() {
        return edital;
    }

    public void setEdital(EditalProcessoAdministrativo edital) {
        this.edital = edital;
    }

    public CorrespondenciaCorreio getCorrespondenciaCorreio() {
        return correspondenciaCorreio;
    }

    public void setCorrespondenciaCorreio(CorrespondenciaCorreio correspondenciaCorreio) {
        this.correspondenciaCorreio = correspondenciaCorreio;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }
    
    public String getDataEmissaoAR(){
        if(entidade != null && entidade.getDataInclusao() != null){
            return Utils.formatDate(entidade.getDataInclusao(), "dd/MM/yyyy");
        }
        return "";
    } 
}