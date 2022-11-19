package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.PAParametroEnum;
import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TDL_PAD_COMPLEMENTO")
@NamedQueries({
    @NamedQuery(
        name = "PAComplemento.getPAComplementoParaConfirmaCancelamentoRecurso",
        query = "SELECT new PAComplemento(tdl.id) "
                + "FROM PAComplemento tdl "
                + "WHERE tdl.parametro = :p0 "
                + "AND tdl.ativo = :p1 "
                + "AND tdl.processoAdministrativo.id = :p2 "
                + "AND tdl.valor = :p3 "),
    @NamedQuery(
            name = "PAComplemento.getComplementoPorPA",
            query = "SELECT tdl "
                    + "FROM PAComplemento tdl "
                    + "INNER JOIN tdl.processoAdministrativo tdc "
                    + "WHERE tdc.id = :p0 "
                    + "AND tdl.ativo = :p1 "),
    @NamedQuery(
            name = "PAComplemento.getPAComplementoPorParametroEAtivoEProcessoAdministrativo",
            query = "SELECT tdl "
                    + "FROM PAComplemento tdl "
                    + "WHERE tdl.parametro = :p0 "
                    + " AND tdl.ativo = :p1 "
                    + " AND tdl.processoAdministrativo.id = :p2"),
    @NamedQuery(
            name = "PAComplemento.getPAComplementoPorNumeroPAEParametroEAtivo",
            query = "SELECT tdl "
                    + "FROM PAComplemento tdl "
                    + "WHERE tdl.parametro = :p0 "
                    + " AND tdl.ativo = :p1 "
                    + " AND tdl.processoAdministrativo.numeroProcesso = :p2"),
    @NamedQuery(
        name = "PAComplemento.getComplementoDesistenciaPorPA",
        query = "SELECT tdl "
                + "FROM PAComplemento tdl "
                + "WHERE tdl.parametro in (:p1) "
                + " AND tdl.ativo = :p2 "
                + " AND tdl.processoAdministrativo.id = :p0"),
    @NamedQuery(
            name = "PAComplemento.getSomaTempoPenalidadeDoCondutor",
            query = "SELECT SUM(CAST(tdl.valor as int)) "
                    + "FROM PAComplemento tdl "
                    + " INNER JOIN tdl.processoAdministrativo tdc "
                    + " where tdc.cpf = :p0 "
                    + " and tdc.origem = :p4 "
                    + " and tdl.parametro = :p1 "
                    + " and tdl.ativo = :p3 "
                    + " and EXISTS("
                    + "            SELECT 1 "
                    + "             From PAOcorrenciaStatus tde "
                    + "                 INNER JOIN tde.statusAndamento tcd "
                    + "                 INNER JOIN tcd.andamentoProcesso tcb "
                    + "             where tde.processoAdministrativo.id = tdc.id "
                    + "                 and tcb.codigo = :p2 "
                    + "                 and tde.ativo = :p3) "),
    @NamedQuery(
            name = "PAComplemento.getSomaTempoPenalidadeEmPJUDoCondutor",
            query = "SELECT SUM(CAST(tdl.valor as int)) "
                    + "FROM PAComplemento tdl "
                    + " INNER JOIN tdl.processoAdministrativo tdc "
                    + " where tdc.cpf = :p0 "
                    + " and tdc.origem = :p4 "
                    + " and tdl.parametro = :p1 "
                    + " and tdl.ativo = :p3 "
                    + " and EXISTS("
                    + "            SELECT 1 "
                    + "             From PAOcorrenciaStatus tde "
                    + "                 INNER JOIN tde.statusAndamento tcd "
                    + "                 INNER JOIN tcd.andamentoProcesso tcb "
                    + "             where tde.processoAdministrativo.id = tdc.id "
                    + "                 and tcb.codigo = :p2 "
                    + "                 and tde.ativo = :p3) "
                    + " and EXISTS(Select 1 From DadoProcessoJudicial tek "
                    + "     INNER JOIN tek.processoJudicial tej "
                    + "     where tej.processoAdministrativo = tdc.id and tek.indicativoPrazoIndeterminado = :p5 and tek.ativo = :p3)"),
    @NamedQuery(
            name = "PAComplemento.getSomaTempoPenalidadeTodosProcessosAtivos",
            query = "SELECT SUM(CAST(tdl.valor as int)) "
                    + "FROM PAComplemento tdl "
                    + "INNER JOIN tdl.processoAdministrativo tdc "
                    + "WHERE tdc.cpf = :p0 "
                    + "AND tdl.parametro = :p1 "
                    + "AND tdl.ativo = :p3 "
                    + "AND tdc.ativo = :p3 "
                    + "AND EXISTS( SELECT 1 "
                    + "             FROM PAOcorrenciaStatus tde "
                    + "             INNER JOIN tde.statusAndamento tcd "
                    + "             INNER JOIN tcd.andamentoProcesso tcb "
                    + "             INNER JOIN tcd.status tca "
                    + "             WHERE tde.processoAdministrativo.id = tdc.id "
                    + "             AND tca.codigo NOT IN (:p2) "
                    + "             AND tde.ativo = :p3) ")
})
public class PAComplemento extends BaseEntityAtivo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdl_ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "Tdl_Processo_Administrativo", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoAdministrativo;
    
//    @ManyToOne
//    @JoinColumn(name = "Tdl_Parametro", referencedColumnName = "Tcn_ID")
//    private PAParametro parametro;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "Tdl_Parametro")
    private PAParametroEnum parametro;
    
    @Column(name = "Tdl_Valor")
    private String valor;
    
    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public PAComplemento() {
    }

    public PAComplemento(Long id) {
        this.id = id;
    }

    public PAComplemento(ProcessoAdministrativo processoAdministrativo, PAParametroEnum parametro, String valor) {
        this.processoAdministrativo = processoAdministrativo;
        this.parametro = parametro;
        this.valor = valor;
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

    public PAParametroEnum getParametro() {
        return parametro;
    }

    public void setParametro(PAParametroEnum parametro) {
        this.parametro = parametro;
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
