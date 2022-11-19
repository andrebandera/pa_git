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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TER_CONSISTENCIA_BLOQUEIO_BCA_ATUALIZACAO")
public class ConsistenciaBloqueioBCAAtualizacao extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TER_ID")
    private Long id;
    
    @Column(name = "TER_CPF")
    private String cpf;
    
    @Column(name = "TER_REGISTRO")
    private String registro;
    
    @Column(name = "TER_NOME")
    private String nome;
    
    @Column(name = "TER_MUNICIPIO")
    private String municipio;
    
    @Column(name = "TER_CODIGO_BLOQUEIO")
    private String codigoBloqueio;
    
    @Column(name = "TER_CODIGO_UF_BLOQUEIO")
    private String codigoUfBloqueio;
    
    @Column(name = "TER_UF_BLOQUEIO")
    private String ufBloqueio;
    
    @Column(name = "TER_DATA_BLOQUEIO")
    private String dataBloqueio;
    
    @Column(name = "TER_DATA_INICIO_PENALIDADE")
    private String dataInicioPenalidade;
    
    @Column(name = "TER_TIPO_DECISAO")
    private String tipoDecisao;
    
    @Column(name = "TER_RECOLHIMENTO_CNH")
    private String recolhimentoCnh;
    
    @Column(name = "TER_REQUISITO_CURSO")
    private String requisitoCurso;
    
    @Column(name = "TER_REQUISITO_EXAME")
    private String requisitoExame;
    
    @Column(name = "TER_TIPO_PRAZO_PENALIDADE")
    private String tipoPrazoPenalidade;
    
    @Column(name = "TER_QTE_PENALIDADE_BLOQUEIO")
    private String qtdPenalidadeBloqueio;
    
    @Column(name = "TER_QTE_TOTAL_PENALIDADES")
    private String qteTotalPenalidades;
    
    @Column(name = "TER_DESCRICAO_BLOQUEIO")
    private String descricaoBloqueio;
    
    @Column(name = "TER_DOCUMENTO_GERADOR_BLOQUEIO")
    private String documentoGeradorBloqueio;
    
    @Column(name = "TER_MENSAGEM")
    private String mensagem;
    
    @Column(name = "TER_QTE")
    private String qte;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TER_ORIGEM")
    private ConsistenciaBloqueioBCAAtualizacaoOrigemEnum origem;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TER_ACAO")
    private ConsistenciaBloqueioBCAAtualizacaoAcaoEnum acao;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TER_SITUACAO")
    private ConsistenciaBloqueioBCAAtualizacaoSituacaoEnum situacao;
    
    @Column(name = "ATIVO")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;
    
    public ConsistenciaBloqueioBCAAtualizacao() {
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getCodigoBloqueio() {
        return codigoBloqueio;
    }

    public void setCodigoBloqueio(String codigoBloqueio) {
        this.codigoBloqueio = codigoBloqueio;
    }

    public String getCodigoUfBloqueio() {
        return codigoUfBloqueio;
    }

    public void setCodigoUfBloqueio(String codigoUfBloqueio) {
        this.codigoUfBloqueio = codigoUfBloqueio;
    }

    public String getUfBloqueio() {
        return ufBloqueio;
    }

    public void setUfBloqueio(String ufBloqueio) {
        this.ufBloqueio = ufBloqueio;
    }

    public String getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(String dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public String getDataInicioPenalidade() {
        return dataInicioPenalidade;
    }

    public void setDataInicioPenalidade(String dataInicioPenalidade) {
        this.dataInicioPenalidade = dataInicioPenalidade;
    }

    public String getTipoDecisao() {
        return tipoDecisao;
    }

    public void setTipoDecisao(String tipoDecisao) {
        this.tipoDecisao = tipoDecisao;
    }

    public String getRecolhimentoCnh() {
        return recolhimentoCnh;
    }

    public void setRecolhimentoCnh(String recolhimentoCnh) {
        this.recolhimentoCnh = recolhimentoCnh;
    }

    public String getRequisitoCurso() {
        return requisitoCurso;
    }

    public void setRequisitoCurso(String requisitoCurso) {
        this.requisitoCurso = requisitoCurso;
    }

    public String getRequisitoExame() {
        return requisitoExame;
    }

    public void setRequisitoExame(String requisitoExame) {
        this.requisitoExame = requisitoExame;
    }

    public String getTipoPrazoPenalidade() {
        return tipoPrazoPenalidade;
    }

    public void setTipoPrazoPenalidade(String tipoPrazoPenalidade) {
        this.tipoPrazoPenalidade = tipoPrazoPenalidade;
    }

    public String getQtdPenalidadeBloqueio() {
        return qtdPenalidadeBloqueio;
    }

    public void setQtdPenalidadeBloqueio(String qtdPenalidadeBloqueio) {
        this.qtdPenalidadeBloqueio = qtdPenalidadeBloqueio;
    }

    public String getQteTotalPenalidades() {
        return qteTotalPenalidades;
    }

    public void setQteTotalPenalidades(String qteTotalPenalidades) {
        this.qteTotalPenalidades = qteTotalPenalidades;
    }

    public String getDescricaoBloqueio() {
        return descricaoBloqueio;
    }

    public void setDescricaoBloqueio(String descricaoBloqueio) {
        this.descricaoBloqueio = descricaoBloqueio;
    }

    public String getDocumentoGeradorBloqueio() {
        return documentoGeradorBloqueio;
    }

    public void setDocumentoGeradorBloqueio(String documentoGeradorBloqueio) {
        this.documentoGeradorBloqueio = documentoGeradorBloqueio;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getQte() {
        return qte;
    }

    public void setQte(String qte) {
        this.qte = qte;
    }

    public ConsistenciaBloqueioBCAAtualizacaoOrigemEnum getOrigem() {
        return origem;
    }

    public void setOrigem(ConsistenciaBloqueioBCAAtualizacaoOrigemEnum origem) {
        this.origem = origem;
    }

    public ConsistenciaBloqueioBCAAtualizacaoAcaoEnum getAcao() {
        return acao;
    }

    public void setAcao(ConsistenciaBloqueioBCAAtualizacaoAcaoEnum acao) {
        this.acao = acao;
    }

    public ConsistenciaBloqueioBCAAtualizacaoSituacaoEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(ConsistenciaBloqueioBCAAtualizacaoSituacaoEnum situacao) {
        this.situacao = situacao;
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