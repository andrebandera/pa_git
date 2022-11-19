package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.projeto.anotacao.negocio.BusinessLogicalExclusion;
import br.gov.ms.detran.comum.util.adapter.CEPAdapter1;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.TipoEnvolvidoPAEnum;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

/**
 * @author Lillydi
 */
@Entity
@Table(name = "TB_TDR_PAD_ENDERECO_ALTERNATIVO")
@NamedQueries({
    @NamedQuery(
            name = "PAEnderecoAlternativo.validarEnderecoJaExistenteParaPA",
            query = "SELECT count(tdr.id) "
                    + " From PAEnderecoAlternativo tdr "
                    + "     INNER JOIN tdr.processoAdministrativo tdc "
                    + " where tdc.numeroProcesso = :p0 "
                    + "     and (:p1 is NULL OR tdr.id <> :p1) "
                    + "     and tdr.ativo = :p2"),
        @NamedQuery(
                name = "PAEnderecoAlternativo getEnderecoAtivoPorPA",
                query = "SELECT tdr "
                        + "FROM PAEnderecoAlternativo tdr "
                        + "INNER JOIN tdr.processoAdministrativo tdc "
                        + "where tdc.id = :p0 "
                        + "and tdr.ativo = :p1 "),
        @NamedQuery(name = "PAEnderecoAlternativo.getEnderecoPorPAETipoNotificacao",
                query = "select tdr From NotificacaoProcessoAdministrativo tcx " +
                        "   inner join tcx.endereco tdr " +
                        "   inner join tcx.processoAdministrativo tdc " +
                        "  where tdc.id = :p0 " +
                        "   and tcx.tipoNotificacaoProcesso = :p1 " +
                        "   and tcx.ativo = :p2 " +
                        "   and tdr.ativo = :p2 ")
})
@BusinessLogicalExclusion
public class PAEnderecoAlternativo  extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdr_ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tdr_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;
    
    @Column(name = "Tdr_Municipio")
    private Long municipio;
    
    @Column(name = "Tdr_Logradouro")
    private String logradouro;
    
    @Column(name = "Tdr_Complemento")
    private String complemento;
    
    @Column(name = "Tdr_Bairro")
    private String bairro;
    
    @Column(name = "Tdr_Numero")
    private String numero;
    
    @Column(name = "Tdr_CEP")
    private Integer cep;
    
    @Column(name = "Tdr_Tipo_Envolvido")
    @Enumerated(EnumType.STRING)
    private TipoEnvolvidoPAEnum tipoEnvolvido;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public PAEnderecoAlternativo() {
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

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public Long getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Long municipio) {
        this.municipio = municipio;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getCep() {
        return cep;
    }

    @XmlElement(name = "cep")
    @XmlJavaTypeAdapter(CEPAdapter1.class)
    public void setCep(Integer cep) {
        this.cep = cep;
    }

    public TipoEnvolvidoPAEnum getTipoEnvolvido() {
        return tipoEnvolvido;
    }

    public void setTipoEnvolvido(TipoEnvolvidoPAEnum tipoEnvolvido) {
        this.tipoEnvolvido = tipoEnvolvido;
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
    
        String tipoLabel = tipoEnvolvido == null ? "" : tipoEnvolvido.toString();
        if (TipoEnvolvidoPAEnum.CONDUTOR.equals(tipoEnvolvido)) {
            tipoLabel = "Endereço Alternativo";
        } else if (TipoEnvolvidoPAEnum.CONDUTOR_SISTEMA.equals(tipoEnvolvido)) {
            tipoLabel = "Endereço da Habilitação";
        }
        return tipoLabel;
    }
    
    public void setTipoLabel(String t){}
}