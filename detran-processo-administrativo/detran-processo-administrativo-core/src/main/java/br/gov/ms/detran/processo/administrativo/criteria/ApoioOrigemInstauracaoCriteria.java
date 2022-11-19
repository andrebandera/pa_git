package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.AcaoInstauracaoEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.AcaoSistemaPAEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.MotivoPenalidadeEnum;
import br.gov.ms.detran.comum.entidade.enums.inf.TipoProcessoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.comum.util.querybuilder.Operand;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.RegraInstaurarEnum;

/*
 * @author Carlos Eduardo
 */

@CriteriaQuery(query = "SELECT NEW br.gov.ms.detran.processo.administrativo.wrapper."
        + "ApoioOrigemInstauracaoWrapper(p) FROM ApoioOrigemInstauracao p", 
        selectCount = "SELECT COUNT(p.id) ")
public class ApoioOrigemInstauracaoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {
    private static final long serialVersionUID = 1L;
    
    @Argument(id = "id", name = "p.id")
    private Long id;
    
    @Argument(id="regra", name = "p.regra", operand = Operand.CONTAINS)
    private RegraInstaurarEnum regra;
    
    @Argument(id="descricao", name = "p.descricao", operand = Operand.CONTAINS)
    private String descricao;

    @Argument(id="tipoResultadoProcesso", name = "p.resultadoTipoProcesso")
    private TipoProcessoEnum resultadoTipoProcesso;
    
    @Argument(id="motivoResultado", name="p.resultadoMotivo")
    private MotivoPenalidadeEnum resultadoMotivo;
    
    @Argument(id="acaoResultado", name="p.resultadoAcao")
    private AcaoInstauracaoEnum resultadoAcao;
    
    @Argument(id="acaoSistema", name="p.acaoSistema")
    private AcaoSistemaPAEnum acaoSistema; 
            
    @Argument(id = "ativo", name = "p.ativo")
    private AtivoEnum ativo;
    
    private PAFluxoProcesso fluxoProcesso;
            
    public ApoioOrigemInstauracaoCriteria(){
        this.sort.addSortItem("p.id");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegraInstaurarEnum getRegra() {
        return regra;
    }

    public void setRegra(RegraInstaurarEnum regra) {
        this.regra = regra;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoProcessoEnum getResultadoTipoProcesso() {
        return resultadoTipoProcesso;
    }

    public void setResultadoTipoProcesso(TipoProcessoEnum resultadoTipoProcesso) {
        this.resultadoTipoProcesso = resultadoTipoProcesso;
    }

    public MotivoPenalidadeEnum getResultadoMotivo() {
        return resultadoMotivo;
    }

    public void setResultadoMotivo(MotivoPenalidadeEnum resultadoMotivo) {
        this.resultadoMotivo = resultadoMotivo;
    }

    public AcaoInstauracaoEnum getResultadoAcao() {
        return resultadoAcao;
    }

    public void setResultadoAcao(AcaoInstauracaoEnum resultadoAcao) {
        this.resultadoAcao = resultadoAcao;
    }

    public AcaoSistemaPAEnum getAcaoSistema() {
        return acaoSistema;
    }

    public void setAcaoSistema(AcaoSistemaPAEnum acaoSistema) {
        this.acaoSistema = acaoSistema;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    public PAFluxoProcesso getFluxoProcesso() {
        return fluxoProcesso;
    }
    
}
