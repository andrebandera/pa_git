package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.ResultLong;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.hab.AcaoEntregaCnhEnum;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TDN_PAD_MOVIMENTO_CNH")
@NamedQueries({
    @NamedQuery(
            name = "MovimentoCnh.getDesistentePorProcessoAdministrativo",
            query = "SELECT "
                    + " new br.gov.ms.detran.processo.administrativo.wrapper.ws.ProcessoAdministrativoDesistenteRetornoWSWrapper("
                    + " tdc.numeroProcesso, "
                    + " tdm.numeroProtocolo, "
                    + " tdc.cpf, "
                    + " tdm.dataProtocolo, "
                    + " tdn.cnhControle ) "
                    + "FROM MovimentoCnh tdn "
                    + "INNER JOIN tdn.protocolo tdm "
                    + "INNER JOIN tdm.numeroProcesso tdc "
                    + "WHERE EXISTS(SELECT 1 "
                    + "             FROM PAOcorrenciaStatus tde "
                    + "             INNER JOIN tde.statusAndamento tcd "
                    + "             INNER JOIN tcd.andamentoProcesso tcb "
                    + "             WHERE tcb.codigo = :p0 "
                    + "             AND tde.processoAdministrativo.id = tdc.id) "
                    + "AND tdn.ativo = :p1 "
                    + "AND tdm.ativo = :p1 "
                    + "AND tdc.id = :p2 "),
    @NamedQuery(
            name = "MovimentoCnh.getMovimentoEntregaPorCpf",
            query = "SELECT tdn FROM MovimentoCnh tdn INNER JOIN tdn.protocolo tdm INNER JOIN tdm.numeroProcesso tdc WHERE tdc.cpf = :p0 AND tdn.acao = :p1 "),
    @NamedQuery(
            name = "MovimentoCnh.getListaMovimentoCnhPorProcessoAdministrativo",
            query = "SELECT tdn FROM MovimentoCnh tdn INNER JOIN tdn.protocolo tdm WHERE tdm.numeroProcesso.id = :p0 "),
    @NamedQuery(
            name = "MovimentoCnh.getCnhControlePorProcessoAdministrativoEAcaoEntrega",
            query = "SELECT new MovimentoCnh(tdn.id, tdn.cnhControle) "
                    + "FROM MovimentoCnh tdn "
                    + "INNER JOIN tdn.protocolo tdm "
                    + "INNER JOIN tdm.numeroProcesso tdc "
                    + "WHERE tdc.id = :p0 "
                    + " AND tdn.acao = :p1 "
                    + " AND tdn.ativo = :p2 "
                    + " AND tdm.ativo = :p2 "),
    @NamedQuery(
            name = "MovimentoCnh.getMovimentoPorProcessoAdministrativoEAcao",
            query = "SELECT tdn FROM MovimentoCnh tdn INNER JOIN tdn.protocolo tdm INNER JOIN tdm.numeroProcesso tdc where tdc.id = :p0 and tdn.acao = :p1 and tdm.ativo = :p2 and tdn.ativo = :p2 "
    ),
    @NamedQuery(
            name = "MovimentoCnh.getMovimentoCnhPorNumeroProcessoAdministrativo",
            query = "SELECT tdn FROM MovimentoCnh tdn INNER JOIN tdn.protocolo tdm WHERE tdm.numeroProcesso.numeroProcesso = :p0 AND tdn.ativo = :p1 "),
    @NamedQuery(
            name = "MovimentoCnh.getListEntregaCnh",
            query = "SELECT tdn FROM MovimentoCnh tdn  "
                    + " INNER JOIN tdn.protocolo tdm "
                    + " INNER JOIN tdm.numeroProcesso tdc "
                    + "WHERE EXISTS("
                    + "         SELECT 1 "
                    + "         FROM PAOcorrenciaStatus tde "
                    + "             inner join tde.statusAndamento tcd "
                    + "             INNER JOIN tcd.andamentoProcesso tcb "
                    + "         where ((tdn.acao = :p0 and tcb.codigo = :p1) OR (tdn.acao = :p2 and tcb.codigo = :p3)) "
                    + "             and tde.processoAdministrativo.id = tdc.id) "
                    + "     and tdn.ativo = :p4 "
                    + "     and tdm.ativo = :p4 "),
    @NamedQuery(name = "MovimentoCnh.getDesistentes",
            query = "SELECT "
                    + " new br.gov.ms.detran.processo.administrativo.wrapper.ws.ProcessoAdministrativoDesistenteRetornoWSWrapper("
                    + "         tdc.numeroProcesso, "
                    + "         tdm.numeroProtocolo, "
                    + "         tdc.cpf, "
                    + "         tdm.dataProtocolo, "
                    + "         tdn.cnhControle "
                    + " ) "
                    + "From MovimentoCnh tdn "
                    + " INNER JOIN tdn.protocolo tdm "
                    + " INNER JOIN tdm.numeroProcesso tdc "
                    + "WHERE EXISTS("
                    + "         SELECT 1 "
                    + "         FROM PAOcorrenciaStatus tde "
                    + "             inner join tde.statusAndamento tcd "
                    + "             INNER JOIN tcd.andamentoProcesso tcb "
                    + "         where tcb.codigo = :p0 "
                    + "             and tde.processoAdministrativo.id = tdc.id) "
                    + "     and tdn.ativo = :p1 "
                    + "     and tdm.ativo = :p1 ")
})
@NamedNativeQueries({
    @NamedNativeQuery(
            name = "MovimentoCnh.getMovimentoPorControleCnhEAcaoEntregaParaDesbloqueio",
            query = "SELECT tdn.* "
                    + "FROM dbo.TB_TDN_PAD_MOVIMENTO_CNH tdn "
                    + " INNER JOIN dbo.TB_BAB_CNH_CONTROLE bab on tdn.Tdn_CNH_Controle = bab.Bab_ID "
                    + " INNER JOIN dbo.TB_BAC_CNH_SITUACAO_ENTREGA bac on bac.Bac_CNH_Controle = bab.Bab_ID "
                    + " INNER JOIN dbo.TB_TDM_PAD_PROTOCOLO tdm on tdn.Tdn_Protocolo = tdm.Tdm_ID "
                    + "WHERE bab.Bab_ID = :p0 "
                    + " AND bac.Bac_Acao = :p1 "
                    + " AND tdn.Tdn_Acao = :p1 "
                    + " AND tdn.Ativo = :p4 "
                    + " AND tdm.Ativo = :p4 "
                    + " AND exists(select 1 "
                    + "             from dbo.TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde "
                    + "                 inner join dbo.TB_TCD_PAD_STATUS_ANDAMENTO tcd on tde.Tde_Status_Andamento = tcd.Tcd_ID "
                    + "                 inner join dbo.TB_TCB_PAD_ANDAMENTO_PROCESSO tcb on tcd.Tcd_Andamento_Processo = tcb.Tcb_ID "
                    + "                 inner join dbo.TB_TCA_PAD_STATUS tca on tcd.Tcd_Status = tca.Tca_ID "
                    + "		where tde.Tde_Processo_Administrativo = tdm.Tdm_Numero_Processo "
                    + "                 and tcb.Tcb_Codigo not in (:p2) "
                    + "                 and tca.Tca_Codigo not in (:p3)) ",
            resultClass = MovimentoCnh.class),
    @NamedNativeQuery(
            name = "MovimentoCnh.getMovimentoCnhParaDesentranhamentoPorCpfCondutor",
            query = "SELECT tdn.*  From TB_TDN_PAD_MOVIMENTO_CNH tdn  "
                    + "	inner JOIN TB_BAB_CNH_CONTROLE bab on tdn.Tdn_CNH_Controle = bab.Bab_ID and tdn.Ativo = :p2 "
                    + "	inner join TB_BAC_CNH_SITUACAO_ENTREGA bac on bab.Bab_ID = bac.Bac_CNH_Controle and bac.Ativo = :p2 "
                    + "	inner join TB_TBC_SETOR_CORRESPONDENCIA tbc on bac.Bac_Setor = tbc.Tbc_ID "
                    + "	where  "
                    + "		tbc.Tbc_Sigla = :p1 "
                    + "		and bab.Bab_ID = :p0  "
                    + "		and bac.Bac_Acao = 0  "
                    + "		and not EXISTS (SELECT 1  "
                    + "                         From TB_BAC_CNH_SITUACAO_ENTREGA bac1  "
                    + "				where bac1.Bac_CNH_Controle = bab.Bab_ID  "
                    + "                             and bac1.Bac_Acao = 1  "
                    + "                             and bac1.Ativo = :p2);  ",
            resultClass = MovimentoCnh.class),
    @NamedNativeQuery(
            name = "MovimentoCnh.getMovimentoPorControleCnhETipo",
            query = "SELECT tdn.* "
                    + "FROM dbo.TB_TDN_PAD_MOVIMENTO_CNH tdn "
                    + " INNER JOIN dbo.TB_BAB_CNH_CONTROLE bab on tdn.Tdn_CNH_Controle = bab.Bab_ID "
                    + " INNER JOIN dbo.TB_BAC_CNH_SITUACAO_ENTREGA bac on bac.Bac_CNH_Controle = bab.Bab_ID "
                    + "WHERE bab.Bab_ID = :p0 "
                    + " AND bac.Bac_Acao = :p1 "
                    + " AND tdn.Tdn_Acao = :p1 ",
            resultClass = MovimentoCnh.class),
    @NamedNativeQuery(
            name = "MovimentoCnh.getMovimentoCnhParaDesentranhamento",
            query = "SELECT tdn.* "
                    + "FROM dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc "
                    + "INNER JOIN dbo.TB_TDM_PAD_PROTOCOLO tdm ON tdm.Tdm_Numero_Processo = tdc.Tdc_ID "
                    + "INNER JOIN dbo.TB_TDN_PAD_MOVIMENTO_CNH tdn ON tdn.Tdn_Protocolo = tdm.Tdm_ID AND tdn.Ativo = 1 "
                    + "INNER JOIN dbo.TB_BAB_CNH_CONTROLE bab ON bab.Bab_ID = tdn.Tdn_CNH_Controle AND bab.Ativo = 1 "
                    + "INNER JOIN dbo.TB_BAC_CNH_SITUACAO_ENTREGA bac ON bac.Bac_CNH_Controle = bab.Bab_ID AND bac.Bac_Acao = 0 AND bac.Ativo = 1 "
                    + "WHERE bab.Bab_ID NOT IN (SELECT bab2.Bab_ID "
                    + "                         from dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc2 "
                    + "                         INNER JOIN dbo.TB_TDM_PAD_PROTOCOLO tdm2 on tdm2.Tdm_Numero_Processo = tdc2.Tdc_ID "
                    + "                         INNER JOIN dbo.TB_TDN_PAD_MOVIMENTO_CNH tdn2 on tdn2.Tdn_Protocolo = tdm2.Tdm_ID and tdn2.Ativo = 1"
                    + "                         INNER JOIN dbo.TB_BAB_CNH_CONTROLE bab2 on bab2.Bab_ID = tdn2.Tdn_CNH_Controle and bab2.Ativo = 1 "
                    + "                         INNER JOIN dbo.TB_BAC_CNH_SITUACAO_ENTREGA bac2 on bac2.Bac_CNH_Controle = bab2.Bab_ID and bac2.Bac_Acao = 1 and bac2.Ativo = 1) "
                    + "AND tdc.Tdc_Numero_Processo = :p0 ",
            resultClass = MovimentoCnh.class),
    @NamedNativeQuery(
            name = "MovimentoCnh.getMovimentoCnhComCnhControleValido",
            query = "SELECT tdn.* FROM TB_TDN_PAD_MOVIMENTO_CNH tdn "
                    + "     INNER JOIN TB_TDM_PAD_PROTOCOLO tdm ON tdn.Tdn_Protocolo = tdm.Tdm_ID and tdm.Ativo = 1 "
                    + "     INNER JOIN TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tdm.Tdm_Numero_Processo = tdc.Tdc_ID and tdc.Ativo = 1 "
                    + "     INNER JOIN TB_BAB_CNH_CONTROLE bab on bab.Bab_ID = tdn.Tdn_CNH_Controle and bab.Ativo = 1 "
                    + "     INNER JOIN TB_BAC_CNH_SITUACAO_ENTREGA bac on bac.Bac_CNH_Controle = bab.Bab_ID and bac.Ativo = 1 "
                    + "     INNER JOIN TB_BAA_CNH_CONTROLE_PESSOA baa on baa.Baa_ID = bac.Bac_Pessoa_Nome and baa.Ativo = 1 "
                    + "WHERE tdn.Tdn_Acao = :p4 AND tdn.Ativo = :p5 "
                    + "AND (:p0 IS NULL OR tdc.Tdc_Numero_Processo = :p0) "
                    + "AND (:p1 IS NULL OR baa.Baa_CPF_Entrega = :p1) "
                    + "AND (:p2 IS NULL OR bab.Bab_Numero_Registro = :p2) "
                    + "AND (:p3 IS NULL OR bab.Bab_Numero_CNH = :p3) "
                    + "AND DATEDIFF(DAY, CONVERT(datetime, bab.Bab_Validade_Cnh), CONVERT(datetime, getDate())) <= 0 ",
            resultClass = MovimentoCnh.class),
     @NamedNativeQuery(
            name = "MovimentoCnh.getCountMovimentoCnhComCnhControleValido",
            query = "SELECT COUNT(*) as resultado FROM TB_TDN_PAD_MOVIMENTO_CNH tdn "
                    + "     INNER JOIN TB_TDM_PAD_PROTOCOLO tdm ON tdn.Tdn_Protocolo = tdm.Tdm_ID and tdm.Ativo = 1 "
                    + "     INNER JOIN TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tdm.Tdm_Numero_Processo = tdc.Tdc_ID and tdc.Ativo = 1 "
                    + "     INNER JOIN TB_BAB_CNH_CONTROLE bab on bab.Bab_ID = tdn.Tdn_CNH_Controle and bab.Ativo = 1 "
                    + "     INNER JOIN TB_BAC_CNH_SITUACAO_ENTREGA bac on bac.Bac_CNH_Controle = bab.Bab_ID and bac.Ativo = 1 "
                    + "     INNER JOIN TB_BAA_CNH_CONTROLE_PESSOA baa on baa.Baa_ID = bac.Bac_Pessoa_Nome and baa.Ativo = 1 "
                    + "WHERE tdn.Tdn_Acao = :p4 AND tdn.Ativo = :p5 "
                    + "AND (:p0 IS NULL OR tdc.Tdc_Numero_Processo = :p0) "
                    + "AND (:p1 IS NULL OR baa.Baa_CPF_Entrega = :p1) "
                    + "AND (:p2 IS NULL OR bab.Bab_Numero_Registro = :p2) "
                    + "AND (:p3 IS NULL OR bab.Bab_Numero_CNH = :p3) "
                    + "AND DATEDIFF(DAY, CONVERT(datetime, bab.Bab_Validade_Cnh), CONVERT(datetime, getDate())) <= 0 ",
            resultClass = ResultLong.class),
    @NamedNativeQuery(
            name = "MovimentoCnh.getMovimentoCnhComCnhControleVencido",
            query = "SELECT tdn.* FROM TB_TDN_PAD_MOVIMENTO_CNH tdn "
                    + "     INNER JOIN TB_TDM_PAD_PROTOCOLO tdm ON tdn.Tdn_Protocolo = tdm.Tdm_ID and tdm.Ativo = 1 "
                    + "     INNER JOIN TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tdm.Tdm_Numero_Processo = tdc.Tdc_ID and tdc.Ativo = 1 "
                    + "     INNER JOIN TB_BAB_CNH_CONTROLE bab on bab.Bab_ID = tdn.Tdn_CNH_Controle and bab.Ativo = 1 "
                    + "     INNER JOIN TB_BAC_CNH_SITUACAO_ENTREGA bac on bac.Bac_CNH_Controle = bab.Bab_ID and bac.Ativo = 1 "
                    + "     INNER JOIN TB_BAA_CNH_CONTROLE_PESSOA baa on baa.Baa_ID = bac.Bac_Pessoa_Nome and baa.Ativo = 1 "
                    + "WHERE tdn.Tdn_Acao = :p4 AND tdn.Ativo = :p5 "
                    + "AND (:p0 IS NULL OR tdc.Tdc_Numero_Processo = :p0) "
                    + "AND (:p1 IS NULL OR baa.Baa_CPF_Entrega = :p1) "
                    + "AND (:p2 IS NULL OR bab.Bab_Numero_Registro = :p2) "
                    + "AND (:p3 IS NULL OR bab.Bab_Numero_CNH = :p3) "
                    + "AND DATEDIFF(DAY, CONVERT(datetime, bab.Bab_Validade_Cnh), CONVERT(datetime, getDate())) > 0 ",
            resultClass = MovimentoCnh.class),
    @NamedNativeQuery(
            name = "MovimentoCnh.getCountMovimentoCnhComCnhControleVencido",
            query = "SELECT COUNT(*) as resultado  FROM TB_TDN_PAD_MOVIMENTO_CNH tdn "
                    + "     INNER JOIN TB_TDM_PAD_PROTOCOLO tdm ON tdn.Tdn_Protocolo = tdm.Tdm_ID and tdm.Ativo = 1 "
                    + "     INNER JOIN TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc ON tdm.Tdm_Numero_Processo = tdc.Tdc_ID and tdc.Ativo = 1 "
                    + "     INNER JOIN TB_BAB_CNH_CONTROLE bab on bab.Bab_ID = tdn.Tdn_CNH_Controle and bab.Ativo = 1 "
                    + "     INNER JOIN TB_BAC_CNH_SITUACAO_ENTREGA bac on bac.Bac_CNH_Controle = bab.Bab_ID and bac.Ativo = 1 "
                    + "     INNER JOIN TB_BAA_CNH_CONTROLE_PESSOA baa on baa.Baa_ID = bac.Bac_Pessoa_Nome and baa.Ativo = 1 "
                    + "WHERE tdn.Tdn_Acao = :p4 AND tdn.Ativo = :p5 "
                    + "AND (:p0 IS NULL OR tdc.Tdc_Numero_Processo = :p0) "
                    + "AND (:p1 IS NULL OR baa.Baa_CPF_Entrega = :p1) "
                    + "AND (:p2 IS NULL OR bab.Bab_Numero_Registro = :p2) "
                    + "AND (:p3 IS NULL OR bab.Bab_Numero_CNH = :p3) "
                    + "AND DATEDIFF(DAY, CONVERT(datetime, bab.Bab_Validade_Cnh), CONVERT(datetime, getDate())) > 0 ",
            resultClass = ResultLong.class)
})
public class MovimentoCnh extends BaseEntityAtivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Tdn_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Tdn_Protocolo", referencedColumnName = "Tdm_ID")
    private Protocolo protocolo;

    @ManyToOne
    @JoinColumn(name = "Tdn_Processo_Original", referencedColumnName = "Tdc_ID")
    private ProcessoAdministrativo processoOriginal;

    @Column(name = "Tdn_CNH_Controle")
    private Long cnhControle;

    @Column(name = "Tdn_Acao")
    @Enumerated(EnumType.ORDINAL)
    private AcaoEntregaCnhEnum acao;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo = AtivoEnum.ATIVO;

    public MovimentoCnh() {
    }

    public MovimentoCnh(Long id) {
        this.id = id;
    }

    public MovimentoCnh(Long id, Long cnhControle) {
        this(id);
        this.cnhControle = cnhControle;
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

    public Protocolo getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(Protocolo protocolo) {
        this.protocolo = protocolo;
    }

    public Long getCnhControle() {
        return cnhControle;
    }

    public void setCnhControle(Long cnhControle) {
        this.cnhControle = cnhControle;
    }

    public ProcessoAdministrativo getProcessoOriginal() {
        return processoOriginal;
    }

    public void setProcessoOriginal(ProcessoAdministrativo processoOriginal) {
        this.processoOriginal = processoOriginal;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public AcaoEntregaCnhEnum getAcao() {
        return acao;
    }

    public void setAcao(AcaoEntregaCnhEnum acao) {
        this.acao = acao;
    }

}
