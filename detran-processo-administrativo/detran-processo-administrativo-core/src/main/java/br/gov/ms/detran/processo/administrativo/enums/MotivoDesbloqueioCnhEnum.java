/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Lillydi
 */
public enum MotivoDesbloqueioCnhEnum {

    PENA_CUMPRIDA("1"),
    PENA_ANULADA("2"),
    REABILITACAO("3"),
    LIBERADO_COM_RESTRICAO_JUDICIAL("4"),
    DETERMINACAO_JUDICIAL("5"),
    NOVO_PROCESSO_HABILITACAO("6"),
    ERRO_LANCAMENTO("7"),
    APTO("8");

    private final String motivo;

    private static final Map<String, MotivoDesbloqueioCnhEnum> lookup = new HashMap<>();

    static {
        for (MotivoDesbloqueioCnhEnum identificador : EnumSet.allOf(MotivoDesbloqueioCnhEnum.class)) {
            lookup.put(identificador.getMotivo(), identificador);
        }
    }

    private MotivoDesbloqueioCnhEnum(String motivo) {
        this.motivo = motivo;
    }

    public String getMotivo() {
        return motivo;
    }

    public static MotivoDesbloqueioCnhEnum getMotivoDesbloqueioCnhEnum(String code) {
        return lookup.get(code);
    }
    
    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }
}
