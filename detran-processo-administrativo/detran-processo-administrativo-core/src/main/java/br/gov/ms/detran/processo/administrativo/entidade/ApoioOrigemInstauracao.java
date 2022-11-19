package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.enums.inf.AcaoInstauracaoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.AcaoSistemaPAEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.MotivoPenalidadeEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
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

@Entity
@Table(name = "TB_TDH_PAD_APOIO_ORIGEM_INSTAURACAO")
@NamedQueries({
    @NamedQuery(
        name = "ApoioOrigemInstauracao.findAll",
        query = "SELECT tdh FROM ApoioOrigemInstauracao tdh"
    ),
    @NamedQuery(
        name = "ApoioOrigemInstauracao.getApoioOrigemPorRegra",
        query = "SELECT tdh FROM ApoioOrigemInstauracao tdh where tdh.regra = :p0"
    ),
    @NamedQuery(
        name = "ApoioOrigemInstauracao.getListOrigemInstauracao",
        query = "SELECT tdh FROM ApoioOrigemInstauracao tdh where tdh.ativo = :p0 and tdh.regra not in(:p1) order by tdh.prioridade ASC"
    ),
    @NamedQuery(
        name = "ApoioOrigemInstauracao.getApoioOrigemPorDescricao",
        query = "SELECT tdh FROM ApoioOrigemInstauracao tdh where tdh.descricao= :p0"
    ),
    @NamedQuery(
            name = "ApoioOrigemInstauracao.ApoioOrigemInstauracaoById",
            query = "SELECT tdh FROM ApoioOrigemInstauracao tdh WHERE tdh.id = :p0"),
    
    @NamedQuery(name = "ApoioOrigemInstauracao.getApoioOrigemInstauracaoNaoUtilizadoPeloFluxoProcesso", 
            query = "SELECT e FROM ApoioOrigemInstauracao e "
                    + " WHERE e.id NOT IN "
                    + "(SELECT ma.id "
                    + "     FROM PAFluxoOrigem m "
                    + "     JOIN m.fluxoProcesso s "
                    + "     JOIN m.origemInstauracao ma "
                    + "     WHERE s.id = :p0 AND m.ativo = :p2) "
                    + "AND (:p1 IS NULL OR e.descricao LIKE :p1 ) "
                    + "AND e.ativo = :p2 "
                    + "ORDER BY e.descricao "),
    @NamedQuery(name = "ApoioOrigemInstauracao.getCountApoioOrigemInstauracaoNaoUtilizadoPeloFluxoProcesso", 
            query = "SELECT COUNT(e) FROM ApoioOrigemInstauracao e WHERE e.id NOT IN "
                    + "(SELECT ma.id FROM PAFluxoOrigem m JOIN "
                    + "m.fluxoProcesso s JOIN m.origemInstauracao ma WHERE s.id = :p0 AND m.ativo = :p2) "
                    + "AND (:p1 IS NULL OR e.descricao LIKE :p1 ) "
                    + "AND e.ativo = :p2 ")
})
public class ApoioOrigemInstauracao extends BaseEntityAtivo implements Serializable {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdh_ID")
    private Long id;
    
    @Column(name = "Tdh_Regra")
    @Enumerated(EnumType.STRING)
    private RegraInstaurarEnum regra;
    
    @Column(name = "Tdh_Descricao")
    private String descricao;
    
    @Column(name = "Tdh_Artigo_Inciso")
    private String artigoInciso;
    
    @Column(name = "Tdh_Motivo")
    @Enumerated(EnumType.STRING)
    private MotivoPenalidadeEnum motivo;    
    
    @Column(name = "Tdh_Tipo_Processo_Reincidencia")
    @Enumerated(EnumType.STRING)
    private TipoProcessoEnum tipoProcessoReincidencia;
    
    @Column(name = "Tdh_Amparo_Legal")
    private Long amparoLegal;
    
    @Column(name = "Tdh_Indice_Historico_Infracao")
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum indiceHistoricoInfracao;
    
    @Column(name = "Tdh_Res_Tipo_Processo")
    @Enumerated(EnumType.STRING)
    private TipoProcessoEnum resultadoTipoProcesso;
    
    @Column(name = "Tdh_Res_Motivo")
    @Enumerated(EnumType.STRING)
    private MotivoPenalidadeEnum resultadoMotivo;
    
    @Column(name = "Tdh_Res_Acao")
    @Enumerated(EnumType.STRING)
    private AcaoInstauracaoEnum resultadoAcao;
    
