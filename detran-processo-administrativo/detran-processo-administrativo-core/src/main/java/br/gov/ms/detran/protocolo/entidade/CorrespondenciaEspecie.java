/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.protocolo.entidade;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Lillydi
 */
@Entity
@Table(name = "TB_TAM_CORRESPONDENCIA_ESPECIE")
@NamedQueries({
    @NamedQuery(name = "CorrespondenciaEspecie.getCorrespondenciaEspeciePorDescricao",
            query = "SELECT tam From CorrespondenciaEspecie tam where tam.descricao = :p0 AND tam.ativo = :p1")
})
public class CorrespondenciaEspecie extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tam_ID")
    private Long id;

    @Column(name = "Tam_Descricao")
    private String descricao;

    @Column(name = "Tam_Indicativo_Generalizacao")
    private String indicativoGeneralizacao;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public CorrespondenciaEspecie() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public void setId(Serializable id) {
        this.id = Long.valueOf(id.toString());
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIndicativoGeneralizacao() {
        return indicativoGeneralizacao;
    }

    public void setIndicativoGeneralizacao(String indicativoGeneralizacao) {
        this.indicativoGeneralizacao = indicativoGeneralizacao;
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
