package br.gov.ms.detran.processo.administrativo.wrapper;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Christiano Carrilho.
 */
public class InstauracaoBlocoWrapper2 implements Serializable {
    
    private Long execucaoId;
    private final List<DadosCondutorWrapper> condutores;

    public InstauracaoBlocoWrapper2(List<DadosCondutorWrapper> condutores) {
        this.condutores = condutores;
    }

    public Long getExecucaoId() {
        return execucaoId;
    }

    public void setExecucaoId(Long execucaoId) {
        this.execucaoId = execucaoId;
    }

    public List<DadosCondutorWrapper> getCondutores() {
        return condutores;
    }
}