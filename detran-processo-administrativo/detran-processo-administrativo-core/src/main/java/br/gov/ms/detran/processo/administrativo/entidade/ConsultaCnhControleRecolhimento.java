package br.gov.ms.detran.processo.administrativo.entidade;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

/**
 * @author <a href="mailto:fantonio@detran.ms.gov.br">Felipe Alves</a>
 * @since 10/06/2020
 */
@Entity
@NamedNativeQueries({
    @NamedNativeQuery(
            name = "ConsultaCnhControleRecolhimento.buscarCnhControleSemValidade",
            query = "SELECT DISTINCT bab.Bab_ID AS CNH_CONTROLE_ID, "
            + "tdc.Tdc_Cpf AS NUMERO_DOCUMENTO, "
            + "bab.Bab_Numero_Registro AS NUMERO_REGISTRO, "
            + "bab.Bab_Numero_CNH AS NUMERO_CNH "
            + "FROM TB_TDE_PAD_OCORRENCIA_STATUS_PROCESSO_ADM tde "
            + "     inner join TB_TCD_PAD_STATUS_ANDAMENTO tcd on tcd.Tcd_Id = Tde_Status_Andamento "
            + "     inner join TB_TCB_PAD_ANDAMENTO_PROCESSO tcb on tcb.Tcb_ID = tcd.Tcd_Andamento_Processo "
            + "     inner join TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc on tdc.Tdc_ID = tde.Tde_Processo_Administrativo "
            + "     inner join TB_TDM_PAD_PROTOCOLO tdm on tdm.Tdm_Numero_Processo = tdc.Tdc_ID and tdm.Ativo = 1 "
            + "     inner join TB_XHV_TEMPLATE_PROTOCOLO xhv on xhv.XHV_ID = tdm.Tdm_Template_Protocolo and xhv.Ativo = 1 "
            + "     inner join TB_TDN_PAD_MOVIMENTO_CNH tdn ON tdn.Tdn_Protocolo = tdm.Tdm_ID and tdn.Ativo = 1 "
            + "     inner join TB_BAB_CNH_CONTROLE bab on bab.Bab_ID = tdn.Tdn_CNH_Controle and bab.Ativo = 1 "
            + "     inner join TB_BAC_CNH_SITUACAO_ENTREGA bac on bac.Bac_CNH_Controle = bab.Bab_ID and bac.Ativo = 1 "
            + "     inner join TB_BAA_CNH_CONTROLE_PESSOA baa on baa.Baa_ID = bac.Bac_Pessoa_Nome and baa.Ativo = 1 "
            + "WHERE bab.Bab_Validade_Cnh is null "
            + "and tcb.Tcb_Codigo NOT IN(:p2)"
            + "AND xhv.XHV_Tipo_Situacao = :p0 "
            + "AND bac.Bac_Acao = :p1 "
            + "AND tdn.Tdn_Acao = :p1 "
            + "Order by bab.Bab_ID, tdc.Tdc_Cpf",
            resultClass = ConsultaCnhControleRecolhimento.class),
    @NamedNativeQuery(
            name = "ConsultaCnhControleRecolhimento.buscarRecolhimentoCnh",
            query = "SELECT DISTINCT bab.Bab_ID AS CNH_CONTROLE_ID, "
            + "tdc.Tdc_Cpf AS NUMERO_DOCUMENTO, "
            + "bab.Bab_Numero_Registro AS NUMERO_REGISTRO, "
            + "bab.Bab_Numero_CNH AS NUMERO_CNH "
            + "from  dbo.TB_TDC_PAD_PROCESSO_ADMINISTRATIVO tdc "
            + "inner join dbo.TB_TDM_PAD_PROTOCOLO tdm on tdm.Tdm_Numero_Processo = tdc.Tdc_ID and tdm.Ativo = 1 "
            + "inner join dbo.TB_TCT_ARQUIVO_PA tct on tct.Tct_ID = tdm.Tdm_Arquivo_Pa "
            + "inner join dbo.TB_XHV_TEMPLATE_PROTOCOLO xhv on tdm.Tdm_Template_Protocolo = xhv.XHV_ID and xhv.Ativo = 1 "
            + "inner join dbo.TB_TDN_PAD_MOVIMENTO_CNH tdn on tdn.Tdn_Protocolo = tdm.Tdm_ID and tdn.Ativo = 1 "
            + "inner join dbo.TB_BAB_CNH_CONTROLE bab on bab.Bab_ID = tdn.Tdn_CNH_Controle and bab.Ativo = 1 "
            + "inner join dbo.TB_BAC_CNH_SITUACAO_ENTREGA bac on bac.Bac_CNH_Controle = tdn.Tdn_CNH_Controle and bac.Ativo = 1 "
            + "inner join dbo.TB_BAA_CNH_CONTROLE_PESSOA baa on baa.Baa_ID = bac.Bac_Pessoa_Nome and baa.Ativo = 1 "
            + "inner join dbo.TB_TDK_BLOQUEIO_BCA tdk on tdk.Tdk_Processo_Administrativo = tdc.Tdc_ID and tdk.Ativo = 1 "
            + "inner join dbo.TB_TEA_PAD_PENALIDADE_PROCESSO_PAT tea on tea.Tea_Processo_Administrativo = tdc.Tdc_ID and tea.Ativo = 1 "
            + "inner join dbo.TB_TDL_PAD_COMPLEMENTO tdl on tdl.Tdl_Processo_Administrativo = tdc.Tdc_ID and tdl.ATIVO = 1 "
            + "where not EXISTS (SELECT 1 from dbo.TB_TDN_PAD_MOVIMENTO_CNH tdn where tdn.TDN_ACAO = 1 and tdn.TDN_CNH_CONTROLE = bab.BAB_ID and tdn.ATIVO = 1) "
            + "and bac.BAC_SETOR  = 162 "
            + "and tdc.Tdc_ID = :p0 ",
            resultClass = ConsultaCnhControleRecolhimento.class)
})
public class ConsultaCnhControleRecolhimento implements Serializable {

    @Id
    @Column(name = "CNH_CONTROLE_ID")
    private Long cnhControleId;

    @Column(name = "NUMERO_DOCUMENTO")
    private String numeroDocumento;

    @Column(name = "NUMERO_REGISTRO")
    private String numeroRegistro;

    @Column(name = "NUMERO_CNH")
    private Long numeroCnh;

    public ConsultaCnhControleRecolhimento() {
    }

    public ConsultaCnhControleRecolhimento(Long cnhControleId, String numeroDocumento, String numeroRegistro, Long numeroCnh) {
        this.cnhControleId = cnhControleId;
        this.numeroDocumento = numeroDocumento;
        this.numeroRegistro = numeroRegistro;
        this.numeroCnh = numeroCnh;
    }

    public Long getCnhControleId() {
        return cnhControleId;
    }

    public void setCnhControleId(Long cnhControleId) {
        this.cnhControleId = cnhControleId;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public Long getNumeroCnh() {
        return numeroCnh;
    }

    public void setNumeroCnh(Long numeroCnh) {
        this.numeroCnh = numeroCnh;
    }

}
