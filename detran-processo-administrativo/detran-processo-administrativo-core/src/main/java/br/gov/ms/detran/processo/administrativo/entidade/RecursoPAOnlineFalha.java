package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntity;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "TB_TEO_RECURSO_PA_ONLINE_FALHA")
public class RecursoPAOnlineFalha extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Teo_ID")
    private Long id;

    @Column(name = "Teo_CPF")
    private String cpf;

    @Column(name = "Teo_Data")
    @Temporal(TemporalType.TIMESTAMP)
    private final Date data = Calendar.getInstance().getTime();

    @Column(name = "Teo_Numero_Processo")
    private String numeroProcesso;

    @Column(name = "Teo_Causa")
    private String causa;

    @Column(name = "Teo_Mensagem")
    private String mensagem;

    public RecursoPAOnlineFalha() {
    }
    
    public RecursoPAOnlineFalha(String exception, String numeroProcesso, String cpf, String causa) {
        this.mensagem       = exception;
        this.numeroProcesso = numeroProcesso;
        this.cpf            = cpf;
        this.causa          = causa;
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Date getData() {
        return data;
    }
}
