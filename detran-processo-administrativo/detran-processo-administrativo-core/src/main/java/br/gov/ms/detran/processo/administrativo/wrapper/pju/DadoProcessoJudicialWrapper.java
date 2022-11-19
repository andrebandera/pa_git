package br.gov.ms.detran.processo.administrativo.wrapper.pju;

import br.gov.ms.detran.comum.core.projeto.entidade.adm.OrgaoBca;
import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoDocumento;
import br.gov.ms.detran.comum.core.projeto.entidade.vei.OrgaoJudicial;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@EntityMapping2(entity = {DadoProcessoJudicial.class})
@XmlRootElement
public class DadoProcessoJudicialWrapper implements IBaseEntity, IEntityResource<DadoProcessoJudicial> {
    
    private DadoProcessoJudicial entidade;
    
    private String orgaoBCA;
    
    private String orgaoJudicial;
    
    private String tribunal;
    
    private String usuario;
    
    private List<ProcessoAdministrativoWrapper> processosAdministrativosParaProcessoJuridico;
    
    private TipoDocumento tipoDocumento;
    
    private OrgaoJudicial orgaoJudicialEntidade;
    
    private OrgaoBca orgaoBcaEntidade;
    
    private Boolean indicativoPrazoIndeterminado;
    
    private String cpf;
    
    private TipoProcessoEnum tipoProcesso;
    
    private Long numeroDetran;
    
    private Boolean cnhEntregue;
    
    private Boolean isCondutor;
    
    private Date dataInicioPenalidade;
    
    private String dataEntregaCnh;
    
    public DadoProcessoJudicialWrapper() {
    }

    @Override
    public Long getId() {
        return entidade != null ? entidade.getId() : null;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {
        if (this.entidade == null) {
            this.entidade = new DadoProcessoJudicial();
        }
        this.entidade.setId(id);
    }

    @Override
    public DadoProcessoJudicial getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(DadoProcessoJudicial entidade) {
        this.entidade = entidade;
    }

    public String getOrgaoBCA() {
        return orgaoBCA;
    }

    public void setOrgaoBCA(String orgaoBCA) {
        this.orgaoBCA = orgaoBCA;
    }

    public String getOrgaoJudicial() {
        return orgaoJudicial;
    }

    public void setOrgaoJudicial(String orgaoJudicial) {
        this.orgaoJudicial = orgaoJudicial;
    }

    public String getTribunal() {
        return tribunal;
    }

    public void setTribunal(String tribunal) {
        this.tribunal = tribunal;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    public String getParteLabel(){
        if(this.entidade == null || this.entidade.getParteProcessoJuridico() == null){
        return "";
    }
        return this.entidade.getParteProcessoJuridico().toString();
    }
    
    public String getTipoMedidaLabel(){
        if(this.entidade == null || this.entidade.getTipoMedida()== null){
        return "";
    }
        return this.entidade.getTipoMedida().toString();
    }
    
    public String getDelitoLabel(){
        if(this.entidade == null || this.entidade.getIdentificacaoDelito()== null){
        return "";
    }
        return this.entidade.getIdentificacaoDelito().toString();
    }
    
    public String getRecolhimentoCnhLabel(){
        if(this.entidade == null || this.entidade.getIdentificacaoRecolhimentoCnh()== null){
        return "";
    }
        return this.entidade.getIdentificacaoRecolhimentoCnh().toString();
    }
    
    public String getRequisitoCursoLabel(){
        if(this.entidade == null || this.entidade.getRequisitoCursoBloqueio()== null){
        return "";
    }
        return this.entidade.getRequisitoCursoBloqueio().toString();
    }

    public List<ProcessoAdministrativoWrapper> getProcessosAdministrativosParaProcessoJuridico() {
        return processosAdministrativosParaProcessoJuridico;
    }

    public void setProcessosAdministrativosParaProcessoJuridico(List<ProcessoAdministrativoWrapper> processosAdministrativosParaProcessoJuridico) {
        this.processosAdministrativosParaProcessoJuridico = processosAdministrativosParaProcessoJuridico;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public OrgaoJudicial getOrgaoJudicialEntidade() {
        return orgaoJudicialEntidade;
    }

    public void setOrgaoJudicialEntidade(OrgaoJudicial orgaoJudicialEntidade) {
        this.orgaoJudicialEntidade = orgaoJudicialEntidade;
    }

    public OrgaoBca getOrgaoBcaEntidade() {
        return orgaoBcaEntidade;
    }

    public void setOrgaoBcaEntidade(OrgaoBca orgaoBcaEntidade) {
        this.orgaoBcaEntidade = orgaoBcaEntidade;
    }

    public Boolean getIndicativoPrazoIndeterminado() {
        return indicativoPrazoIndeterminado;
    }

    public void setIndicativoPrazoIndeterminado(Boolean indicativoPrazoIndeterminado) {
        this.indicativoPrazoIndeterminado = indicativoPrazoIndeterminado;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public TipoProcessoEnum getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcessoEnum tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public Long getNumeroDetran() {
        return numeroDetran;
    }

    public void setNumeroDetran(Long numeroDetran) {
        this.numeroDetran = numeroDetran;
    }

    public Boolean getCnhEntregue() {
        return cnhEntregue;
    }

    public void setCnhEntregue(Boolean cnhEntregue) {
        this.cnhEntregue = cnhEntregue;
    }

    public Boolean getIsCondutor() {
        return isCondutor;
    }

    public void setIsCondutor(Boolean isCondutor) {
        this.isCondutor = isCondutor;
    }

    public Date getDataInicioPenalidade() {
        return dataInicioPenalidade;
    }

    @XmlElement(name = "dataInicioPenalidade")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataInicioPenalidade(Date dataInicioPenalidade) {
        this.dataInicioPenalidade = dataInicioPenalidade;
    }

    public String getDataEntregaCnh() {
        return dataEntregaCnh;
    }

    public void setDataEntregaCnh(String dataEntregaCnh) {
        this.dataEntregaCnh = dataEntregaCnh;
    }

   
    
}
