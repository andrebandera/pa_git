package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.comum.util.adapter.DataInvertidaAdapter;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoMotivoEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class MovimentacaoPaWrapper {
    
    @XmlElement(name = "numeroprocessoadministrativo", nillable = true)
    String numeroProcessoAdministrativo;
    
    @XmlElement(name = "acao", nillable = true)
    MovimentacaoAcaoEnum acao;
    
    @XmlElement(name = "motivo", nillable = true)
    MovimentacaoMotivoEnum movimentacaoMotivo;
    
    @XmlElement(name = "data", nillable = true)
    Date data;
    
    @XmlElement(name = "cpfusuario", nillable = true)
    String cpfUsuario;
    
    @XmlElement(name = "observacao", nillable = true)
    String observacao;

    public MovimentacaoPaWrapper() {
    }
    
    public MovimentacaoPaWrapper(String numeroProcessoAdministrativo, 
                                 MovimentacaoAcaoEnum acao, 
                                 MovimentacaoMotivoEnum movimentacaoMotivo, 
                                 Date data, 
                                 String cpfUsuario, 
                                 String observacao) {
        this.numeroProcessoAdministrativo = numeroProcessoAdministrativo;
        this.acao = acao;
        this.movimentacaoMotivo = movimentacaoMotivo;
        this.data = data;
        this.cpfUsuario = cpfUsuario;
        this.observacao = observacao;
    }

    public String getNumeroProcessoAdministrativo() {
        return numeroProcessoAdministrativo;
    }

    public void setNumeroProcessoAdministrativo(String numeroProcessoAdministrativo) {
        this.numeroProcessoAdministrativo = numeroProcessoAdministrativo;
    }

    public MovimentacaoAcaoEnum getAcao() {
        return acao;
    }

    public void setAcao(MovimentacaoAcaoEnum acao) {
        this.acao = acao;
    }

    public MovimentacaoMotivoEnum getMovimentacaoMotivo() {
        return movimentacaoMotivo;
    }

    public void setMovimentacaoMotivo(MovimentacaoMotivoEnum movimentacaoMotivo) {
        this.movimentacaoMotivo = movimentacaoMotivo;
    }

    public Date getData() {
        return data;
    }
    
    @XmlJavaTypeAdapter(DataInvertidaAdapter.class)
    public void setData(Date data) {
        this.data = data;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    public void setCpfUsuario(String cpfUsuario) {
        this.cpfUsuario = cpfUsuario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}