package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

/**
 * @author desenvolvimento
 */
public class ProcessoAdministrativoRecursoWrapper implements IBaseEntity {

    private ProcessoAdministrativo processoAdministrativo;

    private OrigemDestinoEnum origemDestino;

    private TipoFasePaEnum tipoRecurso;

    public ProcessoAdministrativoRecursoWrapper(ProcessoAdministrativo processoAdministrativo, OrigemDestinoEnum origemDestino) {
        this.processoAdministrativo = processoAdministrativo;
        this.origemDestino = origemDestino;
    }

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

    public OrigemDestinoEnum getOrigemDestino() {
        return origemDestino;
    }

    public void setOrigemDestino(OrigemDestinoEnum origemDestino) {
        this.origemDestino = origemDestino;
    }

    public TipoFasePaEnum getTipoRecurso() {
        return tipoRecurso;
    }

    public void setTipoRecurso(TipoFasePaEnum tipoRecurso) {
        this.tipoRecurso = tipoRecurso;
    }
}