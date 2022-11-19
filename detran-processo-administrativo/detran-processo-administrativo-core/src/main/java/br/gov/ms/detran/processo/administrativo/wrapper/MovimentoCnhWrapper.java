package br.gov.ms.detran.processo.administrativo.wrapper;

import br.gov.ms.detran.comum.core.projeto.entidade.hab.CnhSituacaoEntrega;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.iface.IEntityResource;
import br.gov.ms.detran.comum.projeto.anotacao.wrapper.EntityMapping2;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.adapter.LongAdapter;
import br.gov.ms.detran.processo.administrativo.entidade.MovimentoCnh;
import br.gov.ms.detran.processo.administrativo.enums.ValidadeCnhEnum;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Lillydi
 */
@EntityMapping2(entity = {MovimentoCnh.class})
@XmlRootElement
public class MovimentoCnhWrapper implements IBaseEntity, IEntityResource<MovimentoCnh> {

    private MovimentoCnh entidade;

    private CnhSituacaoEntrega situacao;

    private Date dataFimPenalidade;
    
    private byte[] byteArquivo;
    
    private String validadeCnhLabel;

    public MovimentoCnhWrapper() {
        
    }
    
    public MovimentoCnhWrapper(MovimentoCnh entidade) {
        this.entidade = entidade;
    }
    
    public MovimentoCnhWrapper(MovimentoCnh entidade, CnhSituacaoEntrega situacao, Date dataFim) {
        this(entidade);
        this.situacao = situacao;
        this.dataFimPenalidade = dataFim;
    }

    @Override
    public Long getId() {
        return entidade != null ? entidade.getId() : null;
    }

    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(LongAdapter.class)
    public void setId(Serializable id) {
        if (this.entidade == null) {
            this.entidade = new MovimentoCnh();
        }
        this.entidade.setId(id);
    }

    @Override
    public MovimentoCnh getEntidade() {
        return entidade;
    }

    @Override
    public void setEntidade(MovimentoCnh entidade) {
        this.entidade = entidade;
    }

    public CnhSituacaoEntrega getSituacao() {
        return situacao;
    }

    public void setSituacao(CnhSituacaoEntrega situacao) {
        this.situacao = situacao;
    }

    public byte[] getByteArquivo() {
        return byteArquivo;
    }

    public void setByteArquivo(byte[] byteArquivo) {
        this.byteArquivo = byteArquivo;
    }

    public String getAcaoLabel() {
        if (this.situacao != null) {
            if (this.situacao.getAcao() != null) {
                return this.situacao.getAcao().toString();
            }
        }

        return null;
    }

    public void setAcaoLabel(String a) {
    }

    public Date getDataFimPenalidade() {
        return dataFimPenalidade;
    }

    @XmlElement(name = "dataFimPenalidade")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataFimPenalidade(Date dataFimPenalidade) {
        this.dataFimPenalidade = dataFimPenalidade;
    }

    public String getValidadeCnhLabel() {
        if (this.situacao != null && this.situacao.getCnhControle() != null) {
            if(this.situacao.getCnhControle().getValidadeCnh() != null){
                if (Utils.compareToDate(this.situacao.getCnhControle().getValidadeCnh(), Calendar.getInstance().getTime()) >= 0) {
                    validadeCnhLabel = ValidadeCnhEnum.VALIDA.toString();
                } else {
                    validadeCnhLabel = ValidadeCnhEnum.VENCIDA.toString();
                }
            }
        }
        
        return validadeCnhLabel;
    }

    public void setValidadeCnhLabel(String validadeCnhLabel) {
        this.validadeCnhLabel = validadeCnhLabel;
    }
    
}