package br.gov.ms.detran.processo.administrativo.core.bo.recursoonline;

import br.gov.ms.detran.comum.core.projeto.entidade.apo.Municipio;
import br.gov.ms.detran.comum.entidade.enums.AtivoEnum;
import br.gov.ms.detran.comum.entidade.enums.BooleanEnum;
import br.gov.ms.detran.comum.projeto.service.FabricaJSPASequencial;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.*;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.integracao.comum.wrapper.AEMNPP08;
import br.gov.ms.detran.processo.administrativo.core.integracao.AEMNPP08BO;
import br.gov.ms.detran.processo.administrativo.core.repositorio.NotificacaoProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.RecursoPAOnlineRepositorio;
import br.gov.ms.detran.processo.administrativo.ejb.IPassoRecursoOnlinePA;
import br.gov.ms.detran.processo.administrativo.entidade.NotificacaoProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.entidade.RecursoPAOnline;
import br.gov.ms.detran.processo.administrativo.enums.PassoRecursoOnlinePAEnum;
import br.gov.ms.detran.processo.administrativo.enums.RecursoSituacaoPAEnum;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoOnlinePaWrapper;

import javax.persistence.EntityManager;
import java.util.Calendar;

public class RecursoOnlinePasso01BO extends FabricaJSPASequencial implements IPassoRecursoOnlinePA {

    private static final Logger LOG = Logger.getLogger(RecursoOnlinePasso01BO.class);

    private IApoioService apoioService;

    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }

    @Override
    public RecursoOnlinePaWrapper executa(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {
        LOG.info("Passo 1 - EXECUTA");

        valida(em, wrapper);

        wrapper = registra(em, wrapper);

        return wrapper;
    }

    @Override
    public void valida(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {

        validarObrigatoriedade(wrapper);

        ProcessoAdministrativo processoAdministrativo = new ProcessoAdministrativoRepositorio().getProcessoAdministrativoPorNumeroProcessoAtivo(em, wrapper.getNumeroProcesso());

        if(!wrapper.getCpf().equalsIgnoreCase(processoAdministrativo.getCpf())){
            DetranWebUtils.applicationMessageException("O número de processo informado não pertence ao CPF.");
        }


        new RecursoOnlineBO().validarExucucaoPassoRecursoOnline(em, wrapper, processoAdministrativo);

    }

    private void validarObrigatoriedade(RecursoOnlinePaWrapper wrapper) throws AppException {
        if (DetranStringUtil.ehBrancoOuNulo(wrapper.getCpf())
                || DetranStringUtil.ehBrancoOuNulo(wrapper.getEmail())
                || DetranStringUtil.ehBrancoOuNulo(wrapper.getNumeroProcesso())) {
            DetranWebUtils.applicationMessageException("Campos obrigatório não informado.");
        }
    }

    @Override
    public RecursoOnlinePaWrapper registra(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {

        wrapper.setToken(generateToken(wrapper));
        wrapper.setProtocolo(CriptografiaUtil.gerarChaveRandomica(10).toUpperCase());

        RecursoPAOnline recursoOnline = criaInstanciaRecursoOnline(em, wrapper);

        new RecursoOnlineBO().envioEmailIdentificacaoTokenProtocolo(em, recursoOnline);

        return retorno(em, recursoOnline, wrapper);
    }

    private RecursoPAOnline criaInstanciaRecursoOnline(EntityManager em, RecursoOnlinePaWrapper wrapper) throws AppException {
        RecursoPAOnline recursoOnline = new RecursoPAOnline();

        recursoOnline.setDataRecurso(Calendar.getInstance().getTime());
        recursoOnline.setAtivo(AtivoEnum.ATIVO);
        recursoOnline.setCpf(wrapper.getCpf());
        recursoOnline.setDestino(wrapper.getDestinoRecurso());
        recursoOnline.setEmail(wrapper.getEmail());
        recursoOnline.setNumeroProcesso(wrapper.getNumeroProcesso());
        recursoOnline.setOrdemPasso(PassoRecursoOnlinePAEnum.PASSO_01.getCodigo());
        recursoOnline.setPasso(PassoRecursoOnlinePAEnum.PASSO_01);
        recursoOnline.setProtocolo(wrapper.getProtocolo());
        recursoOnline.setTipo(wrapper.getTipoRecurso());
        recursoOnline.setToken(wrapper.getToken());
        recursoOnline.setIp(wrapper.getIp());
        recursoOnline.setProcessoAdministrativo(wrapper.getProcessoAdministrativo());
        recursoOnline.setSituacao(RecursoSituacaoPAEnum.INICIADO);
        recursoOnline.setRequerente(getApoioService().getNomeDoAtendimento(wrapper.getProcessoAdministrativo().getAtendimento()));
        recursoOnline.setNumeroRegistroCnh(wrapper.getProcessoAdministrativo().getNumeroRegistro());

        NotificacaoProcessoAdministrativo notificacao
                = new NotificacaoProcessoAdministrativoRepositorio().
                getNotificacaoPorProcessoAdministrativoETipoNotificacao(em,
                        wrapper.getProcessoAdministrativo().getId(),
                        wrapper.getTipoRecurso());

        if (notificacao != null) {
            recursoOnline.setDataPrazoLimite(notificacao.getDataPrazoLimite());
            recursoOnline.setIndiceTempestividade(notificacao.getDataPrazoLimite().after(Calendar.getInstance().getTime()) ? BooleanEnum.SIM : BooleanEnum.NAO);

            AEMNPP08 aemnpp08 = new AEMNPP08BO().executarIntegracaoAEMNPP08(wrapper.getCpf());

            if (aemnpp08 != null) {
                recursoOnline.setEndereco(aemnpp08.getLogradouro());
                recursoOnline.setEnderecoBairro(aemnpp08.getBairro());
                recursoOnline.setEnderecoCEP(aemnpp08.getCep());
                recursoOnline.setEnderecoComplemento(aemnpp08.getComplemento());
                recursoOnline.setEnderecoNumero("");

                Municipio municipio = (Municipio) getApoioService().getMunicipioId(Long.parseLong(aemnpp08.getMunicipio()));
                if (municipio != null) {
                    recursoOnline.setMunicipio(municipio.getNome());
                    recursoOnline.setUf(municipio.getEstado().getSigla());
                }
            }
        }

        new RecursoPAOnlineRepositorio().insert(em, recursoOnline);

        return recursoOnline;

    }

    @Override
    public RecursoOnlinePaWrapper retorno(EntityManager em, RecursoPAOnline recursoOnline, RecursoOnlinePaWrapper wrapper) throws AppException {

        wrapper.setPasso(PassoRecursoOnlinePAEnum.PASSO_01.ordinal());
        wrapper.setProcessoAdministrativo(null);

        return wrapper;
    }

    private String generateToken(RecursoOnlinePaWrapper wrapper) throws AppException {

        String token = null;

        try {

            String valuesForToken = wrapper.getCpf() +
                    wrapper.getNumeroProcesso() +
                    Utils.formatDate(Calendar.getInstance().getTime(), "ddMMyyyyHHmmssS");
            token = DetranTokenUtil.convertToken(valuesForToken);

        } catch(Exception e) {
            LOG.error("Erro capturado na geração do token.", e);
        }

        if(DetranStringUtil.ehBrancoOuNulo(token)) {
            DetranWebUtils.applicationMessageException("recursoonlinepa.M8", null, "Token");
        }

        return token.toUpperCase();
    }
}
