package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;
import java.io.Serializable;

public class NotificacaoParaIntegracaoAEMNPP07Wrapper implements Serializable {
    
    private Integer ordem;

    private TipoFasePaEnum tipoNotificacao;
    
    private TipoMotivoAEMNPP07Enum motivo;
    
    private Integer quantidade;

    public NotificacaoParaIntegracaoAEMNPP07Wrapper(Integer ordem,
                                                    TipoFasePaEnum tipoNotificacao, 
                                                    TipoMotivoAEMNPP07Enum motivo, 
                                                    Integer quantidade) {
        this.ordem = ordem;
        this.tipoNotificacao    = tipoNotificacao;
        this.motivo             = motivo;
        this.quantidade         = quantidade;
    }

    public TipoFasePaEnum getTipoNotificacao() {
        return tipoNotificacao;
    }

    public void setTipoNotificacao(TipoFasePaEnum tipoNotificacao) {
        this.tipoNotificacao = tipoNotificacao;
    }

    public TipoMotivoAEMNPP07Enum getMotivo() {
        return motivo;
    }

    public void setMotivo(TipoMotivoAEMNPP07Enum motivo) {
        this.motivo = motivo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}