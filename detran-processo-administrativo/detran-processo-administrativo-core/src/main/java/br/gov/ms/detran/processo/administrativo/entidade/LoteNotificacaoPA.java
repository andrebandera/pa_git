/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import java.io.Serializable;
import java.util.Calendar;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author desenvolvimento
 */
@Entity
@Table(name = "TB_TDI_LOTE_NOTIFICACAO_PA")
public class LoteNotificacaoPA extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdi_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Tdi_Arquivo", referencedColumnName = "Tct_ID")
    private ArquivoPA arquivo;

    @Column(name = "Tdi_Tipo_Notificacao")
    @Enumerated(EnumType.STRING)
    private TipoFasePaEnum tipo;

    @Column(name = "Tdi_Nome")
    private String nome;

    @Column(name = "Tdi_Data_Geracao")
    @Temporal(TemporalType.DATE)
    private Date dataGeracao;

    @Column(name = "Tdi_Qtde_Notificacao")
    private Integer qtdNotificacoes;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public LoteNotificacaoPA() {
    }

    public LoteNotificacaoPA(TipoFasePaEnum tipo, String nome, Integer qtdNotificacoes) {
        this.tipo = tipo;
        this.nome = nome;
        this.qtdNotificacoes = qtdNotificacoes;
        this.dataGeracao = Calendar.getInstance().getTime();
        this.ativo = AtivoEnum.ATIVO;
    }

    public LoteNotificacaoPA(Long id, TipoFasePaEnum tipo, String nome, Date dataGeracao, Integer qtdNotificacoes, AtivoEnum ativo, Long vr) {
        this.id = id;
        this.tipo = tipo;
        this.nome = nome;
        this.dataGeracao = dataGeracao;
        this.qtdNotificacoes = qtdNotificacoes;
        this.ativo = ativo;
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
        this.id = Long.valueOf(id.toString());
    }

    public ArquivoPA getArquivo() {
        return arquivo;
    }

    public void setArquivo(ArquivoPA arquivo) {
        this.arquivo = arquivo;
    }

    public TipoFasePaEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoFasePaEnum tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    @XmlElement(name = "dataGeracao")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Integer getQtdNotificacoes() {
        return qtdNotificacoes;
    }

    public void setQtdNotificacoes(Integer qtdNotificacoes) {
        this.qtdNotificacoes = qtdNotificacoes;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public String getTipoLabel() {

        if ( null != this.getTipo())
          return this.getTipo().toString();

        return null;
    }
}
