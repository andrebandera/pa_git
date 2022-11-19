package br.gov.ms.detran.processo.administrativo.core.bo.recursoonline;

import br.gov.ms.detran.comum.projeto.service.FabricaJSPASequencial;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoPAOnlineRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPassoRecursoOnlinePA;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnline;
import br.gov.ms.detran.processo.administrativo.enums.PassoRecursoOnlinePAEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoSituacaoPAEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoOnlinePaWrapper;

import javax.persistence.EntityManager;
import java.util.Calendar;

public class RecursoOnlinePasso02BO extends FabricaJSPASequencial implements IPassoRecursoOnlinePA {

    private static final Logger LOG = Logger.getLogger(RecursoOnlinePasso02BO.class);

    @Override
    public RecursoOnlinePaWrapper executa(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {
        LOG.info("Passo 2 - EXECUTA");

        valida(em, wrapper);

        registra(em, wrapper);

        return wrapper;
    }

    @Override
    public void valida(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {

        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getToken()) && DetranStringUtil.ehBrancoOuNulo(wrapper.getProtocolo())) {
            DetranWebUtils.applicationMessageException("Favor informar o token ou protocolo.");
        }

        RecursoPAOnline recurso
                = new RecursoPAOnlineRepositorio().
                getRecursoOnlinePorTokenEProtocoloEPasso(em,
                        wrapper.getToken(),
                        wrapper.getProtocolo(),
                        PassoRecursoOnlinePAEnum.PASSO_01);

        if (recurso == null) {
            if (!DetranStringUtil.ehBrancoOuNulo(wrapper.getProtocolo())) {
                DetranWebUtils.applicationMessageException("Solicitação de recurso não encontrada para este Protocolo.");
            } else {
                DetranWebUtils.applicationMessageException("Solicitação de recurso não encontrada para este Token.");
            }
        }
        new RecursoOnlineBO().validarExucucaoPassoRecursoOnline(em, wrapper, recurso.getProcessoAdministrativo());

    }

    @Override
    public RecursoOnlinePaWrapper registra(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {

        RecursoPAOnlineRepositorio repo = new RecursoPAOnlineRepositorio();
        RecursoOnlineBO bo = new RecursoOnlineBO();

        bo.desativarPassoRecursoOnline(em, wrapper);

        RecursoPAOnline recursoPasso2 = bo.criarNovaInstanciaRecursoOnline(em, wrapper, PassoRecursoOnlinePAEnum.PASSO_01);

        recursoPasso2.setPasso(PassoRecursoOnlinePAEnum.PASSO_02);
        recursoPasso2.setOrdemPasso(PassoRecursoOnlinePAEnum.PASSO_02.getCodigo());
        recursoPasso2.setSituacao(RecursoSituacaoPAEnum.CADASTRADO);
        recursoPasso2.setDataRecurso(Calendar.getInstance().getTime());

        repo.insert(em, recursoPasso2);

        return retorno(em, recursoPasso2, wrapper);
    }

    @Override
    public RecursoOnlinePaWrapper retorno(EntityManager em, RecursoPAOnline recursoOnline, RecursoOnlinePaWrapper wrapper) throws AppException {

        RecursoPAOnline recursoMaisRecente
                = new RecursoPAOnlineRepositorio().
                getRecursoOnlineMaisRecentePorTokenEProtocolo(em, recursoOnline.getToken(), recursoOnline.getProtocolo());

        wrapper.setTipo(recursoMaisRecente.getTipo());
        wrapper.setDestino(recursoMaisRecente.getDestino());
        wrapper.setTelefoneCelular(recursoMaisRecente.getCelular());
        wrapper.setDescricao(recursoMaisRecente.getDescricaoRecurso());
        wrapper.setEndereco(recursoMaisRecente.getEndereco());
        wrapper.setBairro(recursoMaisRecente.getEnderecoBairro());
        wrapper.setCep(recursoMaisRecente.getEnderecoCEP());
        wrapper.setNumero(recursoMaisRecente.getEnderecoNumero());
        wrapper.setMunicipio(recursoMaisRecente.getMunicipio());
        wrapper.setMotivoRecusa(recursoMaisRecente.getMotivoRecusa());
        wrapper.setCpfUsuario(recursoMaisRecente.getCpfUsuario());
        wrapper.setRequerente(recursoMaisRecente.getRequerente());
        wrapper.setComplemento(recursoMaisRecente.getEnderecoComplemento());
        wrapper.setMotivoCancelamento(recursoMaisRecente.getMotivoCancelamento());
        wrapper.setRg(recursoMaisRecente.getRg());
        wrapper.setIndiceTempestividade(recursoMaisRecente.getIndiceTempestividade().getBooleanValue());
        wrapper.setUf(recursoMaisRecente.getUf());
        wrapper.setRegistroCnh(recursoMaisRecente.getNumeroRegistroCnh());
        wrapper.setTipoDocumento(recursoMaisRecente.getTipoDocumento());
        wrapper.setDataRecurso(recursoMaisRecente.getDataRecurso());
        wrapper.setTelefoneFixo(recursoMaisRecente.getTelefoneFixo());
        wrapper.setOrdemPasso(recursoMaisRecente.getOrdemPasso());
        wrapper.setPasso(PassoRecursoOnlinePAEnum.PASSO_03.ordinal());
        wrapper.setCpf(recursoMaisRecente.getCpf());
        wrapper.setNumeroProcesso(recursoMaisRecente.getNumeroProcesso());
        wrapper.setEmail(recursoMaisRecente.getEmail());
        wrapper.setConsultaAndamento(false);

        wrapper.setProcessoAdministrativo(null);

        return wrapper;
    }
}
