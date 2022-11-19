package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoMovimentoBloqueioBCAEnum;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TDZ_MOVIMENTACAO_BLOQUEIO_BCA")
@NamedQueries({
    @NamedQuery(
        name = "MovimentoBloqueioBCA.getMovimentosPorBloqueioEAtivo",
        query = "SELECT tdz "
                + "FROM MovimentoBloqueioBCA tdz "
                + "INNER JOIN tdz.bloqueioBCA tdk "
                + "WHERE tdk.id = :p0 "
                + "AND tdz.ativo = :p1 order by tdz.id DESC"),
    @NamedQuery(
        name = "MovimentoBloqueioBCA.getBloqueioPorProcessoAdministrativoETipoAtivoParaDesistencia",
        query = "SELECT tdz "
                + "FROM MovimentoBloqueioBCA tdz "
                + " INNER JOIN tdz.bloqueioBCA tdk "
                + "WHERE tdk.processoAdministrativo.id = :p0 "
                + " AND tdz.tipo = :p1 "
                + " AND tdz.ativo = :p2 "
                + " AND tdk.ativo = :p2 "
    ),
    @NamedQuery(
            name = "MovimentoBloqueioBCA.getBloqueiosPorPA",
            query = "SELECT tdz "
                    + "FROM MovimentoBloqueioBCA tdz "
                    + " INNER JOIN tdz.bloqueioBCA tdk "
                    + " INNER JOIN tdk.processoAdministrativo tdc "
                    + "WHERE tdc.id = :p0 ")

})
public class MovimentoBloqueioBCA extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdz_ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tdz_Bloqueio_BCA", referencedColumnName = "Tdk_ID")
    private BloqueioBCA bloqueioBCA;
    
    @Column(name = "Tdz_Usuario")
    private Long usuario;
    
    @Column(name = "Tdz_Tipo")
    @Enumerated(EnumType.STRING)
    private TipoMovimentoBloqueioBCAEnum tipo;
    
    @Column(name = "Tdz_Data_BCA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataBCA;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public MovimentoBloqueioBCA() {
    }

    public MovimentoBloqueioBCA(Long id) {
        this.id = id;
    }
    
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

    public BloqueioBCA getBloqueioBCA() {
        return bloqueioBCA;
    }

    public void setBloqueioBCA(BloqueioBCA bloqueioBCA) {
        this.bloqueioBCA = bloqueioBCA;
    }

    public Long getUsuario() {
        return usuario;
    }

    public void setUsuario(Long usuario) {
        this.usuario = usuario;
    }

    public TipoMovimentoBloqueioBCAEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentoBloqueioBCAEnum tipo) {
        this.tipo = tipo;
    }

    public Date getDataBCA() {
        return dataBCA;
    }

    @XmlElement(name = "dataBCA")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataBCA(Date dataBCA) {
        this.dataBCA = dataBCA;
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