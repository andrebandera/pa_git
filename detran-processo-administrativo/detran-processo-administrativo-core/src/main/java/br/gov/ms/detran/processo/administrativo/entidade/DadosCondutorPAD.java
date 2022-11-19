package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.enums.adm.SexoEnum;
import br.gov.ms.detran.comum.entidade.BaseEntity;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Lillydi
 */
@Entity
@Table(name = "TB_TDA_PAD_DADOS_CONDUTOR")
@NamedQueries ({
    @NamedQuery(
        name = "DadosCondutorPAD.findAll",
        query = "SELECT tda FROM DadosCondutorPAD tda "),
    @NamedQuery(
        name = "DadosCondutorPAD.findAllByUsuarioInclusao",
        query = "SELECT tda FROM DadosCondutorPAD tda"),
    @NamedQuery(
        name = "DadosCondutorPAD.findAllId",
        query = "SELECT NEW br.gov.ms.detran.processo.administrativo.wrapper.DadosCondutorWrapper(e.id) FROM DadosCondutorPAD e"
    )
})
public class DadosCondutorPAD extends BaseEntity implements Serializable {
    
    @Id
    @Column(name = "Tda_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "Tda_Nome_Condutor")
    private String nomeCondutor;
    
    @Column(name = "Tda_Sexo")
    @Enumerated(EnumType.ORDINAL)
    private SexoEnum sexo;
    
    @Column(name = "Tda_Numero_PGU")
    private String numeroPgu;
    
    @Column(name = "Tda_Numero_Registro")
    private String numeroRegistro;
    
    @Column(name = "Tda_Numero_CPF")
    private String cpf;
    
    @Column(name = "Tda_Numero_CNH")
    private Long numeroCnh;
    
    @Column(name = "Tda_Data_Primeira_Habilitacao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPrimeiraHabilitacao;
    
    @Column(name = "Tda_Data_Habilitacao_Definitiva")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHabilitacaoDefinitiva;
    
    @Column(name = "Tda_Numero_Permissionado")
    private String numeroPermissionado;

    public DadosCondutorPAD() {
    }
    
    public DadosCondutorPAD(Long id) {
        this.id = id;
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

    public String getNomeCondutor() {
        return nomeCondutor;
    }

    public void setNomeCondutor(String nomeCondutor) {
        this.nomeCondutor = nomeCondutor;
    }

    public SexoEnum getSexo() {
        return sexo;
    }

    public void setSexo(SexoEnum sexo) {
        this.sexo = sexo;
    }

    public String getNumeroPgu() {
        return numeroPgu;
    }

    public void setNumeroPgu(String numeroPgu) {
        this.numeroPgu = numeroPgu;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Long getNumeroCnh() {
        return numeroCnh;
    }

    public void setNumeroCnh(Long numeroCnh) {
        this.numeroCnh = numeroCnh;
    }

    public Date getDataPrimeiraHabilitacao() {
        return dataPrimeiraHabilitacao;
    }

    public void setDataPrimeiraHabilitacao(Date dataPrimeiraHabilitacao) {
        this.dataPrimeiraHabilitacao = dataPrimeiraHabilitacao;
    }

    public Date getDataHabilitacaoDefinitiva() {
        return dataHabilitacaoDefinitiva;
    }

    public void setDataHabilitacaoDefinitiva(Date dataHabilitacaoDefinitiva) {
        this.dataHabilitacaoDefinitiva = dataHabilitacaoDefinitiva;
    }

    public String getNumeroPermissionado() {
        return numeroPermissionado;
    }

    public void setNumeroPermissionado(String numeroPermissionado) {
        this.numeroPermissionado = numeroPermissionado;
    }
}