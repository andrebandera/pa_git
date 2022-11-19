/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.enums.apo.TipoExtensaoArquivoEnum;
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
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Lillydi
 */
@Entity
@Table(name = "TB_TCT_ARQUIVO_PA")
public class ArquivoPA extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tct_ID")
    private Long id;

    @Column(name = "Tct_Tipo_Arquivo")
    private Long tipoArquivo;

    @Lob
    @Column(name = "Tct_Byte")
    private byte[] byteArquivo;

    @Column(name = "Tct_Descricao")
    private String descricao;

    @Column(name = "Tct_Extensao")
    @Enumerated(EnumType.ORDINAL)
    private TipoExtensaoArquivoEnum extensao;

    @Column(name = "Tct_Tabela")
    private String tabela;

    @Column(name = "Tct_ID_Registro")
    private Long idTabela;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public ArquivoPA() {
    }

    public ArquivoPA(Long id, byte[] byteArquivo) {
        this.id = id;
        this.byteArquivo = byteArquivo;
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

    public Long getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(Long tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public byte[] getByteArquivo() {
        return byteArquivo;
    }

    public void setByteArquivo(byte[] byteArquivo) {
        this.byteArquivo = byteArquivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoExtensaoArquivoEnum getExtensao() {
        return extensao;
    }

    public void setExtensao(TipoExtensaoArquivoEnum extensao) {
        this.extensao = extensao;
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public Long getIdTabela() {
        return idTabela;
    }

    public void setIdTabela(Long idTabela) {
        this.idTabela = idTabela;
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
