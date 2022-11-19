package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import br.gov.ms.detran.processo.administrativo.entidade.ExecucaoInstauracao;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Lillydi
 */
public class InstauracaoBlocoWrapper implements Serializable {
    
    private ExecucaoInstauracao execucaoInstauracao;
    
    private final List<DadosCondutorPAD> condutores;
    
    private final List<ApoioOrigemInstauracaoWrapper> apoioOrigens;

    public InstauracaoBlocoWrapper(List<DadosCondutorPAD> condutores, List<ApoioOrigemInstauracaoWrapper> apoioOrigens) {
        this.condutores = condutores;
        this.apoioOrigens = apoioOrigens;
    }
    
    public InstauracaoBlocoWrapper(
        ExecucaoInstauracao execucaoInstauracao, 
        List<DadosCondutorPAD> condutores, 
        List<ApoioOrigemInstauracaoWrapper> apoioOrigens) {
        
        this.execucaoInstauracao = execucaoInstauracao;
        this.condutores = condutores;
        this.apoioOrigens = apoioOrigens;
    }

    public List<DadosCondutorPAD> getCondutores() {
        return condutores;
    }

    public List<ApoioOrigemInstauracaoWrapper> getApoioOrigens() {
        return apoioOrigens;
    }

    public ExecucaoInstauracao getExecucaoInstauracao() {
        return execucaoInstauracao;
    }

    public void setExecucaoInstauracao(ExecucaoInstauracao execucaoInstauracao) {
        this.execucaoInstauracao = execucaoInstauracao;
    }
}