package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoDocumentoRecursoPAOnlineEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

@Entity
@Table(name = "TB_TEN_RECURSO_PA_ONLINE_ARQUIVO")
@NamedQueries({
        @NamedQuery(name = "RecursoPAOnlineArquivo.getListArquivosPorRecursoOnline",
                query = "select ten " +
                        "From RecursoPAOnlineArquivo  ten " +
                        "   inner join ten.recursoOnline tem " +
                        "where tem.id = :p0 "),
        @NamedQuery(name = "RecursoPAOnlineArquivo.getFormularioAssinadoDoRecursoOnline",
                query = "select  ten " +
                        "From RecursoPAOnlineArquivo ten " +
                        "   inner join ten.recursoOnline tem " +
                        "where tem.id = :p0 " +
                        "   and ten.tipoDocumento = :p1 " +
                        "   and ten.ativo = :p2")
})
public class RecursoPAOnlineArquivo extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Ten_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Ten_Arquivo", referencedColumnName = "Tct_ID")
    private ArquivoPA arquivoPA;

    @ManyToOne
    @JoinColumn(name = "Ten_Recurso_PA_Online", referencedColumnName = "Tem_ID")
    private RecursoPAOnline recursoOnline;

    @Column(name = "Ten_Descricao")
    private String descricao;

    @Column(name = "Ten_Observacao")
    private String observacao;

    @Column(name = "Ten_Tipo_Documento")
    @Enumerated(EnumType.STRING)
    private TipoDocumentoRecursoPAOnlineEnum tipoDocumento;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    @Override
    public Long getId() {
        return id;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public void setId(Serializable id) {
        this.id = (id != null ? Long.valueOf(id.toString()) : null);
    }

    public ArquivoPA getArquivoPA() {
        return arquivoPA;
    }

    public void setArquivoPA(ArquivoPA arquivoPA) {
        this.arquivoPA = arquivoPA;
    }

    public RecursoPAOnline getRecursoOnline() {
        return recursoOnline;
    }

    public void setRecursoOnline(RecursoPAOnline recursoonline) {
        this.recursoOnline = recursoonline;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public TipoDocumentoRecursoPAOnlineEnum getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoRecursoPAOnlineEnum tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }
}
