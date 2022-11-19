package br.gov.ms.detran.processo.administrativo.wrapper.ws;

import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoAcaoEnum;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoMotivoEnum;

public class MovimentacaoPaWSWrapper {
    
    private String numeroProcessoAdministrativo;
    
    private MovimentacaoAcaoEnum acao;
    
    private MovimentacaoMotivoEnum motivo;

    public MovimentacaoPaWSWrapper() {
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

    public MovimentacaoMotivoEnum getMotivo() {
        return motivo;
    }

    public void setMotivo(MovimentacaoMotivoEnum movimentacaoMotivo) {
        this.motivo = movimentacaoMotivo;
    }
}