package br.gov.ms.detran.processo.administrativo.ejb;

import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnline;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoOnlinePaWrapper;

import javax.persistence.EntityManager;

public interface IPassoRecursoOnlinePA {

    public RecursoOnlinePaWrapper executa(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException;

    void valida(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException;

    RecursoOnlinePaWrapper registra(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException;

    RecursoOnlinePaWrapper retorno(EntityManager em, RecursoPAOnline recursoOnline, RecursoOnlinePaWrapper wrapper) throws AppException;
}
