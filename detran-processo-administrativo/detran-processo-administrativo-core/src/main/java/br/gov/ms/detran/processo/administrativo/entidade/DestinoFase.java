package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.anotacao.negocio.BusinessLogicalExclusion;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.OrigemDestinoEnum;
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
@Table(name = "TB_TCP_PAD_DESTINO_FASE")
@BusinessLogicalExclusion
@NamedQueries({
    @NamedQuery(
        name = "DestinoFase.getDestinoFasePorFluxoFaseEOrigemDestino",
        query = "SELECT tcp "
                + "FROM DestinoFase tcp "
                + "WHERE tcp.fluxoFase.id = :p0 "
                + "AND tcp.origemDestino = :p1 "
                + "AND tcp.ativo = :p2 "),
    @NamedQuery(
        name = "DestinoFase.getDestinoFasePorPAFluxoFaseVinculos",
        query = "SELECT g FROM DestinoFase g WHERE g.fluxoFase.id=:p0 AND g.ativo=:p1")
})
public class DestinoFase extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tcp_Id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tcp_Fluxo_Fase", referencedColumnName = "Tch_ID")
    private PAFluxoFase fluxoFase;
    
    @Column(name = "Tcp_Origem_Destino")
    @Enumerated(EnumType.ORDINAL)
    private OrigemDestinoEnum origemDestino;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public DestinoFase() {
    }

    public DestinoFase(Long id_TCP, OrigemDestinoEnum origemDestino, Long versaoRegistro_TCP) {
        
        this.id = id_TCP;
        this.origemDestino = origemDestino;
        
        this.setVersaoRegistro(versaoRegistro_TCP);
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

    public PAFluxoFase getFluxoFase() {
        return fluxoFase;
    }

    public void setFluxoFase(PAFluxoFase fluxoFase) {
        this.fluxoFase = fluxoFase;
    }

    public OrigemDestinoEnum getOrigemDestino() {
        return origemDestino;
    }

    public void setOrigemDestino(OrigemDestinoEnum origemDestino) {
        this.origemDestino = origemDestino;
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