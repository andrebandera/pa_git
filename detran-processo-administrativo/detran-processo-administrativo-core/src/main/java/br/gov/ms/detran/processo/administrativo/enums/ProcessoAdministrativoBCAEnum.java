/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante;
import br.gov.ms.detran.processo.administrativo.core.bo.ConsistenciaBloqueioBCAAtualizacaoBO;
import br.gov.ms.detran.processo.administrativo.core.bo.IProcessoAdministrativoBCA;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoBCA_ATUALIZACAO_BLOQUEIO_BCA_723_2018;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoBCA_ATUALIZACAO_BLOQUEIO_BCA_723_2018_Varios_Processos;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoBCA_LIBERACAO_BLOQUEIO_BCA_ENTREGA;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoBCA_LIBERACAO_IMPEDIMENTO_LOCAL_BIN;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoBCA_LIB_BLOQUEIO_BCA;
import br.gov.ms.detran.processo.administrativo.core.bo.ProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevidoBO;

/**
 *
 * @author desenvolvimento
 */
public enum ProcessoAdministrativoBCAEnum {
        
    LIB_BLOQUEIO_BCA(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.INCLUIR_BLOQUEIO_BCA, new ProcessoAdministrativoBCA_LIB_BLOQUEIO_BCA()),
    LIBERACAO_BLOQUEIO_BCA_ENTREGA(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.ATUALIZAR_BLOQUEIO_BCA, new ProcessoAdministrativoBCA_LIBERACAO_BLOQUEIO_BCA_ENTREGA()),
    LIBERACAO_IMPEDIMENTO_LOCAL_BIN(PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.DESBLOQUEAR_BCA, new ProcessoAdministrativoBCA_LIBERACAO_IMPEDIMENTO_LOCAL_BIN()),
    ATUALIZACAO_BLOQUEIO_BCA_723(PAAndamentoProcessoConstante.ATUALIZAR_BLOQUEIO_BCA_RESOLUCAO_723_2018, new ProcessoAdministrativoBCA_ATUALIZACAO_BLOQUEIO_BCA_723_2018()),
    ATUALIZACAO_BLOQUEIO_BCA_723_Varios_Processos(PAAndamentoProcessoConstante.ATUALIZAR_BLOQUEIO_BCA_RESOLUCAO_723_2018_VARIOS_PROCESSOS, new ProcessoAdministrativoBCA_ATUALIZACAO_BLOQUEIO_BCA_723_2018_Varios_Processos()),
    ATUALIZACAO_BLOQUEIO_BCA(PAAndamentoProcessoConstante.ATUALIZACAO_BLOQUEIO_BCA, new ConsistenciaBloqueioBCAAtualizacaoBO()),
    EMISSAO_NOTIFICACAO_ARQUIVAMENTO_INDEVIDO(PAAndamentoProcessoConstante.EMISSAO_NOTIFICACAO_ARQUIVAMENTO_INDEVIDO, new ProcessoAdministrativoEmissaoNotificacaoArquivamentoIndevidoBO())
    ;

    private final Integer codigoAndamento;

    private final IProcessoAdministrativoBCA implementacaoAndamentoBCA;

    private ProcessoAdministrativoBCAEnum(Integer codigoAndamento, IProcessoAdministrativoBCA implementacaoAndamentoBCA) {
        this.codigoAndamento            = codigoAndamento;
        this.implementacaoAndamentoBCA  = implementacaoAndamentoBCA;
    }

    public Integer getCodigoAndamento() {
        return codigoAndamento;
    }

    public static IProcessoAdministrativoBCA getImplementacaoAndamentoBCA(Integer codigoAndamento) throws AppException {

        if(codigoAndamento == null) {
            DetranWebUtils.applicationMessageException("severity.error.criteria.isEmpty");
        }

        for (ProcessoAdministrativoBCAEnum processoAdministrativoBCAEnum : ProcessoAdministrativoBCAEnum.values()) {

            if(processoAdministrativoBCAEnum.codigoAndamento.equals(codigoAndamento)) {
                return processoAdministrativoBCAEnum.implementacaoAndamentoBCA;
            }
        }

        throw new AppException("severity.error.criteria.isEmpty");
    }
}
