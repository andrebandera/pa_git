package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.DadosCondutorPAD;
import br.gov.ms.detran.processo.administrativo.entidade.DadosInfracaoPAD;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import java.io.Serializable;
import java.util.List;

/**
 * @author desenvolvimento
 */
public class InstaurarProcessoAdministrativoWrapper implements IBaseEntity {

    private Long execucaoInstauracaoId;

    private ApoioOrigemInstauracao apoioOrigemInstauracao;

    private DadosCondutorPAD dadosCondutorPAD;

    private List<DadosInfracaoPAD> infracoes;

    private Integer valorReferenciaMes;

    private Integer valorReferenciaIntervalo;

    private Integer pontuacao;

    private ProcessoAdministrativo processsoAdministrativoOrigem;

    public InstaurarProcessoAdministrativoWrapper(ApoioOrigemInstauracao apoioOrigemInstauracao, DadosCondutorPAD dadosCondutorPAD) {

        this.apoioOrigemInstauracao = apoioOrigemInstauracao;
        this.dadosCondutorPAD = dadosCondutorPAD;
    }

    public InstaurarProcessoAdministrativoWrapper(
            ApoioOrigemInstauracao apoioOrigemInstauracao,
            DadosCondutorPAD dadosCondutorPAD,
            Integer valorReferenciaMes,
            Integer valorReferenciaIntervalo,
            Long execucaoInstauracaoId) {

        this(apoioOrigemInstauracao, dadosCondutorPAD);

        this.valorReferenciaMes = valorReferenciaMes;
        this.valorReferenciaIntervalo = valorReferenciaIntervalo;
        this.execucaoInstauracaoId = execucaoInstauracaoId;
    }

    public ApoioOrigemInstauracao getApoioOrigemInstauracao() {
        return apoioOrigemInstauracao;
    }

    public void setApoioOrigemInstauracao(ApoioOrigemInstauracao apoioOrigemInstauracao) {
        this.apoioOrigemInstauracao = apoioOrigemInstauracao;
    }

    public DadosCondutorPAD getDadosCondutorPAD() {
        return dadosCondutorPAD;
    }

    public void setDadosCondutorPAD(DadosCondutorPAD dadosCondutorPAD) {
        this.dadosCondutorPAD = dadosCondutorPAD;
    }

    public List<DadosInfracaoPAD> getInfracoes() {
        return infracoes;
    }

    public void setInfracoes(List<DadosInfracaoPAD> infracoes) {
        this.infracoes = infracoes;
    }

    public Integer getValorReferenciaMes() {
        return valorReferenciaMes;
    }

    public void setValorReferenciaMes(Integer valorReferenciaMes) {
        this.valorReferenciaMes = valorReferenciaMes;
    }

    public Integer getValorReferenciaIntervalo() {
        return valorReferenciaIntervalo;
    }

    public void setValorReferenciaIntervalo(Integer valorReferenciaIntervalo) {
        this.valorReferenciaIntervalo = valorReferenciaIntervalo;
    }

    @Override
    public Serializable getId() {
        return this.dadosCondutorPAD != null ? this.dadosCondutorPAD.getId() : null;
    }

    public Integer getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Integer pontuacao) {
        this.pontuacao = pontuacao;
    }

    public ProcessoAdministrativo getProcesssoAdministrativoOrigem() {
        return processsoAdministrativoOrigem;
    }

    public void setProcesssoAdministrativoOrigem(ProcessoAdministrativo processsoAdministrativoOrigem) {
        this.processsoAdministrativoOrigem = processsoAdministrativoOrigem;
    }

    public Long getExecucaoInstauracaoId() {
        return execucaoInstauracaoId;
    }

    public void setExecucaoInstauracaoId(Long execucaoInstauracaoId) {
        this.execucaoInstauracaoId = execucaoInstauracaoId;
    }
}