package br.gov.ms.detran.processo.administrativo.core.bo.recursoonline;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.TipoArquivo;
import br.gov.ms.detran.comum.core.projeto.enums.apo.ModuloTipoArquivoEnum;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.apo.TipoExtensaoArquivoEnum;
import br.gov.ms.detran.comum.persistencia.AbstractJpaDAORepository;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ArquivoPARepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoPAOnlineArquivoRepositorio;
import br.gov.ms.detran.processo.administrativo.entidade.ArquivoPA;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnline;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnlineArquivo;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoOnlinePaDocumentoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoOnlinePaWrapper;

import javax.persistence.EntityManager;
import java.util.List;

import static br.gov.ms.detran.processo.administrativo.constantes.TipoArquivoPAConstante.ANEXOS_RECURSO_ONLINE_PA;

public class RecursoPAOnlineArquivoBO {

    private IApoioService apoioService;

    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    public void desativarArquivosRecursoOnline(EntityManager em, RecursoPAOnline recursoOnline) throws AppException {

        RecursoPAOnlineArquivoRepositorio repositorio = new RecursoPAOnlineArquivoRepositorio();

        List<RecursoPAOnlineArquivo> lArquivos
                = repositorio.getListArquivosPorRecursoOnline(em, recursoOnline.getId());

        if (!DetranCollectionUtil.ehNuloOuVazio(lArquivos)) {
            for (RecursoPAOnlineArquivo arquivo : lArquivos) {
                arquivo.setAtivo(AtivoEnum.DESATIVADO);
                repositorio.update(em, arquivo);
            }
        }
    }

    public void gravarArquivosRecursoOnline(EntityManager em, RecursoOnlinePaWrapper wrapper, RecursoPAOnline recursoOnline) throws AppException {
        if (!DetranCollectionUtil.ehNuloOuVazio(wrapper.getDocumentos())) {

            TipoArquivo tipoArquivo =
                    (TipoArquivo) getApoioService()
                            .getTipoArquivoPorCodigoEModulo(
                                    ANEXOS_RECURSO_ONLINE_PA,
                                    ModuloTipoArquivoEnum.PA);

            for (RecursoOnlinePaDocumentoWrapper documento : wrapper.getDocumentos()) {

                gravar(em, recursoOnline, tipoArquivo, documento);
            }
        }
    }

    public void gravar(EntityManager em, RecursoPAOnline recursoOnline, TipoArquivo tipoArquivo, RecursoOnlinePaDocumentoWrapper documento) throws DatabaseException {

        ArquivoPARepositorio arquivoRepo = new ArquivoPARepositorio();

        /** Arquivo. **/
        ArquivoPA arquivoPA = new ArquivoPA();

        arquivoPA.setByteArquivo(documento.getByteArquivo());
        arquivoPA.setDescricao(documento.getNomeArquivo());

        arquivoPA.setExtensao(TipoExtensaoArquivoEnum.valueOf(documento.getTipoArquivo().toUpperCase()));

        arquivoPA.setTabela("TB_TEM_RECURSO_PA_ONLINE");
        arquivoPA.setIdTabela(recursoOnline.getId());
        arquivoPA.setAtivo(AtivoEnum.ATIVO);
        arquivoPA.setTipoArquivo(tipoArquivo.getId());

        arquivoRepo.insert(em, arquivoPA);

        /** Recurso Online - Documento. **/
        RecursoPAOnlineArquivo recursoOnlineArquivo = new RecursoPAOnlineArquivo();

        recursoOnlineArquivo.setTipoDocumento(documento.getTipoDocumento());
        recursoOnlineArquivo.setArquivoPA(arquivoPA);
        recursoOnlineArquivo.setRecursoOnline(recursoOnline);
        recursoOnlineArquivo.setAtivo(AtivoEnum.ATIVO);
        recursoOnlineArquivo.setDescricao(documento.getNomeArquivo());
        recursoOnlineArquivo.setObservacao("Documento Recurso Online - " + documento.getTipoDocumento().name());

        new AbstractJpaDAORepository().insert(em, recursoOnlineArquivo);
    }

    public void gravarFormularioAssinado(EntityManager em, RecursoOnlinePaWrapper wrapper, RecursoPAOnline recursoPassFinal) throws AppException {
        TipoArquivo tipoArquivo =
                (TipoArquivo) getApoioService()
                        .getTipoArquivoPorCodigoEModulo(
                                ANEXOS_RECURSO_ONLINE_PA,
                                ModuloTipoArquivoEnum.PA);

        gravar(em, recursoPassFinal, tipoArquivo, wrapper.getFormularioRecursoAssinado());
    }
}
