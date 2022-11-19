package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.enums.ComissaoAnaliseEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoNotificacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.ResultadoRecursoEnum;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class RecursoWSWrapper {
    
    private String numeroProcesso;
    
    private String numeroProtocolo;
    
    private ResultadoRecursoEnum resultado;
    
    private RecursoNotificacaoAcaoEnum acao;
    
    private Integer motivoNaoConhecimento;
    
    private ComissaoAnaliseEnum comissaoAnalise;
    
    private Date dataJulgamento;
    
    private String usuario;
    
    private Long idUsuario;
    
    private String parecer;
    
    public RecursoWSWrapper() {
    }

    public RecursoWSWrapper(String numeroProcesso, String numeroProtocolo) {
        this.numeroProcesso = numeroProcesso;
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public ResultadoRecursoEnum getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoRecursoEnum resultado) {
        this.resultado = resultado;
    }

    public RecursoNotificacaoAcaoEnum getAcao() {
        return acao;
    }

    public void setAcao(RecursoNotificacaoAcaoEnum acao) {
        this.acao = acao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getDataJulgamento() {
        return dataJulgamento;
    }
    
    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setDataJulgamento(Date dataJulgamento) {
        this.dataJulgamento = dataJulgamento;
    }

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public Integer getMotivoNaoConhecimento() {
        return motivoNaoConhecimento;
    }

    public void setMotivoNaoConhecimento(Integer motivoNaoConhecimento) {
        this.motivoNaoConhecimento = motivoNaoConhecimento;
    }
    
    public ComissaoAnaliseEnum getComissaoAnalise() {
        return comissaoAnalise;
    }

    public void setComissaoAnalise(ComissaoAnaliseEnum comissaoAnalise) {
        this.comissaoAnalise = comissaoAnalise;
    }
}