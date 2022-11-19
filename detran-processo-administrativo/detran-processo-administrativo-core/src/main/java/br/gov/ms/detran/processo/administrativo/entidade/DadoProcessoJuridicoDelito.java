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

/**
 * @author yanko.campos
 */
@Entity
@Table(name = "TB_TEL_DADO_PJU_DELITO")
@NamedQueries({
    @NamedQuery(
            name = "DadoProcessoJuridicoDelito.getDadoProcessoJudicialDelitoPorDadoProcessoJudicial",
            query = "SELECT tel "
                    + "FROM DadoProcessoJuridicoDelito tel "
                    + "WHERE tel.dadoProcessoJudicial.id = :p0 "),
    @NamedQuery(
            name = "DadoProcessoJuridicoDelito.getAll",
            query = "SELECT tel FROM DadoProcessoJuridicoDelito tel ")
})
public class DadoProcessoJuridicoDelito extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tel_ID")
    private Long id;
    
    @Column(name = "Tel_Amparo_Legal")
    private Long amparoLegal;
    
    @ManyToOne
    @JoinColumn(name = "Tel_Dado_Processo_Judicial", referencedColumnName = "Tek_ID")
    private DadoProcessoJudicial dadoProcessoJudicial;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;
    
    public DadoProcessoJuridicoDelito() {
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

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public Long getAmparoLegal() {
        return amparoLegal;
    }

    public void setAmparoLegal(Long amparoLegal) {
        this.amparoLegal = amparoLegal;
    }

    public DadoProcessoJudicial getDadoProcessoJudicial() {
        return dadoProcessoJudicial;
    }

    public void setDadoProcessoJudicial(DadoProcessoJudicial dadoProcessoJudicial) {
        this.dadoProcessoJudicial = dadoProcessoJudicial;
    }
}