    @Column(name = "Tdh_Reincidencia")
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum reincidencia;
    
    @Column(name = "Tdh_Prioridade")
    private Integer prioridade;
    
    @Column(name = "Tdh_Acao_Sistema")
    @Enumerated(EnumType.STRING)
    private AcaoSistemaPAEnum acaoSistema;
    
    @Column(name = "Tdh_Status_Infracao")
    private String statusInfracao;
    
    @Column(name = "Tdh_Origem_Inf_Pontuacao")
    private String origemInformacaoPontuacao;
    
    @Column(name = "Tdh_Indice_Reincidencia_MAZ")
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum indiceReincidenciaMAZ;
    
    @Column(name = "Tdh_Confirmacao_MainFrame")
    private String codigoConfirmacaoMainFrame;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public ApoioOrigemInstauracao() {
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
    
    public RegraInstaurarEnum getRegra() {
        return regra;
    }

    public void setRegra(RegraInstaurarEnum regra) {
        this.regra = regra;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public MotivoPenalidadeEnum getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoPenalidadeEnum motivo) {
        this.motivo = motivo;
    }

    public TipoProcessoEnum getTipoProcessoReincidencia() {
        return tipoProcessoReincidencia;
    }

    public void setTipoProcessoReincidencia(TipoProcessoEnum tipoProcessoReincidencia) {
        this.tipoProcessoReincidencia = tipoProcessoReincidencia;
    }

    public Long getAmparoLegal() {
        return amparoLegal;
    }

    public void setAmparoLegal(Long amparoLegal) {
        this.amparoLegal = amparoLegal;
    }

    public BooleanEnum getIndiceHistoricoInfracao() {
        return indiceHistoricoInfracao;
    }

    public void setIndiceHistoricoInfracao(BooleanEnum indiceHistoricoInfracao) {
        this.indiceHistoricoInfracao = indiceHistoricoInfracao;
    }

    public TipoProcessoEnum getResultadoTipoProcesso() {
        return resultadoTipoProcesso;
    }

    public void setResultadoTipoProcesso(TipoProcessoEnum resultadoTipoProcesso) {
        this.resultadoTipoProcesso = resultadoTipoProcesso;
    }

    public MotivoPenalidadeEnum getResultadoMotivo() {
        return resultadoMotivo;
    }

    public void setResultadoMotivo(MotivoPenalidadeEnum resultadoMotivo) {
        this.resultadoMotivo = resultadoMotivo;
    }

    public AcaoInstauracaoEnum getResultadoAcao() {
        return resultadoAcao;
    }

    public void setResultadoAcao(AcaoInstauracaoEnum resultadoAcao) {
        this.resultadoAcao = resultadoAcao;
    }

    public BooleanEnum getReincidencia() {
        return reincidencia;
    }

    public void setReincidencia(BooleanEnum reincidencia) {
        this.reincidencia = reincidencia;
    }

    public AcaoSistemaPAEnum getAcaoSistema() {
        return acaoSistema;
    }

    public void setAcaoSistema(AcaoSistemaPAEnum acaoSistema) {
        this.acaoSistema = acaoSistema;
    }

    public String getArtigoInciso() {
        return artigoInciso;
    }

    public void setArtigoInciso(String artigoInciso) {
        this.artigoInciso = artigoInciso;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
    }

    public String getStatusInfracao() {
        return statusInfracao;
    }

    public void setStatusInfracao(String statusInfracao) {
        this.statusInfracao = statusInfracao;
    }

    public String getOrigemInformacaoPontuacao() {
        return origemInformacaoPontuacao;
    }

    public void setOrigemInformacaoPontuacao(String origemInformacaoPontuacao) {
        this.origemInformacaoPontuacao = origemInformacaoPontuacao;
    }

    public BooleanEnum getIndiceReincidenciaMAZ() {
        return indiceReincidenciaMAZ;
    }

    public void setIndiceReincidenciaMAZ(BooleanEnum indiceReincidenciaMAZ) {
        this.indiceReincidenciaMAZ = indiceReincidenciaMAZ;
    }

    public String getCodigoConfirmacaoMainFrame() {
        return codigoConfirmacaoMainFrame;
    }

    public void setCodigoConfirmacaoMainFrame(String codigoConfirmacaoMainFrame) {
        this.codigoConfirmacaoMainFrame = codigoConfirmacaoMainFrame;
    }
}