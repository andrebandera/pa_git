package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.entidade.enums.apo.TipoExtensaoArquivoEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnlineArquivo;
import br.gov.ms.detran.processo.administrativo.enums.TipoDocumentoRecursoPAOnlineEnum;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

@EntityMapping2(entity = {RecursoPAOnlineArquivo.class})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RecursoOnlinePaDocumentoWrapper implements IBaseEntity, IEntityResource<RecursoPAOnlineArquivo> {

    private RecursoPAOnlineArquivo entidade;

    private String tipoArquivo;

    private TipoExtensaoArquivoEnum tipoExtensaoArquivo;

    private TipoDocumentoRecursoPAOnlineEnum tipoDocumento;

    private String nomeArquivo;

    private byte[] byteArquivo;

    private Boolean obrigatorio = Boolean.FALSE;

    public RecursoOnlinePaDocumentoWrapper() {
    }

    public RecursoOnlinePaDocumentoWrapper(RecursoPAOnlineArquivo arquivo) {
        this.entidade = arquivo;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {

        if (this.entidade == null) {
            this.entidade = new RecursoPAOnlineArquivo();
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
    public RecursoPAOnlineArquivo getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(RecursoPAOnlineArquivo entidade) {
        this.entidade = entidade;
    }

    public TipoDocumentoRecursoPAOnlineEnum getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoRecursoPAOnlineEnum tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public byte[] getByteArquivo() {
        return byteArquivo;
    }

    public void setByteArquivo(byte[] byteArquivo) {
        this.byteArquivo = byteArquivo;
    }

    public String getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(String tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public TipoExtensaoArquivoEnum getTipoExtensaoArquivo() {
        return tipoExtensaoArquivo;
    }

    public void setTipoExtensaoArquivo(TipoExtensaoArquivoEnum tipoExtensaoArquivo) {
        this.tipoExtensaoArquivo = tipoExtensaoArquivo;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }
}