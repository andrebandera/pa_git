package br.gov.ms.detran.processo.administrativo.enums;

import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.processo.administrativo.core.cenarios.IInstauracaoCenario;
import br.gov.ms.detran.processo.administrativo.core.cenarios.Instauracao_C10_PontuacaoBO;
import br.gov.ms.detran.processo.administrativo.core.cenarios.Instauracao_C11_PontuacaoAgravamentoBO;
import br.gov.ms.detran.processo.administrativo.core.cenarios.Instauracao_C12_Agravamento_EspecificadaBO;
import br.gov.ms.detran.processo.administrativo.core.cenarios.Instauracao_C1_PermissionadoBO;
import br.gov.ms.detran.processo.administrativo.core.cenarios.Instauracao_C2_PegoDirigindoBO;
import br.gov.ms.detran.processo.administrativo.core.cenarios.Instauracao_C3_PaAtivo_Cassacao_Direta_Reincidencia_Infracao_EspecificadaBO;
import br.gov.ms.detran.processo.administrativo.core.cenarios.Instauracao_C4_CassacaoReincidenciaArtigoBO;
import br.gov.ms.detran.processo.administrativo.core.cenarios.Instauracao_C5_CassacaoDiretaReincidenciaBO;
import br.gov.ms.detran.processo.administrativo.core.cenarios.Instauracao_C6_SuspensaoDiferenteArtigoBO;
import br.gov.ms.detran.processo.administrativo.core.cenarios.Instauracao_C7_SuspensaoReincidenciaMesmoArtigoForaPrazoBO;
import br.gov.ms.detran.processo.administrativo.core.cenarios.Instauracao_C8_BO;
import br.gov.ms.detran.processo.administrativo.core.cenarios.Instauracao_C9_EspecificadaBO;

/**
 *
 * @author desenvolvimento
 */
public enum InstauracaoCenarioEnum {
    
    C1 (new Instauracao_C1_PermissionadoBO()),
    C2 (new Instauracao_C2_PegoDirigindoBO()),
    C3 (new Instauracao_C3_PaAtivo_Cassacao_Direta_Reincidencia_Infracao_EspecificadaBO()),
    C4 (new Instauracao_C4_CassacaoReincidenciaArtigoBO()),
    C5 (new Instauracao_C5_CassacaoDiretaReincidenciaBO()),
    C6 (new Instauracao_C6_SuspensaoDiferenteArtigoBO()),
    C7 (new Instauracao_C7_SuspensaoReincidenciaMesmoArtigoForaPrazoBO()),
    C8 (new Instauracao_C8_BO()),
    C9 (new Instauracao_C9_EspecificadaBO()),
    C10(new Instauracao_C10_PontuacaoBO()),
    C11(new Instauracao_C11_PontuacaoAgravamentoBO()),
    C12(new Instauracao_C12_Agravamento_EspecificadaBO())
    ;
    
    private final IInstauracaoCenario instauracaoCenario;
    
    private InstauracaoCenarioEnum(IInstauracaoCenario iInstauracaoCenario) {
        this.instauracaoCenario = iInstauracaoCenario;
    }

    @Override
    public String toString() {
        String key = getClass().getSimpleName() + "." + this.name();
        return DetranWebUtils.getMessageByKey(key);
    }

    public IInstauracaoCenario getInstauracaoCenario() {
        return instauracaoCenario;
    }
}