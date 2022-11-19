package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;

public enum MovimentacaoMotivoEnum {
    
    CADASTRAMENTO_INDEVIDO, 
    MORTE, 
    DETERMINACAO_JUDICIAL, 
    PENA_CUMPRIDA, 
    PELO_DIRETOR_PRESIDENTE, 
    PELO_DIRETOR_ADJUNTO, 
    MOTIVO_ADMINISTRATIVO,
    SEPEN,
    RECURSO_ACOLHIDO,
    PROCESSO_APENSADO,
    PROCESSO_AGRAVADO,
    INCLUSAO_BLOQUEIO_CASSACAO,
    PROCESSO_PRESCRITO;
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}