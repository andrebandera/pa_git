package br.gov.ms.detran.processo.administrativo.core.bo.recursoonline;

import br.gov.ms.detran.comum.projeto.service.FabricaJSPASequencial;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoPAOnlineArquivoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoPAOnlineRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPassoRecursoOnlinePA;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnline;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnlineArquivo;
import br.gov.ms.detran.processo.administrativo.enums.PassoRecursoOnlinePAEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoOnlinePaWrapper;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.List;

public class RecursoOnlinePasso03BO extends FabricaJSPASequencial implements IPassoRecursoOnlinePA {

    private static final Logger LOG = Logger.getLogger(RecursoOnlinePasso03BO.class);

    @Override
    public RecursoOnlinePaWrapper executa(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {
        LOG.info("Passo 3 - EXECUTA");

        valida(em, wrapper);

        registra(em, wrapper);

        return wrapper;
    }

    @Override
    public void valida(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {

        validarObrigatoriedade(wrapper);

        RecursoPAOnline recurso
                = new RecursoPAOnlineRepositorio().
                getRecursoOnlinePorTokenEProtocoloEPasso(em,
                        wrapper.getToken(),
                        wrapper.getProtocolo(),
                        PassoRecursoOnlinePAEnum.PASSO_02);

        new RecursoOnlineBO().validarExucucaoPassoRecursoOnline(em, wrapper, recurso.getProcessoAdministrativo());
    }

    private void validarObrigatoriedade(RecursoOnlinePaWrapper wrapper) throws AppException {

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getProtocolo()) && DetranStringUtil.ehBrancoOuNulo(wrapper.getToken()))
            DetranWebUtils.applicationMessageException("Token e Protocolo não informados.");

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getCep())) {
            DetranWebUtils.applicationMessageException("recursoonlinepa.M4", "", "CEP");
        }
        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getEndereco())) {
            DetranWebUtils.applicationMessageException("recursoonlinepa.M4", "", "Endereço");
        }
        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getUf())) {
            DetranWebUtils.applicationMessageException("recursoonlinepa.M4", "", "UF");
        }
        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getMunicipio())) {
            DetranWebUtils.applicationMessageException("recursoonlinepa.M4", "", "Cidade");
        }
        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getBairro())) {
            DetranWebUtils.applicationMessageException("recursoonlinepa.M4", "", "Bairro");
        }
        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getTelefoneCelular())) {
            DetranWebUtils.applicationMessageException("recursoonlinepa.M4", "", "Telefone Celular");
        }
        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getEmail())) {
            DetranWebUtils.applicationMessageException("recursoonlinepa.M4", "", "Email");
        }
    }

    @Override
    public RecursoOnlinePaWrapper registra(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {

        RecursoPAOnlineRepositorio repo = new RecursoPAOnlineRepositorio();
        RecursoOnlineBO bo = new RecursoOnlineBO();

        bo.desativarPassoRecursoOnline(em, wrapper);

        RecursoPAOnline recursoPasso3 = bo.criarNovaInstanciaRecursoOnline(em, wrapper, PassoRecursoOnlinePAEnum.PASSO_02);

        recursoPasso3.setPasso(PassoRecursoOnlinePAEnum.PASSO_03);
        recursoPasso3.setOrdemPasso(PassoRecursoOnlinePAEnum.PASSO_03.getCodigo());
        recursoPasso3.setIp(wrapper.getIp());

        /* Dados da tela */
        recursoPasso3.setRg(wrapper.getRg());
//        recursoPasso3.setNumeroRegistroCnh(wrapper.getRegistroCnh());
        recursoPasso3.setCelular(DetranStringUtil.retiraMascara(wrapper.getTelefoneCelular()));
        recursoPasso3.setTelefoneFixo(DetranStringUtil.retiraMascara(wrapper.getTelefoneFixo()));
        recursoPasso3.setEndereco(wrapper.getEndereco());
        recursoPasso3.setEnderecoCEP(DetranStringUtil.retiraMascara(wrapper.getCep()));
        recursoPasso3.setEnderecoNumero(wrapper.getNumero());
        recursoPasso3.setEnderecoComplemento(wrapper.getComplemento());
        recursoPasso3.setUf(wrapper.getUf());
        recursoPasso3.setMunicipio(wrapper.getMunicipio());
        recursoPasso3.setEnderecoBairro(wrapper.getBairro());

        recursoPasso3.setDataRecurso(Calendar.getInstance().getTime());

        repo.insert(em, recursoPasso3);

        return retorno(em, recursoPasso3, wrapper);
    }

    @Override
    public RecursoOnlinePaWrapper retorno(EntityManager em, RecursoPAOnline recursoOnline, RecursoOnlinePaWrapper wrapper) throws AppException {

        wrapper.setPasso(PassoRecursoOnlinePAEnum.PASSO_03.ordinal());

        RecursoPAOnline passo4
                = new RecursoPAOnlineRepositorio()
                .getRecursoOnlinePorTokenEProtocoloEPasso(em, recursoOnline.getToken(), recursoOnline.getProtocolo(), PassoRecursoOnlinePAEnum.PASSO_04);

        if (passo4 != null) {
            wrapper.setDescricao(passo4.getDescricaoRecurso());
            List<RecursoPAOnlineArquivo> arquivos = new RecursoPAOnlineArquivoRepositorio().getListArquivosPorRecursoOnline(em, passo4.getId());
            if (!DetranCollectionUtil.ehNuloOuVazio(arquivos)) {

                wrapper.setDocumentos(new RecursoOnlineBO().montarDocumentoWrapper(arquivos));
            }

        }

        wrapper.setProcessoAdministrativo(null);

        return wrapper;
    }
}
