/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.criteria;

import br.gov.ms.detran.comum.iface.criteria.ICriteriaQueryBuilder;
import br.gov.ms.detran.comum.projeto.criteria.DetranAbstractCriteria;
import br.gov.ms.detran.comum.util.adapter.DateAdapter;
import br.gov.ms.detran.comum.util.querybuilder.Argument;
import br.gov.ms.detran.comum.util.querybuilder.CriteriaQuery;
import br.gov.ms.detran.comum.util.querybuilder.Operand;
import br.gov.ms.detran.processo.administrativo.enums.MovimentacaoMotivoEnum;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Roberto Oliveira
 */
@CriteriaQuery(query = "SELECT m FROM Movimentacao m ", selectCount = "SELECT COUNT(m.id) ")
public class RelProcessoPrescritoCriteria extends DetranAbstractCriteria implements ICriteriaQueryBuilder {

    @Argument(id = "dataEntrada", name = "convert(Date,m.dataInicio)", operand = Operand.GREATER_EQUAL)
    private Date dataEntrada;

    @Argument(id = "dataSaida", name = "convert(Date,m.dataInicio)", operand = Operand.LESSER_EQUAL)
    private Date dataSaida;

    @Argument(name = "m.motivo", id = "motivo")
    private MovimentacaoMotivoEnum motivo;

    public Date getDataEntrada() {
        return dataEntrada;
    }

    @XmlElement(name = "dataEntrada")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    @XmlElement(name = "dataSaida")
    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }

    public MovimentacaoMotivoEnum getMotivo() {
        return motivo;
    }

    public void setMotivo(MovimentacaoMotivoEnum motivo) {
        this.motivo = motivo;
    }

    @Override
    public Boolean isCriteriaEmpty() {

        boolean empty = true;

        if (getDataEntrada() != null) {
            empty = false;
        }
        if (getDataSaida() != null) {
            empty = false;
        }
        return empty;
    }
}
