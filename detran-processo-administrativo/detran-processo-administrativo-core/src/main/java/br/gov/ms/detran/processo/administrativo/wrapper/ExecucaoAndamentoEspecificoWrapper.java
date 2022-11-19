package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import java.io.Serializable;

public class ExecucaoAndamentoEspecificoWrapper implements Serializable{

    private ProcessoAdministrativo processoAdministrativo;
    
    private String numeroProcesso;
    
    private Integer codigoAndamento;
    
    private Long idUsuario;
    
    private String urlBaseBirt;
    
    private String usuarioAlteracao;
    
    private Object objetoWrapper;

    public ExecucaoAndamentoEspecificoWrapper() {
    }

    public ExecucaoAndamentoEspecificoWrapper(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public ExecucaoAndamentoEspecificoWrapper(String numeroProcesso, Long idUsuario) {
        this.numeroProcesso     = numeroProcesso;
        this.idUsuario          = idUsuario;
    }

    public ExecucaoAndamentoEspecificoWrapper(ProcessoAdministrativo processoAdministrativo, Object objetoWrapper) {
        this.processoAdministrativo = processoAdministrativo;
        this.objetoWrapper          = objetoWrapper;
    }
    
    public ExecucaoAndamentoEspecificoWrapper(String numeroProcesso, Integer codigoAndamento, Long idUsuario, String url) {
        this.numeroProcesso     = numeroProcesso;
        this.codigoAndamento    = codigoAndamento;
        this.idUsuario          = idUsuario;
        this.urlBaseBirt        = url;
    }

    public ExecucaoAndamentoEspecificoWrapper(
            ProcessoAdministrativo processoAdministrativo, 
            Long idUsuario, 
            String urlBaseBirt, 
            Object objetoWrapper) {
        
        this.processoAdministrativo = processoAdministrativo;
        this.idUsuario              = idUsuario;
        this.urlBaseBirt            = urlBaseBirt;
        this.objetoWrapper          = objetoWrapper;
    }
    
    public ExecucaoAndamentoEspecificoWrapper(
            ProcessoAdministrativo processoAdministrativo, 
            Long idUsuario, 
            String urlBaseBirt, 
            Object objetoWrapper,
            String usuarioAlteracao) {
        
        this.processoAdministrativo = processoAdministrativo;
        this.idUsuario              = idUsuario;
        this.urlBaseBirt            = urlBaseBirt;
        this.objetoWrapper          = objetoWrapper;
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public String getNumeroProcesso() {
        if(processoAdministrativo != null && !DetranStringUtil.ehBrancoOuNulo(processoAdministrativo.getNumeroProcesso()))
            return processoAdministrativo.getNumeroProcesso();
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Integer getCodigoAndamento() {
        return codigoAndamento;
    }

    public void setCodigoAndamento(Integer codigoAndamento) {
        this.codigoAndamento = codigoAndamento;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public ProcessoAdministrativo getProcessoAdministrativo() {
        return processoAdministrativo;
    }

    public void setProcessoAdministrativo(ProcessoAdministrativo processoAdministrativo) {
        this.processoAdministrativo = processoAdministrativo;
    }

    public String getUrlBaseBirt() {
        return urlBaseBirt;
    }

    public void setUrlBaseBirt(String urlBaseBirt) {
        this.urlBaseBirt = urlBaseBirt;
    }

    public Object getObjetoWrapper() {
        return objetoWrapper;
    }

    public void setObjetoWrapper(Object objetoWrapper) {
        this.objetoWrapper = objetoWrapper;
    }

    public String getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(String usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    
    
}