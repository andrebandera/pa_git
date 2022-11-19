package br.gov.ms.detran.processo.administrativo.core.repositorio;

import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnlineArquivo;
import br.gov.ms.detran.processo.administrativo.enums.TipoDocumentoRecursoPAOnlineEnum;

import javax.persistence.EntityManager;
import java.util.List;

public class RecursoPAOnlineArquivoRepositorio extends AbstractJpaDAORepository<RecursoPAOnlineArquivo> {

    public List<RecursoPAOnlineArquivo> getListArquivosPorRecursoOnline(EntityManager em, Long idRecursoOnline) throws DatabaseException {

        return getListNamedQuery(em, "RecursoPAOnlineArquivo.getListArquivosPorRecursoOnline", idRecursoOnline);
    }

    public RecursoPAOnlineArquivo getFormularioAssinadoDoRecursoOnline(EntityManager em, Long idRecurso) throws DatabaseException {
        return getNamedQuery(em, "RecursoPAOnlineArquivo.getFormularioAssinadoDoRecursoOnline", idRecurso, TipoDocumentoRecursoPAOnlineEnum.DOCUMENTO_FORMULARIO_ASSINADO, AtivoEnum.ATIVO);
    }
}
