/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.processo.administrativo.entidade.ApoioOrigemInstauracao;
import br.gov.ms.detran.processo.administrativo.entidade.PAFluxoFase;
import br.gov.ms.detran.processo.administrativo.enums.TipoFasePaEnum;

/**
 *
 * @author Carlos Eduardo
 */
@CriteriaQuery(
        query = "SELECT NEW br.gov.ms.detran.processo.administrativo.wrapper.PATipoCorpoAndamentoWrapper(p) FROM PATipoCorpoAndamento p",
        selectCount = "SELECT COUNT(p.id) "
)
public class PATipoCorpoAndamentoCriteria  extends DetranAbstractCriteria implements ICriteriaQueryBuilder{
    
    @Argument(id="id", name="p.id")
    private Long id;
    
    @Argument(id="fluxoFase", name="p.fluxoFase")
    private PAFluxoFase fluxoFase;
    
    @Argument(id="origemProcesso", name="p.origemProcesso")
    private ApoioOrigemInstauracao origemProcesso;
    
    @Argument(id="tipoCorpoTexto", name="p.tipoCorpoTexto")
    private Long tipoCorpoTexto;
    
    @Argument(id="tipoNotificacao", name="p.tipoNotificacao")
    private TipoFasePaEnum tipoNotificacao;
    
    @Argument(id="ativo", name="p.ativo")
    private AtivoEnum ativo;
    
    public PATipoCorpoAndamentoCriteria(){
        this.sort.addSortItem("p.id");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PAFluxoFase getFluxoFase() {
        return fluxoFase;
    }

    public void setFluxoFase(PAFluxoFase fluxoFase) {
        this.fluxoFase = fluxoFase;
    }

    public ApoioOrigemInstauracao getOrigemProcesso() {
        return origemProcesso;
    }

    public void setOrigemProcesso(ApoioOrigemInstauracao origemProcesso) {
        this.origemProcesso = origemProcesso;
    }

    public Long getTipoCorpoTexto() {
        return tipoCorpoTexto;
    }

    public void setTipoCorpoTexto(Long tipoCorpoTexto) {
        this.tipoCorpoTexto = tipoCorpoTexto;
    }

    public TipoFasePaEnum getTipoNotificacao() {
        return tipoNotificacao;
    }

    public void setTipoNotificacao(TipoFasePaEnum tipoNotificacao) {
        this.tipoNotificacao = tipoNotificacao;
    }

    public AtivoEnum getAtivo() {
        return ativo;
    }

    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }
    
    
    
}
