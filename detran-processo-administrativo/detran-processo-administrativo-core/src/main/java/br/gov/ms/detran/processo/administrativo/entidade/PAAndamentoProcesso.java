package br.gov.ms.detran.processo.administrativo.entidade;

import br.gov.ms.detran.comum.entidade.BaseEntityAtivo;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.iface.IJSPASequencial;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.enums.OrigemPlataformaEnum;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "TB_TCB_PAD_ANDAMENTO_PROCESSO")
@NamedQueries({
    @NamedQuery(
        name= "PAAndamentoProcesso.getAndamentosAtivos", 
        query = "SELECT tcb FROM PAAndamentoProcesso tcb where tcb.codigo <= :p0 and tcb.ativo = :p1 ORDER BY tcb.codigo"
    ),
    @NamedQuery(
        name= "PAAndamentoProcesso.getListaPAAndamentoProcessoPorListaCodigo", 
        query = "SELECT tcb "
                + "FROM PAAndamentoProcesso tcb "
                + "WHERE tcb.codigo in (:p0) "
    ),
    @NamedQuery(
        name= "PAAndamentoProcesso.getAndamentosEmUso", 
        query = "SELECT tcb FROM PAAndamentoProcesso tcb where EXISTS (SELECT 1 FROM PAOcorrenciaStatus tde INNER JOIN tde.statusAndamento tcd INNER JOIN tcd.andamentoProcesso ap where ap.id = tcb.id) ORDER BY tcb.codigo"
    ),
    @NamedQuery(
        name= "PAAndamentoProcesso.getAndamentosAutomaticos", 
        query = "SELECT tcb FROM PAAndamentoProcesso tcb where EXISTS (SELECT 1 FROM PAOcorrenciaStatus tde INNER JOIN tde.statusAndamento tcd INNER JOIN tcd.andamentoProcesso ap where ap.id = tcb.id) and Exists(SELECT 1 From PAFluxoFase tch INNER JOIN tch.andamentoProcesso tcb1 where tcb1.id = tcb.id and tch.ativo = :p1 and tch.tipoAndamento = :p0) ORDER BY tcb.codigo"
    ),
    @NamedQuery(
            name = "PAAndamentoProcesso.PAAndamentoProcessoAtivoByCodigo",
            query = "SELECT pa FROM PAAndamentoProcesso pa WHERE pa.codigo like :p0 AND pa.ativo = :p1"),
        @NamedQuery(
            name = "PAAndamentoProcesso.PAAndamentoProcessoByDescricao",
            query = "SELECT pa FROM PAAndamentoProcesso pa WHERE UPPER(pa.descricao) = :p0"),
        @NamedQuery(name = "PAAndamentoProcesso.getAndamentoNaoUtilizadoPeloFluxoFase", 
            query = "SELECT e FROM PAAndamentoProcesso e WHERE e.id NOT IN "
                    + "(SELECT ma.id "
                    + "     FROM PAFluxoFase m "
                    + "     JOIN m.prioridadeFluxoAmparo s "
                    + "     JOIN m.andamentoProcesso ma "
                    + "     WHERE s.id = :p0 AND m.ativo = :p2) "
                    + "AND (:p1 IS NULL OR e.descricao LIKE :p1 ) "
                    + "AND e.ativo=:p2 "
                    + "ORDER BY e.descricao "),
        @NamedQuery(name = "PAAndamentoProcesso.getCountAndamentoNaoUtilizadoPeloFluxoFase", 
            query = "SELECT COUNT(e.id) FROM PAAndamentoProcesso e WHERE e.id NOT IN "
                    + "(SELECT ma.id "
                    + "     FROM PAFluxoFase m "
                    + "     JOIN m.prioridadeFluxoAmparo s "
                    + "     JOIN m.andamentoProcesso ma "
                    + "     WHERE s.id = :p0 AND m.ativo = :p2) "
                    + "AND (:p1 IS NULL OR e.descricao LIKE :p1 ) "
                    + "AND e.ativo=:p2 "),
        @NamedQuery(
                    name = "PAAndamentoProcesso.getAndamentosAtivosPorFluxo",
                    query = "SELECT tcb FROM PAFluxoFase tch "
                          + "INNER JOIN tch.andamentoProcesso tcb "
                          + "INNER JOIN tch.prioridadeFluxoAmparo tci "
                          + "INNER JOIN tci.fluxoProcesso tck "
                          + "WHERE tck.codigo = :p0 "
                          + "AND tci.ativo = :p1 "
                          + "AND tch.ativo = :p1 "
                          + "AND tcb.ativo = :p1 "
                          + "ORDER BY tcb.codigo"
                    )
})
public class PAAndamentoProcesso extends BaseEntityAtivo implements Serializable, IJSPASequencial {

    @Id
    @Column(name = "Tcb_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "Tcb_ISN_Migracao")
    private Integer isnMigracao;
    
    @Column(name = "Tcb_Codigo")
    private Integer codigo;
    
    @Column(name = "Tcb_Descricao")
    private String descricao;

    @Column(name = "Tcb_Origem_Plataforma")
    @Enumerated(EnumType.ORDINAL)
    private OrigemPlataformaEnum origemPlataforma;

    @Column(name = "Ativo")
    @Enumerated(EnumType.ORDINAL)
    private AtivoEnum ativo;

    public PAAndamentoProcesso() {
    }

    public PAAndamentoProcesso(Long id, Integer codigo) {
        this.id = id;
        this.codigo = codigo;
    }

    @Override
    public Long getId() {
        return id;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    @Override
    public void setId(Serializable id) {
        this.id = (id != null ? Long.valueOf(id.toString()) : null);
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getIsnMigracao() {
        return isnMigracao;
    }

    public void setIsnMigracao(Integer isnMigracao) {
        this.isnMigracao = isnMigracao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public OrigemPlataformaEnum getOrigemPlataforma() {
        return origemPlataforma;
    }

    public void setOrigemPlataforma(OrigemPlataformaEnum origemPlataforma) {
        this.origemPlataforma = origemPlataforma;
    }

    @Override
    public AtivoEnum getAtivo() {
        return ativo;
    }

    @Override
    public void setAtivo(AtivoEnum ativo) {
        this.ativo = ativo;
    }

    @Override
    public void setSequencial(Long sequencial) {
        this.codigo = sequencial.intValue();
    }
}