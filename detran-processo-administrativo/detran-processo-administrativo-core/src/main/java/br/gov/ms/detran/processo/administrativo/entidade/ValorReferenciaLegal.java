/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Lillydi
 */
@Entity
@Table(name = "TB_TCS_PAD_REFERENCIA_LEGAL_VALOR")
@NamedNativeQueries({
    @NamedNativeQuery(name = "ValorReferenciaLegal.getValorPenalPorPontuacao",
            query = "select tcs.*  "
            + "	from "
            + "		TB_TCS_PAD_REFERENCIA_LEGAL_VALOR tcs inner join "
            + "		TB_MBB_MOTIVO_PENALIDADE mbb on mbb.Mbb_ID = tcs.Tcs_Motivo_Penalidade inner join "
            + "		TB_MAZ_INFRACAO_PENALIDADE maz on mbb.Mbb_Infracao_Penalidade = maz.Maz_ID inner join "
            + "		TB_MBT_AMPARO_LEGAL mbt on mbt.Mbt_ID = Maz_Amparo_Legal inner join "
            + "		TB_TDH_PAD_APOIO_ORIGEM_INSTAURACAO tdh on tdh.Tdh_Amparo_Legal = Mbt_ID  "
            + "	where Tdh_Regra = :p0 "
            + "		and Mbb_Tipo_Processo = :p1 "
            + "		and tcs.Tcs_Intervalo_Inicial <= :p2 "
            + "		and tcs.Tcs_Intervalo_Final >= :p2 "
            + "		and tcs.Ativo = :p3",
            resultClass = ValorReferenciaLegal.class)
})
public class ValorReferenciaLegal extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tcs_ID")
    private Long id;

    @Column(name = "Tcs_Acao_Infracao_Penalidade")
    private Long acaoInfracaoPenalidade;

    @Column(name = "Tcs_Classificacao_Infracao")
    private Long classificacaoInfracao;

    @Column(name = "Tcs_Motivo_Penalidade")
    private Long motivoPenalidade;

    @Column(name = "Tcs_Intervalo_Inicial")
    private String intervaloInicial;

    @Column(name = "Tcs_Intervalo_Final")
    private String intervaloFinal;

    @Column(name = "Tcs_Unidade_Penal")
    private String unidadePenal;

    @Column(name = "Tcs_Valor")
    private String valor;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public ValorReferenciaLegal() {
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

    public Long getAcaoInfracaoPenalidade() {
        return acaoInfracaoPenalidade;
    }

    public void setAcaoInfracaoPenalidade(Long acaoInfracaoPenalidade) {
        this.acaoInfracaoPenalidade = acaoInfracaoPenalidade;
    }

    public Long getClassificacaoInfracao() {
        return classificacaoInfracao;
    }

    public void setClassificacaoInfracao(Long classificacaoInfracao) {
        this.classificacaoInfracao = classificacaoInfracao;
    }

    public Long getMotivoPenalidade() {
        return motivoPenalidade;
    }

    public void setMotivoPenalidade(Long motivoPenalidade) {
        this.motivoPenalidade = motivoPenalidade;
    }

    public String getIntervaloInicial() {
        return intervaloInicial;
    }

    public void setIntervaloInicial(String intervaloInicial) {
        this.intervaloInicial = intervaloInicial;
    }

    public String getIntervaloFinal() {
        return intervaloFinal;
    }

    public void setIntervaloFinal(String intervaloFinal) {
        this.intervaloFinal = intervaloFinal;
    }

    public String getUnidadePenal() {
        return unidadePenal;
    }

    public void setUnidadePenal(String unidadePenal) {
        this.unidadePenal = unidadePenal;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
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
