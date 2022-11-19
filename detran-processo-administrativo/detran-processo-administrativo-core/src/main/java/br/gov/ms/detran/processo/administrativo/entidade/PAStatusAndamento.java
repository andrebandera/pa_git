package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TCD_PAD_STATUS_ANDAMENTO")
@NamedQueries({
    @NamedQuery(
        name = "PAStatusAndamento.getPAStatusAndamentoPorAndamentoProcesso",
        query = "SELECT tcd FROM PAStatusAndamento tcd WHERE tcd.andamentoProcesso.id = :p0 AND tcd.ativo = :p1 "),
    @NamedQuery(
        name = "PAStatusAndamento.getPAStatusAndamentoPorPAAndamentoProcessoAtivo",
        query = "SELECT tcd FROM PAStatusAndamento tcd WHERE tcd.andamentoProcesso.id = :p0 AND tcd.ativo = :p1 "),
    @NamedQuery(
        name = "PAStatusAndamento.getStatusPorProcessoAdministrativo",
        query = "SELECT tcd "
                + "FROM PAOcorrenciaStatus tde "
                + "INNER JOIN tde.statusAndamento tcd "
                + "INNER JOIN tde.processoAdministrativo tdc "
                + "WHERE tdc.id = :p0 "
                + "AND tde.ativo = :p1 "
                + "AND tcd.ativo = :p1 "),
    @NamedQuery(
        name = "PAStatusAndamento.getPAStatusAndamentoAtivoPorStatusEAndamento",
        query = "SELECT tcd "
                + " FROM PAStatusAndamento tcd "
                + "     INNER JOIN tcd.status s "
                + "     INNER JOIN tcd.andamentoProcesso ap "
                + " WHERE ap.codigo = :p0 "
                + "     AND tcd.ativo = :p1 "),
    @NamedQuery(
        name = "PAStatusAndamento.getPAStatusAndamentoAtivoPorPAStatus",
        query = "SELECT tcd FROM PAStatusAndamento tcd WHERE tcd.status.id = :p0 AND tcd.ativo = :p1"),
        
        @NamedQuery(
        name = "PAStatusAndamento.getPAStatusAndamentoAtivoPorPAAndamentoProcesso",
        query = "SELECT tcd FROM PAStatusAndamento tcd WHERE tcd.andamentoProcesso.id = :p0 AND tcd.ativo = :p1")
    
})
public class PAStatusAndamento extends BaseEntityAtivo implements Serializable {

    @Id
    @Column(name = "Tcd_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tcd_Status", referencedColumnName = "Tca_ID")
    private PAStatus status;
    
    @ManyToOne
    @JoinColumn(name = "Tcd_Andamento_Processo", referencedColumnName = "Tcb_ID")
    private PAAndamentoProcesso andamentoProcesso;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public PAStatusAndamento() {
    }

    public PAStatusAndamento(Long id, PAAndamentoProcesso andamentoProcesso) {
        this.id = id;
        this.andamentoProcesso = andamentoProcesso;
    }

    public PAStatusAndamento(Long id, Long vr) {
        this.id = id;
        super.setVersaoRegistro(vr);
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

    public PAStatus getStatus() {
        return status;
    }

    public void setStatus(PAStatus status) {
        this.status = status;
    }

    public PAAndamentoProcesso getAndamentoProcesso() {
        return andamentoProcesso;
    }

    public void setAndamentoProcesso(PAAndamentoProcesso andamentoProcesso) {
        this.andamentoProcesso = andamentoProcesso;
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