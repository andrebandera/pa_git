package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
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
@Table(name = "TB_TDY_RESULTADO_LAUDO_PAD")
@NamedQueries({
    @NamedQuery(
            name = "ResultadoProvaPA.getResultadoProvaPAAtivoPorProcessoAdministrativo",
            query = "SELECT tdy "
                    + "FROM ResultadoProvaPA tdy "
                    + "WHERE tdy.processoAdministrativo.id = :p0 "
                    + "AND tdy.ativo = :p1 "
    )
})
public class ResultadoProvaPA extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdy_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Tdy_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;
    
    @Column(name = "Tdy_Resultado_Laudo")
    private Long resultadoLaudoSituacao;
    
    @Column(name = "Tdy_Tipo_Resultado_Exame")
    private Long tipoResultadoExame;
    
    @Column(name = "Tdy_Municipio")
    private Long municipio;
    
    @Column(name = "Tdy_Usuario")
    private Long usuario;
    
    @Column(name = "Tdy_Data_Prova")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataProva;
    
    @Column(name = "Tdy_Data_Cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public ResultadoProvaPA() {
    }

    public ResultadoProvaPA(Long id, Long resultadoLaudoSituacao) {
        this.id = id;
        this.resultadoLaudoSituacao = resultadoLaudoSituacao;
    }

    public ResultadoProvaPA(Long idResultadoLaudoSituacao, Date dataExame) {
        this.resultadoLaudoSituacao = idResultadoLaudoSituacao;
        this.dataProva = dataExame;
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

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public Long getResultadoLaudoSituacao() {
        return resultadoLaudoSituacao;
    }

    public void setResultadoLaudoSituacao(Long resultadoLaudoSituacao) {
        this.resultadoLaudoSituacao = resultadoLaudoSituacao;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public Long getTipoResultadoExame() {
        return tipoResultadoExame;
    }

    public void setTipoResultadoExame(Long tipoResultadoExame) {
        this.tipoResultadoExame = tipoResultadoExame;
    }

    public Long getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Long municipio) {
        this.municipio = municipio;
    }

    public Long getUsuario() {
        return usuario;
    }

    public void setUsuario(Long usuario) {
        this.usuario = usuario;
    }

    public Date getDataProva() {
        return dataProva;
    }
    
    @XmlElement(name = "dataProva")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataProva(Date dataProva) {
        this.dataProva = dataProva;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    @XmlElement(name = "dataCadastro")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}